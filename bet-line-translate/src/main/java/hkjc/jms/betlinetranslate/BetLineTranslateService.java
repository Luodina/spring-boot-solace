package hkjc.jms.betlinetranslate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;

import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.Iterator;

@Service
public class BetLineTranslateService {

    Logger logger = LoggerFactory.getLogger(BetLineTranslateService.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${hkjc.msg}")
    private String statementMsg;

    @JmsListener(destination = "${hkjc.jms.queue.sub}", containerFactory = "queueListenerFactory")
    public void run(Message msg, Session session) throws Exception {
        TextMessage responseMessage = session.createTextMessage(statementMsg);
        responseMessage.setJMSCorrelationID(msg.getJMSCorrelationID());

        String dest=msg.getJMSDestination().toString();
        String[] arrOfStr = dest.split("/");
        String publishTopic = "";
        for (int i = 0; i < (arrOfStr.length - 2); i++) {
            publishTopic += arrOfStr[i] + "/";
        }
        publishTopic += "translation/pass";
        logger.info("Bet Line Translation Service publishTopic:" + publishTopic);
        logger.info("Bet Line Translation Service responseMessage:" + responseMessage);
        this.jmsTemplate.convertAndSend(publishTopic, responseMessage);
    }

}
