package org.springblade.energy.securitymanagement.service;

import org.springblade.core.mp.base.BaseService;
import org.springblade.core.tool.api.R;
import org.springblade.energy.securitymanagement.entity.Patrol;
import org.springblade.energy.securitymanagement.vo.SecurityCountVo;

import java.util.List;

public interface PatrolService extends BaseService<Patrol> {

	R addPatrol(Patrol patrol);

	R<Patrol> updatePatrol(Patrol patrol);

	void removePatrol(List<Patrol> patrol);

	R<SecurityCountVo> countPatrol(String stationId, String siteId, String time, Integer type);


}
