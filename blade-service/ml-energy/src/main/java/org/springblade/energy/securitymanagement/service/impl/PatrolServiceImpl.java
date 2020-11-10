package org.springblade.energy.securitymanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.operationmaintenance.entity.NewsPush;
import org.springblade.energy.operationmaintenance.entity.PlanCount;
import org.springblade.energy.operationmaintenance.service.NewsPushService;
import org.springblade.energy.operationmaintenance.service.PlanCountService;
import org.springblade.energy.operationmaintenance.service.RepairService;
import org.springblade.energy.runningmanagement.station.entity.Site;
import org.springblade.energy.runningmanagement.station.service.ISiteService;
import org.springblade.energy.runningmanagement.station.service.IStationService;
import org.springblade.energy.securitymanagement.entity.Patrol;
import org.springblade.energy.securitymanagement.entity.SafetyTask;
import org.springblade.energy.securitymanagement.enums.Status;
import org.springblade.energy.securitymanagement.mapper.PatrolMapper;
import org.springblade.energy.securitymanagement.service.PatrolService;
import org.springblade.energy.securitymanagement.service.SafetyTaskService;
import org.springblade.energy.securitymanagement.util.DataFactory;
import org.springblade.energy.securitymanagement.util.ReportForm;
import org.springblade.energy.securitymanagement.vo.DateStatisticsVo;
import org.springblade.energy.securitymanagement.vo.DateTimeVo;
import org.springblade.energy.securitymanagement.vo.LocationStatisticsVo;
import org.springblade.energy.securitymanagement.vo.SecurityCountVo;
import org.springblade.system.user.entity.User;
import org.springblade.system.user.feign.IUserClient;
import org.springblade.util.DateUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PatrolServiceImpl extends BaseServiceImpl<PatrolMapper, Patrol> implements PatrolService {

	NewsPushService newsPushService;

	SafetyTaskService safetyInspectionRecordService;

	RepairService repairService;

	ISiteService siteService;

	IStationService stationService;

	IUserClient iUserClient;

	PlanCountService planCountService;

	static final String ZERO = "0".trim();

	static final String RATE = "0.0".trim();

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R addPatrol(Patrol patrol) {
		Long primaryKey = DataFactory.processPrimaryKey();
		if (DataFactory.isAdministrator(patrol.getResponsible())) {
			patrol.setIsAdmin(0);
		}
		patrol.setId(primaryKey);
		Patrol processingPatrol = new DataFactory<Patrol>().nameFactory(patrol);
		if (patrol.getIsEnable() == 0) {
			if (!this.save(processingPatrol)) {
				throw new Error(Status.ADD_ERROR.getVal());
			}
			return R.success(Status.ADD_SUCCESS.getVal());
		}
		return addNewsPush(processingPatrol, 1, primaryKey);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R<Patrol> updatePatrol(Patrol patrol) {
		Long taskId = patrol.getId();
		Patrol attached = this.getOne(new QueryWrapper<Patrol>().eq("id", taskId));
		if (Func.isNotEmpty(attached)) {
			if (attached.getIsEnable() > 0) {
				throw new Error("已开启自动推送，无法编辑");
			} else {
				if (patrol.getIsEnable() > 0) {
					this.addNewsPush(patrol, 2, taskId);
				}
				return this.update(new DataFactory<Patrol>().nameFactory(patrol), new QueryWrapper<Patrol>().eq("id", taskId)) ? R.success(Status.UPDATE_SUCCESS.getVal()) : R.fail(Status.UPDATE_ERROR.getVal());
			}
		}
		return null;
	}

	@Transactional(rollbackFor = Exception.class)
	public R addNewsPush(Patrol patrol, Integer signal, Long primaryKey) {
		NewsPush newsPush = new NewsPush();
		PlanCount planCount = new PlanCount();
		try {
			Long responsible = patrol.getResponsible();
			Integer isEnable = patrol.getIsEnable();
			Long taskPushAheadDuration = patrol.getTaskPushAheadDuration();
			Long checkCycle = patrol.getCheckCycle();
			Long checkDuration = patrol.getCheckDuration();
			String enableDate = patrol.getEnableDate();
			String checkCycleStartDate = patrol.getCheckCycleStartDate();
			newsPush.setOperationType(3);
			newsPush.setTaskId(primaryKey);
			newsPush.setResponsibleId(responsible);

			AtomicLong triggerTime = new AtomicLong();
			if (Func.isNotEmpty(enableDate) && isEnable == 1) {
				triggerTime.set(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(enableDate.trim() + " " + checkCycleStartDate.trim()).getTime());
				newsPush.setTriggerTime(triggerTime.get());
			}
			if (Func.isEmpty(enableDate) && isEnable == 1) {
				return R.fail("FUCK");
			}

			/*  测试数据
			//final long advanceTime = 60000 * (long) taskPushAheadDuration;
			//long pollingTime = 60000 * (long) checkCycle; */

			final long advanceTime = 3600000L * taskPushAheadDuration;
			final long pollingTime = 86400000L * checkCycle;
			final long expectTime = 3600000L * checkDuration;
			final long scheduleTime = triggerTime.get() - advanceTime;

			R<User> userInfo = iUserClient.userInfoById(patrol.getResponsible());
			User detail = userInfo.getData();
			if (Func.isNotEmpty(detail)) {
				newsPush.setSendSms(detail.getSendSms());
				newsPush.setSendEmail(detail.getSendEmail());
				newsPush.setSendApp(detail.getSendStation());
				newsPush.setPhone(detail.getPhone());
				newsPush.setEmail(detail.getEmail());
			}

			newsPush.setAdvanceTime(advanceTime);
			newsPush.setPollingTime(pollingTime);
			newsPush.setScheduleTime(scheduleTime);
			newsPush.setExpectTime(expectTime);
			newsPush.setIsPolling(isEnable);
			Date taskTime = new Date(triggerTime.get());
			patrol.setNextTime(taskTime);
			newsPush.setSuperiorsId(Long.parseLong(DataFactory.administratorId(responsible).split(",")[0]));
			Long siteId = patrol.getSiteId();
			Long stationId = patrol.getStationId();
			newsPush.setSiteId(siteId);
			newsPush.setStationId(stationId);
			//这里由自动变手动分配
			String processAutomaticStaffAssignment = new DataFactory<>().processAutomaticStaffAssignment(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(triggerTime.get()), responsible, patrol.getStationId(), false);
			if (processAutomaticStaffAssignment.equals("")) {
				throw new ServiceException("指定责任人休息状态，无法分配");
			}
			String[] work = processAutomaticStaffAssignment.split(",");
			int shiftStatus = Integer.parseInt(work[2]);
			newsPush.setWorkStatus(shiftStatus);
			if (shiftStatus == 4) {
				throw new ServiceException("指定责任人下班状态，无法分配");
			}
			newsPush.setWorkShift(Integer.parseInt(work[3]));
			newsPush.setWorkTime(new Date(Long.parseLong(work[4])));
			newsPush.setOffWorkTime(new Date(Long.parseLong(work[5])));

			planCount.setSiteId(siteId);
			planCount.setStationId(stationId);
			planCount.setPlanId(newsPush.getTaskId());
			planCount.setTaskType(3);
			planCount.setTaskTime(taskTime);
			planCount.setTaskResponsible(responsible);
		} catch (
			ParseException e) {
			throw new Error("时间转换异常");
		}

		if (signal == 1) {
			if (!this.save(patrol)) {
				throw new Error(Status.ADD_ERROR.getVal());
			}
		}
		if (signal == 2) {
			if (!this.update(patrol, new QueryWrapper<Patrol>().eq("id", patrol.getId()))) {
				throw new Error(Status.UPDATE_ERROR.getVal());
			}
		}
		return R.status(newsPushService.save(newsPush) && planCountService.save(planCount));
	}

	@Override
	public void removePatrol(List<Patrol> patrols) {
		patrols.forEach((patrol) -> {
			Long taskId = patrol.getId();
			this.remove(new QueryWrapper<Patrol>().eq("id", taskId));
			newsPushService.remove(new QueryWrapper<NewsPush>().eq("task_id", taskId));
			planCountService.remove(new QueryWrapper<PlanCount>().eq("plan_id", taskId));
		});
	}

	@Override
	public R<SecurityCountVo> countPatrol(String stationId, String siteId, String time, Integer type) {
		Map<String, DateTimeVo> season = ReportForm.dateTypeSelect(type, time);
		DateTimeVo date = season.get("date");
		String startTime = date.getStartTime();
		String endsTime = date.getEndsTime();
		AtomicReference<List<SafetyTask>> total = new AtomicReference<>();
		if (Func.isEmpty(siteId)) {
			total.set(safetyInspectionRecordService.list(new QueryWrapper<SafetyTask>().eq("station_id", stationId).between("task_time", startTime, endsTime).eq("task_type", 1).eq("is_count", 1)));

		} else {
			total.set(safetyInspectionRecordService.list(new QueryWrapper<SafetyTask>().eq("station_id", stationId).eq("site_id", siteId).between("task_time", startTime, endsTime).eq("task_type", 1).eq("is_count", 1)));
		}
		return this.resultDataProcess(total.get(), season, stationId, siteId, type, startTime, endsTime);
	}

	public String averageScore(List<SafetyTask> safetyInspectionRecords) {
		if (safetyInspectionRecords.size() <= 0) {
			return ZERO;
		}
		AtomicReference<Integer> score = new AtomicReference<>(0);
		safetyInspectionRecords.forEach((safetyInspectionRecord) ->
			score.set(score.get() + safetyInspectionRecord.getSatisfaction()));
		return BigDecimal.valueOf(Double.parseDouble(score.get().toString()) / Double.parseDouble(safetyInspectionRecords.size() + "")).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
	}

	public Map<String, DateStatisticsVo> dataProcessMonth(String stationId, String siteId, String startTime, String endsTime) {
		List<String> days = DateUtil.getDaysBetween(DateUtil.strToDate(startTime, DateUtil.TIME_PATTERN_24), DateUtil.strToDate(endsTime, DateUtil.TIME_PATTERN_24));
		ConcurrentMap<String, DateStatisticsVo> result = new ConcurrentHashMap<>();
		AtomicReference<List<SafetyTask>> total = this.safetyInspectionTotal(stationId, siteId, startTime, endsTime);
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
		CountDownLatch countDownLatch = new CountDownLatch(days.size());
		days.forEach((day) ->
			threadPoolExecutor.execute(() -> {
				DateStatisticsVo statistics = new DateStatisticsVo();
				final long taskCount = total.get().stream().filter(safetyInspectionRecord -> new SimpleDateFormat("yyyy-MM-dd").format(safetyInspectionRecord.getTaskTime()).equals(day)).count();
				final long completionCount = total.get().stream().filter(safetyInspectionRecord -> new SimpleDateFormat("yyyy-MM-dd").format(safetyInspectionRecord.getTaskTime()).equals(day) && safetyInspectionRecord.getRecordStatus() == 0).count();
				String task_count = Long.toString(taskCount);
				String completion_count = Long.toString(completionCount);
				statistics.setTaskCount(task_count);
				if (completion_count.equalsIgnoreCase(ZERO)) {
					statistics.setCompletionRate(RATE);
				} else {
					BigDecimal rate = BigDecimal.valueOf(Double.parseDouble(completion_count) / Double.parseDouble(task_count) * 100).setScale(1, BigDecimal.ROUND_HALF_UP);
					statistics.setCompletionRate(rate.toString());
				}
				result.put(day, statistics);
				countDownLatch.countDown();
			}));
		try {
			countDownLatch.await(15, TimeUnit.SECONDS);
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
		threadPoolExecutor.shutdownNow();
		return result.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (o, n) -> o, TreeMap::new));
	}

	public Map<String, DateStatisticsVo> dataProcessSeasonAndYear(String stationId, String siteId, Map<String, DateTimeVo> seasonAndYear) {
		Map<String, DateStatisticsVo> result = new TreeMap<>();
		seasonAndYear.forEach((key, value) -> {
			if (!key.equals("date")) {
				DateStatisticsVo statistics = new DateStatisticsVo();
				String startTime = value.getStartTime();
				String endsTime = value.getEndsTime();
				List<String> days = DateUtil.getDaysBetween(DateUtil.strToDate(startTime, DateUtil.TIME_PATTERN_24), DateUtil.strToDate(endsTime, DateUtil.TIME_PATTERN_24));
				AtomicReference<List<SafetyTask>> total = this.safetyInspectionTotal(stationId, siteId, startTime, endsTime);
				String count = Integer.toString(total.get().size());
				if (count.equals(ZERO)) {
					statistics.setTaskCount(ZERO);
					statistics.setCompletionRate(RATE);
					result.put(key, statistics);
					return;
				}
				AtomicLong completionCount = new AtomicLong(0);
				days.forEach((day) ->
					completionCount.set(completionCount.get() + total.get().stream().filter(safetyInspectionRecord -> new SimpleDateFormat("yyyy-MM-dd").format(safetyInspectionRecord.getTaskTime()).equals(day) && safetyInspectionRecord.getRecordStatus() == 0).count()));
				statistics.setTaskCount(count);
				if (completionCount.get() <= 0) {
					statistics.setCompletionRate(RATE);
				} else {
					statistics.setCompletionRate(ReportForm.rate(count, completionCount.get() + ""));
				}
				result.put(key, statistics);
			}
		});
		return result;
	}

	public AtomicReference<List<SafetyTask>> safetyInspectionTotal(String stationId, String siteId, String startTime, String endsTime) {
		AtomicReference<List<SafetyTask>> total = new AtomicReference<>();
		if (Func.isNotEmpty(siteId)) {
			total.set(safetyInspectionRecordService.list(new QueryWrapper<SafetyTask>().between("task_time", startTime, endsTime).eq("task_type", 1).eq("station_id", stationId).eq("site_id", siteId).eq("is_count", 1)));
		} else {
			total.set(safetyInspectionRecordService.list(new QueryWrapper<SafetyTask>().between("task_time", startTime, endsTime).eq("task_type", 1).eq("station_id", stationId).eq("is_count", 1)));
		}
		return total;
	}

	public R<SecurityCountVo> resultDataProcess(List<SafetyTask> total, Map<String, DateTimeVo> season, String stationId, String siteId, Integer type, String startTime, String endsTime) {
		String totalCount = total.size() + "";
		SecurityCountVo result = new SecurityCountVo();
		if (totalCount.equals(ZERO)) {
			result.setTaskCount(ZERO);
			result.setHiddenCount(ZERO);
			result.setAccomplishCount(ZERO);
			result.setExpiredCount(ZERO);
			result.setScore(ZERO);
			result.setExpirationRate(RATE);
			result.setCompletionRate(RATE);
			result.setPassRate(RATE);
			result.setAccomplishRate(RATE);
		} else {
			result.setTaskCount(totalCount);
			String hiddenCount = Long.toString(total.stream().filter(safetyInspectionRecord -> safetyInspectionRecord.getInspectionStatus() == 1).count());
			String accomplishCount = Long.toString(total.stream().filter(safetyInspectionRecord -> safetyInspectionRecord.getCheckStatus() == 1).count());
			String expiredCount = Long.toString(total.stream().filter(safetyInspectionRecord -> safetyInspectionRecord.getRecordStatus() == 1).count());
			String completionCount = Long.toString(total.stream().filter(safetyInspectionRecord -> safetyInspectionRecord.getCheckStatus() == 1 && safetyInspectionRecord.getRecordStatus() == 0).count());
			String qualifiedCount = Long.toString(total.stream().filter(safetyInspectionRecord -> safetyInspectionRecord.getSatisfaction() >= 60).count());
			result.setHiddenCount(hiddenCount);
			result.setAccomplishCount(accomplishCount);
			result.setExpiredCount(expiredCount);
			result.setScore(averageScore(total.stream().filter(s -> s.getCheckStatus() == 1).collect(Collectors.toCollection(ArrayList::new))));
			result.setExpirationRate(ReportForm.rate(totalCount, expiredCount));
			result.setCompletionRate(ReportForm.rate(totalCount, completionCount));
			result.setPassRate(ReportForm.rate(totalCount, qualifiedCount));
			result.setAccomplishRate(ReportForm.rate(totalCount, accomplishCount));
		}
		if (type == 3 || type == 8) {
			result.setRange(dataProcessSeasonAndYear(stationId, siteId, season));
		} else {
			result.setRange(dataProcessMonth(stationId, siteId, startTime, endsTime));
		}
		List<LocationStatisticsVo> locationStatisticsVos = new CopyOnWriteArrayList<>();
		if (Func.isNotEmpty(siteId)) {
			LocationStatisticsVo locationStatisticsVo = new LocationStatisticsVo();
			locationStatisticsVo.setTaskCount(totalCount);
			locationStatisticsVo.setAccomplishCount(result.getAccomplishCount());
			locationStatisticsVo.setExpiredCount(result.getExpiredCount());
			locationStatisticsVo.setExpirationRate(result.getExpirationRate());
			Site location = siteService.getOne(new QueryWrapper<Site>().eq("id", siteId));
			if (Func.isNotEmpty(location))
				locationStatisticsVo.setSiteName(location.getSiteName());
			locationStatisticsVos.add(locationStatisticsVo);
		} else {
			ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
			List<Site> sites = siteService.list(new QueryWrapper<Site>().eq("station_id", stationId));
			CountDownLatch countDownLatch = new CountDownLatch(sites.size());
			sites.forEach((site) ->
				threadPoolExecutor.execute(() -> {
					LocationStatisticsVo locationStatistics = new LocationStatisticsVo();
					String onSite = site.getId().toString();
					String siteTaskCount = Long.toString(total.stream().filter(safetyInspectionRecord -> safetyInspectionRecord.getSiteId().equals(onSite)).count());
					locationStatistics.setTaskCount(siteTaskCount);
					String siteAccomplishCount = Long.toString(total.stream().filter(safetyInspectionRecord -> safetyInspectionRecord.getCheckStatus() == 1 && safetyInspectionRecord.getSiteId().equals(onSite)).count());
					locationStatistics.setAccomplishCount(siteAccomplishCount);
					String siteExpiredCount = Long.toString(total.stream().filter(safetyInspectionRecord -> safetyInspectionRecord.getRecordStatus() == 1 && safetyInspectionRecord.getSiteId().equals(onSite)).count());
					locationStatistics.setExpiredCount(siteExpiredCount);
					if (siteExpiredCount.equalsIgnoreCase(ZERO)) {
						locationStatistics.setExpirationRate(RATE);
					} else {
						locationStatistics.setExpirationRate(ReportForm.rate(siteTaskCount, siteExpiredCount));
					}
					locationStatistics.setSiteName(site.getSiteName());
					locationStatisticsVos.add(locationStatistics);
					countDownLatch.countDown();
				}));
			try {
				countDownLatch.await(15, TimeUnit.SECONDS);
			} catch (Exception e) {
				throw new ServiceException(e.getMessage());
			}
			threadPoolExecutor.shutdownNow();
		}
		result.setSite(locationStatisticsVos);
		return R.data(result);
	}
}
