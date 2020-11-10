package org.springblade.gw.testController;


import org.springblade.core.tool.api.R;
import org.springblade.gw.service.MqttGateway;
import org.springblade.gw.util.MqttTopicUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：bond
 * @date ：Created in 2020/3/10 10:08
 * @description：MQTT操作类
 * @modified By：
 * @version: 1.0.0$
 */
@RestController
@RequestMapping("/mqtt")
public class MqttController {

	@Autowired
	private MqttGateway mqttGateway;

	@RequestMapping("/addTopic")
	public R<String> addTopic(String  topic){
		MqttTopicUtil.addTopic(topic);
		return R.data("OK");
	}


	@RequestMapping("/addMyTopic")
	public R<String> addMyTopic(String  topic){
		MqttTopicUtil.addMyTopic(topic);
		return R.data("OK");
	}

	@RequestMapping("/sendMqtt")
	public R<String> sendMqtt(String  sendData,String topic){
		mqttGateway.sendToMqtt(topic,sendData);
		return R.data("OK");
	}

}
