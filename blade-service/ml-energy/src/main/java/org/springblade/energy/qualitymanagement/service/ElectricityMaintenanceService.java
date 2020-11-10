package org.springblade.energy.qualitymanagement.service;

import org.springblade.core.mp.base.BaseService;
import org.springblade.energy.qualitymanagement.entity.ElectricityMaintenance;
import org.springblade.energy.qualitymanagement.vo.DevNameVo;

public interface ElectricityMaintenanceService extends BaseService<ElectricityMaintenance> {

	DevNameVo selectDevName(Long id);
}
