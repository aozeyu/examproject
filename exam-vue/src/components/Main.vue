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
              <i class="el-icon-s-fold" @click="changeIsCollapse"
                 style="cursor:pointer;font-size: 25px;font-weight: 100"></i>
              <!--面包屑-->
              <el-breadcrumb style="margin-left: 15px">
                <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
                <el-breadcrumb-item>活动管理</el-breadcrumb-item>
                <el-breadcrumb-item>活动列表</el-breadcrumb-item>
                <el-breadcrumb-item>活动详情</el-breadcrumb-item>
              </el-breadcrumb>

              <el-dropdown trigger="click" style="float: right;color: black;cursor:pointer;">
                <span class="el-dropdown-link">
                  下拉菜单<i class="el-icon-arrow-down el-icon--right"></i>
                </span>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item command="a">个人资料</el-dropdown-item>
                  <el-dropdown-item command="b">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </div>

            <div v-for="o in 4" :key="o" class="text item">
              {{'列表内容 ' + o }}
            </div>
          </el-card>
        </el-header>
        <el-main>
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
        isCollapse: false
      }
    },
    mounted () {
      this.getMenu()
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
</style>
