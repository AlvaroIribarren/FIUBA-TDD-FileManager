const API_BASE_URL = require('../constants/index').default.API_BASE_AUTH;
const API_REFRESH = require('../constants/index').default.API_REFRESH;

const axios = require('axios');
const axiosApiInstance = axios.create();

async function refreshAccessToken() {
    console.log("Refrescando token")
    const response = await axios.get(API_BASE_URL + API_REFRESH, {
        headers: {
            refreshToken: localStorage.getItem('refreshToken')
        }
    })

    if (response.status === 200){
        localStorage.setItem('token', response.headers.token)
        localStorage.setItem('refreshToken', response.headers.refreshtoken)
    }
    return response.headers.token;
}

// Request interceptor for API calls
axiosApiInstance.interceptors.request.use(
  async config => {
    config.headers = { 
      'token': localStorage.getItem('token'),
    }
    console.log("Entrando a interceptor")
    return config;
  },
  error => {
    Promise.reject(error)
});


// Response interceptor for API calls
axiosApiInstance.interceptors.response.use((response) => {
  return response
}, async function (error) {
    console.log("Interceptando respuesta")
    const originalRequest = error.config;
    if (error.response.status === 401 && !originalRequest._retry) {
        originalRequest._retry = true;
        const token = await refreshAccessToken();            
        axios.defaults.headers.common['token'] = token;
        return axiosApiInstance(originalRequest);
    }
    return Promise.reject(error);
});

export default axiosApiInstance;
