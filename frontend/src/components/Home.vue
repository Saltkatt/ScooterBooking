<template>
  <div class="row">
    <div class="col">

      <header>
        <!-- Navigation Bar -->
        <nav class="nav-bar">
          <ul>
            <li><router-link to="/logout"><button class="btn">Logout</button></router-link></li>
            <li><router-link to="/delete"><button class="btn">Delete</button></router-link></li>
              <li>Stats</li>
              <li>Trips</li>
          </ul>
        </nav>

        <h1>Welcome {{userInfo.username}}</h1>
        <div class="alert alert-info">
          {{userInfo}}
        </div>

        </header>
      <div class="bookings">
        <div class="current-bookings">
          <div>{{jwtToken}}</div>

          UserInfoApi.getUserInfo()
          <h2>Current Bookings</h2>
        </div>

        <div class="expired-bookings">
          <h2>Expired Bookings</h2>
        </div>

      </div>

    </div>
  </div>
</template>
<script>
  import UserInfoStore from '../app/user-info-store';
  import UserInfoApi from '../app/user-info-api';
  export default {
    name: 'Home',
    data: function() {
      return{
        userInfo: UserInfoStore.state.cognitoInfo,
        jwtToken: UserInfoApi.getUserInfo().jwtToken
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
  .bookings {
    display: grid;
    grid-auto-columns: auto auto;
  }
  .current-bookings {
    grid-column: 1;
  }
  .expired-bookings {
    grid-column: 2;
  }


</style>
