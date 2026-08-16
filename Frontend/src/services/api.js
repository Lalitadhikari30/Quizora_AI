import axios from "axios";

const api = axios.create({
  baseURL: process.env.REACT_APP_API_URL || "http://localhost:8081",
  timeout: 60000,
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      const path = window.location.pathname;
      if (path !== "/login" && path !== "/register") {
        localStorage.removeItem('token');
        localStorage.removeItem('userName');
        window.location.href = "/login";
      }
    }

    const data = error.response?.data;
    let errorMessage = "An unexpected error occurred";

    if (typeof data === "string") {
      errorMessage = data;
    } else if (data && typeof data === "object") {
      errorMessage =
        data.details ||
        data.error ||
        data.message ||
        "An unexpected error occurred";
    } else if (error.message) {
      errorMessage = error.message;
    }

    return Promise.reject(new Error(errorMessage));
  }
);

export default api;
