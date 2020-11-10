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
import org.springblade.energy.diagram.entity.DiagramProduct;
import org.springblade.energy.diagram.service.IDiagramItemService;
import org.springblade.energy.diagram.service.IDiagramProductService;
import org.springblade.energy.runningmanagement.station.service.ISiteService;
import org.springblade.energy.statistics.dto.CurveDataInfo;
import org.springblade.energy.statistics.dto.CurveDataReq;
import org.springblade.energy.statistics.dto.CurveDataResq;
import org.springblade.energy.statistics.dto.WaterDataResq;
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
@RequestMapping("/water")
@Api(value = "能源管理-统计分析-水能数据分析", tags = "能源管理-水能数据分析")
public class WaterController {
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

//	@GetMapping("/getWaterDiagram")
//	@ApiOperationSupport(order = 1)
//	@ApiOperation(value = "获取供水水表", notes = "")
//	public R getWaterDiagram(@ApiParam(value = "站点id") @RequestParam(value = "stationId") Long stationId,
//						   @ApiParam(value = "位置id") @RequestParam(value = "siteId") Long siteId)
//	{
//
//		Map<String,Object> map=new HashMap<>();
//		map.put("stationId",stationId);
//		map.put("siteId",siteId);
//		map.put("diagramType",DiagramType.GONGSHUI.id);
//		map.put("productDtype", ProductDtype.SHUIBIAO.id);
//		List<DiagramProduct> diagramProducts= iDiagramProductService.queryDiagramProductByBtype(map);
//
//		if(Func.isEmpty(diagramProducts)){
//			return 	R.fail("无供水系统或水表");
//		}
//		List<EquipmentResq> equipmentResqs =new ArrayList<>();
//
//		for(DiagramProduct diagramProduct :diagramProducts) {
//			EquipmentResq equipmentResq = new EquipmentResq();
//			equipmentResq.setProductName(diagramProduct.getProductcname());
//			equipmentResq.setDiagramProductId(Func.toLong(diagramProduct.getId()));
//			equipmentResqs.add(equipmentResq);
//		}
//		return R.data(equipmentResqs);
//	}

	@PostMapping("/getUseWaterCurveDate")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "用水曲线图数据", notes = "")
	public R<Map<String,List<CurveDataResq>>> getUseWaterCurveDate(@RequestBody CurveDataReq curveDataReq) {
		R r = curveDataRepository.checkParam(curveDataReq);
		if (!r.isSuccess()) {
			return r;
		}

		DiagramProduct diagramProduct = iDiagramProductService.getById(curveDataReq.getDiagramProductId());
		if (Func.isEmpty(diagramProduct)) {
			return R.fail("系统图中未找到水表");
		}
		Integer dataCurveType=curveDataReq.getDataCurveType();
		String propertyCodes="";

		//总用水量
		if(Func.equals(CurveType.SHUILIANG.getId(),dataCurveType)){
			propertyCodes= ProductSid.SID159.id;
		}
		if(Func.isEmpty(propertyCodes)){
			return R.fail("CurveType参数有问题请检查");
		}
		Map<String, List<CurveDataResq>> res = curveDataRepository.getCurveData(curveDataReq, propertyCodes, ItemBtype.WATERVOLUME.id, HistoryDataType.TOTAL.id);

		//下级数据
//		List<String> items=new ArrayList<>();
//		Map<String,Object> queryMap =new HashMap<>();
//		queryMap.put("parentId",diagramProduct.getPindex());
//		queryMap.put("diagramId",diagramProduct.getId());
//		List<DiagramProduct> waterProducts = iDiagramProductService.querySonDiagramProduct(queryMap);
//		if(Func.isNotEmpty(waterProducts)){
//			for(DiagramProduct waterProduct1 :waterProducts){
//				DiagramItem waterItem=new DiagramItem();
//				waterItem.setPindex(waterProduct1.getPindex());
//				waterItem.setPropertyCode(propertyCodes);
//				List<DiagramItem> waterItems = iDiagramItemService.list(Condition.getQueryWrapper(waterItem));
//				for(DiagramItem item:waterItems){
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
	@PostMapping("/getUseWaterDateList")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "用水数据列表", notes = "")
	public R<List<WaterDataResq>> getUseWaterDateList(@RequestBody CurveDataReq curveDataReq){
		R r = curveDataRepository.checkParam(curveDataReq);
		if (!r.isSuccess()) {
			return r;
		}
		DiagramProduct diagramProduct = iDiagramProductService.getById(curveDataReq.getDiagramProductId());
		if (Func.isEmpty(diagramProduct)) {
			return R.fail("系统图中未找到水表");
		}
		curveDataReq.setDataMolds("0");//"数据类型用英文逗号分隔 0:当前查询日期数据 1:同比数据 2:环比数据"
		CurveDataInfo curveDataInfo= CurveDataFactory.getCurveDataInfo(curveDataReq,false);
		curveDataInfo.setPropertyCodes(ProductSid.SID159.id);//总用水量
		List<CurveDataResq> list=curveDataRepository.queryDiagramItemCurve(curveDataInfo,HistoryDataType.TOTAL.id, null);

		curveDataInfo.setPropertyCodes(ProductSid.SID165.id);//水温
		List<CurveDataResq> waterTemps=curveDataRepository.queryDiagramItemCurve(curveDataInfo, HistoryDataType.TOTAL.id,null);

		curveDataInfo.setPropertyCodes(ProductSid.SID215.id);//瞬时流量
		List<CurveDataResq> instFlows=curveDataRepository.queryDiagramItemCurve(curveDataInfo, HistoryDataType.TOTAL.id,null);

		List<WaterDataResq> waterDataResqList =new ArrayList<>();

		if(Func.isNotEmpty(list)){
			CurveDataResq data=list.get(0);
			List<Object> Ydata=new ArrayList<>();
			Ydata=data.getYvals();

			List<Object> waterYdata = new ArrayList<>();
			if(Func.isNotEmpty(waterTemps)) {
				CurveDataResq waterdata = waterTemps.get(0);
				waterYdata = waterdata.getYvals();
			}
			List<Object> flowsYdata = new ArrayList<>();
			if(Func.isNotEmpty(instFlows)) {
				CurveDataResq dataFlows = instFlows.get(0);
				flowsYdata = dataFlows.getYvals();
			}

			for(int i=0;i<curveDataInfo.getShowRows().size();i++){
				WaterDataResq dataResq=new WaterDataResq();
				dataResq.setSiteId(curveDataInfo.getSiteId());
				dataResq.setSiteName(diagramProduct.getProductcname());
				dataResq.setTime(curveDataInfo.getShowRows().get(i));
				if(Func.isNotEmpty(Ydata)){
					dataResq.setVal(Func.toStr(Ydata.get(i)+"m³"));
				}
				if(Func.isNotEmpty(waterYdata)){
					dataResq.setWaterTemp(Func.toStr(waterYdata.get(i)+"℃"));
				}
				if(Func.isNotEmpty(flowsYdata)){
					dataResq.setInstFlow(Func.toStr(flowsYdata.get(i)+"m3/h"));
				}

				waterDataResqList.add(dataResq);
			}
		}

		return R.data(waterDataResqList);
	}

	/**
	 * 水能报表
	 * @return
	 */
	@GetMapping("/waterReport")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "水能报表", notes = "")
	public R<Map<String,Object>> waterReport(@ApiParam(value = "站点id", required = true) @RequestParam Long stationId,
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
		headNames.add(3,"总用水量");
		headNames.add(4,"名称");
		if(Func.equals(CommonDateType.DAY.getId(),dateType)){
			headNames.add(5,"日用水量");
			for(int i=0;i<timeRows.size();i++){
				headNames.add(6+i,timeRows.get(i)+"时");
			}
		}
		if(Func.equals(CommonDateType.MONTH.getId(),dateType)){
			headNames.add(5,"月用水量");
			for(int i=0;i<timeRows.size();i++){
				headNames.add(6+i,timeRows.get(i)+"日");
			}
		}
		if(Func.equals(CommonDateType.YEAR.getId(),dateType)){
			headNames.add(5,"年用水量");
			for(int i=0;i<timeRows.size();i++){
				headNames.add(6+i,timeRows.get(i)+"月");
			}
		}


		int rows=headNames.size();

		Map<String,Object> queryMap=new HashMap<>();
		queryMap.put("propertyCode",ProductSid.SID159.id);
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
