package org.springblade.energy.operationmaintenance.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.energy.operationmaintenance.entity.InspectionTask;
import org.springblade.energy.operationmaintenance.entity.Repair;
import org.springblade.energy.operationmaintenance.service.InspectionTaskService;
import org.springblade.energy.operationmaintenance.service.RepairService;
import org.springblade.energy.operationmaintenance.vo.TaskTotalVo;
import org.springblade.energy.securitymanagement.util.PageQuery;
import org.springblade.energy.securitymanagement.util.PageUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@AllArgsConstructor
@RequestMapping("/api/task")
@Api(value = "APP--首页", tags = "APP--首页接口")
public class TaskApiController {

	InspectionTaskService inspectionRecordService;

	RepairService repairService;

	@ApiOperationSupport(order = 1)
	@ApiOperation("各项任务总数")
	@GetMapping("/total")
	public R<TaskTotalVo> taskTotal(@RequestParam Integer taskStatus) {
		AtomicBoolean auth = new AtomicBoolean(true);
		Long userId = AuthUtil.getUserId();
		String userRole = AuthUtil.getUserRole();
		if (userRole.trim().equals("admin") || userRole.trim().equals("administrator")) {
			auth.set(false);
		}
		List<InspectionTask> inspectionRecords = inspectionRecordService.list(new QueryWrapper<InspectionTask>().eq(auth.get(), "assigned_person_id", userId));
		List<Repair> repairs = repairService.list(new QueryWrapper<Repair>().eq(auth.get(), "assigned_person_id", userId));
		TaskTotalVo taskTotal = new TaskTotalVo();
		taskTotal.setInspectionCount(inspectionRecords.stream().filter(inspectionRecord -> inspectionRecord.getTaskType() == 1 && inspectionRecord.getAccomplish().equals(taskStatus)).count());
		taskTotal.setMaintainCount(inspectionRecords.stream().filter(inspectionRecord -> inspectionRecord.getTaskType() == 2 && inspectionRecord.getAccomplish().equals(taskStatus)).count());
		AtomicInteger status = new AtomicInteger();
		if (taskStatus == 1) {
			status.set(0);
		} else if (taskStatus == 0) {
			status.set(1);
		}
		taskTotal.setRepairCount(repairs.stream().filter(repair -> repair.getTaskStatus().equals(Integer.toString(status.get()))).count());
		return R.data(taskTotal);
	}

	@ApiOperationSupport(order = 2)
	@ApiOperation("扫码巡检")
	@GetMapping("/scan/code")
	public R<PageUtils> ScanCode(PageQuery pageQuery, String Date, Long devId) {
		List<InspectionTask> inspectionRecords = inspectionRecordService.list(new QueryWrapper<InspectionTask>()
			.eq("task_type", 1)
			.eq("inspection_type", 2)
			.eq("source_type", 1)
			.eq("equipment_no", devId)
			.eq("assigned_person_id", AuthUtil.getUserId())
			.eq("accomplish", 1)
			.between("task_time", Date + " 00:00:00", Date + " 23:59:59")
			.orderByAsc("task_time"));
		return R.data(new PageUtils(inspectionRecords, (long) inspectionRecords.size(), pageQuery.getSize(), pageQuery.getCurrent(), true));
	}
}
