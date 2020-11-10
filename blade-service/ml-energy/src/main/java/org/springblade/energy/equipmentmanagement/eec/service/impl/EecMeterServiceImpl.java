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
package org.springblade.energy.equipmentmanagement.eec.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.energy.diagram.service.IDiagramProductService;
import org.springblade.energy.equipmentmanagement.eec.entity.EecMeter;
import org.springblade.energy.equipmentmanagement.eec.mapper.EecMeterMapper;
import org.springblade.energy.equipmentmanagement.eec.service.IEecMeterService;
import org.springblade.energy.equipmentmanagement.eec.vo.EVO;
import org.springblade.energy.equipmentmanagement.eec.vo.EecMeterVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 重点能耗设备-》仪表配置 服务实现类
 *
 * @author bond
 * @since 2020-05-06
 */
@Service
public class EecMeterServiceImpl extends BaseServiceImpl<EecMeterMapper, EecMeter> implements IEecMeterService {

	@Autowired
	private IDiagramProductService iDiagramProductService;

	@Override
	public IPage<EecMeterVO> selectEecMeterPage(IPage<EecMeterVO> page, EecMeterVO eecMeter) {
		List<EecMeterVO> list = baseMapper.selectEecMeterPage(page, eecMeter);
		return page.setRecords(list);
	}

	@Override
	public List<EVO> selectEVO(Long sid) {
		return this.baseMapper.selectEVO(sid);
	}
}
