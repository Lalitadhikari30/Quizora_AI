

// import {
//   FileText,
//   Image,
//   Upload,
//   X
// } from "lucide-react";

// import { useCallback, useState } from "react";
// import { useDropzone } from "react-dropzone";
// import { useForm } from "react-hook-form";
// import toast from "react-hot-toast";
// import { useNavigate } from "react-router-dom";

// import { quizService } from "../services/quizService";
// import { fileService } from "../services/fileService";

// const QuizGeneration = () => {
//   const navigate = useNavigate();
//   const [isGenerating, setIsGenerating] = useState(false);
//   const [uploadedFile, setUploadedFile] = useState(null);
//   const [generatedQuiz, setGeneratedQuiz] = useState(null);
//   const [activeTab, setActiveTab] = useState("create");

//   const { register, handleSubmit } = useForm({
//     defaultValues: {
//       title: "",
//       sourceContent: "",
//       type: "MIXED",
//       questionCount: 10,
//       difficulty: "INTERMEDIATE",
//       topics: "",
//     },
//   });

//   // ================= FILE DROP =================
//   const onDrop = useCallback((acceptedFiles) => {
//     const file = acceptedFiles[0];
//     if (!file) return;

//     setUploadedFile(file);
//     setGeneratedQuiz(null);
//     toast.success("File selected successfully!");
//   }, []);

//   const { getRootProps, getInputProps } = useDropzone({
//     onDrop,
//     multiple: false,
//   });

//   // ================= MANUAL QUIZ =================
//   const onSubmit = async (data) => {
//     setIsGenerating(true);
//     try {
//       const response = await quizService.generateQuiz(data);
//       const quizData = response?.data || response;

//       const quizId =
//         quizData?.id ||
//         quizData?.quiz?.id;

//       if (!quizId) {
//         toast.error("Quiz ID missing");
//         return;
//       }

//       toast.success("Quiz generated successfully!");
//       navigate(`/quiz/${quizId}`);
//     } catch (error) {
//       toast.error("Failed to generate quiz");
//     } finally {
//       setIsGenerating(false);
//     }
//   };

//   // ================= FILE QUIZ =================
//   const handleGenerateQuiz = async () => {
//     if (!uploadedFile) {
//       toast.error("Please upload a file first");
//       return;
//     }

//     setIsGenerating(true);
//     try {
//       const formData = new FormData();
//       formData.append("file", uploadedFile);

//       const response = await fileService.uploadFileForQuiz(formData);
//       const quizData = response?.data || response;

//       setGeneratedQuiz(quizData);
//       toast.success("Quiz generated successfully!");
//     } catch (error) {
//       console.error("Quiz generation error:", error);
//       toast.error("Failed to generate quiz");
//     } finally {
//       setIsGenerating(false);
//     }
//   };

//   // ================= SAFE OPTION PARSER =================
//   const parseOptions = (options) => {
//     if (!options) return [];

//     if (Array.isArray(options)) return options;

//     if (typeof options === "string") {
//       try {
//         return JSON.parse(options);
//       } catch {
//         return [];
//       }
//     }

//     return [];
//   };

//   // ================= UI =================
//   return (
//     <section className="bg-[#0a0a0a] py-12">
//       <div className="container mx-auto px-4 max-w-6xl">
//         <div className="space-y-8 pb-32">

//           {/* Tabs */}
//           <div className="flex justify-center">
//             <div className="bg-white/5 border border-white/10 rounded-xl p-1 inline-flex">
//               <button
//                 onClick={() => setActiveTab("create")}
//                 className={`px-6 py-2 rounded-lg font-mono ${
//                   activeTab === "create"
//                     ? "bg-orange-500 text-white"
//                     : "text-gray-400"
//                 }`}
//               >
//                 Create New
//               </button>

//               <button
//                 onClick={() => setActiveTab("import")}
//                 className={`px-6 py-2 rounded-lg font-mono ${
//                   activeTab === "import"
//                     ? "bg-orange-500 text-white"
//                     : "text-gray-400"
//                 }`}
//               >
//                 Import Quiz
//               </button>
//             </div>
//           </div>

//           {activeTab === "create" ? (
//             <div className="bg-white/5 border border-white/10 rounded-2xl p-8">
//               <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
//                 <input
//                   type="text"
//                   placeholder="Quiz Title"
//                   className="w-full px-4 py-3 bg-white/5 border border-white/10 rounded-lg text-white font-mono"
//                   {...register("title", { required: true })}
//                 />

//                 <textarea
//                   rows={5}
//                   placeholder="Paste content..."
//                   className="w-full px-4 py-3 bg-white/5 border border-white/10 rounded-lg text-white font-mono"
//                   {...register("sourceContent", { required: true })}
//                 />

//                 <button
//                   type="submit"
//                   disabled={isGenerating}
//                   className="w-full py-3 bg-orange-500 text-white rounded-lg font-mono"
//                 >
//                   {isGenerating ? "Generating..." : "Generate Quiz"}
//                 </button>
//               </form>
//             </div>
//           ) : (
//             <div className="bg-white/5 border border-white/10 rounded-2xl p-8">

//               {/* Dropzone */}
//               <div
//                 {...getRootProps()}
//                 className="w-full p-12 border-2 border-dashed border-white/20 rounded-lg text-center cursor-pointer"
//               >
//                 <input {...getInputProps()} />
//                 <Upload size={48} className="text-orange-400 mx-auto mb-4" />
//                 <p className="text-gray-300 font-mono">
//                   Drop your file here or click to browse
//                 </p>
//               </div>

//               {/* Generate Button */}
//               <button
//                 onClick={handleGenerateQuiz}
//                 disabled={!uploadedFile || isGenerating}
//                 className="mt-6 w-full py-3 bg-orange-500 text-white rounded-lg font-mono hover:bg-orange-600 transition disabled:opacity-50"
//               >
//                 {isGenerating ? "Generating..." : "Generate Quiz"}
//               </button>

//               {/* File Info */}
//               {uploadedFile && (
//                 <div className="mt-6 bg-white/5 border border-white/10 rounded-lg p-4 flex justify-between items-center">
//                   <div>
//                     <p className="text-white font-mono text-sm">
//                       {uploadedFile.name}
//                     </p>
//                     <p className="text-green-400 text-xs font-mono">
//                       ✓ File Selected
//                     </p>
//                   </div>
//                   <button
//                     onClick={() => {
//                       setUploadedFile(null);
//                       setGeneratedQuiz(null);
//                     }}
//                     className="text-red-400"
//                   >
//                     <X size={20} />
//                   </button>
//                 </div>
//               )}

//               {/* Generated Quiz */}
//               {generatedQuiz && (
//                 <div className="mt-8 bg-white/5 border border-white/10 rounded-2xl p-8">
//                   <h3 className="text-xl text-white font-mono mb-6">
//                     Generated Quiz
//                   </h3>

//                   <h4 className="text-lg text-orange-400 font-mono mb-4">
//                     {generatedQuiz.title || "Quiz from Uploaded File"}
//                   </h4>

//                   <div className="space-y-6">
//                     {(generatedQuiz.questions || []).map((question, index) => {
//                       const options = parseOptions(question.options);
//                       return (
//                         <div key={index} className="bg-white/5 p-4 rounded-lg">
//                           <p className="text-white font-mono mb-3">
//                             {question.questionText || question.question}
//                           </p>

//                           <div className="space-y-2">
//                             {options.map((option, optIndex) => {
//                               const isCorrect =
//                                 option === question.correctAnswer ||
//                                 optIndex === question.correctAnswer;

//                               return (
//                                 <div
//                                   key={optIndex}
//                                   className={`p-2 rounded border ${
//                                     isCorrect
//                                       ? "border-green-500 bg-green-500/10"
//                                       : "border-white/20 bg-white/5"
//                                   }`}
//                                 >
//                                   <span className="text-gray-300 text-sm font-mono">
//                                     {option}
//                                   </span>
//                                   {isCorrect && (
//                                     <span className="text-green-400 text-xs ml-2">
//                                       ✓ Correct
//                                     </span>
//                                   )}
//                                 </div>
//                               );
//                             })}
//                           </div>
//                         </div>
//                       );
//                     })}
//                   </div>

//                   <div className="mt-6 flex gap-4">
//                     <button
//                       onClick={() => {
//                         const quizId = generatedQuiz.id;
//                         if (quizId) navigate(`/quiz/${quizId}`);
//                       }}
//                       className="flex-1 py-3 bg-green-500 text-white rounded-lg font-mono"
//                     >
//                       Start Quiz
//                     </button>

//                     <button
//                       onClick={() => setGeneratedQuiz(null)}
//                       className="px-6 py-3 bg-gray-600 text-white rounded-lg font-mono"
//                     >
//                       Clear
//                     </button>
//                   </div>
//                 </div>
//               )}

//             </div>
//           )}
//         </div>
//       </div>
//     </section>
//   );
// };

// export default QuizGeneration;




import {
  FileText,
  Upload,
  X
} from "lucide-react";

import { useCallback, useState } from "react";
import { useDropzone } from "react-dropzone";
import { useForm } from "react-hook-form";
import toast from "react-hot-toast";
import { useNavigate } from "react-router-dom";

import { quizService } from "../services/quizService";
import { fileService } from "../services/fileService";

const QuizGeneration = () => {
  const navigate = useNavigate();
  const [isGenerating, setIsGenerating] = useState(false);
  const [uploadedFile, setUploadedFile] = useState(null);
  const [generatedQuiz, setGeneratedQuiz] = useState(null);
  const [activeTab, setActiveTab] = useState("create");

  const { register, handleSubmit } = useForm({
    defaultValues: {
      title: "",
      sourceContent: "",
      type: "MIXED",
      questionCount: 10,
      difficulty: "INTERMEDIATE",
      topics: "",
    },
  });

  // ================= FILE DROP =================
  const onDrop = useCallback((acceptedFiles) => {
    const file = acceptedFiles[0];
    if (!file) return;

    setUploadedFile(file);
    setGeneratedQuiz(null);
    toast.success("File selected successfully!");
  }, []);

  const { getRootProps, getInputProps } = useDropzone({
    onDrop,
    multiple: false,
  });

  // ================= MANUAL QUIZ =================
  const onSubmit = async (data) => {
    setIsGenerating(true);
    try {
      const response = await quizService.generateQuiz(data);
      const quizData = response?.quiz || response;

      if (!quizData?.id) {
        throw new Error("Quiz ID missing");
      }

      toast.success("Quiz generated successfully!");
      navigate(`/quiz/${quizData.id}`);
    } catch (error) {
      toast.error(error.message || "Failed to generate quiz");
    } finally {
      setIsGenerating(false);
    }
  };

  // ================= FILE QUIZ =================
  const handleGenerateQuiz = async () => {
    if (!uploadedFile) {
      toast.error("Please upload a file first");
      return;
    }

    setIsGenerating(true);

    try {
      const formData = new FormData();
      formData.append("file", uploadedFile);

      const response = await fileService.uploadFileForQuiz(formData);

      // 🔥 IMPORTANT FIX
      const quizData = response?.quiz;

      if (!quizData || !quizData.id) {
        throw new Error("Invalid quiz response from server");
      }

      setGeneratedQuiz(quizData);
      toast.success("Quiz generated successfully!");

    } catch (error) {
      console.error("Quiz generation error:", error);
      toast.error(error.message || "Failed to generate quiz");
    } finally {
      setIsGenerating(false);
    }
  };

  // ================= SAFE OPTION PARSER =================
  const parseOptions = (options) => {
    if (!options) return [];
    if (Array.isArray(options)) return options;

    if (typeof options === "string") {
      try {
        return JSON.parse(options);
      } catch {
        return [];
      }
    }

    return [];
  };

  // ================= UI =================
  return (
    <section className="bg-[#0a0a0a] py-12">
      <div className="container mx-auto px-4 max-w-6xl">
        <div className="space-y-8 pb-32">

          {/* Tabs */}
          <div className="flex justify-center">
            <div className="bg-white/5 border border-white/10 rounded-xl p-1 inline-flex">
              <button
                onClick={() => setActiveTab("create")}
                className={`px-6 py-2 rounded-lg font-mono ${
                  activeTab === "create"
                    ? "bg-orange-500 text-white"
                    : "text-gray-400"
                }`}
              >
                Create New
              </button>

              <button
                onClick={() => setActiveTab("import")}
                className={`px-6 py-2 rounded-lg font-mono ${
                  activeTab === "import"
                    ? "bg-orange-500 text-white"
                    : "text-gray-400"
                }`}
              >
                Import Quiz
              </button>
            </div>
          </div>

          {activeTab === "create" ? (
            <div className="bg-white/5 border border-white/10 rounded-2xl p-8">
              <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
                <input
                  type="text"
                  placeholder="Quiz Title"
                  className="w-full px-4 py-3 bg-white/5 border border-white/10 rounded-lg text-white font-mono"
                  {...register("title", { required: true })}
                />

                <textarea
                  rows={5}
                  placeholder="Paste content..."
                  className="w-full px-4 py-3 bg-white/5 border border-white/10 rounded-lg text-white font-mono"
                  {...register("sourceContent", { required: true })}
                />

                <button
                  type="submit"
                  disabled={isGenerating}
                  className="w-full py-3 bg-orange-500 text-white rounded-lg font-mono"
                >
                  {isGenerating ? "Generating..." : "Generate Quiz"}
                </button>
              </form>
            </div>
          ) : (
            <div className="bg-white/5 border border-white/10 rounded-2xl p-8">

              {/* Dropzone */}
              <div
                {...getRootProps()}
                className="w-full p-12 border-2 border-dashed border-white/20 rounded-lg text-center cursor-pointer"
              >
                <input {...getInputProps()} />
                <Upload size={48} className="text-orange-400 mx-auto mb-4" />
                <p className="text-gray-300 font-mono">
                  Drop your file here or click to browse
                </p>
              </div>

              {/* Generate Button */}
              <button
                onClick={handleGenerateQuiz}
                disabled={!uploadedFile || isGenerating}
                className="mt-6 w-full py-3 bg-orange-500 text-white rounded-lg font-mono hover:bg-orange-600 transition disabled:opacity-50"
              >
                {isGenerating ? "Generating..." : "Generate Quiz"}
              </button>

              {/* File Info */}
              {uploadedFile && (
                <div className="mt-6 bg-white/5 border border-white/10 rounded-lg p-4 flex justify-between items-center">
                  <div>
                    <p className="text-white font-mono text-sm">
                      {uploadedFile.name}
                    </p>
                    <p className="text-green-400 text-xs font-mono">
                      ✓ File Selected
                    </p>
                  </div>
                  <button
                    onClick={() => {
                      setUploadedFile(null);
                      setGeneratedQuiz(null);
                    }}
                    className="text-red-400"
                  >
                    <X size={20} />
                  </button>
                </div>
              )}

              {/* Generated Quiz */}
              {generatedQuiz && (
                <div className="mt-8 bg-white/5 border border-white/10 rounded-2xl p-8">
                  <h3 className="text-xl text-white font-mono mb-6">
                    Generated Quiz
                  </h3>

                  <h4 className="text-lg text-orange-400 font-mono mb-4">
                    {generatedQuiz.title || "Quiz from Uploaded File"}
                  </h4>

                  <div className="space-y-6">
                    {(generatedQuiz.questions || []).map((question, index) => {
                      const options = parseOptions(question.options);
                      return (
                        <div key={index} className="bg-white/5 p-4 rounded-lg">
                          <p className="text-white font-mono mb-3">
                            {question.questionText || question.question}
                          </p>

                          <div className="space-y-2">
                            {options.map((option, optIndex) => {
                              const isCorrect =
                                option === question.correctAnswer ||
                                optIndex === question.correctAnswer;

                              return (
                                <div
                                  key={optIndex}
                                  className={`p-2 rounded border ${
                                    isCorrect
                                      ? "border-green-500 bg-green-500/10"
                                      : "border-white/20 bg-white/5"
                                  }`}
                                >
                                  <span className="text-gray-300 text-sm font-mono">
                                    {option}
                                  </span>
                                  {isCorrect && (
                                    <span className="text-green-400 text-xs ml-2">
                                      ✓ Correct
                                    </span>
                                  )}
                                </div>
                              );
                            })}
                          </div>
                        </div>
                      );
                    })}
                  </div>

                  <div className="mt-6 flex gap-4">
                    <button
                      onClick={() => navigate(`/quiz/${generatedQuiz.id}`)}
                      className="flex-1 py-3 bg-green-500 text-white rounded-lg font-mono"
                    >
                      Start Quiz
                    </button>

                    <button
                      onClick={() => setGeneratedQuiz(null)}
                      className="px-6 py-3 bg-gray-600 text-white rounded-lg font-mono"
                    >
                      Clear
                    </button>
                  </div>
                </div>
              )}

            </div>
          )}
        </div>
      </div>
    </section>
  );
};

export default QuizGeneration;

