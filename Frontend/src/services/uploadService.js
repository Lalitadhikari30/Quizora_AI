// Fixed frontend upload service for Quizora AI
// Handles file upload with proper FormData and error handling

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8081';

class UploadService {
    /**
     * Upload file for quiz generation
     * @param {File} file - The file to upload (PDF, DOCX, TXT)
     * @param {string} token - JWT authentication token
     * @returns {Promise<Object>} - Quiz generation response
     */
    async uploadFileForQuiz(file, token) {
        try {
            console.log('=== FRONTEND UPLOAD START ===');
            console.log('File:', file);
            console.log('File name:', file.name);
            console.log('File size:', file.size);
            console.log('File type:', file.type);
            console.log('Token provided:', !!token);

            // Validate file
            if (!file) {
                throw new Error('No file provided');
            }

            // Validate file type
            const allowedTypes = [
                'application/pdf',
                'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
                'text/plain'
            ];
            
            const fileExtension = file.name.split('.').pop().toLowerCase();
            const allowedExtensions = ['pdf', 'docx', 'txt'];
            
            if (!allowedExtensions.includes(fileExtension)) {
                throw new Error(`Unsupported file type: ${fileExtension}. Allowed types: PDF, DOCX, TXT`);
            }

            // Create FormData properly
            const formData = new FormData();
            formData.append('file', file);
            
            console.log('FormData created successfully');
            console.log('FormData entries count:', formData.getAll('file').length);

            // Make API call
            const response = await fetch(`${API_BASE_URL}/api/upload/quiz`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    // Note: Don't set Content-Type header when using FormData
                    // Browser will set it automatically with boundary
                },
                body: formData
            });

            console.log('Response status:', response.status);
            console.log('Response headers:', response.headers);

            if (!response.ok) {
                const errorText = await response.text();
                console.error('Upload failed:', response.status, errorText);
                
                if (response.status === 400) {
                    throw new Error(`Bad Request: ${errorText}`);
                } else if (response.status === 401) {
                    throw new Error('Authentication failed. Please sign in again.');
                } else if (response.status === 413) {
                    throw new Error('File too large. Please upload a smaller file.');
                } else {
                    throw new Error(`Upload failed: ${response.status} ${errorText}`);
                }
            }

            const result = await response.json();
            console.log('Upload successful:', result);
            console.log('=== FRONTEND UPLOAD END ===');
            
            return result;

        } catch (error) {
            console.error('=== FRONTEND UPLOAD ERROR ===');
            console.error('Error:', error.message);
            console.error('Stack:', error.stack);
            console.error('=== END ERROR ===');
            
            throw error;
        }
    }

    /**
     * Get user's quizzes
     * @param {string} token - JWT authentication token
     * @returns {Promise<Array>} - List of user quizzes
     */
    async getUserQuizzes(token) {
        try {
            const response = await fetch(`${API_BASE_URL}/api/quizzes`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error(`Failed to fetch quizzes: ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            console.error('Error fetching quizzes:', error);
            throw error;
        }
    }

    /**
     * Get quiz by ID
     * @param {number} quizId - Quiz ID
     * @param {string} token - JWT authentication token
     * @returns {Promise<Object>} - Quiz details
     */
    async getQuizById(quizId, token) {
        try {
            const response = await fetch(`${API_BASE_URL}/api/quizzes/${quizId}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error(`Failed to fetch quiz: ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            console.error('Error fetching quiz:', error);
            throw error;
        }
    }
}

// Export singleton instance
const uploadService = new UploadService();
export default uploadService;
