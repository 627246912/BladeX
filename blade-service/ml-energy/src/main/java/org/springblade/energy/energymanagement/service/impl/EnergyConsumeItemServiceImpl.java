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
package org.springblade.energy.energymanagement.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.energy.energymanagement.dto.ConsumeItemResq;
import org.springblade.energy.energymanagement.entity.EnergyConsumeItem;
import org.springblade.energy.energymanagement.mapper.EnergyConsumeItemMapper;
import org.springblade.energy.energymanagement.service.IEnergyConsumeItemService;
import org.springblade.energy.energymanagement.vo.EnergyConsumeItemVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 *  服务实现类
 *
 * @author bond
 * @since 2020-07-04
 */
@Service
public class EnergyConsumeItemServiceImpl extends BaseServiceImpl<EnergyConsumeItemMapper, EnergyConsumeItem> implements IEnergyConsumeItemService {

	@Override
	public IPage<EnergyConsumeItemVO> selectEnergyConsumeItemPage(IPage<EnergyConsumeItemVO> page, EnergyConsumeItemVO energyConsumeItem) {
		return page.setRecords(baseMapper.selectEnergyConsumeItemPage(page, energyConsumeItem));
	}

	public Boolean submitEnergyConsumeItem(EnergyConsumeItem energyConsumeItem){
		return baseMapper.submitEnergyConsumeItem(energyConsumeItem);
	}

	public List<ConsumeItemResq> selectEnergyConsumeItemByMap(Map<String,Object> map){
		return baseMapper.selectEnergyConsumeItemByMap(map);
	}

	public List<ConsumeItemResq> selectNolinkedItemByMap(Map<String,Object> map){
		return baseMapper.selectNolinkedItemByMap(map);
	}
	public Boolean dellEnergyConsumeItemByIds(List<Long> ids){
		return baseMapper.dellEnergyConsumeItemByIds(ids);
	}
}
