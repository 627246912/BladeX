package org.springblade.pms.bigscreen.handler;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springblade.bean.*;
import org.springblade.constants.CoreCommonConstant;
import org.springblade.constants.GwsubscribeConstant;
import org.springblade.constants.XYDdatas;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.utils.Func;
import org.springblade.enums.CommonDateType;
import org.springblade.enums.ProductSid;
import org.springblade.enums.RepairDealStatus;
import org.springblade.enums.ShowType;
import org.springblade.pms.alarmmanagement.entity.EquipmentAlarm;
import org.springblade.pms.alarmmanagement.service.IEquipmentAlarmService;
import org.springblade.pms.bigscreen.dto.DataStaResq;
import org.springblade.pms.bigscreen.dto.ItemResq;
import org.springblade.pms.bigscreen.dto.StationRtuData;
import org.springblade.pms.config.SystemConfig;
import org.springblade.pms.enums.*;
import org.springblade.pms.gw.feign.IDeviceClient;
import org.springblade.pms.gw.feign.IDeviceItemHistoryClient;
import org.springblade.pms.station.entity.BaseStation;
import org.springblade.pms.station.entity.RtuSet;
import org.springblade.pms.station.service.IBaseStationService;
import org.springblade.pms.station.service.IRtuSetService;
import org.springblade.pms.statistics.repository.CurveDataRepository;
import org.springblade.pms.websocket.handler.ChannelHandlerPool;
import org.springblade.pms.websocket.handler.Matcher;
import org.springblade.util.DateUtil;
import org.springblade.util.RedisKeysUtil;
import org.springblade.util.StringUtils;
import org.springframework.beans.BeanUtils;
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
public class BigScreenStationHandler {

	@Autowired
	private ShowTypeAndRealTimeValue showTypeAndRealTimeValue;
	@Autowired
	private IEquipmentAlarmService iEquipmentAlarmService;
	@Autowired
	private IRtuSetService iRtuSetService;

	@Autowired
	private IBaseStationService iBaseStationService;

	@Autowired
	private IDeviceClient iDeviceClient;
	@Autowired
	private IDeviceItemHistoryClient iDeviceItemHistoryClient;

	@Autowired
	private SystemConfig systemConfig;
	@Autowired
	private BladeRedisCache redisCache;

	/**
	 * 大屏端推送数据
	 * @param params
	 * @param channel
	 */
	@Async
	public void pushBigScreenStationData(JSONObject params, Channel channel) {
		String stationId =params.getString("stationId");
		if(Func.isEmpty(stationId)){
			ChannelHandlerPool.channelGroup.writeAndFlush(new TextWebSocketFrame(""), new Matcher(channel));
		}
		String key=BigScreenConstant.BigScreenStationData+stationId;
		String data= redisCache.get(key);
		ChannelHandlerPool.channelGroup.writeAndFlush(new TextWebSocketFrame(data), new Matcher(channel));
		getBigScreenStationData(stationId);
	}


	/**
	 * 根据区域编码获取大屏端数据
	 */
	@Async
	public Map<String,Object> getBigScreenStationData(String stationId){
		if(Func.isEmpty(stationId)){
			return null;
		}
		//根据站点id找站点信息
		BaseStation station = iBaseStationService.getById(stationId);
		String rkey= RedisKeysUtil.getKey(systemConfig.getServerIdCard(), GwsubscribeConstant.dataRefreshTime)+station.getGwId();

		//数据刷新时间
		String dataRefreshTime=redisCache.get(rkey);
		//总电流
		Double Ival=0d;
		//直流总电压
		Double Uval=0d;
		//交流总电压
		Double acUval=0d;
		//设备总温度
		Double temval=0d;

		//总能耗itme
		String item="";
		Map<String,Object> resMap=new HashMap<>();
		//总路rtu
		String rtu1="";


		if(Func.isEmpty(station)){
			return null;
		}
		if(Func.isEmpty(station.getGwId())){
			return null;
		}
		//取出网关id
		String gwId=station.getGwId();
		List<String> gwIds =new ArrayList<>();
		gwIds.add(gwId);

		//redis查询网关状态信息
		DeviceCommunicationStatus deviceStatus = iDeviceClient.getStatussByCode(gwId);
		Integer status=1;
		if(Func.isNotEmpty(deviceStatus)){
			status=deviceStatus.getStatus();
		}


		//根据网关查询已保存的com口
		Map<String,Object> querymap=new HashMap<>();
		querymap.put("gwId",gwId);
		List<RtuSet> gwcomSetlist=iRtuSetService.selectGwcomSetList(querymap);
		List<StationRtuData>  allGwComDatas= new ArrayList<>();
		Set<String>  deviceSubIds= new HashSet<>();

		for(RtuSet gwcomSet:gwcomSetlist) {
			deviceSubIds.add(gwcomSet.getRtuidcb());
			StationRtuData stationGwComData = new StationRtuData();
			BeanUtils.copyProperties(gwcomSet, stationGwComData);
			stationGwComData.setActivateTime(DateUtil.format(gwcomSet.getActivateTime(),DateUtil.TIME_PATTERN_24));
			allGwComDatas.add(stationGwComData);
		}
		Map<String, StationRtuData> allGwComDataMap = new HashMap<>();
		if(Func.isNotEmpty(allGwComDatas)){
			allGwComDataMap =buildStationGwComDataMap(allGwComDatas);
		}
		//跟网关查找redis里面的com口
		List<DeviceSub> gwSubList = iDeviceClient.getDeviceSubsByGwid(gwId);
		//把表里面没有的com放入allGwComDataMap
		for(DeviceSub deviceSub:gwSubList){
			deviceSubIds.add(deviceSub.getRtuidcb());
			StationRtuData stationGwComData=allGwComDataMap.get(deviceSub.getRtuidcb());
			//rtu1为总路不分配给用户
			if(!Func.equals("1",deviceSub.getRtuid()) && Func.isEmpty(stationGwComData)) {
				stationGwComData =new StationRtuData();
				stationGwComData.setGwId(deviceSub.getGwid());
				stationGwComData.setRtuid(deviceSub.getRtuid());
				stationGwComData.setRtuidcb(deviceSub.getRtuidcb());
				stationGwComData.setRtuname(deviceSub.getRtuname());
//				 List<ItemResq> ls=new ArrayList<>();
//				stationGwComData.setItemResqList(ls);
				allGwComDataMap.put(deviceSub.getRtuidcb(),stationGwComData);
			}
			//rtu1为总路 获取电压和电流
			if(Func.equals("1",deviceSub.getRtuid())){
				rtu1=deviceSub.getRtuidcb();
			}
		}
		List<StationRtuData>  returnGwComDatas= new ArrayList<>();
		//如果网关掉线则数据为--
		if(Func.equals(status, OnlineStatus.UNLINE.id)){
			resMap.put("shishishujvjiance",allGwComDatas);
		}else {
			//总路
			List<DeviceItem> rtu1ItemList =getShowItems(rtu1);
			List<String> rtu1itemids = new ArrayList<>();
			for (DeviceItem ditem : rtu1ItemList) {
				rtu1itemids.add(ditem.getId());
			}
			Map<String, DeviceItemRealTimeData> rtu1ItemRealTimeDatas = showTypeAndRealTimeValue.getDeviceItemRealTimeDatas(rtu1itemids);
			for (DeviceItem ditem : rtu1ItemList) {
				//总能耗数据项 306
				if (Func.equals(String.valueOf(ditem.getSid()),ProductSid.SID306.id)) {
					item=ditem.getId();
				}
				//总电流 304
				if (Func.equals(String.valueOf(ditem.getSid()),ProductSid.SID304.id)) {
					DeviceItemRealTimeData data = rtu1ItemRealTimeDatas.get(ditem.getId());
					if (Func.isNotEmpty(data)) {
						Ival=data.getVal();
						//dataRefreshTime=data.getTime();
					}
				}
				//直流总电压 303
				if (Func.equals(String.valueOf(ditem.getSid()),ProductSid.SID303.id)) {
					DeviceItemRealTimeData data = rtu1ItemRealTimeDatas.get(ditem.getId());
					if (Func.isNotEmpty(data)) {
						Uval=data.getVal();
					}
				}
				//交流总电压 302
				if (Func.equals(String.valueOf(ditem.getSid()),ProductSid.SID302.id)) {
					DeviceItemRealTimeData data = rtu1ItemRealTimeDatas.get(ditem.getId());
					if (Func.isNotEmpty(data)) {
						acUval=data.getVal();
					}
				}
				//设备温度
				if (Func.equals(String.valueOf(ditem.getSid()),ProductSid.SID301.id)) {
					DeviceItemRealTimeData data = rtu1ItemRealTimeDatas.get(ditem.getId());
					if (Func.isNotEmpty(data)) {
						temval=data.getVal();
					}
				}
			}
			//各个分路
			for (Map.Entry<String, StationRtuData> entry : allGwComDataMap.entrySet()) {
				StationRtuData stationGwComData = entry.getValue();
				//赋值网关状态
				stationGwComData.setGwStatus(status);
				List<DeviceItem> showItemList =getShowItems(stationGwComData.getRtuidcb());
				List<String> itemids = new ArrayList<>();
				for (DeviceItem ditem : showItemList) {
					itemids.add(ditem.getId());
				}
				Map<String, DeviceItemRealTimeData> deviceItemRealTimeDatas = showTypeAndRealTimeValue.getDeviceItemRealTimeDatas(itemids);

				List<ItemResq> itemResqList = new ArrayList<>();

				for (DeviceItem ditem : showItemList) {
					ItemResq itme = new ItemResq();
					itme.setShowType(String.valueOf(ShowType.SHOW.getId()));
					itme.setItemId(ditem.getId());
					itme.setName(ditem.getName());
					itme.setShortname(ditem.getShortname());
					itme.setUnit(ditem.getUnit());
					itme.setSid(ditem.getSid());
					itme.setStype(ditem.getStype());

					//赋值实时数据
					Double val = 0d;
					DeviceItemRealTimeData deviceItemRealTimeData=new DeviceItemRealTimeData();
					deviceItemRealTimeData = deviceItemRealTimeDatas.get(ditem.getId());
					if(Func.isNotEmpty(deviceItemRealTimeData)) {
						val = deviceItemRealTimeData.getVal();//实时数据
					}
					itme.setRealTimeValue(String.valueOf(val));
					//判断开关
					if (Func.equals(String.valueOf(ditem.getSid()),ProductSid.SID1047.id) && Func.isNotEmpty(deviceItemRealTimeData)) {
						stationGwComData.setSwitchStatus(deviceItemRealTimeData.getVal().intValue());
						itme.setShowType(String.valueOf(ShowType.SWITCH.getId()));
					}

					itemResqList.add(itme);
				}

				stationGwComData.setItemResqList(itemResqList);
				returnGwComDatas.add(stationGwComData);
			}

			resMap.put("shishishujvjiance",returnGwComDatas);
		}
		resMap.put("dataRefreshTime",dataRefreshTime);
		//获取站点运行数据
		resMap.put("yunxingcanshu",yunxingcanshu(returnGwComDatas,station,status,Ival,Uval,acUval,temval));
		resMap.put("shujutongji",getShujutongji(rtu1, gwcomSetlist, Ival,item));



		//放入redis
		String key=BigScreenConstant.BigScreenStationData+stationId;
		String data= JSON.toJSONString(resMap);
		redisCache.set(key,data);
		return resMap;
	}
	public List<DeviceItem>  getShowItems(String Rtuidcb){
		//根据rtuidcb查找数据项
		List<DeviceItem> gwItemList = iDeviceClient.getDeviceItemInfosByRtuidcb(Rtuidcb);
		List<String> itemids = new ArrayList<>();
		List<DeviceItem> showItemList =new ArrayList<>();
		for (DeviceItem ditem : gwItemList) {
			if(Func.isNotEmpty(PmsItemShowSid.getProductSid(String.valueOf(ditem.getSid())))){
				itemids.add(ditem.getId());
				showItemList.add(ditem);
			}
		}
		return showItemList;
	}
	/**
	 * 获取站点运行数据
	 * @param station
	 * @param status
	 * @return
	 */
	public Map<String,Object> yunxingcanshu(List<StationRtuData> GwComDatas,BaseStation station,Integer status,Double Ival,Double Uval,Double acUval,Double temval){
		Map<String,Object> yunxingcanshu=new HashMap<>();

		//当日告警
		Date stime= DateUtil.getStartDate(new Date());
		Date etime= DateUtil.getEndDate(new Date());
		Map<String,Integer> alarmMap =handlealarmNum(station.getId(),stime,etime);
		Integer alarmNum=alarmMap.get("alarmNum");
		//掉线
		if(Func.equals(status, OnlineStatus.UNLINE.id)) {
			yunxingcanshu.put("status", PmsStatus.OFFLINE.id);//状态
			yunxingcanshu.put("Ua", "--");//直流电压
			yunxingcanshu.put("Ia", "--");//电流
			yunxingcanshu.put("acUval", "--");//交流电压
			yunxingcanshu.put("temval", "--");//设备温度
			yunxingcanshu.put("alarmNum", alarmNum);//当日告警
			yunxingcanshu.put("runDay", 0);//安全运行天数
		}else {
			//系统运行参数
			Integer pmsstatus=PmsStatus.ONLINE.id;
			for(StationRtuData rtuData:GwComDatas){
				List<ItemResq> list= rtuData.getItemResqList();
				for(ItemResq itemResq:list){
					if(Func.isNotEmpty(AlarmItemSid.getValue(String.valueOf(itemResq.getSid())))){
						if (Func.equals(String.valueOf(itemResq.getRealTimeValue()),"1")) {
							pmsstatus = PmsStatus.SERIOUS.id;
							continue;
						}
					}
				}
				if(Func.equals(pmsstatus,PmsStatus.SERIOUS.id)){
					continue;
				}
			}
			yunxingcanshu.put("status", pmsstatus);//系统状态
			yunxingcanshu.put("Ua", Uval);//直流电压
			yunxingcanshu.put("Ia", Ival);//电流
			yunxingcanshu.put("acUval", acUval);//交流电压
			yunxingcanshu.put("temval", temval);//设备温度
			yunxingcanshu.put("alarmNum", alarmNum);//当日告警
			yunxingcanshu.put("runDay", runDay(station));//安全运行天数
		}

		return yunxingcanshu;
	}

	//数据统计
	public List<DataStaResq> getShujutongji(String rtu1,List<RtuSet> gwcomSetlist,Double Ival,String item){

		Map<String,List<RtuSet>> userRtuMap=new HashMap<>();
		Map<String,DataStaResq> userMap=new HashMap<>();
		UserGroup[] itemBtypes = UserGroup.values();//用户组
		for (UserGroup itemBtype : itemBtypes) {
			List<RtuSet> rtulist =new ArrayList<>();
			DataStaResq usersta=new DataStaResq();
			usersta.setIval(Ival);
			usersta.setUserId(itemBtype.getId());
			usersta.setUserName(itemBtype.value);
			userMap.put(itemBtype.getId(),usersta);
			for(RtuSet rtu:gwcomSetlist){
				if(Func.equals(itemBtype.getId(),rtu.getUserGroup())){
					rtulist.add(rtu);
				}
			}
			userRtuMap.put(itemBtype.getId(),rtulist);
		}

		return getTongji(rtu1,userRtuMap,userMap, item);
	}
	public List<DataStaResq> getTongji(String rtu1,Map<String,List<RtuSet>> userRtuMap,Map<String,DataStaResq> userMap,String item) {

		//Float energyval= getEnergyval(item);//总能耗
		Float energyval=0f;
		List<DataStaResq> listdata=new ArrayList<>();
		for(Map.Entry<String ,DataStaResq> userData:userMap.entrySet()) {//循环用户组
			DataStaResq dataStaResq = userData.getValue();
			//dataStaResq.setEval(energyval);
			String userId = userData.getKey();
			List<RtuSet> rtuSets = userRtuMap.get(userId);
			List<String> rtridcbList = new ArrayList<>();
			List<String> rtridList = new ArrayList<>();
			List<String> protList = new ArrayList<>();
			for (RtuSet rtu : rtuSets) {
				rtridcbList.add(rtu.getRtuidcb());
				rtridList.add(rtu.getRtuid());
				protList.add(String.valueOf(rtu.getPort()));
			}
			String rtrids = CollectionUtil.join(rtridList, CoreCommonConstant.SPLIT_COMMA_KEY);//端口
			String ports = CollectionUtil.join(protList, CoreCommonConstant.SPLIT_COMMA_KEY);//端口
			dataStaResq.setRtuids(rtrids);//用户端口号
			dataStaResq.setPorts(ports);//用户端口号
			//根据rtuidcb查找数据项
			List<DeviceItem> gwItemList =new ArrayList<>();
			if(Func.isNotEmpty(rtridcbList)){
				gwItemList = iDeviceClient.getDeviceItemInfosByRtuidcb(rtu1);

			}
			List<String> itemids = new ArrayList<>();
			Map<String, DeviceItem> DeviceItemMap = new HashMap<>();
			for (DeviceItem ditem : gwItemList) {
				itemids.add(ditem.getId());
				DeviceItemMap.put(String.valueOf( ditem.getSid()), ditem);
			}
			Map<String, DeviceItemRealTimeData> deviceItemRealTimeDatas=new HashMap<>();
			if(Func.isNotEmpty(itemids)) {
				deviceItemRealTimeDatas = showTypeAndRealTimeValue.getDeviceItemRealTimeDatas(itemids);
			}

			//用户1
			if (Func.equals(userId, UserGroup.YIDONG.id) && Func.isNotEmpty(rtrids)) {
				//用户1总电流 307
				DeviceItem	UaDeviceItem = DeviceItemMap.get(ProductSid.SID307.id);
				if (Func.isNotEmpty(UaDeviceItem)) {
					DeviceItemRealTimeData data = deviceItemRealTimeDatas.get(UaDeviceItem.getId());
					if (Func.isNotEmpty(data)) {
						dataStaResq.setUserIval(data.getVal());
					}
				}
				//用户1 当月能耗309
				UaDeviceItem = DeviceItemMap.get(ProductSid.SID309.id);
				if (Func.isNotEmpty(UaDeviceItem)) {
					dataStaResq.setUserEval(getEnergyval(UaDeviceItem.getId()));
					energyval=energyval+dataStaResq.getUserEval();

				}
			}
			//用户2总电流 310
			if (Func.equals(userId, UserGroup.LIANTONG.id)  && Func.isNotEmpty(rtrids)) {
				DeviceItem UaDeviceItem = DeviceItemMap.get(ProductSid.SID310.id);
				if (Func.isNotEmpty(UaDeviceItem)) {
					DeviceItemRealTimeData data = deviceItemRealTimeDatas.get(UaDeviceItem.getId());
					if (Func.isNotEmpty(data)) {
						dataStaResq.setUserIval(data.getVal());
					}
				}
				//用户2 当月能耗312
				UaDeviceItem = DeviceItemMap.get(ProductSid.SID312.id);
				if (Func.isNotEmpty(UaDeviceItem)) {
					dataStaResq.setUserEval(getEnergyval(UaDeviceItem.getId()));
					energyval=energyval+dataStaResq.getUserEval();
				}
			}

			//用户3总电流 313
			if (Func.equals(userId, UserGroup.DIANXIN.id)  && Func.isNotEmpty(rtrids)) {
				DeviceItem UaDeviceItem = DeviceItemMap.get(ProductSid.SID313.id);
				if (Func.isNotEmpty(UaDeviceItem)) {
					DeviceItemRealTimeData data = deviceItemRealTimeDatas.get(UaDeviceItem.getId());
					if (Func.isNotEmpty(data)) {
						dataStaResq.setUserIval(data.getVal());
					}
				}
				//用户3 当月能耗315
				UaDeviceItem = DeviceItemMap.get(ProductSid.SID315.id);
				if (Func.isNotEmpty(UaDeviceItem)) {
					dataStaResq.setUserEval(getEnergyval(UaDeviceItem.getId()));
					energyval=energyval+dataStaResq.getUserEval();
				}
			}
			//用户4总电流 316
			if (Func.equals(userId, UserGroup.TIETA.id)  && Func.isNotEmpty(rtrids)) {
				DeviceItem UaDeviceItem = DeviceItemMap.get(ProductSid.SID316.id);
				if (Func.isNotEmpty(UaDeviceItem)) {
					DeviceItemRealTimeData data = deviceItemRealTimeDatas.get(UaDeviceItem.getId());
					if (Func.isNotEmpty(data)) {
						dataStaResq.setUserIval(data.getVal());
					}
				}
				//用户4 当月能耗318
				UaDeviceItem = DeviceItemMap.get(ProductSid.SID318.id);
				if (Func.isNotEmpty(UaDeviceItem)) {
					dataStaResq.setUserEval(getEnergyval(UaDeviceItem.getId()));
					energyval=energyval+dataStaResq.getUserEval();
				}
			}
			//用户5总电流 319
			if (Func.equals(userId, UserGroup.GUANGDIAN.id)  && Func.isNotEmpty(rtrids)) {
				DeviceItem UaDeviceItem = DeviceItemMap.get(ProductSid.SID319.id);
				if (Func.isNotEmpty(UaDeviceItem)) {
					DeviceItemRealTimeData data = deviceItemRealTimeDatas.get(UaDeviceItem.getId());
					if (Func.isNotEmpty(data)) {
						dataStaResq.setUserIval(data.getVal());
					}
				}
				//用户5 当月能耗321
				UaDeviceItem = DeviceItemMap.get(ProductSid.SID321.id);
				if (Func.isNotEmpty(UaDeviceItem)) {
					dataStaResq.setUserEval(getEnergyval(UaDeviceItem.getId()));
					energyval=energyval+dataStaResq.getUserEval();
				}
			}
			listdata.add(dataStaResq);
		}
		for(DataStaResq dataStaResq:listdata){
			dataStaResq.setEval(energyval);
		}

	return listdata;

	}
	public float getEnergyval(String temid){
		//能耗
		//到数据中心查询数据
		String stime=DateUtil.format(DateUtil.getMonthStartDate(new Date()),DateUtil.TIME_PATTERN_24);
		String etime=DateUtil.format(DateUtil.getMonthEndDate(new Date()),DateUtil.TIME_PATTERN_24);
		List<DeviceItemHistoryDiffData> deviceItemHistoryDiffDatas = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
			temid, stime, etime, CommonDateType.YEAR.getId(),1 );
		Map<String, Float>  resMap =CurveDataRepository.itemsSumValue(deviceItemHistoryDiffDatas);
		float subval=resMap.get(XYDdatas.YSubVals);
		return subval;
	}


	public Map<String, StationRtuData> buildStationGwComDataMap(List<StationRtuData> deviceItems) {
		if (Func.isEmpty(deviceItems)) {
			return null;
		} else {
			Map<String, StationRtuData> idItemMap = new HashMap();
			for(StationRtuData deviceItem:deviceItems){
				idItemMap.put(deviceItem.getRtuidcb(), deviceItem);
			}
			return idItemMap;
		}
	}


	/**
	 *统计异常告警个数
	 */
	public Map<String,Integer> handlealarmNum(Long stationId,Date startTime,Date endTime) {
		Map<String,Integer> rmap=new HashMap<>();
		Integer leve=0;
		Map<String,Object> queryMap=new HashMap<>();
		queryMap.put("stationId",stationId);
		queryMap.put("startTime",startTime);
		queryMap.put("endTime",endTime);
		queryMap.put("handleStatus", RepairDealStatus.REPAIRED.id);
		List<EquipmentAlarm> list= iEquipmentAlarmService.selectAlarmsByMap(queryMap);
		if(Func.isNotEmpty(list)){
			rmap.put("alarmNum",list.size());
			for(EquipmentAlarm equipmentAlarm :list){
				leve=Func.equals(leve.compareTo(equipmentAlarm.getLevel()),1) ? leve:equipmentAlarm.getLevel();
			}
			rmap.put("alarmLeve",leve);
		}else{
			rmap.put("alarmNum",0);
			rmap.put("alarmLeve",null);
		}


	return  rmap;

	}


	/**
	 *安全运行天数
	 */
	public Integer runDay(BaseStation station) {

		Integer runDay = 0;
		Date stime = station.getCreateTime();
		if (Func.isEmpty(station) && Func.isEmpty(stime)) {
			return runDay;
		}
		Date alarmTime = iEquipmentAlarmService.selectLastAlarmEndTimeByStationId(station.getId());
		if (StringUtils.isNotNull(alarmTime)) {
			stime = alarmTime;
		}
		runDay = (int) DateUtil.dayDiff(new Date(), stime);

		return runDay;
	}

}
