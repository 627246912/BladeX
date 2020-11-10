package org.springblade.energy.operationmaintenance.service;

import org.springblade.core.mp.base.BaseService;
import org.springblade.core.tool.api.R;
import org.springblade.energy.operationmaintenance.entity.Maintain;
import org.springblade.energy.operationmaintenance.entity.NewsPush;
import org.springblade.energy.operationmaintenance.entity.PlanCount;

/**
 * 保养服务接口
 *
 * @author CYL
 * @since 2020-07-23
 */
public interface MaintainService extends BaseService<Maintain> {

	R<Boolean> addSync(Maintain maintain, NewsPush newsPush, PlanCount planCount);

	void removeSync(Long id);
}
