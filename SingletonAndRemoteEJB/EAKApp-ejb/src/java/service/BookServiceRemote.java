package service;

import entities.Book;
import java.util.List;
import javax.ejb.Remote;

@Remote
public interface BookServiceRemote {
    public Long addAuthor(String name);
    public Long addBook(Long authorId, String title);    
    public List<Book> getBooks();    
}
