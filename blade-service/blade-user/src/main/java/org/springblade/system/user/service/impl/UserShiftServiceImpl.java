package org.springblade.system.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.system.user.entity.User;
import org.springblade.system.user.entity.UserShift;
import org.springblade.system.user.mapper.UserShiftMapper;
import org.springblade.system.user.service.IUserService;
import org.springblade.system.user.service.UserShiftService;
import org.springblade.system.user.vo.UserJoinUserShiftVo;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class UserShiftServiceImpl extends BaseServiceImpl<UserShiftMapper, UserShift> implements UserShiftService {

	IUserService userService;

	@Override
	public R<IPage<UserJoinUserShiftVo>> userShiftList(Query query, String time, Long stationId, Long uid) {

		List<User> list = userService.list(new QueryWrapper<User>().eq("sid", stationId).eq("create_user", uid));
		String[] date = time.split("-");
		int year = Integer.parseInt(date[0]);
		int month = Integer.parseInt(date[1]);
		ThreadPoolExecutor executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
		list.forEach((user) -> {
			Long id = user.getId();
			if (this.count(new QueryWrapper<UserShift>()
				.eq("user_id", id)
				.eq("shift_year", year)
				.eq("shift_month", month)) <= 0) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, year);
				calendar.set(Calendar.MONTH, month - 1);
				int actualMaximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				CountDownLatch countDownLatch = new CountDownLatch(actualMaximum);
				String[] str = new String[actualMaximum];
				executor.execute(() -> {
					synchronized (this) {
						IntStream.range(0, actualMaximum).forEach(i -> {
							Map<String, Object> map = new HashMap<>();
							map.put("id", id);
							map.put("name", user.getRealName());
							map.put("day", i);
							map.put("shift", 0);
							map.put("resRange", 0);
							Calendar instance = Calendar.getInstance();
							instance.set(Calendar.YEAR, year);
							instance.set(Calendar.MONTH, month - 1);
							instance.set(Calendar.DAY_OF_MONTH, i);
							int week = instance.get(Calendar.DAY_OF_WEEK);
							switch (week) {
								case 6:
									map.put("week", "六");
									break;
								case 7:
									map.put("week", "日");
									break;
								default:
									map.put("week", i + 1);
							}
							str[i] = JSONObject.toJSONString(map);
							countDownLatch.countDown();
						});
					}
				});
				try {
					countDownLatch.await(30, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					throw new ServiceException(e.getMessage());
				}
				List<String> cycle = Stream.of(str).collect(Collectors.toList());
				UserShift userShift = new UserShift();
				userShift.setUserId(id);
				userShift.setShiftYear(year);
				userShift.setShiftMonth(month);
				userShift.setShiftCycle(cycle.toString());
				this.save(userShift);
			}
		});
		executor.shutdownNow();
		return R.data(userService.userJoinShiftPage(Condition.getPage(query), uid, year, month));
	}
}
