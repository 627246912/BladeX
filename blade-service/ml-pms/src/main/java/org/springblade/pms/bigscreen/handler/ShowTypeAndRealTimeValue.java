package org.springblade.pms.bigscreen.handler;

import org.springblade.bean.DeviceItem;
import org.springblade.bean.DeviceItemExtend;
import org.springblade.bean.DeviceItemRealTimeData;
import org.springblade.constants.GwsubscribeConstant;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.utils.Func;
import org.springblade.enums.ShowType;
import org.springblade.pms.config.SystemConfig;
import org.springblade.pms.factory.ShowGroupService;
import org.springblade.pms.factory.ShowGroupServiceFactory;
import org.springblade.util.ListUtils;
import org.springblade.util.RedisKeysUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bond
 * @date 2020/9/15 9:52
 * @desc
 */
@Repository
public class ShowTypeAndRealTimeValue {
	@Autowired
	private BladeRedisCache redisCache;
	@Autowired
	private SystemConfig systemConfig;


	public String getShowTypes(DeviceItemExtend deviceItemExtend){
		StringBuffer types= new StringBuffer();
		ShowType[] showTypes = ShowType.values();
		for (ShowType showType : showTypes) {
			ShowGroupService showGroupService = ShowGroupServiceFactory.getShowGroupStrategy(showType.getId());
			if(showGroupService.checkShowType(deviceItemExtend)){
				types=types.append(showType.getId()).append(",") ;
			}

		}
		if(Func.isEmpty(types)){
			return "";
		}
		String showType=types.toString();
		return showType.substring(0,showType.length()-1);
	}

	public Map<String, DeviceItemRealTimeData> getDeviceItemRealTimeDatas(List<String> itemids) {
		String itemdatakey= RedisKeysUtil.getKey(systemConfig.getServerIdCard(),GwsubscribeConstant.DEVICE_ITEM_REAL_TIME_DATA);

		String[] items = ListUtils.stringListToArray(itemids);
		List<DeviceItemRealTimeData> deviceItemList = (List<DeviceItemRealTimeData>) redisCache.hmGet(itemdatakey, items);

		deviceItemList.removeAll(Collections.singleton(null));
		return buildItemRdMap(deviceItemList);
	}
	public Map<String,DeviceItem> getDeviceItemMap(List<String> itemids,Map<String, DeviceItemRealTimeData> deviceItemRdMap) {
		String keys = RedisKeysUtil.getKey(systemConfig.getServerIdCard(), GwsubscribeConstant.ID_ITEM);
		String[] items = ListUtils.stringListToArray(itemids);
		List<DeviceItem> DeviceItems = (List<DeviceItem>) redisCache.hmGet(keys, items);
		DeviceItems.removeAll(Collections.singleton(null));
		return buildItemMap(DeviceItems, deviceItemRdMap);
	}

	public Map<String, DeviceItemRealTimeData> buildItemRdMap(List<DeviceItemRealTimeData> deviceItems) {
		Map<String, DeviceItemRealTimeData> idItemMap= new HashMap<>();
		if (Func.isEmpty(deviceItems)) {
			return idItemMap;
		} else {
			for(DeviceItemRealTimeData deviceItem:deviceItems){
				idItemMap.put(deviceItem.getId(), deviceItem);
			}
			return idItemMap;
		}
	}

	public Map<String, DeviceItem> buildItemMap(List<DeviceItem> deviceItems,Map<String,DeviceItemRealTimeData> deviceItemRdMap) {
		if (deviceItems.isEmpty()) {
			return null;
		} else {
			Map<String, DeviceItem> idItemMap = new HashMap();
			for(DeviceItem deviceItem:deviceItems){
				if(Func.isEmpty(deviceItemRdMap)){
					deviceItem.setVal(GwsubscribeConstant.ITEM_INITIAL_VALUE);
					idItemMap.put(deviceItem.getId(), deviceItem);
					continue;
				}
				DeviceItemRealTimeData realTimeData=deviceItemRdMap.get(deviceItem.getId());
				if(Func.isNotEmpty(realTimeData)){
					deviceItem.setVal(realTimeData.getVal());
				}else{
					deviceItem.setVal(GwsubscribeConstant.ITEM_INITIAL_VALUE);
				}
				idItemMap.put(deviceItem.getId(), deviceItem);

			}
			return idItemMap;
		}
	}
}
