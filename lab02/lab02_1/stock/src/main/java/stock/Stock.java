package stock;

public class Stock {
    private final String symbol;
    private final int quantity;

        public Stock(String symbol, int quantity) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Symbol cannot be null or empty");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        this.symbol = symbol;
        this.quantity = quantity;
    }
    public String getSymbol(){
        return symbol;
    }
    public int getQuantity(){
        return quantity;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Stock stock = (Stock) obj;
        return quantity == stock.quantity && symbol.equals(stock.symbol);
    }

    @Override
    public int hashCode(){
        return symbol.hashCode()*31*quantity;
    }

    @Override
        public String toString() {
        return String.format("Stock{symbol='%s', quantity=%d}", symbol, quantity);
    }
}
