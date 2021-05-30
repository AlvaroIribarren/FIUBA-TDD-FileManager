import React, { useState } from 'react';
import Button from '@material-ui/core/Button';
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';
import RemoveDialog from './RemoveDialog'
import ImageModal from '../../Modal/ImageModal'
import TextModal from '../../Modal/TextModal'
import NodesRequester from '../NodesRequester'
import ShareButton from './ShareButton';
import UnshareButton from './UnshareButton';
import { Alert } from '@material-ui/lab';
import { Snackbar } from '@material-ui/core';

const FOLDER = "folder";
const IMAGE = "image";

export default function ActionsMenu(props) {
    const { row, removeNode, setSelectedNode } = props;
    const [anchorEl, setAnchorEl] = React.useState(null);
    const [data, setData] = useState(null)

    const handleClick = (event) => {
        setAnchorEl(event.currentTarget);
    };

    const handleClose = () => {
        setAnchorEl(null);
    };

    const handlePreview = async () => {
        console.log("Preview")
        const response = await NodesRequester.getFile(row.id, row.format);
        setData(response)
        //Cierra el popup
        setAnchorEl(null);
    }

    const handleRemove = () => {
        console.log("Remove")
        setAnchorEl(null);
    }

    const renderModal = () => {
        if (row.format.includes("image")){
            return (
                <ImageModal image={data} name={row.name}/>
            )
        } else if (row.format.includes("text")) {
            return (
                <TextModal text={data} name={row.name}/>
            )
        } else {
            return null;
        }
    }

    const renderFirstAction = () => {
        if (!row.format.includes(FOLDER)){
            return (
                <MenuItem onClick={handlePreview}>
                    {renderModal()}
                </MenuItem>
            )
        }
    }

    return (
    <div>
        <Button variant="outlined" color="default" aria-controls="simple-menu" 
                aria-haspopup="true" onClick={handleClick}>
            ...
        </Button>
        <Menu
            id="simple-menu"
            anchorEl={anchorEl}
            keepMounted
            open={Boolean(anchorEl)}
            onClose={handleClose}
        >

            {renderFirstAction()}

            <MenuItem onClick={handleRemove}>
                <RemoveDialog 
                    removeNode={removeNode} 
                    setSelectedNode={setSelectedNode}
                />
            </MenuItem>

            <MenuItem onClick={handleClose}> 
                <ShareButton nodeInformation={row}/> 
            </MenuItem>

            <MenuItem onClick={handleClose}> 
                <UnshareButton nodeInformation={row}/> 
            </MenuItem>
        </Menu>
    </div>
    );
}


