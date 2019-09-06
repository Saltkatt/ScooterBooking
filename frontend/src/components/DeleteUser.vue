<template>
    <div class="delete-user-container">
        <header>
            <!-- Navigation Bar -->
            <nav class="nav-bar">
                <ul>
                    <li><router-link to="/logout"><button class="btn">Logout</button></router-link></li>
                    <li><router-link to="/delete"><button class="btn">Delete</button></router-link></li>
                    <router-link to="/login">Home</router-link>
                    <li>Stats</li>
                    <li>Trips</li>

                </ul>
            </nav>
        </header>
        <div>
            <h1>Delete Account?</h1>
            <h3>If you want to delete your account with Wireless Scooter, contact admin with your userId, username and email.</h3>
            <ul>
                <li>Username: {{userInfo.username}}</li>
                <li>UserID: {{userInfo.userId}}</li>
                <li>Email: {{userInfo.email}}</li>
            </ul>

            <h3>Admin details: </h3>
            <ul>
                <li>email:</li>
            </ul>


        </div>


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
        text-align: center;
    }

    h1{
        align-content: center;
    }

    .btn{
        cursor: pointer;
    }


</style>