import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import product.ISimpleHttpClient;
import product.Product;
import product.ProductFinderService;

@ExtendWith(MockitoExtension.class)
class ProductFinderServiceTest {
    
    @Mock
    private ISimpleHttpClient httpClient;
    
    private ProductFinderService service;
    
    // JSON real da API para produto id=3
    private static final String PRODUCT_3_JSON = """
        {
            "id": 3,
            "title": "Mens Cotton Jacket",
            "price": 55.99,
            "description": "great outerwear jackets for Spring/Autumn/Winter",
            "category": "men's clothing",
            "image": "https://fakestoreapi.com/img/71li-ujtlUL._AC_UX679_.jpg"
        }
        """;
    
    @BeforeEach
    void setUp() {
        service = new ProductFinderService(httpClient);
    }
    
    // TESTE DO CONSTRUTOR 
    
    @Test
    void constructor_WithNullHttpClient_ThrowsException() {
        assertThatThrownBy(() -> new ProductFinderService(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("HttpClient cannot be null");
    }
    
    //  REQUISITO 2.c.i 
    // findProductDetails(3) returns valid Product with id=3 and title="Mens Cotton Jacket"
    
    @Test
    void findProductDetails_WithId3_ReturnsValidProduct() {
        // Arrange - Mock retorna JSON do produto 3
        when(httpClient.doHttpGet("https://fakestoreapi.com/products/3"))
                .thenReturn(PRODUCT_3_JSON);
        
        // Act
        Optional<Product> result = service.findProductDetails(3);
        
        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(3);
        assertThat(result.get().getTitle()).isEqualTo("Mens Cotton Jacket");
        assertThat(result.get().getPrice()).isEqualTo(55.99);
        assertThat(result.get().getCategory()).isEqualTo("men's clothing");
        
        // Verify - HTTP client foi chamado corretamente
        verify(httpClient).doHttpGet("https://fakestoreapi.com/products/3");
        verifyNoMoreInteractions(httpClient);
    }
    
    // REQUISITO 2.c.ii 
    // findProductDetails(300) returns empty Optional (produto não existe)
    
    @Test
    void findProductDetails_WithId300_ReturnsEmpty() {
        // Arrange - Produto 300 não existe, retorna string vazia ou null
        when(httpClient.doHttpGet("https://fakestoreapi.com/products/300"))
                .thenReturn("");
        
        // Act
        Optional<Product> result = service.findProductDetails(300);
        
        // Assert
        assertThat(result).isEmpty();
        
        // Verify
        verify(httpClient).doHttpGet("https://fakestoreapi.com/products/300");
    }
    
    //TESTES ADICIONAIS (Edge Cases) 
    
    @Test
    void findProductDetails_WithInvalidId_ReturnsEmpty() {
        // IDs inválidos não devem fazer chamada HTTP
        assertThat(service.findProductDetails(0)).isEmpty();
        assertThat(service.findProductDetails(-1)).isEmpty();
        
        verifyNoInteractions(httpClient);
    }
    
    @Test
    void findProductDetails_WhenHttpThrowsException_ReturnsEmpty() {
        // Arrange - Simula erro de rede
        when(httpClient.doHttpGet(anyString()))
                .thenThrow(new RuntimeException("Network error"));
        
        // Act
        Optional<Product> result = service.findProductDetails(1);
        
        // Assert
        assertThat(result).isEmpty();
    }
    
    @Test
    void findProductDetails_WithMalformedJson_ReturnsEmpty() {
        // Arrange - JSON inválido
        when(httpClient.doHttpGet(anyString()))
                .thenReturn("{invalid json");
        
        // Act
        Optional<Product> result = service.findProductDetails(5);
        
        // Assert
        assertThat(result).isEmpty();
    }
    
    @Test
    void findProductDetails_WithNullResponse_ReturnsEmpty() {
        // Arrange
        when(httpClient.doHttpGet(anyString())).thenReturn(null);
        
        // Act
        Optional<Product> result = service.findProductDetails(10);
        
        // Assert
        assertThat(result).isEmpty();
    }
}