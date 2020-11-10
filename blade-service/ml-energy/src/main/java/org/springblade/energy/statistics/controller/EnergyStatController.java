package org.springblade.energy.statistics.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.bean.DeviceItemHistoryDiffData;
import org.springblade.common.cache.CacheNames;
import org.springblade.constants.CoreCommonConstant;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.diagram.entity.DiagramItem;
import org.springblade.energy.diagram.entity.DiagramProduct;
import org.springblade.energy.diagram.service.IDiagramItemService;
import org.springblade.energy.diagram.service.IDiagramProductService;
import org.springblade.energy.energymanagement.dto.*;
import org.springblade.energy.energymanagement.service.IGasMeterService;
import org.springblade.energy.energymanagement.service.IPowerMeterService;
import org.springblade.energy.statistics.dto.CurveDataInfo;
import org.springblade.energy.statistics.dto.CurveDataReq;
import org.springblade.enums.*;
import org.springblade.gw.feign.IDeviceItemHistoryClient;
import org.springblade.system.entity.Dept;
import org.springblade.util.BigDecimalUtil;
import org.springblade.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author bond
 * @date 2020/6/28 10:14
 * @desc 能耗统计
 */
@RestController
@AllArgsConstructor
@RequestMapping("/stat")
@Api(value = "能源管理-能耗统计", tags = "能源管理-能耗统计")
public class EnergyStatController {
	@Autowired
	private IPowerMeterService iPowerMeterService;
	@Autowired
	private IDiagramItemService iDiagramItemService;
	@Autowired
	private IGasMeterService iGasMeterService;

	@Autowired
	private IDiagramProductService iDiagramProductService;
	@Autowired
	private IDeviceItemHistoryClient iDeviceItemHistoryClient;
	@Autowired
	private BladeRedisCache redisCache;
	/**
	 * 能耗统计-电能列表
	 * @param curveDataReq
	 * @return
	 */
	@PostMapping("/getPowerMeterReport")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "能耗统计-电能列表", notes = "curveDataTyReq")
	public R<List<UsePowerDto>> getPowerMeterReport(@RequestBody CurveDataReq curveDataReq) {
		Integer deviceItemCycle= DeviceItemCycle.DAY.id;//默认天
		String stime= DateUtil.format(DateUtil.getStartDate(curveDataReq.getTime()),DateUtil.TIME_PATTERN_24);
		String etime= DateUtil.format(DateUtil.getEndDate(curveDataReq.getTime()),DateUtil.TIME_PATTERN_24);

//		if(Func.equals(curveDataReq.getDateType(), CommonDateType.DAY.getId())){
//			deviceItemCycle= DeviceItemCycle.DAY.id;
//		}
		if(Func.equals(curveDataReq.getDateType(), CommonDateType.MONTH.getId())){
			deviceItemCycle= DeviceItemCycle.MONTH.id;
			stime= DateUtil.format(DateUtil.getMonthStartDate(curveDataReq.getTime()),DateUtil.TIME_PATTERN_24);
			etime= DateUtil.format(DateUtil.getMonthEndDate(curveDataReq.getTime()),DateUtil.TIME_PATTERN_24);


		}
		if(Func.equals(curveDataReq.getDateType(), CommonDateType.YEAR.getId())){
			deviceItemCycle= DeviceItemCycle.YEAR.id;
			stime= DateUtil.format(DateUtil.getYearStartDate(curveDataReq.getTime()),DateUtil.TIME_PATTERN_24);
			etime= DateUtil.format(DateUtil.getYearEndDate(curveDataReq.getTime()),DateUtil.TIME_PATTERN_24);

		}
		CurveDataInfo curveDataInfo= CurveDataFactory.getCurveDataInfo(curveDataReq,true);

		DiagramProduct diagramProduct=iDiagramProductService.getById(curveDataReq.getDiagramProductId());
		if(Func.isEmpty(diagramProduct)){
			R.fail("系统图产品id不存在,请检查");
		}

		List<UsePowerDto> resqDateList=new ArrayList<>();

			UsePowerDto useEnergyDto=new UsePowerDto();
			useEnergyDto.setTime(stime);

			Map<String,Object> queryMap=new HashMap<>();
			String propertyCodes=ProductSid.SID31.id;//总用电量
			queryMap.put("diagramProductId",curveDataReq.getDiagramProductId());
			List<String> pscodes= Arrays.asList(propertyCodes.split(","));
			queryMap.put("propertyCodes",pscodes);
			queryMap.put("btype",ItemBtype.ELECTRICITY.id);

		List<DiagramItem> diagramItemList=iDiagramItemService.selectDiagramItemByMap(queryMap);
			if(Func.isNotEmpty(diagramItemList)){
				DiagramItem powerMeterDto=diagramItemList.get(0);
				List<String> items= new ArrayList<>();
				items.add(powerMeterDto.getItemId());

				List<DeviceItemHistoryDiffData> historyDiffData = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
					CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY), stime, etime,deviceItemCycle,null);
				if(Func.isNotEmpty(historyDiffData)){
					for(DeviceItemHistoryDiffData data:historyDiffData){
						if(Func.isNotEmpty(data)) {
							Float val = data.getVal();//差值
							Float cost = data.getPrice();//价格
//						if(Func.equals(data.getCtg(),HistoryDataType.TOTAL.id)){
//							useEnergyDto.setTotalVal(val);
//							useEnergyDto.setTopCost(cost);
//							//useEnergyDto.setTotalCost(BigDecimalUtil.mul(val,));
//						}
//						if(Func.equals(data.getCtg(),HistoryDataType.TOP.id)){
//							useEnergyDto.setToplVal(val);
//							useEnergyDto.setTopCost(cost);
//						}
//						if(Func.equals(data.getCtg(),HistoryDataType.PEAK.id)){
//							useEnergyDto.setPeakVal(val);
//							useEnergyDto.setPeakCost(cost);
//						}
//						if(Func.equals(data.getCtg(),HistoryDataType.FLAT.id)){
//							useEnergyDto.setFlatVal(val);
//							useEnergyDto.setFlatCost(cost);
//						}
//						if(Func.equals(data.getCtg(), HistoryDataType.VALLEY.id)){
//							useEnergyDto.setValleyVal(val);
//							useEnergyDto.setValleyCost(cost);
//						}
//						double allCost=useEnergyDto.getTopCost()+useEnergyDto.getPeakCost()+useEnergyDto.getFlatCost()+useEnergyDto.getValleyCost();
							if (Func.isNotEmpty(cost)) {
								useEnergyDto.setTotalCost(BigDecimalUtil.round(cost, 2));
							}
							useEnergyDto.setTotalVal(val);
							useEnergyDto.setProductcname(diagramProduct.getProductcname());
							if(Func.isNotEmpty(diagramProduct.getDeptId())){
								Dept dept = redisCache.hGet(CacheNames.DEPT_KEY, diagramProduct.getDeptId());
								if(Func.isNotEmpty(dept)) {
									useEnergyDto.setDeptName(dept.getDeptName());
								}
							}
						}
					}
				}

			}

			resqDateList.add(useEnergyDto);


		return R.data(resqDateList);
	}

	/**
	 * 能耗统计-水量列表
	 * @param curveDataReq
	 * @return
	 */
	@PostMapping("/getWaterMeterReport")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "能耗统计-水量列表", notes = "curveDataTyReq")
	public R<List<UseWaterDto>> getWaterMeterReport(@RequestBody CurveDataReq curveDataReq) {
		Integer deviceItemCycle= DeviceItemCycle.DAY.id;//默认天
		String stime= DateUtil.format(DateUtil.getStartDate(curveDataReq.getTime()),DateUtil.TIME_PATTERN_24);
		String etime= DateUtil.format(DateUtil.getEndDate(curveDataReq.getTime()),DateUtil.TIME_PATTERN_24);


//		if(Func.equals(curveDataReq.getDateType(), CommonDateType.DAY.getId())){
//			deviceItemCycle= DeviceItemCycle.DAY.id;
//		}
		if(Func.equals(curveDataReq.getDateType(), CommonDateType.MONTH.getId())){
			deviceItemCycle= DeviceItemCycle.MONTH.id;
			stime= DateUtil.format(DateUtil.getMonthStartDate(curveDataReq.getTime()),DateUtil.TIME_PATTERN_24);
			etime= DateUtil.format(DateUtil.getMonthEndDate(curveDataReq.getTime()),DateUtil.TIME_PATTERN_24);


		}
		if(Func.equals(curveDataReq.getDateType(), CommonDateType.YEAR.getId())){
			deviceItemCycle= DeviceItemCycle.YEAR.id;
			stime= DateUtil.format(DateUtil.getYearStartDate(curveDataReq.getTime()),DateUtil.TIME_PATTERN_24);
			etime= DateUtil.format(DateUtil.getYearEndDate(curveDataReq.getTime()),DateUtil.TIME_PATTERN_24);

		}
		CurveDataInfo curveDataInfo= CurveDataFactory.getCurveDataInfo(curveDataReq,true);

		//查询所有水表
		Map<String,Object> map =new HashMap<>();
		map.put("stationId",curveDataReq.getStationId());
		map.put("siteId",curveDataReq.getSiteId());
		map.put("diagramType", DiagramType.GONGSHUI.id);
		map.put("productDtype", ProductDtype.SHUIBIAO.id);

		List<DiagramProduct> diagramProducts=iDiagramProductService.queryDiagramProductByMap(map);

		List<UseWaterDto> resqDateList=new ArrayList<>();
		for(DiagramProduct kProduct:diagramProducts){
			UseWaterDto useEnergyDto=new UseWaterDto();
			useEnergyDto.setTime(stime);

			Map<String,Object> queryMap=new HashMap<>();
			String propertyCodes=ProductSid.SID159.id;//总用水量
			queryMap.put("diagramProductId",kProduct.getId());
			List<String> pscodes= Arrays.asList(propertyCodes.split(","));
			queryMap.put("propertyCodes",pscodes);

			List<DiagramItem> diagramItemList=iDiagramItemService.selectDiagramItemByMap(queryMap);
			if(Func.isNotEmpty(diagramItemList)){
				DiagramItem meterDto=diagramItemList.get(0);
				List<String> items= new ArrayList<>();
				items.add(meterDto.getItemId());

				List<DeviceItemHistoryDiffData> historyDiffData = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
					CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY), stime, etime,deviceItemCycle,null);
				if(Func.isNotEmpty(historyDiffData)){
					for(DeviceItemHistoryDiffData data:historyDiffData){
						float val=  data.getVal();//差值
						if(Func.equals(data.getCtg(),HistoryDataType.TOTAL.id)){
							useEnergyDto.setVal(val);
							useEnergyDto.setCost(data.getPrice());
						}
						useEnergyDto.setProductcname(kProduct.getProductcname());
						if(Func.isNotEmpty(kProduct.getDeptId())){
							Dept dept = redisCache.hGet(CacheNames.DEPT_KEY, kProduct.getDeptId());
							if(Func.isNotEmpty(dept)) {
								useEnergyDto.setDeptName(dept.getDeptName());
							}
						}

					}
				}

			}

			resqDateList.add(useEnergyDto);
		}

		return R.data(resqDateList);
	}


	/**
	 * 能耗统计-气量列表
	 * @param curveDataReq
	 * @return
	 */
	@PostMapping("/getGasMeterReport")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "能耗统计-气量列表", notes = "curveDataTyReq")
	public R<List<UseGasDto>> getGasMeterReport(@RequestBody CurveDataReq curveDataReq) {
		Integer deviceItemCycle= DeviceItemCycle.DAY.id;//默认天
		String stime= DateUtil.format(DateUtil.getStartDate(curveDataReq.getTime()),DateUtil.TIME_PATTERN_24);
		String etime= DateUtil.format(DateUtil.getEndDate(curveDataReq.getTime()),DateUtil.TIME_PATTERN_24);


//		if(Func.equals(curveDataReq.getDateType(), CommonDateType.DAY.getId())){
//			deviceItemCycle= DeviceItemCycle.DAY.id;
//		}
		if(Func.equals(curveDataReq.getDateType(), CommonDateType.MONTH.getId())){
			deviceItemCycle= DeviceItemCycle.MONTH.id;
			stime= DateUtil.format(DateUtil.getMonthStartDate(curveDataReq.getTime()),DateUtil.TIME_PATTERN_24);
			etime= DateUtil.format(DateUtil.getMonthEndDate(curveDataReq.getTime()),DateUtil.TIME_PATTERN_24);


		}
		if(Func.equals(curveDataReq.getDateType(), CommonDateType.YEAR.getId())){
			deviceItemCycle= DeviceItemCycle.YEAR.id;
			stime= DateUtil.format(DateUtil.getYearStartDate(curveDataReq.getTime()),DateUtil.TIME_PATTERN_24);
			etime= DateUtil.format(DateUtil.getYearEndDate(curveDataReq.getTime()),DateUtil.TIME_PATTERN_24);

		}
		CurveDataInfo curveDataInfo= CurveDataFactory.getCurveDataInfo(curveDataReq,true);

		//查询所有燃气表
		Map<String,Object> map =new HashMap<>();
		map.put("stationId",curveDataReq.getStationId());
		map.put("siteId",curveDataReq.getSiteId());
		map.put("diagramType",DiagramType.GONGQI.id);
		map.put("productDtype", ProductDtype.RANQIBIAO.id);

		List<DiagramProduct> diagramProducts=iDiagramProductService.queryDiagramProductByMap(map);

		List<UseGasDto> resqDateList=new ArrayList<>();
		for(DiagramProduct kProduct:diagramProducts){
			UseGasDto useEnergyDto=new UseGasDto();
			useEnergyDto.setTime(stime);

			Map<String,Object> queryMap=new HashMap<>();
			String propertyCodes=ProductSid.SID161.id;//总用气量
			queryMap.put("diagramProductId",kProduct.getId());
			List<String> pscodes= Arrays.asList(propertyCodes.split(","));
			queryMap.put("propertyCodes",pscodes);

			List<DiagramItem> diagramItemList=iDiagramItemService.selectDiagramItemByMap(queryMap);
			if(Func.isNotEmpty(diagramItemList)){
				DiagramItem meterDto=diagramItemList.get(0);
				List<String> items= new ArrayList<>();
				items.add(meterDto.getItemId());

				List<DeviceItemHistoryDiffData> historyDiffData = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
					CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY), stime, etime,deviceItemCycle,null);
				if(Func.isNotEmpty(historyDiffData)){
					for(DeviceItemHistoryDiffData data:historyDiffData){
						float val=  data.getVal();//差值
						if(Func.equals(data.getCtg(), HistoryDataType.TOTAL.id)){
							useEnergyDto.setVal(val);
							useEnergyDto.setCost(data.getPrice());
						}
						useEnergyDto.setProductcname(kProduct.getProductcname());
						if(Func.isNotEmpty(kProduct.getDeptId())){
							Dept dept = redisCache.hGet(CacheNames.DEPT_KEY, kProduct.getDeptId());
							if(Func.isNotEmpty(dept)) {
								useEnergyDto.setDeptName(dept.getDeptName());
							}
						}
					}
				}

			}

			resqDateList.add(useEnergyDto);
		}

		return R.data(resqDateList);
	}

}
