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
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.energymanagement.dto.ConsumeItemResq;
import org.springblade.energy.energymanagement.entity.EnergyConsumeType;
import org.springblade.energy.energymanagement.service.IEnergyConsumeItemService;
import org.springblade.energy.energymanagement.service.IEnergyConsumeTypeService;
import org.springblade.energy.statistics.dto.ConsumeTypeSpResq;
import org.springblade.energy.statistics.dto.CurveDataInfo;
import org.springblade.energy.statistics.dto.CurveDataReq;
import org.springblade.energy.statistics.dto.EnergyTarCurveReq;
import org.springblade.energy.statistics.repository.CurveDataRepository;
import org.springblade.enums.HistoryDataType;
import org.springblade.enums.ItemBtype;
import org.springblade.enums.ProductSid;
import org.springblade.gw.feign.IDeviceItemHistoryClient;
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
public class EnergyCaeController {

	@Autowired
	private CurveDataRepository curveDataRepository;

	@Autowired
	private IDeviceItemHistoryClient iDeviceItemHistoryClient;

	private IEnergyConsumeTypeService energyConsumeTypeService;
	private IEnergyConsumeItemService energyConsumeItemService;

	/**
	 * 能耗分析-分类能耗
	 * @param energyTarCurveReq
	 * @return7
	 */
	@PostMapping("/getEnergyConsumeType")
	@ApiOperationSupport(order = 31)
	@ApiOperation(value = "分类能耗-占比及明细", notes = "energyTarCurveReq")
	public R<Map<String,Object>> getEnergyConsumeType(@RequestBody EnergyTarCurveReq energyTarCurveReq) {
		Map<String,Object> resMap =new HashMap<>();

		Integer energyType=energyTarCurveReq.getEnergyType();
		Long stationId= energyTarCurveReq.getStationId();
		Long siteId=energyTarCurveReq.getSiteId();
		EnergyConsumeType energyConsumeType=new EnergyConsumeType();
		energyConsumeType.setEnergyType(energyType);
		energyConsumeType.setIsDeleted(0);
		List<EnergyConsumeType> list =energyConsumeTypeService.list(Condition.getQueryWrapper(energyConsumeType));
		if(Func.isEmpty(list)){
			return R.data(null);
		}

		Map<String,Object> queryMap=new HashMap<>();
		queryMap.put("stationId",stationId);
		queryMap.put("siteId",siteId);
		queryMap.put("energyType",energyType);
		List<ConsumeItemResq> consumeItemResqList= energyConsumeItemService.selectEnergyConsumeItemByMap(queryMap);
		if(Func.isEmpty(consumeItemResqList)){
			return R.data(null);
		}
		List<String> items=new ArrayList<>();
		for(ConsumeItemResq consumeItemResq:consumeItemResqList){
			items.add(consumeItemResq.getItemId());
		}
		Map<String, Integer> consumeItemResqMap=buildMapItem(consumeItemResqList);

		String propertyCode="";
		int btype=ItemBtype.ELECTRICITY.id;
		String unit="";
		if(Func.equals(energyTarCurveReq.getEnergyType(),1)){
			propertyCode= ProductSid.SID31.id;//总用电量
			btype=ItemBtype.ELECTRICITY.id;
			unit="(kWh）";
		}
		if(Func.equals(energyTarCurveReq.getEnergyType(),2)){
			propertyCode= ProductSid.SID159.id;//总用水量
			btype=ItemBtype.WATERVOLUME.id;
			unit="m³";
		}
		if(Func.equals(energyTarCurveReq.getEnergyType(),3)){
			propertyCode= ProductSid.SID161.id;//用气量)
			btype=ItemBtype.AIRVOLUME.id;
			unit="m³";
		}
		resMap=energyConsumeImpl(unit,propertyCode,btype,list,consumeItemResqMap,items,energyTarCurveReq);

		return R.data(resMap);
	}
	public Map<String,Object> energyConsumeImpl(String unit,String propertyCode,int btype,List<EnergyConsumeType> list,Map<String, Integer> consumeItemResqMap,List<String> items,EnergyTarCurveReq energyTarCurveReq){
		Map<String,Object> resMap =new HashMap<>();

		CurveDataReq curveDataReq=new CurveDataReq();
		BeanUtils.copyProperties(energyTarCurveReq,curveDataReq);
		CurveDataInfo curveDataInfo= CurveDataFactory.getCurveDataInfo(curveDataReq,true);
		curveDataInfo.setPropertyCodes(propertyCode);
		List<DeviceItemHistoryDiffData> itemHistoryDiffData = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
			CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY), curveDataInfo.getStime(), curveDataInfo.getEtime(),
			curveDataInfo.getItemCycle().id, HistoryDataType.TOTAL.id);
		if(Func.isEmpty(itemHistoryDiffData)){
			return null;
		}
		Map<Integer, List<DeviceItemHistoryDiffData>>  historyDataMap =buildMapHistoryData(consumeItemResqMap,itemHistoryDiffData);

		List<ConsumeTypeSpResq> resqList =new ArrayList<>();//用电占比

		List<String> headNames=new ArrayList<>();//用明细表头
		List<List<Object>> datas=new ArrayList<>();//用电明细数据
		List<Object> times= curveDataRepository.valueRowsX(curveDataInfo);

		Map<Integer, List<Float>> map=new HashMap<>();
		headNames.add("时间");
		for(EnergyConsumeType EnergyConsumeType:list){
			String name=EnergyConsumeType.getConsumeName();
			headNames.add(name+unit);
			List<DeviceItemHistoryDiffData> diffData=historyDataMap.get(EnergyConsumeType.getConsumeType());
			List<Float> datay =new ArrayList<>();
			ConsumeTypeSpResq consumeTypeSpResq=new ConsumeTypeSpResq();
			consumeTypeSpResq.setConsumeType(EnergyConsumeType.getConsumeType());
			consumeTypeSpResq.setConsumeName(EnergyConsumeType.getConsumeName());
			consumeTypeSpResq.setEnergyType(EnergyConsumeType.getEnergyType());
			if(Func.isNotEmpty(diffData)){
				Map<String, Float> ValMap= curveDataRepository.itemsSumValue(diffData);
				Float val=ValMap.get(XYDdatas.YSubVals);
				consumeTypeSpResq.setVal(val);
				Map<String, List<Float>> Datas=curveDataRepository.groupSumValueByXY(diffData,curveDataInfo.getItemCycle(),curveDataInfo.getShowRows(),null);
				datay=Datas.get(XYDdatas.YSubVals);
			}else{
				consumeTypeSpResq.setVal(0f);
			}
			resqList.add(consumeTypeSpResq);
			map.put(EnergyConsumeType.getConsumeType(),datay);
		}
		resMap.put("proportion",resqList);
		resMap.put("head",headNames);

		for(int i=0;i<times.size();i++){
			List<Object> vals=new ArrayList<>();
			vals.add(times.get(i));
			for(Map.Entry<Integer, List<Float>> mapdata:map.entrySet()){
				if(Func.isNotEmpty(mapdata.getValue())){
					vals.add(mapdata.getValue().get(i));
				}else {
					vals.add("--");
				}
			}
			datas.add(vals);
		}

		resMap.put("data",datas);
		return resMap;
	}


	public static Map<String, Integer> buildMapItem(List<ConsumeItemResq> list) {
		if (list.isEmpty()) {
			return null;
		} else {
			Map<String, Integer> idItemMap = new HashMap();
			for (ConsumeItemResq item : list) {
				idItemMap.put(item.getItemId(), item.getConsumeType());
			}

			return idItemMap;
		}
	}
	public static Map<Integer, List<DeviceItemHistoryDiffData>>  buildMapHistoryData(Map<String, Integer> consumeItemResqMap,List<DeviceItemHistoryDiffData> deviceItemHistoryDiffDatas) {
		if (consumeItemResqMap.isEmpty()) {
			return null;
		}
		if (deviceItemHistoryDiffDatas.isEmpty()) {
			return null;
		}
		Map<Integer, List<DeviceItemHistoryDiffData>> map=new HashMap<>();

		for(Map.Entry<String, Integer> m:consumeItemResqMap.entrySet()){
			List<DeviceItemHistoryDiffData> dataList=new ArrayList<>();
			for(DeviceItemHistoryDiffData deviceItemHistoryDiffData:deviceItemHistoryDiffDatas){
				if(Func.equals(m.getKey(),deviceItemHistoryDiffData.getId())){
					dataList.add(deviceItemHistoryDiffData);
				}
			}
			map.put(m.getValue(),dataList);
		}
		return map;

	}



}
