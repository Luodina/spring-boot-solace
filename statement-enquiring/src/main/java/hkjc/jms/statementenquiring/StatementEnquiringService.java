package hkjc.jms.ewinbridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;

import java.util.Iterator;

@Service
public class StatementEnquiringService {

    Logger logger = LoggerFactory.getLogger(StatementEnquiringService.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @JmsListener(destination = "${hkjc.jms.queue.sub}", containerFactory = "queueListenerFactory")
    public void run(Message<?> msg) throws Exception {
        //StringBuffer msgAsStr = new StringBuffer("============= Received \nHeaders:");
        MessageHeaders hdrs = msg.getHeaders();
        String topicName = hdrs.get("jms_destination").toString()+"/pass";
        logger.info("received topicName:", topicName);
//        msgAsStr.append("\nUUID: "+hdrs.getId());
//        msgAsStr.append("\nTimestamp: "+hdrs.getTimestamp());
//        Iterator<String> keyIter = hdrs.keySet().iterator();
//        while (keyIter.hasNext()) {
//            String key = keyIter.next();
//            msgAsStr.append("\n"+key+": "+hdrs.get(key));
//        }
//        msgAsStr.append("\nPayload: "+msg.getPayload());
//        logger.info(msgAsStr.toString());
        //this.jmsTemplate.convertAndSend("${hkjc.jms.topic.pub}", topicName);
        logger.info("topicName: ", topicName, " has been sent" );
        return;
    }

}
