import axios from 'axios';

export function roundTo(number: number, decimalPlaces: number) {
  const multiplier = Math.pow(10, decimalPlaces);
  return Math.round(number * multiplier) / multiplier;
}


const BASE_USER_URL = 'http://localhost:8080/api/user';
const BASE_PORTFOLIO_URL = 'http://localhost:8080/api/portfolio';
const BASE_STOCK_URL = 'http://localhost:8080/api/stock';

export async function registerUser(data: any) {
  try {
      const response = await axios.post(`${BASE_USER_URL}/create`, data);
      return response.data;
  } catch (error) {
      throw error;
  }
}


export async function loginUser(data: any) {
  try {
      const response = await axios.post(`${BASE_USER_URL}/login`, data);
      return response.data;
  } catch (error) {
      throw error;
  }
}


export async function createPortfolio(data: any) {
  try {
      const response = await axios.post(`${BASE_PORTFOLIO_URL}/create`, data);
      return response.data;
  } catch (error) {
      throw error;
  }
}


export async function createPortfolioPosition(portfolioId: any, data: any) {
  try {
      const response = await axios.post(`${BASE_PORTFOLIO_URL}/${portfolioId}/position/create`, data);
      return response.data;
  } catch (error) {
      throw error;
  }
}


export async function getPortfolioByUserId(userId: any) {
  try {
      const response = await axios.get(`${BASE_PORTFOLIO_URL}/getAllByUser/${userId}`);
      return response.data;
  } catch (error) {
      throw error;
  }
}

export async function getPortfolioByPortfolioId(portfolioId: any) {
  try {
    // localhost:8080/api/portfolio/get/1
    const response = await axios.get(`${BASE_PORTFOLIO_URL}/get/${portfolioId}`);
    return response.data;
  } catch (error) { 
    throw error;
  }
}

export async function getStockStats() {
  try {
    const response = await axios.get(`${BASE_STOCK_URL}/topGainerLoser`);
    return response.data;
  } catch (error) { 
    throw error;
  }
}

export async function getAllUsers() {
  try {
    const response = await axios.get(`${BASE_USER_URL}/get/all`);
    return response.data;
  } catch (error) { 
    throw error;
  }
}

export async function getStockOverview(symbol: string) {
  try {
    const response = await axios.post(`${BASE_STOCK_URL}/companyOverview/${symbol}`);
    return response.data;
  } catch (error) {
    throw error;
  }
}

export async function getActivityLogById(userId: any) {
  try {
      const response = await axios.get(`${BASE_USER_URL}/${userId}/activity-log`);
      return response.data;
  } catch (error) {
      throw error;
  }
}


export async function getStockNews(symbol: string) {
  try {
    const response = await axios.get(`${BASE_STOCK_URL}/NewsSentimentByStock/${symbol}`);
    return response.data;
  } catch (error) {
    throw error;
  }
}

export async function getStockHistoricalValues(symbol: string, days: number) {
  try {
    const response = await axios.get(`${BASE_STOCK_URL}/dailyTimeSeries/${days}/${symbol}`);
    return response.data;
  } catch (error) {
    throw error;
  }
}


export async function getAllEvents() {
  try {
    const response = await axios.get(`${BASE_USER_URL}/all/events`);
    return response.data;
  } catch (error) { 
    throw error;
  }
}

export async function getUserById(userId: any) {
  try {
      const response = await axios.get(`${BASE_USER_URL}/get/${userId}`);
      return response.data;
  } catch (error) {
      throw error;
  }
}

export async function createNewUserEvent(userId: any, data: any) {
  try {
      const response = await axios.post(`${BASE_USER_URL}/${userId}/add-event`, data)
      return response.data;
  } catch (error) {
      throw error;
  }
}

export async function getStockPrice(stockCode: any) {
  try {
    // http://localhost:8080/api/stock/dailyTimeSeries/TSLA
    const response = await axios.get(`${BASE_STOCK_URL}/dailyTimeSeries/${stockCode}`);
    return response.data 
  } catch (error) { 
    throw error;
  }
}