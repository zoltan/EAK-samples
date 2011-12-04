package xx;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import xx.entities.Car;

@Singleton
@LocalBean
public class DataStore {
    private Map<String, Car> datas;
    
    @PostConstruct
    private void init() {
        datas = new HashMap<String, Car>();
    }
    
    public void clear() {
        datas.clear();
    }
    
    public Integer buy(String name) {
        Car car = datas.remove(name);
        return car.getPrice();
    }
    
    public Integer sell(String name, int price) {
        Car car = new Car();
        car.setName(name);
        car.setPrice(price - 10000);
        datas.put(name, car);
        return car.getPrice();
    }
    
    public List<Car> getCars() {
        return Collections.unmodifiableList(
                new LinkedList<Car>(datas.values()));
    }
}
