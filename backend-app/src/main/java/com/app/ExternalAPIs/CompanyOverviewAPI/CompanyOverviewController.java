package com.app.ExternalAPIs.CompanyOverviewAPI;

import com.app.ExternalAPIs.CompanyOverviewAPI.CompanyOverviewDTO;
import com.app.ExternalAPIs.StockTimeSeriesAPI.Daily.StockTimeSeriesDailyDTO;

import io.github.cdimascio.dotenv.Dotenv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

// Postman: http://localhost:8080/api/stock/companyOverview/{TickerSymbol}
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/stock/companyOverview")
public class CompanyOverviewController {
    @Autowired
    private RestTemplate restTemplate;

    private String apiKey;

    private Map<String, CompanyOverviewDTO> dataCache = new HashMap<>();

    @PostMapping("/{symbol}")
    public CompanyOverviewDTO getCompanyOverview(@PathVariable String symbol) {
        if (dataCache.containsKey(symbol)) {
            return dataCache.get(symbol);
        }
        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("ALPHAVANTAGE_APIKEY");

        String apiUrl = "https://www.alphavantage.co/query?function=OVERVIEW&symbol=" + symbol + "&apikey=" + apiKey;

        ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);

        Map<String, Object> responseData = response.getBody();

        // Create the CompanyOverviewDTO from the response data
        CompanyOverviewDTO companyOverviewDTO = new CompanyOverviewDTO(
                (String) responseData.get("Symbol"),
                (String) responseData.get("AssetType"),
                (String) responseData.get("Name"),
                (String) responseData.get("Description"),
                (String) responseData.get("CIK"),
                (String) responseData.get("Exchange"),
                (String) responseData.get("Currency"),
                (String) responseData.get("Country"),
                (String) responseData.get("Sector"),
                (String) responseData.get("Industry"),
                (String) responseData.get("Address"),
                (String) responseData.get("FiscalYearEnd"),
                safelyParseDate((String) responseData.get("LatestQuarter")),
                Long.parseLong((String) responseData.get("MarketCapitalization")),
                Long.parseLong((String) responseData.get("EBITDA")),
                Double.parseDouble((String) responseData.get("PERatio")),
                Double.parseDouble((String) responseData.get("PEGRatio")),
                Double.parseDouble((String) responseData.get("BookValue")),
                Double.parseDouble((String) responseData.get("DividendPerShare")),
                Double.parseDouble((String) responseData.get("DividendYield")),
                Double.parseDouble((String) responseData.get("EPS")),
                Double.parseDouble((String) responseData.get("RevenuePerShareTTM")),
                Double.parseDouble((String) responseData.get("ProfitMargin")),
                Double.parseDouble((String) responseData.get("OperatingMarginTTM")),
                Double.parseDouble((String) responseData.get("ReturnOnAssetsTTM")),
                Double.parseDouble((String) responseData.get("ReturnOnEquityTTM")),
                Long.parseLong((String) responseData.get("RevenueTTM")),
                Long.parseLong((String) responseData.get("GrossProfitTTM")),
                Double.parseDouble((String) responseData.get("DilutedEPSTTM")),
                Double.parseDouble((String) responseData.get("QuarterlyEarningsGrowthYOY")),
                Double.parseDouble((String) responseData.get("QuarterlyRevenueGrowthYOY")),
                Double.parseDouble((String) responseData.get("AnalystTargetPrice")),
                Double.parseDouble((String) responseData.get("TrailingPE")),
                Double.parseDouble((String) responseData.get("ForwardPE")),
                Double.parseDouble((String) responseData.get("PriceToSalesRatioTTM")),
                Double.parseDouble((String) responseData.get("PriceToBookRatio")),
                Double.parseDouble((String) responseData.get("EVToRevenue")),
                Double.parseDouble((String) responseData.get("EVToEBITDA")),
                Double.parseDouble((String) responseData.get("Beta")),
                Double.parseDouble((String) responseData.get("52WeekHigh")),
                Double.parseDouble((String) responseData.get("52WeekLow")),
                Double.parseDouble((String) responseData.get("50DayMovingAverage")),
                Double.parseDouble((String) responseData.get("200DayMovingAverage")),
                Long.parseLong((String) responseData.get("SharesOutstanding")),
                safelyParseDate((String) responseData.get("DividendDate")),
                safelyParseDate((String) responseData.get("ExDividendDate")));

        dataCache.put(symbol, companyOverviewDTO);
        return companyOverviewDTO;
    }

    private LocalDate safelyParseDate(String dateStr) {
        if (dateStr != null && !dateStr.equals("None")) {
            try {
                return LocalDate.parse(dateStr);
            } catch (DateTimeParseException e) {
            }
        }
        return null; 
    }

}
