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
package org.springblade.energy.energymanagement.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.energymanagement.entity.EnergyConsumeType;
import org.springblade.energy.energymanagement.service.IEnergyConsumeTypeService;
import org.springblade.energy.energymanagement.vo.EnergyConsumeTypeVO;
import org.springblade.util.NumberUtil;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 *  控制器
 *
 * @author bond
 * @since 2020-07-04
 */
@RestController
@AllArgsConstructor
@RequestMapping("/energyconsumetype")
@Api(value = "能源管理-能耗分析", tags = "能源管理-能耗分析")
public class EnergyConsumeTypeController extends BladeController {

	private IEnergyConsumeTypeService energyConsumeTypeService;


	/**
	 * 自定义分页
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "能耗分类列表", notes = "传入energyConsumeType")
	public R<IPage<EnergyConsumeTypeVO>> page(EnergyConsumeTypeVO energyConsumeType, Query query) {
		IPage<EnergyConsumeTypeVO> pages = energyConsumeTypeService.selectEnergyConsumeTypePage(Condition.getPage(query), energyConsumeType);
		return R.data(pages);
	}


	/**
	 * 新增
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "添加能耗分类", notes = "传入energyConsumeType")
	public R save(@Valid @RequestBody EnergyConsumeType energyConsumeType) {
		EnergyConsumeType have=energyConsumeTypeService.getOne(Condition.getQueryWrapper(energyConsumeType));
		if(Func.isNotEmpty(have)){
			return R.fail("该分类已近存在啦");
		}
		energyConsumeType.setId(NumberUtil.getRandomNum(11));
		return R.status(energyConsumeTypeService.save(energyConsumeType));
	}


}
