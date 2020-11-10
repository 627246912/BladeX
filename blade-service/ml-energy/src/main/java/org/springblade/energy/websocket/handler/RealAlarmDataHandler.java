package org.springblade.energy.websocket.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springblade.constants.WebsocketConstant;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.energy.alarmmanagement.dto.EquipmentAlarmRsep;
import org.springblade.energy.alarmmanagement.service.IEquipmentAlarmService;
import org.springblade.energy.runningmanagement.station.entity.Station;
import org.springblade.energy.runningmanagement.station.service.IStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bond
 * @date 2020/7/18 10:45
 * @desc
 */
@Component
public class RealAlarmDataHandler {

	@Autowired
	private IEquipmentAlarmService iEquipmentAlarmService;

	@Autowired
	private IStationService iStationService;

	@Autowired
	private BladeRedisCache redisCache;
	/**
	 * 站点未处理告警数据
	 * @param params
	 * @param channel
	 */
	public void pushStationAlarmData(JSONObject params, Channel channel){
		List<EquipmentAlarmRsep> equipmentAlarmRseps =iEquipmentAlarmService.getStationAlarmData(null);
		List<Station> stations= iStationService.list();
		for(Station station:stations){
			//站点放入 放入redis
			Map<Object,Object> map = new HashMap<>();
			map.put(station.getId(),station);
			String key= WebsocketConstant.websocket_station;
			redisCache.hMset(key,map);
			redisCache.expire(key, WebsocketConstant.EXPIRE_TIME);
		}
		for(EquipmentAlarmRsep equipmentAlarmRsep:equipmentAlarmRseps){
			//equipmentAlarmRsep.s
		}
		String data= JSON.toJSONString(equipmentAlarmRseps);
		ChannelHandlerPool.channelGroup.writeAndFlush(new TextWebSocketFrame(data), new Matcher(channel));
		}
}
