package org.springblade.energy.statistics.controller;

import lombok.Data;
import org.springblade.bean.CommonDateInfo;
import org.springblade.dto.DateReq;
import org.springblade.energy.statistics.dto.CurveDataInfo;
import org.springblade.energy.statistics.dto.CurveDataReq;
import org.springblade.enums.CommonDateType;
import org.springblade.enums.DeviceItemCycle;
import org.springblade.factorys.DateTypeServiceFactory;
import org.springblade.util.DateUtil;
import org.springframework.beans.BeanUtils;

/**
 * @author bond
 * @date 2020/5/16 13:32
 * @desc
 */
@Data
public class CurveDataFactory {

	public static CurveDataInfo getCurveDataInfo(CurveDataReq curveDataReq,Boolean dayStat){
		DateReq dateReq=new DateReq();
		BeanUtils.copyProperties(curveDataReq,dateReq);
		dateReq.setDayStat(dayStat);
		CommonDateInfo dateInfo = DateTypeServiceFactory.getDateTypeStrategy(dateReq, CommonDateType.DAY).fillDateInfo(dateReq);
		CurveDataInfo curveDataInfo=new CurveDataInfo();
		BeanUtils.copyProperties(curveDataReq,curveDataInfo);

		String stime= DateUtil.toString(dateInfo.getStartDate());
		String etime= DateUtil.toString(dateInfo.getEndDate());

		DeviceItemCycle itemCycle=DeviceItemCycle.getCycleByDateType(dateInfo.getDateType());
		curveDataInfo.setStime(stime);
		curveDataInfo.setEtime(etime);
		//int rows=getRows(stime,itemCycle);
		curveDataInfo.setShowRows(dateInfo.getShowDays());
		curveDataInfo.setItemCycle(itemCycle);
		return curveDataInfo;
	}


	/**
	 * 报表显示的数据条数
	 * @param stime
	 * @param itemCycle
	 * @return
	 */
	public static int getRows(String stime, DeviceItemCycle itemCycle){
		int rows=0;
		switch (itemCycle){
			case DAY:
				rows = DateUtil.getDayNumByTime(stime);
				break;
			case MONTH:
				rows = 12;
				break;
			case FIVEMINUTE:
				rows = 300;
				break;
			default:
				rows = 25;
				break;
		}
		return rows;
	}
}
