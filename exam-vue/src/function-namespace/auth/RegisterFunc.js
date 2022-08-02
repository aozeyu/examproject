import router from '@/router'
import auth from '@/api/auth'
import utils from '@/utils/utils'

// 注册表单数据信息
const registerForm = {
  username: '',
  trueName: '',
  password: '',
  code: ''
}
// 自定义验证码校验规则
const validateCode = (rule, value, callback) => {
  // 验证码不区分大小写
  if (value.toString().toLocaleLowerCase() !== code.toString().toLocaleLowerCase()) {
    callback(new Error('验证码输入错误'))
  } else {
    callback()
  }
}
// 自定义用户名校验规则
const validateUsername = (rule, value, callback) => {
  auth.checkUsername(registerForm.username)
    .then(resp => {
      if (resp.data) {
        callback()
      } else {
        callback(new Error('用户名已存在'))
      }
    })
}
// 登录表单的校验规则
const registerFormRules = {
  username: [
    {
      required: true,
      message: '请输入账号',
      trigger: 'blur'
    },
    {
      validator: validateUsername,
      trigger: 'blur'
    }
  ],
  trueName: [
    {
      required: true,
      message: '请输入您的姓名',
      trigger: 'blur'
    },
  ],
  password: [
    {
      required: true,
      message: '请输入密码',
      trigger: 'blur'
    },
    {
      min: 5,
      message: '密码不能小于5个字符',
      trigger: 'blur'
    }
  ],
  code: [
    {
      required: true,
      message: '请输入验证码',
      trigger: 'blur'
    },
    {
      validator: validateCode,
      trigger: 'blur'
    }
  ],
}

const toLoginPage = () => {
  router.push('/')
}
// 后台的验证码
let code = window.onload = () => getCode()
// 获取后台验证码
const getCode = () => {
  auth.getCode().then(resp => {
    code = resp.message
  })
}
// 点击图片刷新验证码
const changeCode = () => {
  const code = document.querySelector('#code')
  code.src = `${process.env.VUE_APP_CAPTCHA_URL}util/getCodeImg?id=` + Math.random()
  code.onload = () => getCode()
}

// 表单信息提交
const register = (formEl) => {
  utils.validFormAndInvoke(formEl, () => {
    auth.register(registerForm).then(resp => {
      if (resp.code === 200) {
        localStorage.setItem('authorization', resp.data)
        router.push('/index')
      }
    })
  }, '注册失败')
}

export default {
  // data
  code,
  registerForm,
  registerFormRules,
  // method
  toLoginPage,
  getCode,
  changeCode,
  register
}
