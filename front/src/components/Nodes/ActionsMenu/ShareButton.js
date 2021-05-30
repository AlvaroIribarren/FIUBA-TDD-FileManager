import { Button } from '@material-ui/core'
import React, { useContext, useState } from 'react'
import InvitationsRequester from '../../Invitations/InvitationsRequester'
import { UserContext } from '../../../contexts/UserContext'
import UsersModal from './UsersModal'

export default function ShareButton(props) {
    const [showModal, setShowModal] = useState(false)
    const { user } = useContext(UserContext)
    const { nodeInformation } = props;

    const share = async () => {
        setShowModal(true)
    }

    return (
        <div>
            <Button color="primary" onClick={share}>
                Share
            </Button>
            <UsersModal 
                show={showModal} 
                nodeInformation={nodeInformation}
                onHide={() => setShowModal(false)}
                action={{ share: true }}
            />
        </div>
    )
}
