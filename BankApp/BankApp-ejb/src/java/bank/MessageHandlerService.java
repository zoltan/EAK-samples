package bank;

import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;

@MessageDriven(mappedName = "jms/bankQueue", activationConfig = {
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class MessageHandlerService implements MessageListener {
    @Resource(mappedName="jms/boltQueueFactory")
    private QueueConnectionFactory connectionFactory;
    @Resource(mappedName="jms/boltQueue")
    private Queue boltQueue;
    
    private void sendPaymentReceipt(Long orderId, String status) {
        try {
            QueueConnection connection = connectionFactory.createQueueConnection();
            QueueSession session = connection.createQueueSession(true, QueueSession.AUTO_ACKNOWLEDGE);
            QueueSender sender = session.createSender(boltQueue);
            TextMessage request = session.createTextMessage("PAYMENT");
            request.setLongProperty("orderId", orderId);
            request.setStringProperty("status", status);
            connection.start();
            sender.send(request);
            sender.close();
            session.close();
            connection.close();
        } catch(JMSException e) {
            e.printStackTrace();
        }        
        Logger.getLogger("bank").warning("visszakuldtem, status: " + status + ", orderid: " + orderId);
    }
    
    @Override
    public void onMessage(Message message) {
        Logger.getLogger("bank").warning("kaptunk uzit!");
        if (message instanceof TextMessage) {
            TextMessage msg = (TextMessage)message;
            try {
                if("PURCHASE".equals(msg.getText())) {
                    Long orderId = msg.getLongProperty("orderId");
                    Long cardId = msg.getLongProperty("cardId");
                    Long value = msg.getLongProperty("value");

                    if (cardId.equals(2L)) {
                        sendPaymentReceipt(orderId, "APPROVED");
                    } else if(cardId.equals(1L) &&
                                value <= 100) {
                        sendPaymentReceipt(orderId, "APPROVED");
                    } else {
                        sendPaymentReceipt(orderId, "DENIED");
                    }
                } else {
                    Logger.getLogger("bank").warning("nem jo uzi!");
                }
            } catch(JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
