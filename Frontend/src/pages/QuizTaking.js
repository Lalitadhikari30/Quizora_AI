import React, { useState, useEffect, useCallback } from "react";
import { useParams, useNavigate, useLocation } from "react-router-dom";
import { 
  Clock, 
  CheckCircle2, 
  XCircle, 
  ArrowLeft, 
  ArrowRight, 
  RotateCcw, 
  BookOpen, 
  CheckCircle,
  HelpCircle,
  AlertCircle
} from "lucide-react";
import toast from "react-hot-toast";
import { quizService } from "../services/quizService";

const QuizTaking = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const location = useLocation();

  const [quiz, setQuiz] = useState(null);
  const [currentQuestion, setCurrentQuestion] = useState(0);
  const [answers, setAnswers] = useState({});
  const [timeLeft, setTimeLeft] = useState(600);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [submissionResult, setSubmissionResult] = useState(location.state?.result || null);
  const [isReviewMode, setIsReviewMode] = useState(false);

  /* ================= OPTION PARSER ================= */
  const parseOptions = (options) => {
    if (!options) return [];
    if (Array.isArray(options)) return options;

    if (typeof options === "string") {
      if (options.startsWith("[") && options.endsWith("]")) {
        try {
          return JSON.parse(options);
        } catch {
          return [];
        }
      }
      return options.split(",").map((opt) => opt.trim());
    }

    if (typeof options === "object") {
      return Object.values(options);
    }

    return [];
  };

  /* ================= FETCH QUIZ & PERSISTED ATTEMPT ================= */
  useEffect(() => {
    const searchParams = new URLSearchParams(location.search);
    const mode = searchParams.get("mode");

    const fetchQuiz = async () => {
      try {
        const quizData = await quizService.getQuiz(id);
        setQuiz(quizData);
        
        const totalDuration = quizData.timeLimit || 
          (quizData.questions?.length ? quizData.questions.length * (quizData.timePerQuestion || 60) : 600);
        setTimeLeft(totalDuration);

        // Load saved user answers from previous attempt
        const savedAttempt = localStorage.getItem(`quiz_attempt_${id}`);
        if (savedAttempt) {
          try {
            const parsed = JSON.parse(savedAttempt);
            if (parsed.answers && Object.keys(parsed.answers).length > 0) {
              setAnswers(parsed.answers);
            }
            if (parsed.result) {
              setSubmissionResult(parsed.result);
            }
          } catch (e) {
            console.warn("Could not parse saved attempt", e);
          }
        }

        if (mode === "review") {
          setIsReviewMode(true);
        }
      } catch (error) {
        console.error("Quiz fetch error:", error);
        toast.error("Failed to load quiz");
      } finally {
        setLoading(false);
      }
    };

    fetchQuiz();
  }, [id, location.search]);

  /* ================= ANSWER SELECT ================= */
  const handleAnswerSelect = (questionId, answer) => {
    setAnswers((prev) => ({
      ...prev,
      [questionId]: answer,
    }));
  };

  /* ================= SUBMIT QUIZ ================= */
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

      const result = await quizService.submitQuiz(id, answersArray);
      toast.success("Quiz submitted successfully!");
      setSubmissionResult(result);
      setIsReviewMode(true);

      // Persist answers & result in localStorage so reviewing always displays user's choices
      try {
        localStorage.setItem(`quiz_attempt_${id}`, JSON.stringify({
          answers: answers,
          result: result,
          submittedAt: new Date().toISOString()
        }));
      } catch (e) {
        console.warn("Storage error:", e);
      }

    } catch (error) {
      console.error("Submit error:", error);
      toast.error("Failed to submit quiz");
    } finally {
      setSubmitting(false);
    }
  }, [answers, id, quiz, submitting]);

  /* ================= RETAKE QUIZ ================= */
  const handleRetake = () => {
    setAnswers({});
    setCurrentQuestion(0);
    setSubmissionResult(null);
    setIsReviewMode(false);
    try {
      localStorage.removeItem(`quiz_attempt_${id}`);
    } catch (e) {}
    const totalDuration = quiz?.timeLimit || 
      (quiz?.questions?.length ? quiz.questions.length * (quiz.timePerQuestion || 60) : 600);
    setTimeLeft(totalDuration);
    navigate(`/quiz/${id}`);
  };

  /* ================= TIMER ================= */
  useEffect(() => {
    if (!loading && !isReviewMode && timeLeft > 0) {
      const timer = setTimeout(() => setTimeLeft((prev) => prev - 1), 1000);
      return () => clearTimeout(timer);
    }

    if (!loading && !isReviewMode && timeLeft === 0) {
      handleSubmit();
    }
  }, [timeLeft, loading, isReviewMode, handleSubmit]);

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
      <div className="flex items-center justify-center min-h-screen bg-[#0a0a0a] text-white font-mono">
        Quiz not found
      </div>
    );
  }

  const formatTime = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, "0")}`;
  };

  /* ================= RENDER REVIEW MODE ================= */
  if (isReviewMode) {
    const normalizeText = (s) => (s || "").toString().toLowerCase().replace(/[^a-z0-9]/g, "");
    const reviews = submissionResult?.answerReviews || [];
    
    // Calculate verified score in real-time across all questions
    let calculatedCorrectCount = 0;
    quiz.questions.forEach((question) => {
      const options = parseOptions(question.options);
      const reviewObj = reviews.find((r) => 
        (r.questionId && r.questionId === question.id) ||
        (r.question && r.question.trim().toLowerCase() === question.questionText?.trim().toLowerCase())
      );

      const userSelectedVal = answers[question.id] || reviewObj?.userAnswer;
      const correctAnsStr = question.correctAnswer?.trim() || reviewObj?.correctAnswer?.trim() || "";

      if (userSelectedVal && correctAnsStr) {
        const normCorrect = normalizeText(correctAnsStr);
        const normUser = normalizeText(userSelectedVal);

        let bestCorrectIdx = -1;
        options.forEach((opt, idx) => {
          const normOpt = normalizeText(opt);
          if (normOpt === normCorrect || idx.toString() === correctAnsStr) {
            bestCorrectIdx = idx;
          } else if (bestCorrectIdx === -1 && normCorrect.length > 5 && (normCorrect.startsWith(normOpt) || normOpt.startsWith(normCorrect))) {
            bestCorrectIdx = idx;
          }
        });

        if (bestCorrectIdx === -1 && normCorrect.length > 5) {
          options.forEach((opt, idx) => {
            const normOpt = normalizeText(opt);
            if (bestCorrectIdx === -1 && normOpt.length > 5 && (normCorrect.includes(normOpt) || normOpt.includes(normCorrect))) {
              bestCorrectIdx = idx;
            }
          });
        }

        const isCorrect = (bestCorrectIdx >= 0 && (normUser === normalizeText(options[bestCorrectIdx]) || userSelectedVal.toString() === bestCorrectIdx.toString())) ||
                          (normUser === normCorrect) ||
                          (reviewObj?.isCorrect === true);

        if (isCorrect) {
          calculatedCorrectCount++;
        }
      }
    });

    const total = quiz.questions.length || submissionResult?.totalQuestions || 1;
    const score = submissionResult?.score !== undefined && submissionResult.score > 0
      ? submissionResult.score
      : calculatedCorrectCount;
    const pct = submissionResult?.percentage !== undefined && submissionResult.percentage > 0
      ? Math.round(submissionResult.percentage)
      : Math.round((score / total) * 100);

    return (
      <section className="bg-[#0a0a0a] min-h-screen py-12 font-mono">
        <div className="container mx-auto px-4 max-w-4xl space-y-8 pb-24">

          {/* Results Summary Banner */}
          <div className="bg-white/5 border border-white/10 rounded-2xl p-8 backdrop-blur space-y-6">
            <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
              <div>
                <span className="text-xs px-3 py-1 rounded-full bg-orange-500/10 text-orange-400 border border-orange-500/20 font-bold uppercase tracking-wider">
                  Quiz Completed & Reviewed
                </span>
                <h1 className="text-2xl md:text-3xl font-bold text-white mt-2">
                  {quiz.title || "Quiz Results"}
                </h1>
              </div>

              <div className="flex gap-3">
                <button
                  onClick={handleRetake}
                  className="px-5 py-2.5 bg-orange-500 hover:bg-orange-600 text-white rounded-xl text-sm font-semibold transition flex items-center gap-2 shadow-lg shadow-orange-500/20"
                >
                  <RotateCcw size={16} /> Retake Quiz
                </button>
                <button
                  onClick={() => navigate("/dashboard")}
                  className="px-5 py-2.5 bg-white/10 hover:bg-white/15 text-gray-300 rounded-xl text-sm transition"
                >
                  Dashboard
                </button>
              </div>
            </div>

            {/* Score Overview */}
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 pt-2">
              <div className="bg-white/5 border border-white/10 rounded-xl p-4 text-center">
                <p className="text-xs text-gray-400">Total Score</p>
                <p className="text-3xl font-bold text-orange-400 mt-1">{pct}%</p>
                <p className="text-xs text-gray-500 mt-0.5">{score} out of {total} correct</p>
              </div>

              <div className="bg-white/5 border border-white/10 rounded-xl p-4 text-center">
                <p className="text-xs text-gray-400">Correct Answers</p>
                <p className="text-3xl font-bold text-green-400 mt-1">{score}</p>
                <p className="text-xs text-gray-500 mt-0.5">Accurate responses</p>
              </div>

              <div className="bg-white/5 border border-white/10 rounded-xl p-4 text-center">
                <p className="text-xs text-gray-400">Incorrect / Missed</p>
                <p className="text-3xl font-bold text-red-400 mt-1">{total - score}</p>
                <p className="text-xs text-gray-500 mt-0.5">Questions for review</p>
              </div>
            </div>
          </div>

          {/* Question-by-Question Detailed Review */}
          <div className="space-y-6">
            <h2 className="text-xl font-bold text-white flex items-center gap-2">
              <BookOpen size={20} className="text-orange-400" />
              Detailed Question Analysis
            </h2>

            {quiz.questions.map((question, qIdx) => {
              const options = parseOptions(question.options);
              const reviewObj = reviews.find((r) => 
                (r.questionId && r.questionId === question.id) ||
                (r.question && r.question.trim().toLowerCase() === question.questionText?.trim().toLowerCase())
              );

              // User's selected answer (from answers map or reviewObj)
              const userSelectedVal = answers[question.id] || reviewObj?.userAnswer;
              const correctAnsStr = question.correctAnswer?.trim() || reviewObj?.correctAnswer?.trim() || "";
              const hasAnswered = Boolean(userSelectedVal);

              const normalizeText = (s) => (s || "").toString().toLowerCase().replace(/[^a-z0-9]/g, "");
              const normCorrect = normalizeText(correctAnsStr);
              const normUser = normalizeText(userSelectedVal);

              // Find best matching correct option index
              let bestCorrectIdx = -1;
              options.forEach((opt, idx) => {
                const normOpt = normalizeText(opt);
                if (normOpt === normCorrect || idx.toString() === correctAnsStr) {
                  bestCorrectIdx = idx;
                } else if (bestCorrectIdx === -1 && normCorrect.length > 5 && (normCorrect.startsWith(normOpt) || normOpt.startsWith(normCorrect))) {
                  bestCorrectIdx = idx;
                }
              });

              // If still not found, try inclusion
              if (bestCorrectIdx === -1 && normCorrect.length > 5) {
                options.forEach((opt, idx) => {
                  const normOpt = normalizeText(opt);
                  if (bestCorrectIdx === -1 && normOpt.length > 5 && (normCorrect.includes(normOpt) || normOpt.includes(normCorrect))) {
                    bestCorrectIdx = idx;
                  }
                });
              }

              return (
                <div
                  key={question.id || qIdx}
                  className="bg-white/5 border border-white/10 rounded-2xl p-6 space-y-4"
                >
                  <div className="flex justify-between items-start gap-4">
                    <p className="text-white font-bold text-base">
                      <span className="text-orange-400 mr-2">Q{qIdx + 1}.</span>
                      {question.questionText}
                    </p>
                    {!hasAnswered && (
                      <span className="text-[11px] px-2.5 py-1 rounded bg-yellow-500/10 text-yellow-400 border border-yellow-500/20 whitespace-nowrap flex items-center gap-1 font-mono">
                        <AlertCircle size={12} /> Not Answered
                      </span>
                    )}
                  </div>

                  {/* Options List */}
                  <div className="space-y-2.5 pt-2">
                    {options.map((option, optIdx) => {
                      const normOpt = normalizeText(option);
                      
                      const isCorrectOption = (optIdx === bestCorrectIdx) || (normOpt === normCorrect);
                      
                      const isUserSelected = normUser && (
                        normUser === normOpt ||
                        (normUser.length > 6 && (normUser.startsWith(normOpt) || normOpt.startsWith(normUser))) ||
                        userSelectedVal.toString() === optIdx.toString()
                      );

                      let optionStyle = "border-white/10 bg-white/5 text-gray-300";
                      let badge = null;

                      if (isCorrectOption && isUserSelected) {
                        // User chose correct answer
                        optionStyle = "border-green-500 bg-green-500/15 text-white font-bold shadow-sm shadow-green-500/20";
                        badge = (
                          <span className="flex items-center gap-1 text-xs text-green-400 font-bold ml-auto bg-green-500/20 px-2.5 py-1 rounded-md">
                            <CheckCircle2 size={15} /> Your Correct Answer
                          </span>
                        );
                      } else if (isCorrectOption) {
                        // The actual correct answer
                        optionStyle = "border-green-500 bg-green-500/10 text-green-300 font-semibold";
                        badge = (
                          <span className="flex items-center gap-1 text-xs text-green-400 font-bold ml-auto bg-green-500/10 px-2.5 py-1 rounded-md">
                            <CheckCircle2 size={15} /> Correct Answer
                          </span>
                        );
                      } else if (isUserSelected && !isCorrectOption) {
                        // User chose wrong answer
                        optionStyle = "border-red-500 bg-red-500/15 text-red-300 font-bold shadow-sm shadow-red-500/20";
                        badge = (
                          <span className="flex items-center gap-1 text-xs text-red-400 font-bold ml-auto bg-red-500/20 px-2.5 py-1 rounded-md">
                            <XCircle size={15} /> Your Choice (Wrong)
                          </span>
                        );
                      }

                      return (
                        <div
                          key={optIdx}
                          className={`p-3.5 rounded-xl border flex items-center justify-between transition ${optionStyle}`}
                        >
                          <span className="text-sm">{option}</span>
                          {badge}
                        </div>
                      );
                    })}
                  </div>

                  {/* AI Explanation Box */}
                  {question.explanation && (
                    <div className="bg-orange-500/10 border border-orange-500/20 rounded-xl p-4 mt-3">
                      <div className="flex items-center gap-2 text-xs font-bold text-orange-400 uppercase tracking-wider mb-1">
                        <HelpCircle size={14} />
                        <span>AI Explanation</span>
                      </div>
                      <p className="text-gray-300 text-xs leading-relaxed">
                        {question.explanation}
                      </p>
                    </div>
                  )}
                </div>
              );
            })}
          </div>

          {/* Bottom Retake & Nav Actions */}
          <div className="flex justify-between items-center pt-4">
            <button
              onClick={() => navigate("/dashboard")}
              className="px-6 py-3 bg-white/5 border border-white/10 hover:bg-white/10 text-white rounded-xl text-sm transition"
            >
              Back to Dashboard
            </button>
            <button
              onClick={handleRetake}
              className="px-6 py-3 bg-orange-500 hover:bg-orange-600 text-white rounded-xl text-sm font-semibold transition flex items-center gap-2 shadow-lg shadow-orange-500/20"
            >
              <RotateCcw size={16} /> Retake This Quiz
            </button>
          </div>

        </div>
      </section>
    );
  }

  /* ================= RENDER TAKING MODE ================= */
  const question = quiz.questions[currentQuestion];
  const options = parseOptions(question.options);
  const progress = ((currentQuestion + 1) / quiz.questions.length) * 100;

  return (
    <section className="bg-[#0a0a0a] min-h-screen py-12 font-mono">
      <div className="container mx-auto px-4 max-w-4xl space-y-6">

        {/* Header */}
        <div className="bg-white/5 border border-white/10 rounded-2xl p-6">
          <div className="flex justify-between items-center mb-4">
            <h1 className="text-xl md:text-2xl font-bold text-white">
              {quiz.title || "Quiz"}
            </h1>

            <div className="flex items-center text-orange-400 space-x-2 bg-orange-500/10 px-3 py-1.5 rounded-lg border border-orange-500/20">
              <Clock size={18} />
              <span className="font-mono font-bold text-sm">{formatTime(timeLeft)}</span>
            </div>
          </div>

          <div className="w-full bg-white/10 rounded-full h-2 mb-2 overflow-hidden">
            <div
              className="bg-gradient-to-r from-orange-500 to-orange-400 h-full rounded-full transition-all duration-300"
              style={{ width: `${progress}%` }}
            />
          </div>

          <div className="flex justify-between items-center text-xs text-gray-400">
            <span>Question {currentQuestion + 1} of {quiz.questions.length}</span>
            <span>{Object.keys(answers).length} answered</span>
          </div>
        </div>

        {/* Question Card */}
        <div className="bg-white/5 border border-white/10 rounded-2xl p-6 md:p-8 space-y-6">
          <h2 className="text-white text-lg md:text-xl font-semibold leading-snug">
            <span className="text-orange-400 mr-2">Q{currentQuestion + 1}.</span>
            {question.questionText || question.question}
          </h2>

          <div className="space-y-3">
            {options.map((option, index) => (
              <label
                key={index}
                className={`flex items-center space-x-3 p-4 border rounded-xl cursor-pointer transition ${
                  answers[question.id] === option
                    ? "border-orange-500 bg-orange-500/10 text-white font-semibold"
                    : "border-white/10 bg-white/5 text-gray-300 hover:border-orange-400/50 hover:bg-white/10"
                }`}
              >
                <input
                  type="radio"
                  name={`question-${question.id}`}
                  value={option}
                  checked={answers[question.id] === option}
                  onChange={() => handleAnswerSelect(question.id, option)}
                  className="text-orange-500 focus:ring-orange-400"
                />
                <span className="text-sm">{option}</span>
              </label>
            ))}
          </div>
        </div>

        {/* Navigation */}
        <div className="flex justify-between items-center pt-2">
          <button
            onClick={() => setCurrentQuestion((prev) => Math.max(0, prev - 1))}
            disabled={currentQuestion === 0}
            className="px-6 py-3 bg-white/5 border border-white/10 text-white rounded-xl disabled:opacity-40 flex items-center gap-2 hover:bg-white/10 transition text-sm"
          >
            <ArrowLeft size={16} /> Previous
          </button>

          {currentQuestion === quiz.questions.length - 1 ? (
            <button
              onClick={handleSubmit}
              disabled={submitting}
              className="px-8 py-3 bg-gradient-to-r from-orange-500 to-orange-600 hover:from-orange-400 hover:to-orange-500 text-white rounded-xl font-bold flex items-center gap-2 transition shadow-lg shadow-orange-500/25 disabled:opacity-50 text-sm"
            >
              {submitting ? "Submitting..." : "Submit Quiz"}
              <CheckCircle size={18} />
            </button>
          ) : (
            <button
              onClick={() => setCurrentQuestion((prev) => Math.min(quiz.questions.length - 1, prev + 1))}
              className="px-8 py-3 bg-orange-500 hover:bg-orange-600 text-white rounded-xl font-bold flex items-center gap-2 transition shadow-lg shadow-orange-500/20 text-sm"
            >
              Next <ArrowRight size={16} />
            </button>
          )}
        </div>
      </div>
    </section>
  );
};

export default QuizTaking;
