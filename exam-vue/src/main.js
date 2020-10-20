import Vue from 'vue'
import App from './App.vue'
import router from './router'
import './plugins/element.js'
import axios from 'axios'
import api from './api/api'

//配置请求根路径
axios.defaults.baseURL = 'http://localhost:8888/'

Vue.prototype.API = api.API.api
/**
 * 原型链挂载
 * @type {AxiosStatic}
 */
Vue.prototype.$http = axios
Vue.config.productionTip = false

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
