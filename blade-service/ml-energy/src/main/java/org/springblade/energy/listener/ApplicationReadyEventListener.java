package org.springblade.energy.listener;

import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.energy.bigscreen.handler.BigScreenRedisComTrue;
import org.springblade.energy.device.entity.DeviceRelation;
import org.springblade.energy.device.service.IDeviceRelationService;
import org.springblade.gw.feign.ITopicClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
	private IDeviceRelationService iDeviceRelationService;
	@Autowired
	private BigScreenRedisComTrue bigScreenRedisComTrue;

	@Override public void onApplicationEvent(ApplicationReadyEvent
	  applicationReadyEvent) {
		String tenantId= AuthUtil.getTenantId();
		List<DeviceRelation>  list =iDeviceRelationService.list();
		List<String> gwids= new ArrayList<>();
		for (DeviceRelation device:list){
			gwids.add(device.getDid());
		}
		iTopicClient.addDeviceTopic(gwids);
		//初始化大屏端数据
		bigScreenRedisComTrue.BigScreenDataJob();
	  }

}
