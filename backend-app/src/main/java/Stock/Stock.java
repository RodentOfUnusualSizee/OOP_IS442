package Stock;

import org.springframework.stereotype.Service;

@Service //@Controller @Respository
public class Stock {
    private String symbol;
    private String companyName;
    private String exchangeTraded;
    private String currency;
    private String companySector;

    // ------------------ Getters and Setters (Start) ------------------

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getExchangeTraded() {
        return exchangeTraded;
    }

    public void setExchangeTraded(String exchangeTraded) {
        this.exchangeTraded = exchangeTraded;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCompanySector() {
        return companySector;
    }

    public void setCompanySector(String companySector) {
        this.companySector = companySector;
    }

    // ------------------- Getters and Setters (End) -------------------
}
