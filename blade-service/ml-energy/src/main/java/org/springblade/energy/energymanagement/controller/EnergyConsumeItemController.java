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
package org.springblade.energy.energymanagement.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.diagram.service.IDiagramItemService;
import org.springblade.energy.energymanagement.dto.ConsumeItemReq;
import org.springblade.energy.energymanagement.dto.ConsumeItemResq;
import org.springblade.energy.energymanagement.dto.EnergyConsumeItemResq;
import org.springblade.energy.energymanagement.entity.EnergyConsumeItem;
import org.springblade.energy.energymanagement.entity.EnergyConsumeType;
import org.springblade.energy.energymanagement.service.IEnergyConsumeItemService;
import org.springblade.energy.energymanagement.service.IEnergyConsumeTypeService;
import org.springblade.enums.ProductSid;
import org.springblade.util.NumberUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  控制器
 *
 * @author bond
 * @since 2020-07-04
 */
@RestController
@AllArgsConstructor
@RequestMapping("/energyconsumeitem")
@Api(value = "能源管理-能耗分析", tags = "能源管理-能耗分析")
//@Api(value = "能耗分析-能耗数据项配置", tags = "接口")
public class EnergyConsumeItemController extends BladeController {

	private IEnergyConsumeItemService energyConsumeItemService;
	private IEnergyConsumeTypeService energyConsumeTypeService;
	private IDiagramItemService iDiagramItemService;
	/**
	 * 自定义分页
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "能耗数据项配置列表", notes = "")
	public R<List<EnergyConsumeItemResq>> page(@ApiParam(value = "能源分类") @RequestParam(value= "energyType",required=true)Integer energyType,
	 @ApiParam(value = "站点id") @RequestParam(value = "stationId" ,required=false) Long stationId,
	@ApiParam(value = "位置id") @RequestParam(value = "siteId",required=false) Long siteId){
		EnergyConsumeType energyConsumeType=new EnergyConsumeType();
		energyConsumeType.setEnergyType(energyType);
		energyConsumeType.setIsDeleted(0);
		List<EnergyConsumeType> list =energyConsumeTypeService.list(Condition.getQueryWrapper(energyConsumeType));

		List<EnergyConsumeItemResq> energyConsumeItemResqList=new ArrayList<>();
		for(EnergyConsumeType consumeType: list){
			EnergyConsumeItemResq resq=new EnergyConsumeItemResq();
			BeanUtils.copyProperties(consumeType,resq);

			Map<String,Object> queryMap=new HashMap<>();
			queryMap.put("stationId",stationId);
			queryMap.put("siteId",siteId);
			queryMap.put("consumeType",resq.getConsumeType());
			List<ConsumeItemResq> consumeItemResqList= energyConsumeItemService.selectEnergyConsumeItemByMap(queryMap);
			resq.setItemList(consumeItemResqList);
			energyConsumeItemResqList.add(resq);
		}

		return R.data(energyConsumeItemResqList);
	}

	/**
	 * 新增或修改
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "添加能耗", notes = "传入energyConsumeItem")
	@Transactional(rollbackFor = Exception.class)
	public R submit(@Valid @RequestBody ConsumeItemReq consumeItemReq) {

		Map<String,Object> queryMap=new HashMap<>();
		queryMap.put("stationId",consumeItemReq.getStationId());
		queryMap.put("siteId",consumeItemReq.getSiteId());
		queryMap.put("energyType",consumeItemReq.getEnergyType());
		queryMap.put("consumeType",consumeItemReq.getConsumeType());
		List<ConsumeItemResq> consumeItemResqList= energyConsumeItemService.selectEnergyConsumeItemByMap(queryMap);
		List<Long> ids=new ArrayList<>();
		for(ConsumeItemResq vo:consumeItemResqList){
			ids.add(vo.getId());
		}
		if(Func.isNotEmpty(ids)){
			boolean r= energyConsumeItemService.dellEnergyConsumeItemByIds(ids);
		}

		List<ConsumeItemResq> subItems=consumeItemReq.getConsumeItemResqList();
		List<EnergyConsumeItem> consumeItems= new ArrayList<>();

		for(ConsumeItemResq consumeItemResq :subItems){
			EnergyConsumeItem entity=new EnergyConsumeItem();
			entity.setId(NumberUtil.getRandomNum(11));
			entity.setItemId(consumeItemResq.getItemId());
			entity.setConsumeType(consumeItemResq.getConsumeType());
			consumeItems.add(entity);
		}
		if(Func.isNotEmpty(consumeItems)){
			return R.status(energyConsumeItemService.saveBatch(consumeItems));
		}
		return R.success("操作成功");
	}

	@GetMapping("/linkedItem")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "关联数据项", notes = "")
	public R<Map<String,Object>> linkedItem(
		@ApiParam(value = "能源分类") @RequestParam(value= "energyType",required=true)Integer energyType,
		@ApiParam(value = "能耗类别") @RequestParam(value= "consumeType",required=true)Integer consumeType,
		@ApiParam(value = "站点id") @RequestParam(value = "stationId" ,required=false) Long stationId,
		@ApiParam(value = "位置id") @RequestParam(value = "siteId",required=false) Long siteId){

		Map<String,Object> queryMap=new HashMap<>();
		queryMap.put("stationId",stationId);
		queryMap.put("siteId",siteId);
		queryMap.put("energyType",energyType);
		queryMap.put("consumeType",consumeType);
		List<ConsumeItemResq> alreadylinkedItem= energyConsumeItemService.selectEnergyConsumeItemByMap(queryMap);



		String propertyCodes="";
		//电能（电量）
		if(Func.equals(1,energyType)){
			propertyCodes= ProductSid.SID31.id;
		}
		//水能（水量）
		if(Func.equals(2,energyType)){
			propertyCodes=ProductSid.SID159.id;
		}
		//气能（气量）
		if(Func.equals(3,energyType)){
			propertyCodes=ProductSid.SID161.id;
		}
		Map<String,Object> map=new HashMap<>();
		map.put("stationId",stationId);
		map.put("siteId",siteId);
		map.put("propertyCode",propertyCodes);
		List<ConsumeItemResq> nolinkedItem= energyConsumeItemService.selectNolinkedItemByMap(map);

		Map<String,Object> resqMap=new HashMap<>();
		resqMap.put("alreadylinkedItem",alreadylinkedItem);
		resqMap.put("nolinkedItem",nolinkedItem);
		return R.data(resqMap);
	}

}
