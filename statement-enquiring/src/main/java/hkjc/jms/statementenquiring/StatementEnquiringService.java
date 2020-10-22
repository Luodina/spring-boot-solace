package hkjc.jms.statementenquiring;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class StatementEnquiringService {

    Logger logger = LoggerFactory.getLogger(StatementEnquiringService.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @JmsListener(destination = "${hkjc.jms.queue.sub}", containerFactory = "queueListenerFactory")
    public void run(Message msg, Session session) throws Exception {

        String dest=msg.getJMSDestination().toString();
        String[] arrOfStr = dest.split("/");
        String publishTopic = "";
        for (int i = 0; i < (arrOfStr.length - 2); i++) {
            publishTopic += arrOfStr[i] + "/";
        }
        publishTopic += "backendprocess/pass";

        String publishObject = new JSONObject()
                .put("accountNo", 12343534)
                .put("channelId",  7)
                .put("sessionId", "abc123")
                .put("betlines", "ST WED WIN 1*1+2+3 $10\n\tST WED WIN 1*1+2+3 $10")
                .put("lang", "en-US")
                .put("checksums", "a123df909")
                .put("longBetLines", "")
                .put("betAccount", "20.00")
                .toString();
        TextMessage responseMessage = session.createTextMessage(publishObject);
        responseMessage.setJMSCorrelationID(msg.getJMSCorrelationID());
        logger.info("Statement Enquiring Service publishTopic:" + publishTopic);
        logger.info("Statement Enquiring Service responseMessage:" + responseMessage);
        this.jmsTemplate.convertAndSend(publishTopic, responseMessage);
    }

}
