package org.springblade.energy.qualitymanagement.service.Impl;

import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.energy.qualitymanagement.entity.ElectricityMaintenance;
import org.springblade.energy.qualitymanagement.mapper.ElectricityMaintenanceMapper;
import org.springblade.energy.qualitymanagement.service.ElectricityMaintenanceService;
import org.springblade.energy.qualitymanagement.vo.DevNameVo;
import org.springframework.stereotype.Service;

@Service
public class ElectricityMaintenanceServiceImpl extends BaseServiceImpl<ElectricityMaintenanceMapper, ElectricityMaintenance> implements ElectricityMaintenanceService {

	@Override
	public DevNameVo selectDevName(Long id) {
		return this.baseMapper.selectDevName(id);
	}
}
