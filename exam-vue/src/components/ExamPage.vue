<template>

  <el-container v-if="show">
    <el-header style="margin-top: 60px">
      <el-row>
        <el-col :span="18" :offset="3" style="border-bottom: 1px solid #f5f5f5">
          <span class="startExam">开始考试</span>
          <span class="examTitle">距离考试结束还有：</span>
          <el-button type="warning" round
                     style="background-color: #ffd550;float: right;color: black;font-weight: 800">提交试卷
          </el-button>
        </el-col>
      </el-row>
    </el-header>

    <el-main>
      <el-row>
        <el-col :span="18" :offset="3">
          <el-col :span="16" >
            <el-card style="min-height: 500px">
              <!--题目信息-->
              <div>
                <i class="num">{{ curIndex+1 }}</i>
                <span v-if="questionInfo[curIndex].questionType === 1">【单选题】</span>
                <span v-else-if="questionInfo[curIndex].questionType === 2">【多选题】</span>
                <span v-else-if="questionInfo[curIndex].questionType === 3">【判断题】</span>
                <span v-else>【简答题】</span>
                <span>{{ questionInfo[curIndex].questionContent}}:</span>
              </div>
              <!--题目中的配图-->
              <img v-for="url in questionInfo[curIndex].images" :src="url" title="点击查看大图" alt="题目图片"
                   style="width: 100px;height: 100px;cursor: pointer" @click="showBigImg(url)">

              <!--单选 和 判断 的答案列表-->
              <div style="margin-top: 25px"
                   v-show="questionInfo[curIndex].questionType === 1 || questionInfo[curIndex].questionType === 3">
                <div class="el-radio-group">
                  <label v-for="(item,index) in questionInfo[curIndex].answer"
                         @click="checkSingleAnswer(index)"
                         :class="index === userAnswer[curIndex] ? 'active' : ''">
                    <span>{{ optionName[index] + '、' + item.answer}}</span>
                    <img style="position: absolute;left:100%;top:50%;transform: translateY(-50%);
                  width: 40px;height: 40px;float: right;cursor: pointer;" title="点击查看大图"
                         v-if="item.images !== null"
                         v-for="i2 in item.images" :src="i2" alt="" @mouseover="showBigImg(i2)">
                  </label>
                </div>
              </div>

              <!--多选的答案列表-->
              <div style="margin-top: 25px" v-show="questionInfo[curIndex].questionType === 2">
                <div class="el-radio-group">
                  <label v-for="(item,index) in questionInfo[curIndex].answer"
                         @click="selectedMultipleAnswer(index)"
                         :class="(userAnswer[curIndex]+'').indexOf(index+'') !== -1? 'active' : ''">
                    <span>{{ optionName[index] + '、' + item.answer}}</span>
                    <img style="position: absolute;left:100%;top:50%;transform: translateY(-50%);
                  width: 40px;height: 40px;float: right;cursor: pointer;" title="点击查看大图"
                         v-if="item.images !== null"
                         v-for="i2 in item.images" :src="i2" alt="" @mouseover="showBigImg(i2)">
                  </label>
                </div>
              </div>

              <!--简答题的答案-->
              <div style="margin-top: 25px" v-show="questionInfo[curIndex].questionType === 4">
                <el-input
                  type="textarea"
                  :rows="8"
                  placeholder="请输入答案"
                  v-model="userAnswer[curIndex]">
                </el-input>
              </div>

              <!--上一题 下一题-->
              <div style="margin-top: 25px">
                <el-button type="primary" icon="el-icon-back" :disabled="curIndex<1" @click="curIndex--">上一题</el-button>
                <el-button type="primary" icon="el-icon-right" :disabled="curIndex>=questionInfo.length-1"
                           @click="curIndex++">下一题
                </el-button>
              </div>

            </el-card>
          </el-col>

          <el-col :span="7" :offset="1">
            <!--答题卡卡片-->
            <el-card>
              <div>
                <p style="font-size: 18px;">答题卡</p>
                <div style="margin-top: 25px">
                  <span
                    style="background-color: rgb(238,238,238);padding: 5px 10px 5px 10px;margin-left: 15px">未作答</span>
                  <span style="background-color: rgb(87,148,247);color: white;
                padding: 5px 10px 5px 10px;margin-left: 15px">已作答</span>
                </div>
              </div>

              <!--单选的答题卡-->
              <div style="margin-top: 25px">
                <p style="font-size: 18px;">单选题</p>
                <el-button style="margin-top: 10px;margin-left: 15px" size="mini"
                           v-show="questionInfo[item-1].questionType === 1"
                           :class="questionInfo[item-1].questionType === 1 && userAnswer[item-1] !== undefined ?
                            'done' : userAnswer[item-1] === undefined ? curIndex === (item-1) ? 'orange' : 'noAnswer' : 'noAnswer'"
                           v-for="item in questionInfo.length" :key="item" @click="curIndex = item-1">{{ item }}
                </el-button>
              </div>

              <!--多选的答题卡-->
              <div style="margin-top: 25px">
                <p style="font-size: 18px;">多选题</p>
                <el-button style="margin-top: 10px;margin-left: 15px" size="mini"
                           v-show="questionInfo[item-1].questionType === 2"
                           :class="questionInfo[item-1].questionType === 2 && userAnswer[item-1] !== undefined ?
                            'done' : userAnswer[item-1] === undefined ? curIndex === (item-1) ? 'orange' : 'noAnswer' : 'noAnswer'"
                           v-for="item in questionInfo.length" :key="item" @click="curIndex = item-1">{{ item }}
                </el-button>
              </div>

              <!--判断的答题卡-->
              <div style="margin-top: 25px">
                <p style="font-size: 18px;">判断题</p>
                <el-button style="margin-top: 10px;margin-left: 15px" size="mini"
                           v-show="questionInfo[item-1].questionType === 3"
                           :class="questionInfo[item-1].questionType === 3 && userAnswer[item-1] !== undefined ?
                            'done' : userAnswer[item-1] === undefined ? curIndex === (item-1) ? 'orange' : 'noAnswer' : 'noAnswer'"
                           v-for="item in questionInfo.length" :key="item" @click="curIndex = item-1">{{ item }}
                </el-button>
              </div>

              <!--简答的答题卡-->
              <div style="margin-top: 25px">
                <p style="font-size: 18px;">简答题</p>
                <el-button style="margin-top: 10px;margin-left: 15px" size="mini"
                           v-show="questionInfo[item-1].questionType === 4"
                           :class="questionInfo[item-1].questionType === 4 && userAnswer[item-1] !== undefined ?
                            'done' : userAnswer[item-1] === undefined ? curIndex === (item-1) ? 'orange' : 'noAnswer' : 'noAnswer'"
                           v-for="item in questionInfo.length" :key="item" @click="curIndex = item-1">{{ item }}
                </el-button>
              </div>
            </el-card>
          </el-col>

        </el-col>
      </el-row>
    </el-main>
    <!--图片回显-->
    <el-dialog :visible.sync="bigImgDialog" @close="bigImgDialog = false">
      <img style="width: 100%" :src="bigImgUrl">
    </el-dialog>
  </el-container>

</template>

<script>

  export default {
    name: 'ExamPage',
    data () {
      return {
        //当前考试的信息
        examInfo: {},
        //当前的考试题目
        questionInfo: [
          {
            questionType: ''
          }
        ],
        //当前题目的索引值
        curIndex: 0,
        //控制大图的对话框
        bigImgDialog: false,
        //当前要展示的大图的url
        bigImgUrl: '',
        //用户选择的答案
        userAnswer: [],
        //页面数据加载
        loading: {},
        //页面绘制是否开始
        show: false,
        //答案的选项名abcd数据
        optionName: ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'],
      }
    },
    created () {
      this.getExamInfo()
      //页面数据加载
      this.loading = this.$Loading.service({
        body: true,
        lock: true,
        text: '数据拼命加载中,(*╹▽╹*)',
        spinner: 'el-icon-loading',
      })
    },
    methods: {
      //查询当前考试的信息
      getExamInfo () {
        this.$http.get(this.API.getExamInfoById, { params: this.$route.params }).then((resp) => {
          console.log(resp)
          if (resp.data.code === 200) {
            this.examInfo = resp.data.data
            //设置定时

            this.getQuestionInfo(resp.data.data.questionIds.split(','))
          }
        })
      },
      //查询考试的题目信息
      getQuestionInfo (ids) {
        ids.forEach((item, index) => {
          this.$http.get(this.API.getQuestionById + '/' + item).then((resp) => {
            if (index === 0) this.questionInfo = []
            if (resp.data.code === 200) {
              this.questionInfo.push(resp.data.data)
            }
          })
        })
        this.loading.close()
        this.show = true
      },
      //点击展示高清大图
      showBigImg (url) {
        this.bigImgUrl = url
        this.bigImgDialog = true
      },
      //检验单选题的用户选择的答案
      checkSingleAnswer (index) {
        this.$set(this.userAnswer, this.curIndex, index)
      },
      //多选题用户的答案选中
      selectedMultipleAnswer (index) {
        if (this.userAnswer[this.curIndex] === undefined) {//当前是多选的第一个答案
          this.$set(this.userAnswer, this.curIndex, index)
        } else if (String(this.userAnswer[this.curIndex]).split(',').includes(index + '')) {//取消选中
          let newArr = []
          String(this.userAnswer[this.curIndex]).split(',').forEach(item => {
            if (item !== '' + index) newArr.push(item)
          })
          if (newArr.length === 0) {
            this.$set(this.userAnswer, this.curIndex, undefined)
          } else {
            this.$set(this.userAnswer, this.curIndex, newArr.join(','))
            //答案格式化顺序DBAC -> ABCD
            this.userAnswer[this.curIndex] = String(this.userAnswer[this.curIndex]).split(',').sort(function (a, b) {
              return a - b
            }).join(',')
          }
        } else if (!((this.userAnswer[this.curIndex] + '').split(',').includes(index + ''))) {//第n个答案
          this.$set(this.userAnswer, this.curIndex, this.userAnswer[this.curIndex] += ',' + index)
          //答案格式化顺序DBAC -> ABCD
          this.userAnswer[this.curIndex] = String(this.userAnswer[this.curIndex]).split(',').sort(function (a, b) {
            return a - b
          }).join(',')
        }
      },
    },
  }
</script>

<style scoped lang="scss">
  * {
    font-weight: 800;
  }

  .el-container {
    width: 100%;
    height: 100%;
  }

  .startExam {
    color: #160f58;
    border-bottom: 4px solid #ffd550;
    font-size: 18px;
    font-weight: 700;
    padding-bottom: 10px
  }

  .examTitle {
    font-size: 18px;
    color: #cbcacf;
    margin-left: 20px;
    font-weight: 700;
  }

  .el-radio-group label {
    display: block;
    width: 400px;
    padding: 48px 20px 10px 20px;
    border-radius: 4px;
    border: 1px solid #dcdfe6;
    margin-bottom: 10px;
    cursor: pointer;
    position: relative;

    span {
      position: absolute;
      top: 50%;
      transform: translateY(-50%);
      font-size: 16px;
    }
  }

  .el-radio-group label:hover {
    background-color: rgb(245, 247, 250);
  }

  /*当前选中的答案*/
  .active {
    border: 1px solid #1f90ff !important;
    opacity: .5;
  }

  /*做过的题目的高亮颜色*/
  .done {
    background-color: rgb(87, 148, 247);
  }

  /*未做题目的高亮颜色*/
  .noAnswer {
    background-color: rgb(238, 238, 238);
  }

  /*当前在做的题目高亮的颜色*/
  .orange {
    background-color: rgb(255, 213, 80);
  }

  .num{
    display: inline-block;
    background: url('../assets/imgs/examTitle.png') no-repeat 100% 100%;
    background-size: contain;
    height: 37px;
    width: 37px;
    line-height: 30px;
    color: #fff;
    font-size: 20px;
    text-align: center;
    margin-right: 15px;
  }
</style>
