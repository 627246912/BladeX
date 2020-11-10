package org.springblade.energy.operationmaintenance.service;

import org.springblade.core.mp.base.BaseService;
import org.springblade.core.tool.api.R;
import org.springblade.energy.operationmaintenance.entity.Inspection;
import org.springblade.energy.operationmaintenance.entity.NewsPush;
import org.springblade.energy.operationmaintenance.entity.Notice;
import org.springblade.energy.operationmaintenance.entity.PlanCount;

/**
 * 巡检服务接口
 *
 * @author bini
 * @since 2020-07-08
 */
public interface InspectionService extends BaseService<Inspection> {

	R addSync(Inspection inspection, NewsPush newsPush, PlanCount planCount);

	void removeSync(Long id);

}
