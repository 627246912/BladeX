package org.springblade.energy.operationmaintenance.schedule;

import org.springblade.energy.operationmaintenance.service.NewsPushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NewsPushSchedule {

	@Autowired
	NewsPushService newsPushService;

	@Scheduled(cron = "0 0/1 * * * *")
	public void push() {
		newsPushService.push();
	}
}
