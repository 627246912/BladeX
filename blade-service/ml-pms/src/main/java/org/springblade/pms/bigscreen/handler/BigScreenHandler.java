package org.springblade.pms.bigscreen.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springblade.bean.DeviceCommunicationStatus;
import org.springblade.bean.DeviceItem;
import org.springblade.bean.DeviceItemHistoryDiffData;
import org.springblade.constants.GwsubscribeConstant;
import org.springblade.constants.XYDdatas;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.utils.Func;
import org.springblade.dto.NameValue;
import org.springblade.enums.CommonDateType;
import org.springblade.enums.ProductSid;
import org.springblade.pms.alarmmanagement.dto.EquipmentAlarmRsep;
import org.springblade.pms.alarmmanagement.service.IEquipmentAlarmService;
import org.springblade.pms.bigscreen.dto.StationAlarmDataDto;
import org.springblade.pms.config.SystemConfig;
import org.springblade.pms.enums.OnlineStatus;
import org.springblade.pms.enums.PmsStatus;
import org.springblade.pms.enums.UserGroup;
import org.springblade.pms.gw.feign.IDeviceClient;
import org.springblade.pms.gw.feign.IDeviceItemHistoryClient;
import org.springblade.pms.station.entity.BaseStation;
import org.springblade.pms.station.entity.RtuSet;
import org.springblade.pms.station.service.IBaseStationService;
import org.springblade.pms.station.service.IRtuSetService;
import org.springblade.pms.station.service.ISysAreaService;
import org.springblade.pms.statistics.repository.CurveDataRepository;
import org.springblade.pms.websocket.handler.ChannelHandlerPool;
import org.springblade.pms.websocket.handler.Matcher;
import org.springblade.util.BigDecimalUtil;
import org.springblade.util.DateUtil;
import org.springblade.util.RedisKeysUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author bond
 * @date 2020/8/1 15:06
 * @desc
 */
@Component
public class BigScreenHandler {

	@Autowired
	private SystemConfig systemConfig;
	@Autowired
	private BladeRedisCache redisCache;

	@Autowired
	private ISysAreaService iSysAreaService;
	@Autowired
	private IRtuSetService iGwcomSetService;

	@Autowired
	private IBaseStationService iBaseStationService;
	@Autowired
	private IDeviceClient iDeviceClient;
	@Autowired
	private IEquipmentAlarmService iEquipmentAlarmService;
	@Autowired
	private IDeviceItemHistoryClient iDeviceItemHistoryClient;

	/**
	 * 大屏端推送数据
	 * @param params
	 * @param channel
	 */
	@Async
	public void pushBigScreenData(JSONObject params, Channel channel) {
		String areaCode =params.getString("areaCode");
		String key=BigScreenConstant.BigScreenData+areaCode;
		if(Func.isEmpty(areaCode)){
			key=BigScreenConstant.BigScreenData+"0";
		}
		String data= redisCache.get(key);
		//String data= JSON.toJSONString(resMap);
		ChannelHandlerPool.channelGroup.writeAndFlush(new TextWebSocketFrame(data), new Matcher(channel));
		getBigScreenData(areaCode);
	}


	/**
	 * 根据区域编码获取大屏端数据
	 */
	@Async
	public Map<String,Object> getBigScreenData(String areaCode){
		if(Func.isEmpty(areaCode)){
			areaCode=null;
		}
		Map<String,Object> resMap=new HashMap<>();
		//站点列表
		List<BaseStation> stationList = iBaseStationService.selectStationsByAreaCode(areaCode);

		if(Func.isEmpty(stationList)){
			return resMap;
		}

//		List<SysArea>  sysAreaList= iSysAreaService.getChildAreaList(areaCode);
//		Set<String> areaCodes=new HashSet<>();
//		for(SysArea area:sysAreaList){
//			areaCodes.add(area.getAreaCode());
//		}

		//站点数据统计，在线，离线，站点总数，在线率
		Map<String,Object> zhandianshujvtongji =getStationCount(stationList);
		resMap.put("zhandianshujvtongji",zhandianshujvtongji);

		//用户站点数
		Map<String,Object> userStation =yonghuzhandian(stationList);
		resMap.put("userStation",userStation);
		//监测分路数
		Map<String,Object> jiancefenlushu =jiancefenlushu(stationList);
		resMap.put("jiancefenlushu",jiancefenlushu);

		//电量异常分布
		Map<String,Object> powerEx =yongdianyichang(stationList);
		resMap.put("powerEx",powerEx);
		// 告警数据
		Map<String,Object> alarm= iEquipmentAlarmService.getBigScreenAlarmData(areaCode);
		resMap.putAll(alarm);

		//站点在线状态
		Map<String,Object> zhandianzhuangtai =zhandianzhuangtai(stationList);
		resMap.put("zhandianzhuangtai",zhandianzhuangtai);




		//放入redis
		String key=BigScreenConstant.BigScreenData+areaCode;
		if(Func.isEmpty(areaCode)){
			key=BigScreenConstant.BigScreenData+"0";
		}
		String data= JSON.toJSONString(resMap);
		redisCache.set(key,data);

		return resMap;
	}

public Map<String,Object> yongdianyichang(List<BaseStation> stationList){

	Map<String, Integer> userPortEx = new HashMap<>();

	UserGroup[] users = UserGroup.values();//用户组
	for (UserGroup user : users) {
		userPortEx.put(user.getId(),0);
	}


	Map<String,Object> yongdianyichang = new HashMap<>();
	int portCount=0;
	String key= RedisKeysUtil.getKey(systemConfig.getServerIdCard(), GwsubscribeConstant.DEVICE_CODE_TO_STATUS);
	int portEnergyEx = 0;
	if (Func.isNotEmpty(stationList)) {
		for (BaseStation station : stationList) {
			Map<String,Object> map=new HashMap<>();
			map.put("gw_id",station.getGwId());
			map.put("status",OnlineStatus.ONLINE.id);
			List<RtuSet> rtuSetList=iGwcomSetService.selectGwcomSetList(map);
			if(Func.isNotEmpty(rtuSetList)){
				portCount+=rtuSetList.size();
				duankouyongdianyichang(rtuSetList,portEnergyEx,userPortEx);
			}
		}
	}
	//端口用电异常
	yongdianyichang.put("portExCount",portEnergyEx);
	yongdianyichang.put("portCount",portCount);
	yongdianyichang.put("userPortEx",userPortEx);

	return yongdianyichang;

}
	public Map<String,Object> zhandianzhuangtai(List<BaseStation> stationList) {
		Map<String,Object> zhandianzhuangtai = new HashMap<>();
		String key= RedisKeysUtil.getKey(systemConfig.getServerIdCard(), GwsubscribeConstant.DEVICE_CODE_TO_STATUS);
		if (Func.isNotEmpty(stationList)) {
			for (BaseStation station : stationList) {

				StationAlarmDataDto  entity= new StationAlarmDataDto();
				entity.setStationId(station.getId().toString());
				entity.setStationName(station.getStationName());
				entity.setAddress(station.getAddress());
				entity.setStatus(PmsStatus.OFFLINE.id);
				entity.setEquipmentStatusName(OnlineStatus.UNLINE.value);
				//1.查询是否在线
				DeviceCommunicationStatus sta = redisCache.hGet(key, station.getGwId());
				if (Func.isNotEmpty(sta) && Func.equals(OnlineStatus.ONLINE.id, sta.getStatus())) {
					entity.setStatus(PmsStatus.ONLINE.id);
					entity.setEquipmentStatusName(OnlineStatus.ONLINE.value);
				}
				//2.查询是否告警
				Map<String, Object> map =new HashMap<>();
				map.put("stationId",station.getId());
				EquipmentAlarmRsep alarm=iEquipmentAlarmService.getNewestEquipmentAlarm(map);
				if(Func.isNotEmpty(alarm)){
					entity.setStatus(alarm.getLevel());
					entity.setAlarmContent(alarm.getAlarmContent());
					entity.setAlarmTime(alarm.getAlarmTime());
				}
				zhandianzhuangtai.put(String.valueOf(station.getId()),entity);
			}
		}
		return zhandianzhuangtai;
	}
	/**
	 * 根据区域编码获取站点统计
	 */
	public Map<String,Object> getStationCount(List<BaseStation> stationList) {
		Map<String,Object> zhandianshujvtongji = new HashMap<>();



		String key= RedisKeysUtil.getKey(systemConfig.getServerIdCard(), GwsubscribeConstant.DEVICE_CODE_TO_STATUS);
		int onlineval = 0;
		if (Func.isNotEmpty(stationList)) {
			for (BaseStation station : stationList) {
				DeviceCommunicationStatus sta = redisCache.hGet(key, station.getGwId());
				if (Func.isNotEmpty(sta) && Func.equals(OnlineStatus.ONLINE.id, sta.getStatus())) {
					onlineval++;
				}
				Map<String,Object> map=new HashMap<>();
				map.put("gw_id",station.getGwId());
				map.put("status",OnlineStatus.ONLINE.id);
			}
		}

		//在线
		zhandianshujvtongji.put("online",onlineval);
		//离线
		int unlineval=0;
		unlineval=stationList.size() - onlineval;
		zhandianshujvtongji.put("unline",unlineval);
		//站点总数
		int stationval=stationList.size();
		zhandianshujvtongji.put("stationCount",stationval);

		//在线率
		NameValue onlineRate = new NameValue();
		onlineRate.setValue(String.valueOf(BigDecimalUtil.mul(BigDecimalUtil.div(onlineval,stationval,4),100)+"%"));
		zhandianshujvtongji.put("onlineRate",String.valueOf(BigDecimalUtil.mul(BigDecimalUtil.div(onlineval,stationval,4),100)+"%"));
		return zhandianshujvtongji;
	}

	public void duankouyongdianyichang(List<RtuSet> rtuSetList,int ex,Map<String,Integer> userPortEx){
		List<String> rtridcbList =new ArrayList<>();
		for (RtuSet rtu : rtuSetList) {
			rtridcbList.add(rtu.getRtuidcb());
		}

		//根据rtuidcb查找数据项
		List<DeviceItem> gwItemList =new ArrayList<>();
		if(Func.isNotEmpty(rtridcbList)){
			gwItemList = iDeviceClient.getDeviceItemInfosByRtuidcbs(rtridcbList);

		}
		Map<String, DeviceItem> DeviceItemMap = new HashMap<>();
		if(Func.isEmpty(gwItemList)){
			return;
		}
		for (DeviceItem ditem : gwItemList) {
			DeviceItemMap.put(String.valueOf( ditem.getSid()), ditem);
		}

		for (RtuSet rtu : rtuSetList) {
			//用户1
			if (Func.equals(rtu.getUserGroup(), UserGroup.YIDONG.id)) {
				//用户1 当月能耗309
				DeviceItem UaDeviceItem = DeviceItemMap.get(ProductSid.SID309.id);
				if (Func.isNotEmpty(UaDeviceItem)) {
					float subval=getEnergyval(UaDeviceItem.getId());
					if(subval>rtu.getDayMaxPower()){
						ex+=1;
						userPortEx.put(rtu.getUserGroup(),userPortEx.get(rtu.getUserGroup())+1);
					}
				}
			}
			//用户2
			if (Func.equals(rtu.getUserGroup(), UserGroup.LIANTONG.id)) {
				//用户2 当月能耗312
				DeviceItem UaDeviceItem = DeviceItemMap.get(ProductSid.SID312.id);
				if (Func.isNotEmpty(UaDeviceItem)) {
					float subval=getEnergyval(UaDeviceItem.getId());
					if(subval>rtu.getDayMaxPower()){
						ex+=1;
						userPortEx.put(rtu.getUserGroup(),userPortEx.get(rtu.getUserGroup())+1);
					}
				}
			}

			//用户3
			if (Func.equals(rtu.getUserGroup(), UserGroup.DIANXIN.id)) {
				//用户3 当月能耗315
				DeviceItem UaDeviceItem = DeviceItemMap.get(ProductSid.SID315.id);
				if (Func.isNotEmpty(UaDeviceItem)) {
					float subval=getEnergyval(UaDeviceItem.getId());
					if(subval>rtu.getDayMaxPower()){
						ex+=1;
						userPortEx.put(rtu.getUserGroup(),userPortEx.get(rtu.getUserGroup())+1);
					}
				}
			}
			//用户4
			if (Func.equals(rtu.getUserGroup(), UserGroup.TIETA.id)) {
				//用户4 当月能耗318
				DeviceItem UaDeviceItem = DeviceItemMap.get(ProductSid.SID318.id);
				if (Func.isNotEmpty(UaDeviceItem)) {
					float subval=getEnergyval(UaDeviceItem.getId());
					if(subval>rtu.getDayMaxPower()){
						ex+=1;
						userPortEx.put(rtu.getUserGroup(),userPortEx.get(rtu.getUserGroup())+1);
					}
				}
			}
			//用户5
			if (Func.equals(rtu.getUserGroup(), UserGroup.GUANGDIAN.id)) {
				//用户5 当月能耗321
				DeviceItem UaDeviceItem = DeviceItemMap.get(ProductSid.SID321.id);
				if (Func.isNotEmpty(UaDeviceItem)) {
					float subval=getEnergyval(UaDeviceItem.getId());
					if(subval>rtu.getDayMaxPower()){
						ex+=1;
						userPortEx.put(rtu.getUserGroup(),userPortEx.get(rtu.getUserGroup())+1);
					}
				}
			}
		}

	}

	public float getEnergyval(String temid){
		//能耗
		//到数据中心查询数据
		String stime= DateUtil.format(DateUtil.getStartDate(new Date()),DateUtil.TIME_PATTERN_24);
		String etime=DateUtil.format(DateUtil.getEndDate(new Date()),DateUtil.TIME_PATTERN_24);
		List<DeviceItemHistoryDiffData> deviceItemHistoryDiffDatas = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
			temid, stime, etime, 1, CommonDateType.YEAR.getId());
		Map<String, Float>  resMap = CurveDataRepository.itemsSumValue(deviceItemHistoryDiffDatas);
		float subval=resMap.get(XYDdatas.YSubVals);
		return subval;
	}

	public Map<String,Object> jiancefenlushu(List<BaseStation> stationList) {
		Map<String,Object> jiancefenlushu = new HashMap<>();

		Set<String> gwIds= new HashSet<>();
		for (BaseStation station : stationList) {
			gwIds.add(station.getGwId());
		}

		//监测分路
		List<NameValue>  list= iGwcomSetService.selectGwcomUserGroupCount(gwIds);
		for(NameValue dto:list){
			if(Func.isNotEmpty(dto.getCode())) {
				jiancefenlushu.put(dto.getCode(), dto.getValue());
			}
		}
	return jiancefenlushu;
	}
	public Map<String,Object> yonghuzhandian(List<BaseStation> stationList) {
		Map<String,Object> jiancefenlushu = new HashMap<>();

		Set<String> gwIds= new HashSet<>();
		for (BaseStation station : stationList) {
			gwIds.add(station.getGwId());
		}

		//用户站点
		List<NameValue>  list= iGwcomSetService.selectStationUserGroupCount(gwIds);
		for(NameValue dto:list){
			if(Func.isNotEmpty(dto.getCode())) {
				jiancefenlushu.put(dto.getCode(), dto.getValue());
			}
		}
		return jiancefenlushu;
	}


}
