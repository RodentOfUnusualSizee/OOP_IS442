package com.app.StockTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.app.Stock.Stock;
import com.app.Stock.StockRepository;
import com.app.Stock.StockService;
import com.app.WildcardResponse;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class StockServiceTest {

  @InjectMocks
  private StockService stockService;

  @Mock
  private StockRepository stockRepository;

  private Stock sampleStock;
  private List<Stock> stockList;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // Setup a sample stock
    sampleStock = new Stock("AAPL", "Apple Inc.", "2023-04-01", "EST");
    stockList = new ArrayList<>();
    stockList.add(sampleStock);
  }

  @Test
  void testSave() {
    when(stockRepository.save(any(Stock.class))).thenReturn(sampleStock);

    WildcardResponse response = stockService.save(sampleStock);

    verify(stockRepository, times(1)).save(sampleStock);
    assertTrue(response.getSuccess());
    assertEquals("Success", response.getMessage());
    assertEquals(sampleStock, response.getData());
  }

  @Test
  void testSaveFailure() {
    when(stockRepository.save(any(Stock.class)))
      .thenThrow(new RuntimeException("Database error"));

    WildcardResponse response = stockService.save(sampleStock);

    assertFalse(response.getSuccess());
    assertEquals("Database error", response.getMessage());
    assertEquals(sampleStock, response.getData());
  }

  @Test
  void testGetStock() {
    when(stockRepository.findById("AAPL")).thenReturn(Optional.of(sampleStock));

    Stock foundStock = stockService.getStock("AAPL");

    assertNotNull(foundStock);
    assertEquals("AAPL", foundStock.getSymbol());
    assertEquals("Apple Inc.", foundStock.getInformation());
  }

  @Test
  void testGetStockNotExists() {
    when(stockRepository.findById("AAPL")).thenReturn(Optional.empty());

    Stock foundStock = stockService.getStock("AAPL");

    assertNull(foundStock);
  }

  @Test
  void testGetAllStocks() {
    when(stockRepository.findAll()).thenReturn(stockList);

    WildcardResponse response = stockService.getAllStocks();

    verify(stockRepository, times(1)).findAll();
    assertTrue(response.getSuccess());
    assertEquals("Success", response.getMessage());
    assertNotNull(response.getData());
    List<Stock> stocks = (List<Stock>) response.getData();
    assertFalse(stocks.isEmpty());
    assertEquals(1, stocks.size());
    assertEquals(sampleStock, stocks.get(0));
  }

  @Test
  void testGetAllStocksFailure() {
    when(stockRepository.findAll())
      .thenThrow(new RuntimeException("Database error"));

    WildcardResponse response = stockService.getAllStocks();

    assertFalse(response.getSuccess());
    assertEquals("Database error", response.getMessage());
    assertNull(response.getData());
  }

  @Test
  void testExistsBySymbol() {
    when(stockRepository.existsById("AAPL")).thenReturn(true);

    boolean exists = stockService.existsBySymbol("AAPL");

    assertTrue(exists);
  }

  @Test
  void testNotExistsBySymbol() {
    when(stockRepository.existsById("AAPL")).thenReturn(false);

    boolean exists = stockService.existsBySymbol("AAPL");

    assertFalse(exists);
  }

  @Test
  void testGetStockBySymbol() {
    when(stockRepository.findById("AAPL")).thenReturn(Optional.of(sampleStock));

    Optional<Stock> foundStock = stockService.getStockBySymbol("AAPL");

    assertTrue(foundStock.isPresent());
    assertEquals(sampleStock, foundStock.get());
  }

  @Test
  void testSaveStock() {
    when(stockRepository.save(any(Stock.class))).thenReturn(sampleStock);

    Stock savedStock = stockService.saveStock(sampleStock);

    verify(stockRepository, times(1)).save(sampleStock);
    assertNotNull(savedStock);
    assertEquals("AAPL", savedStock.getSymbol());
  }

  @Test
  void testDeleteStockBySymbol() {
    doNothing().when(stockRepository).deleteById("AAPL");

    stockService.deleteStockBySymbol("AAPL");

    verify(stockRepository, times(1)).deleteById("AAPL");
  }
}
