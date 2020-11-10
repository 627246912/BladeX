package org.springblade.gw.handler;

import com.alibaba.fastjson.TypeReference;
import org.springblade.bean.DeviceItem;
import org.springblade.constants.GwsubscribeConstant;
import org.springblade.gw.adapter.MqttMessageHandlerAdapter;
import org.springblade.gw.constant.PushMessageTopic;
import org.springblade.gw.repository.DeviceItemDataRepository;
import org.springblade.gw.util.SpringContext;
import org.springblade.util.JsonUtils;
import org.springblade.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * @Auther: bond
 * @Date: 2020/03/10
 * @Description:urt下的数据项 数据处理类
 */
public class DataitemMessageHandler implements MqttMessageHandlerAdapter {


	public DataitemMessageHandler() {

	}

	@Override
    public boolean supports(String topic) {
        return Pattern.matches(PushMessageTopic.DATAITEM.regex,topic);
    }

	@Override
    public int handle(String message) {
		int result = GwsubscribeConstant.HANDLER_RESULT_FAIL;
		try {
			Map<String,List<DeviceItem>> deviceItemMap = JsonUtils.getObjectFromJsonString(message,new TypeReference<Map<String,List<DeviceItem>>>(){});

			if(StringUtils.isNotEmpty(deviceItemMap)){
				handlerDeviceItem(deviceItemMap);
			}
			result = GwsubscribeConstant.HANDLER_RESULT_SUCCESS;
		} catch (Exception e) {
			System.out.println("处理数据项信息失败:"+message+e);
		}
		return result;
    }

    /**
     * 处理数据
     * @param
     */
	public void handlerDeviceItem(Map<String, List<DeviceItem>> deviceItemMap) {
		DeviceItemDataRepository deviceItemDataRepository = (DeviceItemDataRepository) SpringContext.getBean("deviceItemDataRepository");
		deviceItemDataRepository.updateDeviceItemInfosOfSub(deviceItemMap);
	}
}
