import axios from 'axios';

// Define the base URL for your API
const BASE_URL = 'https://jsonplaceholder.typicode.com'; // Replace with your API URL

// Function to make a GET request
export async function fetchData() {
  try {
    const response = await axios.get(`${BASE_URL}/posts/1`);
    return response.data; // Return the data
  } catch (error) {
    throw error; // Throw an error if there's an issue
  }
}

// Function to make a POST request
export async function postData(data: any) {
  try {
    const response = await axios.post(`${BASE_URL}/posts`, data);
    return response.data; // Return the data
  } catch (error) {
    throw error; // Throw an error if there's an issue
  }
}
