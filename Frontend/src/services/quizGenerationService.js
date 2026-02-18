// Frontend service for the new quiz generation architecture
// Handles the two-step process: upload/extract → generate quiz

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8081';

class QuizGenerationService {
    /**
     * Step 1: Upload file and extract text
     * Returns extractedContentId for later quiz generation
     */
    async uploadAndExtract(file, token) {
        try {
            console.log('=== UPLOAD AND EXTRACT START ===');
            console.log('File:', file.name, 'Size:', file.size);

            const formData = new FormData();
            formData.append('file', file);

            const response = await fetch(`${API_BASE_URL}/api/upload/extract`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
                body: formData
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.error || `Upload failed: ${response.status}`);
            }

            const result = await response.json();
            console.log('Upload and extract successful:', result);
            console.log('=== UPLOAD AND EXTRACT END ===');

            return {
                success: true,
                extractedContentId: result.extractedContentId,
                fileName: result.fileName,
                fileUrl: result.fileUrl,
                extractedTextLength: result.extractedTextLength,
                status: result.status,
                message: result.message
            };

        } catch (error) {
            console.error('Upload and extract failed:', error);
            return {
                success: false,
                error: error.message,
                message: 'Failed to upload and extract file'
            };
        }
    }

    /**
     * Step 2: Generate quiz from stored extracted content
     */
    async generateQuizFromExtraction(extractedContentId, token) {
        try {
            console.log('=== GENERATE QUIZ FROM EXTRACTION START ===');
            console.log('ExtractedContent ID:', extractedContentId);

            const response = await fetch(`${API_BASE_URL}/api/quizzes/generate-from-extraction/${extractedContentId}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.error || `Quiz generation failed: ${response.status}`);
            }

            const result = await response.json();
            console.log('Quiz generation successful:', result);
            console.log('=== GENERATE QUIZ FROM EXTRACTION END ===');

            return {
                success: true,
                quiz: result.quiz,
                extractedContentId: result.extractedContentId,
                fileName: result.fileName,
                extractedTextLength: result.extractedTextLength,
                questionsGenerated: result.questionsGenerated,
                message: result.message
            };

        } catch (error) {
            console.error('Quiz generation failed:', error);
            return {
                success: false,
                error: error.message,
                extractedContentId: extractedContentId,
                message: 'Failed to generate quiz from extracted content'
            };
        }
    }

    /**
     * Retry quiz generation for failed extraction
     */
    async retryQuizGeneration(extractedContentId, token) {
        try {
            console.log('=== RETRY QUIZ GENERATION START ===');
            console.log('ExtractedContent ID:', extractedContentId);

            const response = await fetch(`${API_BASE_URL}/api/quizzes/retry-generation/${extractedContentId}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.error || `Retry failed: ${response.status}`);
            }

            const result = await response.json();
            console.log('Retry successful:', result);
            console.log('=== RETRY QUIZ GENERATION END ===');

            return {
                success: true,
                quiz: result.quiz,
                extractedContentId: result.extractedContentId,
                fileName: result.fileName,
                questionsGenerated: result.questionsGenerated,
                message: result.message
            };

        } catch (error) {
            console.error('Retry failed:', error);
            return {
                success: false,
                error: error.message,
                extractedContentId: extractedContentId,
                message: 'Failed to retry quiz generation'
            };
        }
    }

    /**
     * Get user's extracted contents
     */
    async getUserExtractedContents(token) {
        try {
            const response = await fetch(`${API_BASE_URL}/api/quizzes/extracted-contents`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error(`Failed to get extracted contents: ${response.status}`);
            }

            const result = await response.json();
            return {
                success: true,
                extractedContents: result.extractedContents,
                count: result.count
            };

        } catch (error) {
            console.error('Get extracted contents failed:', error);
            return {
                success: false,
                error: error.message,
                extractedContents: [],
                count: 0
            };
        }
    }

    /**
     * Get user's failed extractions for retry
     */
    async getUserFailedExtractions(token) {
        try {
            const response = await fetch(`${API_BASE_URL}/api/quizzes/failed-extractions`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error(`Failed to get failed extractions: ${response.status}`);
            }

            const result = await response.json();
            return {
                success: true,
                failedExtractions: result.failedExtractions,
                count: result.count
            };

        } catch (error) {
            console.error('Get failed extractions failed:', error);
            return {
                success: false,
                error: error.message,
                failedExtractions: [],
                count: 0
            };
        }
    }

    /**
     * Complete pipeline: upload → extract → generate quiz
     * This combines both steps for convenience
     */
    async completeQuizGeneration(file, token) {
        try {
            console.log('=== COMPLETE QUIZ GENERATION START ===');

            // Step 1: Upload and extract
            const extractResult = await this.uploadAndExtract(file, token);
            if (!extractResult.success) {
                return extractResult;
            }

            // Step 2: Generate quiz
            const generateResult = await this.generateQuizFromExtraction(extractResult.extractedContentId, token);
            if (!generateResult.success) {
                return generateResult;
            }

            console.log('=== COMPLETE QUIZ GENERATION SUCCESS ===');

            return {
                success: true,
                quiz: generateResult.quiz,
                extractedContentId: extractResult.extractedContentId,
                fileName: extractResult.fileName,
                extractedTextLength: extractResult.extractedTextLength,
                questionsGenerated: generateResult.questionsGenerated,
                message: 'Complete quiz generation successful'
            };

        } catch (error) {
            console.error('Complete quiz generation failed:', error);
            return {
                success: false,
                error: error.message,
                message: 'Complete quiz generation failed'
            };
        }
    }
}

// Export singleton instance
const quizGenerationService = new QuizGenerationService();
export default quizGenerationService;
