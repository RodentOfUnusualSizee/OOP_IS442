package com.app.Portfolio;

import com.app.ExternalAPIs.StockTimeSeriesAPI.Monthly.MonthlyController;
import com.app.ExternalAPIs.StockTimeSeriesAPI.Monthly.MonthlyService;
import com.app.ExternalAPIs.StockTimeSeriesAPI.Monthly.StockTimeSeriesMonthlyDTO;
import com.app.Portfolio.PortfolioComparisionDTOs.FinancialStatsDTO;
import com.app.Position.Position;
import com.app.Position.PositionService;
import com.app.StockDataPoint.StockDataPoint;
import com.app.User.User;
import com.app.User.UserService;
import com.app.WildcardResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service layer class that manages portfolio operations.
 * It offers methods to add, update, delete, and retrieve portfolios.
 */
@Service
public class PortfolioService {

  @Autowired
  private PortfolioRepository portfolioRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private MonthlyController monthlyController;

  @Autowired
  private MonthlyService monthlyService;

  /**
   * Adds a portfolio to the system and associates it with a user.
   * 
   * @param portfolio The portfolio to add, not null, expecting user id to be set.
   * @return WildcardResponse A response object indicating the result of the operation.
   */
  public WildcardResponse addPortfolio(Portfolio portfolio) {
    // Here you need the userId to be present in the Portfolio object or passed
    // separately.
    // return portfolio;
    portfolio.setCreatedTimestamp(new Date());
    portfolio.setLastModifiedTimestamp(new Date());
    return userService.addPortfolioToUser(
      portfolio.getUser().getId(),
      portfolio
    );
  }

  /**
   * Updates an existing portfolio.
   * 
   * @param portfolio The portfolio with updated information, not null.
   * @return Portfolio The updated portfolio if it exists, otherwise null.
   */
  public Portfolio updatePortfolio(Portfolio portfolio) {
    if (portfolioRepository.existsById(portfolio.getPortfolioID())) {
      portfolio.setLastModifiedTimestamp(new Date());
      return portfolioRepository.save(portfolio);
    }
    return null;
  }

  /**
   * Deletes a portfolio by its ID.
   * 
   * @param portfolioID The ID of the portfolio to delete.
   */
  public void deletePortfolio(int portfolioID) {
    portfolioRepository.deleteById(portfolioID);
  }

  /**
   * Retrieves a portfolio by its ID.
   * 
   * @param portfolioID The ID of the portfolio to retrieve.
   * @return Optional&lt;Portfolio&gt; The portfolio if found, otherwise an empty Optional.
   */
  public Optional<Portfolio> getPortfolio(int portfolioID) {
    return portfolioRepository.findById(portfolioID);
  }

  /**
   * Retrieves all portfolios belonging to a user.
   * 
   * @param user The user whose portfolios to retrieve, not null.
   * @return List&lt;Portfolio&gt; A list of portfolios belonging to the given user.
   */
  public List<Portfolio> getAllPortfoliosByUser(User user) {
    return user.getPortfolios();
  }

  /**
   * Checks if the portfolio has enough capital to add a new position.
   * This method subtracts the value of the new position (price * quantity) from
   * the current capital to simulate adding the position to the portfolio.
   * 
   * Note: This method will actually modify the portfolio's capital by subtracting
   * the cost of the new position. This side effect should be considered when calling
   * this method as it could potentially lead to incorrect capital values if not
   * handled properly.
   * 
   * @param portfolio The portfolio where the new position is to be added.
   * @param position  The new position to be added to the portfolio.
   * @return {@code true} if the portfolio has enough capital after subtracting
   *         the cost of the new position, {@code false} otherwise.
   */
  public static boolean checkPortfolioCapitalForNewPosition(
    Portfolio portfolio,
    Position position
  ) {
    float currentValue = portfolio.getCapitalUSD();
    float newDiff = position.getPrice() * position.getQuantity();
    portfolio.setCapitalUSD(currentValue - newDiff);
    if (portfolio.getCapitalUSD() > 0) {
      return false;
    }
    return true;
  }

  /**
   * Computes cumulative positions based on a list of individual positions.
   * This includes aggregating the positions by stock symbol and computing
   * values such as average price, total quantity, and current value. It
   * also enriches the position data with sector and geographical location.
   *
   * @param positions The list of positions to compute cumulative data for.
   * @return A list of maps where each map represents cumulative data for a unique stock symbol.
   */
  public List<Map<String, Object>> computeCumPositions(
    List<Position> positions
  ) {
    if (positions == null || positions.isEmpty()) {
      return new ArrayList<>(); // Return an empty list if positions is null or empty
    }
    // Group the positions by stock symbol
    Map<String, List<Position>> groupedPositions = positions
      .stream()
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
      String stockGeographicalLocation = symbolPositions
        .get(0)
        .getStockGeographicalLocation();

      // Compute the average price, excluding positions with "SELLTOCLOSE" action
      Double averagePrice =
        symbolPositions
          .stream()
          .filter(p -> !"SELLTOCLOSE".equals(p.getPosition()))
          .mapToDouble(p -> p.getPrice() * p.getQuantity())
          .sum() /
        symbolPositions
          .stream()
          .filter(p -> !"SELLTOCLOSE".equals(p.getPosition()))
          .mapToDouble(Position::getQuantity)
          .sum();

      // Compute the total quantity, considering "SELLTOCLOSE" actions as negative
      // quantities
      Integer totalQuantity = symbolPositions
        .stream()
        .mapToInt(p ->
          "SELLTOCLOSE".equals(p.getPosition())
            ? -1 * p.getQuantity()
            : p.getQuantity()
        )
        .sum();

      // Fetch the monthly stock data for the current stock symbol
      StockTimeSeriesMonthlyDTO stockData = monthlyController.getMonthlyTimeSeries(
        stockSymbol
      );
      // Extract the monthly time series data from the DTO
      List<StockDataPoint> monthlyTimeSeries = stockData.getTimeSeries();

      // Sort the time series data by date in descending order to get the most recent
      // data first
      monthlyTimeSeries.sort((data1, data2) ->
        data2.getDate().compareTo(data1.getDate())
      );

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

  /**
   * Computes the historical value of a portfolio based on the monthly closing prices
   * of the stocks in the portfolio. This calculation takes into account the quantity
   * of each position at different points in time to give a historical perspective of
   * the portfolio's value.
   *
   * @param portfolio           The portfolio for which historical values are being computed.
   * @param monthlyController   The controller used to retrieve monthly stock data.
   * @return A map where each key is a date (in YYYY-MM-DD format) and the value is the
   *         total historical value of the portfolio for that date.
   */
  public Map<String, Double> computePortfolioHistoricalValue(
    Portfolio portfolio,
    MonthlyController monthlyController
  ) {
    Map<String, Double> historicalValue = new HashMap<>();

    // 1. Determine the date range for computation
    List<Position> positions = portfolio.getPositions();
    if (positions == null || positions.isEmpty()) {
      return new HashMap<>(); // Return an empty map if positions is null or empty
    }
    Date oldestPositionDate = positions
      .stream()
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
      StockTimeSeriesMonthlyDTO stockData = monthlyController.getMonthlyTimeSeries(
        stockSymbol
      );
      List<StockDataPoint> monthlyTimeSeries = stockData.getTimeSeries();

      // Initialize the historical values map for this stock symbol if it doesn't
      // exist
      historicalValuesByStock.computeIfAbsent(
        stockSymbol,
        k -> new HashMap<>()
      );
      Map<String, Double> historicalValues = historicalValuesByStock.get(
        stockSymbol
      );

      // Compute the historical values for this stock symbol
      for (StockDataPoint monthlyData : monthlyTimeSeries) {
        String date = monthlyData.getDate();
        double priceForMonth = monthlyData.getClose();
        double valueForMonth = priceForMonth * position.getQuantity();

        try {
          if (sdf.parse(date).compareTo(oldestPositionDate) >= 0) {
            historicalValues.put(
              date,
              historicalValues.getOrDefault(date, 0.0) + valueForMonth
            );
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
        historicalValue.put(
          date,
          historicalValue.getOrDefault(date, 0.0) + value
        );
      }
    }

    // 5. Add capital USD to each month's total value
    for (Map.Entry<String, Double> entry : historicalValue.entrySet()) {
      entry.setValue(entry.getValue() + portfolio.getCapitalUSD());
    }

    return historicalValue;
  }

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(
    "yyyy-MM-dd"
  );

  /**
   * Calculates the returns of a portfolio including quarterly returns,
   * quarterly returns percentage, annualized returns, and corresponding
   * date ranges. The calculations are based on the historical value of the
   * portfolio over time.
   *
   * @param portfolio A PortfolioDTO object that includes historical value data.
   * @return A map containing various types of return metrics such as quarterly
   *         returns, quarterly returns percentage, and annualized returns percentage.
   */
  public Map<String, Object> calculateReturns(PortfolioDTO portfolio) {
    Map<String, Object> returns = new HashMap<>();
    Map<String, Double> portfolioHistoricalValue = portfolio.getPortfolioHistoricalValue();

    // 1. Calculate Quarterly Returns, Quarterly Returns Percentage, and Date Ranges
    Map<String, Map<String, String>> quarterlyResults = calculateQuarterlyReturns(
      portfolioHistoricalValue
    );
    Map<String, String> quarterlyReturns = new HashMap<>();
    Map<String, String> quarterlyReturnsPercentage = new HashMap<>();
    Map<String, String> quarterlyDateRanges = new HashMap<>();

    for (Map.Entry<String, Map<String, String>> entry : quarterlyResults.entrySet()) {
      quarterlyReturns.put(entry.getKey(), entry.getValue().get("return"));
      quarterlyReturnsPercentage.put(
        entry.getKey(),
        entry.getValue().get("percentage")
      );
      quarterlyDateRanges.put(
        entry.getKey(),
        entry.getValue().get("dateRange")
      );
    }

    // 2. Calculate Annualized Returns and Annualized Returns Percentage
    String annualizedReturnsPercentage = calculateAnnualizedReturnsPercentage(
      portfolioHistoricalValue
    );

    // 3. Put results in the returns map
    returns.put("quarterlyReturns", quarterlyReturns);
    returns.put("quarterlyReturnsPercentage", quarterlyReturnsPercentage);
    returns.put("quarterlyDateRanges", quarterlyDateRanges);
    returns.put("annualizedReturnsPercentage", annualizedReturnsPercentage);

    return returns;
  }

  private Map<String, Map<String, String>> calculateQuarterlyReturns(
    Map<String, Double> historicalValues
  ) {
    Map<String, Map<String, String>> quarterlyResults = new HashMap<>();
    List<String> quarters = Arrays.asList("Q1", "Q2", "Q3", "Q4");
    int currentYear = LocalDate.now().getYear();

    Map<String, LocalDate[]> quarterDateRanges = Map.of(
      "Q1",
      new LocalDate[] {
        LocalDate.of(currentYear, 1, 1),
        LocalDate.of(currentYear, 3, 31),
      },
      "Q2",
      new LocalDate[] {
        LocalDate.of(currentYear, 4, 1),
        LocalDate.of(currentYear, 6, 30),
      },
      "Q3",
      new LocalDate[] {
        LocalDate.of(currentYear, 7, 1),
        LocalDate.of(currentYear, 9, 30),
      },
      "Q4",
      new LocalDate[] {
        LocalDate.of(currentYear, 10, 1),
        LocalDate.of(currentYear, 12, 31),
      }
    );

    for (String quarter : quarters) {
      LocalDate startDate = quarterDateRanges.get(quarter)[0];
      LocalDate endDate = quarterDateRanges.get(quarter)[1];

      Map.Entry<String, Double> startValueEntry = historicalValues
        .entrySet()
        .stream()
        .filter(e ->
          LocalDate
            .parse(e.getKey(), DATE_FORMATTER)
            .isAfter(startDate.minusDays(1)) &&
          LocalDate
            .parse(e.getKey(), DATE_FORMATTER)
            .isBefore(endDate.plusDays(1))
        )
        .min(Map.Entry.comparingByKey())
        .orElse(null);

      Map.Entry<String, Double> endValueEntry = historicalValues
        .entrySet()
        .stream()
        .filter(e ->
          LocalDate
            .parse(e.getKey(), DATE_FORMATTER)
            .isAfter(startDate.minusDays(1)) &&
          LocalDate
            .parse(e.getKey(), DATE_FORMATTER)
            .isBefore(endDate.plusDays(1))
        )
        .max(Map.Entry.comparingByKey())
        .orElse(null);

      if (startValueEntry != null && endValueEntry != null) {
        double returnAmount =
          endValueEntry.getValue() - startValueEntry.getValue();
        double returnPercentage =
          (returnAmount / startValueEntry.getValue()) * 100;
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
  private String calculateAnnualizedReturnsPercentage(
    Map<String, Double> historicalValues
  ) {
    if (historicalValues == null || historicalValues.size() < 2) {
      return null; // Not enough data to calculate annualized returns
    }

    Map.Entry<String, Double> firstEntry = historicalValues
      .entrySet()
      .iterator()
      .next();
    Map.Entry<String, Double> lastEntry = null;

    for (Map.Entry<String, Double> entry : historicalValues.entrySet()) {
      lastEntry = entry;
    }

    if (firstEntry != null && lastEntry != null) {
      try {
        LocalDate startDate = LocalDate.parse(
          lastEntry.getKey(),
          DATE_FORMATTER
        );
        LocalDate endDate = LocalDate.parse(
          firstEntry.getKey(),
          DATE_FORMATTER
        );
        double startValue = lastEntry.getValue();
        double endValue = firstEntry.getValue();

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        if (daysBetween == 0) {
          return null; // Portfolio has no historical data or only one day of data
        }

        double yearsBetween = (double) daysBetween / 365;
        double annualizedReturn =
          (Math.pow(endValue / startValue, 1 / yearsBetween) - 1) * 100;
        return String.format("%.2f%%", annualizedReturn);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  /**
   * Transforms a given PortfolioDTO into a FinancialStatsDTO.
   * This method maps the financial statistics and metrics from the portfolio
   * into a new Data Transfer Object (DTO) that is specifically structured for financial statistics.
   *
   * @param portfolio The PortfolioDTO object containing the portfolio data to be transformed.
   * @return A FinancialStatsDTO containing the relevant financial metrics extracted from the portfolio.
   */
  public FinancialStatsDTO transformPortfolioToFinancialStatsDTO(
    PortfolioDTO portfolio
  ) {
    FinancialStatsDTO dto = new FinancialStatsDTO();

    dto.setCurrentTotalPortfolioValue(
      portfolio.getCurrentTotalPortfolioValue()
    );
    dto.setPortfolioBeta(portfolio.getPortfolioBeta());
    dto.setInformationRatio(portfolio.getInformationRatio());
    dto.setQuarterlyReturns(portfolio.getQuarterlyReturns());
    dto.setAnnualizedReturnsPercentage(
      portfolio.getAnnualizedReturnsPercentage()
    );
    dto.setQuarterlyReturnsPercentage(
      portfolio.getQuarterlyReturnsPercentage()
    );

    return dto;
  }

  /**
   * Transforms a Portfolio object into a PortfolioDTO (Data Transfer Object) with
   * additional calculated fields such as cumulative positions, sector and geographical
   * allocations, historical values, and performance metrics.
   *
   * @param portfolio the Portfolio object to be transformed into a DTO
   * @return a fully populated PortfolioDTO with calculated fields
   */
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
        String sector = Objects.toString(
          cumPosition.get("stockSector"),
          "Unknown Sector"
        );

        allocationBySector.put(
          sector,
          allocationBySector.getOrDefault(sector, 0.0) + value
        );
      }
    }
    // 3.1. Convert sector allocations to percentages
    if (!allocationBySector.isEmpty()) {
      for (Map.Entry<String, Double> entry : allocationBySector.entrySet()) {
        allocationBySector.put(
          entry.getKey(),
          (entry.getValue() / currentTotalPortfolioValue) * 100
        );
      }
    }

    // 4. Calculate the portfolio allocation by geographical location
    Map<String, Double> allocationByGeographicalLocation = new HashMap<>();
    if (!cumPositions.isEmpty()) {
      for (Map<String, Object> cumPosition : cumPositions) {
        double value = (Double) cumPosition.get("currentValue");
        String geographicalLocation = Objects.toString(
          cumPosition.get("geographicalLocation"),
          "Unknown Location"
        );
        allocationByGeographicalLocation.put(
          geographicalLocation,
          allocationByGeographicalLocation.getOrDefault(
            geographicalLocation,
            0.0
          ) +
          value
        );
      }
    }

    // 4.1. Convert geographical location allocations to percentages
    if (!allocationByGeographicalLocation.isEmpty()) {
      for (Map.Entry<String, Double> entry : allocationByGeographicalLocation.entrySet()) {
        allocationByGeographicalLocation.put(
          entry.getKey(),
          (entry.getValue() / currentTotalPortfolioValue) * 100
        );
      }
    }

    // 5. Calculate the cash percentage in the portfolio
    double cashPercentage =
      (portfolio.getCapitalUSD() / currentTotalPortfolioValue) * 100;
    allocationBySector.put("CASH", cashPercentage);
    allocationByGeographicalLocation.put("CASH", cashPercentage);

    // 6. Compute the portfolio's historical value
    Map<String, Double> portfolioHistoricalValue = computePortfolioHistoricalValue(
      portfolio,
      monthlyController
    );

    // 7. Calculate Monthly Returns for the Portfolio
    Map<String, Double> portfolioMonthlyReturns = calculatePortfolioReturns(
      portfolioHistoricalValue
    );

    // 8. Fetch SPY Monthly Returns
    // You need to implement a method to get SPY data and transform it to monthly
    // returns
    Map<String, Double> spyMonthlyReturns = calculateSPYReturns();

    // 9. Calculate Portfolio Beta
    double portfolioBeta = calculatePortfolioBeta(
      portfolioMonthlyReturns,
      spyMonthlyReturns
    );

    // 10. Calculate Information Ratio
    double informationRatio = calculateInformationRatio(
      portfolioMonthlyReturns,
      spyMonthlyReturns
    );

    // 11. Create a Data Transfer Object (DTO) and set its properties
    PortfolioDTO dto = new PortfolioDTO(portfolio, cumPositions);
    dto.setPortfolioHistoricalValue(portfolioHistoricalValue);
    dto.setCurrentTotalPortfolioValue(currentTotalPortfolioValue);
    dto.setPortfolioAllocationBySector(allocationBySector);
    dto.setPortfolioAllocationByGeographicalLocation(
      allocationByGeographicalLocation
    );

    // 12. Compute returns and set them in the DTO
    Map<String, Object> returns = calculateReturns(dto);
    dto.setQuarterlyReturns(
      (Map<String, String>) returns.get("quarterlyReturns")
    );
    dto.setQuarterlyReturnsPercentage(
      (Map<String, String>) returns.get("quarterlyReturnsPercentage")
    );
    dto.setQuarterlyDateRanges(
      (Map<String, String>) returns.get("quarterlyDateRanges")
    );
    dto.setAnnualizedReturnsPercentage(
      (String) returns.get("annualizedReturnsPercentage")
    );

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
      String oneYearAgoFormatted = oneYearAgo.format(
        DateTimeFormatter.ISO_DATE
      );
      Double valueOneYearAgo = getPortfolioValueAtDate(
        portfolioHistoricalValue,
        oneYearAgoFormatted
      );

      if (valueOneYearAgo != null) {
        yoy =
          String.format(
            "%.2f%%",
            ((currentTotalPortfolioValue - valueOneYearAgo) / valueOneYearAgo) *
            100
          );
      }
      System.out.println("YoY Caluculated");
      // 15 Calculate QoQ
      Map<String, String> quarterlyDateRanges = (Map<String, String>) returns.get(
        "quarterlyDateRanges"
      );
      LocalDate currentDate = LocalDate.now();
      YearMonth currentYearMonth = YearMonth.from(currentDate);
      // Find the current quarter
      String currentQuarter = null;
      String startDateOfCurrentQuarter = null;
      List<String> quarters = new ArrayList<>(quarterlyDateRanges.keySet());

      for (String quarter : quarters) {
        if (!(quarterlyDateRanges.get(quarter).equals("N/A"))) {
          String[] dateRange = quarterlyDateRanges.get(quarter).split(" to ");
          YearMonth endDate = YearMonth.from(
            LocalDate.parse(dateRange[0], DateTimeFormatter.ISO_LOCAL_DATE)
          );
          YearMonth startDate = YearMonth.from(
            LocalDate.parse(dateRange[1], DateTimeFormatter.ISO_LOCAL_DATE)
          );

          if (
            (
              currentYearMonth.equals(startDate) ||
              currentYearMonth.isAfter(startDate)
            ) &&
            (
              currentYearMonth.equals(endDate) ||
              currentYearMonth.isBefore(endDate)
            )
          ) {
            currentQuarter = quarter;
            startDateOfCurrentQuarter = dateRange[1];
            break;
          }
        }
      }
      Double previousQuarterValue = getPortfolioValueAtDate(
        portfolioHistoricalValue,
        startDateOfCurrentQuarter
      );

      if (previousQuarterValue != null) {
        double qoqValue =
          (
            (currentTotalPortfolioValue - previousQuarterValue) /
            previousQuarterValue
          ) *
          100;
        qoq = String.format("%.2f%%", qoqValue);
      }
      System.out.println("QoQ Caluculated");

      // 16 Calculate MoM
      LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
      String oneMonthAgoFormatted = oneMonthAgo.format(
        DateTimeFormatter.ISO_DATE
      );
      Double valueOneMonthAgo = getPortfolioValueAtDate(
        portfolioHistoricalValue,
        oneMonthAgoFormatted
      );

      if (valueOneMonthAgo != null) {
        mom =
          String.format(
            "%.2f%%",
            (
              (currentTotalPortfolioValue - valueOneMonthAgo) / valueOneMonthAgo
            ) *
            100
          );
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

  /**
   * Retrieves the historical portfolio value for a specific date. The date should
   * be formatted as "YYYY-MM-DD". If the exact date is not found, it attempts to
   * match the closest date starting with the same "YYYY-MM" prefix.
   *
   * @param portfolioHistoricalValue a map of date strings to historical portfolio values
   * @param date                     the date string to retrieve the historical value for
   * @return the historical value of the portfolio on the specified date, or null if not found
   */
  public Double getPortfolioValueAtDate(
    Map<String, Double> portfolioHistoricalValue,
    String date
  ) {
    if (date == null) {
      return null;
    }
    // Extracting the yyyy-MM part from the date string
    String datePrefix = date.substring(0, 7);

    // because dd cannot match need to filter and with startwith
    Map.Entry<String, Double> entry = portfolioHistoricalValue
      .entrySet()
      .stream()
      .filter(e -> e.getKey().startsWith(datePrefix))
      .findFirst()
      .orElse(null);

    return entry != null ? entry.getValue() : null;
  }

  /**
   * Calculates the monthly returns for the SPY ETF by comparing the close values
   * of each month with the previous month.
   *
   * @return a map where each key is a date string and each value is the return percentage for that month
   */
  public Map<String, Double> calculateSPYReturns() {
    StockTimeSeriesMonthlyDTO spyData = monthlyService.getMonthlyTimeSeriesProcessed(
      "SPY"
    );

    Map<String, Double> spyReturns = new HashMap<>();
    List<StockDataPoint> timeSeries = spyData.getTimeSeries();

    for (int i = 1; i < timeSeries.size(); i++) {
      String currentDate = timeSeries.get(i).getDate();
      double currentClose = timeSeries.get(i).getClose();
      double previousClose = timeSeries.get(i - 1).getClose();

      double returnPercentage =
        ((currentClose - previousClose) / previousClose) * 100;
      spyReturns.put(currentDate, returnPercentage);
    }

    return spyReturns;
  }

  /**
   * Calculates the monthly returns of a portfolio given its historical values.
   * The return for each month is computed as the percentage change from the previous month.
   *
   * @param portfolioHistoricalValue a linked map of date strings to historical values of the portfolio
   * @return a map where each key is a date string and each value is the return percentage for that month
   */
  public Map<String, Double> calculatePortfolioReturns(
    Map<String, Double> portfolioHistoricalValue
  ) {
    Map<String, Double> portfolioReturns = new LinkedHashMap<>();
    String previousDate = null;
    double previousValue = 0;

    for (Map.Entry<String, Double> entry : portfolioHistoricalValue.entrySet()) {
      if (previousDate != null) {
        double returnValue =
          ((entry.getValue() - previousValue) / previousValue) * 100;
        portfolioReturns.put(entry.getKey(), returnValue);
      }
      previousDate = entry.getKey();
      previousValue = entry.getValue();
    }

    return portfolioReturns;
  }

  /**
   * Calculates the covariance between two sets of returns. This is used in the
   * calculation of portfolio beta.
   *
   * @param returns1 the first set of returns
   * @param returns2 the second set of returns
   * @return the covariance of the two sets of returns
   */
  public double calculateCovariance(
    Map<String, Double> returns1,
    Map<String, Double> returns2
  ) {
    double mean1 = returns1
      .values()
      .stream()
      .mapToDouble(Double::doubleValue)
      .average()
      .orElse(0);
    double mean2 = returns2
      .values()
      .stream()
      .mapToDouble(Double::doubleValue)
      .average()
      .orElse(0);

    double covariance = 0;
    int n = 0;

    for (String date : returns1.keySet()) {
      if (returns2.containsKey(date)) {
        covariance +=
          (returns1.get(date) - mean1) * (returns2.get(date) - mean2);
        n++;
      }
    }

    return n > 1 ? covariance / (n - 1) : 0;
  }

  /**
   * Calculates the variance of a set of returns, which is a measure of dispersion
   * around the mean return.
   *
   * @param returns a map of date strings to return percentages
   * @return the variance of the given returns
   */
  public double calculateVariance(Map<String, Double> returns) {
    double mean = returns
      .values()
      .stream()
      .mapToDouble(Double::doubleValue)
      .average()
      .orElse(0);
    double variance = 0;
    int n = 0;

    for (double value : returns.values()) {
      variance += Math.pow(value - mean, 2);
      n++;
    }

    return n > 1 ? variance / (n - 1) : 0;
  }

  /**
   * Calculates the beta of a portfolio, which is a measure of the portfolio's volatility
   * in relation to the market as represented by the SPY ETF.
   *
   * @param portfolioReturns a map of date strings to the portfolio's monthly returns
   * @param spyReturns       a map of date strings to the SPY's monthly returns
   * @return the beta of the portfolio
   */
  public double calculatePortfolioBeta(
    Map<String, Double> portfolioReturns,
    Map<String, Double> spyReturns
  ) {
    double covariance = calculateCovariance(portfolioReturns, spyReturns);
    double variance = calculateVariance(spyReturns);

    return variance != 0 ? covariance / variance : 0;
  }

  /**
   * Calculates the Information Ratio, which is a measure of risk-adjusted return
   * of a portfolio in relation to a benchmark (in this case, the SPY ETF).
   *
   * @param portfolioReturns a map of date strings to the portfolio's monthly returns
   * @param spyReturns       a map of date strings to the SPY's monthly returns
   * @return the information ratio of the portfolio
   */
  public double calculateInformationRatio(
    Map<String, Double> portfolioReturns,
    Map<String, Double> spyReturns
  ) {
    List<Double> excessReturns = new ArrayList<>();
    for (String date : portfolioReturns.keySet()) {
      if (spyReturns.containsKey(date)) {
        double excessReturn = portfolioReturns.get(date) - spyReturns.get(date);
        excessReturns.add(excessReturn);
      }
    }

    double meanExcessReturn = excessReturns
      .stream()
      .mapToDouble(Double::doubleValue)
      .average()
      .orElse(0);
    double trackingError = Math.sqrt(
      excessReturns
        .stream()
        .mapToDouble(val -> Math.pow(val - meanExcessReturn, 2))
        .average()
        .orElse(0)
    );

    return trackingError != 0 ? meanExcessReturn / trackingError : 0;
  }

  /**
   * Calculates the difference in financial statistics between two portfolios.
   * 
   * This method computes the differences between the current total portfolio value,
   * portfolio beta, information ratio, quarterly returns, quarterly returns percentage,
   * and annualized returns percentage of two given FinancialStatsDTO objects. It
   * subtracts the values of portfolio2Stats from portfolio1Stats and stores the
   * results in a new FinancialStatsDTO object, which is then returned. If any of
   * the values are null or in an improper format, appropriate handling is done to
   * return a default value or indicate the absence of data.
   * 
   * Note: This method uses private helper methods to handle null values and to clean
   * percentage strings. It also includes logging statements to assist with
   * debugging and validation of the data.
   * 
   * @param portfolio1Stats the financial statistics of the first portfolio.
   * @param portfolio2Stats the financial statistics of the second portfolio.
   * @return a FinancialStatsDTO object containing the differences between the two portfolios.
   */
  public FinancialStatsDTO calculateDifference(
    FinancialStatsDTO portfolio1Stats,
    FinancialStatsDTO portfolio2Stats
  ) {
    FinancialStatsDTO differenceStats = new FinancialStatsDTO();

    // Calculate the differences and set the properties of differenceStats
    differenceStats.setCurrentTotalPortfolioValue(
      subtractDoublesHandlingNull(
        portfolio1Stats.getCurrentTotalPortfolioValue(),
        portfolio2Stats.getCurrentTotalPortfolioValue()
      )
    );
    differenceStats.setPortfolioBeta(
      subtractDoublesHandlingNull(
        portfolio1Stats.getPortfolioBeta(),
        portfolio2Stats.getPortfolioBeta()
      )
    );
    differenceStats.setInformationRatio(
      subtractDoublesHandlingNull(
        portfolio1Stats.getInformationRatio(),
        portfolio2Stats.getInformationRatio()
      )
    );

    // Calculate the differences for quarterly returns
    Map<String, String> quarterlyReturnsDifference = new HashMap<>();
    portfolio1Stats
      .getQuarterlyReturns()
      .forEach((quarter, value1) -> {
        String value2 = portfolio2Stats
          .getQuarterlyReturns()
          .getOrDefault(quarter, "N/A");
        if ("N/A".equals(value1) || "N/A".equals(value2)) {
          quarterlyReturnsDifference.put(quarter, "N/A");
        } else {
          try {
            double difference =
              Double.parseDouble(value1) - Double.parseDouble(value2);
            quarterlyReturnsDifference.put(quarter, String.valueOf(difference));
          } catch (NumberFormatException e) {
            quarterlyReturnsDifference.put(quarter, "N/A");
          }
        }
      });
    differenceStats.setQuarterlyReturns(quarterlyReturnsDifference);

    // Handle annualized returns percentage
    String annual1 = safePercentage(
      portfolio1Stats.getAnnualizedReturnsPercentage()
    );
    String annual2 = safePercentage(
      portfolio2Stats.getAnnualizedReturnsPercentage()
    );
    if ("N/A".equals(annual1) || "N/A".equals(annual2)) {
      differenceStats.setAnnualizedReturnsPercentage("N/A");
    } else {
      try {
        double annualizedReturnsDifference =
          Double.parseDouble(annual1) - Double.parseDouble(annual2);
        differenceStats.setAnnualizedReturnsPercentage(
          String.format("%.2f%%", annualizedReturnsDifference)
        );
      } catch (NumberFormatException e) {
        differenceStats.setAnnualizedReturnsPercentage("N/A");
      }
    }

    // Calculate the differences for quarterly returns percentage
    Map<String, String> quarterlyReturnsPercentageDifference = new HashMap<>();
    portfolio1Stats
      .getQuarterlyReturnsPercentage()
      .forEach((quarter, value1) -> {
        String cleanedValue1 = safePercentage(value1);
        String cleanedValue2 = safePercentage(
          portfolio2Stats
            .getQuarterlyReturnsPercentage()
            .getOrDefault(quarter, "N/A")
        );
        System.out.println(
          "Quarter: " +
          quarter +
          ", Cleaned Value1: " +
          cleanedValue1 +
          ", Cleaned Value2: " +
          cleanedValue2
        ); // Log cleaned values
        if ("N/A".equals(cleanedValue1) || "N/A".equals(cleanedValue2)) {
          quarterlyReturnsPercentageDifference.put(quarter, "N/A");
        } else {
          try {
            double difference =
              Double.parseDouble(cleanedValue1) -
              Double.parseDouble(cleanedValue2);
            quarterlyReturnsPercentageDifference.put(
              quarter,
              String.format("%.2f%%", difference)
            );
            System.out.println(
              "Calculated difference for " + quarter + ": " + difference
            ); // Log calculated
            // difference
          } catch (NumberFormatException e) {
            System.out.println(
              "Number format exception for quarter: " + quarter
            ); // Log the exception
            quarterlyReturnsPercentageDifference.put(quarter, "N/A");
          }
        }
      });
    differenceStats.setQuarterlyReturnsPercentage(
      quarterlyReturnsPercentageDifference
    );

    return differenceStats;
  }

  /**
   * Subtracts the second double value from the first, handling nulls.
   * If either value is null, the method returns 0.
   *
   * @param value1 The first double value, from which the second is to be subtracted.
   * @param value2 The second double value, which is to be subtracted from the first.
   * @return The result of the subtraction if neither value is null; otherwise, 0.
   */
  private double subtractDoublesHandlingNull(Double value1, Double value2) {
    if (value1 == null || value2 == null) {
      return 0;
    }
    return value1 - value2;
  }

  /**
   * Safely formats a percentage string by removing any '%' characters and trimming whitespace.
   * If the input is null, "N/A", or empty after trimming, it returns "N/A".
   * It also logs the original and cleaned percentage strings to the console.
   *
   * @param percentage The string representing a percentage to be cleaned.
   * @return A cleaned percentage string without the '%' character and whitespace, or "N/A" if the input is not applicable.
   */
  private String safePercentage(String percentage) {
    if (
      percentage == null ||
      "N/A".equals(percentage) ||
      percentage.trim().isEmpty()
    ) {
      return "N/A";
    } else {
      // Log the original string
      System.out.println("Original percentage string: '" + percentage + "'");
      String cleanedPercentage = percentage.replace("%", "").trim();
      // Log the cleaned string
      System.out.println(
        "Cleaned percentage string: '" + cleanedPercentage + "'"
      );
      return cleanedPercentage;
    }
  }
}
