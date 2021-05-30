import { Button } from '@material-ui/core'
import React, { useState } from 'react'
import UsersModal from './UsersModal'

export default function UnshareButton(props) {
    const { nodeInformation } = props;
    const [showModal, setShowModal] = useState(false)
    const openModal = async () => {
        setShowModal(true)
    }

    return (
        <div>
            <Button onClick={openModal}>
                Unshare
            </Button>
            <UsersModal 
                show={showModal} 
                nodeInformation={nodeInformation}
                onHide={() => setShowModal(false)}
                action={{ share: false }}
            />
        </div>
    )
}
