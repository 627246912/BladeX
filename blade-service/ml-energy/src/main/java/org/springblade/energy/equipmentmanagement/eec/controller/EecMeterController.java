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
package org.springblade.energy.equipmentmanagement.eec.controller;

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
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.diagram.service.IDiagramProductService;
import org.springblade.energy.equipmentmanagement.eec.entity.EecMeter;
import org.springblade.energy.equipmentmanagement.eec.service.IEecMeterService;
import org.springblade.energy.equipmentmanagement.eec.vo.EVO;
import org.springblade.energy.equipmentmanagement.eec.vo.EecMeterVO;
import org.springblade.energy.equipmentmanagement.eec.wrapper.EecMeterWrapper;
import org.springblade.enums.ProductDtype;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 重点能耗设备-》仪表配置 控制器
 *
 * @author bond
 * @since 2020-05-06
 */
@RestController
@AllArgsConstructor
@RequestMapping("/eecmeter")
@Api(value = "重点能耗设备-配置管理", tags = "重点能耗设备-配置管理")
public class EecMeterController extends BladeController {

	private IEecMeterService eecMeterService;
	@Autowired
	private BladeRedisCache redisCache;
	private IDiagramProductService diagramProductService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入eecMeter")
	public R<EecMeterVO> detail(EecMeter eecMeter) {
		EecMeter detail = eecMeterService.getOne(Condition.getQueryWrapper(eecMeter));
		return R.data(EecMeterWrapper.build().entityVO(detail));
	}

	/**
	 * 自定义分页 重点能耗设备-》仪表配置
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入eecMeter")
	public R<IPage<EecMeterVO>> page(EecMeterVO eecMeter, Query query) {
		IPage<EecMeterVO> pages = eecMeterService.selectEecMeterPage(Condition.getPage(query), eecMeter);
		return R.data(pages);
	}

	/**
	 * 新增 重点能耗设备-》仪表配置
	 */
	@ApiLog("新增 重点能耗设备-仪表配置")
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入eecMeter")
	public R save(@Valid @RequestBody EecMeter eecMeter) {
		boolean r = checkProduct(eecMeter).isSuccess();
		if (r) {
			r = eecMeterService.save(eecMeter);
		}
		return R.status(r);
	}

	/**
	 * 修改 重点能耗设备-》仪表配置
	 */
	@ApiLog("修改 重点能耗设备-仪表配置")
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入eecMeter")
	public R update(@Valid @RequestBody EecMeter eecMeter) {
		boolean r = checkProduct(eecMeter).isSuccess();
		if (r) {
			r = eecMeterService.updateById(eecMeter);
		}
		return R.status(r);
	}

	/**
	 * 删除 重点能耗设备-》仪表配置
	 */
	@ApiLog("删除 重点能耗设备-仪表配置")
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(eecMeterService.deleteLogic(Func.toLongList(ids)));
	}

	public R checkProduct(EecMeter eecMeter) {
		EecMeter airc = new EecMeter();
		airc.setSiteId(eecMeter.getSiteId());
		airc.setStationId(eecMeter.getStationId());
		airc.setIsDeleted(0);
		Integer i = eecMeterService.count(Condition.getQueryWrapper(airc));
		if (i > 0) {
			return R.fail("该位置已近有该仪表了");
		}


		EecMeter eecMeter1 = new EecMeter();
		eecMeter1.setEquNo(eecMeter.getEquNo());
		eecMeter1.setIsDeleted(0);
		Integer j = eecMeterService.count(Condition.getQueryWrapper(eecMeter1));
		if (j < 0) {
			return R.fail("该设备已经存在了");
		}
		return R.success("验证成功");
	}

	@ApiOperationSupport(order = 8)
	@ApiOperation("能耗设备")
	@GetMapping("/dev")
	public R<List<EVO>> listEVO(Long sid) {
		return R.data(eecMeterService.selectEVO(sid));
	}

}
