package org.springblade.energy.operationmaintenance.schedule;

import org.springblade.energy.operationmaintenance.service.ActiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ActiveUserSchedule {

	@Autowired
	ActiveService activeService;

	@Scheduled(fixedRate = 3600000 * 24, initialDelay = 20000)
	public void push() {
		activeService.push();
	}
}
