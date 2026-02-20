// // // // import React, { useState, useEffect, useCallback } from 'react';
// // // // import { useParams, useNavigate } from 'react-router-dom';
// // // // import { Clock, CheckCircle, ArrowLeft, ArrowRight } from 'lucide-react';
// // // // import toast from 'react-hot-toast';
// // // // import { quizService } from '../services/quizService';

// // // // const QuizTaking = () => {
// // // //   const { id } = useParams();
// // // //   const navigate = useNavigate();
  
// // // //   const [quiz, setQuiz] = useState(null);
// // // //   const [currentQuestion, setCurrentQuestion] = useState(0);
// // // //   const [answers, setAnswers] = useState({});
// // // //   const [timeLeft, setTimeLeft] = useState(1500); // 25 minutes in seconds
// // // //   const [loading, setLoading] = useState(true);
// // // //   const [submitting, setSubmitting] = useState(false);

// // // //   useEffect(() => {
// // // //     const fetchQuiz = async () => {
// // // //       try {
// // // //         console.log('Fetching quiz with ID:', id);
        
// // // //         // For development, add a dev token if not present
// // // //         const token = localStorage.getItem('token');
// // // //         if (!token) {
// // // //           console.log('No token found, using development mode');
// // // //           localStorage.setItem('token', 'dev-token-' + Date.now());
// // // //           localStorage.setItem('userName', 'Dev User');
// // // //         }
        
// // // //         const quizData = await quizService.getQuiz(id);
// // // //         console.log('Quiz data received:', quizData);
        
// // // //         if (!quizData || !quizData.questions || quizData.questions.length === 0) {
// // // //           throw new Error('Quiz not found or has no questions');
// // // //         }
        
// // // //         setQuiz(quizData);
// // // //         setTimeLeft(quizData.timeLimit || 1500);
// // // //       } catch (error) {
// // // //         console.error('Quiz fetch error:', error);
// // // //         console.error('Error details:', error.response?.data);
        
// // // //         // Don't auto-redirect on error, show error message
// // // //         toast.error(error.message || 'Failed to load quiz. Please try again.');
        
// // // //         // Only redirect if it's a 404 or quiz not found
// // // //         if (error.response?.status === 404 || error.message?.includes('not found')) {
// // // //           setTimeout(() => navigate('/dashboard'), 2000);
// // // //         }
// // // //       } finally {
// // // //         setLoading(false);
// // // //       }
// // // //     };

// // // //     fetchQuiz();
// // // //   }, [id, navigate]);

// // // //   useEffect(() => {
// // // //     if (timeLeft > 0 && !loading) {
// // // //       const timer = setTimeout(() => setTimeLeft(timeLeft - 1), 1000);
// // // //       return () => clearTimeout(timer);
// // // //     } else if (timeLeft === 0 && !loading) {
// // // //       handleSubmit();
// // // //     }
// // // //   }, [timeLeft, loading, handleSubmit]);

// // // //   const handleAnswerSelect = (questionId, answer) => {
// // // //     console.log('Answer selected:', { questionId, answer });
// // // //     setAnswers(prev => ({
// // // //       ...prev,
// // // //       [questionId]: answer
// // // //     }));
// // // //   };

// // // //   const handleSubmit = useCallback(async () => {
// // // //     if (submitting) return;
    
// // // //     setSubmitting(true);
// // // //     try {
// // // //       const answersArray = Object.entries(answers).map(([questionId, answer]) => ({
// // // //         questionId: parseInt(questionId),
// // // //         selectedAnswer: answer
// // // //       }));

// // // //       const result = await quizService.submitQuiz(id, answersArray);
      
// // // //       toast.success('Quiz submitted successfully!');
// // // //       navigate('/performance', { state: { result } });
// // // //     } catch (error) {
// // // //       toast.error(error.message || 'Failed to submit quiz');
// // // //     } finally {
// // // //       setSubmitting(false);
// // // //     }
// // // //   }, [submitting, answers, id, navigate]);

// // // //   const formatTime = (seconds) => {
// // // //     const mins = Math.floor(seconds / 60);
// // // //     const secs = seconds % 60;
// // // //     return `${mins}:${secs.toString().padStart(2, '0')}`;
// // // //   };

// // // //   if (loading) {
// // // //     return (
// // // //       <div className="flex items-center justify-center min-h-screen">
// // // //         <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-orange-500"></div>
// // // //       </div>
// // // //     );
// // // //   }

// // // //   if (!quiz) {
// // // //     return (
// // // //       <div className="flex items-center justify-center min-h-screen">
// // // //         <div className="text-center">
// // // //           <p className="text-white mb-4">Quiz not found</p>
// // // //           <button 
// // // //             onClick={() => navigate('/dashboard')}
// // // //             className="px-4 py-2 bg-orange-500 text-white rounded-lg"
// // // //           >
// // // //             Back to Dashboard
// // // //           </button>
// // // //         </div>
// // // //       </div>
// // // //     );
// // // //   }

// // // //   if (!quiz.questions || quiz.questions.length === 0) {
// // // //     return (
// // // //       <div className="flex items-center justify-center min-h-screen">
// // // //         <div className="text-center">
// // // //           <p className="text-white mb-4">This quiz has no questions</p>
// // // //           <button 
// // // //             onClick={() => navigate('/dashboard')}
// // // //             className="px-4 py-2 bg-orange-500 text-white rounded-lg"
// // // //           >
// // // //             Back to Dashboard
// // // //           </button>
// // // //         </div>
// // // //       </div>
// // // //     );
// // // //   }

// // // //   const question = quiz.questions?.[currentQuestion];
// // // //   const progress = ((currentQuestion + 1) / (quiz.questions?.length || 1)) * 100;

// // // //   // Debug logging
// // // //   console.log('Current question:', question);
// // // //   console.log('All questions:', quiz.questions);

// // // //   return (
// // // //     <section className="bg-[#0a0a0a] min-h-screen py-12">
// // // //       <div className="container mx-auto px-4 max-w-4xl">
        
// // // //         {/* Quiz Header */}
// // // //         <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-6 mb-6">
// // // //           <div className="flex justify-between items-center mb-4">
// // // //             <h1 className="text-2xl font-bold text-white">{quiz.title || 'Quiz'}</h1>
// // // //             <div className="flex items-center space-x-2 text-orange-400">
// // // //               <Clock size={20} />
// // // //               <span className="font-mono">{formatTime(timeLeft)}</span>
// // // //             </div>
// // // //           </div>
// // // //           <div className="w-full bg-white/10 rounded-full h-2 mb-4">
// // // //             <div className="bg-gradient-to-r from-orange-500 to-red-500 h-2 rounded-full transition-all duration-300" style={{ width: `${progress}%` }}></div>
// // // //           </div>
// // // //           <p className="text-gray-400">
// // // //             Question {currentQuestion + 1} of {quiz.questions?.length || 0}
// // // //           </p>
// // // //         </div>

// // // //         {/* Question */}
// // // //         {question && (
// // // //           <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-6 mb-6">
// // // //             <h2 className="text-lg font-semibold text-white mb-6">
// // // //               {question.question || question.questionText || question.text || 'Question text not found'}
// // // //             </h2>
// // // //             <div className="space-y-3">
// // // //               {(question.options || question.choices || []).map((option, index) => (
// // // //                 <label 
// // // //                   key={index} 
// // // //                   className="flex items-center space-x-3 p-4 border border-white/10 rounded-lg cursor-pointer hover:border-orange-400/50 transition-all bg-white/5"
// // // //                 >
// // // //                   <input 
// // // //                     type="radio" 
// // // //                     name={`question-${question.id || currentQuestion}`} 
// // // //                     value={typeof option === 'string' ? option : option.text || option}
// // // //                     checked={answers[question.id || currentQuestion] === (typeof option === 'string' ? option : option.text || option)}
// // // //                     onChange={() => handleAnswerSelect(question.id || currentQuestion, typeof option === 'string' ? option : option.text || option)}
// // // //                     className="w-4 h-4 text-orange-500"
// // // //                   />
// // // //                   <span className="text-white">
// // // //                     {typeof option === 'string' ? option : option.text || option}
// // // //                   </span>
// // // //                 </label>
// // // //               ))}
// // // //             </div>
// // // //           </div>
// // // //         )}

// // // //         {/* No questions found */}
// // // //         {!question && quiz.questions && quiz.questions.length > 0 && (
// // // //           <div className="bg-red-500/10 border border-red-500/30 rounded-2xl p-6 mb-6">
// // // //             <p className="text-red-400">Question data not found. Please check the quiz structure.</p>
// // // //             <pre className="text-red-300 text-xs mt-2">
// // // //               {JSON.stringify(question, null, 2)}
// // // //             </pre>
// // // //           </div>
// // // //         )}

// // // //         {/* Navigation */}
// // // //         <div className="flex justify-between">
// // // //           <button 
// // // //             onClick={() => setCurrentQuestion(Math.max(0, currentQuestion - 1))}
// // // //             disabled={currentQuestion === 0}
// // // //             className="px-6 py-3 bg-white/5 border border-white/10 text-white rounded-lg hover:border-orange-400/50 transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center"
// // // //           >
// // // //             <ArrowLeft size={20} className="mr-2" />
// // // //             Previous
// // // //           </button>
          
// // // //           {currentQuestion === (quiz.questions?.length || 0) - 1 ? (
// // // //             <button 
// // // //               onClick={handleSubmit}
// // // //               disabled={submitting}
// // // //               className="px-6 py-3 bg-gradient-to-r from-orange-500 to-red-500 text-white rounded-lg hover:from-orange-400 hover:to-red-400 transition-all disabled:opacity-50 flex items-center"
// // // //             >
// // // //               {submitting ? 'Submitting...' : 'Submit Quiz'}
// // // //               <CheckCircle size={20} className="ml-2" />
// // // //             </button>
// // // //           ) : (
// // // //             <button 
// // // //               onClick={() => setCurrentQuestion(Math.min(quiz.questions?.length - 1, currentQuestion + 1))}
// // // //               className="px-6 py-3 bg-gradient-to-r from-orange-500 to-red-500 text-white rounded-lg hover:from-orange-400 hover:to-red-400 transition-all flex items-center"
// // // //             >
// // // //               Next
// // // //               <ArrowRight size={20} className="ml-2" />
// // // //             </button>
// // // //           )}
// // // //         </div>

// // // //       </div>
// // // //     </section>
// // // //   );
// // // // };

// // // // export default QuizTaking;


// // // import React, { useState, useEffect, useCallback } from 'react';
// // // import { useParams, useNavigate } from 'react-router-dom';
// // // import { Clock, CheckCircle, ArrowLeft, ArrowRight } from 'lucide-react';
// // // import toast from 'react-hot-toast';
// // // import { quizService } from '../services/quizService';

// // // const QuizTaking = () => {
// // //   const { id } = useParams();
// // //   const navigate = useNavigate();

// // //   const [quiz, setQuiz] = useState(null);
// // //   const [currentQuestion, setCurrentQuestion] = useState(0);
// // //   const [answers, setAnswers] = useState({});
// // //   const [timeLeft, setTimeLeft] = useState(1500);
// // //   const [loading, setLoading] = useState(true);
// // //   const [submitting, setSubmitting] = useState(false);

// // //   /* =====================================================
// // //      FETCH QUIZ
// // //   ===================================================== */
// // //   useEffect(() => {
// // //     const fetchQuiz = async () => {
// // //       try {
// // //         const quizData = await quizService.getQuiz(id);

// // //         if (!quizData || !quizData.questions?.length) {
// // //           throw new Error('Quiz not found or has no questions');
// // //         }

// // //         setQuiz(quizData);
// // //         setTimeLeft(quizData.timeLimit || 1500);
// // //       } catch (error) {
// // //         toast.error(error.message || 'Failed to load quiz');
// // //         setTimeout(() => navigate('/dashboard'), 2000);
// // //       } finally {
// // //         setLoading(false);
// // //       }
// // //     };

// // //     fetchQuiz();
// // //   }, [id, navigate]);

// // //   /* =====================================================
// // //      HANDLE SUBMIT (DEFINED BEFORE TIMER EFFECT)
// // //   ===================================================== */
// // //   const handleSubmit = useCallback(async () => {
// // //     if (submitting || !quiz) return;

// // //     setSubmitting(true);

// // //     try {
// // //       const answersArray = Object.entries(answers).map(
// // //         ([questionId, answer]) => ({
// // //           questionId: parseInt(questionId),
// // //           selectedAnswer: answer
// // //         })
// // //       );

// // //       const result = await quizService.submitQuiz(id, answersArray);

// // //       toast.success('Quiz submitted successfully!');
// // //       navigate('/performance', { state: { result } });
// // //     } catch (error) {
// // //       toast.error(error.message || 'Failed to submit quiz');
// // //     } finally {
// // //       setSubmitting(false);
// // //     }
// // //   }, [submitting, answers, id, navigate, quiz]);

// // //   /* =====================================================
// // //      TIMER EFFECT (NOW SAFE)
// // //   ===================================================== */
// // //   useEffect(() => {
// // //     if (loading || submitting) return;

// // //     if (timeLeft <= 0) {
// // //       handleSubmit();
// // //       return;
// // //     }

// // //     const timer = setTimeout(() => {
// // //       setTimeLeft(prev => prev - 1);
// // //     }, 1000);

// // //     return () => clearTimeout(timer);
// // //   }, [timeLeft, loading, submitting, handleSubmit]);

// // //   /* =====================================================
// // //      HANDLE ANSWER
// // //   ===================================================== */
// // //   const handleAnswerSelect = (questionId, answer) => {
// // //     setAnswers(prev => ({
// // //       ...prev,
// // //       [questionId]: answer
// // //     }));
// // //   };

// // //   const formatTime = (seconds) => {
// // //     const mins = Math.floor(seconds / 60);
// // //     const secs = seconds % 60;
// // //     return `${mins}:${secs.toString().padStart(2, '0')}`;
// // //   };

// // //   /* =====================================================
// // //      LOADING
// // //   ===================================================== */
// // //   if (loading) {
// // //     return (
// // //       <div className="flex items-center justify-center min-h-screen">
// // //         <div className="animate-spin rounded-full h-20 w-20 border-b-2 border-orange-500"></div>
// // //       </div>
// // //     );
// // //   }

// // //   if (!quiz) {
// // //     return (
// // //       <div className="flex items-center justify-center min-h-screen text-white">
// // //         Quiz not found
// // //       </div>
// // //     );
// // //   }

// // //   const question = quiz.questions[currentQuestion];
// // //   const progress =
// // //     ((currentQuestion + 1) / quiz.questions.length) * 100;

// // //   /* =====================================================
// // //      UI
// // //   ===================================================== */
// // //   return (
// // //     <section className="bg-[#0a0a0a] min-h-screen py-12">
// // //       <div className="container mx-auto px-4 max-w-4xl">

// // //         {/* Header */}
// // //         <div className="bg-white/5 border border-white/10 rounded-2xl p-6 mb-6">
// // //           <div className="flex justify-between items-center mb-4">
// // //             <h1 className="text-2xl font-bold text-white">
// // //               {quiz.title || 'Quiz'}
// // //             </h1>

// // //             <div className="flex items-center text-orange-400">
// // //               <Clock size={20} className="mr-2" />
// // //               <span className="font-mono">
// // //                 {formatTime(timeLeft)}
// // //               </span>
// // //             </div>
// // //           </div>

// // //           <div className="w-full bg-white/10 rounded-full h-2 mb-2">
// // //             <div
// // //               className="bg-gradient-to-r from-orange-500 to-red-500 h-2 rounded-full transition-all"
// // //               style={{ width: `${progress}%` }}
// // //             />
// // //           </div>

// // //           <p className="text-gray-400">
// // //             Question {currentQuestion + 1} of {quiz.questions.length}
// // //           </p>
// // //         </div>

// // //         {/* Question */}
// // //         <div className="bg-white/5 border border-white/10 rounded-2xl p-6 mb-6">
// // //           <h2 className="text-lg font-semibold text-white mb-6">
// // //             {question.question || question.questionText}
// // //           </h2>

// // //           <div className="space-y-3">
// // //             {(question.options || []).map((option, index) => {
// // //               const optionValue =
// // //                 typeof option === 'string'
// // //                   ? option
// // //                   : option.text || option;

// // //               const isSelected =
// // //                 answers[question.id || currentQuestion] === optionValue;

// // //               return (
// // //                 <label
// // //                   key={index}
// // //                   className={`flex items-center space-x-3 p-4 border rounded-lg cursor-pointer transition ${
// // //                     isSelected
// // //                       ? 'border-orange-500 bg-orange-500/10'
// // //                       : 'border-white/10 bg-white/5'
// // //                   }`}
// // //                 >
// // //                   <input
// // //                     type="radio"
// // //                     name={`question-${question.id || currentQuestion}`}
// // //                     value={optionValue}
// // //                     checked={isSelected}
// // //                     onChange={() =>
// // //                       handleAnswerSelect(
// // //                         question.id || currentQuestion,
// // //                         optionValue
// // //                       )
// // //                     }
// // //                     className="hidden"
// // //                   />

// // //                   <span className="text-white">
// // //                     {optionValue}
// // //                   </span>
// // //                 </label>
// // //               );
// // //             })}
// // //           </div>
// // //         </div>

// // //         {/* Navigation */}
// // //         <div className="flex justify-between">
// // //           <button
// // //             onClick={() =>
// // //               setCurrentQuestion(prev => Math.max(0, prev - 1))
// // //             }
// // //             disabled={currentQuestion === 0}
// // //             className="px-6 py-3 bg-white/5 border border-white/10 text-white rounded-lg disabled:opacity-40 flex items-center"
// // //           >
// // //             <ArrowLeft size={20} className="mr-2" />
// // //             Previous
// // //           </button>

// // //           {currentQuestion === quiz.questions.length - 1 ? (
// // //             <button
// // //               onClick={handleSubmit}
// // //               disabled={submitting}
// // //               className="px-6 py-3 bg-gradient-to-r from-orange-500 to-red-500 text-white rounded-lg disabled:opacity-50 flex items-center"
// // //             >
// // //               {submitting ? 'Submitting...' : 'Submit Quiz'}
// // //               <CheckCircle size={20} className="ml-2" />
// // //             </button>
// // //           ) : (
// // //             <button
// // //               onClick={() =>
// // //                 setCurrentQuestion(prev =>
// // //                   Math.min(quiz.questions.length - 1, prev + 1)
// // //                 )
// // //               }
// // //               className="px-6 py-3 bg-gradient-to-r from-orange-500 to-red-500 text-white rounded-lg flex items-center"
// // //             >
// // //               Next
// // //               <ArrowRight size={20} className="ml-2" />
// // //             </button>
// // //           )}
// // //         </div>

// // //       </div>
// // //     </section>
// // //   );
// // // };

// // // export default QuizTaking;




// // import React, { useState, useEffect, useCallback } from "react";
// // import { useParams, useNavigate } from "react-router-dom";
// // import { Clock, CheckCircle, ArrowLeft, ArrowRight } from "lucide-react";
// // import toast from "react-hot-toast";
// // import { quizService } from "../services/quizService";

// // const QuizTaking = () => {
// //   const { id } = useParams();
// //   const navigate = useNavigate();

// //   const [quiz, setQuiz] = useState(null);
// //   const [currentQuestion, setCurrentQuestion] = useState(0);
// //   const [answers, setAnswers] = useState({});
// //   const [timeLeft, setTimeLeft] = useState(1500);
// //   const [loading, setLoading] = useState(true);
// //   const [submitting, setSubmitting] = useState(false);

// //   /* ================= SAFE OPTION PARSER ================= */
// //   const parseOptions = (options) => {
// //     if (!options) return [];

// //     if (Array.isArray(options)) return options;

// //     if (typeof options === "string") {
// //       try {
// //         return JSON.parse(options);
// //       } catch {
// //         return [];
// //       }
// //     }

// //     return [];
// //   };

// //   /* ================= FETCH QUIZ ================= */
// //   useEffect(() => {
// //     const fetchQuiz = async () => {
// //       try {
// //         const quizData = await quizService.getQuiz(id);

// //         if (!quizData || !quizData.questions?.length) {
// //           throw new Error("Quiz not found or has no questions");
// //         }

// //         setQuiz(quizData);
// //         setTimeLeft(quizData.timeLimit || 1500);
// //       } catch (error) {
// //         toast.error(error.message || "Failed to load quiz");
// //         navigate("/dashboard");
// //       } finally {
// //         setLoading(false);
// //       }
// //     };

// //     fetchQuiz();
// //   }, [id, navigate]);

// //   /* ================= SUBMIT ================= */
// //   const handleSubmit = useCallback(async () => {
// //     if (submitting) return;

// //     setSubmitting(true);

// //     try {
// //       const answersArray = Object.entries(answers).map(
// //         ([questionId, answer]) => ({
// //           questionId: parseInt(questionId),
// //           selectedAnswer: answer,
// //         })
// //       );

// //       const result = await quizService.submitQuiz(id, answersArray);

// //       toast.success("Quiz submitted successfully!");
// //       navigate("/performance", { state: { result } });
// //     } catch (error) {
// //       toast.error(error.message || "Failed to submit quiz");
// //     } finally {
// //       setSubmitting(false);
// //     }
// //   }, [answers, id, navigate, submitting]);

// //   /* ================= TIMER ================= */
// //   useEffect(() => {
// //     if (loading) return;

// //     if (timeLeft <= 0) {
// //       handleSubmit();
// //       return;
// //     }

// //     const timer = setTimeout(() => {
// //       setTimeLeft((prev) => prev - 1);
// //     }, 1000);

// //     return () => clearTimeout(timer);
// //   }, [timeLeft, loading, handleSubmit]);

// //   const handleAnswerSelect = (questionId, answer) => {
// //     setAnswers((prev) => ({
// //       ...prev,
// //       [questionId]: answer,
// //     }));
// //   };

// //   const formatTime = (seconds) => {
// //     const mins = Math.floor(seconds / 60);
// //     const secs = seconds % 60;
// //     return `${mins}:${secs.toString().padStart(2, "0")}`;
// //   };

// //   if (loading) {
// //     return (
// //       <div className="flex items-center justify-center min-h-screen">
// //         <div className="animate-spin h-16 w-16 border-b-2 border-orange-500 rounded-full"></div>
// //       </div>
// //     );
// //   }

// //   if (!quiz) return null;

// //   const question = quiz.questions[currentQuestion];
// //   console.log("QUESTION OBJECT:", question);
// //   const options = parseOptions(question?.options || question?.choices);

// //   const progress =
// //     ((currentQuestion + 1) / quiz.questions.length) * 100;

// //   return (
// //     <section className="bg-[#0a0a0a] min-h-screen py-12">
// //       <div className="container mx-auto px-4 max-w-4xl">

// //         {/* HEADER */}
// //         <div className="bg-white/5 border border-white/10 rounded-2xl p-6 mb-6">
// //           <div className="flex justify-between items-center mb-4">
// //             <h1 className="text-2xl font-bold text-white">
// //               {quiz.title || "Quiz"}
// //             </h1>
// //             <div className="flex items-center space-x-2 text-orange-400">
// //               <Clock size={20} />
// //               <span className="font-mono">{formatTime(timeLeft)}</span>
// //             </div>
// //           </div>

// //           <div className="w-full bg-white/10 rounded-full h-2 mb-4">
// //             <div
// //               className="bg-orange-500 h-2 rounded-full"
// //               style={{ width: `${progress}%` }}
// //             />
// //           </div>

// //           <p className="text-gray-400">
// //             Question {currentQuestion + 1} of {quiz.questions.length}
// //           </p>
// //         </div>

// //         {/* QUESTION */}
// //         <div className="bg-white/5 border border-white/10 rounded-2xl p-6 mb-6">
// //           <h2 className="text-lg font-semibold text-white mb-6">
// //             {question.questionText || question.question}
// //           </h2>

// //           <div className="space-y-3">
// //             {options.map((option, index) => (
// //               <label
// //                 key={index}
// //                 className="flex items-center space-x-3 p-4 border border-white/10 rounded-lg cursor-pointer hover:border-orange-400/50 transition bg-white/5"
// //               >
// //                 <input
// //                   type="radio"
// //                   name={`question-${question.id}`}
// //                   value={option}
// //                   checked={answers[question.id] === option}
// //                   onChange={() =>
// //                     handleAnswerSelect(question.id, option)
// //                   }
// //                   className="w-4 h-4 text-orange-500"
// //                 />
// //                 <span className="text-white">{option}</span>
// //               </label>
// //             ))}
// //           </div>
// //         </div>

// //         {/* NAVIGATION */}
// //         <div className="flex justify-between">
// //           <button
// //             onClick={() =>
// //               setCurrentQuestion((prev) => Math.max(prev - 1, 0))
// //             }
// //             disabled={currentQuestion === 0}
// //             className="px-6 py-3 bg-white/5 border border-white/10 text-white rounded-lg disabled:opacity-50"
// //           >
// //             <ArrowLeft size={20} className="inline mr-2" />
// //             Previous
// //           </button>

// //           {currentQuestion === quiz.questions.length - 1 ? (
// //             <button
// //               onClick={handleSubmit}
// //               disabled={submitting}
// //               className="px-6 py-3 bg-orange-500 text-white rounded-lg disabled:opacity-50"
// //             >
// //               {submitting ? "Submitting..." : "Submit Quiz"}
// //               <CheckCircle size={20} className="inline ml-2" />
// //             </button>
// //           ) : (
// //             <button
// //               onClick={() =>
// //                 setCurrentQuestion((prev) =>
// //                   Math.min(prev + 1, quiz.questions.length - 1)
// //                 )
// //               }
// //               className="px-6 py-3 bg-orange-500 text-white rounded-lg"
// //             >
// //               Next
// //               <ArrowRight size={20} className="inline ml-2" />
// //             </button>
// //           )}
// //         </div>
// //       </div>
// //     </section>
// //   );
// // };

// // export default QuizTaking;


// import React, { useState, useEffect, useCallback } from "react";
// import { useParams, useNavigate } from "react-router-dom";
// import { Clock, CheckCircle, ArrowLeft, ArrowRight } from "lucide-react";
// import toast from "react-hot-toast";
// import { quizService } from "../services/quizService";

// const QuizTaking = () => {
//   const { id } = useParams();
//   const navigate = useNavigate();

//   const [quiz, setQuiz] = useState(null);
//   const [currentQuestion, setCurrentQuestion] = useState(0);
//   const [answers, setAnswers] = useState({});
//   const [timeLeft, setTimeLeft] = useState(1500);
//   const [loading, setLoading] = useState(true);
//   const [submitting, setSubmitting] = useState(false);

//   /* ================= SAFE OPTION PARSER ================= */
//   const parseOptions = (options) => {
//     if (!options) return [];

//     // Already array
//     if (Array.isArray(options)) return options;

//     // String handling
//     if (typeof options === "string") {
//       // JSON format
//       if (options.startsWith("[") && options.endsWith("]")) {
//         try {
//           return JSON.parse(options);
//         } catch {
//           return [];
//         }
//       }

//       // Comma separated string
//       return options.split(",").map((opt) => opt.trim());
//     }

//     // Object format
//     if (typeof options === "object") {
//       return Object.values(options);
//     }

//     return [];
//   };

//   /* ================= FETCH QUIZ ================= */
//   useEffect(() => {
//     const fetchQuiz = async () => {
//       try {
//         const quizData = await quizService.getQuiz(id);

//         if (!quizData || !quizData.questions?.length) {
//           throw new Error("Quiz not found or has no questions");
//         }

//         setQuiz(quizData);
//         setTimeLeft(quizData.timeLimit || 1500);
//       } catch (error) {
//         toast.error(error.message || "Failed to load quiz");
//         navigate("/dashboard");
//       } finally {
//         setLoading(false);
//       }
//     };

//     fetchQuiz();
//   }, [id, navigate]);

//   /* ================= SUBMIT QUIZ ================= */
//   const handleSubmit = useCallback(async () => {
//     if (submitting) return;

//     setSubmitting(true);

//     try {
//       const answersArray = Object.entries(answers).map(
//         ([questionId, answer]) => ({
//           questionId: parseInt(questionId),
//           selectedAnswer: answer,
//         })
//       );

// const result = await quizService.submitQuiz(id, {
//   answers: answersArray
// });

//       toast.success("Quiz submitted successfully!");
//       navigate("/performance", { state: { result } });
//     } catch (error) {
//       toast.error(error.message || "Failed to submit quiz");
//     } finally {
//       setSubmitting(false);
//     }
//   }, [answers, id, navigate, submitting]);

//   /* ================= TIMER ================= */
//   useEffect(() => {
//     if (loading) return;

//     if (timeLeft <= 0) {
//       handleSubmit();
//       return;
//     }

//     const timer = setTimeout(() => {
//       setTimeLeft((prev) => prev - 1);
//     }, 1000);

//     return () => clearTimeout(timer);
//   }, [timeLeft, loading, handleSubmit]);

//   const handleAnswerSelect = (questionId, answer) => {
//     setAnswers((prev) => ({
//       ...prev,
//       [questionId]: answer,
//     }));
//   };

//   const formatTime = (seconds) => {
//     const mins = Math.floor(seconds / 60);
//     const secs = seconds % 60;
//     return `${mins}:${secs.toString().padStart(2, "0")}`;
//   };

//   /* ================= LOADING STATE ================= */
//   if (loading) {
//     return (
//       <div className="flex items-center justify-center min-h-screen bg-[#0a0a0a]">
//         <div className="animate-spin h-16 w-16 border-b-2 border-orange-500 rounded-full"></div>
//       </div>
//     );
//   }

//   if (!quiz) return null;

//   const question = quiz.questions[currentQuestion];
//   const options = parseOptions(question?.options);

//   const progress =
//     ((currentQuestion + 1) / quiz.questions.length) * 100;

//   return (
//     <section className="bg-[#0a0a0a] min-h-screen py-12">
//       <div className="container mx-auto px-4 max-w-4xl">

//         {/* HEADER */}
//         <div className="bg-white/5 border border-white/10 rounded-2xl p-6 mb-6">
//           <div className="flex justify-between items-center mb-4">
//             <h1 className="text-2xl font-bold text-white">
//               {quiz.title || "Quiz"}
//             </h1>
//             <div className="flex items-center space-x-2 text-orange-400">
//               <Clock size={20} />
//               <span className="font-mono">{formatTime(timeLeft)}</span>
//             </div>
//           </div>

//           <div className="w-full bg-white/10 rounded-full h-2 mb-4">
//             <div
//               className="bg-orange-500 h-2 rounded-full"
//               style={{ width: `${progress}%` }}
//             />
//           </div>

//           <p className="text-gray-400">
//             Question {currentQuestion + 1} of {quiz.questions.length}
//           </p>
//         </div>

//         {/* QUESTION */}
//         <div className="bg-white/5 border border-white/10 rounded-2xl p-6 mb-6">
//           <h2 className="text-lg font-semibold text-white mb-6">
//             {question.questionText || question.question}
//           </h2>

//           <div className="space-y-3">
//             {options.length === 0 && (
//               <p className="text-gray-500 text-sm">
//                 No options available for this question.
//               </p>
//             )}

//             {options.map((option, index) => (
//               <label
//                 key={index}
//                 className="flex items-center space-x-3 p-4 border border-white/10 rounded-lg cursor-pointer hover:border-orange-400/50 transition bg-white/5"
//               >
//                 <input
//                   type="radio"
//                   name={`question-${question.id}`}
//                   value={option}
//                   checked={answers[question.id] === option}
//                   onChange={() =>
//                     handleAnswerSelect(question.id, option)
//                   }
//                   className="w-4 h-4 text-orange-500"
//                 />
//                 <span className="text-white">{option}</span>
//               </label>
//             ))}
//           </div>
//         </div>

//         {/* NAVIGATION */}
//         <div className="flex justify-between">
//           <button
//             onClick={() =>
//               setCurrentQuestion((prev) => Math.max(prev - 1, 0))
//             }
//             disabled={currentQuestion === 0}
//             className="px-6 py-3 bg-white/5 border border-white/10 text-white rounded-lg disabled:opacity-50"
//           >
//             <ArrowLeft size={20} className="inline mr-2" />
//             Previous
//           </button>

//           {currentQuestion === quiz.questions.length - 1 ? (
//             <button
//               onClick={handleSubmit}
//               disabled={submitting}
//               className="px-6 py-3 bg-orange-500 text-white rounded-lg disabled:opacity-50"
//             >
//               {submitting ? "Submitting..." : "Submit Quiz"}
//               <CheckCircle size={20} className="inline ml-2" />
//             </button>
//           ) : (
//             <button
//               onClick={() =>
//                 setCurrentQuestion((prev) =>
//                   Math.min(prev + 1, quiz.questions.length - 1)
//                 )
//               }
//               className="px-6 py-3 bg-orange-500 text-white rounded-lg"
//             >
//               Next
//               <ArrowRight size={20} className="inline ml-2" />
//             </button>
//           )}
//         </div>
//       </div>
//     </section>
//   );
// };

// export default QuizTaking;




import React, { useState, useEffect, useCallback } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Clock, CheckCircle, ArrowLeft, ArrowRight } from "lucide-react";
import toast from "react-hot-toast";
import { quizService } from "../services/quizService";

const QuizTaking = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [quiz, setQuiz] = useState(null);
  const [currentQuestion, setCurrentQuestion] = useState(0);
  const [answers, setAnswers] = useState({});
  const [timeLeft, setTimeLeft] = useState(1500);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  /* ================= OPTION PARSER ================= */
  const parseOptions = (options) => {
    if (!options) return [];

    if (Array.isArray(options)) return options;

    if (typeof options === "string") {
      // JSON format
      if (options.startsWith("[") && options.endsWith("]")) {
        try {
          return JSON.parse(options);
        } catch {
          return [];
        }
      }
      // Comma separated string
      return options.split(",").map((opt) => opt.trim());
    }

    if (typeof options === "object") {
      return Object.values(options);
    }

    return [];
  };

  /* ================= FETCH QUIZ ================= */
  useEffect(() => {
    const fetchQuiz = async () => {
      try {
        const quizData = await quizService.getQuiz(id);
        setQuiz(quizData);
        setTimeLeft(quizData.timeLimit || 1500);
      } catch (error) {
        console.error("Quiz fetch error:", error);
        toast.error("Failed to load quiz");
      } finally {
        setLoading(false);
      }
    };

    fetchQuiz();
  }, [id]);

  /* ================= TIMER ================= */
  useEffect(() => {
    if (!loading && timeLeft > 0) {
      const timer = setTimeout(() => setTimeLeft((prev) => prev - 1), 1000);
      return () => clearTimeout(timer);
    }

    if (!loading && timeLeft === 0) {
      handleSubmit();
    }
  }, [timeLeft, loading]);

  /* ================= ANSWER SELECT ================= */
  const handleAnswerSelect = (questionId, answer) => {
    setAnswers((prev) => ({
      ...prev,
      [questionId]: answer,
    }));
  };

  /* ================= SUBMIT ================= */
  const handleSubmit = useCallback(async () => {
    if (submitting || !quiz) return;

    setSubmitting(true);

    try {
      const answersArray = quiz.questions
        .map((question) => {
          const selectedValue = answers[question.id];
          if (selectedValue === undefined) return null;

          const options = parseOptions(question.options);
          const selectedIndex = options.findIndex(
            (opt) => opt === selectedValue
          );

          return {
            questionId: question.id,
            selectedAnswer: selectedIndex >= 0 ? selectedIndex : null,
          };
        })
        .filter(Boolean);

      console.log("FINAL SUBMIT PAYLOAD:", answersArray);

      const result = await quizService.submitQuiz(id, answersArray);

      toast.success("Quiz submitted successfully!");
      navigate("/performance", { state: { result } });
    } catch (error) {
      console.error("Submit error:", error);
      toast.error("Failed to submit quiz");
    } finally {
      setSubmitting(false);
    }
  }, [answers, id, navigate, quiz, submitting]);

  /* ================= LOADING ================= */
  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-[#0a0a0a]">
        <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-orange-500"></div>
      </div>
    );
  }

  if (!quiz || !quiz.questions || quiz.questions.length === 0) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-[#0a0a0a] text-white">
        Quiz not found
      </div>
    );
  }

  const question = quiz.questions[currentQuestion];
  const options = parseOptions(question.options);

  const progress =
    ((currentQuestion + 1) / quiz.questions.length) * 100;

  const formatTime = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, "0")}`;
  };

  /* ================= UI ================= */
  return (
    <section className="bg-[#0a0a0a] min-h-screen py-12">
      <div className="container mx-auto px-4 max-w-4xl">

        {/* Header */}
        <div className="bg-white/5 border border-white/10 rounded-2xl p-6 mb-6">
          <div className="flex justify-between items-center mb-4">
            <h1 className="text-2xl font-bold text-white">
              {quiz.title || "Quiz"}
            </h1>

            <div className="flex items-center text-orange-400 space-x-2">
              <Clock size={18} />
              <span className="font-mono">{formatTime(timeLeft)}</span>
            </div>
          </div>

          <div className="w-full bg-white/10 rounded-full h-2 mb-2">
            <div
              className="bg-orange-500 h-2 rounded-full transition-all"
              style={{ width: `${progress}%` }}
            />
          </div>

          <p className="text-gray-400 text-sm">
            Question {currentQuestion + 1} of {quiz.questions.length}
          </p>
        </div>

        {/* Question */}
        <div className="bg-white/5 border border-white/10 rounded-2xl p-6 mb-6">
          <h2 className="text-white text-lg font-semibold mb-6">
            {question.questionText || question.question}
          </h2>

          <div className="space-y-3">
            {options.map((option, index) => (
              <label
                key={index}
                className="flex items-center space-x-3 p-4 border border-white/10 rounded-lg cursor-pointer hover:border-orange-400 transition bg-white/5"
              >
                <input
                  type="radio"
                  name={`question-${question.id}`}
                  value={option}
                  checked={answers[question.id] === option}
                  onChange={() =>
                    handleAnswerSelect(question.id, option)
                  }
                  className="text-orange-500"
                />
                <span className="text-white">{option}</span>
              </label>
            ))}
          </div>
        </div>

        {/* Navigation */}
        <div className="flex justify-between">
          <button
            onClick={() =>
              setCurrentQuestion((prev) => Math.max(0, prev - 1))
            }
            disabled={currentQuestion === 0}
            className="px-6 py-3 bg-white/5 border border-white/10 text-white rounded-lg disabled:opacity-50 flex items-center"
          >
            <ArrowLeft size={18} className="mr-2" />
            Previous
          </button>

          {currentQuestion === quiz.questions.length - 1 ? (
            <button
              onClick={handleSubmit}
              disabled={submitting}
              className="px-6 py-3 bg-orange-500 text-white rounded-lg flex items-center"
            >
              {submitting ? "Submitting..." : "Submit Quiz"}
              <CheckCircle size={18} className="ml-2" />
            </button>
          ) : (
            <button
              onClick={() =>
                setCurrentQuestion((prev) =>
                  Math.min(quiz.questions.length - 1, prev + 1)
                )
              }
              className="px-6 py-3 bg-orange-500 text-white rounded-lg flex items-center"
            >
              Next
              <ArrowRight size={18} className="ml-2" />
            </button>
          )}
        </div>
      </div>
    </section>
  );
};

export default QuizTaking;
