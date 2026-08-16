# Quizora AI - Complete Integration

A fully integrated AI-powered quiz generation and interview practice platform built with React (Frontend) and Spring Boot (Backend).

## 🚀 Features

### Frontend (React)
- **Authentication**: Login/Register with Supabase JWT
- **Dashboard**: Real-time analytics and activity tracking
- **Quiz Generation**: AI-powered quiz creation from text, files, or URLs
- **Quiz Taking**: Interactive quiz interface with timer and progress tracking
- **Interview Practice**: AI-driven interview simulation with real-time feedback
- **Performance Analytics**: Comprehensive charts and progress tracking
- **File Upload**: Support for PDF, DOC, TXT files for quiz generation

### Backend (Spring Boot)
- **Authentication**: Supabase JWT integration
- **Quiz Management**: Generate, retrieve, and submit quizzes
- **Interview System**: AI-powered interview questions and answer analysis
- **Performance Tracking**: User analytics and progress monitoring
- **File Processing**: Multi-format file upload and content extraction
- **AI Integration**: Gemini AI for quiz generation and interview analysis

## 📋 Prerequisites

- Node.js 16+ 
- Java 17+
- Maven 3.6+
- PostgreSQL (via Supabase)
- Gemini AI API Key

## 🛠️ Setup Instructions

### 1. Backend Setup

```bash
# Navigate to backend directory
cd Backend

# Copy environment file
cp .env.example .env

# Update .env with your configuration:
# - Supabase URL and keys
# - Gemini AI API key
# - Database credentials

# Install dependencies and run
mvn clean install
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### 2. Frontend Setup

```bash
# Navigate to frontend directory
cd Frontend

# Install dependencies
npm install

# Create environment file
cp .env.example .env.local

# Update .env.local with your backend URL:
# REACT_APP_API_URL=http://localhost:8080

# Start development server
npm start
```

The frontend will start on `http://localhost:3000`

### 3. Environment Configuration

#### Backend (.env)
```env
# Supabase Configuration
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_SERVICE_KEY=your-service-role-key
SUPABASE_JWK_URL=https://your-project.supabase.co/auth/v1/jwks

# Database Configuration
SUPABASE_DB_URL=postgresql://postgres:password@db.your-project.supabase.co:5432/postgres
SUPABASE_DB_USERNAME=postgres.your-project
SUPABASE_DB_PASSWORD=your-password

# AI Configuration
AI_API_KEY=your-gemini-api-key
AI_API_URL=https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent

# Storage Configuration
SUPABASE_STORAGE_BUCKET=quizora-files
```

#### Frontend (.env.local)
```env
REACT_APP_API_URL=http://localhost:8080
REACT_APP_SUPABASE_URL=https://your-project.supabase.co
REACT_APP_SUPABASE_ANON_KEY=your-anon-key
```

## 🔗 API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login
- `GET /api/auth/me` - Get current user
- `POST /api/auth/logout` - User logout

### Quiz Management
- `POST /api/quizzes/generate` - Generate quiz from content
- `GET /api/quizzes/{id}` - Get quiz by ID
- `POST /api/quizzes/{id}/submit` - Submit quiz answers
- `GET /api/quizzes` - Get user quizzes

### Interview System
- `POST /api/interview/start` - Start interview session
- `POST /api/interview/{sessionId}/answer` - Submit answer
- `GET /api/interview/{sessionId}/report` - Get interview report
- `GET /api/interview` - Get user interviews

### File Upload
- `POST /api/upload/pdf` - Upload PDF file
- `POST /api/upload/file` - Upload general file

### Performance Analytics
- `GET /api/performance` - Get user performance
- `GET /api/performance/history` - Get performance history
- `GET /api/performance/interviews` - Get interview performance

## 🧪 Testing

### Backend Tests
```bash
cd Backend
mvn test
```

### Frontend Tests
```bash
cd Frontend
npm test
```

## 📁 Project Structure

```
QuizoraAI/
├── Frontend/
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── services/
│   │   └── hooks/
│   ├── package.json
│   └── .env.local
├── Backend/
│   ├── src/main/java/com/quizora/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   └── dto/
│   ├── pom.xml
│   └── .env
└── README.md
```

## 🔧 Architecture

### Frontend Architecture
- **Service Layer**: Centralized API calls with Axios
- **Component Layer**: Reusable UI components
- **State Management**: React hooks for local state
- **Authentication**: JWT token management
- **Error Handling**: Global error interceptors

### Backend Architecture
- **Controller Layer**: REST API endpoints
- **Service Layer**: Business logic implementation
- **Repository Layer**: Data access with JPA
- **Security**: Supabase JWT validation
- **AI Integration**: Gemini AI service wrapper

## 🚀 Deployment

### Backend Deployment
1. Build JAR file: `mvn clean package`
2. Deploy to your preferred cloud platform
3. Configure environment variables
4. Set up PostgreSQL database

### Frontend Deployment
1. Build production bundle: `npm run build`
2. Deploy to static hosting (Vercel, Netlify, etc.)
3. Configure environment variables
4. Update CORS settings in backend

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 🐛 Troubleshooting

### Common Issues

1. **CORS Errors**: Ensure frontend URL is in backend CORS configuration
2. **Authentication Failures**: Check Supabase configuration and JWT setup
3. **AI API Errors**: Verify Gemini API key is valid and has quota
4. **Database Connection**: Ensure Supabase credentials are correct

### Debug Mode

Enable debug logging by setting:
```env
LOG_LEVEL=DEBUG
```

## 📞 Support

For support and questions, please open an issue in the repository.
