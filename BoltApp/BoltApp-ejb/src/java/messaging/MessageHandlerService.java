package messaging;

import entities.OrderStatus;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import x.InventoryService;

@MessageDriven(mappedName = "jms/boltQueue", activationConfig = {
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class MessageHandlerService implements MessageListener {
    @EJB
    private InventoryService inventoryService;
    
    @Override
    public void onMessage(Message message) {
        Logger.getLogger("bolt").warning("jott uzi!");
        if (message instanceof TextMessage) {
            TextMessage msg = (TextMessage)message;
            try {
                if("PAYMENT".equals(msg.getText())) {
                inventoryService.setOrderStatus(
                        message.getLongProperty("orderId"),
                        OrderStatus.valueOf(
                            message.getStringProperty("status")
                        ));
                Logger.getLogger("bolt").warning("atallitottuk, order: " + message.getStringProperty("orderId") + ", status: " + message.getStringProperty("status"));
                } else {
                    Logger.getLogger("bolt").warning("nem payment, nem dolgozzuk fel, benne: " + msg.getText());
                }
                
            } catch(JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
