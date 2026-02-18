import api from './api';

export const fileService = {
  // Upload PDF file
  async uploadPdf(file) {
    try {
      const formData = new FormData();
      formData.append('file', file);
      
      const response = await api.post('/api/upload/pdf', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Upload general file
  async uploadFile(file) {
    try {
      const formData = new FormData();
      formData.append('file', file);
      
      const response = await api.post('/api/upload/file', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Upload file for quiz generation
  async uploadFileForQuiz(file, quizOptions = {}) {
    try {
      const formData = new FormData();
      formData.append('file', file);
      
      // Add quiz options to form data
      Object.keys(quizOptions).forEach(key => {
        formData.append(key, quizOptions[key]);
      });
      
      const response = await api.post('/api/upload/quiz', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Validate file type and size
  validateFile(file, allowedTypes = ['pdf', 'doc', 'docx', 'txt', 'xlsx', 'xls', 'csv', 'json', 'jpg', 'png'], maxSizeMB = 10) {
    const fileExtension = file.name.split('.').pop().toLowerCase();
    const fileSizeMB = file.size / (1024 * 1024);
    
    if (!allowedTypes.includes(fileExtension)) {
      throw new Error(`File type .${fileExtension} is not allowed. Allowed types: ${allowedTypes.join(', ')}`);
    }
    
    if (fileSizeMB > maxSizeMB) {
      throw new Error(`File size ${fileSizeMB.toFixed(2)}MB exceeds maximum allowed size of ${maxSizeMB}MB`);
    }
    
    return true;
  }
};
