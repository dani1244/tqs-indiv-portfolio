package product;

public interface ISimpleHttpClient {
    /**
     * Executa uma requisição HTTP GET
     * @param url URL para fazer a requisição
     * @return conteúdo da resposta como String (JSON)
     */
    String doHttpGet(String url);
}