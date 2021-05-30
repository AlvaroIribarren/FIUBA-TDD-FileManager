import React, { useState } from 'react'
import './NavbarItem.css'

export default function NavbarItem( props) {
    const [open, setOpen] = useState(false)
    return (
        <li className="nav-item">
            <a href="#" className="icon-button" onClick={() => setOpen(!open)}>
                {props.icon}
            </a>

            {open && props.children}
        </li>
    )
}
