<template>
  <el-container>
    <!--用户头部菜单-->
    <el-aside id="aside" width="210px">
      <el-menu default-active="/index" :router="true" class="el-menu-vertical-demo"
               background-color="rgb(48,65,86)"
               text-color="rgb(191,203,217)"
               active-text-color="rgb(64,158,255)"
               :collapse="isCollapse"
      >
        <el-menu-item index="/index" style="text-align: center">
          <i class="el-icon-sunny"></i>
          <span slot="title">
            追风考试系统
          </span>
        </el-menu-item>
        <!-- 单独的导航 -->
        <el-menu-item :index="menuInfo[0].url" v-if="!menuInfo[0].submenu">
          <i :class="menuInfo[0].topIcon"></i>
          <span slot="title">{{ menuInfo[0].topMenuName }}</span>
        </el-menu-item>

        <!--具有子导航的-->
        <el-submenu v-if="menu.submenu" v-for="(menu,index) in menuInfo" :key="index" :index="index+''">
          <template slot="title">
            <i :class="menu.topIcon"></i>
            <span slot="title">{{ menu.topMenuName }}</span>
          </template>

          <!--子导航的分组-->
          <el-menu-item-group>
            <el-menu-item :index="sub.url" v-for="(sub,index) in menu.submenu" :key="index">
              <i :class="sub.icon"></i>
              <span slot="title">{{ sub.name }}</span>
            </el-menu-item>
          </el-menu-item-group>

        </el-submenu>
      </el-menu>
    </el-aside>

    <!--右侧的面板-->
    <el-main>

      <el-container>

        <el-header height="100px">
          <el-card class="box-card">
            <div slot="header">
              <!--缩小图标-->
              <el-tooltip class="item" effect="dark" content="缩小侧边栏" placement="top-start">
                <i class="el-icon-s-fold" @click="changeIsCollapse"
                   style="cursor:pointer;font-size: 25px;font-weight: 100"></i>
              </el-tooltip>

              <!--面包屑-->
              <el-breadcrumb style="margin-left: 15px">
                <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
                <el-breadcrumb-item>活动管理</el-breadcrumb-item>
                <el-breadcrumb-item>活动列表</el-breadcrumb-item>
                <el-breadcrumb-item>活动详情</el-breadcrumb-item>
              </el-breadcrumb>

              <!--右侧的个人信息下拉框-->
              <el-dropdown trigger="click" style="float: right;color: black;cursor:pointer;" @command="handleCommand">
                <span class="el-dropdown-link">
                  {{ currentUserInfo.username }}
                  <i class="el-icon-caret-bottom"></i>
                </span>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item command="personInfo">个人资料</el-dropdown-item>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>

              <!--右侧的放大图标-->
              <el-tooltip effect="dark" content="全屏预览" placement="top-start">
                <i class="el-icon-full-screen" id="full" @click="fullShow" style="float: right;margin-right:10px;
              margin-bottom:5px;cursor:pointer;font-size: 25px;font-weight: 100"></i>
              </el-tooltip>

            </div>

            <!--卡片面板的主内容-->
            <div>
              <el-tag @close="handleClose(tag)"
                      type="info" :key="item" size="small"
                      closable v-for="item in 2"
                      effect="plain">
                测试标签{{item}}
              </el-tag>
            </div>
          </el-card>
        </el-header>

        <el-main style="margin-top: 25px;">
          <router-view></router-view>
        </el-main>

      </el-container>

    </el-main>
  </el-container>
</template>

<script>
  export default {
    name: 'Main',
    data () {
      return {
        //菜单信息
        menuInfo: [
          {
            'topIcon': ''
          }
        ],
        //面板是否收缩
        isCollapse: false,
        //当前是否全屏显示
        isFullScreen: false,
        //当前登录的用户信息
        currentUserInfo: {
          'username': ''
        }
      }
    },
    mounted () {
      this.getMenu()
      //获取登录用户信息
      this.getUserInfoByCheckToken()
    },
    methods: {
      //根据token后台判断用户权限,传递相对应的菜单
      getMenu () {
        this.$http.get(this.API.getMenuInfo).then((resp) => {
          if (resp.data.code === 200) {
            this.menuInfo = JSON.parse(resp.data.data)
          } else {//后台认证失败,跳转登录页面
            this.$message.error('权限认证失败,获取菜单数据失败')
            this.$router.push('/')
          }
        })
      },
      //放大缩小侧边栏
      changeIsCollapse () {
        const aside = document.querySelector('#aside')
        if (this.isCollapse) {
          aside.style.width = 210 + 'px'
        } else {
          aside.style.width = 65 + 'px'
        }
        this.isCollapse = !this.isCollapse
      },
      //是否全屏显示
      fullShow () {
        var docElm = document.documentElement
        const full = document.querySelector('#full')
        if (this.isFullScreen) {//退出全屏模式
          //切换图标样式
          full.className = 'el-icon-full-screen'
          //W3C
          if (document.exitFullscreen) {
            document.exitFullscreen()
          }
          //FireFox
          else if (document.mozCancelFullScreen) {
            document.mozCancelFullScreen()
          }
          //Chrome等
          else if (document.webkitCancelFullScreen) {
            document.webkitCancelFullScreen()
          }
          //IE11
          else if (document.msExitFullscreen) {
            document.msExitFullscreen()
          }
        } else {//进入全屏模式
          //W3C
          //切换图标样式
          full.className = 'el-icon-switch-button'
          if (docElm.requestFullscreen) {
            docElm.requestFullscreen()
          }
          //FireFox
          else if (docElm.mozRequestFullScreen) {
            docElm.mozRequestFullScreen()
          }
          //Chrome等
          else if (docElm.webkitRequestFullScreen) {
            docElm.webkitRequestFullScreen()
          }
          //IE11
          else if (docElm.msRequestFullscreen) {
            docElm.msRequestFullscreen()
          }
        }
        //改变标志位
        this.isFullScreen = !this.isFullScreen
      },
      //处理右上角下拉菜单的处理事件
      handleCommand (command) {
        if (command === 'logout') {//退出
          this.logout()
        } else if (command === 'personInfo') {

        }
      },
      //退出登录
      async logout () {
        const resp = await this.$http.get(this.API.logout)
        if (resp.data.code === 200) {//退出成功
          this.$message.success('注销成功')
          window.localStorage.removeItem('authorization')
          //右侧提示通知
          this.$notify({
            title: 'Tips',
            message: '注销成功',
            type: 'success',
            duration: 2000
          })
          await this.$router.push('/')
        } else {//异常
          this.$notify({
            title: 'Tips',
            message: '注销失败,服务器异常',
            type: 'error',
            duration: 2000
          })
        }
      },
      //检查token获取其中的用户信息
      async getUserInfoByCheckToken () {
        const resp = await this.$http.get(this.API.checkToken)
        this.currentUserInfo = resp.data.data
      },
      //关闭tag标签
      handleClose (tag) {

      }
    }
  }
</script>

<style scoped lang="scss">
  .el-container {
    width: 100%;
    height: 100%;

    .el-menu {
      height: 100% !important;
    }
  }

  .el-main, .el-header {
    padding: 0;
  }

  .el-menu-item:hover {
    background-color: rgb(38, 52, 69) !important;
  }

  /deep/ .el-submenu__title:hover {
    background-color: rgb(38, 52, 69) !important;
  }

  /deep/ .el-menu-item-group__title {
    padding: 0 !important;
  }

  .el-menu-vertical-demo:not(.el-menu--collapse) {
    width: 200px;
    min-height: 400px;
  }

  /*  右侧面板根据左侧的宽度变化而变化,侧边栏缩小,右侧面板变大,反之同理*/
  #aside {
    transition: width .3s;
  }

  .el-breadcrumb {
    display: inline-block;
  }

  /*右上角的个人信息字体*/
  .el-dropdown-link {
    font-weight: 500;
    font-size: 18px;
  }

  .el-tag {
    border: none;
    border-radius: 0;
    box-shadow: 0 0 .5px .5px gray;
    color: black;
    font-weight: 400;
    text-align: center;
    margin-left: 10px;
  }

  /deep/ .el-card__body {
    padding: 10px;
  }
</style>
