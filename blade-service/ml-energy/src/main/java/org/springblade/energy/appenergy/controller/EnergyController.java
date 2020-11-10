package org.springblade.energy.appenergy.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.bean.DeviceItemHistoryDiffData;
import org.springblade.constants.BigScreenRedis;
import org.springblade.constants.CoreCommonConstant;
import org.springblade.constants.CurveTypeDatas;
import org.springblade.constants.XYDdatas;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.bigscreen.dto.BigSrceenData;
import org.springblade.energy.diagram.dto.DiagramItemDTO;
import org.springblade.energy.diagram.entity.DiagramItem;
import org.springblade.energy.diagram.service.IDiagramItemService;
import org.springblade.energy.equipmentmanagement.eec.entity.EecMeter;
import org.springblade.energy.equipmentmanagement.eec.service.IEecMeterService;
import org.springblade.energy.statistics.controller.CurveDataFactory;
import org.springblade.energy.statistics.dto.CurveDataInfo;
import org.springblade.energy.statistics.dto.CurveDataReq;
import org.springblade.energy.statistics.dto.CurveDataResq;
import org.springblade.energy.statistics.repository.CurveDataRepository;
import org.springblade.enums.*;
import org.springblade.gw.feign.IDeviceItemHistoryClient;
import org.springblade.util.BigDecimalUtil;
import org.springblade.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author bond
 * @date 2020/8/3 10:12
 * @desc
 */
@RestController
@AllArgsConstructor
@RequestMapping("/app")
@Api(value = "APP能耗", tags = "APP能耗")
public class EnergyController {
	@Autowired
	private BladeRedisCache redisCache;
	@Autowired
	private IEecMeterService eecMeterService;
	@Autowired
	private IDeviceItemHistoryClient iDeviceItemHistoryClient;
	@Autowired
	private CurveDataRepository curveDataRepository;
	@Autowired
	private IDiagramItemService iDiagramItemService;


	/**
	 * 根据站点Id查询能耗数据
	 */
	@GetMapping("/getEnergyData")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "查询月累计能耗数据", notes = "传入stationId")
	public R<Map<String,Object>> getEnergyData(@ApiParam(value = "stationId", required = true) @RequestParam Long stationId)
	{
		Map<String,Object> resMap=new HashMap<>();

		//月度累计消耗看板
		List<BigSrceenData> mdatas=new ArrayList<>();
		BigSrceenData stationMonthPowerTotalVal=redisCache.get(BigScreenRedis.stationMonthPowerTotalVal+stationId);
		mdatas.add(stationMonthPowerTotalVal);
		BigSrceenData stationMonthWaterTotalVal=redisCache.get(BigScreenRedis.stationMonthWaterTotalVal+stationId);
		mdatas.add(stationMonthWaterTotalVal);
		BigSrceenData stationMonthGasTotalVal=redisCache.get(BigScreenRedis.stationMonthGasTotalVal+stationId);
		mdatas.add(stationMonthGasTotalVal);

		BigSrceenData stationMonthAircGasTotalVal=redisCache.get(BigScreenRedis.stationMonthAircGasTotalVal+stationId);
		mdatas.add(stationMonthAircGasTotalVal);
		BigSrceenData stationMonthAircPowerTotalVal=redisCache.get(BigScreenRedis.stationMonthAircPowerTotalVal+stationId);
		mdatas.add(stationMonthAircPowerTotalVal);



		//一度电=0.1229千克标准煤
		//一吨水=0.086千克标准煤
		//一立方天然气=1.3300千克标准煤
		BigSrceenData stationMonthTCETotalVal=new BigSrceenData();
		stationMonthTCETotalVal.setName("总能耗");
		stationMonthTCETotalVal.setUnit("tce");
		Float thisMonthPowerVal=stationMonthPowerTotalVal==null?0f:stationMonthPowerTotalVal.getThisVal();
		Float thisMonthWaterVal=stationMonthWaterTotalVal==null?0f:stationMonthWaterTotalVal.getThisVal();
		Float thisMonthGasVal=stationMonthGasTotalVal==null?0f:stationMonthGasTotalVal.getThisVal();

		Float lastMonthPowerVal=stationMonthPowerTotalVal==null?0f:stationMonthPowerTotalVal.getLastVal();
		Float lastMonthWaterVal=stationMonthWaterTotalVal==null?0f:stationMonthWaterTotalVal.getLastVal();
		Float lastMonthGasVal=stationMonthGasTotalVal==null?0f:stationMonthGasTotalVal.getLastVal();

		Float thisVal= BigDecimalUtil.mulF(thisMonthPowerVal,0.1229F)+
			BigDecimalUtil.mulF(thisMonthWaterVal,0.086F)+
			BigDecimalUtil.mulF(thisMonthGasVal,1.33f);
		Float lasVal= BigDecimalUtil.mulF(lastMonthPowerVal,0.1229F)+
			BigDecimalUtil.mulF(lastMonthWaterVal,0.086F)+
			BigDecimalUtil.mulF(lastMonthGasVal,1.33f);
		stationMonthTCETotalVal.setThisVal(thisVal);
		stationMonthTCETotalVal.setLastVal(lasVal);
		mdatas.add(stationMonthTCETotalVal);

		//mdatas.add(redisCache.get(BigScreenRedis.stationMonthAircPowerTotalVal+stationId));
		//mdatas.add(redisCache.get(BigScreenRedis.stationMonthAircGasTotalVal+stationId));

		resMap.put("yueduleijixiaohao", JSONArray.parseArray(JSON.toJSONString(mdatas)));
		return R.data(resMap);
	}

	@GetMapping("/eccList")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "查询重点能耗设备", notes = "传入stationId")
	public R<List<EecMeter>> eccList(@ApiParam(value = "stationId", required = true) @RequestParam Long stationId){
		Map<String,Object> query=new HashMap<>();
		query.put("station_id",stationId);
		query.put("is_deleted",0);
		List<EecMeter> list = eecMeterService.listByMap(query);
		return R.data(list);
	}

	@GetMapping("/eccCurve")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "重点能耗曲线图", notes = "")
	public R<Map<String,List<CurveDataResq>>> waterReport(@ApiParam(value = "时间0:日，2:月 ,3:年", required = true) @RequestParam Integer dateType,
														  @ApiParam(value = "时间", required = true) @RequestParam Date time,
														  @ApiParam(value = "设备ID", required = true) @RequestParam Long meterId ) {


		Map<String, List<CurveDataResq>> res =new HashMap<>();

		CurveDataReq curveDataReq = new CurveDataReq();
		curveDataReq.setTime(new Date());
		curveDataReq.setDateType(dateType);
		CurveDataInfo curveDataInfo= CurveDataFactory.getCurveDataInfo(curveDataReq,false);
		List<Object> xvals=curveDataRepository.valueRowsX(curveDataInfo);
		DeviceItemCycle itemCycle =curveDataInfo.getItemCycle();

		List<CurveDataResq> list=new ArrayList<>();

		EecMeter meter=eecMeterService.getById(meterId);

		//总用电量/
		Map<String,Object> queryMap=new HashMap<>();
		queryMap.put("stationId",meter.getStationId());
		queryMap.put("diagramType",DiagramType.KONGTIAN.id);
		queryMap.put("siteId",meter.getSiteId());
		queryMap.put("propertyCode",ProductSid.SID31.id);
		List<DiagramItemDTO> diagramItemDtoList=iDiagramItemService.getItem(queryMap);
		Set<String> poertitems=new HashSet<>();
		for(DiagramItem item:diagramItemDtoList){
			poertitems.add(item.getItemId());
		}
		if(Func.isEmpty(poertitems)){
			return R.data(null);
		}

		List<DeviceItemHistoryDiffData> deviceItemHistoryDiffDatas = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
			CollectionUtil.join(poertitems, CoreCommonConstant.SPLIT_COMMA_KEY)
			, curveDataInfo.getStime(), curveDataInfo.getEtime(), curveDataInfo.getItemCycle().id, HistoryDataType.TOTAL.id);
		if(Func.isEmpty(deviceItemHistoryDiffDatas)){
			return R.data(null);
		}

		Map<String, List<Float>> valMap=curveDataRepository.groupSumValueByXY(deviceItemHistoryDiffDatas,itemCycle,curveDataInfo.getShowRows(),null);
		List<Float> yvals= valMap.get(XYDdatas.YSubVals);

		CurveDataResq curveDataResq =new CurveDataResq();
		curveDataResq.setYvals((List<Object>)(List)yvals);
		curveDataResq.setXvals(xvals);
		curveDataResq.setTime(DateUtil.toString(time));
		curveDataResq.setUnit("kwh");

		list.add(curveDataResq);

		//总用气量
		queryMap.put("propertyCode",ProductSid.SID161.id);
		diagramItemDtoList=iDiagramItemService.getItem(queryMap);
		Set<String> gasitems=new HashSet<>();
		for(DiagramItem item:diagramItemDtoList){
			gasitems.add(item.getItemId());
		}
		if(Func.isEmpty(gasitems)){
			return R.data(null);
		}

		List<DeviceItemHistoryDiffData> gasdeviceItemHistoryDiffDatas = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
			CollectionUtil.join(poertitems, CoreCommonConstant.SPLIT_COMMA_KEY)
			, curveDataInfo.getStime(), curveDataInfo.getEtime(), curveDataInfo.getItemCycle().id, HistoryDataType.TOTAL.id);
		if(Func.isEmpty(deviceItemHistoryDiffDatas)){
			return R.data(null);
		}

		Map<String, List<Float>> gasvalMap=curveDataRepository.groupSumValueByXY(gasdeviceItemHistoryDiffDatas,itemCycle,curveDataInfo.getShowRows(),null);
		List<Float> gasyvals= gasvalMap.get(XYDdatas.YSubVals);

		CurveDataResq gascurveDataResq =new CurveDataResq();
		gascurveDataResq.setYvals((List<Object>)(List)gasyvals);
		gascurveDataResq.setXvals(xvals);
		gascurveDataResq.setTime(DateUtil.toString(time));
		gascurveDataResq.setUnit("m³");

		list.add(curveDataResq);

		res.put(CurveTypeDatas.curData, list);
		return R.data(res);
	}

	@GetMapping("/energyCurve")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "能源统计曲线图", notes = "")
	public R<Map<String,List<CurveDataResq>>> waterReport(@ApiParam(value = "站点id", required = true) @RequestParam Long stationId,
														  @ApiParam(value = "时间0:日，2:月 ,3:年", required = true) @RequestParam Integer dateType,
														  @ApiParam(value = "时间", required = true) @RequestParam Date time,
														  @ApiParam(value = "能源类型1电，2水，3气", required = true) @RequestParam Integer energyType ) {
		Map<String, List<CurveDataResq>> res =new HashMap<>();

		CurveDataReq curveDataReq =new CurveDataReq();
		curveDataReq.setDateType(dateType);
		curveDataReq.setTime(time);
		curveDataReq.setDataMolds("0");
		curveDataReq.setStationId(stationId);
		String propertyCodes="";
		Integer itemBtype=null;

		//总用电量
		if(Func.equals(EnergyType.POWER.id,energyType)){
			propertyCodes= ProductSid.SID31.id;
			itemBtype=ItemBtype.ELECTRICITY.id;
		}
		//总用水量
		if(Func.equals(EnergyType.WATER.id,energyType)){
			propertyCodes= ProductSid.SID159.id;
			itemBtype=ItemBtype.WATERVOLUME.id;
		}
		//总用气量
		if(Func.equals(EnergyType.GAS.id,energyType)){
			propertyCodes= ProductSid.SID161.id;
			itemBtype=ItemBtype.AIRVOLUME.id;
		}
		CurveDataInfo curveDataInfo= CurveDataFactory.getCurveDataInfo(curveDataReq,false);
		curveDataInfo.setPropertyCodes(propertyCodes);
		//查询具体数据项·
		Map<String,Object> queryMap=new HashMap<>();
		queryMap.put("stationId",curveDataInfo.getStationId());
		List<String> pscodes= Arrays.asList(curveDataInfo.getPropertyCodes().split(","));
		queryMap.put("propertyCodes",pscodes);
		queryMap.put("btype",itemBtype);
		List<DiagramItem> diagramItemList=iDiagramItemService.selectDiagramItemByMap(queryMap);
		if(Func.isEmpty(diagramItemList)){
			return R.data(res);
		}
		List<CurveDataResq> resqList =curveDataRepository.queryDiagramItemCurve(curveDataInfo,diagramItemList, HistoryDataType.TOTAL.id, itemBtype);
		res.put(CurveTypeDatas.curData, resqList);
		return R.data(res);
	}
}
