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

import org.springblade.energy.diagram.entity.DiagramShowItem;
import org.springblade.energy.diagram.vo.DiagramShowItemVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;

/**
 * 供水供气系统图展示数据项 Mapper 接口
 *
 * @author bond
 * @since 2020-06-02
 */
public interface DiagramShowItemMapper extends BaseMapper<DiagramShowItem> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param waterGasShowItem
	 * @return
	 */
	List<DiagramShowItemVO> selectWaterGasShowItemPage(IPage page, DiagramShowItemVO waterGasShowItem);



	/**
	 * 删除供水供气系统图展示数据项
	 */
	public boolean delDiagramShowItem(List<Long> diagramIds);

}
