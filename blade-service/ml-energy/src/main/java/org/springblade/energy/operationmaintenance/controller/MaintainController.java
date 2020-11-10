package org.springblade.energy.operationmaintenance.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.operationmaintenance.entity.*;
import org.springblade.energy.operationmaintenance.service.*;
import org.springblade.energy.operationmaintenance.vo.InspectionQualityStandardVo;
import org.springblade.energy.securitymanagement.util.DataFactory;
import org.springblade.energy.securitymanagement.util.PageQuery;
import org.springblade.energy.securitymanagement.util.PageUtils;
import org.springblade.enums.OperationType;
import org.springblade.system.user.entity.User;
import org.springblade.system.user.feign.IUserClient;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 控制器
 *
 * @author CYL
 * @since 2020-07-23
 */
@RestController
@AllArgsConstructor
@RequestMapping("/maintain")
@Api(value = "运行管理--保养", tags = "运行管理--保养接口")
public class MaintainController extends BladeController {

	MaintainService maintainService;

	IUserClient userClient;

	NewsPushService newsPushServiceImpl;

	CheckItemRecordService checkRecordService;

	MaintainRecordService maintainRecordService;

	InspectionTaskService inspectionRecordService;


	/**
	 * 保养新增 CYL
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "新增")
	public R addMaintain(@RequestBody Maintain maintain) {
		NewsPush newsPush = new NewsPush();
		PlanCount planCount = new PlanCount();
		processAutomaticStaffAssignment(maintain, newsPush);
		if (Func.isEmpty(maintain.getAssignedPersonId())) {
			return R.fail("分配人员不能为空");
		}
		Long primaryKey = DataFactory.processPrimaryKey();
		if (DataFactory.isAdministrator(Long.parseLong(maintain.getAssignedPersonId()))) {
			maintain.setIsAdmin(0);
		}
		maintain.setId(primaryKey);

		if (maintain.getIsStart() > 0) {
			return setOrUpdateNewsPush(maintain, newsPush, planCount, primaryKey);
		}
		return R.status(maintainService.save(new DataFactory<Maintain>().nameFactory(maintain)));
	}

	/**
	 * 保养计划列表 CYL
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 2)
	@ApiOperation("保养计划列表")
	public R<PageUtils> pageMaintain(PageQuery pageQuery, Maintain maintain) {
		Page<Maintain> page = maintainService.page(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new QueryWrapper<>(maintain).orderByDesc("create_time"));
		return R.data(new PageUtils(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
	}


	@PostMapping("/remove")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "批量删除")
	public R<Boolean> removeInspection(@RequestBody List<Maintain> maintains) {
		maintains.forEach((maintain) -> {
			if (maintain.getIsStart() > 0) {
				maintainService.removeSync(maintain.getId());
			} else {
				maintainService.remove(new QueryWrapper<Maintain>().eq("id", maintain.getId()));
			}
		});
		return R.status(true);
	}

	@PostMapping("/update")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "编辑")
	public R<Boolean> updateInspection(@RequestBody Maintain maintain) {
		Maintain main = maintainService.getOne(new QueryWrapper<Maintain>().eq("id", maintain.getId()));
		if (Func.isEmpty(main)) {
			return R.status(false);
		}
		Integer isStart = main.getIsStart();
		if (isStart > 0) {
			return R.status(false);
		}
		if (Func.isEmpty(maintain.getAssignedPersonId())) {
			return R.fail("分配人员不能为空");
		}
		NewsPush newsPush = new NewsPush();
		PlanCount planCount = new PlanCount();
		processAutomaticStaffAssignment(maintain, newsPush);
		if (maintain.getIsStart() > 0) {
			return setOrUpdateNewsPush(maintain, newsPush, planCount, null);
		}
		return R.status(maintainService.updateById(new DataFactory<Maintain>().nameFactory(maintain)));
	}

	/**
	 * 保养任务分页 CYL
	 */
	@GetMapping("/taskPage")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "保养任务列表")
	public R<PageUtils> maintainTaskPage(InspectionTask inspectionRecord, PageQuery pageQuery) {
		Page<InspectionTask> page = inspectionRecordService.page(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new QueryWrapper<>(inspectionRecord).orderByDesc("create_time"));
		return R.data(new PageUtils(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
	}

	/**
	 * 任务项分页 CYL
	 */
	@GetMapping("/maintainItem")
	@ApiOperationSupport(order = 6)
	@ApiOperation("任务巡检项列表")
	public R<PageUtils> maintainItem(PageQuery pageQuery, InspectionTask inspectionRecord) {
		InspectionTask inspectionRecordServiceOne = inspectionRecordService.getOne(new QueryWrapper<InspectionTask>().eq("id", inspectionRecord.getId()));
		String inspectionItem = inspectionRecordServiceOne.getInspectionItem();
		List<InspectionQualityStandardVo> item = JSONObject.parseArray(inspectionItem, InspectionQualityStandardVo.class);
		return R.data(new PageUtils(item, (long) item.size(), pageQuery.getSize(), pageQuery.getCurrent(), true));
	}

	/**
	 * 更新或新增消息推送表  CYL
	 */
	private R<Boolean> setOrUpdateNewsPush(Maintain maintain, NewsPush newsPush, PlanCount planCount, Long primaryKey) {
		newsPush.setOperationType(OperationType.MAINTAIN.id);
		setNewsPushValue(newsPush, maintain, planCount, primaryKey);
		return maintainService.addSync(new DataFactory<Maintain>().nameFactory(maintain), newsPush, planCount);
	}

	/**
	 * 存值到消息推送表  CYL
	 */
	private void setNewsPushValue(NewsPush newsPush, Maintain maintain, PlanCount planCount, Long primaryKey) {
		Long userId = Long.parseLong(maintain.getAssignedPersonId());
		Date startDate = maintain.getStartDate();
		Date cycleStart = maintain.getCycleStart();
		Date time = DateUtil.parse(DateUtil.formatDate(startDate) + " " + DateUtil.formatTime(cycleStart), DateUtil.DATETIME_FORMAT);
		/* ------------------------------------------------------ 触发时间戳 */
		final long triggerTime = time.getTime();
		/* ------------------------------------------------------ 提前时间戳 */
		final long advanceTime = 3600000L * maintain.getMaintainBefore();
		/* ------------------------------------------------------ 轮询时间戳 */
		final long pollingTime = 86400000L * maintain.getMaintainCycle();
		/* ------------------------------------------------------ 推送时间戳 */
		final long scheduleTime = triggerTime - advanceTime;

		final long expectTime = 3600000L * maintain.getMaintainAfter();
		newsPush.setExpectTime(expectTime);
		maintain.setRemindTime(new Date(scheduleTime));
		maintain.setNextTime(new Date(triggerTime));

		User user = userClient.userInfoById(userId).getData();
		if (Func.isNotEmpty(user)) {
			newsPush.setSendSms(user.getSendSms());
			newsPush.setSendEmail(user.getSendEmail());
			newsPush.setSendApp(user.getSendStation());
			newsPush.setPhone(user.getPhone());
			newsPush.setEmail(user.getEmail());
		}
		newsPush.setTriggerTime(triggerTime);
		newsPush.setAdvanceTime(advanceTime);
		newsPush.setPollingTime(pollingTime);
		newsPush.setScheduleTime(scheduleTime);
		newsPush.setTime(new Date(scheduleTime));
		newsPush.setIsPolling(maintain.getIsStart());
		newsPush.setTaskTime(time);
		if (Func.isEmpty(primaryKey)) {
			newsPush.setTaskId(maintain.getId());
		} else {
			newsPush.setTaskId(primaryKey);
		}
		newsPush.setResponsibleId(userId);
		newsPush.setSuperiorsId(Long.parseLong(DataFactory.administratorId(userId).split(",")[0]));
		Long stationId = maintain.getStationId();
		Long siteId = maintain.getSiteId();
		newsPush.setSiteId(siteId);
		newsPush.setStationId(stationId);
		newsPush.setAutoAssign(maintain.getAssignedType());

		planCount.setSiteId(siteId);
		planCount.setStationId(stationId);
		planCount.setPlanId(newsPush.getTaskId());
		planCount.setTaskType(1);
		planCount.setTaskTime(time);
		planCount.setTaskResponsible(userId);
	}

	public void processAutomaticStaffAssignment(Maintain maintain, NewsPush newsPush) {
		if (Func.isNotEmpty(maintain.getStartDate())) {
			String date = new SimpleDateFormat("yyyy-MM-dd").format(maintain.getStartDate());
			String time = new SimpleDateFormat("HH:mm:ss").format(maintain.getCycleStart());
			String datetime = date + " " + time;
			AtomicReference<String> processAutomaticStaffAssignment = new AtomicReference<>("");
			if (maintain.getAssignedType() == 0) {
				processAutomaticStaffAssignment.set(new DataFactory<>().processAutomaticStaffAssignment(datetime, AuthUtil.getUserId(), maintain.getStationId(), true));
			} else {
				processAutomaticStaffAssignment.set(new DataFactory<>().processAutomaticStaffAssignment(datetime, Long.parseLong(maintain.getAssignedPersonId()), maintain.getStationId(), false));
			}
			if (processAutomaticStaffAssignment.get().equals("")) {
				throw new ServiceException("无上班人员，无法分配");
			}
			String[] work = processAutomaticStaffAssignment.get().split(",");
			maintain.setAssignedPersonId(work[0]);
			newsPush.setWorkStatus(Integer.parseInt(work[2]));
			newsPush.setWorkShift(Integer.parseInt(work[3]));
			newsPush.setWorkTime(new Date(Long.parseLong(work[4])));
			newsPush.setOffWorkTime(new Date(Long.parseLong(work[5])));
		}
	}
}
