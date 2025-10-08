package product;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Implementação REAL do HTTP Client usando OkHttp
 * Esta implementação faz chamadas HTTP reais à API externa
 */
public class SimpleHttpClient implements ISimpleHttpClient {
    
    private final OkHttpClient client;
    
    /**
     * Construtor padrão com timeout configurado
     */
    public SimpleHttpClient() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }
    
    /**
     * Construtor que aceita cliente customizado (útil para testes)
     */
    public SimpleHttpClient(OkHttpClient client) {
        this.client = client;
    }
    
    /**
     * Executa uma requisição HTTP GET
     * 
     * @param url URL completa para fazer a requisição
     * @return corpo da resposta como String
     * @throws RuntimeException se houver erro na requisição
     */
    @Override
    public String doHttpGet(String url) {
        // Construir requisição
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            // Verificar se resposta foi bem-sucedida
            if (!response.isSuccessful()) {
                throw new RuntimeException("HTTP request failed with code: " + response.code());
            }
            
            // Retornar corpo da resposta
            return response.body() != null ? response.body().string() : "";
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to execute HTTP GET: " + e.getMessage(), e);
        }
    }
}