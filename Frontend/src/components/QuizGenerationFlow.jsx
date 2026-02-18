import React, { useState, useCallback } from 'react';
import quizGenerationService from '../services/quizGenerationService';

const QuizGenerationFlow = ({ token, onQuizGenerated }) => {
    const [file, setFile] = useState(null);
    const [step, setStep] = useState('upload'); // upload, generating, success, error
    const [extractedContentId, setExtractedContentId] = useState(null);
    const [uploadResult, setUploadResult] = useState(null);
    const [quizResult, setQuizResult] = useState(null);
    const [error, setError] = useState('');
    const [progress, setProgress] = useState('');
    const [isRetrying, setIsRetrying] = useState(false);

    const handleFileChange = (event) => {
        const selectedFile = event.target.files[0];
        if (selectedFile) {
            // Validate file size (max 10MB)
            if (selectedFile.size > 10 * 1024 * 1024) {
                setError('File size must be less than 10MB');
                return;
            }
            
            // Validate file type
            const allowedTypes = ['pdf', 'docx', 'txt'];
            const fileExtension = selectedFile.name.split('.').pop().toLowerCase();
            
            if (!allowedTypes.includes(fileExtension)) {
                setError('Only PDF, DOCX, and TXT files are allowed');
                return;
            }
            
            setFile(selectedFile);
            setError('');
            resetState();
        }
    };

    const resetState = () => {
        setStep('upload');
        setExtractedContentId(null);
        setUploadResult(null);
        setQuizResult(null);
        setError('');
        setProgress('');
        setIsRetrying(false);
    };

    const handleUploadAndExtract = useCallback(async () => {
        if (!file) {
            setError('Please select a file first');
            return;
        }

        if (!token) {
            setError('Please sign in to upload files');
            return;
        }

        setStep('generating');
        setError('');
        setProgress('Uploading file and extracting text...');

        try {
            const result = await quizGenerationService.uploadAndExtract(file, token);
            
            if (!result.success) {
                throw new Error(result.error);
            }

            setUploadResult(result);
            setExtractedContentId(result.extractedContentId);
            setProgress('File uploaded and text extracted successfully!');
            
            // Automatically proceed to quiz generation
            setTimeout(() => {
                handleGenerateQuiz(result.extractedContentId);
            }, 1000);

        } catch (error) {
            setError(error.message);
            setProgress('');
            setStep('error');
        }
    }, [file, token]);

    const handleGenerateQuiz = useCallback(async (contentId = extractedContentId) => {
        if (!contentId) {
            setError('No extracted content available');
            return;
        }

        setStep('generating');
        setError('');
        setProgress('Generating quiz with AI...');

        try {
            const result = await quizGenerationService.generateQuizFromExtraction(contentId, token);
            
            if (!result.success) {
                throw new Error(result.error);
            }

            setQuizResult(result);
            setProgress('Quiz generated successfully!');
            setStep('success');
            
            // Call parent callback with the result
            if (onQuizGenerated) {
                onQuizGenerated(result);
            }

        } catch (error) {
            setError(error.message);
            setProgress('');
            setStep('error');
        }
    }, [extractedContentId, token, onQuizGenerated]);

    const handleRetry = useCallback(async () => {
        if (!extractedContentId) {
            setError('No extracted content available for retry');
            return;
        }

        setIsRetrying(true);
        setStep('generating');
        setError('');
        setProgress('Retrying quiz generation...');

        try {
            const result = await quizGenerationService.retryQuizGeneration(extractedContentId, token);
            
            if (!result.success) {
                throw new Error(result.error);
            }

            setQuizResult(result);
            setProgress('Quiz generation retry successful!');
            setStep('success');
            setIsRetrying(false);
            
            // Call parent callback with the result
            if (onQuizGenerated) {
                onQuizGenerated(result);
            }

        } catch (error) {
            setError(error.message);
            setProgress('');
            setStep('error');
            setIsRetrying(false);
        }
    }, [extractedContentId, token, onQuizGenerated]);

    const handleDrop = useCallback((event) => {
        event.preventDefault();
        const droppedFile = event.dataTransfer.files[0];
        if (droppedFile) {
            const fileInput = document.getElementById('file-input');
            fileInput.files = event.dataTransfer.files;
            handleFileChange({ target: fileInput });
        }
    }, []);

    const handleDragOver = useCallback((event) => {
        event.preventDefault();
    }, []);

    const handleNewUpload = () => {
        resetState();
        document.getElementById('file-input').value = '';
        setFile(null);
    };

    return (
        <div className="quiz-generation-flow">
            <div className="flow-header">
                <h2>🚀 AI Quiz Generation</h2>
                <p>Upload a document → Extract text → Generate quiz with AI</p>
            </div>

            {/* Upload Area */}
            {step === 'upload' && (
                <div 
                    className="upload-area"
                    onDrop={handleDrop}
                    onDragOver={handleDragOver}
                >
                    <div className="upload-content">
                        <div className="upload-icon">📄</div>
                        <h3>Step 1: Upload Document</h3>
                        <p>Supports PDF, DOCX, and TXT files (max 10MB)</p>
                        
                        <input
                            id="file-input"
                            type="file"
                            accept=".pdf,.docx,.txt"
                            onChange={handleFileChange}
                            style={{ display: 'none' }}
                        />
                        
                        <button
                            className="upload-button"
                            onClick={() => document.getElementById('file-input').click()}
                            disabled={step === 'generating'}
                        >
                            Choose File
                        </button>
                        
                        {file && (
                            <div className="selected-file">
                                <strong>Selected:</strong> {file.name} ({(file.size / 1024 / 1024).toFixed(2)} MB)
                            </div>
                        )}
                        
                        {error && (
                            <div className="error-message">
                                ❌ {error}
                            </div>
                        )}
                        
                        {file && (
                            <button
                                className="generate-button"
                                onClick={handleUploadAndExtract}
                                disabled={step === 'generating'}
                            >
                                🚀 Upload & Extract Text
                            </button>
                        )}
                    </div>
                </div>
            )}

            {/* Processing State */}
            {step === 'generating' && (
                <div className="processing-area">
                    <div className="processing-content">
                        <div className="processing-icon">⏳</div>
                        <h3>Processing...</h3>
                        <p>{progress}</p>
                        <div className="processing-steps">
                            <div className={`step ${uploadResult ? 'completed' : 'pending'}`}>
                                ✓ Upload & Extract Text
                            </div>
                            <div className={`step ${quizResult ? 'completed' : 'pending'}`}>
                                {uploadResult ? '⏳' : '○'} Generate Quiz with AI
                            </div>
                        </div>
                    </div>
                </div>
            )}

            {/* Success State */}
            {step === 'success' && quizResult && (
                <div className="success-area">
                    <div className="success-content">
                        <div className="success-icon">🎉</div>
                        <h3>Quiz Generated Successfully!</h3>
                        <div className="success-details">
                            <p><strong>File:</strong> {uploadResult?.fileName}</p>
                            <p><strong>Extracted Text:</strong> {uploadResult?.extractedTextLength} characters</p>
                            <p><strong>Questions Generated:</strong> {quizResult.questionsGenerated}</p>
                            <p><strong>Quiz ID:</strong> {quizResult.quiz?.id}</p>
                        </div>
                        
                        <div className="success-actions">
                            <button className="new-upload-button" onClick={handleNewUpload}>
                                📄 Upload New Document
                            </button>
                            <button className="view-quiz-button">
                                👁️ View Quiz
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* Error State */}
            {step === 'error' && (
                <div className="error-area">
                    <div className="error-content">
                        <div className="error-icon">❌</div>
                        <h3>Something went wrong</h3>
                        <p>{error}</p>
                        
                        {extractedContentId && (
                            <div className="error-actions">
                                <button className="retry-button" onClick={handleRetry} disabled={isRetrying}>
                                    {isRetrying ? '🔄 Retrying...' : '🔄 Retry Quiz Generation'}
                                </button>
                                <button className="new-upload-button" onClick={handleNewUpload}>
                                    📄 Upload New Document
                                </button>
                            </div>
                        )}
                        
                        {!extractedContentId && (
                            <div className="error-actions">
                                <button className="new-upload-button" onClick={handleNewUpload}>
                                    📄 Upload New Document
                                </button>
                            </div>
                        )}
                    </div>
                </div>
            )}

            <style jsx>{`
                .quiz-generation-flow {
                    max-width: 800px;
                    margin: 0 auto;
                    padding: 20px;
                }

                .flow-header {
                    text-align: center;
                    margin-bottom: 30px;
                }

                .flow-header h2 {
                    color: #333;
                    margin-bottom: 10px;
                }

                .flow-header p {
                    color: #666;
                    font-size: 16px;
                }

                .upload-area, .processing-area, .success-area, .error-area {
                    border: 2px solid #e1e5e9;
                    border-radius: 10px;
                    padding: 40px;
                    text-align: center;
                    background-color: #f8f9fa;
                    margin-bottom: 20px;
                }

                .upload-content, .processing-content, .success-content, .error-content {
                    display: flex;
                    flex-direction: column;
                    align-items: center;
                    gap: 15px;
                }

                .upload-icon, .processing-icon, .success-icon, .error-icon {
                    font-size: 48px;
                    margin-bottom: 10px;
                }

                .upload-button, .generate-button, .retry-button, .new-upload-button, .view-quiz-button {
                    background-color: #007bff;
                    color: white;
                    border: none;
                    padding: 12px 24px;
                    border-radius: 6px;
                    cursor: pointer;
                    font-size: 16px;
                    transition: background-color 0.3s ease;
                    margin: 5px;
                }

                .generate-button {
                    background-color: #28a745;
                }

                .retry-button {
                    background-color: #ffc107;
                    color: #212529;
                }

                .new-upload-button {
                    background-color: #6c757d;
                }

                .view-quiz-button {
                    background-color: #17a2b8;
                }

                .upload-button:hover, .generate-button:hover, .retry-button:hover, .new-upload-button:hover, .view-quiz-button:hover {
                    opacity: 0.8;
                }

                .upload-button:disabled, .generate-button:disabled, .retry-button:disabled {
                    background-color: #ccc;
                    cursor: not-allowed;
                }

                .selected-file, .success-details {
                    background-color: #e9ecef;
                    padding: 15px;
                    border-radius: 4px;
                    font-size: 14px;
                    width: 100%;
                    max-width: 400px;
                }

                .error-message {
                    color: #dc3545;
                    background-color: #f8d7da;
                    border: 1px solid #f5c6cb;
                    padding: 15px;
                    border-radius: 4px;
                    margin-top: 10px;
                    width: 100%;
                    max-width: 400px;
                }

                .processing-steps {
                    margin-top: 20px;
                    width: 100%;
                    max-width: 300px;
                }

                .step {
                    padding: 10px;
                    margin: 5px 0;
                    border-radius: 4px;
                    text-align: left;
                }

                .step.completed {
                    background-color: #d4edda;
                    color: #155724;
                }

                .step.pending {
                    background-color: #f8f9fa;
                    color: #6c757d;
                }

                .success-actions, .error-actions {
                    display: flex;
                    gap: 10px;
                    flex-wrap: wrap;
                    justify-content: center;
                    margin-top: 20px;
                }
            `}</style>
        </div>
    );
};

export default QuizGenerationFlow;
