import { Route, Routes } from 'react-router-dom';
import ProtectedRoute from './components/auth/ProtectedRoute';
import Footer from './components/layout/Footer';
import Navbar from './components/layout/Navbar';
import Dashboard from './pages/Dashboard';
import Home from './pages/Home';
import Interview from './pages/Interview';
import Login from './pages/Login';
import Performance from './pages/Performance';
import QuizGeneration from './pages/QuizGeneration';
import QuizTaking from './pages/QuizTaking';
import Register from './pages/Register';

function App() {
  return (
    <div className="flex flex-col min-h-screen bg-[#0a0a0a]">

      <Navbar />

      <main className="flex-1 w-full overflow-x-hidden">
        <Routes>

          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />

          <Route path="/dashboard"
            element={<ProtectedRoute><Dashboard /></ProtectedRoute>}
          />

          <Route path="/quiz/generate"
            element={<ProtectedRoute><QuizGeneration /></ProtectedRoute>}
          />

          <Route path="/quiz/:id"
            element={<QuizTaking />}
          />


          <Route path="/interview"
            element={<ProtectedRoute><Interview /></ProtectedRoute>}
          />

          <Route path="/performance"
            element={<ProtectedRoute><Performance /></ProtectedRoute>}
          />

        </Routes>
      </main>

      <Footer />

    </div>
  );
}

export default App;
