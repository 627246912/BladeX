package org.springblade.energy.securitymanagement.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springblade.energy.securitymanagement.entity.Patrol;
import org.springblade.energy.securitymanagement.service.PatrolService;
import org.springblade.energy.securitymanagement.util.PageQuery;
import org.springblade.energy.securitymanagement.util.PageUtils;
import org.springblade.energy.securitymanagement.vo.SecurityCountVo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/security/patrol")
@Api(value = "安全管理--安全检查", tags = "安全管理--安全检查接口")
@AllArgsConstructor
public class PatrolController {

	PatrolService patrolService;

	@ApiOperationSupport(order = 1)
	@ApiOperation("添加")
	@PostMapping("/save")
	public R addPatrol(@RequestBody Patrol patrol) {
		return patrolService.addPatrol(patrol);
	}

	@ApiOperationSupport(order = 2)
	@ApiOperation("列表")
	@GetMapping("/page")
	public R<PageUtils> listPatrol(PageQuery pageQuery, Patrol patrol) {
		Page<Patrol> page = patrolService.page(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new QueryWrapper<>(patrol).orderByDesc("create_time"));
		return R.data(new PageUtils(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
	}

	@ApiOperationSupport(order = 3)
	@ApiOperation("编辑")
	@PutMapping("/update")
	public R<Patrol> updatePatrol(@RequestBody Patrol patrol) {
		return patrolService.updatePatrol(patrol);
	}

	@ApiOperationSupport(order = 4)
	@ApiOperation("批量删除")
	@PostMapping("/remove")
	public R batchDeletedPatrol(@RequestBody List<Patrol> patrols) {
		patrolService.removePatrol(patrols);
		return R.status(true);
	}

	@ApiOperationSupport(order = 5)
	@ApiOperation("统计分析")
	@GetMapping("/count")
	public R<SecurityCountVo> countPatrol(String stationId, String siteId, String time, Integer type) {
		return patrolService.countPatrol(stationId, siteId, time, type);
	}

}
