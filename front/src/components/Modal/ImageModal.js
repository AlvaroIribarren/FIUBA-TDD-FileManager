import { Button } from '@material-ui/core';
import React, { useEffect, useState } from 'react'
import Lightbox from 'react-modal-image/lib/Lightbox';

export default function SimpleModal(props) {
  const { image, name } = props;
  const [open, setOpen] = useState(false)
  
  const closeLightbox = () => {
    setOpen(false)
  }

  const openLightbox = () => {
    setOpen(true)
  }
  
  const showIfOpen = () => {
    if (open) {
      return (
        <Lightbox
          large={image}
          alt={name}
          onClose={closeLightbox}
        />
      )
    }
  }

  return (
    <div>
      <Button onClick={openLightbox}>
        Preview
      </Button>
      {showIfOpen()}
    </div>
  )
}


