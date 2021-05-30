import Node from './Node'
import actions from './NodeActions'
import { getNodesForCurrentPath, parsePath } from './NodesHelper'
const ROOT_ID = 1;

export class NodesController {
    constructor(state, dispatch){
        this.state = state;
        this.dispatch = dispatch;
    }

    addNodeToCurrentPath(node, selectedNode, currentPath) {
        this.dispatch({ type: actions.ADD_NODE, payload: {node, selectedNode, currentPath}})
    }

    removeNodeFromCurrentPath(id, currentPath) {
        this.dispatch({ type: actions.REMOVE_NODE, payload: {id, currentPath}})
    }

    getNodesForCurrentPath(currentPath) {
        return getNodesForCurrentPath(this.state, currentPath)
    }

    setNodes(content){
        this.dispatch({ type: actions.SET_NODES, payload: {content}})
    }

    setNodesToFolder(id, content, currentPath) {
        this.dispatch({ type: actions.SET_NODES_TO_FOLDER, payload: {id, content, currentPath}})
    }

    returnToPreviousDirectory(e, currentPath, setCurrentPath){
        e.preventDefault();
        const directoriesInPath = parsePath(currentPath)
        directoriesInPath.pop();

        let path = "";
        for (let directory of directoriesInPath) {
            path += "/" + directory
        }
        setCurrentPath(path)
    }

    getParentIdFromCurrentPath(currentPath){
        const directoriesInPath = parsePath(currentPath)
        let actualDirectory = {content: this.state.content}
        let id = ROOT_ID;
        for (let directory of directoriesInPath) {
            const node = actualDirectory.content.find(actualNode => actualNode.name === directory)
            actualDirectory.content = node.content
            id = node.id
        }
        return id;
    }

    isDirectory(node) {
        return node.format === "folder"
    }
}