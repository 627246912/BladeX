package org.springblade.pms.statistics.repository;

import org.springblade.bean.DeviceItem;
import org.springblade.core.tool.utils.Func;
import org.springblade.enums.ProductSid;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bond
 * @date 2020/5/25 14:06
 * @desc
 */
@Component
public class ReportDataRepository {

	//过滤数据项获得电流数据项
	public static List<DeviceItem>  getElectricityItem(List<DeviceItem> gwItemList){
		List<DeviceItem> itemList =new ArrayList<>();
		for(DeviceItem item:gwItemList){
			if(//Func.equals(item.getSid(),ProductSid.SID304.id) ||
				Func.equals(item.getSid().toString(),ProductSid.SID307.id) ||
				Func.equals(item.getSid().toString(),ProductSid.SID310.id) ||
				Func.equals(item.getSid().toString(),ProductSid.SID313.id) ||
				Func.equals(item.getSid().toString(),ProductSid.SID316.id) ||
				Func.equals(item.getSid().toString(),ProductSid.SID319.id)
			){
				itemList.add(item);
			}

		}
		return itemList;
	}

	//过滤数据项获得电能数据项
	public static List<DeviceItem>  getPowerItem(List<DeviceItem> gwItemList){
		List<DeviceItem> itemList =new ArrayList<>();
		for(DeviceItem item:gwItemList){
			if(//Func.equals(item.getSid(),ProductSid.SID306.id) ||
				Func.equals(item.getSid().toString(),ProductSid.SID309.id) ||
				Func.equals(item.getSid().toString(),ProductSid.SID312.id) ||
				Func.equals(item.getSid().toString(),ProductSid.SID315.id) ||
				Func.equals(item.getSid().toString(),ProductSid.SID318.id) ||
				Func.equals(item.getSid().toString(),ProductSid.SID321.id)
			){
				itemList.add(item);
			}

		}
		return itemList;
	}

}
