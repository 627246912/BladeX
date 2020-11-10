package org.springblade.energy.statistics.controller;

import lombok.Data;
import org.springblade.bean.CommonDateInfo;
import org.springblade.dto.DateReq;
import org.springblade.energy.statistics.dto.CurveDataInfo;
import org.springblade.energy.statistics.dto.CurveDataReq;
import org.springblade.enums.CommonDateType;
import org.springblade.enums.DeviceItemCycle;
import org.springblade.factorys.ReportDateTypeServiceFactory;
import org.springblade.util.DateUtil;
import org.springframework.beans.BeanUtils;

/**
 * @author bond
 * @date 2020/5/16 13:32
 * @desc
 */
@Data
public class ReportDataFactory {

	public static CurveDataInfo getCurveDataInfo(CurveDataReq curveDataReq){
		DateReq dateReq=new DateReq();
		BeanUtils.copyProperties(curveDataReq,dateReq);
		CommonDateInfo dateInfo = ReportDateTypeServiceFactory.getDateTypeStrategy(dateReq, CommonDateType.DAY).fillDateInfo(dateReq);
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


}
