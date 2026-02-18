import React, { useState, useCallback } from 'react';
import uploadService from '../services/uploadService';

const QuizUpload = ({ token, onQuizGenerated }) => {
    const [file, setFile] = useState(null);
    const [uploading, setUploading] = useState(false);
    const [error, setError] = useState('');
    const [progress, setProgress] = useState('');

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
            setProgress('');
        }
    };

    const handleUpload = useCallback(async () => {
        if (!file) {
            setError('Please select a file first');
            return;
        }

        if (!token) {
            setError('Please sign in to upload files');
            return;
        }

        setUploading(true);
        setError('');
        setProgress('Uploading file...');

        try {
            setProgress('Extracting text from file...');
            const result = await uploadService.uploadFileForQuiz(file, token);
            
            setProgress('Quiz generated successfully!');
            
            // Call parent callback with the result
            if (onQuizGenerated) {
                onQuizGenerated(result);
            }
            
            // Reset form
            setFile(null);
            document.getElementById('file-input').value = '';
            
        } catch (error) {
            setError(error.message);
            setProgress('');
        } finally {
            setUploading(false);
        }
    }, [file, token, onQuizGenerated]);

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

    return (
        <div className="quiz-upload-container">
            <div 
                className="upload-area"
                onDrop={handleDrop}
                onDragOver={handleDragOver}
            >
                <div className="upload-content">
                    <div className="upload-icon">
                        📄
                    </div>
                    <h3>Upload Document for Quiz Generation</h3>
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
                        disabled={uploading}
                    >
                        {uploading ? 'Processing...' : 'Choose File'}
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
                    
                    {progress && (
                        <div className="progress-message">
                            ⏳ {progress}
                        </div>
                    )}
                    
                    {file && !uploading && (
                        <button
                            className="generate-button"
                            onClick={handleUpload}
                        >
                            🚀 Generate Quiz
                        </button>
                    )}
                </div>
            </div>
            
            <style jsx>{`
                .quiz-upload-container {
                    max-width: 600px;
                    margin: 0 auto;
                    padding: 20px;
                }
                
                .upload-area {
                    border: 2px dashed #ccc;
                    border-radius: 10px;
                    padding: 40px;
                    text-align: center;
                    background-color: #f9f9f9;
                    transition: border-color 0.3s ease;
                }
                
                .upload-area:hover {
                    border-color: #007bff;
                }
                
                .upload-content {
                    display: flex;
                    flex-direction: column;
                    align-items: center;
                    gap: 15px;
                }
                
                .upload-icon {
                    font-size: 48px;
                    margin-bottom: 10px;
                }
                
                .upload-button, .generate-button {
                    background-color: #007bff;
                    color: white;
                    border: none;
                    padding: 12px 24px;
                    border-radius: 6px;
                    cursor: pointer;
                    font-size: 16px;
                    transition: background-color 0.3s ease;
                }
                
                .upload-button:hover, .generate-button:hover {
                    background-color: #0056b3;
                }
                
                .upload-button:disabled, .generate-button:disabled {
                    background-color: #ccc;
                    cursor: not-allowed;
                }
                
                .generate-button {
                    background-color: #28a745;
                    margin-top: 10px;
                }
                
                .generate-button:hover {
                    background-color: #1e7e34;
                }
                
                .selected-file {
                    background-color: #e9ecef;
                    padding: 10px;
                    border-radius: 4px;
                    font-size: 14px;
                }
                
                .error-message {
                    color: #dc3545;
                    background-color: #f8d7da;
                    border: 1px solid #f5c6cb;
                    padding: 10px;
                    border-radius: 4px;
                    margin-top: 10px;
                }
                
                .progress-message {
                    color: #007bff;
                    background-color: #d1ecf1;
                    border: 1px solid #bee5eb;
                    padding: 10px;
                    border-radius: 4px;
                    margin-top: 10px;
                }
            `}</style>
        </div>
    );
};

export default QuizUpload;
