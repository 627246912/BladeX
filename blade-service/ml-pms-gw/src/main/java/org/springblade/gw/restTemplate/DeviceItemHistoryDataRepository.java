package org.springblade.gw.restTemplate;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springblade.bean.DeviceItemHistoryData;
import org.springblade.bean.DeviceItemHistoryDiffData;
import org.springblade.enums.DeviceItemCycle;
import org.springblade.gw.config.PushConfig;
import org.springblade.util.PushResponseUtil;
import org.springblade.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: bond
 * @Date: 2020/03/31
 * @Description:
 */
@Repository
public class DeviceItemHistoryDataRepository {
    private static final Logger log = LoggerFactory.getLogger(DeviceItemHistoryDataRepository.class);
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private PushConfig pushConfig;


    /**
     * 通过数据项id列表(多个id用，隔开)和运营商id，开始时间，结束时间，时间间隔类型获取历史数据
     * @param itemIds
     * @param startTime
     * @param endTime
     * @param type
     * @param appid
     * @return
     */
    public List<DeviceItemHistoryData> getItemDataByItemIdAndTime(String itemIds, String startTime, String endTime, Integer type,String appid,String stationId)  {
        String url = pushConfig.getUrl(pushConfig.PUSH_BASE_URL,pushConfig.GET_SIGNAL_URI);
        final JSONObject postData = new JSONObject();
        postData.put("appid", appid);
		postData.put("stationId", stationId);
        postData.put("itemids", itemIds);
        postData.put("stime",startTime);
        postData.put("etime",endTime);

        if(!DeviceItemCycle.RECORD.id.equals(type)){
            postData.put("cycle",type);
        }else{
            url = pushConfig.getUrl(pushConfig.PUSH_BASE_URL,pushConfig.GET_RECORD_URI);
        }
        String res = restTemplate.postForEntity(url,postData, String.class).getBody();

        List<DeviceItemHistoryData> historyDatas = new ArrayList<>();
        if(StringUtils.isNotEmpty(res)){
            historyDatas = PushResponseUtil.processResponseListData(res,DeviceItemHistoryData.class);
        }
        return historyDatas;
    }

    /**
     * 通过数据项id列表(多个id用，隔开)和运营商id，开始时间，结束时间，时间间隔类型获取历史数据
     * @param itemIds
     * @param startTime
     * @param endTime
     * @param type
     * @return
     */
    public List<DeviceItemHistoryDiffData> getItemDataAndDiffByItemIdAndTime(String itemIds, String startTime, String endTime, Integer type,Integer ctg)  {
        String url = pushConfig.getUrl(pushConfig.PUSH_BASE_URL,pushConfig.GET_SIGNALEX_URI);
        final JSONObject postData = new JSONObject();
       // postData.put("appid", pushConfig.SYSTEM_ID);
        postData.put("itemids", itemIds);
        postData.put("stime",startTime);
        postData.put("etime",endTime);
        postData.put("cycle",type);
		postData.put("ctg",ctg);
        String res = restTemplate.postForEntity(url,postData, String.class).getBody();

        List<DeviceItemHistoryDiffData> historyDatas = new ArrayList<>();
        if(StringUtils.isNotEmpty(res)){
            historyDatas = PushResponseUtil.processResponseListData(res,DeviceItemHistoryDiffData.class);
        }
        return historyDatas;
    }


}
