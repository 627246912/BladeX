package org.springblade.energy.securitymanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.operationmaintenance.entity.Notice;
import org.springblade.energy.operationmaintenance.service.NoticeService;
import org.springblade.energy.operationmaintenance.service.RepairService;
import org.springblade.energy.securitymanagement.entity.SafetyTask;
import org.springblade.energy.securitymanagement.enums.Status;
import org.springblade.energy.securitymanagement.mapper.SafetyTaskMapper;
import org.springblade.energy.securitymanagement.service.SafetyTaskService;
import org.springblade.energy.securitymanagement.util.DataFactory;
import org.springblade.energy.securitymanagement.util.PageQuery;
import org.springblade.energy.securitymanagement.util.PageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class SafetyTaskServiceImpl extends BaseServiceImpl<SafetyTaskMapper, SafetyTask> implements SafetyTaskService {

	RepairService repairService;

	NoticeService noticeService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R updateSafetyInspectionRecord(SafetyTask safetyTask) {
		if (safetyTask.getTaskStatus().equals("3") && safetyTask.getCheckStatus() == 0) {
			Notice notice = new Notice();
			notice.setTaskId(safetyTask.getId());
			notice.setSiteId(Long.parseLong(safetyTask.getSiteId()));
			notice.setStationId(safetyTask.getStationId());
			Long responsibleId = safetyTask.getResponsiblePersonId();
			notice.setResponsibleId(responsibleId);
			notice.setLeaderId(Long.parseLong(DataFactory.administratorId(responsibleId).split(",")[0]));
			notice.setNoticeType(5);
			switch (safetyTask.getTaskType()) {
				case 1:
					notice.setTaskType(3);
					notice.setNoticeName("安全检查");
					break;
				case 2:
					notice.setTaskType(4);
					notice.setNoticeName("整改计划");
					break;
			}
			notice.setNoticeTime(DataFactory.datetime);
			if (noticeService.save(notice)) {
				throw new ServiceException("安全审核通知失败");
			}
		}
		return R.status(this.update(safetyTask, new QueryWrapper<SafetyTask>().eq("id", safetyTask.getId())));
	}

	@Override
	public R<PageUtils> listSafetyInspectionRecord(PageQuery pageQuery, SafetyTask safetyInspectionRecord) {
		Integer taskType = safetyInspectionRecord.getTaskType();
		String taskStatus = safetyInspectionRecord.getTaskStatus();
		String siteId = safetyInspectionRecord.getSiteId();
		AtomicBoolean taskBool = new AtomicBoolean(true);
		AtomicBoolean authBool = new AtomicBoolean(true);
		AtomicReference<String> responsiblePersonId = new AtomicReference<>("");
		Long userId = AuthUtil.getUserId();
		String userRole = AuthUtil.getUserRole();
		responsiblePersonId.set(userId.toString());
		if (Func.isEmpty(taskType)) {
			taskBool.set(false);
		}
		if (userRole.trim().equals("admin") || userRole.trim().equals("administrator")) {
			if (Func.isEmpty(safetyInspectionRecord.getResponsiblePersonId())) {
				authBool.set(false);
			} else {
				responsiblePersonId.set(safetyInspectionRecord.getResponsiblePersonId().toString());
			}
		}

		if (Func.isEmpty(taskStatus) && Func.isEmpty(siteId)) {
			return this.getListSafetyInspectionRecordResult(pageQuery, safetyInspectionRecord, taskBool.get(), authBool.get(), taskType, responsiblePersonId.get());
		}

		AtomicReference<List<SafetyTask>> safetyInspectionRecords = new AtomicReference<>();
		safetyInspectionRecords.set(this.list(new QueryWrapper<SafetyTask>().orderByDesc("create_time").eq(authBool.get(), "responsible_person_id", responsiblePersonId.get()).eq("station_id", safetyInspectionRecord.getStationId()).eq(taskBool.get(), "task_type", taskType)));

		List<SafetyTask> records = new ArrayList<>();
		if (Func.isNotEmpty(siteId)) {
			Stream.of(safetyInspectionRecord.getSiteId().split(",")).forEach((s) ->
				Stream.of(safetyInspectionRecord.getTaskStatus().split(",")).forEach((t) ->
					safetyInspectionRecords.get().stream().sorted(Comparator.comparing(SafetyTask::getCreateTime).reversed()).filter(record -> record.getTaskStatus().equals(t) && record.getSiteId().equals(s)).forEach(records::add)));
		}
		return R.data(new PageUtils(records, (long) records.size(), pageQuery.getSize(), pageQuery.getCurrent(), true));
	}

	public R<PageUtils> getListSafetyInspectionRecordResult(PageQuery pageQuery, SafetyTask safetyInspectionRecord, Boolean taskBool, Boolean authBool, Integer taskType, String responsiblePersonId) {
		Page<SafetyTask> page = this.page(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new QueryWrapper<>(safetyInspectionRecord)
			.eq(taskBool, "task_type", taskType)
			.eq(authBool, "responsible_person_id", responsiblePersonId)
			.eq("station_id", safetyInspectionRecord.getStationId())
			.orderByDesc("create_time"));
		return R.data(new PageUtils(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
	}
}
