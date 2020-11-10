package org.springblade.gw.restTemplate;

import com.alibaba.fastjson.JSON;
import org.springblade.bean.Alarm;
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
 * @Date: 2020/04/9
 * @Description:
 */
@Repository
public class AlarmRepository {

	@Autowired
	private PushConfig pushConfig;
    @Autowired
    private RestTemplate restTemplate;


    /**
     * 通过通信机code(多个code用，隔开)，开始时间，结束时间，pinding
     * @param deviceCodes 通信机code列表
     * @param stime
     * @param etime
     * @param pinding 0：获取所有告警；1：获取所有未结束的告警
     * @param tflag 是按哪种时间查询，1是告警结束时间，2是告警记录更新时间，其余的都是告警开始时间
     * @return
     */
    public List<Alarm> getAlarmInfoByDiviceCodesAndType(String tenant_id,String deviceCodes, String stime,
														String etime, Integer pinding, Integer tflag){
        final Map<String,Object> params = new HashMap<String,Object>(6);
        params.put("appid", tenant_id);
        params.put("gwids", deviceCodes);
        params.put("stime", stime);
        params.put("etime", etime);
        params.put("pending", pinding);
        params.put("tflag",tflag);
        String param = JSON.toJSONString(params);
        String res = restTemplate.postForEntity(pushConfig.getUrl(pushConfig.GET_ALARM_URI),param, String.class).getBody();
        return PushResponseUtil.processResponseListData(res,Alarm.class);
    }

    public List<Alarm> getAlarmInfoByItemsAndType(String tenant_id,String itemids,String stime,String etime,Integer pinding){
        final Map<String,Object> params = new HashMap<String,Object>(5);
        params.put("appid", tenant_id);
        params.put("itemids", itemids);
        params.put("stime", stime);
        params.put("etime", etime);
        params.put("pending", pinding);
        String param = JSON.toJSONString(params);
        String res = restTemplate.postForEntity(pushConfig.getUrl(pushConfig.GET_ALARM_URI),param, String.class).getBody();
        return PushResponseUtil.processResponseListData(res,Alarm.class);
    }


    public Boolean clearAlarms(String tenant_id,String deviceCodes) {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("appid", tenant_id);
        params.put("gwids", deviceCodes);
        String res = restTemplate.postForEntity(pushConfig.getUrl(pushConfig.CLEAR_ALARM_URI),params, String.class).getBody();
        return PushResponseUtil.processSuccess(res);
    }

}
