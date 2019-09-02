import axios from 'axios';
import auth from './auth';
import { Auth } from 'aws-amplify';


export default {
    removeUserInfo() {
        console.log("Entered removeUserInfo");
        var jwtToken = auth.auth.getSignInUserSession().getAccessToken().jwtToken;
        const USERINFO_URL = 'https://'+auth.auth.getAppWebDomain() + '/oauth2/userInfo';
        var requestData = {
            headers: {
                'Authorization': 'Bearer '+ jwtToken
            }
        }
        console.log("123");
        return axios.delete(USERINFO_URL, {data: requestData});

    }
}