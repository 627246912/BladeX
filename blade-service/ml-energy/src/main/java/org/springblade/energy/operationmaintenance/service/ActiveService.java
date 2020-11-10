package org.springblade.energy.operationmaintenance.service;

import org.springblade.core.mp.base.BaseService;
import org.springblade.core.tool.api.R;
import org.springblade.energy.operationmaintenance.entity.Active;

import java.util.Date;

public interface ActiveService extends BaseService<Active> {
	R updateActive(Integer isActive, Date time, Integer noticeType);

	void push();
}
