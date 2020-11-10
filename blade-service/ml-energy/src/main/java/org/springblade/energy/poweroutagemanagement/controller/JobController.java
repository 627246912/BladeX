package org.springblade.energy.poweroutagemanagement.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.tool.api.R;
import org.springblade.energy.operationmaintenance.entity.Notice;
import org.springblade.energy.operationmaintenance.service.NoticeService;
import org.springblade.energy.poweroutagemanagement.entity.Job;
import org.springblade.energy.poweroutagemanagement.service.JobService;
import org.springblade.energy.securitymanagement.enums.Status;
import org.springblade.energy.securitymanagement.util.DataFactory;
import org.springblade.energy.securitymanagement.util.PageQuery;
import org.springblade.energy.securitymanagement.util.PageUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/job")
@Api(value = "APP--工作票", tags = "APP--工作票接口")
//app
public class JobController {

	JobService jobService;

	NoticeService noticeService;


	@ApiOperationSupport(order = 1)
	@ApiOperation("添加")
	@PostMapping("/save")
	public R addJob(@RequestBody Job job) {
		Long primaryKey = DataFactory.processPrimaryKey();
		job.setId(primaryKey);
		Notice notice = new Notice();
		notice.setTaskId(primaryKey);
		notice.setSiteId(job.getSiteId());
		notice.setStationId(job.getStationId());
		Long responsibleId = job.getApplicationPerson();
		notice.setResponsibleId(responsibleId);
		notice.setLeaderId(Long.parseLong(DataFactory.administratorId(responsibleId).split(",")[0]));
		notice.setNoticeType(5);
		notice.setTaskType(6);
		notice.setNoticeName("工作票");
		notice.setNoticeTime(DataFactory.datetime);
		return R.status(jobService.save(new DataFactory<Job>().nameFactory(job)) && noticeService.save(notice));
	}


	@ApiOperationSupport(order = 2)
	@ApiOperation("列表 & 条件查询")
	@GetMapping("/page")
	public R<PageUtils> listJob(PageQuery pageQuery, Job job) {
		Page<Job> page = jobService.page(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new QueryWrapper<>(job).orderByDesc("create_time"));
		return R.data(new PageUtils(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
	}


	@ApiOperationSupport(order = 3)
	@ApiOperation("编辑")
	@PutMapping("/update")
	public R<Job> updateJob(@RequestBody Job job) {
		return jobService.update(new DataFactory<Job>().nameFactory(job), new QueryWrapper<Job>().eq("id", job.getId())) ? R.success(Status.UPDATE_SUCCESS.getVal()) : R.fail(Status.UPDATE_ERROR.getVal());
	}


	@ApiOperationSupport(order = 4)
	@ApiOperation("批量删除")
	@PostMapping("/remove")
	public R<Job> removeJob(@RequestBody List<Job> params) {
		if (params.size() <= 0)
			return R.fail(Status.DELETE_ERROR.getVal());

		params.stream().distinct().forEach((job) -> jobService.remove(new QueryWrapper<Job>().eq("id", job.getId())));
		return R.success(Status.DELETE_SUCCESS.getVal());
	}
}
