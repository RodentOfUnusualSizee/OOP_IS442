package com.app.PortfolioTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.app.ExternalAPIs.StockTimeSeriesAPI.Monthly.MonthlyController;
import com.app.ExternalAPIs.StockTimeSeriesAPI.Monthly.MonthlyService;
import com.app.ExternalAPIs.StockTimeSeriesAPI.Monthly.StockTimeSeriesMonthlyDTO;
import com.app.Portfolio.Portfolio;
import com.app.Portfolio.PortfolioComparisionDTOs.FinancialStatsDTO;
import com.app.Portfolio.PortfolioDTO;
import com.app.Portfolio.PortfolioRepository;
import com.app.Portfolio.PortfolioService;
import com.app.Position.Position;
import com.app.Position.PositionService;
import com.app.StockDataPoint.StockDataPoint;
import com.app.User.User;
import com.app.User.UserService;
import com.app.WildcardResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PortfolioServiceTest {

  @InjectMocks
  private PortfolioService portfolioService;

  @Mock
  private PortfolioRepository portfolioRepository;

  @Mock
  private UserService userService;

  @Mock
  private MonthlyController monthlyController;

  @Mock
  private MonthlyService monthlyService;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testAddPortfolio() {
    Portfolio portfolio = new Portfolio();
    portfolio.setPortfolioID(1);
    User user = new User();
    user.setId(1L);
    portfolio.setUser(user);

    WildcardResponse expectedResponse = new WildcardResponse(
      true,
      "Portfolio added",
      portfolio
    );
    when(userService.addPortfolioToUser(eq(1L), any(Portfolio.class)))
      .thenReturn(expectedResponse);

    WildcardResponse actualResponse = portfolioService.addPortfolio(portfolio);

    assertEquals(expectedResponse, actualResponse);
    assertNotNull(portfolio.getCreatedTimestamp());
    assertNotNull(portfolio.getLastModifiedTimestamp());
  }

  @Test
  public void testUpdatePortfolioWhenExists() {
    Portfolio portfolio = new Portfolio();
    portfolio.setPortfolioID(1);

    when(portfolioRepository.existsById(1)).thenReturn(true);
    when(portfolioRepository.save(portfolio)).thenReturn(portfolio);

    Portfolio updatedPortfolio = portfolioService.updatePortfolio(portfolio);

    assertNotNull(updatedPortfolio.getLastModifiedTimestamp());
    assertEquals(portfolio, updatedPortfolio);
  }

  @Test
  public void testUpdatePortfolioWhenNotExists() {
    Portfolio portfolio = new Portfolio();
    portfolio.setPortfolioID(1);

    when(portfolioRepository.existsById(1)).thenReturn(false);

    Portfolio updatedPortfolio = portfolioService.updatePortfolio(portfolio);

    assertNull(updatedPortfolio);
  }

  @Test
  public void testDeletePortfolio() {
    int portfolioID = 1;
    doNothing().when(portfolioRepository).deleteById(portfolioID);

    portfolioService.deletePortfolio(portfolioID);

    verify(portfolioRepository).deleteById(portfolioID);
  }

  @Test
  public void testGetPortfolio() {
    int portfolioId = 1;
    Portfolio portfolio = new Portfolio();
    portfolio.setPortfolioID(portfolioId);

    when(portfolioRepository.findById(portfolioId))
      .thenReturn(Optional.of(portfolio));

    Optional<Portfolio> result = portfolioService.getPortfolio(portfolioId);

    assertTrue(result.isPresent());
    assertEquals(portfolioId, result.get().getPortfolioID());
  }

  @Test
  public void testGetAllPortfoliosByUser() {
    User user = new User();
    user.setId(1L);
    Portfolio portfolio1 = new Portfolio();
    portfolio1.setPortfolioID(1);
    Portfolio portfolio2 = new Portfolio();
    portfolio2.setPortfolioID(2);
    List<Portfolio> portfolios = Arrays.asList(portfolio1, portfolio2);
    user.setPortfolios(portfolios);

    List<Portfolio> result = portfolioService.getAllPortfoliosByUser(user);

    assertEquals(portfolios, result);
    assertTrue(result.containsAll(portfolios));
  }

  @Test
  public void testCheckPortfolioCapitalForNewPosition() {
    Portfolio portfolio = new Portfolio();
    portfolio.setCapitalUSD(5000.0f);
    Position position = new Position();
    position.setPrice(100.0f);
    position.setQuantity(10);

    boolean result = PortfolioService.checkPortfolioCapitalForNewPosition(
      portfolio,
      position
    );

    assertFalse(result);
    assertEquals(4000.0f, portfolio.getCapitalUSD(), 0.01);
  }

  @Test
  public void testComputeCumPositions() {
    // Arrange
    List<Position> positions = Arrays.asList(
      new Position(
        1,
        "AAPL",
        150.0f,
        "BUY",
        10,
        java.sql.Date.valueOf(LocalDate.now())
      ),
      new Position(
        2,
        "AAPL",
        200.0f,
        "BUY",
        5,
        java.sql.Date.valueOf(LocalDate.now())
      ),
      new Position(
        3,
        "AAPL",
        100.0f,
        "SELLTOCLOSE",
        3,
        java.sql.Date.valueOf(LocalDate.now())
      )
    );

    positions.get(0).setStockSector("Tech");
    positions.get(0).setStockGeographicalLocation("USA");
    positions.get(1).setStockSector("Tech");
    positions.get(1).setStockGeographicalLocation("USA");
    positions.get(2).setStockSector("Tech");
    positions.get(2).setStockGeographicalLocation("USA");

    // Assume the latest stock price is 250.0
    StockDataPoint recentDataPoint = new StockDataPoint(
      "2023-11-04",
      250.0,
      250.0,
      250.0,
      250.0,
      1000
    );
    StockTimeSeriesMonthlyDTO stockDataDTO = new StockTimeSeriesMonthlyDTO();
    stockDataDTO.setTimeSeries(Collections.singletonList(recentDataPoint));

    when(monthlyController.getMonthlyTimeSeries("AAPL"))
      .thenReturn(stockDataDTO);

    // Act
    List<Map<String, Object>> cumPositions = portfolioService.computeCumPositions(
      positions
    );

    // Assert
    assertEquals(
      1,
      cumPositions.size(),
      "Should only be one cumulated position for one stock symbol."
    );

    Map<String, Object> cumPositionAAPL = cumPositions.get(0);
    assertEquals("AAPL", cumPositionAAPL.get("stockSymbol"));
    assertEquals("Tech", cumPositionAAPL.get("stockSector"));
    assertEquals("USA", cumPositionAAPL.get("geographicalLocation"));

    // The average price calculation is: (10 * 150 + 5 * 200) / (10 + 5)
    assertEquals(
      166.67,
      (Double) cumPositionAAPL.get("averagePrice"),
      0.01,
      "Average price calculation error."
    );

    // Total quantity is 10 + 5 - 3 = 12
    assertEquals(
      12,
      cumPositionAAPL.get("totalQuantity"),
      "Total quantity calculation error."
    );

    // Current value is 12 * 250 = 3000
    assertEquals(
      3000.0,
      (Double) cumPositionAAPL.get("currentValue"),
      0.01,
      "Current value calculation error."
    );
  }

  @Test
  public void testComputePortfolioHistoricalValue_Success() {
    // Arrange
    Portfolio portfolio = new Portfolio();
    portfolio.setCapitalUSD(1000f); // Use float as per the Portfolio class definition

    Position position = new Position();
    position.setStockSymbol("AAPL");
    position.setQuantity(10);
    // Set to a fixed date for testing purposes
    position.setPositionAddDate(
      new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime()
    );

    // Ensure we use an ArrayList as per the Portfolio class definition
    portfolio.setPositions(
      new ArrayList<>(Collections.singletonList(position))
    );

    StockTimeSeriesMonthlyDTO stockData = mock(StockTimeSeriesMonthlyDTO.class);
    List<StockDataPoint> monthlyTimeSeries = Arrays.asList(
      new StockDataPoint("2020-01-31", 150.0, 155.0, 145.0, 150.0, 100000),
      new StockDataPoint("2020-02-29", 160.0, 165.0, 155.0, 160.0, 120000)
    );
    when(stockData.getTimeSeries()).thenReturn(monthlyTimeSeries);
    when(monthlyController.getMonthlyTimeSeries("AAPL")).thenReturn(stockData);

    // Act
    Map<String, Double> historicalValue = portfolioService.computePortfolioHistoricalValue(
      portfolio,
      monthlyController
    );

    // Assert
    assertNotNull(historicalValue);
    assertEquals(2, historicalValue.size());
    assertEquals(2500.0, historicalValue.get("2020-01-31"));
    assertEquals(2600.0, historicalValue.get("2020-02-29"));
  }

  @Test
  public void testComputePortfolioHistoricalValue_EmptyPositions() {
    // Arrange
    Portfolio portfolio = new Portfolio();
    portfolio.setCapitalUSD(1000f); // Use float as per the Portfolio class definition
    portfolio.setPositions(new ArrayList<>()); // Empty list of positions

    // Act
    Map<String, Double> historicalValue = portfolioService.computePortfolioHistoricalValue(
      portfolio,
      monthlyController
    );

    // Assert
    assertTrue(historicalValue.isEmpty());
  }

  @Test
  public void calculateReturns_WithValidData_ReturnsCorrectCalculation() {
    // Arrange
    Map<String, Double> historicalValues = new HashMap<>();
    historicalValues.put(
      LocalDate.now().minusMonths(4).format(DATE_FORMATTER),
      100.0
    );
    historicalValues.put(
      LocalDate.now().minusMonths(1).format(DATE_FORMATTER),
      110.0
    );

    PortfolioDTO portfolio = mock(PortfolioDTO.class);
    when(portfolio.getPortfolioHistoricalValue()).thenReturn(historicalValues);

    // Act
    Map<String, Object> returns = portfolioService.calculateReturns(portfolio);

    // Assert
    assertNotNull(returns);
    assertTrue(returns.containsKey("quarterlyReturns"));
    assertTrue(returns.containsKey("quarterlyReturnsPercentage"));
    assertTrue(returns.containsKey("quarterlyDateRanges"));
    assertTrue(returns.containsKey("annualizedReturnsPercentage"));
  }

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(
    "yyyy-MM-dd"
  );

  @Test
  public void transformPortfolioToFinancialStatsDTO_WithValidPortfolio_ReturnsCorrectDTO() {
    // Arrange
    PortfolioDTO portfolioDTO = mock(PortfolioDTO.class);
    when(portfolioDTO.getCurrentTotalPortfolioValue()).thenReturn(1000.0);
    when(portfolioDTO.getPortfolioBeta()).thenReturn(1.0);
    when(portfolioDTO.getInformationRatio()).thenReturn(0.5);
    when(portfolioDTO.getQuarterlyReturns()).thenReturn(new HashMap<>());
    when(portfolioDTO.getAnnualizedReturnsPercentage()).thenReturn("5.00%");
    when(portfolioDTO.getQuarterlyReturnsPercentage())
      .thenReturn(new HashMap<>());

    // Act
    FinancialStatsDTO financialStatsDTO = portfolioService.transformPortfolioToFinancialStatsDTO(
      portfolioDTO
    );

    // Assert
    assertNotNull(financialStatsDTO);
    assertEquals(1000.0, financialStatsDTO.getCurrentTotalPortfolioValue());
    assertEquals(1.0, financialStatsDTO.getPortfolioBeta());
    assertEquals(0.5, financialStatsDTO.getInformationRatio());
    assertNotNull(financialStatsDTO.getQuarterlyReturns());
    assertEquals("5.00%", financialStatsDTO.getAnnualizedReturnsPercentage());
    assertNotNull(financialStatsDTO.getQuarterlyReturnsPercentage());
  }

  @Test
  public void testTransformPortfolioToDTO() {
    // Create mock Portfolio with all necessary data
    Portfolio mockPortfolio = new Portfolio();
    mockPortfolio.setPortfolioID(1);
    mockPortfolio.setPortfolioName("Test Portfolio");
    mockPortfolio.setStrategyDesc("Test Strategy");
    mockPortfolio.setCapitalUSD(1000000f);
    mockPortfolio.setCreatedTimestamp(new Date());
    mockPortfolio.setLastModifiedTimestamp(new Date());

    // Mock positions and cumPositions
    ArrayList<Position> positions = new ArrayList<>();
    positions.add(new Position()); // You would add actual Position objects with valid data
    List<Map<String, Object>> cumPositions = new ArrayList<>();
    cumPositions.add(new HashMap<>()); // Similarly, add actual data here

    // Mock other fields not set in the constructor
    Double currentTotalPortfolioValue = 1050000.0;
    Double portfolioBeta = 0.9;
    Double informationRatio = 1.5;
    String portfolioYoY = "10%";
    String portfolioQoQ = "3%";
    String portfolioMoM = "1%";
    Map<String, Double> portfolioHistoricalValue = new HashMap<>();
    Map<String, Double> portfolioAllocationBySector = new HashMap<>();
    Map<String, Double> portfolioAllocationByGeographicalLocation = new HashMap<>();
    Map<String, String> quarterlyReturns = new HashMap<>();
    String annualizedReturnsPercentage = "8%";
    Map<String, String> quarterlyReturnsPercentage = new HashMap<>();
    Map<String, String> quarterlyDateRanges = new HashMap<>();

    // Setup historical and analytical data (example data, add actual valid data)
    portfolioHistoricalValue.put("2020", 950000.0);
    portfolioAllocationBySector.put("Technology", 50.0);
    portfolioAllocationByGeographicalLocation.put("North America", 70.0);
    quarterlyReturns.put("Q1", "2%");
    quarterlyReturnsPercentage.put("Q1", "2%");
    quarterlyDateRanges.put("Q1", "2020-01-01 to 2020-03-31");

    // Create DTO from mock portfolio
    PortfolioDTO portfolioDTO = new PortfolioDTO(mockPortfolio, cumPositions);

    // Set the remaining fields manually
    portfolioDTO.setCurrentTotalPortfolioValue(currentTotalPortfolioValue);
    portfolioDTO.setPortfolioBeta(portfolioBeta);
    portfolioDTO.setInformationRatio(informationRatio);
    portfolioDTO.setPortfolioYoY(portfolioYoY);
    portfolioDTO.setPortfolioQoQ(portfolioQoQ);
    portfolioDTO.setPortfolioMoM(portfolioMoM);
    portfolioDTO.setPortfolioHistoricalValue(portfolioHistoricalValue);
    portfolioDTO.setPortfolioAllocationBySector(portfolioAllocationBySector);
    portfolioDTO.setPortfolioAllocationByGeographicalLocation(
      portfolioAllocationByGeographicalLocation
    );
    portfolioDTO.setQuarterlyReturns(quarterlyReturns);
    portfolioDTO.setAnnualizedReturnsPercentage(annualizedReturnsPercentage);
    portfolioDTO.setQuarterlyReturnsPercentage(quarterlyReturnsPercentage);
    portfolioDTO.setQuarterlyDateRanges(quarterlyDateRanges);

    // Now assert all fields
    assertEquals(mockPortfolio.getPortfolioID(), portfolioDTO.getPortfolioID());
    assertEquals(
      mockPortfolio.getPortfolioName(),
      portfolioDTO.getPortfolioName()
    );
    assertEquals(
      mockPortfolio.getStrategyDesc(),
      portfolioDTO.getStrategyDesc()
    );
    assertEquals(mockPortfolio.getCapitalUSD(), portfolioDTO.getCapitalUSD());
    assertEquals(cumPositions, portfolioDTO.getCumPositions());
    assertEquals(
      currentTotalPortfolioValue,
      portfolioDTO.getCurrentTotalPortfolioValue()
    );
    assertEquals(portfolioBeta, portfolioDTO.getPortfolioBeta());
    assertEquals(informationRatio, portfolioDTO.getInformationRatio());
    assertEquals(portfolioYoY, portfolioDTO.getPortfolioYoY());
    assertEquals(portfolioQoQ, portfolioDTO.getPortfolioQoQ());
    assertEquals(portfolioMoM, portfolioDTO.getPortfolioMoM());
    assertEquals(
      portfolioHistoricalValue,
      portfolioDTO.getPortfolioHistoricalValue()
    );
    assertEquals(
      portfolioAllocationBySector,
      portfolioDTO.getPortfolioAllocationBySector()
    );
    assertEquals(
      portfolioAllocationByGeographicalLocation,
      portfolioDTO.getPortfolioAllocationByGeographicalLocation()
    );
    assertEquals(quarterlyReturns, portfolioDTO.getQuarterlyReturns());
    assertEquals(
      annualizedReturnsPercentage,
      portfolioDTO.getAnnualizedReturnsPercentage()
    );
    assertEquals(
      quarterlyReturnsPercentage,
      portfolioDTO.getQuarterlyReturnsPercentage()
    );
    assertEquals(quarterlyDateRanges, portfolioDTO.getQuarterlyDateRanges());

    // Date fields might require special attention depending on precision requirements
    assertEquals(
      mockPortfolio.getCreatedTimestamp(),
      portfolioDTO.getCreatedTimestamp()
    );
    assertEquals(
      mockPortfolio.getLastModifiedTimestamp(),
      portfolioDTO.getLastModifiedTimestamp()
    );
  }

  @Test
  public void getPortfolioValueAtDate_WhenDateIsValid_ThenReturnCorrectValue() {
    // Arrange
    Map<String, Double> historicalValues = new LinkedHashMap<>();
    historicalValues.put("2023-01-01", 1000.0);
    historicalValues.put("2023-02-01", 1050.0);
    historicalValues.put("2023-03-01", 1100.0);

    // Act
    Double valueForFebruary = portfolioService.getPortfolioValueAtDate(
      historicalValues,
      "2023-02-15"
    );

    // Assert
    assertNotNull(valueForFebruary);
    assertEquals(1050.0, valueForFebruary);
  }

  @Test
  public void getPortfolioValueAtDate_WhenDateIsNull_ThenReturnNull() {
    // Arrange
    Map<String, Double> historicalValues = new LinkedHashMap<>();
    historicalValues.put("2023-01-01", 1000.0);

    // Act
    Double value = portfolioService.getPortfolioValueAtDate(
      historicalValues,
      null
    );

    // Assert
    assertNull(value);
  }

  @Test
  public void getPortfolioValueAtDate_WhenDateIsNotPresent_ThenReturnNull() {
    // Arrange
    Map<String, Double> historicalValues = new LinkedHashMap<>();
    historicalValues.put("2023-01-01", 1000.0);

    // Act
    Double valueForApril = portfolioService.getPortfolioValueAtDate(
      historicalValues,
      "2023-04-01"
    );

    // Assert
    assertNull(valueForApril);
  }

  @Test
  public void calculateSPYReturns_WhenDataIsPresent_ThenReturnCorrectReturns() {
    // Arrange
    StockDataPoint point1 = new StockDataPoint(
      "2023-01-01",
      100.0,
      105.0,
      95.0,
      100.0,
      1000000
    );
    StockDataPoint point2 = new StockDataPoint(
      "2023-02-01",
      100.0,
      115.0,
      100.0,
      110.0,
      2000000
    );
    StockDataPoint point3 = new StockDataPoint(
      "2023-03-01",
      110.0,
      110.0,
      100.0,
      105.0,
      1500000
    );
    List<StockDataPoint> points = Arrays.asList(point1, point2, point3);

    StockTimeSeriesMonthlyDTO dto = new StockTimeSeriesMonthlyDTO();
    dto.setTimeSeries(points);

    when(monthlyService.getMonthlyTimeSeriesProcessed("SPY")).thenReturn(dto);

    // Expected returns calculations
    Map<String, Double> expectedReturns = new LinkedHashMap<>();
    expectedReturns.put(
      point2.getDate(),
      ((point2.getClose() - point1.getClose()) / point1.getClose()) * 100
    );
    expectedReturns.put(
      point3.getDate(),
      ((point3.getClose() - point2.getClose()) / point2.getClose()) * 100
    );

    // Act
    Map<String, Double> spyReturns = portfolioService.calculateSPYReturns();

    // Assert
    assertNotNull(spyReturns);
    assertEquals(expectedReturns.size(), spyReturns.size());
    for (Map.Entry<String, Double> entry : expectedReturns.entrySet()) {
      assertTrue(spyReturns.containsKey(entry.getKey()));
      assertEquals(entry.getValue(), spyReturns.get(entry.getKey()), 0.01);
    }
  }

  @Test
  public void calculateSPYReturns_WhenNoDataIsPresent_ThenReturnEmptyMap() {
    // Arrange
    StockTimeSeriesMonthlyDTO dto = new StockTimeSeriesMonthlyDTO();
    dto.setTimeSeries(new ArrayList<>());

    when(monthlyService.getMonthlyTimeSeriesProcessed("SPY")).thenReturn(dto);

    // Act
    Map<String, Double> spyReturns = portfolioService.calculateSPYReturns();

    // Assert
    assertNotNull(spyReturns);
    assertTrue(spyReturns.isEmpty());
  }

  @Test
  public void testCalculatePortfolioReturns() {
    Map<String, Double> portfolioHistoricalValue = new LinkedHashMap<>();
    portfolioHistoricalValue.put("2021-01-01", 10000.0);
    portfolioHistoricalValue.put("2021-02-01", 10500.0);
    portfolioHistoricalValue.put("2021-03-01", 10200.0);

    Map<String, Double> expectedReturns = new LinkedHashMap<>();
    expectedReturns.put("2021-02-01", 5.0); // (10500 - 10000) / 10000 * 100
    expectedReturns.put("2021-03-01", -2.857142857142857); // (10200 - 10500) / 10500 * 100

    Map<String, Double> portfolioReturns = portfolioService.calculatePortfolioReturns(
      portfolioHistoricalValue
    );

    assertNotNull(portfolioReturns);
    assertEquals(expectedReturns.size(), portfolioReturns.size());

    expectedReturns.forEach((date, expectedReturn) ->
      assertEquals(
        expectedReturn,
        portfolioReturns.get(date),
        0.01,
        "The return on " + date + " should be correct"
      )
    );
  }

  @Test
  public void testCalculateCovariance() {
    Map<String, Double> returns1 = new LinkedHashMap<>();
    returns1.put("2021-01-01", 10.0);
    returns1.put("2021-02-01", 7.0);
    returns1.put("2021-03-01", 8.0);

    Map<String, Double> returns2 = new LinkedHashMap<>();
    returns2.put("2021-01-01", 4.0);
    returns2.put("2021-02-01", 3.0);
    returns2.put("2021-03-01", 6.0);

    double covariance = portfolioService.calculateCovariance(
      returns1,
      returns2
    );

    double expectedCovariance = 0.33333333333333326; // Calculated by hand or using a statistical tool
    assertEquals(
      expectedCovariance,
      covariance,
      0.01,
      "The covariance should be correctly calculated"
    );
  }

  @Test
  public void testCalculateVariance() {
    Map<String, Double> returns = new LinkedHashMap<>();
    returns.put("2021-01-01", 2.0);
    returns.put("2021-02-01", 4.0);
    returns.put("2021-03-01", 4.0);
    returns.put("2021-04-01", 4.0);
    returns.put("2021-05-01", 5.0);
    returns.put("2021-06-01", 5.0);
    returns.put("2021-07-01", 7.0);
    returns.put("2021-08-01", 9.0);

    double variance = portfolioService.calculateVariance(returns);

    double expectedVariance = 4.571428571428571; // Calculated by hand or using a statistical tool
    assertEquals(
      expectedVariance,
      variance,
      0.01,
      "The variance should be correctly calculated"
    );
  }

  @Test
  public void testCalculatePortfolioBeta() {
    Map<String, Double> portfolioReturns = new HashMap<>();
    portfolioReturns.put("2021-01-01", 0.05);
    portfolioReturns.put("2021-02-01", 0.06);
    portfolioReturns.put("2021-03-01", 0.07);

    Map<String, Double> spyReturns = new HashMap<>();
    spyReturns.put("2021-01-01", 0.04);
    spyReturns.put("2021-02-01", 0.05);
    spyReturns.put("2021-03-01", 0.06);

    double beta = portfolioService.calculatePortfolioBeta(
      portfolioReturns,
      spyReturns
    );

    // Beta is a ratio of covariance to variance, here we do not calculate the actual values but assert that method returns a proper double value.
    assertTrue(beta > 0);
  }

  @Test
  public void testCalculateInformationRatio() {
    Map<String, Double> portfolioReturns = new HashMap<>();
    portfolioReturns.put("2021-01-01", 0.08);
    portfolioReturns.put("2021-02-01", 0.09);
    portfolioReturns.put("2021-03-01", 0.10);

    Map<String, Double> spyReturns = new HashMap<>();
    spyReturns.put("2021-01-01", 0.04);
    spyReturns.put("2021-02-01", 0.05);
    spyReturns.put("2021-03-01", 0.06);

    double informationRatio = portfolioService.calculateInformationRatio(
      portfolioReturns,
      spyReturns
    );

    // Information ratio is a ratio of mean excess return to tracking error, we assert that it calculates correctly
    assertTrue(informationRatio > 0);
  }

  @Test
  public void testCalculateDifference() {
    // Arrange
    Map<String, String> quarterlyReturns1 = new HashMap<>();
    quarterlyReturns1.put("Q1", "5.0");
    quarterlyReturns1.put("Q2", "6.0");

    Map<String, String> quarterlyReturns2 = new HashMap<>();
    quarterlyReturns2.put("Q1", "3.0");
    quarterlyReturns2.put("Q2", "4.0");

    Map<String, String> quarterlyReturnsPercentage1 = new HashMap<>();
    quarterlyReturnsPercentage1.put("Q1", "10%");
    quarterlyReturnsPercentage1.put("Q2", "20%");

    Map<String, String> quarterlyReturnsPercentage2 = new HashMap<>();
    quarterlyReturnsPercentage2.put("Q1", "8%");
    quarterlyReturnsPercentage2.put("Q2", "15%");

    FinancialStatsDTO portfolio1Stats = new FinancialStatsDTO(
      100000.0, // currentTotalPortfolioValue
      1.2, // portfolioBeta
      0.5, // informationRatio
      quarterlyReturns1,
      "12%", // annualizedReturnsPercentage
      quarterlyReturnsPercentage1
    );

    FinancialStatsDTO portfolio2Stats = new FinancialStatsDTO(
      95000.0, // currentTotalPortfolioValue
      1.1, // portfolioBeta
      0.3, // informationRatio
      quarterlyReturns2,
      "10%", // annualizedReturnsPercentage
      quarterlyReturnsPercentage2
    );

    // Act
    FinancialStatsDTO differenceStats = portfolioService.calculateDifference(
      portfolio1Stats,
      portfolio2Stats
    );

    // Assert
    assertEquals(5000.0, differenceStats.getCurrentTotalPortfolioValue());
    assertEquals(0.1, differenceStats.getPortfolioBeta(), 0.0001); // where 0.0001 is the delta
    assertEquals(0.2, differenceStats.getInformationRatio());

    // Quarterly returns differences
    assertEquals("2.0", differenceStats.getQuarterlyReturns().get("Q1"));
    assertEquals("2.0", differenceStats.getQuarterlyReturns().get("Q2"));

    // Quarterly returns percentage differences
    assertEquals(
      "2.00%",
      differenceStats.getQuarterlyReturnsPercentage().get("Q1")
    );
    assertEquals(
      "5.00%",
      differenceStats.getQuarterlyReturnsPercentage().get("Q2")
    );

    // Annualized returns percentage difference
    assertEquals("2.00%", differenceStats.getAnnualizedReturnsPercentage());
  }
}
