import './App.css';
import Navbar from './components/Navbar/Navbar';
import MaterialNavbar from './components/Navbar/MaterialNavbar';
import { BrowserRouter as Router, Switch, Route } from 'react-router-dom';
import Home from './pages/HomePage'
import Signup from './components/Signup/Signup';
import Login from './components/Login';
import FilesPage from './pages/FilesPage'
import { UserContext } from './contexts/UserContext'
import { useMemo, useState } from 'react';
import NodesStore from './components/Nodes/NodesStore'
import InvitationsPage from './pages/InvitationsPage';

function App() {
  const [user, setUser] = useState({id:0, name: "", email: "", password:"", token: "", refreshToken: "", logged: false })

  const value = useMemo(() => ({ user, setUser}), [user, setUser])

  return (
    <div className="App">
      <Router>
        <NodesStore>
          <UserContext.Provider value={{user, setUser}}>
            <Navbar />
            <Switch>
                <Route path='/' exact component={Home}/>
                <Route path='/login' exact component={Login} />
                <Route path='/sign-up' exact component={Signup}/>
                <Route path='/invitations' exact component={InvitationsPage}/>
                <Route path='/files' exact component={FilesPage} />
            </Switch>
          </UserContext.Provider>
        </NodesStore>
      </Router>
    </div>
  );
}

export default App;
