import React, { useEffect, useState } from 'react'
import PropTypes from 'prop-types';
import { makeStyles, useTheme } from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableFooter from '@material-ui/core/TableFooter';
import TablePagination from '@material-ui/core/TablePagination';
import TableRow from '@material-ui/core/TableRow';
import Paper from '@material-ui/core/Paper';
import IconButton from '@material-ui/core/IconButton';
import FirstPageIcon from '@material-ui/icons/FirstPage';
import KeyboardArrowLeft from '@material-ui/icons/KeyboardArrowLeft';
import KeyboardArrowRight from '@material-ui/icons/KeyboardArrowRight';
import LastPageIcon from '@material-ui/icons/LastPage';
import { Container, TableHead } from '@material-ui/core';
import AcceptButton from './AcceptButton';
import RejectButton from './RejectButton';
import InvitationsRequester from './InvitationsRequester';
import NodesRequester from '../Nodes/NodesRequester';

const useStyles1 = makeStyles((theme) => ({
    root: {
        flexShrink: 0,
        marginLeft: theme.spacing(2.5),
    },
}));

function TablePaginationActions(props) {
    const classes = useStyles1();
    const theme = useTheme();
    const { count, page, rowsPerPage, onChangePage } = props;

    const handleFirstPageButtonClick = (event) => {
        onChangePage(event, 0);
    };

    const handleBackButtonClick = (event) => {
        onChangePage(event, page - 1);
    };

    const handleNextButtonClick = (event) => {
        onChangePage(event, page + 1);
    };

    const handleLastPageButtonClick = (event) => {
        onChangePage(event, Math.max(0, Math.ceil(count / rowsPerPage) - 1));
    };

    return (
        <div className={classes.root}>
            <IconButton
                onClick={handleFirstPageButtonClick}
                disabled={page === 0}
                aria-label="first page"
            >
                {theme.direction === 'rtl' ? <LastPageIcon /> : <FirstPageIcon />}
            </IconButton>
            <IconButton onClick={handleBackButtonClick} disabled={page === 0} aria-label="previous page">
                {theme.direction === 'rtl' ? <KeyboardArrowRight /> : <KeyboardArrowLeft />}
            </IconButton>
            <IconButton
                onClick={handleNextButtonClick}
                disabled={page >= Math.ceil(count / rowsPerPage) - 1}
                aria-label="next page"
            >
                {theme.direction === 'rtl' ? <KeyboardArrowLeft /> : <KeyboardArrowRight />}
            </IconButton>
            <IconButton
                onClick={handleLastPageButtonClick}
                disabled={page >= Math.ceil(count / rowsPerPage) - 1}
                aria-label="last page"
            >
                {theme.direction === 'rtl' ? <FirstPageIcon /> : <LastPageIcon />}
            </IconButton>
        </div>
    );
}

TablePaginationActions.propTypes = {
    count: PropTypes.number.isRequired,
    onChangePage: PropTypes.func.isRequired,
    page: PropTypes.number.isRequired,
    rowsPerPage: PropTypes.number.isRequired,
};


const useStyles2 = makeStyles({
    table: {
        minWidth: 500,
    },
});

const READ = 'R';
const WRITE = 'W';
const LECTURA = 'Lectura'
const ESCRITURA = 'Escritura'

function getFormat(format) {
    if (READ){
        return LECTURA
    } else {
        return ESCRITURA
    }
}

export default function InvitationsTable() {
    const classes = useStyles2();
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(5);
    const [changes, setChanges] = useState(0)
    const [invitations, setInvitations] = useState([])

    useEffect(()=>{
        async function askForInvitations() {
            const invitations = await InvitationsRequester.getInvitations();

            setInvitations(invitations)
        }
        askForInvitations()
    }, [changes])


    const updateInvitations = () => {
        setChanges(changes => changes+1)
    }

    const emptyRows = rowsPerPage - Math.min(rowsPerPage, invitations.length - page * rowsPerPage);

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    const header = (
        <TableHead>
        <TableRow key="names">
            <TableCell> Node name </TableCell>
            <TableCell> User email </TableCell>
            <TableCell> Username </TableCell>
            <TableCell> Tipo de archivo </TableCell>
            <TableCell> Mode </TableCell>
            <TableCell></TableCell>
            <TableCell></TableCell>
        </TableRow>
        </TableHead>
    )

    const generateRows = () => {
        return (rowsPerPage > 0
            ? invitations.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
            : invitations
        ).map((row) => (
            <TableRow key={row.name}>
                <TableCell>
                    {row.node.name}
                </TableCell>
                <TableCell component="th" scope="row">
                    {row.user.email}
                </TableCell>
                <TableCell >
                    {row.user.name}
                </TableCell>
                <TableCell>
                    {row.node.format}
                </TableCell>
                <TableCell>
                    {getFormat(row.mode)}
                </TableCell>
                <TableCell>
                    <AcceptButton nodeId={row.nodeId} updateInvitations={updateInvitations}/>
                </TableCell>
                <TableCell>
                    <RejectButton nodeId={row.nodeId} updateInvitations={updateInvitations}/>
                </TableCell>
            </TableRow>
        ))
    }

    return (
        <Container fixed>
        <br/>
        <br/>
        <br/>
        <TableContainer component={Paper}>
            <Table className={classes.table} aria-label="custom pagination table">
                {header}
                <TableBody>
                    {generateRows()}

                    {emptyRows > 0 && (
                        <TableRow style={{ height: 53 * emptyRows }}>
                            <TableCell colSpan={6} />
                        </TableRow>
                    )}
                </TableBody>
                <TableFooter>
                    <TableRow>
                        <TablePagination
                            rowsPerPageOptions={[5, 10, 25, { label: 'All', value: -1 }]}
                            colSpan={3}
                            count={invitations.length}
                            rowsPerPage={rowsPerPage}
                            page={page}
                            SelectProps={{
                                inputProps: { 'aria-label': 'rows per page' },
                                native: true,
                            }}
                            onChangePage={handleChangePage}
                            onChangeRowsPerPage={handleChangeRowsPerPage}
                            ActionsComponent={TablePaginationActions}
                        />
                    </TableRow>
                </TableFooter>
            </Table>
        </TableContainer>
        </Container>
    );
}
