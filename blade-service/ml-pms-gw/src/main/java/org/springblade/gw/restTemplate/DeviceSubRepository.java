package org.springblade.gw.restTemplate;

import cn.hutool.core.collection.CollectionUtil;
import org.springblade.bean.DeviceSub;
import org.springblade.constants.CoreCommonConstant;
import org.springblade.gw.config.PushConfig;
import org.springblade.util.PushResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
public class DeviceSubRepository {
    @Autowired
    private PushConfig pushConfig;
    @Autowired
    private RestTemplate restTemplate;


	/**
	 * 网关rtuid信息读取
	 * @param tenant_id 客户id
	 * @param stationId 站点id（项目id）
	 * @param gwids 网关列表
	 * @param rtuidcbs rtuid列表
	 * @return
	 */
    public List<DeviceSub> getDeviceSubs(String tenant_id,String stationId,List<String> gwids,List<String> rtuidcbs){

        Map<String, Object> params = new HashMap<String, Object>(2);
        params.put("appid", tenant_id);
		params.put("stationId", stationId);
        params.put("gwids", CollectionUtil.join(gwids, CoreCommonConstant.SPLIT_COMMA_KEY));
		params.put("rtuidcbs", CollectionUtil.join(rtuidcbs,CoreCommonConstant.SPLIT_COMMA_KEY));

		String res = restTemplate.postForEntity(pushConfig.getUrl(pushConfig.PUSH_BASE_URL,pushConfig.GET_SUB_URI),params, String.class).getBody();

        List<DeviceSub> subList = PushResponseUtil.processResponseListData(res,DeviceSub.class);
        return subList;
    }

	public List<DeviceSub> getDeviceSubsByGwIds(List<String> gwids){

		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("gwids", CollectionUtil.join(gwids, CoreCommonConstant.SPLIT_COMMA_KEY));
		String res = restTemplate.postForEntity(pushConfig.getUrl(pushConfig.PUSH_BASE_URL,pushConfig.GET_SUB_URI),params, String.class).getBody();
		List<DeviceSub> subList = PushResponseUtil.processResponseListData(res,DeviceSub.class);
		return subList;
	}

	public List<DeviceSub> getDeviceSubsByGwId(String gwid){

		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("gwids", gwid);
		String res = restTemplate.postForEntity(pushConfig.getUrl(pushConfig.PUSH_BASE_URL,pushConfig.GET_SUB_URI),params, String.class).getBody();
		List<DeviceSub> subList = PushResponseUtil.processResponseListData(res,DeviceSub.class);
		return subList;
	}
	public DeviceSub getDeviceSubsByRtuId(String rtuidcb){

		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("rtuidcbs",rtuidcb);
		String res = restTemplate.postForEntity(pushConfig.getUrl(pushConfig.PUSH_BASE_URL,pushConfig.GET_SUB_URI),params, String.class).getBody();
		List<DeviceSub> subList = PushResponseUtil.processResponseListData(res,DeviceSub.class);
		if (subList.isEmpty()){
			return null;
		}
		return subList.get(0);
	}

}
