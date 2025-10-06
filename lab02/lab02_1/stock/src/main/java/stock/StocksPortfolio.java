package stock;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StocksPortfolio {
    private final List<Stock> stocks;
    private final IStockMarketService stockMarketService;
    
    /**
     * Construtor que aceita o serviço de mercado (Dependency Injection)
     */
    public StocksPortfolio(IStockMarketService stockMarketService) {
        if (stockMarketService == null) {
            throw new IllegalArgumentException("StockMarketService cannot be null");
        }
        
        this.stocks = new ArrayList<>();
        this.stockMarketService = stockMarketService;
    }
    

    public void addStock(Stock stock) {
        if (stock == null) {
            throw new IllegalArgumentException("Stock cannot be null");
        }
        stocks.add(stock);
    }
    
   
    public double totalValue() {
        return stocks.stream()
                .mapToDouble(stock -> {
                    double price = stockMarketService.getPrice(stock.getSymbol());
                    return price * stock.getQuantity();
                })
                .sum();
    }
    
   
    public List<Stock> mostValuableStocks(int topN) {
        if (topN <= 0) {
            throw new IllegalArgumentException("topN must be positive");
        }
        
        return stocks.stream()
                .sorted((stock1, stock2) -> {
                    double value1 = stockMarketService.getPrice(stock1.getSymbol()) * stock1.getQuantity();
                    double value2 = stockMarketService.getPrice(stock2.getSymbol()) * stock2.getQuantity();
                    return Double.compare(value2, value1); // Ordem decrescente
                })
                .limit(topN)
                .collect(Collectors.toList());
    }
    
    /**
     * Retorna todas as ações do portfólio
     */
    public List<Stock> getStocks() {
        return new ArrayList<>(stocks); 
    }
}
