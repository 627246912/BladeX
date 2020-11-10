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
package org.springblade.resource.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.bean.ProductType;
import org.springblade.constants.ProductConstant;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.resource.service.IProductTypeService;
import org.springblade.resource.vo.ProductTypeVO;
import org.springblade.resource.wrapper.ProductTypeWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 产品分类 控制器
 *
 * @author bond
 * @since 2020-03-24
 */
@RestController
@AllArgsConstructor
@RequestMapping("/producttype")
@Api(value = "产品分类", tags = "产品分类接口")
public class ProductTypeController extends BladeController {

	private IProductTypeService productTypeService;
	@Autowired
	private BladeRedisCache redisCache;
	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入productType")
	public R<ProductTypeVO> detail(ProductType productType) {
		ProductType detail = productTypeService.getOne(Condition.getQueryWrapper(productType));
		return R.data(ProductTypeWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 产品分类
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入productType")
	public R<IPage<ProductTypeVO>> list(ProductType productType, Query query) {
		IPage<ProductType> pages = productTypeService.page(Condition.getPage(query), Condition.getQueryWrapper(productType));
		return R.data(ProductTypeWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 产品分类
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入productType")
	public R<IPage<ProductTypeVO>> page(ProductTypeVO productType, Query query) {
		IPage<ProductTypeVO> pages = productTypeService.selectProductTypePage(Condition.getPage(query), productType);
		return R.data(pages);
	}

	/**
	 * 新增 产品分类
	 */
	@ApiLog("新增 产品分类")
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入productType")
	public R save(@Valid @RequestBody ProductType productType) {
		boolean r=productTypeService.save(productType);
		if(r){
			saveCachPeoductType();
		}
		return R.status(r);
	}

	/**
	 * 修改 产品分类
	 */
	@ApiLog("修改 产品分类")
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入productType")
	public R update(@Valid @RequestBody ProductType productType) {
		boolean r=productTypeService.updateById(productType);
		if(r){
			saveCachPeoductType();
		}
		return R.status(r);
	}

	/**
	 * 新增或修改 产品分类
	 */
	@ApiLog("新增或修改 产品分类")
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入productType")
	public R submit(@Valid @RequestBody ProductType productType) {
		boolean r= productTypeService.saveOrUpdate(productType);
		if(r){
			saveCachPeoductType();
		}
		return R.status(r);
	}


	/**
	 * 删除 产品分类
	 */
	@ApiLog("删除 产品分类")
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		boolean r =productTypeService.deleteLogic(Func.toLongList(ids));
		if(r){
			saveCachPeoductType();
		}
		return R.status(r);
	}

	@Async
	public  void saveCachPeoductType(){
		redisCache.del(ProductConstant.PRODUCT_TYPE_KEY);
		List<ProductType> productTypes=productTypeService.list();
		for (ProductType productType : productTypes) {
			Map<Object, Object> mapP = new HashMap<>();
			mapP.put(productType.getId(), productType);
			redisCache.hMset(ProductConstant.PRODUCT_TYPE_KEY, mapP);
		}
	}

}
