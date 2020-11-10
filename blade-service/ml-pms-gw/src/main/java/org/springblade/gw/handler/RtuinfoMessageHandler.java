package org.springblade.gw.handler;

import com.alibaba.fastjson.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springblade.bean.DeviceSub;
import org.springblade.constants.GwsubscribeConstant;
import org.springblade.core.tool.utils.Func;
import org.springblade.enums.PushMessageTopic;
import org.springblade.gw.adapter.MqttMessageHandlerAdapter;
import org.springblade.gw.cache.DeviceSubCache;
import org.springblade.gw.repository.DeviceItemDataRepository;
import org.springblade.gw.util.SpringContext;
import org.springblade.util.JsonUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @Auther: bond
 * @Date: 2020/05/08
 * @Description:
 */
public class RtuinfoMessageHandler implements MqttMessageHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(RtuinfoMessageHandler.class);

	public RtuinfoMessageHandler(){

	}

    @Override
    public boolean supports(String topic) {
        return Pattern.matches(PushMessageTopic.RTUINFO.regex,topic);
    }

    @Override
    public int handle(String message) {
        int result = GwsubscribeConstant.HANDLER_RESULT_FAIL;
        try {
            List<DeviceSub> deviceSubList = JsonUtils.getObjectFromJsonString(message,new TypeReference<List<DeviceSub>>(){});

            if(Func.isNotEmpty(deviceSubList)){
                handlerRtuInfos(deviceSubList);
            }
            result = GwsubscribeConstant.HANDLER_RESULT_SUCCESS;
        } catch (Exception e) {
            log.error("处理RTU信息失败,message:{}",message,e);
        }
        return result;
    }

    /**
     * 处理rtu信息
     * @param deviceSubList
     */
    public void handlerRtuInfos(List<DeviceSub> deviceSubList){
		DeviceSubCache deviceSubCache = (DeviceSubCache) SpringContext.getBean("deviceSubCache");
		deviceSubCache.updateDeviceSubs(deviceSubList);

		Set<String> gwIdSet = new HashSet<>();
		for (DeviceSub deviceSub : deviceSubList) {
			gwIdSet.add(deviceSub.getGwid());
		}
		DeviceItemDataRepository deviceItemRepository = SpringContext.getBean(DeviceItemDataRepository.class);
		deviceItemRepository.asyncUpdateGwBtypeMapByGwIds(new ArrayList<>(gwIdSet));
	};
}
