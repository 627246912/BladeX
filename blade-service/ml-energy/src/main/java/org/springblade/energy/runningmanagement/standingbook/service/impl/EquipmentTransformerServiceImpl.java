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
package org.springblade.energy.runningmanagement.standingbook.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.energy.runningmanagement.standingbook.entity.EquipmentTransformer;
import org.springblade.energy.runningmanagement.standingbook.mapper.EquipmentTransformerMapper;
import org.springblade.energy.runningmanagement.standingbook.service.IEquipmentTransformerService;
import org.springblade.energy.runningmanagement.standingbook.vo.EquipmentTransformerVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 台账--变压器 服务实现类
 *
 * @author bond
 * @since 2020-04-03
 */
@Service
public class EquipmentTransformerServiceImpl extends BaseServiceImpl<EquipmentTransformerMapper, EquipmentTransformer> implements IEquipmentTransformerService {

	@Override
	public IPage<EquipmentTransformerVO> selectEquipmentTransformerPage(IPage<EquipmentTransformerVO> page, EquipmentTransformerVO equipmentTransformer) {
		return page.setRecords(baseMapper.selectEquipmentTransformerPage(page, equipmentTransformer));
	}

	public List<EquipmentTransformerVO> selectEquipmentTransformer(EquipmentTransformerVO equipmentTransformer) {
		return baseMapper.selectEquipmentTransformer(equipmentTransformer);
	}

}
