# Quizora AI 🚀

A modern, AI-powered quiz and interview preparation platform that helps users practice and improve their skills through interactive quizzes and mock interviews.

## 🌟 Features

- **AI-Generated Quizzes**: Get personalized quizzes based on topics and difficulty levels
- **Interview Preparation**: Practice with mock interviews and get AI-powered feedback
- **Performance Analytics**: Track your progress with detailed performance reports
- **File Uploads**: Upload study materials for AI analysis
- **User Authentication**: Secure JWT-based authentication with Supabase
- **Real-time Feedback**: Get instant AI-powered evaluation of your answers
- **Multi-topic Support**: Practice various subjects and technical domains

## 🏗️ System Architecture

```mermaid
graph TD
    A[Frontend React App] -->|HTTPS API Calls| B[Backend Spring Boot]
    B --> C[Supabase Auth]
    B --> D[Supabase Storage]
    B --> E[Supabase PostgreSQL]
    B --> F[Google Gemini AI]
    
    subgraph "Frontend Layer"
        A1[React Components]
        A2[Redux Store]
        A3[Axios Client]
        A4[Material-UI]
    end
    
    subgraph "Backend Layer"
        B1[Spring Boot App]
        B2[JWT Security]
        B3[REST Controllers]
        B4[Business Services]
        B5[JPA Repositories]
    end
    
    subgraph "External Services"
        C[Authentication]
        D[File Storage]
        E[Database]
        F[AI Service]
    end
    
    A --> A1
    A1 --> A2
    A2 --> A3
    A3 --> B
    
    B --> B1
    B1 --> B2
    B2 --> B3
    B3 --> B4
    B4 --> B5
    B5 --> E
```

## 📂 Project Structure

```
QuizoraAI/
├── Backend/                          # Spring Boot Backend
│   ├── src/main/java/com/quizora/
│   │   ├── config/                   # Configuration Classes
│   │   │   ├── SecurityConfig.java   # Spring Security Configuration
│   │   │   └── WebClientConfig.java  # WebClient Bean Configuration
│   │   │
│   │   ├── controller/               # REST API Controllers
│   │   │   ├── AuthController.java   # Authentication Endpoints
│   │   │   ├── QuizController.java   # Quiz Management
│   │   │   ├── InterviewController.java # Interview Management
│   │   │   ├── FileUploadController.java # File Upload
│   │   │   ├── PerformanceController.java # Performance Tracking
│   │   │   └── TestController.java   # Testing Endpoints
│   │   │
│   │   ├── dto/                      # Data Transfer Objects
│   │   │   ├── QuizGenerationRequest.java
│   │   │   ├── QuizSubmissionRequest.java
│   │   │   ├── InterviewStartRequest.java
│   │   │   ├── InterviewSessionResponse.java
│   │   │   └── InterviewReportResponse.java
│   │   │
│   │   ├── entity/                   # JPA Entities
│   │   │   ├── User.java             # User Entity
│   │   │   ├── Quiz.java             # Quiz Entity
│   │   │   ├── Question.java         # Question Entity
│   │   │   ├── QuizAttempt.java      # Quiz Attempts
│   │   │   ├── InterviewSession.java # Interview Sessions
│   │   │   ├── InterviewResponse.java # Interview Responses
│   │   │   └── UserPerformance.java  # Performance Metrics
│   │   │
│   │   ├── repository/               # Data Access Layer
│   │   │   ├── UserRepository.java
│   │   │   ├── QuizRepository.java
│   │   │   ├── QuestionRepository.java
│   │   │   ├── QuizAttemptRepository.java
│   │   │   ├── InterviewSessionRepository.java
│   │   │   ├── InterviewResponseRepository.java
│   │   │   └── UserPerformanceRepository.java
│   │   │
│   │   ├── service/                  # Business Logic Layer
│   │   │   ├── QuizService.java      # Quiz Management Logic
│   │   │   ├── InterviewService.java # Interview Logic
│   │   │   ├── FileUploadService.java # File Upload Logic
│   │   │   ├── SupabaseService.java  # Supabase Integration
│   │   │   ├── ContentExtractionService.java # Content Processing
│   │   │   ├── PdfExtractionService.java # PDF Processing
│   │   │   ├── YoutubeTranscriptService.java # YouTube Processing
│   │   │   ├── AiIntegrationService.java # AI Integration
│   │   │   └── PerformanceService.java # Performance Tracking
│   │   │
│   │   └── QuizoraApplication.java  # Main Application Class
│   │
│   ├── src/main/resources/
│   │   ├── application.properties    # Application Configuration
│   │   └── .env                      # Environment Variables
│   │
│   ├── pom.xml                       # Maven Dependencies
│   └── .gitignore                    # Git Ignore File
│
├── Frontend/                          # React Frontend (if exists)
│   ├── src/
│   │   ├── components/               # React Components
│   │   ├── pages/                    # Page Components
│   │   ├── services/                 # API Services
│   │   ├── store/                    # Redux Store
│   │   └── utils/                    # Utility Functions
│   ├── package.json                  # Node Dependencies
│   └── public/                       # Static Assets
│
└── README.md                          # This File
```

## 🔄 Data Flow

```mermaid
sequenceDiagram
    participant U as User
    participant F as Frontend
    participant B as Backend
    participant DB as Supabase DB
    participant AI as Google Gemini AI
    participant S as Supabase Storage
    
    U->>F: Request Quiz Generation
    F->>B: POST /api/quizzes/generate
    B->>AI: Generate Questions
    AI-->>B: Return Questions
    B->>DB: Save Quiz
    DB-->>B: Confirmation
    B-->>F: Quiz Data
    F-->>U: Display Quiz
    
    U->>F: Submit Quiz Answers
    F->>B: POST /api/quizzes/submit
    B->>AI: Evaluate Answers
    AI-->>B: Evaluation Results
    B->>DB: Save Results
    DB-->>B: Confirmation
    B-->>F: Results Data
    F-->>U: Display Results
    
    U->>F: Upload File
    F->>B: POST /api/files/upload
    B->>S: Store File
    S-->>B: File URL
    B->>AI: Extract Content
    AI-->>B: Extracted Content
    B->>DB: Save Metadata
    DB-->>B: Confirmation
    B-->>F: Success Response
    F-->>U: Upload Complete
```

## 🔐 Authentication Flow

```mermaid
graph LR
    A[User] -->|1. Login Request| B[Frontend]
    B -->|2. Redirect to Supabase| C[Supabase Auth]
    C -->|3. Authentication| D[Supabase]
    D -->|4. JWT Token| B
    B -->|5. Store Token| E[Local Storage]
    B -->|6. Include in Headers| F[Backend API]
    F -->|7. Verify JWT| D
    D -->|8. User Data| F
    F -->|9. Process Request| G[Business Logic]
    G -->|10. Response| B
    B -->|11. Update UI| A
```

## 🚀 Getting Started

### Prerequisites

- **Java 17+**
- **Maven 3.6+**
- **Node.js 16+** (for frontend)
- **Supabase Account**
- **Google Cloud Account** (for Gemini AI)

### Backend Setup

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/QuizoraAI.git
   cd QuizoraAI/Backend
   ```

2. **Configure Environment Variables**
   ```bash
   # Copy the environment template
   cp .env.example .env
   
   # Edit .env with your credentials
   # Supabase Configuration
   SUPABASE_URL=https://your-project.supabase.co
   SUPABASE_SERVICE_KEY=your-service-key
   SUPABASE_JWK_URL=https://your-project.supabase.co/auth/v1/jwks
   
   # Database Configuration
   SUPABASE_DB_URL=postgresql://postgres:password@db.your-project.supabase.co:5432/postgres
   SUPABASE_DB_USERNAME=postgres
   SUPABASE_DB_PASSWORD=your-password
   
   # AI Configuration
   AI_API_KEY=your-gemini-api-key
   AI_API_URL=https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent
   ```

3. **Build and Run the Application**
   ```bash
   # Clean and compile
   mvn clean compile
   
   # Run the application
   mvn spring-boot:run
   
   # Or build and run
   mvn clean package
   java -jar target/quizora-backend-1.0.0.jar
   ```

4. **Access the Application**
   - Backend API: `http://localhost:8080`
   - API Documentation: `http://localhost:8080/swagger-ui.html`
   - Health Check: `http://localhost:8080/actuator/health`

### Frontend Setup (if applicable)

1. **Navigate to Frontend Directory**
   ```bash
   cd ../Frontend
   ```

2. **Install Dependencies**
   ```bash
   npm install
   ```

3. **Start Development Server**
   ```bash
   npm start
   ```

4. **Access the Frontend**
   - Frontend App: `http://localhost:3000`

## 📚 API Documentation

### Authentication Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | User login |
| POST | `/api/auth/register` | User registration |
| POST | `/api/auth/refresh` | Refresh JWT token |

### Quiz Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/quizzes/generate` | Generate AI-powered quiz |
| POST | `/api/quizzes/submit` | Submit quiz answers |
| GET | `/api/quizzes/{id}` | Get quiz by ID |
| GET | `/api/quizzes/user/{userId}` | Get user's quizzes |

### Interview Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/interviews/start` | Start interview session |
| POST | `/api/interviews/respond` | Submit interview response |
| GET | `/api/interviews/{id}/report` | Get interview report |

### File Upload Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/files/upload` | Upload study material |
| GET | `/api/files/{id}` | Download file |
| DELETE | `/api/files/{id}` | Delete file |

### Performance Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/performance/user/{userId}` | Get user performance |
| GET | `/api/performance/quiz/{quizId}` | Get quiz performance |

## 🛠️ Tech Stack

### Backend Technologies
- **Framework**: Spring Boot 3.2.0
- **Security**: Spring Security 6
- **Database**: PostgreSQL (via Supabase)
- **ORM**: Spring Data JPA & Hibernate
- **Authentication**: JWT with Supabase
- **AI Integration**: Google Gemini API
- **File Storage**: Supabase Storage
- **HTTP Client**: Spring WebFlux WebClient
- **Build Tool**: Maven

### Key Dependencies
```xml
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
    
    <!-- Database -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>
    
    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
</dependencies>
```

## 🔧 Configuration

### Application Properties
```properties
# Database Configuration
spring.datasource.url=${SUPABASE_DB_URL}
spring.datasource.username=${SUPABASE_DB_USERNAME}
spring.datasource.password=${SUPABASE_DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Server Configuration
server.port=8080

# Supabase Configuration
supabase.url=${SUPABASE_URL}
supabase.service.key=${SUPABASE_SERVICE_KEY}
supabase.jwk.url=${SUPABASE_JWK_URL}
supabase.storage.bucket=uploads

# AI Configuration
ai.api.url=${AI_API_URL}
ai.api.key=${AI_API_KEY}
```

## 🧪 Testing

### Running Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=QuizServiceTest

# Run with coverage
mvn jacoco:report
```

### Test Endpoints
- **Database Test**: `GET /api/test/db`
- **Auth Test**: `GET /api/test/auth`
- **Storage Test**: `GET /api/test/storage`

## 📊 Monitoring & Logging

### Application Monitoring
- **Health Checks**: `/actuator/health`
- **Metrics**: `/actuator/metrics`
- **Info**: `/actuator/info`

### Logging Configuration
```properties
logging.level.root=INFO
logging.level.com.quizora=DEBUG
logging.level.org.springframework.security=DEBUG
```

## 🚀 Deployment

### Docker Deployment
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/quizora-backend-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Environment Variables
- **Production**: Use environment-specific properties
- **Development**: Use `.env` file
- **Testing**: Use test properties

## 🤝 Contributing

1. **Fork the Repository**
2. **Create Feature Branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Commit Changes**
   ```bash
   git commit -m 'Add amazing feature'
   ```
4. **Push to Branch**
   ```bash
   git push origin feature/amazing-feature
   ```
5. **Open Pull Request**

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **[Supabase](https://supabase.io)** - Authentication and Database
- **[Spring Boot](https://spring.io/projects/spring-boot)** - Backend Framework
- **[Google Gemini AI](https://ai.google.dev)** - AI Services
- **[PostgreSQL](https://www.postgresql.org)** - Database
- **[Maven](https://maven.apache.org)** - Build Tool

## 📞 Support

For support and questions:
- Create an issue on GitHub
- Email: support@quizora.com
- Documentation: [Wiki](https://github.com/yourusername/QuizoraAI/wiki)

---

**Built with ❤️ by the Quizora Team**
