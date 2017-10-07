package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.stream.binding.BindingService;
import org.springframework.cloud.stream.binding.BindingTargetFactory;
import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Component;

@Component
public class MessageListener implements CommandLineRunner {

	@Autowired
	private ConfigurableListableBeanFactory beanFactory;

	@Autowired
	private BindingTargetFactory bindingTargetFactory;

	@Autowired
	private BindingService bindingService;

	@Autowired
	BindingServiceProperties bindingServiceProperties;

	@Override
	public void run(String... args) throws Exception {

		for (String key : bindingServiceProperties.getBindings().keySet()) {
			String destination = bindingServiceProperties.getBindingDestination(key);

			SubscribableChannel channel = (SubscribableChannel) bindingTargetFactory.createInput(destination);
			beanFactory.registerSingleton(destination, channel);
			channel = (SubscribableChannel) beanFactory.initializeBean(channel, destination);
			channel.subscribe(new MessageHandler() {
				@Override
				public void handleMessage(Message<?> message) throws MessagingException {
					System.out.println(message.toString());
				}
			});
			bindingService.bindConsumer(channel, destination);
		}

	}

}
