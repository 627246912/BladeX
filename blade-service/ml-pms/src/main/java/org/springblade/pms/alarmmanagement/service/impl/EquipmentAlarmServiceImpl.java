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
package org.springblade.pms.alarmmanagement.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.bean.DeviceCommunicationStatus;
import org.springblade.constants.GwsubscribeConstant;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.utils.Func;
import org.springblade.dto.NameValue;
import org.springblade.enums.AlarmLevel;
import org.springblade.enums.AlarmType;
import org.springblade.pms.alarmmanagement.dto.EquipmentAlarmRsep;
import org.springblade.pms.alarmmanagement.dto.FaultAnalysisReq;
import org.springblade.pms.alarmmanagement.entity.EquipmentAlarm;
import org.springblade.pms.alarmmanagement.mapper.EquipmentAlarmMapper;
import org.springblade.pms.alarmmanagement.service.IEquipmentAlarmService;
import org.springblade.pms.config.SystemConfig;
import org.springblade.pms.enums.OnlineStatus;
import org.springblade.pms.enums.PmsAlarmType;
import org.springblade.pms.enums.UserGroup;
import org.springblade.pms.gw.feign.IDeviceClient;
import org.springblade.pms.station.entity.BaseStation;
import org.springblade.pms.station.service.IBaseStationService;
import org.springblade.pms.station.service.IRtuSetService;
import org.springblade.util.DateUtil;
import org.springblade.util.RedisKeysUtil;
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
	private SystemConfig systemConfig;
	@Autowired
	private BladeRedisCache redisCache;

	@Autowired
	private IBaseStationService iStationService;
	@Autowired
	private IDeviceClient deviceClient;

	@Autowired
	private IRtuSetService iGwcomSetService;

	@Override
	public IPage<EquipmentAlarmRsep> selectEquipmentAlarmPage(IPage<EquipmentAlarmRsep> page, FaultAnalysisReq faultAnalysisReq) {
	List<EquipmentAlarmRsep> list= baseMapper.selectEquipmentAlarmPage(page, faultAnalysisReq);
		String key= RedisKeysUtil.getKey(systemConfig.getServerIdCard(), GwsubscribeConstant.DEVICE_CODE_TO_STATUS);
		for(EquipmentAlarmRsep dto:list){
			DeviceCommunicationStatus sta=redisCache.hGet(key, dto.getGwId());
			if(Func.isNotEmpty(sta)) {
				dto.setEquipmentStatus(sta.getStatus());
				dto.setEquipmentStatusName(OnlineStatus.getValue(sta.getStatus()));
			}else{
				dto.setEquipmentStatus(OnlineStatus.UNLINE.id);
				dto.setEquipmentStatusName(OnlineStatus.UNLINE.value);
			}
			dto.setLevelName(AlarmLevel.getValue(dto.getLevel()));
			if(Func.isNotEmpty(dto.getAlarmType())){
				dto.setAlarmTypeName(AlarmType.getValue(dto.getAlarmType()));
			}

		}
		return page.setRecords(list);
	}
	public List<EquipmentAlarm> selectAlarmsByMap(Map<String,Object> map){
		return baseMapper.selectAlarmsByMap(map);
	}


	/**
	 * 查询最新告警时间（用于站点安全运行天数）
	 */
	public Date selectLastAlarmEndTimeByStationId(Long stationId){
		return baseMapper.selectLastAlarmEndTimeByStationId(stationId);
	}
	/**
	 * 站点最新告警数据
	 */
	public EquipmentAlarmRsep getNewestEquipmentAlarm(Map<String, Object> map){
		return baseMapper.getNewestEquipmentAlarm(map);
	}


	/**
	 * 根据区域编码获取大屏端数据
	 */
	public Map<String,Object> getBigScreenAlarmData(String areaCode){
		Map<String,Object> resMap=new HashMap<>();

		List<BaseStation> stationList = iStationService.selectStationsByAreaCode(areaCode);
		if(Func.isEmpty(stationList)){
			return resMap;
		}
		Set<String> gwIds= new HashSet<>();
		for (BaseStation station : stationList) {
			gwIds.add(station.getGwId());
		}
		//告警分析

		Map<String,Object> parameterMap=new HashMap<>();
		parameterMap.put("gwIds",gwIds);
		Date time= DateUtil.getCurrentDate();
		Date startTime=DateUtil.getStartDate(time);
		Date endTime=DateUtil.getEndDate(time);
		parameterMap.put("startTime",DateUtil.format(startTime,DateUtil.TIME_PATTERN_24));
		parameterMap.put("endTime",DateUtil.format(endTime,DateUtil.TIME_PATTERN_24));
		Integer thisdayCount=baseMapper.selectAlarmsSta(parameterMap);

		Map<String,Object> gaojingfenxi=new HashMap<>();
		gaojingfenxi.put("thisdayAlram",thisdayCount);

		time=DateUtil.addHour(time,-24);
		startTime=DateUtil.getStartDate(time);
		endTime=DateUtil.getEndDate(time);
		parameterMap.put("startTime",DateUtil.format(startTime,DateUtil.TIME_PATTERN_24));
		parameterMap.put("endTime",DateUtil.format(endTime,DateUtil.TIME_PATTERN_24));
		Integer yesterdayCount=baseMapper.selectAlarmsSta(parameterMap);
		gaojingfenxi.put("yesterdayAlram",yesterdayCount);

		NameValue dayHb = new NameValue();
		double  rate=((double) (thisdayCount-yesterdayCount)/yesterdayCount)*100;
		if(Func.equals(0,yesterdayCount) && thisdayCount>0){
			rate=100;
		}

		gaojingfenxi.put("dayHb",String.valueOf(rate)+"%");


		startTime=DateUtil.getMonthStartDate(DateUtil.getCurrentDate());
		endTime=DateUtil.getMonthEndDate(startTime);
		parameterMap.put("startTime",DateUtil.format(startTime,DateUtil.TIME_PATTERN_24));
		parameterMap.put("endTime",DateUtil.format(endTime,DateUtil.TIME_PATTERN_24));
		int thisMonthCount=baseMapper.selectAlarmsSta(parameterMap);
		gaojingfenxi.put("thismonthAlram",thisMonthCount);

		startTime=DateUtil.getMonthStartDate(DateUtil.addMonth(startTime,-1));
		endTime=DateUtil.getMonthEndDate(startTime);
		parameterMap.put("startTime",DateUtil.format(startTime,DateUtil.TIME_PATTERN_24));
		parameterMap.put("endTime",DateUtil.format(endTime,DateUtil.TIME_PATTERN_24));
		int lastMonthCount=baseMapper.selectAlarmsSta(parameterMap);
		gaojingfenxi.put("lastmonthAlram",lastMonthCount);

		double  monthrate=((double) (thisMonthCount-lastMonthCount)/lastMonthCount)*100;
		if(Func.equals(0,lastMonthCount) && thisMonthCount>0){
			monthrate=100;
		}
		gaojingfenxi.put("monthHb",String.valueOf(monthrate)+"%");
		resMap.put("gaojingfenxi",gaojingfenxi);

		//重点告警监测
		parameterMap=new HashMap<>();
		parameterMap.put("gwIds",gwIds);
//		 time= DateUtil.getCurrentDate();
//		 startTime=DateUtil.getStartDate(time);
//		 endTime=DateUtil.getEndDate(time);
//		parameterMap.put("startTime",DateUtil.format(startTime,DateUtil.TIME_PATTERN_24));
//		parameterMap.put("endTime",DateUtil.format(endTime,DateUtil.TIME_PATTERN_24));
		Set<Integer> alarmTypes=new HashSet<>();
		alarmTypes.add(PmsAlarmType.PSID1125.id);
		alarmTypes.add(PmsAlarmType.PSID1126.id);
		alarmTypes.add(PmsAlarmType.PSID1062.id);
		alarmTypes.add(PmsAlarmType.PSID1127.id);
		parameterMap.put("alarmTypes",alarmTypes);


		Map<String,Object> zhongdiangaojingjiance=new HashMap<>();
		zhongdiangaojingjiance.put("dyqy",0);
		zhongdiangaojingjiance.put("dygy",0);
		zhongdiangaojingjiance.put("dlgl",0);
		zhongdiangaojingjiance.put("cwd",0);

		List<NameValue> AlarmsTypeSta= baseMapper.selectAlarmsTypeSta(parameterMap);
		for(NameValue nameValue:AlarmsTypeSta){
			//nameValue.setName(AlarmType.getValue(Integer.valueOf(nameValue.getCode())));
			if(Func.equals(nameValue.getCode(),PmsAlarmType.PSID1125.id)){
				zhongdiangaojingjiance.put("dyqy",nameValue.getValue());
			}
			if(Func.equals(nameValue.getCode(),PmsAlarmType.PSID1126.id)){
				zhongdiangaojingjiance.put("dygy",nameValue.getValue());
			}
			if(Func.equals(nameValue.getCode(),PmsAlarmType.PSID1062.id)){
				zhongdiangaojingjiance.put("dlgl",nameValue.getValue());
			}
			if(Func.equals(nameValue.getCode(),PmsAlarmType.PSID1127.id)){
				zhongdiangaojingjiance.put("cwd",nameValue.getValue());
			}
		}
		resMap.put("zhongdiangaojingjiance",zhongdiangaojingjiance);

		//最新告警
		parameterMap=new HashMap<>();
		parameterMap.put("gwIds",gwIds);
		List<EquipmentAlarmRsep> zuixingaojing= baseMapper.getNotDisposeEquipmentAlarm(parameterMap);
		String key= RedisKeysUtil.getKey(systemConfig.getServerIdCard(), GwsubscribeConstant.DEVICE_CODE_TO_STATUS);
		for(EquipmentAlarmRsep dto:zuixingaojing){
			DeviceCommunicationStatus sta=redisCache.hGet(key, dto.getGwId());
			if(Func.isNotEmpty(sta)) {
				dto.setEquipmentStatus(sta.getStatus());
				dto.setEquipmentStatusName(OnlineStatus.getValue(sta.getStatus()));
			}
			dto.setAlarmTypeName(PmsAlarmType.getValue(dto.getAlarmType()));
			dto.setLevelName(AlarmLevel.getValue(dto.getLevel()));
			dto.setUserGroupName(userGruop(dto.getUserGroup()));
		}
		resMap.put("zuixingaojing",zuixingaojing);


		Map<String,Object> gaojingCount=new HashMap<>();

		gaojingCount.put("gaojingCount",zuixingaojing==null?0:zuixingaojing.size());
		resMap.put("gaojingCount",gaojingCount);
		return resMap;
	}
	public String userGruop(String userGroup){
		String str="";
		if(Func.isEmpty(userGroup)){
			return "--";
		}
		String[]  strings= userGroup.split(",");
		for(int i=0;i<strings.length;i++){
			if(Func.isNotEmpty(strings[i])){
				str+=","+UserGroup.getValue(strings[i]);
			}
		}

		return Func.isNotEmpty(str)==true?str.substring(1):"--";
	}

	/**
	 *获取告警数量
	 */
	public int getAlarmCount(Map<String, Object> map){
		return baseMapper.getAlarmCount(map);
	}

	/**
	 * 告警严重程度统计
	 */
	public List<NameValue> selectAlarmsLevelSta(Map<String, Object> map){
		return baseMapper.selectAlarmsLevelSta(map);
	}
	/**
	 *
	 * 告警类型统计
	 */
	public List<NameValue> selectAlarmsTypeSta(Map<String, Object> map){
		return baseMapper.selectAlarmsTypeSta(map);
	}
}
