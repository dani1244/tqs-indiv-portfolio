package product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {
    
    @JsonProperty("id")
    private int id;
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("price")
    private double price;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("category")
    private String category;
    
    @JsonProperty("image")
    private String image;
    
    // Construtor vazio (necessário para Jackson)
    public Product() {}
    
    // Construtor completo (útil para testes)
    public Product(int id, String title, double price, String description, 
                   String category, String image) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.description = description;
        this.category = category;
        this.image = image;
    }
    
    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    
    @Override
    public String toString() {
        return String.format("Product{id=%d, title='%s', price=%.2f}", id, title, price);
    }
}