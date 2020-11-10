package org.springblade.energy.operationmaintenance.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springblade.energy.operationmaintenance.vo.ApiTaskCountVo;
import org.springblade.energy.securitymanagement.util.DataFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
@Api(value = "APP--我的绩效", tags = "APP--我的绩效接口")
public class ApiCountController {

	@ApiOperationSupport(order = 1)
	@ApiOperation("统计")
	@GetMapping("/count")
	public R<ApiTaskCountVo> apiCount(String datetime, Integer type, Long stationId, Long responsibleId) {
		return R.data(new DataFactory<>().apiCount(datetime, type, stationId, responsibleId));
	}
}
