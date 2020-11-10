package org.springblade.energy.bigscreen.schedule;

import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.energy.bigscreen.handler.BigScreenRedisComTrue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BigScreenDataJob {

	@Autowired
	private BladeRedisCache redisCache;
	@Autowired
	private BigScreenRedisComTrue bigScreenRedisComTrue;

	/**
	 * 项目启动延迟两分钟在执行
	 * 每15分钟执行一次，跑大屏端数据到redis里
	 */
	@Scheduled(initialDelay=1000*60*3,fixedDelay=1000*60*15)
	public void run() {
		bigScreenRedisComTrue.BigScreenDataJob();
		}

}
