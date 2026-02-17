import api from './api';

export const quizService = {
  // Generate new quiz
  async generateQuiz(quizData) {
    try {
      const response = await api.post('/api/quizzes/generate', quizData);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Get quiz by ID
  async getQuiz(quizId) {
    try {
      const response = await api.get(`/api/quizzes/${quizId}`);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Submit quiz answers
  async submitQuiz(quizId, answers) {
    try {
      const response = await api.post(`/api/quizzes/${quizId}/submit`, answers);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Get all user quizzes
  async getUserQuizzes() {
    try {
      const response = await api.get('/api/quizzes');
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Delete quiz
  async deleteQuiz(quizId) {
    try {
      const response = await api.delete(`/api/quizzes/${quizId}`);
      return response.data;
    } catch (error) {
      throw error;
    }
  }
};
