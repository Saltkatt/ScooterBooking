<template>
  <div id="app">
    <img alt="Vue logo" src="./assets/logo.png">
    <div class="contents">
      <router-view/>
    </div>
  </div>
</template>

<script>
  import Home from './components/Home.vue'
  export default {
    name: 'app',
    components: {
      Home,
    }
  }
</script>

<style>
  #app {
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
    margin-top: 60px;
  }
</style>