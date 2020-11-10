package org.springblade.energy.statistics.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.bean.DeviceItemHistoryDiffData;
import org.springblade.constants.CoreCommonConstant;
import org.springblade.constants.CurveTypeDatas;
import org.springblade.constants.XYDdatas;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.dto.NameValue;
import org.springblade.energy.diagram.dto.DiagramItemDTO;
import org.springblade.energy.diagram.entity.DiagramItem;
import org.springblade.energy.diagram.service.IDiagramItemService;
import org.springblade.energy.energymanagement.service.IEnergyTarService;
import org.springblade.energy.statistics.dto.CurveDataInfo;
import org.springblade.energy.statistics.dto.CurveDataReq;
import org.springblade.energy.statistics.dto.CurveDataResq;
import org.springblade.energy.statistics.dto.EnergyTarCurveReq;
import org.springblade.energy.statistics.enums.DataMold;
import org.springblade.energy.statistics.repository.CurveDataRepository;
import org.springblade.enums.*;
import org.springblade.gw.feign.IDeviceItemHistoryClient;
import org.springblade.util.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author bond
 * @date 2020/6/28 10:14
 * @desc 费用管理
 */
@RestController
@AllArgsConstructor
@RequestMapping("/cost")
@Api(value = "能源管理-费用管理", tags = "能源管理-费用管理")
public class DeptCostController {

	@Autowired
	private CurveDataRepository curveDataRepository;
	@Autowired
	private IDiagramItemService iDiagramItemService;

	@Autowired
	private IEnergyTarService iEnergyTarService;

	@Autowired
	private IDeviceItemHistoryClient iDeviceItemHistoryClient;

	@PostMapping("/deptCost")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "部门分摊费用占比及明细", notes = "energyTarCurveReq")
	public R<Map<String,Object>> deptCost(@RequestBody EnergyTarCurveReq energyTarCurveReq) {
		Map<String,Object> resMap =new HashMap<>();
		List<NameValue> resqList=new ArrayList<>();
		List<String> headNames=new ArrayList<>();//用明细表头
		List<List<Object>> datas=new ArrayList<>();//用电明细数据

		headNames.add("时间");

		String propertyCodes="";
		//电能（电量）费用
		propertyCodes= ProductSid.SID31.id;
		Map<String,Object> powerDataMap=getDatas( energyTarCurveReq, propertyCodes, ItemBtype.ELECTRICITY.id);
		NameValue nameValue=new NameValue();
		nameValue.setName("用电");
		nameValue.setValue(powerDataMap.get("totalCost"));
		resqList.add(nameValue);
		List<Object> powerYPrices= (List<Object>)powerDataMap.get(XYDdatas.YPrices);
		List<Object> times= (List<Object>)powerDataMap.get(XYDdatas.XVals);

		headNames.add("用电(元)");

		//水能（水量）费用
		propertyCodes=ProductSid.SID159.id;
		Map<String,Object> waterDataMap=getDatas( energyTarCurveReq, propertyCodes,ItemBtype.WATERVOLUME.id);
		nameValue=new NameValue();
		nameValue.setName("用水");
		nameValue.setValue(waterDataMap.get("totalCost"));
		resqList.add(nameValue);
		List<Object> waterYPrices= (List<Object>)waterDataMap.get(XYDdatas.YPrices);

		headNames.add("用水(元)");

		//气能（气量）费用
		propertyCodes=ProductSid.SID161.id;
		Map<String,Object> gasDataMap=getDatas( energyTarCurveReq, propertyCodes,ItemBtype.AIRVOLUME.id);
		nameValue=new NameValue();
		nameValue.setName("用气");
		nameValue.setValue(gasDataMap.get("totalCost"));
		resqList.add(nameValue);
		List<Object> gasYPrices= (List<Object>)gasDataMap.get(XYDdatas.YPrices);

		headNames.add("用气(元)");

		for(int i=0;i<times.size();i++){
			List<Object> vals=new ArrayList<>();
			vals.add(times.get(i));
			vals.add(powerYPrices.get(i));
			vals.add(waterYPrices.get(i));
			vals.add(gasYPrices.get(i));
			datas.add(vals);
		}

		resMap.put("proportion",resqList);
		resMap.put("head",headNames);
		resMap.put("data",datas);
		return R.data(resMap);
	}
	/**
	 * 查询部门能耗-能源能耗数据项
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
	public Map<String,Object> getDatas(EnergyTarCurveReq energyTarCurveReq,String propertyCodes,int btype) {
		Map<String,Object> res =new HashMap<>();

		CurveDataReq curveDataReq=new CurveDataReq();
		BeanUtils.copyProperties(energyTarCurveReq,curveDataReq);
		CurveDataInfo curveDataInfo= CurveDataFactory.getCurveDataInfo(curveDataReq,true);

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
			CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY), stime, etime, itemCycle.id, HistoryDataType.TOTAL.id);
		Map<String, Float> allCostMap= curveDataRepository.itemsSumValue(deviceItemHistoryDiffDatas);
		Float totalCost=allCostMap.get(XYDdatas.YPrices);
		res.put("totalCost",totalCost);
		Map<String, List<Float>> valMap=curveDataRepository.groupSumValueByXY(deviceItemHistoryDiffDatas,itemCycle,curveDataInfo.getShowRows(),null);
		res.put(XYDdatas.YPrices,valMap.get(XYDdatas.YPrices));
		res.put(XYDdatas.XVals,curveDataInfo.getShowRows());
		return res;
	}

	@PostMapping("/deptCostCurve")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "部门分摊费用曲线图", notes = "energyTarCurveReq")
	public R<Map<String,List<CurveDataResq>>> deptCostCurve(@RequestBody EnergyTarCurveReq energyTarCurveReq) {
		Map<String, List<CurveDataResq>> res = new HashMap<>();

		Integer type=energyTarCurveReq.getDateType();

		List<String> propertyCodes=new ArrayList<>();
		propertyCodes.add(ProductSid.SID31.id);
		propertyCodes.add(ProductSid.SID159.id);
		propertyCodes.add(ProductSid.SID161.id);
		List<Integer> btypes=new ArrayList<>();
		btypes.add(ItemBtype.ELECTRICITY.id);
		btypes.add(ItemBtype.WATERVOLUME.id);
		btypes.add(ItemBtype.AIRVOLUME.id);

		Long deptId=energyTarCurveReq.getDeptId();
		Long stationId=energyTarCurveReq.getStationId();
		Long siteId=energyTarCurveReq.getSiteId();

		Map<String,Object> queryMap=new HashMap<>();
		queryMap.put("stationId",stationId);
		queryMap.put("btypes",btypes);
		queryMap.put("propertyCodes",propertyCodes);
		queryMap.put("siteId",siteId);
		queryMap.put("deptId",deptId);
		List<DiagramItemDTO> diagramItemDtoList=iDiagramItemService.getItem(queryMap);
		Set<String> items=new HashSet<>();
		for(DiagramItem item:diagramItemDtoList){
			items.add(item.getItemId());
		}



		String dataMolds=energyTarCurveReq.getDataMolds();
		if(DataMold.getflgnow(dataMolds)){
			CurveDataReq curveDataReq=new CurveDataReq();
			BeanUtils.copyProperties(energyTarCurveReq,curveDataReq);
			CurveDataInfo curveDataInfo= CurveDataFactory.getCurveDataInfo(curveDataReq,false);
			List<Object> xvals=curveDataRepository.valueRowsX(curveDataInfo);
			String stime= curveDataInfo.getStime();
			String etime =curveDataInfo.getEtime();
			DeviceItemCycle itemCycle =curveDataInfo.getItemCycle();

			//查询具体数据项
			List<DeviceItemHistoryDiffData> deviceItemHistoryDiffDatas = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
				CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY), stime, etime, itemCycle.id, HistoryDataType.TOTAL.id);
			Map<String, List<Float>> valMap=curveDataRepository.groupSumValueByXY(deviceItemHistoryDiffDatas,itemCycle,curveDataInfo.getShowRows(),null);
			List<Float> yvals= valMap.get(XYDdatas.YPrices);

			CurveDataResq curveDataResq =new CurveDataResq();
			curveDataResq.setYvals((List<Object>)(List)yvals);
			curveDataResq.setXvals(xvals);
			curveDataResq.setTime(DateUtil.toString(DateUtil.getStartDate(curveDataInfo.getTime())));
			curveDataResq.setUnit("元");
			List<CurveDataResq> list=new ArrayList<>();
			list.add(curveDataResq);
			res.put(CurveTypeDatas.curData, list);
		}
		if(DataMold.getflgTb(dataMolds)){//同比
			CurveDataReq curveDataReqtb= new CurveDataReq();
			BeanUtils.copyProperties(energyTarCurveReq,curveDataReqtb);
			curveDataReqtb.setTime(DateUtil.addYear(curveDataReqtb.getTime(),-1));

			CurveDataInfo curveDataInfo=CurveDataFactory.getCurveDataInfo(curveDataReqtb,false);

			List<Object> xvals=curveDataRepository.valueRowsX(curveDataInfo);
			String stime= curveDataInfo.getStime();
			String etime =curveDataInfo.getEtime();
			DeviceItemCycle itemCycle =curveDataInfo.getItemCycle();

			//查询具体数据项
			List<DeviceItemHistoryDiffData> deviceItemHistoryDiffDatas = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
				CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY), stime, etime, itemCycle.id, HistoryDataType.TOTAL.id);
			Map<String, List<Float>> valMap=curveDataRepository.groupSumValueByXY(deviceItemHistoryDiffDatas,itemCycle,curveDataInfo.getShowRows(),null);
			List<Float> yvals= valMap.get(XYDdatas.YPrices);

			CurveDataResq curveDataResq =new CurveDataResq();
			curveDataResq.setYvals((List<Object>)(List)yvals);
			curveDataResq.setXvals(xvals);

			curveDataResq.setTime(DateUtil.toString(DateUtil.getStartDate(curveDataInfo.getTime())));
			curveDataResq.setUnit("元");
			List<CurveDataResq> list=new ArrayList<>();
			list.add(curveDataResq);
			res.put(CurveTypeDatas.curDataTb, list);
		}
		if(DataMold.getflgHb(dataMolds)){//环比
			CurveDataReq curveDataReqhb= new CurveDataReq();
			BeanUtils.copyProperties(energyTarCurveReq,curveDataReqhb);
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
			List<Object> xvals=curveDataRepository.valueRowsX(curveDataInfo);
			String stime= curveDataInfo.getStime();
			String etime =curveDataInfo.getEtime();
			DeviceItemCycle itemCycle =curveDataInfo.getItemCycle();

			//查询具体数据项
			List<DeviceItemHistoryDiffData> deviceItemHistoryDiffDatas = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
				CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY), stime, etime, itemCycle.id, HistoryDataType.TOTAL.id);
			Map<String, List<Float>> valMap=curveDataRepository.groupSumValueByXY(deviceItemHistoryDiffDatas,itemCycle,curveDataInfo.getShowRows(),null);
			List<Float> yvals= valMap.get(XYDdatas.YPrices);

			CurveDataResq curveDataResq =new CurveDataResq();
			curveDataResq.setYvals((List<Object>)(List)yvals);
			curveDataResq.setXvals(xvals);
			curveDataResq.setTime(DateUtil.toString(DateUtil.getStartDate(curveDataInfo.getTime())));
			curveDataResq.setUnit("元");
			List<CurveDataResq> list=new ArrayList<>();
			list.add(curveDataResq);
			res.put(CurveTypeDatas.curDataHb, list);
		}
		return R.data(res);
	}


}
