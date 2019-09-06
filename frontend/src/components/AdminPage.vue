<template>
    <div class="container">
        <header>
            <!-- Navigation Bar -->
            <nav class="nav-bar">
                <ul>
                    <li><router-link to="/logout"><button class="btn">Logout</button></router-link></li>

                    <li>Stats</li>
                    <li>Trips</li>
                    <li>Settings</li>
                </ul>
            </nav>

            <h1>Welcome to AdminPage {{userInfo.username}}</h1>
            <div class="alert alert-info">
                {{userInfo.email}}
            </div>

        </header>
        <div>


        </div>


    </div>
</template>

<script>
    export default {
        name: "AdminPage"
    }
</script>

<style scoped>

</style>