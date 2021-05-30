import React, { useContext, useEffect, useState } from 'react'
import TreeMenu from 'react-simple-tree-menu'
import { NodesContext } from './NodesStore'

export default function TreeView() {
    const [nodesState, nodesDispatch] = useContext(NodesContext)
    const [changes, setChanges] = useState(0)

    const parseData = () => {
    }

    return (
        <div>

        </div>
    )
}
