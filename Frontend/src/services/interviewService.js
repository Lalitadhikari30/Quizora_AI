import api from './api';

export const interviewService = {
  // Start new interview session
  async startInterview(interviewData) {
    try {
      const response = await api.post('/api/interview/start', interviewData);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Submit answer to interview question
  async submitAnswer(sessionId, question, userAnswer) {
    try {
      const response = await api.post(`/api/interview/${sessionId}/answer`, null, {
        params: { question, userAnswer }
      });
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Get interview report
  async getInterviewReport(sessionId) {
    try {
      const response = await api.get(`/api/interview/${sessionId}/report`);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Get all user interviews
  async getUserInterviews() {
    try {
      const response = await api.get('/api/interview');
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // End interview session
  async endInterview(sessionId) {
    try {
      const response = await api.post(`/api/interview/${sessionId}/end`);
      return response.data;
    } catch (error) {
      throw error;
    }
  }
};
