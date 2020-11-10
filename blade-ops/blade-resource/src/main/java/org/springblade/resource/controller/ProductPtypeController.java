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
import org.springblade.bean.Ptype;
import org.springblade.constants.ProductConstant;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.enums.ItemStype;
import org.springblade.resource.dto.ProductPtypeDTO;
import org.springblade.resource.entity.ProductPtype;
import org.springblade.resource.service.IProductPropertyService;
import org.springblade.resource.service.IProductPtypeService;
import org.springblade.resource.vo.ProductPtypeVO;
import org.springblade.resource.wrapper.ProductPtypeWrapper;
import org.springblade.util.DateUtil;
import org.springblade.util.excel.util.ExcelUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 产品模型编码 控制器
 *
 * @author bond
 * @since 2020-07-14
 */
@RestController
@AllArgsConstructor
@RequestMapping("/productptype")
@Api(value = "产品模型编码", tags = "产品模型编码接口")
public class ProductPtypeController extends BladeController {

	private IProductPtypeService productPtypeService;
	private BladeRedisCache redisCache;
	private IProductPropertyService iProductPropertyService;
	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入productPtype")
	public R<ProductPtypeVO> detail(ProductPtype productPtype) {
		ProductPtype detail = productPtypeService.getOne(Condition.getQueryWrapper(productPtype));
		return R.data(ProductPtypeWrapper.build().entityVO(detail));
	}


	/**
	 * 自定义分页 产品模型编码
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入productPtype")
	public R<IPage<ProductPtypeVO>> page(ProductPtypeVO productPtype, Query query) {
		IPage<ProductPtypeVO> pages = productPtypeService.selectProductPtypePage(Condition.getPage(query), productPtype);
		return R.data(pages);
	}

	/**
	 * 新增 产品模型编码
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增修改", notes = "传入productPtype")
	public R submit(@Valid @RequestBody ProductPtype productPtype) {
		ProductPtype query=new ProductPtype();
		query.setPtype(productPtype.getPtype());
		ProductPtype have=productPtypeService.getOne(Condition.getQueryWrapper(query));
		boolean r =false;
		if(Func.isEmpty(have)){
			r=productPtypeService.save(productPtype);
		}else{
			r=productPtypeService.updateById(productPtype);
		}
		updateRedisProductPtype();
		return R.status(r);
	}

//	/**
//	 * 修改 产品模型编码
//	 */
//	@PostMapping("/update")
//	@ApiOperationSupport(order = 5)
//	@ApiOperation(value = "修改", notes = "传入productPtype")
//	public R update(@Valid @RequestBody ProductPtype productPtype) {
//		return R.status(productPtypeService.updateById(productPtype));
//	}
//
//	/**
//	 * 新增或修改 产品模型编码
//	 */
//	@PostMapping("/submit")
//	@ApiOperationSupport(order = 6)
//	@ApiOperation(value = "新增或修改", notes = "传入productPtype")
//	public R submit(@Valid @RequestBody ProductPtype productPtype) {
//		return R.status(productPtypeService.saveOrUpdate(productPtype));
//	}


	/**
	 * 删除 产品模型编码
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		 boolean r= productPtypeService.deleteLogic(Func.toLongList(ids));
		 if(r){
			 updateRedisProductPtype();
		 }
		return R.status(r);
	}

	public void updateRedisProductPtype(){
		List<Ptype> ptypeList=iProductPropertyService.selectProductPropertyPType();
		redisCache.set(ProductConstant.PTYPE_ALL_LIST,ptypeList);
	}

	/**
	 * 导出产品模型编码
	 */
	@GetMapping("/codeExport")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "导出产品模型编码", notes = "传入productPtype")
	public void codeExport(HttpServletResponse response) {
		List<ProductPtype> ptypeList=productPtypeService.list();

		List<List<ProductPtypeDTO>> lsits=new ArrayList<>();
		List<ProductPtypeDTO> lsit1=new ArrayList<>();
		List<ProductPtypeDTO> lsit2=new ArrayList<>();
		List<ProductPtypeDTO> lsit3=new ArrayList<>();
		List<ProductPtypeDTO> lsit4=new ArrayList<>();

		List<String> sheetNames=new ArrayList<>();
		for(ProductPtype productPtype: ptypeList){
			ProductPtypeDTO dto=new ProductPtypeDTO();
			BeanUtils.copyProperties(productPtype,dto);
			if(Func.equals(productPtype.getStype(),ItemStype.TRANSPORTYC.id)){
				lsit1.add(dto);
			}
			if(Func.equals(productPtype.getStype(),ItemStype.TRANSPORTYX.id)){
				lsit2.add(dto);
			}
			if(Func.equals(productPtype.getStype(),ItemStype.TRANSPORTYT.id)){
				lsit3.add(dto);
			}
			if(Func.equals(productPtype.getStype(),ItemStype.TRANSPORTYK.id)){
				lsit4.add(dto);
			}
		}
		lsits.add(lsit1);
		sheetNames.add(ItemStype.TRANSPORTYC.value);
		lsits.add(lsit2);
		sheetNames.add(ItemStype.TRANSPORTYX.value);
		lsits.add(lsit3);
		sheetNames.add(ItemStype.TRANSPORTYT.value);
		lsits.add(lsit4);
		sheetNames.add(ItemStype.TRANSPORTYK.value);

		//ExcelUtils.export(response,"产品模型编码","1",ptypeList,ProductPtype.class);//
		String time=DateUtil.format(new Date(),DateUtil.TIME_NUMBER_PATTERN);
		ExcelUtils.export(response,"产品模型统一编码"+time,sheetNames,lsits,ProductPtypeDTO.class);
	}

}
