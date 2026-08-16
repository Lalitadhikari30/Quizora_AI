import {
  Clock,
  FileText,
  Sliders,
  Sparkles,
  Timer,
  Upload,
  X
} from "lucide-react";
import { useCallback, useState } from "react";
import { useDropzone } from "react-dropzone";
import toast from "react-hot-toast";
import { useNavigate } from "react-router-dom";

import { quizService } from "../services/quizService";
import { fileService } from "../services/fileService";

const QuizGeneration = () => {
  const navigate = useNavigate();
  const [isGenerating, setIsGenerating] = useState(false);
  const [uploadedFile, setUploadedFile] = useState(null);
  const [activeTab, setActiveTab] = useState("create");

  // Customization State for both sections
  const [title, setTitle] = useState("");
  const [sourceContent, setSourceContent] = useState("");
  const [questionCount, setQuestionCount] = useState(10);
  const [timePerQuestion, setTimePerQuestion] = useState(60); // seconds
  const [customTotalMinutes, setCustomTotalMinutes] = useState(10); // minutes
  const [useCustomTotalTime, setUseCustomTotalTime] = useState(false);
  const [difficulty, setDifficulty] = useState("INTERMEDIATE");

  // Calculated total time in seconds
  const calculatedTotalSeconds = useCustomTotalTime
    ? customTotalMinutes * 60
    : questionCount * timePerQuestion;

  const formatDuration = (totalSeconds) => {
    const mins = Math.floor(totalSeconds / 60);
    const secs = totalSeconds % 60;
    if (mins === 0) return `${secs}s`;
    if (secs === 0) return `${mins} min${mins > 1 ? "s" : ""}`;
    return `${mins}m ${secs}s`;
  };

  // ================= FILE DROP =================
  const onDrop = useCallback((acceptedFiles) => {
    const file = acceptedFiles[0];
    if (!file) return;

    setUploadedFile(file);
    toast.success(`Selected "${file.name}"`);
  }, []);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    multiple: false,
    accept: {
      "application/pdf": [".pdf"],
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document": [".docx"],
      "application/msword": [".doc"],
      "text/plain": [".txt"]
    }
  });

  // ================= MANUAL QUIZ SUBMISSION =================
  const handleManualGenerate = async (e) => {
    e.preventDefault();
    if (!title.trim()) {
      toast.error("Please enter a quiz title");
      return;
    }
    if (!sourceContent.trim()) {
      toast.error("Please provide source content or topic notes");
      return;
    }

    setIsGenerating(true);
    try {
      const payload = {
        title: title.trim(),
        sourceContent: sourceContent.trim(),
        sourceType: "TEXT",
        type: "MULTIPLE_CHOICE",
        questionCount: parseInt(questionCount),
        difficulty: difficulty,
        timePerQuestion: parseInt(timePerQuestion),
        timeLimit: parseInt(calculatedTotalSeconds)
      };

      toast.loading("Generating customized quiz with AI...", { id: "generating" });
      const response = await quizService.generateQuiz(payload);
      toast.dismiss("generating");

      const quizData = response?.quiz || response?.data || response;
      const quizId = quizData?.id || quizData?.quizId;
      if (!quizId) {
        throw new Error("Quiz generation succeeded but Quiz ID was not returned.");
      }

      toast.success("Quiz generated successfully! Starting quiz...");
      navigate(`/quiz/${quizId}`);
    } catch (error) {
      toast.dismiss("generating");
      console.error("Manual quiz generation error:", error);
      toast.error(error.response?.data?.error || error.message || "Failed to generate quiz");
    } finally {
      setIsGenerating(false);
    }
  };

  // ================= FILE QUIZ SUBMISSION =================
  const handleFileGenerate = async () => {
    if (!uploadedFile) {
      toast.error("Please upload a file first");
      return;
    }

    setIsGenerating(true);
    try {
      const formData = new FormData();
      formData.append("file", uploadedFile);
      formData.append("questionCount", questionCount);
      formData.append("difficulty", difficulty);
      formData.append("timePerQuestion", timePerQuestion);
      formData.append("timeLimit", calculatedTotalSeconds);

      toast.loading("Reading document and generating quiz questions...", { id: "generating-file" });
      const response = await fileService.uploadFileForQuiz(formData);
      toast.dismiss("generating-file");

      const quizData = response?.quiz || response?.data?.quiz || response?.data || response;
      const quizId = quizData?.id || quizData?.quizId;
      if (!quizId) {
        throw new Error("Quiz created but Quiz ID missing from response");
      }

      toast.success("Quiz generated from document! Starting quiz...");
      navigate(`/quiz/${quizId}`);
    } catch (error) {
      toast.dismiss("generating-file");
      console.error("File quiz generation error:", error);
      toast.error(error.response?.data?.error || error.message || "Failed to generate quiz");
    } finally {
      setIsGenerating(false);
    }
  };

  // ================= REUSABLE CUSTOMIZATION SETTINGS PANEL =================
  const renderCustomizationPanel = () => (
    <div className="bg-[#141416] border border-white/10 rounded-xl p-6 space-y-6">
      <div className="flex items-center space-x-2 text-orange-400 font-mono font-semibold text-sm uppercase tracking-wider">
        <Sliders size={18} />
        <span>Quiz Customization Settings</span>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">

        {/* 1. Number of Questions */}
        <div className="space-y-2">
          <label className="block text-sm font-mono text-gray-300">
            Number of Questions: <span className="text-orange-400 font-bold">{questionCount}</span>
          </label>
          <div className="flex flex-wrap gap-2">
            {[5, 10, 15, 20].map((num) => (
              <button
                key={num}
                type="button"
                onClick={() => setQuestionCount(num)}
                className={`px-4 py-2 rounded-lg font-mono text-sm transition ${
                  questionCount === num
                    ? "bg-orange-500 text-white font-bold"
                    : "bg-white/5 text-gray-400 hover:bg-white/10 border border-white/10"
                }`}
              >
                {num}
              </button>
            ))}
            <div className="flex items-center space-x-2">
              <input
                type="number"
                min="1"
                max="30"
                value={questionCount}
                onChange={(e) => setQuestionCount(Math.max(1, Math.min(30, parseInt(e.target.value) || 1)))}
                className="w-20 px-3 py-2 bg-white/5 border border-white/10 rounded-lg text-white font-mono text-center text-sm focus:border-orange-400 focus:outline-none"
                placeholder="Custom"
              />
              <span className="text-xs text-gray-500 font-mono">max 30</span>
            </div>
          </div>
        </div>

        {/* 2. Difficulty Level */}
        <div className="space-y-2">
          <label className="block text-sm font-mono text-gray-300">
            Difficulty Level
          </label>
          <div className="grid grid-cols-3 gap-2">
            {[
              { id: "BEGINNER", label: "Beginner" },
              { id: "INTERMEDIATE", label: "Medium" },
              { id: "ADVANCED", label: "Advanced" }
            ].map((d) => (
              <button
                key={d.id}
                type="button"
                onClick={() => setDifficulty(d.id)}
                className={`py-2 px-3 rounded-lg font-mono text-xs text-center transition ${
                  difficulty === d.id
                    ? "bg-orange-500 text-white font-bold"
                    : "bg-white/5 text-gray-400 hover:bg-white/10 border border-white/10"
                }`}
              >
                {d.label}
              </button>
            ))}
          </div>
        </div>

        {/* 3. Time Per Question */}
        <div className="space-y-2">
          <label className="block text-sm font-mono text-gray-300 flex items-center space-x-1.5">
            <Timer size={15} className="text-orange-400" />
            <span>Time Per Question: <span className="text-orange-400 font-bold">{timePerQuestion}s</span></span>
          </label>
          <div className="flex flex-wrap gap-2">
            {[30, 45, 60, 90, 120].map((sec) => (
              <button
                key={sec}
                type="button"
                onClick={() => {
                  setTimePerQuestion(sec);
                  if (!useCustomTotalTime) {
                    setCustomTotalMinutes(Math.ceil((questionCount * sec) / 60));
                  }
                }}
                className={`px-3 py-1.5 rounded-lg font-mono text-xs transition ${
                  timePerQuestion === sec
                    ? "bg-orange-500 text-white font-bold"
                    : "bg-white/5 text-gray-400 hover:bg-white/10 border border-white/10"
                }`}
              >
                {sec}s
              </button>
            ))}
          </div>
        </div>

        {/* 4. Total Quiz Duration */}
        <div className="space-y-2">
          <div className="flex justify-between items-center">
            <label className="text-sm font-mono text-gray-300 flex items-center space-x-1.5">
              <Clock size={15} className="text-orange-400" />
              <span>Total Quiz Time: <span className="text-orange-400 font-bold">{formatDuration(calculatedTotalSeconds)}</span></span>
            </label>
            <button
              type="button"
              onClick={() => setUseCustomTotalTime(!useCustomTotalTime)}
              className="text-xs text-orange-400/80 hover:text-orange-400 font-mono underline"
            >
              {useCustomTotalTime ? "Auto calculate" : "Custom time"}
            </button>
          </div>

          {useCustomTotalTime ? (
            <div className="flex items-center space-x-3">
              <input
                type="number"
                min="1"
                max="120"
                value={customTotalMinutes}
                onChange={(e) => setCustomTotalMinutes(Math.max(1, Math.min(120, parseInt(e.target.value) || 1)))}
                className="w-24 px-3 py-2 bg-white/5 border border-white/10 rounded-lg text-white font-mono text-sm focus:border-orange-400 focus:outline-none"
              />
              <span className="text-sm text-gray-400 font-mono">minutes total</span>
            </div>
          ) : (
            <div className="bg-white/5 border border-white/10 rounded-lg p-2.5 text-xs text-gray-400 font-mono flex justify-between items-center">
              <span>{questionCount} questions × {timePerQuestion}s</span>
              <span className="text-orange-400 font-bold">{formatDuration(calculatedTotalSeconds)}</span>
            </div>
          )}
        </div>

      </div>
    </div>
  );

  return (
    <section className="bg-[#0a0a0a] min-h-screen py-12">
      <div className="container mx-auto px-4 max-w-4xl space-y-8 pb-24">

        {/* Header */}
        <div className="text-center space-y-3">
          <div className="flex justify-center items-center space-x-2">
            <Sparkles className="text-orange-400" size={28} />
            <h1 className="text-3xl md:text-4xl font-bold text-white font-mono">
              AI Quiz Generator
            </h1>
          </div>
          <p className="text-gray-400 font-mono text-sm">
            Generate custom interactive quizzes from notes or uploaded study materials
          </p>
        </div>

        {/* Tabs */}
        <div className="flex justify-center">
          <div className="bg-white/5 border border-white/10 rounded-xl p-1.5 inline-flex gap-1">
            <button
              onClick={() => setActiveTab("create")}
              className={`px-6 py-2.5 rounded-lg font-mono text-sm transition flex items-center gap-2 ${
                activeTab === "create"
                  ? "bg-orange-500 text-white font-bold shadow-lg shadow-orange-500/20"
                  : "text-gray-400 hover:text-white"
              }`}
            >
              <FileText size={16} /> Create from Text
            </button>

            <button
              onClick={() => setActiveTab("import")}
              className={`px-6 py-2.5 rounded-lg font-mono text-sm transition flex items-center gap-2 ${
                activeTab === "import"
                  ? "bg-orange-500 text-white font-bold shadow-lg shadow-orange-500/20"
                  : "text-gray-400 hover:text-white"
              }`}
            >
              <Upload size={16} /> Upload Document
            </button>
          </div>
        </div>

        {/* TAB 1: MANUAL TEXT GENERATION */}
        {activeTab === "create" && (
          <form onSubmit={handleManualGenerate} className="bg-white/5 border border-white/10 rounded-2xl p-6 md:p-8 space-y-6">
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-mono text-gray-300 mb-2">
                  Quiz Title *
                </label>
                <input
                  type="text"
                  placeholder="e.g. Java Concurrency & Threads Quiz"
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  className="w-full px-4 py-3 bg-white/5 border border-white/10 rounded-lg text-white font-mono focus:border-orange-400 focus:outline-none"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-mono text-gray-300 mb-2">
                  Source Content / Notes / Topic Description *
                </label>
                <textarea
                  rows={6}
                  placeholder="Paste your study notes, article content, or key concepts here..."
                  value={sourceContent}
                  onChange={(e) => setSourceContent(e.target.value)}
                  className="w-full px-4 py-3 bg-white/5 border border-white/10 rounded-lg text-white font-mono focus:border-orange-400 focus:outline-none"
                  required
                />
              </div>
            </div>

            {/* Customization Settings */}
            {renderCustomizationPanel()}

            {/* Submit Button */}
            <button
              type="submit"
              disabled={isGenerating}
              className="w-full py-4 bg-gradient-to-r from-orange-500 to-orange-600 hover:from-orange-400 hover:to-orange-500 text-white rounded-xl font-mono font-bold text-base transition shadow-lg shadow-orange-500/25 disabled:opacity-50 flex items-center justify-center gap-2"
            >
              <Sparkles size={20} />
              {isGenerating ? "Generating Customized Quiz..." : `Generate Quiz (${questionCount} Questions, ${formatDuration(calculatedTotalSeconds)})`}
            </button>
          </form>
        )}

        {/* TAB 2: FILE UPLOAD GENERATION */}
        {activeTab === "import" && (
          <div className="bg-white/5 border border-white/10 rounded-2xl p-6 md:p-8 space-y-6">
            {/* Dropzone */}
            <div
              {...getRootProps()}
              className={`w-full p-10 border-2 border-dashed rounded-xl text-center cursor-pointer transition ${
                isDragActive
                  ? "border-orange-400 bg-orange-500/10"
                  : "border-white/20 hover:border-orange-400/50 bg-white/5"
              }`}
            >
              <input {...getInputProps()} />
              <Upload size={40} className="text-orange-400 mx-auto mb-3" />
              <p className="text-white font-mono font-semibold mb-1">
                Drop your document here or click to browse
              </p>
              <p className="text-gray-400 font-mono text-xs">
                Supports PDF, DOCX, DOC, and TXT files (up to 50MB)
              </p>
            </div>

            {/* File Selected Badge */}
            {uploadedFile && (
              <div className="bg-white/5 border border-orange-500/30 rounded-xl p-4 flex justify-between items-center">
                <div className="flex items-center space-x-3">
                  <div className="w-10 h-10 rounded-lg bg-orange-500/10 flex items-center justify-center text-orange-400">
                    <FileText size={20} />
                  </div>
                  <div>
                    <p className="text-white font-mono text-sm font-bold">
                      {uploadedFile.name}
                    </p>
                    <p className="text-green-400 text-xs font-mono">
                      ✓ Ready for quiz generation ({(uploadedFile.size / 1024).toFixed(1)} KB)
                    </p>
                  </div>
                </div>
                <button
                  onClick={() => setUploadedFile(null)}
                  className="p-1.5 rounded-lg hover:bg-white/10 text-gray-400 hover:text-red-400 transition"
                  title="Remove file"
                >
                  <X size={18} />
                </button>
              </div>
            )}

            {/* Customization Settings */}
            {renderCustomizationPanel()}

            {/* Generate Button */}
            <button
              onClick={handleFileGenerate}
              disabled={!uploadedFile || isGenerating}
              className="w-full py-4 bg-gradient-to-r from-orange-500 to-orange-600 hover:from-orange-400 hover:to-orange-500 text-white rounded-xl font-mono font-bold text-base transition shadow-lg shadow-orange-500/25 disabled:opacity-50 flex items-center justify-center gap-2"
            >
              <Sparkles size={20} />
              {isGenerating
                ? "Reading & Generating Quiz..."
                : uploadedFile
                ? `Generate Quiz from "${uploadedFile.name}" (${questionCount} Qs, ${formatDuration(calculatedTotalSeconds)})`
                : "Select a file to generate quiz"}
            </button>
          </div>
        )}

      </div>
    </section>
  );
};

export default QuizGeneration;
