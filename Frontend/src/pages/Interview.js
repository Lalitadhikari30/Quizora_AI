import { Brain, CheckCircle, Clock, MessageSquare, Mic, Play, RotateCcw, Video } from 'lucide-react';
import { useState, useEffect, useRef } from 'react';
import toast from 'react-hot-toast';
import { useNavigate } from 'react-router-dom';
import { interviewService } from '../services/interviewService';

const Interview = () => {
  const navigate = useNavigate();
  const [showSetup, setShowSetup] = useState(true);
  const [setupData, setSetupData] = useState({
    jobRole: '',
    experience: 'MID',
    difficulty: 'INTERMEDIATE'
  });
  const [currentSession, setCurrentSession] = useState(null);
  const [currentQuestionObj, setCurrentQuestionObj] = useState(null);
  const [questionIndex, setQuestionIndex] = useState(0);
  const totalQuestions = 10;
  const [userAnswer, setUserAnswer] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [secondsElapsed, setSecondsElapsed] = useState(0);

  // === CAMERA PREVIEW LOGIC ===
  const [cameraStream, setCameraStream] = useState(null);
  const videoRef = useRef(null);

  const toggleCamera = async () => {
    if (cameraStream) {
      cameraStream.getTracks().forEach(track => track.stop());
      setCameraStream(null);
      toast.success('Camera disabled');
    } else {
      try {
        const stream = await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
        setCameraStream(stream);
        if (videoRef.current) {
          videoRef.current.srcObject = stream;
        }
        toast.success('Camera enabled');
      } catch (err) {
        console.error('Error accessing camera:', err);
        toast.error('Failed to access camera. Please check permissions.');
      }
    }
  };

  useEffect(() => {
    return () => {
      if (cameraStream) {
        cameraStream.getTracks().forEach(track => track.stop());
      }
    };
  }, [cameraStream]);

  useEffect(() => {
    if (cameraStream && videoRef.current) {
      videoRef.current.srcObject = cameraStream;
    }
  }, [cameraStream, showSetup]);

  // === AI VOICE ASSISTANT (SPEECH SYNTHESIS - TTS) ===
  const [isMuted, setIsMuted] = useState(false);

  const speakQuestion = (text) => {
    if (isMuted) return;
    window.speechSynthesis.cancel();
    if (text) {
      const utterance = new SpeechSynthesisUtterance(text);
      const voices = window.speechSynthesis.getVoices();
      const englishVoice = voices.find(voice => voice.lang.startsWith('en-'));
      if (englishVoice) {
        utterance.voice = englishVoice;
      }
      window.speechSynthesis.speak(utterance);
    }
  };

  // === SPEECH-TO-TEXT (STT DICTATION) ===
  const [isListening, setIsListening] = useState(false);
  const [recognition, setRecognition] = useState(null);

  useEffect(() => {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (SpeechRecognition) {
      const rec = new SpeechRecognition();
      rec.continuous = true;
      rec.interimResults = true;
      rec.lang = 'en-US';

      rec.onresult = (event) => {
        let finalTranscript = '';
        for (let i = event.resultIndex; i < event.results.length; ++i) {
          if (event.results[i].isFinal) {
            finalTranscript += event.results[i][0].transcript;
          }
        }
        if (finalTranscript) {
          setUserAnswer(prev => prev + (prev ? ' ' : '') + finalTranscript);
        }
      };

      rec.onerror = (event) => {
        console.error('Speech recognition error:', event.error);
        if (event.error === 'not-allowed') {
          toast.error('Microphone permission denied.');
        }
        setIsListening(false);
      };

      rec.onend = () => {
        setIsListening(false);
      };

      setRecognition(rec);
    }
  }, []);

  // Auto-start listening after the question loads and finishes reading
  useEffect(() => {
    if (!showSetup && currentQuestionObj?.questionText && recognition) {
      // Reset transcript for the new question
      setUserAnswer('');
      
      const speakTimer = setTimeout(() => {
        speakQuestion(currentQuestionObj.questionText);
      }, 500);
      
      const listenTimer = setTimeout(() => {
        try {
          if (!isListening) {
            recognition.start();
            setIsListening(true);
            toast.success('Microphone activated. Please speak your answer.');
          }
        } catch (e) {
          console.warn("SpeechRecognition already started:", e);
        }
      }, 4000); // 4-second delay for welcome/question speak warm-up

      return () => {
        clearTimeout(speakTimer);
        clearTimeout(listenTimer);
      };
    }
  }, [currentQuestionObj, showSetup, recognition]);

  const toggleListening = () => {
    if (!recognition) {
      toast.error('Speech recognition is not supported in this browser. Please use Chrome or Edge.');
      return;
    }

    if (isListening) {
      recognition.stop();
      setIsListening(false);
      toast.success('Speech dictation stopped.');
    } else {
      try {
        recognition.start();
        setIsListening(true);
        toast.success('Listening... Speak your answer.');
      } catch (err) {
        console.error('Failed to start speech recognition:', err);
      }
    }
  };

  // === SILENCE DETECTION AND INACTIVITY TIMEOUT ===
  const [silenceTime, setSilenceTime] = useState(0);

  useEffect(() => {
    setSilenceTime(0);
  }, [userAnswer, currentQuestionObj]);

  useEffect(() => {
    let silenceInterval = null;
    if (!showSetup && currentSession) {
      silenceInterval = setInterval(() => {
        setSilenceTime(prev => {
          const nextTime = prev + 1;
          
          if (nextTime === 45) {
            speakQuestion("Take your time. Whenever you're ready, feel free to speak your answer.");
            toast("No voice input detected. Take your time to answer.", { icon: '🎙️' });
          }
          
          if (nextTime >= 90) {
            clearInterval(silenceInterval);
            speakQuestion("Ending the mock interview due to prolonged inactivity. Goodbye.");
            toast.error("Interview ended due to prolonged inactivity.");
            
            // Automatically complete/end interview
            setTimeout(() => {
              endInterview();
            }, 3000);
          }
          
          return nextTime;
        });
      }, 1000);
    }
    return () => {
      if (silenceInterval) clearInterval(silenceInterval);
    };
  }, [showSetup, currentSession]);

  // General Timer effect
  useEffect(() => {
    let interval = null;
    if (!showSetup && currentSession) {
      interval = setInterval(() => {
        setSecondsElapsed(prev => prev + 1);
      }, 1000);
    }
    return () => {
      if (interval) clearInterval(interval);
    };
  }, [showSetup, currentSession]);

  const formatTimer = (totalSeconds) => {
    const mins = Math.floor(totalSeconds / 60);
    const secs = totalSeconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  const startInterview = async () => {
    if (!setupData.jobRole || !setupData.experience) {
      toast.error('Please fill in all required fields');
      return;
    }

    let stream = null;
    try {
      // Request Camera and Audio permissions
      stream = await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
      toast.success('Permissions granted!');
    } catch (permissionErr) {
      console.warn('Camera/mic notice:', permissionErr);
      try {
        // Try audio only if video failed
        stream = await navigator.mediaDevices.getUserMedia({ audio: true });
        toast.success('Microphone enabled!');
      } catch (audioErr) {
        console.warn('Audio only notice:', audioErr);
        toast("Continuing with text & voice synthesis mode.", { icon: 'ℹ️' });
      }
    }

    try {
      toast.loading('Starting AI Mock Interview...', { id: 'start-interview' });
      const response = await interviewService.startInterview(setupData);
      toast.dismiss('start-interview');

      const sessionId = response.sessionId || response.id || Date.now();
      const nextQ = response.nextQuestion || { 
        questionId: "1", 
        questionText: `Welcome to your mock interview for the ${setupData.jobRole} role! Please introduce yourself and discuss your relevant background.` 
      };
      
      if (stream) {
        setCameraStream(stream);
      }
      setCurrentSession({ id: sessionId });
      setCurrentQuestionObj(nextQ);
      setQuestionIndex(0);
      setSecondsElapsed(0);
      setShowSetup(false);

      setTimeout(() => {
        if (stream && videoRef.current) {
          videoRef.current.srcObject = stream;
        }
      }, 300);

      toast.success('Interview started!');
    } catch (error) {
      toast.dismiss('start-interview');
      console.error('Start interview error:', error);
      toast.error(error.response?.data?.error || error.message || 'Failed to start interview');
    }
  };

  const submitAnswer = async () => {
    if (!userAnswer.trim() || !currentSession || !currentQuestionObj) {
      toast.error('Please speak your answer before proceeding');
      return;
    }

    // Stop listening during evaluation to prevent transcribing background noise
    if (isListening && recognition) {
      try {
        recognition.stop();
      } catch (e) {
        console.warn(e);
      }
      setIsListening(false);
    }

    setIsSubmitting(true);
    try {
      const response = await interviewService.submitAnswer(
        currentSession.id,
        currentQuestionObj.questionId || (questionIndex + 1),
        userAnswer
      );

      toast.success(`Answer submitted! Score: ${response.evaluation?.score || 0}/10`);
      
      if (response.nextQuestion) {
        setQuestionIndex(prev => prev + 1);
        setCurrentQuestionObj(response.nextQuestion);
        setUserAnswer('');
      } else {
        toast("Interview complete! Generating assessment report...", { icon: 'ℹ️' });
        endInterview();
      }
    } catch (error) {
      toast.error(error.response?.data?.error || error.message || 'Failed to submit answer');
    } finally {
      setIsSubmitting(false);
    }
  };

  const endInterview = async () => {
    if (!currentSession) return;

    try {
      await interviewService.getInterviewReport(currentSession.id);
      toast.success('Interview completed!');
      navigate('/performance');
    } catch (error) {
      toast.error(error.message || 'Failed to complete interview');
    }
  };

  if (showSetup) {
    return (
      <section className="min-h-screen bg-[#0a0a0a] py-12" data-scroll-section>
        <div className="container mx-auto px-4 sm:px-6 lg:px-8 max-w-4xl">
          <div className="space-y-8">
            {/* Header */}
            <div className="text-center space-y-4">
              <div className="flex items-center justify-center space-x-3">
                <Brain size={48} className="text-orange-400" />
                <h1 className="text-4xl font-bold bg-gradient-to-r from-orange-400 via-orange-500 to-red-500 bg-clip-text text-transparent font-mono">Interview Practice</h1>
              </div>
              <p className="text-gray-400 max-w-2xl text-lg font-mono mx-auto">
                Practice your interview skills with AI-powered questions and real-time feedback
              </p>
            </div>

            {/* Setup Form */}
            <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-8">
              <h2 className="text-2xl font-bold text-white mb-6 font-mono text-center">Setup Your Interview</h2>
              <div className="space-y-6">
                <div>
                  <label className="block text-sm font-medium text-gray-300 mb-2 font-mono text-left">Job Role *</label>
                  <input
                    type="text"
                    placeholder="e.g., Frontend Developer"
                    className="w-full px-4 py-3 bg-white/5 border border-white/10 rounded-lg text-white placeholder-gray-500 focus:border-orange-400 focus:outline-none focus:ring-2 focus:ring-orange-400/20 font-mono text-left"
                    value={setupData.jobRole}
                    onChange={(e) => setSetupData({...setupData, jobRole: e.target.value})}
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-300 mb-2 font-mono text-left">
                    Experience Level *
                  </label>
                  <div className="grid grid-cols-3 gap-3">
                    {[
                      { id: "ENTRY", label: "Entry Level" },
                      { id: "MID", label: "Mid Level" },
                      { id: "SENIOR", label: "Senior Level" }
                    ].map((exp) => (
                      <button
                        key={exp.id}
                        type="button"
                        onClick={() => setSetupData({ ...setupData, experience: exp.id })}
                        className={`py-3 px-4 rounded-xl font-mono text-xs md:text-sm font-semibold transition-all duration-200 ${
                          setupData.experience === exp.id
                            ? "bg-orange-500 text-white shadow-lg shadow-orange-500/25 border border-orange-400"
                            : "bg-white/5 text-gray-400 hover:bg-white/10 hover:text-white border border-white/10"
                        }`}
                      >
                        {exp.label}
                      </button>
                    ))}
                  </div>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-300 mb-2 font-mono text-left">
                    Difficulty Level
                  </label>
                  <div className="grid grid-cols-3 gap-3">
                    {[
                      { id: "BEGINNER", label: "Beginner" },
                      { id: "INTERMEDIATE", label: "Intermediate" },
                      { id: "ADVANCED", label: "Advanced" }
                    ].map((diff) => (
                      <button
                        key={diff.id}
                        type="button"
                        onClick={() => setSetupData({ ...setupData, difficulty: diff.id })}
                        className={`py-3 px-4 rounded-xl font-mono text-xs md:text-sm font-semibold transition-all duration-200 ${
                          setupData.difficulty === diff.id
                            ? "bg-orange-500 text-white shadow-lg shadow-orange-500/25 border border-orange-400"
                            : "bg-white/5 text-gray-400 hover:bg-white/10 hover:text-white border border-white/10"
                        }`}
                      >
                        {diff.label}
                      </button>
                    ))}
                  </div>
                </div>

                <button
                  onClick={startInterview}
                  className="w-full py-3 px-4 bg-gradient-to-r from-orange-500 to-orange-600 text-white font-semibold rounded-lg hover:from-orange-400 hover:to-orange-500 transition-all duration-200 border border-orange-500/20 flex items-center justify-center font-mono"
                >
                  <Play className="mr-2" size={20} />
                  Start Interview
                </button>
              </div>
            </div>
          </div>
        </div>
      </section>
    );
  }

  // Interview Interface
  return (
    <section className="min-h-screen bg-[#0a0a0a] py-12" data-scroll-section>
      <div className="container mx-auto px-4 sm:px-6 lg:px-8 max-w-4xl">
        <div className="space-y-6">
          {/* Interview Header */}
          <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-6">
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-4">
                <div className="flex items-center space-x-3">
                  <Video className="text-orange-400" size={24} />
                  <div>
                    <h2 className="text-xl font-bold text-white font-mono">Interview in Progress</h2>
                    <p className="text-gray-400 text-sm font-mono">{setupData.jobRole} - {setupData.experience}</p>
                  </div>
                </div>
              </div>
              <div className="flex items-center space-x-4">
                <div className="flex items-center space-x-2 text-gray-400 font-mono">
                  <Clock size={16} />
                  <span className="text-sm font-mono">{formatTimer(secondsElapsed)}</span>
                </div>
                <button className="p-2 bg-white/5 border border-white/10 rounded-lg hover:border-orange-400/50 transition-all">
                  <RotateCcw size={16} className="text-gray-400" />
                </button>
              </div>
            </div>
          </div>

          {/* Main Interview Area */}
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            {/* Video Area */}
            <div className="lg:col-span-2">
              <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl aspect-video relative overflow-hidden flex items-center justify-center">
                {cameraStream ? (
                  <video 
                    ref={videoRef}
                    autoPlay 
                    playsInline 
                    muted 
                    className="w-full h-full object-cover"
                  />
                ) : (
                  <div className="text-center space-y-4">
                    <Video size={48} className="text-orange-400 mx-auto" />
                    <p className="text-gray-400">Camera preview will appear here</p>
                  </div>
                )}
                <button 
                  onClick={toggleCamera}
                  className="absolute bottom-4 right-4 px-4 py-2 bg-orange-500 hover:bg-orange-600 text-white rounded-lg transition-colors font-mono text-sm shadow-lg"
                >
                  {cameraStream ? "Disable Camera" : "Enable Camera"}
                </button>
              </div>

              {/* Current Question */}
              <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-6 mt-6">
                <h3 className="text-lg font-semibold text-white mb-4 font-mono text-left">Current Question</h3>
                <p className="text-gray-300 mb-6 font-mono text-left text-lg">
                  {currentQuestionObj?.questionText || currentQuestionObj?.question || "Loading question..."}
                </p>
                
                {/* Speech Transcript Output */}
                <div className="w-full min-h-[120px] max-h-[200px] overflow-y-auto px-4 py-3 bg-white/5 border border-white/10 rounded-lg text-white font-mono mb-4 text-left relative flex flex-col justify-between">
                  {userAnswer ? (
                    <p className="text-gray-200 text-sm leading-relaxed">{userAnswer}</p>
                  ) : (
                    <div className="flex flex-col items-center justify-center py-4 text-gray-500 space-y-2">
                      {isListening ? (
                        <>
                          <div className="flex space-x-1 justify-center items-center h-4">
                            <span className="w-1.5 h-3 bg-orange-500 rounded-full animate-bounce" style={{ animationDelay: '0ms' }}></span>
                            <span className="w-1.5 h-4 bg-orange-400 rounded-full animate-bounce" style={{ animationDelay: '150ms' }}></span>
                            <span className="w-1.5 h-3 bg-orange-500 rounded-full animate-bounce" style={{ animationDelay: '300ms' }}></span>
                          </div>
                          <p className="text-xs text-orange-400/80 animate-pulse">Listening... Start speaking your answer.</p>
                        </>
                      ) : (
                        <p className="text-xs">Waiting for microphone activation...</p>
                      )}
                    </div>
                  )}
                  {isListening && (
                    <div className="absolute top-3 right-3 flex items-center space-x-1.5">
                      <span className="w-2.5 h-2.5 bg-red-500 rounded-full animate-ping"></span>
                      <span className="text-[10px] text-red-400 uppercase font-mono tracking-wider font-semibold">REC</span>
                    </div>
                  )}
                </div>
                
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-3">
                    <button 
                      onClick={toggleListening}
                      title={isListening ? "Stop listening" : "Start speaking answer"}
                      className={`p-3 border rounded-lg transition-all ${
                        isListening 
                          ? "bg-red-500/20 border-red-500 text-red-500 animate-pulse" 
                          : "bg-white/5 border-white/10 hover:border-orange-400/50 text-gray-400"
                      }`}
                    >
                      <Mic size={18} />
                    </button>
                    <button 
                      onClick={() => {
                        const newMute = !isMuted;
                        setIsMuted(newMute);
                        if (newMute) {
                          window.speechSynthesis.cancel();
                        } else if (currentQuestionObj?.questionText) {
                          speakQuestion(currentQuestionObj.questionText);
                        }
                        toast.success(newMute ? "Assistant voice muted" : "Assistant voice unmuted");
                      }}
                      title={isMuted ? "Unmute Assistant Voice" : "Mute Assistant Voice"}
                      className={`p-3 border rounded-lg transition-all ${
                        isMuted 
                          ? "bg-red-500/10 border-red-500/30 text-red-400" 
                          : "bg-white/5 border-white/10 hover:border-orange-400/50 text-orange-400"
                      }`}
                    >
                      <MessageSquare size={18} />
                    </button>
                  </div>
                  <button 
                    onClick={submitAnswer}
                    disabled={isSubmitting}
                    className="px-6 py-3 bg-orange-500 hover:bg-orange-600 text-white rounded-lg transition-colors font-mono disabled:opacity-50 font-semibold"
                  >
                    {isSubmitting ? "Evaluating..." : "Next Question"}
                  </button>
                </div>
              </div>
            </div>

            {/* Sidebar */}
            <div className="space-y-6">
              {/* AI Assistant */}
              <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-6">
                <div className="flex items-center space-x-3 mb-4">
                  <Brain size={20} className="text-orange-400" />
                  <h3 className="text-lg font-semibold text-white font-mono text-left">AI Assistant</h3>
                </div>
                <div className="space-y-3">
                  <div className="bg-orange-500/10 border border-orange-500/20 rounded-lg p-3">
                    <p className="text-orange-400 text-sm font-medium font-mono text-left">Tip:</p>
                    <p className="text-gray-300 text-sm mt-1 font-mono text-left">
                      Speak clearly and structure your answers using the STAR method.
                    </p>
                  </div>
                  <div className="bg-white/5 rounded-lg p-3">
                    <p className="text-gray-400 text-sm font-mono text-left">
                      The AI is analyzing your responses and will provide feedback at the end.
                    </p>
                  </div>
                </div>
              </div>

              {/* Progress */}
              <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-6">
                <h3 className="text-lg font-semibold text-white mb-4 font-mono text-left">Progress</h3>
                <div className="space-y-3">
                  <div className="flex items-center justify-between">
                    <span className="text-gray-400 text-sm font-mono text-left">Questions Answered</span>
                    <span className="text-orange-400 text-sm font-medium font-mono text-right">{questionIndex + 1}/{totalQuestions}</span>
                  </div>
                  <div className="w-full bg-white/10 rounded-full h-2">
                    <div className="bg-gradient-to-r from-orange-500 to-orange-600 h-2 rounded-full" style={{width: `${((questionIndex + 1) / totalQuestions) * 100}%`}}></div>
                  </div>
                  <div className="space-y-2 mt-4">
                    {Array.from({length: totalQuestions}, (_, i) => (
                      <div key={i} className={`w-8 h-8 rounded-full flex items-center justify-center text-xs font-medium ${
                        i <= questionIndex ? 'bg-orange-500 text-white' : 'bg-white/10 text-gray-500'
                      }`}>
                        {i <= questionIndex ? <CheckCircle size={12} /> : i + 1}
                      </div>
                    ))}
                  </div>
                </div>
              </div>

              {/* End Interview */}
              <button 
                onClick={endInterview}
                className="w-full py-3 px-4 bg-red-500/10 border border-red-500/20 text-red-400 rounded-lg hover:bg-red-500/20 transition-colors font-mono"
              >
                End Interview
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};



export default Interview;
