import axios from 'axios'

const baseURL = import.meta.env.VITE_API_BASE_URL || '/api'
console.log('API baseURL:', baseURL)
const api = axios.create({ baseURL, timeout: 10000 })

api.interceptors.request.use(config => {
  const user = localStorage.getItem('user')
  if (user) {
    config.headers['X-User-Id'] = JSON.parse(user).id
  }
  return config
})

api.interceptors.response.use(res => res, err => {
  console.error('API Error:', err.response?.data || err.message)
  return Promise.reject(err)
})

export const login = (data) => api.post('/user/login', data)
export const register = (data) => api.post('/user/register', data)

export const getShops = (params) => api.get('/shops', { params })
export const getShopDetail = (id) => api.get(`/shops/${id}`)
export const getHotShops = () => api.get('/shops/hot')

export const getNotes = (params) => api.get('/notes', { params })
export const publishNote = (data) => api.post('/notes', data)

export const getComments = (shopId) => api.get(`/comments/${shopId}`)
export const postComment = (data) => api.post('/comments', data)

export const toggleFav = (shopId) => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  return api.post('/favorites/toggle', { shopId, userId: user.id || 1 })
}
export const getFavorites = () => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  return api.get('/favorites', { params: { userId: user.id || 1 } })
}

export const search = (keyword) => api.get('/search', { params: { keyword } })

export default api