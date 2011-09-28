import java.rmi.*;
import java.util.*;

public interface EmployeeManager extends Remote
{
    List<Employee> getEmployees() throws RemoteException;
}
