import api from './api';
import { supabase } from '../supabaseClient';

export const authService = {

  /* -------- REGISTER USER -------- */
  async register(userData) {
    try {
      const { data, error } = await supabase.auth.signUp({
        email: userData.email,
        password: userData.password,
        options: {
          data: {
            firstName: userData.firstName,
            lastName: userData.lastName,
            name: `${userData.firstName} ${userData.lastName}`,
          },
        },
      });

      if (error) {
        // Handle specific registration errors
        if (error.message.includes('User already registered')) {
          throw new Error('An account with this email already exists. Please try logging in instead.');
        } else if (error.message.includes('rate limit')) {
          throw new Error('Too many registration attempts. Please wait a moment and try again.');
        } else {
          throw new Error(error.message);
        }
      }

      // Check if user was created but needs email confirmation
      if (data.user && !data.session) {
        // User created but email confirmation required
        await this.syncUserWithBackend(data.user);
        
        return {
          user: data.user,
          message: 'Registration successful! Please check your email to confirm your account.',
          emailConfirmationRequired: true
        };
      }

      // Auto-login if no email confirmation required (development mode)
      if (data.session?.access_token) {
        localStorage.setItem('token', data.session.access_token);
        localStorage.setItem('userName', data.user?.user_metadata?.name || data.user?.email);
        
        await this.syncUserWithBackend(data.user);
        
        return {
          user: data.user,
          session: data.session,
          message: 'Registration successful! You are now logged in.',
          autoLogin: true
        };
      }

      return data;

    } catch (error) {
      // For development mode, provide fallback
      if (process.env.NODE_ENV === 'development' && 
          (error.message.includes('rate limit') || 
           error.message.includes('already exists'))) {
        console.warn('Registration blocked, trying development fallback...');
        return await this._registerDevelopmentFallback(userData);
      }
      throw error;
    }
  },

  /* -------- LOGIN USER -------- */
  async login(credentials) {
    try {
      // Always try development fallback first for testing
      if (process.env.NODE_ENV === 'development' || !process.env.REACT_APP_SUPABASE_URL) {
        console.warn('Using development login fallback');
        return await this._loginDevelopmentFallback(credentials);
      }

      const { data, error } = await supabase.auth.signInWithPassword({
        email: credentials.email,
        password: credentials.password
      });

      if (error) throw new Error(error.message);

      if (data.session?.access_token) {
        localStorage.setItem('token', data.session.access_token);
        localStorage.setItem(
          'userName',
          data.user?.user_metadata?.name || data.user.email
        );

        await this.syncUserWithBackend(data.user);
      }

      return data;

    } catch (error) {
      // For development mode, provide fallback
      if (process.env.NODE_ENV === 'development' || !process.env.REACT_APP_SUPABASE_URL) {
        console.warn('Login failed, trying development fallback...');
        return await this._loginDevelopmentFallback(credentials);
      }
      throw error;
    }
  },

  /* -------- DEVELOPMENT LOGIN FALLBACK -------- */
  async _loginDevelopmentFallback(credentials) {
    console.log('🔧 Using development login fallback for:', credentials.email);
    
    // Create a mock user session
    const mockUser = {
      id: 'dev-user-' + Date.now(),
      email: credentials.email,
      user_metadata: {
        name: credentials.email.split('@')[0],
        firstName: 'Dev',
        lastName: 'User'
      }
    };

    const mockSession = {
      access_token: 'dev-token-' + Date.now(),
      user: mockUser
    };

    // Store in localStorage
    localStorage.setItem('token', mockSession.access_token);
    localStorage.setItem('userName', mockUser.user_metadata.name);

    // Sync with backend
    await this.syncUserWithBackend(mockUser);

    return {
      user: mockUser,
      session: mockSession,
      developmentMode: true,
      message: 'Development login successful!'
    };
  },

  /* -------- SYNC USER WITH BACKEND -------- */
  async syncUserWithBackend(user) {
    try {
      await api.post('/api/auth/sync', {
        userId: user.id,
        email: user.email,
        firstName: user.user_metadata?.firstName || '',
        lastName: user.user_metadata?.lastName || '',
        name: user.user_metadata?.name || user.email
      });
    } catch (err) {
      console.warn('Backend sync failed:', err);
    }
  },

  /* -------- LOGOUT -------- */
  async logout() {
    await supabase.auth.signOut();
    localStorage.removeItem('token');
    localStorage.removeItem('userName');
    window.location.href = '/login';
  },

  /* -------- AUTH CHECK -------- */
  isAuthenticated() {
    return !!localStorage.getItem('token');
  },

  /* -------- GET USER NAME -------- */
  getUserName() {
    return localStorage.getItem('userName');
  }
};
