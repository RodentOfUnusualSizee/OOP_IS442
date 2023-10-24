
package com.app.Portfolio;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
            String stockGeographicalLocation = symbolPositions.get(0).getStockGeographicalLocation();

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
            cumPosition.put("geographicalLocation", stockGeographicalLocation);

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

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Map<String, Object> calculateReturns(PortfolioDTO portfolio) {
        Map<String, Object> returns = new HashMap<>();
        Map<String, Double> portfolioHistoricalValue = portfolio.getPortfolioHistoricalValue();

        // 1. Calculate Quarterly Returns, Quarterly Returns Percentage, and Date Ranges
        Map<String, Map<String, String>> quarterlyResults = calculateQuarterlyReturns(portfolioHistoricalValue);
        Map<String, String> quarterlyReturns = new HashMap<>();
        Map<String, String> quarterlyReturnsPercentage = new HashMap<>();
        Map<String, String> quarterlyDateRanges = new HashMap<>();

        for (Map.Entry<String, Map<String, String>> entry : quarterlyResults.entrySet()) {
            quarterlyReturns.put(entry.getKey(), entry.getValue().get("return"));
            quarterlyReturnsPercentage.put(entry.getKey(), entry.getValue().get("percentage"));
            quarterlyDateRanges.put(entry.getKey(), entry.getValue().get("dateRange"));
        }

        // 2. Calculate Annualized Returns and Annualized Returns Percentage
        String annualizedReturnsPercentage = calculateAnnualizedReturnsPercentage(portfolioHistoricalValue);

        // 3. Put results in the returns map
        returns.put("quarterlyReturns", quarterlyReturns);
        returns.put("quarterlyReturnsPercentage", quarterlyReturnsPercentage);
        returns.put("quarterlyDateRanges", quarterlyDateRanges);
        returns.put("annualizedReturnsPercentage", annualizedReturnsPercentage);

        return returns;
    }

    private Map<String, Map<String, String>> calculateQuarterlyReturns(Map<String, Double> historicalValues) {
        Map<String, Map<String, String>> quarterlyResults = new HashMap<>();
        List<String> quarters = Arrays.asList("Q1", "Q2", "Q3", "Q4");
        int currentYear = LocalDate.now().getYear();

        Map<String, LocalDate[]> quarterDateRanges = Map.of(
                "Q1", new LocalDate[] { LocalDate.of(currentYear, 1, 1), LocalDate.of(currentYear, 3, 31) },
                "Q2", new LocalDate[] { LocalDate.of(currentYear, 4, 1), LocalDate.of(currentYear, 6, 30) },
                "Q3", new LocalDate[] { LocalDate.of(currentYear, 7, 1), LocalDate.of(currentYear, 9, 30) },
                "Q4", new LocalDate[] { LocalDate.of(currentYear, 10, 1), LocalDate.of(currentYear, 12, 31) });

        for (String quarter : quarters) {
            LocalDate startDate = quarterDateRanges.get(quarter)[0];
            LocalDate endDate = quarterDateRanges.get(quarter)[1];

            Map.Entry<String, Double> startValueEntry = historicalValues.entrySet().stream()
                    .filter(e -> LocalDate.parse(e.getKey(), DATE_FORMATTER).isAfter(startDate.minusDays(1))
                            && LocalDate.parse(e.getKey(), DATE_FORMATTER).isBefore(endDate.plusDays(1)))
                    .min(Map.Entry.comparingByKey())
                    .orElse(null);

            Map.Entry<String, Double> endValueEntry = historicalValues.entrySet().stream()
                    .filter(e -> LocalDate.parse(e.getKey(), DATE_FORMATTER).isAfter(startDate.minusDays(1))
                            && LocalDate.parse(e.getKey(), DATE_FORMATTER).isBefore(endDate.plusDays(1)))
                    .max(Map.Entry.comparingByKey())
                    .orElse(null);

            if (startValueEntry != null && endValueEntry != null) {
                double returnAmount = endValueEntry.getValue() - startValueEntry.getValue();
                double returnPercentage = (returnAmount / startValueEntry.getValue()) * 100;
                String dateRange = endDate + " to " + startDate;
                Map<String, String> results = new HashMap<>();
                results.put("return", String.format("%.2f", returnAmount));
                results.put("percentage", String.format("%.2f%%", returnPercentage));
                results.put("dateRange", dateRange);
                quarterlyResults.put(quarter, results);
            } else {
                Map<String, String> results = new HashMap<>();
                results.put("return", "N/A");
                results.put("percentage", "N/A");
                results.put("dateRange", "N/A");
                quarterlyResults.put(quarter, results);
            }
        }
        return quarterlyResults;
    }

    // Calculate Annualized Returns Percentage
    private String calculateAnnualizedReturnsPercentage(Map<String, Double> historicalValues) {
        if (historicalValues == null || historicalValues.size() < 2) {
            return null; // Not enough data to calculate annualized returns
        }

        Map.Entry<String, Double> firstEntry = historicalValues.entrySet().iterator().next();
        Map.Entry<String, Double> lastEntry = null;

        for (Map.Entry<String, Double> entry : historicalValues.entrySet()) {
            lastEntry = entry;
        }

        if (firstEntry != null && lastEntry != null) {
            try {
                LocalDate startDate = LocalDate.parse(lastEntry.getKey(), DATE_FORMATTER);
                LocalDate endDate = LocalDate.parse(firstEntry.getKey(), DATE_FORMATTER);
                double startValue = lastEntry.getValue();
                double endValue = firstEntry.getValue();

                long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
                if (daysBetween == 0) {
                    return null; // Portfolio has no historical data or only one day of data
                }

                double yearsBetween = (double) daysBetween / 365;
                double annualizedReturn = (Math.pow(endValue / startValue, 1 / yearsBetween) - 1) * 100;
                return String.format("%.2f%%", annualizedReturn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
