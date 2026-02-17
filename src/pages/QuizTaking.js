import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Clock, CheckCircle, ArrowLeft, ArrowRight } from 'lucide-react';
import toast from 'react-hot-toast';

const QuizTaking = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const handleSubmit = () => {
    toast.success('Quiz submitted successfully!');
    navigate('/dashboard');
  };

  return (
    <div className="max-w-4xl mx-auto">
      <div className="card-custom p-6 mb-6">
        <div className="flex justify-between items-center mb-4">
          <h1 className="text-2xl font-bold">Quiz Title</h1>
          <div className="flex items-center space-x-2 text-gray-600">
            <Clock size={20} />
            <span>25:00</span>
          </div>
        </div>
        <div className="w-full bg-gray-200 rounded-full h-2 mb-4">
          <div className="bg-blue-500 h-2 rounded-full" style={{ width: '33%' }}></div>
        </div>
        <p className="text-gray-600">Question 1 of 10</p>
      </div>

      <div className="card-custom p-6 mb-6">
        <h2 className="text-lg font-semibold mb-4">Sample Question?</h2>
        <div className="space-y-3">
          {['Option A', 'Option B', 'Option C', 'Option D'].map((option, index) => (
            <label key={index} className="flex items-center space-x-3 p-3 border rounded-lg cursor-pointer hover:bg-gray-50">
              <input type="radio" name="answer" className="w-4 h-4" />
              <span>{option}</span>
            </label>
          ))}
        </div>
      </div>

      <div className="flex justify-between">
        <button className="btn-secondary">
          <ArrowLeft size={20} className="mr-2" />
          Previous
        </button>
        <button onClick={handleSubmit} className="btn-primary">
          Submit Quiz
          <CheckCircle size={20} className="ml-2" />
        </button>
        <button className="btn-primary">
          Next
          <ArrowRight size={20} className="ml-2" />
        </button>
      </div>
    </div>
  );
};

export default QuizTaking;
