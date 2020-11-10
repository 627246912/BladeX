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
import org.springblade.bean.ProductType;
import org.springblade.constants.ProductConstant;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.resource.dto.ProductAndType;
import org.springblade.resource.service.IProductService;
import org.springblade.resource.vo.ProductVO;
import org.springblade.resource.wrapper.ProductWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

/**
 * 产品基础表 控制器
 *
 * @author bond
 * @since 2020-03-24
 */
@RestController
@AllArgsConstructor
@RequestMapping("/product")
@Api(value = "产品基础表", tags = "产品基础表接口")
public class ProductController extends BladeController {

	private IProductService productService;
	private BladeRedisCache redisCache;
	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入product")
	public R<ProductVO> detail(Product product) {
		Product detail = productService.getOne(Condition.getQueryWrapper(product));
		return R.data(ProductWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 产品基础表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入product")
	public R<IPage<ProductVO>> list(Product product, Query query) {
		IPage<Product> pages = productService.page(Condition.getPage(query), Condition.getQueryWrapper(product));
		return R.data(ProductWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 产品基础表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入product")
	public R<IPage<ProductVO>> page(ProductVO product, Query query) {
		IPage<ProductVO> pages = productService.selectProductPage(Condition.getPage(query), product);
		return R.data(pages);
	}

	/**
	 * 新增 产品基础表
	 */
	@ApiLog("新增 产品基础表")
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入product")
	public R save(@Valid @RequestBody Product product) {
		boolean r=productService.save(product);
		if(r){
			saveCachProduct();
		}
		return R.status(r);
	}

	/**
	 * 修改 产品基础表
	 */
	@ApiLog("修改 产品基础表")
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入product")
	public R update(@Valid @RequestBody Product product) {
	boolean r=productService.updateById(product);
		if(r){
			saveCachProduct();
		}
		return R.status(r);
	}

	/**
	 * 新增或修改 产品基础表
	 */
	@ApiLog("新增或修改 产品基础表")
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入product")
	public R submit(@Valid @RequestBody Product product) {
		boolean r=productService.saveOrUpdate(product);
		if(r){
			saveCachProduct();
		}
		return R.status(r);
	}


	/**
	 * 删除 产品基础表
	 */
	@ApiLog("删除 产品基础表")
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		boolean r=productService.deleteLogic(Func.toLongList(ids));
		if(r){
			saveCachProduct();
		}
		return R.status(r);
	}

	public void saveCachProduct() {
		redisCache.del(ProductConstant.PRODUCT_KEY);
		List<Product> products = productService.selectProduct(new Product());
		for (Product product : products) {
			Map<Object, Object> mapP = new HashMap<>();
			mapP.put(product.getId(), product);
			redisCache.hMset(ProductConstant.PRODUCT_KEY, mapP);
		}
	}

	/**
	 * 查询全部产品分类 及产品
	 */
	@PostMapping("/getAllTypeAndProduct")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "查询全部产品分类 及产品")
	public R<List<ProductAndType>> getAllTypeAndProduct() {
		Map<Long, Product> mapProduct =redisCache.hGetAll(ProductConstant.PRODUCT_KEY);
		List<Product> products=new ArrayList<>();
		for (Map.Entry<Long, Product> m : mapProduct.entrySet()) {
			//System.out.println("key:" + m.getKey() + " value:" + m.getValue());
			products.add(m.getValue());
		}
		List<ProductAndType> productAndTypes=new ArrayList<>();

		Map<Long, ProductType> map=redisCache.hGetAll(ProductConstant.PRODUCT_TYPE_KEY);

		Set set=map.keySet();
		Object[] arr=set.toArray();
		Arrays.sort(arr);
		for(Object key:arr){
			ProductType m=map.get(key);
			List<Product> productList=new ArrayList<>();
			ProductAndType productAndType= new ProductAndType();
			BeanUtils.copyProperties(m,productAndType);

			for(Product product:products){
				if(Func.equals(product.getProductType(),productAndType.getId())){
					productList.add(product);
				}
			}
			productAndType.setProductList(productList);
			productAndTypes.add(productAndType);
		};

		return R.data(productAndTypes);
	}

}
