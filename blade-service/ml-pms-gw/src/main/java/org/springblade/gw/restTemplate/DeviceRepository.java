package org.springblade.gw.restTemplate;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import org.springblade.bean.Device;
import org.springblade.bean.DevieceJFPGDate;
import org.springblade.constants.CoreCommonConstant;
import org.springblade.gw.config.PushConfig;
import org.springblade.util.PushResponseUtil;
import org.springblade.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: bond
 * @Date: 2020/03/31
 * @Description:
 */
@Repository
public class DeviceRepository {
    @Autowired
    private PushConfig pushConfig;
    @Autowired
    private RestTemplate restTemplate;

	/**
	 * 网关信息读取
	 * @param tenant_id 客户id
	 * @param stationId 站点id（项目id）
	 * @param gwids 网关列表
	 * @return
	 */
	public List<Device> getDevices(String tenant_id,String stationId,List<String> gwids){
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("appid", tenant_id);

		String deviceUrl = pushConfig.getUrl(pushConfig.PUSH_BASE_URL,pushConfig.GET_DEVICE_URI)+"?appid={appid}";
		if(StringUtils.isNotEmpty(gwids)) {
			params.put("gwids", CollectionUtil.join(gwids, CoreCommonConstant.SPLIT_COMMA_KEY));
			deviceUrl += "&gwids={gwids}";
		}
		if(StringUtils.isNotEmpty(stationId)) {
			params.put("stationids", stationId);
			deviceUrl += "&stationids={stationids}";
		}
		String res = restTemplate.getForEntity(deviceUrl,String.class,params).getBody();
		return PushResponseUtil.processResponseListData(res,Device.class);
	}
	public Device getDevice(String gwid){
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("appid", null);
		String deviceUrl = pushConfig.getUrl(pushConfig.PUSH_BASE_URL,pushConfig.GET_DEVICE_URI)+"?appid={appid}";
		params.put("gwids",gwid);
		deviceUrl += "&gwids={gwids}";

		String res = restTemplate.getForEntity(deviceUrl,String.class,params).getBody();
		List<Device> re=PushResponseUtil.processResponseListData(res,Device.class);
		if(re.isEmpty()){
			return null;
		}
		Device device=re.get(0);
		return device;
	}

	/**
	 * 设置通信机峰平谷时间段
	 * @param jfpgDates
	 * @return
	 */
	@Async
	public Boolean setjfpgDate(List<DevieceJFPGDate> jfpgDates) {
		final Map<String,Object> params = new HashMap<String,Object>(2);
		params.put("appid", pushConfig.SYSTEM_ID);
		params.put("paras", jfpgDates);

		String data = JSON.toJSONString(params);
		String res = restTemplate.postForEntity(pushConfig.getUrl(pushConfig.SET_JFPGDATE_URI),data, String.class).getBody();
		return PushResponseUtil.processSuccess(res);
	}

}
