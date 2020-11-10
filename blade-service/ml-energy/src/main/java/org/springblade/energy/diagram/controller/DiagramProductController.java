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
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.diagram.entity.DiagramProduct;
import org.springblade.energy.diagram.service.IDiagramProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  控制器
 *
 * @author bond
 * @since 2020-03-31
 */
@RestController
@AllArgsConstructor
@RequestMapping("/diagramproduct")
@Api(value = "系统图产品接口", tags = "系统图产品接口")
public class DiagramProductController extends BladeController {

	private IDiagramProductService diagramProductService;




	@GetMapping("/queryProductList")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "查询系统图产品", notes = "传入ids")
	public R<List<DiagramProduct>> queryProductList(@ApiParam(value = "站点ID", required = false) @RequestParam(required = false) Long stationId,
													@ApiParam(value = "位置id", required = true) @RequestParam Long siteId	,
													@ApiParam(value = "系统图类别，多个时用逗号连接", required = true) @RequestParam String DiagramType	,
													@ApiParam(value = "设备类型，多个时用逗号连接", required = false) @RequestParam(required = false) String productDtype
	){
		Map<String,Object> map=new HashMap<>();
		map.put("stationId",stationId);
		map.put("siteId",siteId);
		if(Func.isNotEmpty(DiagramType)) {
			List<String> diagramTypes = Arrays.asList(DiagramType.split(","));
			map.put("diagramTypes", diagramTypes);
		}
		if(Func.isNotEmpty(productDtype)) {
			List<String> productDtypes = Arrays.asList(productDtype.split(","));
			map.put("productDtypes",productDtypes);
		}

		List<DiagramProduct> list=diagramProductService.queryDiagramProductByMap(map);
		return 	R.data(list);
	}

//	/**
//	 * 详情
//	 */
//	@GetMapping("/detail")
//	@ApiOperationSupport(order = 1)
//	@ApiOperation(value = "详情", notes = "传入diagramProduct")
//	public R<DiagramProductVO> detail(DiagramProduct diagramProduct) {
//		DiagramProduct detail = diagramProductService.getOne(Condition.getQueryWrapper(diagramProduct));
//		return R.data(DiagramProductWrapper.build().entityVO(detail));
//	}
//
//	/**
//	 * 分页
//	 */
//	@GetMapping("/list")
//	@ApiOperationSupport(order = 2)
//	@ApiOperation(value = "分页", notes = "传入diagramProduct")
//	public R<IPage<DiagramProductVO>> list(DiagramProduct diagramProduct, Query query) {
//		IPage<DiagramProduct> pages = diagramProductService.page(Condition.getPage(query), Condition.getQueryWrapper(diagramProduct));
//		return R.data(DiagramProductWrapper.build().pageVO(pages));
//	}
//
//
//	/**
//	 * 自定义分页
//	 */
//	@GetMapping("/page")
//	@ApiOperationSupport(order = 3)
//	@ApiOperation(value = "分页", notes = "传入diagramProduct")
//	public R<IPage<DiagramProductVO>> page(DiagramProductVO diagramProduct, Query query) {
//		IPage<DiagramProductVO> pages = diagramProductService.selectDiagramProductPage(Condition.getPage(query), diagramProduct);
//		return R.data(pages);
//	}
//
//	/**
//	 * 新增
//	 */
//	@PostMapping("/save")
//	@ApiOperationSupport(order = 4)
//	@ApiOperation(value = "新增", notes = "传入diagramProduct")
//	public R save(@Valid @RequestBody DiagramProduct diagramProduct) {
//		return R.status(diagramProductService.save(diagramProduct));
//	}
//
//	/**
//	 * 修改
//	 */
//	@PostMapping("/update")
//	@ApiOperationSupport(order = 5)
//	@ApiOperation(value = "修改", notes = "传入diagramProduct")
//	public R update(@Valid @RequestBody DiagramProduct diagramProduct) {
//		return R.status(diagramProductService.updateById(diagramProduct));
//	}
//
//	/**
//	 * 新增或修改
//	 */
//	@PostMapping("/submit")
//	@ApiOperationSupport(order = 6)
//	@ApiOperation(value = "新增或修改", notes = "传入diagramProduct")
//	public R submit(@Valid @RequestBody DiagramProduct diagramProduct) {
//		return R.status(diagramProductService.saveOrUpdate(diagramProduct));
//	}
//
//
//	/**
//	 * 删除
//	 */
//	@PostMapping("/remove")
//	@ApiOperationSupport(order = 7)
//	@ApiOperation(value = "逻辑删除", notes = "传入ids")
//	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
//		return R.status(diagramProductService.deleteLogic(Func.toLongList(ids)));
//	}


}
