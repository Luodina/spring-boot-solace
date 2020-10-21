package hkjc.jms.ewinbridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.support.destination.DynamicDestinationResolver;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.util.ErrorHandler;

import javax.jms.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

@EnableJms
@SpringBootApplication
@EnableScheduling
public class EwinBridgeApplication {

	public static void main(String[] args) {
		SpringApplication.run(EwinBridgeApplication.class, args);
	}

	// override default jms container factory with topic behaviour
	//	@Bean
	//	public JmsListenerContainerFactory<?> topicListenerFactory(ConnectionFactory connectionFactory,
	//															   DefaultJmsListenerContainerFactoryConfigurer configurer) {
	//		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
	//		factory.setPubSubDomain(true); // true for topic
	//		// This provides all boot's default to this factory, including the message converter
	//		configurer.configure(factory, connectionFactory);
	//		// perhaps externalize other factory configuration
	//		return factory;
	//	}

		// override default jms container factory with queue behaviour
	//	@Bean
	//	public JmsListenerContainerFactory<?> queueListenerFactory(ConnectionFactory connectionFactory,
	//															   DefaultJmsListenerContainerFactoryConfigurer configurer) {
	//		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
	//		factory.setPubSubDomain(false); // false for queue
	//		// This provides all boot's default to this factory, including the message converter
	//		configurer.configure(factory, connectionFactory);
	//		// perhaps externalize other factory configuration
	//		return factory;
	//	}

	@Bean
	public DefaultJmsListenerContainerFactory queueListenerFactory(ConnectionFactory connectionFactory, DemoErrorHandler errorHandler) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setErrorHandler(errorHandler);
		return factory;
	}
	// sets the pubSubDomain on the destination when jms template send message
	@Bean
	public DynamicDestinationResolver destinationResolver() {
		return new DynamicDestinationResolver() {
			@Override
			public Destination resolveDestinationName(Session session, String destinationName, boolean pubSubDomain) throws JMSException, JMSException {
				// may consider adding condition, when for topic or queue
				//if (destinationName.contains(".topic."))
				System.out.println("destinationResolver====" + destinationName);
				pubSubDomain = true;
				return super.resolveDestinationName(session, destinationName, pubSubDomain);
			}
		};
	}

	@Service
	public class DemoErrorHandler implements ErrorHandler {

		public void handleError(Throwable t) {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(os);
			t.printStackTrace(ps);
			try {
				String output = os.toString("UTF8");
				System.out.println("============= Error processing message: " + t.getMessage()+"\n"+output);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	//
	//	@Bean // Serialize message content to json using TextMessage
	//	public MessageConverter jacksonJmsMessageConverter() {
	//		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
	//		converter.setTargetType(MessageType.TEXT);
	//		converter.setTypeIdPropertyName("_type");
	//		return converter;
	//	}
	//
}
