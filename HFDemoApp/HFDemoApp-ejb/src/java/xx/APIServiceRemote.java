package xx;

import java.util.List;
import javax.ejb.Remote;
import xx.entities.Car;

@Remote
public interface APIServiceRemote {
    public void clear();
    public Integer buy(String name);    
    public Integer sell(String name, int price);
    public List<Car> getCars();    
}
