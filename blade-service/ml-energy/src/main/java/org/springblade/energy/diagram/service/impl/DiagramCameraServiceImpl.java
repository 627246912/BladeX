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
import org.springblade.energy.diagram.entity.DiagramCamera;
import org.springblade.energy.diagram.mapper.DiagramCameraMapper;
import org.springblade.energy.diagram.service.IDiagramCameraService;
import org.springblade.energy.diagram.vo.DiagramCameraVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *  服务实现类
 *
 * @author bond
 * @since 2020-05-18
 */
@Service
public class DiagramCameraServiceImpl extends BaseServiceImpl<DiagramCameraMapper, DiagramCamera> implements IDiagramCameraService {

	@Override
	public IPage<DiagramCameraVO> selectDiagramCameraPage(IPage<DiagramCameraVO> page, DiagramCameraVO diagramCamera) {
		return page.setRecords(baseMapper.selectDiagramCameraPage(page, diagramCamera));
	}

	public boolean delDiagramCameraByDiagramId (List<Long> diagramIds){
		return baseMapper.delDiagramCameraByDiagramId(diagramIds);
	}

}
