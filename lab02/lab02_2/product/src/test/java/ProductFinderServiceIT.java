import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import product.ISimpleHttpClient;
import product.Product;
import product.ProductFinderService;
import product.SimpleHttpClient;

/**
 * Testes de INTEGRAÇÃO para ProductFinderService
 * 
 * Diferenças dos testes unitários:
 * - SEM Mockito (sem @ExtendWith, sem @Mock)
 * - Usa implementação REAL do HTTP Client
 * - Faz chamadas REAIS à API https://fakestoreapi.com
 * - Mais lento (depende de rede)
 * - Pode falhar se API estiver offline
 * - Sufixo IT (não Test) para ser executado pelo failsafe plugin
 */
class ProductFinderServiceIT {
    
    private ProductFinderService service;
    private ISimpleHttpClient realHttpClient;
    
    @BeforeEach
    void setUp() {
        // Usa implementação REAL do HTTP Client
        realHttpClient = new SimpleHttpClient();
        service = new ProductFinderService(realHttpClient);
    }
    
    // ========== TESTES COM API REAL ==========
    
    @Test
    void findProductDetails_WithRealApi_Product3_ReturnsValidProduct() {
        // Act - Faz chamada REAL à API
        Optional<Product> result = service.findProductDetails(3);
        
        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(3);
        assertThat(result.get().getTitle()).isEqualTo("Mens Cotton Jacket");
        assertThat(result.get().getPrice()).isGreaterThan(0);
        assertThat(result.get().getCategory()).isEqualTo("men's clothing");
        assertThat(result.get().getDescription()).isNotBlank();
        assertThat(result.get().getImage()).isNotBlank();
        
        // Note: Não usamos verify() porque não há mocks!
    }
    
    @Test
    void findProductDetails_WithRealApi_Product1_ReturnsValidProduct() {
        // Act - Outro produto para garantir que API está funcionando
        Optional<Product> result = service.findProductDetails(1);
        
        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1);
        assertThat(result.get().getTitle()).isNotBlank();
        assertThat(result.get().getPrice()).isGreaterThan(0);
        assertThat(result.get().getCategory()).isNotBlank();
    }
    
    @Test
    void findProductDetails_WithRealApi_Product20_ReturnsValidProduct() {
        // Act - Testar último produto da API (id=20)
        Optional<Product> result = service.findProductDetails(20);
        
        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(20);
        assertThat(result.get().getTitle()).isNotBlank();
    }
    
    @Test
    void findProductDetails_WithRealApi_NonExistent_ReturnsEmpty() {
        // Act - Produto 9999 não existe na API
        Optional<Product> result = service.findProductDetails(9999);
        
        // Assert - API retorna erro 404, nosso código trata e retorna empty
        assertThat(result).isEmpty();
    }
    
    @Test
    void findProductDetails_WithRealApi_InvalidId_ReturnsEmpty() {
        // Act - IDs inválidos não fazem chamada HTTP
        Optional<Product> result = service.findProductDetails(-1);
        
        // Assert
        assertThat(result).isEmpty();
    }
    
    @Test
    void findProductDetails_WithRealApi_ZeroId_ReturnsEmpty() {
        // Act
        Optional<Product> result = service.findProductDetails(0);
        
        // Assert
        assertThat(result).isEmpty();
    }
    
    /**
     * Teste para confirmar que a API real está sendo chamada
     * 
     * Para verificar:
     * 1. Execute com internet: deve passar
     * 2. Desconecte da internet e execute novamente: deve falhar
     * 
     * Isto prova que estamos fazendo chamadas HTTP reais!
     */
    @Test
    void findProductDetails_WithRealApi_ConfirmRealHttpCall() {
        // Este teste só passa se conseguir conectar à API real
        Optional<Product> result = service.findProductDetails(5);
        
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(5);
        
        // Se este teste passar, confirma que:
        // - Temos conexão à internet
        // - API está disponível
        // - HTTP Client real está funcionando
    }
}