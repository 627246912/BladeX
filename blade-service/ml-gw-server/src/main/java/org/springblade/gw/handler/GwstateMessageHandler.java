package org.springblade.gw.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springblade.bean.DeviceCommunicationStatus;
import org.springblade.constants.GwsubscribeConstant;
import org.springblade.enums.PushMessageTopic;
import org.springblade.enums.Status;
import org.springblade.gw.adapter.MqttMessageHandlerAdapter;
import org.springblade.gw.cache.DeviceCache;
import org.springblade.gw.util.SpringContext;
import org.springblade.util.JsonUtils;
import org.springblade.util.StringUtils;

import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

/**
 * @Auther: bond
 * @Date: 2020/05/08
 * @Description:
 */
public class GwstateMessageHandler implements MqttMessageHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(GwstateMessageHandler.class);

	public GwstateMessageHandler() {
	}
    @Override
    public boolean supports(String topic) {
        return Pattern.matches(PushMessageTopic.GWSTATE.regex,topic);
    }

    @Override
    public int handle(String message) {
        int result = GwsubscribeConstant.HANDLER_RESULT_FAIL;
        try {
            DeviceCommunicationStatus deviceCommunicationStatus = JsonUtils.getObjectFromJsonString(message,DeviceCommunicationStatus.class);
            if(StringUtils.isNotNull(deviceCommunicationStatus)){
            	//log.info("接收到MQTT参数如下：gwid：{}，status：{}，lasttime：{}",deviceCommunicationStatus.getGwid(),deviceCommunicationStatus.getStatus(),deviceCommunicationStatus.getLasttime());
                handlerGwStatus(deviceCommunicationStatus);
            }
            result = GwsubscribeConstant.HANDLER_RESULT_SUCCESS;
        } catch (Exception e) {
          //  log.error("处理网关状态数据失败,message:{}",message,e);
        }
        return result;
    }

    /**
     * 处理状态方法
     * @param deviceCommunicationStatus
     * @return 状态是否变更
     */
    public boolean handlerGwStatus(DeviceCommunicationStatus deviceCommunicationStatus){

		DeviceCache deviceCache = (DeviceCache) SpringContext.getBean("deviceCache");
        ExecutorService executorService = SpringContext.getBean(ExecutorService.class);
        String gwid = deviceCommunicationStatus.getGwid();
        DeviceCommunicationStatus oldStatus = deviceCache.getStatusByCode(gwid);
        deviceCache.updateStatus(deviceCommunicationStatus);

        boolean needHandler = false;
        boolean changeStatus = StringUtils.isNotNull(oldStatus) && oldStatus.getStatus().intValue() !=deviceCommunicationStatus.getStatus().intValue();
        if(StringUtils.isNull(oldStatus) || changeStatus) {
            if (Status.NORMAL.id.intValue() == deviceCommunicationStatus.getStatus().intValue()) {
                needHandler = true;
                /*executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        *//*延时订阅*//*
                        try {
                            Thread.sleep(30000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        MqttTopicUtil.addTopic(deviceCommunicationStatus.getGwid());
                    }
                });*/
            }
            if (Status.DISABLE.id.intValue() == deviceCommunicationStatus.getStatus().intValue()) {
                /*executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        MqttTopicUtil.removeTopic(deviceCommunicationStatus.getGwid(), false);
                    }
                });*/
                needHandler = true;
            }
        }
        return needHandler;
    }
}
