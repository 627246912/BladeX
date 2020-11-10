package org.springblade.energy.bigscreen.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.energy.bigscreen.handler.BigScreenRedisComTrue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author bond
 * @date 2020/8/3 10:12
 * @desc
 */
@RestController
@AllArgsConstructor
@RequestMapping("/bigScreen")
@Api(value = "大屏端", tags = "大屏端接口")
public class BigScreenController {
	@Autowired
	private BladeRedisCache redisCache;
	@Autowired
	private BigScreenRedisComTrue bigScreenRedisComTrue;



	/**
	 * 根据站点ID查询大屏端数据
	 */
//	@GetMapping("/getData")
//	@ApiOperationSupport(order = 1)
//	@ApiOperation(value = "根据站点ID查询大屏端数据", notes = "传入id")
//	public R<Map<String,Object>> getData(@ApiParam(value = "stationId", required = true) @RequestParam Long stationId)
//	{
//		Map<String,Object> resMap=new HashMap<>();
//		return R.data(resMap);
//	}


	@GetMapping("/BigScreenDataJob")
	@ApiOperation(value = "BigScreenDataJob", notes = "")
	public void BigScreenDataJob() {
		bigScreenRedisComTrue.BigScreenDataJob();
	}
}
