
package com.app.Portfolio;

import java.util.List;
import java.util.Optional;
import com.app.User.User;
import com.app.User.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PortfolioService {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private UserService userService;
    
    // Method to add a portfolio
    public Portfolio addPortfolio(Portfolio portfolio) {
        System.out.println(portfolio);
        return portfolio;
        // User user = userService.getUser(portfolio.getUserId());
        // portfolio.setUser(user);
        // user.addPortfolio(portfolio);
        // return portfolioRepository.save(portfolio);
    }
    // public Portfolio addPortfolio(Portfolio portfolio) {
    //     return portfolioRepository.save(portfolio);
    // }

    // Method to update a portfolio
    public Portfolio updatePortfolio(Portfolio portfolio) {
        if (portfolioRepository.existsById(portfolio.getPorfolioID())) {
            return portfolioRepository.save(portfolio);
        }
        return null;
    }
    
    // Method to delete a portfolio
    public void deletePortfolio(int portfolioID) {
        portfolioRepository.deleteById(portfolioID);
    }
    
    // Method to retrieve a portfolio
    public Optional<Portfolio> getPortfolio(int portfolioID) {
        return portfolioRepository.findById(portfolioID);
    }
    
    // Method to retrieve all portfolios of a user
    public List<Portfolio> getAllPortfoliosByUser(User user) {
        return user.getPortfolios();
    }
}
