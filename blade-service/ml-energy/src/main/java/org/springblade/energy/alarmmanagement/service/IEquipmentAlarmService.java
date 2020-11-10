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
package org.springblade.energy.alarmmanagement.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;
import org.springblade.energy.alarmmanagement.dto.*;
import org.springblade.energy.alarmmanagement.entity.EquipmentAlarm;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 设备告警表 服务类
 *
 * @author bond
 * @since 2020-04-08
 */
public interface IEquipmentAlarmService extends BaseService<EquipmentAlarm> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @return
	 */
	IPage<EquipmentAlarmRsep> selectEquipmentAlarmPage(IPage<EquipmentAlarmRsep> page, FaultAnalysisReq faultAnalysisReq);

	List<EquipmentAlarm> selectAlarmsByMap(Map<String, Object> map);
	/**
	 * 故障分析
	 */
	IPage<FaultAnalysisRsep> selectFaultAnalysisPage(IPage<FaultAnalysisRsep> page, FaultAnalysisReq faultAnalysisReq);

	/**
	 * 告警严重程度统计
	 */
	List<Val> selectAlarmsLevelSta(FaultAnalysisReq faultAnalysisReq);
	/**
	 *
	 * 告警类型统计
	 */
	List<Val> selectAlarmsTypeSta(FaultAnalysisReq faultAnalysisReq);

	/**
	 * 租户未处理的告警数据
	 */
	List<EquipmentAlarmRsep>  getStationAlarmData(Long stationId);

	/**
	 * 站点最新告警数据
	 */
	public EquipmentAlarmRsep getNewestEquipmentAlarm(Map<String, Object> map);

	/**
	 * 查询最新告警时间（用于站点安全运行天数）
	 */
	Date selectLastAlarmEndTimeByItemIds(List<String> itemIds);

}
