import request from '@/utils/request'

export default {
  getCurrentNewNotice () {
    return request({
      url: '/public/getCurrentNewNotice'
    })
  }
}
