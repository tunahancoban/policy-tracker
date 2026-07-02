import axios from 'axios';
const api = axios.create({
  baseURL: 'http://localhost:8080', // Spring Boot adresin
  headers: {
    'Content-Type': 'application/json',
  },
});

export { api };
