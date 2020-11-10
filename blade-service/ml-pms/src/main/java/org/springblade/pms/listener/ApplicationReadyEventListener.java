package org.springblade.pms.listener;

import org.springblade.pms.gw.feign.ITopicClient;
import org.springblade.pms.station.entity.BaseStation;
import org.springblade.pms.station.service.IBaseStationService;
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
	private IBaseStationService iBaseStationService;


	@Override public void onApplicationEvent(ApplicationReadyEvent
	  applicationReadyEvent) {
		List<BaseStation>  list =iBaseStationService.list();
		List<String> gwids= new ArrayList<>();
		for (BaseStation device:list){
			gwids.add(device.getGwId());
		}

		iTopicClient.addDeviceTopic(gwids);

		//iTopicClient.addTopic("mailian/wemsgwinfo");
	}

}
