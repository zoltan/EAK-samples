package x;

import entities.Order;
import entities.OrderStatus;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;

@Stateless
@LocalBean
public class InventoryService {
    private Map<Long, Long> products;
    private Map<Long, Order> orders;
    private AtomicLong orderIds;
    
    @PostConstruct
    private void init() {
        products = new HashMap<Long, Long>();
        orders = new HashMap<Long, Order>();
        orderIds = new AtomicLong(1L);
        
        products.put(1L, 100L);
        products.put(2L, 200L);
    }
    
    public OrderStatus getOrderStatus(Long orderId) {
        return orders.get(orderId).getStatus();
    }
    
    public Long addOrder() {
        Order order = new Order();
        order.setId(orderIds.getAndIncrement());
        order.setStatus(OrderStatus.NEW);
        orders.put(order.getId(), order);
        return order.getId();
    }
    
    public void setOrderStatus(Long orderId, OrderStatus status) {
        orders.get(orderId).setStatus(status);
    }
    
    public Long getItemPrice(Long itemId) {
        return products.get(itemId);
    }
}
