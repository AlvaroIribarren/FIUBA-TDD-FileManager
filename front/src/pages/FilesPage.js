import React, { useContext, useState } from 'react'
import TableView from '../components/Nodes/TableView'
import TreeView from '../components/Nodes/TreeView'

import { makeStyles } from '@material-ui/core/styles';
import Grid from '@material-ui/core/Grid';
import { UserContext } from '../contexts/UserContext';
import { Redirect } from 'react-router';

const useStyles = makeStyles((theme) => ({
  root: {
    flexGrow: 1,
    width: "100%"
  },
  TreeView: {
    height: 140,
    width: "20%",
  },
  TableView: {
    padding: theme.spacing(2),
    width: 800
  },
}));


export default function FilesPage() {
  const { user, setUser } = useContext(UserContext)
  const classes = useStyles();

  const redirectIfNotLoggedIn = () => {
    if (!user.logged) {
        return (
            <div>
                {alert("Para ver esta pestaña tenés que estar logeado, sino create una cuenta")}
                <Redirect push to="/login"/>
            </div>
        )
    }
  }

  return (
    <div>
      {redirectIfNotLoggedIn()}
      <Grid container className={classes.root} justify="center" spacing={10}>
        
        {/*<Grid key="1" item>
          <TreeView className={classes.TreeView}/>
        </Grid>*/}
        <Grid key="2" item xs={12}>
          <TableView className={classes.TableView}/>
        </Grid>
      </Grid>
    </div>
  )
}
