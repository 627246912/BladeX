package org.springblade.energy.qualitymanagement.service.Impl;

import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.energy.qualitymanagement.entity.EnergyMaintenance;
import org.springblade.energy.qualitymanagement.mapper.EnergyMaintenanceMapper;
import org.springblade.energy.qualitymanagement.service.EnergyMaintenanceService;
import org.springblade.energy.qualitymanagement.vo.DevNameVo;
import org.springframework.stereotype.Service;

@Service
public class EnergyMaintenanceServiceImpl extends BaseServiceImpl<EnergyMaintenanceMapper, EnergyMaintenance> implements EnergyMaintenanceService {
	@Override
	public DevNameVo selectDevName(Long id) {
		return this.baseMapper.selectDevName(id);
	}
}
