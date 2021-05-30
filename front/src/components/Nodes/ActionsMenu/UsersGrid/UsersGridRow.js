import { Button, Checkbox, TableCell, TableRow } from '@material-ui/core'
import React, { useState } from 'react'
import InvitationButton from './InvitationButton';
import UnshareButtonInRow from './UnshareButtonInRow';

export default function UsersGridRow(props) {
    const { row, nodeInformation, action} = props;
    const [writePermission, setWritePermission] = useState(false)

    const changeWritePermission = (e) => {
        e.preventDefault();
        setWritePermission(writePermission => !writePermission)
    }

    const invitationButtonOrUnshare = () => {
        if (action.share) {
            return (
                <InvitationButton
                    nodeId={nodeInformation.id}
                    userId={row.id}
                    writePermission={writePermission}    
                />
            )
        } else {
            return (
                <UnshareButtonInRow
                    nodeId={nodeInformation.id}    
                    userId={row.userId}
                    writePermission={writePermission}     
                />
            )
        }
    }

    const getUserId = () => {
        if (action.share) {
            return row.id
        } else {
            return row.userId
        }
    }

    return (
        <TableRow
            hover
            key={row.id}
        >
            <TableCell component="th" align="center" key={row.id} scope="row" padding="none">
                {getUserId(row)}
            </TableCell>
            <TableCell align="center">{row.name}</TableCell>
            <TableCell align="center">{row.email}</TableCell>
            <TableCell align="center">
                <Checkbox 
                    checked={writePermission} 
                    onClick={changeWritePermission} 
                    inputProps={{ 'aria-label': 'primary checkbox' }}
                />
            </TableCell>
            <TableCell>
                { invitationButtonOrUnshare() }
            </TableCell>
        </TableRow>
    )
}
