package com.app.ExternalAPIs.CompanyOverviewAPI;

import java.time.LocalDate;

/**
 * The {@code CompanyOverviewDTO} class is a data transfer object that encapsulates 
 * various pieces of information about a company. It includes financial metrics, 
 * descriptive information, and statistics commonly used in the finance and investment 
 * industries.
 * 
 * <p>This class uses the builder pattern for constructing an instance and is meant 
 * to be used in conjunction with financial data APIs to represent company overview data.</p>
 */
public class CompanyOverviewDTO {

    /** The ticker symbol for the company. */
    private String symbol;

    /** The type of asset (e.g., "Stock", "ETF"). */
    private String assetType;

    /** The company's official name. */
    private String name;

     /** A brief description of the company. */
    private String description;

    /** The Central Index Key (CIK) used to identify the company on SEC filings. */
    private String cik;

    /** The stock exchange on which the company's stock is traded. */
    private String exchange;

    /** The currency in which the company's financials are reported. */
    private String currency;

    /** The country where the company is headquartered. */
    private String country;

    /** The sector in which the company operates. */
    private String sector;

    /** The industry category to which the company belongs. */
    private String industry;

    /** The physical address of the company's headquarters. */
    private String address;

    /** The end of the fiscal year for the company. */
    private String fiscalYearEnd;

    /** The date of the latest quarter's financial reporting. */
    private LocalDate latestQuarter;

    /** The market capitalization of the company. */
    private long marketCapitalization;

    /** Earnings before interest, taxes, depreciation, and amortization. */
    private long ebitda;

    /** The price-to-earnings ratio. */
    private double peRatio;

    /** The price/earnings-to-growth ratio. */
    private double pegRatio;

    /** The book value per share. */
    private double bookValue;

    /** The dividend paid per share. */
    private double dividendPerShare;

    /** The dividend yield as a percentage. */
    private double dividendYield;

    /** Earnings per share. */
    private double eps;

     /** Revenue per share for the trailing twelve months (TTM). */
    private double revenuePerShareTTM;

    /** The profit margin as a percentage. */
    private double profitMargin;

    /** Operating margin for the trailing twelve months (TTM). */
    private double operatingMarginTTM;

    /** Return on assets for the trailing twelve months (TTM). */
    private double returnOnAssetsTTM;

    /** Return on equity for the trailing twelve months (TTM). */
    private double returnOnEquityTTM;

    /** Revenue for the trailing twelve months (TTM). */
    private long revenueTTM;

    /** Gross profit for the trailing twelve months (TTM). */
    private long grossProfitTTM;

    /** Diluted earnings per share for the trailing twelve months (TTM). */
    private double dilutedEPSTTM;

    /** Quarterly earnings growth year over year as a percentage. */
    private double quarterlyEarningsGrowthYOY;

    /** Quarterly revenue growth year over year as a percentage. */
    private double quarterlyRevenueGrowthYOY;

    /** The target price of the stock as forecasted by analysts. */
    private double analystTargetPrice;

    /** The trailing price-to-earnings ratio. */
    private double trailingPE;

    /** The forward price-to-earnings ratio. */
    private double forwardPE;

    /** The price-to-sales ratio for the trailing twelve months (TTM). */
    private double priceToSalesRatioTTM;

    /** The price-to-book ratio. */
    private double priceToBookRatio;

     /** Enterprise value to revenue ratio. */
    private double evToRevenue;

    /** Enterprise value to EBITDA ratio. */
    private double evToEBITDA;

    /** The beta value, representing the volatility of the stock relative to the market. */
    private double beta;

    /** The highest stock price in the last 52 weeks. */
    private double _52WeekHigh;

    /** The lowest stock price in the last 52 weeks. */
    private double _52WeekLow;

    /** The average stock price over the last 50 days. */
    private double _50DayMovingAverage;

     /** The average stock price over the last 200 days. */
    private double _200DayMovingAverage;

    /** The number of shares currently outstanding. */
    private long sharesOutstanding;

    /** The date when the last dividend was paid. */
    private LocalDate dividendDate;

    /** The date before which a shareholder must own shares to be eligible for the next dividend. */
    private LocalDate exDividendDate;


    /**
     * Default constructor for creating an instance of CompanyOverviewDTO.
     */
    public CompanyOverviewDTO() {
    }

    // Parameterized constructor
    public CompanyOverviewDTO(String symbol, String assetType, String name, String description, String cik, String exchange, String currency, String country, String sector, String industry, String address, String fiscalYearEnd, LocalDate latestQuarter, long marketCapitalization, long ebitda, double peRatio, double pegRatio, double bookValue, double dividendPerShare, double dividendYield, double eps, double revenuePerShareTTM, double profitMargin, double operatingMarginTTM, double returnOnAssetsTTM, double returnOnEquityTTM, long revenueTTM, long grossProfitTTM, double dilutedEPSTTM, double quarterlyEarningsGrowthYOY, double quarterlyRevenueGrowthYOY, double analystTargetPrice, double trailingPE, double forwardPE, double priceToSalesRatioTTM, double priceToBookRatio, double evToRevenue, double evToEBITDA, double beta, double _52WeekHigh, double _52WeekLow, double _50DayMovingAverage, double _200DayMovingAverage, long sharesOutstanding, LocalDate dividendDate, LocalDate exDividendDate) {
        this.symbol = symbol;
        this.assetType = assetType;
        this.name = name;
        this.description = description;
        this.cik = cik;
        this.exchange = exchange;
        this.currency = currency;
        this.country = country;
        this.sector = sector;
        this.industry = industry;
        this.address = address;
        this.fiscalYearEnd = fiscalYearEnd;
        this.latestQuarter = latestQuarter;
        this.marketCapitalization = marketCapitalization;
        this.ebitda = ebitda;
        this.peRatio = peRatio;
        this.pegRatio = pegRatio;
        this.bookValue = bookValue;
        this.dividendPerShare = dividendPerShare;
        this.dividendYield = dividendYield;
        this.eps = eps;
        this.revenuePerShareTTM = revenuePerShareTTM;
        this.profitMargin = profitMargin;
        this.operatingMarginTTM = operatingMarginTTM;
        this.returnOnAssetsTTM = returnOnAssetsTTM;
        this.returnOnEquityTTM = returnOnEquityTTM;
        this.revenueTTM = revenueTTM;
        this.grossProfitTTM = grossProfitTTM;
        this.dilutedEPSTTM = dilutedEPSTTM;
        this.quarterlyEarningsGrowthYOY = quarterlyEarningsGrowthYOY;
        this.quarterlyRevenueGrowthYOY = quarterlyRevenueGrowthYOY;
        this.analystTargetPrice = analystTargetPrice;
        this.trailingPE = trailingPE;
        this.forwardPE = forwardPE;
        this.priceToSalesRatioTTM = priceToSalesRatioTTM;
        this.priceToBookRatio = priceToBookRatio;
        this.evToRevenue = evToRevenue;
        this.evToEBITDA = evToEBITDA;
        this.beta = beta;
        this._52WeekHigh = _52WeekHigh;
        this._52WeekLow = _52WeekLow;
        this._50DayMovingAverage = _50DayMovingAverage;
        this._200DayMovingAverage = _200DayMovingAverage;
        this.sharesOutstanding = sharesOutstanding;
        this.dividendDate = dividendDate;
        this.exDividendDate = exDividendDate;
    }
    // Getters

    /**
     * Retrieves the ticker symbol for the company.
     * 
     * @return the ticker symbol
     */
    public String getSymbol() {
        return symbol;
    }

    public String getAssetType() {
        return assetType;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCik() {
        return cik;
    }

    public String getExchange() {
        return exchange;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCountry() {
        return country;
    }

    public String getSector() {
        return sector;
    }

    public String getIndustry() {
        return industry;
    }

    public String getAddress() {
        return address;
    }

    public String getFiscalYearEnd() {
        return fiscalYearEnd;
    }

    public LocalDate getLatestQuarter() {
        return latestQuarter;
    }

    public long getMarketCapitalization() {
        return marketCapitalization;
    }

    public long getEbitda() {
        return ebitda;
    }

    public double getPeRatio() {
        return peRatio;
    }

    public double getPegRatio() {
        return pegRatio;
    }

    public double getBookValue() {
        return bookValue;
    }

    public double getDividendPerShare() {
        return dividendPerShare;
    }

    public double getDividendYield() {
        return dividendYield;
    }

    public double getEps() {
        return eps;
    }

    public double getRevenuePerShareTTM() {
        return revenuePerShareTTM;
    }

    public double getProfitMargin() {
        return profitMargin;
    }

    public double getOperatingMarginTTM() {
        return operatingMarginTTM;
    }

    public double getReturnOnAssetsTTM() {
        return returnOnAssetsTTM;
    }

    public double getReturnOnEquityTTM() {
        return returnOnEquityTTM;
    }

    public long getRevenueTTM() {
        return revenueTTM;
    }

    public long getGrossProfitTTM() {
        return grossProfitTTM;
    }

    public double getDilutedEPSTTM() {
        return dilutedEPSTTM;
    }

    public double getQuarterlyEarningsGrowthYOY() {
        return quarterlyEarningsGrowthYOY;
    }

    public double getQuarterlyRevenueGrowthYOY() {
        return quarterlyRevenueGrowthYOY;
    }

    public double getAnalystTargetPrice() {
        return analystTargetPrice;
    }

    public double getTrailingPE() {
        return trailingPE;
    }

    public double getForwardPE() {
        return forwardPE;
    }

    public double getPriceToSalesRatioTTM() {
        return priceToSalesRatioTTM;
    }

    public double getPriceToBookRatio() {
        return priceToBookRatio;
    }

    public double getEvToRevenue() {
        return evToRevenue;
    }

    public double getEvToEBITDA() {
        return evToEBITDA;
    }

    public double getBeta() {
        return beta;
    }

    public double get_52WeekHigh() {
        return _52WeekHigh;
    }

    public double get_52WeekLow() {
        return _52WeekLow;
    }

    public double get_50DayMovingAverage() {
        return _50DayMovingAverage;
    }

    public double get_200DayMovingAverage() {
        return _200DayMovingAverage;
    }

    public long getSharesOutstanding() {
        return sharesOutstanding;
    }

    public LocalDate getDividendDate() {
        return dividendDate;
    }

    public LocalDate getExDividendDate() {
        return exDividendDate;
    }

    // Setters

    /**
     * Sets the ticker symbol for the company.
     *
     * @param symbol the ticker symbol to set
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCik(String cik) {
        this.cik = cik;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setFiscalYearEnd(String fiscalYearEnd) {
        this.fiscalYearEnd = fiscalYearEnd;
    }

    public void setLatestQuarter(LocalDate latestQuarter) {
        this.latestQuarter = latestQuarter;
    }

    public void setMarketCapitalization(long marketCapitalization) {
        this.marketCapitalization = marketCapitalization;
    }

    public void setEbitda(long ebitda) {
        this.ebitda = ebitda;
    }

    public void setPeRatio(double peRatio) {
        this.peRatio = peRatio;
    }

    public void setPegRatio(double pegRatio) {
        this.pegRatio = pegRatio;
    }

    public void setBookValue(double bookValue) {
        this.bookValue = bookValue;
    }

    public void setDividendPerShare(double dividendPerShare) {
        this.dividendPerShare = dividendPerShare;
    }

    public void setDividendYield(double dividendYield) {
        this.dividendYield = dividendYield;
    }

    public void setEps(double eps) {
        this.eps = eps;
    }

    public void setRevenuePerShareTTM(double revenuePerShareTTM) {
        this.revenuePerShareTTM = revenuePerShareTTM;
    }

    public void setProfitMargin(double profitMargin) {
        this.profitMargin = profitMargin;
    }

    public void setOperatingMarginTTM(double operatingMarginTTM) {
        this.operatingMarginTTM = operatingMarginTTM;
    }

    public void setReturnOnAssetsTTM(double returnOnAssetsTTM) {
        this.returnOnAssetsTTM = returnOnAssetsTTM;
    }

    public void setReturnOnEquityTTM(double returnOnEquityTTM) {
        this.returnOnEquityTTM = returnOnEquityTTM;
    }

    public void setRevenueTTM(long revenueTTM) {
        this.revenueTTM = revenueTTM;
    }

    public void setGrossProfitTTM(long grossProfitTTM) {
        this.grossProfitTTM = grossProfitTTM;
    }

    public void setDilutedEPSTTM(double dilutedEPSTTM) {
        this.dilutedEPSTTM = dilutedEPSTTM;
    }

    public void setQuarterlyEarningsGrowthYOY(double quarterlyEarningsGrowthYOY) {
        this.quarterlyEarningsGrowthYOY = quarterlyEarningsGrowthYOY;
    }

    public void setQuarterlyRevenueGrowthYOY(double quarterlyRevenueGrowthYOY) {
        this.quarterlyRevenueGrowthYOY = quarterlyRevenueGrowthYOY;
    }

    public void setAnalystTargetPrice(double analystTargetPrice) {
        this.analystTargetPrice = analystTargetPrice;
    }

    public void setTrailingPE(double trailingPE) {
        this.trailingPE = trailingPE;
    }

    public void setForwardPE(double forwardPE) {
        this.forwardPE = forwardPE;
    }

    public void setPriceToSalesRatioTTM(double priceToSalesRatioTTM) {
        this.priceToSalesRatioTTM = priceToSalesRatioTTM;
    }

    public void setPriceToBookRatio(double priceToBookRatio) {
        this.priceToBookRatio = priceToBookRatio;
    }

    public void setEvToRevenue(double evToRevenue) {
        this.evToRevenue = evToRevenue;
    }

    public void setEvToEBITDA(double evToEBITDA) {
        this.evToEBITDA = evToEBITDA;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public void set_52WeekHigh(double _52WeekHigh) {
        this._52WeekHigh = _52WeekHigh;
    }

    public void set_52WeekLow(double _52WeekLow) {
        this._52WeekLow = _52WeekLow;
    }

    public void set_50DayMovingAverage(double _50DayMovingAverage) {
        this._50DayMovingAverage = _50DayMovingAverage;
    }

    public void set_200DayMovingAverage(double _200DayMovingAverage) {
        this._200DayMovingAverage = _200DayMovingAverage;
    }

    public void setSharesOutstanding(long sharesOutstanding) {
        this.sharesOutstanding = sharesOutstanding;
    }

    public void setDividendDate(LocalDate dividendDate) {
        this.dividendDate = dividendDate;
    }

    public void setExDividendDate(LocalDate exDividendDate) {
        this.exDividendDate = exDividendDate;
    }
}