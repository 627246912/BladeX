/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package org.springblade.energy.deptcost.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.deptcost.entity.TurnUnit;
import org.springblade.energy.deptcost.service.ITurnUnitService;
import org.springblade.energy.deptcost.vo.TurnUnitVO;
import org.springblade.energy.deptcost.wrapper.TurnUnitWrapper;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 *  控制器
 *
 * @author bond
 * @since 2020-08-07
 */
@RestController
@AllArgsConstructor
@RequestMapping("/turnunit")
@Api(value = "能源管理-费用管理", tags = "能源管理-费用管理")
public class TurnUnitController extends BladeController {

	private ITurnUnitService turnUnitService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 12)
	@ApiOperation(value = "转供单位-详情", notes = "传入turnUnit")
	public R<TurnUnitVO> detail(TurnUnit turnUnit) {
		TurnUnit detail = turnUnitService.getOne(Condition.getQueryWrapper(turnUnit));
		return R.data(TurnUnitWrapper.build().entityVO(detail));
	}


	/**
	 * 自定义分页
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 13)
	@ApiOperation(value = "转供单位-分页", notes = "传入turnUnit")
	public R<IPage<TurnUnitVO>> page(TurnUnitVO turnUnit, Query query) {
		IPage<TurnUnitVO> pages = turnUnitService.selectTurnUnitPage(Condition.getPage(query), turnUnit);
		return R.data(pages);
	}

	/**
	 * 新增
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 14)
	@ApiOperation(value = "转供单位-新增", notes = "传入turnUnit")
	public R save(@Valid @RequestBody TurnUnit turnUnit) {
		return R.status(turnUnitService.save(turnUnit));
	}

	/**
	 * 修改
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 15)
	@ApiOperation(value = "转供单位-修改", notes = "传入turnUnit")
	public R update(@Valid @RequestBody TurnUnit turnUnit) {
		return R.status(turnUnitService.updateById(turnUnit));
	}

	/**
	 * 新增或修改
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 16)
	@ApiOperation(value = "转供单位-新增或修改", notes = "传入turnUnit")
	public R submit(@Valid @RequestBody TurnUnit turnUnit) {
		return R.status(turnUnitService.saveOrUpdate(turnUnit));
	}


	/**
	 * 删除
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 17)
	@ApiOperation(value = "转供单位-逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(turnUnitService.deleteLogic(Func.toLongList(ids)));
	}


}
