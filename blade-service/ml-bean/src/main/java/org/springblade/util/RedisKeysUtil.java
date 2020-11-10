package org.springblade.util;

import org.springblade.core.log.exception.ServiceException;

/**
 * @Auther: bond
 * @Date: 2020/03/11
 * @Description:
 */
public class RedisKeysUtil {

    public static String getSysConfigKey(String serverIdCard,String key){
    	if (StringUtils.isEmpty(serverIdCard)){
			throw new ServiceException("RedisKeysUtil--serverIdCard为空!");
		}
		if (StringUtils.isEmpty(key)){
			throw new ServiceException("RedisKeysUtil--key!");
		}
        return serverIdCard+":sys:config:" + key;
    }

    public static String getKey(String serverIdCard,String key){
		if (StringUtils.isEmpty(serverIdCard)){
			throw new ServiceException("RedisKeysUtil--serverIdCard为空!");
		}
		if (StringUtils.isEmpty(key)){
			throw new ServiceException("RedisKeysUtil--key!");
		}
		return serverIdCard+":"+key;
    }
}
