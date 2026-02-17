// Test script to check Supabase API endpoints
const https = require('https');

const supabaseUrl = 'https://gplfxihikpsppsbctjpv.supabase.co';
const supabaseKey = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImdwbGZ4aWhpa3BzcHBzYmN0anB2Iiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc3MTEwODQxNiwiZXhwIjoyMDg2Njg0NDE2fQ.O7Zc6SKB96ygerDIOFu7IjgT3N17L2XXBQTC-HdR4lE';

async function testSupabaseAuth() {
    console.log('Testing Supabase authentication...');
    
    try {
        // Test registration
        const signUpResponse = await https.request({
            hostname: `${supabaseUrl.replace('https://', '')}`,
            port: 443,
            path: '/auth/v1/signup',
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${supabaseKey}`,
                'apikey': supabaseKey,
                'Content-Type': 'application/json'
            },
            json: {
                email: 'test@example.com',
                password: 'testpassword123',
                data: {
                    first_name: 'Test',
                    last_name: 'User'
                }
            }
        });
        
        console.log('Registration response:', signUpResponse);
        
        // Test login
        const signInResponse = await https.request({
            hostname: `${supabaseUrl.replace('https://', '')}`,
            port: 443,
            path: '/auth/v1/token?grant_type=password',
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${supabaseKey}`,
                'apikey': supabaseKey,
                'Content-Type': 'application/json'
            },
            json: {
                email: 'test@example.com',
                password: 'testpassword123'
            }
        });
        
        console.log('Login response:', signInResponse);
        
    } catch (error) {
        console.error('Supabase API error:', error.message);
    }
}

testSupabaseAuth();
