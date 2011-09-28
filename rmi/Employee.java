import java.util.*;
import java.io.*;

public class Employee implements Serializable {
    private String nev;
    
    public void setNev(String nev) {
	this.nev = nev;
    }
    
    public String getNev() {
	return nev;
    }
}
