package org.springblade.energy.qualitymanagement.service.Impl;

import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.energy.qualitymanagement.entity.ElectricityInspection;
import org.springblade.energy.qualitymanagement.mapper.ElectricityInspectionMapper;
import org.springblade.energy.qualitymanagement.service.ElectricityInspectionService;
import org.springframework.stereotype.Service;

@Service
public class ElectricityInspectionServiceImpl extends BaseServiceImpl<ElectricityInspectionMapper, ElectricityInspection> implements ElectricityInspectionService {
}
