package library;

import java.time.LocalDate;
import java.util.Objects;

public class Book {
    private String title;
    private String author;
    private String category;
    private LocalDate publishedDate;

    public Book(String title, String author, String category, LocalDate publishedDate) {
        this.title = title;
        this.author = author;
        this.category = category;
        this.publishedDate = publishedDate;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public LocalDate getPublishedDate() {
        return publishedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(title, book.title) &&
               Objects.equals(author, book.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author);
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", category='" + category + '\'' +
                ", publishedDate=" + publishedDate +
                '}';
    }
}
