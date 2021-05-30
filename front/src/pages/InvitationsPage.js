import React, { useContext, useState } from 'react'
import { Redirect } from 'react-router'
import Alert from '@material-ui/lab/Alert';
import InvitationsTable from '../components/Invitations/InvitationsTable'
import { UserContext } from '../contexts/UserContext'
import { Snackbar } from '@material-ui/core';

export default function InvitationsPage() {
    const {user, setUser} = useContext(UserContext)
    const [open, setOpen] = useState(false)

    const handleClose = (event, reason) => {
        if (reason === 'clickaway') {
          return;
        }
    
        setOpen(false);
    };

    const redirectIfNotLoggedIn = () => {
        if (!user.logged) {
            return (
                <div>
                    {alert("Para ver esta pestaña tenés que estar logeado, sino create una cuenta")}
                    <Redirect push to="/login"/>
                </div>
            )
        }
    }

    return (
        <div>
            {redirectIfNotLoggedIn()}
            <InvitationsTable/>
        </div>
    )
}
