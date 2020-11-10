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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.deptcost.entity.TurnUnitData;
import org.springblade.energy.deptcost.service.ITurnUnitDataService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 单位电量转供月份数据 控制器
 *
 * @author bond
 * @since 2020-08-07
 */
@RestController
@AllArgsConstructor
@RequestMapping("/turnunitdata")
@Api(value = "能源管理-费用管理", tags = "能源管理-费用管理")
public class TurnUnitDataController extends BladeController {

	private ITurnUnitDataService turnUnitDataService;

	/**
	 * 修改 单位电量转供月份数据
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 25)
	@ApiOperation(value = "单位电量转供月份数据-修改", notes = "传入turnUnitData")
	public R update(@Valid @RequestBody TurnUnitData turnUnitData) {

		TurnUnitData entiry=new TurnUnitData();
		entiry.setMonthTime(turnUnitData.getMonthTime());
		entiry.setUnitId(turnUnitData.getUnitId());
		TurnUnitData detail = turnUnitDataService.getOne(Condition.getQueryWrapper(entiry));
		if(Func.isNotEmpty(detail)){
			return R.data(turnUnitDataService.update(turnUnitData,
				new QueryWrapper<TurnUnitData>().eq("unit_id", turnUnitData.getUnitId()).eq("month_time",turnUnitData.getMonthTime())));
		}else{
			turnUnitData.setId(null);
			return R.status(turnUnitDataService.save(turnUnitData));
		}

	}



}
