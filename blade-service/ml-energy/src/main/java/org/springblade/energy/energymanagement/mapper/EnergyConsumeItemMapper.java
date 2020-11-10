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
package org.springblade.energy.energymanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.energy.energymanagement.dto.ConsumeItemResq;
import org.springblade.energy.energymanagement.entity.EnergyConsumeItem;
import org.springblade.energy.energymanagement.vo.EnergyConsumeItemVO;

import java.util.List;
import java.util.Map;

/**
 *  Mapper 接口
 *
 * @author bond
 * @since 2020-07-04
 */
public interface EnergyConsumeItemMapper extends BaseMapper<EnergyConsumeItem> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param energyConsumeItem
	 * @return
	 */
	List<EnergyConsumeItemVO> selectEnergyConsumeItemPage(IPage page, EnergyConsumeItemVO energyConsumeItem);
	public Boolean submitEnergyConsumeItem(EnergyConsumeItem energyConsumeItem);
	public List<ConsumeItemResq> selectEnergyConsumeItemByMap(Map<String,Object> map);
	public List<ConsumeItemResq> selectNolinkedItemByMap(Map<String,Object> map);
	public Boolean dellEnergyConsumeItemByIds(List<Long> ids);
}
