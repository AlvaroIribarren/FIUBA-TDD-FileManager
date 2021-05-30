import React, { useContext } from 'react'
import { GoogleLogin } from 'react-google-login';
import { UserContext } from '../contexts/UserContext'

const axios = require('axios').default;
const API_BASE_URL = require('../constants/index').default.API_BASE_AUTH;
const API_GOOGLE_SIGN_IN = require('../constants/index').default.API_GOOGLE_SIGN_IN;
const API_USERS = require('../constants/index').default.API_USERS;

export default function GoogleAuth() {
    const { user, setUser } = useContext(UserContext)

    const responseGoogle = (googleResponse) => {
        console.log(googleResponse)
        const profileObj = googleResponse.profileObj;
        
        axios.post(API_BASE_URL + API_GOOGLE_SIGN_IN, {
            name: profileObj.name,
            email: profileObj.email,
            token: googleResponse.accessToken
        }).then(response => {
            setUser({
                ...user, 
                id: response.data.id,
                name: profileObj.name,
                email: profileObj.email,
                token: response.headers.token,
                refreshToken: response.headers.refreshtoken,
                logged: true
            })
        }).catch(error => {
            console.log(error)
        })
    }

    return (
        <div>
            <GoogleLogin
                clientId="721702658160-e1nhi1ii1hkeoc6n6vmnan8v1lo1idsn.apps.googleusercontent.com"
                buttonText="Login"
                onSuccess={responseGoogle}
                onFailure={responseGoogle}
                cookiePolicy={'single_host_origin'}
            />
        </div>
    )
}
