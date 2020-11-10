package org.springblade.energy.operationmaintenance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.operationmaintenance.entity.InspectionTask;
import org.springblade.energy.operationmaintenance.mapper.InspectionTaskMapper;
import org.springblade.energy.operationmaintenance.service.InspectionTaskService;
import org.springblade.energy.operationmaintenance.vo.*;
import org.springblade.energy.runningmanagement.station.entity.Site;
import org.springblade.energy.runningmanagement.station.service.ISiteService;
import org.springblade.energy.securitymanagement.util.PageQuery;
import org.springblade.energy.securitymanagement.util.PageUtils;
import org.springblade.energy.securitymanagement.util.ReportForm;
import org.springblade.energy.securitymanagement.vo.DateTimeVo;
import org.springblade.util.DateUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 服务实现类
 *
 * @author CYL
 * @since 2020-08-05
 */
@Service
@AllArgsConstructor
public class InspectionTaskServiceImpl extends BaseServiceImpl<InspectionTaskMapper, InspectionTask> implements InspectionTaskService {

	ISiteService siteService;

	static final String ZERO = "0".trim();
	static final String RATE = "0.0".trim();
	static final String NORMAL = "正常".trim();
	static final String ABNORMAL = "异常".trim();
	static final String NOT_INSPECTION = "未巡".trim();
	static final String PROCESSING = "进行中".trim();


	@Override
	public R<PageUtils> customizePage(PageQuery pageQuery, InspectionTask inspectionRecord) {
		Integer inspectionType = inspectionRecord.getInspectionType();
		Long assignedPersonId = inspectionRecord.getAssignedPersonId();
		AtomicBoolean sourceBool = new AtomicBoolean(false);
		if (Func.isNotEmpty(inspectionRecord.getSourceType())) {
			sourceBool.set(true);
		}
		AtomicBoolean inspectionTypeBool = new AtomicBoolean(true);
		String taskStatus = inspectionRecord.getTaskStatus();
		String siteId = inspectionRecord.getSiteId();
		AtomicBoolean authBool = new AtomicBoolean(true);
		AtomicReference<String> responsiblePersonId = new AtomicReference<>("");
		Long userId = AuthUtil.getUserId();
		String userRole = AuthUtil.getUserRole();
		responsiblePersonId.set(userId.toString());
		if (Func.isEmpty(inspectionType)) {
			inspectionTypeBool.set(false);
		}
		if (userRole.trim().equals("admin") || userRole.trim().equals("administrator")) {
			if (Func.isEmpty(assignedPersonId)) {
				authBool.set(false);
			} else {
				responsiblePersonId.set(assignedPersonId.toString());
			}
		}
		if (Func.isEmpty(taskStatus) && Func.isEmpty(siteId))
			return this.getListSafetyInspectionRecordResult(pageQuery, inspectionRecord, inspectionTypeBool.get(), authBool.get(), inspectionType, responsiblePersonId.get(), sourceBool.get());
		AtomicReference<List<InspectionTask>> inspectionRecords = new AtomicReference<>();
		inspectionRecords.set(this.list(new QueryWrapper<InspectionTask>().orderByDesc("create_time")
			.eq(authBool.get(), "assigned_person_id", responsiblePersonId.get())
			.eq("station_id", inspectionRecord.getStationId())
			.eq(inspectionTypeBool.get(), "task_type", inspectionType)
			.eq(sourceBool.get(), "source_type", inspectionRecord.getSourceType())));
		List<InspectionTask> records = new ArrayList<>();
		if (Func.isNotEmpty(siteId)) {
			Stream.of(inspectionRecord.getSiteId().split(",")).forEach((s) ->
				Stream.of(inspectionRecord.getTaskStatus().split(",")).forEach((t) ->
					inspectionRecords.get().stream().sorted(Comparator.comparing(InspectionTask::getCreateTime).reversed()).filter(record -> record.getTaskStatus().equals(t) && record.getSiteId().equals(s)).forEach(records::add)));
		}
		return R.data(new PageUtils(records, (long) records.size(), pageQuery.getSize(), pageQuery.getCurrent(), true));
	}

	@Override
	public InspectionRouteStatusVo inspectionRouteStatus(Long id, String yearMonthDay) {
		List<InspectionTask> inspectionRecords = this.list(new QueryWrapper<InspectionTask>().eq("assigned_person_id", id).between("task_time", yearMonthDay.trim() + " 00:00:00", yearMonthDay.trim() + " 23:59:59").orderByAsc("task_time"));
		InspectionRouteStatusVo inspectionRouteStatusVo = new InspectionRouteStatusVo();
		List<InspectionRouteVo> inspectionRouteVos = new ArrayList<>();
		String[] controller = new String[inspectionRecords.size()];
		AtomicInteger count = new AtomicInteger(0);
		inspectionRecords.stream().distinct().forEach(record -> {
			InspectionRouteVo inspectionRouteVo = new InspectionRouteVo();
			String taskStatus = record.getTaskStatus();
			if (taskStatus.equals(ZERO)) {
				inspectionRouteVo.setInspectionStatus(NOT_INSPECTION);
				inspectionRouteVo.setCode(ZERO);
				controller[count.get()] = NOT_INSPECTION;
			}
			if (!taskStatus.equals(ZERO) && !taskStatus.equals("3")) {
				inspectionRouteVo.setStartTime(record.getActualStartTime());
				inspectionRouteVo.setInspectionStatus(PROCESSING);
				inspectionRouteVo.setCode("1");
				controller[count.get()] = PROCESSING;
			}
			inspectionRouteVo.setSiteName(record.getSiteName());
			if (taskStatus.equals("3")) {
				switch (record.getIsAbnormal()) {
					case 0:
						inspectionRouteVo.setInspectionStatus(NORMAL);
						inspectionRouteVo.setCode("2");
						controller[count.get()] = NORMAL;
						break;
					case 1:
						inspectionRouteVo.setInspectionStatus(ABNORMAL);
						inspectionRouteVo.setCode("3");
						controller[count.get()] = ABNORMAL;
						break;
				}
				Date startTime = record.getActualStartTime();
				Date endTime = record.getActualEndTime();
				inspectionRouteVo.setStartTime(startTime);
				inspectionRouteVo.setEndsTime(endTime);
				BigDecimal expired = BigDecimal.valueOf((endTime.getTime() - startTime.getTime()) / 60000).setScale(0, BigDecimal.ROUND_HALF_UP);
				String firstTime = new SimpleDateFormat("yyyyMMddHHmmss").format(startTime).substring(10, 14);
				String lastTime = new SimpleDateFormat("yyyyMMddHHmmss").format(endTime).substring(10, 14);
				AtomicInteger second = new AtomicInteger(0);
				String firstSecond = firstTime.substring(2, 4).intern();
				String lastSecond = lastTime.substring(2, 4).intern();
				int remainderSecond = Integer.parseInt(lastSecond) - Integer.parseInt(firstSecond);
				if (remainderSecond < 0) {
					second.set(remainderSecond + 60);
				}
				if (remainderSecond >= 0) {
					second.set(remainderSecond);
				}
				inspectionRouteVo.setTimeConsuming(expired.toString() + "分" + second.get() + "秒");
			}
			inspectionRouteVos.add(inspectionRouteVo);
			count.getAndIncrement();
		});
		inspectionRouteStatusVo.setInspectionRouteVos(inspectionRouteVos);
		StringBuilder splice = new StringBuilder();
		for (String content : controller) {
			splice.append(content.trim());
		}
		String status = splice.toString();
		if (status.contains(NOT_INSPECTION) && !status.contains(PROCESSING)) {
			inspectionRouteStatusVo.setStatus(NOT_INSPECTION);
		}
		if (status.contains(PROCESSING)) {
			inspectionRouteStatusVo.setStatus(PROCESSING);
		}
		if (status.contains(ABNORMAL) && !status.contains(PROCESSING) && !status.contains(NOT_INSPECTION)) {
			inspectionRouteStatusVo.setStatus(ABNORMAL);
		}
		if (!status.contains(ABNORMAL) && !status.contains(PROCESSING) && !status.contains(NOT_INSPECTION) && status.contains(NORMAL)) {
			inspectionRouteStatusVo.setStatus(NORMAL);
		}
		return inspectionRouteStatusVo;
	}

	@Override
	public R<InspectionCountVo> inspectionCount(String stationId, String siteId, String time, Integer type, Integer taskType) {
		Map<String, DateTimeVo> season = ReportForm.dateTypeSelect(type, time);
		DateTimeVo date = season.get("date");
		String startTime = date.getStartTime();
		String endsTime = date.getEndsTime();
		AtomicReference<List<InspectionTask>> total = new AtomicReference<>();
		if (Func.isEmpty(siteId)) {
			total.set(this.list(new QueryWrapper<InspectionTask>().eq("station_id", stationId).between("task_time", startTime, endsTime).eq("task_type", taskType).eq("is_count", 1)));
		} else {
			total.set(this.list(new QueryWrapper<InspectionTask>().eq("station_id", stationId).eq("site_id", siteId).between("task_time", startTime, endsTime).eq("task_type", taskType).eq("is_count", 1)));
		}
		return this.resultDataProcess(total.get(), season, stationId, siteId, type, startTime, endsTime, taskType);
	}

	public R<PageUtils> getListSafetyInspectionRecordResult(PageQuery pageQuery, InspectionTask inspectionRecord, Boolean inspectionTypeBool, Boolean authBool, Integer taskType, String responsiblePersonId, Boolean sourceBool) {
		Page<InspectionTask> page = this.page(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new QueryWrapper<>(inspectionRecord)
			.eq(inspectionTypeBool, "task_type", taskType)
			.eq("station_id", inspectionRecord.getStationId())
			.eq(authBool, "assigned_person_id", responsiblePersonId)
			.eq(sourceBool, "source_type", inspectionRecord.getSourceType())
			.orderByDesc("create_time"));
		return R.data(new PageUtils(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
	}

	public R<InspectionCountVo> resultDataProcess(List<InspectionTask> total, Map<String, DateTimeVo> seasonAndYear, String stationId, String siteId, Integer type, String startTime, String endsTime, Integer taskType) {
		String totalCount = Integer.toString(total.size());
		InspectionCountVo result = new InspectionCountVo();
		/* 任务总数 */
		result.setTaskCount(totalCount);
		if (totalCount.equals(ZERO)) {
			result.setCompleteCount(ZERO);
			result.setExpiredCompleteCount(ZERO);
			result.setExpiredCount(ZERO);
			result.setInspectionItemCount(ZERO);
			result.setRepairItemCount(ZERO);
			result.setCompleteCountRate(RATE);
			result.setExpiredCountRate(RATE);
		} else {
			/* 任务完成总数 */
			String completeCount = Long.toString(total.stream().filter(inspectionRecord -> inspectionRecord.getAccomplish() == 0).count());
			/* 过期完成总数 */
			String expiredCompleteCount = Long.toString(total.stream().filter(inspectionRecord -> inspectionRecord.getAccomplish() == 0 && inspectionRecord.getRecordStatus() == 1).count());
			/* 过期总数 */
			String expiredCount = Long.toString(total.stream().filter(inspectionRecord -> inspectionRecord.getAccomplish() == 1 && inspectionRecord.getRecordStatus() == 1).count());
			/* 任务项总数 */
			String taskItemCount = Integer.toString(total.stream().mapToInt(InspectionTask::getInspectionItemCount).sum());
			/* 任务项报修总数 */
			String taskItemRepairCount = Integer.toString(total.stream().mapToInt(InspectionTask::getRepairNumber).sum());

			result.setCompleteCount(completeCount);
			result.setExpiredCompleteCount(expiredCompleteCount);
			result.setExpiredCount(expiredCount);
			result.setInspectionItemCount(taskItemCount);
			result.setRepairItemCount(taskItemRepairCount);

			/* 任务完成率 */
			result.setCompleteCountRate(ReportForm.rate(totalCount, completeCount));
			/* 任务过期率 */
			result.setExpiredCountRate(ReportForm.rate(totalCount, expiredCount));
		}
		if (type == 3 || type == 8) {
			result.setRange(dataProcessSeasonAndYear(stationId, siteId, seasonAndYear, taskType));
		} else {
			result.setRange(dataProcessMonth(stationId, siteId, startTime, endsTime, taskType));
		}
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
		List<InspectionSiteCountVo> inspectionSiteCountVos = new CopyOnWriteArrayList<>();
		if (Func.isNotEmpty(siteId)) {
			InspectionSiteCountVo inspectionSiteCountVo = new InspectionSiteCountVo();
			inspectionSiteCountVo.setTaskCount(result.getTaskCount());
			inspectionSiteCountVo.setExpiredCount(result.getExpiredCount());
			inspectionSiteCountVo.setCompleteCount(result.getCompleteCount());
			inspectionSiteCountVo.setCompleteCountRate(result.getCompleteCountRate());
			inspectionSiteCountVo.setExpiredCountRate(result.getExpiredCountRate());
			this.inspectionSiteDetail(total, inspectionSiteCountVo, totalCount);

			Site location = siteService.getOne(new QueryWrapper<Site>().eq("id", siteId));
			if (Func.isNotEmpty(location))
				inspectionSiteCountVo.setSiteName(location.getSiteName());
			inspectionSiteCountVos.add(inspectionSiteCountVo);
		} else {
			List<Site> sites = siteService.list(new QueryWrapper<Site>().eq("station_id", stationId));
			CountDownLatch countDownLatch = new CountDownLatch(sites.size());
			sites.forEach((site) ->
				threadPoolExecutor.execute(() -> {
					InspectionSiteCountVo inspectionSiteCount = new InspectionSiteCountVo();
					String onSite = site.getId().toString();
					String siteTaskCount = Long.toString(total.stream().filter(inspectionRecord -> inspectionRecord.getSiteId().equals(onSite)).count());
					inspectionSiteCount.setTaskCount(siteTaskCount);
					if (siteTaskCount.equals(ZERO)) {
						inspectionSiteCount.setCompleteCount(ZERO);
						inspectionSiteCount.setExpiredCount(ZERO);
						inspectionSiteCount.setCompleteCountRate(RATE);
						inspectionSiteCount.setExpiredCountRate(RATE);
						inspectionSiteCount.setAbnormalRate(RATE);
						inspectionSiteCount.setStandardWorkHours(ZERO);
						inspectionSiteCount.setActualWorkHours(ZERO);
						inspectionSiteCount.setTimelyCompleteRate(RATE);
					} else {
						String siteAccomplishCount = Long.toString(total.stream().filter(inspectionRecord -> inspectionRecord.getAccomplish() == 0 && inspectionRecord.getSiteId().equals(onSite)).count());
						inspectionSiteCount.setCompleteCount(siteAccomplishCount);
						String siteExpiredCount = Long.toString(total.stream().filter(inspectionRecord -> inspectionRecord.getRecordStatus() == 1 && inspectionRecord.getAccomplish() == 1 && inspectionRecord.getSiteId().equals(onSite)).count());
						String siteTimelyCompleteCount = Long.toString(total.stream().filter(inspectionRecord -> inspectionRecord.getRecordStatus() == 0 && inspectionRecord.getAccomplish() == 0 && inspectionRecord.getSiteId().equals(onSite)).count());
						inspectionSiteCount.setTimelyCompleteRate(siteTimelyCompleteCount);
						inspectionSiteCount.setExpiredCount(siteExpiredCount);
						inspectionSiteCount.setCompleteCountRate(ReportForm.rate(siteTaskCount, siteAccomplishCount));
						inspectionSiteCount.setExpiredCountRate(ReportForm.rate(siteTaskCount, siteExpiredCount));
						this.inspectionSiteDetail(total, inspectionSiteCount, totalCount);
					}
					inspectionSiteCount.setSiteName(site.getSiteName());
					inspectionSiteCountVos.add(inspectionSiteCount);
					countDownLatch.countDown();
				}));
			try {
				countDownLatch.await(15, TimeUnit.SECONDS);
			} catch (Exception e) {
				throw new Error(e.getMessage());
			}
		}
		result.setSite(inspectionSiteCountVos);
		threadPoolExecutor.shutdownNow();
		return R.data(result);
	}

	public Map<String, InspectionDateCountVo> dataProcessSeasonAndYear(String stationId, String siteId, Map<String, DateTimeVo> seasonAndYear, Integer taskType) {
		Map<String, InspectionDateCountVo> result = new TreeMap<>();
		seasonAndYear.forEach((key, value) -> {
			if (!key.equals("date")) {
				InspectionDateCountVo inspectionDateCount = new InspectionDateCountVo();
				String startTime = value.getStartTime();
				String endsTime = value.getEndsTime();
				List<String> days = DateUtil.getDaysBetween(DateUtil.strToDate(startTime, DateUtil.TIME_PATTERN_24), DateUtil.strToDate(endsTime, DateUtil.TIME_PATTERN_24));
				List<InspectionTask> total = this.inspectionRecordTotal(stationId, siteId, startTime, endsTime, taskType);
				String count = Integer.toString(total.size());
				if (count.equals(ZERO)) {
					inspectionDateCount.setTaskCount(ZERO);
					inspectionDateCount.setCompleteCountRate(RATE);
					result.put(key, inspectionDateCount);
					return;
				}
				AtomicLong completionCount = new AtomicLong(0);
				days.forEach((day) ->
					completionCount.addAndGet(total.stream().filter(inspectionRecord -> new SimpleDateFormat("yyyy-MM-dd").format(inspectionRecord.getTaskTime()).equals(day) && inspectionRecord.getAccomplish() == 0).count()));
				inspectionDateCount.setTaskCount(count);
				if (completionCount.get() <= 0) {
					inspectionDateCount.setCompleteCountRate(RATE);
				} else {
					BigDecimal rate = BigDecimal.valueOf(Double.parseDouble(completionCount.get() + "") / Double.parseDouble(count) * 100).setScale(1, BigDecimal.ROUND_HALF_UP);
					inspectionDateCount.setCompleteCountRate(rate.toString());
				}
				result.put(key, inspectionDateCount);
			}
		});
		return result;
	}

	public Map<String, InspectionDateCountVo> dataProcessMonth(String stationId, String siteId, String startTime, String endsTime, Integer taskType) {
		List<String> days = DateUtil.getDaysBetween(DateUtil.strToDate(startTime, DateUtil.TIME_PATTERN_24), DateUtil.strToDate(endsTime, DateUtil.TIME_PATTERN_24));
		ConcurrentMap<String, InspectionDateCountVo> result = new ConcurrentHashMap<>();
		List<InspectionTask> total = this.inspectionRecordTotal(stationId, siteId, startTime, endsTime, taskType);
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
		CountDownLatch countDownLatch = new CountDownLatch(days.size());
		days.forEach((day) ->
			threadPoolExecutor.execute(() -> {
				InspectionDateCountVo inspectionDateCount = new InspectionDateCountVo();
				final long taskCount = total.stream().filter(inspectionRecord -> new SimpleDateFormat("yyyy-MM-dd").format(inspectionRecord.getTaskTime()).equals(day)).count();
				final long completionCount = total.stream().filter(inspectionRecord -> new SimpleDateFormat("yyyy-MM-dd").format(inspectionRecord.getTaskTime()).equals(day) && inspectionRecord.getAccomplish() == 0).count();
				String task_count = Long.toString(taskCount);
				String completion_count = Long.toString(completionCount);
				inspectionDateCount.setTaskCount(task_count);
				if (completion_count.equals(ZERO)) {
					inspectionDateCount.setCompleteCountRate(RATE);
				} else {
					inspectionDateCount.setCompleteCountRate(ReportForm.rate(task_count, completion_count));
				}
				result.put(day, inspectionDateCount);
				countDownLatch.countDown();
			}));
		try {
			countDownLatch.await(15, TimeUnit.SECONDS);
		} catch (Exception e) {
			throw new Error(e.getMessage());
		}
		threadPoolExecutor.shutdownNow();
		return result.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (o, n) -> o, TreeMap::new));
	}

	public List<InspectionTask> inspectionRecordTotal(String stationId, String siteId, String startTime, String endsTime, Integer taskType) {
		AtomicReference<List<InspectionTask>> total = new AtomicReference<>();
		if (Func.isNotEmpty(siteId)) {
			total.set(this.list(new QueryWrapper<InspectionTask>().between("task_time", startTime, endsTime).eq("task_type", taskType).eq("station_id", stationId).eq("site_id", siteId).eq("is_count", 1)));
		} else {
			total.set(this.list(new QueryWrapper<InspectionTask>().between("task_time", startTime, endsTime).eq("task_type", taskType).eq("station_id", stationId).eq("is_count", 1)));
		}
		return total.get();
	}

	public void inspectionSiteDetail(List<InspectionTask> total, InspectionSiteCountVo inspectionSiteCountVo, String totalCount) {
		if (totalCount.equals(ZERO)) {
			inspectionSiteCountVo.setAbnormalRate(RATE);
			inspectionSiteCountVo.setStandardWorkHours(ZERO);
			inspectionSiteCountVo.setActualWorkHours(ZERO);
		} else {
			/* 异常率  */
			String abnormalCount = Long.toString(total.stream().filter(inspectionRecord -> inspectionRecord.getIsAbnormal() == 1).count());
			/* 标准工时 */
			String standardWorkHours = Integer.toString(total.stream().mapToInt(InspectionTask::getWorkHours).sum());

			AtomicInteger sum = new AtomicInteger(0);
			try {
				total.forEach(inspectionRecord -> {
					if (inspectionRecord.getAccomplish() == 0) {
						if (Func.isNotEmpty(inspectionRecord.getActualStartTime()) && Func.isNotEmpty(inspectionRecord.getActualEndTime())) {
							String startTime = new SimpleDateFormat("HHmm").format(inspectionRecord.getActualStartTime()).intern();
							String endsTime = new SimpleDateFormat("HHmm").format(inspectionRecord.getActualEndTime()).intern();
							String startHours = startTime.substring(0, 2).intern();
							String endsHours = endsTime.substring(0, 2).intern();
							String startMinute = startTime.substring(2, 4).intern();
							String endsMinute = endsTime.substring(2, 4).intern();
							sum.addAndGet((((Integer.parseInt(endsHours) - Integer.parseInt(startHours)) * 60) + Integer.parseInt(endsMinute)) - Integer.parseInt(startMinute));
						} else {
							sum.set(0);
						}
					}
				});
				inspectionSiteCountVo.setAbnormalRate(ReportForm.rate(totalCount, abnormalCount));
				inspectionSiteCountVo.setStandardWorkHours(standardWorkHours);
				if (sum.get() >= 60) {
					String hours = Integer.toString(sum.get() / 60).intern();
					String minute = Integer.toString(sum.get() - (Integer.parseInt(hours) * 60)).intern();
					inspectionSiteCountVo.setActualWorkHours(hours + "时" + minute + "分");
				} else if (sum.get() > 0 && sum.get() < 60) {
					inspectionSiteCountVo.setActualWorkHours(sum.get() + "分");
				} else if (sum.get() == 0) {
					inspectionSiteCountVo.setActualWorkHours(ZERO);
				}
			} catch (Exception e) {
				throw new ServiceException(e.getMessage());
			}
		}
	}
}
