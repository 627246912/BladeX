package org.springblade.energy.securitymanagement.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.tool.api.R;
import org.springblade.energy.securitymanagement.entity.SafetyTask;
import org.springblade.energy.securitymanagement.enums.Status;
import org.springblade.energy.securitymanagement.service.SafetyTaskService;
import org.springblade.energy.securitymanagement.util.PageQuery;
import org.springblade.energy.securitymanagement.util.PageUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/security/patrol")
@Api(value = "APP--安全巡视", tags = "APP--安全巡视接口")
public class SafetyTaskController {


	SafetyTaskService safetyInspectionRecordService;


	@ApiOperationSupport(order = 1)
	@ApiOperation("列表")
	@GetMapping("/page")
	public R<PageUtils> appSafetyInspectionRecord(PageQuery pageQuery, SafetyTask safetyInspectionRecord) {
		return safetyInspectionRecordService.listSafetyInspectionRecord(pageQuery, safetyInspectionRecord);
	}

	@ApiOperationSupport(order = 2)
	@ApiOperation("PC列表")
	@GetMapping("/page/pc")
	public R<PageUtils> pcSafetyInspectionRecord(PageQuery pageQuery, SafetyTask safetyTask) {
		Page<SafetyTask> page = safetyInspectionRecordService.page(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new QueryWrapper<>(safetyTask).orderByDesc("create_time"));
		return R.data(new PageUtils(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
	}

	@ApiLog("提交")
	@ApiOperationSupport(order = 3)
	@ApiOperation("提交")
	@PutMapping("/update")
	public R updateSafetyInspectionRecord(@RequestBody SafetyTask safetyInspectionRecord) {
		return safetyInspectionRecordService.updateSafetyInspectionRecord(safetyInspectionRecord);
	}


	@ApiOperationSupport(order = 4)
	@ApiOperation("N+ 状态查询")
	@PostMapping("/status")
	public R<PageUtils> listStatus(PageQuery pageQuery, @RequestBody List<Integer> statusId, Long responsiblePersonId) {
		List<SafetyTask> safetyInspectionRecords = new ArrayList<>();
		statusId.stream().distinct().forEach((safetyInspectionRecord) -> {
			List<SafetyTask> list = safetyInspectionRecordService.list(new QueryWrapper<SafetyTask>().eq("task_status", safetyInspectionRecord).eq("responsible_person_id", responsiblePersonId));
			list.stream().distinct().sorted(Comparator.comparing(SafetyTask::getCreateTime).reversed()).forEach(safetyInspectionRecords::add);
		});
		return R.data(new PageUtils(safetyInspectionRecords, (long) safetyInspectionRecords.size(), pageQuery.getSize(), pageQuery.getCurrent(), true));
	}


	@ApiOperationSupport(order = 5)
	@ApiOperation("批量删除")
	@PostMapping("/remove")
	public R<SafetyTask> removeSafetyInspectionRecord(@RequestBody List<SafetyTask> params) {
		if (params.size() <= 0)
			return R.fail(Status.DELETE_ERROR.getVal());
		params.stream().distinct().forEach((safetyInspectionRecord) -> safetyInspectionRecordService.remove(new QueryWrapper<SafetyTask>().eq("id", safetyInspectionRecord.getId())));
		return R.success(Status.DELETE_SUCCESS.getVal());
	}


}
