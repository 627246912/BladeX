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
package org.springblade.energy.equipmentmanagement.realtimemonitor.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.bean.Product;
import org.springblade.constants.ProductConstant;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.diagram.entity.DiagramItem;
import org.springblade.energy.diagram.entity.DiagramProduct;
import org.springblade.energy.diagram.service.IDiagramItemService;
import org.springblade.energy.diagram.service.IDiagramProductService;
import org.springblade.energy.statistics.dto.CurveDataReq;
import org.springblade.energy.statistics.dto.EquipmentResq;
import org.springblade.enums.ProductDtype;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 重点能耗设备-》仪表配置 控制器
 *
 * @author bond
 * @since 2020-05-06
 */
@RestController
@AllArgsConstructor
@RequestMapping("/eecmeter")
@Api(value = "重点能耗设备-》实时监测", tags = "重点能耗设备-》实时监测")
public class EquipmentController extends BladeController {
	@Autowired
	private BladeRedisCache redisCache;
	private IDiagramProductService iDiagramProductService;
	private IDiagramItemService iDiagramItemService;

	@GetMapping("/getEccEquipment")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "获取重点能耗设备", notes = "")
	public R getEccEquipment(@ApiParam(value = "站点id") @RequestParam(value = "stationId") Long stationId,
							 @ApiParam(value = "位置id") @RequestParam(value = "siteId") Long siteId,
							 @ApiParam(value = "产品类别ID") @RequestParam(value = "productPtype") String productPtype)
	{
		ProductDtype ProductDtypeEntity=ProductDtype.getProductDtype(productPtype);
		if(Func.isEmpty(ProductDtypeEntity)){
			return R.fail("产品类别ID不存在："+productPtype);
		}

		DiagramProduct product=new DiagramProduct();
		product.setStationId(stationId);
		product.setProductDtype(productPtype);
		List<DiagramProduct> diagramProductList =iDiagramProductService.list(Condition.getQueryWrapper(product));
		if(Func.isEmpty(diagramProductList)){
			R.fail("无设备");
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





	@GetMapping("/airMonitor")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "空调-(实时数据监测)", notes = "传入diagramProductId")
	public R<Map<String,Object>> airMonitor(@ApiParam(value = "系统图产品ID") @RequestParam(value = "diagramProductId") String diagramProductId) {
		//查询具体数据项
		Map<String,Object> queryMap=new HashMap<>();
		//queryMap.put("stationId",curveDataInfo.getStationId());
		queryMap.put("diagramProductId",diagramProductId);
		List<DiagramItem> diagramItemList=iDiagramItemService.selectDiagramItemByMap(queryMap);


		return null;
	}
	@GetMapping("/airRunDataStatis")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "空调-(运行数据统计)", notes = "传入curveDataReq")
	public R<Map<String,Object>> airRunDataStatis(@RequestBody CurveDataReq curveDataReq) {
		//查询具体数据项
		Map<String,Object> queryMap=new HashMap<>();
		//queryMap.put("stationId",curveDataInfo.getStationId());
		queryMap.put("diagramProductId",curveDataReq.getDiagramProductId());
		List<DiagramItem> diagramItemList=iDiagramItemService.selectDiagramItemByMap(queryMap);


		return null;
	}
}
