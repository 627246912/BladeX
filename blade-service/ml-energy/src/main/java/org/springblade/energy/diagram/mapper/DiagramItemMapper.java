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
package org.springblade.energy.diagram.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.energy.diagram.dto.DiagramItemDTO;
import org.springblade.energy.diagram.entity.DiagramItem;
import org.springblade.energy.diagram.vo.DiagramItemVO;

import java.util.List;
import java.util.Map;

/**
 * 系统图网关数据项 Mapper 接口
 *
 * @author bond
 * @since 2020-04-15
 */
public interface DiagramItemMapper extends BaseMapper<DiagramItem> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param diagramItem
	 * @return
	 */
	List<DiagramItemVO> selectDiagramItemPage(IPage page, DiagramItemVO diagramItem);

    boolean delDiagramItem(List<Long>  diagramIds);
	public boolean delDiagramItemByIds(List<Long> ids);
    List<DiagramItemVO> selectDiagramItem(DiagramItemVO diagramItem);

	List<DiagramItem> selectDiagramItemByMap(Map<String,Object> map);
	/**
	 * 根据数据项ID 查询站点位置
	 */
	public DiagramItem getOneDiagramItemByItem(String item);

	/**
	 * 计量管账一级配置
	 */
	public List<DiagramItemDTO> getItem(Map<String,Object> map);


	/**
	 * 查询未加入电能配置表的数据项
	 */
	public List<DiagramItem> getNotInPowerMeterItem(Map<String,Object> map);
	/**
	 * 查询未加入水能配置表的数据项
	 */
	public List<DiagramItem> getNotInWaterMeterItem(Map<String,Object> map);
	/**
	 * 查询未加入气能配置表的数据项
	 */
	public List<DiagramItem> getNotInGasMeterItem(Map<String,Object> map);

	List<DiagramItem> queryItemByDiagramPropductIds(List<Long> diagramProductIds);

	boolean delDiagramItemByProductIds(List<Long> diagramProductIds);
}
