
package com.app.Portfolio;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Date;

import com.app.User.User;
import com.app.User.UserService;
import com.app.WildcardResponse;
import com.app.Position.Position;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PortfolioService {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private UserService userService;

    // Method to add a portfolio
    public WildcardResponse addPortfolio(Portfolio portfolio) {
        // Here you need the userId to be present in the Portfolio object or passed
        // separately.
        // return portfolio;
        portfolio.setCreatedTimestamp(new Date());
        portfolio.setLastModifiedTimestamp(new Date());
        return userService.addPortfolioToUser(portfolio.getUser().getId(), portfolio);
    }

    public Portfolio updatePortfolio(Portfolio portfolio) {
        if (portfolioRepository.existsById(portfolio.getPortfolioID())) {
            portfolio.setLastModifiedTimestamp(new Date());
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

    public static boolean checkPortfolioCapitalForNewPosition(Portfolio portfolio, Position position) {
        float currentValue = portfolio.getCapitalUSD();
        float newDiff = position.getPrice() * position.getQuantity();
        portfolio.setCapitalUSD(currentValue - newDiff);
        if (portfolio.getCapitalUSD() > 0) {
            return false;
        }
        return true;
    }
}
