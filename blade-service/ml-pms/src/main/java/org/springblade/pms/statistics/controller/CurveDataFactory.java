package org.springblade.pms.statistics.controller;

import lombok.Data;
import org.springblade.bean.CommonDateInfo;
import org.springblade.bean.DeviceItem;
import org.springblade.core.tool.utils.Func;
import org.springblade.dto.DateReq;
import org.springblade.enums.CommonDateType;
import org.springblade.enums.DeviceItemCycle;
import org.springblade.factorys.DateTypeServiceFactory;
import org.springblade.pms.statistics.dto.CurveDataInfo;
import org.springblade.pms.statistics.dto.CurveDataReq;
import org.springblade.util.DateUtil;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bond
 * @date 2020/5/16 13:32
 * @desc
 */
@Data
public class CurveDataFactory {

	public static Map<String, DeviceItem> buildItemRtuidMap(List<DeviceItem> deviceItems) {
		Map<String, DeviceItem> idItemMap= new HashMap<>();
		if (Func.isEmpty(deviceItems)) {
			return idItemMap;
		} else {
			for(DeviceItem deviceItem:deviceItems){
				idItemMap.put(deviceItem.getRtuid(), deviceItem);
			}
			return idItemMap;
		}
	}

	public static Map<String, DeviceItem> buildItemSidMap(List<DeviceItem> deviceItems) {
		Map<String, DeviceItem> idItemMap= new HashMap<>();
		if (Func.isEmpty(deviceItems)) {
			return idItemMap;
		} else {
			for(DeviceItem deviceItem:deviceItems){
				idItemMap.put(deviceItem.getSid().toString(), deviceItem);
			}
			return idItemMap;
		}
	}

	public static Map<String, DeviceItem> buildItemIdMap(List<DeviceItem> deviceItems) {
		Map<String, DeviceItem> idItemMap= new HashMap<>();
		if (Func.isEmpty(deviceItems)) {
			return idItemMap;
		} else {
			for(DeviceItem deviceItem:deviceItems){
				idItemMap.put(deviceItem.getId(), deviceItem);
			}
			return idItemMap;
		}
	}

	public static CurveDataInfo getCurveDataInfo(CurveDataReq curveDataReq, Boolean dayStat){
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
