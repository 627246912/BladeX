package org.springblade.energy.qualitymanagement.service;

import org.springblade.core.mp.base.BaseService;
import org.springblade.energy.qualitymanagement.entity.EnergyMaintenance;
import org.springblade.energy.qualitymanagement.vo.DevNameVo;

public interface EnergyMaintenanceService extends BaseService<EnergyMaintenance> {

	DevNameVo selectDevName(Long id);
}
