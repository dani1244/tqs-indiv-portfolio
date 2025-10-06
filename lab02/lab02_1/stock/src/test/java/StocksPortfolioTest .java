

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import stock.IStockMarketService;
import stock.Stock;
import stock.StocksPortfolio;



@ExtendWith(MockitoExtension.class)
class StocksPortfolioTest {
    
    @Mock
    private IStockMarketService stockMarketService;
    
    private StocksPortfolio portfolio;
    
    @BeforeEach
    void setUp() {
        portfolio = new StocksPortfolio(stockMarketService);
    }
    
    // ============= TESTES DO CONSTRUTOR =============
    
    @Test
    void constructor_WithNullService_ThrowsException() {
        assertThatThrownBy(() -> new StocksPortfolio(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("StockMarketService cannot be null");
    }
    
    // ============= TESTES DO totalValue() =============
    
    @Test
    void totalValue_WithEmptyPortfolio_ReturnsZero() {
        // Arrange - portfolio já está vazio
        
        // Act
        double total = portfolio.totalValue();
        
        // Assert
        assertThat(total).isEqualTo(0.0);
        
        // Verify - nenhuma chamada ao serviço deve ter sido feita
        verifyNoInteractions(stockMarketService);
    }
    
    @Test
    void totalValue_WithSingleStock_ReturnsCorrectValue() {
        // Arrange
        Stock appleStock = new Stock("AAPL", 10);
        portfolio.addStock(appleStock);
        
        when(stockMarketService.getPrice("AAPL")).thenReturn(150.0);
        
        // Act
        double total = portfolio.totalValue();
        
        // Assert
        assertThat(total).isEqualTo(1500.0); // 10 * 150.0
        
        // Verify
        verify(stockMarketService).getPrice("AAPL");
    }
    
    @Test
    void totalValue_WithMultipleStocks_ReturnsCorrectSum() {
        // Arrange
        Stock appleStock = new Stock("AAPL", 10);
        Stock googleStock = new Stock("GOOGL", 5);
        Stock microsoftStock = new Stock("MSFT", 20);
        
        portfolio.addStock(appleStock);
        portfolio.addStock(googleStock);
        portfolio.addStock(microsoftStock);
        
        // Setup mock expectations
        when(stockMarketService.getPrice("AAPL")).thenReturn(150.0);
        when(stockMarketService.getPrice("GOOGL")).thenReturn(2500.0);
        when(stockMarketService.getPrice("MSFT")).thenReturn(300.0);
        
        // Act
        double total = portfolio.totalValue();
        
        // Assert
        // AAPL: 10 * 150 = 1500
        // GOOGL: 5 * 2500 = 12500  
        // MSFT: 20 * 300 = 6000
        // Total: 20000
        assertThat(total).isEqualTo(20000.0);
        
        // Verify all interactions
        verify(stockMarketService).getPrice("AAPL");
        verify(stockMarketService).getPrice("GOOGL");
        verify(stockMarketService).getPrice("MSFT");
        verifyNoMoreInteractions(stockMarketService);
    }
    
    @Test  
    void totalValue_WithExtraUnusedMockExpectations_ShowsLenientBehavior() {
        // Arrange
        Stock appleStock = new Stock("AAPL", 10);
        portfolio.addStock(appleStock);
        
        // Setup MORE expectations than needed - usa lenient() para evitar erro
        when(stockMarketService.getPrice("AAPL")).thenReturn(150.0);
        lenient().when(stockMarketService.getPrice("GOOGL")).thenReturn(2500.0); // Lenient!
        lenient().when(stockMarketService.getPrice("MSFT")).thenReturn(300.0);   // Lenient!
        
        // Act
        double total = portfolio.totalValue();
        
        // Assert
        assertThat(total).isEqualTo(1500.0);
        
        // Verify only AAPL was called
        verify(stockMarketService).getPrice("AAPL");
        // As expectativas GOOGL e MSFT não foram usadas, mas com lenient() não dá erro
    }
    
    // ============= TESTES DO mostValuableStocks() =============
    
    @Test
    void mostValuableStocks_WithNegativeN_ThrowsException() {
        assertThatThrownBy(() -> portfolio.mostValuableStocks(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("topN must be positive");
    }
    
    @Test
    void mostValuableStocks_WithZeroN_ThrowsException() {
        assertThatThrownBy(() -> portfolio.mostValuableStocks(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("topN must be positive");
    }
    
    @Test
    void mostValuableStocks_WithEmptyPortfolio_ReturnsEmptyList() {
        // Act
        List<Stock> result = portfolio.mostValuableStocks(3);
        
        // Assert
        assertThat(result).isEmpty();
    }
    
    @Test
    void mostValuableStocks_WithTop3_ReturnsCorrectOrder() {
        // Arrange
        Stock lowValueStock = new Stock("LOW", 10);    // 10 * 50 = 500
        Stock highValueStock = new Stock("HIGH", 5);   // 5 * 1000 = 5000
        Stock midValueStock = new Stock("MID", 20);    // 20 * 100 = 2000
        
        portfolio.addStock(lowValueStock);
        portfolio.addStock(highValueStock);
        portfolio.addStock(midValueStock);
        
        // Setup prices
        when(stockMarketService.getPrice("LOW")).thenReturn(50.0);
        when(stockMarketService.getPrice("HIGH")).thenReturn(1000.0);
        when(stockMarketService.getPrice("MID")).thenReturn(100.0);
        
        // Act
        List<Stock> top3 = portfolio.mostValuableStocks(3);
        
        // Assert - deve retornar em ordem decrescente de valor
        assertThat(top3)
                .hasSize(3)
                .containsExactly(highValueStock, midValueStock, lowValueStock);
        
        // Verify todas as chamadas foram feitas (ordenação requer consultar preços)
        verify(stockMarketService, atLeastOnce()).getPrice("LOW");
        verify(stockMarketService, atLeastOnce()).getPrice("HIGH");
        verify(stockMarketService, atLeastOnce()).getPrice("MID");
    }
    
    @Test
    void mostValuableStocks_WithTopNGreaterThanPortfolioSize_ReturnsAllStocks() {
        // Arrange
        Stock stock1 = new Stock("S1", 10);
        Stock stock2 = new Stock("S2", 5);
        
        portfolio.addStock(stock1);
        portfolio.addStock(stock2);
        
        when(stockMarketService.getPrice("S1")).thenReturn(100.0);
        when(stockMarketService.getPrice("S2")).thenReturn(200.0);
        
        // Act - pedimos top 5, mas só temos 2 ações
        List<Stock> result = portfolio.mostValuableStocks(5);
        
        // Assert
        assertThat(result).hasSize(2);
    }
    
    // ============= TESTES DO addStock() =============
    
    @Test
    void addStock_WithNullStock_ThrowsException() {
        assertThatThrownBy(() -> portfolio.addStock(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Stock cannot be null");
    }
    
    @Test
    void addStock_WithValidStock_AddsToPortfolio() {
        // Arrange
        Stock stock = new Stock("TEST", 15);
        
        // Act
        portfolio.addStock(stock);
        
        // Assert
        assertThat(portfolio.getStocks())
                .hasSize(1)
                .contains(stock);
    }
    
    // ============= TESTES DA CLASSE Stock =============
    
    @Test
    void stock_Constructor_WithValidValues_CreatesStock() {
        // Act
        Stock stock = new Stock("AAPL", 10);
        
        // Assert
        assertThat(stock.getSymbol()).isEqualTo("AAPL");
        assertThat(stock.getQuantity()).isEqualTo(10);
    }
    
    @Test
    void stock_Constructor_WithNullSymbol_ThrowsException() {
        assertThatThrownBy(() -> new Stock(null, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Symbol cannot be null or empty");
    }
    
    @Test
    void stock_Constructor_WithEmptySymbol_ThrowsException() {
        assertThatThrownBy(() -> new Stock("", 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Symbol cannot be null or empty");
    }
    
    @Test
    void stock_Constructor_WithZeroQuantity_ThrowsException() {
        assertThatThrownBy(() -> new Stock("AAPL", 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantity must be positive");
    }
    
    @Test
    void stock_Constructor_WithNegativeQuantity_ThrowsException() {
        assertThatThrownBy(() -> new Stock("AAPL", -5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantity must be positive");
    }
    
    @Test
    void stock_Equals_WithSameValues_ReturnsTrue() {
        // Arrange
        Stock stock1 = new Stock("AAPL", 10);
        Stock stock2 = new Stock("AAPL", 10);
        
        // Assert
        assertThat(stock1).isEqualTo(stock2);
        assertThat(stock1.hashCode()).isEqualTo(stock2.hashCode());
    }
    
    @Test
    void stock_Equals_WithDifferentValues_ReturnsFalse() {
        // Arrange
        Stock stock1 = new Stock("AAPL", 10);
        Stock stock2 = new Stock("GOOGL", 10);
        Stock stock3 = new Stock("AAPL", 5);
        
        // Assert
        assertThat(stock1).isNotEqualTo(stock2);
        assertThat(stock1).isNotEqualTo(stock3);
    }
}