package eda.club.demo;

import java.util.UUID;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.support.JmsUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 


@RestController
public class HelloController {

    @Value("${club.jms.queue.transReplyQueue}")
    private String replyQueueName;
    
    @Value("${club.jms.topic.trans}")
    private String topicTemplate;

    @Value("${club.jms.statementMsg}")
    private String statementMsg;

    Logger logger = LoggerFactory.getLogger(HelloController.class);

    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private JmsMessagingTemplate jmsMessageTemplate;

    @RequestMapping("/plancebet/{instanceId}/{acctNo}")
    public void plancebet(@PathVariable long instanceId, @PathVariable String acctNo) {
        UUID uuid = UUID.randomUUID();
        String topicName = String.format(topicTemplate, "plancebet", instanceId, uuid, acctNo);
        String msg = "Hello plancebet ";
        logger.info("about to send message=" + msg + " to topic=" + topicName);
        this.jmsTemplate.convertAndSend(topicName, msg, messagePostProcessor -> {
            messagePostProcessor.setStringProperty("correlation", uuid.toString());
            return messagePostProcessor;
        });
    }

    @RequestMapping("/statement/{instanceId}/{acctNo}")
    public String balance(@PathVariable long instanceId, @PathVariable String acctNo) throws JMSException {
        UUID uuid = UUID.randomUUID();
        String topicName = String.format(topicTemplate, "statement", instanceId, uuid, acctNo);
        
  
        JSONObject jsonObject = new JSONObject (statementMsg);
        logger.info(String.format(jsonObject.getString("publishTopic") , instanceId, uuid, acctNo));
        jsonObject.put("publishTopic", String.format(jsonObject.getString("publishTopic") , instanceId, uuid, acctNo));
     

        jmsTemplate.setReceiveTimeout(30000L);
        jmsMessageTemplate.setJmsTemplate(jmsTemplate);

        Session session = jmsMessageTemplate.getConnectionFactory().createConnection()
                .createSession(false, Session.AUTO_ACKNOWLEDGE);

        logger.info(jsonObject.toString());
        TextMessage objectMessage = session.createTextMessage(jsonObject.toString());
        String correctionId = uuid.toString();
        objectMessage.setJMSCorrelationID(correctionId);
        logger.info(replyQueueName);
        //objectMessage.setJMSReplyTo(session.createQueue(replyQueueName));
        //objectMessage.setJMSExpiration(30000L);
        //objectMessage.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
        //objectMessage.setStringProperty("action", "balance");
        //objectMessage.setStringProperty("acctNo", acctNo);
        //objectMessage.setLongProperty("instanceId", instanceId);
        //objectMessage.setStringProperty("sessionId", uuid.toString());

        //objectMessage.setStringProperty("sessionId", uuid.toString());

        //TextMessage message = session.createTextMessage(jsonObject.toString());
       // message.setJMSCorrelationID(correctionId);
        //jmsTemplate.convertAndSend(topicName, message);
        //Message replyMsg = this.jmsTemplate.receiveSelected(replyQueueName, "JMSCorrelationID = '" + correctionId +"'");

      
        Message replyMsg = this.doSendAndReceive(session, session.createTopic(topicName),  new MessageCreator() {


            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage message = session.createTextMessage(jsonObject.toString());
                message.setJMSCorrelationID(correctionId);
                return message;
            }
        }, session.createQueue(replyQueueName),correctionId );
       
        TextMessage text = null;
        if (replyMsg != null) {
            text = (TextMessage) replyMsg;
            logger.info(text.getText());
            return text.getText();
        }
      
         return null;   
 
      }

     
      

   
    private Message doSendAndReceive(Session session, Destination destination, MessageCreator messageCreator, Destination replyTo, String correctionId) throws JMSException {
       MessageProducer producer = null;
      
      
       try {
          Message requestMessage = messageCreator.createMessage(session);
          producer = session.createProducer(destination);
          requestMessage.setJMSReplyTo(replyTo);
          this.doSend(producer, requestMessage);
        
         // var8 = this.receiveFromConsumer(consumer, this.jmsTemplate.getReceiveTimeout());
          //this.logger.info("Sending created message:5 " + var8 );

       } finally {
          //JmsUtils.closeMessageConsumer(consumer);
          JmsUtils.closeMessageProducer(producer);
        
       }
       return this.jmsTemplate.receiveSelected(replyTo, "JMSCorrelationID = '" + correctionId +"'");
        
    }

    private void doSend(MessageProducer producer, Message message) throws JMSException {
 
  
        if (this.jmsTemplate.isExplicitQosEnabled()) {
           producer.send(message, this.jmsTemplate.getDeliveryMode(), this.jmsTemplate.getPriority(), this.jmsTemplate.getTimeToLive());
        } else {
           producer.send(message);
            
        }
  
     }
 
      
     
}
