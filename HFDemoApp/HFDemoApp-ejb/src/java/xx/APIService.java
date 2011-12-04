package xx;

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebService;
import xx.entities.Car;

@Stateless
@WebService
public class APIService implements APIServiceRemote {
    @EJB
    private DataStore dataStore;

    public void clear() {
        dataStore.clear();
    }

    public Integer buy(String name) {
        return dataStore.buy(name);
    }

    public Integer sell(String name, int price) {
        return dataStore.sell(name, price);
    }

    public List<Car> getCars() {
        return dataStore.getCars();
    }    
}
