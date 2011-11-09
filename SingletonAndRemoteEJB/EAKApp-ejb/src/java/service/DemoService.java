package service;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class DemoService implements DemoServiceRemote {
    private Integer counter = 0;
    
    public void add(Integer arg) {
        counter += arg;
    }
    
    public Integer getCounter() {
        return counter;
    }
}
