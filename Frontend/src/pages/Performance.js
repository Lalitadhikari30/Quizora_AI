import {
  Award,
  BookOpen,
  CheckCircle2,
  Clock,
  HelpCircle,
  PlusCircle,
  Target,
  TrendingUp
} from "lucide-react";
import { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import {
  CartesianGrid,
  Cell,
  Line,
  LineChart,
  Pie,
  PieChart as RechartsPieChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis
} from "recharts";
import { performanceService } from '../services/performanceService';

const Performance = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const recentResult = location.state?.result;

  const [performanceData, setPerformanceData] = useState([]);
  const [categoryData, setCategoryData] = useState([]);
  const [recentAttempts, setRecentAttempts] = useState([]);
  const [stats, setStats] = useState({
    totalQuizzes: 0,
    averageScore: 0,
    studyStreak: 0,
    totalStudyTime: 0,
    improvement: 0
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchPerformanceData = async () => {
      try {
        const [performance, history] = await Promise.all([
          performanceService.getUserPerformance(),
          performanceService.getPerformanceHistory()
        ]);

        const totalQuizzes = performance?.totalQuizzesTaken ?? performance?.totalQuizzes ?? 0;
        const averageScore = Math.round(performance?.averageQuizScore ?? performance?.averageScore ?? 0);
        const streak = performance?.studyStreak ?? (totalQuizzes > 0 ? 1 : 0);
        const studyTime = performance?.totalStudyTime ?? Math.round(totalQuizzes * 0.25 * 10) / 10;

        setStats({
          totalQuizzes,
          averageScore,
          studyStreak: streak,
          totalStudyTime: studyTime,
          improvement: 0
        });

        // Set performance history for progress chart
        if (Array.isArray(history) && history.length > 0) {
          setRecentAttempts(history);
          setPerformanceData(history.map(item => ({
            date: item.date || "Recent",
            score: Math.round(item.score ?? item.percentage ?? 0),
            title: item.quizTitle || "Quiz"
          })));
        } else if (totalQuizzes > 0) {
          setPerformanceData([
            { date: "Completed", score: averageScore, title: "Quiz" }
          ]);
        } else {
          setPerformanceData([]);
        }

        // Set dynamic category data
        if (Array.isArray(performance?.categories) && performance.categories.length > 0) {
          setCategoryData(performance.categories);
        } else {
          setCategoryData([
            { name: "General Knowledge", value: 100, score: averageScore, color: "#f97316" }
          ]);
        }
      } catch (error) {
        console.error('Failed to fetch performance data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchPerformanceData();
  }, [timeRange]);

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-[#0a0a0a]">
        <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-orange-500"></div>
      </div>
    );
  }

  return (
    <section className="bg-[#0a0a0a] min-h-screen py-12 font-mono">
      <div className="container mx-auto px-6 max-w-7xl space-y-10">

        {/* RECENT SUBMISSION BANNER (If just completed a quiz) */}
        {recentResult && (
          <div className="bg-gradient-to-r from-orange-500/10 via-orange-500/5 to-transparent border border-orange-500/30 rounded-2xl p-6 flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
            <div className="space-y-1">
              <div className="flex items-center space-x-2">
                <CheckCircle2 size={20} className="text-orange-400" />
                <span className="text-orange-400 font-bold text-sm uppercase tracking-wider">Quiz Completed!</span>
              </div>
              <h2 className="text-xl font-bold text-white">
                {recentResult.title || "Quiz Submission"}
              </h2>
              <p className="text-gray-400 text-sm">
                You scored {recentResult.score} out of {recentResult.totalQuestions} ({Math.round(recentResult.percentage || 0)}%)
              </p>
            </div>

            <div className="flex items-center gap-3">
              <button
                onClick={() => navigate('/quiz/generate')}
                className="px-5 py-2.5 bg-orange-500 hover:bg-orange-600 text-white rounded-lg text-sm font-semibold transition flex items-center gap-2"
              >
                <PlusCircle size={16} /> Take Another Quiz
              </button>
            </div>
          </div>
        )}

        {/* HEADER */}
        <div className="flex justify-between items-center">
          <div>
            <h1 className="text-3xl font-bold text-white font-mono">
              Performance Analytics
            </h1>
            <p className="text-gray-500 text-sm mt-1 font-mono">
              Real-time tracking of your quiz performance and learning progress
            </p>
          </div>

          <div className="flex items-center gap-3">
            <button
              onClick={() => navigate('/quiz/generate')}
              className="px-4 py-2 bg-white/10 hover:bg-white/20 border border-white/10 rounded-lg text-white text-sm font-mono transition"
            >
              + New Quiz
            </button>
          </div>
        </div>

        {/* STATS */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
          {[
            { label: "Total Quizzes", value: stats.totalQuizzes, icon: BookOpen },
            { label: "Average Score", value: `${stats.averageScore}%`, icon: Target },
            { label: "Study Streak", value: `${stats.studyStreak} day${stats.studyStreak === 1 ? '' : 's'}`, icon: Award },
            { label: "Study Time", value: `${stats.totalStudyTime}h`, icon: Clock }
          ].map((stat, i) => {
            const Icon = stat.icon;
            return (
              <div key={i} className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-6">
                <div className="flex justify-between items-center">
                  <div>
                    <p className="text-sm text-gray-500 font-mono">{stat.label}</p>
                    <p className="text-2xl font-bold text-white font-mono mt-1">{stat.value}</p>
                  </div>
                  <Icon size={24} className="text-orange-400" />
                </div>
              </div>
            );
          })}
        </div>

        {/* CHARTS */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">

          {/* SCORE PROGRESS */}
          <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-6">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-lg font-semibold text-white font-mono flex items-center gap-2">
                <TrendingUp size={18} className="text-orange-400" /> Score Progression
              </h3>
              <span className="text-xs text-gray-500 font-mono">
                {performanceData.length} Attempt{performanceData.length === 1 ? '' : 's'}
              </span>
            </div>

            <div className="h-64">
              {performanceData.length > 0 ? (
                <ResponsiveContainer width="100%" height="100%">
                  <LineChart data={performanceData}>
                    <CartesianGrid stroke="#1f2937" strokeDasharray="3 3" />
                    <XAxis dataKey="date" stroke="#9ca3af" tick={{ fill: '#9ca3af', fontSize: 12 }} />
                    <YAxis stroke="#9ca3af" domain={[0, 100]} tick={{ fill: '#9ca3af', fontSize: 12 }} />
                    <Tooltip
                      contentStyle={{ backgroundColor: '#18181b', borderColor: '#27272a', borderRadius: '8px', color: '#fff' }}
                      formatter={(value) => [`${value}%`, 'Score']}
                    />
                    <Line
                      type="monotone"
                      dataKey="score"
                      stroke="#f97316"
                      strokeWidth={2}
                      dot={{ fill: '#f97316', r: 4 }}
                      activeDot={{ r: 6 }}
                    />
                  </LineChart>
                </ResponsiveContainer>
              ) : (
                <div className="h-full flex flex-col items-center justify-center text-gray-500 space-y-2">
                  <HelpCircle size={32} className="text-gray-600" />
                  <p className="text-sm">No quiz attempts recorded yet.</p>
                  <button
                    onClick={() => navigate('/quiz/generate')}
                    className="text-xs text-orange-400 hover:underline"
                  >
                    Take your first quiz →
                  </button>
                </div>
              )}
            </div>
          </div>

          {/* CATEGORY DISTRIBUTION */}
          <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-6">
            <h3 className="text-lg font-semibold text-white mb-4 font-mono">
              Topic & Category Breakdown
            </h3>

            <div className="h-64">
              {categoryData.length > 0 ? (
                <ResponsiveContainer width="100%" height="100%">
                  <RechartsPieChart>
                    <Pie
                      data={categoryData}
                      dataKey="value"
                      nameKey="name"
                      outerRadius={80}
                      label={({ name, percent }) => `${name} (${(percent * 100).toFixed(0)}%)`}
                    >
                      {categoryData.map((entry, i) => (
                        <Cell key={i} fill={entry.color || "#f97316"} />
                      ))}
                    </Pie>
                    <Tooltip
                      contentStyle={{ backgroundColor: '#18181b', borderColor: '#27272a', borderRadius: '8px', color: '#fff' }}
                      formatter={(value, name) => [`${value}% of quizzes`, name]}
                    />
                  </RechartsPieChart>
                </ResponsiveContainer>
              ) : (
                <div className="h-full flex items-center justify-center text-gray-500 text-sm">
                  Complete quizzes to see topic distribution.
                </div>
              )}
            </div>
          </div>
        </div>

        {/* CATEGORY PERFORMANCE PROGRESS BARS */}
        <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-6">
          <h3 className="text-lg font-semibold text-white mb-4 font-mono">
            Category Accuracy & Performance
          </h3>

          <div className="space-y-4">
            {categoryData.map((cat) => (
              <div key={cat.name}>
                <div className="flex justify-between mb-1 text-sm font-mono">
                  <span className="text-gray-300">{cat.name}</span>
                  <span className="text-orange-400 font-semibold">{cat.score ?? cat.value}%</span>
                </div>
                <div className="w-full bg-white/10 rounded-full h-2">
                  <div
                    className="h-2 rounded-full transition-all duration-500"
                    style={{
                      width: `${Math.min(100, Math.max(0, cat.score ?? cat.value))}%`,
                      backgroundColor: cat.color || "#f97316"
                    }}
                  />
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* RECENT ATTEMPTS TABLE */}
        {recentAttempts.length > 0 && (
          <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-6">
            <h3 className="text-lg font-semibold text-white mb-4 font-mono">
              Recent Quiz History
            </h3>

            <div className="overflow-x-auto">
              <table className="w-full text-left font-mono text-sm">
                <thead>
                  <tr className="border-b border-white/10 text-gray-400 text-xs uppercase tracking-wider">
                    <th className="py-3 px-4">Quiz Title</th>
                    <th className="py-3 px-4">Date</th>
                    <th className="py-3 px-4">Correct / Total</th>
                    <th className="py-3 px-4">Score</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-white/5">
                  {recentAttempts.slice().reverse().map((attempt, idx) => {
                    const score = Math.round(attempt.score ?? attempt.percentage ?? 0);
                    const isPassing = score >= 60;
                    return (
                      <tr key={idx} className="hover:bg-white/5 transition">
                        <td className="py-3.5 px-4 text-white font-medium">
                          {attempt.quizTitle || `Quiz #${attempt.id}`}
                        </td>
                        <td className="py-3.5 px-4 text-gray-400">
                          {attempt.date || "Recent"}
                        </td>
                        <td className="py-3.5 px-4 text-gray-300">
                          {attempt.correctAnswers !== undefined ? `${attempt.correctAnswers} / ${attempt.totalQuestions}` : '—'}
                        </td>
                        <td className="py-3.5 px-4">
                          <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-bold ${
                            isPassing ? 'bg-green-500/10 text-green-400 border border-green-500/20' : 'bg-orange-500/10 text-orange-400 border border-orange-500/20'
                          }`}>
                            {score}%
                          </span>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          </div>
        )}

      </div>
    </section>
  );
};

export default Performance;
