import React, { useContext } from 'react'
import DropdownItem from './DropdownItem'
import './DropdownMenu.css'
import { UserContext } from '../../../contexts/UserContext'

import { ReactComponent as CrossIcon} from '../../../images/icons/close.svg'
import { ReactComponent as EditIcon} from '../../../images/icons/edit.svg'
import axiosAuth from '../../../auth/auth'

const API_BASE_URL = require('../../../constants/index').default.API_BASE_AUTH;
const API_USERS = require('../../../constants/index').default.API_USERS;


export default function DropdownMenu() {
    const { user, setUser } = useContext(UserContext)

    const editUser = () => {
        console.log("Edit")
    }

    const deleteUser = () => {
        axiosAuth.delete(API_BASE_URL + API_USERS, {
            params: {
                id: user.id
            },
        })
        .then (response => {
            console.log(response)
        })
    }

    return (
        <div className="dropdown">
            <DropdownItem
                leftIcon={<EditIcon/>}
                onClick={editUser}
            > 
                <p className="icon-text"> Edit user </p> 
            </DropdownItem>
            <DropdownItem 
                leftIcon={<CrossIcon/>}
                onClick={deleteUser}
            > 
                <p className="icon-text"> Delete user </p> 
            </DropdownItem>
        </div>
    )
}
