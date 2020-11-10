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
package org.springblade.energy.runningmanagement.repair.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.energy.runningmanagement.repair.entity.RepairWorkOrder;
import org.springblade.energy.runningmanagement.repair.mapper.RepairWorkOrderMapper;
import org.springblade.energy.runningmanagement.repair.service.IRepairWorkOrderService;
import org.springblade.energy.runningmanagement.repair.vo.RepairWorkOrderVO;
import org.springframework.stereotype.Service;

/**
 * 用户报修工单表 服务实现类
 *
 * @author bond
 * @since 2020-04-14
 */
@Service
public class RepairWorkOrderServiceImpl extends BaseServiceImpl<RepairWorkOrderMapper, RepairWorkOrder> implements IRepairWorkOrderService {

	@Override
	public IPage<RepairWorkOrderVO> selectRepairWorkOrderPage(IPage<RepairWorkOrderVO> page, RepairWorkOrderVO repairWorkOrder) {
		return page.setRecords(baseMapper.selectRepairWorkOrderPage(page, repairWorkOrder));
	}

}
