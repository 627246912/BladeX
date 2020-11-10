package org.springblade.energy.bigscreen.handler;

import cn.hutool.core.collection.CollectionUtil;
import org.springblade.bean.DeviceItemHistoryDiffData;
import org.springblade.constants.BigScreenRedis;
import org.springblade.constants.CoreCommonConstant;
import org.springblade.constants.XYDdatas;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.alarmmanagement.dto.EquipmentAlarmRsep;
import org.springblade.energy.alarmmanagement.entity.EquipmentAlarm;
import org.springblade.energy.alarmmanagement.service.IEquipmentAlarmService;
import org.springblade.energy.bigscreen.dto.BigSrceenData;
import org.springblade.energy.bigscreen.dto.BigSrceenHandleAlarmData;
import org.springblade.energy.bigscreen.dto.Nenghaogongshi;
import org.springblade.energy.diagram.dto.DiagramItemDTO;
import org.springblade.energy.diagram.service.IDiagramItemService;
import org.springblade.energy.runningmanagement.station.entity.Site;
import org.springblade.energy.runningmanagement.station.entity.Station;
import org.springblade.energy.runningmanagement.station.service.ISiteService;
import org.springblade.energy.runningmanagement.station.service.IStationService;
import org.springblade.energy.statistics.controller.CurveDataFactory;
import org.springblade.energy.statistics.dto.CurveDataInfo;
import org.springblade.energy.statistics.dto.CurveDataReq;
import org.springblade.energy.statistics.repository.CurveDataRepository;
import org.springblade.enums.*;
import org.springblade.gw.feign.IDeviceItemHistoryClient;
import org.springblade.util.DateUtil;
import org.springblade.util.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author bond
 * @date 2020/8/1 15:06
 * @desc
 */
@Component
public class BigScreenRedisComTrue {


	@Autowired
	private IStationService iStationService;
	@Autowired
	private ISiteService iSiteService;
	@Autowired
	private IEquipmentAlarmService iEquipmentAlarmService;
	@Autowired
	private IDiagramItemService iDiagramItemService;
	@Autowired
	private IDeviceItemHistoryClient iDeviceItemHistoryClient;
	@Autowired
	private CurveDataRepository curveDataRepository;
	@Autowired
	private BladeRedisCache redisCache;


	public List<Station> queryStation(){
		return iStationService.list();
	}

	/**
	 *位置个数
	 */
	public Integer siteNum(Long stationId) {
		Map<String,Object> queryMap=new HashMap<>();
		queryMap.put("station_id",stationId);
		List<Site> lsit= iSiteService.listByMap(queryMap);
		if(Func.isNotEmpty(lsit)){
			return lsit.size();
		}
		return 0;
	}

	/**
	 *未处理告警个数
	 */
	public Integer alarmNum(Long stationId) {
		List<EquipmentAlarmRsep> lsit= iEquipmentAlarmService.getStationAlarmData(stationId);
		if(Func.isNotEmpty(lsit)){
			return lsit.size();
		}
		return 0;
	}


	/**
	 *统计异常告警个数
	 */
	public Integer handlealarmNum(Long stationId,Date startTime,Date endTime) {
		Map<String,Object> queryMap=new HashMap<>();
		queryMap.put("stationId",stationId);
		queryMap.put("startTime",startTime);
		queryMap.put("endTime",endTime);
		queryMap.put("handleStatus", RepairDealStatus.REPAIRED.id);
		List<EquipmentAlarm> lsit= iEquipmentAlarmService.selectAlarmsByMap(queryMap);
		if(Func.isNotEmpty(lsit)){
			return lsit.size();
		}
		return 0;
	}

	/**
	 *安全运行天数
	 */
	public Integer runDay(Long stationId) {

		Integer runDay = 0;
		Station station = iStationService.getById(stationId);
		Date stime = station.getCreateTime();
		if (Func.isEmpty(station)) {
			return runDay;
		}
		Map<String,Object> queryMap=new HashMap<>();
		queryMap.put("stationId",stationId);
		queryMap.put("btype", ItemBtype.TRIPPING.id);
		List<DiagramItemDTO> diagramItemList=iDiagramItemService.getItem(queryMap);

		if(Func.isEmpty(diagramItemList)){
			return (int) DateUtil.dayDiff(new Date(), stime);
		}
		List<String> items=new ArrayList<>();
		for(DiagramItemDTO item:diagramItemList){
			items.add(item.getItemId());
		}

		Date alarmTime = iEquipmentAlarmService.selectLastAlarmEndTimeByItemIds(items);
		if (StringUtils.isNotNull(alarmTime)) {
			stime = alarmTime;
		}
		runDay = (int) DateUtil.dayDiff(new Date(), stime);

		return runDay;
	}

	/**
	 * 电能能耗公示
	 * @param stationId
	 * @return
	 */
	public List<Nenghaogongshi> nenghaogongshi(Long stationId) {
		CurveDataReq curveDataReq = new CurveDataReq();
		curveDataReq.setTime(new Date());
		curveDataReq.setDateType(CommonDateType.MONTH.getId());
		CurveDataInfo curveDataInfo = CurveDataFactory.getCurveDataInfo(curveDataReq,true);

		List<Nenghaogongshi> nenghaogongshiList=new ArrayList<>();

		Map<String,Object> querySite=new HashMap<>();
		querySite.put("station_id",stationId);
		List<Site> siteList=iSiteService.listByMap(querySite);
		for(Site site:siteList){
			Nenghaogongshi nenghaogongshi=new Nenghaogongshi();
			Map<String,Object> queryMap=new HashMap<>();
			queryMap.put("stationId",stationId);
			queryMap.put("siteId",site.getId());
			queryMap.put("propertyCode", ProductSid.SID31.id);
			queryMap.put("btype", ItemBtype.ELECTRICITY.id);

			List<DiagramItemDTO> diagramItemList=iDiagramItemService.getItem(queryMap);
			List<String> items=new ArrayList<>();
			for(DiagramItemDTO item:diagramItemList){
				items.add(item.getItemId());
			}

			nenghaogongshi.setUnit("Kwh");
			nenghaogongshi.setSiteName(site.getSiteName());
			Map<String, Float> itemsSumValueMap=itemsSumValue(items,curveDataInfo);
			Float thisMonthtotalSubVals=itemsSumValueMap.get(XYDdatas.YSubVals);
			nenghaogongshi.setThisMothVal(String.valueOf(thisMonthtotalSubVals));

			CurveDataReq curveDataReqhb= new CurveDataReq();
			BeanUtils.copyProperties(curveDataReq,curveDataReqhb);
			curveDataReqhb.setTime(DateUtil.addHour(curveDataReqhb.getTime(),-24));
			CurveDataInfo curveDataInfoHb= CurveDataFactory.getCurveDataInfo(curveDataReqhb,false);
			Map<String, Float> lastitemsSumValueMap=itemsSumValue(items,curveDataInfoHb);
			Float lastMonthtotalSubVals=lastitemsSumValueMap.get(XYDdatas.YSubVals);
			nenghaogongshi.setLastMothVal(String.valueOf(lastMonthtotalSubVals));

			nenghaogongshiList.add(nenghaogongshi);
		}


	return nenghaogongshiList;
	}

	public Map<String, Float> itemsSumValue(List<String> items,CurveDataInfo curveDataInfo){
		//查询具体数据项数据
		List<DeviceItemHistoryDiffData> deviceItemHistoryDiffDatas = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
			CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY)
			, curveDataInfo.getStime(), curveDataInfo.getEtime(), curveDataInfo.getItemCycle().id, HistoryDataType.TOTAL.id);
		Map<String, Float> itemsSumValueMap= curveDataRepository.itemsSumValue(deviceItemHistoryDiffDatas);
		//Float thisMonthtotalSubVals=thisMonthitemsSumValueMap.get(XYDdatas.YSubVals);
		//Float thisMonthtotalSubVals=thisMonthitemsSumValueMap.get(XYDdatas.YSubVals);
		return itemsSumValueMap;
	}

	/**
	 * 水电气 月,年度累计消耗
	 * @param stationId
	 * @return
	 */
	public BigSrceenData leijixiaohao(Long stationId,Integer dateType,String propertyCode,Integer btype,String diagramType) {
		CurveDataReq curveDataReq = new CurveDataReq();
		curveDataReq.setTime(new Date());
		//curveDataReq.setDateType(CommonDateType.YEAR.getId());
		curveDataReq.setDateType(dateType);
		CurveDataInfo curveDataInfo = CurveDataFactory.getCurveDataInfo(curveDataReq,true);

		CurveDataReq curveDataReqhb= new CurveDataReq();
		BeanUtils.copyProperties(curveDataReq,curveDataReqhb);
		if(Func.equals(CommonDateType.YEAR.getId(),dateType)){
			curveDataReqhb.setTime(DateUtil.addYear(curveDataReqhb.getTime(),-1));
		}
		if(Func.equals(CommonDateType.MONTH.getId(),dateType)){
			curveDataReqhb.setTime(DateUtil.addMonth(curveDataReqhb.getTime(),-1));
		}
		if(Func.equals(CommonDateType.DAY.getId(),dateType)){
			curveDataReqhb.setTime(DateUtil.addHour(curveDataReqhb.getTime(),-24));
		}

		CurveDataInfo curveDataInfoHb= CurveDataFactory.getCurveDataInfo(curveDataReqhb,true);



		Map<String,Object> queryMap=new HashMap<>();
		queryMap.put("stationId",stationId);
		queryMap.put("propertyCode",propertyCode);
		queryMap.put("btype", btype);
		queryMap.put("diagramType",diagramType);
		List<DiagramItemDTO> itemList=iDiagramItemService.getItem(queryMap);
		List<String> items=new ArrayList<>();
		for(DiagramItemDTO item:itemList){
			items.add(item.getItemId());
		}

		Map<String, Float> thisitemsSumValueMap=itemsSumValue(items,curveDataInfo);
		Map<String, Float> lastitemsSumValueMap=itemsSumValue(items,curveDataInfoHb);

		BigSrceenData bigSrceenData=new BigSrceenData();
		bigSrceenData.setThisVal(thisitemsSumValueMap.get(XYDdatas.YSubVals));
		bigSrceenData.setThisCost(thisitemsSumValueMap.get(XYDdatas.YPrices));
		bigSrceenData.setLastVal(lastitemsSumValueMap.get(XYDdatas.YSubVals));
		bigSrceenData.setLastCost(lastitemsSumValueMap.get(XYDdatas.YPrices));

		return bigSrceenData;
	}




	public void BigScreenDataJob() {
		List<Station> stationList = queryStation();
		if (Func.isNotEmpty(stationList)) {
			for (Station station : stationList) {
				Long stationId=station.getId();
				//站点位置个数
				Integer siteNum =siteNum(stationId);
				redisCache.set(BigScreenRedis.siteNum+stationId,siteNum);
				//站点安全运行天数
				Integer runDay =runDay(stationId);
				redisCache.set(BigScreenRedis.runDay+stationId,runDay);
				//能耗公示
				List<Nenghaogongshi> nenghaogongshiList=nenghaogongshi(stationId);
				redisCache.set(BigScreenRedis.stationPowerNenghaogongshi+stationId,nenghaogongshiList);

				//站点电能年统计
				BigSrceenData powerYearBigSrceenData=leijixiaohao(stationId,CommonDateType.YEAR.getId(), ProductSid.SID31.id,
					ItemBtype.ELECTRICITY.id,null);
				powerYearBigSrceenData.setUnit("Kwh");
				powerYearBigSrceenData.setName("年用电情况");
				redisCache.set(BigScreenRedis.stationYearPowerTotalVal+stationId,powerYearBigSrceenData);

				//站点电能月统计
				BigSrceenData powerMonthBigSrceenData=leijixiaohao(stationId,CommonDateType.MONTH.getId(), ProductSid.SID31.id,
					ItemBtype.ELECTRICITY.id,null);
				powerMonthBigSrceenData.setUnit("Kwh");
				powerMonthBigSrceenData.setName("月用电情况");
				redisCache.set(BigScreenRedis.stationMonthPowerTotalVal+stationId,powerMonthBigSrceenData);

				//站点电能月统计--低压
				BigSrceenData powerLowMonthBigSrceenData=leijixiaohao(stationId,CommonDateType.MONTH.getId(), ProductSid.SID31.id,
					ItemBtype.ELECTRICITY.id,DiagramType.DIYA.id);
				powerLowMonthBigSrceenData.setUnit("Kwh");
				powerLowMonthBigSrceenData.setName("低压月用电情况");
				redisCache.set(BigScreenRedis.stationMonthPowerLowTotalVal+stationId,powerLowMonthBigSrceenData);

				//站点电能月统计--高压
				BigSrceenData powerHighMonthBigSrceenData=leijixiaohao(stationId,CommonDateType.MONTH.getId(), ProductSid.SID31.id,
					ItemBtype.ELECTRICITY.id,DiagramType.ZHONGYA.id);
				powerHighMonthBigSrceenData.setUnit("Kwh");
				powerHighMonthBigSrceenData.setName("高压月用电情况");
				redisCache.set(BigScreenRedis.stationMonthPowerHighTotalVal+stationId,powerHighMonthBigSrceenData);

				//站点水能年统计
				BigSrceenData waterYearBigSrceenData=leijixiaohao(stationId,CommonDateType.YEAR.getId(), ProductSid.SID159.id,
					ItemBtype.WATERVOLUME.id,null);
				waterYearBigSrceenData.setUnit("t");
				waterYearBigSrceenData.setName("年用水情况");
				redisCache.set(BigScreenRedis.stationYearWaterTotalVal+stationId,waterYearBigSrceenData);

				//站点水能月统计
				BigSrceenData waterMonthBigSrceenData=leijixiaohao(stationId,CommonDateType.MONTH.getId(), ProductSid.SID159.id,
					ItemBtype.WATERVOLUME.id,null);
				waterMonthBigSrceenData.setUnit("t");
				waterMonthBigSrceenData.setName("月用水情况");
				redisCache.set(BigScreenRedis.stationMonthWaterTotalVal+stationId,waterMonthBigSrceenData);


				//站点气能年统计
				BigSrceenData gasYearBigSrceenData=leijixiaohao(stationId,CommonDateType.YEAR.getId(), ProductSid.SID161.id,
					ItemBtype.AIRVOLUME.id,null);
				gasYearBigSrceenData.setUnit("m³");
				gasYearBigSrceenData.setName("年用气情况");
				redisCache.set(BigScreenRedis.stationYearGasTotalVal+stationId,gasYearBigSrceenData);

				//站点气能月统计
				BigSrceenData gasMonthBigSrceenData=leijixiaohao(stationId,CommonDateType.MONTH.getId(), ProductSid.SID161.id,
					ItemBtype.AIRVOLUME.id,null);
				gasMonthBigSrceenData.setUnit("m³");
				gasMonthBigSrceenData.setName("月用气情况");
				redisCache.set(BigScreenRedis.stationMonthGasTotalVal+stationId,gasMonthBigSrceenData);



				//站点电能月统计---空调
				BigSrceenData aircpowerMonthBigSrceenData=leijixiaohao(stationId,CommonDateType.MONTH.getId(), ProductSid.SID31.id,
					ItemBtype.ELECTRICITY.id,DiagramType.KONGTIAN.id);
				aircpowerMonthBigSrceenData.setUnit("Kwh");
				aircpowerMonthBigSrceenData.setName("空调月用电情况");
				redisCache.set(BigScreenRedis.stationMonthAircPowerTotalVal+stationId,aircpowerMonthBigSrceenData);

				//站点气能月统计---空调
				BigSrceenData aircgasMonthBigSrceenData=leijixiaohao(stationId,CommonDateType.MONTH.getId(), ProductSid.SID161.id,
					ItemBtype.AIRVOLUME.id,DiagramType.KONGTIAN.id);
				aircgasMonthBigSrceenData.setUnit("m³");
				aircgasMonthBigSrceenData.setName("空调月用气情况");
				redisCache.set(BigScreenRedis.stationMonthAircGasTotalVal+stationId,aircgasMonthBigSrceenData);


				/**
				 *统计异常告警个数
				 */

				List<BigSrceenHandleAlarmData> bigSrceenHandleAlarmDataList= new ArrayList<>();

				Date stime= DateUtil.getYearStartDate(new Date());
				Date etime= DateUtil.getYearEndDate(new Date());
				Integer thisYearAlarmNum=handlealarmNum(stationId,stime,etime);
				Integer lastYearAlarmNum=handlealarmNum(stationId,DateUtil.addYear(stime,-1),DateUtil.addYear(etime,-1));
				BigSrceenHandleAlarmData yearBigSrceenHandleAlarmData =new BigSrceenHandleAlarmData();
				yearBigSrceenHandleAlarmData.setThisVal(thisYearAlarmNum);
				yearBigSrceenHandleAlarmData.setLastVal(lastYearAlarmNum);
				yearBigSrceenHandleAlarmData.setName("年异常");
				bigSrceenHandleAlarmDataList.add(yearBigSrceenHandleAlarmData);

				Date stimemonth= DateUtil.getMonthStartDate(new Date());
				Date etimemonth= DateUtil.getMonthEndDate(new Date());
				Integer thisMonthAlarmNum=handlealarmNum(stationId,stimemonth,etimemonth);
				Integer lastMonthAlarmNum=handlealarmNum(stationId,DateUtil.addMonth(stimemonth,-1),DateUtil.addMonth(etimemonth,-1));
				BigSrceenHandleAlarmData monthBigSrceenHandleAlarmData =new BigSrceenHandleAlarmData();
				yearBigSrceenHandleAlarmData.setThisVal(thisMonthAlarmNum);
				yearBigSrceenHandleAlarmData.setLastVal(lastMonthAlarmNum);
				yearBigSrceenHandleAlarmData.setName("月异常");
				bigSrceenHandleAlarmDataList.add(monthBigSrceenHandleAlarmData);

				redisCache.set(BigScreenRedis.stationhandlealarmNum+stationId,bigSrceenHandleAlarmDataList);

			}
		}
	}

}
