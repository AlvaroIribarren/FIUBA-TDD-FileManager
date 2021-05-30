import React, { useContext, useEffect, useReducer, useState } from 'react';
import { makeStyles } from '@material-ui/core/styles';
import { TableBody, TableCell, TableContainer, Table, 
    Container, TableHead, TableRow, Paper, Input, Typography, Box } from '@material-ui/core'
import { NodesContext } from './NodesStore';
import { Button } from 'react-bootstrap';
import { NodesController } from './NodesController'
import TableViewRow from './TableViewRow'
import Popup from 'reactjs-popup';
import 'reactjs-popup/dist/index.css';
import { UserContext } from '../../contexts/UserContext';
import Node from './Node';
import NodesRequester from './NodesRequester'
import axiosInstance from '../../auth/auth'
import { useAlert } from 'react-alert'

const API_BASE_FILES = require('../../constants/index').default.API_BASE_FILES;
const API_FOLDER = require('../../constants/index').default.API_FOLDER;

const OK = 200
const FORBIDDEN = 403

const useStyles = makeStyles(theme => ({
    root: {
      width: "100%"
    },
    paper: {
      marginTop: theme.spacing(3),
      width: "100%",
      overflowX: "auto",
      marginBottom: theme.spacing(2),
      margin: "auto"
    },
    table: {
      width: '100%',
    }
}));

export default function TableView() {
    const { user, setUser} = useContext(UserContext)
    const [nodesState, nodesDispatch] = useContext(NodesContext)
    const nodesController = new NodesController(nodesState, nodesDispatch) 
    const classes = useStyles();
    const [changes, setChanges] = useState(0)

    const [selectedFile, setSelectedFile] = useState({});
	const [isFilePicked, setIsFilePicked] = useState(false);
    const [directoryName, setDirectoryName] = useState("")
    const [selectedNode, setSelectedNode] = useState()    
    const [currentPath, setCurrentPath] = useState("")  

    const alert = useAlert()

    const createShared = async (root) => {
        const sharedNodes = await NodesRequester.getSharedNodes()
        return {
            content: sharedNodes,
            createdAt: root.createdAt,
            updatedAt: root.updatedAt,
            folder: true,
            id: 0,
            name: 'shared',
            owner: root.owner,
            parentId: null,
            format: "folder"
        }
    }

    useEffect(() => {
        console.log("Corriendo useEffect")
    }, [changes])

    useEffect(()=>{
        async function askNodes() {
            const response = await axiosInstance.get(API_BASE_FILES + API_FOLDER);
            const nodes = [response.data]
            if (nodes.length === 1) {
                const shared = await createShared(nodes[0])
                nodes.push(shared)
            }
            nodesController.setNodes(nodes)
            refreshNodes();
        }
        askNodes()
    }, [])

    const generateRows = () => {
        const nodesForCurrentPath = nodesController.getNodesForCurrentPath(currentPath);
        return nodesForCurrentPath.map(row => {
            return ( 
                <TableViewRow 
                    key={row.id} 
                    row={row} 
                    nodesController={nodesController}
                    refreshNodes={refreshNodes}
                    selectedNode={selectedNode}
                    setSelectedNode={setSelectedNode}
                    currentPath={currentPath}
                    setCurrentPath={setCurrentPath}
                />
            )
        })
    }

    const addFileChangeHandler = (e) => {
        const file = e.target.files[0];
		setSelectedFile(file);
		setIsFilePicked(true);
	};

    const addDirectoryChangeHandler = (e) => {
        setDirectoryName(e.target.value)
	};

    const refreshNodes = () => {
         //Se usa para obligar a realizar el render
         setChanges(changes+1)
    }

    const resetActualNode = () => {
        setIsFilePicked(false)
        setSelectedFile({})
    }

    const handleAddFileSubmission = async (e) => {
        e.preventDefault();
        if (isFilePicked && currentPath !== '') {
            const parentId = nodesController.getParentIdFromCurrentPath(currentPath)
            const response = await NodesRequester.addFile(parentId, selectedFile)
            if (response.status === OK) {
                const size = 0;
                const { id, name, owner, createdAt, updatedAt, format} = response.data;
    
                const node = new Node(id, name, size, owner, format, parentId, createdAt, updatedAt);
                
                nodesController.addNodeToCurrentPath(node, selectedNode, currentPath)
                refreshNodes();
                resetActualNode();   
                alert.show("Agregado correctamente", { type: "success"})
            } else {
                alert.show("Ocurrió un error", { type: "error"})
            }
        } else if (!isFilePicked) {
            alert.show("Elija un archivo", { type: "error"})
        } else if (currentPath === '') {
            alert.show("No se pueden agregar archivos fuera de la carpeta root", { type: "error"})
        }
	};

    const handleAddDirectorySubmission = async (e) => {
        e.preventDefault();
        e.stopPropagation();
        if (currentPath !== '') {
            const parentId = nodesController.getParentIdFromCurrentPath(currentPath)
            const response = await NodesRequester.addDirectory(directoryName, parentId)
    
            if (response.status === OK) {
                const { id, name, owner, createdAt, updatedAt, format } = response.data;
                const size = 0;
                const node = new Node(id, name, size, owner, format, parentId, createdAt, updatedAt);
                nodesController.addNodeToCurrentPath(node, selectedNode, currentPath)
        
                console.log(nodesState)
                refreshNodes();
                alert.show("Agregado correctamente", { type: "success"})
            } else if (response.status === FORBIDDEN){
                alert.show("Este nombre ya existe, por favor, elija otro", { type: "error"})
            } else {
                alert.show("Ocurrió un error", { type: "error"})
            }
        } 
    }

    const renderCleanSelectedNode = () => {
        if (selectedNode) {
            return (
                <div>
                    <Button onClick={e => {
                        e.preventDefault();
                        setSelectedNode("")
                    }}>
                        Clean selected node
                    </Button>
                </div>

            )
        } else {
            return null
        }
    }
    
    return (
        <div>
            <Container fixed>
                <Button onClick={e => {
                        nodesController.returnToPreviousDirectory(e, currentPath, setCurrentPath)
                    }}>
                    Go back
                </Button>
                <Popup 
                    trigger={
                        <Button>
                            Add file
                        </Button>
                    } 
                    position="right center">
                    <Input type="file" name="file" onChange={addFileChangeHandler} />
                    <div>
                        <Button onClick={handleAddFileSubmission}>Submit</Button>
                    </div>
                </Popup>

                <Popup 
                    trigger={
                        <Button>
                            Add directory
                        </Button>
                    } 
                    position="right center">
                    <Input type="text" name="text" onChange={addDirectoryChangeHandler}/>
                    <Button className="add-directory" onClick={handleAddDirectorySubmission}>Submit</Button>
                </Popup>

                <div>
                    <Typography> Selected node: {selectedNode ? selectedNode.name : null}</Typography>
                    {renderCleanSelectedNode()}
                </div>

                <Typography> Path: {currentPath}</Typography>

                <TableContainer component={Paper}>
                    <Table className={classes.table} size="medium" aria-label="a dense table">
                        <TableHead>
                        <TableRow key="names">
                            <TableCell> Name</TableCell>
                            <TableCell> Owner name</TableCell>
                            <TableCell> Owner email</TableCell>
                            <TableCell> Format </TableCell>
                            <TableCell> CreatedAt</TableCell>
                            <TableCell> UpdatedAt</TableCell>
                            <TableCell> </TableCell>
                        </TableRow>
                        </TableHead>
                        <TableBody key="body">
                            {generateRows()}
                        </TableBody>
                    </Table>
                </TableContainer>
            </Container>
        </div>
    );
}
