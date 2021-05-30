import { IconButton } from '@material-ui/core'
import CloseIcon from '@material-ui/icons/Close';
import React from 'react'
import InvitationsRequester from './InvitationsRequester';

const OK = 200;

export default function RejectButton(props) {
    const { nodeId, updateInvitations } = props
    const rejectInvitation = async () => {
        const response = await InvitationsRequester.acceptInvitation(nodeId, false)
        if (response.status === OK) {
            updateInvitations();
            alert.show('Invitacion rechazada', { type: 'success'})
        } else {
            alert.show('Ocurrio un error', { type: 'error'})
        }
    }

    return (
        <IconButton onClick={rejectInvitation}>
            <CloseIcon />
        </IconButton>
    )
}