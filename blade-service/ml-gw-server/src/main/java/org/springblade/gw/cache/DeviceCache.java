package org.springblade.gw.cache;


import org.springblade.bean.Device;
import org.springblade.bean.DeviceCommunicationStatus;
import org.springblade.constants.GwsubscribeConstant;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.gw.config.SystemConfig;
import org.springblade.util.ListUtils;
import org.springblade.util.RedisKeysUtil;
import org.springblade.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: bond
 * @Date: 2020/3/16
 * @Description:
 */
@Component
public class DeviceCache {
	@Autowired
	private BladeRedisCache redisCache;
    @Autowired
    private SystemConfig systemConfig;

    /**
     * 新增网关
     * @param devices
     */
    public void addDevices(List<Device> devices) {
        Map<Object,Object> codeDeviceMap = new HashMap<>(devices.size());
        for (Device device : devices) {
            codeDeviceMap.put(device.getCode(),device);
        }
		//网关rediskey
		String key= RedisKeysUtil.getKey(systemConfig.getServerIdCard(), GwsubscribeConstant.DEVICE_CODE_TO_DEVICE);
		redisCache.hMset(key,codeDeviceMap);
		redisCache.expire(key, GwsubscribeConstant.RTDATA_EXPIRE_TIME);
    }

    /**
     * 新增网关
     * @param device
     */
    public void addDevice(Device device) {
		Map<Object,Object> codeDeviceMap = new HashMap<>(1);
		codeDeviceMap.put(device.getCode(),device);
		//网关rediskey
		String key= RedisKeysUtil.getKey(systemConfig.getServerIdCard(),GwsubscribeConstant.DEVICE_CODE_TO_DEVICE);
		redisCache.hMset(key,codeDeviceMap);
		redisCache.expire(key,GwsubscribeConstant.RTDATA_EXPIRE_TIME);
    }

    /**
     * 通过code 列表获取网关信息
     * @param deviceCodes
     * @return
     */
    public List<Device> getDevicesByCodes(List<String> deviceCodes){
        if(StringUtils.isEmpty(deviceCodes)){
            return new ArrayList<>();
        }
        List<String> needFindCodes = new ArrayList<String>(deviceCodes);
		//网关rediskey
		String key= RedisKeysUtil.getKey(systemConfig.getServerIdCard(),GwsubscribeConstant.DEVICE_CODE_TO_DEVICE);
		List<Device> devices =redisCache.hmGet(key, ListUtils.stringListToArray(deviceCodes));
        return devices;
    }

    /**
     * 通过code列表获取网关信息map
     * @param deviceCodes
     * @return
     */
    public Map<String,Device> getDeviceMapByCodes(List<String> deviceCodes){
        List<Device> deviceList = getDevicesByCodes(deviceCodes);
        Map<String,Device> deviceMap = new HashMap<>(16);
        for (Device device : deviceList) {
            deviceMap.put(device.getCode(),device);
        }
        return deviceMap;
    }


    /**
     * 通过code 获取网关信息
     * @param deviceCode
     * @return
     */
    public Device getDeviceByCode(String deviceCode) {
		//网关rediskey
		String key= RedisKeysUtil.getKey(systemConfig.getServerIdCard(),GwsubscribeConstant.DEVICE_CODE_TO_DEVICE);
		Device device =redisCache.hGet(key,deviceCode);
        List<String> needFindCodes = new ArrayList<String>();
		needFindCodes.add(deviceCode);
        return device;
    }



	/**
	 * 根据 code列表 获取网关状态
	 * @param codes
	 * @return
	 */
	public Map<String,DeviceCommunicationStatus> getStatussByCodes(List<String> codes){

		String key= RedisKeysUtil.getKey(systemConfig.getServerIdCard(),GwsubscribeConstant.DEVICE_CODE_TO_STATUS);

		List<DeviceCommunicationStatus> statusList =redisCache.hmGet(key,ListUtils.stringListToArray(codes));
		Map<String,DeviceCommunicationStatus> resultMap = new HashMap<>(codes.size());
		for (DeviceCommunicationStatus deviceCommunicationStatus : statusList) {
			if(StringUtils.isNotNull(deviceCommunicationStatus)) {
				resultMap.put(deviceCommunicationStatus.getGwid(), deviceCommunicationStatus);
			}
		}
		return resultMap;
	}

	/**
	 * 更新网关状态
	 * @param status
	 */
	public void updateStatus(DeviceCommunicationStatus status) {
		String key= RedisKeysUtil.getKey(systemConfig.getServerIdCard(),GwsubscribeConstant.DEVICE_CODE_TO_STATUS);

		//redisCache.hMset(key,status.getGwid(),status);
		redisCache.hSet(key,status.getGwid(),status);
	}


	/**
	 * 根据 code获取网关状态
	 * @param code
	 * @return
	 */
	public DeviceCommunicationStatus getStatusByCode(String code) {
		String key= RedisKeysUtil.getKey(systemConfig.getServerIdCard(),GwsubscribeConstant.DEVICE_CODE_TO_STATUS);
		DeviceCommunicationStatus status = redisCache.hGet(key,code);
		return status;
	}

	/**
	 * 删除网关状态
	 * @param deviceCode
	 */
	public void delStatus(String deviceCode) {
		String key= RedisKeysUtil.getKey(systemConfig.getServerIdCard(),GwsubscribeConstant.DEVICE_CODE_TO_STATUS);

		redisCache.hDel(key,deviceCode);
	}

	/**
	 * 清除所有网关状态
	 */
	public void delStatus() {
		String key= RedisKeysUtil.getKey(systemConfig.getServerIdCard(),GwsubscribeConstant.DEVICE_CODE_TO_STATUS);
		redisCache.del(key);
	}

}
