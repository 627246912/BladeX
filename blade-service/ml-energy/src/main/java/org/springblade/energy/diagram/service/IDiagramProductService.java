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
package org.springblade.energy.diagram.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;
import org.springblade.energy.diagram.entity.DiagramProduct;
import org.springblade.energy.diagram.vo.DiagramProductVO;

import java.util.List;
import java.util.Map;

/**
 *  服务类
 *
 * @author bond
 * @since 2020-03-31
 */
public interface IDiagramProductService extends BaseService<DiagramProduct> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param diagramProduct
	 * @return
	 */
	IPage<DiagramProductVO> selectDiagramProductPage(IPage<DiagramProductVO> page, DiagramProductVO diagramProduct);

	/**
	 * 删除系统图产品
	 */
	public boolean delDiagramProduct(List<Long> diagramIds);
	/**
	 * 删除系统图产品
	 */
	public boolean delDiagramProductById(List<Long> ids);

	/**
	 * 查询低压总进线开关
	 */
	Map<String,String> queryDiagramProductDtype2(Map<String,String> map);
	/**
	 * 查询低压馈线线开关
	 */
	Map<String,String> queryDiagramProductDtype3(Map<String,String> map);

	/**
	 * 查询系统产品
	 */
	List<DiagramProduct> queryDiagramProductByMap(Map<String,Object> map);

	/**
	 * 根据pindex查询子产品
	 */
	List<DiagramProduct> querySonDiagramProduct(Map<String,Object> map);

	DiagramProduct getOneDiagramProductByRtuidcb(String rtuidcb);
	DiagramProduct getOneDiagramProductByDid(String did);
}
