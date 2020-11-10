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
package org.springblade.energy.alarmmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.alarmmanagement.dto.*;
import org.springblade.energy.alarmmanagement.entity.EquipmentAlarm;
import org.springblade.energy.alarmmanagement.mapper.EquipmentAlarmMapper;
import org.springblade.energy.alarmmanagement.service.IEquipmentAlarmService;
import org.springblade.energy.diagram.entity.DiagramProduct;
import org.springblade.energy.diagram.service.IDiagramProductService;
import org.springblade.energy.runningmanagement.station.entity.Station;
import org.springblade.energy.runningmanagement.station.service.IStationService;
import org.springblade.enums.AlarmLevel;
import org.springblade.enums.AlarmType;
import org.springblade.util.BigDecimalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 设备告警表 服务实现类
 *
 * @author bond
 * @since 2020-04-08
 */
@Service
public class EquipmentAlarmServiceImpl extends BaseServiceImpl<EquipmentAlarmMapper, EquipmentAlarm> implements IEquipmentAlarmService {

	@Autowired
	private BladeRedisCache redisCache;
	@Autowired
	private IDiagramProductService iDiagramProductService;
	@Autowired
	private IStationService iStationService;

	@Override
	public IPage<EquipmentAlarmRsep> selectEquipmentAlarmPage(IPage<EquipmentAlarmRsep> page, FaultAnalysisReq faultAnalysisReq) {
		List<EquipmentAlarmRsep> list = baseMapper.selectEquipmentAlarmPage(page, faultAnalysisReq);
		for (EquipmentAlarmRsep dto : list) {

			dto.setLevelName(AlarmLevel.getValue(dto.getLevel()));
			if (Func.isNotEmpty(dto.getEquipmentId())) {
				DiagramProduct diagramProduct = iDiagramProductService.getById(dto.getEquipmentId());
				if (Func.isNotEmpty(diagramProduct)) {
					dto.setEquipmentName(diagramProduct.getProductcname());
				}
			}
			if (Func.isNotEmpty(dto.getAlarmType())) {
				dto.setAlarmTypeName(AlarmType.getValue(dto.getAlarmType()));
			}

		}
		return page.setRecords(list);
	}

	public List<EquipmentAlarm> selectAlarmsByMap(Map<String, Object> map) {
		return baseMapper.selectAlarmsByMap(map);
	}

	/**
	 * 故障分析
	 */
	public IPage<FaultAnalysisRsep> selectFaultAnalysisPage(IPage<FaultAnalysisRsep> page, FaultAnalysisReq faultAnalysisReq) {
		List<FaultAnalysisRsep> list = baseMapper.selectFaultAnalysisPage(page, faultAnalysisReq);
		for (FaultAnalysisRsep data : list) {
			data.setAlarmLevelName(AlarmLevel.getValue(data.getAlarmLevel()));
			data.setAlarmTypeName(AlarmType.getValue(data.getAlarmType()));
			if (Func.equals(AlarmType.TXZD.id, data.getAlarmType())) {
				data.setHaltNum(data.getAlarmNum());

				Double time = 24 * 60.0;
				Double c = (1 - BigDecimalUtil.div(data.getHaltTime(), time, 4)) * 100;
				data.setClosedLoop(String.valueOf(c) + "%");
			} else {
				data.setHaltTime(null);
				data.setClosedLoop(String.valueOf(100) + "%");
			}
		}
		return page.setRecords(list);
	}

	/**
	 * 告警严重程度统计
	 */
	public List<Val> selectAlarmsLevelSta(FaultAnalysisReq faultAnalysisReq) {
		return baseMapper.selectAlarmsLevelSta(faultAnalysisReq);
	}

	/**
	 * 告警类型统计
	 */
	public List<Val> selectAlarmsTypeSta(FaultAnalysisReq faultAnalysisReq) {
		return baseMapper.selectAlarmsTypeSta(faultAnalysisReq);
	}

	/**
	 * 租户未处理的告警数据
	 */
	public List<EquipmentAlarmRsep> getStationAlarmData(Long stationId) {
		Map<String, Object> map = new HashMap<>();
		Station querystation = new Station();
		querystation.setId(stationId);
		List<Station> stations = iStationService.list(Condition.getQueryWrapper(querystation));
		List<EquipmentAlarmRsep> list = new ArrayList<>();
		for (Station station : stations) {
			StationAlarmDataDto dto = new StationAlarmDataDto();
			map.put("stationId", station.getId());
			EquipmentAlarmRsep equipmentAlarmRsep = baseMapper.getNotDisposeEquipmentAlarm(map);
			if (Func.isNotEmpty(equipmentAlarmRsep)) {
				list.add(equipmentAlarmRsep);
			}
		}
		return list;
	}

	/**
	 * 站点最新告警数据
	 */
	public EquipmentAlarmRsep getNewestEquipmentAlarm(Map<String, Object> map) {
		return baseMapper.getNewestEquipmentAlarm(map);
	}


	/**
	 * 查询最新告警时间（用于站点安全运行天数）
	 */
	public Date selectLastAlarmEndTimeByItemIds(List<String> itemIds) {
		return baseMapper.selectLastAlarmEndTimeByItemIds(itemIds);
	}
}
