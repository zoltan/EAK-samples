package service;

import javax.ejb.Remote;

@Remote
public interface DemoServiceRemote {
    public void add(Integer arg);
    public Integer getCounter();
}
