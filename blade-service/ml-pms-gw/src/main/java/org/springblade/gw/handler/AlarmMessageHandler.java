package org.springblade.gw.handler;

import com.alibaba.fastjson.TypeReference;
import org.springblade.bean.Alarm;
import org.springblade.constants.GwsubscribeConstant;
import org.springblade.gw.adapter.MqttMessageHandlerAdapter;
import org.springblade.gw.constant.PushMessageTopic;
import org.springblade.gw.service.AlarmServiceImpl;
import org.springblade.gw.util.SpringContext;
import org.springblade.util.JsonUtils;
import org.springblade.util.StringUtils;

import java.util.List;
import java.util.regex.Pattern;


/**
 * @Auther: bond
 * @Date: 2020/03/10
 * @Description:告警数据处理类
 */
public class AlarmMessageHandler implements MqttMessageHandlerAdapter {

	public AlarmMessageHandler() {

	}

	@Override
    public boolean supports(String topic) {
        return Pattern.matches(PushMessageTopic.ALARM.regex,topic);
    }

	@Override
    public int handle(String message) {
        int result = GwsubscribeConstant.HANDLER_RESULT_FAIL;
        try {
			//System.out.println("AlarmMessageHandler===================="+message);
			List<Alarm> alarmList = JsonUtils.getObjectFromJsonString(message,new TypeReference<List<Alarm>>(){});
            if(StringUtils.isNotEmpty(alarmList)){
                handlerAlarmDatas(alarmList);
            }
            result = GwsubscribeConstant.HANDLER_RESULT_SUCCESS;
        } catch (Exception e) {

        }
        return result;
    }

    /**
     * 处理数据
     * @param alarmList
     */
    public void handlerAlarmDatas(List<Alarm> alarmList){
		AlarmServiceImpl alarmServiceImpl =(AlarmServiceImpl) SpringContext.getBean("alarmServiceImpl");
		alarmServiceImpl.saveAlarm(alarmList);
	}
}
