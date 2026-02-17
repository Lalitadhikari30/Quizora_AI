// This file marks TypeScript files for deletion
// Run: node delete-typescript-files.js

const fs = require('fs');
const path = require('path');

const filesToDelete = [
  'src/App.tsx',
  'src/index.tsx', 
  'src/components/layout/Navbar.tsx',
  'src/components/layout/Footer.tsx',
  'src/components/auth/ProtectedRoute.tsx',
  'src/pages/Home.tsx',
  'src/pages/Login.tsx',
  'src/pages/Register.tsx',
  'src/pages/Dashboard.tsx',
  'src/pages/QuizGeneration.tsx',
  'src/pages/Interview.tsx',
  'src/pages/Performance.tsx',
  'src/pages/QuizTaking.tsx',
  'src/hooks/useAuth.tsx',
];

filesToDelete.forEach(file => {
  const filePath = path.join(__dirname, file);
  if (fs.existsSync(filePath)) {
    fs.unlinkSync(filePath);
    console.log(`Deleted: ${file}`);
  }
});

console.log('TypeScript files cleanup complete!');
