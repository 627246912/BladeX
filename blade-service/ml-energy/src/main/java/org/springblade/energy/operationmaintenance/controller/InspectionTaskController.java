package org.springblade.energy.operationmaintenance.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springblade.energy.operationmaintenance.entity.InspectionTask;
import org.springblade.energy.operationmaintenance.service.InspectionTaskService;
import org.springblade.energy.securitymanagement.util.PageQuery;
import org.springblade.energy.securitymanagement.util.PageUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inspection/record")
@Api(value = "APP--巡检", tags = "APP--巡检接口")
@AllArgsConstructor
public class InspectionTaskController {

	InspectionTaskService inspectionRecordService;

	@GetMapping("/page")
	@ApiOperationSupport(order = 1)
	@ApiOperation("列表")
	public R<PageUtils> inspectionRecordList(PageQuery pageQuery, InspectionTask inspectionRecord) {
		return inspectionRecordService.customizePage(pageQuery, inspectionRecord);
	}

	@PutMapping("/update")
	@ApiOperationSupport(order = 2)
	@ApiOperation("编辑")
	public R<Boolean> updateInspectionRecord(@RequestBody InspectionTask inspectionRecord) {
		return inspectionRecordService.update(inspectionRecord, new QueryWrapper<InspectionTask>().eq("id", inspectionRecord.getId())) ? R.status(true) : R.status(false);
	}
}
