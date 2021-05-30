import React, { useContext, useEffect, useState } from 'react'
import { Redirect } from 'react-router-dom';
import { UserContext } from '../../contexts/UserContext'
import { Button, ButtonGroup } from 'react-bootstrap'
import './Signup.css'
import GoogleButton from '../GoogleAuth'
import { Container } from '@material-ui/core';

const axios = require('axios').default;
const API_BASE_URL = require('../../constants/index').default.API_BASE_AUTH;
const API_USERS = require('../../constants/index').default.API_USERS;
const storage = window.localStorage;

export default function Signup() {
    const { user, setUser } = useContext(UserContext)

    useEffect(() => {
        localStorage.setItem('token', user.token);
        localStorage.setItem('refreshToken', user.refreshToken)
    }, [user])

    const submitHandler = (e) => {
        e.preventDefault()
        axios.post(API_BASE_URL + API_USERS, {
            name: user.name,
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
        //Redirecciona apenas se da el submit
        <form onSubmit={submitHandler}>
            { user.logged ? (<Redirect push to="/"/>) : null }
            
            <h2 className="sign-up-text"> Sign Up </h2>
            
            <div className="form-group">
                <label> Username </label>
                <input 
                    type="text"
                    className="form-control"
                    placeholder="Enter name"
                    onChange={e => {
                        setUser({...user, name: e.target.value})
                    }}
                    value={user.name}
                />
            </div>

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

            <ButtonGroup className="mr-2" aria-label="First group">
                <Button 
                    style={{maxWidth: '100px', maxHeight: '100px', minWidth: '100px', minHeight: '25px'}} 
                    type="submit" active="false"> 
                    Sign Up 
                </Button>
                     
                <GoogleButton/> 
            </ButtonGroup>
     
       



         
            <p className="forgot-password text-left">
                Already registered <a href="/login"> sign in?</a>
            </p>
        </form>
    )
}
