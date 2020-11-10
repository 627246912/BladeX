package org.springblade.energy.statistics.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.bean.DeviceItemHistoryDiffData;
import org.springblade.constants.CoreCommonConstant;
import org.springblade.constants.XYDdatas;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.diagram.dto.DiagramItemDTO;
import org.springblade.energy.diagram.entity.DiagramItem;
import org.springblade.energy.diagram.service.IDiagramItemService;
import org.springblade.energy.energymanagement.entity.EnergyTar;
import org.springblade.energy.energymanagement.service.IEnergyTarService;
import org.springblade.energy.energymanagement.service.IGasMeterService;
import org.springblade.energy.energymanagement.service.IPowerMeterService;
import org.springblade.energy.energymanagement.service.IWaterMeterService;
import org.springblade.energy.statistics.dto.*;
import org.springblade.energy.statistics.enums.DataMold;
import org.springblade.energy.statistics.repository.CurveDataRepository;
import org.springblade.enums.*;
import org.springblade.gw.feign.IDeviceItemHistoryClient;
import org.springblade.system.entity.Dept;
import org.springblade.system.feign.ISysClient;
import org.springblade.util.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bond
 * @date 2020/6/28 10:14
 * @desc 能耗分析
 */
@RestController
@AllArgsConstructor
@RequestMapping("/cae")
@Api(value = "能源管理-能耗分析", tags = "能源管理-能耗分析")
public class DeptEnergyCaeController {

	@Autowired
	private CurveDataRepository curveDataRepository;
	@Autowired
	private IDiagramItemService iDiagramItemService;

	@Autowired
	private IEnergyTarService iEnergyTarService;

	@Autowired
	private IDeviceItemHistoryClient iDeviceItemHistoryClient;

	private IPowerMeterService iPowerMeterService;
	private IWaterMeterService iWaterMeterService;
	private IGasMeterService iGasMeterService;
	@Autowired
	private ISysClient sysClient;

	/**
	 * 能耗分析-能源指标设置
	 * @param energyTar
	 * @return
	 */
	@PostMapping("/energyTarSet")
	@ApiOperationSupport(order = 22)
	@ApiOperation(value = "指标调整", notes = "EnergyTar")
	public R energyTarSet(@RequestBody EnergyTar energyTar){

		EnergyTar entity =new EnergyTar();
		entity.setDeptId(energyTar.getDeptId());
		entity.setStationId(energyTar.getStationId());
		entity.setSiteId(energyTar.getSiteId());
		entity.setType(energyTar.getType());
		entity.setEnergyType(energyTar.getEnergyType());
		EnergyTar have=iEnergyTarService.getOne(Condition.getQueryWrapper(entity));

		Boolean res =false;
		if(Func.isNotEmpty(have)){
			energyTar.setId(have.getId());
			res =iEnergyTarService.updateById(energyTar);
		}else{
			res =iEnergyTarService.save(energyTar);
		}
	return R.data(res);

	}


	/**
	 * 能耗分析-能源指标
	 * @param energyTarCurveReq
	 * @return
	 */
	@PostMapping("/energyTarCurve")
	@ApiOperationSupport(order = 21)
	@ApiOperation(value = "能源指标曲线", notes = "energyTarCurveReq")
	//R<Map<String,List<CurveDataResq>>>
	public R<Map<String,Object>> energyTarCurve(@RequestBody EnergyTarCurveReq energyTarCurveReq) {
		return R.data(energyTarCurveRealize(energyTarCurveReq));
	}
	/**
	 * 能耗分析-能源指标
	 * @param energyTarCurveReq
	 * @return
	 */
	public Map<String,Object> energyTarCurveRealize(EnergyTarCurveReq energyTarCurveReq) {
		Map<String,Object> res = new HashMap<>();
		EnergyTar energyTar=new EnergyTar();
		energyTar.setEnergyType(energyTarCurveReq.getEnergyType());
		energyTar.setType(energyTarCurveReq.getDateType());
		energyTar.setStationId(energyTarCurveReq.getStationId());
		energyTar.setSiteId(energyTarCurveReq.getSiteId());
		energyTar.setDeptId(energyTarCurveReq.getDeptId());
		EnergyTar have=iEnergyTarService.getOne(Condition.getQueryWrapper(energyTar));

		Integer energyType=energyTarCurveReq.getEnergyType();

		Integer type=energyTarCurveReq.getDateType();//2:月3:年8:季度
		String propertyCodes=ProductSid.SID31.id;;
		Integer btype=ItemBtype.ELECTRICITY.id;
		//电能（电量）
		if(Func.equals(1,energyType)){
			propertyCodes=ProductSid.SID31.id;
			btype= ItemBtype.ELECTRICITY.id;
		}
		//水能（水量）//
		if(Func.equals(2,energyType)){
			propertyCodes=ProductSid.SID159.id;
			btype= ItemBtype.WATERVOLUME.id;
		}//气能（气量）
		if(Func.equals(3,energyType)){
			propertyCodes=ProductSid.SID161.id;
			btype= ItemBtype.AIRVOLUME.id;
		}
		List<DiagramItem> diagramItemList=queryConsumeItems(energyTarCurveReq,propertyCodes,btype);
		if(Func.isEmpty(diagramItemList)){
			return null;
		}

		CurveDataReq curveDataReq=new CurveDataReq();
		BeanUtils.copyProperties(energyTarCurveReq,curveDataReq);
		curveDataReq.setDateType(type);

		CurveDataInfo curveDataInfo= CurveDataFactory.getCurveDataInfo(curveDataReq,false);
		curveDataInfo.setPropertyCodes(propertyCodes);
		//查询具体数据项
		List<CurveDataResq> itemCurveDataReqlist= curveDataRepository.queryDiagramItemCurve(curveDataInfo,diagramItemList, HistoryDataType.TOTAL.id,btype);
		res.put("curData", itemCurveDataReqlist);

		String dataMolds=curveDataReq.getDataMolds();
		//同比
		if(DataMold.getflgTb(dataMolds)) {
				CurveDataReq curveDataReqLast = new CurveDataReq();
				BeanUtils.copyProperties(curveDataReq, curveDataReqLast);
				curveDataReqLast.setTime(DateUtil.addYear(curveDataReqLast.getTime(), -1));
				CurveDataInfo curveDataInfoLast = CurveDataFactory.getCurveDataInfo(curveDataReqLast,false);
				curveDataInfoLast.setPropertyCodes(propertyCodes);

				//查询具体数据项
				List<CurveDataResq> itemCurveDataReqLastlist = curveDataRepository.queryDiagramItemCurve(curveDataInfoLast, diagramItemList, HistoryDataType.TOTAL.id,btype);
				res.put("curDataTb", itemCurveDataReqLastlist);
		}
		//环比
		if(DataMold.getflgHb(dataMolds)) {
			CurveDataReq curveDataReqLast = new CurveDataReq();
			BeanUtils.copyProperties(curveDataReq, curveDataReqLast);
			if (Func.equals(CommonDateType.DAY.getId(), type)) {
				curveDataReqLast.setTime(DateUtil.addHour(curveDataReqLast.getTime(), -24));
			}
			if (Func.equals(CommonDateType.MONTH.getId(), type)) {
				curveDataReqLast.setTime(DateUtil.addMonth(curveDataReqLast.getTime(), -1));
			}
			if (Func.equals(CommonDateType.QUARTER.getId(), type)) {
				curveDataReqLast.setTime(DateUtil.addMonth(curveDataReqLast.getTime(), -3));
			}
			if (Func.equals(CommonDateType.YEAR.getId(), type)) {
				curveDataReqLast.setTime(DateUtil.addYear(curveDataReqLast.getTime(), -1));
			}
			CurveDataInfo curveDataInfoLast = CurveDataFactory.getCurveDataInfo(curveDataReqLast,false);
			curveDataInfoLast.setPropertyCodes(propertyCodes);
			//查询具体数据项
			List<CurveDataResq> itemCurveDataReqLastlist = curveDataRepository.queryDiagramItemCurve(curveDataInfoLast, diagramItemList, HistoryDataType.TOTAL.id,btype);
			res.put("curDataHb", itemCurveDataReqLastlist);
		}
		if(Func.isNotEmpty(have)){
			res.put("tar",have.getTar() );

		}else {
			res.put("tar","" );
		}
		return res;
	}

	/**
	 * 查询-能源能耗数据项
	 * @param energyTarCurveReq
	 * @return
	 */
	public List<DiagramItem> queryConsumeItems(EnergyTarCurveReq energyTarCurveReq,String propertyCodes, int btype){
		Long deptId=energyTarCurveReq.getDeptId();
		Long stationId=energyTarCurveReq.getStationId();
		Long siteId=energyTarCurveReq.getSiteId();

		Map<String,Object> queryMap=new HashMap<>();
		queryMap.put("stationId",stationId);
		queryMap.put("btype",btype);
		queryMap.put("propertyCode",propertyCodes);
		queryMap.put("siteId",siteId);
		queryMap.put("deptId",deptId);
		List<DiagramItemDTO> diagramItemDtoList=iDiagramItemService.getItem(queryMap);
		List<DiagramItem> diagramItemList =new ArrayList<>();
		for(DiagramItemDTO item:diagramItemDtoList){
				DiagramItem diagramItem= new DiagramItem();
				BeanUtils.copyProperties(item,diagramItem);
				diagramItemList.add(diagramItem);

		}
		return  diagramItemList;
	}

	public Float getEnergyVal(EnergyTarCurveReq energyTarCurveReq,String propertyCodes,int btype) {
		CurveDataReq curveDataReq=new CurveDataReq();
		BeanUtils.copyProperties(energyTarCurveReq,curveDataReq);
		CurveDataInfo curveDataInfo= CurveDataFactory.getCurveDataInfo(curveDataReq,false);

		String stime= curveDataInfo.getStime();
		String etime =curveDataInfo.getEtime();
		DeviceItemCycle itemCycle =curveDataInfo.getItemCycle();

		List<DiagramItem> itemList=queryConsumeItems(energyTarCurveReq,propertyCodes,btype);
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

	@PostMapping("/energyConsume")
	@ApiOperationSupport(order = 23)
	@ApiOperation(value = "部门能耗-能源消耗", notes = "energyTarCurveReq")
	public R<Map<String,Object>> energyConsume(@RequestBody EnergyTarCurveReq energyTarCurveReq) {
		Map<String,Object> res = new HashMap<>();
		String propertyCodes="";
		//电能（电量）
		propertyCodes=ProductSid.SID31.id;
		Float powerval=getEnergyVal( energyTarCurveReq, propertyCodes,ItemBtype.ELECTRICITY.id);

		//水能（水量）
		propertyCodes=ProductSid.SID159.id;
		Float waterval=getEnergyVal( energyTarCurveReq, propertyCodes,ItemBtype.WATERVOLUME.id);

		//气能（气量）
		propertyCodes=ProductSid.SID161.id;
		Float gasval=getEnergyVal( energyTarCurveReq, propertyCodes,ItemBtype.AIRVOLUME.id);

		res.put("powerval",powerval);
		res.put("waterval",waterval);
		res.put("gasval",gasval);

		return R.data(res);
	}

	@PostMapping("/powerEnergySp")
	@ApiOperationSupport(order = 24)
	@ApiOperation(value = "部门能耗-电能占比", notes = "energyTarCurveReq")
	public R<Map<String,Object>> powerEnergySp(@RequestBody EnergyTarCurveReq energyTarCurveReq) {
		Map<String,Object> res = new HashMap<>();

		String propertyCodes="";
		//电能（电量）
		propertyCodes=ProductSid.SID31.id;
		int btype=ItemBtype.ELECTRICITY.id;

		CurveDataReq curveDataReq=new CurveDataReq();
		BeanUtils.copyProperties(energyTarCurveReq,curveDataReq);
		CurveDataInfo curveDataInfo= CurveDataFactory.getCurveDataInfo(curveDataReq,false);

		String stime= curveDataInfo.getStime();
		String etime =curveDataInfo.getEtime();
		DeviceItemCycle itemCycle =curveDataInfo.getItemCycle();

		List<DiagramItem> itemList=queryConsumeItems(energyTarCurveReq,propertyCodes,btype);
		List<String> items=new ArrayList<>();
		for(DiagramItem item:itemList){
			items.add(item.getItemId());
		}
		//查询具体数据项
		List<DeviceItemHistoryDiffData> deviceItemHistoryDiffDatas = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
			CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY), stime, etime, itemCycle.id,null);

		List<DeviceItemHistoryDiffData>  topHistoryDiffDatas =new ArrayList<>();//尖数据
		List<DeviceItemHistoryDiffData>  peakHistoryDiffDatas =new ArrayList<>();//峰数据
		List<DeviceItemHistoryDiffData>  flatHistoryDiffDatas =new ArrayList<>();//平数据
		List<DeviceItemHistoryDiffData>  valleyHistoryDiffDatas =new ArrayList<>();//谷数据

		for(DeviceItemHistoryDiffData data:deviceItemHistoryDiffDatas){
			if(Func.equals(HistoryDataType.TOP.id,data.getCtg())){
				topHistoryDiffDatas.add(data);
			}
			if(Func.equals(HistoryDataType.PEAK.id,data.getCtg())){
				peakHistoryDiffDatas.add(data);
			}
			if(Func.equals(HistoryDataType.FLAT.id,data.getCtg())){
				flatHistoryDiffDatas.add(data);
			}
			if(Func.equals(HistoryDataType.VALLEY.id,data.getCtg())){
				valleyHistoryDiffDatas.add(data);
			}
		}
		Map<String, Float> topMap= curveDataRepository.itemsSumValue(topHistoryDiffDatas);
		Float topval=topMap.get(XYDdatas.YSubVals);
		Map<String, Float> peakMap= curveDataRepository.itemsSumValue(peakHistoryDiffDatas);
		Float peakval=peakMap.get(XYDdatas.YSubVals);
		Map<String, Float> flatMap= curveDataRepository.itemsSumValue(flatHistoryDiffDatas);
		Float flatval=flatMap.get(XYDdatas.YSubVals);
		Map<String, Float> valleyMap= curveDataRepository.itemsSumValue(valleyHistoryDiffDatas);
		Float valleyval=valleyMap.get(XYDdatas.YSubVals);
		//Float allval=topval+peakval+flatval+valleyval;
		res.put("topval",topval);
		res.put("peakval",peakval);
		res.put("flatval",flatval);
		res.put("valleyval",valleyval);

		return R.data(res);
	}
	/**
	 * 能耗分析-部门能耗-对标分析
	 * @param energyTarCurveReq
	 * @return
	 */
	@PostMapping("/energyTarReport")
	@ApiOperationSupport(order = 25)
	@ApiOperation(value = "部门能耗-对标分析报表", notes = "energyTarCurveReq")
	public R<List<EnergyTarReport>> energyTarReport(@RequestBody EnergyTarCurveReq energyTarCurveReq) {
		String dataMolds=energyTarCurveReq.getDataMolds();
		Integer type=energyTarCurveReq.getDateType();//1:日 2:月3:年8:季度

		List<EnergyTarReport> reportList=new ArrayList<>();
		EnergyTarReport PenergyTar=new EnergyTarReport();
		PenergyTar.setEnergyName("电");
		EnergyTarReport WenergyTar=new EnergyTarReport();
		WenergyTar.setEnergyName("水");
		EnergyTarReport GenergyTar=new EnergyTarReport();
		GenergyTar.setEnergyName("气");


		//电能（电量）
		Float powerval=getEnergyVal(energyTarCurveReq, ProductSid.SID31.id,ItemBtype.ELECTRICITY.id);
		//水能（水量）
		Float waterval=getEnergyVal(energyTarCurveReq, ProductSid.SID159.id,ItemBtype.WATERVOLUME.id);
		//气能（气量）
		Float gasval=getEnergyVal(energyTarCurveReq, ProductSid.SID161.id,ItemBtype.AIRVOLUME.id);

		//同比
		if(DataMold.getflgTb(dataMolds)) {
			energyTarCurveReq.setTime(DateUtil.addYear(energyTarCurveReq.getTime(), -1));
			//电能（电量）
			Float powervalTb=getEnergyVal(energyTarCurveReq, ProductSid.SID31.id,ItemBtype.ELECTRICITY.id);

			Float pval=powerval-powervalTb;
			PenergyTar.setVal(String.valueOf(pval)+"kWh");//上升下降
			Float prate=(pval/powerval)*100;
			PenergyTar.setRate(String.valueOf(prate)+"%");

			//水能（水量）
			Float watervalTb=getEnergyVal(energyTarCurveReq, ProductSid.SID159.id,ItemBtype.WATERVOLUME.id);
			Float wval=waterval-watervalTb;
			WenergyTar.setVal(String.valueOf(wval)+"T");
			Float wrate=(wval/waterval)*100;
			WenergyTar.setRate(String.valueOf(wrate)+"%");

			//气能（气量）
			Float gasvalTb=getEnergyVal(energyTarCurveReq, ProductSid.SID161.id,ItemBtype.AIRVOLUME.id);
			Float gval=gasval-gasvalTb;
			GenergyTar.setVal(String.valueOf(gval)+"m³");
			Float grate=(wval/gasval)*100;
			GenergyTar.setRate(String.valueOf(grate)+"%");
		}
		//环比
		if(DataMold.getflgHb(dataMolds)) {
			if (Func.equals(CommonDateType.DAY.getId(), type)) {
				energyTarCurveReq.setTime(DateUtil.addHour(energyTarCurveReq.getTime(), -24));
			}
			if (Func.equals(CommonDateType.MONTH.getId(), type)) {
				energyTarCurveReq.setTime(DateUtil.addMonth(energyTarCurveReq.getTime(), -1));
			}
			if (Func.equals(CommonDateType.QUARTER.getId(), type)) {
				energyTarCurveReq.setTime(DateUtil.addMonth(energyTarCurveReq.getTime(), -3));
			}
			if (Func.equals(CommonDateType.YEAR.getId(), type)) {
				energyTarCurveReq.setTime(DateUtil.addYear(energyTarCurveReq.getTime(), -1));
			}
			//电能（电量）
			Float powervalHb=getEnergyVal(energyTarCurveReq, ProductSid.SID31.id,ItemBtype.ELECTRICITY.id);

			Float pval=powerval-powervalHb;
			PenergyTar.setVal(String.valueOf(pval)+"kWh");//上升下降
			Float prate=(pval/powerval)*100;
			PenergyTar.setRate(String.valueOf(prate)+"%");
			//水能（水量）
			Float watervalHb=getEnergyVal(energyTarCurveReq,  ProductSid.SID159.id,ItemBtype.WATERVOLUME.id);
			Float wval=waterval-watervalHb;
			WenergyTar.setVal(String.valueOf(wval)+"T");
			Float wrate=(wval/waterval)*100;
			WenergyTar.setRate(String.valueOf(wrate)+"%");
			//气能（气量）
			Float gasvalHb=getEnergyVal(energyTarCurveReq,  ProductSid.SID161.id,ItemBtype.AIRVOLUME.id);
			Float gval=gasval-gasvalHb;
			GenergyTar.setVal(String.valueOf(gval)+"m³");
			Float grate=(wval/gasval)*100;
			GenergyTar.setRate(String.valueOf(grate)+"%");

		}
		reportList.add(PenergyTar);
		reportList.add(WenergyTar);
		reportList.add(GenergyTar);

		return R.data(reportList);
	}


	@PostMapping("/deptEnergyReportForms")
	@ApiOperationSupport(order = 26)
	@ApiOperation(value = "部门能耗-能耗统计报表", notes = "energyTarCurveReq")
	public R<Map<String,Object>> deptEnergyReportForms(@RequestBody EnergyTarCurveReq energyTarCurveReq) {
		Map<String,Object> res = new HashMap<>();

		CurveDataReq curveDataReq=new CurveDataReq();
		BeanUtils.copyProperties(energyTarCurveReq,curveDataReq);
		CurveDataInfo curveDataInfo= CurveDataFactory.getCurveDataInfo(curveDataReq,false);

		String stime= curveDataInfo.getStime();
		String etime =curveDataInfo.getEtime();
		DeviceItemCycle itemCycle =curveDataInfo.getItemCycle();


		String propertyCodes="";
		//电能（电量）
		propertyCodes=ProductSid.SID31.id;
		int btype=ItemBtype.ELECTRICITY.id;
		List<DiagramItem> itemList=queryConsumeItems(energyTarCurveReq,propertyCodes,btype);
		List<String> items=new ArrayList<>();
		for(DiagramItem item:itemList){
			items.add(item.getItemId());
		}
		//查询具体数据项
		List<DeviceItemHistoryDiffData> deviceItemHistoryDiffDatas = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
			CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY), stime, etime, itemCycle.id,null);


		List<DeviceItemHistoryDiffData>  totalHistoryDiffDatas =new ArrayList<>();//总数据
		List<DeviceItemHistoryDiffData>  topHistoryDiffDatas =new ArrayList<>();//尖数据
		List<DeviceItemHistoryDiffData>  peakHistoryDiffDatas =new ArrayList<>();//峰数据
		List<DeviceItemHistoryDiffData>  flatHistoryDiffDatas =new ArrayList<>();//平数据
		List<DeviceItemHistoryDiffData>  valleyHistoryDiffDatas =new ArrayList<>();//谷数据

		for(DeviceItemHistoryDiffData data:deviceItemHistoryDiffDatas){
			if(Func.equals(HistoryDataType.TOTAL.id,data.getCtg())){
				totalHistoryDiffDatas.add(data);
			}
			if(Func.equals(HistoryDataType.TOP.id,data.getCtg())){
				topHistoryDiffDatas.add(data);
			}
			if(Func.equals(HistoryDataType.PEAK.id,data.getCtg())){
				peakHistoryDiffDatas.add(data);
			}
			if(Func.equals(HistoryDataType.FLAT.id,data.getCtg())){
				flatHistoryDiffDatas.add(data);
			}
			if(Func.equals(HistoryDataType.VALLEY.id,data.getCtg())){
				valleyHistoryDiffDatas.add(data);
			}
		}

		//电的总量
		Map<String, List<Float>> totalMap= curveDataRepository.groupSumValueByXY(totalHistoryDiffDatas, itemCycle, curveDataInfo.getShowRows(),null);
		List<Float> totalvals=totalMap.get(XYDdatas.YSubVals);
		//尖
		Map<String, Float> topMap= curveDataRepository.itemsSumValue(topHistoryDiffDatas);
		Float topvals=topMap.get(XYDdatas.YSubVals);
		//峰
		Map<String, Float> peakMap= curveDataRepository.itemsSumValue(peakHistoryDiffDatas);
		Float peakvals=peakMap.get(XYDdatas.YSubVals);
		//平
		Map<String, Float> flatMap= curveDataRepository.itemsSumValue(flatHistoryDiffDatas);
		Float flatvals=flatMap.get(XYDdatas.YSubVals);
		//谷
		Map<String, Float> valleyMap= curveDataRepository.itemsSumValue(valleyHistoryDiffDatas);
		Float valleyvals=valleyMap.get(XYDdatas.YSubVals);


		//Float allval=topval+peakval+flatval+valleyval;
//
//		res.put("topval",topval);
//		res.put("peakval",peakval);
//		res.put("flatval",flatval);
//		res.put("valleyval",valleyval);

		return R.data(res);
	}


	@PostMapping("/deptEnergyConsumeStatics")
	@ApiOperationSupport(order = 27)
	@ApiOperation(value = "部门能耗-部门用电量统计", notes = "energyTarCurveReq")
	public R<List<DeptStatisticDTO>> deptEnergyConsumeStatics(@RequestBody EnergyTarCurveReq energyTarCurveReq) {
		List<DeptStatisticDTO> resObject = new ArrayList<>();
		//获取所有部门数据
		R<List<Dept>> allDepts = sysClient.getAllDept();
		if(Func.isNotEmpty(allDepts) && Func.isNotEmpty(allDepts.getData())){
			for(Dept dept: allDepts.getData()){
				Map<String,Object> res = new HashMap<>();
				String propertyCodes="";
				//电能（电量）
				propertyCodes=ProductSid.SID31.id;
				energyTarCurveReq.setDeptId(dept.getId());
				energyTarCurveReq.setSiteId(null);
				energyTarCurveReq.setStationId(null);
				Float powerval=getEnergyVal( energyTarCurveReq, propertyCodes,ItemBtype.ELECTRICITY.id);

				DeptStatisticDTO dto = new DeptStatisticDTO();
				dto.setDeptId(dept.getId());
				dto.setDeptName(dept.getDeptName());
				dto.setPowerVal(powerval);
				resObject.add(dto);
			}
		}
		return R.data(resObject);
	}

}
