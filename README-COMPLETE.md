# 🧠 Quizora AI - Complete Quiz & Interview Platform

A comprehensive AI-powered quiz generation and interview practice platform built with React (Frontend) and Spring Boot (Backend).

---

## 🎯 Overview

Quizora AI leverages Google's Gemini AI to automatically generate quizzes from various content sources (text, PDFs, documents) and provides an intelligent interview practice system with real-time feedback and performance analytics.

---

## 🚀 Core Features

### 📱 Frontend (React + TailwindCSS)
- **🔐 Authentication System**
  - JWT-based login/register with Supabase
  - Persistent session management
  - User profile management

- **📊 Dashboard & Analytics**
  - Real-time quiz performance tracking
  - Interview history and progress charts
  - Skill assessment and recommendations
  - Activity monitoring and statistics

- **🎯 Quiz Generation**
  - **Text Input**: Direct text-to-quiz conversion
  - **File Upload**: PDF, DOCX, TXT support with OCR
  - **URL Import**: Web content extraction
  - **Custom Settings**: Difficulty, topics, question count
  - **Real-time Preview**: Instant quiz preview before generation

- **📝 Quiz Taking Interface**
  - Interactive question display with multiple choice
  - Real-time timer and progress tracking
  - Answer validation and immediate feedback
  - Question navigation and review

- **🎤 Interview Practice**
  - AI-powered interview simulation
  - Real-time answer analysis
  - Follow-up question generation
  - Performance scoring and feedback

- **📈 Performance Analytics**
  - Comprehensive performance charts
  - Progress tracking over time
  - Skill gap analysis
  - Export capabilities (PDF, CSV)

### ⚙️ Backend (Spring Boot + Supabase)
- **🔐 Authentication & Security**
  - Supabase JWT integration
  - Role-based access control
  - API rate limiting
  - CORS configuration

- **🧠 AI Integration**
  - Google Gemini AI API integration
  - Content extraction from multiple formats
  - Intelligent question generation
  - Answer validation and explanation

- **📁 File Processing**
  - Multi-format upload support (PDF, DOCX, TXT, Images)
  - OCR integration for image processing
  - Content extraction and parsing
  - Supabase storage integration

- **💾 Data Management**
  - PostgreSQL via Supabase
  - JPA/Hibernate ORM
  - Entity relationships and validation
  - Audit logging and tracking

- **📊 Analytics Engine**
  - Real-time performance calculation
  - Statistical analysis
  - Progress tracking algorithms
  - Report generation

---

## 🏗️ System Architecture

### 📐 High-Level Architecture

```mermaid
graph TB
    subgraph "Frontend Layer"
        A[React App] --> B[Authentication Service]
        A --> C[Quiz Service]
        A --> D[Interview Service]
        A --> E[Analytics Service]
        A --> F[File Upload Service]
    end
    
    subgraph "API Gateway"
        G[Spring Boot REST API]
        H[JWT Interceptor]
        I[CORS Configuration]
    end
    
    subgraph "Business Logic"
        J[Quiz Generation Service]
        K[Interview Engine]
        L[Performance Calculator]
        M[File Processing Service]
    end
    
    subgraph "AI & Data Layer"
        N[Gemini AI API]
        O[Supabase Storage]
        P[PostgreSQL Database]
        Q[Content Extraction]
    end
    
    B --> G
    C --> G
    D --> G
    E --> G
    F --> G
    
    G --> H
    G --> I
    
    H --> J
    H --> K
    H --> L
    H --> M
    
    J --> N
    K --> N
    M --> Q
    
    J --> P
    K --> P
    L --> P
    M --> P
    
    P --> O
    P --> Q
```

### 🔄 Data Flow Architecture

```mermaid
sequenceDiagram
    participant U as User
    participant F as Frontend
    participant A as API Gateway
    participant S as Services
    participant AI as Gemini AI
    participant DB as Database
    participant ST as Supabase Storage

    U->>F: Upload File/Text
    F->>A: POST /api/upload/file
    A->>S: Store File
    S->>A: File Metadata
    A->>F: Upload Success
    
    U->>F: Generate Quiz
    F->>A: POST /api/quizzes/generate
    A->>S: Extract Content
    S->>A: Extracted Text
    A->>AI: Generate Questions
    AI->>A: Generated Quiz
    A->>DB: Save Quiz
    DB->>A: Quiz ID
    A->>F: Quiz Data
    
    F->>U: Display Quiz Preview
    U->>F: Start Quiz
    F->>A: GET /api/quizzes/{id}
    A->>DB: Fetch Quiz
    DB->>A: Quiz Questions
    A->>F: Quiz Data
    
    U->>F: Submit Answers
    F->>A: POST /api/quizzes/{id}/submit
    A->>DB: Save Results
    DB->>A: Performance Data
    A->>F: Results
    F->>U: Performance Analytics
```

---

## 🛠️ Technical Stack

### Frontend Technologies
- **React 18+** - Component-based UI framework
- **TypeScript** - Type-safe development
- **TailwindCSS** - Utility-first CSS framework
- **React Router** - Client-side routing
- **Axios** - HTTP client with interceptors
- **React Hook Form** - Form management
- **React Hot Toast** - Notification system
- **Lucide React** - Icon library
- **React Dropzone** - File upload component

### Backend Technologies
- **Spring Boot 3.2+** - Java application framework
- **Java 17+** - Programming language
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database ORM
- **PostgreSQL** - Primary database
- **Supabase** - Backend-as-a-Service
- **Gemini AI API** - AI question generation
- **Maven** - Dependency management
- **JWT** - Token-based authentication

### Infrastructure & Services
- **Supabase** - Database, auth, and storage
- **Google Gemini AI** - AI question generation
- **RESTful APIs** - Standard API design
- **CORS** - Cross-origin resource sharing

---

## 📋 Detailed Feature Implementation

### 🎯 Quiz Generation Flow

```mermaid
flowchart TD
    A[User Selects Input Method] --> B{Input Type}
    
    B -->|Text| C[Text Input Form]
    B -->|File| D[File Upload]
    B -->|URL| E[URL Import]
    
    C --> F[Validation & Processing]
    D --> G[File Upload & Storage]
    E --> H[Content Extraction]
    
    F --> I[AI Quiz Generation]
    G --> I
    H --> I
    
    I --> J[Gemini AI API]
    J --> K[Question Generation]
    K --> L[Answer Options]
    L --> M[Explanations]
    
    M --> N[Quiz Assembly]
    N --> O[Database Storage]
    O --> P[Quiz ID Generation]
    P --> Q[Preview Display]
    
    Q --> R{User Action}
    R -->|Edit| S[Modify Quiz]
    R -->|Accept| T[Start Quiz]
    
    S --> F
    T --> U[Quiz Taking Interface]
```

### 🎤 Interview System Flow

```mermaid
flowchart TD
    A[Start Interview] --> B[Select Topic/Role]
    B --> C[Initial Question Generation]
    C --> D[Display Question]
    
    D --> E[User Answers]
    E --> F[AI Answer Analysis]
    F --> G{Answer Quality}
    
    G -->|Excellent| H[Next Topic Question]
    G -->|Good| I[Follow-up Question]
    G -->|Needs Improvement| J[Clarification Question]
    
    H --> K[Update Performance Score]
    I --> K
    J --> K
    
    K --> L{More Questions?}
    L -->|Yes| M[Generate Next Question]
    L -->|No| N[Generate Report]
    
    M --> D
    N --> O[Performance Analysis]
    O --> P[Detailed Report]
    P --> Q[Recommendations]
```

### 📊 Analytics & Performance Flow

```mermaid
flowchart TD
    A[Quiz/Interview Completion] --> B[Answer Collection]
    B --> C[Score Calculation]
    C --> D[Performance Metrics]
    
    D --> E[Time Analysis]
    D --> F[Accuracy Rate]
    D --> G[Difficulty Assessment]
    D --> H[Topic Mastery]
    
    E --> I[Time Performance Chart]
    F --> J[Accuracy Chart]
    G --> K[Progress Chart]
    H --> L[Skill Radar Chart]
    
    I --> M[Dashboard Display]
    J --> M
    K --> M
    L --> M
    
    M --> N[Trend Analysis]
    N --> O[Improvement Suggestions]
    O --> P[Personalized Recommendations]
```

---

## 🔧 Setup & Configuration

### 📋 Prerequisites
- **Node.js 16+** and **npm 8+**
- **Java 17+** and **Maven 3.6+**
- **PostgreSQL** (via Supabase)
- **Google Gemini AI API Key**

### 🚀 Quick Start

#### 1. Backend Setup
```bash
# Clone repository
git clone https://github.com/your-username/quizora-ai.git
cd quizora-ai/Backend

# Configure environment
cp .env.example .env
# Edit .env with your credentials:
# - SUPABASE_URL=your-project.supabase.co
# - SUPABASE_SERVICE_KEY=your-service-role-key
# - AI_API_KEY=your-gemini-api-key

# Install and run
mvn clean install
mvn spring-boot:run
```

#### 2. Frontend Setup
```bash
# Navigate to frontend
cd ../Frontend

# Configure environment
cp .env.example .env.local
# Edit .env.local:
# - REACT_APP_API_URL=http://localhost:8080
# - REACT_APP_SUPABASE_URL=your-project.supabase.co

# Install and run
npm install
npm start
```

### ⚙️ Environment Variables

#### Backend Configuration
```env
# Database Configuration
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_SERVICE_KEY=your-service-role-key
SUPABASE_JWK_URL=https://your-project.supabase.co/auth/v1/jwks
SUPABASE_DB_URL=postgresql://user:pass@db.supabase.co:5432/postgres

# AI Configuration
AI_API_KEY=AIzaSyB3dy9OHtdwkhyp0RD4a3d42Xg7-9y2EJY
AI_API_URL=https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent

# Storage Configuration
SUPABASE_STORAGE_BUCKET=quizora-files

# Server Configuration
SERVER_PORT=8081
CORS_ALLOWED_ORIGINS=http://localhost:3000,https://your-domain.com
```

#### Frontend Configuration
```env
REACT_APP_API_URL=http://localhost:8080
REACT_APP_SUPABASE_URL=https://your-project.supabase.co
REACT_APP_SUPABASE_ANON_KEY=your-anon-key
```

---

## 📡 API Documentation

### 🔐 Authentication Endpoints
```
POST /api/auth/register
{
  "username": "string",
  "email": "string", 
  "password": "string"
}

POST /api/auth/login
{
  "email": "string",
  "password": "string"
}

GET /api/auth/me
Headers: Authorization: Bearer <token>

POST /api/auth/logout
Headers: Authorization: Bearer <token>
```

### 🎯 Quiz Management Endpoints
```
POST /api/quizzes/generate
{
  "title": "string",
  "sourceContent": "string",
  "difficulty": "BEGINNER|INTERMEDIATE|ADVANCED",
  "topics": "string",
  "questionCount": "number"
}

GET /api/quizzes/{id}
Headers: Authorization: Bearer <token>

POST /api/quizzes/{id}/submit
{
  "answers": [
    {"questionId": "number", "selectedAnswer": "number"}
  ]
}

GET /api/quizzes
Headers: Authorization: Bearer <token>
```

### 📁 File Upload Endpoints
```
POST /api/upload/file
Content-Type: multipart/form-data
Body: file=<binary>

POST /api/upload/pdf
Content-Type: multipart/form-data  
Body: file=<binary>
```

### 🎤 Interview Endpoints
```
POST /api/interview/start
{
  "topic": "string",
  "difficulty": "string",
  "role": "string"
}

POST /api/interview/{sessionId}/answer
{
  "questionId": "string",
  "answer": "string"
}

GET /api/interview/{sessionId}/report
Headers: Authorization: Bearer <token>
```

### 📊 Analytics Endpoints
```
GET /api/performance
Headers: Authorization: Bearer <token>

GET /api/performance/history
Headers: Authorization: Bearer <token>

GET /api/performance/interviews  
Headers: Authorization: Bearer <token>
```

---

## 🧪 Testing Strategy

### 🧪 Backend Testing
```bash
# Unit tests
mvn test

# Integration tests  
mvn test -Dspring.profiles.active=test

# Test coverage
mvn jacoco:report
```

### 🧪 Frontend Testing
```bash
# Unit tests
npm test

# Integration tests
npm run test:integration

# E2E tests
npm run test:e2e

# Test coverage
npm run test:coverage
```

### 🔍 API Testing
```bash
# Health check
curl http://localhost:8081/api/health

# Authentication test
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password"}'

# Quiz generation test
curl -X POST http://localhost:8081/api/quizzes/generate \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"title":"Test Quiz","sourceContent":"Test content","questionCount":5}'
```

---

## 🚀 Deployment Guide

### 🐳 Docker Deployment
```dockerfile
# Backend Dockerfile
FROM openjdk:17-jdk-slim
COPY target/quizora-*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app.jar"]

# Frontend Dockerfile  
FROM node:18-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production
COPY . .
RUN npm run build
EXPOSE 3000
CMD ["npm","start"]
```

```yaml
# docker-compose.yml
version: '3.8'
services:
  backend:
    build: ./Backend
    ports:
      - "8081:8081"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
  frontend:
    build: ./Frontend  
    ports:
      - "3000:3000"
    environment:
      - REACT_APP_API_URL=http://localhost:8081
```

### ☁️ Cloud Deployment

#### Backend Options
- **AWS EC2** + RDS PostgreSQL
- **Google Cloud Run** + Cloud SQL
- **Azure App Service** + Azure Database
- **Heroku** + Heroku Postgres

#### Frontend Options
- **Vercel** (Recommended for React)
- **Netlify** (Static hosting)
- **AWS S3** + CloudFront
- **GitHub Pages** (Static)

---

## 🔧 Development Workflow

### 🌟 Feature Development Process
```mermaid
gitgraph LR
    A[Feature Branch] --> B[Development]
    B --> C[Unit Tests]
    C --> D[Integration Tests]
    D --> E[Code Review]
    E --> F[Merge to Main]
    F --> G[Staging Deploy]
    G --> H[QA Testing]
    H --> I[Production Deploy]
```

### 🔄 CI/CD Pipeline
```yaml
# .github/workflows/ci-cd.yml
name: Quizora CI/CD
on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  test-backend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
      - uses: actions/setup-maven@v3
      - run: mvn test
      
  test-frontend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: '18'
      - run: npm ci
      - run: npm test
      
  deploy:
    needs: [test-backend, test-frontend]
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    steps:
      - name: Deploy to production
        run: |
          # Deployment commands
```

---

## 📈 Performance & Monitoring

### 📊 Key Metrics
- **Response Time**: API endpoint performance
- **Quiz Generation Speed**: AI processing time
- **User Engagement**: Session duration and completion rates
- **Error Rates**: System reliability metrics
- **Resource Usage**: Memory and CPU utilization

### 🔍 Monitoring Setup
```yaml
# application-monitoring.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  metrics:
    export:
      prometheus:
        enabled: true
logging:
  level:
    com.quizora: DEBUG
```

---

## 🛡️ Security & Best Practices

### 🔐 Security Measures
- **JWT Authentication**: Secure token-based auth
- **Input Validation**: Comprehensive input sanitization
- **Rate Limiting**: API abuse prevention
- **CORS Configuration**: Secure cross-origin requests
- **Environment Variables**: Sensitive data protection
- **SQL Injection Prevention**: Parameterized queries
- **HTTPS Enforcement**: Secure data transmission

### 📋 Security Checklist
- [ ] Environment variables properly configured
- [ ] Database credentials secured
- [ ] API keys have restricted access
- [ ] CORS properly configured
- [ ] Input validation implemented
- [ ] Error handling doesn't expose sensitive info
- [ ] Logging doesn't include sensitive data
- [ ] Dependencies regularly updated
- [ ] Security headers configured

---

## 🐛 Troubleshooting Guide

### 🔧 Common Issues & Solutions

#### Authentication Issues
**Problem**: Login failures or token errors
**Solution**: 
1. Verify Supabase configuration
2. Check JWT token format
3. Ensure CORS settings include frontend URL
4. Clear browser cache and localStorage

#### AI Integration Issues  
**Problem**: Quiz generation failures
**Solution**:
1. Verify Gemini API key validity
2. Check API quota and rate limits
3. Review content extraction logs
4. Test with simple text input first

#### File Upload Issues
**Problem**: Upload failures or processing errors
**Solution**:
1. Check file size limits (50MB max)
2. Verify supported formats (PDF, DOCX, TXT)
3. Ensure Supabase bucket permissions
4. Review network connectivity

#### Database Issues
**Problem**: Connection failures or data errors
**Solution**:
1. Verify Supabase connection string
2. Check database permissions
3. Review entity relationships
4. Validate data types and constraints

### 🔍 Debug Mode
Enable comprehensive logging:
```env
LOG_LEVEL=DEBUG
QUIZORA_DEBUG=true
AI_LOGGING=true
```

---

## 🤝 Contributing Guidelines

### 📋 Development Standards
- **Code Style**: Follow ESLint and Prettier configurations
- **Commit Messages**: Use conventional commit format
- **Branch Naming**: feature/description, bugfix/description
- **Testing**: Minimum 80% test coverage required
- **Documentation**: Update README for new features

### 🔄 Pull Request Process
1. **Fork** the repository
2. **Create** feature branch from develop
3. **Develop** with comprehensive testing
4. **Submit** PR with detailed description
5. **Review** process requires at least 1 approval
6. **Merge** after successful review

### 📝 Code Quality Standards
- **TypeScript**: Strict mode enabled
- **React Hooks**: Follow rules of hooks
- **Error Boundaries**: Implement for error handling
- **Performance**: Optimize for bundle size
- **Accessibility**: WCAG 2.1 AA compliance

---

## 📄 License & Support

### 📜 License
This project is licensed under the **MIT License**. See [LICENSE](LICENSE) file for details.

### 📞 Support & Contact
- **GitHub Issues**: [Create new issue](https://github.com/your-username/quizora-ai/issues)
- **Discord Community**: [Join our server](https://discord.gg/quizora)
- **Email Support**: support@quizora.ai
- **Documentation**: [Full docs](https://docs.quizora.ai)

---

## 🎯 Roadmap & Future Features

### 🚀 Upcoming Features
- **🌐 Multi-language Support**: Quiz generation in multiple languages
- **🎥 Video Interview**: AI-powered video interview practice
- **👥 Team Collaboration**: Group quiz sessions
- **🔊 Audio Support**: Voice-based interview practice
- **📱 Mobile Apps**: Native iOS and Android applications
- **🧠 Advanced AI**: GPT-4 and Claude integration options

### 📈 Platform Improvements
- **🔍 Advanced Analytics**: Machine learning insights
- **🎯 Personalized Learning**: Adaptive difficulty adjustment
- **🏆 Gamification**: Points, badges, and leaderboards
- **🔄 Real-time Collaboration**: Live quiz sessions
- **📊 Advanced Reporting**: Detailed performance analytics

---

## 🎉 Conclusion

Quizora AI represents a comprehensive solution for automated quiz generation and intelligent interview practice. By leveraging cutting-edge AI technology and modern web development practices, it provides users with a powerful platform for skill assessment and improvement.

The platform's modular architecture ensures scalability, maintainability, and extensibility, while the comprehensive testing strategy guarantees reliability and performance.

**Ready to transform how people learn and practice? Start with Quizora AI today!** 🚀
