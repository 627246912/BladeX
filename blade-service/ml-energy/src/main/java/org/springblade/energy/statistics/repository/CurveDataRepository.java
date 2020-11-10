package org.springblade.energy.statistics.repository;

import cn.hutool.core.collection.CollectionUtil;
import org.springblade.bean.DeviceItemHistoryDiffData;
import org.springblade.bean.ProductProperty;
import org.springblade.constants.CoreCommonConstant;
import org.springblade.constants.GwsubscribeConstant;
import org.springblade.constants.ProductConstant;
import org.springblade.constants.XYDdatas;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.diagram.entity.DiagramItem;
import org.springblade.energy.diagram.entity.DiagramProduct;
import org.springblade.energy.diagram.service.IDiagramItemService;
import org.springblade.energy.diagram.service.IDiagramProductService;
import org.springblade.energy.runningmanagement.standingbook.entity.EquipmentTransformer;
import org.springblade.energy.runningmanagement.standingbook.service.IEquipmentTransformerService;
import org.springblade.energy.runningmanagement.standingbook.vo.EquipmentTransformerVO;
import org.springblade.energy.runningmanagement.station.service.IStationService;
import org.springblade.energy.statistics.controller.CurveDataFactory;
import org.springblade.energy.statistics.dto.CurveDataInfo;
import org.springblade.energy.statistics.dto.CurveDataReq;
import org.springblade.energy.statistics.dto.CurveDataResq;
import org.springblade.energy.statistics.enums.DataMold;
import org.springblade.enums.*;
import org.springblade.gw.feign.IDeviceItemHistoryClient;
import org.springblade.util.BigDecimalUtil;
import org.springblade.util.DateUtil;
import org.springblade.util.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author bond
 * @date 2020/5/25 14:06
 * @desc
 */
@Component
public class CurveDataRepository {

	@Autowired
	private IStationService stationService;
	@Autowired
	private IDiagramProductService iDiagramProductService;
	@Autowired
	private IDiagramItemService iDiagramItemService;
	@Autowired
	private IDeviceItemHistoryClient iDeviceItemHistoryClient;

	@Autowired
	private BladeRedisCache redisCache;


	@Autowired
	private IEquipmentTransformerService iEquipmentTransformerService;


	/**
	 * 查询变压器
	 */

	public List<EquipmentTransformerVO> getTransformers(Long stationId, Long siteId) {
		//查询站点下面的所有变压器
		EquipmentTransformerVO equipmentTransformer = new EquipmentTransformerVO();
		equipmentTransformer.setStationId(stationId);
		equipmentTransformer.setSiteId(siteId);
		List<EquipmentTransformerVO> transformers = iEquipmentTransformerService.selectEquipmentTransformer(equipmentTransformer);
		return transformers;
	}

	public R checkParam(CurveDataReq curveDataReq) {
		if (Func.isEmpty(curveDataReq)) {
			return R.fail("参数不能为空");
		}
		if (Func.isEmpty(curveDataReq.getDiagramProductId())) {
			return R.fail("系统图产品ID参数不能为空");
		}
		if (Func.isEmpty(curveDataReq.getDataCurveType())) {
			return R.fail("产品属性ID参数不能为空");
		}
		if (Func.isEmpty(curveDataReq.getDataMolds())) {
			return R.fail("查询数据类型参数不能为空");
		}
		if (Func.isEmpty(curveDataReq.getDateType())) {
			return R.fail("日期类型参数不能为空");
		}
		if (Func.isEmpty(curveDataReq.getTime())) {
			return R.fail("日期不能为空");
		}

		CurveType curveType = CurveType.getById(curveDataReq.getDataCurveType());
		if (StringUtils.isNull(curveType)) {
			return R.fail("曲线类型参数无效");
		}
		return R.success("验证成功");
	}

	/**
	 * 查找具体的系统图产品 低压总进线开关数据项
	 * curveDataReq.getDiagramProductId() 为变压器的
	 */
	public List<DiagramItem> getJinXianItem(CurveDataReq curveDataReq, String propertyCodes) {
		DiagramProduct diagramProduct = iDiagramProductService.getById(curveDataReq.getDiagramProductId());
		if (Func.isEmpty(diagramProduct)) {
			return null;
		}
		Map<String, String> map = new HashMap<>();
		map.put("in_id", diagramProduct.getPindex());
		map.put("out_ids", null);
		map = iDiagramProductService.queryDiagramProductDtype2(map);

		if (Func.isEmpty(map)) {
			return null;
		}

		List<String> diagramProductIds = new ArrayList<>(Arrays.asList(map.get("out_ids").split(",")));

		//查询具体数据项
		Map<String, Object> queryMap = new HashMap<>();
		//queryMap.put("stationId",curveDataInfo.getStationId());
		queryMap.put("diagramProductIds", diagramProductIds);
		List<String> pscodes = Arrays.asList(propertyCodes.split(","));
		queryMap.put("propertyCodes", pscodes);
		List<DiagramItem> diagramItemList = iDiagramItemService.selectDiagramItemByMap(queryMap);
		return diagramItemList;
	}

		/**
		 * 查找具体的系统图产品 低压总进线开关
		 */
	public Map<String, List<CurveDataResq>> getCurveDataZongJinXian(CurveDataReq curveDataReq,String propertyCodes,Integer ctg,Integer btype){
		DiagramProduct diagramProduct=iDiagramProductService.getById(curveDataReq.getDiagramProductId());
		if(Func.isEmpty(diagramProduct)){
			return null;
		}
		Map<String,String> map =new HashMap<>();
		map.put("in_id",diagramProduct.getPindex());
		map.put("out_ids",null);
		map=iDiagramProductService.queryDiagramProductDtype2(map);

		if(Func.isEmpty(map)){
			return null;
		}

		List<String> diagramProductIds= new ArrayList<>(Arrays.asList(map.get("out_ids").split(",")));



		//查询具体数据项
		Map<String,Object> queryMap=new HashMap<>();
		//queryMap.put("stationId",curveDataInfo.getStationId());
		queryMap.put("diagramProductIds",diagramProductIds);
		List<String> pscodes= Arrays.asList(propertyCodes.split(","));
		queryMap.put("propertyCodes",pscodes);
		List<DiagramItem> diagramItemList=iDiagramItemService.selectDiagramItemByMap(queryMap);

		String dataMolds=curveDataReq.getDataMolds();

		Map<String, List<CurveDataResq>> res = new HashMap<>();
		if(DataMold.getflgnow(dataMolds)){
			CurveDataInfo curveDataInfo= CurveDataFactory.getCurveDataInfo(curveDataReq,false);
			curveDataInfo.setPropertyCodes(propertyCodes);
			List<CurveDataResq> itemCurveDataReqlist=queryDiagramItemCurve(curveDataInfo,diagramItemList,ctg,btype);
			res.put("curData", itemCurveDataReqlist);
		}
		if(DataMold.getflgTb(dataMolds)){//同比
			CurveDataReq curveDataReqtb= new CurveDataReq();
			BeanUtils.copyProperties(curveDataReq,curveDataReqtb);
			curveDataReqtb.setTime(DateUtil.addYear(curveDataReqtb.getTime(),-1));

			CurveDataInfo curveDataInfo=CurveDataFactory.getCurveDataInfo(curveDataReqtb,false);
			curveDataInfo.setPropertyCodes(propertyCodes);
			//查询具体数据项
			List<CurveDataResq> itemCurveDataReqlist=queryDiagramItemCurve(curveDataInfo,diagramItemList,ctg,btype);
			res.put("curDataTb", itemCurveDataReqlist);
		}
		if(DataMold.getflgHb(dataMolds)){//环比
			CurveDataReq curveDataReqhb= new CurveDataReq();
			BeanUtils.copyProperties(curveDataReq,curveDataReqhb);

			curveDataReqhb.setTime(DateUtil.addHour(curveDataReqhb.getTime(),-24));
			CurveDataInfo curveDataInfo=CurveDataFactory.getCurveDataInfo(curveDataReqhb,false);
			curveDataInfo.setPropertyCodes(propertyCodes);

			//查询具体数据项
			List<CurveDataResq> itemCurveDataReqlist=queryDiagramItemCurve(curveDataInfo,diagramItemList,ctg,btype);
			res.put("curDataHb", itemCurveDataReqlist);
		}
		return res;
	}

	public Map<String, List<CurveDataResq>> getCurveData(CurveDataReq curveDataReq,String propertyCodes,Integer btype,Integer ctg){

		Integer type=curveDataReq.getDateType();//2:月3:年8:季度
		String dataMolds=curveDataReq.getDataMolds();

		Map<String, List<CurveDataResq>> res = new HashMap<>();
		if(DataMold.getflgnow(dataMolds)){
			CurveDataInfo curveDataInfo= CurveDataFactory.getCurveDataInfo(curveDataReq,false);
			curveDataInfo.setPropertyCodes(propertyCodes);
			//查询具体数据项
			List<CurveDataResq> itemCurveDataReqlist=queryDiagramItemCurve(curveDataInfo,ctg,btype);
			res.put("curData", itemCurveDataReqlist);
		}
		if(DataMold.getflgTb(dataMolds)){//同比
			CurveDataReq curveDataReqtb= new CurveDataReq();
			BeanUtils.copyProperties(curveDataReq,curveDataReqtb);
			curveDataReqtb.setTime(DateUtil.addYear(curveDataReqtb.getTime(),-1));

			CurveDataInfo curveDataInfo=CurveDataFactory.getCurveDataInfo(curveDataReqtb,false);
			curveDataInfo.setPropertyCodes(propertyCodes);
			//查询具体数据项
			List<CurveDataResq> itemCurveDataReqlist=queryDiagramItemCurve(curveDataInfo,ctg,btype);
			res.put("curDataTb", itemCurveDataReqlist);
		}
		if(DataMold.getflgHb(dataMolds)){//环比
			CurveDataReq curveDataReqhb= new CurveDataReq();
			BeanUtils.copyProperties(curveDataReq,curveDataReqhb);
			if (Func.equals(CommonDateType.DAY.getId(), type)) {
				curveDataReqhb.setTime(DateUtil.addHour(curveDataReqhb.getTime(), -24));
			}
			if (Func.equals(CommonDateType.MONTH.getId(), type)) {
				curveDataReqhb.setTime(DateUtil.addMonth(curveDataReqhb.getTime(), -1));
			}
			if (Func.equals(CommonDateType.QUARTER.getId(), type)) {
				curveDataReqhb.setTime(DateUtil.addMonth(curveDataReqhb.getTime(), -3));
			}
			if (Func.equals(CommonDateType.YEAR.getId(), type)) {
				curveDataReqhb.setTime(DateUtil.addYear(curveDataReqhb.getTime(), -1));
			}
			CurveDataInfo curveDataInfo = CurveDataFactory.getCurveDataInfo(curveDataReqhb,false);
			curveDataInfo.setPropertyCodes(propertyCodes);
			//查询具体数据项
			List<CurveDataResq> itemCurveDataReqlist=queryDiagramItemCurve(curveDataInfo,ctg,btype);
			res.put("curDataHb", itemCurveDataReqlist);
		}
		return res;
	}



	/**
	 * 查询具体数据项
	 * @return
	 */
	public List<CurveDataResq> queryDiagramItemCurve(CurveDataInfo curveDataInfo,Integer ctg,Integer btype){

		String stime= curveDataInfo.getStime();
		String etime =curveDataInfo.getEtime();
		DeviceItemCycle itemCycle =curveDataInfo.getItemCycle();
		List rows=curveDataInfo.getShowRows();
		//查询具体数据项
		Map<String,Object> queryMap=new HashMap<>();
		queryMap.put("stationId",curveDataInfo.getStationId());
		queryMap.put("siteId",curveDataInfo.getSiteId());
		queryMap.put("diagramProductId",curveDataInfo.getDiagramProductId());
		List<String> pscodes= Arrays.asList(curveDataInfo.getPropertyCodes().split(","));
		queryMap.put("propertyCodes",pscodes);
		queryMap.put("btype",btype);
		List<DiagramItem> diagramItemList=iDiagramItemService.selectDiagramItemByMap(queryMap);

		List<CurveDataResq> itemCurveDataReqlist =new ArrayList<>();

		List<String> items =new ArrayList<>();
		Set<Long> productIds =new HashSet<>();
		for(DiagramItem diagramItem:diagramItemList){
			items.add(diagramItem.getItemId());
			productIds.add(diagramItem.getProductId());
			ProductProperty productProperty=redisCache.hGet(ProductConstant.PROPERTY_KEY,diagramItem.getPropertyId());

			CurveDataResq itemCurveDataReq=new CurveDataResq();
			if(Func.isNotEmpty(productProperty)){
				itemCurveDataReq.setUplimit(productProperty.getUplimit());
				itemCurveDataReq.setLowlimit(productProperty.getLowlimit());
				itemCurveDataReq.setPropertyCode(productProperty.getPropertyCode());
			}
			itemCurveDataReq.setItemId(diagramItem.getItemId());
			itemCurveDataReq.setItemName(diagramItem.getPropertyName());
			itemCurveDataReq.setUnit(diagramItem.getUnit());
			itemCurveDataReq.setTime(stime);


			itemCurveDataReqlist.add(itemCurveDataReq);
		}
		//到数据中心查询数据
		List<DeviceItemHistoryDiffData> deviceItemHistoryDiffDatas = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
			CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY), stime, etime, itemCycle.id,ctg);

		if(Func.isNotEmpty(deviceItemHistoryDiffDatas)){
			for (CurveDataResq itemCurveDataReq : itemCurveDataReqlist) {
				//Map<String,List<DeviceItemHistoryDiffData>> map =new HashMap<>();
				List<DeviceItemHistoryDiffData> datas=new ArrayList<>();
				for(DeviceItemHistoryDiffData historyData : deviceItemHistoryDiffDatas) {
					if(Func.equals(itemCurveDataReq.getItemId(),historyData.getId())){
						datas.add(historyData);
					}
				}
				//map.put(itemCurveDataReq.getItemId(),datas);
				Map<String, List<Object>> YXVals= groupValueByXY(itemCurveDataReq.getItemId(),datas, itemCycle, rows);
				itemCurveDataReq.setXvals(YXVals.get(XYDdatas.XVals));
				//总用水，电，气
				if(Func.equals(curveDataInfo.getPropertyCodes(), ProductSid.SID31.id) ||
				Func.equals(curveDataInfo.getPropertyCodes(), ProductSid.SID159.id) ||
					Func.equals(curveDataInfo.getPropertyCodes(), ProductSid.SID161.id)
					){
					itemCurveDataReq.setYvals(YXVals.get(XYDdatas.YSubVals));

				}
				else if(Func.equals(btype, ItemBtype.ELECTRICITY.id) ||
					Func.equals(btype, ItemBtype.WATERVOLUME.id) || Func.equals(btype, ItemBtype.AIRVOLUME.id)
				){
					itemCurveDataReq.setYvals(YXVals.get(XYDdatas.YSubVals));

				}
				else {
					itemCurveDataReq.setYvals(YXVals.get(XYDdatas.YVals));
				}
			}
		}

		return itemCurveDataReqlist;
	}

	/**
	 *  数据项之和
	 * @return
	 */
	public List<CurveDataResq> queryDiagramItemCurve(CurveDataInfo curveDataInfo,List<DiagramItem> diagramItemList,Integer ctg,Integer btype){

		String stime= curveDataInfo.getStime();
		String etime =curveDataInfo.getEtime();
		DeviceItemCycle itemCycle =curveDataInfo.getItemCycle();
		List rows=curveDataInfo.getShowRows();

		List<String> pscodes= Arrays.asList(curveDataInfo.getPropertyCodes().split(","));
		List<CurveDataResq> itemCurveDataReqlist =new ArrayList<>();

		List<String> items =new ArrayList<>();
		Map<String, DiagramItem> mapDiagramItem=buildMapDiagramItem(diagramItemList);

		for(DiagramItem diagramItem:diagramItemList){
			items.add(diagramItem.getItemId());
		}
		for(String pscode:pscodes){
			DiagramItem diagramItem= new DiagramItem();
			for(DiagramItem Item:diagramItemList){
				if(Func.equals(pscode,Item.getPropertyCode())){
					diagramItem=Item;
				}
			}
			ProductProperty productProperty=redisCache.hGet(ProductConstant.PROPERTY_KEY,diagramItem.getPropertyId());
			CurveDataResq itemCurveDataReq=new CurveDataResq();
			if(Func.isEmpty(productProperty)){
				itemCurveDataReq.setUplimit(productProperty.getUplimit());
				itemCurveDataReq.setLowlimit(productProperty.getLowlimit());
			}
			//itemCurveDataReq.setItemId(diagramItem.getItemId());
			itemCurveDataReq.setItemName(productProperty.getPropertyName());
			itemCurveDataReq.setUnit(productProperty.getUnit());
			itemCurveDataReq.setTime(stime);
			itemCurveDataReq.setPropertyCode(productProperty.getPropertyCode());


			itemCurveDataReqlist.add(itemCurveDataReq);
		}
		//到数据中心查询数据
		List<DeviceItemHistoryDiffData> deviceItemHistoryDiffDatas = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
			CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY), stime, etime, itemCycle.id,ctg);

		//if(Func.isNotEmpty(deviceItemHistoryDiffDatas)){
		for (CurveDataResq itemCurveDataReq : itemCurveDataReqlist) {
			//Map<String,List<DeviceItemHistoryDiffData>> map =new HashMap<>();
			List<DeviceItemHistoryDiffData> datas=new ArrayList<>();
			for(DeviceItemHistoryDiffData historyData : deviceItemHistoryDiffDatas) {
				DiagramItem diagramItem= new DiagramItem();
				diagramItem =mapDiagramItem.get(historyData.getId());
				if(Func.equals(diagramItem.getPropertyCode(),itemCurveDataReq.getPropertyCode())){
					datas.add(historyData);
				}
			}
			//map.put(itemCurveDataReq.getItemId(),datas);
			Map<String, List<Object>> YXVals= groupSumValueByXY(datas, itemCycle, rows,null);
			itemCurveDataReq.setXvals(valueRowsX(curveDataInfo));
			//总用水，电，气
			if(Func.equals(curveDataInfo.getPropertyCodes(), ProductSid.SID31.id) ||
				Func.equals(curveDataInfo.getPropertyCodes(), ProductSid.SID159.id) ||
				Func.equals(curveDataInfo.getPropertyCodes(), ProductSid.SID161.id)
			){
				itemCurveDataReq.setYvals(YXVals.get(XYDdatas.YSubVals));

			}
			else if(Func.equals(btype, ItemBtype.ELECTRICITY.id) ||
				Func.equals(btype, ItemBtype.WATERVOLUME.id) || Func.equals(btype, ItemBtype.AIRVOLUME.id)
			){
				itemCurveDataReq.setYvals(YXVals.get(XYDdatas.YSubVals));

			}
			else {
				itemCurveDataReq.setYvals(YXVals.get(XYDdatas.YVals));
			}
		}
		//}

		return itemCurveDataReqlist;
	}

	public static List<Object> valueRowsX(CurveDataInfo curveDataInfo) {

		List<String> rows = curveDataInfo.getShowRows();
		DeviceItemCycle itemCycle = curveDataInfo.getItemCycle();
		List<Object> xvals = new ArrayList<>();
		String x = "";
		for (int i = 0; i < rows.size(); i++) {
			switch (itemCycle) {
				case DAY:
					x = rows.get(i).substring(8, 10);
					break;
				case MONTH:
					x = rows.get(i).substring(5, 7);
					break;
				case FIVEMINUTE:
					x = rows.get(i).substring(11,16);
					break;
				default:
					x = rows.get(i).substring(11, 13);
					if(i==24){
					x="24";
					}
					break;
			}
			xvals.add(i, x);

		}
		return xvals;
	}

	public static List<Object> valueRowsXRopert(CurveDataInfo curveDataInfo) {

		List<String> rows = curveDataInfo.getShowRows();
		DeviceItemCycle itemCycle = curveDataInfo.getItemCycle();
		List<Object> xvals = new ArrayList<>();
		String x = "";
		for (int i = 0; i < rows.size(); i++) {
			switch (itemCycle) {
				case DAY:
					x = rows.get(i).substring(0, 10);
					break;
				case MONTH:
					x = rows.get(i).substring(0, 7);
					break;
				case FIVEMINUTE:
					x = rows.get(i).substring(0,16);
					break;
				default:
					x = rows.get(i).substring(0, 13);
					break;
			}
			xvals.add(i, x);

		}
		return xvals;
	}
	/**
	 * 同一数据项历史数据排序 按X ,Y轴
	 * ctg
	 * @param historyDataList
	 * @param itemCycle
	 * @param rows
	 * @return
	 */
	public static Map<String, List<Object>> groupValueByXY(String item,List<DeviceItemHistoryDiffData> historyDataList,
														   DeviceItemCycle itemCycle, List<String> rows) {
		Map<String, List<Object>>  resMap =new HashMap<>();
		List<Object> XVals=new ArrayList<>();
		Map<String, Object> mapYVals =new LinkedHashMap<>();
		Map<String, Object> mapYSubVals =new LinkedHashMap<>();
		Map<String, Object> mapYAvgs =new LinkedHashMap<>();
		Map<String, Object> mapYMaxs =new LinkedHashMap<>();
		Map<String, Object> mapYMins =new LinkedHashMap<>();
		Map<String, Object> mapYPrices =new LinkedHashMap<>();

		List<Object> YVals=new ArrayList<>();
		List<Object> YSubVals=new ArrayList<>();
		List<Object> YAvgs=new ArrayList<>();

		List<Object> YMins=new ArrayList<>();
		List<Object> YMaxs=new ArrayList<>();
		List<Object> YPrices=new ArrayList<>();
		String x="";
		for(int i = 0; i < rows.size(); i++) {
			switch (itemCycle){
				case DAY:
					x = rows.get(i).substring(8, 10);
					break;
				case MONTH:
					x = rows.get(i).substring(5, 7);
					break;
				case FIVEMINUTE:
					x = rows.get(i).substring(11,16);
					break;
				default:
					x =rows.get(i).substring(11, 13);
					if(i==24){
						x="24";
					}
					break;
			}
			XVals.add(i, x);
			mapYVals.put(x,GwsubscribeConstant.ITEM_NULL_VALUE);
			mapYSubVals.put(x,GwsubscribeConstant.ITEM_NULL_VALUE);
			mapYAvgs.put(x,GwsubscribeConstant.ITEM_NULL_VALUE);
			mapYMins.put(x,GwsubscribeConstant.ITEM_NULL_VALUE);
			mapYMaxs.put(x,GwsubscribeConstant.ITEM_NULL_VALUE);
			mapYPrices.put(x,GwsubscribeConstant.ITEM_NULL_VALUE);

//			YVals.add(i, GwsubscribeConstant.ITEM_NULL_VALUE);
//			YSubVals.add(i, GwsubscribeConstant.ITEM_NULL_VALUE);
//			YAvgs.add(i, GwsubscribeConstant.ITEM_NULL_VALUE);
//			YMins.add(i, GwsubscribeConstant.ITEM_NULL_VALUE);
//			YMaxs.add(i, GwsubscribeConstant.ITEM_NULL_VALUE);
//			YPrices.add(i, GwsubscribeConstant.ITEM_NULL_VALUE);
		}

		int key=0;
		String mapkey="";
		if(Func.isNotEmpty(historyDataList)) {
			int i=0;
			for (DeviceItemHistoryDiffData historyData : historyDataList) {
				switch (itemCycle) {
					case DAY:
//						key = Integer.parseInt(historyData.getTm().substring(8, 10)) - 1;
						mapkey=historyData.getTm().substring(8, 10);
						break;
					case MONTH:
//						key = Integer.parseInt(historyData.getTm().substring(5, 7)) - 1;
						mapkey=historyData.getTm().substring(5, 7);
						break;
					case FIVEMINUTE:
//						key = Integer.parseInt(historyData.getTm().substring(5, 7)) - 1;
						mapkey=historyData.getTm().substring(11,16);
						break;
					default:
//						key = Integer.parseInt(historyData.getTm().substring(11, 13));
						mapkey=historyData.getTm().substring(11, 13);
						if(i==24){
//							key=24;
							mapkey="24";
						}
						break;
				}
				i++;

				mapYVals.put(mapkey,Func.isEmpty(historyData.getValx()) ? GwsubscribeConstant.ITEM_NULL_VALUE : historyData.getValx());
				mapYSubVals.put(mapkey, Func.isEmpty(historyData.getVal()) ? GwsubscribeConstant.ITEM_NULL_VALUE : historyData.getVal());
				mapYAvgs.put(mapkey, Func.isEmpty(historyData.getAvg()) ? GwsubscribeConstant.ITEM_NULL_VALUE : historyData.getAvg());
				mapYMaxs.put(mapkey, Func.isEmpty(historyData.getMax()) ? GwsubscribeConstant.ITEM_NULL_VALUE : historyData.getMax());
				mapYMins.put(mapkey, Func.isEmpty(historyData.getMin()) ? GwsubscribeConstant.ITEM_NULL_VALUE : historyData.getMin());
				mapYPrices.put(mapkey, Func.isEmpty(historyData.getPrice()) ? GwsubscribeConstant.ITEM_NULL_VALUE : historyData.getPrice());

//				YVals.set(key, Func.isEmpty(historyData.getValx()) ? GwsubscribeConstant.ITEM_NULL_VALUE : historyData.getValx());
//				YSubVals.set(key, Func.isEmpty(historyData.getVal()) ? GwsubscribeConstant.ITEM_NULL_VALUE : historyData.getVal());
//				YAvgs.set(key, Func.isEmpty(historyData.getAvg()) ? GwsubscribeConstant.ITEM_NULL_VALUE : historyData.getAvg());
//				YMaxs.set(key, Func.isEmpty(historyData.getMax()) ? GwsubscribeConstant.ITEM_NULL_VALUE : historyData.getMax());
//				YMins.set(key, Func.isEmpty(historyData.getMin()) ? GwsubscribeConstant.ITEM_NULL_VALUE : historyData.getMin());
//				YPrices.set(key, Func.isEmpty(historyData.getPrice()) ? GwsubscribeConstant.ITEM_NULL_VALUE : historyData.getPrice());
			}
		}

		for(Map.Entry<String, Object> val:mapYVals.entrySet()){
			YVals.add(val.getValue());
		}
		for(Map.Entry<String, Object> val:mapYSubVals.entrySet()){
			YSubVals.add(val.getValue());
		}
		for(Map.Entry<String, Object> val:mapYAvgs.entrySet()){
			YAvgs.add(val.getValue());
		}
		for(Map.Entry<String, Object> val:mapYMaxs.entrySet()){
			YMaxs.add(val.getValue());
		}
		for(Map.Entry<String, Object> val:mapYMins.entrySet()){
			YMins.add(val.getValue());
		}
		for(Map.Entry<String, Object> val:mapYPrices.entrySet()){
			YPrices.add(val.getValue());
		}

		resMap.put(XYDdatas.XVals,XVals);
		resMap.put(XYDdatas.YVals,YVals);
		resMap.put(XYDdatas.YSubVals,YSubVals);
		resMap.put(XYDdatas.YAvgs,YAvgs);
		resMap.put(XYDdatas.YMaxs,YMaxs);
		resMap.put(XYDdatas.YMins,YMins);
		resMap.put(XYDdatas.YPrices,YPrices);
		return resMap;
	}


	/**
	 * 同一数据项历史数据排序 按X ,Y轴
	 * ctg
	 * @param historyDataList
	 * @param itemCycle
	 * @param rows
	 * @return
	 */
	public static Map<String, List<Object>> groupValueByXYReport(String item,List<DeviceItemHistoryDiffData> historyDataList,
														   DeviceItemCycle itemCycle, List<String> rows) {
		Map<String, List<Object>>  resMap =new HashMap<>();
		List<Object> XVals=new ArrayList<>();
		Map<String, Object> mapYVals =new LinkedHashMap<>();
		Map<String, Object> mapYSubVals =new LinkedHashMap<>();
		Map<String, Object> mapYAvgs =new LinkedHashMap<>();
		Map<String, Object> mapYMaxs =new LinkedHashMap<>();
		Map<String, Object> mapYMins =new LinkedHashMap<>();
		Map<String, Object> mapYPrices =new LinkedHashMap<>();

		List<Object> YVals=new ArrayList<>();
		List<Object> YSubVals=new ArrayList<>();
		List<Object> YAvgs=new ArrayList<>();

		List<Object> YMins=new ArrayList<>();
		List<Object> YMaxs=new ArrayList<>();
		List<Object> YPrices=new ArrayList<>();
		String x="";
		for(int i = 0; i < rows.size(); i++) {
			switch (itemCycle){
				case DAY:
					x = rows.get(i).substring(0, 10);
					break;
				case MONTH:
					x = rows.get(i).substring(0, 7);
					break;
				case FIVEMINUTE:
					x = rows.get(i).substring(0,16);
					break;
				default:
					x =rows.get(i).substring(0, 13);
					break;
			}
			XVals.add(i, x);
			mapYVals.put(x,GwsubscribeConstant.ITEM_NULL_VALUE);
			mapYSubVals.put(x,GwsubscribeConstant.ITEM_NULL_VALUE);
			mapYAvgs.put(x,GwsubscribeConstant.ITEM_NULL_VALUE);
			mapYMins.put(x,GwsubscribeConstant.ITEM_NULL_VALUE);
			mapYMaxs.put(x,GwsubscribeConstant.ITEM_NULL_VALUE);
			mapYPrices.put(x,GwsubscribeConstant.ITEM_NULL_VALUE);

//			YVals.add(i, GwsubscribeConstant.ITEM_NULL_VALUE);
//			YSubVals.add(i, GwsubscribeConstant.ITEM_NULL_VALUE);
//			YAvgs.add(i, GwsubscribeConstant.ITEM_NULL_VALUE);
//			YMins.add(i, GwsubscribeConstant.ITEM_NULL_VALUE);
//			YMaxs.add(i, GwsubscribeConstant.ITEM_NULL_VALUE);
//			YPrices.add(i, GwsubscribeConstant.ITEM_NULL_VALUE);
		}

		int key=0;
		String mapkey="";
		if(Func.isNotEmpty(historyDataList)) {
			int i=0;
			for (DeviceItemHistoryDiffData historyData : historyDataList) {
				switch (itemCycle) {
					case DAY:
//						key = Integer.parseInt(historyData.getTm().substring(8, 10)) - 1;
						mapkey=historyData.getTm().substring(0, 10);
						break;
					case MONTH:
//						key = Integer.parseInt(historyData.getTm().substring(5, 7)) - 1;
						mapkey=historyData.getTm().substring(0, 7);
						break;
					case FIVEMINUTE:
//						key = Integer.parseInt(historyData.getTm().substring(5, 7)) - 1;
						mapkey=historyData.getTm().substring(0,16);
						break;
					default:
//						key = Integer.parseInt(historyData.getTm().substring(11, 13));
						mapkey=historyData.getTm().substring(0, 13);

						break;
				}
				i++;

				mapYVals.put(mapkey,Func.isEmpty(historyData.getValx()) ? GwsubscribeConstant.ITEM_NULL_VALUE : historyData.getValx());
				mapYSubVals.put(mapkey, Func.isEmpty(historyData.getVal()) ? GwsubscribeConstant.ITEM_NULL_VALUE : historyData.getVal());
				mapYAvgs.put(mapkey, Func.isEmpty(historyData.getAvg()) ? GwsubscribeConstant.ITEM_NULL_VALUE : historyData.getAvg());
				mapYMaxs.put(mapkey, Func.isEmpty(historyData.getMax()) ? GwsubscribeConstant.ITEM_NULL_VALUE : historyData.getMax());
				mapYMins.put(mapkey, Func.isEmpty(historyData.getMin()) ? GwsubscribeConstant.ITEM_NULL_VALUE : historyData.getMin());
				mapYPrices.put(mapkey, Func.isEmpty(historyData.getPrice()) ? GwsubscribeConstant.ITEM_NULL_VALUE : historyData.getPrice());

//				YVals.set(key, Func.isEmpty(historyData.getValx()) ? GwsubscribeConstant.ITEM_NULL_VALUE : historyData.getValx());
//				YSubVals.set(key, Func.isEmpty(historyData.getVal()) ? GwsubscribeConstant.ITEM_NULL_VALUE : historyData.getVal());
//				YAvgs.set(key, Func.isEmpty(historyData.getAvg()) ? GwsubscribeConstant.ITEM_NULL_VALUE : historyData.getAvg());
//				YMaxs.set(key, Func.isEmpty(historyData.getMax()) ? GwsubscribeConstant.ITEM_NULL_VALUE : historyData.getMax());
//				YMins.set(key, Func.isEmpty(historyData.getMin()) ? GwsubscribeConstant.ITEM_NULL_VALUE : historyData.getMin());
//				YPrices.set(key, Func.isEmpty(historyData.getPrice()) ? GwsubscribeConstant.ITEM_NULL_VALUE : historyData.getPrice());
			}
		}

		for(Map.Entry<String, Object> val:mapYVals.entrySet()){
			YVals.add(val.getValue());
		}
		for(Map.Entry<String, Object> val:mapYSubVals.entrySet()){
			YSubVals.add(val.getValue());
		}
		for(Map.Entry<String, Object> val:mapYAvgs.entrySet()){
			YAvgs.add(val.getValue());
		}
		for(Map.Entry<String, Object> val:mapYMaxs.entrySet()){
			YMaxs.add(val.getValue());
		}
		for(Map.Entry<String, Object> val:mapYMins.entrySet()){
			YMins.add(val.getValue());
		}
		for(Map.Entry<String, Object> val:mapYPrices.entrySet()){
			YPrices.add(val.getValue());
		}

		resMap.put(XYDdatas.XVals,XVals);
		resMap.put(XYDdatas.YVals,YVals);
		resMap.put(XYDdatas.YSubVals,YSubVals);
		resMap.put(XYDdatas.YAvgs,YAvgs);
		resMap.put(XYDdatas.YMaxs,YMaxs);
		resMap.put(XYDdatas.YMins,YMins);
		resMap.put(XYDdatas.YPrices,YPrices);
		return resMap;
	}
	/**
	 * 不同数据项 历史数据排序 求和Y轴
	 * @param historyDataList
	 * @param itemCycle
	 * @param rows
	 * @return
	 */
	public static Map<String, List<Float>> groupSumValueByXY(List<DeviceItemHistoryDiffData> historyDataList,
														   DeviceItemCycle itemCycle, List<String> rows,Float div) {
		Map<String, List<Float>>  resMap =new HashMap<>();
		List<Float> YVals=new ArrayList<>();
		List<Float> YSubVals=new ArrayList<>();
		List<Float> YPrices=new ArrayList<>();


		for(int i = 0; i < rows.size(); i++) {
			YVals.add(i, 0f);
			YSubVals.add(i, 0f);
			YPrices.add(i, 0f);
		}
		if(Func.isNotEmpty(historyDataList)) {
			Map<String, List<DeviceItemHistoryDiffData>> ItemDatas = buildMap(historyDataList);
			if (Func.isNotEmpty(ItemDatas)) {
				for (List<DeviceItemHistoryDiffData> historyDatas : ItemDatas.values()) {
					Map<String, List<Object>> resMap1 = groupValueByXY(null, historyDataList, itemCycle, rows);
					List<Object> vals = resMap1.get(XYDdatas.YVals);
					List<Object> subvals = resMap1.get(XYDdatas.YSubVals);
					List<Object> yprices = resMap1.get(XYDdatas.YPrices);
					for (int i = 0; i < rows.size(); i++) {
						String val = Func.equals(vals.get(i), GwsubscribeConstant.ITEM_NULL_VALUE) ? "0" : vals.get(i).toString();
						String valsub = Func.equals(subvals.get(i), GwsubscribeConstant.ITEM_NULL_VALUE) ? "0" : subvals.get(i).toString();
						String prices = Func.equals(yprices.get(i), GwsubscribeConstant.ITEM_NULL_VALUE) ? "0" : yprices.get(i).toString();
						if (Func.isNotEmpty(div)) {
							YSubVals.set(i, BigDecimalUtil.divF(YSubVals.get(i) + Float.valueOf(valsub), div));
							YVals.set(i, BigDecimalUtil.divF(YVals.get(i) + Float.valueOf(val), div));
						} else {
							YSubVals.set(i, YSubVals.get(i) + Float.valueOf(valsub));
							YVals.set(i, YVals.get(i) + Float.valueOf(val));
							YPrices.set(i, YPrices.get(i) + Float.valueOf(prices));
						}
					}

				}
			}
		}
		resMap.put(XYDdatas.YSubVals,YSubVals);
		resMap.put(XYDdatas.YVals,YVals);
		resMap.put(XYDdatas.YPrices,YPrices);
		return resMap;
	}

	public static Map<String, List<Float>> groupSumValueByXYReport(List<DeviceItemHistoryDiffData> historyDataList,
															 DeviceItemCycle itemCycle, List<String> rows,Float div) {
		Map<String, List<Float>>  resMap =new HashMap<>();
		List<Float> YVals=new ArrayList<>();
		List<Float> YSubVals=new ArrayList<>();
		List<Float> YPrices=new ArrayList<>();


		for(int i = 0; i < rows.size(); i++) {
			YVals.add(i, 0f);
			YSubVals.add(i, 0f);
			YPrices.add(i, 0f);
		}
		if(Func.isNotEmpty(historyDataList)) {
			Map<String, List<DeviceItemHistoryDiffData>> ItemDatas = buildMap(historyDataList);
			if (Func.isNotEmpty(ItemDatas)) {
				for (List<DeviceItemHistoryDiffData> historyDatas : ItemDatas.values()) {
					Map<String, List<Object>> resMap1 = groupValueByXYReport(null, historyDataList, itemCycle, rows);
					List<Object> vals = resMap1.get(XYDdatas.YVals);
					List<Object> subvals = resMap1.get(XYDdatas.YSubVals);
					List<Object> yprices = resMap1.get(XYDdatas.YPrices);
					for (int i = 0; i < rows.size(); i++) {
						String val = Func.equals(vals.get(i), GwsubscribeConstant.ITEM_NULL_VALUE) ? "0" : vals.get(i).toString();
						String valsub = Func.equals(subvals.get(i), GwsubscribeConstant.ITEM_NULL_VALUE) ? "0" : subvals.get(i).toString();
						String prices = Func.equals(yprices.get(i), GwsubscribeConstant.ITEM_NULL_VALUE) ? "0" : yprices.get(i).toString();
						if (Func.isNotEmpty(div)) {
							YSubVals.set(i, BigDecimalUtil.divF(YSubVals.get(i) + Float.valueOf(valsub), div));
							YVals.set(i, BigDecimalUtil.divF(YVals.get(i) + Float.valueOf(val), div));
						} else {
							YSubVals.set(i, YSubVals.get(i) + Float.valueOf(valsub));
							YVals.set(i, YVals.get(i) + Float.valueOf(val));
							YPrices.set(i, YPrices.get(i) + Float.valueOf(prices));
						}
					}

				}
			}
		}
		resMap.put(XYDdatas.YSubVals,YSubVals);
		resMap.put(XYDdatas.YVals,YVals);
		resMap.put(XYDdatas.YPrices,YPrices);
		return resMap;
	}

	/**
	 * 不同数据项 历史数据 求和
	 * @param historyDataList
	 * @return
	 */
	public static Map<String, Float> itemsSumValue(List<DeviceItemHistoryDiffData> historyDataList) {
		Map<String, Float>  resMap =new HashMap<>();
		Float YVals=0f;
		Float YSubVals=0f;

		Float YPrices=0f;
		if(Func.isNotEmpty(historyDataList)) {
			for (DeviceItemHistoryDiffData historyDatas : historyDataList) {
				String val = "0";
				if (Func.isNotEmpty(historyDatas.getValx())) {
					val = Func.equals(historyDatas.getValx(), GwsubscribeConstant.ITEM_NULL_VALUE) ? "0" : historyDatas.getValx().toString();
				}
				String valsub = "0";
				if (Func.isNotEmpty(historyDatas.getVal())) {
					valsub = Func.equals(historyDatas.getVal(), GwsubscribeConstant.ITEM_NULL_VALUE) ? "0" : historyDatas.getVal().toString();
				}
				String prices = "0";
				if (Func.isNotEmpty(historyDatas.getPrice())) {
					prices = Func.equals(historyDatas.getPrice(), GwsubscribeConstant.ITEM_NULL_VALUE) ? "0" : historyDatas.getPrice().toString();
				}


				YVals = YVals + Float.valueOf(val);
				YSubVals = YSubVals + Float.valueOf(valsub);
				YPrices = YPrices + Float.valueOf(prices);
			}
		}
		resMap.put(XYDdatas.YSubVals,YSubVals);
		resMap.put(XYDdatas.XVals,YVals);
		resMap.put(XYDdatas.YPrices,YPrices);
		return resMap;
	}


	public static Map<String, List<DeviceItemHistoryDiffData>> buildMap(List<DeviceItemHistoryDiffData> historyDataList) {
		if (historyDataList.isEmpty()) {
			return null;
		} else {
			Map<String, List<DeviceItemHistoryDiffData>> idItemMap = new HashMap();
			Set<String> items=new HashSet<>();
			for(DeviceItemHistoryDiffData historyData : historyDataList) {
				idItemMap.put(historyData.getId(),null);
			}

			for(String item: idItemMap.keySet()){
				List<DeviceItemHistoryDiffData> idItemData=new ArrayList<>();
				for(DeviceItemHistoryDiffData historyData : historyDataList) {
					if(Func.equals(item,historyData.getId())){
						idItemData.add(historyData);
					}
				}
				idItemMap.put(item,idItemData);
			}

			return idItemMap;
		}
	}

	public static Map<String, DiagramItem> buildMapDiagramItem(List<DiagramItem> diagramItemList) {
		if (diagramItemList.isEmpty()) {
			return null;
		} else {
			Map<String, DiagramItem> idItemMap = new HashMap();
			for(DiagramItem diagramItem:diagramItemList){
				idItemMap.put(diagramItem.getItemId(),diagramItem);
			}

			return idItemMap;
		}
	}


	/**
	 *  数据项之和
	 * @return
	 */
	public List<DeviceItemHistoryDiffData> queryItemHistoryDiffDatas(CurveDataInfo curveDataInfo,List<DiagramItem> diagramItemList,Integer ctg){

		String stime= curveDataInfo.getStime();
		String etime =curveDataInfo.getEtime();
		DeviceItemCycle itemCycle =curveDataInfo.getItemCycle();

		List<String> items =new ArrayList<>();
		for(DiagramItem diagramItem:diagramItemList){
			items.add(diagramItem.getItemId());
		}
		//到数据中心查询数据
		List<DeviceItemHistoryDiffData> deviceItemHistoryDiffDatas = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
			CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY), stime, etime, itemCycle.id,ctg);


		return deviceItemHistoryDiffDatas;
	}


	/**
	 * 变压器负载率
	 */
	public Map<String, List<CurveDataResq>> getBianYaQiCurveData(CurveDataReq curveDataReq,String propertyCodes,Integer btype,Integer ctg){

		Map<String, List<CurveDataResq>> res = new HashMap<>();

		Long diagramProductId =curveDataReq.getDiagramProductId();
		EquipmentTransformer transformer= new EquipmentTransformer();
		transformer.setCode(String.valueOf(diagramProductId));
		List<EquipmentTransformer> list=iEquipmentTransformerService.list(Condition.getQueryWrapper(transformer));
		if(Func.isEmpty(list)){
			return res;
		}
		//变压器额定容量
		Double capacity =list.get(0).getCapacity();

		Integer type=curveDataReq.getDateType();//2:月3:年8:季度
		String dataMolds=curveDataReq.getDataMolds();


		if(DataMold.getflgnow(dataMolds)){
			CurveDataInfo curveDataInfo= CurveDataFactory.getCurveDataInfo(curveDataReq,false);
			curveDataInfo.setPropertyCodes(propertyCodes);
			//查询具体数据项
			List<CurveDataResq> itemCurveDataReqlist=queryDiagramItemCurve(curveDataInfo,ctg,btype);
			itemCurveDataReqlist=CurveDataUtil.yvalsdivmul(itemCurveDataReqlist, BigDecimalUtil.convertsToFloat(capacity),100f);
			res.put("curData", itemCurveDataReqlist);
		}
		if(DataMold.getflgTb(dataMolds)){//同比
			CurveDataReq curveDataReqtb= new CurveDataReq();
			BeanUtils.copyProperties(curveDataReq,curveDataReqtb);
			curveDataReqtb.setTime(DateUtil.addYear(curveDataReqtb.getTime(),-1));

			CurveDataInfo curveDataInfo=CurveDataFactory.getCurveDataInfo(curveDataReqtb,false);
			curveDataInfo.setPropertyCodes(propertyCodes);
			//查询具体数据项
			List<CurveDataResq> itemCurveDataReqlist=queryDiagramItemCurve(curveDataInfo,ctg,btype);
			itemCurveDataReqlist=CurveDataUtil.yvalsdivmul(itemCurveDataReqlist, BigDecimalUtil.convertsToFloat(capacity),100f);
			res.put("curDataTb", itemCurveDataReqlist);
		}
		if(DataMold.getflgHb(dataMolds)){//环比
			CurveDataReq curveDataReqhb= new CurveDataReq();
			BeanUtils.copyProperties(curveDataReq,curveDataReqhb);
			if (Func.equals(CommonDateType.DAY.getId(), type)) {
				curveDataReqhb.setTime(DateUtil.addHour(curveDataReqhb.getTime(), -24));
			}
			if (Func.equals(CommonDateType.MONTH.getId(), type)) {
				curveDataReqhb.setTime(DateUtil.addMonth(curveDataReqhb.getTime(), -1));
			}
			if (Func.equals(CommonDateType.QUARTER.getId(), type)) {
				curveDataReqhb.setTime(DateUtil.addMonth(curveDataReqhb.getTime(), -3));
			}
			if (Func.equals(CommonDateType.YEAR.getId(), type)) {
				curveDataReqhb.setTime(DateUtil.addYear(curveDataReqhb.getTime(), -1));
			}
			CurveDataInfo curveDataInfo = CurveDataFactory.getCurveDataInfo(curveDataReqhb,false);
			curveDataInfo.setPropertyCodes(propertyCodes);
			//查询具体数据项
			List<CurveDataResq> itemCurveDataReqlist=queryDiagramItemCurve(curveDataInfo,ctg,btype);
			itemCurveDataReqlist=CurveDataUtil.yvalsdivmul(itemCurveDataReqlist, BigDecimalUtil.convertsToFloat(capacity),100f);
			res.put("curDataHb", itemCurveDataReqlist);
		}
		return res;
	}
}
