
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import service.DemoServiceRemote;

public class Main {
    public static void main(String[] args) throws Exception {
        Context c = new InitialContext();
	
        DemoServiceRemote service = 
    	    (DemoServiceRemote)c.lookup("service.DemoServiceRemote");
	service.add(10);
	service.add(-15);
	System.out.println(service.getCounter());

        DemoServiceRemote service2 = 
    	    (DemoServiceRemote)c.lookup("service.DemoServiceRemote");
	System.out.println(service2.getCounter());
    }

}
