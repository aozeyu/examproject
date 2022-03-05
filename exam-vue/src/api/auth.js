import request from '@/utils/request'

export default {
  login (loginUser) {
    return request({
      url: '/common/login',
      method: 'post',
      data: loginUser
    })
  },
  checkToken () {
    return request({
      url: '/common/checkToken',
      method: 'get'
    })
  },
  getCode () {
    return request({
      url: '/util/getCode',
      method: 'get'
    })
  }
}
