package org.springblade.gw.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springblade.bean.Device;
import org.springblade.constants.GwsubscribeConstant;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.gw.adapter.MqttMessageHandlerAdapter;
import org.springblade.gw.config.SystemConfig;
import org.springblade.gw.constant.PushMessageTopic;
import org.springblade.util.JsonUtils;
import org.springblade.util.RedisKeysUtil;
import org.springblade.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @Auther: bond
 * @Date: 2020/05/08
 * @Description:
 */
public class GwinfoMessageHandler implements MqttMessageHandlerAdapter {
	private static final Logger log = LoggerFactory.getLogger(GwinfoMessageHandler.class);

	@Autowired
	private BladeRedisCache redisCache;
	@Autowired
	private SystemConfig systemConfig;

	public GwinfoMessageHandler() {
	}

	@Override
	public boolean supports(String topic) {
		return Pattern.matches(PushMessageTopic.GWINFO.regex, topic);
	}


	@Override
	public int handle(String message) {
		int result = GwsubscribeConstant.HANDLER_RESULT_FAIL;
		try {
			Device device = JsonUtils.getObjectFromJsonString(message, Device.class);

			if (StringUtils.isNull(device)) {
				addDevice(device);
			}
			result = GwsubscribeConstant.HANDLER_RESULT_SUCCESS;
		} catch (Exception e) {
			log.error("处理网关数据失败,message:{}", message, e);
		}
		return result;
	}

	public Map<String, Device> addDevice(Device device) {
		Map<String, Device> codeDeviceMap = new HashMap<>(1);
		codeDeviceMap.put(device.getCode(), device);
		String key= RedisKeysUtil.getKey(systemConfig.getServerIdCard(),GwsubscribeConstant.DEVICE_CODE_TO_DEVICE);
		Map<Object,Object> map =new HashMap<>(codeDeviceMap);
		redisCache.hMset(key,map);
		return codeDeviceMap;
	}
}
