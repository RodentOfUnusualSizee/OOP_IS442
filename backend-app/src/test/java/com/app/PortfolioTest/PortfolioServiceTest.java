// package com.app.PortfolioTest;

// import com.app.User.User;
// import com.app.Position.Position;
// import com.app.WildcardResponse;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import org.springframework.boot.test.context.SpringBootTest;

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyInt;
// import static org.mockito.ArgumentMatchers.anyLong;
// import static org.mockito.Mockito.*;
// import static org.junit.jupiter.api.Assertions.*;

// import com.app.Portfolio.PortfolioService;
// import com.app.Portfolio.PortfolioComparisionDTOs.FinancialStatsDTO;
// import com.app.Portfolio.PortfolioRepository;
// import com.app.User.UserService;
// import com.app.ExternalAPIs.StockTimeSeriesAPI.Monthly.*;
// import com.app.ExternalAPIs.StockTimeSeriesAPI.Monthly.StockTimeSeriesMonthlyDTO.MonthlyStockData;
// import com.app.Position.PositionService;
// import com.app.Portfolio.Portfolio;
// import com.app.Portfolio.PortfolioDTO;
// import java.util.*;

// @SpringBootTest
// public class PortfolioServiceTest {

//     @InjectMocks
//     private PortfolioService portfolioService;

//     @Mock
//     private PortfolioRepository portfolioRepository;

//     @Mock
//     private UserService userService;

//     @Mock
//     private MonthlyController monthlyController;

//     @Mock
//     private PositionService positionService;

//     @Mock
//     private MonthlyService monthlyService;

//     @BeforeEach
//     public void setup() {
//         MockitoAnnotations.openMocks(this);
//     }

//     @Test
//     public void testAddPortfolio() {
//         Portfolio portfolio = new Portfolio();
//         User user = new User();
//         user.setId(1);
//         portfolio.setUser(user);
//         WildcardResponse response = new WildcardResponse();

//         when(userService.addPortfolioToUser(anyLong(), any())).thenReturn(response);

//         WildcardResponse result = portfolioService.addPortfolio(portfolio);
//         assertEquals(response, result);
//     }

//     @Test
//     public void testUpdatePortfolio() {
//         Portfolio portfolio = new Portfolio();
//         portfolio.setPortfolioID(1);

//         when(portfolioRepository.existsById(1)).thenReturn(true);
//         when(portfolioRepository.save(portfolio)).thenReturn(portfolio);

//         Portfolio result = portfolioService.updatePortfolio(portfolio);
//         assertEquals(portfolio, result);
//     }

//     @Test
//     public void testDeletePortfolio() {
//         doNothing().when(portfolioRepository).deleteById(1);
//         portfolioService.deletePortfolio(1);
//         verify(portfolioRepository, times(1)).deleteById(1);
//     }

//     @Test
//     public void testGetPortfolio() {
//         Portfolio portfolio = new Portfolio();
//         portfolio.setPortfolioID(1);

//         when(portfolioRepository.findById(1)).thenReturn(Optional.of(portfolio));

//         Optional<Portfolio> result = portfolioService.getPortfolio(1);
//         assertTrue(result.isPresent());
//         assertEquals(portfolio, result.get());
//     }

//     @Test
//     public void testGetAllPortfoliosByUser() {
//         User user = new User();
//         List<Portfolio> portfolios = Arrays.asList(new Portfolio(), new Portfolio());
//         user.setPortfolios(portfolios);

//         List<Portfolio> result = portfolioService.getAllPortfoliosByUser(user);
//         assertEquals(portfolios, result);
//     }

//     @Test
//     public void testCheckPortfolioCapitalForNewPositionWithEnoughCapital() {
//         Portfolio portfolio = new Portfolio();
//         portfolio.setCapitalUSD(500);
//         Position position = new Position();
//         position.setPrice(50);
//         position.setQuantity(5);

//         assertFalse(portfolioService.checkPortfolioCapitalForNewPosition(portfolio, position));
//     }

//     @Test
//     public void testCheckPortfolioCapitalForNewPositionWithoutEnoughCapital() {
//         Portfolio portfolio = new Portfolio();
//         portfolio.setCapitalUSD(200);
//         Position position = new Position();
//         position.setPrice(50);
//         position.setQuantity(5);

//         assertTrue(portfolioService.checkPortfolioCapitalForNewPosition(portfolio, position));
//     }

//     @Test
//     public void testComputeCumPositions() {
//         Position position1 = new Position();
//         position1.setStockSymbol("AAPL");
//         position1.setPrice(100);
//         position1.setQuantity(5);
//         position1.setPosition("BUY");

//         Position position2 = new Position();
//         position2.setStockSymbol("AAPL");
//         position2.setPrice(150);
//         position2.setQuantity(5);
//         position2.setPosition("BUY");

//         List<Position> positions = Arrays.asList(position1, position2);

//         // Mocking data for MonthlyController
//         StockTimeSeriesMonthlyDTO stockData = new StockTimeSeriesMonthlyDTO();
//         MonthlyStockData monthlyData = new MonthlyStockData();
//         monthlyData.setClose(200.0);
//         stockData.setTimeSeries(Collections.singletonList(monthlyData));

//         when(monthlyController.getMonthlyTimeSeries("AAPL")).thenReturn(stockData);

//         List<Map<String, Object>> result = portfolioService.computeCumPositions(positions);
//         assertEquals(1, result.size());
//         assertEquals(125.0, result.get(0).get("averagePrice"));
//         assertEquals(10, result.get(0).get("totalQuantity"));
//         assertEquals(2000.0, result.get(0).get("currentValue"));
//     }

//     // @Test
//     // public void testTransformPortfolioToDTO() {
//     //     Portfolio portfolio = new Portfolio();
//     //     portfolio.setCapitalUSD(500);
//     //     portfolio.setPortfolioID(1);

//     //     Position position = new Position();
//     //     position.setStockSymbol("AAPL");
//     //     position.setPrice(100);
//     //     position.setQuantity(5);
//     //     position.setPositionAddDate(new Date());

//     //     ArrayList<Position> inputPositionList = new ArrayList<Position>();
//     //     inputPositionList.add(position);

//     //     portfolio.setPositions(inputPositionList);

//     //     // Mocking the computeCumPositions method
//     //     List<Map<String, Object>> mockedCumPositions = new ArrayList<>();
//     //     Map<String, Object> positionData = new HashMap<>();
//     //     positionData.put("stockSymbol", "AAPL");
//     //     positionData.put("averagePrice", 100.0);
//     //     positionData.put("totalQuantity", 5);
//     //     positionData.put("currentValue", 500.0);
//     //     mockedCumPositions.add(positionData);
//     //     when(portfolioService.computeCumPositions(portfolio.getPositions())).thenReturn(mockedCumPositions);

//     //     // Mocking the computePortfolioHistoricalValue method
//     //     Map<String, Double> mockedHistoricalValue = new HashMap<>();
//     //     mockedHistoricalValue.put("2022-01-01", 1000.0);
//     //     when(portfolioService.computePortfolioHistoricalValue(portfolio, monthlyController))
//     //             .thenReturn(mockedHistoricalValue);

//     //     // Mocking the calculateReturns method
//     //     Map<String, Object> mockedReturns = new HashMap<>();
//     //     mockedReturns.put("quarterlyReturns", Collections.singletonMap("Q1", "5%"));
//     //     mockedReturns.put("annualizedReturnsPercentage", "10%");
//     //     when(portfolioService.calculateReturns(any())).thenReturn(mockedReturns);

//     //     PortfolioDTO result = portfolioService.transformPortfolioToDTO(portfolio);

//     //     // Assert various fields of the result based on the expected transformations
//     //     assertEquals(1, result.getPortfolioID());
//     //     assertEquals(500.0, result.getCurrentTotalPortfolioValue(), 0.01);
//     //     assertEquals("5%", result.getQuarterlyReturns().get("Q1"));
//     //     assertEquals("10%", result.getAnnualizedReturnsPercentage());

//     //     // ... further assertions based on other transformations ...
//     // }

//     // @Test
//     // public void testComputePortfolioHistoricalValue() {
//     //     Portfolio portfolio = new Portfolio();
//     //     portfolio.setCapitalUSD(500);

//     //     Position position = new Position();
//     //     position.setStockSymbol("AAPL");
//     //     position.setPrice(100);
//     //     position.setQuantity(5);
//     //     position.setPositionAddDate(new Date());

//     //     ArrayList<Position> inputPositionList = new ArrayList<Position>();
//     //     inputPositionList.add(position);

//     //     portfolio.setPositions(inputPositionList);

//     //     // Mocking data for MonthlyController
//     //     StockTimeSeriesMonthlyDTO stockData = new StockTimeSeriesMonthlyDTO();
//     //     MonthlyStockData monthlyData = new MonthlyStockData();
//     //     monthlyData.setClose(200.0);
//     //     monthlyData.setDate("2022-10-25");
//     //     stockData.setTimeSeries(Collections.singletonList(monthlyData));

//     //     when(monthlyController.getMonthlyTimeSeries("AAPL")).thenReturn(stockData);

//     //     Map<String, Double> result = portfolioService.computePortfolioHistoricalValue(portfolio, monthlyController);
//     //     assertTrue(result.containsKey("2022-10-25"));
//     //     assertEquals(1500.0, result.get("2022-10-25"), 0.01); // 5 * 200 + 500
//     // }

//     @Test
//     public void testCalculateReturns() {
//         PortfolioDTO portfolioDTO = new PortfolioDTO();
//         Map<String, Double> historicalValues = new HashMap<>();
//         historicalValues.put("2022-01-01", 1000.0);
//         historicalValues.put("2022-04-01", 1100.0);
//         portfolioDTO.setPortfolioHistoricalValue(historicalValues);

//         Map<String, Object> result = portfolioService.calculateReturns(portfolioDTO);
//         assertNotNull(result);
//         // Further assertions based on the expected output
//     }

//     @Test
//     public void testTransformPortfolioToFinancialStatsDTO() {
//         PortfolioDTO portfolioDTO = new PortfolioDTO();
//         portfolioDTO.setCurrentTotalPortfolioValue(1500.0);
//         portfolioDTO.setPortfolioBeta(1.5);
//         portfolioDTO.setInformationRatio(1.2);
//         Map<String, String> quarterlyReturns = new HashMap<>();
//         quarterlyReturns.put("Q1", "5%");
//         portfolioDTO.setQuarterlyReturns(quarterlyReturns);
//         portfolioDTO.setAnnualizedReturnsPercentage("10%");

//         FinancialStatsDTO result = portfolioService.transformPortfolioToFinancialStatsDTO(portfolioDTO);
//         assertEquals(1500.0, result.getCurrentTotalPortfolioValue(), 0.01);
//         assertEquals(1.5, result.getPortfolioBeta(), 0.01);
//         assertEquals(1.2, result.getInformationRatio(), 0.01);
//         assertEquals("5%", result.getQuarterlyReturns().get("Q1"));
//         assertEquals("10%", result.getAnnualizedReturnsPercentage());
//     }

//     // @Test
//     // public void testCalculateDifference() {
//     //     FinancialStatsDTO portfolio1Stats = new FinancialStatsDTO();
//     //     portfolio1Stats.setCurrentTotalPortfolioValue(2000.0);
//     //     portfolio1Stats.setPortfolioBeta(1.5);
//     //     portfolio1Stats.setInformationRatio(1.2);

//     //     FinancialStatsDTO portfolio2Stats = new FinancialStatsDTO();
//     //     portfolio2Stats.setCurrentTotalPortfolioValue(1500.0);
//     //     portfolio2Stats.setPortfolioBeta(1.2);
//     //     portfolio2Stats.setInformationRatio(1.0);

//     //     FinancialStatsDTO result = portfolioService.calculateDifference(portfolio1Stats, portfolio2Stats);
//     //     assertEquals(500.0, result.getCurrentTotalPortfolioValue(), 0.01);
//     //     assertEquals(0.3, result.getPortfolioBeta(), 0.01);
//     //     assertEquals(0.2, result.getInformationRatio(), 0.01);
//     // }

//     // ... further tests for remaining methods ...

// }
