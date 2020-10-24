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
    checkToken: '/common/checkToken',
    //用户退出登录
    logout: '/common/logout',
    //获取用户信息(可附带查询条件,可分页,n功能合一接口)
    getUserInfo: '/admin/getUser',
    //管理员操作用户
    handleUser: '/admin/handleUser',
    //管理员新增用户
    addUser: '/admin/addUser',
    //获取角色信息
    getRoleInfo: '/admin/getRole',
    //获取所有题库信息
    getQuestionBank: '/teacher/getQuestionBank',
    //获取题目信息(可附带查询条件,可分页,n功能合一接口)
    getQuestion: '/teacher/getQuestion',
    //批量删除题目
    deleteQuestion: '/teacher/deleteQuestion',
    //将题目加入进题库
    addBankQuestion: '/teacher/addBankQuestion',
    //讲题目从题库中移除
    removeBankQuestion: '/teacher/removeBankQuestion'
  },
}

export default {
  API: API,
}
