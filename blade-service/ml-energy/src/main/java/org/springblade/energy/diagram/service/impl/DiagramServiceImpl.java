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
import org.springblade.energy.diagram.entity.Diagram;
import org.springblade.energy.diagram.mapper.DiagramMapper;
import org.springblade.energy.diagram.service.IDiagramService;
import org.springblade.energy.diagram.vo.DiagramVO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统图基本信息 服务实现类
 *
 * @author bond
 * @since 2020-03-26
 */
@Service
public class DiagramServiceImpl extends BaseServiceImpl<DiagramMapper, Diagram> implements IDiagramService {

	@Override
	public IPage<DiagramVO> selectDiagramPage(IPage<DiagramVO> page, DiagramVO diagram) {
		return page.setRecords(baseMapper.selectDiagramPage(page, diagram));
	}


	/**
	 * 根据ID查询系统图
	 */
	public Map<String,Object> getDiagramDataById(String id){
		String diagramData=baseMapper.getDiagramDataById(id);
		Map<String,Object> map =new HashMap<String,Object> ();
		map.put("id",id);
		map.put("diagramData",diagramData);
		return map;
	}

	public boolean delDiagramById(List<Long> ids){
		return baseMapper.delDiagramById(ids);
	}


	public List<Diagram> selectDiagramByMap(Map<String,Object> map){
		return baseMapper.selectDiagramByMap(map);
	}
}
