import { Brain, Eye, EyeOff } from 'lucide-react';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import toast from 'react-hot-toast';
import { Link, useNavigate } from 'react-router-dom';

const Register = () => {
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm();

  const password = watch('password');

  const onSubmit = async (data) => {
    setIsLoading(true);
    try {
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1500));
      
      // Mock successful registration
      localStorage.setItem('token', 'mock-jwt-token');
      localStorage.setItem('userName', `${data.firstName} ${data.lastName}`);
      
      toast.success('Registration Successful! Welcome to Quizora AI');

      navigate('/dashboard');
    } catch (error) {
      toast.error('Registration Failed: Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <section className="min-h-screen bg-[#0a0a0a] flex items-center justify-center" data-scroll-section>
      <div className="container mx-auto px-4 sm:px-6 lg:px-8 max-w-md w-full">
        <div className="space-y-8">
          {/* Logo and Title */}
          <div className="text-center space-y-4">
            <div className="flex items-center justify-center space-x-2">
              <Brain size={40} className="text-orange-400" />
              <h1 className="text-2xl font-bold bg-gradient-to-r from-orange-400 via-orange-500 to-red-500 bg-clip-text text-transparent">Quizora AI</h1>
            </div>
            <p className="text-gray-400">
              Create your account and start learning
            </p>
          </div>

          {/* Register Form */}
          <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-8">
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-300 mb-2">
                    First Name
                  </label>
                  <input
                    type="text"
                    placeholder="John"
                    className={`w-full px-4 py-3 bg-white/5 border border-white/10 rounded-lg text-white placeholder-gray-500 focus:border-orange-400 focus:outline-none focus:ring-2 focus:ring-orange-400/20 ${errors.firstName ? 'border-red-500' : ''}`}
                    {...register('firstName', { required: 'First name is required' })}
                  />
                  {errors.firstName && (
                    <p className="text-red-400 text-sm mt-1">{errors.firstName.message}</p>
                  )}
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-300 mb-2">
                    Last Name
                  </label>
                  <input
                    type="text"
                    placeholder="Doe"
                    className={`w-full px-4 py-3 bg-white/5 border border-white/10 rounded-lg text-white placeholder-gray-500 focus:border-orange-400 focus:outline-none focus:ring-2 focus:ring-orange-400/20 ${errors.lastName ? 'border-red-500' : ''}`}
                    {...register('lastName', { required: 'Last name is required' })}
                  />
                  {errors.lastName && (
                    <p className="text-red-400 text-sm mt-1">{errors.lastName.message}</p>
                  )}
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-300 mb-2">
                  Email Address
                </label>
                <input
                  type="email"
                  placeholder="john@example.com"
                  className={`w-full px-4 py-3 bg-white/5 border border-white/10 rounded-lg text-white placeholder-gray-500 focus:border-orange-400 focus:outline-none focus:ring-2 focus:ring-orange-400/20 ${errors.email ? 'border-red-500' : ''}`}
                  {...register('email', {
                    required: 'Email is required',
                    pattern: {
                      value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                      message: 'Invalid email address',
                    },
                  })}
                />
                {errors.email && (
                  <p className="text-red-400 text-sm mt-1">{errors.email.message}</p>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-300 mb-2">
                  Password
                </label>
                <div className="relative">
                  <input
                    type={showPassword ? 'text' : 'password'}
                    placeholder="••••••••"
                    className={`w-full px-4 py-3 bg-white/5 border border-white/10 rounded-lg text-white placeholder-gray-500 focus:border-orange-400 focus:outline-none focus:ring-2 focus:ring-orange-400/20 ${errors.password ? 'border-red-500' : ''}`}
                    {...register('password', {
                      required: 'Password is required',
                      minLength: {
                        value: 6,
                        message: 'Password must be at least 6 characters',
                      },
                    })}
                  />
                  <button
                    type="button"
                    className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-orange-400"
                    onClick={() => setShowPassword(!showPassword)}
                  >
                    {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                  </button>
                </div>
                {errors.password && (
                  <p className="text-red-400 text-sm mt-1">{errors.password.message}</p>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-300 mb-2">
                  Confirm Password
                </label>
                <div className="relative">
                  <input
                    type={showConfirmPassword ? 'text' : 'password'}
                    placeholder="••••••••"
                    className={`w-full px-4 py-3 bg-white/5 border border-white/10 rounded-lg text-white placeholder-gray-500 focus:border-orange-400 focus:outline-none focus:ring-2 focus:ring-orange-400/20 ${errors.confirmPassword ? 'border-red-500' : ''}`}
                    {...register('confirmPassword', {
                      required: 'Please confirm your password',
                      validate: value => value === password || 'Passwords do not match',
                    })}
                  />
                  <button
                    type="button"
                    className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-orange-400"
                    onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                  >
                    {showConfirmPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                  </button>
                </div>
                {errors.confirmPassword && (
                  <p className="text-red-400 text-sm mt-1">{errors.confirmPassword.message}</p>
                )}
              </div>

              <div className="flex items-center">
                <input
                  type="checkbox"
                  className="w-4 h-4 bg-white/5 border border-white/10 rounded text-orange-400 focus:ring-orange-400 focus:ring-2"
                />
                <span className="ml-2 text-sm text-gray-400">
                  I agree to the{' '}
                  <a href="#" className="text-orange-400 hover:text-orange-300">
                    Terms of Service
                  </a>{' '}
                  and{' '}
                  <a href="#" className="text-orange-400 hover:text-orange-300">
                    Privacy Policy
                  </a>
                </span>
              </div>

              <button
                type="submit"
                disabled={isLoading}
                className="w-full py-3 px-4 bg-gradient-to-r from-orange-500 to-orange-600 text-white font-semibold rounded-lg hover:from-orange-400 hover:to-orange-500 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed border border-orange-500/20"
              >
                {isLoading ? 'Creating Account...' : 'Create Account'}
              </button>
            </form>

            <div className="mt-6 text-center">
              <p className="text-gray-400">
                Already have an account?{' '}
                <Link to="/login" className="text-orange-400 hover:text-orange-300 font-medium">
                  Sign in
                </Link>
              </p>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

export default Register;
