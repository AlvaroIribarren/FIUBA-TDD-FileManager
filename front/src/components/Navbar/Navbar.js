import React, { useState, useEffect } from 'react'
import { Link, Redirect } from 'react-router-dom'
import { UserContext } from '../../contexts/UserContext'
import { useContext } from 'react'
import { Button, ButtonGroup } from 'react-bootstrap'
import 'bootstrap/dist/css/bootstrap.min.css'
import './Navbar.css'

const axios = require('axios').default;
const API_BASE_AUTH = require('../../constants/index').default.API_BASE_AUTH;
const API_LOGOUT = require('../../constants/index').default.API_LOGOUT;

function Navbar() {
    const { user, setUser } = useContext(UserContext)
    const [click, setClick] = useState(false);
    const [button, setButton] = useState(true);
    const [redirectHome, setRedirectHome] = useState(false)

    const handleClick = () => {
        setClick(!click)
    }

    const closeMobileMenu = () => {
        setClick(false)
    }

    const showButton = () => {
        if (document.documentElement.clientWidth <= 960){
            setButton(false);
        } else {
            setButton(true);
        }
    }

    const logUserOut = (e) => {
        e.preventDefault();
        //Sin auth
        axios.post(API_BASE_AUTH + API_LOGOUT, {}, {
            headers: {
                refreshToken: user.refreshToken
            }
        }).then(response => {
            console.log(response)
        })
        setUser({})
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
        setRedirectHome(true)
    }

    const renderIfLoggedIn = () => {
        if (!user.logged) {
            return (<ButtonGroup size="md">
                <Button href="/sign-up" active="true" variant="primary"> Sign up!</Button>)
                <Button href="/login" active="true"> Login </Button>)  
            </ButtonGroup>)
        } else {
            return ( 
                <Button active="true" variant="primary" onClick={e => logUserOut(e)}>
                    Logout 
                </Button>
            )
        }
    }

    useEffect(() => {
        showButton()
    }, [])

    window.addEventListener('resize', showButton);

    return (
        <>
            { redirectHome ? (<Redirect push to="/"/>) : null }
            <nav className="navbar">
                <div className="navbar-container">
                    <Link to="/" className="navbar-logo" onClick={closeMobileMenu}>
                        TDD <i className="fab fa-typo3"/>
                    </Link>
                    <div className="menu-icon" onClick={handleClick}>
                        <i className={click ? 'fas fa-times' : 'fas fa-bars'}/>
                    </div>
                    <ul className={click ? 'nav-menu active' : 'nav-menu'}>
                        <li className="nav-item">
                            <Link to='/' className='nav-links' onClick={closeMobileMenu}>
                                Home
                            </Link>
                        </li>
                        <li className="nav-item">
                            <Link to='/files' className='nav-links' onClick={closeMobileMenu}>
                                Files
                            </Link>
                        </li>
                        <li className="nav-item">
                            <Link to='/invitations' className='nav-links' onClick={closeMobileMenu}>
                                Invitations
                            </Link>
                        </li>
                        <li className="nav-item">
                            <Link to='/sign-up' className='nav-links-mobile' onClick={closeMobileMenu}>
                                Sign up
                            </Link>
                        </li>
                    </ul>
                    {renderIfLoggedIn()}
 
                </div>
            </nav>
        </>
    )
}


export default Navbar
