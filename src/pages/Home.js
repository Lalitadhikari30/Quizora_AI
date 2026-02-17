import { motion } from 'framer-motion';
import {
  ArrowRight,
  Award,
  BookOpen,
  Brain,
  CheckCircle,
  Play,
  Star,
  Target,
  Users,
  Zap
} from 'lucide-react';
import { Link } from 'react-router-dom';

const Home = () => {
  const features = [
    {
      icon: Brain,
      title: 'AI-Powered Generation',
      description: 'Advanced AI algorithms create personalized quizzes and interview questions tailored to your learning goals.',
    },
    {
      icon: BookOpen,
      title: 'Multiple Content Sources',
      description: 'Upload PDFs, paste text, or use YouTube links to generate comprehensive learning materials.',
    },
    {
      icon: Target,
      title: 'Adaptive Learning',
      description: 'Our system adapts to your performance, providing questions that match your skill level.',
    },
    {
      icon: Zap,
      title: 'Instant Feedback',
      description: 'Get immediate feedback on your answers with detailed explanations and improvement suggestions.',
    },
    {
      icon: Users,
      title: 'Interview Simulation',
      description: 'Practice real-world interview scenarios with AI-powered question generation and evaluation.',
    },
    {
      icon: Award,
      title: 'Performance Tracking',
      description: 'Monitor your progress with detailed analytics and performance insights.',
    },
  ];

  const stats = [
    { label: 'Active Users', value: '10,000+', icon: Users },
    { label: 'Quizzes Generated', value: '50,000+', icon: BookOpen },
    { label: 'Success Rate', value: '95%', icon: Target },
    { label: 'Satisfaction', value: '4.9/5', icon: Star },
  ];

  const testimonials = [
    {
      name: 'Sarah Johnson',
      role: 'Software Developer',
      content: 'Quizora AI helped me prepare for my technical interviews. The AI-generated questions were incredibly realistic!',
      rating: 5,
    },
    {
      name: 'Michael Chen',
      role: 'Student',
      content: 'I love how I can upload my study materials and get personalized quizzes. It\'s revolutionized my study routine.',
      rating: 5,
    },
    {
      name: 'Emily Davis',
      role: 'HR Manager',
      content: 'We use Quizora AI for candidate assessments. The interview simulation feature is outstanding!',
      rating: 5,
    },
  ];

  return (
    <div>
      {/* Hero Section - DentWise Style */}
      <section className="relative bg-[#0a0a0a] h-screen w-full overflow-hidden font-mono" data-scroll-section>
        {/* Grid Background */}
        <div className="absolute inset-0 w-full h-full" style={{
          backgroundImage: `linear-gradient(rgba(255, 255, 255, 0.03) 1px, transparent 1px), linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px)`,
          backgroundSize: '60px 60px'
        }}></div>
        
        {/* Radial Gradient Overlay */}
        <div className="absolute inset-0 w-full h-full bg-[radial-gradient(ellipse_80%_50%_at_50%_-20%,rgba(249,115,22,0.15),transparent)]"></div>
        
        {/* Glow Effects */}
        <div className="absolute top-1/4 right-1/4 w-96 h-96 bg-orange-500/10 rounded-full blur-3xl" data-scroll data-scroll-speed="2"></div>
        <div className="absolute bottom-1/4 left-1/4 w-96 h-96 bg-purple-500/10 rounded-full blur-3xl" data-scroll data-scroll-speed="1"></div>

        <div className="relative z-10 h-full w-full flex items-center">
          <div className="container mx-auto px-4 sm:px-6 lg:px-8 w-full">
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 items-center">
              <div className="space-y-8" data-scroll data-scroll-speed="1">
                {/* Badge */}
                <motion.div 
                  className="inline-flex items-center gap-2 bg-white/5 border border-white/10 rounded-full px-4 py-2"
                  initial={{ opacity: 0, y: -20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ duration: 0.6, delay: 0.2 }}
                >
                  <span className="w-2 h-2 bg-orange-400 rounded-full animate-pulse"></span>
                  <span className="text-sm text-gray-300">AI-Powered Learning Platform</span>
                </motion.div>
                
                {/* Main Heading - Monospace Style */}
                <motion.h1 
                  className="text-4xl sm:text-5xl lg:text-6xl font-bold text-white leading-tight tracking-tight font-mono"
                  initial={{ opacity: 0, y: 30 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ duration: 0.8, delay: 0.3 }}
                >
                  Master your
                  <span className="block mt-2">
                    <span className="bg-gradient-to-r from-orange-400 via-orange-500 to-red-500 bg-clip-text text-transparent">
                      skills
                    </span>
                    <span className="text-gray-400"> with AI</span>
                  </span>
                </motion.h1>
                
                {/* Subtitle */}
                <motion.p 
                  className="text-lg text-gray-400 max-w-lg leading-relaxed"
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ duration: 0.6, delay: 0.4 }}
                >
                  Chat with our AI learning assistant for instant quizzes, realistic interviews, 
                  and personalized recommendations. Available 24/7.
                </motion.p>
                
                {/* CTA Buttons */}
                <motion.div 
                  className="flex flex-col sm:flex-row gap-4"
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ duration: 0.6, delay: 0.5 }}
                >
                  <Link
                    to="/register"
                    className="group inline-flex items-center justify-center px-6 py-3 font-semibold text-white bg-gradient-to-r from-orange-500 to-orange-600 rounded-lg hover:from-orange-400 hover:to-orange-500 transition-all duration-200 border border-orange-500/20"
                  >
                    <Brain size={20} className="mr-2" />
                    Try AI Quiz
                  </Link>
                  <button className="inline-flex items-center justify-center px-6 py-3 font-semibold text-gray-300 bg-white/5 border border-white/10 rounded-lg hover:bg-white/10 hover:text-white transition-all duration-200">
                    <Play size={20} className="mr-2" />
                    Watch Demo
                  </button>
                </motion.div>

                {/* Trust Section */}
                <motion.div 
                  className="flex items-center gap-4 pt-4"
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ duration: 0.6, delay: 0.6 }}
                >
                  {/* Avatar Group */}
                  <div className="flex -space-x-2">
                    <img src="https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&h=100&fit=crop&crop=face" alt="User" className="w-8 h-8 rounded-full border-2 border-[#0a0a0a] object-cover" />
                    <img src="https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&h=100&fit=crop&crop=face" alt="User" className="w-8 h-8 rounded-full border-2 border-[#0a0a0a] object-cover" />
                    <img src="https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100&h=100&fit=crop&crop=face" alt="User" className="w-8 h-8 rounded-full border-2 border-[#0a0a0a] object-cover" />
                    <img src="https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop&crop=face" alt="User" className="w-8 h-8 rounded-full border-2 border-[#0a0a0a] object-cover" />
                  </div>
                  <div>
                    <div className="flex items-center gap-1">
                      {[...Array(5)].map((_, i) => (
                        <Star key={i} size={14} className="text-yellow-400 fill-current" />
                      ))}
                      <span className="text-white font-semibold ml-1">4.9/5</span>
                    </div>
                    <p className="text-gray-500 text-sm">Trusted by 1,200+ learners</p>
                  </div>
                </motion.div>
              </div>

              {/* Right Side - Visual */}
              <motion.div 
                className="relative hidden lg:block"
                initial={{ opacity: 0, x: 50 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ duration: 0.8, delay: 0.4 }}
              >
                <div className="relative">
                  {/* Main Card */}
                  <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-6 space-y-4">
                    {/* Chat Header */}
                    <div className="flex items-center gap-3 pb-4 border-b border-white/10">
                      <div className="w-10 h-10 bg-gradient-to-br from-orange-500 to-red-500 rounded-lg flex items-center justify-center">
                        <Brain size={24} className="text-white" />
                      </div>
                      <div>
                        <h3 className="text-white font-semibold">Quizora AI</h3>
                        <p className="text-gray-400 text-sm">Online</p>
                      </div>
                    </div>

                    {/* Chat Messages */}
                    <div className="space-y-3">
                      <div className="bg-white/5 rounded-lg p-3 max-w-[80%]">
                        <p className="text-gray-300 text-sm">Generate a quiz about React Hooks?</p>
                      </div>
                      <div className="bg-orange-500/20 rounded-lg p-3 max-w-[80%] ml-auto border border-orange-500/20">
                        <p className="text-orange-200 text-sm">Creating 10 questions on useState, useEffect, and custom hooks...</p>
                        <div className="flex gap-1 mt-2">
                          <span className="w-1.5 h-1.5 bg-orange-400 rounded-full animate-bounce"></span>
                          <span className="w-1.5 h-1.5 bg-orange-400 rounded-full animate-bounce delay-100"></span>
                          <span className="w-1.5 h-1.5 bg-orange-400 rounded-full animate-bounce delay-200"></span>
                        </div>
                      </div>
                    </div>

                    {/* Progress */}
                    <div className="space-y-2 pt-2">
                      <div className="flex justify-between text-xs">
                        <span className="text-gray-400">Generating quiz...</span>
                        <span className="text-orange-400">75%</span>
                      </div>
                      <div className="w-full bg-white/10 rounded-full h-1.5">
                        <div className="bg-gradient-to-r from-orange-500 to-red-500 h-1.5 rounded-full" style={{ width: '75%' }}></div>
                      </div>
                    </div>
                  </div>

                  {/* Floating Badge */}
                  <motion.div 
                    className="absolute -top-4 -right-4 bg-white/10 backdrop-blur-xl border border-white/20 rounded-xl px-4 py-2"
                    initial={{ opacity: 0, scale: 0.8 }}
                    animate={{ opacity: 1, scale: 1 }}
                    transition={{ duration: 0.5, delay: 0.8 }}
                  >
                    <div className="flex items-center gap-2">
                      <Target size={16} className="text-green-400" />
                      <span className="text-white text-sm font-medium">Quiz Ready!</span>
                    </div>
                  </motion.div>

                  {/* Stats Badge */}
                  <motion.div 
                    className="absolute -bottom-4 -left-4 bg-white/10 backdrop-blur-xl border border-white/20 rounded-xl px-4 py-3"
                    initial={{ opacity: 0, scale: 0.8 }}
                    animate={{ opacity: 1, scale: 1 }}
                    transition={{ duration: 0.5, delay: 1 }}
                  >
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 bg-orange-500/20 rounded-lg flex items-center justify-center">
                        <Users size={20} className="text-orange-400" />
                      </div>
                      <div>
                        <p className="text-white font-semibold">10K+</p>
                        <p className="text-gray-400 text-xs">Active learners</p>
                      </div>
                    </div>
                  </motion.div>
                </div>
              </motion.div>
            </div>
          </div>
        </div>
      </section>

      {/* Stats Section */}
      <motion.section 
        className="py-16 bg-[#0a0a0a] border-y border-white/5"
        initial={{ opacity: 0 }}
        whileInView={{ opacity: 1 }}
        transition={{ duration: 0.6 }}
        viewport={{ once: true }}
      >
        <div className="container mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-8">
            {stats.map((stat, index) => {
              const Icon = stat.icon;
              return (
                <motion.div 
                  key={stat.label} 
                  className="text-center space-y-2"
                  initial={{ opacity: 0, y: 20 }}
                  whileInView={{ opacity: 1, y: 0 }}
                  transition={{ duration: 0.5, delay: index * 0.1 }}
                  viewport={{ once: true }}
                >
                  <Icon size={32} className="text-orange-400 mx-auto" />
                  <h3 className="text-3xl font-bold text-white font-mono">{stat.value}</h3>
                  <p className="text-gray-500">{stat.label}</p>
                </motion.div>
              );
            })}
          </div>
        </div>
      </motion.section>

      {/* Features Section */}
      <section className="py-20 bg-[#0a0a0a] relative" data-scroll-section>
        <div className="absolute inset-0" style={{
          backgroundImage: `linear-gradient(rgba(255, 255, 255, 0.02) 1px, transparent 1px), linear-gradient(90deg, rgba(255, 255, 255, 0.02) 1px, transparent 1px)`,
          backgroundSize: '60px 60px'
        }}></div>
        <div className="container mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
          <div className="text-center space-y-4 mb-12">
            <h2 className="text-3xl lg:text-4xl font-bold text-white font-mono" data-scroll data-scroll-speed="1">Powerful Features</h2>
            <motion.p className="text-lg text-gray-500 max-w-2xl mx-auto" data-scroll data-scroll-delay="0.2">
              Everything you need to accelerate your learning and career growth
            </motion.p>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {features.map((feature, index) => {
              const Icon = feature.icon;
              return (
                <motion.div
                  key={feature.title}
                  className="bg-white/5 backdrop-blur border border-white/10 rounded-xl p-6 hover:border-orange-500/30 transition-all duration-300 group"
                  data-scroll data-scroll-speed="1"
                  initial={{ opacity: 0, y: 30 }}
                  whileInView={{ opacity: 1, y: 0 }}
                  transition={{ duration: 0.6, delay: index * 0.1 }}
                  viewport={{ once: true }}
                >
                  <div className="space-y-4">
                    <div className="w-12 h-12 bg-orange-500/10 rounded-xl flex items-center justify-center group-hover:bg-orange-500/20 transition-colors">
                      <Icon size={24} className="text-orange-400" />
                    </div>
                    <h3 className="text-xl font-semibold text-white font-mono">{feature.title}</h3>
                    <p className="text-gray-500">{feature.description}</p>
                  </div>
                </motion.div>
              );
            })}
          </div>
        </div>
      </section>

      {/* Testimonials Section */}
      <section className="py-20 bg-[#0a0a0a]" data-scroll-section>
        <div className="container mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center space-y-4 mb-12">
            <h2 className="text-3xl lg:text-4xl font-bold text-white font-mono" data-scroll data-scroll-speed="1">What Our Users Say</h2>
            <p className="text-lg text-gray-500" data-scroll data-scroll-delay="0.2">
              Join thousands of satisfied learners transforming their careers
            </p>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {testimonials.map((testimonial, index) => (
              <motion.div key={testimonial.name} className="bg-white/5 backdrop-blur border border-white/10 rounded-xl p-6 hover:border-orange-500/20 transition-all" data-scroll data-scroll-speed="1"
                initial={{ opacity: 0, y: 30 }}
                whileInView={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.6, delay: index * 0.1 }}
                viewport={{ once: true }}
              >
                <div className="space-y-4">
                  <div className="flex">
                    {[...Array(testimonial.rating)].map((_, i) => (
                      <Star key={i} size={16} className="text-yellow-400 fill-current" />
                    ))}
                  </div>
                  <p className="text-gray-400">"{testimonial.content}"</p>
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 bg-orange-500/20 rounded-full flex items-center justify-center">
                      <span className="text-orange-400 font-mono font-bold">{testimonial.name.charAt(0)}</span>
                    </div>
                    <div>
                      <p className="font-semibold text-white font-mono">{testimonial.name}</p>
                      <p className="text-gray-500 text-sm">{testimonial.role}</p>
                    </div>
                  </div>
                </div>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20 bg-[#0a0a0a] relative overflow-hidden" data-scroll-section>
        <div className="absolute inset-0 bg-[radial-gradient(ellipse_60%_40%_at_50%_50%,rgba(249,115,22,0.1),transparent)]"></div>
        <div className="container mx-auto px-4 sm:px-6 lg:px-8 text-center relative z-10">
          <div className="space-y-8 max-w-3xl mx-auto">
            <motion.h2 
              className="text-3xl lg:text-5xl font-bold text-white font-mono" data-scroll data-scroll-speed="1"
              initial={{ opacity: 0, y: 30 }}
              whileInView={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.8 }}
              viewport={{ once: true }}
            >
              Ready to Transform Your
              <span className="block mt-2 bg-gradient-to-r from-orange-400 via-orange-500 to-red-500 bg-clip-text text-transparent">
                Learning Journey?
              </span>
            </motion.h2>
            <motion.p 
              className="text-lg text-gray-500" data-scroll data-scroll-delay="0.2"
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.6 }}
              viewport={{ once: true }}
            >
              Join thousands of learners who are already using Quizora AI to 
              accelerate their career growth and master new skills.
            </motion.p>
            <motion.div 
              className="flex flex-col sm:flex-row gap-4 justify-center" data-scroll data-scroll-delay="0.3"
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.6 }}
              viewport={{ once: true }}
            >
              <Link
                to="/register"
                className="inline-flex items-center justify-center px-8 py-4 font-semibold text-white bg-gradient-to-r from-orange-500 to-orange-600 rounded-lg hover:from-orange-400 hover:to-orange-500 transition-all border border-orange-500/20"
              >
                Start Free Trial
                <ArrowRight size={20} className="ml-2" />
              </Link>
              <button className="inline-flex items-center justify-center px-8 py-4 font-semibold text-gray-300 bg-white/5 border border-white/10 rounded-lg hover:bg-white/10 hover:text-white transition-all">
                Learn More
              </button>
            </motion.div>
            <motion.div 
              className="flex flex-wrap justify-center gap-6 pt-4" data-scroll data-scroll-delay="0.4"
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.6 }}
              viewport={{ once: true }}
            >
              <div className="flex items-center gap-2 text-gray-500">
                <CheckCircle size={18} className="text-green-400" />
                <span>No credit card required</span>
              </div>
              <div className="flex items-center gap-2 text-gray-500">
                <CheckCircle size={18} className="text-green-400" />
                <span>14-day free trial</span>
              </div>
              <div className="flex items-center gap-2 text-gray-500">
                <CheckCircle size={18} className="text-green-400" />
                <span>Cancel anytime</span>
              </div>
            </motion.div>
          </div>
        </div>
      </section>
    </div>
  );
};

export default Home;
