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
import org.springblade.energy.operationmaintenance.vo.*;
import org.springblade.energy.securitymanagement.util.DataFactory;
import org.springblade.energy.securitymanagement.util.PageQuery;
import org.springblade.energy.securitymanagement.util.PageUtils;
import org.springblade.enums.OperationType;
import org.springblade.system.user.entity.User;
import org.springblade.system.user.feign.IUserClient;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 控制器
 *
 * @author CYL
 * @since 2020-07-08
 */
@RestController
@AllArgsConstructor
@RequestMapping("/inspection")
@Api(value = "运行管理--巡检", tags = "运行管理--巡检接口")
public class InspectionController extends BladeController {

	InspectionService inspectionService;

	IUserClient userClient;

	NewsPushService newsPushServiceImpl;

	CheckItemRecordService checkRecordService;

	InspectionTaskService inspectionRecordService;

	MaintainService maintainService;

	NoticeService noticeService;


	/**
	 * 新增 CYL
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "新增", notes = "新增巡检计划")
	public R addInspection(@RequestBody Inspection inspection) {
		NewsPush newsPush = new NewsPush();
		PlanCount planCount = new PlanCount();
		processAutomaticStaffAssignment(inspection, newsPush);
		if (Func.isEmpty(inspection.getAssignedPersonId())) {
			return R.fail("分配人员不能为空");
		}
		Long primaryKey = DataFactory.processPrimaryKey();
		if (DataFactory.isAdministrator(Long.parseLong(inspection.getAssignedPersonId()))) {
			inspection.setIsAdmin(0);
		}
		inspection.setId(primaryKey);
		if (inspection.getIsStart() > 0) {
			if (inspection.getInspectionCycle() == 0) {
				throw new Error("开启轮询，轮询时间不能为0");
			}
			return setOrUpdateNewsPush(inspection, newsPush, planCount, primaryKey);
		}
		return R.status(inspectionService.save(new DataFactory<Inspection>().nameFactory(inspection)));
	}

	/**
	 * 任务项分页 CYL
	 */
	@GetMapping("/inspectionItem")
	@ApiOperationSupport(order = 2)
	@ApiOperation("任务巡检项列表")
	public R<PageUtils> inspectionItem(PageQuery pageQuery, InspectionTask inspectionRecord) {
		InspectionTask inspectionRecordServiceOne = inspectionRecordService.getOne(new QueryWrapper<InspectionTask>().eq("id", inspectionRecord.getId()));
		String inspectionItem = inspectionRecordServiceOne.getInspectionItem();
		List<InspectionQualityStandardVo> item = JSONObject.parseArray(inspectionItem, InspectionQualityStandardVo.class);
		return R.data(new PageUtils(item, (long) item.size(), pageQuery.getSize(), pageQuery.getCurrent(), true));
	}

	/**
	 * 进度跟踪 CYL
	 */
	@GetMapping("/progressTrack")
	@ApiOperationSupport(order = 3)
	@ApiOperation("进度跟踪")
	public R<String> progressTrack(InspectionTask inspectionRecord) {
		InspectionTask inspectionRecordServiceOne = inspectionRecordService.getOne(new QueryWrapper<InspectionTask>().eq("id", inspectionRecord.getId()));
		String progressTrack = inspectionRecordServiceOne.getProgressTrack();
		return R.data(progressTrack);
	}

	/**
	 * 进度跟踪列表 CYL
	 */
	@GetMapping("/progressTrack/page")
	@ApiOperationSupport(order = 4)
	@ApiOperation("进度跟踪列表")
	public R<PageUtils> progressTrackPage(PageQuery pageQuery, InspectionTask inspectionRecord, String yearMonthDay) {
		Page<InspectionTask> page = inspectionRecordService.page(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new QueryWrapper<>(inspectionRecord).eq("task_type", 1).orderByDesc("create_time").between("task_time", yearMonthDay.trim() + " 00:00:00", yearMonthDay.trim() + " 23:59:59"));
		return R.data(new PageUtils(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
	}


	/**
	 * 巡检线路 CYL
	 */
	@GetMapping("/inspectionRoute")
	@ApiOperationSupport(order = 5)
	@ApiOperation("巡检线路")
	public R<InspectionRouteStatusVo> inspectionRoute(InspectionTask inspectionRecord, String yearMonthDay) {
		return R.data(inspectionRecordService.inspectionRouteStatus(inspectionRecord.getAssignedPersonId(), yearMonthDay));
	}

	/**
	 * 巡检线路分页 CYL
	 */
	@GetMapping("/inspectionRoute/page")
	@ApiOperationSupport(order = 6)
	@ApiOperation("巡检线路列表")
	public R<PageUtils> inspectionRoute(PageQuery pageQuery, InspectionTask inspectionRecord, String yearMonthDay) {
		String userRole = AuthUtil.getUserRole();
		if (userRole.equals("admin") || userRole.equals("administrator")) {
			List<User> userList = userClient.userList();
			Long recordAssignedPersonId = inspectionRecord.getAssignedPersonId();
			List<InspectionPageVo> inspectionPageVos = new ArrayList<>();
			List<InspectionTask> inspectionRecords = inspectionRecordService.list(new QueryWrapper<InspectionTask>().between("task_time", yearMonthDay.trim() + " 00:00:00", yearMonthDay.trim() + " 23:59:59").orderByAsc("task_time"));
			if (inspectionRecords.size() == 0) {
				return R.data(new PageUtils(inspectionRecords, (long) inspectionRecords.size(), pageQuery.getSize(), pageQuery.getCurrent(), true));
			}
			AtomicInteger count = new AtomicInteger(0);
			AtomicReference<String> key = new AtomicReference<>("");
			userList.stream().distinct().filter(user -> user.getCreateUser().equals(AuthUtil.getUserId())).forEach(user ->
				inspectionRecords.stream().distinct().filter(record -> record.getAssignedPersonId().equals(user.getId())).forEach(record -> {
					String assignedPersonId = record.getAssignedPersonId().toString();
					if (!key.get().equals(assignedPersonId)) {
						InspectionPageVo inspectionPageVo = new InspectionPageVo();
						key.set(assignedPersonId);
						inspectionPageVo.setResponsiblePersonId(record.getAssignedPersonId());
						inspectionPageVo.setResponsiblePersonName(record.getAssignedPersonName());
						inspectionPageVo.setDate(record.getTaskTime());
						InspectionRouteStatusVo inspectionRouteStatusVo = inspectionRecordService.inspectionRouteStatus(Long.parseLong(assignedPersonId), yearMonthDay);
						inspectionPageVo.setStatus(inspectionRouteStatusVo.getStatus());
						inspectionPageVos.add(inspectionPageVo);
					}
					count.getAndIncrement();
				}));
			if (Func.isNotEmpty(recordAssignedPersonId)) {
				List<InspectionPageVo> responsiblePerson = inspectionPageVos.stream().filter(page -> page.getResponsiblePersonId().equals(recordAssignedPersonId)).collect(Collectors.toList());
				return R.data(new PageUtils(responsiblePerson, (long) responsiblePerson.size(), pageQuery.getSize(), pageQuery.getCurrent(), true));
			}
			return R.data(new PageUtils(inspectionPageVos, (long) inspectionPageVos.size(), pageQuery.getSize(), pageQuery.getCurrent(), true));
		} else {
			throw new ServiceException("无权访问");
		}
	}

	/**
	 * 巡检计划分页  CYL
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "巡检计划分页", notes = "巡检计划分页")
	public R<PageUtils> inspectionPage(Inspection inspection, PageQuery pageQuery) {
		Page<Inspection> page = inspectionService.page(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new QueryWrapper<>(inspection).orderByDesc("create_time", "next_time"));
		return R.data(new PageUtils(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
	}


	/**
	 * 巡检任务分页 CYL
	 */
	@GetMapping("/inspectionTaskPage")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "巡检任务分页", notes = "巡检任务分页")
	public R<PageUtils> inspectionTaskPage(InspectionTask inspectionRecord, PageQuery pageQuery) {
		Page<InspectionTask> page = inspectionRecordService.page(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new QueryWrapper<>(inspectionRecord).orderByDesc("create_time"));
		return R.data(new PageUtils(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
	}

	/**
	 * 删除巡检 CYL
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "逻辑删除巡检计划", notes = "逻辑删除巡检计划")
	public R removeInspection(@RequestBody List<Inspection> inspections) {
		inspections.forEach((inspection) -> {
			if (inspection.getIsStart() > 0) {
				inspectionService.removeSync(inspection.getId());
			} else {
				inspectionService.remove(new QueryWrapper<Inspection>().eq("id", inspection.getId()));
			}
		});
		return R.status(true);
	}

	/**
	 * 更新巡检 CYL
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 10)
	@ApiOperation(value = "修改巡检", notes = "修改巡检")
	public R updateInspection(@RequestBody Inspection inspection) {
		Inspection ins = inspectionService.getOne(new QueryWrapper<Inspection>().eq("id", inspection.getId()));
		if (Func.isEmpty(ins)) {
			return R.status(false);
		}
		Integer isStart = ins.getIsStart();
		if (isStart > 0) {
			return R.status(false);
		}
		if (Func.isEmpty(inspection.getAssignedPersonId())) {
			return R.fail("分配人员不能为空");
		}
		NewsPush newsPush = new NewsPush();
		PlanCount planCount = new PlanCount();
		processAutomaticStaffAssignment(inspection, newsPush);
		if (inspection.getIsStart() > 0) {
			return setOrUpdateNewsPush(inspection, newsPush, planCount, null);
		}
		return R.status(inspectionService.updateById(new DataFactory<Inspection>().nameFactory(inspection)));
	}

	@GetMapping("/count")
	@ApiOperationSupport(order = 11)
	@ApiOperation(value = "巡检结果统计")
	public R<InspectionCountVo> inspectionCount(String stationId, String siteId, String time, Integer type, Integer taskType) {
		return inspectionRecordService.inspectionCount(stationId, siteId, time, type, taskType);
	}

	@GetMapping("/count/detail")
	@ApiOperationSupport(order = 12)
	@ApiOperation(value = "巡检详情统计")
	public R<PageUtils> inspectionSiteCount(PageQuery pageQuery, String stationId, String siteId, String time, Integer type, Integer taskType) {
		R<InspectionCountVo> inspectionCountVo = inspectionRecordService.inspectionCount(stationId, siteId, time, type, taskType);
		List<InspectionSiteCountVo> site = inspectionCountVo.getData().getSite();
		return R.data(new PageUtils(site, (long) site.size(), pageQuery.getSize(), pageQuery.getCurrent(), true));
	}

	@GetMapping("/auto/distribution")
	@ApiOperationSupport(order = 13)
	@ApiOperation(value = "自动分配责任人名称显示")
	public R<String> autoDistribution(@RequestParam String datetime) {
		String[] admin = DataFactory.administratorId(AuthUtil.getUserId()).split(",");
		return R.data(new DataFactory<>().processAutomaticStaffAssignment(datetime, Long.parseLong(admin[0]), Long.parseLong(admin[1]), true));
	}

	/**
	 * 更新或新增消息推送表  CYL
	 */
	private R setOrUpdateNewsPush(Inspection inspection, NewsPush newsPush, PlanCount planCount, Long primaryKey) {
		newsPush.setOperationType(OperationType.INSPECTION.id);
		setNewsPushValue(newsPush, inspection, planCount, primaryKey);
		return inspectionService.addSync(new DataFactory<Inspection>().nameFactory(inspection), newsPush, planCount);
	}

	/**
	 * 存值到消息推送表  CYL
	 */
	private void setNewsPushValue(NewsPush newsPush, Inspection inspection, PlanCount planCount, Long primaryKey) {
		Long userId = Long.parseLong(inspection.getAssignedPersonId());
		Date startDate = inspection.getStartDate();
		Date cycleStart = inspection.getCycleStart();
		Date time = DateUtil.parse(DateUtil.formatDate(startDate) + " " + DateUtil.formatTime(cycleStart), DateUtil.DATETIME_FORMAT);
		/* ------------------------------------------------------ 触发时间戳 */
		final long triggerTime = time.getTime();
		/* ------------------------------------------------------ 提前时间戳 */
		final long advanceTime = 3600000L * inspection.getInspectionBefore();
		/* ------------------------------------------------------ 轮询时间戳 */
		final long pollingTime = 86400000L * inspection.getInspectionCycle();
		/* ------------------------------------------------------ 推送时间戳 */
		final long scheduleTime = triggerTime - advanceTime;

		final long expectTime = 3600000L * inspection.getInspectionAfter();
		newsPush.setExpectTime(expectTime);
		inspection.setRemindTime(new Date(scheduleTime));
		inspection.setNextTime(new Date(triggerTime));

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
		newsPush.setIsPolling(inspection.getIsStart());
		newsPush.setTaskTime(time);
		if (Func.isEmpty(primaryKey)) {
			newsPush.setTaskId(inspection.getId());
		} else {
			newsPush.setTaskId(primaryKey);
		}
		newsPush.setResponsibleId(userId);
		Long superiorsId = Long.parseLong(DataFactory.administratorId(userId).split(",")[0]);
		newsPush.setSuperiorsId(superiorsId);
		Long siteId = inspection.getSiteId();
		Long stationId = inspection.getStationId();
		newsPush.setSiteId(siteId);
		newsPush.setStationId(stationId);
		newsPush.setAutoAssign(inspection.getAssignedType());

		Long taskId = newsPush.getTaskId();
		planCount.setSiteId(siteId);
		planCount.setStationId(stationId);
		planCount.setPlanId(taskId);
		planCount.setTaskType(0);
		planCount.setTaskTime(time);
		planCount.setTaskResponsible(userId);
	}

	public void processAutomaticStaffAssignment(Inspection inspection, NewsPush newsPush) {
		if (Func.isNotEmpty(inspection.getStartDate())) {
			String date = new SimpleDateFormat("yyyy-MM-dd").format(inspection.getStartDate());
			String time = new SimpleDateFormat("HH:mm:ss").format(inspection.getCycleStart());
			String datetime = date + " " + time;
			AtomicReference<String> processAutomaticStaffAssignment = new AtomicReference<>("");
			if (inspection.getAssignedType() == 0) {
				processAutomaticStaffAssignment.set(new DataFactory<>().processAutomaticStaffAssignment(datetime, AuthUtil.getUserId(), inspection.getStationId(), true));
			} else {
				processAutomaticStaffAssignment.set(new DataFactory<>().processAutomaticStaffAssignment(datetime, Long.parseLong(inspection.getAssignedPersonId()), inspection.getStationId(), false));
			}
			if (processAutomaticStaffAssignment.get().equals("")) {
				throw new ServiceException("无上班人员，无法分配");
			}
			String[] work = processAutomaticStaffAssignment.get().split(",");
			inspection.setAssignedPersonId(work[0]);
			newsPush.setWorkStatus(Integer.parseInt(work[2]));
			newsPush.setWorkShift(Integer.parseInt(work[3]));
			newsPush.setWorkTime(new Date(Long.parseLong(work[4])));
			newsPush.setOffWorkTime(new Date(Long.parseLong(work[5])));
		}
	}
}
