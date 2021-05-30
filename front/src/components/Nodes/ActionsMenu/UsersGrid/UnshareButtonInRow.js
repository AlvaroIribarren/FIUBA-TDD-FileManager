import { Button, Snackbar } from '@material-ui/core'
import { Alert } from '@material-ui/lab'
import React, { useState } from 'react'
import InvitationsRequester from '../../../Invitations/InvitationsRequester'

const OK = 200;
const FORBIDDEN = 403;

export default function UnshareButtonInRow(props) {
    const { nodeId, userId, writePermission } = props
    const [success, setSuccess] = useState(false)
    const [failure, setFailure] = useState(false)

    const unshare = async () => {
        const response = await InvitationsRequester.unshare(nodeId, userId, writePermission)
        if (response && response.status === OK) {
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
                        Ya no compartís mas este nodo con la persona.
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
            <Button onClick={unshare} disabled={success || failure}>
                Unshare
            </Button>
            {showAlertOnSuccess()}
            {showAlertOnFailure()}
        </div>
    )
}
