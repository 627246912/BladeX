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
package org.springblade.resource.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.bean.ProductProperty;
import org.springblade.bean.Ptype;
import org.springblade.resource.vo.ProductPropertyVO;

import java.util.List;
import java.util.Map;

/**
 * 产品属性表 Mapper 接口
 *
 * @author bond
 * @since 2020-03-24
 */
public interface ProductPropertyMapper extends BaseMapper<ProductProperty> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param productProperty
	 * @return
	 */
	List<ProductPropertyVO> selectProductPropertyPage(IPage page, ProductPropertyVO productProperty);

    List<ProductProperty> selectProductProperty(Map<String, Object> map);
	public List<ProductProperty> selectProductPropertyByPids(List<Long> productIds);

	List<Ptype> selectProductPropertyPType();

}
