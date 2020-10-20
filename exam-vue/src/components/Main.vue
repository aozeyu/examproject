<template>
  <el-container>
    <el-aside width="210px">
      <!--      <el-menu-->
      <!--        default-active="2"-->
      <!--        class="el-menu-vertical-demo"-->
      <!--        @open="handleOpen"-->
      <!--        @close="handleClose">-->
      <el-menu default-active="2"
               background-color="rgb(48,65,86)"
               text-color="rgb(191,203,217)"
               active-text-color="rgb(64,158,255)"
      >
        <el-menu-item index="1" style="text-align: center">
          <span slot="title">
            追风考试系统
          </span>
        </el-menu-item>
        <!-- 单独的导航 -->
        <el-menu-item index="1">
          <i :class="menuInfo[0].topIcon"></i>
          <span slot="title">{{ menuInfo[0].topMenuName }}</span>
        </el-menu-item>

        <!--具有子导航的-->
        <el-submenu index="2" v-if="menu.submenu" v-for="(menu,index) in menuInfo" :key="index">
          <template slot="title"><i :class="menu.topIcon"></i>{{ menu.topMenuName }}</template>
          <!--子导航的分组-->
          <el-menu-item-group>
            <el-menu-item v-for="(sub,index) in menu.submenu" :key="index">
              <i :class="sub.icon"></i>
              <span slot="title">{{ sub.name }}</span>
            </el-menu-item>
          </el-menu-item-group>

        </el-submenu>
      </el-menu>

    </el-aside>

    <el-main>{{menuInfo}}</el-main>
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
        ]
      }
    },
    created () {
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

    .el-menu-item:hover {
      background-color: rgb(38, 52, 69) !important;
    }
  }

  /deep/ .el-submenu__title:hover {
    background-color: rgb(38, 52, 69) !important;
  }
</style>
