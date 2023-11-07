import axios from 'axios';



const BASE_USER_URL = 'http://localhost:8080/api/user';
const BASE_PORTFOLIO_URL = 'http://localhost:8080/api/portfolio';
const BASE_STOCK_URL = 'http://localhost:8080/api/stock';
const BASE_URL = 'http://localhost:8080'

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

export async function getStockPrice(stockCode: any) {
  try {
    // http://localhost:8080/api/stock/dailyTimeSeries/TSLA
    const response = await axios.get(`${BASE_STOCK_URL}/dailyTimeSeries/${stockCode}`);
    return response.data 
  } catch (error) { 
    throw error;
  }
}

export async function getResetPasswordToken(email: any) {
  try {
      const response = await axios.get(`${BASE_USER_URL}/resetPassword/getToken/${email}`);
      return response.data;
  } catch (error) {
      throw error;
  }
}

export async function checkResetPasswordToken(email: any, token: any) {
  try {
      const response = await axios.get(`${BASE_USER_URL}/resetPassword/checkToken`, {
        params: {
          'email': email,
          'token': token
        }
      });

      return response.data;
      
  } catch (error) {
      throw error;
  }
}

export async function resetPassword(email: string, newPassword: string) {
  try {
    const response = await axios.put(`${BASE_USER_URL}/updatePassword`, null, {
      params: {
        email: email,
        newPassword: newPassword,
      }
    });

    return response.data;
  } catch (error) {
    throw error;
  }
}

export async function sendResetPasswordEmail(email: any, token: any) {
  try {
      let message = "Dear User,\n\n"
      +"We have received your password reset request. To reset your password, please click on the following link:\n\n"
      +"http://localhost:3000/resetpassword?email=" + email + "&token=" + token + "\n\n"
      +"Best regards,\n"
      +"Your Application Team";

      const response = await axios.get(`${BASE_URL}/sendEmail`, {
        params: {
          'toEmail': email,
          'subject': "Reset Password",
          'message': message
        }
      });

      return response.data;
      
  } catch (error) {
      throw error;
  }
}

export async function getTickerData(stockSymbol : any) {
  try { 
    const response = await axios.get(`${BASE_STOCK_URL}/tickerSearch/${stockSymbol}`);
    return response.data;
  } catch (error) {
    throw error;
  }
}

export async function comparePortfolio(firstPortfolioId: any, secondPortfolioId: any) {
  try {
    const response = await axios.get(`${BASE_PORTFOLIO_URL}/compare/${firstPortfolioId}/${secondPortfolioId}`);
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