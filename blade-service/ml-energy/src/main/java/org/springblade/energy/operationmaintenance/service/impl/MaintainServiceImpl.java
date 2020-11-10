package org.springblade.energy.operationmaintenance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.tool.api.R;
import org.springblade.energy.operationmaintenance.entity.Maintain;
import org.springblade.energy.operationmaintenance.entity.NewsPush;
import org.springblade.energy.operationmaintenance.entity.PlanCount;
import org.springblade.energy.operationmaintenance.mapper.MaintainMapper;
import org.springblade.energy.operationmaintenance.service.MaintainService;
import org.springblade.energy.operationmaintenance.service.NewsPushService;
import org.springblade.energy.operationmaintenance.service.PlanCountService;
import org.springframework.stereotype.Service;

/**
 * 保养实现类
 *
 * @author CYL
 * @since 2020-07-08
 */
@Service
@AllArgsConstructor
public class MaintainServiceImpl extends BaseServiceImpl<MaintainMapper, Maintain> implements MaintainService {

	NewsPushService newsPushService;

	PlanCountService planCountService;

	@Override
	public R<Boolean> addSync(Maintain maintain, NewsPush newsPush, PlanCount planCount) {
		if (this.count(new QueryWrapper<Maintain>().eq("id", maintain.getId())) > 0) {
			return R.status(newsPushService.save(newsPush) && planCountService.save(planCount) && this.update(maintain, new QueryWrapper<Maintain>().eq("id", maintain.getId())));
		}
		return R.status(newsPushService.save(newsPush) && planCountService.save(planCount) && this.save(maintain));
	}

	@Override
	public void removeSync(Long id) {
		planCountService.remove(new QueryWrapper<PlanCount>().eq("plan_id", id));
		newsPushService.remove(new QueryWrapper<NewsPush>().eq("task_id", id));
		this.remove(new QueryWrapper<Maintain>().eq("id", id));
	}
}
