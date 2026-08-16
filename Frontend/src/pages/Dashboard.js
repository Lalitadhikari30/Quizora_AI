import {
  Award,
  BookOpen,
  Brain,
  Clock,
  Compass,
  PlusCircle,
  Sparkles,
  Target,
  TrendingUp,
  Users
} from 'lucide-react';
import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { performanceService } from '../services/performanceService';
import { quizService } from '../services/quizService';
import { interviewService } from '../services/interviewService';

const Dashboard = () => {
  const navigate = useNavigate();
  const [userName, setUserName] = useState('');
  const [stats, setStats] = useState({
    totalQuizzes: 0,
    completedQuizzes: 0,
    averageScore: 0,
    interviewSessions: 0,
    studyStreak: 0,
    totalStudyTime: 0,
  });
  const [recentActivities, setRecentActivities] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Get user name from storage
    const storedName = localStorage.getItem('userName');
    if (storedName) {
      setUserName(storedName);
    }

    const fetchDashboardData = async () => {
      try {
        const [performanceData, quizzesData, interviewsData] = await Promise.all([
          performanceService.getUserPerformance().catch(() => ({})),
          quizService.getUserQuizzes().catch(() => []),
          interviewService.getUserInterviews().catch(() => ({}))
        ]);

        const quizzesList = Array.isArray(quizzesData) 
          ? quizzesData 
          : (quizzesData?.quizzes || quizzesData?.data || []);

        const interviewsList = Array.isArray(interviewsData?.interviews)
          ? interviewsData.interviews
          : Array.isArray(interviewsData)
          ? interviewsData
          : (interviewsData?.data || []);

        // Real Stats from Database
        const completedQuizzes = performanceData?.totalQuizzesTaken ?? performanceData?.totalQuizzes ?? 0;
        const totalQuizzesCreated = Math.max(quizzesList.length, completedQuizzes);
        const avgScore = Math.round(performanceData?.averageQuizScore ?? performanceData?.averageScore ?? 0);
        const interviewsCount = Math.max(interviewsList.length, performanceData?.totalInterviewsTaken ?? 0);
        const streak = performanceData?.studyStreak ?? (completedQuizzes > 0 ? 1 : 0);
        const studyTime = performanceData?.totalStudyTime ?? Math.round((completedQuizzes * 0.25 + interviewsCount * 0.5) * 10) / 10;

        setStats({
          totalQuizzes: totalQuizzesCreated,
          completedQuizzes: completedQuizzes,
          averageScore: avgScore,
          interviewSessions: interviewsCount,
          studyStreak: streak,
          totalStudyTime: studyTime,
        });

        // Combine and map recent user activities
        const activities = [];

        quizzesList.slice(0, 5).forEach((quiz, index) => {
          activities.push({
            id: `quiz-${quiz.id || index}`,
            type: 'quiz',
            title: quiz.title || 'Interactive Quiz',
            score: null,
            date: quiz.createdAt ? new Date(quiz.createdAt).toLocaleDateString() : 'Recent',
            rawDate: quiz.createdAt ? new Date(quiz.createdAt) : new Date(0),
            duration: quiz.questions?.length ? `${quiz.questions.length} questions` : '10 questions',
            icon: BookOpen,
            link: `/quiz/${quiz.id}`
          });
        });

        interviewsList.slice(0, 5).forEach((interview, index) => {
          activities.push({
            id: `interview-${interview.id || index}`,
            type: 'interview',
            title: `Mock Interview: ${interview.jobRole || 'Technical Session'}`,
            score: interview.totalScore ? `${interview.totalScore}/10` : null,
            date: interview.startedAt ? new Date(interview.startedAt).toLocaleDateString() : 'Recent',
            rawDate: interview.startedAt ? new Date(interview.startedAt) : new Date(0),
            duration: interview.difficulty ? `${interview.difficulty} Level` : 'Mid Level',
            icon: Brain,
            link: '/interview'
          });
        });

        activities.sort((a, b) => b.rawDate - a.rawDate);
        setRecentActivities(activities.slice(0, 5));

      } catch (error) {
        console.error('Failed to fetch dashboard data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

  const quickActions = [
    {
      title: 'Generate Quiz',
      description: 'Create custom quizzes from text or documents',
      icon: Sparkles,
      link: '/quiz/generate',
      badge: 'AI Powered'
    },
    {
      title: 'Mock Interview',
      description: 'Practice real-time speech and technical simulations',
      icon: Users,
      link: '/interview',
      badge: 'Voice & Video'
    },
    {
      title: 'Performance Analytics',
      description: 'Detailed score progressions & topic mastery',
      icon: TrendingUp,
      link: '/performance',
      badge: 'Insights'
    },
  ];

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-[#0a0a0a]">
        <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-orange-500"></div>
      </div>
    );
  }

  return (
    <section className="bg-[#0a0a0a] min-h-screen py-12 font-mono">
      <div className="container mx-auto px-4 max-w-7xl space-y-10">

        {/* HEADER */}
        <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 bg-white/5 border border-white/10 rounded-2xl p-8 backdrop-blur">
          <div className="space-y-1">
            <h1 className="text-3xl md:text-4xl font-bold bg-gradient-to-r from-orange-400 via-orange-500 to-red-500 bg-clip-text text-transparent">
              Welcome back{userName ? `, ${userName}` : ''}!
            </h1>
            <p className="text-gray-400 text-sm">
              Track your personalized quiz progress and AI mock interview evaluations in real time
            </p>
          </div>

          <button
            onClick={() => navigate('/quiz/generate')}
            className="px-5 py-3 bg-orange-500 hover:bg-orange-600 text-white rounded-xl text-sm font-semibold transition flex items-center gap-2 shadow-lg shadow-orange-500/20"
          >
            <PlusCircle size={18} /> New Quiz
          </button>
        </div>

        {/* REAL STATS CARDS */}
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-5">

          <div className="bg-white/5 border border-white/10 rounded-2xl p-5 backdrop-blur">
            <div className="flex justify-between items-start">
              <div>
                <p className="text-gray-400 text-xs">Total Quizzes</p>
                <p className="text-2xl font-bold text-white mt-1">{stats.totalQuizzes}</p>
              </div>
              <BookOpen className="text-orange-400" size={20} />
            </div>
          </div>

          <div className="bg-white/5 border border-white/10 rounded-2xl p-5 backdrop-blur">
            <div className="flex justify-between items-start">
              <div>
                <p className="text-gray-400 text-xs">Completed</p>
                <p className="text-2xl font-bold text-white mt-1">{stats.completedQuizzes}</p>
              </div>
              <Target className="text-green-400" size={20} />
            </div>
          </div>

          <div className="bg-white/5 border border-white/10 rounded-2xl p-5 backdrop-blur">
            <div className="flex justify-between items-start">
              <div>
                <p className="text-gray-400 text-xs">Average Score</p>
                <p className="text-2xl font-bold text-white mt-1">{stats.averageScore}%</p>
              </div>
              <Award className="text-yellow-400" size={20} />
            </div>
          </div>

          <div className="bg-white/5 border border-white/10 rounded-2xl p-5 backdrop-blur">
            <div className="flex justify-between items-start">
              <div>
                <p className="text-gray-400 text-xs">Interviews</p>
                <p className="text-2xl font-bold text-white mt-1">{stats.interviewSessions}</p>
              </div>
              <Brain className="text-purple-400" size={20} />
            </div>
          </div>

          <div className="bg-white/5 border border-white/10 rounded-2xl p-5 backdrop-blur">
            <div className="flex justify-between items-start">
              <div>
                <p className="text-gray-400 text-xs">Study Streak</p>
                <p className="text-2xl font-bold text-white mt-1">{stats.studyStreak}d</p>
              </div>
              <TrendingUp className="text-orange-400" size={20} />
            </div>
          </div>

          <div className="bg-white/5 border border-white/10 rounded-2xl p-5 backdrop-blur">
            <div className="flex justify-between items-start">
              <div>
                <p className="text-gray-400 text-xs">Study Time</p>
                <p className="text-2xl font-bold text-white mt-1">{stats.totalStudyTime}h</p>
              </div>
              <Clock className="text-blue-400" size={20} />
            </div>
          </div>

        </div>

        {/* QUICK ACTIONS & RECENT ACTIVITY */}
        <div className="grid lg:grid-cols-3 gap-8">

          {/* Quick Actions */}
          <div className="space-y-4">
            <h2 className="text-white text-lg font-bold flex items-center gap-2">
              <Compass className="text-orange-400" size={20} /> Quick Actions
            </h2>
            <div className="space-y-3">
              {quickActions.map((action) => {
                const Icon = action.icon;
                return (
                  <Link
                    key={action.title}
                    to={action.link}
                    className="block bg-white/5 border border-white/10 hover:border-orange-400/40 rounded-xl p-4 transition-all duration-200 group"
                  >
                    <div className="flex justify-between items-start">
                      <div className="flex gap-3.5 items-center">
                        <div className="p-2.5 rounded-lg bg-orange-500/10 text-orange-400 group-hover:scale-110 transition-transform">
                          <Icon size={20} />
                        </div>
                        <div>
                          <p className="text-white font-bold text-sm">{action.title}</p>
                          <p className="text-gray-400 text-xs mt-0.5">{action.description}</p>
                        </div>
                      </div>
                      <span className="text-[10px] px-2 py-0.5 rounded bg-white/10 text-gray-400">
                        {action.badge}
                      </span>
                    </div>
                  </Link>
                );
              })}
            </div>
          </div>

          {/* Recent Activity */}
          <div className="lg:col-span-2 space-y-4">
            <div className="flex justify-between items-center">
              <h2 className="text-white text-lg font-bold">Your Recent Activity</h2>
              <Link to="/performance" className="text-xs text-orange-400 hover:underline">
                View all analytics →
              </Link>
            </div>

            {recentActivities.length > 0 ? (
              <div className="space-y-3">
                {recentActivities.map((activity) => {
                  const Icon = activity.icon;
                  const isQuiz = activity.type === 'quiz';

                  return (
                    <div
                      key={activity.id}
                      className="bg-white/5 border border-white/10 hover:border-white/20 rounded-xl p-4 transition flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4"
                    >
                      <div className="flex gap-3.5 items-center">
                        <div className="p-2.5 rounded-lg bg-orange-500/10 text-orange-400">
                          <Icon size={18} />
                        </div>
                        <div>
                          <p className="text-white text-sm font-medium">{activity.title}</p>
                          <p className="text-gray-500 text-xs">
                            {activity.date} • {activity.duration}
                          </p>
                        </div>
                      </div>

                      <div className="flex items-center gap-2 w-full sm:w-auto justify-end">
                        {isQuiz ? (
                          <>
                            <button
                              onClick={() => navigate(`${activity.link}?mode=review`)}
                              className="text-xs px-3.5 py-1.5 rounded-lg bg-orange-500/15 hover:bg-orange-500/25 text-orange-400 font-bold border border-orange-500/30 transition flex items-center gap-1.5"
                            >
                              <BookOpen size={13} /> Review Answers
                            </button>
                            <button
                              onClick={() => navigate(activity.link)}
                              className="text-xs px-3 py-1.5 rounded-lg bg-white/10 hover:bg-white/20 text-gray-300 transition"
                            >
                              Retake
                            </button>
                          </>
                        ) : (
                          <button
                            onClick={() => navigate(activity.link)}
                            className="text-xs px-3.5 py-1.5 rounded-lg bg-purple-500/15 hover:bg-purple-500/25 text-purple-300 font-semibold border border-purple-500/30 transition"
                          >
                            Practice Again
                          </button>
                        )}
                      </div>
                    </div>
                  );
                })}
              </div>
            ) : (
              <div className="bg-white/5 border border-white/10 rounded-xl p-8 text-center space-y-3">
                <BookOpen size={32} className="text-gray-600 mx-auto" />
                <p className="text-gray-400 text-sm">No activity recorded yet for your account.</p>
                <button
                  onClick={() => navigate('/quiz/generate')}
                  className="px-4 py-2 bg-orange-500 hover:bg-orange-600 text-white rounded-lg text-xs font-semibold transition"
                >
                  Generate Your First Quiz
                </button>
              </div>
            )}
          </div>

        </div>

        {/* PROGRESS OVERVIEW */}
        <div className="bg-white/5 border border-white/10 rounded-2xl p-6 backdrop-blur space-y-3">
          <div className="flex justify-between items-center text-sm">
            <span className="text-white font-bold">Overall Learning Mastery</span>
            <span className="text-orange-400 font-bold">{stats.averageScore}%</span>
          </div>

          <div className="w-full bg-white/10 h-2.5 rounded-full overflow-hidden">
            <div 
              className="bg-gradient-to-r from-orange-500 to-red-500 h-full rounded-full transition-all duration-700" 
              style={{ width: `${Math.min(100, Math.max(stats.averageScore > 0 ? stats.averageScore : 2, 2))}%` }}
            />
          </div>
          <p className="text-xs text-gray-500">
            Calculated in real-time across all your completed quizzes and mock interview sessions.
          </p>
        </div>

      </div>
    </section>
  );
};

export default Dashboard;
