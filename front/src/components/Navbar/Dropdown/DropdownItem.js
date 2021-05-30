import React from 'react'
import './DropdownItem.css'

export default function DropdownItem(props) {
    return (
        <a href="#" className="menu-item" onClick={props.onClick}> 
            <span className="icon-button"> {props.leftIcon} </span>
            {props.children}
            <span className="icon-right"> {props.rightIcon} </span>
        </a>
    )
}
