package service;

import entities.Author;
import entities.Book;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;

@Singleton
@LocalBean
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class Store {
    private ConcurrentHashMap<Long, Author> authors;
    private CopyOnWriteArrayList<Book> books;
    private AtomicLong counter;
    
    @PostConstruct
    public void init() {
        authors = new ConcurrentHashMap<Long, Author>();
        books = new CopyOnWriteArrayList<Book>();
        counter = new AtomicLong(0L);
    }
    
    public Store() {
    }
    
    public void addBook(Long authorId, Book book) {
        book.setId(counter.incrementAndGet());
        book.setAuthor(getAuthor(authorId));
        books.add(book);
    }
    
    public void addAuthor(Author author) {
        author.setId(counter.incrementAndGet());
        authors.put(author.getId(), author);
    }
    
    public Author getAuthor(Long authorId) {
        return authors.get(authorId);
    }
    
    public List<Book> getBooks() {
        return Collections.unmodifiableList(books);
    }
}
