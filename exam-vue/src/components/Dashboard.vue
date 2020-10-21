<template>
  <div style="animation: leftMoveIn 3s infinite;">
    <p>欢迎使用追风考试系统</p>
    数据绘图暂留
  </div>
</template>

<script>
  export default {
    name: 'Dashboard'
  }
</script>

<style scoped lang="scss">
  @keyframes leftMoveIn {
    0% {
      right: 400px;
      background-color: red;
    }
    25% {
      right: 300px;
      background-color: blueviolet;

    }
    50% {
      right: 200px;
      background-color: yellow;
    }
    100% {
      right: 100px;
      background-color: gray;

    }
  }
</style>
