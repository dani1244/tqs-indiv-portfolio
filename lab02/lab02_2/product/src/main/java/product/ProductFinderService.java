package product;

import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ProductFinderService {
    
    private static final String API_BASE_URL = "https://fakestoreapi.com/products/";
    
    private final ISimpleHttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    /**
     * Construtor com Dependency Injection
     * @param httpClient cliente HTTP para fazer requisições
     */
    public ProductFinderService(ISimpleHttpClient httpClient) {
        if (httpClient == null) {
            throw new IllegalArgumentException("HttpClient cannot be null");
        }
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Busca detalhes de um produto pelo ID
     * 
     * @param id ID do produto na API
     * @return Optional contendo o produto se encontrado, ou empty se:
     *         - ID inválido (≤ 0)
     *         - Produto não existe (API retorna vazio)
     *         - Erro de rede ou parsing
     */
    public Optional<Product> findProductDetails(int id) {
        // Validação: IDs inválidos retornam empty sem fazer chamada HTTP
        if (id <= 0) {
            return Optional.empty();
        }
        
        try {
            // Construir URL
            String url = API_BASE_URL + id;
            
            // Fazer requisição HTTP GET (via mock em testes, via real em produção)
            String jsonResponse = httpClient.doHttpGet(url);
            
            // Se resposta vazia ou null, produto não existe
            if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
                return Optional.empty();
            }
            
            // Parse JSON para objeto Product usando Jackson
            Product product = objectMapper.readValue(jsonResponse, Product.class);
            
            return Optional.of(product);
            
        } catch (Exception e) {
            // Qualquer erro (rede, parsing, etc.) retorna empty
            // Em produção, poderíamos logar o erro aqui
            return Optional.empty();
        }
    }
}