import React, { useContext, useEffect } from 'react'
import 'bootstrap'
import { UserContext } from '../contexts/UserContext';
import { Redirect } from 'react-router-dom';
import '../components/Signup/Signup.css'

const axios = require('axios').default;
const API_BASE_URL = require('../constants/index').default.API_BASE_AUTH;
const API_LOGIN = require('../constants/index').default.API_LOGIN;

export default function Login() {
    const { user, setUser} = useContext(UserContext)   

    useEffect(() => {
        localStorage.setItem('token', user.token);
        localStorage.setItem('refreshToken', user.refreshToken)
    }, [user])

    const handleLogin = (e) => {
        e.preventDefault()
        axios.post(API_BASE_URL + API_LOGIN, {
            email: user.email,
            password: user.password
        }).then(response => {
            setUser({
                ...user, 
                id: response.data.id,
                token: response.headers.token,
                refreshToken: response.headers.refreshtoken,
                logged: true
            })
        })
    }

    return (
        <div>
            <form onSubmit={handleLogin}>
                { user.logged ? (<Redirect push to="/"/>) : null }

                <h3 className="sign-in">
                    Sign In
                </h3>

                <div className="form-group">
                    <label>Email address</label>
                    <input 
                        type="email" 
                        className="form-control" 
                        placeholder="Enter email" 
                        onChange={e => {
                            setUser({...user, email: e.target.value})
                        }}
                        value={user.email}
                    />
                </div>

                <div className="form-group">
                    <label>Password</label>
                    <input 
                        type="password" 
                        className="form-control" 
                        placeholder="Enter password" 
                        onChange={e => {
                            setUser({...user, password: e.target.value})
                        }}
                        value={user.password}
                    />
                </div>

                <div className="form-group">
                    <div className="custom-control custom-checkbox">
                    </div>
                </div>

                <button type="submit" className="btn btn-primary btn-block">Submit</button>
            </form>
        </div>
    )
}
