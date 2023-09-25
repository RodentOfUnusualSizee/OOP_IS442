import React, { useEffect } from 'react';
import { fetchData, postData } from './api/APIUtilsTemplate'; // Adjust the path as needed
import Header from './components/Header';
import logo from './logo.svg';
import './App.css';

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
      <Header></Header>
    </div>
  );
}

export default App;
