package org.springblade.factorys;


import org.springblade.bean.CommonDateInfo;
import org.springblade.dto.DateReq;
import org.springblade.enums.CommonDateType;
import org.springblade.interfaces.DateTypeService;
import org.springblade.util.DateUtil;
import org.springblade.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: wangqiaoqing
 * @Date: 2019/4/16
 * @Description:
 */
public class ReportDateTypeServiceFactory {
    private static Map<Integer, DateTypeService> dateTypeMap = new HashMap<>();
    static {

		dateTypeMap.put(CommonDateType.FIVEMINUTE.getId(),(dateReq) -> {
			//按天 -> 获取每5分钟数列表
			Date startDate = dateReq.getTime();
			Date endDate = dateReq.getEtime();
			return new CommonDateInfo(DateUtil.getfiveMinBetween(startDate,endDate),DateUtil.getfiveMinBetween(startDate,endDate),
				startDate,endDate,CommonDateType.FIVEMINUTE);
		});
        dateTypeMap.put(CommonDateType.DAY.getId(),(dateReq) -> {
            //按天 -> 获取小时数列表
			Date startDate = dateReq.getTime();
			Date endDate = dateReq.getEtime();
            return new CommonDateInfo(DateUtil.getHoursBetween(startDate,endDate),DateUtil.getHoursBetween(startDate,endDate),
				startDate,endDate,CommonDateType.DAY);
        });
        dateTypeMap.put(CommonDateType.WEEK.getId(),(dateReq) -> {
            //按星期 -> 获取当前星期日期列表
			Date startDate = dateReq.getTime();
			Date endDate = dateReq.getEtime();
            return new CommonDateInfo(DateUtil.getDaysBetween(startDate,endDate),DateUtil.getDaysBetween(startDate,endDate),startDate,endDate,CommonDateType.WEEK);
        });
        dateTypeMap.put(CommonDateType.MONTH.getId(),(dateReq) -> {
            //按月 -> 获取当月日期列表
			Date startDate = dateReq.getTime();
			Date endDate = dateReq.getEtime();
            return new CommonDateInfo(DateUtil.getDaysBetween(startDate,endDate),DateUtil.getDaysBetween(startDate,endDate),startDate,endDate,CommonDateType.MONTH);
        });
        dateTypeMap.put(CommonDateType.YEAR.getId(),(dateReq) -> {
            //按年 -> 获取当前年份月份列表
			Date startDate = dateReq.getTime();
			Date endDate = dateReq.getEtime();
            return new CommonDateInfo(DateUtil.getMonthsBetween(startDate,endDate),DateUtil.getMonthsBetween(startDate,endDate),startDate,endDate,CommonDateType.YEAR);
        });
        dateTypeMap.put(CommonDateType.RECENTDAY.getId(),(dateReq) -> {
            //按天 -> 获取最近24小时 统计范围包含本小时
			Date startDate = dateReq.getTime();
			Date endDate = dateReq.getEtime();
            return new CommonDateInfo(DateUtil.getHoursBetween(startDate,endDate),DateUtil.getHoursBetween(startDate,endDate),startDate,endDate, CommonDateType.DAY);
        });
        dateTypeMap.put(CommonDateType.RECENTWEEK.getId(),(dateReq) -> {
            //按星期 -> 获取最近7天
			Date startDate = dateReq.getTime();
			Date endDate = dateReq.getEtime();
            return new CommonDateInfo(DateUtil.getDaysBetween(startDate,endDate), DateUtil.getDaysBetween(startDate,endDate),DateUtil.getStartDate(startDate),endDate,CommonDateType.WEEK);
        });
        dateTypeMap.put(CommonDateType.RECENTMONTH.getId(),(dateReq) -> {
            //按月 -> 获取最近30天
			Date startDate = dateReq.getTime();
			Date endDate = dateReq.getEtime();
            return new CommonDateInfo(DateUtil.getDaysBetween(startDate,endDate),DateUtil.getDaysBetween(startDate,endDate),
				DateUtil.getStartDate(startDate),endDate,CommonDateType.MONTH);
        });
        dateTypeMap.put(CommonDateType.RECENTYEAR.getId(),(dateReq) -> {
			//按年 -> 获取最近12个月
			Date startDate = dateReq.getTime();
			Date endDate = dateReq.getEtime();
			return new CommonDateInfo(DateUtil.getMonthsBetween(startDate,endDate),DateUtil.getMonthsBetween(startDate,endDate),
				DateUtil.getMonthStartDate(startDate),
				endDate,CommonDateType.YEAR);
		});

		dateTypeMap.put(CommonDateType.QUARTER.getId(),(dateReq) -> {
			//按季度
			Date endDate = DateUtil.getQuarterEndDate(dateReq.getTime());
			Date startDate = DateUtil.getQuarterStartDate(dateReq.getTime());
			return new CommonDateInfo(DateUtil.getMonthsBetween(startDate,endDate),DateUtil.getMonthsBetween(startDate,endDate),
				DateUtil.getMonthStartDate(startDate),
				endDate,CommonDateType.YEAR);
		});
    }

    /**
     * 根据日期参数获取日期信息
     * @param dateReq
     * @param defaultDateType
     * @return
     */
    public static DateTypeService getDateTypeStrategy(DateReq dateReq, CommonDateType defaultDateType){
        if(StringUtils.isNull(defaultDateType)){
            defaultDateType = CommonDateType.DAY;
        }
        if(StringUtils.isNull(dateReq)){
            dateReq = new DateReq();
        }
        Date now = new Date();
        if(StringUtils.isNull(dateReq.getTime())){
            dateReq.setTime(now);
        }

        if(now.compareTo(dateReq.getTime())<0){
            dateReq.setTime(now);
        }
        dateReq.setDateType(StringUtils.nvl(dateReq.getDateType(),defaultDateType.getId()));
        return dateTypeMap.get(dateReq.getDateType());
    }
}
