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
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.runningmanagement.standingbook.entity.EquipmentCabinet;
import org.springblade.energy.runningmanagement.standingbook.service.IEquipmentCabinetService;
import org.springblade.energy.runningmanagement.standingbook.vo.EquipmentCabinetVO;
import org.springblade.energy.runningmanagement.standingbook.wrapper.EquipmentCabinetWrapper;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 台账--柜体 控制器
 *
 * @author bond
 * @since 2020-04-03
 */
@RestController
@AllArgsConstructor
@RequestMapping("/equipmentcabinet")
@Api(value = "台账--柜体", tags = "台账--柜体接口")
public class EquipmentCabinetController extends BladeController {

	private IEquipmentCabinetService equipmentCabinetService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入equipmentCabinet")
	public R<EquipmentCabinetVO> detail(EquipmentCabinet equipmentCabinet) {
		EquipmentCabinet detail = equipmentCabinetService.getOne(Condition.getQueryWrapper(equipmentCabinet));
		return R.data(EquipmentCabinetWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 台账--柜体
	 */
//	@GetMapping("/list")
//	@ApiOperationSupport(order = 2)
//	@ApiOperation(value = "分页", notes = "传入equipmentCabinet")
//	public R<IPage<EquipmentCabinetVO>> list(EquipmentCabinet equipmentCabinet, Query query) {
//		IPage<EquipmentCabinet> pages = equipmentCabinetService.page(Condition.getPage(query), Condition.getQueryWrapper(equipmentCabinet));
//		return R.data(EquipmentCabinetWrapper.build().pageVO(pages));
//	}


	/**
	 * 自定义分页 台账--柜体
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入equipmentCabinet")
	public R<IPage<EquipmentCabinetVO>> page(EquipmentCabinetVO equipmentCabinet, Query query) {
		IPage<EquipmentCabinetVO> pages = equipmentCabinetService.selectEquipmentCabinetPage(Condition.getPage(query), equipmentCabinet);
		return R.data(pages);
	}

	/**
	 * 新增 台账--柜体
	 */
	@ApiLog("新增 台账--柜体")
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入equipmentCabinet")
	public R save(@Valid @RequestBody EquipmentCabinet equipmentCabinet) {
		return R.status(equipmentCabinetService.save(equipmentCabinet));
	}

	/**
	 * 修改 台账--柜体
	 */
	@ApiLog("修改 台账--柜体")
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入equipmentCabinet")
	public R update(@Valid @RequestBody EquipmentCabinet equipmentCabinet) {
		return R.status(equipmentCabinetService.updateById(equipmentCabinet));
	}

	/**
	 * 新增或修改 台账--柜体
	 */
	@ApiLog("新增或修改 台账--柜体")
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入equipmentCabinet")
	public R submit(@Valid @RequestBody EquipmentCabinet equipmentCabinet) {
		return R.status(equipmentCabinetService.saveOrUpdate(equipmentCabinet));
	}


	/**
	 * 删除 台账--柜体
	 */
	@ApiLog("删除 台账--柜体")
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(equipmentCabinetService.deleteLogic(Func.toLongList(ids)));
	}


}
