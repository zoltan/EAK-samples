import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.net.*;
import java.util.*;

class EmployeeManagerImpl extends java.rmi.server.UnicastRemoteObject implements EmployeeManager {
    public EmployeeManagerImpl() throws RemoteException {
    }
    
    public List<Employee> getEmployees() throws RemoteException {
	List<Employee> ret = new LinkedList<Employee>();
	
	Employee emp = new Employee(); emp.setNev("teszt1"); ret.add(emp);
	emp = new Employee(); emp.setNev("teszt2"); ret.add(emp);
	emp = new Employee(); emp.setNev("teszt3"); ret.add(emp);
	return ret;
    }
}

public class RmiServer
{
    int thisPort;
    String thisAddress;
    Registry registry;

    public RmiServer() throws RemoteException
    {
        try {
            thisAddress= (InetAddress.getByName("127.0.0.1")).toString();
        }

        catch(Exception e){
            throw new RemoteException("can't get inet address.");
        }

	thisPort = 3232;
        System.out.println("this address="+thisAddress+",port="+thisPort);
        try {
    	    registry = LocateRegistry.createRegistry( thisPort );
            registry.rebind("manager", new EmployeeManagerImpl());
        }
        catch(RemoteException e){
            throw e;
        }
    }
    
    static public void main(String args[])
    {
        try {
    	    RmiServer server = new RmiServer();
	}
	catch (Exception e) {
           e.printStackTrace();
           System.exit(1);
	}
     }
}
