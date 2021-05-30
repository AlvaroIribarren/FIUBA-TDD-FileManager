import Node from './Node'
import actions from './NodeActions'
import { parsePath } from './NodesHelper'
import axiosInstance from '../../auth/auth'

const API_BASE_FILES = require('../../constants/index').default.API_BASE_FILES;
const API_FOLDER = require('../../constants/index').default.API_FOLDER;

function removeElementFromArrayById(array, id) {
    return array.filter(actualElement => actualElement.id != id)
}

function addNodeToSelectedNode(state, node, selectedNode, currentPath){
    //Busco la carpeta segun el path y el seleccionado
    let actualDirectory = { content: state.content};
    const directoriesInPath = parsePath(currentPath, selectedNode)
    
    for (let directory of directoriesInPath) {
        actualDirectory = actualDirectory.content.find(actualNode => actualNode.name === directory)
    }

    actualDirectory.content.push(node)
}

function removeNodeFromCurrentPath(state, id, currentPath) {
    let actualDirectory = { content: state.content }
    const directoriesInPath = parsePath(currentPath)
    
    for (let directory of directoriesInPath) {
        actualDirectory = actualDirectory.content.find(actualNode => actualNode.name === directory)
    }

    //Si el current path es root
    if (!currentPath) {
        state.content = removeElementFromArrayById(state.content, id)
    } else {
        actualDirectory.content = removeElementFromArrayById(actualDirectory.content, id)
    }
    
}

function setNodes(state, content){
    state.content = content
}

function setNodesToFolder(state, id, content, currentPath) {
    let actualDirectory = { content: state.content }
    const directoriesInPath = parsePath(currentPath)

    for (let directory of directoriesInPath) {
        actualDirectory = actualDirectory.content.find(actualNode => actualNode.name === directory)
    }

    const folderWhereContentMustBeAdded = actualDirectory.content.find(actualNode => actualNode.id === id)
    folderWhereContentMustBeAdded.content  = content;
}

const NodesReducer = (state, action) => {
    switch(action.type) {
        case actions.ADD_NODE:
            const {node, selectedNode, currentPath} = action.payload;
            addNodeToSelectedNode(state, node, selectedNode, currentPath)
            break;
        case actions.REMOVE_NODE:
            removeNodeFromCurrentPath(state, action.payload.id, action.payload.currentPath)
            break;
        case actions.SET_NODES:
            setNodes(state, action.payload.content);
            break;
        case actions.SET_NODES_TO_FOLDER:
            setNodesToFolder(state, action.payload.id, action.payload.content, action.payload.currentPath)
            break;
    }
    return state;
}

export default NodesReducer;


