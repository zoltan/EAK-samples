package hfdemoclient;

import java.util.UUID;
import javax.naming.Context;
import javax.naming.InitialContext;
import xx.APIServiceRemote;
import xx.entities.Car;

public class HFDemoClient {
    public static void main(String[] args) throws Exception {
        Context c = new InitialContext();

        APIServiceRemote service =
                (APIServiceRemote)c.lookup("xx.APIServiceRemote");
        String x = UUID.randomUUID().toString();
        service.sell(UUID.randomUUID().toString(), 100000);
        service.sell(x, 200000);
        service.sell(UUID.randomUUID().toString(), 300000);
        service.buy(x);
        for(Car car : service.getCars())
            System.out.println(car);
    }
}
