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
package org.springblade.resource.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.bean.ProductProperty;
import org.springblade.bean.Ptype;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.resource.mapper.ProductPropertyMapper;
import org.springblade.resource.service.IProductPropertyService;
import org.springblade.resource.vo.ProductPropertyVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 产品属性表 服务实现类
 *
 * @author bond
 * @since 2020-03-24
 */
@Service
public class ProductPropertyServiceImpl extends BaseServiceImpl<ProductPropertyMapper, ProductProperty> implements IProductPropertyService {

	@Override
	public IPage<ProductPropertyVO> selectProductPropertyPage(IPage<ProductPropertyVO> page, ProductPropertyVO productProperty) {
		return page.setRecords(baseMapper.selectProductPropertyPage(page, productProperty));
	}
	/**
	 *查询产品属性
	 */
	public List<ProductProperty> selectProductProperty(Map<String,Object> map){
		return baseMapper.selectProductProperty(map);
	}

	/**
	 *查询产品属性
	 */
	public List<ProductProperty> selectProductPropertyByPids(List<Long> productIds){
		return baseMapper.selectProductPropertyByPids(productIds);
	}


	public List<Ptype> selectProductPropertyPType(){
		return baseMapper.selectProductPropertyPType();
	}
}
