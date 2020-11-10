package org.springblade.energy.operationmaintenance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.operationmaintenance.entity.Repair;
import org.springblade.energy.operationmaintenance.mapper.RepairMapper;
import org.springblade.energy.operationmaintenance.service.RepairService;
import org.springblade.energy.operationmaintenance.vo.*;
import org.springblade.energy.runningmanagement.station.entity.Site;
import org.springblade.energy.runningmanagement.station.service.ISiteService;
import org.springblade.energy.securitymanagement.util.DataFactory;
import org.springblade.energy.securitymanagement.util.PageQuery;
import org.springblade.energy.securitymanagement.util.PageUtils;
import org.springblade.energy.securitymanagement.util.ReportForm;
import org.springblade.energy.securitymanagement.vo.DateTimeVo;
import org.springblade.util.DateUtil;
import org.springframework.stereotype.Service;

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
 * 维修实现类
 *
 * @author CYL
 * @since 2020-07-23
 */
@Service
@AllArgsConstructor
public class RepairServiceImpl extends BaseServiceImpl<RepairMapper, Repair> implements RepairService {

	static final String ZERO = "0".trim();
	static final String ONE = "1".trim();
	static final String RATE = "0.0".trim();

	ISiteService siteService;

	@Override
	public R<RepairCountVo> repairCount(String stationId, String siteId, String time, Integer type) {
		Map<String, DateTimeVo> season = ReportForm.dateTypeSelect(type, time);
		DateTimeVo date = season.get("date");
		String startTime = date.getStartTime().intern();
		String endsTime = date.getEndsTime().intern();
		AtomicReference<List<Repair>> total = new AtomicReference<>();
		if (Func.isEmpty(siteId)) {
			total.set(this.list(new QueryWrapper<Repair>().eq("station_id", stationId).eq("is_create", ONE).between("dispatch_time", startTime, endsTime)));
		} else {
			total.set(this.list(new QueryWrapper<Repair>().eq("station_id", stationId).eq("is_crate", ONE).eq("site_id", siteId).between("dispatch_time", startTime, endsTime)));
		}
		return this.resultDataProcess(total.get(), season, stationId, siteId, type, startTime, endsTime);
	}

	@Override
	public R<PageUtils> customizePage(PageQuery pageQuery, Repair repair) {
		String siteId = repair.getSiteId();
		String taskStatus = repair.getTaskStatus();
		String assignedPersonId = repair.getAssignedPersonId();
		AtomicBoolean authBool = new AtomicBoolean(true);
		AtomicReference<String> responsiblePersonId = new AtomicReference<>("");
		Long userId = AuthUtil.getUserId();
		String userRole = AuthUtil.getUserRole();
		responsiblePersonId.set(userId.toString());
		if (userRole.trim().equals("admin") || userRole.trim().equals("administrator")) {
			if (Func.isEmpty(assignedPersonId)) {
				authBool.set(false);
			} else {
				responsiblePersonId.set(assignedPersonId);
			}
		}
		if (Func.isEmpty(taskStatus) && Func.isEmpty(siteId))
			return this.repairRecordResult(pageQuery, repair, authBool.get(), responsiblePersonId.get());
		AtomicReference<List<Repair>> repairs = new AtomicReference<>();
		repairs.set(this.list(new QueryWrapper<Repair>().orderByDesc("create_time")
			.eq(authBool.get(), "assigned_person_id", responsiblePersonId.get())
			.eq("is_create", ONE)
			.eq("station_id", repair.getStationId())));
		List<Repair> records = new ArrayList<>();
		if (Func.isNotEmpty(siteId)) {
			Stream.of(repair.getSiteId().split(",")).forEach((s) ->
				Stream.of(repair.getTaskStatus().split(",")).forEach((t) ->
					repairs.get().stream().sorted(Comparator.comparing(Repair::getCreateTime).reversed()).filter(record -> record.getTaskStatus().equals(t) && record.getSiteId().equals(s)).forEach(records::add)));
		}
		return R.data(new PageUtils(records, (long) records.size(), pageQuery.getSize(), pageQuery.getCurrent(), true));
	}

	@Override
	public R<PageUtils> transferOrderPage(PageQuery pageQuery, String dateTime, Long adminId) {
		String[] date = dateTime.substring(0, 10).split("-");
		String[] time = dateTime.substring(11, 19).split(":");
		AtomicReference<Long> user = new AtomicReference<>(0L);
		String[] admin = DataFactory.administratorId(adminId).split(",");
		user.set(Long.parseLong(admin[0]));
		List<TaskAllocationVo> taskAllocationVos = new DataFactory<>().taskAllocation(user.get(), date[0], date[1], date[2], time[0], time[1], time[2], Long.parseLong(admin[1]));
		return R.data(new PageUtils(taskAllocationVos, (long) taskAllocationVos.size(), pageQuery.getSize(), pageQuery.getCurrent(), true));
	}

	public R<PageUtils> repairRecordResult(PageQuery pageQuery, Repair repair, Boolean authBool, String responsiblePersonId) {
		Page<Repair> page = this.page(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new QueryWrapper<>(repair)
			.eq("station_id", repair.getStationId())
			.eq(authBool, "assigned_person_id", responsiblePersonId)
			.eq("is_create", 1)
			.orderByDesc("create_time"));
		return R.data(new PageUtils(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
	}

	public R<RepairCountVo> resultDataProcess(List<Repair> total, Map<String, DateTimeVo> seasonAndYear, String stationId, String siteId, Integer type, String startTime, String endsTime) {
		String totalCount = Integer.toString(total.size());
		RepairCountVo result = new RepairCountVo();
		result.setTaskCount(totalCount);
		if (totalCount.equals(ZERO)) {
			result.setCompleteCount(ZERO);
			result.setAlertCount(ZERO);
			result.setRepairCount(ZERO);
			result.setGeneralCount(ZERO);
			result.setGeneralUntreatedCount(ZERO);
			result.setGeneralProcessedCount(ZERO);
			result.setUrgentCount(ZERO);
			result.setUrgentUntreatedCount(ZERO);
			result.setUrgentProcessedCount(ZERO);
			result.setCompleteCountRate(RATE);
			result.setAlertCountRate(RATE);
			result.setRepairCountRate(RATE);
		} else {
			String completeCount = Long.toString(total.stream().filter(repair -> repair.getTaskStatus().equals(ONE)).count()).intern();
			String alertCount = Long.toString(total.stream().filter(repair -> repair.getType() == 2).count()).intern();
			String repairCount = Long.toString(total.stream().filter(repair -> repair.getType() == 1).count()).intern();
			String generalCount = Long.toString(total.stream().filter(repair -> repair.getHandleLevel() == 1).count()).intern();
			String generalUntreatedCount = Long.toString(total.stream().filter(repair -> repair.getIsHandle() == 0 && repair.getHandleLevel() == 1).count()).intern();
			String generalProcessedCount = Long.toString(total.stream().filter(repair -> repair.getIsHandle() == 1 && repair.getHandleLevel() == 1).count()).intern();
			String urgentCount = Long.toString(total.stream().filter(repair -> repair.getHandleLevel() == 2).count()).intern();
			String urgentUntreatedCount = Long.toString(total.stream().filter(repair -> repair.getIsHandle() == 0 && repair.getHandleLevel() == 2).count()).intern();
			String urgentProcessedCount = Long.toString(total.stream().filter(repair -> repair.getIsHandle() == 1 && repair.getHandleLevel() == 2).count()).intern();
			result.setCompleteCount(completeCount);
			result.setAlertCount(alertCount);
			result.setRepairCount(repairCount);
			result.setGeneralCount(generalCount);
			result.setGeneralUntreatedCount(generalUntreatedCount);
			result.setGeneralProcessedCount(generalProcessedCount);
			result.setUrgentCount(urgentCount);
			result.setUrgentUntreatedCount(urgentUntreatedCount);
			result.setUrgentProcessedCount(urgentProcessedCount);
			result.setCompleteCountRate(ReportForm.rate(totalCount, completeCount));
			result.setAlertCountRate(ReportForm.rate(totalCount, alertCount));
			result.setRepairCountRate(ReportForm.rate(totalCount, repairCount));
		}
		if (type == 3 || type == 8) {
			result.setRange(dataProcessSeasonAndYear(stationId, siteId, seasonAndYear));
		} else {
			result.setRange(dataProcessMonth(stationId, siteId, startTime, endsTime));
		}
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
		List<RepairSiteCountVo> repairSiteCountVos = new CopyOnWriteArrayList<>();
		if (Func.isNotEmpty(siteId)) {
			RepairSiteCountVo repairSiteCountVo = new RepairSiteCountVo();
			repairSiteCountVo.setTaskCount(totalCount);
			if (!totalCount.equals(ZERO)) {
				repairSiteCountVo.setCompleteCount(result.getCompleteCount());
				String undoneCount = Long.toString(total.stream().filter(repair -> repair.getTaskStatus().equals(ZERO)).count()).intern();
				repairSiteCountVo.setUndoneCount(undoneCount);
				repairSiteCountVo.setCompleteCountRate(result.getCompleteCountRate());
				repairSiteCountVo.setUndoneCountRate(ReportForm.rate(totalCount, undoneCount));
				repairSiteCountVo.setTimelyCompleteRate(ReportForm.rate(totalCount, Long.toString(total.stream().filter(repair -> repair.getTaskStatus().equals(ZERO) && repair.getIsExpired() == 0).count())));
				this.repairSiteDetail(total, repairSiteCountVo, totalCount);
			}
			this.repairCheck(total, repairSiteCountVo, totalCount);
			Site location = siteService.getOne(new QueryWrapper<Site>().eq("id", siteId));
			if (Func.isNotEmpty(location))
				repairSiteCountVo.setSiteName(location.getSiteName());
			repairSiteCountVos.add(repairSiteCountVo);
		} else {
			List<Site> sites = siteService.list(new QueryWrapper<Site>().eq("station_id", stationId));
			CountDownLatch countDownLatch = new CountDownLatch(sites.size());
			sites.forEach((site) ->
				threadPoolExecutor.execute(() -> {
					RepairSiteCountVo repairSiteCountVo = new RepairSiteCountVo();
					String onSite = Long.toString(site.getId());
					String siteTaskCount = Long.toString(total.stream().filter(repair -> repair.getSiteId().equals(onSite)).count());
					repairSiteCountVo.setTaskCount(siteTaskCount);
					if (!siteTaskCount.equals(ZERO)) {
						String siteAccomplishCount = Long.toString(total.stream().filter(repair -> repair.getTaskStatus().equals(ONE) && repair.getSiteId().equals(onSite)).count());
						repairSiteCountVo.setCompleteCount(siteAccomplishCount);
						String siteUndoneCount = Long.toString(total.stream().filter(repair -> repair.getTaskStatus().equals(ZERO) && repair.getSiteId().equals(onSite)).count());
						repairSiteCountVo.setUndoneCount(siteUndoneCount);
						String siteTimelyCompleteCount = Long.toString(total.stream().filter(repair -> repair.getTaskStatus().equals(ONE) && repair.getIsExpired() == 0 && repair.getSiteId().equals(onSite)).count());
						repairSiteCountVo.setTimelyCompleteRate(siteTimelyCompleteCount);
						repairSiteCountVo.setCompleteCountRate(ReportForm.rate(siteTaskCount, siteAccomplishCount));
						repairSiteCountVo.setUndoneCountRate(ReportForm.rate(siteTaskCount, siteUndoneCount));
						this.repairSiteDetail(total, repairSiteCountVo, totalCount);
					}
					this.repairCheck(total, repairSiteCountVo, siteTaskCount);
					repairSiteCountVo.setSiteName(site.getSiteName());
					repairSiteCountVos.add(repairSiteCountVo);
					countDownLatch.countDown();
				}));
			try {
				countDownLatch.await(15, TimeUnit.SECONDS);
			} catch (Exception e) {
				throw new Error(e.getMessage());
			}
		}
		result.setSite(repairSiteCountVos);
		threadPoolExecutor.shutdownNow();
		return R.data(result);
	}

	public Map<String, RepairDateCountVo> dataProcessSeasonAndYear(String stationId, String siteId, Map<String, DateTimeVo> seasonAndYear) {
		Map<String, RepairDateCountVo> result = new TreeMap<>();
		seasonAndYear.forEach((key, value) -> {
			if (!key.equals("date")) {
				RepairDateCountVo repairDateCountVo = new RepairDateCountVo();
				String startTime = value.getStartTime().intern();
				String endsTime = value.getEndsTime().intern();
				List<String> days = DateUtil.getDaysBetween(DateUtil.strToDate(startTime, DateUtil.TIME_PATTERN_24), DateUtil.strToDate(endsTime, DateUtil.TIME_PATTERN_24));
				List<Repair> total = this.repairTotal(stationId, siteId, startTime, endsTime);
				String taskCount = Integer.toString(total.size()).intern();
				repairDateCountVo.setTaskCount(taskCount);
				if (taskCount.equals(ZERO)) {
					repairDateCountVo.setCompleteCount(ZERO);
					repairDateCountVo.setUndoneCount(ZERO);
					result.put(key, repairDateCountVo);
					return;
				}
				AtomicLong completionCount = new AtomicLong(0);
				AtomicLong undoneCount = new AtomicLong(0);
				days.forEach((day) -> {
					completionCount.addAndGet(total.stream().filter(repair -> new SimpleDateFormat("yyyy-MM-dd").format(repair.getDispatchTime()).equals(day) && repair.getTaskStatus().equals(ONE)).count());
					undoneCount.addAndGet(total.stream().filter(repair -> new SimpleDateFormat("yyyy-MM-dd").format(repair.getDispatchTime()).equals(day) && repair.getTaskStatus().equals(ZERO)).count());
				});
				result.put(key, repairDateCountVo);
			}
		});
		return result;
	}

	public Map<String, RepairDateCountVo> dataProcessMonth(String stationId, String siteId, String startTime, String endsTime) {
		List<String> days = DateUtil.getDaysBetween(DateUtil.strToDate(startTime, DateUtil.TIME_PATTERN_24), DateUtil.strToDate(endsTime, DateUtil.TIME_PATTERN_24));
		ConcurrentMap<String, RepairDateCountVo> result = new ConcurrentHashMap<>();
		List<Repair> total = this.repairTotal(stationId, siteId, startTime, endsTime);
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
		CountDownLatch countDownLatch = new CountDownLatch(days.size());
		days.forEach((day) ->
			threadPoolExecutor.execute(() -> {
				RepairDateCountVo repairDateCountVo = new RepairDateCountVo();
				final long taskCount = total.stream().filter(repair -> new SimpleDateFormat("yyyy-MM-dd").format(repair.getDispatchTime()).equals(day)).count();
				final long completionCount = total.stream().filter(repair -> new SimpleDateFormat("yyyy-MM-dd").format(repair.getDispatchTime()).equals(day) && repair.getTaskStatus().equals(ONE)).count();
				final long undoneCount = total.stream().filter(repair -> new SimpleDateFormat("yyyy-MM-dd").format(repair.getDispatchTime()).equals(day) && repair.getTaskStatus().equals(ZERO)).count();
				repairDateCountVo.setTaskCount(Long.toString(taskCount));
				repairDateCountVo.setCompleteCount(Long.toString(completionCount));
				repairDateCountVo.setUndoneCount(Long.toString(undoneCount));
				result.put(day, repairDateCountVo);
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

	public List<Repair> repairTotal(String stationId, String siteId, String startTime, String endsTime) {
		AtomicReference<List<Repair>> total = new AtomicReference<>();
		if (Func.isNotEmpty(siteId)) {
			total.set(this.list(new QueryWrapper<Repair>().between("dispatch_time", startTime, endsTime).eq("is_create", ONE).eq("station_id", stationId).eq("site_id", siteId)));
		} else {
			total.set(this.list(new QueryWrapper<Repair>().between("dispatch_time", startTime, endsTime).eq("is_create", ONE).eq("station_id", stationId)));
		}
		return total.get();
	}

	public void repairSiteDetail(List<Repair> total, RepairSiteCountVo repairSiteCountVo, String totalCount) {
		if (totalCount.equals(ZERO)) {
			repairSiteCountVo.setStandardWorkHours(ZERO);
			repairSiteCountVo.setActualWorkHours(ZERO);
		} else {
			/* 标准工时 */
			String standardWorkHours = Long.toString(total.stream().mapToInt(Repair::getFinishWorkTime).sum()).intern();

			AtomicInteger sum = new AtomicInteger(0);
			try {
				total.forEach(repair -> {
					if (repair.getTaskStatus().equals(ONE)) {
						if (Func.isNotEmpty(repair.getStartTime()) && Func.isNotEmpty(repair.getRestoreTime())) {
							String startTime = new SimpleDateFormat("HHmm").format(repair.getStartTime()).intern();
							String endsTime = new SimpleDateFormat("HHmm").format(repair.getRestoreTime()).intern();
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
				repairSiteCountVo.setStandardWorkHours(standardWorkHours);
				if (sum.get() >= 60) {
					String hours = Integer.toString(sum.get() / 60).intern();
					String minute = Integer.toString(sum.get() - (Integer.parseInt(hours) * 60)).intern();
					repairSiteCountVo.setActualWorkHours(hours + "时" + minute + "分");
				} else if (sum.get() > 0 && sum.get() < 60) {
					repairSiteCountVo.setActualWorkHours(sum.get() + "分");
				} else if (sum.get() == 0) {
					repairSiteCountVo.setActualWorkHours(ZERO);
				}
			} catch (Exception e) {
				throw new ServiceException(e.getMessage());
			}
		}
	}

	public void repairCheck(List<Repair> total, RepairSiteCountVo repairSiteCountVo, String totalCount) {
		if (totalCount.equals(ZERO)) {
			repairSiteCountVo.setCompleteCount(ZERO);
			repairSiteCountVo.setUndoneCount(ZERO);
			repairSiteCountVo.setCompleteCountRate(RATE);
			repairSiteCountVo.setUndoneCountRate(RATE);
			repairSiteCountVo.setTimelyCompleteRate(RATE);
			repairSiteCountVo.setStandardWorkHours(ZERO);
			repairSiteCountVo.setActualWorkHours(ZERO);

			repairSiteCountVo.setPassCount(ZERO);
			repairSiteCountVo.setFailCount(ZERO);
			repairSiteCountVo.setDifferCount(ZERO);
			repairSiteCountVo.setGeneralCount(ZERO);
			repairSiteCountVo.setSatisfactionCount(ZERO);
			repairSiteCountVo.setVerySatisfactionCount(ZERO);
		} else {
			String passCount = Long.toString(total.stream().filter(repair -> repair.getTaskStatus().equals(ONE) && repair.getCheckAcceptQuality() == 0 && repair.getCheckStatus() == 1).count()).intern();
			repairSiteCountVo.setPassCount(passCount);
			String failCount = Long.toString(total.stream().filter(repair -> repair.getTaskStatus().equals(ONE) && repair.getCheckAcceptQuality() == 1 && repair.getCheckStatus() == 1).count()).intern();
			repairSiteCountVo.setFailCount(failCount);
			String differCount = Long.toString(total.stream().filter(repair -> repair.getTaskStatus().equals(ONE) && repair.getSatisfaction() == 50 && repair.getCheckStatus() == 1).count()).intern();
			repairSiteCountVo.setDifferCount(differCount);
			String generalCount = Long.toString(total.stream().filter(repair -> repair.getTaskStatus().equals(ONE) && repair.getSatisfaction() == 60 && repair.getCheckStatus() == 1).count()).intern();
			repairSiteCountVo.setGeneralCount(generalCount);
			String satisfactionCount = Long.toString(total.stream().filter(repair -> repair.getTaskStatus().equals(ONE) && repair.getSatisfaction() == 80 && repair.getCheckStatus() == 1).count()).intern();
			repairSiteCountVo.setSatisfactionCount(satisfactionCount);
			String verySatisfactionCount = Long.toString(total.stream().filter(repair -> repair.getTaskStatus().endsWith(ONE) && repair.getSatisfaction() == 100 && repair.getCheckStatus() == 1).count()).intern();
			repairSiteCountVo.setVerySatisfactionCount(verySatisfactionCount);
		}
	}
}
