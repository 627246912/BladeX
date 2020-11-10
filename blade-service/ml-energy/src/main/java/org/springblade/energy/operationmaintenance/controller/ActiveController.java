package org.springblade.energy.operationmaintenance.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springblade.energy.operationmaintenance.service.ActiveService;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@AllArgsConstructor
@RequestMapping("/api/active")
@Api(value = "APP--用户活跃", tags = "APP--用户活跃接口")
public class ActiveController {

	ActiveService activeService;

	@ApiOperationSupport(order = 2)
	@ApiOperation("编辑")
	@PutMapping("/update")
	public R updateActive(@RequestParam Integer isActive, @RequestParam Date time, @RequestParam Integer noticeType) {
		return activeService.updateActive(isActive, time, noticeType);
	}
}
