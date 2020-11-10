package org.springblade.energy.operationmaintenance.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springblade.energy.operationmaintenance.entity.NewsPush;
import org.springblade.energy.operationmaintenance.service.NewsPushService;
import org.springblade.energy.securitymanagement.util.PageQuery;
import org.springblade.energy.securitymanagement.util.PageUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/newPush")
@Api(value = "任务总控", tags = "任务总控接口")
@AllArgsConstructor
public class NewPushController {

	NewsPushService newsPushService;

	@GetMapping("/page")
	@ApiOperationSupport(order = 1)
	@ApiOperation("列表")
	public R<PageUtils> pageNewPush(PageQuery pageQuery, NewsPush newsPush) {
		Page<NewsPush> page = newsPushService.page(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new QueryWrapper<>(newsPush).orderByDesc("create_time"));
		return R.data(new PageUtils(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
	}
}
