<template>
  <el-container>

    <el-header height="220">
      <el-row>
        <el-select @change="typeChange" clearable v-model="queryInfo.questionType" placeholder="请选择题目类型">
          <el-option
            v-for="item in questionType"
            :key="item.id"
            :label="item.name"
            :value="item.id">
          </el-option>
        </el-select>

        <el-select @change="bankChange" clearable v-model="queryInfo.questionBank" placeholder="请选择题库" style="margin-left: 5px">
          <el-option
            v-for="item in allBank"
            :key="item.id"
            :label="item.bankName"
            :value="item.bankName">
          </el-option>
        </el-select>

        <el-input v-model="queryInfo.questionContent" @blur="contentChange" placeholder="题目内容"
                  style="margin-left: 5px;width: 220px"
                  prefix-icon="el-icon-search"></el-input>
      </el-row>

      <el-row style="margin-top: 10px">
        <el-button type="primary" icon="el-icon-plus" @click="addQuTableVisible = true">添加</el-button>
      </el-row>
    </el-header>

    <el-main>
      <!--操作的下拉框-->
      <el-select @change="operationChange" clearable v-if="selectionTable.length !== 0" v-model="operation"
                 :placeholder="'已选择' + selectionTable.length + '项'" style="margin-bottom: 25px;">

        <el-option v-for="(item,index) in optionInfo" :key="index" :value="item.desc">
          <span style="float: left">{{ item.label }}</span>
          <span style="float: right; color: #8492a6; font-size: 13px">{{ item.desc }}</span>
        </el-option>

      </el-select>

      <el-table
        ref="questionTable"
        highlight-current-row
        v-loading="loading"
        :border="true"
        :data="questionInfo"
        tooltip-effect="dark"
        style="width: 100%;" @selection-change="handleTableSectionChange">

        <el-table-column align="center"
                         type="selection"
                         width="55">
        </el-table-column>

        <el-table-column align="center"
                         label="题目类型">
          <template slot-scope="scope">
            <span v-if="scope.row.quType === 1">单选题</span>
            <span v-else-if="scope.row.quType === 2">多选题</span>
            <span v-else-if="scope.row.quType === 3">判断题</span>
            <span v-else-if="scope.row.quType === 4">简答题</span>
          </template>
        </el-table-column>

        <el-table-column align="center"
                         prop="quContent"
                         label="题目内容">
        </el-table-column>

        <el-table-column align="center"
                         label="难度">
          <template slot-scope="scope">
            <span v-if="scope.row.level === 1">简单</span>
            <span v-if="scope.row.level === 2">中等</span>
            <span v-if="scope.row.level === 3">困难</span>
          </template>
        </el-table-column>

        <el-table-column align="center"
                         prop="quBankName"
                         label="所属题库">
        </el-table-column>

        <el-table-column align="center"
                         prop="createPerson"
                         label="创建人">
        </el-table-column>

        <el-table-column align="center"
                         label="创建时间">
          <template slot-scope="scope">
            {{ scope.row.createTime }}
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

    <el-dialog title="新增题目" :visible.sync="addQuTableVisible" width="30%" @close="resetAddQuForm"
               center>


      <div slot="footer" class="dialog-footer">
        <el-button @click="addQuTableVisible = false">取 消</el-button>
        <el-button type="primary">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="加入题库" :visible.sync="addTableVisible" width="30%" @close="resetAddForm"
               center>

      <el-form :model="addForm" :rules="addFormRules" ref="addForm">

        <el-form-item label="题库名称" label-width="120px" prop="bankId">

          <el-select multiple v-model="addForm.bankId" placeholder="请选择题库">
            <el-option v-for="item in allBank" :key="item.bankId"
                       :label="item.bankName" :value="item.bankId"></el-option>
          </el-select>

        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="addTableVisible = false">取 消</el-button>
        <el-button type="primary" @click="addBank">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="从题库中移除" :visible.sync="removeTableVisible" width="30%" @close="resetRemoveForm"
               center>

      <el-form :model="removeForm" :rules="removeFormRules" ref="removeForm">

        <el-form-item label="题库名称" label-width="120px" prop="bankId">

          <el-select multiple v-model="removeForm.bankId" placeholder="请选择题库">

            <el-option v-for="item in allBank" :key="item.bankId"
                       :label="item.bankName" :value="item.bankId"></el-option>
          </el-select>

        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="removeTableVisible = false">取 消</el-button>
        <el-button type="primary" @click="removeBank">确 定</el-button>
      </div>
    </el-dialog>
  </el-container>

</template>

<script>
  export default {
    name: 'QuestionManage',
    data () {
      return {
        //查询用户的参数
        queryInfo: {
          //题目类型下拉款所选的内容
          'questionType': '',
          'questionBank': '',
          'questionContent': '',
          'pageNo': 1,
          'pageSize': 10
        },
        //题目类型
        questionType: [
          {
            id: 1,
            name: '单选题',
          },
          {
            id: 2,
            name: '多选题',
          },
          {
            id: 3,
            name: '判断题',
          },
          {
            id: 4,
            name: '简答题',
          },
        ],
        //题库信息
        allBank: [],
        //题目信息
        questionInfo: [],
        //题目信息表格是否加载
        loading: true,
        //表格被选中的所有行
        selectionTable: [],
        //表格行被选中后的所有操作方式的数据
        optionInfo: [
          {
            'label': '删除',
            'desc': 'delete'
          },
          {
            'label': '加入题库',
            'desc': 'add'
          },
          {
            'label': '题库中移除',
            'desc': 'remove'
          }
        ],
        //表格行被选中后的数据
        operation: '',
        //题目总数
        total: 0,
        //是否显示加入题库对话框
        addTableVisible: false,
        //是否显示移除题库对话框
        removeTableVisible: false,
        //是否显示添加题目的对话框
        addQuTableVisible: false,
        //添加题库的表单信息
        addForm: {
          bankId: ''
        },
        removeForm: {
          bankId: ''
        },
        //添加题库表单的验证
        addFormRules: {
          bankId: [
            {
              required: true,
              message: '请选择需要添加进的题库',
              trigger: 'blur'
            }
          ]
        },
        //移除题库表单的验证
        removeFormRules: {
          bankId: [
            {
              required: true,
              message: '请选择需要移除的题库',
              trigger: 'blur'
            }
          ]
        },

      }
    },
    created () {
      this.getQuestionBankInfo()
      this.getQuestionInfo()
      this.getQuestionTotal()
    },
    methods: {
      //获取所有的题库信息
      getQuestionBankInfo () {
        this.$http.get(this.API.getQuestionBank).then((resp) => {
          if (resp.data.code === 200) {
            this.allBank = resp.data.data
          } else {
            this.$notify({
              title: 'Tips',
              message: '获取题库信息失败',
              type: 'error',
              duration: 2000
            })
          }
        })
      },
      //题目类型变化
      typeChange (val) {
        this.queryInfo.questionType = val
        this.getQuestionInfo()
      },
      //题库变化
      bankChange (val) {
        this.queryInfo.questionBank = val
        this.getQuestionInfo()

      },
      //题目名字筛选
      contentChange () {
        //发送查询题目总数的请求
        this.getQuestionInfo()
      },
      //获取题目信息
      getQuestionInfo () {
        this.$http.get(this.API.getQuestion, { params: this.queryInfo }).then((resp) => {
          if (resp.data.code === 200) {
            this.questionInfo = resp.data.data
            this.loading = false
          } else {
            this.$notify({
              title: 'Tips',
              message: '获取题库信息失败',
              type: 'error',
              duration: 2000
            })
          }
        })
      },
      //处理表格被选中
      handleTableSectionChange (val) {
        this.selectionTable = val
      },
      //处理操作选择框的变化
      operationChange (val) {
        //清空上一次的选择
        this.operation = ''

        let questionIds = []
        if (val === 'delete') {
          this.selectionTable.map(item => {
            questionIds.push(item.id)
          })
          //发起删除请求
          this.$http.get(this.API.deleteQuestion, { params: { 'questionIds': questionIds.join(',') } }).then(resp => {
            if (resp.data.code === 200) {
              this.$notify({
                title: 'Tips',
                message: '删除成功',
                type: 'success',
                duration: 2000
              })
              this.getQuestionInfo()
              this.getQuestionTotal()
            } else {
              this.$notify({
                title: 'Tips',
                message: '删除失败',
                type: 'error',
                duration: 2000
              })
            }
          })
        } else if (val === 'add') {
          this.addTableVisible = true
        } else if (val === 'remove') {
          this.removeTableVisible = true
        }
      },
      //分页页面大小改变
      handleSizeChange (val) {
        this.queryInfo.pageSize = val
        this.getQuestionInfo()
      },
      //分页插件的页数
      handleCurrentChange (val) {
        this.queryInfo.pageNo = val
        this.getQuestionInfo()
      },
      //查询所有题目的数据
      getQuestionTotal () {
        this.$http.get(this.API.getQuestion, {
          params: {
            //题目类型下拉款所选的内容
            'questionType': '',
            'questionBank': '',
            'questionContent': '',
            'pageNo': 1,
            'pageSize': 9999
          }
        }).then((resp) => {
          if (resp.data.code === 200) {
            this.total = resp.data.data.length
          } else {
            this.$notify({
              title: 'Tips',
              message: '获取题目失败',
              type: 'error',
              duration: 2000
            })
          }
        })
      },
      //表单信息重置
      resetAddForm () {
        //清空表格数据
        this.$refs['addForm'].resetFields()
      },
      resetRemoveForm () {
        //清空表格数据
        this.$refs['removeForm'].resetFields()
      },
      resetAddQuForm () {
        this.$refs['addQuForm'].resetFields()
      },
      //提交加入题库的表单信息
      addBank () {
        this.$refs['addForm'].validate((valid) => {
          if (valid) {
            let questionIds = []
            let banks = this.addForm.bankId
            //将表格选中的数据中的问题id加入进去
            this.selectionTable.map(item => {
              questionIds.push(item.id)
            })
            this.$http.get(this.API.addBankQuestion, {
              params: {
                'questionIds': questionIds.join(','),
                'banks': banks.join(',')
              }
            }).then((resp) => {
              if (resp.data.code === 200) {
                this.getQuestionInfo()
                this.$notify({
                  title: 'Tips',
                  message: resp.data.message,
                  type: 'success',
                  duration: 2000
                })
              } else {
                this.$notify({
                  title: 'Tips',
                  message: resp.data.message,
                  type: 'error',
                  duration: 2000
                })
              }
              this.addTableVisible = false
            })
          } else {
            this.$message.error('请选择需要加入进的题库')
            return false
          }
        })
      },
      //提交移除题库的表单信息
      removeBank () {
        this.$refs['removeForm'].validate((valid) => {
          if (valid) {
            let questionIds = []
            let banks = this.removeForm.bankId
            //将表格选中的数据中的问题id加入进去
            this.selectionTable.map(item => {
              questionIds.push(item.id)
            })
            //发起移除请求
            this.$http.get(this.API.removeBankQuestion, {
              params: {
                'questionIds': questionIds.join(','),
                'banks': banks.join(',')
              }
            }).then((resp) => {
              if (resp.data.code === 200) {
                this.getQuestionInfo()
                this.$notify({
                  title: 'Tips',
                  message: resp.data.message,
                  type: 'success',
                  duration: 2000
                })
              } else {
                this.$notify({
                  title: 'Tips',
                  message: resp.data.message,
                  type: 'error',
                  duration: 2000
                })
              }
              this.removeTableVisible = false
            })
          } else {
            this.$message.error('请选择需要移除的题库')
            return false
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
