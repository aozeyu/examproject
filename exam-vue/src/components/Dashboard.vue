<template>
  <div>
    <p>
      欢迎使用追风考试系统
    </p>
    预设为项目参考文档
  </div>
</template>

<script>
  export default {
    name: 'Dashboard'
  }
</script>

<style scoped lang="scss">
  div {
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
