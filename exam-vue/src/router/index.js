import Vue from 'vue'
import VueRouter from 'vue-router'
import axios from 'axios'

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
    component: () => import('../components/Main'),
    redirect: '/dashboard',
    children: [
      //仪表盘介绍(all)
      {
        path: '/dashboard',
        component: () => import('../components/Dashboard')
      },
      //用户管理(超级管理员)
      {
        path: '/userManage',
        component: () => import('../components/UserManage')
      }
    ]
  }
]

const router = new VueRouter({
  routes
})
router.beforeEach((to, from, next) => {
  const token = window.localStorage.getItem('authorization')
  //2个不用token的页面请求
  if (to.path === '/' || to.path === '/register') {
    return next()
  }
  //没有token的情况 直接返回登录页
  if (!token) return next('/')
  //属于超级管理员的功能
  if (to.path === '/userManage') {
    axios.get('/common/checkToken').then((resp) => {
        if (resp.data.code === 200 && resp.data.data.roleId === '3') {//当前用户携带的token信息正确并且是管理员
          next();
        }else return next('/index');
    })
  }
  next()
})

export default router
