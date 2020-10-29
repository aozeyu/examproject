<template>
  <div>
    {{ $route.params }}
    {{tagInfo}}
  </div>
</template>

<script>
  export default {
    name: 'TrainPage',
    data () {
      return {
        //当前题库id
        bankId: this.$route.params.bankId,
        //当前训练类型(1顺序,2随机,3背题,4单选,5多选,6判断)
        trainType: this.$route.params.trainType
      }
    },
    props: ['tagInfo'],
    created () {
      //一创建就改变头部的面包屑
      this.$emit('giveChildChangeBreakInfo', '开始训练','在线考试')
      this.createTagsInParent()
      this.getQuestionInfo()
    },
    mounted () {
    },
    methods: {
      //向父组件中添加头部的tags标签
      createTagsInParent () {
        let flag = false
        this.tagInfo.map(item => {
          //如果tags全部符合
          if (item.name === '开始训练' && item.url === this.$route.path) {
            flag = true
          } else if (item.name === '开始训练' && item.url !== this.$route.path) {
            this.$emit('updateTagInfo', '开始训练', this.$route.path)
            flag = true
          }
        })
        if (!flag) this.$emit('giveChildAddTag', '开始训练', this.$route.path)
      },
      //获取题目顺序 并按照对应方式加载
      getQuestionInfo () {
        switch (parseInt(this.$route.params.trainType)) {
          case 1: {//顺序生成题目
            break
          }
          case 2:{
            break
          }
        }
      },
    }
  }
</script>

<style scoped>

</style>
