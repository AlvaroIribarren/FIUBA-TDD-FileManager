import React, { useEffect, useState } from 'react'
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import { Button } from '@material-ui/core';
import { useAlert } from 'react-alert'

const OK = 200

export default function RemoveDialog(props) {
    const [open, setOpen] = useState(false);
    const alert = useAlert()

    const handleClickOpen = () => {
      setOpen(true);
    };
  
    const handleClose = async (e) => {
      setOpen(false)   
      props.setSelectedNode("")
      const status = await props.removeNode(e);
      if (status === OK) {
        alert.show("Nodo eliminado correctamente", { type: 'success'})
      } else {
        alert.show("Este nodo no pudo ser eliminado", { type: 'error'})
      }
    };

    const disagreeOnRemoval = (e) => {
      setOpen(false)
    }
  
    return (
      <div>
        <Button color="secondary" onClick={handleClickOpen}>
          Eliminar
        </Button>
        <Dialog
          open={open}
          onClose={disagreeOnRemoval}
          aria-labelledby="alert-dialog-title"
          aria-describedby="alert-dialog-description"
        >
          <DialogTitle id="alert-dialog-title">{"About to remove node"}</DialogTitle>
          <DialogContent>
            <DialogContentText id="alert-dialog-description">
              Are you sure you want to remove this node? This action cannot be undone.
            </DialogContentText>
          </DialogContent>
          <DialogActions>
            <Button color="primary" onClick={disagreeOnRemoval}>
              Disagree
            </Button>
            <Button onClick={handleClose} color="primary" autoFocus>
              Agree
            </Button>
          </DialogActions>
        </Dialog>
      </div>
    );
}
