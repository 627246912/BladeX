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
package org.springblade.pms.alarmmanagement.controller;

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
import org.springblade.dto.NameValue;
import org.springblade.enums.AlarmLevel;
import org.springblade.pms.alarmmanagement.dto.EquipmentAlarmRsep;
import org.springblade.pms.alarmmanagement.dto.FaultAnalysisReq;
import org.springblade.pms.alarmmanagement.service.IEquipmentAlarmService;
import org.springblade.pms.enums.PmsAlarmType;
import org.springblade.pms.station.dto.AreaResp;
import org.springblade.pms.station.entity.BaseStation;
import org.springblade.pms.station.service.IBaseStationService;
import org.springblade.pms.statistics.controller.CurveDataFactory;
import org.springblade.pms.statistics.dto.CurveDataInfo;
import org.springblade.pms.statistics.dto.CurveDataReq;
import org.springblade.pms.statistics.repository.CurveDataRepository;
import org.springblade.util.DateUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 设备告警表 控制器
 *
 * @author bond
 * @since 2020-04-08
 */
@RestController
@AllArgsConstructor
@RequestMapping("/equipmentalarm")
@Api(value = "报警管理", tags = "报警管理")
public class EquipmentAlarmController extends BladeController {

	private IEquipmentAlarmService equipmentAlarmService;
	private IBaseStationService iBaseStationService;


	/**
	 * 自定义分页 设备告警表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "事件列表", notes = "传入equipmentAlarm")
	public R<IPage<EquipmentAlarmRsep>> page(FaultAnalysisReq faultAnalysisReq, Query query) {
		IPage<EquipmentAlarmRsep> pages = equipmentAlarmService.selectEquipmentAlarmPage(Condition.getPage(query), faultAnalysisReq);
		return R.data(pages);
	}


	@GetMapping("/getAreaStationTree")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "根据区域编码获取区域树", notes = "传入areaCode")
	public R<List<AreaResp>> getAreaStationTree(@RequestParam(value = "areaCode", required = false) String areaCode)
	{
		List<AreaResp> list=iBaseStationService.getAreaStationTree(areaCode);
		return R.data(list);
	}

	@GetMapping("/getAlarmSta")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "告警统计", notes = "传入areaCode")
	public R<Map<String,Object>> getAlarmSta(@ApiParam(value = "areaCode", required = true)  @RequestParam String areaCode,
											 @ApiParam(value = "统计时间类型0:小时报表，2:日报表 ,3:月报表", required = true) @RequestParam Integer dateType,
											 @ApiParam(value = "时间", required = true) @RequestParam Date time) {
		//站点列表
		List<BaseStation> stationList = iBaseStationService.selectStationsByAreaCode(areaCode);
		if (Func.isEmpty(stationList)) {
			return R.fail("没有站点");
		}

		List<Long> stations=new ArrayList<>();
		for(BaseStation baseStation:stationList){
			stations.add(baseStation.getId());
		}

		CurveDataReq curveDataReq = new CurveDataReq();
		curveDataReq.setTime(time);
		curveDataReq.setDateType(dateType);
		CurveDataInfo curveDataInfo= CurveDataFactory.getCurveDataInfo(curveDataReq,false);

		List<String> times=curveDataInfo.getShowRows();
		List<Object> xvals= CurveDataRepository.valueRowsX(curveDataInfo);
		List<Integer> yvals= new ArrayList<>();
		for(String ctime:times){
			Date startTime= null;
			Date endTime= null;
			switch (dateType){
				case 0:
					startTime= DateUtil.strToDate(ctime+":00:00",DateUtil.TIME_PATTERN_24);
					endTime = DateUtil.addHour(startTime,1);
					break;
				case 2:
					startTime=DateUtil.strToDate(ctime+" 00:00:00",DateUtil.TIME_PATTERN_24);
					endTime = DateUtil.addHour(startTime,24);
					break;
				case 3:
					startTime=DateUtil.strToDate(ctime+"-00 00:00:00",DateUtil.TIME_PATTERN_24);
					endTime = DateUtil.addMonth(startTime,1);
					break;
			}

			Map<String,Object> map =new HashMap<>();
			map.put("stations",stations);
			map.put("startTime",startTime);
			map.put("endTime",endTime);
			int count=equipmentAlarmService.getAlarmCount(map);
			yvals.add(count);
		}

		Map<String,Object> repsMap =new HashMap<>();
		repsMap.put("xvals",xvals);
		repsMap.put("yvals",yvals);
		return R.data(repsMap);

	}


	/**
	 * 故障分析
	 */
	@GetMapping("/alarmAnalysis")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "故障分析", notes = "faultAnalysisReq")
	public R<Map<String,Object>> alarmAnalysis(@ApiParam(value = "areaCode", required = true)  @RequestParam String areaCode,
											   @ApiParam(value = "统计时间类型0:小时报表，2:日报表 ,3:月报表", required = true) @RequestParam Integer dateType,
											   @ApiParam(value = "时间", required = true) @RequestParam Date time) {
		Map<String, Object> resMap = new HashMap<>();
		//站点列表
		List<BaseStation> stationList = iBaseStationService.selectStationsByAreaCode(areaCode);
		if (Func.isEmpty(stationList)) {
			return R.fail("没有站点");
		}
		List<String> gwIds=new ArrayList<>();
		for(BaseStation baseStation:stationList){
			gwIds.add(baseStation.getGwId());
		}

		CurveDataReq curveDataReq = new CurveDataReq();
		curveDataReq.setTime(time);
		curveDataReq.setDateType(dateType);
		CurveDataInfo curveDataInfo= CurveDataFactory.getCurveDataInfo(curveDataReq,false);

		Date startTime =DateUtil.strToDate(curveDataInfo.getStime(),DateUtil.TIME_PATTERN_24);
		Date endTime =DateUtil.strToDate(curveDataInfo.getEtime(),DateUtil.TIME_PATTERN_24);

		Map<String,Object> map =new HashMap<>();
		map.put("gwIds",gwIds);
		map.put("startTime",startTime);
		map.put("endTime",endTime);

		//告警严重程度统计
		List<NameValue> alarmsLeves = equipmentAlarmService.selectAlarmsLevelSta(map);
		for (NameValue val : alarmsLeves) {
			if(Func.isNotEmpty(val.getCode())){
				val.setName(AlarmLevel.getValue(Integer.valueOf(val.getCode())));
			}
		}

		//所有告警类型统计
		List<NameValue> alarmsTypes = equipmentAlarmService.selectAlarmsTypeSta(map);
		//频繁告警类型统计(一天内出现两次以上告警)
		List<NameValue> oftenAlarmTypes = new ArrayList<>();

		for (NameValue val : alarmsTypes) {
			if(Func.isNotEmpty(val.getCode())){
				val.setName(PmsAlarmType.getValue(Integer.valueOf(val.getCode())));
			}

			if (Integer.valueOf( val.getValue().toString()) >= 2) {
				oftenAlarmTypes.add(val);
			}
		}

		Map<String,Object> res =new HashMap<>();
		res.put("alarmLevel", alarmsLeves);//告警严重程度统计
		res.put("alarmType", alarmsTypes);//所有告警类型统计
		res.put("oftenAlarmType", oftenAlarmTypes);//频繁告警类型统计(一天内出现两次以上告警)

		return R.data(res);
	}

}
