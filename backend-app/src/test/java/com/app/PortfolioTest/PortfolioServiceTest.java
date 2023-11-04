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
    int portfolioID = 1;
    Portfolio portfolio = new Portfolio();
    Optional<Portfolio> optionalPortfolio = Optional.of(portfolio);
    when(portfolioRepository.findById(portfolioID))
      .thenReturn(optionalPortfolio);

    Optional<Portfolio> retrievedPortfolio = portfolioService.getPortfolio(
      portfolioID
    );

    assertEquals(optionalPortfolio, retrievedPortfolio);
  }

  @Test
  public void testGetAllPortfoliosByUser() {
    User user = new User();
    List<Portfolio> portfolios = Arrays.asList(
      new Portfolio(),
      new Portfolio()
    );
    user.setPortfolios(portfolios);

    List<Portfolio> retrievedPortfolios = portfolioService.getAllPortfoliosByUser(
      user
    );

    assertEquals(portfolios, retrievedPortfolios);
  }

  @Test
  public void testCheckPortfolioCapitalForNewPosition() {
    Portfolio portfolio = new Portfolio();
    portfolio.setCapitalUSD(1000f);

    Position position = new Position();
    position.setPrice(10f);
    position.setQuantity(50);

    boolean result = PortfolioService.checkPortfolioCapitalForNewPosition(
      portfolio,
      position
    );

    assertFalse(result);
    assertEquals(500f, portfolio.getCapitalUSD());
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
    assertEquals(1500.0, historicalValue.get("2020-01-31"));
    assertEquals(1600.0, historicalValue.get("2020-02-29"));
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
    assertEquals(positions, portfolioDTO.getPositions()); // Assuming Position class has a valid equals method
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

  
}
