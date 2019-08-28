<template>
  <div class="home">
    <HelloWorld msg="Welcome to Your Vue.js App" />
    <Auth></Auth>
    <DeleteUserComponent></DeleteUserComponent>
  </div>
</template>

<script>
// @ is an alias to /src
import HelloWorld from "@/components/HelloWorld.vue";
import Auth from "@/components/Auth";
import DeleteUserComponent from "@/components/DeleteUserComponent";

export default {
  name: "home",
  components: {
    HelloWorld,
    Auth,
    DeleteUserComponent
  }
};
</script>
