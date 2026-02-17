import { Brain, CheckCircle, Clock, MessageSquare, Mic, Play, RotateCcw, Video } from 'lucide-react';
import { useState } from 'react';
import toast from 'react-hot-toast';
import { useNavigate } from 'react-router-dom';
import { interviewService } from '../services/interviewService';

const Interview = () => {
  const navigate = useNavigate();
  const [showSetup, setShowSetup] = useState(true);
  const [setupData, setSetupData] = useState({
    jobRole: '',
    experience: '',
    difficulty: 'INTERMEDIATE'
  });
  const [currentSession, setCurrentSession] = useState(null);
  const [currentQuestion, setCurrentQuestion] = useState('');
  const [questionIndex, setQuestionIndex] = useState(0);
  const [totalQuestions, setTotalQuestions] = useState(10);
  const [userAnswer, setUserAnswer] = useState('');

  const startInterview = async () => {
    if (!setupData.jobRole || !setupData.experience) {
      toast.error('Please fill in all required fields');
      return;
    }

    try {
      const session = await interviewService.startInterview(setupData);
      setCurrentSession(session);
      setCurrentQuestion(session.firstQuestion || 'Tell me about your experience with React and how you\'ve used it in your previous projects.');
      setShowSetup(false);
      toast.success('Interview started!');
    } catch (error) {
      toast.error(error.message || 'Failed to start interview');
    }
  };

  const submitAnswer = async () => {
    if (!userAnswer.trim() || !currentSession) {
      toast.error('Please provide an answer');
      return;
    }

    try {
      await interviewService.submitAnswer(currentSession.id, currentQuestion, userAnswer);
      
      // Move to next question or end interview
      if (questionIndex < totalQuestions - 1) {
        setQuestionIndex(questionIndex + 1);
        setCurrentQuestion(`What is your experience with ${setupData.jobRole} technologies?`);
        setUserAnswer('');
      } else {
        endInterview();
      }
    } catch (error) {
      toast.error(error.message || 'Failed to submit answer');
    }
  };

  const endInterview = async () => {
    if (!currentSession) return;

    try {
      const report = await interviewService.getInterviewReport(currentSession.id);
      toast.success('Interview completed! Generating report...');
      // Navigate to results page or show report
      navigate('/performance');
    } catch (error) {
      toast.error(error.message || 'Failed to complete interview');
    }
  };

  if (showSetup) {
    return (
      <section className="min-h-screen bg-[#0a0a0a] py-12" data-scroll-section>
        <div className="container mx-auto px-4 sm:px-6 lg:px-8 max-w-4xl">
          <div className="space-y-8">
            {/* Header */}
            <div className="text-center space-y-4">
              <div className="flex items-center justify-center space-x-3">
                <Brain size={48} className="text-orange-400" />
                <h1 className="text-4xl font-bold bg-gradient-to-r from-orange-400 via-orange-500 to-red-500 bg-clip-text text-transparent font-mono">Interview Practice</h1>
              </div>
              <p className="text-gray-400 max-w-2xl text-lg font-mono mx-auto">
                Practice your interview skills with AI-powered questions and real-time feedback
              </p>
            </div>

            {/* Setup Form */}
            <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-8">
              <h2 className="text-2xl font-bold text-white mb-6 font-mono text-center">Setup Your Interview</h2>
              <div className="space-y-6">
                <div>
                  <label className="block text-sm font-medium text-gray-300 mb-2 font-mono text-left">Job Role *</label>
                  <input
                    type="text"
                    placeholder="e.g., Frontend Developer"
                    className="w-full px-4 py-3 bg-white/5 border border-white/10 rounded-lg text-white placeholder-gray-500 focus:border-orange-400 focus:outline-none focus:ring-2 focus:ring-orange-400/20 font-mono text-left"
                    value={setupData.jobRole}
                    onChange={(e) => setSetupData({...setupData, jobRole: e.target.value})}
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-300 mb-2 font-mono text-left">Experience Level *</label>
                  <select
                    className="w-full px-4 py-3 bg-white/5 border border-white/10 rounded-lg text-white focus:border-orange-400 focus:outline-none focus:ring-2 focus:ring-orange-400/20 font-mono text-left"
                    value={setupData.experience}
                    onChange={(e) => setSetupData({...setupData, experience: e.target.value})}
                  >
                    <option value="">Select experience</option>
                    <option value="ENTRY">Entry Level</option>
                    <option value="MID">Mid Level</option>
                    <option value="SENIOR">Senior Level</option>
                  </select>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-300 mb-2 font-mono text-left">Difficulty Level</label>
                  <select
                    className="w-full px-4 py-3 bg-white/5 border border-white/10 rounded-lg text-white focus:border-orange-400 focus:outline-none focus:ring-2 focus:ring-orange-400/20 font-mono text-left"
                    value={setupData.difficulty}
                    onChange={(e) => setSetupData({...setupData, difficulty: e.target.value})}
                  >
                    <option value="BEGINNER">Beginner</option>
                    <option value="INTERMEDIATE">Intermediate</option>
                    <option value="ADVANCED">Advanced</option>
                  </select>
                </div>

                <button
                  onClick={startInterview}
                  className="w-full py-3 px-4 bg-gradient-to-r from-orange-500 to-orange-600 text-white font-semibold rounded-lg hover:from-orange-400 hover:to-orange-500 transition-all duration-200 border border-orange-500/20 flex items-center justify-center font-mono"
                >
                  <Play className="mr-2" size={20} />
                  Start Interview
                </button>
              </div>
            </div>
          </div>
        </div>
      </section>
    );
  }

  // Interview Interface
  return (
    <section className="min-h-screen bg-[#0a0a0a] py-12" data-scroll-section>
      <div className="container mx-auto px-4 sm:px-6 lg:px-8 max-w-4xl">
        <div className="space-y-6">
          {/* Interview Header */}
          <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-6">
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-4">
                <div className="flex items-center space-x-3">
                  <Video className="text-orange-400" size={24} />
                  <div>
                    <h2 className="text-xl font-bold text-white font-mono">Interview in Progress</h2>
                    <p className="text-gray-400 text-sm font-mono">{setupData.jobRole} - {setupData.experience}</p>
                  </div>
                </div>
              </div>
              <div className="flex items-center space-x-4">
                <div className="flex items-center space-x-2 text-gray-400 font-mono">
                  <Clock size={16} />
                  <span className="text-sm font-mono">00:00</span>
                </div>
                <button className="p-2 bg-white/5 border border-white/10 rounded-lg hover:border-orange-400/50 transition-all">
                  <RotateCcw size={16} className="text-gray-400" />
                </button>
              </div>
            </div>
          </div>

          {/* Main Interview Area */}
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            {/* Video Area */}
            <div className="lg:col-span-2">
              <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl aspect-video flex items-center justify-center">
                <div className="text-center space-y-4">
                  <Video size={48} className="text-orange-400 mx-auto" />
                  <p className="text-gray-400">Camera preview will appear here</p>
                  <button className="px-4 py-2 bg-orange-500 text-white rounded-lg hover:bg-orange-400 transition-colors">
                    Enable Camera
                  </button>
                </div>
              </div>

              {/* Current Question */}
              <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-6">
                <h3 className="text-lg font-semibold text-white mb-4 font-mono text-left">Current Question</h3>
                <p className="text-gray-300 mb-6 font-mono text-left">
                  {currentQuestion}
                </p>
                
                <textarea
                  value={userAnswer}
                  onChange={(e) => setUserAnswer(e.target.value)}
                  placeholder="Type your answer here..."
                  className="w-full px-4 py-3 bg-white/5 border border-white/10 rounded-lg text-white placeholder-gray-500 focus:border-orange-400 focus:outline-none focus:ring-2 focus:ring-orange-400/20 font-mono mb-4"
                  rows={4}
                />
                
                <div className="flex items-center space-x-4">
                  <button className="p-2 bg-white/5 border border-white/10 rounded-lg hover:border-orange-400/50 transition-all">
                    <Mic size={16} className="text-gray-400" />
                  </button>
                  <button className="p-2 bg-white/5 border border-white/10 rounded-lg hover:border-orange-400/50 transition-all">
                    <MessageSquare size={16} className="text-gray-400" />
                  </button>
                  <button 
                    onClick={submitAnswer}
                    className="px-4 py-2 bg-orange-500 text-white rounded-lg hover:bg-orange-400 transition-colors font-mono"
                  >
                    Next Question
                  </button>
                </div>
              </div>
            </div>

            {/* Sidebar */}
            <div className="space-y-6">
              {/* AI Assistant */}
              <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-6">
                <div className="flex items-center space-x-3 mb-4">
                  <Brain size={20} className="text-orange-400" />
                  <h3 className="text-lg font-semibold text-white font-mono text-left">AI Assistant</h3>
                </div>
                <div className="space-y-3">
                  <div className="bg-orange-500/10 border border-orange-500/20 rounded-lg p-3">
                    <p className="text-orange-400 text-sm font-medium font-mono text-left">Tip:</p>
                    <p className="text-gray-300 text-sm mt-1 font-mono text-left">
                      Speak clearly and structure your answers using the STAR method.
                    </p>
                  </div>
                  <div className="bg-white/5 rounded-lg p-3">
                    <p className="text-gray-400 text-sm font-mono text-left">
                      The AI is analyzing your responses and will provide feedback at the end.
                    </p>
                  </div>
                </div>
              </div>

              {/* Progress */}
              <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-6">
                <h3 className="text-lg font-semibold text-white mb-4 font-mono text-left">Progress</h3>
                <div className="space-y-3">
                  <div className="flex items-center justify-between">
                    <span className="text-gray-400 text-sm font-mono text-left">Questions Answered</span>
                    <span className="text-orange-400 text-sm font-medium font-mono text-right">{questionIndex + 1}/{totalQuestions}</span>
                  </div>
                  <div className="w-full bg-white/10 rounded-full h-2">
                    <div className="bg-gradient-to-r from-orange-500 to-orange-600 h-2 rounded-full" style={{width: `${((questionIndex + 1) / totalQuestions) * 100}%`}}></div>
                  </div>
                  <div className="space-y-2 mt-4">
                    {Array.from({length: totalQuestions}, (_, i) => (
                      <div key={i} className={`w-8 h-8 rounded-full flex items-center justify-center text-xs font-medium ${
                        i <= questionIndex ? 'bg-orange-500 text-white' : 'bg-white/10 text-gray-500'
                      }`}>
                        {i <= questionIndex ? <CheckCircle size={12} /> : i + 1}
                      </div>
                    ))}
                  </div>
                </div>
              </div>

              {/* End Interview */}
              <button 
                onClick={endInterview}
                className="w-full py-3 px-4 bg-red-500/10 border border-red-500/20 text-red-400 rounded-lg hover:bg-red-500/20 transition-colors font-mono"
              >
                End Interview
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};



export default Interview;
