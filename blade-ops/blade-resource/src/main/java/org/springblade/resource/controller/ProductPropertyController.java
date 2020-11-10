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
import org.springblade.bean.Product;
import org.springblade.bean.ProductProperty;
import org.springblade.bean.Ptype;
import org.springblade.constants.ProductConstant;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.resource.service.IProductPropertyService;
import org.springblade.resource.service.IProductService;
import org.springblade.resource.vo.ProductPropertyVO;
import org.springblade.resource.wrapper.ProductPropertyWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 产品属性表 控制器
 *
 * @author bond
 * @since 2020-03-24
 */
@RestController
@AllArgsConstructor
@RequestMapping("/productproperty")
@Api(value = "产品属性表", tags = "产品属性表接口")
public class ProductPropertyController extends BladeController {

	private IProductService iProductService;
	IProductPropertyService productPropertyService;
	@Autowired
	private BladeRedisCache redisCache;
	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入productProperty")
	public R<ProductPropertyVO> detail(ProductProperty productProperty) {
		ProductProperty detail = productPropertyService.getOne(Condition.getQueryWrapper(productProperty));
		return R.data(ProductPropertyWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 产品属性表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入productProperty")
	public R<IPage<ProductPropertyVO>> list(ProductProperty productProperty, Query query) {
		IPage<ProductProperty> pages = productPropertyService.page(Condition.getPage(query), Condition.getQueryWrapper(productProperty));
		return R.data(ProductPropertyWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 产品属性表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入productProperty")
	public R<IPage<ProductPropertyVO>> page(ProductPropertyVO productProperty, Query query) {
		IPage<ProductPropertyVO> pages = productPropertyService.selectProductPropertyPage(Condition.getPage(query), productProperty);
		return R.data(pages);
	}

	/**
	 * 新增 产品属性表
	 */
	@ApiLog("新增 产品属性表")
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入productProperty")
	public R save(@Valid @RequestBody ProductProperty productProperty) {
		ProductProperty property =new ProductProperty();
		property.setProductId(productProperty.getProductId());
		property.setPropertyCode(productProperty.getPropertyCode());
		property.setIsDeleted(0);
		ProductProperty have=productPropertyService.getOne(Condition.getQueryWrapper(property));
	if(Func.isNotEmpty(have)){
	return R.fail("改产品已经有该属性了，不能重复添加哦");
	}
		boolean r= productPropertyService.save(productProperty);
		if(r){
			saveCachProperty(productProperty.getProductId());
		}

		return R.status(r);
	}

	/**
	 * 修改 产品属性表
	 */
	@ApiLog("修改 产品属性表")
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入productProperty")
	public R update(@Valid @RequestBody ProductProperty productProperty) {
		ProductProperty property =new ProductProperty();
		property.setProductId(productProperty.getProductId());
		property.setPropertyCode(productProperty.getPropertyCode());
		property.setIsDeleted(0);
		ProductProperty have=productPropertyService.getOne(Condition.getQueryWrapper(property));

		if(Func.isNotEmpty(have) && !Func.equals(have.getId(),productProperty.getId())){
			return R.fail("改产品已经有该属性了，不能重复添加哦");
		}
		boolean r=productPropertyService.updateById(productProperty);
		if(r){
			saveCachProperty(productProperty.getProductId());
		}

		return R.status(r);
	}

	/**
	 * 新增或修改 产品属性表
	 */
	@ApiLog("新增或修改 产品属性表")
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入productProperty")
	public R submit(@Valid @RequestBody ProductProperty productProperty) {
		ProductProperty property =new ProductProperty();
		property.setProductId(productProperty.getProductId());
		property.setPropertyCode(productProperty.getPropertyCode());
		property.setIsDeleted(0);
		ProductProperty have=productPropertyService.getOne(Condition.getQueryWrapper(property));
		if(Func.isNotEmpty(have) && !Func.equals(have.getId(),productProperty.getId())){
			return R.fail("改产品已经有该属性了，不能重复添加哦");
		}
		boolean r=productPropertyService.saveOrUpdate(productProperty);
		if(r){
			saveCachProperty(productProperty.getProductId());
		}
		return R.status(r);
	}
	@Async
	public  void saveCachProperty(Long productId){
		List<Long> productids= new ArrayList<Long>();
		productids.add(productId);
		List<ProductProperty> productPropertys=productPropertyService.selectProductPropertyByPids(productids);
		Map<Object,Object>  map=new HashMap<>();
		map.put(productId,productPropertys);
		redisCache.hMset(ProductConstant.PRODUCT_PROPERTY_KEY,map);

		redisCache.del(ProductConstant.PROPERTY_KEY);
		List<ProductProperty> AllproductPropertys=productPropertyService.list();
		for(ProductProperty productProperty :AllproductPropertys){
			Map<Object, Object> map1 = new HashMap<>();
			map1.put(productProperty.getId(), productProperty);
			redisCache.hMset(ProductConstant.PROPERTY_KEY, map1);
		}
	}


	/**
	 * 删除 产品属性表
	 */
	@ApiLog("删除 产品属性表")
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		boolean r =productPropertyService.deleteLogic(Func.toLongList(ids));
		if (r){
			List<Product> products = iProductService.selectProduct(new Product());

			for(Product product : products){
				List<Long> productids= new ArrayList<Long>();
				productids.add(product.getId());
				List<ProductProperty> productPropertys=productPropertyService.selectProductPropertyByPids(productids);
				if(!StringUtil.isEmpty(productPropertys)) {
					Map<Object, Object> map = new HashMap<>();
					map.put(product.getId(), productPropertys);
					redisCache.hMset(ProductConstant.PRODUCT_PROPERTY_KEY, map);
				}
			}
		}
		return R.status(r);
	}


	@GetMapping("/selectProductPropertyPType")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "查询产品属性code", notes = "传入1遥测2遥信3遥调4遥控5计算遥测6计算遥信")
	public R<List<Ptype>> selectProductPropertyPType(@RequestParam String stype) {
		List<Ptype> list= redisCache.get(ProductConstant.PTYPE_ALL_LIST);
		List<Ptype> list1=new ArrayList<>();
		if(Func.isEmpty(stype)){
			return R.data(list);
		}else {
			for (Ptype p : list) {
				if(Func.equals(stype,p.getStype().toString())){
					list1.add(p);
				}
			}
		}
		return R.data(list1);
	}

}
