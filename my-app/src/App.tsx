import React, { useEffect } from 'react';
import { fetchData, postData } from './utils/api';
import { Routes, Route } from "react-router-dom";

import Home from './pages/Home';
import AdminHome from './pages/AdminHome';
import Audit from './pages/Audit';
import DataManagement from './pages/DataManagement';
import Order from './pages/Order';
import Portfolio from './pages/Portfolio';
import StockHome from './pages/StockHome';
import UserHome from './pages/UserHome';
import Registration from './pages/Registration';
import Stock from './pages/Stock';
import Sandbox from './pages/Sandbox';

import { AuthProvider } from './context/AuthContext';

import './App.css';


function App() {
  // New login code 


  return (
    <AuthProvider>
      <div className="App">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="adminhome" element={<AdminHome />} />
          <Route path="audit" element={<Audit />} />
          <Route path="datamanagement" element={<DataManagement />} />
          <Route path="order" element={<Order />} />
          <Route path="portfolio" element={<Portfolio />} />
          <Route path="stockhome" element={<StockHome />} />
          <Route path="userhome" element={<UserHome />} />
          <Route path="registration" element={<Registration />} />
          <Route path="stock" element={<Stock />} />
          <Route path="sandbox" element={<Sandbox />} />
        </Routes>
      </div>
    </AuthProvider>
  );
}

export default App;
