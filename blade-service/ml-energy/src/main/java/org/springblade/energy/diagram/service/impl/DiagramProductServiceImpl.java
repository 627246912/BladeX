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
package org.springblade.energy.diagram.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.energy.diagram.entity.DiagramProduct;
import org.springblade.energy.diagram.mapper.DiagramProductMapper;
import org.springblade.energy.diagram.service.IDiagramProductService;
import org.springblade.energy.diagram.vo.DiagramProductVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 *  服务实现类
 *
 * @author bond
 * @since 2020-03-31
 */
@Service
public class DiagramProductServiceImpl extends BaseServiceImpl<DiagramProductMapper, DiagramProduct> implements IDiagramProductService {

	@Override
	public IPage<DiagramProductVO> selectDiagramProductPage(IPage<DiagramProductVO> page, DiagramProductVO diagramProduct) {
		return page.setRecords(baseMapper.selectDiagramProductPage(page, diagramProduct));
	}

	@Override
	public boolean delDiagramProduct(List<Long> diagramIds) {
		return baseMapper.delDiagramProduct(diagramIds);
	}

	public boolean delDiagramProductById(List<Long> ids){
		return baseMapper.delDiagramProductById(ids);
	}

	public Map<String,String> queryDiagramProductDtype2(Map<String,String> map){
		return baseMapper.queryDiagramProductDtype2(map);
	}
	public Map<String,String> queryDiagramProductDtype3(Map<String,String> map){
		return baseMapper.queryDiagramProductDtype3(map);
	}

	/**
	 * 查询系统图产品
	 */
	public List<DiagramProduct> queryDiagramProductByMap(Map<String,Object> map){
		return baseMapper.queryDiagramProductByMap(map);
	}

	/**
	 * 根据pindex查询子产品
	 */
	public List<DiagramProduct> querySonDiagramProduct(Map<String,Object> map){
	return baseMapper.querySonDiagramProduct(map);
	}

	public DiagramProduct getOneDiagramProductByRtuidcb(String rtuidcb){
		return baseMapper.getOneDiagramProductByRtuidcb(rtuidcb);
	}

	public DiagramProduct getOneDiagramProductByDid(String did){
		return baseMapper.getOneDiagramProductByDid(did);
	}
}
