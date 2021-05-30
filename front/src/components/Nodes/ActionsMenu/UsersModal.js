import React from 'react'
import { Button, Container, Modal } from 'react-bootstrap';
import UsersGrid from './UsersGrid/UsersGrid'

export default function UsersModal(props) {
    return (
        <Modal {...props} size="lg" aria-labelledby="contained-modal-title-vcenter">
            <Modal.Header closeButton>
                <Modal.Title id="contained-modal-title-vcenter">
                    Compartiendo "{props.nodeInformation.name}"
          </Modal.Title>
            </Modal.Header>
            <Modal.Body className="show-grid">
                <Container>
                    <UsersGrid 
                        nodeInformation={props.nodeInformation}
                        action={props.action}
                    />
                </Container>
            </Modal.Body>
            <Modal.Footer>
                <Button onClick={props.onHide}>Close</Button>
            </Modal.Footer>
        </Modal>
    );
}