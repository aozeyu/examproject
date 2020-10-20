import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    component: () => import('../components/Login')
  },
  {
    path: '/register',
    component: () => import('../components/Register')
  },
  {
    path: '/index',
    component: () => import('../components/Main')
  }
]

const router = new VueRouter({
  routes
})

router.beforeEach((to, from, next) => {
  //三个不用token的页面请求
  if (to.path === '/' || to.path === '/register') {
    return next()
  }
  const token = window.localStorage.getItem('authorization')
  //没有token的情况 直接返回登录页
  if (!token) return next('/')
  next()
})

export default router
