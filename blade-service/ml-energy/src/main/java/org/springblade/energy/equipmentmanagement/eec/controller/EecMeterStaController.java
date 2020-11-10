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
package org.springblade.energy.equipmentmanagement.eec.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.bean.DeviceItemHistoryDiffData;
import org.springblade.constants.CoreCommonConstant;
import org.springblade.constants.XYDdatas;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.diagram.dto.DiagramItemDTO;
import org.springblade.energy.diagram.entity.Diagram;
import org.springblade.energy.diagram.entity.DiagramItem;
import org.springblade.energy.diagram.service.IDiagramItemService;
import org.springblade.energy.diagram.service.IDiagramProductService;
import org.springblade.energy.diagram.service.IDiagramService;
import org.springblade.energy.equipmentmanagement.eec.dto.AirConditionerCurveReq;
import org.springblade.energy.equipmentmanagement.eec.dto.AirConditionerData;
import org.springblade.energy.equipmentmanagement.eec.entity.EecMeter;
import org.springblade.energy.equipmentmanagement.eec.service.IEecMeterService;
import org.springblade.energy.statistics.controller.CurveDataFactory;
import org.springblade.energy.statistics.dto.CurveDataInfo;
import org.springblade.energy.statistics.dto.CurveDataReq;
import org.springblade.energy.statistics.dto.CurveDataResq;
import org.springblade.energy.statistics.enums.DataMold;
import org.springblade.energy.statistics.repository.CurveDataRepository;
import org.springblade.enums.*;
import org.springblade.gw.feign.IDeviceItemHistoryClient;
import org.springblade.util.BigDecimalUtil;
import org.springblade.util.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 重点能耗设备-》仪表配置 控制器
 *
 * @author bond
 * @since 2020-05-06
 */
@RestController
@AllArgsConstructor
@RequestMapping("/eecmeter")
@Api(value = "重点能耗设备-数据统计", tags = "重点能耗设备-数据统计")
public class EecMeterStaController extends BladeController {

	private IEecMeterService eecMeterService;
	@Autowired
	private BladeRedisCache redisCache;
	private IDiagramProductService diagramProductService;
	private IDiagramService iDiagramService;
	private IDiagramItemService iDiagramItemService;
	@Autowired
	private IDeviceItemHistoryClient iDeviceItemHistoryClient;
	@Autowired
	private CurveDataRepository curveDataRepository;


	/**
	 * 1、单位面积空调能耗（ECA）单位KW*h/m²
	 * ECA=能耗总和/空调面积
	 * 2、空调冷热量
	 * Qt=Q1+Q2
	 * Q1=P×η1×η2×η3（KW） 　　　
	 * Q1：计算机设备热负荷　　　
	 * P：空调总能耗（KW） 　　　
	 * η1：同时使用系数 　　　
	 * η2：利用系数 　　　
	 * η3：负荷工作均匀系数　　　　
	 * 通常η1×η2×η3取值为0.8
	 * Q2=环境热负荷（=0.12～0.18KW/m2×机房面积），南方地区可选0.18
	 * 所以Qt=能耗总和*0.8+0.18*空调面积
	 * 3、单位面积空调耗冷量（CCA） 单位KW*h/m²
	 * CCA=Q/A
	 * 4、空调系统能效比（EERs）
	 * EERs=Q/能耗总和
	 *
	 * 5、制冷系统能效比（EERr）
	 * EERr=Q/制冷设备能耗总和
	 *
	 * 6、冷水机组运行效率(COP)
	 * COP=Q/冷水机组设备能耗总和
	 */

	public static float n=7.156f;//燃气转电能系数
	public static float r=0.8f;//
	public static float r1=0.18f;//


	@PostMapping("/getAirConditioner")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "获取空调数据", notes = "airConditionerCurveReq")
	public R<List<AirConditionerData>> getAirConditioner(@RequestBody AirConditionerCurveReq airConditionerCurveReq){

		Long stationId= airConditionerCurveReq.getStationId();
		Long siteId= airConditionerCurveReq.getSiteId();
		//1.查询空调系统图
		Diagram diagram=new Diagram();
		diagram.setStationId(stationId);
		diagram.setSiteId(siteId);
		diagram.setIsDeleted(0);
		diagram.setDiagramType(DiagramType.KONGTIAN.id);
		Diagram airDiagram= iDiagramService.getOne(Condition.getQueryWrapper(diagram));
		if(Func.isEmpty(airDiagram)){
			return R.fail("没有空调系统设备哦，请先配置空调系统吧！");
		}
		//2.查询空调设备
		EecMeter eecMeter =new EecMeter();
		eecMeter.setStationId(stationId);
		eecMeter.setSiteId(siteId);
		eecMeter.setIsDeleted(0);
		EecMeter airCon = eecMeterService.getOne(Condition.getQueryWrapper(eecMeter));
		if(Func.isEmpty(airCon)){
			return R.fail("没有空调重点能耗哦！请先配置空调重点能耗吧！");
		}
		Long diagramId=airDiagram.getId();
		//A空调面积 单位m²
		Float A =airCon.getUnitArea();
		//能耗总和 单位KW*h (31电，161气)
		Float W=0f;

		//电的能耗总和 KW*h
		Float powerSumval=getEnergyVal(airConditionerCurveReq,diagramId,ProductSid.SID31.id,null);
		//燃气的能耗总和 立方米
		Float gasSumval=getEnergyVal(airConditionerCurveReq,diagramId,ProductSid.SID161.id,null);
		//把天然气转成标准电 7.156KW*h/m³
		Float gas_power= BigDecimalUtil.divF(gasSumval,n,2);
		W=BigDecimalUtil.addF(powerSumval,gas_power);

		//总用水量 159
		Float waterSumval=getEnergyVal(airConditionerCurveReq,diagramId,ProductSid.SID159.id,null);
		//单位面积空调能耗（ECA）单位KW*h/m²
	 	// ECA=能耗总和/空调面积
		Float ECA=BigDecimalUtil.divF(W,A,2);
		//Qt=能耗总和*0.8+0.18*空调面积

		Float Q=BigDecimalUtil.addF(BigDecimalUtil.mulF(W,r),BigDecimalUtil.mulF(r1,A));
//		Qt=Q1+Q2
//		3、单位面积空调耗冷量（CCA） 单位KW*h/m²
		//	 * CCA=Q/A
		Float CCA=BigDecimalUtil.divF(Q,A,2);

//	* 4、空调系统能效比（EERs）
//	 * EERs=Q/能耗总和
		Float EERs=BigDecimalUtil.divF(Q,W,2);

//	 * 5、制冷系统能效比（EERr）
//	 * EERr=Q/制冷设备能耗总和

		//制冷设备能电的能耗总和 KW*h
		List<String> productDtypes =new ArrayList<>();
		productDtypes.add(ProductDtype.LENGSHUIJIZU.id);
		productDtypes.add(ProductDtype.LENGQUESHUIBENG.id);
		productDtypes.add(ProductDtype.LENGDONGSHUIBENG.id);
		productDtypes.add(ProductDtype.LENGQUETA.id);
		Float l_powerSumval=getEnergyVal(airConditionerCurveReq,diagramId,ProductSid.SID31.id,productDtypes);
		//制冷设备能燃气的能耗总和 立方米
		Float l_gasSumval=getEnergyVal(airConditionerCurveReq,diagramId,ProductSid.SID161.id,productDtypes);
		//气转成电
		Float l_gas_power= BigDecimalUtil.mulF(l_gasSumval,n);
		Float l_sum=BigDecimalUtil.addF(l_powerSumval,l_gas_power);
		Float EERr=BigDecimalUtil.divF(Q,l_sum,2);


//			*
//	 * 6、冷水机组运行效率(COP)
//			* COP=Q/冷水机组设备能耗总和
		List<String> jproductDtypes =new ArrayList<>();
		jproductDtypes.add(ProductDtype.LENGSHUIJIZU.id);
		Float j_powerSumval=getEnergyVal(airConditionerCurveReq,diagramId,ProductSid.SID31.id,jproductDtypes);
		//制冷设备能燃气的能耗总和 立方米
		Float j_gasSumval=getEnergyVal(airConditionerCurveReq,diagramId,ProductSid.SID161.id,jproductDtypes);
		//气转成电
		Float j_gas_power= BigDecimalUtil.divF(j_gasSumval,n,2);
		Float j_sum=BigDecimalUtil.addF(j_powerSumval,j_gas_power);
		Float COP=BigDecimalUtil.divF(Q,j_sum,2);







		List<AirConditionerData> list=new ArrayList<>();
		AirConditionerData airConditionerData=new AirConditionerData();
		airConditionerData.setVal(String.valueOf(powerSumval)+"KWH");
		airConditionerData.setName(AirConditionerDataType.WQ.value);
		airConditionerData.setType(AirConditionerDataType.WQ.id);
		list.add(airConditionerData);

		airConditionerData=new AirConditionerData();
		airConditionerData.setVal(String.valueOf(gasSumval)+"m³");
		airConditionerData.setName(AirConditionerDataType.GQ.value);
		airConditionerData.setType(AirConditionerDataType.GQ.id);
		list.add(airConditionerData);

		airConditionerData=new AirConditionerData();
		airConditionerData.setVal(String.valueOf(waterSumval)+"m³");
		airConditionerData.setName(AirConditionerDataType.SQ.value);
		airConditionerData.setType(AirConditionerDataType.SQ.id);
		list.add(airConditionerData);

		airConditionerData=new AirConditionerData();
		airConditionerData.setVal(String.valueOf(ECA)+"KW*h/m²");
		airConditionerData.setName(AirConditionerDataType.ECA.value);
		airConditionerData.setType(AirConditionerDataType.ECA.id);
		list.add(airConditionerData);

		airConditionerData=new AirConditionerData();
		airConditionerData.setVal(String.valueOf(CCA)+"KW*h/m²");
		airConditionerData.setName(AirConditionerDataType.CCA.value);
		airConditionerData.setType(AirConditionerDataType.CCA.id);
		list.add(airConditionerData);

		airConditionerData=new AirConditionerData();
		airConditionerData.setVal(String.valueOf(EERs)+"%");
		airConditionerData.setName(AirConditionerDataType.EERs.value);
		airConditionerData.setType(AirConditionerDataType.EERs.id);
		list.add(airConditionerData);

		airConditionerData=new AirConditionerData();
		airConditionerData.setVal(String.valueOf(EERr)+"%");
		airConditionerData.setName(AirConditionerDataType.EERr.value);
		airConditionerData.setType(AirConditionerDataType.EERr.id);
		list.add(airConditionerData);


		airConditionerData=new AirConditionerData();
		airConditionerData.setVal(String.valueOf(COP)+"%");
		airConditionerData.setName(AirConditionerDataType.COP.value);
		airConditionerData.setType(AirConditionerDataType.COP.id);
		list.add(airConditionerData);
		return R.data(list);
	}
	public Float getEnergyVal(AirConditionerCurveReq airConditionerCurveReq, Long diagramId, String propertyCodes,List<String> productDtypes) {
		CurveDataReq curveDataReq=new CurveDataReq();
		BeanUtils.copyProperties(airConditionerCurveReq,curveDataReq);
		CurveDataInfo curveDataInfo= CurveDataFactory.getCurveDataInfo(curveDataReq,true);

		String stime= curveDataInfo.getStime();
		String etime =curveDataInfo.getEtime();
		DeviceItemCycle itemCycle =curveDataInfo.getItemCycle();

		List<DiagramItem> itemList=queryItems(diagramId,propertyCodes,productDtypes);
		List<String> items=new ArrayList<>();
		for(DiagramItem item:itemList){
			items.add(item.getItemId());
		}
		//查询具体数据项
		List<DeviceItemHistoryDiffData> deviceItemHistoryDiffDatas = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
			CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY), stime, etime, itemCycle.id,HistoryDataType.TOTAL.id);
		Map<String, Float> ValMap= curveDataRepository.itemsSumValue(deviceItemHistoryDiffDatas);
		Float val=ValMap.get(XYDdatas.YSubVals);
		return val;
	}
	public List<DiagramItem> queryItems(Long diagramId, String propertyCodes,List<String> productDtypes){

		Map<String,Object> queryMap=new HashMap<>();
		queryMap.put("propertyCode",propertyCodes);
		if(Func.isNotEmpty(productDtypes) && productDtypes.size()>0){
			queryMap.put("productDtypes",productDtypes);
		}
		queryMap.put("id",diagramId);
		List<DiagramItemDTO> diagramItemDtoList=iDiagramItemService.getItem(queryMap);
		List<DiagramItem> diagramItemList =new ArrayList<>();
		for(DiagramItemDTO item:diagramItemDtoList){
			DiagramItem diagramItem= new DiagramItem();
			BeanUtils.copyProperties(item,diagramItem);
			diagramItemList.add(diagramItem);

		}
		return  diagramItemList;
	}

	public Map<String,List<CurveDataResq>> getCurveDataResq(AirConditionerCurveReq airConditionerCurveReq, List<DiagramItem> itemList,String unit, Float div) {

		CurveDataReq curveDataReq=new CurveDataReq();
		BeanUtils.copyProperties(airConditionerCurveReq,curveDataReq);
		CurveDataInfo curveDataInfo= CurveDataFactory.getCurveDataInfo(curveDataReq,false);
		//curveDataInfo.setPropertyCodes(propertyCode);
		Integer type=curveDataReq.getDateType();//
		String dataMolds=curveDataReq.getDataMolds();


		List<Object> xvals=curveDataRepository.valueRowsX(curveDataInfo);

		Map<String, List<CurveDataResq> > res = new HashMap<>();
		if(DataMold.getflgnow(dataMolds)){
			//查询具体数据项
			List<DeviceItemHistoryDiffData> ItemHistoryDiffDatas=curveDataRepository.queryItemHistoryDiffDatas(curveDataInfo,itemList,HistoryDataType.TOTAL.id);
			List<Float> yvals=new ArrayList<>();
			Map<String, List<Float>> datamap=curveDataRepository.groupSumValueByXY(ItemHistoryDiffDatas,curveDataInfo.getItemCycle(),curveDataInfo.getShowRows(), div);
			yvals=datamap.get(XYDdatas.YSubVals);
			CurveDataResq curveDataResq =new CurveDataResq();
			curveDataResq.setYvals((List<Object>)(List)yvals);
			curveDataResq.setXvals(xvals);
			curveDataResq.setTime(DateUtil.toString(curveDataInfo.getTime()));
			curveDataResq.setUnit(unit);

			List<CurveDataResq> list=new ArrayList<>();
			list.add(curveDataResq);
			res.put("curData",list);
		}
		if(DataMold.getflgTb(dataMolds)){//同比
			CurveDataReq curveDataReqtb= new CurveDataReq();
			BeanUtils.copyProperties(curveDataReq,curveDataReqtb);
			curveDataReqtb.setTime(DateUtil.addYear(curveDataReqtb.getTime(),-1));

			CurveDataInfo curveDataInfoTb=CurveDataFactory.getCurveDataInfo(curveDataReqtb,false);
			//查询具体数据项
			List<DeviceItemHistoryDiffData> ItemHistoryDiffDatas=curveDataRepository.queryItemHistoryDiffDatas(curveDataInfoTb,itemList,HistoryDataType.TOTAL.id);
			List<Float> yvals=new ArrayList<>();
			Map<String, List<Float>> datamap=curveDataRepository.groupSumValueByXY(ItemHistoryDiffDatas,curveDataInfoTb.getItemCycle(),curveDataInfoTb.getShowRows(),  div);
			yvals=datamap.get(XYDdatas.YSubVals);
			CurveDataResq curveDataResq =new CurveDataResq();
			curveDataResq.setYvals((List<Object>)(List)yvals);
			curveDataResq.setXvals(xvals);
			curveDataResq.setTime(DateUtil.toString(curveDataInfoTb.getTime()));
			curveDataResq.setUnit(unit);

			List<CurveDataResq> list=new ArrayList<>();
			list.add(curveDataResq);
			res.put("curDataTb", list);
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
			CurveDataInfo curveDataInfoHb = CurveDataFactory.getCurveDataInfo(curveDataReqhb,false);
			//查询具体数据项
			List<DeviceItemHistoryDiffData> ItemHistoryDiffDatas=curveDataRepository.queryItemHistoryDiffDatas(curveDataInfoHb,itemList,HistoryDataType.TOTAL.id);
			List<Float> yvals=new ArrayList<>();
			Map<String, List<Float>> datamap=curveDataRepository.groupSumValueByXY(ItemHistoryDiffDatas,curveDataInfoHb.getItemCycle(),curveDataInfoHb.getShowRows(),div);
			yvals=datamap.get(XYDdatas.YSubVals);
			CurveDataResq curveDataResq =new CurveDataResq();
			curveDataResq.setYvals((List<Object>)(List)yvals);
			curveDataResq.setXvals(xvals);
			curveDataResq.setTime(DateUtil.toString(curveDataInfoHb.getTime()));
			curveDataResq.setUnit(unit);

			List<CurveDataResq> list=new ArrayList<>();
			list.add(curveDataResq);
			res.put("curDataHb", list);
		}
		return res;
	}

	@PostMapping("/getAirConditionerCurve")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "获取空调数据曲线", notes = "airConditionerCurveReq")
	public R<Map<String,List<CurveDataResq>>>  getAirConditionerCurve(@RequestBody AirConditionerCurveReq airConditionerCurveReq){

		Map<String,List<CurveDataResq>> res=new HashMap<>();

		Long stationId= airConditionerCurveReq.getStationId();
		Long siteId= airConditionerCurveReq.getSiteId();
		//1.查询空调系统图
		Diagram diagram=new Diagram();
		diagram.setStationId(stationId);
		diagram.setSiteId(siteId);
		diagram.setIsDeleted(0);
		diagram.setDiagramType(DiagramType.KONGTIAN.id);
		Diagram airDiagram= iDiagramService.getOne(Condition.getQueryWrapper(diagram));
		if(Func.isEmpty(airDiagram)){
			return R.fail("没有空调系统设备哦，请先配置空调系统吧！");
		}
		//2.查询空调设备
		EecMeter eecMeter =new EecMeter();
		eecMeter.setStationId(stationId);
		eecMeter.setSiteId(siteId);
		eecMeter.setIsDeleted(0);
		EecMeter airCon = eecMeterService.getOne(Condition.getQueryWrapper(eecMeter));
		if(Func.isEmpty(airCon)){
			return R.fail("没有空调重点能耗哦！请先配置空调重点能耗吧！");
		}
		Long diagramId=airDiagram.getId();
		//A空调面积 单位m²
		Float A =airCon.getUnitArea();



		Integer dataType= airConditionerCurveReq.getDataType();
		List<DiagramItem> diagramItemList =new ArrayList<>();
		String propertyCodes="";
		List<String> productDtypes =new ArrayList<>();

		//耗电量
		if(Func.equals(AirConditionerDataType.WQ.id,dataType)){
			propertyCodes=ProductSid.SID31.id;
			diagramItemList=queryItems(diagramId,propertyCodes,productDtypes);
			res =getCurveDataResq(airConditionerCurveReq, diagramItemList,"kWh",null);
			return R.data(res);
		}
		//耗气量
		if(Func.equals(AirConditionerDataType.GQ.id,dataType)){
			propertyCodes=ProductSid.SID161.id;
			diagramItemList=queryItems(diagramId,propertyCodes,productDtypes);
			res =getCurveDataResq(airConditionerCurveReq, diagramItemList,"m³",null);

			return R.data(res);
		}
		//耗水量
		if(Func.equals(AirConditionerDataType.SQ.id,dataType)){
			propertyCodes=ProductSid.SID159.id;
			diagramItemList=queryItems(diagramId,propertyCodes,productDtypes);
			res =getCurveDataResq(airConditionerCurveReq, diagramItemList,"m³",null);
			return R.data(res);

		}

		//单位面积空调能耗（ECA）单位KW*h/m²
		// ECA=能耗总和/空调面积
		if(Func.equals(AirConditionerDataType.ECA.id,dataType)){
			//电的能耗总和 KW*h
			Float powerSumval=getEnergyVal(airConditionerCurveReq,diagramId,ProductSid.SID31.id,null);
			if(powerSumval>0){
				propertyCodes=ProductSid.SID31.id;
				diagramItemList=queryItems(diagramId,propertyCodes,productDtypes);
				//ECA=能耗总和/空调面积
				Map<String,List<CurveDataResq>> powerRes =getCurveDataResq(airConditionerCurveReq, diagramItemList,"KW*h/m²",A*100);
				return R.data(powerRes);
			}
			//燃气的能耗总和 立方米
			Float gasSumval=getEnergyVal(airConditionerCurveReq,diagramId,ProductSid.SID161.id,null);
			if(gasSumval>0) {
				propertyCodes = ProductSid.SID161.id;
				diagramItemList = queryItems(diagramId, propertyCodes, productDtypes);
				//把天然气转成标准电 7.156KW*h=1m³
				//ECA=能耗总和/空调面积     10/(1/7.156*100);
				Map<String, List<CurveDataResq>> gasRes = getCurveDataResq(airConditionerCurveReq, diagramItemList, "KW*h/m²",1/n*A*100);
				return R.data(gasRes);
			}

		}

		//Qt=能耗总和*0.8+0.18*空调面积
//		Qt=Q1+Q2
//		3、单位面积空调耗冷量（CCA） 单位KW*h/m²
		//	 * CCA=Q/A
//		Float CCA=BigDecimalUtil.divF(Q,A,2);

		if(Func.equals(AirConditionerDataType.CCA.id,dataType)){
			//电的能耗总和 KW*h
			Float powerSumval=getEnergyVal(airConditionerCurveReq,diagramId,ProductSid.SID31.id,null);
			if(powerSumval>0){
				propertyCodes=ProductSid.SID31.id;
				diagramItemList=queryItems(diagramId,propertyCodes,productDtypes);
				//CCA=Q/A
				Map<String,List<CurveDataResq>> powerRes =getCurveDataResq(airConditionerCurveReq, diagramItemList,"KW*h/m²",null);
				String dataMolds=airConditionerCurveReq.getDataMolds();
				//当前的查询数据
				if(DataMold.getflgnow(dataMolds)){
					List<CurveDataResq> list=powerRes.get("curData");
					for(CurveDataResq dataResq:list){
						List<Float> listy=(List<Float>)(List)dataResq.getYvals();
						List<Float> listvals=new ArrayList<>();
						for(Float val:listy){
							//Qt=能耗总和*0.8+0.18*空调面积
							// 单位面积空调耗冷量（CCA）CCA=Q/A
							listvals.add(BigDecimalUtil.divF(val*r+r1*A,A,2));
						}
						dataResq.setYvals((List<Object>)(List)listvals);
					}
					powerRes.put("curData",list);
				}
				if(DataMold.getflgTb(dataMolds)){
					List<CurveDataResq> list=powerRes.get("curDataTb");
					for(CurveDataResq dataResq:list){
						List<Float> listy=(List<Float>)(List)dataResq.getYvals();
						List<Float> listvals=new ArrayList<>();
						for(Float val:listy){
							//Qt=能耗总和*0.8+0.18*空调面积
							// 单位面积空调耗冷量（CCA）CCA=Q/A
							listvals.add(BigDecimalUtil.divF(val*r+r1*A,A,2));
						}
						dataResq.setYvals((List<Object>)(List)listvals);
					}
					powerRes.put("curDataTb",list);
				}
				if(DataMold.getflgHb(dataMolds)){
					List<CurveDataResq> list=powerRes.get("curDataHb");
					for(CurveDataResq dataResq:list){
						List<Float> listy=(List<Float>)(List)dataResq.getYvals();
						List<Float> listvals=new ArrayList<>();
						for(Float val:listy){
							//Qt=能耗总和*0.8+0.18*空调面积
							// 单位面积空调耗冷量（CCA）CCA=Q/A
							listvals.add(BigDecimalUtil.divF(val*r+r1*A,A,2));
						}
						dataResq.setYvals((List<Object>)(List)listvals);
					}
					powerRes.put("curDataHb",list);
				}
				return R.data(powerRes);
			}
			//燃气的能耗总和 立方米
			Float gasSumval=getEnergyVal(airConditionerCurveReq,diagramId,ProductSid.SID161.id,null);
			if(gasSumval>0) {
				propertyCodes = ProductSid.SID161.id;
				diagramItemList = queryItems(diagramId, propertyCodes, productDtypes);
				//把天然气转成标准电 7.156KW*h=1m³
				//CCA=Q/A
				Map<String,List<CurveDataResq>> gasRes =getCurveDataResq(airConditionerCurveReq, diagramItemList,"KW*h/m²",null);
				String dataMolds=airConditionerCurveReq.getDataMolds();
				//当前的查询数据
				if(DataMold.getflgnow(dataMolds)){
					List<CurveDataResq> list=gasRes.get("curData");
					for(CurveDataResq dataResq:list){
						List<Float> listy=(List<Float>)(List)dataResq.getYvals();
						List<Float> listvals=new ArrayList<>();
						for(Float val:listy){
							//Qt=能耗总和*0.8+0.18*空调面积
							// 单位面积空调耗冷量（CCA）CCA=Q/A
							listvals.add(BigDecimalUtil.divF(val*n*r+r1*A,A,2));
						}
						dataResq.setYvals((List<Object>)(List)listvals);
					}
					gasRes.put("curData",list);
				}
				if(DataMold.getflgTb(dataMolds)){
					List<CurveDataResq> list=gasRes.get("curDataTb");
					for(CurveDataResq dataResq:list){
						List<Float> listy=(List<Float>)(List)dataResq.getYvals();
						List<Float> listvals=new ArrayList<>();
						for(Float val:listy){
							//Qt=能耗总和*0.8+0.18*空调面积
							// 单位面积空调耗冷量（CCA）CCA=Q/A
							listvals.add(BigDecimalUtil.divF(val*n*r+r1*A,A,2));
						}
						dataResq.setYvals((List<Object>)(List)listvals);
					}
					gasRes.put("curDataTb",list);
				}
				if(DataMold.getflgHb(dataMolds)){
					List<CurveDataResq> list=gasRes.get("curDataHb");
					for(CurveDataResq dataResq:list){
						List<Float> listy=(List<Float>)(List)dataResq.getYvals();
						List<Float> listvals=new ArrayList<>();
						for(Float val:listy){
							//Qt=能耗总和*0.8+0.18*空调面积
							// 单位面积空调耗冷量（CCA）CCA=Q/A
							listvals.add(BigDecimalUtil.divF(val*n*r+r1*A,A,2));
						}
						dataResq.setYvals((List<Object>)(List)listvals);
					}
					gasRes.put("curDataHb",list);
				}
				return R.data(gasRes);
			}
		}

//	* 4、空调系统能效比（EERs）
//	 * EERs=Q/能耗总和
// Qt=能耗总和*0.8+0.18*空调面积
//		Qt=Q1+Q2
		if(Func.equals(AirConditionerDataType.EERs.id,dataType)){
			//电的能耗总和 KW*h
			Float powerSumval=getEnergyVal(airConditionerCurveReq,diagramId,ProductSid.SID31.id,null);
			if(powerSumval>0){
				propertyCodes=ProductSid.SID31.id;
				diagramItemList=queryItems(diagramId,propertyCodes,productDtypes);
				Map<String,List<CurveDataResq>> powerRes =getCurveDataResq(airConditionerCurveReq, diagramItemList,"%",null);
				String dataMolds=airConditionerCurveReq.getDataMolds();
				//当前的查询数据
				if(DataMold.getflgnow(dataMolds)){
					List<CurveDataResq> list=powerRes.get("curData");
					for(CurveDataResq dataResq:list){
						List<Float> listy=(List<Float>)(List)dataResq.getYvals();
						List<Float> listvals=new ArrayList<>();
						for(Float val:listy){
							//Qt=能耗总和*0.8+0.18*空调面积
							//空调系统能效比（EERs） EERs=Q/能耗总和
							listvals.add(BigDecimalUtil.divF(val*r+r1*A,val,2));
						}
						dataResq.setYvals((List<Object>)(List)listvals);
					}
					powerRes.put("curData",list);
				}
				if(DataMold.getflgTb(dataMolds)){
					List<CurveDataResq> list=powerRes.get("curDataTb");
					for(CurveDataResq dataResq:list){
						List<Float> listy=(List<Float>)(List)dataResq.getYvals();
						List<Float> listvals=new ArrayList<>();
						for(Float val:listy){
							//Qt=能耗总和*0.8+0.18*空调面积
							//空调系统能效比（EERs） EERs=Q/能耗总和
							listvals.add(BigDecimalUtil.divF(val*r+r1*A,val,2));
						}
						dataResq.setYvals((List<Object>)(List)listvals);
					}
					powerRes.put("curDataTb",list);
				}
				if(DataMold.getflgHb(dataMolds)){
					List<CurveDataResq> list=powerRes.get("curDataHb");
					for(CurveDataResq dataResq:list){
						List<Float> listy=(List<Float>)(List)dataResq.getYvals();
						List<Float> listvals=new ArrayList<>();
						for(Float val:listy){
							//Qt=能耗总和*0.8+0.18*空调面积
							//空调系统能效比（EERs） EERs=Q/能耗总和
							listvals.add(BigDecimalUtil.divF(val*r+r1*A,val,2));
						}
						dataResq.setYvals((List<Object>)(List)listvals);
					}
					powerRes.put("curDataHb",list);
				}
				return R.data(powerRes);
			}
			//燃气的能耗总和 立方米
			Float gasSumval=getEnergyVal(airConditionerCurveReq,diagramId,ProductSid.SID161.id,null);
			if(gasSumval>0) {
				propertyCodes = ProductSid.SID161.id;
				diagramItemList = queryItems(diagramId, propertyCodes, productDtypes);
				//把天然气转成标准电 7.156KW*h=1m³
				//CCA=Q/A
				Map<String,List<CurveDataResq>> gasRes =getCurveDataResq(airConditionerCurveReq, diagramItemList,"KW*h/m²",null);
				String dataMolds=airConditionerCurveReq.getDataMolds();
				//当前的查询数据
				if(DataMold.getflgnow(dataMolds)){
					List<CurveDataResq> list=gasRes.get("curData");
					for(CurveDataResq dataResq:list){
						List<Float> listy=(List<Float>)(List)dataResq.getYvals();
						List<Float> listvals=new ArrayList<>();
						for(Float val:listy){
							//Qt=能耗总和*0.8+0.18*空调面积
							//空调系统能效比（EERs） EERs=Q/能耗总和
							listvals.add(BigDecimalUtil.divF(val*r+r1*A,val,2));
						}
						dataResq.setYvals((List<Object>)(List)listvals);
					}
					gasRes.put("curData",list);
				}
				if(DataMold.getflgTb(dataMolds)){
					List<CurveDataResq> list=gasRes.get("curDataTb");
					for(CurveDataResq dataResq:list){
						List<Float> listy=(List<Float>)(List)dataResq.getYvals();
						List<Float> listvals=new ArrayList<>();
						for(Float val:listy){
							//Qt=能耗总和*0.8+0.18*空调面积
							//空调系统能效比（EERs） EERs=Q/能耗总和
							listvals.add(BigDecimalUtil.divF(val*r+r1*A,val,2));
						}
						dataResq.setYvals((List<Object>)(List)listvals);
					}
					gasRes.put("curDataTb",list);
				}
				if(DataMold.getflgHb(dataMolds)){
					List<CurveDataResq> list=gasRes.get("curDataHb");
					for(CurveDataResq dataResq:list){
						List<Float> listy=(List<Float>)(List)dataResq.getYvals();
						List<Float> listvals=new ArrayList<>();
						for(Float val:listy){
							//Qt=能耗总和*0.8+0.18*空调面积
							//空调系统能效比（EERs） EERs=Q/能耗总和
							listvals.add(BigDecimalUtil.divF(val*r+r1*A,val,2));
						}
						dataResq.setYvals((List<Object>)(List)listvals);
					}
					gasRes.put("curDataHb",list);
				}
				return R.data(gasRes);
			}
		}

//	 * 5、制冷系统能效比（EERr）
//	 * EERr=Q/制冷设备能耗总和

		if(Func.equals(AirConditionerDataType.EERr.id,dataType)){
			//制冷设备能电的能耗总和 KW*h
			List<String> productDtypes1 =new ArrayList<>();
			productDtypes1.add(ProductDtype.LENGSHUIJIZU.id);
			productDtypes1.add(ProductDtype.LENGQUESHUIBENG.id);
			productDtypes1.add(ProductDtype.LENGDONGSHUIBENG.id);
			productDtypes1.add(ProductDtype.LENGQUETA.id);
			Float l_powerSumval=getEnergyVal(airConditionerCurveReq,diagramId,ProductSid.SID31.id,productDtypes1);
			Float powerSumval=getEnergyVal(airConditionerCurveReq,diagramId,ProductSid.SID31.id,productDtypes1);
			if(powerSumval>0){
				propertyCodes=ProductSid.SID31.id;
				diagramItemList=queryItems(diagramId,propertyCodes,productDtypes);
				Map<String,List<CurveDataResq>> powerRes =getCurveDataResq(airConditionerCurveReq, diagramItemList,"%",null);
				String dataMolds=airConditionerCurveReq.getDataMolds();
				//当前的查询数据
				if(DataMold.getflgnow(dataMolds)){
					List<CurveDataResq> list=powerRes.get("curData");
					for(CurveDataResq dataResq:list){
						List<Float> listy=(List<Float>)(List)dataResq.getYvals();
						List<Float> listvals=new ArrayList<>();
						for(Float val:listy){
							//Qt=能耗总和*0.8+0.18*空调面积
							//空调系统能效比（EERs） EERs=Q/能耗总和
							listvals.add(BigDecimalUtil.divF(val*r+r1*A,val,2));
						}
						dataResq.setYvals((List<Object>)(List)listvals);
					}
					powerRes.put("curData",list);
				}
				if(DataMold.getflgTb(dataMolds)){
					List<CurveDataResq> list=powerRes.get("curDataTb");
					for(CurveDataResq dataResq:list){
						List<Float> listy=(List<Float>)(List)dataResq.getYvals();
						List<Float> listvals=new ArrayList<>();
						for(Float val:listy){
							//Qt=能耗总和*0.8+0.18*空调面积
							//空调系统能效比（EERs） EERs=Q/能耗总和
							listvals.add(BigDecimalUtil.divF(val*r+r1*A,val,2));
						}
						dataResq.setYvals((List<Object>)(List)listvals);
					}
					powerRes.put("curDataTb",list);
				}
				if(DataMold.getflgHb(dataMolds)){
					List<CurveDataResq> list=powerRes.get("curDataHb");
					for(CurveDataResq dataResq:list){
						List<Float> listy=(List<Float>)(List)dataResq.getYvals();
						List<Float> listvals=new ArrayList<>();
						for(Float val:listy){
							//Qt=能耗总和*0.8+0.18*空调面积
							//空调系统能效比（EERs） EERs=Q/能耗总和
							listvals.add(BigDecimalUtil.divF(val*r+r1*A,val,2));
						}
						dataResq.setYvals((List<Object>)(List)listvals);
					}
					powerRes.put("curDataHb",list);
				}
				return R.data(powerRes);
			}
			//燃气的能耗总和 立方米
			Float gasSumval=getEnergyVal(airConditionerCurveReq,diagramId,ProductSid.SID161.id,productDtypes1);
			if(gasSumval>0) {
				propertyCodes = ProductSid.SID161.id;
				diagramItemList = queryItems(diagramId, propertyCodes, productDtypes1);
				//把天然气转成标准电 7.156KW*h=1m³
				//CCA=Q/A
				Map<String,List<CurveDataResq>> gasRes =getCurveDataResq(airConditionerCurveReq, diagramItemList,"KW*h/m²",null);
				String dataMolds=airConditionerCurveReq.getDataMolds();
				//当前的查询数据
				if(DataMold.getflgnow(dataMolds)){
					List<CurveDataResq> list=gasRes.get("curData");
					for(CurveDataResq dataResq:list){
						List<Float> listy=(List<Float>)(List)dataResq.getYvals();
						List<Float> listvals=new ArrayList<>();
						for(Float val:listy){
							//Qt=能耗总和*0.8+0.18*空调面积
							//空调系统能效比（EERs） EERs=Q/能耗总和
							listvals.add(BigDecimalUtil.divF(val*r+r1*A,val,2));
						}
						dataResq.setYvals((List<Object>)(List)listvals);
					}
					gasRes.put("curData",list);
				}
				if(DataMold.getflgTb(dataMolds)){
					List<CurveDataResq> list=gasRes.get("curDataTb");
					for(CurveDataResq dataResq:list){
						List<Float> listy=(List<Float>)(List)dataResq.getYvals();
						List<Float> listvals=new ArrayList<>();
						for(Float val:listy){
							//Qt=能耗总和*0.8+0.18*空调面积
							//空调系统能效比（EERs） EERs=Q/能耗总和
							listvals.add(BigDecimalUtil.divF(val*r+r1*A,val,2));
						}
						dataResq.setYvals((List<Object>)(List)listvals);
					}
					gasRes.put("curDataTb",list);
				}
				if(DataMold.getflgHb(dataMolds)){
					List<CurveDataResq> list=gasRes.get("curDataHb");
					for(CurveDataResq dataResq:list){
						List<Float> listy=(List<Float>)(List)dataResq.getYvals();
						List<Float> listvals=new ArrayList<>();
						for(Float val:listy){
							//Qt=能耗总和*0.8+0.18*空调面积
							//空调系统能效比（EERs） EERs=Q/能耗总和
							listvals.add(BigDecimalUtil.divF(val*r+r1*A,val,2));
						}
						dataResq.setYvals((List<Object>)(List)listvals);
					}
					gasRes.put("curDataHb",list);
				}
				return R.data(gasRes);
			}
		}



		//	 * 6、冷水机组运行效率(COP)
//			* COP=Q/冷水机组设备能耗总和
		if(Func.equals(AirConditionerDataType.COP.id,dataType)){
			//制冷设备能电的能耗总和 KW*h
			List<String> productDtypes1 =new ArrayList<>();
			productDtypes1.add(ProductDtype.LENGSHUIJIZU.id);
			Float l_powerSumval=getEnergyVal(airConditionerCurveReq,diagramId,ProductSid.SID31.id,productDtypes1);
			Float powerSumval=getEnergyVal(airConditionerCurveReq,diagramId,ProductSid.SID31.id,productDtypes1);
			if(powerSumval>0){
				propertyCodes=ProductSid.SID31.id;
				diagramItemList=queryItems(diagramId,propertyCodes,productDtypes);
				Map<String,List<CurveDataResq>> powerRes =getCurveDataResq(airConditionerCurveReq, diagramItemList,"%",null);
				String dataMolds=airConditionerCurveReq.getDataMolds();
				//当前的查询数据
				if(DataMold.getflgnow(dataMolds)){
					List<CurveDataResq> list=powerRes.get("curData");
					for(CurveDataResq dataResq:list){
						List<Float> listy=(List<Float>)(List)dataResq.getYvals();
						List<Float> listvals=new ArrayList<>();
						for(Float val:listy){
							//Qt=能耗总和*0.8+0.18*空调面积
							//空调系统能效比（EERs） EERs=Q/能耗总和
							listvals.add(BigDecimalUtil.divF(val*r+r1*A,val,2));
						}
						dataResq.setYvals((List<Object>)(List)listvals);
					}
					powerRes.put("curData",list);
				}
				if(DataMold.getflgTb(dataMolds)){
					List<CurveDataResq> list=powerRes.get("curDataTb");
					for(CurveDataResq dataResq:list){
						List<Float> listy=(List<Float>)(List)dataResq.getYvals();
						List<Float> listvals=new ArrayList<>();
						for(Float val:listy){
							//Qt=能耗总和*0.8+0.18*空调面积
							//空调系统能效比（EERs） EERs=Q/能耗总和
							listvals.add(BigDecimalUtil.divF(val*r+r1*A,val,2));
						}
						dataResq.setYvals((List<Object>)(List)listvals);
					}
					powerRes.put("curDataTb",list);
				}
				if(DataMold.getflgHb(dataMolds)){
					List<CurveDataResq> list=powerRes.get("curDataHb");
					for(CurveDataResq dataResq:list){
						List<Float> listy=(List<Float>)(List)dataResq.getYvals();
						List<Float> listvals=new ArrayList<>();
						for(Float val:listy){
							//Qt=能耗总和*0.8+0.18*空调面积
							//空调系统能效比（EERs） EERs=Q/能耗总和
							listvals.add(BigDecimalUtil.divF(val*r+r1*A,val,2));
						}
						dataResq.setYvals((List<Object>)(List)listvals);
					}
					powerRes.put("curDataHb",list);
				}
				return R.data(powerRes);
			}
			//燃气的能耗总和 立方米
			Float gasSumval=getEnergyVal(airConditionerCurveReq,diagramId,ProductSid.SID161.id,productDtypes1);
			if(gasSumval>0) {
				propertyCodes = ProductSid.SID161.id;
				diagramItemList = queryItems(diagramId, propertyCodes, productDtypes1);
				//把天然气转成标准电 7.156KW*h=1m³
				//CCA=Q/A
				Map<String,List<CurveDataResq>> gasRes =getCurveDataResq(airConditionerCurveReq, diagramItemList,"KW*h/m²",null);
				String dataMolds=airConditionerCurveReq.getDataMolds();
				//当前的查询数据
				if(DataMold.getflgnow(dataMolds)){
					List<CurveDataResq> list=gasRes.get("curData");
					for(CurveDataResq dataResq:list){
						List<Float> listy=(List<Float>)(List)dataResq.getYvals();
						List<Float> listvals=new ArrayList<>();
						for(Float val:listy){
							//Qt=能耗总和*0.8+0.18*空调面积
							//空调系统能效比（EERs） EERs=Q/能耗总和
							listvals.add(BigDecimalUtil.divF(val*r+r1*A,val,2));
						}
						dataResq.setYvals((List<Object>)(List)listvals);
					}
					gasRes.put("curData",list);
				}
				if(DataMold.getflgTb(dataMolds)){
					List<CurveDataResq> list=gasRes.get("curDataTb");
					for(CurveDataResq dataResq:list){
						List<Float> listy=(List<Float>)(List)dataResq.getYvals();
						List<Float> listvals=new ArrayList<>();
						for(Float val:listy){
							//Qt=能耗总和*0.8+0.18*空调面积
							//空调系统能效比（EERs） EERs=Q/能耗总和
							listvals.add(BigDecimalUtil.divF(val*r+r1*A,val,2));
						}
						dataResq.setYvals((List<Object>)(List)listvals);
					}
					gasRes.put("curDataTb",list);
				}
				if(DataMold.getflgHb(dataMolds)){
					List<CurveDataResq> list=gasRes.get("curDataHb");
					for(CurveDataResq dataResq:list){
						List<Float> listy=(List<Float>)(List)dataResq.getYvals();
						List<Float> listvals=new ArrayList<>();
						for(Float val:listy){
							//Qt=能耗总和*0.8+0.18*空调面积
							//空调系统能效比（EERs） EERs=Q/能耗总和
							listvals.add(BigDecimalUtil.divF(val*r+r1*A,val,2));
						}
						dataResq.setYvals((List<Object>)(List)listvals);
					}
					gasRes.put("curDataHb",list);
				}
				return R.data(gasRes);
			}
		}

		return R.data(res);
	}

	@PostMapping("/getAirConditionerRunDate")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "运行工艺数据统计", notes = "airConditionerCurveReq")
	public R<Map<String,Object>> getAirConditionerRunDate(@RequestBody AirConditionerCurveReq airConditionerCurveReq){

		Map<String,Object> resMap =new HashMap<>();

		Long diagramProductId=airConditionerCurveReq.getDiagramProductId();
		Map<String,Object> queryMap=new HashMap<>();
		//queryMap.put("diagramProductId",diagramProductId);
		queryMap.put("stationId",airConditionerCurveReq.getStationId());
		queryMap.put("siteId",airConditionerCurveReq.getSiteId());
		queryMap.put("diagramType",DiagramType.KONGTIAN.id);
		List<DiagramItemDTO> diagramItemDtoList=iDiagramItemService.getItem(queryMap);

		Set<String> items=new HashSet<>();
		for(DiagramItemDTO item:diagramItemDtoList){
			items.add(item.getItemId());
		}

		CurveDataReq curveDataReq=new CurveDataReq();
		BeanUtils.copyProperties(airConditionerCurveReq,curveDataReq);
		CurveDataInfo curveDataInfo= CurveDataFactory.getCurveDataInfo(curveDataReq,true);

		List<String> headNames=new ArrayList<>();//用明细表头
		List<List<Object>> datas=new ArrayList<>();//用电明细数据
		List<Object> times= curveDataRepository.valueRowsX(curveDataInfo);

		Map<String,List<Object>> itemdataMap=new HashMap<>();

		List<DeviceItemHistoryDiffData> itemHistoryDiffData = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
			CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY), curveDataInfo.getStime(), curveDataInfo.getEtime(),
			curveDataInfo.getItemCycle().id, HistoryDataType.TOTAL.id);
		if(Func.isEmpty(itemHistoryDiffData)){
			return null;
		}

		Map<String ,List<DeviceItemHistoryDiffData>> HistoryDiffDataMap=curveDataRepository.buildMap(itemHistoryDiffData);
		headNames.add("时间");
		for(DiagramItemDTO item:diagramItemDtoList){
			headNames.add(item.getPropertyName());
			List<DeviceItemHistoryDiffData> diffData=HistoryDiffDataMap.get(item.getItemId());
			Map<String,List<Object>> ValMap= curveDataRepository.groupValueByXY(null,diffData,curveDataInfo.getItemCycle(),curveDataInfo.getShowRows());
			List<Object>  ydatas=new ArrayList<>();
			//总用水，电，气
			if(Func.equals(item.getPropertyCode(),ProductSid.SID31.id) || Func.equals(item.getPropertyCode(),ProductSid.SID159.id) || Func.equals(item.getPropertyCode(),ProductSid.SID161.id) ){
				ydatas=ValMap.get(XYDdatas.YSubVals);
			}else {
				ydatas=ValMap.get(XYDdatas.YVals);
			}
			itemdataMap.put(item.getItemId(),ydatas);
		}
		for(int i=0;i<times.size();i++){
			List<Object> vals=new ArrayList<>();
			vals.add(times.get(i));
			for(Map.Entry<String, List<Object>> mapdata:itemdataMap.entrySet()){
				if(Func.isNotEmpty(mapdata.getValue())){
					vals.add(mapdata.getValue().get(i));
				}else {
					vals.add("--");
				}
			}
			datas.add(vals);
		}
		resMap.put("head",headNames);
		resMap.put("data",datas);
		return  R.data(resMap);
	}


}
