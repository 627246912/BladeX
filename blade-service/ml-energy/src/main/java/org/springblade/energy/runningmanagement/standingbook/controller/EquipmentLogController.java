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
package org.springblade.energy.runningmanagement.standingbook.controller;

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
import org.springblade.energy.runningmanagement.standingbook.entity.EquipmentLog;
import org.springblade.energy.runningmanagement.standingbook.service.IEquipmentLogService;
import org.springblade.energy.runningmanagement.standingbook.vo.EquipmentLogVO;
import org.springblade.energy.runningmanagement.standingbook.wrapper.EquipmentLogWrapper;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 设备运行记录表 控制器
 *
 * @author bond
 * @since 2020-04-09
 */
@RestController
@AllArgsConstructor
@RequestMapping("/equipmentlog")
@Api(value = "设备运行记录表", tags = "设备运行记录表接口")
public class EquipmentLogController extends BladeController {

	private IEquipmentLogService equipmentLogService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入equipmentLog")
	public R<EquipmentLogVO> detail(EquipmentLog equipmentLog) {
		EquipmentLog detail = equipmentLogService.getOne(Condition.getQueryWrapper(equipmentLog));
		return R.data(EquipmentLogWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 设备运行记录表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入equipmentLog")
	public R<IPage<EquipmentLogVO>> list(EquipmentLog equipmentLog, Query query) {
		IPage<EquipmentLog> pages = equipmentLogService.page(Condition.getPage(query), Condition.getQueryWrapper(equipmentLog));
		return R.data(EquipmentLogWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 设备运行记录表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入equipmentLog")
	public R<IPage<EquipmentLogVO>> page(EquipmentLogVO equipmentLog, Query query) {
		IPage<EquipmentLogVO> pages = equipmentLogService.selectEquipmentLogPage(Condition.getPage(query), equipmentLog);
		return R.data(pages);
	}

	/**
	 * 新增 设备运行记录表
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入equipmentLog")
	public R save(@Valid @RequestBody EquipmentLog equipmentLog) {
		return R.status(equipmentLogService.save(equipmentLog));
	}

	/**
	 * 修改 设备运行记录表
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入equipmentLog")
	public R update(@Valid @RequestBody EquipmentLog equipmentLog) {
		return R.status(equipmentLogService.updateById(equipmentLog));
	}

	/**
	 * 新增或修改 设备运行记录表
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入equipmentLog")
	public R submit(@Valid @RequestBody EquipmentLog equipmentLog) {
		return R.status(equipmentLogService.saveOrUpdate(equipmentLog));
	}


	/**
	 * 删除 设备运行记录表
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(equipmentLogService.deleteLogic(Func.toLongList(ids)));
	}


}
