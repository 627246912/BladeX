package org.springblade.energy.operationmaintenance.service.impl;

import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.energy.operationmaintenance.entity.PlanCount;
import org.springblade.energy.operationmaintenance.mapper.PlanCountMapper;
import org.springblade.energy.operationmaintenance.service.PlanCountService;
import org.springframework.stereotype.Service;

@Service
public class PlanCountServiceImpl extends BaseServiceImpl<PlanCountMapper, PlanCount> implements PlanCountService {
}
