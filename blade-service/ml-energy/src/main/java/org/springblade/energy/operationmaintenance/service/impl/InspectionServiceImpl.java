package org.springblade.energy.operationmaintenance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.tool.api.R;
import org.springblade.energy.operationmaintenance.entity.Inspection;
import org.springblade.energy.operationmaintenance.entity.NewsPush;
import org.springblade.energy.operationmaintenance.entity.PlanCount;
import org.springblade.energy.operationmaintenance.mapper.InspectionMapper;
import org.springblade.energy.operationmaintenance.service.InspectionService;
import org.springblade.energy.operationmaintenance.service.NewsPushService;
import org.springblade.energy.operationmaintenance.service.PlanCountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 服务实现类
 *
 * @author CYL
 * @since 2020-07-08
 */
@Service
@AllArgsConstructor
public class InspectionServiceImpl extends BaseServiceImpl<InspectionMapper, Inspection> implements InspectionService {

	NewsPushService newsPushService;

	PlanCountService planCountService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R addSync(Inspection inspection, NewsPush newsPush, PlanCount planCount) {
		if (this.count(new QueryWrapper<Inspection>().eq("id", inspection.getId())) > 0) {
			return R.status(newsPushService.save(newsPush) && planCountService.save(planCount) && this.update(inspection, new QueryWrapper<Inspection>().eq("id", inspection.getId())));
		}
		return R.status(newsPushService.save(newsPush) && planCountService.save(planCount) && this.save(inspection));
	}

	@Override
	public void removeSync(Long id) {
		planCountService.remove(new QueryWrapper<PlanCount>().eq("plan_id", id));
		newsPushService.remove(new QueryWrapper<NewsPush>().eq("task_id", id));
		this.remove(new QueryWrapper<Inspection>().eq("id", id));
	}
}
