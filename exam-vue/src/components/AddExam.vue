<template>
  <el-container>
    <el-header height="220">
      <el-steps :active="curStep" simple>
        <el-step title="组卷配置" icon="el-icon-edit"></el-step>
        <el-step title="考试权限" icon="el-icon-lock"></el-step>
        <el-step title="补充配置" icon="el-icon-setting"></el-step>
      </el-steps>

      <el-button style="margin-top: 10px" v-show="curStep !== 1" @click="curStep--">上一步</el-button>

      <el-button style="float:right;margin-top: 10px" v-show="curStep !== 3" type="primary" @click="curStep++">下一步
      </el-button>
      <el-button style="float:right;margin-top: 10px" v-show="curStep === 3" type="primary">提交</el-button>
    </el-header>

    <el-main>
      <!--设置试题信息-->
      <el-card v-show="curStep === 1">

        <el-radio-group v-model="makeModel" size="medium">
          <el-radio :label="1" border>题库组卷</el-radio>
          <el-radio :label="2" border>自由组卷</el-radio>
        </el-radio-group>

        <span style="float: right;color: red;font-weight: bold">{{ '试卷总分：' + sumTotalScore }}</span>

        <!-- 题库组卷内容-->
        <div v-show="makeModel === 1" style="margin-top: 25px">
          <el-button icon="el-icon-plus" size="mini" @click="addBank">添加题库</el-button>

          <!--存放答案表单的信息-->
          <el-table :data="addExamQuestion" border style="margin-top: 10px">

            <el-table-column label="题库" width="155" align="center">
              <template slot-scope="scope">
                <el-select clearable v-model="scope.row.bankId" placeholder="请选择题库"
                           style="margin-left: 5px">
                  <el-option
                    v-for="item in allBank"
                    :key="item.questionBank.bankId"
                    :label="item.questionBank.bankName"
                    :value="item.questionBank.bankId">
                  </el-option>
                </el-select>
              </template>
            </el-table-column>

            <el-table-column label="单选题分数" align="center">
              <template slot-scope="scope">
                <el-input v-model="scope.row.singleScore" style="margin-left: 5px"></el-input>
              </template>
            </el-table-column>

            <el-table-column label="多选题分数" align="center">
              <template slot-scope="scope">
                <el-input v-model="scope.row.multipleScore" style="margin-left: 5px"></el-input>
              </template>
            </el-table-column>

            <el-table-column label="判断题分数" align="center">
              <template slot-scope="scope">
                <el-input v-model="scope.row.judgeScore" style="margin-left: 5px"></el-input>
              </template>
            </el-table-column>

            <el-table-column label="简答题分数" align="center">
              <template slot-scope="scope">
                <el-input v-model="scope.row.shortScore" style="margin-left: 5px"></el-input>
              </template>
            </el-table-column>

            <el-table-column label="操作" width="80" align="center">
              <template slot-scope="scope">
                <el-button type="danger" icon="el-icon-delete" circle @click="delBank(scope.row.bankId)"></el-button>
              </template>
            </el-table-column>

          </el-table>
        </div>

        <!-- 自由组卷内容-->
        <div v-show="makeModel === 2">

        </div>
      </el-card>
    </el-main>
  </el-container>
</template>

<script>
  export default {
    name: 'AddExam',
    data () {
      return {
        //当前的步骤
        curStep: 1,
        //组卷模式
        makeModel: 1,
        //添加考试题目信息
        addExamQuestion: [],
        //所有题库信息
        allBank: []
      }
    },
    props: ['tagInfo'],
    created () {
      //一创建就改变头部的面包屑
      this.$emit('giveChildChangeBreakInfo', '添加考试', '添加考试')
      this.createTagsInParent()
      this.getBankInfo()
    },
    methods: {
      //向父组件中添加头部的tags标签
      createTagsInParent () {
        let flag = false
        this.tagInfo.map(item => {
          //如果tags全部符合
          if (item.name === '添加考试' && item.url === this.$route.path) {
            flag = true
          } else if (item.name === '添加考试' && item.url !== this.$route.path) {
            this.$emit('updateTagInfo', '添加考试', this.$route.path)
            flag = true
          }
        })
        if (!flag) this.$emit('giveChildAddTag', '添加考试', this.$route.path)
      },
      //获取所有的题库信息
      getBankInfo () {
        this.$http.get(this.API.getBankHaveQuestionSumByType, {
          params: {
            'pageNo': 1,
            'pageSize': 9999
          }
        }).then((resp) => {
          if (resp.data.code === 200) {
            this.allBank = resp.data.data
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
      //删除当前需要去除的题库
      delBank (bankId) {
        this.addExamQuestion.forEach((item, index) => {
          if (item.bankId === bankId) this.addExamQuestion.splice(index, 1)
        })
      },
      //添加题库组卷中的题库
      addBank () {
        this.addExamQuestion.push(
          {
            'bankId': '',
            'singleScore': 0,
            'multipleScore': 0,
            'judgeScore': 0,
            'shortScore': 0
          })
      }
    },
    computed: {
      //计算总分
      sumTotalScore () {
        if (this.makeModel === 1){//题库组卷
          let score = 0
          this.addExamQuestion.forEach(item => {
            this.allBank.forEach(i2 => {
              if (item.bankId === i2.questionBank.bankId){
                score += i2.judge * item.judgeScore +
                  i2.multipleChoice * item.multipleScore +
                  i2.shortAnswer * item.shortScore +
                  i2.singleChoice * item.singleScore
              }
            })
          })
          return score
        }
      }
    }
  }
</script>

<style scoped lang="scss">
  .el-container {
    width: 100%;
    height: 100%;
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
</style>
