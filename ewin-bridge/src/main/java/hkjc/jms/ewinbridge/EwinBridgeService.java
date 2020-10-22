package hkjc.jms.ewinbridge;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;

import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.Iterator;

@Service
public class EwinBridgeService {

    Logger logger = LoggerFactory.getLogger(EwinBridgeService.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @JmsListener(destination = "${hkjc.jms.queue.sub}", containerFactory = "queueListenerFactory")
    public void run(Message msg, Session session) throws Exception {

        String  msgBody = ((TextMessage)msg).getText();
        JSONObject jsonObjectBody = new JSONObject(msgBody);
        String publishTopic = jsonObjectBody.getString("publishTopic");
        String publishObject = jsonObjectBody.getJSONObject("publishObject").toString();
        TextMessage responseMessage = session.createTextMessage(publishObject);
        responseMessage.setJMSCorrelationID(msg.getJMSCorrelationID());
        logger.info("Bet Line Translation Service publishTopic:" + publishTopic);
        logger.info("Bet Line Translation Service responseMessage:" + responseMessage);
        this.jmsTemplate.convertAndSend(publishTopic, responseMessage);
    }

}
