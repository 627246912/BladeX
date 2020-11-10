package org.springblade.gw.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springblade.bean.DeviceSub;
import org.springblade.constants.GwsubscribeConstant;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.gw.config.SystemConfig;
import org.springblade.gw.restTemplate.DeviceSubRepository;
import org.springblade.util.ListUtils;
import org.springblade.util.RedisKeysUtil;
import org.springblade.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Auther: bond
 * @Date: 2020/3/16
 * @Description:
 */
@Component
public class DeviceSubCache {
    private static final Logger log = LoggerFactory.getLogger(DeviceSubCache.class);
    @Autowired
    private DeviceSubRepository deviceSubRepository;
    @Autowired
    private BladeRedisCache redisCache;
    @Autowired
    private SystemConfig systemConfig;


    /**
     * 更新RTU信息
     * @param deviceSubs
     */
    public Map<String,List<DeviceSub>> updateDeviceSubs(List<DeviceSub> deviceSubs) {
        Map<String,List<DeviceSub>> gwDeviceMap = new HashMap<>(16);
        if(StringUtils.isNotEmpty(deviceSubs)){
            for (DeviceSub deviceSub : deviceSubs) {
                String gwid = deviceSub.getGwid();
                List<DeviceSub> deviceSubList = gwDeviceMap.containsKey(gwid)?gwDeviceMap.get(gwid):new ArrayList<>();

                deviceSubList.add(deviceSub);
                gwDeviceMap.put(gwid,deviceSubList);
            }
			String key= RedisKeysUtil.getKey(systemConfig.getServerIdCard(), GwsubscribeConstant.DEVICE_CODE_TO_DEVICE_SUB);
            Map<Object,Object> map=new HashMap<>(gwDeviceMap);
			redisCache.hMset(key,map);
        }
        return gwDeviceMap;
    }

    /**
     * 通过code获取RTU信息
     * @param deviceCodes
     * @return
     */
    public Map<String,List<DeviceSub>> getSubMapByCodes(List<String> deviceCodes){
        List<DeviceSub> subList = getSubListByCodes(deviceCodes);
        Map<String,List<DeviceSub>> code2Subs = new HashMap<>(16);
        for (String deviceCode : deviceCodes) {
            code2Subs.put(deviceCode,new ArrayList<>());
        }
        for (DeviceSub deviceSub : subList) {
            code2Subs.get(deviceSub.getGwid()).add(deviceSub);
        }
        return code2Subs;
    }

    /**
     * 通过code获取RTU信息
     * @param deviceCodes
     * @return
     */
    public List<DeviceSub> getSubListByCodes(List<String> deviceCodes){
        List<List<DeviceSub>> gwSubList = redisCache.hmGet(RedisKeysUtil.getKey(systemConfig.getServerIdCard(),GwsubscribeConstant.DEVICE_CODE_TO_DEVICE_SUB), ListUtils.stringListToArray(deviceCodes));
        List<String> needFindCodes = new ArrayList<>(deviceCodes);
        List<DeviceSub> result = new ArrayList<>();
        if(StringUtils.isNull(gwSubList)){
            gwSubList = new ArrayList<>();
        }
        for (List<DeviceSub> deviceSubList : gwSubList) {
            if(StringUtils.isNotNull(deviceSubList)) {
                for (DeviceSub deviceSub : deviceSubList) {
                    needFindCodes.remove(deviceSub.getGwid());
                    result.add(deviceSub);
                }
            }
        }

        //如果通过code在缓存中获取为null,则通过codes调接口获取
        if(StringUtils.isNotEmpty(needFindCodes)) {
            List<DeviceSub> apiDeviceSubList = deviceSubRepository.getDeviceSubsByGwIds(needFindCodes);
            updateDeviceSubs(apiDeviceSubList);
            if(StringUtils.isNotEmpty(apiDeviceSubList)){
                result.addAll(apiDeviceSubList);
            }
        }
        return result;
    }


    /**
     * 通过code获取RTU map
     * @param deviceCodes
     * @return
     */
    public Map<String,DeviceSub> getSubCodeMapByCodes(List<String> deviceCodes){
        List<DeviceSub> deviceSubs = getSubListByCodes(deviceCodes);
        return deviceSubs.stream().collect(Collectors.toMap(DeviceSub::getRtuidcb,deviceSub -> deviceSub,(oldDeviceSub,newDeviceSub) -> newDeviceSub));
    }

    /**
     * 根据RTU组合id 获取rtu信息
     * @param subIds
     * @return
     */
    public Map<String,DeviceSub> getDeviceSubsBySubIds(List<String> subIds){
        Map<String,DeviceSub> subId2Sub = new HashMap<String,DeviceSub>(16);
        List<DeviceSub> deviceSubs = getDeviceSubListBySubIds(subIds);
        for (DeviceSub deviceSub : deviceSubs) {
            subId2Sub.put(deviceSub.getRtuidcb(),deviceSub);
        }
        return subId2Sub;
    }

    /**
     * 根据RTU组合id 获取rtu信息
     * @param subIds
     * @return
     */
    public List<DeviceSub> getDeviceSubListBySubIds(List<String> subIds){
		String key= RedisKeysUtil.getKey(systemConfig.getServerIdCard(), GwsubscribeConstant.SUB_ID_TO_DEVICE_SUB);

		List<DeviceSub> deviceSubList =redisCache.hmGet(key,ListUtils.stringListToArray(subIds));
        if(StringUtils.isNull(deviceSubList)){
            deviceSubList = new ArrayList<>();
        }

        List<DeviceSub> resultList = new ArrayList<>();
        List<String> needFindSubIds = new ArrayList<>(subIds);
        for (DeviceSub deviceSub : deviceSubList) {
            if(StringUtils.isNotNull(deviceSub)) {
                resultList.add(deviceSub);
                needFindSubIds.remove(deviceSub.getRtuidcb());
            }
        }

        if(StringUtils.isNotEmpty(needFindSubIds)) {
            List<DeviceSub> apiDeviceSubList = deviceSubRepository.getDeviceSubsByGwIds(needFindSubIds);
            if(StringUtils.isNotEmpty(apiDeviceSubList)){
                Map<String,DeviceSub> apiDeviceSubMap = new HashMap<String,DeviceSub>(16);
                for (DeviceSub deviceSub : apiDeviceSubList) {
                    apiDeviceSubMap.put(deviceSub.getRtuidcb(),deviceSub);
                }
				Map<Object,Object> map=new HashMap<>(apiDeviceSubMap);
				redisCache.hMset(key,map);
                resultList.addAll(apiDeviceSubList);
            }
        }
        return resultList;
    }


    /**
     * 根据 id 获取rtu信息
     * @param subId
     * @return
     */
    public DeviceSub getDeviceSubBySubId(String subId) {
        if(StringUtils.isEmpty(subId)){
            return null;
        }

        String redisKey = RedisKeysUtil.getKey(systemConfig.getServerIdCard(),GwsubscribeConstant.SUB_ID_TO_DEVICE_SUB);
        if(redisCache.hExists(redisKey,subId)){
            return redisCache.hGet(redisKey,subId);
        }else{
            List<DeviceSub> deviceSubList = deviceSubRepository.getDeviceSubsByGwIds(Arrays.asList(subId));
            if(StringUtils.isNotEmpty(deviceSubList)){
                DeviceSub deviceSub = deviceSubList.get(0);
				Map<Object,Object> map=new HashMap<>();
				map.put(deviceSub.getRtuidcb(),deviceSub);
				redisCache.hMset(redisKey,map);
                return deviceSub;
            }
        }

        return null;
    }
}
