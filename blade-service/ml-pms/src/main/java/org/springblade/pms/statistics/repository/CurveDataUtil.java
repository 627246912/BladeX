package org.springblade.pms.statistics.repository;

import org.springblade.bean.DeviceItemHistoryDiffData;
import org.springblade.constants.GwsubscribeConstant;
import org.springblade.core.tool.utils.Func;
import org.springblade.pms.statistics.dto.CurveDataResq;
import org.springblade.util.BigDecimalUtil;

import java.util.*;

/**
 * @author bond
 * @date 2020/9/3 17:49
 * @desc
 */
public class CurveDataUtil {

	/**
	 * Y的数据除以val在乘以
	 * @return
	 */
	public static List<CurveDataResq> yvalsdivmul(List<CurveDataResq> list, Float div, Float mul){
		if(Func.isEmpty(list)){
			return list;
		}
		for(CurveDataResq curveDataResq:list){
			List<Object> resYvals= new ArrayList<>();
			List<Object> Yvals= curveDataResq.getYvals();
			for(Object Yval:Yvals){
				if(!Func.equals(Yval,GwsubscribeConstant.ITEM_NULL_VALUE)){
					Yval=	BigDecimalUtil.mulF( BigDecimalUtil.divF(Float.valueOf(Yval.toString()),div),mul);
				}
				resYvals.add(Yval);
			}
			curveDataResq.setYvals(resYvals);
			curveDataResq.setUnit("%");
			curveDataResq.setItemName("负载率");
		}

		return list;
	}

	public static Map<String, List<DeviceItemHistoryDiffData>> buildDeviceItemHistoryDiffDataMap(List<DeviceItemHistoryDiffData> historyDataList) {
		if (historyDataList.isEmpty()) {
			return null;
		} else {
			Map<String, List<DeviceItemHistoryDiffData>> idItemMap = new HashMap();
			Set<String> items=new HashSet<>();
			for(DeviceItemHistoryDiffData historyData : historyDataList) {
				idItemMap.put(historyData.getId(),null);
			}

			for(String item: idItemMap.keySet()){
				List<DeviceItemHistoryDiffData> idItemData=new ArrayList<>();
				for(DeviceItemHistoryDiffData historyData : historyDataList) {
					if(Func.equals(item,historyData.getId())){
						idItemData.add(historyData);
					}
				}
				idItemMap.put(item,idItemData);
			}

			return idItemMap;
		}
	}
}
