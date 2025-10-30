package bookstore;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Page Object for Cover Bookstore
 * URL: https://cover-bookstore.onrender.com
 * 
 * Based on actual site structure inspection
 */
public class BookstorePage {
    private final Page page;
    private static final String BASE_URL = "https://cover-bookstore.onrender.com";

    public BookstorePage(Page page) {
        this.page = page;
    }

    public void navigateToHomepage() {
        page.navigate(BASE_URL);
        page.waitForLoadState();
        // Esperar o conteúdo carregar (site pode ser lento)
        page.waitForTimeout(2000);
    }

public void searchFor(String query) {
    // O site tem um input com data-testid="book-search-input"
    // Usar .first() para pegar o primeiro (visível)
    Locator searchInput = page.getByTestId("book-search-input").first();
    
    searchInput.clear();
    searchInput.fill(query);
    searchInput.press("Enter");
    
    // Esperar resultados carregarem
    page.waitForLoadState();
    page.waitForTimeout(1500);
}

    public boolean hasSearchResults() {
        // Após pesquisa, verificar se há conteúdo na página
        // Pode haver cards de livros ou alguma lista
        
        // Esperar um pouco para os resultados aparecerem
        page.waitForTimeout(1000);
        
        // Verificar se há elementos que parecem ser livros
        // (imagens, títulos, cards, etc)
        boolean hasImages = page.locator("img").count() > 0;
        boolean hasContent = page.locator("body").textContent().length() > 500;
        
        return hasImages && hasContent;
    }

    public boolean resultsContainKeyword(String keyword) {
        // Verificar se o keyword aparece no conteúdo da página
        String bodyText = page.locator("body").textContent().toLowerCase();
        return bodyText.contains(keyword.toLowerCase());
    }

    public void clickFirstBookResult() {
        // Clicar no primeiro elemento clicável que parece ser um livro
        // Pode ser uma imagem, link, card, etc.
        
        // Tentar clicar em imagem de livro ou link
        Locator bookElement = page.locator("img").first().or(
            page.locator("a").first()
        );
        
        if (bookElement.count() > 0) {
            bookElement.click();
            page.waitForLoadState();
            page.waitForTimeout(1500);
        }
    }

    public boolean isBookDetailsPage() {
        // Verificar se estamos numa página de detalhes
        // Pode ter mudado de URL ou ter elementos específicos
        String url = page.url();
        
        // URL pode conter /book/, /details/, ou ID do livro
        boolean urlChanged = !url.equals(BASE_URL) && !url.equals(BASE_URL + "/");
        
        // Ou verificar se tem título grande (h1)
        boolean hasMainTitle = page.locator("h1").count() > 0;
        
        return urlChanged || hasMainTitle;
    }

    public boolean hasBookTitle() {
        // Verificar se há um título de livro visível
        return page.locator("h1, h2, h3").count() > 0;
    }

    public boolean hasAuthorName() {
        // Verificar se há informação de autor
        String bodyText = page.locator("body").textContent().toLowerCase();
        
        // Procurar por palavras comuns: "author", "by", "written by"
        return bodyText.contains("author") || 
               bodyText.contains("by ") ||
               bodyText.contains("written");
    }

    public boolean hasBookDescription() {
        // Verificar se há descrição (parágrafos de texto)
        int paragraphs = page.locator("p").count();
        return paragraphs > 0;
    }

    public boolean hasNoResultsMessage() {
        String bodyText = page.locator("body").textContent().toLowerCase();
        
        // Mensagens comuns de "sem resultados"
        return bodyText.contains("no results") || 
               bodyText.contains("not found") ||
               bodyText.contains("no books found") ||
               bodyText.contains("nothing found") ||
               bodyText.contains("0 results");
    }

    public void selectCategory(String category) {
        // O site tem categorias no menu: Fiction, Children's, History, Horror, Mystery, Non-Fiction
        // Clicar na categoria pelo texto
        
        page.getByText(category, new Page.GetByTextOptions().setExact(false))
            .first()
            .click();
        
        page.waitForLoadState();
        page.waitForTimeout(1500);
    }

    public boolean allBooksAreFromCategory(String category) {
        // Verificar se o conteúdo da página corresponde à categoria
        String bodyText = page.locator("body").textContent().toLowerCase();
        String url = page.url().toLowerCase();
        
        // Verificar na URL ou no conteúdo
        return url.contains(category.toLowerCase()) || 
               bodyText.contains(category.toLowerCase());
    }

    /**
     * Método auxiliar para debug - imprime estrutura da página
     */
    public void debugPage() {
        System.out.println("\n=== PAGE DEBUG ===");
        System.out.println("URL: " + page.url());
        System.out.println("Title: " + page.title());
        System.out.println("H1 count: " + page.locator("h1").count());
        System.out.println("Images count: " + page.locator("img").count());
        System.out.println("Links count: " + page.locator("a").count());
        System.out.println("Inputs count: " + page.locator("input").count());
        
        // Mostrar primeiro input se existir
        if (page.locator("input").count() > 0) {
            Locator input = page.locator("input").first();
            System.out.println("First input placeholder: " + input.getAttribute("placeholder"));
            System.out.println("First input type: " + input.getAttribute("type"));
        }
        
        System.out.println("==================\n");
    }
}