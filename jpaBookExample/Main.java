import entities.Author;
import entities.Book;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf =
           Persistence.createEntityManagerFactory("EakSampleApplicationPU");

        EntityManager em = emf.createEntityManager();

        Author a = new Author();
        a.setName("Teszt Jozsef");

        em.getTransaction().begin();
        em.persist(a);
        em.flush();
        em.getTransaction().commit();

        Book b = new Book();
        b.setTitle("valami cim");

        em.getTransaction().begin();
        em.persist(b);
        em.flush();
        em.getTransaction().commit();

        em.getTransaction().begin();
        b.addAuthor(a);
        em.getTransaction().commit();

        em.refresh(a);
        System.out.println(a.getBooks());

        em.close();
        emf.close();
    }
}
