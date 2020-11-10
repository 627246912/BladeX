package org.springblade.gw.repository;


import cn.hutool.core.collection.CollectionUtil;
import org.springblade.bean.DeviceItem;
import org.springblade.bean.DeviceItemRealTimeData;
import org.springblade.bean.DeviceSub;
import org.springblade.constants.GwsubscribeConstant;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.enums.ItemStype;
import org.springblade.gw.cache.DeviceSubCache;
import org.springblade.gw.config.PushConfig;
import org.springblade.gw.config.SystemConfig;
import org.springblade.gw.restTemplate.DeviceItemRepository;
import org.springblade.gw.util.SpringContext;
import org.springblade.interfaces.IFilterProperty;
import org.springblade.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author ：bond
 * @date ：Created in 2020/3/10 10:08
 * @description：实时数据操作类
 * @modified By：
 * @version: 1.0.0$
 */
@Repository
public class DeviceItemDataRepository {
	@Autowired
    private  BladeRedisCache redisCache;
	@Autowired
    private SystemConfig systemConfig;
	@Autowired
	private DeviceItemRepository deviceItemRepository;
	@Autowired
	private PushConfig pushConfig;
	@Autowired
	private DeviceSubCache deviceSubCache;
	@Autowired
	private ExecutorService executorService;
	/**
     * 通过数据项id获取数据项实时数据
     * @param itemIds
     * @return
     */
    public Map<String, DeviceItemRealTimeData> getDeviceItemRealTimeDatasByItemIds(List<String> itemIds){
        Map<String, DeviceItemRealTimeData> realTimeDataMap = getRtDataMapByItemIds(itemIds);
        return realTimeDataMap;
    }

    /**

    /**
     * 更新实时数据
     * @param rtData
     */
    public void updateRealTime(DeviceItemRealTimeData rtData) {
		//实时数据rediskey
		String key= RedisKeysUtil.getKey(systemConfig.getServerIdCard(), GwsubscribeConstant.DEVICE_ITEM_REAL_TIME_DATA);
		Map<Object, Object> hash=new HashMap<Object, Object>();
		hash.put(rtData.getId(),rtData);
        rtData.setLastUpdateTime(new Date());
		redisCache.hMset(key,hash);
		redisCache.expire(key,GwsubscribeConstant.RTDATA_EXPIRE_TIME);
	}

    /**
     * 更新实时数据
     */
    public void updateRealTime(List<DeviceItemRealTimeData> rtDataList) {
		//实时数据rediskey
		String key=RedisKeysUtil.getKey(systemConfig.getServerIdCard(),GwsubscribeConstant.DEVICE_ITEM_REAL_TIME_DATA);
		Map<Object, Object> hash=new HashMap<Object, Object>();
        Date lastUpdatetime = new Date();
        for (DeviceItemRealTimeData deviceItemRealTimeData : rtDataList) {
            deviceItemRealTimeData.setLastUpdateTime(lastUpdatetime);
			hash.put(deviceItemRealTimeData.getId(),deviceItemRealTimeData);
        }
		redisCache.hMset(key,hash);
		redisCache.expire(key,GwsubscribeConstant.RTDATA_EXPIRE_TIME);
    }


    /**
     * 根据数据项id获取实时数据
     * @param itemId
     * @return
     */
    public DeviceItemRealTimeData getRtDataByItemId(String itemId) {
		//实时数据rediskey
		String key=RedisKeysUtil.getKey(systemConfig.getServerIdCard(),GwsubscribeConstant.DEVICE_ITEM_REAL_TIME_DATA);
		DeviceItemRealTimeData rtData =redisCache.hGet(key,itemId);
        if(StringUtils.isNotNull(rtData)) {
            long diffMinute = DateUtil.getDiffMinute(rtData.getLastUpdateTime(),new Date());
            if(diffMinute >= GwsubscribeConstant.MAX_RT_TIME){
                rtData.setVal(GwsubscribeConstant.ITEM_INITIAL_VALUE);
            }
        }
        return rtData;
    }

    /**
     * 根据数据项id列表获取实时数据
     * @param itemIds
     * @return
     */
    public List<DeviceItemRealTimeData> getRtDataByItemIds(List<String> itemIds){
		//实时数据rediskey
		String key=RedisKeysUtil.getKey(systemConfig.getServerIdCard(),GwsubscribeConstant.DEVICE_ITEM_REAL_TIME_DATA);
        List<DeviceItemRealTimeData> rtDataList = new ArrayList<>();
		List<DeviceItemRealTimeData> rtDatas = redisCache.hmGet(key,ListUtils.stringListToArray(itemIds));
			Date now = new Date();
        for (DeviceItemRealTimeData deviceItemRealTimeData : rtDatas) {
            if(StringUtils.isNotNull(deviceItemRealTimeData)){
                long diffMinute = DateUtil.getDiffMinute(deviceItemRealTimeData.getLastUpdateTime(),now);
                if(diffMinute >= GwsubscribeConstant.MAX_RT_TIME){
                    deviceItemRealTimeData.setVal(GwsubscribeConstant.ITEM_INITIAL_VALUE);
                    deviceItemRealTimeData.setVal(BigDecimalUtil.round(deviceItemRealTimeData.getVal(),GwsubscribeConstant.DEFAULT_KEEP_SCALE));
                }
                rtDataList.add(deviceItemRealTimeData);
            }
        }
        return rtDataList;
    }

    /**
     * 根据数据项id列表获取实时数据
     * @param itemIds
     * @return
     */
    public Map<String,DeviceItemRealTimeData> getRtDataMapByItemIds(List<String> itemIds){
		//实时数据rediskey
		String key=RedisKeysUtil.getKey(systemConfig.getServerIdCard(),GwsubscribeConstant.DEVICE_ITEM_REAL_TIME_DATA);
		String items[]= ListUtils.stringListToArray(itemIds);
        List<DeviceItemRealTimeData> rtDataList = redisCache.hmGet(key,items);
        Map<String,DeviceItemRealTimeData> rtDataMap = new HashMap<>(10);
        Date now = new Date();
        for (DeviceItemRealTimeData deviceItemRealTimeData : rtDataList) {
            if(StringUtils.isNotNull(deviceItemRealTimeData)){
                long diffMinute = DateUtil.getDiffMinute(deviceItemRealTimeData.getLastUpdateTime(),now);
                if(diffMinute >= GwsubscribeConstant.MAX_RT_TIME){
                    deviceItemRealTimeData.setVal(GwsubscribeConstant.ITEM_INITIAL_VALUE);
                }
                rtDataMap.put(deviceItemRealTimeData.getId(),deviceItemRealTimeData);
            }
        }
        return rtDataMap;
    }


	/**
	 * 更新RTU底下的数据项信息
	 * @param deviceItemMap
	 */
	public void updateDeviceItemInfosOfSub(Map<String,List<DeviceItem>> deviceItemMap) {
		List<DeviceItem> deviceItemList = new ArrayList<>();
		List<DeviceItem> itemList;
		Set<String> gwIdSet = new HashSet<>();
		for (Map.Entry<String, List<DeviceItem>> deviceItemEntry : deviceItemMap.entrySet()) {
			itemList = deviceItemEntry.getValue();
			for (DeviceItem deviceItem : itemList) {
				gwIdSet.add(deviceItem.getGwid());
			}

			deviceItemList.addAll(itemList);
			CollectionUtil.sort(itemList, new Comparator<DeviceItem>() {
				@Override
				public int compare(DeviceItem o1, DeviceItem o2) {
					return o1.getSid().compareTo(o2.getSid());
				}
			});
		}
		asyncUpdateGwBtypeMapByGwIds(new ArrayList<>(gwIdSet));
		//更新redis数据项和数据项信息
		updateId2ItemInfo(deviceItemList);
		Map<Object,Object> map=new HashMap<>(deviceItemMap);
		String key= RedisKeysUtil.getKey(systemConfig.getServerIdCard(), GwsubscribeConstant.DEVICE_SUB_ITEM);
		redisCache.hMset(key,map);



	}
	/**
	 * 更新数据项id和数据项信息
	 * @param deviceItems
	 */
	public Map<String,DeviceItem> updateId2ItemInfo(List<DeviceItem> deviceItems) {
		if(StringUtils.isEmpty(deviceItems)){
			return null;
		}
		Map<String,DeviceItem> idItemMap = new HashMap<>(deviceItems.size());
		for(DeviceItem item : deviceItems) {
			idItemMap.put(item.getId(), item);
		}
		Map<Object,Object> map =new HashMap<>(idItemMap);
		String key=RedisKeysUtil.getKey(systemConfig.getServerIdCard(),GwsubscribeConstant.ID_ITEM);
		redisCache.hMset(key,map);
		return idItemMap;
	}


	/**
	 * 从缓存中获取，没有获取到则把id保存下来
	 * @param itemIds
	 * @return
	 */
	public Map<String, DeviceItem> getDeviceItemInfosByItemIds(List<String> itemIds,boolean needRv){
		//List<DeviceItem> deviceItemList = redisUtils.getHashMultiValue(RedisKeys.getKey(systemConfig.serverIdCard,GwsubscribeConstant.ID_ITEM),itemIds);
		String key=RedisKeysUtil.getKey(systemConfig.getServerIdCard(),GwsubscribeConstant.ID_ITEM);
		List<DeviceItem> deviceItemList=redisCache.hmGet(key,ListUtils.stringListToArray(itemIds));

		if(StringUtils.isNull(deviceItemList)){
			deviceItemList = new ArrayList<>();
		}

		Map<String,DeviceItem> resultMap = new HashMap<>(16);
		Map<String, DeviceItemRealTimeData> rtDataMap = new HashMap<>(16);
		if(needRv) {
			rtDataMap = getRtDataMapByItemIds(itemIds);
		}
		List<String> needFindIds = new ArrayList<String>(itemIds);
		for (DeviceItem deviceItem : deviceItemList) {
			if(StringUtils.isNotNull(deviceItem)){
				String itemId = deviceItem.getId();
				needFindIds.remove(itemId);
				if(needRv) {
					DeviceItemRealTimeData rtData = rtDataMap.get(itemId);
					Double val = null == rtData ? GwsubscribeConstant.ITEM_INITIAL_VALUE : rtData.getVal();
					deviceItem.setVal(val);
				}
				resultMap.put(itemId,deviceItem);
			}
		}
		//缓存没有则去数据库中查找
		if(StringUtils.isNotEmpty(needFindIds)) {
			List<DeviceItem> deviceItems = deviceItemRepository.getItemInfos(null,null,null,null,needFindIds);
			if(needRv) {
				setItemsValByRtMap(deviceItems, rtDataMap);
			}
			Map<String,DeviceItem> deviceItemMap = updateId2ItemInfo(deviceItems);
			if(StringUtils.isNotEmpty(deviceItemMap)){
				resultMap.putAll(deviceItemMap);
			}
		}
		return resultMap;
	}
	/**
	 * 根据实时数据map设置实时数据
	 * @param items
	 * @param rtDataMap
	 */
	public void setItemsValByRtMap(List<DeviceItem> items, Map<String, DeviceItemRealTimeData> rtDataMap) {
		for(DeviceItem item : items){
			DeviceItemRealTimeData rtData = rtDataMap.get(item.getId());
			Double val = null == rtData?GwsubscribeConstant.ITEM_INITIAL_VALUE:rtData.getVal();
			item.setVal(val);
		}
	}
	@Async
	public void asyncUpdateGwBtypeMapByGwIds(List<String> gwIds){
		updateGwBtypeMap(null,new HashSet<>(),gwIds);
	}
	private void updateGwBtypeMap(Integer btype, Set<String> itemIdSet, List<String> gwIds) {
		if(!pushConfig.ITEM_BTYPE_ENABLED){
			//禁用数据项按业务类型分类
			return;
		}

		String btypeStr;
		DeviceItemRepository deviceItemRepository = (DeviceItemRepository) SpringContext.getBean("deviceItemRepository");
		List<DeviceItem> deviceItemList = deviceItemRepository.getItemInfosByGwids(gwIds);
		Map<String, Map<String,List<String>>> gwBtypeItemMap = new HashMap<>(16);
		for (String needFindId : gwIds) {
			gwBtypeItemMap.put(needFindId,new HashMap<>(16));
		}
		Map<String, List<String>> btypeItemMap;
		List<String> itemList;
		for (DeviceItem item : deviceItemList) {
			String gwId = item.getGwid();
			btypeItemMap = gwBtypeItemMap.get(gwId);

			Integer btypeTemp = StringUtils.nvl(item.getBtype(), 0);
			btypeStr = btypeTemp.toString();
			if (btypeItemMap.containsKey(btypeStr)) {
				itemList = btypeItemMap.get(btypeStr);
			} else {
				itemList = new ArrayList<>();
				btypeItemMap.put(btypeStr, itemList);
			}
			itemList.add(item.getId());

			if(StringUtils.isNotNull(btype) && btype.equals(btypeTemp)){
				itemIdSet.add(item.getId());
			}
		}

		for (String needFindId : gwIds) {
			String key=RedisKeysUtil.getKey(systemConfig.getServerIdCard(),GwsubscribeConstant.BTYPE_ITEM_IDS)+ needFindId;
			Map<Object,Object> map =new HashMap<>(gwBtypeItemMap.get(needFindId));
			redisCache.hMset(key,map);
		}
	}





	/**
	 * 修改传输库数据项后更新缓存
	 * @param rtuIds
	 */
	public void updateCacheAfterUpdateTranItems(List<String> rtuIds) {
		if(StringUtils.isEmpty(rtuIds)){
			return;
		}
		Map<String,List<DeviceItem>> deviceItemMap = getItemInfoMapBySubs(rtuIds);
		if(StringUtils.isNotEmpty(deviceItemMap)){
			updateDeviceItemInfosOfSub(deviceItemMap);
		}
	}

	/**
	 * 通过RTU列表获取数据项信息
	 * @param rtuIds
	 * @return
	 */
	public Map<String,List<DeviceItem>> getItemInfoMapBySubs(List<String> rtuIds){
		List<DeviceItem> items = deviceItemRepository.getItemInfosBySubs(rtuIds);
		return convertItemListToMap(items);
	}

	/**
	 * 转化数据项为rtu map
	 * @param items
	 * @return
	 */
	public Map<String,List<DeviceItem>> convertItemListToMap(List<DeviceItem> items){
		Map<String,List<DeviceItem>> rtuIdItemMap = new HashMap<>(16);
		String rtuId;
		List<DeviceItem> deviceItems;
		for(DeviceItem item:items){
			rtuId = item.getRtuidcb();
			if(rtuIdItemMap.containsKey(rtuId)){
				deviceItems = rtuIdItemMap.get(rtuId);
			}else{
				deviceItems = new ArrayList<>();
				rtuIdItemMap.put(rtuId,deviceItems);
			}
			deviceItems.add(item);
		}
		return rtuIdItemMap;
	}

	/**
	 * 修改存储库，告警库数据项后更新缓存
	 * @param gwIds
	 * @param rtuIds
	 */
	@Async
	public void upateCacheAfterUpdateAlarmStoreItems(List<String> gwIds,List<String> rtuIds) {
		if(StringUtils.isNotEmpty(rtuIds)) {
			updateCacheAfterUpdateTranItems(rtuIds);
		}else {
			updateCacheAfterUpdateCalcItems(gwIds);
		}
	}
	/**
	 * 修改运算库数据项后更新缓存
	 * @param deviceCodes
	 */
	public void updateCacheAfterUpdateCalcItems(List<String> deviceCodes) {
		if(StringUtils.isEmpty(deviceCodes)){
			return;
		}
		List<DeviceItem> calcItems = deviceItemRepository.getCalcItemsByCodesAndType(CollectionUtil.join(deviceCodes,","), ItemStype.OPERATIONYC.id+","+ItemStype.OPERATIONYX.id);

		updateCalcItem(calcItems);
	}
	/**
	 * 更新运算库
	 * @param calcItems
	 * @return
	 */
	public Map<String, List<DeviceItem>> updateCalcItem(List<DeviceItem> calcItems) {
		if(StringUtils.isEmpty(calcItems)){
			return null;
		}

		updateId2ItemInfo(calcItems);

		Map<String, List<DeviceItem>> calcItemMap = new HashMap<>(16);
		for (DeviceItem calcItem : calcItems) {
			String gwid = calcItem.getGwid();
			List<DeviceItem> items = calcItemMap.containsKey(gwid)?calcItemMap.get(gwid) :new ArrayList<>();
			items.add(calcItem);
			calcItemMap.put(gwid, items);
		}

		String key=RedisKeysUtil.getKey(systemConfig.getServerIdCard(),GwsubscribeConstant.DEVICE_CODE_CALC_ITEM);
		Map<Object,Object> map =new HashMap<>(calcItemMap);
		redisCache.hMset(key,map);
		//redisUtils.addAllHashValue(RedisKeys.getKey(systemConfig.serverIdCard, GwsubscribeConstant.DEVICE_CODE_CALC_ITEM),calcItemMap,GwsubscribeConstant.PUSH_REDIS_DEFAULT_EXPIRE);
		return calcItemMap;
	}

	/**
	 * 通过通信机code列表获取传输库数据项实时数据
	 * @param deviceCodes
	 * @return
	 */
	public Map<String, List<DeviceItemRealTimeData>> getDeviceItemRealTimeDataByCodes(List<String> deviceCodes){
		Map<String, List<DeviceItemRealTimeData>> code2Datas = new HashMap<>(16);
		List<DeviceItemRealTimeData> realTimeDatas;
		List<String> subIds;

		Map<String, List<DeviceSub>> code2Subs = deviceSubCache.getSubMapByCodes(deviceCodes);
		if(StringUtils.isEmpty(code2Subs)){
			code2Subs = new HashMap<>(16);
		}
		for(Map.Entry<String, List<DeviceSub>> entry : code2Subs.entrySet()) {
			subIds = new ArrayList<>();
			realTimeDatas = new ArrayList<>();

			for(DeviceSub sub : entry.getValue()) {
				subIds.add(sub.getRtuidcb());
			}

			List<String> itemIds = new ArrayList<>();
			List<DeviceItem> deviceItemList = getDeviceItemInfosBySubIds(subIds,null,true);
			for(DeviceItem item : deviceItemList) {
				itemIds.add(item.getId());
			}
			Map<String, DeviceItemRealTimeData> id2RealTimeData = getDeviceItemRealTimeDatasByItemIds(itemIds);
			for(Map.Entry<String, DeviceItemRealTimeData> en : id2RealTimeData.entrySet()) {
				if(null != en.getValue()) {
					realTimeDatas.add(en.getValue());
				}
			}
			code2Datas.put(entry.getKey(), realTimeDatas);
		}

		return code2Datas;
	}
	/**
	 * 通过RTUId列表获取数据项信息
	 * @param deviceSubIds
	 * @param filter 过滤器
	 * @param needRv 是否需要实时数据
	 * @return
	 */
	public List<DeviceItem> getDeviceItemInfosBySubIds(List<String> deviceSubIds, IFilterProperty<DeviceItem> filter, boolean needRv){
		List<DeviceItem> resultList = new ArrayList<>();
		List<List<DeviceItem>> gwItemList = redisCache.hmGet(RedisKeysUtil.getKey(systemConfig.getServerIdCard(), GwsubscribeConstant.DEVICE_SUB_ITEM),ListUtils.stringListToArray(deviceSubIds));
		if(StringUtils.isNull(gwItemList)){
			gwItemList = new ArrayList<>();
		}
		List<String> needFindSubIds = new ArrayList<>(deviceSubIds);
		for (List<DeviceItem> deviceItemList : gwItemList) {
			if(StringUtils.isNotNull(deviceItemList)) {
				for (DeviceItem deviceItem : deviceItemList) {
					needFindSubIds.remove(deviceItem.getRtuidcb());
					if(StringUtils.isNotNull(filter) && !filter.filter(deviceItem)) {
						continue;
					}
					resultList.add(deviceItem);
				}
			}
		}

		if(StringUtils.isNotEmpty(needFindSubIds)) {
			List<DeviceItem> apiDeviceItemList = getItemInfosBySubsPage(needFindSubIds);
			updateDeviceItemInfosOfSub(convertItemListToMap(apiDeviceItemList));
			if(StringUtils.isNotNull(filter)){
				for (DeviceItem deviceItem : apiDeviceItemList) {
					if(!filter.filter(deviceItem)) {
						continue;
					}
					resultList.add(deviceItem);
				}
			}else{
				resultList.addAll(apiDeviceItemList);
			}
		}

		if(needRv){
			setItemsVal(resultList);
		}
		return resultList;
	}
	private List<DeviceItem> getItemInfosBySubsPage(List<String> needFindSubIds) {
		int total = needFindSubIds.size();
		if(total > GwsubscribeConstant.BATCH_MAX_SUB_SIZE){
			//分页 多线程处理
			int totalPage = PageUtil.getTotalPage(GwsubscribeConstant.BATCH_MAX_SUB_SIZE,total);

			List<DeviceItem> deviceItemList = new ArrayList<>();
			List<Future<List<DeviceItem>>> futureTasks = new ArrayList<>();
			for (int i=1; i<=totalPage;i++) {
				List<String> pagedList = PageUtil.pagedList(i, GwsubscribeConstant.BATCH_MAX_ITEM_SIZE, needFindSubIds);
				futureTasks.add(executorService.submit(new Callable<List<DeviceItem>>() {
					@Override
					public List<DeviceItem> call() throws Exception {
						return deviceItemRepository.getItemInfosBySubs(pagedList);
					}
				}));
			}

			for (Future<List<DeviceItem>> futureTask : futureTasks) {
				try {
					deviceItemList.addAll(futureTask.get());
				} catch (InterruptedException e) {
					System.out.println("根据rtu_id获取数据项异常"+e);
				} catch (ExecutionException e) {
					System.out.println("根据rtu_id获取数据项异常"+e);
				}
			}
			return deviceItemList;
		}else{
			return deviceItemRepository.getItemInfosBySubs(needFindSubIds);
		}
	}
	/**
	 * 在数据项中添加实时数据
	 * @param items
	 */
	private void setItemsVal(List<DeviceItem> items){
		if(StringUtils.isEmpty(items)){
			return;
		}
		List<String> itemIdList = new ArrayList<>();
		for (DeviceItem item : items) {
			itemIdList.add(item.getId());
		}

		Map<String,DeviceItemRealTimeData> rtDataMap = getRtDataMapByItemIds(itemIdList);

		setItemsValByRtMap(items, rtDataMap);
	}
}
