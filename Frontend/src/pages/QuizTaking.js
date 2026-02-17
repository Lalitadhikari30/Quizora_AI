import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Clock, CheckCircle, ArrowLeft, ArrowRight } from 'lucide-react';
import toast from 'react-hot-toast';
import { quizService } from '../services/quizService';

const QuizTaking = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  
  const [quiz, setQuiz] = useState(null);
  const [currentQuestion, setCurrentQuestion] = useState(0);
  const [answers, setAnswers] = useState({});
  const [timeLeft, setTimeLeft] = useState(1500); // 25 minutes in seconds
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    const fetchQuiz = async () => {
      try {
        const quizData = await quizService.getQuiz(id);
        setQuiz(quizData);
        setTimeLeft(quizData.timeLimit || 1500);
      } catch (error) {
        toast.error(error.message || 'Failed to load quiz');
        navigate('/dashboard');
      } finally {
        setLoading(false);
      }
    };

    fetchQuiz();
  }, [id, navigate]);

  useEffect(() => {
    if (timeLeft > 0 && !loading) {
      const timer = setTimeout(() => setTimeLeft(timeLeft - 1), 1000);
      return () => clearTimeout(timer);
    } else if (timeLeft === 0 && !loading) {
      handleSubmit();
    }
  }, [timeLeft, loading]);

  const handleAnswerSelect = (questionId, answer) => {
    setAnswers(prev => ({
      ...prev,
      [questionId]: answer
    }));
  };

  const handleSubmit = async () => {
    if (submitting) return;
    
    setSubmitting(true);
    try {
      const answersArray = Object.entries(answers).map(([questionId, answer]) => ({
        questionId: parseInt(questionId),
        selectedAnswer: answer
      }));

      const result = await quizService.submitQuiz(id, answersArray);
      
      toast.success('Quiz submitted successfully!');
      navigate('/performance', { state: { result } });
    } catch (error) {
      toast.error(error.message || 'Failed to submit quiz');
    } finally {
      setSubmitting(false);
    }
  };

  const formatTime = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-orange-500"></div>
      </div>
    );
  }

  if (!quiz) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <p className="text-white">Quiz not found</p>
      </div>
    );
  }

  const question = quiz.questions?.[currentQuestion];
  const progress = ((currentQuestion + 1) / (quiz.questions?.length || 1)) * 100;

  return (
    <section className="bg-[#0a0a0a] min-h-screen py-12">
      <div className="container mx-auto px-4 max-w-4xl">
        
        {/* Quiz Header */}
        <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-6 mb-6">
          <div className="flex justify-between items-center mb-4">
            <h1 className="text-2xl font-bold text-white">{quiz.title || 'Quiz'}</h1>
            <div className="flex items-center space-x-2 text-orange-400">
              <Clock size={20} />
              <span className="font-mono">{formatTime(timeLeft)}</span>
            </div>
          </div>
          <div className="w-full bg-white/10 rounded-full h-2 mb-4">
            <div className="bg-gradient-to-r from-orange-500 to-red-500 h-2 rounded-full transition-all duration-300" style={{ width: `${progress}%` }}></div>
          </div>
          <p className="text-gray-400">
            Question {currentQuestion + 1} of {quiz.questions?.length || 0}
          </p>
        </div>

        {/* Question */}
        {question && (
          <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-6 mb-6">
            <h2 className="text-lg font-semibold text-white mb-6">{question.questionText}</h2>
            <div className="space-y-3">
              {question.options?.map((option, index) => (
                <label 
                  key={index} 
                  className="flex items-center space-x-3 p-4 border border-white/10 rounded-lg cursor-pointer hover:border-orange-400/50 transition-all bg-white/5"
                >
                  <input 
                    type="radio" 
                    name={`question-${question.id}`} 
                    value={option}
                    checked={answers[question.id] === option}
                    onChange={() => handleAnswerSelect(question.id, option)}
                    className="w-4 h-4 text-orange-500"
                  />
                  <span className="text-white">{option}</span>
                </label>
              ))}
            </div>
          </div>
        )}

        {/* Navigation */}
        <div className="flex justify-between">
          <button 
            onClick={() => setCurrentQuestion(Math.max(0, currentQuestion - 1))}
            disabled={currentQuestion === 0}
            className="px-6 py-3 bg-white/5 border border-white/10 text-white rounded-lg hover:border-orange-400/50 transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center"
          >
            <ArrowLeft size={20} className="mr-2" />
            Previous
          </button>
          
          {currentQuestion === (quiz.questions?.length || 0) - 1 ? (
            <button 
              onClick={handleSubmit}
              disabled={submitting}
              className="px-6 py-3 bg-gradient-to-r from-orange-500 to-red-500 text-white rounded-lg hover:from-orange-400 hover:to-red-400 transition-all disabled:opacity-50 flex items-center"
            >
              {submitting ? 'Submitting...' : 'Submit Quiz'}
              <CheckCircle size={20} className="ml-2" />
            </button>
          ) : (
            <button 
              onClick={() => setCurrentQuestion(Math.min(quiz.questions?.length - 1, currentQuestion + 1))}
              className="px-6 py-3 bg-gradient-to-r from-orange-500 to-red-500 text-white rounded-lg hover:from-orange-400 hover:to-red-400 transition-all flex items-center"
            >
              Next
              <ArrowRight size={20} className="ml-2" />
            </button>
          )}
        </div>

      </div>
    </section>
  );
};

export default QuizTaking;
