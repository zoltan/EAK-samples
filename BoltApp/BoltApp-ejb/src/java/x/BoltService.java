package x;

import entities.OrderStatus;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;
import javax.jws.WebService;

@Stateless
@LocalBean
@WebService
public class BoltService {
    @EJB
    private InventoryService inventoryService;
    @Resource(mappedName="jms/bankQueueFactory")
    private QueueConnectionFactory connectionFactory;
    @Resource(mappedName="jms/bankQueue")
    private Queue bankQueue;    
    
    private void sendPaymentRequest(Long orderId,
                                    Long cardNumber,
                                    Long price) {
        try {
            QueueConnection connection = connectionFactory.createQueueConnection();
            QueueSession session = connection.createQueueSession(true, QueueSession.AUTO_ACKNOWLEDGE);
            QueueSender sender = session.createSender(bankQueue);
            TextMessage request = session.createTextMessage("PURCHASE");
            request.setLongProperty("orderId", orderId);
            request.setLongProperty("value", price);
            request.setLongProperty("cardId", cardNumber);
            connection.start();
            sender.send(request);
            sender.close();
            session.close();
            connection.close();
        } catch(JMSException e) {
            e.printStackTrace();
        }        
        
        Logger.getLogger("x").warning("haho!");
    }
    
    public Long buy(Long itemId, Long cardNumber) {
        Long price = inventoryService.getItemPrice(itemId);
        Long orderId = inventoryService.addOrder();
        Logger.getLogger("x").warning("eladunk");
        sendPaymentRequest(orderId, cardNumber, price);
        return orderId;
    }
    
    public OrderStatus getOrderStatus(Long orderId) {
        return inventoryService.getOrderStatus(orderId);
    }
}
