import {
    BookOpen,
    Brain,
    ChevronDown,
    LogOut,
    Menu,
    Settings,
    TrendingUp,
    User,
    X
} from 'lucide-react';
import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

const Navbar = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const { isAuthenticated, userName } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('userName');
    navigate('/login');
    window.location.reload(); // Force reload to update auth state
  };

  const navItems = [
    { name: 'Dashboard', path: '/dashboard', icon: BookOpen },
    { name: 'Create Quiz', path: '/quiz/generate', icon: Brain },
    { name: 'Interview', path: '/interview', icon: User },
    { name: 'Performance', path: '/performance', icon: TrendingUp },
  ];

  return (
    <div className="sticky top-0 z-50 bg-[#0a0a0a]/80 backdrop-blur-xl border-b border-white/10">
      <div className="container mx-auto px-4">
        <div className="flex h-16 items-center justify-between">
          {/* Logo */}
          <Link 
            to="/" 
            className="flex items-center space-x-2 text-2xl font-bold bg-gradient-to-r from-orange-400 via-orange-500 to-red-500 bg-clip-text text-transparent hover:opacity-80 transition-opacity font-mono"
          >
            <Brain size={32} className="text-orange-400" />
            <span>Quizora AI</span>
          </Link>

          {/* Desktop Navigation */}
          <nav className="hidden md:flex items-center space-x-8">
            {isAuthenticated && navItems.map((item) => {
              const Icon = item.icon;
              return (
                <Link
                  key={item.name}
                  to={item.path}
                  className={`flex items-center space-x-2 font-medium transition-colors font-mono ${
                    location.pathname === item.path 
                      ? 'text-orange-400' 
                      : 'text-gray-400 hover:text-orange-400'
                  }`}
                >
                  <Icon size={18} />
                  <span>{item.name}</span>
                </Link>
              );
            })}
          </nav>

          {/* Desktop Auth */}
          <div className="hidden md:flex items-center space-x-4">
            {isAuthenticated ? (
              <div className="relative">
                <button
                  onClick={() => setIsProfileOpen(!isProfileOpen)}
                  className="flex items-center space-x-2 bg-white/5 backdrop-blur-xl border border-white/10 rounded-lg px-4 py-2 hover:border-orange-400/50 transition-all font-mono"
                >
                  <div className="w-8 h-8 bg-gradient-to-br from-orange-500 to-red-500 rounded-full flex items-center justify-center text-white font-bold">
                    {userName.charAt(0).toUpperCase()}
                  </div>
                  <span className="text-gray-300">{userName}</span>
                  <ChevronDown size={16} className="text-gray-400" />
                </button>
                
                {isProfileOpen && (
                  <div className="absolute right-0 mt-2 w-48 bg-[#0a0a0a]/95 backdrop-blur-xl border border-white/10 rounded-lg shadow-lg py-2 z-50">
                    <Link
                      to="/profile"
                      className="flex items-center space-x-2 px-4 py-2 text-gray-300 hover:bg-white/5 hover:text-orange-400 transition-colors font-mono"
                      onClick={() => setIsProfileOpen(false)}
                    >
                      <User size={16} />
                      <span>Profile</span>
                    </Link>
                    <Link
                      to="/settings"
                      className="flex items-center space-x-2 px-4 py-2 text-gray-300 hover:bg-white/5 hover:text-orange-400 transition-colors font-mono"
                      onClick={() => setIsProfileOpen(false)}
                    >
                      <Settings size={16} />
                      <span>Settings</span>
                    </Link>
                    <hr className="my-2 border-white/10" />
                    <button
                      onClick={handleLogout}
                      className="flex items-center space-x-2 px-4 py-2 hover:bg-white/5 hover:text-red-400 transition-colors w-full text-left text-gray-300 font-mono"
                    >
                      <LogOut size={16} />
                      <span>Logout</span>
                    </button>
                  </div>
                )}
              </div>
            ) : (
              <>
                <Link
                  to="/login"
                  className="px-4 py-2 bg-white/5 backdrop-blur-xl border border-white/10 rounded-lg text-gray-300 hover:border-orange-400/50 hover:text-orange-400 transition-all font-mono"
                >
                  Login
                </Link>
                <Link
                  to="/register"
                  className="px-4 py-2 bg-gradient-to-r from-orange-500 to-orange-600 text-white rounded-lg hover:from-orange-400 hover:to-orange-500 transition-all font-mono border border-orange-500/20"
                >
                  Sign Up
                </Link>
              </>
            )}
          </div>

          {/* Mobile menu button */}
          <button
            onClick={() => setIsMenuOpen(!isMenuOpen)}
            className="md:hidden p-2 bg-white/5 backdrop-blur-xl border border-white/10 rounded-lg hover:border-orange-400/50 transition-all"
          >
            {isMenuOpen ? <X size={24} className="text-orange-400" /> : <Menu size={24} className="text-gray-400" />}
          </button>
        </div>
      </div>

      {/* Mobile Navigation */}
      {isMenuOpen && (
        <div className="md:hidden bg-[#0a0a0a]/95 backdrop-blur-xl border-t border-white/10">
          <div className="container mx-auto px-4 py-4">
            <nav className="space-y-4">
              {isAuthenticated &&
                navItems.map((item) => {
                  const Icon = item.icon;
                  return (
                    <Link
                      key={item.name}
                      to={item.path}
                      onClick={() => setIsMenuOpen(false)}
                      className={`flex items-center space-x-3 p-3 rounded-lg transition-colors font-mono ${
                        location.pathname === item.path
                          ? 'bg-orange-500/20 text-orange-400 font-semibold border border-orange-500/30'
                          : 'bg-white/5 text-gray-400 hover:bg-white/10 hover:text-orange-400 border border-white/10'
                      }`}
                    >
                      <Icon size={20} />
                      <span>{item.name}</span>
                    </Link>
                  );
                })}
              
              {isAuthenticated ? (
                <>
                  <Link
                    to="/profile"
                    onClick={() => setIsMenuOpen(false)}
                    className="flex items-center space-x-3 p-3 rounded-lg bg-white/5 text-gray-400 hover:bg-white/10 hover:text-orange-400 transition-colors border border-white/10 font-mono"
                  >
                    <User size={20} />
                    <span>Profile</span>
                  </Link>
                  <button
                    onClick={handleLogout}
                    className="flex items-center space-x-3 p-3 rounded-lg bg-white/5 text-gray-400 hover:bg-white/10 hover:text-red-400 transition-colors w-full text-left border border-white/10 font-mono"
                  >
                    <LogOut size={20} />
                    <span>Logout</span>
                  </button>
                </>
              ) : (
                <>
                  <Link
                    to="/login"
                    onClick={() => setIsMenuOpen(false)}
                    className="block p-3 rounded-lg bg-white/5 text-gray-400 hover:bg-white/10 hover:text-orange-400 transition-colors border border-white/10 font-mono text-center"
                  >
                    Login
                  </Link>
                  <Link
                    to="/register"
                    onClick={() => setIsMenuOpen(false)}
                    className="block p-3 rounded-lg bg-gradient-to-r from-orange-500 to-orange-600 text-white hover:from-orange-400 hover:to-orange-500 transition-colors border border-orange-500/20 font-mono text-center"
                  >
                    Sign Up
                  </Link>
                </>
              )}
            </nav>
          </div>
        </div>
      )}
    </div>
  );
};

export default Navbar;
