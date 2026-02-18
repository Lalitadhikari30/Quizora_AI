import api from './api';

export const fileService = {

  async uploadPdf(file) {
    const formData = new FormData();
    formData.append('file', file);

    const response = await api.post('/api/upload/pdf', formData);
    return response.data;
  },

  async uploadFile(file) {
    const formData = new FormData();
    formData.append('file', file);

    const response = await api.post('/api/upload/file', formData);
    return response.data;
  },

  async uploadFileForQuiz(formData, quizOptions = {}) {
    // Add quiz options to FormData
    Object.keys(quizOptions).forEach(key => {
      formData.append(key, quizOptions[key]);
    });

    const response = await api.post('/api/upload/quiz', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
    return response.data;
  },

  validateFile(file, allowedTypes = ['pdf','doc','docx','txt','xlsx','xls','csv','json','jpg','png'], maxSizeMB = 10) {
    const fileExtension = file.name.split('.').pop().toLowerCase();
    const fileSizeMB = file.size / (1024 * 1024);

    if (!allowedTypes.includes(fileExtension)) {
      throw new Error(`File type .${fileExtension} is not allowed`);
    }

    if (fileSizeMB > maxSizeMB) {
      throw new Error(`File size exceeds ${maxSizeMB}MB`);
    }

    return true;
  }
};
