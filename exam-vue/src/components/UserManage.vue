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
      <!--操作的下拉框-->
      <el-select @change="selectChange" v-if="selectedInTable.length !== 0" v-model="selected"
                 :placeholder="'已选择' + selectedInTable.length + '项'" style="margin-bottom: 25px;">

        <el-option v-for="(item,index) in optionInfo" :key="index" :value="item.desc">
          <span style="float: left">{{ item.label }}</span>
          <span style="float: right; color: #8492a6; font-size: 13px">{{ item.desc }}</span>
        </el-option>

      </el-select>

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

      <!--分页-->
      <el-pagination style="margin-top: 25px"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="queryInfo.pageNo"
        :page-sizes="[10, 20, 30, 50]"
        :page-size="queryInfo.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
      </el-pagination>

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
        userInfo: [],
        //下拉选择框的数据
        optionInfo: [
          {
            'label': '启用',
            'desc': 'on'
          },
          {
            'label': '禁用',
            'desc': 'off'
          },
          {
            'label': '删除',
            'desc': 'delete'
          }
        ],
        //下拉框所选择的数据
        selected: '',
        //被选择的表格中的行数据
        selectedInTable: [],
        //所有用户的条数
        total: 0,
      }
    },
    created () {
      this.getUserInfo()
      //获取用户总数
      this.getUserTotal()
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
      handleSelectionChange (val) {
        this.selectedInTable = val
      },
      //功能下拉框被选择
      selectChange (val) {
        //表格中所选中的用户的id
        let userIds = []
        this.selectedInTable.map(item => {
          userIds.push(item.id)
        })
        if (val === 'on') {//状态设置为正常
          this.$http.get(this.API.handleUser + '/' + 1, { params: { 'userIds': userIds.join(',') } }).then((resp) => {
            if (resp.data.code === 200) {
              //删除成功后,回调更新用户数据
              this.getUserInfo()
              this.$notify({
                title: 'Tips',
                message: '操作成功',
                type: 'success',
                duration: 2000
              })
            } else {
              this.$notify({
                title: 'Tips',
                message: '操作失败',
                type: 'error',
                duration: 2000
              })
            }
          })
        } else if (val === 'off') {//禁用用户
          this.$http.get(this.API.handleUser + '/' + 2, { params: { 'userIds': userIds.join(',') } }).then((resp) => {
            if (resp.data.code === 200) {
              //删除成功后,回调更新用户数据
              this.getUserInfo()
              this.$notify({
                title: 'Tips',
                message: '操作成功',
                type: 'success',
                duration: 2000
              })
            } else {
              this.$notify({
                title: 'Tips',
                message: '操作失败',
                type: 'error',
                duration: 2000
              })
            }
          })
        } else if (val === 'delete') {//删除用户
          this.$http.get(this.API.handleUser + '/' + 3, { params: { 'userIds': userIds.join(',') } }).then((resp) => {
            if (resp.data.code === 200) {
              //删除成功后,回调更新用户数据
              this.getUserInfo()
              this.$notify({
                title: 'Tips',
                message: '操作成功',
                type: 'success',
                duration: 2000
              })
            } else {
              this.$notify({
                title: 'Tips',
                message: '操作失败',
                type: 'error',
                duration: 2000
              })
            }
          })
        }
      },
      //分页插件的页数改变
      handleSizeChange (val) {
        this.queryInfo.pageSize = val
        this.getUserInfo()
      },
      //分页插件的页面大小
      handleCurrentChange (val) {
        this.queryInfo.pageNo = val
        this.getUserInfo()
      },
      //查询所有用户的数据
      getUserTotal () {
        this.$http.get(this.API.getUserInfo, {
          params: {
            'loginName': '',
            'trueName': '',
            'pageNo': 1,
            'pageSize': 9999
          }
        }).then((resp) => {
          if (resp.data.code === 200) {
            this.total = resp.data.data.length
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

  .el-table {
    box-shadow: 0 0 1px 1px gainsboro;
  }
</style>
