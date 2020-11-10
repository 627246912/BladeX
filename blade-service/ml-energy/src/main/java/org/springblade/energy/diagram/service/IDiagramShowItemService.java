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
import org.springblade.energy.diagram.entity.DiagramShowItem;
import org.springblade.energy.diagram.vo.DiagramShowItemVO;

import java.util.List;

/**
 * 供水供气系统图展示数据项 服务类
 *
 * @author bond
 * @since 2020-06-02
 */
public interface IDiagramShowItemService extends BaseService<DiagramShowItem> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param waterGasShowItem
	 * @return
	 */
	IPage<DiagramShowItemVO> selectWaterGasShowItemPage(IPage<DiagramShowItemVO> page, DiagramShowItemVO waterGasShowItem);
	/**
	 * 删除供水供气系统图展示数据项
	 */
	public boolean delDiagramShowItem(List<Long> diagramIds);
}
