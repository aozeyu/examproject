<template>
  <el-container>
    <el-header>
      <el-input v-model="queryInfo.bankName" @blur="contentChange" placeholder="题库内容"
                style="margin-left: 5px;width: 220px"
                prefix-icon="el-icon-search"></el-input>
    </el-header>

    <el-main>
      <!--操作的下拉框-->
      <el-select @change="operationChange" clearable v-if="selectedTable.length !== 0" v-model="operation"
                 :placeholder="'已选择' + selectedTable.length + '项'" style="margin-bottom: 25px;">

        <el-option value="delete">
          <span style="float: left">删除</span>
          <span style="float: right; color: #8492a6; font-size: 13px">delete</span>
        </el-option>

      </el-select>

      <el-table
        ref="questionTable"
        highlight-current-row
        v-loading="loading"
        :border="true"
        :data="questionBankInfo"
        tooltip-effect="dark"
        style="width: 100%;" @selection-change="handleTableSectionChange">

        <el-table-column align="center"
                         type="selection"
                         width="55">
        </el-table-column>

        <el-table-column align="center"
                         prop="questionBank.bankName"
                         label="题库名称">
        </el-table-column>

        <el-table-column align="center"
                         prop="questionBank.bankName"
                         label="单选题数量">
        </el-table-column>

        <el-table-column align="center"
                         prop="multipleChoice"
                         label="多选题数量">
        </el-table-column>

        <el-table-column align="center"
                         prop="judge"
                         label="判断题数量">
        </el-table-column>

        <el-table-column align="center"
                         prop="shortAnswer"
                         label="简答题数量">
        </el-table-column>

      </el-table>
      <!--分页-->
      <el-pagination style="margin-top: 25px"
                     @size-change="handleSizeChange"
                     @current-change="handleCurrentChange"
                     :current-page="queryInfo.pageNo"
                     :page-sizes="[10, 2, 30, 50]"
                     :page-size="queryInfo.pageSize"
                     layout="total, sizes, prev, pager, next, jumper"
                     :total="total">
      </el-pagination>
    </el-main>
  </el-container>
</template>

<script>
  export default {
    name: 'QuestionBankManage',
    data () {
      return {
        queryInfo: {
          bankName: '',
          pageNo: 1,
          pageSize: 10
        },
        //被选中的表格的信息
        selectedTable: [],
        //所有题库信息
        questionBankInfo: [],
        //当前被选中的操作
        operation: '',
        loading: true,
        //所有的题库条数
        total: 0,
      }
    },
    created () {
      this.getBankInfo()
      this.getBankTotal()
    },
    methods: {
      //获取所有的题库信息
      getBankInfo () {
        this.$http.get(this.API.getBankHaveQuestionSumByType, { params: this.queryInfo }).then((resp) => {
          if (resp.data.code === 200) {
            this.questionBankInfo = resp.data.data
            this.loading = false
          } else {
            this.$notify({
              title: 'Tips',
              message: resp.data.message,
              type: 'error',
              duration: 2000
            })
          }
        })
      },
      //查询内容变化
      contentChange () {
        this.getBankInfo()
        this.getBankTotal()
      },
      //操作选项的被触发
      operationChange (val) {
        console.log(val)
      },
      //表格部分行被选中
      handleTableSectionChange (row) {
        this.selectedTable = row
      },
      //分页插件的大小改变
      handleSizeChange (val) {
        this.queryInfo.pageSize = val
        this.getBankInfo()
      },
      //分页插件的页数
      handleCurrentChange (val) {
        this.queryInfo.pageNo = val
        this.getBankInfo()
      },
      //获取总数量
      getBankTotal () {
        this.$http.get(this.API.getBankHaveQuestionSumByType, {
          params: {
            'pageNo': 1,
            'pageSize': 9999,
            'bankname': ''
          }
        }).then((resp) => {
          if (resp.data.code === 200){
            this.total = resp.data.data.length
          }else {
            this.$notify({
              title: 'Tips',
              message: resp.data.message,
              type: 'error',
              duration: 2000
            })
          }
        })
      },
    }
  }
</script>

<style scoped>

</style>
