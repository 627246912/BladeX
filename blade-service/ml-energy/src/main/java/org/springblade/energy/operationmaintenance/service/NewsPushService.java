package org.springblade.energy.operationmaintenance.service;

import org.springblade.core.mp.base.BaseService;
import org.springblade.energy.operationmaintenance.entity.NewsPush;


public interface NewsPushService extends BaseService<NewsPush> {

	void push();
}
