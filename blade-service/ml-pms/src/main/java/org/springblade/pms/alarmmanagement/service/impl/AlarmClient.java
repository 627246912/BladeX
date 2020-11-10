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

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springblade.bean.Alarm;
import org.springblade.bean.Device;
import org.springblade.bean.DeviceItem;
import org.springblade.bean.DeviceSub;
import org.springblade.constants.GwsubscribeConstant;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.utils.DateTimeUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.feign.IAlarmClient;
import org.springblade.enums.AlarmStatus;
import org.springblade.enums.ItemStype;
import org.springblade.enums.RepairDealStatus;
import org.springblade.pms.alarmmanagement.entity.EquipmentAlarm;
import org.springblade.pms.alarmmanagement.service.IEquipmentAlarmService;
import org.springblade.pms.enums.PmsAlarmType;
import org.springblade.pms.gw.feign.IDeviceClient;
import org.springblade.pms.station.entity.BaseStation;
import org.springblade.pms.station.entity.RtuSet;
import org.springblade.pms.station.service.IBaseStationService;
import org.springblade.pms.station.service.IRtuSetService;
import org.springblade.pms.station.vo.BaseStationVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备告警表 服务实现类
 *
 * @author bond
 * @since 2020-04-08
 */
@RestController
@AllArgsConstructor
@Service
public class AlarmClient implements IAlarmClient {
	private IBaseStationService stationService;

	private IEquipmentAlarmService equipmentAlarmService;
	private IDeviceClient deviceClient;
	@Autowired
	private BladeRedisCache redisCache;

	IRtuSetService iGwcomSetService;

	/* 产生告警推送APP( [告警状态,告警等级] 站点名称,地址,数据项名称,告警内容) */
	private static final String PUSH_APP_START_ALARM_INFO = "[%s%s告警]%s（%s）%s%s！";
	/* 消除告警推送 */
	private static final String PUSH_END_ALARM_INFO = "[%s%s告警]%s（%s）%s%s！";
	/* 告警内容 数据项名称,告警内容*/
	private static final String ALARM_INFO = "%s%s";

	/* 产生遥测告警推送短信 */
	private static final String PUSH_SMS_START_YCALARM_INFO = "越限，越限值：%s，限值：%s";
	/* 产生遥信告警推送短信 */
	private static final String PUSH_SMS_START_YXALARM_INFO = "状态：由%s到%s";
	/* 消除遥测告警推送短信 */
	private static final String PUSH_SMS_END_YCALARM_INFO = "实时值恢复正常，当前值：%s";
	/* 消除遥信告警推送短信 */
	private static final String PUSH_SMS_END_YXALARM_INFO = "状态恢复正常，状态：由%s到%s";

	private static final String UPLIMIT = " 超过上限";
	private static final String LOWLIMIT = " 低于下限";
	private static final String UPUPLIMIT = " 超过上上限";
	private static final String LOWLOWLIMIT = " 低于下下限";
	private static final String ALARM_CURRENT_VALUE = " 当前值： ";
	private static final String COMMUNICATION_INTERRUPTION = "通讯中断";
	private static final String INIT_ALARM_TYPE = "严重";


	/**
	 * 警告数据处理
	 */
	@PostMapping(SAVEALARM)
	public void saveAlarm(List<Alarm> alarmList){
		dealRealTimeAlarm(alarmList);
	}
	public void dealRealTimeAlarm(List<Alarm> alarms){
		//要新增的报警
		List<EquipmentAlarm> addAlarmList = new ArrayList<>();
		//修改更新的报警
		List<EquipmentAlarm> updateAlarmList = new ArrayList<>();
		//网关对应的站点
		Map<String, BaseStation> deviceStationMap = new HashMap<>();
		//
		Map<Integer,EquipmentAlarm> alarmMap = new HashMap<>();
		for(Alarm alarm : alarms) {
			if(StringUtils.isNotEmpty(alarm.getGwid())) {
				deviceStationMap.put(alarm.getGwid(), null);
			}
			alarmMap.put(alarm.getAlarmid(),null);
		}

		if(Func.isEmpty(deviceStationMap)){
			return;
		}

		/* 获取站点列表 */
		List<BaseStationVO> stationList = stationService.selectStationsByDeviceIds(deviceStationMap.keySet());
		if(Func.isEmpty(stationList)){
			return;
		}

		for (BaseStationVO stationVo : stationList) {
			deviceStationMap.put(stationVo.getGwId(),stationVo);
		}



		/* 获取已存在的告警列表 */
		Map<String,Object> queryMap = new HashMap<>();
		queryMap.put("alarmIds",alarmMap.keySet());
		List<EquipmentAlarm> equipmentAlarms = equipmentAlarmService.selectAlarmsByMap(queryMap);
		for (EquipmentAlarm equipmentAlarm : equipmentAlarms) {
			alarmMap.put(equipmentAlarm.getAlarmId(),equipmentAlarm);
		}


		for(Alarm alarm : alarms) {
			BaseStationVO stationVo = (BaseStationVO)deviceStationMap.get(alarm.getGwid());
			if(stationVo==null){
				continue;
			}
			dealEqAlarmData(alarm,alarmMap,stationVo,addAlarmList,updateAlarmList);
		}


		/* 添加告警信息 */
		if(Func.isNotEmpty(addAlarmList)) {
			boolean r= equipmentAlarmService.saveBatch(addAlarmList);
			//看是否有websocke,有则发送告警信息
			//pushRealAlarmData(addAlarmList);

		}

		/* 更新告警信息 */
		if(Func.isNotEmpty(updateAlarmList)){
			equipmentAlarmService.saveOrUpdateBatch(updateAlarmList);
		}
	}

	private void dealEqAlarmData(Alarm alarm, Map<Integer, EquipmentAlarm> alarmMap, BaseStationVO stationVo,
										List<EquipmentAlarm> addAlarmList, List<EquipmentAlarm> updateAlarmList) {
		Integer alarmId = alarm.getAlarmid();
		String itemId = alarm.getId();//数据项ID
		String etime = alarm.getEtime();
		//获取报警的内容及设备名称
		Map<String, String> alarmInfoMap = getAlarmItemName(alarm);
		String alarmItemName = alarmInfoMap.get("alarmItemName");
		String eqAlarmContent = alarmInfoMap.get("eqAlarmContent");
		String userGroup = alarmInfoMap.get("userGroup");
		AlarmStatus alarmStatus=AlarmStatus.ALARMING;
		if(Func.isEmpty(etime) || Func.equals(etime,"0001-01-01 00:00:00")){
			alarmStatus=AlarmStatus.ALARMED;
		}

		EquipmentAlarm eqAlarm = alarmMap.get(alarmId);



		//更新告警
		if (alarmMap.containsKey(alarmId) && eqAlarm!=null) {
			if (StringUtils.isNotEmpty(etime)) {
				//有数据则更新告警状态和处理状态
				if (eqAlarm.getAlarmEndTime() != null) {
					EquipmentAlarm upEqAlarm = new EquipmentAlarm();
					BeanUtils.copyProperties(eqAlarm, upEqAlarm);
					upEqAlarm.setId(eqAlarm.getId());
					upEqAlarm.setAlarmStatus(AlarmStatus.ALARMED.id);
					if (RepairDealStatus.DELETE.id.intValue() != eqAlarm.getHandleStatus().intValue()) {
						upEqAlarm.setHandleStatus(RepairDealStatus.NONEEDTODEAL.id);
					}
					upEqAlarm.setAlarmEndTime(DateTimeUtil.parseDateTime(etime));
					upEqAlarm.setTenantId(stationVo.getTenantId());
					updateAlarmList.add(upEqAlarm);


				}
			}
		} else {


			EquipmentAlarm equipmentAlarm = new EquipmentAlarm();
				equipmentAlarm.setAlarmId(alarmId);
			equipmentAlarm.setGwId(alarm.getGwid());
				equipmentAlarm.setAlarmItemId(itemId);
				equipmentAlarm.setAlarmContent(eqAlarmContent);
				equipmentAlarm.setLevel(alarm.getLevel());
				equipmentAlarm.setStationId(stationVo.getId());
				equipmentAlarm.setAddress(stationVo.getAddress());
				equipmentAlarm.setAlarmType(alarm.getAlarmtype());

			equipmentAlarm.setTenantId(stationVo.getTenantId());

			if (eqAlarmContent.contains(PmsAlarmType.TXZD.value)) {
				equipmentAlarm.setAlarmType(PmsAlarmType.TXZD.id);
			} else {
				equipmentAlarm.setAlarmType(PmsAlarmType.SBGZ.id);
			}

				if (StringUtils.isNotEmpty(alarm.getStime())) {
					equipmentAlarm.setAlarmTime(DateTimeUtil.parseDateTime(alarm.getStime()));
				}
				if (StringUtils.isNotEmpty(etime)) {
					equipmentAlarm.setAlarmEndTime(DateTimeUtil.parseDateTime(etime));
					equipmentAlarm.setHandleStatus(RepairDealStatus.NONEEDTODEAL.id);
				} else {
					equipmentAlarm.setHandleStatus(RepairDealStatus.NODEAL.id);

				}
				equipmentAlarm.setAlarmStatus(alarmStatus.id);
				equipmentAlarm.setEquipmentName(alarmItemName);
				equipmentAlarm.setUserGroup(userGroup);

				addAlarmList.add(equipmentAlarm);


		}
	}

	private Map<String, String>  getAlarmItemName(Alarm alarm){
		String userGroup="";

		String alarmItemName = "";
		String idStr = alarm.getId();
		Float val = alarm.getVal();
		StringBuffer alarmContent = new StringBuffer();

		if(idStr.contains(GwsubscribeConstant.DAT_KEY) || idStr.contains(GwsubscribeConstant.YSYC_KEY)) {
			DeviceItem deviceItem = deviceClient.getDeviceItemInfosByItemid(idStr);
			if (Func.isNotEmpty( deviceItem)) {
				int sid=deviceItem.getSid();
				PmsAlarmType pmsAlarmType= PmsAlarmType.getAlarmType(sid);
				if(Func.isNotEmpty(pmsAlarmType)){
					alarm.setAlarmtype(pmsAlarmType.id);
				}
				alarmItemName = deviceItem.getShortname();
				int stype = deviceItem.getStype().intValue();
				if(ItemStype.TRANSPORTYC.id.equals(stype) || ItemStype.OPERATIONYC.id.equals(stype)) {
					Float uulimit = deviceItem.getUulimit();
					Float ulimit = deviceItem.getUlimit();
					Float llimit = deviceItem.getLlimit();
					Float lllimit = deviceItem.getLllimit();
					Float overLimitVal = 1f;

					if(ulimit!=null && val.compareTo(ulimit) > 0){
						if(uulimit!=null && val.compareTo(uulimit) > 0){
							alarmContent.append(UPUPLIMIT);
							overLimitVal = uulimit;
						}else {
							alarmContent.append(UPLIMIT);
							overLimitVal = ulimit;
						}
					}
					if(llimit!=null && val.compareTo(llimit) < 0){
						if(lllimit!=null && val.compareTo(lllimit) < 0){
							alarmContent.append(LOWLOWLIMIT);
							overLimitVal = lllimit;
						}else {
							alarmContent.append(LOWLIMIT);
							overLimitVal = llimit;
						}
					}
					alarmContent.append(ALARM_CURRENT_VALUE +  val);
				}else if(ItemStype.TRANSPORTYX.id.equals(stype) || ItemStype.OPERATIONYX.id.equals(stype)) {
					String curStatus = "",oldStatus = "";
					if(0 == val){
						curStatus = deviceItem.getDesc0();
						oldStatus = deviceItem.getDesc1();
					}else {
						curStatus = deviceItem.getDesc1();
						oldStatus = deviceItem.getDesc0();
					}
					alarmContent.append(curStatus);
				}

				//查询告警影响用户
				Map<String,Object> query =new HashMap<>();
				query.put("rtuidcb",deviceItem.getRtuidcb());
				List<RtuSet> gwcomSets=iGwcomSetService.selectGwcomSetList(query);
				for(RtuSet gwcomSet:gwcomSets){
					if(Func.isNotEmpty(gwcomSet.getUserGroup()) && !Func.equals(gwcomSet.getUserGroup(),"0")){
						userGroup+=","+gwcomSet.getUserGroup();
					}

				}
			}

		}else if(idStr.contains("_")) {
			//rtu等级告警
			DeviceSub sub =	deviceClient.getDeviceSubsByRtuidcb(idStr);
			if(Func.isNotEmpty(sub)){
				alarmItemName = sub.getRtuname();

				//查询告警影响用户
				Map<String,Object> query =new HashMap<>();
				query.put("rtuidcb",sub.getRtuidcb());
				List<RtuSet> gwcomSets=iGwcomSetService.selectGwcomSetList(query);
				for(RtuSet gwcomSet:gwcomSets){
					if(Func.isNotEmpty(gwcomSet.getUserGroup()) && !Func.equals(gwcomSet.getUserGroup(),"0")){
						userGroup+=","+gwcomSet.getUserGroup();
					}
				}
			}
			alarmContent.append("（设备）");
			alarmContent.append(COMMUNICATION_INTERRUPTION);



		}else {
			//网关告警
			Device device = deviceClient.getDeviceBygwid(alarm.getGwid());
			if(Func.isNotEmpty( device)){
				alarmItemName = device.getName();

				//查询告警影响用户
				Map<String,Object> query =new HashMap<>();
				query.put("gwId",alarm.getGwid());
				List<RtuSet> gwcomSets=iGwcomSetService.selectGwcomSetList(query);
				for(RtuSet gwcomSet:gwcomSets){
					if(Func.isNotEmpty(gwcomSet.getUserGroup()) && !Func.equals(gwcomSet.getUserGroup(),"0")){
						userGroup+=","+gwcomSet.getUserGroup();
					}
				}
			}
			alarmContent.append("（通讯机）");
			alarmContent.append(COMMUNICATION_INTERRUPTION);


		}

		/* 产生告警推送( [告警状态,告警等级] 站点名称,地址,数据项名称,告警内容) */

		String eqAlarmContent = String.format(ALARM_INFO,alarmItemName,alarmContent.toString());
		Map<String,String> alarmInfoMap = new HashMap<>();
		alarmInfoMap.put("alarmItemName",alarmItemName);
		alarmInfoMap.put("eqAlarmContent",eqAlarmContent);

		alarmInfoMap.put("userGroup",Func.isNotEmpty(userGroup)==true?userGroup.substring(1):null);
		return alarmInfoMap;
	}

	/*public void pushRealAlarmData(List<EquipmentAlarm> addAlarmList) {
		//从redis里面取所有的socket连接数据
		String wkey= WebsocketConstant.websocket_kek;
		Map<Object,Object> redisMap=new HashMap<>(redisCache.hGetAll(wkey));

		//查询连接池里面的channel数据
		Iterator<Channel> iterator = ChannelHandlerPool.channelGroup.iterator();
		//iterator.hasNext()如果存在元素的话返回true
		while(iterator.hasNext()) {
			//iterator.next()返回迭代的下一个元素
			Channel channel=iterator.next();
			Object params= redisMap.get(channel.id().toString());
			if (Func.isNotEmpty(params)){
				JSONObject param= (JSONObject) JSONObject.toJSON(params);
				//告警实时数据
				if(param.get(WebsocketConstant.METHOD).equals(WebsocketConstant.getRealAlarmData)) {
					Map<String, Object> map =new HashMap<>();
					for(EquipmentAlarm equipmentAlarm: addAlarmList){
						String key= WebsocketConstant.websocket_station;
						BaseStationVO station=
						if(Func.isNotEmpty(station)){
							map.put("alarmId",equipmentAlarm.getAlarmId());
							EquipmentAlarmRsep equipmentAlarmRsep= equipmentAlarmService.getNewestEquipmentAlarm(map);
							String data= JSON.toJSONString(equipmentAlarmRsep);
							ChannelHandlerPool.channelGroup.writeAndFlush(new TextWebSocketFrame(data), new Matcher(channel));
						}

					}

				}
			}
		}

	}*/




}
