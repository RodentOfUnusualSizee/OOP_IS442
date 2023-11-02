import React, { useEffect } from 'react';
import { Routes, Route } from "react-router-dom";

import Home from './pages/Home';
import AdminHome from './pages/AdminHome';
import Audit from './pages/Audit';
import Loading from './pages/Loading';
import NoStock from './pages/NoStock';
import Portfolio from './pages/Portfolio';
import StockHome from './pages/StockHome';
import UserHome from './pages/UserHome';
import Registration from './pages/Registration';
import Stock from './pages/Stock';
import Sandbox from './pages/Sandbox';
import StockRecord from './pages/StockRecord';
import ResetPassword from './pages/ResetPassword';

import { AuthProvider } from './context/AuthContext';

// import ProtectedWrapper from './components/ProtectedWrapper';

import './App.css';

function App() {
  return (
    <AuthProvider>
      <div className="App">
        <Routes>
          <Route path="/" element={<Home />} />
          {/* <Route path="adminhome" element={<ProtectedWrapper element={<AdminHome/>}/>} />
          <Route path="audit" element={<ProtectedWrapper element={<Audit/>}/>} />
          <Route path="datamanagement"element={<ProtectedWrapper element={<DataManagement/>}/>} />
          <Route path="order" element={<ProtectedWrapper element={<Order/>}/>} />
          <Route path="portfolio" element={<ProtectedWrapper element={<Portfolio/>}/>} />
          <Route path="stockhome" element={<ProtectedWrapper element={<StockHome/>}/>} />
          <Route path="registration" element={<Registration />} />
          <Route path="stock" element={<ProtectedWrapper element={<Stock/>}/>} /> */}
          <Route path="adminhome" element={<AdminHome/>} />
          <Route path="audit"element={<Audit/>} />
          <Route path="portfolio" element={<Portfolio/>}/>
          <Route path="stockhome" element={<StockHome/>} />
          <Route path="userhome" element={<UserHome/>}/>
          <Route path="registration" element={<Registration />} />
          <Route path="stock" element={<Stock/>}/>
          <Route path="sandbox" element={<Sandbox />} />
          <Route path="stockrecord" element={<StockRecord />} />
          <Route path="resetpassword" element={<ResetPassword />} />
          <Route path="loading" element={<Loading/>} />
          <Route path="nostock" element={<NoStock/>} />
        </Routes>
      </div>
    </AuthProvider>
  );
}

export default App;
