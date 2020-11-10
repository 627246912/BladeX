package org.springblade.energy.securitymanagement.util;

import org.springblade.energy.securitymanagement.vo.DateTimeVo;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ReportForm {

	public static Map<String, DateTimeVo> dateTypeSelect(Integer type, String time) {
		AtomicReference<String> startTime = new AtomicReference<>("");
		AtomicReference<String> endsTime = new AtomicReference<>("");
		Map<String, DateTimeVo> result = new TreeMap<>();
		Calendar calendar = Calendar.getInstance();
		String[] date = time.split("-");
		final int year = Integer.parseInt(date[0]);
		AtomicInteger month = new AtomicInteger(Integer.parseInt(date[1]));
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month.get() - 1);
		final int day = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

		switch (type) {
			/* 月*/
			case 2:
				if (month.get() < 10) {
					startTime.set(year + "-0" + month + "-01" + " 00:00:00");
					endsTime.set(year + "-0" + month + "-" + day + " 23:59:59");
				} else {
					startTime.set(year + "-" + month + "-01" + " 00:00:00");
					endsTime.set(year + "-" + month + "-" + day + " 23:59:59");
				}
				break;
			/* 年*/
			case 3:
				startTime.set(year + "-01-01 00:00:00");
				endsTime.set(year + "-12-31 23:59:59");
				month.set(2);

				DateTimeVo oneMonth = new DateTimeVo();
				oneMonth.setStartTime(year + "-01-01 00:00:00");
				oneMonth.setEndsTime(year + "-01-31 23:59:59");
				result.put("1", oneMonth);

				DateTimeVo twoMonth = new DateTimeVo();
				twoMonth.setStartTime(year + "-02-01 00:00:00");
				twoMonth.setEndsTime(year + "-02-" + calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + " 23:59:59");
				result.put("2", twoMonth);

				DateTimeVo threeMonth = new DateTimeVo();
				threeMonth.setStartTime(year + "-03-01 00:00:00");
				threeMonth.setEndsTime(year + "-03-31 23:59:59");
				result.put("3", threeMonth);

				DateTimeVo fourMonth = new DateTimeVo();
				fourMonth.setStartTime(year + "-04-01 00:00:00");
				fourMonth.setEndsTime(year + "-04-30 23:59:59");
				result.put("4", fourMonth);

				DateTimeVo fiveMonth = new DateTimeVo();
				fiveMonth.setStartTime(year + "-05-01 00:00:00");
				fiveMonth.setEndsTime(year + "-05-31 23:59:59");
				result.put("5", fiveMonth);

				DateTimeVo sixMonth = new DateTimeVo();
				sixMonth.setStartTime(year + "-06-01 00:00:00");
				sixMonth.setEndsTime(year + "-06-30 23:59:59");
				result.put("6", sixMonth);

				DateTimeVo sevenMonth = new DateTimeVo();
				sevenMonth.setStartTime(year + "-07-01 00:00:00");
				sevenMonth.setEndsTime(year + "-07-31 23:59:59");
				result.put("7", sevenMonth);

				DateTimeVo nightMonth = new DateTimeVo();
				nightMonth.setStartTime(year + "-08-01 00:00:00");
				nightMonth.setEndsTime(year + "-08-31 23:59:59");
				result.put("8", nightMonth);

				DateTimeVo nineMonth = new DateTimeVo();
				nineMonth.setStartTime(year + "-09-01 00:00:00");
				nineMonth.setEndsTime(year + "-09-30 23:59:59");
				result.put("9", nineMonth);

				DateTimeVo tenMonth = new DateTimeVo();
				tenMonth.setStartTime(year + "-10-01 00:00:00");
				tenMonth.setEndsTime(year + "-10-31 23:59:59");
				result.put("10", tenMonth);

				DateTimeVo elevenMonth = new DateTimeVo();
				elevenMonth.setStartTime(year + "-11-01 00:00:00");
				elevenMonth.setEndsTime(year + "-11-30 23:59:59");
				result.put("11", elevenMonth);

				DateTimeVo twelveMonth = new DateTimeVo();
				twelveMonth.setStartTime(year + "-12-01 00:00:00");
				twelveMonth.setEndsTime(year + "-12-31 23:59:59");
				result.put("12", twelveMonth);
				break;
			/* 季*/
			case 8:
				startTime.set(year + "-01-01 00:00:00");
				AtomicInteger period = new AtomicInteger(month.get() / 3 + 1);
				switch (period.get()) {
					case 1:
						endsTime.set(year + "-03-31 23:59:59");
						break;
					case 2:
						endsTime.set(year + "-06-30 23:59:59");
						break;
					case 3:
						endsTime.set(year + "-09-30 23:59:59");
						break;
					default:
						endsTime.set(year + "-12-31 23:59:59");
				}

				DateTimeVo oneSeason = new DateTimeVo();
				oneSeason.setStartTime(year + "-01-01 00:00:00");
				oneSeason.setEndsTime(year + "-03-31 23:59:59");
				result.put("1", oneSeason);

				DateTimeVo twoSeason = new DateTimeVo();
				twoSeason.setStartTime(year + "-04-01 00:00:00");
				twoSeason.setEndsTime(year + "-06-30 23:59:59");
				result.put("2", twoSeason);

				DateTimeVo threeSeason = new DateTimeVo();
				threeSeason.setStartTime(year + "-07-01 00:00:00");
				threeSeason.setEndsTime(year + "-09-30 23:59:59");
				result.put("3", threeSeason);

				DateTimeVo fourSeason = new DateTimeVo();
				fourSeason.setStartTime(year + "-10-01 00:00:00");
				fourSeason.setEndsTime(year + "-12-31 23:59:59");
				result.put("4", fourSeason);
				break;
			default:
				throw new Error("请上传正确的类型");
		}
		DateTimeVo dateTime = new DateTimeVo();
		dateTime.setStartTime(startTime.get());
		dateTime.setEndsTime(endsTime.get());
		result.put("date", dateTime);
		return result;
	}

	public static String rate(String a, String b) {
		return BigDecimal.valueOf(Double.parseDouble(b) / Double.parseDouble(a) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
	}
}
