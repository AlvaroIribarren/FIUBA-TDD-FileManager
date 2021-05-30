export function getNodesForCurrentPath(nodesState, currentPath){
    if (currentPath == "") {
        return nodesState.content;
    } else {
        const directories = currentPath.split("/");
        //Saco el elemento vacio
        directories.shift()

        //Arranca con el valor inicial de la carpeta root
        let actualDirectory = {content: nodesState.content};
        //Baja hasta llegar al path
        for (let directory of directories) {
            if (actualDirectory.content && actualDirectory.content.length !== 0) {
                actualDirectory = actualDirectory.content.find(node => node.name === directory)
            } else if (actualDirectory.content && actualDirectory.content.length === 0){
                return actualDirectory.content;
            } else {
                return []
            }
        }
        return actualDirectory.content;
    }
}

export function parsePath(currentPath, selectedNode=null) {
    let pathWereNodeMustBe;

    if (selectedNode){
        pathWereNodeMustBe = currentPath + '/' + selectedNode.name
    } else {
        pathWereNodeMustBe = currentPath
    }

    const directoriesInPath = pathWereNodeMustBe.split("/")
    directoriesInPath.shift()
    return directoriesInPath
}
