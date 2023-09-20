package Stock;
import java.util.ArrayList;
import org.springframework.stereotype.Service;

@Service
public class StockSeries {
    private Stock stock;
    private ArrayList<StockDataPoint> priceData;
    private String dataSource;

    // ------------------ Getters and Setters (Start) ------------------

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public ArrayList<StockDataPoint> getPriceData() {
        return priceData;
    }

    public void setPriceData(ArrayList<StockDataPoint> priceData) {
        this.priceData = priceData;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    // ------------------- Getters and Setters (End) -------------------
}
