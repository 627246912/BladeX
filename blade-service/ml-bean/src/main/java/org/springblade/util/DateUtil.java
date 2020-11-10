package org.springblade.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springblade.core.tool.utils.Func;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.Calendar.*;

public class DateUtil {

	private static final Logger log = LoggerFactory.getLogger(DateUtil.class);

	public static final String DATE_FORMAT_FOR_YYYY = "yyyy";
	public static final String DATE_FORMAT_FOR_YM = "yyyy-MM";
	public static final String DATE_FORMAT_FOR_YMD = "yyyy-MM-dd";
	public static final String DATE_FORMAT_FOR_YMDH = "yyyy-MM-dd HH";
	public static final String DATE_FORMAT_FOR_YMDHM = "yyyy-MM-dd HH:mm";
	public static final String DATE_FORMAT_FOR_HH = "HH";
	public static final String DATE_FORMAT_FOR_HHmmss="HH:mm:ss";
	public static final int WEEKS = 7;

	public final  static String DATE_PATTERN = "yyyy-MM-dd";
	public final  static String TIME_PATTERN = "yyyy-MM-dd hh:mm:ss";
	public final  static String TIME_PATTERN_24 = "yyyy-MM-dd HH:mm:ss";
	public final  static String TIME_PATTERN_24_YMDHM = "yyyy年MM月dd日 HH:mm";

	public final  static String TIME_NUMBER_PATTERN = "yyyyMMddhhmmss";

	public static String format(Date date, String format) {
		if (date == null) {
			return null;
		}
		DateFormat df = new SimpleDateFormat(format);
		return df.format(date);
	}

	public static String formatYMD(Date date) {
		if(date == null)
			return "";

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(date);
	}

	public static Date strToDate(String s, String format) {
		DateFormat df = new SimpleDateFormat(format);
		Date d = null;
		try {
			d = df.parse(s);
		} catch (ParseException e) {
			throw new RuntimeException("转换成" + format + "日期格式，异常");
		}
		return d;
	}

	public static Date strToDate(String s, String format,Date defaultDate) {
		DateFormat df = new SimpleDateFormat(format);
		Date d = null;
		try {
			d = df.parse(s);
		} catch (Exception e) {
			return defaultDate;
		}
		return d;
	}

	/**
	 * 字符串转时间
	 *
	 * @param format
	 * @param str_time
	 * @return
	 */
	public static Date dateFormat(String format, String str_time)
	{
		if (format == null || "".equals(format))
		{
			format = DATE_PATTERN;
		}
		try
		{
			return new SimpleDateFormat(format).parse(str_time);
		}
		catch (ParseException e)
		{
			return null;
		}
	}


	/**
	    * 计算两个日期之间相差的月数
	    * @param date1
	    * @param date2
	    * @return
	    */
	  public static int getMonths(Date date1, Date date2){
	       int iMonth = 0;
	       int flag = 0;
	       try{
	           Calendar objCalendarDate1 = Calendar.getInstance();
	           objCalendarDate1.setTime(date1);

	           Calendar objCalendarDate2 = Calendar.getInstance();
	           objCalendarDate2.setTime(date2);

	           if (objCalendarDate2.equals(objCalendarDate1))
	               return 0;
	           if (objCalendarDate1.after(objCalendarDate2)){
	               Calendar temp = objCalendarDate1;
	               objCalendarDate1 = objCalendarDate2;
	               objCalendarDate2 = temp;
	           }
	           if (objCalendarDate2.get(Calendar.DAY_OF_MONTH) < objCalendarDate1.get(Calendar.DAY_OF_MONTH))
	               flag = 1;

	           if (objCalendarDate2.get(Calendar.YEAR) > objCalendarDate1.get(Calendar.YEAR))
	               iMonth = ((objCalendarDate2.get(Calendar.YEAR) - objCalendarDate1.get(Calendar.YEAR))
	                       * 12 + objCalendarDate2.get(Calendar.MONTH) - flag)
	                       - objCalendarDate1.get(Calendar.MONTH);
	           else
	               iMonth = objCalendarDate2.get(Calendar.MONTH)
	                       - objCalendarDate1.get(Calendar.MONTH) - flag;

	       } catch (Exception e){
	        e.printStackTrace();
	       }
	       return iMonth;
	 }

	/**
	 * 计算日期 added by taking.wang
	 *
	 * @param date
	 *            日期
	 * @param month
	 *            月份
	 * @return
	 */
	public static Date calcDate(Date date, int month) throws Exception {
		if (null != date) { // 先判断当前日期和试用期是否都有数据

			Calendar cal = Calendar.getInstance();
			cal.setTime(date);

			int vYear = cal.get(Calendar.YEAR); // 得到年
			int vMonth = cal.get(Calendar.MONTH) + 1; // 得到月
			int vDay = cal.get(Calendar.DATE); // 得到日

			vMonth += month;
			if (vMonth > 12) { // 如果日期加起来大于12的话，则年份要加1
				vYear++;
				vMonth -= 12;
			}

			return strToDate(vYear + "-" + ((String.valueOf(vMonth).length() == 1) ? "0" + vMonth : vMonth) + "-" + vDay, "yyyy-MM-dd");
		}
		return null;
	}

	/**
	 * d1到d2之间有多少天 比如 5月1号 5月31号有31天
	 *
	 * @return
	 */
	public static int getDays(Date endDate, Date startDate) {
		return (int) (dayDiff(endDate, startDate) + 1);
	}

	/**
	 * 间隔天数
	 *
	 * @param d1
	 * @param d2
	 * @return d1 - d2 实际天数,如果 d1 表示的时间等于 d2 表示的时间，则返回 0 值；如果此 d1
	 *         的时间在d2表示的时间之前，则返回小于 0 的值；如果 d1 的时间在 d2 表示的时间之后，则返回大于 0 的值。
	 */
	public static long dayDiff(Date d1, Date d2) {
		Calendar c1 = new GregorianCalendar();
		Calendar c2 = new GregorianCalendar();

		c1.setTime(d1);
		c2.setTime(d2);

		long diffDays = getDiffDays(c1, c2);

		return diffDays;
	}

	public static long getDiffDays(Calendar c1, Calendar c2) {
		Calendar c1Copy = new GregorianCalendar(c1.get(YEAR), c1.get(MONTH),
				c1.get(DATE));
		Calendar c2Copy = new GregorianCalendar(c2.get(YEAR), c2.get(MONTH),
				c2.get(DATE));

		long diffMillis = getDiffMillis(c1Copy, c2Copy);

		long dayMills = 24L * 60L * 60L * 1000L;
		long diffDays = diffMillis / dayMills;

		return diffDays;
	}

	public static long getDiffMinute(Date sDate, Date eDate) {
		long diffMillis = getDiffMillis(sDate, eDate);
		long dayMills = 60L * 1000L;
		long diffMinute = diffMillis / dayMills;
		return diffMinute;
	}

	/**
	 * 获取相差秒数
	 * @param sDate
	 * @param eDate
	 * @return
	 */
	public static long getDiffSeconds(Date sDate, Date eDate) {
		long diffMillis = getDiffMillis(sDate, eDate);
		long dayMills = 1000L;
		long diffSeconds = diffMillis / dayMills;
		return diffSeconds;
	}

	public static long getDiffMillis(Calendar c1, Calendar c2) {
		long diff = c1.getTimeInMillis() - c2.getTimeInMillis();

		return diff;
	}

	public static long getDiffMillis(Date sDate, Date eDate) {
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();

		c1.setTime(sDate);
		c2.setTime(eDate);
		long diff = c2.getTimeInMillis() - c1.getTimeInMillis();

		return diff;
	}

	/**
	 * 计算7个工作日后日期
	 *
	 * @param date
	 * @return
	 */
	public static Date get7WorkDays(Date date) {
		return getNWorkDays(date, 7);
	}

	/**
	 * 获取N个工作日以后的日期
	 *
	 * @param date
	 * @param n
	 * @return
	 */
	public static Date getNWorkDays(Date date, int n) {
		int week = n / 5;
		int day = n % 5;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;// 获取这个星期的第几天
		int plusDay = 0;
		if (day_of_week != 0) {// 周日
			int tmp = day_of_week >> 2;// 除以4
			if (tmp == 0) {// 周一到周三，周数乘以7+剩余天数
				plusDay = week * WEEKS + day;
			} else {// 周四到周六，周数乘以7+剩余天数+当周的周六周日两天
				plusDay = week * WEEKS + day + 2;
			}
		} else {
			plusDay = week * WEEKS + day + 1;// 跳过周日本身
		}
		c.add(Calendar.DAY_OF_MONTH, plusDay);

		return c.getTime();
	}


	/**
	 *
	 * @param date
	 * @param n
	 * @return
	 */
	public static Date getNWorkDaysAgo(Date date, int n) {
		int week = n / 5;// 周数
		int day = n % 5;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;// 获取这个星期的第几天
		int substactDay = WEEKS * week;
		if (day_of_week > 0 && day_of_week <= 2) {// 周一周二,再去除周六日
			substactDay += day + 2;
		} else if (day_of_week == 0 || day_of_week == 6) {
			substactDay += day + 1;
		} else {
			substactDay += day;
		}
		// 减去相应的时间
		c.add(Calendar.DAY_OF_MONTH, -substactDay);
		return c.getTime();
	}

	/**
	 * 获取7个工作日以前的日期
	 *
	 * @param date
	 * @return
	 */
	public static Date get7WorkDaysAgo(Date date) {
		return getNWorkDaysAgo(date, 7);
	}

	public static Date parseToCommonDate(String str) throws Exception {
		if (StringUtils.isEmpty(str)) {
			return null;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.parse(str);
	}

	/**
	 * 求两个时间区间的交集天数
	 *
	 * @param from_1
	 *            区间1开始日期
	 * @param to_1
	 *            区间1结束日期
	 * @param from_2
	 *            区间2开始日期
	 * @param to_2
	 *            区间2结束日期
	 * @param withWeekend
	 *            是否包括周末
	 * @return
	 */
	public static int getDateAreaMix(Date from_1, Date to_1, Date from_2, Date to_2, boolean withWeekend) {
		Calendar start = Calendar.getInstance();
		if (from_1.before(from_2)) {
			start.setTime(from_2);
		} else {
			start.setTime(from_1);
		}
		Calendar end = Calendar.getInstance();
		if (to_1.before(to_2)) {
			end.setTime(to_1);
		} else {
			end.setTime(to_2);
		}
		if (end.before(start)) {
			return 0;
		}
		int count = 0;
		while (end.after(start) || end.equals(start)) {
			if (withWeekend) {
				count++;
			} else {
				if (start.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && start.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
					count++;
				}
			}
			start.add(Calendar.DATE, 1);
		}
		return count;
	}

	/**
	 * 得到date-day
	 * @param date
	 * @param day
	 * @return
	 */
	public static Date getDateAfterDay(Date date,int day)
	{
		if(date==null)
		{
			return null;
		}
        Calendar c=Calendar.getInstance();
        c.setTime(date);
	    c.add(Calendar.DAY_OF_YEAR, day);
	    return c.getTime();
	}

	/**
	 * 添加小时
	 * @param date
	 * @param hour
	 * @return
	 */
	public static Date addHour(Date date,int hour){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.HOUR, hour);
		return c.getTime();
	}

	/**
	 * 添加月份
	 * @param date
	 * @param month
	 * @return
	 */
	public static Date addMonth(Date date,int month){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, month);
		return c.getTime();
	}

	/**
	 * 添加分钟
	 * @param date
	 * @param minute
	 * @return
	 */
	public static Date addMinute(Date date,int minute){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MINUTE, minute);
		return c.getTime();
	}
	/**
	 * 添加秒分钟
	 * @param date
	 * @param second
	 * @return
	 */
	public static Date addSecond(Date date,int second){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.SECOND, second);
		return c.getTime();
	}

	/**
	 * 添加年份
	 * @param date
	 * @param year
	 * @return
	 */
	public static Date addYear(Date date,int year){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.YEAR, year);
		return c.getTime();
	}

	/**
	 * 获取指定日期属于年份的第几周
	 * @param date
	 * @return
	 */
	public static int getWeekOfYear(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.WEEK_OF_YEAR);//获得当前日期属于今年的第几周
	}

	public static Date getDateAfterHour(Date date, BigDecimal hour)
	{
		if(date==null)
		{
			return null;
		}

		Integer integer =  hour.intValue();
		double decimal = BigDecimalUtil.sub(hour.doubleValue(),integer);
		int minute = (int)(60 * decimal);

		Calendar c=Calendar.getInstance();
		c.setTime(date);
		if(0 != integer){
			c.add(Calendar.HOUR_OF_DAY, integer);
		}
		if(0 != minute){
			c.add(Calendar.MINUTE,minute);
		}

		return c.getTime();
	}

	/**
	 * 得到一小时开始时间
	 * @param date
	 * @return
	 */
	public static Date getHourStartDate(Date date) {
		if(date==null)
		{
			return null;
		}
		Calendar c=Calendar.getInstance();
		c.setTime(new Date(date.getTime()));
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), c.get(HOUR_OF_DAY), 0, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	/**
	 * 得到一天开始时间
	 * @param date
	 * @return
	 */
	public static Date getStartDate(Date date)
	{
		if(date==null)
		{
			return null;
		}
		 Calendar c=Calendar.getInstance();
	     c.setTime(new Date(date.getTime()));
	     c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), 0, 0, 0);
	     c.set(Calendar.MILLISECOND, 0);
	     return c.getTime();
	}

	public static Date getEndDate(Date date)
	{
		if(date==null)
		{
			return null;
		}
		 Calendar c=Calendar.getInstance();
	     c.setTime(new Date(date.getTime()));
	     c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), 23, 59, 59);
	     c.set(Calendar.MILLISECOND, 999);
	     return c.getTime();
	}


	/**
	 * 得到一周开始时间
	 * @param date
	 * @return
	 */
	public static Date getWeekStartDate(Date date) {
		if(date==null)
		{
			return null;
		}
		Calendar c=Calendar.getInstance();
		c.setTime(new Date(date.getTime()));

		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);

		return c.getTime();
	}

	/**
	 * 得到一周结束时间
	 * @param date
	 * @return
	 */
	public static Date getWeekEndDate(Date date)
	{
		if(date==null)
		{
			return null;
		}
		Calendar c=Calendar.getInstance();
		c.setTime(new Date(date.getTime()));

		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6);
		c.set(Calendar.HOUR, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		return c.getTime();
	}

	/**
	 * 得到一月开始时间
	 * @param date
	 * @return
	 */
	public static Date getMonthStartDate(Date date)
	{
		if(date==null)
		{
			return null;
		}
		Calendar c=Calendar.getInstance();
		c.setTime(new Date(date.getTime()));
		//获取某月最小天数
		int startDay = c.getActualMinimum(Calendar.DAY_OF_MONTH);
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), startDay, 0, 0, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	/**
	 * 得到一月结束时间
	 * @param date
	 * @return
	 */
	public static Date getMonthEndDate(Date date)
	{
		if(date==null)
		{
			return null;
		}
		Calendar c=Calendar.getInstance();
		c.setTime(new Date(date.getTime()));
		//获取某周最大天数
		int lastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), lastDay, 23, 59, 59);
		c.set(Calendar.MILLISECOND, 999);
		return c.getTime();
	}

	/**
	 * 得到--季度开始时间
	 * @param date
	 * @return
	 */
//	第一季度：1月－3月
//	第二季度：4月－6月
//	第三季度：7月－9月
//	第四季度：10月－12月
	public static Date getQuarterStartDate(Date date)
	{
		if(date==null)
		{
			return null;
		}
		Calendar c=Calendar.getInstance();
		c.setTime(new Date(date.getTime()));
		int year = c.get(YEAR);
		int month = c.get(Calendar.MONTH)+1;
		String mt="";
		if(month<=3){
			month=1;
			mt="01";
		}
		if(month>3 && month<=6){
			month=4;
			mt="04";
		}
		if(month>6 && month<=9){
			month=7;
			mt="07";
		}
		if(month>9 && month<=12){
			month=10;
			mt="10";
		}


		Date dateTime= DateUtil.strToDate(year+"-"+mt+"-01 00:00:00" ,DateUtil.TIME_PATTERN_24);
		c.setTime(new Date(dateTime.getTime()));
		return c.getTime();
	}

	/**
	 * 得到一月结束时间
	 * @param date
	 * @return
	 */
	public static Date getQuarterEndDate(Date date)
	{
		if(date==null)
		{
			return null;
		}
		Calendar c=Calendar.getInstance();
		c.setTime(new Date(date.getTime()));

		int year = c.get(YEAR);
		int month = c.get(Calendar.MONTH)+1;
		String mt="";
		if(month<=3){
			month=3;
			mt="03";
		}
		if(month>3 && month<=6){
			month=6;
			mt="06";
		}
		if(month>6 && month<=9){
			month=9;
			mt="09";
		}
		if(month>9 && month<=12){
			month=12;
			mt="12";
		}


		Date dateTime= DateUtil.strToDate(year+"-"+mt+"-01" ,DateUtil.DATE_FORMAT_FOR_YMD);
		c.setTime(new Date(dateTime.getTime()));
		//获取某月最大天数
		int lastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		dateTime= DateUtil.strToDate(year+"-"+mt+"-"+lastDay+" 23:59:59" ,DateUtil.TIME_PATTERN_24);
		c.setTime(new Date(dateTime.getTime()));

		return c.getTime();
	}


	/**
	 * 得到一年开始时间
	 * @param date
	 * @return
	 */
	public static Date getYearStartDate(Date date) {
		if(date==null) {
			return null;
		}
		Calendar c=Calendar.getInstance();
		c.setTime(new Date(date.getTime()));
		//获取某月最小天数
		int startDay = c.getActualMinimum(Calendar.DAY_OF_YEAR);
		c.set(c.get(Calendar.YEAR), 0, startDay, 0, 0, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	/**
	 * 得到一年结束时间
	 * @param date
	 * @return
	 */
	public static Date getYearEndDate(Date date)
	{
		if(date==null)
		{
			return null;
		}
		Calendar c=Calendar.getInstance();
		c.setTime(new Date(date.getTime()));
		c.set(c.get(Calendar.YEAR), 11, 1, 23, 59, 59);
		//获取某周最大天数
		int lastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		c.set(c.get(Calendar.YEAR), 11, lastDay, 23, 59, 59);
		c.set(Calendar.MILLISECOND, 999);
		return c.getTime();
	}


	/**
	 * 得到一天的开始
	 * @param dateStr
	 * @param format
	 * @return
	 */
	public static Date getStartDate(String dateStr, String format){
		if(dateStr == null){
			return null;
		}
		Date date = null;
		try {
			date = getStartDate(strToDate(dateStr, format));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return date;
	}

	/**
	 * 得到一天的结束
	 * @param dateStr
	 * @param format
	 * @return
	 */
	public static Date getEndDate(String dateStr, String format){
		if(dateStr == null){
			return null;
		}
		Date date = null;
		try {
			date = getEndDate(strToDate(dateStr, format));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return date;
	}
	/**
	 * 获取某月的最后一天
	 * @Title:getLastDayOfMonth
	 * @Description:
	 * @param:@param year
	 * @param:@param month
	 * @param:@return
	 * @return:String
	 * @throws
	 */
	public static String getLastDayOfMonth(int year,int month)
	{
		Calendar cal = Calendar.getInstance();
		//设置年份
		cal.set(Calendar.YEAR,year);
		//设置月份
		cal.set(Calendar.MONTH, month-1);
		//获取某月最大天数
		int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		//设置日历中月份的最大天数
		cal.set(Calendar.DAY_OF_MONTH, lastDay);
		//格式化日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String lastDayOfMonth = sdf.format(cal.getTime());
		return lastDayOfMonth;
	}

	/**
	 * 获取某月的最后一天
	 * @Title:getLastDayOfMonth
	 * @Description:
	 * @param:@return
	 * @return:String
	 * @throws
	 */
	public static Date getLastDayOfMonth(String yearAndMonth)
	{
		Date date = new Date();
		if(StringUtils.isNotEmpty(yearAndMonth)){
			date = strToDate(yearAndMonth,"yyyy-MM");
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		//获取某月最大天数
		int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		//设置日历中月份的最大天数
		cal.set(Calendar.DAY_OF_MONTH, lastDay);
		return cal.getTime();
	}

	/**
	 * 获取某月第一天
	 * @param yearAndMonth
	 * @return
	 */
	public static Date getFirstDayOfMonth(String yearAndMonth)
	{
		Date date = new Date();
		if(StringUtils.isNotEmpty(yearAndMonth)){
			date = strToDate(yearAndMonth,"yyyy-MM");
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		//获取某月最小天数
		int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
		//设置日历中月份的最小天数
		cal.set(Calendar.DAY_OF_MONTH, firstDay);
		return cal.getTime();
	}

	public static Date getCurrentDate(){
		return Calendar.getInstance().getTime();
	}

	//获取两个date间的相差
	public static long getDateGap(Date d1,Date d2){
		return Math.abs(d1.getTime()-d2.getTime());
	}

	//返回相差的表示，用天、时、分、秒
	public static long[] getGapShow(long diff){
		  long[] l = new long[4];
		  long days = diff / (1000 * 60 * 60 * 24);
		  long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
		  long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);
		  long seconds = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60)-minutes*(60*1000))/1000;
		  l[0] = days;
		 l[1] = hours;
		 l[2] = minutes;
		 l[3] = seconds;
		 return l;
	}

	/**
	 * @MethodName convertSecond
	 * @Description 秒数转换，转换成 * 小时 * 分 * 秒
	 *
	 * @author zhangliancai
	 * @date 2015年12月22日 上午10:03:28
	 * @param obj
	 *            秒数
	 * @return * 小时 * 分 * 秒
	 */
	public static String convertSecond(Object obj) {
		if (!NumberUtil.isNumber(obj)) {
			return "";
		}
		int time = NumberUtil.toInteger(obj, 0);

		if (time < 60) {
			return time + "秒";
		}

		int second = time % 60;
		int minute = time / 60;
		if (minute < 60) {
			return minute + "分" + second + "秒";
		}

		int hour = minute / 60;
		minute = minute % 60;
		return hour + "小时" + minute + "分" + second + "秒";
	}

	public static final int daysBetween(Date early, Date late) {

		Calendar calst = Calendar.getInstance();
		Calendar caled = Calendar.getInstance();
		calst.setTime(early);
		caled.setTime(late);
		//设置时间为0时
		calst.set(Calendar.HOUR_OF_DAY, 0);
		calst.set(Calendar.MINUTE, 0);
		calst.set(Calendar.SECOND, 0);
		caled.set(Calendar.HOUR_OF_DAY, 0);
		caled.set(Calendar.MINUTE, 0);
		caled.set(Calendar.SECOND, 0);
		//得到两个日期相差的天数
		int days = ((int) (caled.getTime().getTime() / 1000) - (int) (calst
				.getTime().getTime() / 1000)) / 3600 / 24;

		return days;
	}

	public static boolean isThisTime(long time,String pattern){
        Date date = new Date(time);
         SimpleDateFormat sdf = new SimpleDateFormat(pattern);
         String param = sdf.format(date);//参数时间
         String now = sdf.format(new Date());//当前时间
         if(param.equals(now)){
           return true;
         }
         return false;
    }

	/**
	 * 比较两个日期指定格式是否一致
	 * @param d1
	 * @param d2
	 * @param pattern
	 * @return
	 */
	public static boolean isThisTime(Date d1,Date d2,String pattern){
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		String param = sdf.format(d1);//参数时间
		String now = sdf.format(d2);//当前时间
		if(param.equals(now)){
			return true;
		}
		return false;
	}

	/**
	 *判断选择的日期是否是今天
	 * @throws ParseException
	 */
    public static boolean isToday(String timeStr){
    	Date time = strToDate(timeStr,TIME_PATTERN_24);
        return isThisTime(time.getTime(),DATE_FORMAT_FOR_YMD);
    }

	/**
	 *判断选择的日期是否是本月
	 */
    public static boolean isThisMonth(String timeStr){
		 Date time = strToDate(timeStr,TIME_PATTERN_24);
         return isThisTime(time.getTime(),DATE_FORMAT_FOR_YM);
    }

	/**
	 *判断选择的日期是否是本年
	 */
    public static boolean isThisYear(String timeStr){
		Date time = strToDate(timeStr,TIME_PATTERN_24);
		return isThisTime(time.getTime(),DATE_FORMAT_FOR_YYYY);
    }


    public static Integer getDayNumByTime(String date){
    	if(StringUtils.isEmpty(date)){
			SimpleDateFormat sdf = new SimpleDateFormat(TIME_PATTERN_24);
    		date = sdf.format(new Date());
		}

    	int year = Integer.parseInt(date.substring(0, 4));
    	int month = Integer.parseInt(date.substring(5, 7));
    	int day;

    	boolean yearleap = (year % 400 == 0) || (year % 4 == 0) && (year % 100 != 0);
        if(yearleap && month == 2)
        {
            day = 29;
        }
        else if(!yearleap && month == 2)
        {
            day = 28;
        }
        else if(month == 4 || month == 6 || month == 9 || month == 11)
        {
            day = 30;
        }
        else
        {
            day = 31;
        }
        return day;
    }

	/**
	 * 判断时间是否在时间段内
	 * @param nowTime
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	public static boolean belongCalendar(Date nowTime, Date beginTime, Date endTime) {
		Calendar date = Calendar.getInstance();
		date.setTime(nowTime);

		Calendar begin = Calendar.getInstance();
		begin.setTime(beginTime);

		Calendar end = Calendar.getInstance();
		end.setTime(endTime);

		if (date.after(begin) && date.before(end)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取小时差
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static double getDiffHours(Date startDate, Date endDate) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(startDate);

		Calendar c2 = Calendar.getInstance();
		c2.setTime(endDate);

		long diffMillis = c2.getTimeInMillis() - c1.getTimeInMillis();

		long dayMills = 60L * 60L * 1000L;
		double diffDays = BigDecimalUtil.div(diffMillis,dayMills,2);
		return diffDays;
	}

	/**
	 * 获取指定日期为星期几
	 * @param date
	 * @return
	 */
	public static String getWeekStr(Date date){
		String[] weeks = {"星期日","星期一","星期二","星期三","星期四","星期五","星期六"};
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if(week_index<0){
			week_index = 0;
		}
		return weeks[week_index];
	}

	/**
	 * 获取指定日期为星期几
	 * @param date
	 * @return
	 */
	public static int getWeek(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int week = cal.get(Calendar.DAY_OF_WEEK) - 1;
		return week;
	}

	/**
	 * 几个月以后
	 *
	 * @param date
	 * @param month
	 * @return
	 */
	public static Date getDateAfterMonth(Date date, int month) {
		if(null == date){
			return date;
		}

		Calendar c = Calendar.getInstance();
		c.setTime(new Date(date.getTime()));
		c.add(Calendar.MONTH,month);
		return c.getTime();
	}

	/**
	 * 几个月以前
	 *
	 * @param date
	 * @param month
	 * @return
	 */
	public static Date getDateBeforeMonth(Date date, int month){
		return getDateAfterMonth(date,-month);
	}

	/**
	 * 获取在指定日期之前的指定天数的日期
	 *
	 * @param date
	 * @param day
	 * @return
	 */
	public static Date getDateBeforeDay(Date date, int day) {
		return getDateAfterDay(date, -day);
	}

	/**
	 * 获取某个时间段内所有的日
	 * @param dBegin
	 * @param dEnd
	 * @return
	 *
	 */
	public static List<String> getDaysBetween(Date dBegin, Date dEnd){
		List<String> lDate = new ArrayList<>();
		SimpleDateFormat sd = new SimpleDateFormat(DATE_FORMAT_FOR_YMD);
		Calendar calBegin = Calendar.getInstance();
		// 使用给定的 Date 设置此 Calendar 的时间
		calBegin.setTime(dBegin);
		Calendar calEnd = Calendar.getInstance();
		// 使用给定的 Date 设置此 Calendar 的时间
		calEnd.setTime(dEnd);

		// 测试此日期是否在指定日期之后
		while (dEnd.after(calBegin.getTime())){
			// 根据日历的规则，为给定的日历字段添加或减去指定的时间量
			lDate.add(sd.format(calBegin.getTime()));
			calBegin.add(Calendar.DAY_OF_MONTH, 1);
		}
		return lDate;
	}

	public static String toString(Date date, String pattern) {
		if(date==null){
			return null;
		}
		Assert.notNull(pattern);
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	public static String toString(Date date) {
		return toString(date, TIME_PATTERN_24);
	}

	/**
	 * 获取某个时间段内所有的小时
	 * @param dBegin
	 * @param dEnd
	 * @return
	 *
	 */
	public static List<String> getHoursBetween(Date dBegin, Date dEnd){
		List<String> lDate = new ArrayList<>();
		SimpleDateFormat sd = new SimpleDateFormat(DATE_FORMAT_FOR_YMDH);
		Calendar calBegin = Calendar.getInstance();
		calBegin.setTime(dBegin);
		Calendar calEnd = Calendar.getInstance();
		calEnd.setTime(dEnd);

		// 测试此日期是否在指定日期之后
		while (dEnd.after(calBegin.getTime())){
			// 根据日历的规则，为给定的日历字段添加或减去指定的时间量
			lDate.add(sd.format(calBegin.getTime()));
			calBegin.add(Calendar.HOUR_OF_DAY, 1);
		}
		return lDate;
	}

	/**
	 * 获取某个时间段内所有的月
	 * @param dBegin
	 * @param dEnd
	 * @return
	 *
	 */
	public static List<String> getMonthsBetween(Date dBegin, Date dEnd){
		List<String> lDate = new ArrayList<>();
		SimpleDateFormat sd = new SimpleDateFormat(DATE_FORMAT_FOR_YM);
		Calendar calBegin = Calendar.getInstance();
		// 使用给定的 Date 设置此 Calendar 的时间
		calBegin.setTime(dBegin);
		Calendar calEnd = Calendar.getInstance();
		// 使用给定的 Date 设置此 Calendar 的时间
		calEnd.setTime(dEnd);

		// 测试此日期是否在指定日期之后
		while (dEnd.after(calBegin.getTime())){
			// 根据日历的规则，为给定的日历字段添加或减去指定的时间量
			lDate.add(sd.format(calBegin.getTime()));
			calBegin.add(Calendar.MONTH, 1);
		}
		return lDate;
	}


	/**
	 * 获取某个时间段内所有的年
	 * @param dBegin
	 * @param dEnd
	 * @return
	 *
	 */
	public static List<String> getYearsBetween(Date dBegin, Date dEnd){
		List<String> lDate = new ArrayList<>();
		SimpleDateFormat sd = new SimpleDateFormat(DATE_FORMAT_FOR_YYYY);
		lDate.add(sd.format(dBegin));
		Calendar calBegin = Calendar.getInstance();
		calBegin.setTime(dBegin);
		Calendar calEnd = Calendar.getInstance();
		calEnd.setTime(dEnd);

		// 测试此日期是否在指定日期之后
		while (dEnd.after(calBegin.getTime())){
			// 根据日历的规则，为给定的日历字段添加或减去指定的时间量
			lDate.add(sd.format(calBegin.getTime()));
			calBegin.add(Calendar.YEAR, 1);
		}
		return lDate;
	}

	public static String addOneHour(String beforeDate){
		String tempDate = "";
		String afterDate = "";
		String lastHour = beforeDate.substring(12, 13);

		if("0".equals(beforeDate.substring(11, 12)) && !"9".equals(lastHour)) {
			tempDate = String.valueOf(Integer.valueOf(lastHour) + 1);
			StringBuilder sb = new StringBuilder(beforeDate);
			sb.replace(12, 13,tempDate);
			afterDate = sb.toString();
		}else {
			tempDate = String.valueOf(Integer.valueOf(beforeDate.substring(11, 13)) + 1);
			if("24".equals(tempDate)){
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern(TIME_PATTERN_24);
				LocalDateTime localTime = LocalDateTime.parse(beforeDate,dtf);
				afterDate = localTime.plusHours(1).format(dtf);
			}else {
				StringBuilder sb = new StringBuilder(beforeDate);
				sb.replace(11, 13, tempDate);
				afterDate = sb.toString();
			}
		}
		return afterDate;
	}

	/**
	 * 获取某个时间段内所有的15分钟的倍数的时间
	 * @param dBegin
	 * @param dEnd
	 * @return
	 *
	 */
	public static List<String> getfifteenMinBetween(Date dBegin, Date dEnd){
		//获取最近的为15倍数的时间
		Date stime = getClosetFifteenMinDate(dBegin);
		List<String> lDate = new ArrayList<>();
		SimpleDateFormat sd = new SimpleDateFormat(DATE_FORMAT_FOR_YMDHM);
		Calendar calBegin = Calendar.getInstance();
		calBegin.setTime(stime);
		Calendar calEnd = Calendar.getInstance();
		calEnd.setTime(dEnd);

		// 测试此日期是否在指定日期之后
		while (dEnd.after(calBegin.getTime())){
			// 根据日历的规则，为给定的日历字段添加或减去指定的时间量
			lDate.add(sd.format(calBegin.getTime()));
			calBegin.add(MINUTE, 15);
		}
		return lDate;
	}


	/**
	 * 获取某个时间点最近的 分钟数为15倍数的时间点
	 * @param date
	 * @return
	 *
	 */
	public static Date getClosetFifteenMinDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int minute = calendar.get(Calendar.MINUTE);// 得到分钟
		int count = 0;

		if (minute != 0 && minute != 15 && minute != 30 && minute != 45) {
			for (int i = 1; i < 15; i++) { //判断距离最近的00，15，30，45的分钟数
				count++;
				if ((minute + i) % 15 == 0) {
					break;
				}
			}
		}

		calendar.add(MINUTE,count);
		return calendar.getTime();
	}

	/**
	 * 获取某个时间段内所有的5分钟的倍数的时间
	 * @param dBegin
	 * @param dEnd
	 * @return
	 *
	 */
	public static List<String> getfiveMinBetween(Date dBegin, Date dEnd){
		//获取最近的为5倍数的时间
		Date stime = getClosetFifteenMinDate(dBegin);
		List<String> lDate = new ArrayList<>();
		SimpleDateFormat sd = new SimpleDateFormat(DATE_FORMAT_FOR_YMDHM);
		Calendar calBegin = Calendar.getInstance();
		calBegin.setTime(stime);
		Calendar calEnd = Calendar.getInstance();
		calEnd.setTime(dEnd);

		// 测试此日期是否在指定日期之后
		while (dEnd.after(calBegin.getTime())){
			// 根据日历的规则，为给定的日历字段添加或减去指定的时间量
			lDate.add(sd.format(calBegin.getTime()));
			calBegin.add(MINUTE, 5);
		}
		return lDate;
	}
	/**
	 * 获取某个时间点最近的 分钟数为5倍数的时间点
	 * @param date
	 * @return
	 *
	 */
	public static Date getClosetFiveMinDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int minute = calendar.get(Calendar.MINUTE);// 得到分钟
		int count = 0;

		if (minute != 0 && minute != 5 && minute != 10 && minute != 15 && minute != 20
			&& minute != 25 && minute != 30 && minute != 35 && minute != 40
			&& minute != 45 && minute != 50 && minute != 55) {
			for (int i = 1; i < 5; i++) { //判断距离最近的00，15，30，45的分钟数
				count++;
				if ((minute + i) % 5 == 0) {
					break;
				}
			}
		}

		calendar.add(MINUTE,count);
		return calendar.getTime();
	}

	/**
	 * 今天是不是本月1日
	 * @param date
	 * @return
	 */
	public static boolean isFirstDayOfCurMonth(Date date){
		boolean res = false;
		Date now = new Date();
		if(DateUtil.isThisTime(now,date, DateUtil.DATE_FORMAT_FOR_YMD)){
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
			cal.set(DAY_OF_MONTH,firstDay);
			if(DateUtil.isThisTime(date,cal.getTime(),DateUtil.DATE_FORMAT_FOR_YMD)){
				res = true;
			}
		}
		return res;
	}

	public static String get24TypeHour(String beforeDate){
		String afterDate = toString( getDateAfterHour(strToDate(beforeDate,TIME_PATTERN_24),new BigDecimal(1)))  ;
		if(Objects.equals("23",beforeDate.substring(11, 13))){
			StringBuffer sb = new StringBuffer();
			afterDate = sb.append(beforeDate.substring(0,11)).append("24").append(beforeDate.substring(13)).toString();
		}
		return afterDate;
	}

	public static Integer getminutes(Date date)
	{
		if(Func.isEmpty(date)){
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		// 赋值时年月日时分秒常用的6个值，注意月份下标从0开始，所以取月份要+1
//		System.out.println("年:" + cal.get(Calendar.YEAR));
//		System.out.println("月:" + (cal.get(Calendar.MONTH) + 1));
//		System.out.println("日:" + cal.get(Calendar.DAY_OF_MONTH));
//		System.out.println("时:" + cal.get(Calendar.HOUR_OF_DAY));
//		System.out.println("分:" + cal.get(Calendar.MINUTE));
//		System.out.println("秒:" + cal.get(Calendar.SECOND));
		int hour= cal.get(Calendar.HOUR_OF_DAY);
		int minute=cal.get(Calendar.MINUTE);
		int minutes=hour*60+minute;
		return minutes;
	}

//	public static void main(String[] args) {
//		Calendar cal = Calendar.getInstance();
//		cal.setTime(DateUtil.strToDate("02:10:10","HH:mm:ss"));
//		// 赋值时年月日时分秒常用的6个值，注意月份下标从0开始，所以取月份要+1
//		System.out.println("年:" + cal.get(Calendar.YEAR));
//		System.out.println("月:" + (cal.get(Calendar.MONTH) + 1));
//		System.out.println("日:" + cal.get(Calendar.DAY_OF_MONTH));
//		System.out.println("时:" + cal.get(Calendar.HOUR_OF_DAY));
//		System.out.println("分:" + cal.get(Calendar.MINUTE));
//		System.out.println("秒:" + cal.get(Calendar.SECOND));
//
//		int hour= cal.get(Calendar.HOUR_OF_DAY);
//		int minute=cal.get(Calendar.MINUTE);
//		int minutes=(hour*60)+minute;
//		System.out.println("----------"+minutes);
//
//	}
}
