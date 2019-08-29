<template>
    <div class="delete-user-container">
        <div>
            <h1>Delete Account</h1>
        </div>
        <div class="alert alert-info">
            {{userInfo}}
        </div>
        <router-link to="/delete">
            Delete!!
        </router-link>


    </div>
</template>

<script>
    import UserInfoStore from '../app/user-info-store';
    export default {
        name: "DeleteUser",
        data: function() {
            return{
                userInfo: UserInfoStore.state.cognitoInfo
            }
        }
    }
</script>

<style scoped>
    .delete-user-container{
        background-color: blue;
        font-family: 'Courier New', Courier, monospace;
        text-align: center;
    }

    h1{
        align-content: center;
    }


</style>