package org.springblade.energy.statistics.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.bean.DeviceItemHistoryDiffData;
import org.springblade.bean.Product;
import org.springblade.constants.CoreCommonConstant;
import org.springblade.constants.GwsubscribeConstant;
import org.springblade.constants.ProductConstant;
import org.springblade.constants.XYDdatas;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.diagram.dto.DiagramItemDTO;
import org.springblade.energy.diagram.entity.DiagramItem;
import org.springblade.energy.diagram.entity.DiagramProduct;
import org.springblade.energy.diagram.service.IDiagramItemService;
import org.springblade.energy.diagram.service.IDiagramProductService;
import org.springblade.energy.runningmanagement.standingbook.entity.EquipmentTransformer;
import org.springblade.energy.runningmanagement.standingbook.service.IEquipmentTransformerService;
import org.springblade.energy.runningmanagement.standingbook.vo.EquipmentTransformerVO;
import org.springblade.energy.statistics.dto.*;
import org.springblade.energy.statistics.repository.CurveDataRepository;
import org.springblade.energy.statistics.repository.ReportDataRepository;
import org.springblade.enums.*;
import org.springblade.gw.feign.IDeviceItemHistoryClient;
import org.springblade.util.BigDecimalUtil;
import org.springblade.util.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 统计分析
 *
 * @author bond
 * @since 2020-05-06
 */
@RestController
@AllArgsConstructor
@RequestMapping("/curve")
@Api(value = "能源管理-电能数据分析", tags = "能源管理-电能数据分析")
public class PowerController extends BladeController {
	@Autowired
	private BladeRedisCache redisCache;
	@Autowired
	private CurveDataRepository curveDataRepository;
	@Autowired
	private IDiagramProductService iDiagramProductService;
	@Autowired
	private IDiagramItemService iDiagramItemService;

	@Autowired
	private ReportDataRepository reportDataRepository;

	@Autowired
	private IDeviceItemHistoryClient iDeviceItemHistoryClient;
	@Autowired
	private IEquipmentTransformerService iEquipmentTransformerService;

///*
//	@GetMapping("/getTransformer")
//	@ApiOperationSupport(order = 1)
//	@ApiOperation(value = "获取变压器", notes = "")
//	public R equipmentList(@ApiParam(value = "站点id") @RequestParam(value = "stationId") Long stationId,
//						   @ApiParam(value = "位置id") @RequestParam(value = "siteId") Long siteId)
//	{
//
//		List<EquipmentTransformerVO> transformers=curveDataRepository.getTransformers(stationId,siteId);
//
//		if(Func.isEmpty(transformers)){
//			return 	R.fail("无变压器设备");
//		}
//		List<EquipmentResq> equipmentResqs =new ArrayList<>();
//
//		for(EquipmentTransformerVO transformer :transformers) {
//			EquipmentResq equipmentResq = new EquipmentResq();
//			equipmentResq.setProductName(transformer.getName());
//			equipmentResq.setDiagramProductId(Func.toLong(transformer.getCode()));
//			equipmentResqs.add(equipmentResq);
//		}
//		return R.data(equipmentResqs);
//	}
//
//	@GetMapping("/getPowerKaiGuan")
//	@ApiOperationSupport(order = 1)
//	@ApiOperation(value = "获取低压开关", notes = "")
//	public R getDYKaiGuan(@ApiParam(value = "站点id") @RequestParam(value = "stationId") Long stationId,
//													 @ApiParam(value = "位置id") @RequestParam(value = "siteId") Long siteId)
//	{
//		List<EquipmentResq> equipmentResqs =new ArrayList<>();
//
//		//一级为中压，查询中压所有馈线开关
//		Map<String,Object> map=new HashMap<>();
//		map.put("stationId",stationId);
//		map.put("siteId",siteId);
//		map.put("diagramType", DiagramType.ZHONGYA.id);//中压
//		map.put("productDtype", ProductDtype.KUIXIANKAIGUAN.id);//馈线开关
//		List<DiagramProduct> diagramProducts=iDiagramProductService.queryDiagramProductByBtype(map);
//		map.put("productDtype", ProductDtype.JINXIANKAIGUAN.id);//进线开关
//		List<DiagramProduct> jinxiandiagramProduct=iDiagramProductService.queryDiagramProductByBtype(map);
//
//		diagramProducts.addAll(jinxiandiagramProduct);
//		for(DiagramProduct diagramProduct:diagramProducts){
//			EquipmentResq equipmentResq = new EquipmentResq();
//			equipmentResq.setProductName(diagramProduct.getProductcname());
//			equipmentResq.setDiagramProductId(diagramProduct.getId());
//			equipmentResqs.add(equipmentResq);
//		}
//		return R.data(equipmentResqs);
//	}
//*/

	/**
	 * 历史运行数据 查询中压，低压
	 * @param curveDataReq
	 * @return
	 */
	@PostMapping("/getCurveDate")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "历史运行数据-曲线图数据", notes = "")
	public R<Map<String,List<CurveDataResq>>> curve(@RequestBody CurveDataReq curveDataReq) {
		R r=curveDataRepository.checkParam(curveDataReq);
		if(!r.isSuccess()){
			return r;
		}

		Integer dataCurveType=curveDataReq.getDataCurveType();

		DiagramProduct diagramProduct=iDiagramProductService.getById(curveDataReq.getDiagramProductId());
		if(Func.isEmpty(diagramProduct)){
			return R.fail("系统图产品ID不存在");
		}


		StringBuffer propertyCodes= new StringBuffer();
		Integer btype=null;
		//电压
		if(Func.equals(CurveType.VOLTAGE.getId(),dataCurveType)){
			//propertyCodes="1,2,3";
			propertyCodes.append(ProductSid.SID1.id).append(",").append(ProductSid.SID2.id).append(",").append(ProductSid.SID3.id);
			//btype=ItemBtype.VOLTAGE.id;
		}
		//电流
		if(Func.equals(CurveType.ELECTRICCURRENT.getId(),dataCurveType)){
			//propertyCodes="7,8,9";
			propertyCodes.append(ProductSid.SID7.id).append(",").append(ProductSid.SID8.id).append(",").append(ProductSid.SID9.id);
			//btype=ItemBtype.ELECTRICCURRENT.id;
		}
		//功率因数
		if(Func.equals(CurveType.POWERFACTOR.getId(),dataCurveType)){
			//propertyCodes="14";
			propertyCodes.append(ProductSid.SID14.id);
			//btype=ItemBtype.POWERFACTOR.id;
			//总进线功率因数
		}
		//温度
		if(Func.equals(CurveType.EQ_TEMPERATURE.getId(),dataCurveType)){
			//propertyCodes="59,60,61";
			propertyCodes.append(ProductSid.SID59.id).append(",").append(ProductSid.SID60.id).append(",").append(ProductSid.SID61.id);
			//btype=ItemBtype.TEMPERATURE.id;
		}
		//频率
		if(Func.equals(CurveType.FREQUENCY.getId(),dataCurveType)){
			propertyCodes.append(ProductSid.SID15.id);
			//propertyCodes="15";
			//btype=null;
		}
		//谐波
		if(Func.equals(CurveType.HARMONIC.getId(),dataCurveType)){
			//查询总进线 电压，电流总谐波
			//propertyCodes="63,64,65,66,67,68";
			propertyCodes.append(ProductSid.SID63.id).append(",").append(ProductSid.SID64.id).append(",").append(ProductSid.SID65.id)
				.append(",").append(ProductSid.SID66.id).append(",").append(ProductSid.SID67.id).append(",").append(ProductSid.SID68.id);

			//总进线谐波
		}
		//电能（电量）
		if(Func.equals(CurveType.ELECTRICITY.getId(),dataCurveType)){
			//propertyCodes=ProductSid.SID31.id;
			propertyCodes.append(ProductSid.SID31.id);
			//btype=ItemBtype.ELECTRICITY.id;
		}
		//负载率
		if(Func.equals(CurveType.LOAD_RATE.getId(),dataCurveType)){

			DiagramProduct dgp=iDiagramProductService.getById(curveDataReq.getDiagramProductId());
			if(Func.isEmpty(dgp)){
				return  R.fail("产品不存在");
			}
				//开关负载率
			if(Func.equals(dgp.getProductDtype(),ProductDtype.JINXIANKAIGUAN.id) || Func.equals(dgp.getProductDtype(),ProductDtype.KUIXIANKAIGUAN.id)) {
				//propertyCodes="18";
				propertyCodes.append(ProductSid.SID18.id);
			}

			//变压器负载率
			if(Func.equals(dgp.getProductDtype(),ProductDtype.BIANYAQU.id)){
				//总有功功率
				propertyCodes.append(ProductSid.SID22.id);
				Map<String, List<CurveDataResq>> res=curveDataRepository.getBianYaQiCurveData(curveDataReq,propertyCodes.toString(),btype,HistoryDataType.TOTAL.id);
				return R.data(res);
			}

		}
		//线电压
		if(Func.equals(CurveType.XIANDIANYA.getId(),dataCurveType)){
			propertyCodes.append(ProductSid.SID4.id).append(",").append(ProductSid.SID5.id).append(",").append(ProductSid.SID6.id);
		}
		//用功功率
		if(Func.equals(CurveType.YOUGONGGONGLV.getId(),dataCurveType)){
			propertyCodes.append(ProductSid.SID22.id);
		}
		//无功功率
		if(Func.equals(CurveType.WUGONGGONGLV.getId(),dataCurveType)){
			propertyCodes.append(ProductSid.SID26.id);
		}
		//无功功率
		if(Func.equals(CurveType.SHIZAIGGONGLV.getId(),dataCurveType)){
			propertyCodes.append(ProductSid.SID30.id);
		}
		//abc箱功率因数
		if(Func.equals(CurveType.ABCPOWERFACTOR.getId(),dataCurveType)){
			propertyCodes.append(ProductSid.SID19.id).append(",").append(ProductSid.SID20.id).append(",").append(ProductSid.SID21.id);
		}


		if(Func.isEmpty(propertyCodes)){
			return R.fail("在系统图中未找到总进线开关");
		}
		if(Func.isEmpty(propertyCodes)){
			return R.fail("CurveType参数有问题请检查");
		}
		Map<String, List<CurveDataResq>> res=curveDataRepository.getCurveData(curveDataReq,propertyCodes.toString(),btype,HistoryDataType.TOTAL.id);
		return R.data(res);
	}



	@PostMapping("/getReportList")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "历史运行数据报表", notes = "curveDataReq")
	public R<List<YunXingReportResq>> getReportList(@RequestBody CurveDataReq curveDataReq) {
		if (Func.isEmpty(curveDataReq)) {
			return R.fail("参数不能为空");
		}
		if (Func.isEmpty(curveDataReq.getDiagramProductId())) {
			return R.fail("系统图产品ID参数不能为空");
		}
		if (Func.isEmpty(curveDataReq.getDateType())) {
			return R.fail("日期类型参数不能为空");
		}
		if (Func.isEmpty(curveDataReq.getTime())) {
			return R.fail("日期不能为空");
		}

		List<Object> avggonglvs=new ArrayList<>();//平均功率
		List<Object> topgonglvs=new ArrayList<>();//峰值功率
		List<Object> gonglvyinshus=new ArrayList<>();//功率因数
		List<Object> avgfuzailvs=new ArrayList<>();//平均负载率
		List<Object> topfuzailvs=new ArrayList<>();//峰值负载率
		List<Object> yongdianliangs=new ArrayList<>();//总用电量


		CurveDataInfo curveDataInfo= CurveDataFactory.getCurveDataInfo(curveDataReq,false);
		List<Object> valueRowsX= curveDataRepository.valueRowsX(curveDataInfo);

		Long diagramProductId=curveDataReq.getDiagramProductId();
		if(Func.isEmpty(diagramProductId)){
			return R.fail("没有总进线开关");
		}
		String propertyCodes=ProductSid.SID14.id;//14.总功率因数，18.负载率，22.总有功功率，31.总用电量
		Integer btype=null;
		//查询具体数据项
		DiagramItem diagramItem=reportDataRepository.queryDiagramItem(Long.valueOf(diagramProductId),propertyCodes,btype);
		if(Func.isNotEmpty(diagramItem)){
			List<String> items = new ArrayList<>();
			items.add(diagramItem.getItemId());
			//14.总功率因数//到数据中心查询数据
			List<DeviceItemHistoryDiffData> deviceItemHistoryDiffDatas = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
				CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY), curveDataInfo.getStime(), curveDataInfo.getEtime(),
				curveDataInfo.getItemCycle().id,HistoryDataType.TOTAL.id);
			gonglvyinshus=curveDataRepository.groupValueByXY(null,deviceItemHistoryDiffDatas,curveDataInfo.getItemCycle(),curveDataInfo.getShowRows()).get(XYDdatas.YVals);
		}

		propertyCodes=ProductSid.SID18.id;//14.总功率因数，18.负载率，22.总有功功率，31.总用电量
		//查询具体数据项
		diagramItem=reportDataRepository.queryDiagramItem(Long.valueOf(diagramProductId),propertyCodes,null);
		if(Func.isNotEmpty(diagramItem)){
			//List<Object> avgfuzailvs=new ArrayList<>();//平均负载率
			//List<Object> topfuzailvs=new ArrayList<>();//峰值负载率
			List<String> items = new ArrayList<>();
			items.add(diagramItem.getItemId());
			//18.负载率//到数据中心查询数据
			List<DeviceItemHistoryDiffData> fuzaiItemHistoryDiffDatas = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
				CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY), curveDataInfo.getStime(), curveDataInfo.getEtime(),
				curveDataInfo.getItemCycle().id,HistoryDataType.TOTAL.id);
			avgfuzailvs=curveDataRepository.groupValueByXY(null,fuzaiItemHistoryDiffDatas,curveDataInfo.getItemCycle(),curveDataInfo.getShowRows()).get(XYDdatas.YAvgs);

			List<DeviceItemHistoryDiffData> topItemHistoryDiffDatas = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
				CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY), curveDataInfo.getStime(), curveDataInfo.getEtime(),
				curveDataInfo.getItemCycle().id,HistoryDataType.PEAK.id);
			topfuzailvs=curveDataRepository.groupValueByXY(null,topItemHistoryDiffDatas,curveDataInfo.getItemCycle(),curveDataInfo.getShowRows()).get(XYDdatas.YVals);

		}

		propertyCodes=ProductSid.SID22.id;//14.总功率因数，18.负载率，22.总有功功率，31.总用电量
		//查询具体数据项
		diagramItem=reportDataRepository.queryDiagramItem(Long.valueOf(diagramProductId),propertyCodes,null);
		if(Func.isNotEmpty(diagramItem)){
			//List<Object> avggonglvs=new ArrayList<>();//平均功率
			//List<Object> topgonglvs=new ArrayList<>();//峰值功率
			List<String> items = new ArrayList<>();
			items.add(diagramItem.getItemId());
			//22.总有功功率//到数据中心查询数据
			List<DeviceItemHistoryDiffData> ItemHistoryDiffDatas = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
				CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY), curveDataInfo.getStime(), curveDataInfo.getEtime(),
				curveDataInfo.getItemCycle().id,HistoryDataType.TOTAL.id);
			avggonglvs=curveDataRepository.groupValueByXY(null,ItemHistoryDiffDatas,curveDataInfo.getItemCycle(),curveDataInfo.getShowRows()).get(XYDdatas.YAvgs);

			List<DeviceItemHistoryDiffData> topItemHistoryDiffDatas = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
				CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY), curveDataInfo.getStime(), curveDataInfo.getEtime(),
				curveDataInfo.getItemCycle().id,HistoryDataType.PEAK.id);
			topgonglvs=curveDataRepository.groupValueByXY(null,topItemHistoryDiffDatas,curveDataInfo.getItemCycle(),curveDataInfo.getShowRows()).get(XYDdatas.YVals);

		}

		propertyCodes=ProductSid.SID31.id;//14.总功率因数，18.负载率，22.总有功功率，31.总用电量
		//查询具体数据项
		diagramItem=reportDataRepository.queryDiagramItem(Long.valueOf(diagramProductId),propertyCodes,null);
		if(Func.isNotEmpty(diagramItem)){
			List<String> items = new ArrayList<>();
			items.add(diagramItem.getItemId());
			//31.总用电量//到数据中心查询数据
			List<DeviceItemHistoryDiffData> deviceItemHistoryDiffDatas = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
				CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY), curveDataInfo.getStime(), curveDataInfo.getEtime(),
				curveDataInfo.getItemCycle().id,HistoryDataType.TOTAL.id);
			yongdianliangs=curveDataRepository.groupValueByXY(null,deviceItemHistoryDiffDatas,curveDataInfo.getItemCycle(),curveDataInfo.getShowRows()).get(XYDdatas.YVals);
		}


		List<YunXingReportResq> resqList=new ArrayList<>();
		YunXingReportResq entity=new YunXingReportResq();

		for(int i=0;i<valueRowsX.size();i++){
			entity=new YunXingReportResq();
			entity.setTime(curveDataInfo.getShowRows().get(i));
			//平均功率
			if(Func.isNotEmpty(avggonglvs)) {
				if (Func.equals(avggonglvs.get(i), GwsubscribeConstant.ITEM_NULL_VALUE)) {
					entity.setAvgPower (Func.toStr(avggonglvs.get(i)));
				} else {
					Double val = (Func.toDouble(avggonglvs.get(i))) * 100d;
					entity.setAvgPower(String.valueOf(BigDecimalUtil.round(val, 2)) + "%");
				}
			}
			//峰值功率
			if(Func.isNotEmpty(topgonglvs)){
				entity.setTopPowerRate(Func.toStr(topgonglvs.get(i)));
			}
			//平均负载率
			if(Func.isNotEmpty(avgfuzailvs)) {
				if (Func.equals(avgfuzailvs.get(i), GwsubscribeConstant.ITEM_NULL_VALUE)) {
					entity.setAvgLoadRate(Func.toStr(avgfuzailvs.get(i)));
				} else {
					Double val = (Func.toDouble(avgfuzailvs.get(i))) * 100d;
					entity.setAvgLoadRate(String.valueOf(BigDecimalUtil.round(val, 2)) + "%");
				}
			}
			//峰值负载率 topfuzailvs
			if(Func.isNotEmpty(topfuzailvs)){
				entity.setTopLoadRate(Func.toStr(topfuzailvs.get(i)));
			}
			//用电量
			if(Func.isNotEmpty(yongdianliangs)){
				entity.setUseVal(Func.toStr(yongdianliangs.get(i)));
				if(!Func.equals(Func.toStr(yongdianliangs.get(i)),GwsubscribeConstant.ITEM_NULL_VALUE)){
				//最佳负载率
					entity.setDestLoadRate("60%-75%");
				}

			}
			//功率因子
			if(Func.isNotEmpty(gonglvyinshus)){
				entity.setPowerFactor(Func.toStr(gonglvyinshus.get(i)));
			}

			resqList.add(entity);
		}
		return R.data(resqList);

	}

	/**
	 * 只监测中压进线
	 * @param curveDataReq
	 * @return
	 */
	@PostMapping("/getDianLiuBuPingHeng")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "三相电流不平衡度", notes = "curveDataReq")
	public R<Map<String,Object>> getDianLiuBuPingHengDuList(@RequestBody CurveDataReq curveDataReq) {
		R r=curveDataRepository.checkParam(curveDataReq);
		if(!r.isSuccess()){
			return r;
		}

		Map<String,Object>  returnMap = new HashMap<>();

		List<DianLiuBuPingHengBuResq> resqList =new ArrayList<>();

		CurveDataInfo curveDataInfo=CurveDataFactory.getCurveDataInfo(curveDataReq,null);
		String propertyCodes=ProductSid.SID17.id;//三相电流不平衡度
		curveDataInfo.setPropertyCodes(propertyCodes);

		String stime= curveDataInfo.getStime();
		String etime =curveDataInfo.getEtime();
		DeviceItemCycle itemCycle =curveDataInfo.getItemCycle();

		DiagramProduct diagramProduct=iDiagramProductService.getById(curveDataReq.getDiagramProductId());


		Map<String, List<CurveDataResq>> res=curveDataRepository.getCurveData(curveDataReq,propertyCodes,null,HistoryDataType.TOTAL.id);
		if(Func.isEmpty(res.get("curData"))){
			return R.fail("总进线开关三相电流不平衡度数据项不存在");
		}
		returnMap.put("CurveData",res);


		Map<String,Object> queryMap=new HashMap<>();
		//queryMap.put("stationId",curveDataInfo.getStationId());
		queryMap.put("diagramProductId",curveDataInfo.getDiagramProductId());
		List<String> pscodes= Arrays.asList(curveDataInfo.getPropertyCodes().split(","));
		queryMap.put("propertyCodes",pscodes);
		List<DiagramItem> diagramItemList=iDiagramItemService.selectDiagramItemByMap(queryMap);

		List<String> items=new ArrayList<>();
		for(DiagramItem DiagramItem:diagramItemList){
			items.add(DiagramItem.getItemId());
		}
		if(Func.isNotEmpty(items)) {
			String item=items.get(0);
			List<DeviceItemHistoryDiffData> deviceItemHistoryDiffDatas = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
				item, stime, etime, itemCycle.id, HistoryDataType.TOTAL.id);
			Map<String, List<Object>> YXVals= curveDataRepository.groupValueByXY(item,deviceItemHistoryDiffDatas, itemCycle, curveDataInfo.getShowRows());

			List<Object> Xvals = YXVals.get(XYDdatas.XVals);

			List<Object> YMams =YXVals.get(XYDdatas.YMaxs);
			List<Object> YMins = YXVals.get(XYDdatas.YMins);
			List<Object> YAvgs = YXVals.get(XYDdatas.YAvgs);
			for (int i = 0; i < Xvals.size(); i++) {
				DianLiuBuPingHengBuResq dianLiuBuPingHengBuResq = new DianLiuBuPingHengBuResq();
				dianLiuBuPingHengBuResq.setMname(diagramProduct.getProductcname());
				dianLiuBuPingHengBuResq.setItem(item);
				dianLiuBuPingHengBuResq.setMaxval(YMams.get(i));
				dianLiuBuPingHengBuResq.setMinval(YMins.get(i));
				dianLiuBuPingHengBuResq.setAvgval(YAvgs.get(i));
				resqList.add(dianLiuBuPingHengBuResq);
			}
		}

		returnMap.put("reportData",resqList);
		return R.data(returnMap);
	}


	/**
	 * 只监测中压进线
	 * 电压波动分析
	 * @param curveDataReq
	 * @return
	 */
	@PostMapping("/getDianYaBoDong")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "电压波动分析", notes = "curveDataReq")
	public R<Map<String,Object>> getDianYaBoDong(@RequestBody CurveDataReq curveDataReq){

		R r=curveDataRepository.checkParam(curveDataReq);
		if(!r.isSuccess()){
			return r;
		}

		DiagramProduct diagramProduct=iDiagramProductService.getById(curveDataReq.getDiagramProductId());
		if(Func.isEmpty(diagramProduct)){
			return R.fail("系统图产品ID参数不存在");
		}

		Map<String,Object>  returnMap = new HashMap<>();

		List<DianYaBoDongResq> resqList =new ArrayList<>();
		StringBuffer propertyCodes= new StringBuffer();//A,B,C 相电压
		propertyCodes.append(ProductSid.SID1.id).append(",").append(ProductSid.SID2.id).append(",").append(ProductSid.SID3.id);

		Map<String, List<CurveDataResq>> res=curveDataRepository.getCurveData(curveDataReq,propertyCodes.toString(),null,HistoryDataType.TOTAL.id);
		returnMap.put("CurveData",res);

		DianYaBoDongResq entity=new DianYaBoDongResq();

		for(int i=0;i<18;i++){
			resqList.add(entity);
		}
		returnMap.put("reportData",resqList);
		return R.data(returnMap);
	}



	@GetMapping("/getZongJinXian")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "获取总进线", notes = "")
	public R getZongJinXian(@ApiParam(value = "站点id") @RequestParam(value = "stationId") Long stationId,
							@ApiParam(value = "位置id") @RequestParam(value = "siteId") Long siteId)
	{
		//查询站点下面的所有总进线开关
		DiagramProduct product=new DiagramProduct();
		product.setStationId(stationId);
		product.setSiteId(siteId);
		product.setProductDtype(ProductDtype.JINXIANKAIGUAN.id);//进线
		List<DiagramProduct> diagramProductList =iDiagramProductService.list(Condition.getQueryWrapper(product));
		if(Func.isEmpty(diagramProductList)){
			R.fail("总进线开关设备");
		}
		List<EquipmentResq> equipmentResqs =new ArrayList<>();

		for(DiagramProduct diagramProduct :diagramProductList) {
			EquipmentResq equipmentResq = new EquipmentResq();
			if (Func.isNotEmpty(diagramProduct.getProductcname())) {
				equipmentResq.setProductName(diagramProduct.getProductcname());
			} else {
				Product pro = redisCache.hGet(ProductConstant.PRODUCT_KEY, diagramProduct.getProductId());
				equipmentResq.setProductName(Func.isNotEmpty(pro) ? pro.getProductName() : diagramProduct.getProductId().toString());
			}
			equipmentResq.setDiagramProductId(diagramProduct.getId());
			equipmentResqs.add(equipmentResq);
		}
		return R.data(equipmentResqs);
	}

	/**
	 * 中低压进线 ----进线总电量-馈线总电量；
	 * 线损分析
	 * @param curveDataReq
	 * @return
	 */
	@PostMapping("/getXianSunFenXi")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "线损分析", notes = "curveDataReq")
	public R<Map<String,Object>> getXianSunFenXi(@RequestBody CurveDataReq curveDataReq){

		Map<String,Object> returnMap=new HashMap<>();

		R r=curveDataRepository.checkParam(curveDataReq);
		if(!r.isSuccess()){
			return r;
		}

		DiagramProduct diagramP=iDiagramProductService.getById(curveDataReq.getDiagramProductId());
		if(Func.isEmpty(diagramP)){
			return R.fail("系统图产品ID参数不存在");
		}

		//返回分析列表
		List<XianSunResq> xianSunResqList=new ArrayList<>();


		//返回数据准备
		CurveDataInfo curveDataInfo= CurveDataFactory.getCurveDataInfo(curveDataReq,false);
		List<Object> xvals = curveDataRepository.valueRowsX(curveDataInfo);

		CurveDataResq itemCurveDataReq=new CurveDataResq();
		itemCurveDataReq.setUnit("%");
		itemCurveDataReq.setTime(curveDataInfo.getStime());
		itemCurveDataReq.setXvals(xvals);


		//返回环比数据准备
		CurveDataReq curveDataReqhb= new CurveDataReq();
		BeanUtils.copyProperties(curveDataReq,curveDataReqhb);
		curveDataReqhb.setTime(DateUtil.addHour(curveDataReqhb.getTime(),-24));
		CurveDataInfo curveDataInfoHb= CurveDataFactory.getCurveDataInfo(curveDataReqhb,false);
		List<Object> xvalsHb= curveDataRepository.valueRowsX(curveDataInfoHb);

		CurveDataResq itemCurveDataReqHb=new CurveDataResq();
		itemCurveDataReqHb.setUnit("%");
		itemCurveDataReqHb.setTime(curveDataInfoHb.getStime());
		itemCurveDataReqHb.setXvals(xvalsHb);



		List<Float> zyvals= new ArrayList<>();//进线总电量
		List<Float> zyvalsHb= new ArrayList<>();//进线总电量

		List<Float> kyvals= new ArrayList<>();//馈线总电量
		List<Float> kyvalsHb= new ArrayList<>();///馈线总电量

		List<Double> lossval= new ArrayList<>();//线损量
		List<Double> lossvalHb= new ArrayList<>();//线损量

		List<Double> lossrate= new ArrayList<>();//线损率
		List<Double> lossrateHb= new ArrayList<>();

		Long diagramPId=curveDataReq.getDiagramProductId();//总进线 系统图产品ID
		String propertyCodes=ProductSid.SID31.id; //总电量

		DiagramProduct diagramProduct=iDiagramProductService.getById(diagramPId);

		curveDataReq.setDiagramProductId(null);
		Map<String,String> map =new HashMap<>();
		map.put("in_id",diagramProduct.getParentId());
		map.put("out_ids",null);

		//查询总进线开关

		List<String> diagramProductIdS= new ArrayList<>(Arrays.asList(diagramPId.toString().split(",")));
		Map<String, List<Float>> zmap=queryXianSunData(diagramProductIdS, propertyCodes, null,curveDataReq);
		zyvals=zmap.get(XYDdatas.YVals);
		zyvalsHb=zmap.get("YHB");


		//查询馈线开关
		Map<String,String> kxmap=iDiagramProductService.queryDiagramProductDtype3(map);
		if(Func.isEmpty(kxmap)){
			return R.fail("无馈线开关");
		}
		if(Func.isNotEmpty(kxmap)){
			diagramProductIdS= new ArrayList<>(Arrays.asList(kxmap.get("out_ids").split(",")));
			if(Func.isEmpty(diagramProductIdS)||Func.equals("",kxmap.get("out_ids"))){
				return R.fail("无馈线开关");
			}
			Map<String, List<Float>> kmap=queryXianSunData(diagramProductIdS, propertyCodes,null, curveDataReq);
				kyvals=kmap.get(XYDdatas.YVals);
				kyvalsHb=kmap.get("YHB");

		}

		for(int i = 0; i < xvals.size(); i++) {
			Double lossF= BigDecimalUtil.sub(zyvals.get(i),kyvals.get(i));//线损量：进线总电量-馈线总电量
			lossF=BigDecimalUtil.round(lossF,2);
			lossval.add(i,lossF);
			Double lossR= BigDecimalUtil.div(lossF,zyvals.get(i),4)*100;

			lossrate.add(i,BigDecimalUtil.round(lossR,2));//线损率：(线损电量/进线总电量)*100%

			XianSunResq  xunsun=new XianSunResq();
			xunsun.setTime(curveDataInfo.getShowRows().get(i));
			xunsun.setLossrate(String.valueOf(lossR)+"%");
			xunsun.setLossval(String.valueOf(lossF));
			xunsun.setMname(diagramP.getProductcname());
			xianSunResqList.add(xunsun);

			Double lossFHb= BigDecimalUtil.sub(zyvalsHb.get(i),kyvalsHb.get(i));//线损量：进线总电量-馈线总电量
			lossvalHb.add(i,lossFHb);
			Double lossRHb= BigDecimalUtil.div(lossFHb,zyvalsHb.get(i),4)*100;
			lossrateHb.add(i,BigDecimalUtil.round(lossRHb,2));//线损率：(线损电量/进线总电量)*100%

		}
		itemCurveDataReq.setYvals((List<Object>)(List)lossrate);
		itemCurveDataReqHb.setYvals((List<Object>)(List)lossrateHb);

		Map<String, List<CurveDataResq>> res = new HashMap<>();
		List<CurveDataResq> curveDataResqList=new ArrayList<>();
		curveDataResqList.add(itemCurveDataReq);

		List<CurveDataResq> curveDataResqListHb=new ArrayList<>();
		curveDataResqListHb.add(itemCurveDataReqHb);

		res.put("curData",curveDataResqList);
		res.put("curDataHb",curveDataResqListHb);
		returnMap.put("CurveData",res);

		returnMap.put("reportData",xianSunResqList);

		return R.data(returnMap);

	}

	public Map<String, List<Float>> queryXianSunData(List<String> diagramProductIdS,String propertyCodes,Integer btype, CurveDataReq curveDataReq){
		Map<String, List<Float>>  resMap =new HashMap<>();

		List<String> items = new ArrayList<>();
		for(String diagramProductId:diagramProductIdS){
			DiagramProduct product=iDiagramProductService.getById( Long.valueOf(diagramProductId));
			//查询具体数据项
			DiagramItem diagramItem=reportDataRepository.queryDiagramItem(Long.valueOf(diagramProductId),propertyCodes,btype);
			if(Func.isNotEmpty(diagramItem)){
				items.add(diagramItem.getItemId());
			}
		}

		CurveDataInfo curveDataInfo= CurveDataFactory.getCurveDataInfo(curveDataReq,false);
		curveDataInfo.setPropertyCodes(propertyCodes);
		List<DeviceItemHistoryDiffData> kuixianData = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
			CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY), curveDataInfo.getStime(), curveDataInfo.getEtime(),
			curveDataInfo.getItemCycle().id,HistoryDataType.TOTAL.id);
		List<Float> yvals=new ArrayList<>();

		Map<String, List<Float>> datamap=curveDataRepository.groupSumValueByXY(kuixianData,curveDataInfo.getItemCycle(),curveDataInfo.getShowRows(),null);
		yvals=datamap.get(XYDdatas.YSubVals);

		CurveDataReq curveDataReqhb= new CurveDataReq();
		BeanUtils.copyProperties(curveDataReq,curveDataReqhb);
		curveDataReqhb.setTime(DateUtil.addHour(curveDataReqhb.getTime(),-24));
		CurveDataInfo curveDataInfoHb= CurveDataFactory.getCurveDataInfo(curveDataReqhb,false);
		curveDataInfoHb.setPropertyCodes(propertyCodes);
		List<DeviceItemHistoryDiffData> kuixianDataHb = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
			CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY), curveDataInfoHb.getStime(), curveDataInfoHb.getEtime(),
			curveDataInfoHb.getItemCycle().id,HistoryDataType.TOTAL.id);
		List<Float> yvalsHb=new ArrayList<>();

		Map<String, List<Float>> datamapHb=curveDataRepository.groupSumValueByXY(kuixianDataHb,curveDataInfoHb.getItemCycle(),curveDataInfoHb.getShowRows(),null);
		yvalsHb=datamapHb.get(XYDdatas.YSubVals);



		resMap.put(XYDdatas.YVals,yvals);
		resMap.put("YHB",yvalsHb);
		return resMap;
	}


	/**
	 * 效率分析 变压器
	 * @param curveDataTyReq
	 * @return
	 */
	@PostMapping("/getXiaoLvFenXi")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "效率分析", notes = "curveDataTyReq")
	public R<Map<String,Object>> getXiaoLvFenXi(@RequestBody CurveDataTyReq curveDataTyReq) {
		/**
		 * 一次侧输入电量 是指中压馈线到 变压器的电量
		 * 二次侧输出电量 指低压总进线电量
		 */

		Double theoryval=curveDataTyReq.getTheoryval();
		if(Func.isEmpty(theoryval)){
			return R.fail("理论值参数缺失");
		}

		CurveDataReq curveDataReq=new CurveDataReq();
		BeanUtil.copyProperties(curveDataTyReq,curveDataReq);


		R r = curveDataRepository.checkParam(curveDataReq);
		if (!r.isSuccess()) {
			return r;
		}

		DiagramProduct diagramP = iDiagramProductService.getById(curveDataReq.getDiagramProductId());
		if (Func.isEmpty(diagramP)) {
			return R.fail("变压器不存在");
		}
		//
		if(Func.isEmpty(diagramP.getMesolow())){
			return R.fail("变压器未关联到中压馈线开关");
		}
		//根据 mesolow 变压器中低压关联 找到中压馈线开关
		DiagramProduct queryD=new DiagramProduct();
		queryD.setPindex(diagramP.getMesolow());
		DiagramProduct kuixianDiagramProduct = iDiagramProductService.getOne(Condition.getQueryWrapper(queryD));
		if (Func.isEmpty(kuixianDiagramProduct)) {
			return R.fail("变压器未关联到中压馈线开关");
		}
		String propertyCodes=ProductSid.SID31.id; //总电量


		List<Float> dyzjxYvals= new ArrayList<>();//低压进线总电量
		List<Float> dyzjxYvalsHb= new ArrayList<>();//低压进线总电量

		List<Float> zykxYvals= new ArrayList<>();//中压馈线总电量
		List<Float> zykxYvalsHb= new ArrayList<>();///中压馈线总电量

		List<Double> rates= new ArrayList<>();//实际使用率
		List<Double> ratesHb= new ArrayList<>();//实际使用率

		//查询 中压馈线开关-总电量数据项
		//查询具体数据项
		Map<String,Object> queryMap=new HashMap<>();
		//queryMap.put("stationId",curveDataInfo.getStationId());
		queryMap.put("diagramProductId",kuixianDiagramProduct.getId());
		List<String> pscodes= Arrays.asList(propertyCodes.split(","));
		queryMap.put("propertyCodes",pscodes);
		//queryMap.put("btype",ItemBtype.ELECTRICITY.id);

		List<DiagramItem> diagramItemList=iDiagramItemService.selectDiagramItemByMap(queryMap);
		List<String> items= new ArrayList<>();
		for(DiagramItem diagramItem:diagramItemList ){
			items.add(diagramItem.getItemId());
		}
		CurveDataInfo curveDataInfo= CurveDataFactory.getCurveDataInfo(curveDataReq,false);
		//curveDataInfo.setDiagramProductId(kuixianDiagramProduct.getId());
		List<Object> xvals = curveDataRepository.valueRowsX(curveDataInfo);

		List<DeviceItemHistoryDiffData> kuixianData = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
			CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY), curveDataInfo.getStime(), curveDataInfo.getEtime(),
			curveDataInfo.getItemCycle().id,HistoryDataType.TOTAL.id);
		Map<String, List<Float>> datakuixlianmap=curveDataRepository.groupSumValueByXY(kuixianData,curveDataInfo.getItemCycle(),curveDataInfo.getShowRows(),null);
		zykxYvals=datakuixlianmap.get(XYDdatas.YVals);

		curveDataReq.setTime(DateUtil.addHour(curveDataInfo.getTime(),-24));
		CurveDataInfo curveDataInfoHb= CurveDataFactory.getCurveDataInfo(curveDataReq,false);
		List<DeviceItemHistoryDiffData> kuixianDataHb = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
			CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY), curveDataInfoHb.getStime(), curveDataInfoHb.getEtime(),
			curveDataInfoHb.getItemCycle().id,HistoryDataType.TOTAL.id);
		Map<String, List<Float>> datakuixianmapHb=curveDataRepository.groupSumValueByXY(kuixianDataHb,curveDataInfoHb.getItemCycle(),curveDataInfoHb.getShowRows(),null);
		zykxYvalsHb=datakuixianmapHb.get(XYDdatas.YVals);

		//查询 低压总进线-总电量数据项
		//查询具体数据项
		queryMap.put("diagramProductId",curveDataReq.getDiagramProductId());

		List<DiagramItem> diyaDiagramItemList=iDiagramItemService.selectDiagramItemByMap(queryMap);
		List<String> diyaitems= new ArrayList<>();
		for(DiagramItem diagramItem:diyaDiagramItemList ){
			diyaitems.add(diagramItem.getItemId());
		}
		List<DeviceItemHistoryDiffData> jinxianData = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
			CollectionUtil.join(diyaitems, CoreCommonConstant.SPLIT_COMMA_KEY), curveDataInfo.getStime(), curveDataInfo.getEtime(),
			curveDataInfo.getItemCycle().id,HistoryDataType.TOTAL.id);
		Map<String, List<Float>> datadiyajinxianmap=curveDataRepository.groupSumValueByXY(jinxianData,curveDataInfo.getItemCycle(),curveDataInfo.getShowRows(),null);
		dyzjxYvals=datadiyajinxianmap.get(XYDdatas.YVals);

		List<DeviceItemHistoryDiffData> jinxianDataHb = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
			CollectionUtil.join(diyaitems, CoreCommonConstant.SPLIT_COMMA_KEY), curveDataInfoHb.getStime(), curveDataInfoHb.getEtime(),
			curveDataInfoHb.getItemCycle().id,HistoryDataType.TOTAL.id);
		Map<String, List<Float>> datadiyajinxianmapHb=curveDataRepository.groupSumValueByXY(jinxianDataHb,curveDataInfoHb.getItemCycle(),curveDataInfoHb.getShowRows(),null);
		dyzjxYvalsHb=datadiyajinxianmapHb.get(XYDdatas.YVals);


		Map<String,Object> returnMap=new HashMap<>();
		List<XiaoLvResq> xiaoLvResqList=new ArrayList<>();
		for(int i = 0; i < xvals.size(); i++) {
			Double auseRate= BigDecimalUtil.div(dyzjxYvals.get(i),zykxYvals.get(i),4);//实际使用效率：低压总进线电量除以中压馈线输入总电量x100
			rates.add(i,auseRate);

			Double auseRateHb= BigDecimalUtil.div(dyzjxYvalsHb.get(i),zykxYvalsHb.get(i),4);//实际使用效率：低压总进线电量除以中压馈线输入总电量x100
			ratesHb.add(i,auseRateHb);

			Double luseRate= BigDecimalUtil.div(auseRate,theoryval,4)*100;  ////实际使用效率 除以 理论运行值 x100
			XiaoLvResq  xiaolv=new XiaoLvResq();
			xiaolv.setTime(curveDataInfo.getShowRows().get(i));
			xiaolv.setOneEval(zykxYvals.get(i));
			xiaolv.setTwoEval(dyzjxYvals.get(i));
			xiaolv.setAuseRate(String.valueOf(auseRate)+"%");
			xiaolv.setLuseRate(String.valueOf(luseRate)+"%");
			xiaoLvResqList.add(xiaolv);


		}
		CurveDataResq itemCurveDataReq=new CurveDataResq();
		CurveDataResq itemCurveDataReqHb=new CurveDataResq();
		itemCurveDataReq.setXvals(xvals);
		itemCurveDataReq.setYvals((List<Object>)(List)rates);
		itemCurveDataReq.setUnit("%");
		itemCurveDataReq.setTime(curveDataInfo.getStime());

		itemCurveDataReqHb.setXvals(xvals);
		itemCurveDataReqHb.setYvals((List<Object>)(List)ratesHb);
		itemCurveDataReqHb.setUnit("%");
		itemCurveDataReqHb.setTime(curveDataInfoHb.getStime());

		Map<String, List<CurveDataResq>> res = new HashMap<>();
		List<CurveDataResq> curveDataResqList=new ArrayList<>();
		curveDataResqList.add(itemCurveDataReq);

		List<CurveDataResq> curveDataResqListHb=new ArrayList<>();
		curveDataResqListHb.add(itemCurveDataReqHb);

		res.put("curData",curveDataResqList);
		res.put("curDataHb",curveDataResqListHb);
		returnMap.put("CurveData",res);

		returnMap.put("reportData",xiaoLvResqList);
		return R.data(returnMap);
	}



	/**
	 * 损失率分析 变压器
	 * @param curveDataTyReq
	 * @return
	 */
	@PostMapping("/getSunshiLvFenXi")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "损失率分析", notes = "curveDataTyReq")
	public R<Map<String,Object>> getSunshiLvFenXi(@RequestBody CurveDataTyReq curveDataTyReq) {
		/**
		 * 一次侧输入功率 是指中压馈线到 变压器的功率
		 * 二次侧输出功率 指低压总进线功率
		 */

		Double theoryval=curveDataTyReq.getTheoryval();
		if(Func.isEmpty(theoryval)){
			return R.fail("理论值参数缺失");
		}

		CurveDataReq curveDataReq=new CurveDataReq();
		BeanUtil.copyProperties(curveDataTyReq,curveDataReq);


		R r = curveDataRepository.checkParam(curveDataReq);
		if (!r.isSuccess()) {
			return r;
		}

		DiagramProduct diagramP = iDiagramProductService.getById(curveDataReq.getDiagramProductId());
		if (Func.isEmpty(diagramP)) {
			return R.fail("变压器不存在");
		}
		//
		if(Func.isEmpty(diagramP.getMesolow())){
			return R.fail("变压器未关联到中压馈线开关");
		}
		//根据 mesolow 变压器中低压关联 找到中压馈线开关
		DiagramProduct queryD=new DiagramProduct();
		queryD.setPindex(diagramP.getMesolow());
		DiagramProduct kuixianDiagramProduct = iDiagramProductService.getOne(Condition.getQueryWrapper(queryD));
		if (Func.isEmpty(kuixianDiagramProduct)) {
			return R.fail("变压器未关联到中压馈线开关");
		}
		String propertyCodes=ProductSid.SID22.id;//"22"; //总有功功率



		List<Float> dyzjxYvals= new ArrayList<>();//低压进线总有功功率
		List<Float> dyzjxYvalsHb= new ArrayList<>();//低压进线总有功功率

		List<Float> zykxYvals= new ArrayList<>();//中压馈线总有功功率
		List<Float> zykxYvalsHb= new ArrayList<>();///中压馈线总有功功率

		List<Double> rates= new ArrayList<>();//实际损失率
		List<Double> ratesHb= new ArrayList<>();//实际损失率

		//查询 中压馈线开关-总电量数据项
		//查询具体数据项
		Map<String,Object> queryMap=new HashMap<>();
		//queryMap.put("stationId",curveDataInfo.getStationId());
		queryMap.put("diagramProductId",kuixianDiagramProduct.getId());
		List<String> pscodes= Arrays.asList(propertyCodes.split(","));
		queryMap.put("propertyCodes",pscodes);

		List<DiagramItem> diagramItemList=iDiagramItemService.selectDiagramItemByMap(queryMap);
		List<String> items= new ArrayList<>();
		for(DiagramItem diagramItem:diagramItemList ){
			items.add(diagramItem.getItemId());
		}
		CurveDataInfo curveDataInfo= CurveDataFactory.getCurveDataInfo(curveDataReq,false);
		//curveDataInfo.setDiagramProductId(kuixianDiagramProduct.getId());
		List<Object> xvals = curveDataRepository.valueRowsX(curveDataInfo);

		List<DeviceItemHistoryDiffData> kuixianData = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
			CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY), curveDataInfo.getStime(), curveDataInfo.getEtime(),
			curveDataInfo.getItemCycle().id,HistoryDataType.TOTAL.id);
		Map<String, List<Float>> datakuixlianmap=curveDataRepository.groupSumValueByXY(kuixianData,curveDataInfo.getItemCycle(),curveDataInfo.getShowRows(),null);
		zykxYvals=datakuixlianmap.get(XYDdatas.YVals);

		CurveDataInfo curveDataInfoHb= CurveDataFactory.getCurveDataInfo(curveDataReq,false);
		curveDataInfoHb.setTime(DateUtil.addHour(curveDataInfoHb.getTime(),-24));
		List<DeviceItemHistoryDiffData> kuixianDataHb = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
			CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY), curveDataInfoHb.getStime(), curveDataInfoHb.getEtime(),
			curveDataInfoHb.getItemCycle().id,HistoryDataType.TOTAL.id);
		Map<String, List<Float>> datakuixianmapHb=curveDataRepository.groupSumValueByXY(kuixianDataHb,curveDataInfoHb.getItemCycle(),curveDataInfoHb.getShowRows(),null);
		zykxYvalsHb=datakuixianmapHb.get(XYDdatas.YVals);

		//查询 低压总进线-总电量数据项
		//查询具体数据项
		queryMap.put("diagramProductId",curveDataReq.getDiagramProductId());

		List<DiagramItem> diyaDiagramItemList=iDiagramItemService.selectDiagramItemByMap(queryMap);
		List<String> diyaitems= new ArrayList<>();
		for(DiagramItem diagramItem:diyaDiagramItemList ){
			diyaitems.add(diagramItem.getItemId());
		}
		List<DeviceItemHistoryDiffData> jinxianData = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
			CollectionUtil.join(diyaitems, CoreCommonConstant.SPLIT_COMMA_KEY), curveDataInfo.getStime(), curveDataInfo.getEtime(),
			curveDataInfo.getItemCycle().id,HistoryDataType.TOTAL.id);
		Map<String, List<Float>> datadiyajinxianmap=curveDataRepository.groupSumValueByXY(jinxianData,curveDataInfo.getItemCycle(),curveDataInfo.getShowRows(),null);
		dyzjxYvals=datadiyajinxianmap.get(XYDdatas.YVals);

		List<DeviceItemHistoryDiffData> jinxianDataHb = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
			CollectionUtil.join(diyaitems, CoreCommonConstant.SPLIT_COMMA_KEY), curveDataInfoHb.getStime(), curveDataInfoHb.getEtime(),
			curveDataInfoHb.getItemCycle().id,HistoryDataType.TOTAL.id);
		Map<String, List<Float>> datadiyajinxianmapHb=curveDataRepository.groupSumValueByXY(jinxianDataHb,curveDataInfoHb.getItemCycle(),curveDataInfoHb.getShowRows(),null);
		dyzjxYvalsHb=datadiyajinxianmapHb.get(XYDdatas.YVals);


		Map<String,Object> returnMap=new HashMap<>();
		List<SunshiLvResq> sunshiLvResqList=new ArrayList<>();
		for(int i = 0; i < xvals.size(); i++) {
			Double auseRate= BigDecimalUtil.div(dyzjxYvals.get(i),zykxYvals.get(i),4);//实际损失率：低压总进线有用总功率除以中压馈线输入有用总功率x100
			rates.add(i,auseRate);

			Double auseRateHb= BigDecimalUtil.div(dyzjxYvalsHb.get(i),zykxYvalsHb.get(i),4);//实际损失率：低压总进线有用总功率除以中压馈线输入有用总功率x100
			ratesHb.add(i,auseRateHb);

			Double luseRate= BigDecimalUtil.div(auseRate,theoryval,4)*100;  ////实际使用效率 除以 理论运行值 x100
			SunshiLvResq  sunshilv=new SunshiLvResq();
			sunshilv.setTime(curveDataInfo.getShowRows().get(i));
			sunshilv.setOneEval(zykxYvals.get(i));
			sunshilv.setTwoEval(dyzjxYvals.get(i));
			sunshilv.setAuseRate(String.valueOf(auseRate)+"%");
			sunshilv.setLuseRate(String.valueOf(luseRate)+"%");
			sunshiLvResqList.add(sunshilv);


		}
		CurveDataResq itemCurveDataReq=new CurveDataResq();
		CurveDataResq itemCurveDataReqHb=new CurveDataResq();
		itemCurveDataReq.setXvals(xvals);
		itemCurveDataReq.setYvals((List<Object>)(List)rates);

		itemCurveDataReqHb.setXvals(xvals);
		itemCurveDataReqHb.setYvals((List<Object>)(List)ratesHb);

		Map<String, List<CurveDataResq>> res = new HashMap<>();
		List<CurveDataResq> curveDataResqList=new ArrayList<>();
		curveDataResqList.add(itemCurveDataReq);

		List<CurveDataResq> curveDataResqListHb=new ArrayList<>();
		curveDataResqListHb.add(itemCurveDataReqHb);

		res.put("curData",curveDataResqList);
		res.put("curDataHb",curveDataResqListHb);
		returnMap.put("CurveData",res);

		returnMap.put("reportData",sunshiLvResqList);
		return R.data(returnMap);
	}


	/**
	 * 负载率分析 变压器
	 * @param curveDataReq
	 * @return
	 */
	@PostMapping("/getFuzaiLvFenXi")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "负载率分析", notes = "curveDataReq")
	public R<Map<String,Object>> getFuzaiLvFenXi(@RequestBody CurveDataReq curveDataReq){

		R r=curveDataRepository.checkParam(curveDataReq);
		if(!r.isSuccess()){
			return r;
		}

		DiagramProduct diagramProduct=iDiagramProductService.getById(curveDataReq.getDiagramProductId());
		if(Func.isEmpty(diagramProduct)){
			return R.fail("该变压器在系统图产品里不存在");
		}

		Map<String,Object>  returnMap = new HashMap<>();

		StringBuffer propertyCodes= new StringBuffer();
		propertyCodes.append(ProductSid.SID18.id);//"18";//负载率

		Map<String, List<CurveDataResq>> res=curveDataRepository.getCurveData(curveDataReq,propertyCodes.toString(),null,HistoryDataType.TOTAL.id);
		returnMap.put("CurveData",res);




		//查询站点下面的所有变压器
		List<EquipmentTransformerVO> transformers=curveDataRepository.getTransformers(curveDataReq.getStationId(),curveDataReq.getSiteId());
		List<FuZaiLvFenXiResq> resqList =new ArrayList<>();

		CurveDataInfo curveDataInfo= CurveDataFactory.getCurveDataInfo(curveDataReq,false);
		for(EquipmentTransformer transformer:transformers){
			Map<String,Object> queryMap=new HashMap<>();
			//propertyCodes="14,18,22";//18负载率 14总功率因数 22有用总功率
			propertyCodes=null;
			propertyCodes.append(ProductSid.SID14.id).append(",").append(ProductSid.SID18.id).append(",").append(ProductSid.SID22.id);
			queryMap.put("diagramProductId",transformer.getCode());
			List<String> pscodes= Arrays.asList(propertyCodes.toString().split(","));
			queryMap.put("propertyCodes",pscodes);

			List<DiagramItem> diagramItemList=iDiagramItemService.selectDiagramItemByMap(queryMap);
			List<String> items=new ArrayList<>();
			Map<String,String> itemPC=new HashMap<>();
			for(DiagramItem diagramItem:diagramItemList){
				items.add(diagramItem.getItemId());
				itemPC.put(diagramItem.getPropertyCode(),diagramItem.getItemId());
			}

			List<DeviceItemHistoryDiffData> historyDiffData = iDeviceItemHistoryClient.getItemDataAndDiffByItemIdAndTime(
				CollectionUtil.join(items, CoreCommonConstant.SPLIT_COMMA_KEY), curveDataInfo.getEtime(), curveDataInfo.getEtime(),
				curveDataInfo.getItemCycle().id,null);

			FuZaiLvFenXiResq entity=new FuZaiLvFenXiResq();
			entity.setCapacity(Func.toStr(transformer.getCapacity())+"KVA");
			entity.setDestLoadRate("60%-75%");
			for(DeviceItemHistoryDiffData hdata:historyDiffData){
				//14总功率因数
				if(Func.equals(hdata.getId(),itemPC.get("14")) && Func.equals(hdata.getCtg(),HistoryDataType.TOTAL)){
					entity.setAvgOutPowerFactor(Func.toStr(hdata.getAvg()));
				}
				if(Func.equals(hdata.getId(),itemPC.get("14")) && Func.equals(hdata.getCtg(),HistoryDataType.PEAK)){
					entity.setTopPowerFactor(Func.toStr(hdata.getVal()));
				}
				//22有用总功率
				if(Func.equals(hdata.getId(),itemPC.get("22")) && Func.equals(hdata.getCtg(),HistoryDataType.TOTAL)){
					entity.setAvgOutPowerRate(Func.toStr(hdata.getAvg())+"KW");
				}
				if(Func.equals(hdata.getId(),itemPC.get("22")) && Func.equals(hdata.getCtg(),HistoryDataType.PEAK)){
					entity.setTopPowerRate(Func.toStr(hdata.getVal())+"KW");
				}
				//18负载率
				if(Func.equals(hdata.getId(),itemPC.get("18")) && Func.equals(hdata.getCtg(),HistoryDataType.TOTAL)){
					entity.setAvgLoadRate(Func.toStr(BigDecimalUtil.mul(hdata.getAvg(),100)+"%"));
				}
				if(Func.equals(hdata.getId(),itemPC.get("18")) && Func.equals(hdata.getCtg(),HistoryDataType.PEAK)){
					entity.setTopLoadRate(Func.toStr(BigDecimalUtil.mul(hdata.getVal(),100)+"%"));
				}
			}
			resqList.add(entity);

		}

		returnMap.put("reportData",resqList);
		return R.data(returnMap);

	}



	/**
	 * 电能报表
	 * @return
	 */
	@GetMapping("/powerReport")
	@ApiOperationSupport(order = 10)
	@ApiOperation(value = "电能报表", notes = "")
	public R<Map<String,Object>> powerReport(@ApiParam(value = "站点id", required = true) @RequestParam Long stationId,
											 @ApiParam(value = "时间0:小时报表，2:日报表 ,3:月报表", required = true) @RequestParam Integer dateType,
											 @ApiParam(value = "开始时间", required = true) @RequestParam Date startTime,
											 @ApiParam(value = "结束时间", required = true) @RequestParam Date endTime) {

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
		headNames.add(3,"总用电量");
		headNames.add(4,"柜号");
		headNames.add(5,"用电回路名称");
		if(Func.equals(CommonDateType.DAY.getId(),dateType)){
			headNames.add(6,"日用电量");
			for(int i=0;i<timeRows.size();i++){
				headNames.add(7+i,timeRows.get(i)+"时");
			}
		}
		if(Func.equals(CommonDateType.MONTH.getId(),dateType)){
			headNames.add(6,"月用电量");
			for(int i=0;i<timeRows.size();i++){
				headNames.add(7+i,timeRows.get(i)+"日");
			}
		}
		if(Func.equals(CommonDateType.YEAR.getId(),dateType)){
			headNames.add(6,"年用电量");
			for(int i=0;i<timeRows.size();i++){
				headNames.add(7+i,timeRows.get(i)+"月");
			}
		}


		int rows=headNames.size();

		Map<String,Object> queryMap=new HashMap<>();
		queryMap.put("propertyCode",ProductSid.SID31.id);
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
				nodesdata.set(4, diagramItem.getParentproductcname());
				nodesdata.set(5, diagramItem.getProductcname());
				nodesdata.set(6, itemTotalValMap.get(diagramItem.getItemId()));
				List<Float> YSubVals = YVals.get(diagramItem.getItemId());
				for (int i = 0; i < YSubVals.size(); i++) {
					nodesdata.set(7 + i, YSubVals.get(i));
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
