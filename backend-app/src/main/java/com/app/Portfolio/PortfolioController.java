
package com.app.Portfolio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import com.app.User.User;
import com.app.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    @Autowired
    private UserService userService;

    @Autowired
    private PortfolioService portfolioService;
    
    // Endpoint to create a new portfolio
    @PostMapping("/create")
    public Portfolio createPortfolio(@RequestBody Portfolio portfolio) {
        // return portfolio;
        return portfolioService.addPortfolio(portfolio);
    }
    
    // Endpoint to update an existing portfolio
    @PutMapping("/update")
    public Portfolio updatePortfolio(@RequestBody Portfolio portfolio) {
        return portfolioService.updatePortfolio(portfolio);
    }
    
    // Endpoint to delete a portfolio
    @DeleteMapping("/delete/{portfolioID}")
    public void deletePortfolio(@PathVariable int portfolioID) {
        portfolioService.deletePortfolio(portfolioID);
    }
    
    // Endpoint to retrieve a portfolio
    @GetMapping("/get/{portfolioID}")
    public Optional<Portfolio> getPortfolio(@PathVariable int portfolioID) {
        return portfolioService.getPortfolio(portfolioID);
    }
    
    // Endpoint to retrieve all portfolios of a user
    @GetMapping("/getAllByUser/{userID}")
    public List<Portfolio> getAllPortfoliosByUser(@PathVariable Long userID) {
        Optional<User> userOptional = userService.findById(userID);
        if (userOptional.isPresent()) {
            return portfolioService.getAllPortfoliosByUser(userOptional.get());
        }
    
        return new ArrayList<>(); 
    }
}
