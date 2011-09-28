import java.rmi.*;
import java.rmi.registry.*;
import java.net.*;
    
public class RmiClient
{
    static public void main(String args[]) {
	Registry registry;
	String serverAddress = "localhost";
	int serverPort = 3232;

	try {
	    registry = LocateRegistry.getRegistry(serverAddress, serverPort);
	    EmployeeManager manager = (EmployeeManager)(registry.lookup("manager"));
	    for(Employee emp : manager.getEmployees())
		System.out.println(emp.getNev());
       }
       catch(RemoteException e){
           e.printStackTrace();
       }
       catch(NotBoundException e){
           e.printStackTrace();
       }
    }
}
