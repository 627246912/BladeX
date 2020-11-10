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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springblade.bean.Alarm;
import org.springblade.bean.Device;
import org.springblade.bean.DeviceItem;
import org.springblade.bean.DeviceSub;
import org.springblade.constants.GwsubscribeConstant;
import org.springblade.constants.WebsocketConstant;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.utils.DateTimeUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.alarmmanagement.dto.EquipmentAlarmRsep;
import org.springblade.energy.alarmmanagement.entity.EquipmentAlarm;
import org.springblade.energy.alarmmanagement.service.IEquipmentAlarmService;
import org.springblade.energy.diagram.entity.DiagramItem;
import org.springblade.energy.diagram.entity.DiagramProduct;
import org.springblade.energy.diagram.service.IDiagramItemService;
import org.springblade.energy.diagram.service.IDiagramProductService;
import org.springblade.energy.diagram.service.IDiagramService;
import org.springblade.energy.feign.IAlarmClient;
import org.springblade.energy.runningmanagement.station.entity.Site;
import org.springblade.energy.runningmanagement.station.entity.Station;
import org.springblade.energy.runningmanagement.station.service.ISiteService;
import org.springblade.energy.runningmanagement.station.service.IStationService;
import org.springblade.energy.runningmanagement.station.vo.StationVO;
import org.springblade.energy.websocket.handler.ChannelHandlerPool;
import org.springblade.energy.websocket.handler.Matcher;
import org.springblade.energy.websocket.handler.RealAlarmDataHandler;
import org.springblade.enums.AlarmStatus;
import org.springblade.enums.AlarmType;
import org.springblade.enums.ItemStype;
import org.springblade.enums.RepairDealStatus;
import org.springblade.gw.feign.IDeviceClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

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
	private IStationService stationService;
	private IDiagramItemService iDiagramItemService;
	private IDiagramService iDiagramService;
	private IDiagramProductService iDiagramProductService;
	ISiteService iSiteService;
	IStationService iStationService;

	private IEquipmentAlarmService equipmentAlarmService;
	private IDeviceClient deviceClient;
	@Autowired
	private BladeRedisCache redisCache;

	@Autowired
	private RealAlarmDataHandler realAlarmDataHandler;

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
	public void saveAlarm(List<Alarm> alarmList) {
		dealRealTimeAlarm(alarmList);
	}

	public void dealRealTimeAlarm(List<Alarm> alarms) {
		//要新增的报警
		List<EquipmentAlarm> addAlarmList = new ArrayList<>();
		//修改更新的报警
		List<EquipmentAlarm> updateAlarmList = new ArrayList<>();
		//网关对应的站点
		Map<String, Station> deviceStationMap = new HashMap<>();
		//
		Map<Integer, EquipmentAlarm> alarmMap = new HashMap<>();
		for (Alarm alarm : alarms) {
			if (StringUtils.isNotEmpty(alarm.getGwid())) {
				deviceStationMap.put(alarm.getGwid(), null);
			}
			alarmMap.put(alarm.getAlarmid(), null);
		}

		if (deviceStationMap == null || deviceStationMap.isEmpty()) {
			return;
		}
		Set<String> deviceIds = deviceStationMap.keySet();

		/* 获取站点列表 */
		List<StationVO> stationList = stationService.selectStationsByDeviceIds(deviceStationMap.keySet());
		if (stationList == null || stationList.isEmpty()) {
			return;
		}

		for (StationVO stationVo : stationList) {
			deviceStationMap.put(stationVo.getDeviceId(), stationVo);
		}



		/* 获取已存在的告警列表 */
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("alarmIds", alarmMap.keySet());
		List<EquipmentAlarm> equipmentAlarms = equipmentAlarmService.selectAlarmsByMap(queryMap);
		for (EquipmentAlarm equipmentAlarm : equipmentAlarms) {
			alarmMap.put(equipmentAlarm.getAlarmId(), equipmentAlarm);
		}


		for (Alarm alarm : alarms) {
			StationVO stationVo = (StationVO) deviceStationMap.get(alarm.getGwid());
			if (stationVo == null) {
				continue;
			}
			dealEqAlarmData(alarm, alarmMap, stationVo, addAlarmList, updateAlarmList);
		}


		/* 添加告警信息 */
		if (!addAlarmList.isEmpty()) {
			boolean r = equipmentAlarmService.saveBatch(addAlarmList);
			//看是否有websocke,有则发送告警信息
			pushRealAlarmData(addAlarmList);

		}

		/* 更新告警信息 */
		if (!updateAlarmList.isEmpty()) {
			equipmentAlarmService.saveOrUpdateBatch(updateAlarmList);
		}
	}

	private void dealEqAlarmData(Alarm alarm, Map<Integer, EquipmentAlarm> alarmMap, StationVO stationVo,
								 List<EquipmentAlarm> addAlarmList, List<EquipmentAlarm> updateAlarmList) {
		Integer alarmId = alarm.getAlarmid();
		String itemId = alarm.getId();//数据项ID
		String etime = alarm.getEtime();
		//获取报警的内容及设备名称
		Map<String, String> alarmInfoMap = getAlarmItemName(alarm);
		String alarmItemName = alarmInfoMap.get("alarmItemName");
		String eqAlarmContent = alarmInfoMap.get("eqAlarmContent");
		AlarmStatus alarmStatus = AlarmStatus.ALARMING;
		if (Func.isEmpty(etime) || Func.equals(etime, "0001-01-01 00:00:00")) {
			alarmStatus = AlarmStatus.ALARMED;
		}

		EquipmentAlarm eqAlarm = alarmMap.get(alarmId);

		//根据数据项ID 查询站点位置
		Long siteId = null;
		DiagramItem diagramItem = iDiagramItemService.getOneDiagramItemByItem(itemId);
		DiagramProduct diagramProduct = new DiagramProduct();
		if (Func.isNotEmpty(diagramItem)) {
			siteId = diagramItem.getSiteId();
			diagramProduct = iDiagramProductService.getById(diagramItem.getDiagramProductId());
		}

		if (Func.isEmpty(diagramItem)) {
			diagramProduct = iDiagramProductService.getOneDiagramProductByRtuidcb(alarm.getId());
			if (Func.isNotEmpty(diagramProduct)) {
				siteId = diagramProduct.getSiteId();

			} else {
				diagramProduct = iDiagramProductService.getOneDiagramProductByDid(alarm.getId());
				if (Func.isNotEmpty(diagramProduct)) {
					siteId = diagramProduct.getSiteId();

				}
			}
		}


		//更新告警
		if (alarmMap.containsKey(alarmId) && eqAlarm != null) {
			if (StringUtils.isNotEmpty(etime)) {
				//有数据则更新告警状态和处理状态
				if (eqAlarm.getAlarmEndTime() != null) {
					EquipmentAlarm upEqAlarm = new EquipmentAlarm();
					bonus(upEqAlarm);
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
			//新增告警
			DiagramItem newdiagramItem = new DiagramItem();
			newdiagramItem.setItemId(itemId);
			newdiagramItem.setStationId(stationVo.getId());

			//DiagramItem dItem=iDiagramItemService.getOne(Condition.getQueryWrapper(newdiagramItem));

			EquipmentAlarm equipmentAlarm = new EquipmentAlarm();
			equipmentAlarm.setAlarmId(alarmId);
			equipmentAlarm.setAlarmItemId(itemId);
			equipmentAlarm.setAlarmContent(eqAlarmContent);
			equipmentAlarm.setStationId(stationVo.getId());
			equipmentAlarm.setAddress(stationVo.getAddress());

			equipmentAlarm.setAlarmType(alarm.getAlarmtype());

			equipmentAlarm.setLevel(alarm.getLevel());


			equipmentAlarm.setSiteId(siteId);

			equipmentAlarm.setTenantId(stationVo.getTenantId());
//				if(Func.isNotEmpty(dItem)){
//					equipmentAlarm.setEquipmentId(dItem.getDiagramProductId());
//					equipmentAlarm.setAlarmType(dItem.getAlarmType());
//				}else{
			if (Func.isNotEmpty(diagramProduct)) {
				equipmentAlarm.setEquipmentId(diagramProduct.getId());
				if (eqAlarmContent.contains(AlarmType.TXZD.value)) {
					equipmentAlarm.setAlarmType(AlarmType.SBGZ.id);
				} else {
					equipmentAlarm.setAlarmType(AlarmType.SBGZ.id);
				}
			}
			//}

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
			bonus(equipmentAlarm);
			addAlarmList.add(equipmentAlarm);


		}
	}

	private Map<String, String> getAlarmItemName(Alarm alarm) {
		String alarmItemName = "";
		String idStr = alarm.getId();
		Float val = alarm.getVal();
		StringBuffer alarmContent = new StringBuffer();

		if (idStr.contains(GwsubscribeConstant.DAT_KEY) || idStr.contains(GwsubscribeConstant.YSYC_KEY)) {
			DeviceItem deviceItem = deviceClient.getDeviceItemInfosByItemid(idStr);
			if (deviceItem != null) {
				alarmItemName = deviceItem.getShortname();
				int stype = deviceItem.getStype().intValue();
				if (ItemStype.TRANSPORTYC.id.equals(stype) || ItemStype.OPERATIONYC.id.equals(stype)) {
					Float uulimit = deviceItem.getUulimit();
					Float ulimit = deviceItem.getUlimit();
					Float llimit = deviceItem.getLlimit();
					Float lllimit = deviceItem.getLllimit();
					Float overLimitVal = 1f;

					if (ulimit != null && val.compareTo(ulimit) > 0) {
						if (uulimit != null && val.compareTo(uulimit) > 0) {
							alarmContent.append(UPUPLIMIT);
							overLimitVal = uulimit;
						} else {
							alarmContent.append(UPLIMIT);
							overLimitVal = ulimit;
						}
					}
					if (llimit != null && val.compareTo(llimit) < 0) {
						if (lllimit != null && val.compareTo(lllimit) < 0) {
							alarmContent.append(LOWLOWLIMIT);
							overLimitVal = lllimit;
						} else {
							alarmContent.append(LOWLIMIT);
							overLimitVal = llimit;
						}
					}
					alarmContent.append(ALARM_CURRENT_VALUE + val);
				} else if (ItemStype.TRANSPORTYX.id.equals(stype) || ItemStype.OPERATIONYX.id.equals(stype)) {
					String curStatus = "", oldStatus = "";
					if (0 == val) {
						curStatus = deviceItem.getDesc0();
						oldStatus = deviceItem.getDesc1();
					} else {
						curStatus = deviceItem.getDesc1();
						oldStatus = deviceItem.getDesc0();
					}
					alarmContent.append(curStatus);
				}
			}
		} else if (idStr.contains("_")) {
			//rtu等级告警
			DeviceSub sub = deviceClient.getDeviceSubsByRtuidcb(idStr);
			if (sub != null) {
				alarmItemName = sub.getRtuname();
			}
			alarmContent.append("（设备）");
			alarmContent.append(COMMUNICATION_INTERRUPTION);
		} else {
			//网关告警
			Device device = deviceClient.getDeviceBygwid(alarm.getGwid());
			if (device != null) {
				alarmItemName = device.getName();
			}
			alarmContent.append("（通讯机）");
			alarmContent.append(COMMUNICATION_INTERRUPTION);
		}

		/* 产生告警推送( [告警状态,告警等级] 站点名称,地址,数据项名称,告警内容) */

		String eqAlarmContent = String.format(ALARM_INFO, alarmItemName, alarmContent.toString());
		Map<String, String> alarmInfoMap = new HashMap<>();
		alarmInfoMap.put("alarmItemName", alarmItemName);
		alarmInfoMap.put("eqAlarmContent", eqAlarmContent);
		return alarmInfoMap;
	}

	public void pushRealAlarmData(List<EquipmentAlarm> addAlarmList) {
		//从redis里面取所有的socket连接数据
		String wkey = WebsocketConstant.websocket_kek;
		Map<Object, Object> redisMap = new HashMap<>(redisCache.hGetAll(wkey));

		//查询连接池里面的channel数据
		Iterator<Channel> iterator = ChannelHandlerPool.channelGroup.iterator();
		//iterator.hasNext()如果存在元素的话返回true
		while (iterator.hasNext()) {
			//iterator.next()返回迭代的下一个元素
			Channel channel = iterator.next();
			Object params = redisMap.get(channel.id().toString());
			if (Func.isNotEmpty(params)) {
				JSONObject param = (JSONObject) JSONObject.toJSON(params);
				//告警实时数据
				if (param.get(WebsocketConstant.METHOD).equals(WebsocketConstant.getRealAlarmData)) {
					Map<String, Object> map = new HashMap<>();
					for (EquipmentAlarm equipmentAlarm : addAlarmList) {
						String key = WebsocketConstant.websocket_station;
						Station station = redisCache.hGet(key, equipmentAlarm.getStationId());
						if (Func.isNotEmpty(station)) {
							map.put("alarmId", equipmentAlarm.getAlarmId());
							EquipmentAlarmRsep equipmentAlarmRsep = equipmentAlarmService.getNewestEquipmentAlarm(map);
							String data = JSON.toJSONString(equipmentAlarmRsep);
							ChannelHandlerPool.channelGroup.writeAndFlush(new TextWebSocketFrame(data), new Matcher(channel));
						}

					}

				}
			}
		}

	}

	public void bonus(EquipmentAlarm alarm) {
		if (Func.isNotEmpty(alarm.getSiteId())) {
			Site site = iSiteService.getOne(new QueryWrapper<Site>().eq("id", alarm.getSiteId()));
			if (Func.isNotEmpty(site))
				alarm.setSiteName(site.getSiteName());
		}
		if (Func.isNotEmpty(alarm.getStationId())) {
			Station station = stationService.getOne(new QueryWrapper<Station>().eq("id", alarm.getStationId()));
			if (Func.isNotEmpty(station))
				alarm.setStationName(station.getName());
		}
	}
}
