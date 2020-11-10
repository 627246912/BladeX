package org.springblade.gw.config;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ZipUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.gw.handler.MessageBaseHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import javax.annotation.Resource;

/**
 * @Auther: bond
 * @Date: 2020/03/09
 * @Description:MQTT工具类
 */
@Configuration
@IntegrationComponentScan
@Slf4j
public class MqttConfig {

	@Value("${mailian.mqtt.username}")
	private String username;

	@Value("${mailian.mqtt.password}")
	private String password;

	@Value("${mailian.mqtt.url}")
	private String hostUrl;

//	@Value("${mailian.mqtt.client.id}")
//	private String clientId;

	@Value("${mailian.mqtt.default.topic}")
	private String defaultTopic;

	@Value("${mailian.mqtt.keepAliveInterval}")
	private Integer keepAliveInterval;

	@Value("${mailian.mqtt.timeOut}")
	private Integer timeOut;

	@Resource
	private MessageBaseHandler messageBaseHandler;

	@Bean
	public MqttConnectOptions getMqttConnectOptions() {

		MqttConnectOptions mqttConnectOptions=new MqttConnectOptions();
		//mqttConnectOptions.setUserName(username);
		//mqttConnectOptions.setPassword(password.toCharArray());

		mqttConnectOptions.setServerURIs(new String[]{hostUrl});
		mqttConnectOptions.setKeepAliveInterval(keepAliveInterval);
		mqttConnectOptions.setConnectionTimeout(timeOut);
		//是否持久订阅  为true是非持久订阅
		mqttConnectOptions.setCleanSession(true);
		mqttConnectOptions.setAutomaticReconnect(true);
		return mqttConnectOptions;
	}

	@Bean
	public MqttPahoClientFactory mqttClientFactory() {
		DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
		factory.setConnectionOptions(getMqttConnectOptions());
		return factory;
	}

	//接收通道
	@Bean
	public MessageChannel mqttInputChannel() {
		return new DirectChannel();
	}


	@Bean
	public MessageProducer mqttInbound(MqttPahoClientFactory mqttPahoClientFactory,MessageChannel mqttInputChannel) {
		String clientId= StringUtil.randomUUID();
		MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(clientId,mqttPahoClientFactory,defaultTopic);
		adapter.setCompletionTimeout(5000);
		DefaultPahoMessageConverter defaultPahoMessageConverter = new DefaultPahoMessageConverter();
		defaultPahoMessageConverter.setPayloadAsBytes(true);
		adapter.setConverter(defaultPahoMessageConverter);
		adapter.setQos(1);
		adapter.setOutputChannel(mqttInputChannel);
		adapter.setAutoStartup(false);
		//默认重连时间10s
		adapter.setRecoveryInterval(10000);
		return adapter;
	}
	//通过通道获取数据
	@Bean
	@ServiceActivator(inputChannel = "mqttInputChannel")
	public MessageHandler handler() {
		return new MessageHandler() {
			@SneakyThrows
			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
				String topic = message.getHeaders().get("mqtt_receivedTopic").toString();
				String type = topic.substring(topic.lastIndexOf("/") + 1, topic.length());
				byte[] data = (byte[]) message.getPayload();

				//System.out.println(topic);
				String messageStr3 = ZipUtil.unGzip(data, CharsetUtil.UTF_8);
				//System.out.println(messageStr3);

					int res = messageBaseHandler.doDispatch(topic, data);

			}
		};
	}

	@Bean
	@ServiceActivator(inputChannel = "mqttOutboundChannel")
	public MessageHandler mqttOutbound() {
		String clientId= StringUtil.randomUUID();
		MqttPahoMessageHandler messageHandler =  new MqttPahoMessageHandler(clientId, mqttClientFactory());
		messageHandler.setAsync(true);
		messageHandler.setDefaultTopic(defaultTopic);
		return messageHandler;
	}
	@Bean
	public MessageChannel mqttOutboundChannel() {
		return new DirectChannel();
	}

}
