
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
import com.app.ExternalAPIs.StockTimeSeriesAPI.Monthly.MonthlyService;
import com.app.ExternalAPIs.StockTimeSeriesAPI.Monthly.StockTimeSeriesMonthlyDTO;
import com.app.StockDataPoint.StockDataPoint;
import com.app.Portfolio.PortfolioComparisionDTOs.FinancialStatsDTO;
import com.app.Position.Position;
import com.app.Position.PositionService;
import com.app.StockDataPoint.StockDataPoint;

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
                    /
                    symbolPositions.stream()
                            .filter(p -> !"SELLTOCLOSE".equals(p.getPosition()))
                            .mapToDouble(Position::getQuantity).sum();

            // Compute the total quantity, considering "SELLTOCLOSE" actions as negative
            // quantities
            Integer totalQuantity = symbolPositions.stream()
                    .mapToInt(p -> "SELLTOCLOSE".equals(p.getPosition()) ? -1 * p.getQuantity() : p.getQuantity())
                    .sum();

            // Fetch the monthly stock data for the current stock symbol
            StockTimeSeriesMonthlyDTO stockData = monthlyController.getMonthlyTimeSeries(stockSymbol);
            // Extract the monthly time series data from the DTO
            List<StockDataPoint> monthlyTimeSeries = stockData.getTimeSeries();

            // Sort the time series data by date in descending order to get the most recent
            // data first
            monthlyTimeSeries.sort((data1, data2) -> data2.getDate().compareTo(data1.getDate()));

            // Get the most recent stock data
            StockDataPoint recentStockData = monthlyTimeSeries.get(0);
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
            List<StockDataPoint> monthlyTimeSeries = stockData.getTimeSeries();

            // Initialize the historical values map for this stock symbol if it doesn't
            // exist
            historicalValuesByStock.computeIfAbsent(stockSymbol, k -> new HashMap<>());
            Map<String, Double> historicalValues = historicalValuesByStock.get(stockSymbol);

            // Compute the historical values for this stock symbol
            for (StockDataPoint monthlyData : monthlyTimeSeries) {
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

    public FinancialStatsDTO transformPortfolioToFinancialStatsDTO(PortfolioDTO portfolio) {
        FinancialStatsDTO dto = new FinancialStatsDTO();

        dto.setCurrentTotalPortfolioValue(portfolio.getCurrentTotalPortfolioValue());
        dto.setPortfolioBeta(portfolio.getPortfolioBeta());
        dto.setInformationRatio(portfolio.getInformationRatio());
        dto.setQuarterlyReturns(portfolio.getQuarterlyReturns());
        dto.setAnnualizedReturnsPercentage(portfolio.getAnnualizedReturnsPercentage());
        dto.setQuarterlyReturnsPercentage(portfolio.getQuarterlyReturnsPercentage());

        return dto;
    }

    public PortfolioDTO transformPortfolioToDTO(Portfolio portfolio) {
        // 1. Initialize an empty list to store cumulative positions and set the initial
        // total portfolio value
        List<Map<String, Object>> cumPositions = new ArrayList<>();
        double currentTotalPortfolioValue = 0.0 + portfolio.getCapitalUSD();

        // 2. Fetch all positions from the portfolio
        List<Position> positions = portfolio.getPositions();
        if (positions != null && !positions.isEmpty()) {
            // 2.1. Compute the cumulative positions for this portfolio
            cumPositions = computeCumPositions(positions);

            // 2.2. Update the total portfolio value by summing up the current values of all
            // cumulative positions
            for (Map<String, Object> cumPosition : cumPositions) {
                currentTotalPortfolioValue += (Double) cumPosition.get("currentValue");
            }
        }

        // 3. Calculate the portfolio allocation by sector
        Map<String, Double> allocationBySector = new HashMap<>();
        if (!cumPositions.isEmpty()) {
            for (Map<String, Object> cumPosition : cumPositions) {
                double value = (Double) cumPosition.get("currentValue");
                String sector = Objects.toString(cumPosition.get("stockSector"), "Unknown Sector");

                allocationBySector.put(sector, allocationBySector.getOrDefault(sector, 0.0) + value);
            }
        }
        // 3.1. Convert sector allocations to percentages
        if (!allocationBySector.isEmpty()) {
            for (Map.Entry<String, Double> entry : allocationBySector.entrySet()) {
                allocationBySector.put(entry.getKey(), (entry.getValue() / currentTotalPortfolioValue) * 100);
            }
        }

        // 4. Calculate the portfolio allocation by geographical location
        Map<String, Double> allocationByGeographicalLocation = new HashMap<>();
        if (!cumPositions.isEmpty()) {
            for (Map<String, Object> cumPosition : cumPositions) {
                double value = (Double) cumPosition.get("currentValue");
                String geographicalLocation = Objects.toString(cumPosition.get("geographicalLocation"),
                        "Unknown Location");
                allocationByGeographicalLocation.put(geographicalLocation,
                        allocationByGeographicalLocation.getOrDefault(geographicalLocation, 0.0) + value);
            }
        }

        // 4.1. Convert geographical location allocations to percentages
        if (!allocationByGeographicalLocation.isEmpty()) {
            for (Map.Entry<String, Double> entry : allocationByGeographicalLocation.entrySet()) {
                allocationByGeographicalLocation.put(entry.getKey(),
                        (entry.getValue() / currentTotalPortfolioValue) * 100);
            }
        }

        // 5. Calculate the cash percentage in the portfolio
        double cashPercentage = (portfolio.getCapitalUSD() / currentTotalPortfolioValue) * 100;
        allocationBySector.put("CASH", cashPercentage);
        allocationByGeographicalLocation.put("CASH", cashPercentage);

        // 6. Compute the portfolio's historical value
        Map<String, Double> portfolioHistoricalValue = computePortfolioHistoricalValue(portfolio,
                monthlyController);

        // 7. Calculate Monthly Returns for the Portfolio
        Map<String, Double> portfolioMonthlyReturns = calculatePortfolioReturns(portfolioHistoricalValue);

        // 8. Fetch SPY Monthly Returns
        // You need to implement a method to get SPY data and transform it to monthly
        // returns
        Map<String, Double> spyMonthlyReturns = calculateSPYReturns();

        // 9. Calculate Portfolio Beta
        double portfolioBeta = calculatePortfolioBeta(portfolioMonthlyReturns, spyMonthlyReturns);

        // 10. Calculate Information Ratio
        double informationRatio = calculateInformationRatio(portfolioMonthlyReturns, spyMonthlyReturns);

        // 11. Create a Data Transfer Object (DTO) and set its properties
        PortfolioDTO dto = new PortfolioDTO(portfolio, cumPositions);
        dto.setPortfolioHistoricalValue(portfolioHistoricalValue);
        dto.setCurrentTotalPortfolioValue(currentTotalPortfolioValue);
        dto.setPortfolioAllocationBySector(allocationBySector);
        dto.setPortfolioAllocationByGeographicalLocation(allocationByGeographicalLocation);

        // 12. Compute returns and set them in the DTO
        Map<String, Object> returns = calculateReturns(dto);
        dto.setQuarterlyReturns((Map<String, String>) returns.get("quarterlyReturns"));
        dto.setQuarterlyReturnsPercentage((Map<String, String>) returns.get("quarterlyReturnsPercentage"));
        dto.setQuarterlyDateRanges((Map<String, String>) returns.get("quarterlyDateRanges"));
        dto.setAnnualizedReturnsPercentage((String) returns.get("annualizedReturnsPercentage"));

        // 13. Compute risk related rtaios and set them in the DTO
        dto.setPortfolioBeta(portfolioBeta);
        dto.setInformationRatio(informationRatio);

        String qoq = "N/A";
        String yoy = "N/A";
        String mom = "N/A";

        System.out.println("cumPositions");
        System.out.println(cumPositions);

        if (!cumPositions.isEmpty()) {
            // 14 Calculate YoY
            LocalDate oneYearAgo = LocalDate.now().minusYears(1);
            String oneYearAgoFormatted = oneYearAgo.format(DateTimeFormatter.ISO_DATE);
            Double valueOneYearAgo = getPortfolioValueAtDate(portfolioHistoricalValue, oneYearAgoFormatted);

            if (valueOneYearAgo != null) {
                yoy = String.format("%.2f%%", ((currentTotalPortfolioValue - valueOneYearAgo)
                        / valueOneYearAgo) * 100);
            }
            System.out.println("YoY Caluculated");
            // 15 Calculate QoQ
            Map<String, String> quarterlyDateRanges = (Map<String, String>) returns.get("quarterlyDateRanges");
            LocalDate currentDate = LocalDate.now();
            YearMonth currentYearMonth = YearMonth.from(currentDate);
            // Find the current quarter
            String currentQuarter = null;
            String startDateOfCurrentQuarter = null;
            List<String> quarters = new ArrayList<>(quarterlyDateRanges.keySet());

            for (String quarter : quarters) {
                if (!(quarterlyDateRanges.get(quarter).equals("N/A"))) {
                    String[] dateRange = quarterlyDateRanges.get(quarter).split(" to ");
                    YearMonth endDate = YearMonth.from(LocalDate.parse(dateRange[0], DateTimeFormatter.ISO_LOCAL_DATE));
                    YearMonth startDate = YearMonth
                            .from(LocalDate.parse(dateRange[1], DateTimeFormatter.ISO_LOCAL_DATE));

                    if ((currentYearMonth.equals(startDate) || currentYearMonth.isAfter(startDate))
                            && (currentYearMonth.equals(endDate) || currentYearMonth.isBefore(endDate))) {
                        currentQuarter = quarter;
                        startDateOfCurrentQuarter = dateRange[1];
                        break;
                    }
                }
            }
            Double previousQuarterValue = getPortfolioValueAtDate(portfolioHistoricalValue, startDateOfCurrentQuarter);

            if (previousQuarterValue != null) {
                double qoqValue = ((currentTotalPortfolioValue - previousQuarterValue) / previousQuarterValue) * 100;
                qoq = String.format("%.2f%%", qoqValue);

            }
            System.out.println("QoQ Caluculated");

            // 16 Calculate MoM
            LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
            String oneMonthAgoFormatted = oneMonthAgo.format(DateTimeFormatter.ISO_DATE);
            Double valueOneMonthAgo = getPortfolioValueAtDate(portfolioHistoricalValue, oneMonthAgoFormatted);

            if (valueOneMonthAgo != null) {
                mom = String.format("%.2f%%",
                        ((currentTotalPortfolioValue - valueOneMonthAgo) / valueOneMonthAgo) * 100);
            }

            System.out.println("MoM Caluculated");
        }

        // 17. Compute YoY,QoQ,MoM
        dto.setPortfolioYoY(yoy);
        dto.setPortfolioQoQ(qoq);
        dto.setPortfolioMoM(mom);

        // 18. Return the populated DTO
        return dto;
    }

    public Double getPortfolioValueAtDate(Map<String, Double> portfolioHistoricalValue, String date) {
        if (date == null) {
            return null;
        }
        // Extracting the yyyy-MM part from the date string
        String datePrefix = date.substring(0, 7);

        // because dd cannot match need to filter and with startwith
        Map.Entry<String, Double> entry = portfolioHistoricalValue.entrySet().stream()
                .filter(e -> e.getKey().startsWith(datePrefix))
                .findFirst()
                .orElse(null);

        return entry != null ? entry.getValue() : null;
    }

    @Autowired
    private MonthlyService monthlyService;

    public Map<String, Double> calculateSPYReturns() {
        StockTimeSeriesMonthlyDTO spyData = monthlyService.getMonthlyTimeSeriesProcessed("SPY");

        Map<String, Double> spyReturns = new HashMap<>();
        List<StockDataPoint> timeSeries = spyData.getTimeSeries();

        for (int i = 1; i < timeSeries.size(); i++) {
            String currentDate = timeSeries.get(i).getDate();
            double currentClose = timeSeries.get(i).getClose();
            double previousClose = timeSeries.get(i - 1).getClose();

            double returnPercentage = ((currentClose - previousClose) / previousClose) * 100;
            spyReturns.put(currentDate, returnPercentage);
        }

        return spyReturns;
    }

    public Map<String, Double> calculatePortfolioReturns(Map<String, Double> portfolioHistoricalValue) {
        Map<String, Double> portfolioReturns = new LinkedHashMap<>();
        String previousDate = null;
        double previousValue = 0;

        for (Map.Entry<String, Double> entry : portfolioHistoricalValue.entrySet()) {
            if (previousDate != null) {
                double returnValue = ((entry.getValue() - previousValue) / previousValue) * 100;
                portfolioReturns.put(entry.getKey(), returnValue);
            }
            previousDate = entry.getKey();
            previousValue = entry.getValue();
        }

        return portfolioReturns;
    }

    public double calculateCovariance(Map<String, Double> returns1, Map<String, Double> returns2) {
        double mean1 = returns1.values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);
        double mean2 = returns2.values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);

        double covariance = 0;
        int n = 0;

        for (String date : returns1.keySet()) {
            if (returns2.containsKey(date)) {
                covariance += (returns1.get(date) - mean1) * (returns2.get(date) - mean2);
                n++;
            }
        }

        return n > 1 ? covariance / (n - 1) : 0;
    }

    public double calculateVariance(Map<String, Double> returns) {
        double mean = returns.values().stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double variance = 0;
        int n = 0;

        for (double value : returns.values()) {
            variance += Math.pow(value - mean, 2);
            n++;
        }

        return n > 1 ? variance / (n - 1) : 0;
    }

    public double calculatePortfolioBeta(Map<String, Double> portfolioReturns, Map<String, Double> spyReturns) {
        double covariance = calculateCovariance(portfolioReturns, spyReturns);
        double variance = calculateVariance(spyReturns);

        return variance != 0 ? covariance / variance : 0;
    }

    public double calculateInformationRatio(Map<String, Double> portfolioReturns, Map<String, Double> spyReturns) {
        List<Double> excessReturns = new ArrayList<>();
        for (String date : portfolioReturns.keySet()) {
            if (spyReturns.containsKey(date)) {
                double excessReturn = portfolioReturns.get(date) - spyReturns.get(date);
                excessReturns.add(excessReturn);
            }
        }

        double meanExcessReturn = excessReturns.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double trackingError = Math.sqrt(
                excessReturns.stream().mapToDouble(val -> Math.pow(val - meanExcessReturn, 2)).average().orElse(0));

        return trackingError != 0 ? meanExcessReturn / trackingError : 0;
    }

    public FinancialStatsDTO calculateDifference(FinancialStatsDTO portfolio1Stats, FinancialStatsDTO portfolio2Stats) {
        FinancialStatsDTO differenceStats = new FinancialStatsDTO();

        // Calculate the differences and set the properties of differenceStats
        differenceStats.setCurrentTotalPortfolioValue(
                portfolio1Stats.getCurrentTotalPortfolioValue() - portfolio2Stats.getCurrentTotalPortfolioValue());
        differenceStats.setPortfolioBeta(portfolio1Stats.getPortfolioBeta() - portfolio2Stats.getPortfolioBeta());
        differenceStats
                .setInformationRatio(portfolio1Stats.getInformationRatio() - portfolio2Stats.getInformationRatio());

        // Calculate the differences for quarterly returns
        Map<String, String> quarterlyReturnsDifference = new HashMap<>();
        for (String quarter : portfolio1Stats.getQuarterlyReturns().keySet()) {
            double value1 = Double.parseDouble(portfolio1Stats.getQuarterlyReturns().get(quarter));
            double value2 = Double.parseDouble(portfolio2Stats.getQuarterlyReturns().getOrDefault(quarter, "0.0"));
            double difference = value1 - value2;
            quarterlyReturnsDifference.put(quarter, String.valueOf(difference));
        }
        differenceStats.setQuarterlyReturns(quarterlyReturnsDifference);

        // Calculate the difference for annualized returns percentage
        double annualizedReturnsDifference = Double
                .parseDouble(portfolio1Stats.getAnnualizedReturnsPercentage().replace("%", ""))
                - Double.parseDouble(portfolio2Stats.getAnnualizedReturnsPercentage().replace("%", ""));
        differenceStats.setAnnualizedReturnsPercentage(String.valueOf(annualizedReturnsDifference) + "%");

        // Calculate the differences for quarterly returns percentage
        Map<String, String> quarterlyReturnsPercentageDifference = new HashMap<>();
        for (String quarter : portfolio1Stats.getQuarterlyReturnsPercentage().keySet()) {
            double value1 = Double
                    .parseDouble(portfolio1Stats.getQuarterlyReturnsPercentage().get(quarter).replace("%", ""));
            double value2 = Double.parseDouble(
                    portfolio2Stats.getQuarterlyReturnsPercentage().getOrDefault(quarter, "0.00").replace("%", ""));
            double difference = value1 - value2;
            quarterlyReturnsPercentageDifference.put(quarter, String.valueOf(difference) + "%");
        }
        differenceStats.setQuarterlyReturnsPercentage(quarterlyReturnsPercentageDifference);

        return differenceStats;
    }

}
