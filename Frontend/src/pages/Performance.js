import {
  Award,
  BookOpen,
  Clock,
  Target
} from "lucide-react";
import { useState, useEffect } from "react";
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
  const [timeRange, setTimeRange] = useState("month");
  const [performanceData, setPerformanceData] = useState([]);
  const [categoryData, setCategoryData] = useState([]);
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
        const [performance, history, interviewPerf] = await Promise.all([
          performanceService.getUserPerformance(),
          performanceService.getPerformanceHistory(),
          performanceService.getInterviewPerformance()
        ]);

        // Set stats
        setStats({
          totalQuizzes: performance.totalQuizzes || 0,
          averageScore: performance.averageScore || 0,
          studyStreak: performance.studyStreak || 0,
          totalStudyTime: performance.totalStudyTime || 0,
          improvement: performance.improvement || 0
        });

        // Set performance history data (mock for now)
        setPerformanceData([
          { date: "Jan 1", score: 75 },
          { date: "Jan 8", score: 82 },
          { date: "Jan 15", score: 78 },
          { date: "Jan 22", score: 85 },
          { date: "Jan 29", score: performance.averageScore || 88 }
        ]);

        // Set category data (mock for now)
        setCategoryData([
          { name: "Programming", value: 45, color: "#f97316" },
          { name: "Frontend", value: 30, color: "#fb923c" },
          { name: "Backend", value: 15, color: "#fdba74" },
          { name: "Database", value: 10, color: "#fed7aa" }
        ]);
      } catch (error) {
        console.error('Failed to fetch performance data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchPerformanceData();
  }, [timeRange]);

  return (
    <section className="bg-[#0a0a0a] min-h-screen py-12 font-geist">
      <div className="container mx-auto px-6 max-w-7xl space-y-10">

        {/* HEADER */}
        <div className="flex justify-between items-center">
          <div>
            <h1 className="text-3xl font-bold text-white font-geist">
              Performance Analytics
            </h1>
            <p className="text-gray-500 text-sm mt-1">
              Track your learning progress
            </p>
          </div>

          <select
            value={timeRange}
            onChange={(e) => setTimeRange(e.target.value)}
            className="bg-white/5 border border-white/10 rounded-lg px-4 py-2 text-gray-300 focus:outline-none focus:border-orange-400 font-geist"
          >
            <option value="week">Last Week</option>
            <option value="month">Last Month</option>
            <option value="quarter">Last Quarter</option>
          </select>
        </div>

        {/* STATS */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
          {[
            { label: "Total Quizzes", value: stats.totalQuizzes, icon: BookOpen },
            { label: "Average Score", value: `${stats.averageScore}%`, icon: Target },
            { label: "Study Streak", value: `${stats.studyStreak} days`, icon: Award },
            { label: "Study Time", value: `${stats.totalStudyTime}h`, icon: Clock }
          ].map((stat, i) => {
            const Icon = stat.icon;
            return (
              <div key={i} className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-6">
                <div className="flex justify-between items-center">
                  <div>
                    <p className="text-sm text-gray-500 font-geist">{stat.label}</p>
                    <p className="text-2xl font-bold text-white font-geist">{stat.value}</p>
                  </div>
                  <Icon size={24} className="text-orange-400" />
                </div>
              </div>
            );
          })}
        </div>

        {/* CHARTS */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">

          <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-6">
            <h3 className="text-lg font-semibold text-white mb-4 font-geist">
              Score Progress
            </h3>

            <div className="h-64">
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={performanceData}>
                  <CartesianGrid stroke="#1f2937" strokeDasharray="3 3" />
                  <XAxis dataKey="date" stroke="#9ca3af" />
                  <YAxis stroke="#9ca3af" />
                  <Tooltip />
                  <Line
                    type="monotone"
                    dataKey="score"
                    stroke="#f97316"
                    strokeWidth={2}
                  />
                </LineChart>
              </ResponsiveContainer>
            </div>
          </div>

          <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-6">
            <h3 className="text-lg font-semibold text-white mb-4 font-geist">
              Category Distribution
            </h3>

            <div className="h-64">
              <ResponsiveContainer width="100%" height="100%">
                <RechartsPieChart>
                  <Pie
                    data={categoryData}
                    dataKey="value"
                    outerRadius={80}
                    label
                  >
                    {categoryData.map((entry, i) => (
                      <Cell key={i} fill={entry.color} />
                    ))}
                  </Pie>
                  <Tooltip />
                </RechartsPieChart>
              </ResponsiveContainer>
            </div>
          </div>
        </div>

        {/* CATEGORY PROGRESS */}
        <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-6">
          <h3 className="text-lg font-semibold text-white mb-4 font-geist">
            Category Performance
          </h3>

          <div className="space-y-4">
            {categoryData.map((cat) => (
              <div key={cat.name}>
                <div className="flex justify-between mb-1">
                  <span className="text-gray-300">{cat.name}</span>
                  <span className="text-gray-500">{cat.value}%</span>
                </div>
                <div className="w-full bg-white/10 rounded-full h-2">
                  <div
                    className="h-2 rounded-full"
                    style={{
                      width: `${cat.value}%`,
                      backgroundColor: cat.color
                    }}
                  />
                </div>
              </div>
            ))}
          </div>
        </div>

      </div>
    </section>
  );
};

export default Performance;

