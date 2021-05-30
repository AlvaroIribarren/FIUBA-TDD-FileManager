import React, { createContext, useReducer } from 'react'
import Reducer from './NodesReducer'

const initialState = {
    content: []
}

const NodesStore = ({children}) => {
    const [state, dispatch] = useReducer(Reducer, initialState);

    return (
        <NodesContext.Provider value={[state, dispatch]}>
            {children}
        </NodesContext.Provider>
    )
}

export const NodesContext = createContext(initialState)

export default NodesStore;