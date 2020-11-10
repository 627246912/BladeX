package org.springblade.gw.service;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * @author ：bond
 * @date ：Created in 2020/3/10 9:50
 * @description：MQTT订阅发送类
 * @modified By：
 * @version: 1.0.0$
 */
@Component
@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface MqttGateway {
	void sendToMqtt(String data);
	void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, String data);
	void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, @Header(MqttHeaders.QOS) int qos, String data);
}

