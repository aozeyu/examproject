<template>
  <el-container>
    <el-main>
      <el-card class="box-card" shadow="always">
        <div slot="header" class="card-header">
          <p>追风考试系统</p>
        </div>

        <div>
          <el-form :model="loginForm" :rules="loginFormRules" ref="loginForm" :status-icon="true" label-width="100px">
            <el-form-item prop="username">
              <el-input prefix-icon="el-icon-user" v-model="loginForm.username" placeholder="账号"></el-input>
            </el-form-item>

            <el-form-item prop="password">
              <el-input prefix-icon="el-icon-lock" v-model="loginForm.password" placeholder="密码"
                        show-password></el-input>
            </el-form-item>

            <el-form-item prop="code">
              <el-input class="code" prefix-icon="el-icon-chat-line-round" v-model="loginForm.code"
                        placeholder="验证码"></el-input>
              <img src="http://localhost:8888/util/getCodeImg" @click="changeCode" id="code"
                   style="float: right;margin-top: 4px;cursor: pointer" title="看不清,点击刷新"
                   alt="验证码"/>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="login($refs['loginForm'])" icon="el-icon el-icon-s-promotion">登录
              </el-button>
              <el-button type="warning" @click="toRegisterPage" icon="el-icon el-icon-circle-plus">学员注册</el-button>
            </el-form-item>

          </el-form>
        </div>
      </el-card>
    </el-main>

    <el-footer>
      <span>&copy;2020-2020 Power By Wzz</span>
      <br>
      <i class="el-icon-thumb"></i>
      <a href="https://gitee.com/wzhouzhou/vue_wzz_cloudMusic" target="_blank">高仿网易云音乐</a>
      /
      <a href="https://gitee.com/wzhouzhou/privateBlog" target="_blank">自研博客系统</a>
      /
      <span style="color: blueviolet">Q群: 970804317</span>
    </el-footer>
  </el-container>
</template>

<script>
import LoginFunc from '@/function-namespace/auth/LoginFunc'
import common from '@/utils/common'

export default {
  name: 'Login',
  data () {
    return {
      ...LoginFunc
    }
  },
  created () {
    // 检验用户是否存在token,存在直接跳转主页
    common.checkToken('/index')
  },
  mounted () {
    this.changeCode()
  }
}
</script>

<style scoped lang="scss">
.el-container {
  min-width: 417px;
  height: 100%;
  background: url("../../assets/imgs/bg.png");
  background-size: 100% 100%;
}

a {
  text-decoration: none;
  color: blueviolet;
}

/*  card样式  */
.box-card {
  width: 450px;
}

.el-card {
  position: absolute;
  top: 45%;
  left: 50%;
  transform: translateX(-50%) translateY(-50%);
  border-radius: 15px;
}

.card-header {
  text-align: center;

  p {
    font-size: 20px;
  }
}

/*  表单的左侧margin清楚 */
/deep/ .el-form-item__content {
  margin: 0 !important;
}

/*  按钮样式 */
.el-button {
  width: 48%;
}

/*  按钮前的小图标样式更改*/
/deep/ .el-icon {
  margin-right: 3px;
}

/*  验证码的输入框*/
.code {
  width: 72%;
}
</style>
