import auth from '@/api/auth'
import Vue from 'vue'
import router from '../router/index'

// valid el form and invoke target method
const validFormAndInvoke = (formEl, success, fail = function () {
}, message = '信息有误') => {
  if (!formEl) {
    return
  }
  formEl.validate(valid => {
    if (valid) {// form valid succeed
      // do success function
      success()
      // reset fields
      formEl.resetFields()
    } else {// form valid fail
      Vue.prototype.$notify({
        title: 'Tips',
        message: message,
        type: 'error',
        duration: 2000
      })
      // do something when fail
      fail()
      return false
    }
  })
}
// check token and router link
const checkToken = (to) => {
  if (localStorage.getItem('authorization') !== null) {
    auth.checkToken()
      .then(resp => {
        if (resp.code === 200) {
          router.push(to)
        }
      })
      .catch(error => {
        localStorage.removeItem('authorization')
      })
  }
}

export default {
  validFormAndInvoke,
  checkToken
}
