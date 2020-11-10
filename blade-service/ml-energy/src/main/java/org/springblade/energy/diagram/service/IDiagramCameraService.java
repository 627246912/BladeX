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
import org.springblade.energy.diagram.entity.DiagramCamera;
import org.springblade.energy.diagram.vo.DiagramCameraVO;

import java.util.List;

/**
 *  服务类
 *
 * @author bond
 * @since 2020-05-18
 */
public interface IDiagramCameraService extends BaseService<DiagramCamera> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param diagramCamera
	 * @return
	 */
	IPage<DiagramCameraVO> selectDiagramCameraPage(IPage<DiagramCameraVO> page, DiagramCameraVO diagramCamera);

	boolean delDiagramCameraByDiagramId (List<Long> diagramIds);

}
