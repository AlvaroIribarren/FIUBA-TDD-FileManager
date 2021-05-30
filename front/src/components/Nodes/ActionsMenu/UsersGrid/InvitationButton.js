import { Button, Snackbar } from '@material-ui/core'
import { Alert } from '@material-ui/lab'
import React, { useState } from 'react'
import InvitationsRequester from '../../../Invitations/InvitationsRequester'

export default function InvitationButton(props) {
    const { nodeId, userId, writePermission} = props
    const [success, setSuccess] = useState(false)
    const [failure, setFailure] = useState(false)

    const sendInvitation = async () => {
        const response = await InvitationsRequester.share(nodeId, userId, writePermission)
        if (response && response.status === 200) {
            setSuccess(true)
        } else {
            setFailure(true)
        }
    }
    
    const handleSuccessfulClose = () => {
        setSuccess(false)
    }

    const handleFailureClose = () => {
        setFailure(false)
    }

    const showAlertOnSuccess = () => {
        if (success) {
            return (
                <Snackbar open={success} autoHideDuration={6000} onClose={handleSuccessfulClose}>
                    <Alert 
                        onClose={() => {}}
                        severity="success" 
                    >
                        Invitation sent 
                    </Alert>
                </Snackbar>
            )
        }
    }

    const showAlertOnFailure = () => {
        if (failure) {
            return (
                <Snackbar open={failure} autoHideDuration={6000} onClose={handleFailureClose}>
                    <Alert 
                        onClose={() => {}}
                        severity="error" 
                    >
                        Hubo un error.
                    </Alert>
                </Snackbar>
            )
        }
    }

    return (
        <div>
            <Button onClick={sendInvitation} disabled={success || failure}>
                Send invitation
            </Button>
            {showAlertOnSuccess()}
            {showAlertOnFailure()}
        </div>
    )
}
