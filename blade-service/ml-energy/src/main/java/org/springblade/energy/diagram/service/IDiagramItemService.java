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
import org.springblade.energy.diagram.dto.DiagramItemDTO;
import org.springblade.energy.diagram.entity.DiagramItem;
import org.springblade.energy.diagram.vo.DiagramItemVO;

import java.util.List;
import java.util.Map;

/**
 * 系统图网关数据项 服务类
 *
 * @author bond
 * @since 2020-04-15
 */
public interface IDiagramItemService extends BaseService<DiagramItem> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param diagramItem
	 * @return
	 */
	IPage<DiagramItemVO> selectDiagramItemPage(IPage<DiagramItemVO> page, DiagramItemVO diagramItem);

	/**
	 * 删除系统图数据项
	 */
	public boolean delDiagramItem(List<Long> diagramIds);
	public boolean delDiagramItemByIds(List<Long> ids);
	public boolean delDiagramItemByProductIds(List<Long> diagramProductIds);

	List<DiagramItemVO> selectDiagramItem(DiagramItemVO diagramItem);


	List<DiagramItem> selectDiagramItemByMap(Map<String,Object> map);

	/**
	 * 根据数据项ID 查询站点位置
	 */
	DiagramItem getOneDiagramItemByItem(String item);

	/**
	 * 计量管账配置
	 */
	List<DiagramItemDTO> getItem(Map<String,Object> map);

	/**
	 * 查询未加入电能配置表的数据项
	 */
	List<DiagramItem> getNotInPowerMeterItem(Map<String,Object> map);

	/**
	 * 查询未加入水能配置表的数据项
	 */
	public List<DiagramItem> getNotInWaterMeterItem(Map<String,Object> map);
	/**
	 * 查询未加入气能配置表的数据项
	 */
	public List<DiagramItem> getNotInGasMeterItem(Map<String,Object> map);

	public List<DiagramItem> queryItemByDiagramPropductIds(List<Long> diagramProductIds);
}
