// Simple test to check Supabase connectivity
const https = require('https');

async function testSupabase() {
    try {
        console.log('Testing Supabase API...');
        
        const options = {
            hostname: 'gplfxihikpsppsbctjpv.supabase.co',
            port: 443,
            path: '/rest/v1/',
            method: 'GET',
            headers: {
                'apikey': 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImdwbGZ4aWhpa3BzcHBzYmN0anB2Iiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc3MTEwODQxNiwiZXhwIjoyMDg2Njg0NDE2fQ.O7Zc6SKB96ygerDIOFu7IjgT3N17L2XXBQTC-HdR4lE',
                'Content-Type': 'application/json'
            }
        };
        
        console.log('Request options:', JSON.stringify(options, null, 2));
        
        const req = https.request(options, (res) => {
            console.log(`Status: ${res.statusCode}`);
            console.log(`Headers: ${JSON.stringify(res.headers, null, 2)}`);
            
            let data = '';
            res.on('data', (chunk) => {
                data += chunk;
            });
            
            res.on('end', () => {
                console.log('Response body:', data);
            });
        });
        
        req.on('error', (error) => {
            console.error('Request error:', error.message);
        });
        
        req.end();
        
    } catch (error) {
        console.error('Test error:', error.message);
    }
}

testSupabase();
