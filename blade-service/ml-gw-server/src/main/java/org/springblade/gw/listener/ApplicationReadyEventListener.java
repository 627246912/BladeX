package org.springblade.gw.listener;

import org.springblade.bean.Device;
import org.springblade.constants.GwsubscribeConstant;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.utils.Func;
import org.springblade.gw.config.SystemConfig;
import org.springblade.gw.feign.ITopicClient;
import org.springblade.util.RedisKeysUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: bond
 * @Date: 2020/4/22
 * @Description:
 */
@Component
public class ApplicationReadyEventListener implements ApplicationListener<ApplicationReadyEvent> {

	@Autowired
	private ITopicClient iTopicClient;
	@Autowired
	private BladeRedisCache redisCache;
	@Autowired
	private SystemConfig systemConfig;

	@Override public void onApplicationEvent(ApplicationReadyEvent
	  applicationReadyEvent) {

		Map<String, Device> codeDeviceMap = new HashMap<>(1);
		String key= RedisKeysUtil.getKey(systemConfig.getServerIdCard(), GwsubscribeConstant.DEVICE_CODE_TO_DEVICE);
		codeDeviceMap=redisCache.hGetAll(key);

		if(Func.isNotEmpty(codeDeviceMap)){
			iTopicClient.addDeviceTopic(new ArrayList<>(codeDeviceMap.keySet()));
		}


	}

}
