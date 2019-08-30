<template>
  <div class="row">
    <div class="col">

      <header>
        <!-- Navigation Bar -->
        <nav class="nav-bar">
          <ul>
            <li><router-link to="/logout"><button class="btn">Logout</button></router-link></li>
            <!--<li><router-link to="/delete"><button class="btn">Delete</button></router-link></li>-->
              <li>Stats</li>
              <li>Trips</li>


          </ul>
        </nav>

        <h1>Welcome {{userInfo.username}}</h1>
        <div class="alert alert-info">
          {{userInfo.email}}
        </div>

        </header>

        <div class="current-bookings">
            <h2>Current Bookings</h2>
        </div>

        <div class="expired-bookings">
            <h2>Expired Bookings</h2>
        </div>



    </div>
  </div>
</template>
<script>
  import UserInfoStore from '../app/user-info-store';
  export default {
    name: 'Home',
    data: function() {
      return{
        userInfo: UserInfoStore.state.cognitoInfo
      }
    }
  }
</script>
<style>
  .nav-bar{
    background-color: azure;

  }
  ul{
    list-style-type: none;
    margin: 0;
    padding: 0;
    overflow: hidden;
  }
  li{
    display: inline;
    margin: 5%;
  }
  .btn{
    cursor: pointer;
      margin-right: 5px;
      float: right;
  }

  .alert{
      color: black;
  }


</style>
