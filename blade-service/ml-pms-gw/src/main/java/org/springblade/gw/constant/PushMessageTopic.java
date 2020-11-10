package org.springblade.gw.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: wangqiaoqing
 * @Date: 2018/8/29
 * @Description:
 */
public enum PushMessageTopic {
    GWINFO("/push/v1/%s/gwinfo","/push/v1/\\S*/gwinfo"),//网关信息
    RTUINFO("/push/v1/%s/rtuinfo","/push/v1/\\S*/rtuinfo"),
    DATAITEM("/push/v1/%s/dataitem","/push/v1/\\S*/dataitem"),
    ALARM("/push/v1/%s/alarm","/push/v1/\\S*/alarm"),//报警信息
    GWSTATE("/push/v1/%s/gwstate","/push/v1/\\S*/gwstate"),//网关状态10秒一次
	SIGNAL("/push/v1/%s/signal","/push/v1/\\S*/signal");//数据项实时数据

	public String topic;
    public String regex;

    PushMessageTopic(String topic, String regex) {
        this.topic = topic;
        this.regex = regex;
    }

    /**
     * 根据网关id获取可以订阅的主体列表
     * @param deviceId
     * @return
     */
    public static List<String> getTopicsByDeviceId(String deviceId){
    	PushMessageTopic [] pushMessageTopics = PushMessageTopic.values();
        List<String> topics = new ArrayList<>();
        for (PushMessageTopic pushMessageTopic : pushMessageTopics) {
            topics.add(String.format(pushMessageTopic.topic,deviceId));

        }
      //目前只订阅实时数据
//    	List<String> topics = new ArrayList<>();
//    	topics.add(String.format(PushMessageTopic.SIGNAL.topic,deviceId));
        return topics;
    }
}
