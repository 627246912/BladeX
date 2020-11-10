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
package org.springblade.energy.diagram.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Synchronized;
import org.springblade.bean.*;
import org.springblade.constants.GwsubscribeConstant;
import org.springblade.constants.ProductConstant;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.dto.*;
import org.springblade.energy.device.entity.DeviceRelation;
import org.springblade.energy.device.service.IDeviceRelationService;
import org.springblade.energy.diagram.dto.*;
import org.springblade.energy.diagram.entity.Diagram;
import org.springblade.energy.diagram.entity.DiagramItem;
import org.springblade.energy.diagram.entity.DiagramProduct;
import org.springblade.energy.diagram.entity.DiagramShowItem;
import org.springblade.energy.diagram.service.*;
import org.springblade.energy.energymanagement.controller.MeterRepository;
import org.springblade.enums.BooleanEnum;
import org.springblade.enums.DiagramType;
import org.springblade.enums.ItemStype;
import org.springblade.gw.feign.IDataItemClient;
import org.springblade.gw.feign.IDeviceClient;
import org.springblade.gw.feign.ITopicClient;
import org.springblade.util.ComputeUtil;
import org.springblade.util.NumberUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

/**
 * 系统图基本信息 控制器
 *
 * @author bond
 * @since 2020-03-26
 */
@RestController
@AllArgsConstructor
@RequestMapping("/diagram")
@Api(value = "系统图基本信息", tags = "系统图基本信息接口")
public class DiagramController extends BladeController {

	private IDiagramService diagramService;
	private IDiagramProductService iDiagramProductService;
	private IDiagramItemService iDiagramItemService;
	private IDeviceRelationService iDeviceRelationService;
	private IDeviceClient iDeviceClient;
	private IDataItemClient iDataItemClient;
	@Autowired
	private BladeRedisCache redisCache;
	@Autowired
	private ITopicClient iTopicClient;

	private IDiagramCameraService iDiagramCameraService;
	private IDiagramShowItemService iDiagramShowItemService;
	@Autowired
	private MeterRepository meterRepository;




	@GetMapping("/getDiagramList")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "改接口用于修复系统图data里面id问题--批量修改系统图", notes = "")
	public R<List<Diagram>> getDiagramList() {
		Map<String,Object> map=new HashMap<>();
		map.put("tenant_id","CRRCZhuzhou");
		List<Diagram> diagramList = diagramService.selectDiagramByMap(map);
		return R.data(diagramList);
	}

	@PostMapping("/updateDiagramBatchById")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "改接口用于修复系统图data里面id问题--批量修改系统图", notes = "diagramList")
	public R getDiagramDataById(@RequestBody List<Diagram> diagramList) {
		boolean r= diagramService.updateBatchById(diagramList);
		return R.data(r);
	}



	/**
	 * 根据ID查询系统图
	 */
	@GetMapping("/getDiagramDataById")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "根据ID查询系统图", notes = "传入id")
	public R<Map<String,Object>> getDiagramDataById(@ApiParam(value = "主键id", required = true) @RequestParam String id) {
		return R.data(diagramService.getDiagramDataById(id));
	}

	/**
	 * 根据站点ID，位置ID 查询系统图
	 */
	@GetMapping("/getDiagram")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "根据站点ID，位置ID 查询系统图", notes = "传入id")
	public R<List<DiagramResp>> getDiagram(@ApiParam(value = "stationId", required = true) @RequestParam Long stationId,
										   @ApiParam(value = "siteId", required = true) @RequestParam Long siteId,
										   @ApiParam(value = "diagramType", required = true) @RequestParam String diagramType) {
//		Diagram diagram = new Diagram();
//		diagram.setStationId(stationId);
//		diagram.setSiteId(siteId);
//		diagram.setIsDeleted(0);
//		diagram.setDiagramType(diagramType);

		List<String> diagramTypes=new ArrayList<>(Arrays.asList(diagramType.split(",")));
		Map<String,Object> map =new HashMap<>();
		map.put("stationId",stationId);
		map.put("siteId",siteId);
		map.put("diagramTypes",diagramTypes);
		List<Diagram> details = diagramService.selectDiagramByMap(map);

		List<DiagramResp> listDiagramResp =new ArrayList<>();
		for(Diagram dgm:details){
			DiagramResp diagramResp=new DiagramResp();

			BeanUtils.copyProperties(dgm,diagramResp);
			diagramResp.setDiagramId(dgm.getId());
			DiagramProduct dp=new DiagramProduct();
			dp.setDiagramId(dgm.getId());
			dp.setIsDeleted(0);
			List<DiagramProduct>  listDiagramProduct= iDiagramProductService.list(Condition.getQueryWrapper(dp));

			List<DiagramProductReq> listDiagramProductReq=new ArrayList<>();

			for(DiagramProduct diagramProduct:listDiagramProduct){
				DiagramProductReq diagramProductReq=new DiagramProductReq();
				BeanUtils.copyProperties(diagramProduct,diagramProductReq);

				Product p=redisCache.hGet(ProductConstant.PRODUCT_KEY, diagramProductReq.getProductId());
				if(Func.isNotEmpty(p)){
					diagramProductReq.setLayout(p.getLayout());
				}

				listDiagramProductReq.add(diagramProductReq);
			}
			diagramResp.setProducts(listDiagramProductReq);
			List<DiagramShowItem> diagramShowItem =new ArrayList<>();

			DiagramShowItem showItem=new DiagramShowItem();
			showItem.setDiagramId(dgm.getId());
			showItem.setIsDeleted(0);
			diagramShowItem=iDiagramShowItemService.list(Condition.getQueryWrapper(showItem));

			diagramResp.setDiagramShowItem(diagramShowItem);

//			if(Func.isEmpty(diagramType)) {
//				DiagramItem diagramItem = new DiagramItem();
//				diagramItem.setDiagramId(dgm.getId());
//				diagramItem.setIsDeleted(0);
//				diagramItem.setFit(false);
//				List<DiagramItem> listDiagramItem = iDiagramItemService.list(Condition.getQueryWrapper(diagramItem));
//
//				List<DiagramItemReq> listDiagramItemReq = new ArrayList<>();
//				for (DiagramItem dItem : listDiagramItem) {
//					DiagramItemReq diagramItemReq = new DiagramItemReq();
//					BeanUtils.copyProperties(dItem, diagramItemReq);
//					listDiagramItemReq.add(diagramItemReq);
//				}
//				diagramResp.setDiagramItemReq(listDiagramItemReq);
//
//			}
			listDiagramResp.add(diagramResp);
		}


		return R.data(listDiagramResp);
	}

	public R checkDiagramData(List<DiagramReq> diagramReqs){
		List<String> ruts=new ArrayList<>();
		for(DiagramReq diagramReq:diagramReqs) {
			if(Func.isEmpty(diagramReq.getStationId())){
				return R.fail("站点ID参数缺失");
			}
			if(Func.isEmpty(diagramReq.getSiteId())){
				return R.fail("位置ID参数缺失");
			}
			if(Func.isEmpty(diagramReq.getDiagramType())){
				return R.fail("系统图类型参数缺失");
			}
			if(Func.isEmpty(diagramReq.getDid())){
				return R.fail("网关ID参数缺失");
			}
			if(Func.isEmpty(diagramReq.getProducts())){
				return R.fail("没有产品不能提交");
			}
			List<DiagramProductReq> products=diagramReq.getProducts();

			for(DiagramProductReq diagramProductReq: products) {
				if (Func.isEmpty(diagramProductReq.getProductId())) {
					return R.fail("提交的产品没有产品ID");
				}
				if (Func.isNotEmpty(diagramProductReq.getRtuidcb())) {
					ruts.add(diagramProductReq.getRtuidcb());
				}
			}

			if(Func.isEmpty(DiagramType.getProductDtype(diagramReq.getDiagramType()))){
				return R.fail("系统图类型参数不正确");
			}

			if(Func.isNotEmpty(diagramReq.getDiagramItemReq())){
				List<DiagramItemReq> diagramItemReqs=diagramReq.getDiagramItemReq();
				for(DiagramItemReq diagramItemReq :diagramItemReqs){
					if(Func.isEmpty(diagramItemReq.getItemkey())){
						return R.fail("数据项diagramItemReq的itemkey参数缺失");
					}
				}

			}
			if(Func.equals(diagramReq.getDiagramType(),DiagramType.GONGSHUI.id) || Func.equals(diagramReq.getDiagramType(),DiagramType.GONGQI.id) ){
				if(Func.isEmpty(diagramReq.getDiagramShowItemReq())){
					return R.fail("供水供气展示数据不能为空");
				}
			}

		}
		Map<String,Integer> map = new HashMap<>();
		for(String str:ruts){
			Integer i = 1; //定义一个计数器，用来记录重复数据的个数
			if(map.get(str) != null){
				i=map.get(str)+1;
			}
			map.put(str,i);
		}
//		for(String s:map.keySet()){
//			if(map.get(s) > 1){
//				return R.fail("ctu数据重复："+s);
//			}
//		}

		return  R.success("验证通过");
	}

	/**
	 * 根据ID修改系统图
	 */
	@ApiLog("新增修改 系统图")
	@PostMapping("/submitDiagramData")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "根据ID修改系统图", notes = "传入id")
	@Transactional(rollbackFor = Exception.class)
	@Synchronized
	public R<List<Long>> submitDiagramData(@Valid @RequestBody List<DiagramReq> diagramReqs
	) {
		R r=checkDiagramData(diagramReqs);
		if(!r.isSuccess()){
			return r;
		}
		Set<String> deviceCodes=new HashSet<>();
		Set<String> rtuids=new HashSet<>();
		Long stationId=null;
		Long siteId=null;

		boolean res = false;
		//原有的系统图id
		Set<Long> oldDiagramIds =new HashSet<>();
		for(DiagramReq diagramReq:diagramReqs) {
			Diagram diagram = new Diagram();
			diagram.setStationId(diagramReq.getStationId());
			diagram.setSiteId(diagramReq.getSiteId());
			diagram.setIsDeleted(0);
			List<String> diagramTypes =Arrays.asList(diagramReq.getDiagramType().split(","));
			List<Diagram> details =new ArrayList<>();
			for(String diagramType:diagramTypes){
				diagram.setDiagramType(diagramType);
				List<Diagram> oldllsit = diagramService.list(Condition.getQueryWrapper(diagram));
				if(Func.isNotEmpty(oldllsit)){
					for(Diagram detail :oldllsit){
						details.add(detail);
						oldDiagramIds.add(detail.getId());
					}

				}
			}
		}



		//新增的系统图id
		Set<Long> newDiagramIds =new HashSet<>();
		//本次要提交的所有系统图id
		Set<Long> allDiagramIds =new HashSet<>();


		Set<DeviceRelation> dids=new HashSet<>();//用于保存网关
		for(DiagramReq diagramReq:diagramReqs) {
			Diagram diagram = new Diagram();
			if(Func.isEmpty(diagramReq.getId())) {
				//新增系统图
				Long id = NumberUtil.getRandomNum(11);
				diagramReq.setId(id);
				diagram.setId(id);
				diagram.setStationId(diagramReq.getStationId());
				diagram.setSiteId(diagramReq.getSiteId());
				diagram.setDiagramType(diagramReq.getDiagramType());
				diagram.setDiagramName(diagramReq.getDiagramName());
				diagram.setDid(diagramReq.getDid());
				diagram.setDiagramData(diagramReq.getDiagramData());
				diagram.setTmType(diagramReq.getTmType());
				diagram.setBkImage(diagramReq.getBkImage());
				//保存新增系统图
				res = diagramService.save(diagram);
				allDiagramIds.add(id);
				newDiagramIds.add(id);
				if (!res) {
					return R.fail("添加失败");
				}
			}
			else{
				//修改保存的系统图
				allDiagramIds.add(diagramReq.getId());
				diagram =diagramService.getById(diagramReq.getId());
				diagram.setStationId(diagramReq.getStationId());
				diagram.setSiteId(diagramReq.getSiteId());
				diagram.setDiagramType(diagramReq.getDiagramType());
				diagram.setDiagramName(diagramReq.getDiagramName());
				diagram.setDid(diagramReq.getDid());
				diagram.setDiagramData(diagramReq.getDiagramData());
				diagram.setTmType(diagramReq.getTmType());
				diagram.setBkImage(diagramReq.getBkImage());
				boolean re = diagramService.updateById(diagram);

				if (!re) {
					return R.fail("修改失败");
				}
			}
			List<String> gws= Arrays.asList(diagramReq.getDid().split(","));
			for(String gw:gws){
				DeviceRelation deviceRelation=new DeviceRelation();
				deviceRelation.setDid(gw);
				deviceRelation.setStationId(diagramReq.getStationId());
				dids.add(deviceRelation);
			}
			//保存网关
			saveDevice(dids);


			//本次提交中原本存在的系统图产品id
			Set<Long> oldDiagramProductIds =new HashSet<>();
			List<DiagramProduct> oldDiagramProducts =new ArrayList<>();

			//新增的系统图产品id
			Set<Long> newDiagramProductIds =new HashSet<>();
			List<DiagramProduct> newDiagramProducts =new ArrayList<>();

			List<DiagramProductReq> products = diagramReq.getProducts();

			List<DiagramProduct> diagramProductList = new ArrayList<DiagramProduct>();
			for (DiagramProductReq product : products) {
				rtuids.add(product.getRtuidcb());


				DiagramProduct diagramProduct = new DiagramProduct();
				diagramProduct.setDiagramId(diagramReq.getId());
				diagramProduct.setParentId(product.getParentId());
				diagramProduct.setProductId(product.getProductId());
				diagramProduct.setStationId(diagramReq.getStationId());
				diagramProduct.setSiteId(diagramReq.getSiteId());
				diagramProduct.setDid(product.getDid());
				diagramProduct.setRtuidcb(product.getRtuidcb());
				diagramProduct.setProductcname(product.getProductcname());
				diagramProduct.setPindex(product.getPindex());
				diagramProduct.setMesolow(product.getMesolow());
				diagramProduct.setDeptId(product.getDeptId());
				diagramProduct.setGrade(product.getGrade());
				diagramProduct.setElectricTypekey(product.getElectricTypekey());
				Product p=redisCache.hGet(ProductConstant.PRODUCT_KEY, product.getProductId());
				if(Func.isNotEmpty(p)){
					diagramProduct.setProductDtype(p.getProductDtype());
					diagramProduct.setAssetCode(p.getAssetCode());
					if(Func.isEmpty(diagramProduct.getProductcname())){
						diagramProduct.setProductcname(p.getProductName());
					}
				}
				//判断是是否新增的产品
				if(Func.isNotEmpty(product.getId())){
					diagramProduct.setId(product.getId());
					oldDiagramProductIds.add(product.getId());
					oldDiagramProducts.add(diagramProduct);

				}else{
					Long diagramProductId=NumberUtil.getRandomNum(11);
					diagramProduct.setId(diagramProductId);
					newDiagramProductIds.add(diagramProductId);
					newDiagramProducts.add(diagramProduct);
				}
				diagramProductList.add(diagramProduct);
			}
			//本次提交要删除的系统图产品id
			Set<Long> needDelDiagramProductIds =new HashSet<>();
			Map<String,Object> querymap=new HashMap<>(1);
			querymap.put("diagram_id",diagram.getId());
			List<DiagramProduct> alloldDiagramProducts =iDiagramProductService.listByMap(querymap);
			for(DiagramProduct diagramProduct:alloldDiagramProducts){
				needDelDiagramProductIds.add(diagramProduct.getId());
			}
			//本次提交要删除的系统图产品
			needDelDiagramProductIds.removeAll(oldDiagramProductIds);
			if(Func.isNotEmpty(needDelDiagramProductIds)){
				iDiagramProductService.delDiagramProductById(new ArrayList<>(needDelDiagramProductIds));
				iDiagramItemService.delDiagramItemByProductIds(new ArrayList<>(needDelDiagramProductIds));
			}
			res = iDiagramProductService.saveOrUpdateBatch(diagramProductList);

			//数据项处理
			if (res) {



				//保存数据项
				SaveDiagramItem(diagramProductList,oldDiagramProductIds,newDiagramProductIds);
				//自定义图形展示数据
				saveDiagramShowItem(diagram,diagramReq.getDiagramShowItemReq());
			}

		}
		//本次提交要删除的系统图
		Set<Long> delDiagramIds =new HashSet<>();
		delDiagramIds.addAll(allDiagramIds);
		delDiagramIds.addAll(oldDiagramIds);
		delDiagramIds.addAll(newDiagramIds);
		delDiagramIds.removeAll(allDiagramIds);

		if(Func.isNotEmpty(delDiagramIds)) {
			diagramService.delDiagramById(new ArrayList<>(delDiagramIds));
			iDiagramProductService.delDiagramProduct(new ArrayList<>(delDiagramIds));
			iDiagramItemService.delDiagramItem(new ArrayList<>(delDiagramIds));
			iDiagramCameraService.delDiagramCameraByDiagramId(new ArrayList<>(delDiagramIds));
		}

		//提交数据到仪表计量
		meterRepository.insertPowerMeter(stationId,siteId);
		meterRepository.insertWaterMeter(stationId,siteId);
		meterRepository.insertGasMeter(stationId,siteId);
		return R.data(new ArrayList<>(allDiagramIds));
	}

	public void SaveDiagramItem(List<DiagramProduct> alldiagramProductList,
								Set<Long> olddiagramProductIds,Set<Long> newdiagramProductIds) {

		//需要插入的数据项
		List<DiagramItem> needInsertItems =new ArrayList<>();
		Map<String,DiagramItem> needInsertItemsmap=new HashMap<>();


		//需要删除的数据项
		List<Long> needDelItems =new ArrayList<>();
		Map<String,DiagramItem> needDelItemsmap=new HashMap<>();

		//已经存在库中的数据项
		if(Func.isNotEmpty(olddiagramProductIds)) {
			List<DiagramItem> oldItemList = iDiagramItemService.queryItemByDiagramPropductIds(new ArrayList<>(olddiagramProductIds));
			for (DiagramItem diagramItem : oldItemList) {
				String key = diagramItem.getDiagramProductId() + diagramItem.getItemId();
				needDelItemsmap.put(key, diagramItem);
			}
		}
		//根据全部初始化的数据比较出需要删除的数据项
		List<DiagramItem> allinitItemList= getDiagramItem(alldiagramProductList);
		for(DiagramItem diagramItem:allinitItemList){
			String key=diagramItem.getDiagramProductId()+diagramItem.getItemId();
			DiagramItem olddiagramItem=needDelItemsmap.get(key);
			if(Func.isNotEmpty(olddiagramItem)){
				//needDelItemsmap.put(key,olddiagramItem);
				needDelItemsmap.remove(key);
			}else{
				//needInsertItemsmap.put(key,diagramItem);
				needInsertItems.add(diagramItem);
			}

		}
		for(Map.Entry<String,DiagramItem> diagramItem:needDelItemsmap.entrySet()){
			needDelItems.add(diagramItem.getValue().getId());
		}
		iDiagramItemService.saveBatch(needInsertItems);
		if(Func.isNotEmpty(needDelItems)){
			iDiagramItemService.delDiagramItem(needDelItems);
		}
	}

	public void saveDiagramShowItem(Diagram diagram,List<DiagramShowItemReq> showList) {

		//供水供气展示数据项
		List<DiagramShowItem> DiagramShowItems=new ArrayList<>();
		if(Func.isNotEmpty(showList)){
			for (DiagramShowItemReq waterGasShowItemReq : showList) {
				//根据RTUIDCB查询网关上报的数据线(数据中心查询)
				List<DeviceItem> deviceItemList = iDeviceClient.getDeviceItemInfosByRtuidcb(waterGasShowItemReq.getRtuidcb());
				if (Func.isEmpty(deviceItemList)) {
					continue;
				}
				//map的key是sid（对应的产品属性编码） 一个rtu下sid是唯一的
				Map<Integer, DeviceItem> mapDeviceItem = buildMap(deviceItemList);
				DeviceItem item = mapDeviceItem.get(Integer.valueOf(waterGasShowItemReq.getPropertyCode()));
				if(Func.isNotEmpty(item)) {
					DiagramShowItem diagramShowItem=new DiagramShowItem();
					BeanUtils.copyProperties(waterGasShowItemReq,diagramShowItem);
					diagramShowItem.setItemId(item.getId());
					diagramShowItem.setDiagramId(diagram.getId());
					diagramShowItem.setSiteId(diagram.getSiteId());
					diagramShowItem.setStationId(diagram.getStationId());
					DiagramShowItems.add(diagramShowItem);

				}
			}
		}
		if(Func.isNotEmpty(showList) && Func.isNotEmpty(DiagramShowItems)){
			iDiagramShowItemService.saveBatch(DiagramShowItems);

		}

	}




	/**
	 * 根据系统图ID恢复系统图产品默认数据项
	 */
	@ApiLog("恢复系统图产品默认数据项")
	@PostMapping("/initDiagramData")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "恢复系统图产品默认数据项", notes = "传入id")
	@Transactional(rollbackFor = Exception.class)
	@Synchronized
	public R<List<Long>> initDiagram(@Valid @RequestBody List<DiagramReq> diagramReqs
	) {
		List<Long> diagramIds=new ArrayList<>();//系统图id
		Set<String> deviceCodes=new HashSet<>();//关联网关
		Set<String> rtuids=new HashSet<>();//关联Rtuidcbs
		for(DiagramReq diagramReq:diagramReqs) {
			if (Func.isEmpty(diagramReq.getId())) {
				return R.fail("系统图ID参数缺失");
			}
			diagramIds.add(diagramReq.getId());

			List<String> dids=Arrays.asList(diagramReq.getDid().split(","));
			deviceCodes.addAll(dids);
		}
		//1.删除系统图数据项
		iDiagramItemService.delDiagramItem(diagramIds);
		//2.查询出系统图
		List<Diagram> diagramList= diagramService.listByIds(diagramIds);
		//3.处理系统图数据项逻辑
		List<DiagramProduct> alldiagramProductList =new ArrayList<>();
		for(Diagram diagram:diagramList){
			Map<String,Object> querymap =new HashMap<>(1);
			querymap.put("diagram_id",diagram.getId());
			//3.1查询出系统图的产品
			List<DiagramProduct> diagramProducts= iDiagramProductService.listByMap(querymap);
			for (DiagramProduct product : diagramProducts) {
				List<String> Rtuidcbs=Arrays.asList(product.getRtuidcb().split(","));
				rtuids.addAll(Rtuidcbs);
			}
			alldiagramProductList.addAll(diagramProducts);
		}
		//4全部要插入的数据项
		List<DiagramItem> alldiagramItemList=  getDiagramItem(alldiagramProductList);
		boolean res= iDiagramItemService.saveBatch(alldiagramItemList);

		//5插入数据项成功则发送数据中心 更新 遥测，遥信，遥控 设置
		Map<String,DiagramItem> DiagramItemMap=new HashMap<>();
		for(DiagramItem diagramItem:alldiagramItemList){
			DiagramItemMap.put(diagramItem.getItemId(),diagramItem);
		}


		//插入数据项成功则发送数据中心 更新 遥测，遥信，遥控 设置
		if (res){
			sendItemToDataCenter(deviceCodes,rtuids,DiagramItemMap);
		}

		return R.data(diagramIds);
	}
	public List<DiagramItem>  getDiagramItem(List<DiagramProduct> diagramProductList) {
		//1.准备插入的数据项
		List<List<ProductProperty>> lists = new ArrayList<>();
		//需要插入的
		List<DiagramItem> diagramItems = new ArrayList<>();
		Map<String, DiagramItem> DiagramItemMap = new HashMap<>();
		//Map<String,DeviceItem> DeviceItemMap=new HashMap<>();

		for (DiagramProduct diagramProduct : diagramProductList) {
			if (Func.isEmpty(diagramProduct.getRtuidcb())) {
				continue;
			}
			//根据RTUIDCB查询网关上报的数据线(数据中心查询)
			List<String> rtuidcbs = new ArrayList<>(Arrays.asList(diagramProduct.getRtuidcb().split(",")));
			List<DeviceItem> deviceItemList = iDeviceClient.getDeviceItemInfosByRtuidcbs(rtuidcbs);
			if (Func.isEmpty(deviceItemList)) {
				continue;
			}
			//map的key是sid（对应的产品属性编码） 一个rtu下sid是唯一的
			Map<Integer, DeviceItem> mapDeviceItem = buildMap(deviceItemList);
			lists = redisCache.hmGet(ProductConstant.PRODUCT_PROPERTY_KEY, diagramProduct.getProductId());
			//
			for (List<ProductProperty> productPropertys : lists) {
				if (Func.isNotEmpty(productPropertys)) {
					for (ProductProperty productProperty : productPropertys) {
						DeviceItem item = mapDeviceItem.get(Integer.valueOf(productProperty.getPropertyCode()));
						if (Func.isNotEmpty(item)) {
							DiagramItem diagramItem = new DiagramItem();
							BeanUtils.copyProperties(productProperty, diagramItem);
							diagramItem.setId(null);
							diagramItem.setStationId(diagramProduct.getStationId());
							diagramItem.setSiteId(diagramProduct.getSiteId());
							diagramItem.setDiagramId(diagramProduct.getDiagramId());
							diagramItem.setDid(diagramProduct.getDid());
							diagramItem.setPindex(diagramProduct.getPindex());
							diagramItem.setDiagramProductId(diagramProduct.getId());
							diagramItem.setPropertyId(productProperty.getId());
							diagramItem.setItemId(item.getId());
							diagramItem.setRtuidcb(item.getRtuidcb());
							diagramItem.setItemkey(item.getRtuidcb() + productProperty.getPropertyCode());
							diagramItem.setDeptId(diagramProduct.getDeptId());
							diagramItem.setCreateTime(new Date());
							diagramItem.setUpdateTime(new Date());
							diagramItem.setBtype(productProperty.getBtype());

							diagramItems.add(diagramItem);
							DiagramItemMap.put(item.getId(), diagramItem);
						}
					}
				}
			}
		}
		return diagramItems;
	}


	/**
	 * 查询产品数据项
	 */
	@PostMapping("/queryDiagramItem")
	@ApiOperationSupport(order = 10)
	@ApiOperation(value = "查询产品数据项", notes = "传入List")
	public R<List<QueryDiagramItemResq>> queryDiagramItem(@RequestBody QueryDiagramItemReq queryDiagramItemReq) {
		if (Func.isEmpty(queryDiagramItemReq.getDiagramId())) {
			return R.fail("参数DiagramId不能为空");
		}
		if (Func.isEmpty(queryDiagramItemReq.getRtuidcb())) {
			return R.fail("参数rtuidcb不能为空");
		}
		if (Func.isEmpty(queryDiagramItemReq.getProductId())) {
			return R.fail("参数productId不能为空");
		}
		if (Func.isEmpty(queryDiagramItemReq.getDid())) {
			return R.fail("参数did不能为空");
		}

		List<DiagramItem> diagramItems=new ArrayList<>();
		//如果系统图产品id为空则是首次编辑,查产品库
		if(Func.isEmpty(queryDiagramItemReq.getDiagramProductId())){


			//根据RTUIDCB查询网关上报的数据线(数据中心查询)
			List<DeviceItem> deviceItemList = iDeviceClient.getDeviceItemInfosByRtuidcb(queryDiagramItemReq.getRtuidcb());
			if (Func.isEmpty(deviceItemList)) {
				return R.fail(queryDiagramItemReq.getRtuidcb()+"下没有数据项");
			}


			List<List<ProductProperty>>  lists =new ArrayList<>();
			//map的key是sid（对应的产品属性编码） 一个rtu下sid是唯一的
			Map<Integer, DeviceItem> mapDeviceItem = buildMap(deviceItemList);
			lists = redisCache.hmGet(ProductConstant.PRODUCT_PROPERTY_KEY, queryDiagramItemReq.getProductId());
			//
			for (List<ProductProperty> productPropertys : lists) {
				if (Func.isNotEmpty(productPropertys)) {
					for (ProductProperty productProperty : productPropertys) {
						DeviceItem item = mapDeviceItem.get(Integer.valueOf(productProperty.getPropertyCode()));
						if(Func.isNotEmpty(item)){
							DiagramItem diagramItem = new DiagramItem();
							BeanUtils.copyProperties(productProperty, diagramItem);
							diagramItem.setId(NumberUtil.getRandomNum(11));
							diagramItem.setStationId(queryDiagramItemReq.getStationId());
							diagramItem.setSiteId(queryDiagramItemReq.getSiteId());
							diagramItem.setDid(queryDiagramItemReq.getDid());
							diagramItem.setPindex(queryDiagramItemReq.getPindex());
							diagramItem.setDiagramProductId(queryDiagramItemReq.getProductId());
							diagramItem.setPropertyId(productProperty.getId());
							diagramItem.setItemId(item.getId());
							diagramItem.setItemkey(queryDiagramItemReq.getRtuidcb()+productProperty.getPropertyCode());
							diagramItems.add(diagramItem);
						}
					}
				}
			}

		}else{
			DiagramItem diagramItem=new DiagramItem();
			diagramItem.setDiagramId(queryDiagramItemReq.getDiagramId());
			diagramItem.setStationId(queryDiagramItemReq.getStationId());
			diagramItem.setSiteId(queryDiagramItemReq.getSiteId());
			diagramItem.setDid(queryDiagramItemReq.getDid());
			diagramItem.setProductId(queryDiagramItemReq.getProductId());
			diagramItem.setDiagramProductId(queryDiagramItemReq.getDiagramProductId());
			diagramItems=iDiagramItemService.list(Condition.getQueryWrapper(diagramItem));
		}

		List<QueryDiagramItemResq> list=new ArrayList<>();

		List<DiagramItem> ycItems=new ArrayList<>();
		List<DiagramItem> yxItems=new ArrayList<>();
		List<DiagramItem> ytItems=new ArrayList<>();
		List<DiagramItem> ykItems=new ArrayList<>();
		List<DiagramItem> ycycItems=new ArrayList<>();
		List<DiagramItem> ycyxItems=new ArrayList<>();



		for(DiagramItem diagramItem:diagramItems){
			if(Func.equals(ItemStype.TRANSPORTYC.id.toString(),diagramItem.getFtype())){
				ycItems.add(diagramItem);
			}
			if(Func.equals(ItemStype.TRANSPORTYX.id.toString(),diagramItem.getFtype())){
				yxItems.add(diagramItem);
			}
			if(Func.equals(ItemStype.TRANSPORTYT.id.toString(),diagramItem.getFtype())){
				ytItems.add(diagramItem);
			}
			if(Func.equals(ItemStype.TRANSPORTYK.id.toString(),diagramItem.getFtype())){
				ykItems.add(diagramItem);
			}
			if(Func.equals(ItemStype.OPERATIONYC.id.toString(),diagramItem.getFtype())){
				ycycItems.add(diagramItem);
			}
			if(Func.equals(ItemStype.OPERATIONYX.id.toString(),diagramItem.getFtype())){
				ycyxItems.add(diagramItem);
			}
		}
		QueryDiagramItemResq queryDiagramItemResq =new QueryDiagramItemResq();
		queryDiagramItemResq.setFtype(ItemStype.TRANSPORTYC.id.toString());
		queryDiagramItemResq.setFtypeName(ItemStype.TRANSPORTYC.value);
		queryDiagramItemResq.setDiagramItemList(ycItems);
		list.add(queryDiagramItemResq);
		queryDiagramItemResq =new QueryDiagramItemResq();
		queryDiagramItemResq.setFtype(ItemStype.TRANSPORTYX.id.toString());
		queryDiagramItemResq.setFtypeName(ItemStype.TRANSPORTYX.value);
		queryDiagramItemResq.setDiagramItemList(yxItems);
		list.add(queryDiagramItemResq);
		queryDiagramItemResq =new QueryDiagramItemResq();
		queryDiagramItemResq.setFtype(ItemStype.TRANSPORTYT.id.toString());
		queryDiagramItemResq.setFtypeName(ItemStype.TRANSPORTYT.value);
		queryDiagramItemResq.setDiagramItemList(ytItems);
		list.add(queryDiagramItemResq);
		queryDiagramItemResq =new QueryDiagramItemResq();
		queryDiagramItemResq.setFtype(ItemStype.TRANSPORTYK.id.toString());
		queryDiagramItemResq.setFtypeName(ItemStype.TRANSPORTYK.value);
		queryDiagramItemResq.setDiagramItemList(ykItems);
		list.add(queryDiagramItemResq);
		queryDiagramItemResq =new QueryDiagramItemResq();
		queryDiagramItemResq.setFtype(ItemStype.OPERATIONYC.id.toString());
		queryDiagramItemResq.setFtypeName(ItemStype.OPERATIONYC.value);
		queryDiagramItemResq.setDiagramItemList(ycycItems);
		list.add(queryDiagramItemResq);
		queryDiagramItemResq =new QueryDiagramItemResq();
		queryDiagramItemResq.setFtype(ItemStype.OPERATIONYX.id.toString());
		queryDiagramItemResq.setFtypeName(ItemStype.OPERATIONYX.value);
		queryDiagramItemResq.setDiagramItemList(ycyxItems);
		list.add(queryDiagramItemResq);



			return R.data(list);

	}

	/**
	 * 查询自定义展示数据项
	 */
	@GetMapping("/queryDiagramShowItem")
	@ApiOperationSupport(order = 11)
	@ApiOperation(value = "查询自定义展示数据项", notes = "itemId")
	public R<DiagramItem> queryDiagramShowItem( @ApiParam(value = "关联产品id", required = true) @RequestParam Long productId,
		@ApiParam(value = "数据项id", required = true) @RequestParam String itemId) {
		DiagramShowItem queryItem=new DiagramShowItem();
		queryItem.setItemId(itemId);
		DiagramShowItem	showItem=iDiagramShowItemService.getOne(Condition.getQueryWrapper(queryItem));
		if(Func.isEmpty(showItem)){
			return R.fail("itemId参数无效请检查");
		}


		//根据查询网关上报的数据线(数据中心查询)
		DeviceItem deviceItem = iDeviceClient.getDeviceItemInfosByItemid(itemId);
		if (Func.isEmpty(deviceItem)) {
			return R.fail("itemId数据项无效请检查");
		}

		DiagramItem diagramItem = new DiagramItem();

		List<List<ProductProperty>>  lists =new ArrayList<>();
		//map的key是sid（对应的产品属性编码） 一个rtu下sid是唯一的
		lists = redisCache.hmGet(ProductConstant.PRODUCT_PROPERTY_KEY, productId);
		//
		for (List<ProductProperty> productPropertys : lists) {
			if (Func.isNotEmpty(productPropertys)) {
				for (ProductProperty productProperty : productPropertys) {
						BeanUtils.copyProperties(productProperty, diagramItem);
						diagramItem.setId(NumberUtil.getRandomNum(11));
						diagramItem.setStationId(showItem.getStationId());
						diagramItem.setSiteId(showItem.getSiteId());
						diagramItem.setDid(showItem.getDid());
						diagramItem.setPindex(showItem.getPindex());
						diagramItem.setDiagramProductId(showItem.getProductId());
						diagramItem.setPropertyId(productProperty.getId());
						diagramItem.setItemId(itemId);
						diagramItem.setItemkey(showItem.getRtuidcb()+productProperty.getPropertyCode());
						diagramItem.setBtype(deviceItem.getBtype());
					//diagramItem.setFtype(deviceItem.getStype());
					diagramItem.setUnit(deviceItem.getUnit());
					diagramItem.setRatio(deviceItem.getRatio());
					diagramItem.setUplimit(deviceItem.getUlimit());
					diagramItem.setUpuplimit(deviceItem.getUulimit());
					diagramItem.setLowlimit(deviceItem.getLlimit());
					diagramItem.setLowlowlimit(deviceItem.getLllimit());
					diagramItem.setStore(deviceItem.getStore());
					diagramItem.setBasic(deviceItem.getBasic());
					diagramItem.setCtratio(deviceItem.getCtratio());
					diagramItem.setAlarm(deviceItem.getPalarm());
					diagramItem.setSendSms(deviceItem.getAlarmsms());
					diagramItem.setSendEmail(deviceItem.getAlarmemail());
					diagramItem.setAlarmUrl(deviceItem.getAlarmurl());
				}
			}
		}
		return R.data(diagramItem);
	}

	@ApiLog("修改自定义系统图展示数据项")
	@PostMapping("/updateDiagramShowItem")
	@ApiOperationSupport(order = 12)
	@ApiOperation(value = "修改自定义系统图展示数据项", notes = "diagramItem")
	@Transactional(rollbackFor = Exception.class)
	@Synchronized
	public R updateDiagramShowItem(@RequestBody DiagramItem diagramItem) {
		Set<String> deviceCodes =new HashSet<>();
		Set<String> rtuids=new HashSet<>();
		if ( Func.isEmpty(diagramItem.getItemId())) {
			return R.fail("数据项id不能为空");
		}

		if (Func.isEmpty(diagramItem.getDid())) {
			return R.fail("网关不能为空");
		}
		DiagramShowItem queryItem=new DiagramShowItem();
		queryItem.setItemId(diagramItem.getItemId());
		DiagramShowItem	showItem=iDiagramShowItemService.getOne(Condition.getQueryWrapper(queryItem));
		if (Func.isEmpty(showItem)) {
			return R.fail("itemId数据项无效请检查");
		}

		deviceCodes.add(diagramItem.getDid());
		rtuids.add(diagramItem.getRtuidcb());
		Map<String, DiagramItem> DiagramItemMap = new HashMap<>();
		DiagramItemMap.put(showItem.getItemId(), diagramItem);
		sendItemToDataCenter(deviceCodes, rtuids,DiagramItemMap);

		return R.status(true);
	}

		/**
		 * 提交修改系统图数据项
		 */
	@ApiLog("提交修改系统图数据项")
	@PostMapping("/updateDiagramItem")
	@ApiOperationSupport(order = 12)
	@ApiOperation(value = "提交保存编辑系统图数据项", notes = "传入List")
	@Transactional(rollbackFor = Exception.class)
	@Synchronized
	public R updateDiagramItem(@RequestBody List<DiagramItemReq> diagramItemReqs) {
		List<DiagramItem> updatediagramItems = new ArrayList<>();
		List<DiagramItem> adddiagramItems = new ArrayList<>();

		Map<String, DiagramItem> DiagramItemMap = new HashMap<>();
		//Map<String, DeviceItem> DeviceItemMap = new HashMap<>();
		Set<String> deviceCodes =new HashSet<>();
		Set<String> rtuids=new HashSet<>();
		for (DiagramItemReq diagramItemReq : diagramItemReqs) {
//			if (Func.isEmpty(diagramItemReq.getId())) {
//				return R.fail("主键ID不能为空");
//			}
			if ( Func.isEmpty(diagramItemReq.getItemId())) {
				return R.fail("数据项id不能为空");
			}
//			if (Func.isEmpty(diagramItemReq.getRtuidcb())){
//				return R.fail("rtuidcb不能为空");
//			}
			if (Func.isEmpty(diagramItemReq.getDid())) {
				return R.fail("网关不能为空");
			}

			deviceCodes.add(diagramItemReq.getDid());
			rtuids.add(diagramItemReq.getRtuidcb());
		}
		for (DiagramItemReq diagramItemReq : diagramItemReqs) {
			DiagramItem diagramItem = new DiagramItem();
			if(Func.isNotEmpty(diagramItemReq.getItemId())){
				diagramItem =iDiagramItemService.getOneDiagramItemByItem(diagramItemReq.getItemId());
			}

			if (Func.isNotEmpty(diagramItem)) {
				diagramItem = new DiagramItem();
				BeanUtils.copyProperties(diagramItemReq, diagramItem);
				DiagramItemMap.put(diagramItem.getItemId(), diagramItem);
				DeviceItem item = iDeviceClient.getDeviceItemInfosByItemid(diagramItem.getItemId());
				if(Func.isNotEmpty(item)) {
					updatediagramItems.add(diagramItem);
					//DeviceItemMap.put(item.getId(), item);
				}
			}else{
				diagramItem = new DiagramItem();


				BeanUtils.copyProperties(diagramItemReq, diagramItem);
				diagramItem.setId(null);
				diagramItem.setStationId(diagramItemReq.getStationId());
				diagramItem.setSiteId(diagramItemReq.getSiteId());
				diagramItem.setDiagramId(diagramItemReq.getDiagramId());
				diagramItem.setDid(diagramItemReq.getDid());
				diagramItem.setPindex(diagramItemReq.getPindex());
				diagramItem.setDiagramProductId(diagramItemReq.getDiagramProductId());
				diagramItem.setItemkey(diagramItem.getItemId());
				diagramItem.setDeptId(diagramItemReq.getDeptId());
				diagramItem.setCreateTime(new Date());
				diagramItem.setUpdateTime(new Date());

				List<List<ProductProperty>> lists = redisCache.hmGet(ProductConstant.PRODUCT_PROPERTY_KEY, diagramItemReq.getProductId());
				ProductProperty productProperty =null;
				for (List<ProductProperty> productPropertys : lists) {
					if (Func.isNotEmpty(productPropertys)) {
						for (ProductProperty productPro : productPropertys) {
							if(Func.equals(diagramItemReq.getPropertyCode(),productPro.getPropertyCode())){
								productProperty=productPro;
								continue;
							}
						}
					}
				}
				if(Func.isNotEmpty(productProperty)){
					diagramItem.setPropertyId(productProperty.getId());
				}
//				else{
//					return R.fail("该产品模型没属性"+diagramItem.getPropertyName()+"请先到产品模型添加");
//				}
				adddiagramItems.add(diagramItem);
			}
		}

		//插入新的数据项
		iDiagramItemService.saveBatch(adddiagramItems);
		boolean	res= iDiagramItemService.updateBatchById(updatediagramItems);
		//插入数据项成功则发送数据中心 更新 遥测，遥信，遥控 设置
		if (res){
			sendItemToDataCenter(deviceCodes, rtuids,DiagramItemMap);
		}

		return R.status(true);

	}
	/**
	 * 保存网关，并订阅MQTT
	 * @param dids
	 */
	@Async
	public void saveDevice(Set<DeviceRelation> dids){
		if(Func.isNotEmpty(dids)) {
			List<String> gwids = new ArrayList<>();
			for (DeviceRelation device : dids) {
				DeviceRelation deviceRelation = iDeviceRelationService.getOne(Condition.getQueryWrapper(device));
				if (Func.isNotEmpty(deviceRelation)) {
					dids.remove(device);
				}
				gwids.add(device.getDid());
			}
			iDeviceRelationService.saveBatch(dids);
			iTopicClient.addDeviceTopic(gwids);
		}
	}


	public Map<Integer, DeviceItem> buildMap(List<DeviceItem> deviceItems) {
		if (deviceItems.isEmpty()) {
			return null;
		} else {
			Map<Integer, DeviceItem> idItemMap = new HashMap(deviceItems.size());
			Iterator var3 = deviceItems.iterator();

			while(var3.hasNext()) {
				DeviceItem item = (DeviceItem)var3.next();
				idItemMap.put(item.getSid(), item);
			}

			return idItemMap;
		}
	}


	/**
	 * 更新 遥测，遥信，遥控 设置
	 * @param
	 */
	@Async
	public void sendItemToDataCenter(Set<String> deviceCodes,Set<String> rtuids,Map<String,DiagramItem> DiagramItemMap){
		String dids= org.apache.commons.lang.StringUtils.join(deviceCodes.toArray(), ",");
		String rtuidcbs= org.apache.commons.lang.StringUtils.join(rtuids.toArray(), ",");

		//遥测
		List<YcTran> ycTrans = new ArrayList<>();
		List<YcAlarmConfig> ycAlarmConfigs = new ArrayList<>();
		List<YcStore> ycStores = new ArrayList<>();
		//遥信
		List<YxTran> yxTrans = new ArrayList<>();
		List<YxAlarmConfig> yxAlarmConfigs = new ArrayList<>();
		//遥控
		List<YkTran> ykTrans = new ArrayList<>();
		//计算遥测
		List<YcCalc> ycCalcs = new ArrayList<>();
		//计算遥信
		List<YxCalc> yxCalcs = new ArrayList<>();

		for(Map.Entry<String,DiagramItem> m:DiagramItemMap.entrySet()){
			DiagramItem diagramItem=m.getValue();
			//DeviceItem deviceItem=DeviceItemMap.get(diagramItem.getItemId());
			//ProductProperty productProperty=redisCache.hGet(ProductConstant.PROPERTY_KEY,diagramItem.getPropertyId());
			//判断4遥类型
			Integer stype = Integer.valueOf(diagramItem.getFtype());
			if(ItemStype.TRANSPORTYC.id.equals(stype)){
				setYcInfo(ycTrans, ycAlarmConfigs, ycStores, diagramItem);
			}else if(ItemStype.TRANSPORTYX.id.equals(stype)){
				setYxInfo(yxTrans, yxAlarmConfigs, diagramItem);
			}else if(ItemStype.TRANSPORTYT.id.equals(stype)){
				//遥调
			}else if(ItemStype.TRANSPORTYK.id.equals(stype)){
				setYkInfo(ykTrans, diagramItem);
			}
			//计算遥测
			else if(ItemStype.OPERATIONYC.id.equals(stype)){
				setYcCalcInfo(ycCalcs, diagramItem);
			}
			//计算遥信
			else if(ItemStype.OPERATIONYX.id.equals(stype)){
				setYxCalcInfo(yxCalcs, diagramItem);
			}
		}

		//执行传输库参数配置
		boolean clearCache = false;
		if(Func.isNotEmpty(ycTrans)){
			clearCache = true;
			YcTranReq req=new YcTranReq();
			req.setYcTranInfos(ycTrans);
			req.setRtuIds(rtuidcbs);
			iDataItemClient.setYcTranInfos(req);
		}
		if(Func.isNotEmpty(yxTrans)){
			YxTranReq req=new YxTranReq();
			req.setYxTranInfos(yxTrans);
			req.setRtuIds(rtuidcbs);
			iDataItemClient.setYxTranInfos(req);
		}
		if(Func.isNotEmpty(ykTrans)){
			YkTranReq req=new YkTranReq();
			req.setYkTranInfos(ykTrans);
			req.setRtuIds(rtuidcbs);
			iDataItemClient.setYkTranInfos(req);
		}

		if(Func.isNotEmpty(ycCalcs)){
			YcCalcReq req=new YcCalcReq();
			req.setYcCalcs(ycCalcs);
			req.setDeviceCodes(dids);
			iDataItemClient.setYcCalcItem(req);
		}
		if(Func.isNotEmpty(yxCalcs)){
			YxCalcReq req=new YxCalcReq();
			req.setYxCalcs(yxCalcs);
			req.setDeviceCodes(dids);
			iDataItemClient.setYxCalcItem(req);
		}

		//执行存储库参数配置
		if(Func.isNotEmpty(ycStores)){
			clearCache = true;
			YcStoreReq req=new YcStoreReq();
			req.setYcStore(ycStores);
			req.setRtuIds(new ArrayList<>(rtuids));
			iDataItemClient.setYcStoreInfos(req);
		}

		//执行告警库参数配置
		if(Func.isNotEmpty(ycAlarmConfigs)){
			clearCache = true;
			YcAlarmConfigReq req= new YcAlarmConfigReq();
			req.setYcAlarmConfig(ycAlarmConfigs);
			req.setRtuIds(new ArrayList<>(rtuids));
			iDataItemClient.setYcAlarmConfig(req);
		}
		if(Func.isNotEmpty(yxAlarmConfigs)){
			clearCache = true;
			YxAlarmConfigReq req= new YxAlarmConfigReq();
			req.setYxAlarmConfig(yxAlarmConfigs);
			req.setRtuIds(new ArrayList<>(rtuids));
			iDataItemClient.setYxAlarmConfig(req);
		}

	}

	/**
	 * 设置遥测参数
	 * @param ycTrans
	 * @param ycAlarmConfigs
	 * @param ycStores
	 * @param deviceItem
	 */
	private void setYcInfo(List<YcTran> ycTrans, List<YcAlarmConfig> ycAlarmConfigs, List<YcStore> ycStores, DiagramItem deviceItem) {
		//遥测
		YcTran ycTran = new YcTran();
		ycTran.setId(deviceItem.getItemId());
		//名称带rtu名称更能区分位置，所以名称不同步物模型配置
		ycTran.setShortname(deviceItem.getPropertyAlias());
		ycTran.setBasic(deviceItem.getBasic());
		ycTran.setPalarm(deviceItem.getAlarm());
		ycTran.setStore(deviceItem.getStore());
		ycTran.setBtype(deviceItem.getBtype());
		ycTran.setCtratio(deviceItem.getCtratio());
		ycTran.setDisplayname(deviceItem.getPropertyName());
		ycTrans.add(ycTran);

		//判断是否告警
		if(BooleanEnum.YES.id.equals(deviceItem.getAlarm())){
			YcAlarmConfig ycAlarmConfig = new YcAlarmConfig();
			ycAlarmConfig.setId(deviceItem.getItemId());
			ycAlarmConfig.setUlimit(deviceItem.getUplimit());
			ycAlarmConfig.setLlimit(deviceItem.getLowlimit());
			ycAlarmConfig.setUulimit(deviceItem.getUpuplimit());
			ycAlarmConfig.setLllimit(deviceItem.getLowlowlimit());
			ycAlarmConfig.setDuration(deviceItem.getDuration());
			ycAlarmConfig.setAlarmsms(deviceItem.getSendSms());
			ycAlarmConfig.setAlarmemail(deviceItem.getSendEmail());
			ycAlarmConfig.setAlarmurl(deviceItem.getAlarmUrl());
			ycAlarmConfig.setAlarmtype(deviceItem.getAlarmType());
			ycAlarmConfigs.add(ycAlarmConfig);
		}
		//判断是否存储
		if(BooleanEnum.YES.id.equals(deviceItem.getStore())){
			ycTran.setStore(GwsubscribeConstant.DEFAULT_SOTRE);
			YcStore ycStore = new YcStore();
			ycStore.setId(deviceItem.getItemId());
			ycStore.setStore(GwsubscribeConstant.DEFAULT_SOTRE);
			ycStore.setCalcrule(deviceItem.getCalcrule());
			ycStores.add(ycStore);
		}
	}
	/**
	 * 设置遥控参数
	 * @param ykTrans
	 * @param deviceItem
	 */
	private void setYkInfo(List<YkTran> ykTrans, DiagramItem deviceItem) {
		//遥控
		YkTran ykTran = new YkTran();
		ykTran.setId(deviceItem.getItemId());
		ykTran.setShortname(deviceItem.getPropertyAlias());
		ykTran.setDesc(deviceItem.getRemark());
		ykTran.setDisplayname(deviceItem.getPropertyName());
		ykTrans.add(ykTran);
	}

	/**
	 * 设置遥信参数
	 * @param yxTrans
	 * @param yxAlarmConfigs
	 * @param deviceItem
	 */
	private void setYxInfo(List<YxTran> yxTrans, List<YxAlarmConfig> yxAlarmConfigs, DiagramItem deviceItem) {
		//遥信
		YxTran yxTran = new YxTran();
		yxTran.setId(deviceItem.getItemId());
		yxTran.setShortname(deviceItem.getPropertyAlias());
		yxTran.setPalarm(deviceItem.getAlarm());
		yxTran.setBtype(deviceItem.getBtype());
		yxTran.setDisplayname(deviceItem.getPropertyName());
		yxTrans.add(yxTran);
		//判断是否告警
		if(BooleanEnum.YES.id.equals(deviceItem.getAlarm())){
			YxAlarmConfig yxAlarmConfig = new YxAlarmConfig();
			yxAlarmConfig.setId(deviceItem.getItemId());
			yxAlarmConfig.setYxalarmval(deviceItem.getYxAlarmVal());
			yxAlarmConfig.setYxalarmlevel(deviceItem.getYxAlarmLevel());
			yxAlarmConfig.setAlarmsms(deviceItem.getSendSms());
			yxAlarmConfig.setAlarmemail(deviceItem.getSendEmail());
			yxAlarmConfig.setAlarmurl(deviceItem.getAlarmUrl());
			yxAlarmConfig.setAlarmtype(deviceItem.getAlarmType());
			yxAlarmConfigs.add(yxAlarmConfig);
		}
	}


	/**
	 * 设置计算遥测参数
	 * @param ycCalcs
	 * @param deviceItem
	 */
	private void setYcCalcInfo(List<YcCalc> ycCalcs, DiagramItem deviceItem) {
		//遥控
		YcCalc ycCalc = new YcCalc();
		ycCalc.setShortname(deviceItem.getPropertyAlias());
		ycCalc.setBtype(deviceItem.getBtype());
		ycCalc.setDisplayname(deviceItem.getPropertyAlias());
		ycCalc.setFormula(deviceItem.getFormula());
		ycCalc.setId(deviceItem.getItemId());
		ycCalc.setName(deviceItem.getPropertyName());
		ycCalc.setPalarm(deviceItem.getAlarm());
		ycCalc.setRelid(deviceItem.getFormula());
		ycCalc.setStore(deviceItem.getStore());
		ycCalc.setUnit(deviceItem.getUnit());
		ycCalcs.add(ycCalc);
	}



	/**
	 * 设置计算遥测参数
	 * @param yxCalcs
	 * @param deviceItem
	 */
	private void setYxCalcInfo(List<YxCalc> yxCalcs, DiagramItem deviceItem) {
		//遥信
		YxCalc yxCalc = new YxCalc();
		yxCalc.setShortname(deviceItem.getPropertyAlias());
		yxCalc.setBtype(deviceItem.getBtype());
		yxCalc.setDisplayname(deviceItem.getPropertyAlias());
		yxCalc.setFormula(deviceItem.getFormula());
		yxCalc.setId(deviceItem.getItemId());
		yxCalc.setName(deviceItem.getPropertyName());
		yxCalc.setPalarm(deviceItem.getAlarm());
		//yxCalc.setRelid(deviceItem.getRelid());
		yxCalc.setUnit(deviceItem.getUnit());
		//yxCalc.setDesc0(deviceItem.getd);
		//yxCalc.setDesc1(deviceItem.getDesc1());
		yxCalcs.add(yxCalc);
	}


	/**
	 * 添加计算遥测遥信数据项
	 */
	@ApiLog("系统图 添加计算遥测遥信数据项")
	@PostMapping("/addCalcItem")
	@ApiOperationSupport(order = 13)
	@ApiOperation(value = "添加计算遥测遥信数据项", notes = "传入List")
	public R addCalcItem(@RequestBody List<CalcItemReq> calcItemReqs) {
		boolean res=true;
		for (CalcItemReq calcItemReq :calcItemReqs) {
			if (Func.isEmpty(calcItemReq.getDid())) {
			return R.fail("网关did参数缺失");
			}
			if (Func.isEmpty(calcItemReq.getFtype())) {
				return R.fail("ftype参数缺失");
			}
			if (Func.isEmpty(calcItemReq.getItemId())) {
				return R.fail("数据itemId项参数缺失");
			}
			if (Func.isEmpty(calcItemReq.getFormula())) {
				return R.fail("js脚本参数缺失");
			}
//			if(Func.isEmpty(calcItemReq.getRelid())){
//				return R.fail("关联数据项参数缺失");
//			}
			DeviceItem deviceItem =new DeviceItem();
			deviceItem.setId(calcItemReq.getItemId());
			deviceItem.setStype(Integer.valueOf(calcItemReq.getFtype()));
			deviceItem.setGetval(calcItemReq.getFormula());
			deviceItem.setGwid(calcItemReq.getDid());
			DeviceItem enerty = iDeviceClient.getDeviceItemInfosByItemid(calcItemReq.getItemId());
			if(Func.isEmpty(enerty)){
				res=iDataItemClient.addCalcItem(deviceItem);
			}
		}
		return R.status(res);
	}
	@ApiOperation(value = "脚本验证", httpMethod = "POST")
	@PostMapping(value = "/jsCheck")
	public R<Object> jsCheck(@ApiParam(value = "js脚本") @RequestParam(value = "content") String content,
										  @ApiParam(value = "网关id") @RequestParam(value = "deviceCode")  String deviceCode) throws Exception {
		Set<String> deviceIds = new HashSet<String>();
		if(!content.contains("_")){
			return R.data(content.replace("return",""));
		}
		String[] s = content.split("\\+");
		for(String str : s) {
			if(str.contains("_")){
				str = str.split("_")[1];
				deviceIds.add(str);
			}
		}
		List<String> deviceIdList = new ArrayList<String>();
		deviceIdList.addAll(deviceIds);
		Map<String, List<DeviceItemRealTimeData>> code2Dates = iDeviceClient.getDeviceItemRealTimeDataByCodes(deviceIdList);
		if(Func.isEmpty(code2Dates)){
			return R.fail("");
		}
		System.out.println("--code2Dates--"+code2Dates);
		Map<String, Double> id2Data = new HashMap<>(16);
		Double val;
		for(Map.Entry<String, List<DeviceItemRealTimeData>> entry : code2Dates.entrySet()) {
			for(DeviceItemRealTimeData realTimeData : entry.getValue()) {
				id2Data.put(realTimeData.getId(), realTimeData.getVal());
			}
		}
		Double res = ComputeUtil.compute(id2Data, content);
		return R.data(res);
	}

	@ApiLog("系统图 修改编辑计算遥测")
	@GetMapping("/updateYcCalcItem")
	@ApiOperationSupport(order = 14)
	@ApiOperation(value = "修改编辑计算遥测", notes = "传入List")
	public R updateYcCalcItem(@RequestBody List<YcCalc> ycCalcs,@ApiParam(value = "网关id") @RequestParam(value = "deviceCodes")String deviceCodes) {
		if (Func.isEmpty(ycCalcs) || Func.isEmpty(deviceCodes)) {
			return R.fail("传输数据为空");
		}
		YcCalcReq req=new YcCalcReq();
		req.setYcCalcs(ycCalcs);
		req.setDeviceCodes(deviceCodes);
		return R.status(iDataItemClient.setYcCalcItem(req));
	}

	@ApiLog("系统图 修改编辑计算遥信")
	@GetMapping("/updateYxCalcItem")
	@ApiOperationSupport(order = 15)
	@ApiOperation(value = "修改编辑计算遥信", notes = "传入List")
	public R updateYxCalcItem(@RequestBody List<YxCalc> yxCalcs,@ApiParam(value = "网关id") @RequestParam(value = "deviceCodes")String deviceCodes) {
		if (Func.isEmpty(yxCalcs) || Func.isEmpty(deviceCodes)) {
			return R.fail("传输数据为空");
		}
		YxCalcReq req=new YxCalcReq();
		req.setYxCalcs(yxCalcs);
		req.setDeviceCodes(deviceCodes);
		return R.status(iDataItemClient.setYxCalcItem(req));
	}

}
