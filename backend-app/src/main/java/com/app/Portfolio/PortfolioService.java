
package com.app.Portfolio;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.app.User.User;
import com.app.User.UserService;
import com.app.WildcardResponse;
import com.app.ExternalAPIs.StockTimeSeriesAPI.Monthly.MonthlyController;
import com.app.ExternalAPIs.StockTimeSeriesAPI.Monthly.StockTimeSeriesMonthlyDTO;
import com.app.Position.Position;
import com.app.Position.PositionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PortfolioService {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MonthlyController monthlyController;

    @Autowired
    private PositionService positionService;

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

    public List<Map<String, Object>> computeCumPositions(List<Position> positions) {
        if (positions == null || positions.isEmpty()) {
            return new ArrayList<>(); // Return an empty list if positions is null or empty
        }
        // Group the positions by stock symbol
        Map<String, List<Position>> groupedPositions = positions.stream()
                .collect(Collectors.groupingBy(Position::getStockSymbol));

        // Initialize the list to store cumulative positions
        List<Map<String, Object>> cumPositions = new ArrayList<>();

        // Iterate through each group of positions (grouped by stock symbol)
        for (Map.Entry<String, List<Position>> entry : groupedPositions.entrySet()) {
            // The stock symbol for the current group
            String stockSymbol = entry.getKey();
            // The list of positions for the current stock symbol
            List<Position> symbolPositions = entry.getValue();

            // Fetch the stock sector from the first position of this stock symbol
            String stockSector = symbolPositions.get(0).getStockSector();

            // Compute the average price, excluding positions with "SELLTOCLOSE" action
            Double averagePrice = symbolPositions.stream()
                    .filter(p -> !"SELLTOCLOSE".equals(p.getPosition()))
                    .mapToDouble(p -> p.getPrice() * p.getQuantity())
                    .sum()
                    / symbolPositions.stream().filter(p -> !"SELLTOCLOSE".equals(p.getPosition()))
                            .mapToDouble(Position::getQuantity).sum();

            // Compute the total quantity, considering "SELLTOCLOSE" actions as negative
            // quantities
            Integer totalQuantity = symbolPositions.stream()
                    .mapToInt(p -> "SELLTOCLOSE".equals(p.getPosition()) ? -1 * p.getQuantity() : p.getQuantity())
                    .sum();

            // Fetch the monthly stock data for the current stock symbol
            StockTimeSeriesMonthlyDTO stockData = monthlyController.getMonthlyTimeSeries(stockSymbol);
            // Extract the monthly time series data from the DTO
            List<StockTimeSeriesMonthlyDTO.MonthlyStockData> monthlyTimeSeries = stockData.getTimeSeries();

            // Sort the time series data by date in descending order to get the most recent
            // data first
            monthlyTimeSeries.sort((data1, data2) -> data2.getDate().compareTo(data1.getDate()));

            // Get the most recent stock data
            StockTimeSeriesMonthlyDTO.MonthlyStockData recentStockData = monthlyTimeSeries.get(0);
            // Get the closing price from the most recent stock data
            Double recentStockPrice = recentStockData.getClose();

            // Calculate the current value of the stock position
            Double currentValue = recentStockPrice * totalQuantity;

            // Create a map to store the cumulative position data
            Map<String, Object> cumPosition = new HashMap<>();
            cumPosition.put("stockSymbol", stockSymbol);
            cumPosition.put("stockSector", stockSector);
            cumPosition.put("averagePrice", averagePrice);
            cumPosition.put("totalQuantity", totalQuantity);
            cumPosition.put("currentValue", currentValue);

            // Add the cumulative position map to the list
            cumPositions.add(cumPosition);
        }

        // Return the list of cumulative positions
        return cumPositions;
    }

    public Map<String, Double> computePortfolioHistoricalValue(Portfolio portfolio,
            MonthlyController monthlyController) {
        Map<String, Double> historicalValue = new HashMap<>();

        // 1. Determine the date range for computation
        List<Position> positions = portfolio.getPositions();
        if (positions == null || positions.isEmpty()) {
            return new HashMap<>(); // Return an empty map if positions is null or empty
        }
        Date oldestPositionDate = positions.stream()
                .min(Comparator.comparing(Position::getPositionAddDate))
                .orElseThrow(() -> new NoSuchElementException("No value present"))
                .getPositionAddDate();

        // 2. Initialize a map to store historical values for each stock symbol
        Map<String, Map<String, Double>> historicalValuesByStock = new HashMap<>();

        // 3. Compute Monthly Value for each position
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Position position : positions) {
            String stockSymbol = position.getStockSymbol();

            // Fetch the monthly time series data for the current position's stock symbol
            StockTimeSeriesMonthlyDTO stockData = monthlyController.getMonthlyTimeSeries(stockSymbol);
            List<StockTimeSeriesMonthlyDTO.MonthlyStockData> monthlyTimeSeries = stockData.getTimeSeries();

            // Initialize the historical values map for this stock symbol if it doesn't
            // exist
            historicalValuesByStock.computeIfAbsent(stockSymbol, k -> new HashMap<>());
            Map<String, Double> historicalValues = historicalValuesByStock.get(stockSymbol);

            // Compute the historical values for this stock symbol
            for (StockTimeSeriesMonthlyDTO.MonthlyStockData monthlyData : monthlyTimeSeries) {
                String date = monthlyData.getDate();
                double priceForMonth = monthlyData.getClose();
                double valueForMonth = priceForMonth * position.getQuantity();

                try {
                    if (sdf.parse(date).compareTo(oldestPositionDate) >= 0) {
                        historicalValues.put(date, historicalValues.getOrDefault(date, 0.0) + valueForMonth);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        // 4. Sum up the historical values of all positions for each month
        for (Map<String, Double> values : historicalValuesByStock.values()) {
            for (Map.Entry<String, Double> entry : values.entrySet()) {
                String date = entry.getKey();
                double value = entry.getValue();
                historicalValue.put(date, historicalValue.getOrDefault(date, 0.0) + value);
            }
        }

        // 5. Add capital USD to each month's total value
        for (Map.Entry<String, Double> entry : historicalValue.entrySet()) {
            entry.setValue(entry.getValue() + portfolio.getCapitalUSD());
        }

        return historicalValue;
    }

    // Depricated for now
    // public double computeCumValueForMonth(String date, Portfolio portfolio,
    // MonthlyController monthlyController) {
    // double monthlyValue = 0.0;

    // for (Position position : portfolio.getPositions()) {
    // StockTimeSeriesMonthlyDTO stockData =
    // monthlyController.getMonthlyTimeSeries(position.getStockSymbol());
    // Map<String, StockTimeSeriesMonthlyDTO.MonthlyStockData> monthlyTimeSeries =
    // stockData.getTimeSeries();

    // StockTimeSeriesMonthlyDTO.MonthlyStockData monthData =
    // monthlyTimeSeries.get(date);
    // double priceForMonth = monthData.getClose();

    // monthlyValue += priceForMonth * position.getQuantity();
    // }

    // return monthlyValue;
    // }
}
