import { IconButton } from '@material-ui/core'
import CheckIcon from '@material-ui/icons/Check';
import React from 'react'
import InvitationsRequester from './InvitationsRequester';
import { useAlert } from 'react-alert'

const OK = 200;

export default function AcceptButton(props) {
    const { nodeId, updateInvitations } = props
    const alert = useAlert()

    const acceptInvitation = async () => {
        const response = await InvitationsRequester.acceptInvitation(nodeId, true)
        if (response.status === OK) {
            updateInvitations();
            alert.show('Aceptado correctamente, podes ver el archivo en tu carpeta "shared" ', { type: 'success'})
        } else {
            alert.show('Ocurrio un error', { type: 'error'})
        }
    }

    return (
        <IconButton onClick={acceptInvitation}>
            <CheckIcon />
        </IconButton>
    )
}
