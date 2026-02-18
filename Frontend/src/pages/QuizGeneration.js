import { BookOpen, Brain, Code, Database, File, FileText, Globe, Image, Link, Upload } from 'lucide-react';

import { useCallback, useState } from 'react';

import { useDropzone } from 'react-dropzone';

import { useForm } from 'react-hook-form';

import toast from 'react-hot-toast';

import { useNavigate } from 'react-router-dom';

import { quizService } from '../services/quizService';

import { fileService } from '../services/fileService';



const QuizGeneration = () => {

  const navigate = useNavigate();

  const [isGenerating, setIsGenerating] = useState(false);

  const [uploadedFile, setUploadedFile] = useState(null);

  const [importLink, setImportLink] = useState('');

  const [activeTab, setActiveTab] = useState('create');



  const {

    register,

    handleSubmit,

    watch,

    setValue,

    formState: { errors },

  } = useForm({

    defaultValues: {

      title: '',

      description: '',

      sourceType: 'TEXT',

      sourceContent: '',

      type: 'MIXED',

      questionCount: 10,

      difficulty: 'INTERMEDIATE',

      topics: '',

    },

  });



  const sourceType = watch('sourceType');



  const onDrop = useCallback(async (acceptedFiles) => {

    const file = acceptedFiles[0];

    if (file) {

      try {

        fileService.validateFile(file);

        

        // Actually upload the file to backend

        setIsGenerating(true);

        const uploadResponse = await fileService.uploadFile(file);

        

        setUploadedFile(file);

        toast.success(`File uploaded successfully! (${uploadResponse.fileName})`);

        

        // Store the upload response for later use

        setUploadedFile(prev => ({ ...prev, uploadResponse }));

      } catch (error) {

        toast.error(error.message || 'Failed to upload file');

      } finally {

        setIsGenerating(false);

      }

    }

  }, []);



  const { getRootProps, getInputProps } = useDropzone({

    onDrop,

    multiple: false,

  });



  const onSubmit = async (data) => {

    setIsGenerating(true);

    try {

      let quizData = { ...data };



      // Handle file upload if in import tab

      if (activeTab === 'import' && uploadedFile) {

        const uploadResponse = await fileService.uploadFileForQuiz(uploadedFile, data);

        quizData.sourceContent = uploadResponse.publicUrl;

        quizData.sourceType = 'FILE';

      } else if (activeTab === 'import' && importLink) {

        quizData.sourceContent = importLink;

        quizData.sourceType = 'URL';

      }



      const response = await quizService.generateQuiz(quizData);

      

      toast.success('Quiz generated successfully!');

      navigate(`/quiz/${response.id}`);

    } catch (error) {

      toast.error(error.message || 'Failed to generate quiz.');

    } finally {

      setIsGenerating(false);

    }

  };



  const importOptions = [

    { icon: FileText, title: 'PDF Document', supported: '.pdf' },

    { icon: File, title: 'Word Document', supported: '.doc, .docx' },

    { icon: Code, title: 'Excel/CSV', supported: '.xlsx, .xls, .csv' },

    { icon: Database, title: 'JSON File', supported: '.json' },

    { icon: Link, title: 'URL/Website', supported: 'https://' },

    { icon: Globe, title: 'YouTube Video', supported: 'youtube.com' },

    { icon: BookOpen, title: 'Text File', supported: '.txt' },

    { icon: Image, title: 'Image OCR', supported: '.jpg, .png' }

  ];



  return (

    <section className="bg-[#0a0a0a] py-12" data-scroll-section>

      <div className="container mx-auto px-4 sm:px-6 lg:px-8 max-w-6xl">

        <div className="space-y-8 pb-32">



          {/* Tabs */}

          <div className="flex justify-center">

            <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-xl p-1 inline-flex">

              <button

                onClick={() => setActiveTab('create')}

                className={`px-6 py-2 rounded-lg font-medium transition-all font-mono ${

                  activeTab === 'create' 

                    ? 'bg-orange-500 text-white' 

                    : 'text-gray-400 hover:text-white'

                }`}

              >

                Create New

              </button>

              <button

                onClick={() => setActiveTab('import')}

                className={`px-6 py-2 rounded-lg font-medium transition-all font-mono ${

                  activeTab === 'import' 

                    ? 'bg-orange-500 text-white' 

                    : 'text-gray-400 hover:text-white'

                }`}

              >

                Import Quiz

              </button>

            </div>

          </div>



          {activeTab === 'create' ? (



            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">



              {/* LEFT */}

              <div className="lg:col-span-2 space-y-6">



                <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-8">

                  <h2 className="text-2xl font-bold text-white mb-6 font-mono text-center">

                    Quiz Details

                  </h2>



                  <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">



                    <input

                      type="text"

                      placeholder="Quiz Title"

                      className="w-full px-4 py-3 bg-white/5 border border-white/10 rounded-lg text-white font-mono"

                      {...register('title', { required: true })}

                    />



                    <textarea

                      rows={3}

                      placeholder="Description"

                      className="w-full px-4 py-3 bg-white/5 border border-white/10 rounded-lg text-white font-mono"

                      {...register('description')}

                    />



                    <textarea

                      rows={6}

                      placeholder="Paste content..."

                      className="w-full px-4 py-3 bg-white/5 border border-white/10 rounded-lg text-white font-mono"

                      {...register('sourceContent', { required: true })}

                    />



                    <button

                      type="submit"

                      disabled={isGenerating}

                      className="w-full py-3 bg-orange-500 text-white rounded-lg font-mono"

                    >

                      {isGenerating ? 'Generating...' : 'Generate Quiz'}

                    </button>



                  </form>

                </div>



              </div>



              {/* RIGHT */}

              <div className="space-y-6">

                <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-6">

                  <div className="flex items-center space-x-3 mb-4">

                    <div className="w-12 h-12 bg-gradient-to-br from-orange-500 to-red-500 rounded-xl flex items-center justify-center">

                      <Brain size={24} className="text-white" />

                    </div>

                    <div>

                      <h3 className="text-lg font-semibold text-white font-mono">AI Assistant</h3>

                      <p className="text-gray-400 text-sm font-mono">

                        Your intelligent quiz creation partner

                      </p>

                    </div>

                  </div>

                </div>

              </div>



            </div>



          ) : (



            <div className="max-w-4xl mx-auto">

              <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-8">



                <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">

                  {importOptions.map((option, index) => (

                    <div key={index}

                      className="bg-white/5 border border-white/10 rounded-lg p-4"

                    >

                      <option.icon className="text-orange-400 mb-2" size={24} />

                      <h4 className="text-white font-medium font-mono text-sm">

                        {option.title}

                      </h4>

                      <p className="text-gray-400 text-xs font-mono mt-1">

                        {option.supported}

                      </p>

                    </div>

                  ))}

                </div>



                {/* LINK INPUT */}

                <input

                  type="text"

                  placeholder="Paste YouTube / Website Link here..."

                  value={importLink}

                  onChange={(e) => setImportLink(e.target.value)}

                  className="w-full mb-6 px-4 py-3 bg-white/5 border border-white/10 rounded-lg text-white placeholder-gray-500 focus:border-orange-400 focus:outline-none focus:ring-2 focus:ring-orange-400/20 font-mono"

                />



                <div

                  {...getRootProps()}

                  className="w-full p-12 border-2 border-dashed border-white/20 rounded-lg text-center"

                >

                  <input {...getInputProps()} />

                  <Upload size={48} className="text-orange-400 mx-auto mb-4" />

                  <p className="text-gray-300 text-lg mb-2 font-mono">

                    Drop your file here or click to browse

                  </p>

                </div>



                {/* CREATE QUIZ BUTTON */}

                <button

                  onClick={handleSubmit(onSubmit)}

                  disabled={isGenerating || (!uploadedFile && !importLink)}

                  className="mt-6 w-full py-3 px-4 bg-gradient-to-r from-orange-500 to-orange-600 text-white font-semibold rounded-lg hover:from-orange-400 hover:to-orange-500 transition-all duration-200 border border-orange-500/20 flex items-center justify-center font-mono disabled:opacity-50 disabled:cursor-not-allowed"

                >

                  {isGenerating ? 'Generating Quiz...' : 'Create Quiz'}

                </button>



              </div>

            </div>



          )}



        </div>

      </div>

    </section>

  );

};



export default QuizGeneration;

