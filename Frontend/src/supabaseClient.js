import { createClient } from '@supabase/supabase-js';

const supabaseUrl = process.env.REACT_APP_SUPABASE_URL || 'https://gplfxihikpsppsbctjpv.supabase.co';
const supabaseAnonKey = process.env.REACT_APP_SUPABASE_ANON_KEY || 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImdwbGZ4aWhpa3BzcHBzYmN0anB2Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzExMDg0MTYsImV4cCI6MjA4NjY4NDQxNn0.I9blXazWlDJjMZihkpltWU_FfyKvViFsdXCDneuuCE0';

export const supabase = createClient(supabaseUrl, supabaseAnonKey);
