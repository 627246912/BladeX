package org.springblade.energy.operationmaintenance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.operationmaintenance.entity.Active;
import org.springblade.energy.operationmaintenance.mapper.ActiveMappper;
import org.springblade.energy.operationmaintenance.service.ActiveService;
import org.springblade.system.user.entity.User;
import org.springblade.system.user.feign.IUserClient;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class ActiveServiceImpl extends BaseServiceImpl<ActiveMappper, Active> implements ActiveService {

	IUserClient iUserClient;

	@Override
	public R updateActive(Integer isActive, Date time, Integer noticeType) {
		Active activePersonId = this.getOne(new QueryWrapper<Active>().eq("active_person_id", AuthUtil.getUserId()));
		Active active = new Active();
		active.setIsActive(isActive);
		switch (isActive) {
			case 0:
				if (!activePersonId.getIsActive().equals(1)) {
					throw new ServiceException("用户非活跃中无法强行离开");
				}
				active.setActiveEndsTime(time);
				if (!noticeType.equals(-1)) {
					throw new ServiceException("用户离开通知状态未清空");
				}
			case 1:
				if (!activePersonId.getIsActive().equals(0)) {
					throw new ServiceException("用户活跃中请先离开");
				}
				active.setActiveStartTime(time);
				break;
		}
		active.setNoticeType(noticeType);
		return R.status(this.update(active, new QueryWrapper<Active>().eq("active_person_id", AuthUtil.getUserId())));
	}

	@Override
	public void push() {
		List<User> users = iUserClient.userList();
		this.autoAddActiveUser(users);
	}

	public void autoAddActiveUser(List<User> users) {
		try {
			List<Active> actives = this.list();
			users.forEach(user -> {
				Long userId = user.getId();
				final long count = actives.stream().filter(active -> active.getActivePersonId().equals(userId)).count();
				if (count == 0) {
					Active active = new Active();
					active.setActivePersonId(userId);
					String stationId = user.getSid();
					if (Func.isNotEmpty(stationId)) {
						active.setStationId(Long.parseLong(stationId));
					}
					active.setTenantId(NewsPushServiceImpl.TENANT);
					this.save(active);
				}
			});
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
	}
}
