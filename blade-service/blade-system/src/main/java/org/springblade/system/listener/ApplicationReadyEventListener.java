package org.springblade.system.listener;

import org.springblade.common.cache.CacheNames;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.system.entity.Dept;
import org.springblade.system.service.IDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Auther: bond
 * @Date: 2020/4/22
 * @Description:
 */
@Component
public class ApplicationReadyEventListener implements ApplicationListener<ApplicationReadyEvent> {

	@Autowired
	private IDeptService iDeptService;

	@Autowired
	private BladeRedisCache redisCache;

	@Override public void onApplicationEvent(ApplicationReadyEvent
	  applicationReadyEvent) {
		List<Dept>  list =iDeptService.list();

		for (Dept dept:list){
			redisCache.hSet(CacheNames.DEPT_KEY,dept.getId(),dept);
		}

	  }

}
