let API = {
  //应用表
  api: {
    // 查询应用列表
    register:  '/common/register',
    //查询当前的验证码
    getCode: '/util/getCode',
    //校验用户名
    checkUsername: '/common/check',
    //登陆接口
    login: '/common/login',
    //获取主页面的左侧menu数据
    getMenuInfo: '/common/getMenu',
    //检验token合法(返回当前对象)
    checkToken: '/common/checkToken'
  },
}

export default {
  API: API,
}
