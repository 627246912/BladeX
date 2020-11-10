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
package org.springblade.energy.alarmmanagement.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.alarmmanagement.dto.EquipmentAlarmRsep;
import org.springblade.energy.alarmmanagement.dto.FaultAnalysisReq;
import org.springblade.energy.alarmmanagement.dto.FaultAnalysisRsep;
import org.springblade.energy.alarmmanagement.dto.Val;
import org.springblade.energy.alarmmanagement.entity.EquipmentAlarm;
import org.springblade.energy.alarmmanagement.service.IEquipmentAlarmService;
import org.springblade.energy.diagram.service.IDiagramProductService;
import org.springblade.enums.AlarmLevel;
import org.springblade.enums.AlarmType;
import org.springblade.enums.RepairDealStatus;
import org.springblade.util.DateUtil;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备告警表 控制器
 *
 * @author bond
 * @since 2020-04-08
 */
@RestController
@AllArgsConstructor
@RequestMapping("/equipmentalarm")
@Api(value = "设备告警表", tags = "设备告警表接口")
public class EquipmentAlarmController extends BladeController {

	private IEquipmentAlarmService equipmentAlarmService;
	private IDiagramProductService iDiagramProductService;


	/**
	 * 自定义分页 设备告警表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入equipmentAlarm")
	public R<IPage<EquipmentAlarmRsep>> page(FaultAnalysisReq faultAnalysisReq, Query query) {
		IPage<EquipmentAlarmRsep> pages = equipmentAlarmService.selectEquipmentAlarmPage(Condition.getPage(query), faultAnalysisReq);
		return R.data(pages);
	}

	/**
	 * 修改 设备告警表
	 */
	@ApiLog("修改 设备告警状态")
	@PostMapping("/updateStatus")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改-无需处理", notes = "传入equipmentAlarm")
	public R updateStatus(Long id) {
		EquipmentAlarm equipmentAlarm = equipmentAlarmService.getById(id);
		equipmentAlarm.setHandleStatus(RepairDealStatus.NONEEDTODEAL.id);
		return R.status(equipmentAlarmService.updateById(equipmentAlarm));
	}

	@ApiOperationSupport(order = 5)
	@ApiOperation("编辑")
	@PutMapping("/update")
	public R updateAlarm(@RequestBody EquipmentAlarm equipmentAlarm) {
		return R.status(equipmentAlarmService.update(equipmentAlarm, new QueryWrapper<EquipmentAlarm>().eq("id", equipmentAlarm.getId())));
	}

	/**
	 * 故障分析
	 */
	@GetMapping("/faultAnalysis")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "故障分析", notes = "faultAnalysisReq")
	public R<IPage<FaultAnalysisRsep>> faultAnalysis(FaultAnalysisReq faultAnalysisReq, Query query) {
		if (Func.isEmpty(faultAnalysisReq.getStartTime())) {
			return R.fail("时间不能为空");
		}
		faultAnalysisReq.setStartTime(DateUtil.getStartDate(faultAnalysisReq.getStartTime()));
		faultAnalysisReq.setEndTime(DateUtil.getEndDate(faultAnalysisReq.getStartTime()));
		IPage<FaultAnalysisRsep> pages = equipmentAlarmService.selectFaultAnalysisPage(Condition.getPage(query), faultAnalysisReq);
		return R.data(pages);
	}

	/**
	 * 报警统计
	 */
	@GetMapping("/alarmSta")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "告警统计", notes = "faultAnalysisReq")
	public R<Map<String, Object>> alarmStatistics(FaultAnalysisReq faultAnalysisReq) {
		Map<String, Object> res = new HashMap<>();

		//告警严重程度统计
		List<Val> alarmsLeves = equipmentAlarmService.selectAlarmsLevelSta(faultAnalysisReq);
		for (Val val : alarmsLeves) {
			val.setName(AlarmLevel.getValue(val.getId()));
		}
		//所有告警类型统计
		List<Val> alarmsTypes = equipmentAlarmService.selectAlarmsTypeSta(faultAnalysisReq);
		//频繁告警类型统计(一天内出现两次以上告警)
		List<Val> oftenAlarmTypes = new ArrayList<>();
		List<Val> allalarmsTypes = new ArrayList<>();
		AlarmType[] alarmTypes = AlarmType.values();
		for (AlarmType alarmType : alarmTypes) {
			Val val = new Val();
			val.setId(alarmType.id);
			val.setName(alarmType.value);
			val.setVal(0);
			for (Val val1 : alarmsTypes) {
				if (Func.equals(alarmType.id, val1.getId())) {
					val.setVal(val1.getVal());
				}
			}
			allalarmsTypes.add(val);
		}
		for (Val val : alarmsTypes) {
			val.setName(AlarmType.getValue(val.getId()));
			if (val.getVal() >= 2) {
				oftenAlarmTypes.add(val);
			}
		}
		res.put("alarmLevel", alarmsLeves);//告警严重程度统计
		res.put("alarmType", allalarmsTypes);//所有告警类型统计
		res.put("oftenAlarmType", oftenAlarmTypes);//频繁告警类型统计(一天内出现两次以上告警)

		return R.data(res);
	}

}
