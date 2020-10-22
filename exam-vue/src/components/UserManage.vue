<template>
  <el-container>

    <el-header>
      <el-input v-model="queryInfo.loginName" @blur="getUserInfo" placeholder="搜索登录名"
                prefix-icon="el-icon-search"></el-input>
      <el-input v-model="queryInfo.trueName" @blur="getUserInfo" placeholder="搜索姓名" prefix-icon="el-icon-search"
                style="margin-left: 5px"></el-input>
      <el-button type="primary" style="margin-left: 5px" icon="el-icon-plus">添加</el-button>
    </el-header>

    <el-main>
      <el-table
        ref="multipleTable"
        highlight-current-row
        :border="true"
        :data="userInfo"
        tooltip-effect="dark"
        style="width: 100%"
        @selection-change="handleSelectionChange">

        <el-table-column align="center"
                         type="selection"
                         width="55">
        </el-table-column>

        <el-table-column align="center"
                         prop="username"
                         label="用户名">
        </el-table-column>

        <el-table-column align="center"
                         prop="trueName"
                         label="姓名">
        </el-table-column>

        <el-table-column align="center"
                         label="角色">
          <template slot-scope="scope">
            <span class="role" v-show="scope.row.roleId === 3">超级管理员</span>
            <span class="role" v-show="scope.row.roleId === 2">教师</span>
            <span class="role" v-show="scope.row.roleId === 1">学生</span>
          </template>
        </el-table-column>

        <el-table-column align="center"
                         label="创建时间">
          <template slot-scope="scope">
            {{ scope.row.createDate }}
          </template>
        </el-table-column>

        <el-table-column align="center"
                         label="状态">
          <template slot-scope="scope">
            {{ scope.row.status === 1 ? '正常' : '禁用' }}
          </template>
        </el-table-column>

      </el-table>

    </el-main>
  </el-container>

</template>

<script>
  export default {
    name: 'UserManage',
    data () {
      return {
        //查询用户的参数
        queryInfo: {
          'loginName': '',
          'trueName': '',
          'pageNo': 1,
          'pageSize': 10
        },
        //用户信息
        userInfo: []
      }
    },
    created () {
      this.getUserInfo()
    },
    methods: {
      //获取用户信息
      getUserInfo () {
        this.$http.get(this.API.getUserInfo, { params: this.queryInfo }).then((resp) => {
          if (resp.data.code === 200) {
            this.userInfo = resp.data.data
          } else {
            this.$notify({
              title: 'Tips',
              message: '获取信息失败',
              type: 'error',
              duration: 2000
            })
          }
        })
      },
      //表格某一行被选中
      handleSelectionChange(row) {
        console.log(row)
        // this.multipleSelection = val;
      }
    }
  }
</script>

<style scoped lang="scss">
  .el-container {
    width: 100%;
    height: 100%;
  }

  .el-input {
    width: 200px;
  }

  .el-container {
    animation: leftMoveIn .7s ease-in;
  }

  @keyframes leftMoveIn {
    0% {
      transform: translateX(-100%);
      opacity: 0;
    }
    100% {
      transform: translateX(0%);
      opacity: 1;
    }
  }

  .role {
    color: #606266;
  }

  /deep/ .el-table thead {
    color: rgb(85, 85, 85) !important;
  }

  /*表格的头部样式*/
  /deep/ .has-gutter tr th {
    background: rgb(242, 243, 244);
    color: rgb(85, 85, 85);
    font-weight: bold;
    line-height: 32px;
  }

  .el-table{
    box-shadow: 0 0 1px 1px gainsboro;
  }
</style>
