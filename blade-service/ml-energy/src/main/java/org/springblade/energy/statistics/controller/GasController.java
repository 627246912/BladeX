package org.springblade.energy.statistics.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.bean.DeviceItemHistoryDiffData;
import org.springblade.constants.CoreCommonConstant;
import org.springblade.constants.XYDdatas;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.diagram.dto.DiagramItemDTO;
import org.springblade.energy.diagram.entity.DiagramItem;
import org.springblade.energy.diagram.entity.DiagramProduct;
import org.springblade.energy.diagram.service.IDiagramItemService;
import org.springblade.energy.diagram.service.IDiagramProductService;
import org.springblade.energy.runningmanagement.station.service.ISiteService;
import org.springblade.energy.statistics.dto.*;
import org.springblade.energy.statistics.repository.CurveDataRepository;
import org.springblade.enums.*;
import org.springblade.gw.feign.IDeviceItemHistoryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author bond
 * @date 2020/6/15 9:59
 * @desc
 */
@RestController
@AllArgsConstructor
@RequestMapping("/gas")
@Api(value = "能源管理-统计分析-气能数据分析", tags = "能源管理-气能数据分析")
public class GasController {
	@Autowired
	private IDiagramProductService iDiagramProductService;
	@Autowired
	private IDiagramItemService iDiagramItemService;
	@Autowired
	private CurveDataRepository curveDataRepository;
	@Autowired
	private IDeviceItemHistoryClient iDeviceItemHistoryClient;
	@Autowired
	private ISiteService iSiteService;


	@PostMapping("/getUseGasCurveDate")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "用气曲线图数据", notes = "")
	public R<Map<String,List<CurveDataResq>>> getUseGasCurveDate(@RequestBody CurveDataReq curveDataReq) {
		R r = curveDataRepository.checkParam(curveDataReq);
		if (!r.isSuccess()) {
			return r;
		}

		DiagramProduct diagramProduct = iDiagramProductService.getById(curveDataReq.getDiagramProductId());
		if (Func.isEmpty(diagramProduct)) {
			return R.fail("系统图中未找到燃气表");
		}
		Integer dataCurveType=curveDataReq.getDataCurveType();
		String propertyCodes="";

		int btype= ItemBtype.AIRVOLUME.id;
		if(Func.equals(CurveType.QILIANG.getId(),dataCurveType)){
			propertyCodes= ProductSid.SID161.id;
		}
		if(Func.isEmpty(propertyCodes)){
			return R.fail("CurveType参数有问题请检查");
		}

		Map<String, List<CurveDataResq>> res = curveDataRepository.getCurveData(curveDataReq, propertyCodes,btype, HistoryDataType.TOTAL.id);

		//下级数据
//		List<String> items=new ArrayList<>();
//		Map<String,Object> queryMap =new HashMap<>();
//		queryMap.put("pindex",diagramProduct.getPindex());
//		queryMap.put("diagramId",diagramProduct.getId());
//		List<DiagramProduct> gasProducts = iDiagramProductService.querySonDiagramProduct(queryMap);
//
//		if(Func.isNotEmpty(gasProducts)){
//			for(DiagramProduct gasProduct1 :gasProducts){
//				DiagramItem gasItem=new DiagramItem();
//				gasItem.setPindex(gasProduct1.getPindex());
//				gasItem.setPropertyCode(propertyCodes);
//				List<DiagramItem> gasItems = iDiagramItemService.list(Condition.getQueryWrapper(gasItem));
//				for(DiagramItem item:gasItems){
//					items.add(item.getItemId());
//				}
//			}
//
//		}
//		CurveDataInfo curveDataInfo= CurveDataFactory.getCurveDataInfo(curveDataReq);
//		if(Func.isNotEmpty(items)){
//			//到数据中心查询数据
//			List<DeviceItemHistoryDiffData> deviceItemHistoryDiffDatas = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
//				CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY), curveDataInfo.getStime(), curveDataInfo.getEtime(), curveDataInfo.getItemCycle().id,HistoryDataType.TOTAL.id);
//			Map<String, List<Float>> datamap = curveDataRepository.groupSumValueByXY(deviceItemHistoryDiffDatas,curveDataInfo.getItemCycle(),curveDataInfo.getShowRows(),null);
//			List<CurveDataResq> curDataLower= new ArrayList<>();
//			CurveDataResq curData = new CurveDataResq();
//			curData.setTime(curveDataInfo.getStime());
//			//curData.setXvals(datamap.get(""));
//			List<Float> Ydata=datamap.get(XYDdatas.YVals);
//			curData.setYvals((List<Object>)(List)Ydata);
//			res.put("curDataLower", curDataLower);
//		}
		return R.data(res);
	}
	@PostMapping("/getUseGasDateList")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "用气数据列表", notes = "")
	public R<List<GasDataResq>> getUseGasDateList(@RequestBody CurveDataReq curveDataReq){


		R r = curveDataRepository.checkParam(curveDataReq);
		if (!r.isSuccess()) {
			return r;
		}
		DiagramProduct diagramProduct = iDiagramProductService.getById(curveDataReq.getDiagramProductId());
		if (Func.isEmpty(diagramProduct)) {
			return R.fail("系统图中未找到燃气表");
		}
		curveDataReq.setDataMolds("0");//"数据类型用英文逗号分隔 0:当前查询日期数据 1:同比数据 2:环比数据"
		CurveDataInfo curveDataInfo= CurveDataFactory.getCurveDataInfo(curveDataReq,false);
		curveDataInfo.setPropertyCodes(ProductSid.SID161.id);//标况气量
		List<CurveDataResq> list=curveDataRepository.queryDiagramItemCurve(curveDataInfo,HistoryDataType.TOTAL.id,null);
		List<GasDataResq> gasDataResqList =new ArrayList<>();

		curveDataInfo.setPropertyCodes(ProductSid.SID160.id);//气压
		List<CurveDataResq> listpressure=curveDataRepository.queryDiagramItemCurve(curveDataInfo,HistoryDataType.TOTAL.id, null);

		curveDataInfo.setPropertyCodes(ProductSid.SID165.id);//气温度
		List<CurveDataResq> listgasTemp=curveDataRepository.queryDiagramItemCurve(curveDataInfo, HistoryDataType.TOTAL.id,null);

		curveDataInfo.setPropertyCodes(ProductSid.SID217.id);//标况瞬时流量
		List<CurveDataResq> listnormFlow=curveDataRepository.queryDiagramItemCurve(curveDataInfo,HistoryDataType.TOTAL.id,null);

		curveDataInfo.setPropertyCodes(ProductSid.SID218.id);//工况瞬时流量
		List<CurveDataResq> listworkFlow=curveDataRepository.queryDiagramItemCurve(curveDataInfo,HistoryDataType.TOTAL.id,null);

		if(Func.isNotEmpty(list)){
			List<Object> Ydata=list.get(0).getYvals();

			List<Object> pressureYdata= new ArrayList<>();
			if(Func.isNotEmpty(listpressure)){
				pressureYdata=listpressure.get(0).getYvals();
			}
			List<Object> normFlowYdata=new ArrayList<>();
			if(Func.isNotEmpty(listnormFlow)){
				normFlowYdata=listnormFlow.get(0).getYvals();
			}
			List<Object> workFlowYdata=new ArrayList<>();
			if(Func.isNotEmpty(listworkFlow)){
				workFlowYdata=listworkFlow.get(0).getYvals();
			}
			List<Object> gasTempYdata=new ArrayList<>();
			if(Func.isNotEmpty(listgasTemp)){
				gasTempYdata=listgasTemp.get(0).getYvals();
			}

			for(int i=0;i<curveDataInfo.getShowRows().size();i++){
				GasDataResq dataResq=new GasDataResq();
				dataResq.setSiteId(curveDataInfo.getSiteId());
				dataResq.setSiteName(diagramProduct.getProductcname());
				dataResq.setTime(curveDataInfo.getShowRows().get(i));
				if(Func.isNotEmpty(Ydata)){
					dataResq.setVal(Func.toStr(Ydata.get(i)+"m³"));
				}
				if(Func.isNotEmpty(normFlowYdata)){
					dataResq.setNormFlow(Func.toStr(normFlowYdata.get(i)+"m³"));
				}
				if(Func.isNotEmpty(pressureYdata)){
					dataResq.setPressure(Func.toStr(pressureYdata.get(i)+"m³"));
				}
				if(Func.isNotEmpty(workFlowYdata)){
					dataResq.setWorkFlow(Func.toStr(workFlowYdata.get(i)+"m³"));
				}
				if(Func.isNotEmpty(gasTempYdata)){
					dataResq.setGasTemp(Func.toStr(gasTempYdata.get(i)+"℃"));
				}

				gasDataResqList.add(dataResq);
			}
		}

		return R.data(gasDataResqList);
	}


	@PostMapping("/getPressureCurveDate")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "压力曲线图数据", notes = "")
	public R<Map<String,List<CurveDataResq>>> getPressureCurveDate(@RequestBody CurveDataReq curveDataReq) {
		R r = curveDataRepository.checkParam(curveDataReq);
		if (!r.isSuccess()) {
			return r;
		}

		DiagramProduct diagramProduct = iDiagramProductService.getById(curveDataReq.getDiagramProductId());
		if (Func.isEmpty(diagramProduct)) {
			return R.fail("系统图中未找到燃气表");
		}
		//curveDataReq.setDataMolds("0,2");//"数据类型用英文逗号分隔 0:当前查询日期数据 1:同比数据 2:环比数据"
		String propertyCodes = ProductSid.SID160.id;//气压
		Map<String, List<CurveDataResq>> res = curveDataRepository.getCurveData(curveDataReq, propertyCodes,null, HistoryDataType.TOTAL.id);

//		//下级数据
//		List<String> items=new ArrayList<>();
//		DiagramProduct gasProduct=new DiagramProduct();
//		gasProduct.setParentId(diagramProduct.getPindex());
//		List<DiagramProduct> gasProducts = iDiagramProductService.list(Condition.getQueryWrapper(gasProduct));
//		if(Func.isNotEmpty(gasProducts)){
//			for(DiagramProduct gasProduct1 :gasProducts){
//				DiagramItem gasItem=new DiagramItem();
//				gasItem.setPindex(gasProduct1.getPindex());
//				gasItem.setPropertyCode(propertyCodes);
//				List<DiagramItem> gasItems = iDiagramItemService.list(Condition.getQueryWrapper(gasItem));
//				for(DiagramItem item:gasItems){
//					items.add(item.getItemId());
//				}
//			}
//
//		}
//		CurveDataInfo curveDataInfo= CurveDataFactory.getCurveDataInfo(curveDataReq);
//		if(Func.isNotEmpty(items)){
//			//到数据中心查询数据
//			List<DeviceItemHistoryDiffData> deviceItemHistoryDiffDatas = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
//				CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY), curveDataInfo.getStime(), curveDataInfo.getEtime(), curveDataInfo.getItemCycle().id,HistoryDataType.TOTAL.id);
//			Map<String, List<Float>> datamap = curveDataRepository.groupSumValueByXY(deviceItemHistoryDiffDatas,curveDataInfo.getItemCycle(),curveDataInfo.getShowRows(),null);
//			List<CurveDataResq> curDataLower= new ArrayList<>();
//			CurveDataResq curData = new CurveDataResq();
//			curData.setTime(curveDataInfo.getStime());
//			//curData.setXvals(datamap.get(""));
//			List<Float> Ydata=datamap.get(XYDdatas.YVals);
//			curData.setYvals((List<Object>)(List)Ydata);
//			res.put("curDataLower", curDataLower);
//		}

		return R.data(res);
	}
	@PostMapping("/getPressureDateList")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "压力列表数据", notes = "")
	public R<List<GasPressureResq>> getPressureDateList(@RequestBody CurveDataReq curveDataReq) {
		R r = curveDataRepository.checkParam(curveDataReq);
		if (!r.isSuccess()) {
			return r;
		}

		DiagramProduct diagramProduct = iDiagramProductService.getById(curveDataReq.getDiagramProductId());
		if (Func.isEmpty(diagramProduct)) {
			return R.fail("系统图中未找到燃气表");
		}
		//curveDataReq.setDataMolds("0,2");//"数据类型用英文逗号分隔 0:当前查询日期数据 1:同比数据 2:环比数据"
		String propertyCodes = ProductSid.SID160.id;//气压
		CurveDataInfo curveDataInfo= CurveDataFactory.getCurveDataInfo(curveDataReq,false);
		curveDataInfo.setPropertyCodes(ProductSid.SID160.id);//气压

		//查询具体数据项
		Map<String,Object> queryMap=new HashMap<>();
		queryMap.put("diagramProductId",diagramProduct.getId());
		queryMap.put("propertyCode",propertyCodes);
		//queryMap.put("btype",ItemBtype.PRESSURE.id);
		List<DiagramItem> diagramItemList=iDiagramItemService.selectDiagramItemByMap(queryMap);

		List<String> items =new ArrayList<>();
		for(DiagramItem diagramItem:diagramItemList) {
			items.add(diagramItem.getItemId());
		}

		List<DeviceItemHistoryDiffData> deviceItemHistoryDiffDatas = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
				CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY), curveDataInfo.getStime(), curveDataInfo.getEtime(), curveDataInfo.getItemCycle().id,HistoryDataType.TOTAL.id);

		Map<String, List<Object>> datamap = curveDataRepository.groupValueByXY(null,deviceItemHistoryDiffDatas,curveDataInfo.getItemCycle(),curveDataInfo.getShowRows());

		//
		//List<Object> Xdata=datamap.get(XYDdatas.XVals);
		List<Object> Ydata=datamap.get(XYDdatas.YVals);
		List<Object> YMaxs=datamap.get(XYDdatas.YMaxs);
		List<Object> YMins=datamap.get(XYDdatas.YMins);

		List<GasPressureResq> gasDataResqList =new ArrayList<>();

		for(int i=0;i<curveDataInfo.getShowRows().size();i++) {
			GasPressureResq dataResq = new GasPressureResq();
			dataResq.setProductName(diagramProduct.getProductcname());
			dataResq.setTime(curveDataInfo.getShowRows().get(i));
			if(Func.isNotEmpty(Ydata) && Func.isNotEmpty(YMaxs) && Func.isNotEmpty(YMins)) {
				dataResq.setVal(Ydata.get(i).toString());
				dataResq.setMaxval(YMaxs.get(i).toString());
				dataResq.setMinval(YMins.get(i).toString());
			}
			gasDataResqList.add(dataResq);
		}
	return R.data(gasDataResqList);
	}


	/**
	 * 气能报表
	 * @return
	 */
	@GetMapping("/gasReport")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "气能报表", notes = "")
	public R<Map<String,Object>> gasReport(@ApiParam(value = "站点id", required = true) @RequestParam Long stationId,
											 @ApiParam(value = "时间0:小时报表，2:日报表 ,3:月报表", required = true) @RequestParam Integer dateType,
										   @ApiParam(value = "开始时间", required = true) @RequestParam Date startTime,
										   @ApiParam(value = "结束时间", required = true) @RequestParam Date endTime
	) {

		CurveDataReq curveDataReq=new CurveDataReq();
		curveDataReq.setTime(startTime);
		curveDataReq.setEtime(endTime);
		curveDataReq.setDateType(dateType);
		CurveDataInfo curveDataInfo= ReportDataFactory.getCurveDataInfo(curveDataReq);

		String stime= curveDataInfo.getStime();
		String etime =curveDataInfo.getEtime();
		DeviceItemCycle itemCycle =curveDataInfo.getItemCycle();
		List<Object> timeRows= curveDataRepository.valueRowsXRopert(curveDataInfo);
		List<String> headNames=new ArrayList<>();//用明细表头
		List<List<Object>> datas=new ArrayList<>();//用电明细数据
		headNames.add(0,"数据项");
		headNames.add(1,"位置id");
		headNames.add(2,"位置");
		headNames.add(3,"总用气量");
		headNames.add(4,"名称");
		if(Func.equals(CommonDateType.DAY.getId(),dateType)){
			headNames.add(5,"日用气量");
			for(int i=0;i<timeRows.size();i++){
				headNames.add(6+i,timeRows.get(i)+"时");
			}
		}
		if(Func.equals(CommonDateType.MONTH.getId(),dateType)){
			headNames.add(5,"月用气量");
			for(int i=0;i<timeRows.size();i++){
				headNames.add(6+i,timeRows.get(i)+"日");
			}
		}
		if(Func.equals(CommonDateType.YEAR.getId(),dateType)){
			headNames.add(5,"年用气量");
			for(int i=0;i<timeRows.size();i++){
				headNames.add(6+i,timeRows.get(i)+"月");
			}
		}


		int rows=headNames.size();

		Map<String,Object> queryMap=new HashMap<>();
		queryMap.put("propertyCode",ProductSid.SID161.id);
		queryMap.put("stationId",stationId);
		//queryMap.put("btype",ItemBtype.ELECTRICITY.id);
		List<DiagramItemDTO> diagramItemList=iDiagramItemService.getItem(queryMap);

		Set<Long> sites=new HashSet<>();//位置
		List<String> allitems=new ArrayList<>();//数据项
		Map<Long,List<String>> groupSite=new HashMap<>();//key位置 ，分组数据项
		Map<String,Float> itemTotalValMap=new HashMap<>();//key数据项，数据项数据之和
		Map<Long,Float> siteTotalValMap=new HashMap<>();//key位置，数据项数据之和
		Map<String, List<Float>> YVals=new HashMap<>();//key数据项，数据

		for(DiagramItemDTO diagramItem:diagramItemList){
			allitems.add(diagramItem.getItemId());
			groupSite.put(diagramItem.getSiteId(),null);
			sites.add(diagramItem.getSiteId());
		}
		for(Long site:sites){
			siteTotalValMap.put(site,0f);
			List<String> items=new ArrayList<>();
			for(DiagramItemDTO diagramItem:diagramItemList){
				if(Func.equals(site,diagramItem.getSiteId())){
					items.add(diagramItem.getItemId());
				}
			}
			groupSite.put(site,items);
		}

		//查询具体数据项数据
		List<DeviceItemHistoryDiffData> deviceItemHistoryDiffDatas = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
			CollectionUtil.join(allitems, CoreCommonConstant.SPLIT_COMMA_KEY)
			, stime, etime, itemCycle.id, HistoryDataType.TOTAL.id);
		if(Func.isNotEmpty(deviceItemHistoryDiffDatas)) {
			Map<String, List<DeviceItemHistoryDiffData>> itemHistoryDiffDatasMap = curveDataRepository.buildMap(deviceItemHistoryDiffDatas);

			for (DiagramItemDTO diagramItem : diagramItemList) {
				List<DeviceItemHistoryDiffData> ItemHistoryDiffDatas = itemHistoryDiffDatasMap.get(diagramItem.getItemId());
				Map<String, Float> itemsSumValueMap = curveDataRepository.itemsSumValue(ItemHistoryDiffDatas);
				Float totalSubVals = itemsSumValueMap.get(XYDdatas.YSubVals);
				itemTotalValMap.put(diagramItem.getItemId(), totalSubVals);
				Float siteTotalVal = totalSubVals + siteTotalValMap.get(diagramItem.getSiteId());
				siteTotalValMap.put(diagramItem.getSiteId(), siteTotalVal);

				Map<String, List<Float>> valMap = curveDataRepository.groupSumValueByXYReport(ItemHistoryDiffDatas, itemCycle, curveDataInfo.getShowRows(), null);
				List<Float> YSubVals = valMap.get(XYDdatas.YSubVals);
				YVals.put(diagramItem.getItemId(), YSubVals);
			}

			for (DiagramItemDTO diagramItem : diagramItemList) {
				List<DeviceItemHistoryDiffData> ItemHistoryDiffDatas = itemHistoryDiffDatasMap.get(diagramItem.getItemId());
				List<Object> nodesdata = new ArrayList<>();
				for (int i = 0; i < rows; i++) {
					nodesdata.add(i, null);
				}
				nodesdata.set(0, diagramItem.getItemId());
				nodesdata.set(1, diagramItem.getSiteId().toString());
				nodesdata.set(2, diagramItem.getSiteName());

				nodesdata.set(3, siteTotalValMap.get(diagramItem.getSiteId()));
				nodesdata.set(4, diagramItem.getProductcname());
				nodesdata.set(5, itemTotalValMap.get(diagramItem.getItemId()));
				List<Float> YSubVals = YVals.get(diagramItem.getItemId());
				for (int i = 0; i < YSubVals.size(); i++) {
					nodesdata.set(6 + i, YSubVals.get(i));
				}

				datas.add(nodesdata);
			}
		}

		Map<String,Object> resMap =new HashMap<>();
		resMap.put("head",headNames);
		resMap.put("data",datas);
		return R.data(resMap);

	}

}
