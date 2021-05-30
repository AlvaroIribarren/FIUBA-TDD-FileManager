import { Button, TableCell, TableRow } from '@material-ui/core'
import React, { useState } from 'react'
import NodesRequester from './NodesRequester'
import ActionsMenu from './ActionsMenu/ActionsMenu'


function goToFolder(props, row) {
    props.setCurrentPath(props.currentPath + '/' + row.name);
    props.setSelectedNode("");
}

function parseDate(date){
    const newDate = new Date(date);
    const day = newDate.getDate()
    const month = newDate.getMonth()
    const year = newDate.getFullYear()
    const dateString = day + '/' + month + '/' + year
    return dateString
}

export default function TableViewRow(props) {
    const { row, nodesController, currentPath } = props

    const handleClick = (e) => {
        if (e.type === 'click') {
            console.log("Click izq")
            props.setSelectedNode(row);
        } else {
            console.log('Right click')
        } 
    }

    const openSelectedNode = async (e) => {
        e.stopPropagation();
        if (nodesController.isDirectory(row)) {
            if (row.id !== 0) {
                const content = await NodesRequester.getFolder(row.id)
                if (!row.content) {
                    row.content = []
                }

                if (row.content.length === 0) {
                    nodesController.setNodesToFolder(row.id, content, currentPath) 
                }
            }

            goToFolder(props, row);
        } 
    }

    const removeNode = async (e) => {
        e.stopPropagation();
        const response = await NodesRequester.removeNode(row.id)
        if (response && response.status === 200){
            nodesController.removeNodeFromCurrentPath(row.id, props.currentPath)
        } 
        props.refreshNodes();
        return response.status
    }


    return (
        <TableRow hover 
            key={row.id} 
            onClick={handleClick}
            onContextMenu={handleClick} 
            onDoubleClick={openSelectedNode}
        >
            {/*Name */}
            <TableCell component="th" scope="row">
                {row.name}
            </TableCell>
            {/*Owner name*/}
            <TableCell component="th" scope="row">
                {row.owner.name}
            </TableCell>
            {/*Owner name*/}
            <TableCell component="th" scope="row">
                {row.owner.email}
            </TableCell>
            {/*Format*/}
            <TableCell component="th" scope="row">
                {row.format}
            </TableCell>
            {/*CreatedAt*/}
            <TableCell component="th" scope="row">
                {parseDate(row.createdAt)}
            </TableCell>
            {/*UpdatedAt*/}
            <TableCell component="th" scope="row">
                {parseDate(row.updatedAt)}
            </TableCell>
            <TableCell>
                <ActionsMenu 
                    row={row}
                    removeNode={removeNode} 
                    setSelectedNode={props.setSelectedNode}
                />
            </TableCell>
        </TableRow>  
    )
}


