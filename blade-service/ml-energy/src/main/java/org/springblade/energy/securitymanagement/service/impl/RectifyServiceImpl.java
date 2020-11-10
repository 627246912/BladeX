package org.springblade.energy.securitymanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.tool.api.R;
import org.springblade.energy.runningmanagement.station.service.ISiteService;
import org.springblade.energy.runningmanagement.station.service.IStationService;
import org.springblade.energy.securitymanagement.entity.Rectify;
import org.springblade.energy.securitymanagement.entity.SafetyTask;
import org.springblade.energy.securitymanagement.enums.Status;
import org.springblade.energy.securitymanagement.mapper.RectifyMapper;
import org.springblade.energy.securitymanagement.service.RectifyService;
import org.springblade.energy.securitymanagement.service.SafetyTaskService;
import org.springblade.energy.securitymanagement.util.DataFactory;
import org.springblade.system.user.feign.IUserClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class RectifyServiceImpl extends BaseServiceImpl<RectifyMapper, Rectify> implements RectifyService {

	ISiteService siteService;

	IStationService stationService;

	IUserClient iUserClient;

	SafetyTaskService safetyInspectionRecordService;

	@Override
	public R<Rectify> addRectify(Rectify rectify) {
		new DataFactory<Rectify>().nameFactory(rectify);
		Long primaryKey = DataFactory.processPrimaryKey();
		rectify.setTaskId(primaryKey);
		SafetyTask safetyInspectionRecord = new SafetyTask();
		safetyInspectionRecord.setTaskId(primaryKey);
		safetyInspectionRecord.setTaskType(2);
		safetyInspectionRecord.setStationId(rectify.getStationId());
		safetyInspectionRecord.setSiteId(rectify.getSiteId().toString());
		safetyInspectionRecord.setProblemLocation(rectify.getProblemLocation());
		safetyInspectionRecord.setProblemDescription(rectify.getProblemDescription());
		safetyInspectionRecord.setRecommend(rectify.getRecommend());
		safetyInspectionRecord.setRectifyTime(rectify.getRectifyTime());
		safetyInspectionRecord.setStationName(rectify.getStationName());
		safetyInspectionRecord.setSiteName(rectify.getSiteName());
		safetyInspectionRecord.setPushStatus(0);
		safetyInspectionRecord.setResponsiblePersonId(rectify.getResponsiblePersonId());
		safetyInspectionRecord.setResponsiblePersonName(rectify.getResponsiblePersonName());
		if (!safetyInspectionRecordService.save(safetyInspectionRecord)) {
			throw new Error(Status.ADD_ERROR.getVal());
		}
		return this.save(rectify) ? R.success(Status.ADD_SUCCESS.getVal()) : R.fail(Status.ADD_ERROR.getVal());
	}

	@Override
	public R<Boolean> removeRectify(List<Rectify> rectify) {
		if (rectify.size() <= 0)
			return R.fail(Status.DELETE_ERROR.getVal());

		rectify.stream().distinct().forEach((rec) -> {
			Long taskId = rec.getTaskId();

			if (!this.remove(new QueryWrapper<Rectify>().eq("task_id", taskId))) {
				throw new Error(Status.DELETE_ERROR.getVal());
			}
		/*	if (!safetyInspectionRecordService.remove(new QueryWrapper<SafetyInspectionRecord>().eq("task_id", taskId))) {
				throw new Error(Status.DELETE_ERROR.getVal());
			}*/
		});
		return R.success(Status.DELETE_SUCCESS.getVal());
	}
}
