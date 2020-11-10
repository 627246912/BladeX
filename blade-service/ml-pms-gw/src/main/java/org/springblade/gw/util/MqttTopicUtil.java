package org.springblade.gw.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springblade.enums.PushMessageTopic;
import org.springblade.util.StringUtils;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;

import java.util.Arrays;
import java.util.List;

/**
 * @Auther: bond
 * @Date: 2020/03/10
 * @Description: MQTT 订阅主题工具类
 */
public class MqttTopicUtil {
    private static final Logger log = LoggerFactory.getLogger(MqttTopicUtil.class);

    /**
     * 添加订阅主题
     * @param topicName
     */
    public static void addTopic(String topicName){
        if(StringUtils.isEmpty(topicName)){
            return;
        }
        List<String> topicList = PushMessageTopic.getTopicsByDeviceId(topicName);
        MqttPahoMessageDrivenChannelAdapter adapter = (MqttPahoMessageDrivenChannelAdapter) SpringContext.getBean("mqttInbound");

        List<String> existsTopic = Arrays.asList(adapter.getTopic());
        for (String topic : topicList) {
            if(!existsTopic.contains(topic)) {
                try {
                    if(topic.contains("signal")){
                        adapter.addTopic(topic,0);
                    }else {
                        adapter.addTopic(topic);
                    }
                }catch (Exception e){
                }
            }
        }

        if(!adapter.isRunning() && StringUtils.isNotEmpty(adapter.getTopic())){
            adapter.start();
        }
    }

    /**
     * 添加默认订阅主题
     * @param topicName
     */
    public static void addDefaultTopic(String topicName){
        if(StringUtils.isEmpty(topicName)){
            return;
        }
        String defaultTopic = String.format(String.format(PushMessageTopic.GWSTATE.topic,topicName));
        MqttPahoMessageDrivenChannelAdapter adapter = (MqttPahoMessageDrivenChannelAdapter) SpringContext.getBean("mqttInbound");

        List<String> existsTopic = Arrays.asList(adapter.getTopic());
        if(!existsTopic.contains(defaultTopic)) {
            adapter.addTopic(defaultTopic);
        }

        if(!adapter.isRunning() && StringUtils.isNotEmpty(adapter.getTopic())){
            adapter.start();
        }
    }

    /**
     * 更新订阅主题
     * @param oldTopicName
     * @param newTopicName
     */
    public static void updateTopic(String oldTopicName,String newTopicName){
        removeTopic(oldTopicName,true);
        addTopic(newTopicName);
    }

    /**
     * 移除订阅主题
     * @param topicName
     * @param removeAll 移除所有
     */
    public static void removeTopic(String topicName,boolean removeAll){
        if(StringUtils.isEmpty(topicName)){
            return;
        }

        List<String> topicList = PushMessageTopic.getTopicsByDeviceId(topicName);
        MqttPahoMessageDrivenChannelAdapter adapter = (MqttPahoMessageDrivenChannelAdapter) SpringContext.getBean("mqttInbound");
        List<String> existsTopic = Arrays.asList(adapter.getTopic());

        String defaultTopic = String.format(PushMessageTopic.GWSTATE.topic,topicName);
        for (String topic : topicList) {
            if(!existsTopic.contains(topic)){
                continue;
            }
            if(removeAll || !defaultTopic.equals(topic)){
                try {
                    adapter.removeTopic(topic);
                }catch (Exception e){

                }
            }
        }

        if(adapter.isRunning() && StringUtils.isEmpty(adapter.getTopic())){
            adapter.stop();
        }
    }

    /**
     * 获取已订阅的主题
     * @return
     */
    public List<String> getTopics(){
        MqttPahoMessageDrivenChannelAdapter adapter = (MqttPahoMessageDrivenChannelAdapter) SpringContext.getBean("mqttInbound");
        return Arrays.asList(adapter.getTopic());
    }

	public static void addMyTopic(String topic){
		if(StringUtils.isEmpty(topic)){
			return;
		}
		MqttPahoMessageDrivenChannelAdapter adapter = (MqttPahoMessageDrivenChannelAdapter) SpringContext.getBean("mqttInbound");
		List<String> existsTopic = Arrays.asList(adapter.getTopic());

		if(!existsTopic.contains(topic)) {
			adapter.addTopic(topic);
		}
		if(!adapter.isRunning() && StringUtils.isNotEmpty(adapter.getTopic())){
			adapter.start();
		}
	}
}
