import {
  Award,
  BookOpen,
  Brain,
  Clock,
  Target,
  TrendingUp,
  Users
} from 'lucide-react';
import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { performanceService } from '../services/performanceService';
import { quizService } from '../services/quizService';
import { interviewService } from '../services/interviewService';

const Dashboard = () => {
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
    const fetchDashboardData = async () => {
      try {
        const [performanceData, quizzesData, interviewsData] = await Promise.all([
          performanceService.getUserPerformance(),
          quizService.getUserQuizzes(),
          interviewService.getUserInterviews()
        ]);

        // Update stats
        setStats({
          totalQuizzes: quizzesData?.length || 0,
          completedQuizzes: performanceData?.completedQuizzes || 0,
          averageScore: performanceData?.averageScore || 0,
          interviewSessions: interviewsData?.length || 0,
          studyStreak: performanceData?.studyStreak || 0,
          totalStudyTime: performanceData?.totalStudyTime || 0,
        });

        // Update recent activities
        const activities = [
          ...(quizzesData?.slice(0, 2).map((quiz, index) => ({
            id: `quiz-${index}`,
            type: 'quiz',
            title: quiz.title || 'Untitled Quiz',
            score: quiz.score,
            completedAt: quiz.completedAt || new Date().toISOString().split('T')[0],
            duration: quiz.duration || '25 min',
            icon: BookOpen,
          })) || []),
          ...(interviewsData?.slice(0, 2).map((interview, index) => ({
            id: `interview-${index}`,
            type: 'interview',
            title: interview.jobRole || 'Interview Session',
            completedAt: interview.completedAt || new Date().toISOString().split('T')[0],
            duration: interview.duration || '45 min',
            icon: Brain,
          })) || [])
        ].sort((a, b) => new Date(b.completedAt) - new Date(a.completedAt)).slice(0, 4);

        setRecentActivities(activities);
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
      title: 'Create Quiz',
      description: 'Generate quiz from your study material',
      icon: Brain,
      link: '/quiz/generate',
    },
    {
      title: 'Start Interview',
      description: 'Practice AI interview simulation',
      icon: Users,
      link: '/interview',
    },
    {
      title: 'View Progress',
      description: 'Track performance insights',
      icon: TrendingUp,
      link: '/performance',
    },
  ];

  return (
    <section className="bg-[#0a0a0a] min-h-screen py-12 font-mono">
      <div className="container mx-auto px-4 max-w-7xl space-y-10">

        {/* HEADER */}
        <div className="text-center space-y-2">
          <h1 className="text-4xl font-bold bg-gradient-to-r from-orange-400 via-orange-500 to-red-500 bg-clip-text text-transparent">
            Welcome back
          </h1>
          <p className="text-gray-400">
            Here's your Quizora AI learning analytics overview
          </p>
        </div>

        {/* STATS */}
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-6">

          <div className="bg-white/5 border border-white/10 rounded-2xl p-6 backdrop-blur">
            <div className="flex justify-between">
              <div>
                <p className="text-gray-400 text-sm">Total Quizzes</p>
                <p className="text-2xl font-bold text-white">{stats.totalQuizzes}</p>
              </div>
              <BookOpen className="text-orange-400"/>
            </div>
          </div>

          <div className="bg-white/5 border border-white/10 rounded-2xl p-6 backdrop-blur">
            <div className="flex justify-between">
              <div>
                <p className="text-gray-400 text-sm">Completed</p>
                <p className="text-2xl font-bold text-white">{stats.completedQuizzes}</p>
              </div>
              <Target className="text-green-400"/>
            </div>
          </div>

          <div className="bg-white/5 border border-white/10 rounded-2xl p-6 backdrop-blur">
            <div className="flex justify-between">
              <div>
                <p className="text-gray-400 text-sm">Average Score</p>
                <p className="text-2xl font-bold text-white">{stats.averageScore}%</p>
              </div>
              <Award className="text-yellow-400"/>
            </div>
          </div>

          <div className="bg-white/5 border border-white/10 rounded-2xl p-6 backdrop-blur">
            <div className="flex justify-between">
              <div>
                <p className="text-gray-400 text-sm">Interviews</p>
                <p className="text-2xl font-bold text-white">{stats.interviewSessions}</p>
              </div>
              <Brain className="text-purple-400"/>
            </div>
          </div>

          <div className="bg-white/5 border border-white/10 rounded-2xl p-6 backdrop-blur">
            <div className="flex justify-between">
              <div>
                <p className="text-gray-400 text-sm">Study Streak</p>
                <p className="text-2xl font-bold text-white">{stats.studyStreak}d</p>
              </div>
              <TrendingUp className="text-orange-400"/>
            </div>
          </div>

          <div className="bg-white/5 border border-white/10 rounded-2xl p-6 backdrop-blur">
            <div className="flex justify-between">
              <div>
                <p className="text-gray-400 text-sm">Study Time</p>
                <p className="text-2xl font-bold text-white">{stats.totalStudyTime}h</p>
              </div>
              <Clock className="text-blue-400"/>
            </div>
          </div>

        </div>

        {/* QUICK ACTIONS */}
        <div className="grid lg:grid-cols-3 gap-8">

          <div className="space-y-4">
            <h2 className="text-white text-xl">Quick Actions</h2>
            {quickActions.map((action)=>{
              const Icon = action.icon;
              return(
                <Link
                  key={action.title}
                  to={action.link}
                  className="block bg-white/5 border border-white/10 rounded-xl p-4 hover:border-orange-400/40 transition"
                >
                  <div className="flex gap-3 items-center">
                    <Icon className="text-orange-400"/>
                    <div>
                      <p className="text-white">{action.title}</p>
                      <p className="text-gray-400 text-sm">{action.description}</p>
                    </div>
                  </div>
                </Link>
              )
            })}
          </div>

          {/* RECENT */}
          <div className="lg:col-span-2 space-y-4">
            <h2 className="text-white text-xl">Recent Activity</h2>

            {recentActivities.map((activity)=>{
              const Icon = activity.icon;
              return(
                <div key={activity.id}
                className="bg-white/5 border border-white/10 rounded-xl p-5 backdrop-blur">

                  <div className="flex justify-between">

                    <div className="flex gap-4">
                      <div className="p-2 rounded-lg bg-orange-500/10">
                        <Icon className="text-orange-400"/>
                      </div>

                      <div>
                        <p className="text-white">{activity.title}</p>
                        <p className="text-gray-500 text-sm">
                          {activity.completedAt} • {activity.duration}
                        </p>
                      </div>
                    </div>

                    {activity.score &&
                      <span className="text-sm px-3 py-1 rounded bg-green-500/10 text-green-400">
                        {activity.score}%
                      </span>
                    }

                  </div>
                </div>
              )
            })}

          </div>

        </div>

        {/* PROGRESS */}
        <div className="bg-white/5 border border-white/10 rounded-2xl p-6 backdrop-blur space-y-4">
          <h2 className="text-white text-xl">Learning Progress</h2>

          <div>
            <div className="flex justify-between text-sm text-gray-400 mb-1">
              <span>Overall Progress</span>
              <span>67%</span>
            </div>

            <div className="w-full bg-white/10 h-2 rounded-full">
              <div className="bg-gradient-to-r from-orange-400 to-red-500 h-2 rounded-full w-[67%]"></div>
            </div>
          </div>
        </div>

      </div>
    </section>
  );
};

export default Dashboard;
