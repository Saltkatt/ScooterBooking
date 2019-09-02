<template>
    <div class="delete-user-container">
        <div>
            <h1>Delete Account</h1>
        </div>

        <div class="alert alert-info">
            {{userInfo}}
        </div>

        <router-link to="/deleteUser"><button class="btn"> Delete </button></router-link>
        <router-link to="/login"><button class="btn"> Login </button></router-link>

    </div>
</template>

<script>
    import UserInfoStore from '../app/user-info-store';
    import DeleteUserInfo from '../app/delete-user-info';
    export default {
        name: "DeleteUser",
        data: function() {
            return{
                userInfo: UserInfoStore.state.cognitoInfo
            }
        },
       /* methods: {
            deleteUser: function(){
                this.$store.dispatch('removeUserFromCognito');
            }
        }*/



    }
</script>

<style scoped>
    .delete-user-container{
        background-color: limegreen;
        font-family: 'Courier New', Courier, monospace;
        text-align: center;
    }

    h1{
        align-content: center;
    }

    .btn{
        cursor: pointer;
    }


</style>