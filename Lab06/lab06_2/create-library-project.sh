#!/bin/bash

# Script para criar o projeto Lab 6.2 - Library Management System

set -e

PROJECT_NAME="lab06_2_library"

echo "🚀 Criando projeto Maven: $PROJECT_NAME"
echo ""

# Verificar Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven não encontrado!"
    exit 1
fi

# Criar projeto Maven básico
echo "📦 Criando estrutura Maven..."
mvn archetype:generate \
    -DgroupId=com.library \
    -DartifactId=$PROJECT_NAME \
    -DarchetypeArtifactId=maven-archetype-quickstart \
    -DarchetypeVersion=1.4 \
    -DinteractiveMode=false

cd $PROJECT_NAME

# Limpar ficheiros de exemplo
rm -rf src/main/java/com
rm -rf src/test/java/com

# Criar estrutura
mkdir -p src/main/java/library
mkdir -p src/test/java/library
mkdir -p src/test/resources/library

echo "📝 Criando pom.xml..."
cat > pom.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.library</groupId>
    <artifactId>lab06_2_library</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <cucumber.version>7.14.0</cucumber.version>
        <junit.version>5.10.0</junit.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>io.cucumber</groupId>
                <artifactId>cucumber-bom</artifactId>
                <version>${cucumber.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>org.junit</groupId>
                <artifactId>junit-bom</artifactId>
                <version>${junit.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>io.cucumber</groupId>
            <artifactId>cucumber-java</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>io.cucumber</groupId>
            <artifactId>cucumber-junit-platform-engine</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.platform</groupId>
            <artifactId>junit-platform-suite</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.14.1</version>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.1.2</version>
            </plugin>
        </plugins>
    </build>
</project>
EOF

echo "📝 Criando Book.java..."
cat > src/main/java/library/Book.java << 'EOF'
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
EOF

echo "📝 Criando Library.java..."
cat > src/main/java/library/Library.java << 'EOF'
package library;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Library {
    private List<Book> books;

    public Library() {
        this.books = new ArrayList<>();
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books);
    }

    public List<Book> searchByAuthor(String author) {
        return books.stream()
                .filter(book -> book.getAuthor().equalsIgnoreCase(author))
                .collect(Collectors.toList());
    }

    public List<Book> searchByCategory(String category) {
        return books.stream()
                .filter(book -> book.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public List<Book> searchByTitle(String title) {
        return books.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Book> searchByDateRange(LocalDate startDate, LocalDate endDate) {
        return books.stream()
                .filter(book -> {
                    LocalDate pubDate = book.getPublishedDate();
                    return !pubDate.isBefore(startDate) && !pubDate.isAfter(endDate);
                })
                .collect(Collectors.toList());
    }

    public int getTotalBooks() {
        return books.size();
    }
}
EOF

echo "📝 Criando library.feature..."
cat > src/test/resources/library/library.feature << 'EOF'
Feature: Library Book Search
  As a library user
  I want to search for books
  So that I can find books by different criteria

  Background:
    Given a library with the following books:
      | title              | author          | category    | publishedDate |
      | Clean Code         | Robert Martin   | Programming | 2008-08-01    |
      | The Pragmatic      | Andy Hunt       | Programming | 1999-10-20    |
      | Design Patterns    | Gang of Four    | Programming | 1994-10-21    |
      | Domain-Driven      | Eric Evans      | Architecture| 2003-08-20    |
      | Refactoring        | Martin Fowler   | Programming | 2018-11-20    |

  Scenario: Search books by author
    When the customer searches for books by author "Robert Martin"
    Then 1 book should be found
    And the book title should be "Clean Code"

  Scenario: Search books by category
    When the customer searches for books in category "Programming"
    Then 4 books should be found

  Scenario: Search books by title keyword
    When the customer searches for books with title containing "Design"
    Then 1 book should be found

  Scenario: Search with no results
    When the customer searches for books by author "Unknown Author"
    Then 0 books should be found

  Scenario: Search books by date range
    When the customer searches for books published between 2000-01-01 and 2010-12-31
    Then 2 books should be found
EOF

echo "📝 Criando LibrarySteps.java..."
cat > src/test/java/library/LibrarySteps.java << 'EOF'
package library;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.datatable.DataTable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class LibrarySteps {
    
    private Library library;
    private List<Book> searchResults;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Custom ParameterType for dates
    @ParameterType("\\d{4}-\\d{2}-\\d{2}")
    public LocalDate iso8601Date(String dateString) {
        return LocalDate.parse(dateString, DATE_FORMATTER);
    }

    @Given("a library with the following books:")
    public void aLibraryWithTheFollowingBooks(DataTable dataTable) {
        library = new Library();
        
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        
        for (Map<String, String> row : rows) {
            String title = row.get("title");
            String author = row.get("author");
            String category = row.get("category");
            LocalDate publishedDate = LocalDate.parse(row.get("publishedDate"), DATE_FORMATTER);
            
            Book book = new Book(title, author, category, publishedDate);
            library.addBook(book);
        }
    }

    @When("the customer searches for books by author {string}")
    public void theCustomerSearchesForBooksByAuthor(String author) {
        searchResults = library.searchByAuthor(author);
    }

    @When("the customer searches for books in category {string}")
    public void theCustomerSearchesForBooksInCategory(String category) {
        searchResults = library.searchByCategory(category);
    }

    @When("the customer searches for books with title containing {string}")
    public void theCustomerSearchesForBooksWithTitleContaining(String keyword) {
        searchResults = library.searchByTitle(keyword);
    }

    @When("the customer searches for books published between {iso8601Date} and {iso8601Date}")
    public void theCustomerSearchesForBooksPublishedBetween(LocalDate startDate, LocalDate endDate) {
        searchResults = library.searchByDateRange(startDate, endDate);
    }

    @Then("{int} book(s) should be found")
    public void booksShouldBeFound(int expectedCount) {
        assertEquals(expectedCount, searchResults.size());
    }

    @Then("the book title should be {string}")
    public void theBookTitleShouldBe(String expectedTitle) {
        assertFalse(searchResults.isEmpty(), "No books found");
        assertEquals(expectedTitle, searchResults.get(0).getTitle());
    }
}
EOF

echo "📝 Criando RunCucumberTest.java..."
cat > src/test/java/library/RunCucumberTest.java << 'EOF'
package library;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("library")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "library")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-reports.html")
public class RunCucumberTest {
}
EOF

echo ""
echo "✅ Projeto criado com sucesso!"
echo ""
echo "📂 Estrutura criada:"
find . -name "*.java" -o -name "*.feature" -o -name "pom.xml" | grep -v target | sort

echo ""
echo "🧪 Para testar o projeto:"
echo "   cd $PROJECT_NAME"
echo "   mvn clean test"
echo ""
echo "📊 Relatório HTML estará em: target/cucumber-reports.html"