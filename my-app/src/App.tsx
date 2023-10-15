import React, { useEffect } from 'react';
import { fetchData, postData } from './api/APIUtilsTemplate'; 
import { Routes, Route } from "react-router-dom";

import Home from './pages/Home';
import AdminHome from './pages/AdminHome';
import Audit from "./pages/Audit";
import DataManagement from './pages/DataManagement';
import Order from './pages/Order';
import Portfolio from './pages/Portfolio';
import StockHome from './pages/StockHome';
import UserHome from './pages/UserHome';
import Registration from './pages/Registration';
import Stock from './pages/Stock';

import './App.css';


function App() {
  // SAMPLE POST AND GET REQUEST USED IN USEEFFECT CAN REMOVE AFTER U UNDERSTAND
  useEffect(() => {
    // Make a GET request
    fetchData()
      .then((data) => {
        // console.log('GET Request Response:', data);
      })
      .catch((error) => {
        console.error('Error making GET request:', error);
      });

    // Make a POST request
    const postPayload = {
      title: 'Sample Post',
      body: 'This is a sample post body.',
      userId: 1,
    };
    postData(postPayload)
      .then((data) => {
        // console.log('POST Request Response:', data);
      })
      .catch((error) => {
        console.error('Error making POST request:', error);
      });
  }, []);

  return (
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
      </Routes>
    </div>
  );
}

export default App;
