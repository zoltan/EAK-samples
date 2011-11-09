package service;

import entities.Author;
import entities.Book;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;

@Stateless
@LocalBean
public class BookService implements BookServiceRemote {
    @EJB
    private Store store;
    
    public Long addAuthor(String name) {
        Author author = new Author();
        author.setName(name);
        store.addAuthor(author);
        return author.getId();
    }
    
    public Long addBook(Long authorId, String title) {
        Book book = new Book();
        book.setTitle(title);
        store.addBook(authorId, book);
        return book.getId();
    }
    
    public List<Book> getBooks() {
        return store.getBooks();
    }
}
