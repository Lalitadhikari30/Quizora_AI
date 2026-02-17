import api from './api';

export const performanceService = {
  // Get user performance data
  async getUserPerformance() {
    try {
      const response = await api.get('/api/performance');
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Get performance history
  async getPerformanceHistory() {
    try {
      const response = await api.get('/api/performance/history');
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Get interview performance
  async getInterviewPerformance() {
    try {
      const response = await api.get('/api/performance/interviews');
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Get quiz performance analytics
  async getQuizAnalytics() {
    try {
      const response = await api.get('/api/performance/quizzes');
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Get learning progress
  async getLearningProgress() {
    try {
      const response = await api.get('/api/performance/progress');
      return response.data;
    } catch (error) {
      throw error;
    }
  }
};
