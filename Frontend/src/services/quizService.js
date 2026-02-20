import api from './api';

export const quizService = {
  // Generate new quiz
  async generateQuiz(quizData) {
    try {
      console.log('Generating quiz with data:', quizData);
      const response = await api.post('/api/quizzes/generate', quizData);
      console.log('Quiz generation response:', response.data);
      return response.data;
    } catch (error) {
      console.error('Quiz generation error:', error);
      throw error;
    }
  },

  // Get quiz by ID
  async getQuiz(quizId) {
    try {
      console.log('Fetching quiz with ID:', quizId);
      const response = await api.get(`/api/quizzes/${quizId}`);
      console.log('Quiz fetch response:', response.data);
      return response.data;
    } catch (error) {
      console.error('Quiz fetch error:', error);
      console.error('Error response data:', error.response?.data);
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
