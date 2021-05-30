import React, { useContext } from 'react'
import 'bootstrap'
import { UserContext } from '../contexts/UserContext'
import { Redirect } from 'react-router-dom';

const API_BASE_URL = require('../constants/index').default.API_BASE_AUTH;
const API_LOGIN = require('../constants/index').default.API_LOGIN;
const axios = require('axios').default;

export default function Login() {
    const { user, setUser } = useContext(UserContext)
    const setTokens = (token, refreshToken) => {
        setUser({...user, 
            token, refreshToken,
            logged: true
        })
    }
    
    const handleSubmit = (e) => {
        e.preventDefault()

        axios.post(API_BASE_URL + API_LOGIN, {
                email: user.email,
                password: user.password
            })
            .then (response => {
                console.log(response)
                const token = response.headers.token
                const refreshToken = response.headers.refreshtoken
                setTokens(token, refreshToken)
            })
    }

    return (
        <div>
            <form onSubmit={handleSubmit}>
                { user.logged ? (<Redirect push to="/"/>) : null }
                <h2 className="sign-in"> Sign In </h2>

                <div className="form-group">
                    <label>Email address</label>
                    <input 
                        type="email" 
                        className="form-control" 
                        placeholder="Enter email" 
                        onChange={ e => {
                            setUser({...user, email: e.target.value})
                        }}
                    />
                </div>

                <div className="form-group">
                    <label>Password</label>
                    <input 
                        type="password" 
                        className="form-control" 
                        placeholder="Enter password" 
                        onChange={ e => {
                            setUser({...user, password: e.target.value})
                        }}
                    />
                </div>

                <div className="form-group">
                    <div className="custom-control custom-checkbox">
                        <input type="checkbox" className="custom-control-input" id="customCheck1" />
                        <label className="custom-control-label" htmlFor="customCheck1">Remember me</label>
                    </div>
                </div>

                <button type="submit" className="btn btn-primary btn-block">Submit</button>
                <p className="forgot-password text-left">
                    Forgot <a href="#"> password? </a>
                </p>
            </form>
        </div>
    )
}
