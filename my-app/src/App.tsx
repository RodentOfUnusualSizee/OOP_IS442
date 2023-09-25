import React, { useEffect } from 'react';
import { fetchData, postData } from './api/APIUtilsTemplate'; // Adjust the path as needed
import Header from './components/Header';
import Footer from './components/Footer';
import logo from './logo.svg';
import './App.css';
import './tailwind.css';

function App() {
  // SAMPLE POST AND GET REQUEST USED IN USEEFFECT CAN REMOVE AFTER U UNDERSTAND
  useEffect(() => {
    // Make a GET request
    fetchData()
      .then((data) => {
        console.log('GET Request Response:', data);
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
        console.log('POST Request Response:', data);
      })
      .catch((error) => {
        console.error('Error making POST request:', error);
      });
  }, []);

  return (
    <div className="App">
      <Header management={false} userType="user" login={false}></Header>
      <div className="container mx-auto max-w-screen-xl h-screen bg-gray-200 rounded-l shadow border p-8 m-10 grid place-items-center">
        <div className = "">
          <img src="/images/gs-blue.png" className="w-20 mb-6 mx-auto"></img>
          <p className="text-4xl text-gray-700 font-bold mb-5">
              Portfolio Performance Analyser App
          </p>
          <p className="container mx-auto text-sm">
            <button className=" bg-gsblue60 hover:bg-gsblue70 focus:outline-none focus:ring-2 focus:ring-black w-20 mx-2 px-2 py-2 text-white rounded-sm font-light">Login</button>
            <button className=" bg-gsblue60 hover:bg-gsblue70 focus:outline-none focus:ring-2 focus:ring-black w-20 mx-2 px-2 py-2 text-white rounded-sm font-light">Register</button>
          </p>
        </div>

        <div className="hidden">
          <img src="/images/gs-blue.png" className="w-20 mb-6 mx-auto"></img>
          <p className="text-4xl text-gray-700 font-bold mb-5">
            Portfolio Performance Analyser App
          </p>
          <form className="bg-white shadow-md rounded p-8 m-10">

            <div className="mb-4">
              {/*
              <label className="block text-gray-700 text-sm font-bold mb-2" >
                User ID
              </label>
              */}
              <input className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight" id="username" type="text" placeholder="Enter ID">
              </input>
            </div>
            <div className="mb-4">
              {/* <label className="block text-gray-700 text-sm font-bold mb-2">
                Password
              </label> */}
              <input className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight" id="password" type="password" placeholder="Enter Password">
              </input>
              </div>
            <div className="">
              <button className= "bg-gsblue60 hover:bg-gsblue70 text-white font-bold w-30 py-2 px-2 rounded mx-2" type="button">
                User Login
              </button>

              <button className= "bg-gsblue60 hover:bg-gsblue70 text-white font-bold w-30 py-2 px-2 rounded mx-2" type="button">
                Admin Login
              </button>
            </div>
            <a className="inline-block align-baseline font-bold text-xs mt-2 text-gsblue60 hover:bg-gsblue50" href="#">
                Forgot Password?
            </a>
          </form>
        </div>
      </div>
      <Footer></Footer>
    </div>
  );
}

export default App;
