package org.springblade.gw.handler;

import com.alibaba.fastjson.TypeReference;
import org.springblade.bean.DeviceItemRealTimeData;
import org.springblade.constants.GwsubscribeConstant;
import org.springblade.gw.adapter.MqttMessageHandlerAdapter;
import org.springblade.gw.constant.PushMessageTopic;
import org.springblade.gw.repository.DeviceItemDataRepository;
import org.springblade.util.BigDecimalUtil;
import org.springblade.util.JsonUtils;
import org.springblade.gw.util.SpringContext;
import org.springblade.util.StringUtils;

import java.util.List;
import java.util.regex.Pattern;


/**
 * @Auther: bond
 * @Date: 2020/03/10
 * @Description:网关实时数据处理类
 */
public class SignalMessageHandler implements MqttMessageHandlerAdapter {
    @Override
    public boolean supports(String topic) {
        return Pattern.matches(PushMessageTopic.SIGNAL.regex,topic);
    }

	@Override
    public int handle(String message) {
        int result = GwsubscribeConstant.HANDLER_RESULT_FAIL;
        try {
            List<DeviceItemRealTimeData> deviceItemRealTimeDataList = JsonUtils.getObjectFromJsonString(message,new TypeReference<List<DeviceItemRealTimeData>>(){});
            if(StringUtils.isNotEmpty(deviceItemRealTimeDataList)){
                handlerItemRealTimeDatas(deviceItemRealTimeDataList);
            }
            result = GwsubscribeConstant.HANDLER_RESULT_SUCCESS;
        } catch (Exception e) {

        }
        return result;
    }

    /**
     * 处理数据项实时数据
     * @param deviceItemRealTimeDataList
     */
    public void handlerItemRealTimeDatas(List<DeviceItemRealTimeData> deviceItemRealTimeDataList){
        for (DeviceItemRealTimeData deviceItemRealTimeData : deviceItemRealTimeDataList) {
            deviceItemRealTimeData.setVal(BigDecimalUtil.round(deviceItemRealTimeData.getVal(),GwsubscribeConstant.DEFAULT_KEEP_SCALE));
        }
		DeviceItemDataRepository deviceItemDataRepository = (DeviceItemDataRepository) SpringContext.getBean("deviceItemDataRepository");

		deviceItemDataRepository.updateRealTime(deviceItemRealTimeDataList);

    }
}
