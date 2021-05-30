import React, { useContext } from 'react';
import '../../App.css';
import { UserContext } from '../../contexts/UserContext';
import './MiddleSection.css';
import { Button, ButtonGroup } from 'react-bootstrap'
import { Redirect } from 'react-router-dom';

import authAxios from '../../auth/auth';

const API_BASE_AUTH = require('../../constants/index').default.API_BASE_AUTH;
const API_USERS = require('../../constants/index').default.API_USERS;
const API_REFRESH = require('../../constants/index').default.API_REFRESH;

function MiddleSection() {
  const { user, setUser } = useContext(UserContext)

  const showUserInformation = () => {
    if (user.logged) {
      return (
        <div> 
          <h1 className="display-1"> Bienvenido, {user.email}</h1>
        </div>
      ) 
    } else {
      return (
          <h1 className="display-1"> Bienvenido </h1>
      )
    }
  }


  return (
    <div className='middle-container'>
      <div className='middle-btns'>
          {showUserInformation()}
      </div>
    </div>
  );
}

export default MiddleSection;