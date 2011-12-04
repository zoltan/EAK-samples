package wsclient;

import java.util.UUID;
import qwe.Car;

public class WSClient {
    public static void main(String[] args) {
        qwe.APIServiceService serviceX = new qwe.APIServiceService();
        qwe.APIService service = serviceX.getAPIServicePort();

        String x = UUID.randomUUID().toString();
        service.sell(UUID.randomUUID().toString(), 100000);
        service.sell(x, 200000);
        service.sell(UUID.randomUUID().toString(), 300000);
        service.buy(x);
        for(Car car : service.getCars())
            System.out.println(car.getName() + ", " + 
                    car.getPrice());
        
    }
}
