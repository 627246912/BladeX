package org.springblade.gw.restTemplate;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springblade.bean.*;
import org.springblade.constants.CoreCommonConstant;
import org.springblade.dto.JfpgPriceNew;
import org.springblade.dto.WaterOrGasPrice;
import org.springblade.gw.config.PushConfig;
import org.springblade.gw.repository.DeviceItemDataRepository;
import org.springblade.gw.util.SpringContext;
import org.springblade.util.PushResponseUtil;
import org.springblade.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @Auther: bond
 * @Date: 2020/03/31
 * @Description:
 */
@Repository
public class DeviceItemRepository {
    private static final Logger log = LoggerFactory.getLogger(DeviceItemRepository.class);

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private PushConfig pushConfig;
	@Autowired
	private ExecutorService executorService;

    /**
     * 获取数据项数据
	 * @param tenant_id 客户id
	 * @param stationId 站点id（项目id）
	 * @param gwids 网关列表
	 * @param rtuidcbs rtuid列表
	 * @param itemids 数据项列表
	 * @return
	 */
    public List<DeviceItem> getItemInfos(String tenant_id,String stationId,List<String> gwids,List<String> rtuidcbs,List<String> itemids){

    	List<DeviceItem> itemList = new ArrayList<>();

        Map<String, Object> params = new HashMap<>(2);
        params.put("appid", tenant_id);
		params.put("stationId", stationId);
        params.put("gwids", CollectionUtil.join(gwids, CoreCommonConstant.SPLIT_COMMA_KEY));
		params.put("rtuidcbs", CollectionUtil.join(rtuidcbs, CoreCommonConstant.SPLIT_COMMA_KEY));
		params.put("itemids", CollectionUtil.join(itemids, CoreCommonConstant.SPLIT_COMMA_KEY));

		String param = JSON.toJSONString(params);
        //请求后台接口获取数据
        String res = restTemplate.postForEntity(pushConfig.getUrl(pushConfig.PUSH_BASE_URL,pushConfig.GET_ITEM_URI),param, String.class).getBody();
        if(StringUtils.isEmpty(res)){
            return itemList;
        }

        itemList = PushResponseUtil.processResponseListData(res,DeviceItem.class);
        return itemList;
    }

	public List<DeviceItem> getItemInfosByGwids(List<String> gwids){
		List<DeviceItem> itemList = new ArrayList<>();

		Map<String, Object> params = new HashMap<>(2);
		params.put("gwids", CollectionUtil.join(gwids, CoreCommonConstant.SPLIT_COMMA_KEY));
		String param = JSON.toJSONString(params);
		//请求后台接口获取数据
		String res = restTemplate.postForEntity(pushConfig.getUrl(pushConfig.PUSH_BASE_URL,pushConfig.GET_ITEM_URI),param, String.class).getBody();
		if(StringUtils.isEmpty(res)){
			return itemList;
		}
		itemList = PushResponseUtil.processResponseListData(res,DeviceItem.class);
		return itemList;
	}
	public List<DeviceItem> getItemInfosByGwid(String gwid){
		List<DeviceItem> itemList = new ArrayList<>();

		Map<String, Object> params = new HashMap<>(2);
		params.put("gwids", gwid);
		String param = JSON.toJSONString(params);
		//请求后台接口获取数据
		String res = restTemplate.postForEntity(pushConfig.getUrl(pushConfig.PUSH_BASE_URL,pushConfig.GET_ITEM_URI),param, String.class).getBody();
		if(StringUtils.isEmpty(res)){
			return itemList;
		}
		itemList = PushResponseUtil.processResponseListData(res,DeviceItem.class);
		return itemList;
	}
	public List<DeviceItem> getItemInfosByRtuidcb(String rtuidcb){
		List<DeviceItem> itemList = new ArrayList<>();
		Map<String, Object> params = new HashMap<>(2);
		params.put("rtuidcbs",rtuidcb);
		String param = JSON.toJSONString(params);
		//请求后台接口获取数据
		String res = restTemplate.postForEntity(pushConfig.getUrl(pushConfig.PUSH_BASE_URL,pushConfig.GET_ITEM_URI),param, String.class).getBody();
		if(StringUtils.isEmpty(res)){
			return itemList;
		}
		itemList = PushResponseUtil.processResponseListData(res,DeviceItem.class);
		return itemList;
	}

	public List<DeviceItem> getItemInfosBySubs(List<String> rtuidcbs){
		List<DeviceItem> itemList = new ArrayList<>();
		Map<String, Object> params = new HashMap<>(2);
		params.put("rtuidcbs", CollectionUtil.join(rtuidcbs, CoreCommonConstant.SPLIT_COMMA_KEY));

		String param = JSON.toJSONString(params);
		//请求后台接口获取数据
		String res = restTemplate.postForEntity(pushConfig.getUrl(pushConfig.PUSH_BASE_URL,pushConfig.GET_ITEM_URI),param, String.class).getBody();
		if(StringUtils.isEmpty(res)){
			return itemList;
		}
		itemList = PushResponseUtil.processResponseListData(res,DeviceItem.class);
		return itemList;
	}

	public DeviceItem getItemInfosByItemid(String itemid){
		DeviceItem item = new DeviceItem();
		Map<String, Object> params = new HashMap<>(2);
		params.put("itemids",itemid);
		String param = JSON.toJSONString(params);
		//请求后台接口获取数据
		String res = restTemplate.postForEntity(pushConfig.getUrl(pushConfig.PUSH_BASE_URL,pushConfig.GET_ITEM_URI),param, String.class).getBody();
		if(StringUtils.isEmpty(res)){
			return item;
		}
		item = PushResponseUtil.processResponseData(res,DeviceItem.class);
		return item;
	}

	/**
	 * 根据网关编码和类型获取运算库
	 * @param deviceCodes
	 * @param types
	 * @return
	 */
	public List<DeviceItem> getCalcItemsByCodesAndType(String deviceCodes,String types){
		if(StringUtils.isEmpty(deviceCodes) && StringUtils.isEmpty(types)){
			return null;
		}

		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("appid", pushConfig.SYSTEM_ID);
		params.put("gwids", deviceCodes);
		params.put("stypes", types);

		String param = JSON.toJSONString(params);
		String res = restTemplate.postForEntity(pushConfig.getUrl(pushConfig.GET_ITEM_URI),param, String.class).getBody();
		return PushResponseUtil.processResponseListData(res,DeviceItem.class);
	}

	/**
	 * 设置遥控值
	 * @param itemId
	 * @param val
	 * @return
	 */
	public Boolean setYk(String itemId,Integer val){
		final Map<String,Object> params = new HashMap<String,Object>(3);
		params.put("appid", pushConfig.SYSTEM_ID);
		params.put("itemid", itemId);
		params.put("val", val);

		String param = JSON.toJSONString(params);
		String res = restTemplate.postForEntity(pushConfig.getUrl(pushConfig.SET_YK_URI),param, String.class).getBody();
		return PushResponseUtil.processSuccess(res);
	}

	/**
	 * 设置遥测传输参数
	 * @param ycTrans
	 * @param rtuIds
	 * @return
	 */
	public Boolean setYcTranInfos(List<YcTran> ycTrans, String rtuIds) {
		final Map<String,Object> params = new HashMap<String,Object>(2);
		params.put("appid", pushConfig.SYSTEM_ID);
		params.put("paras", ycTrans);
		String data = JSON.toJSONString(params);
		String res = restTemplate.postForEntity(pushConfig.getUrl(pushConfig.SET_YCTRAN_URI),data, String.class).getBody();

		boolean updateRes = PushResponseUtil.processSuccess(res);
		if(updateRes && StringUtils.isNotEmpty(rtuIds)) {
			DeviceItemDataRepository deviceItemDataRepository = (DeviceItemDataRepository) SpringContext.getBean("deviceItemDataRepository");
			executorService.execute(() -> deviceItemDataRepository.updateCacheAfterUpdateTranItems(StrUtil.splitTrim(rtuIds,",")));
		}
		return updateRes;
	}

	/**
	 * 设置遥信传输参数
	 * @param yxTrans
	 * @param rtuIds
	 * @return
	 */
	public Boolean setYxTranInfos(List<YxTran> yxTrans, String rtuIds) {
		final Map<String,Object> params = new HashMap<String,Object>(2);
		params.put("appid", pushConfig.SYSTEM_ID);
		params.put("paras", yxTrans);
		String data = JSON.toJSONString(params);
		String res = restTemplate.postForEntity(pushConfig.getUrl(pushConfig.SET_YXTRAN_URI),data, String.class).getBody();

		boolean updateRes = PushResponseUtil.processSuccess(res);
		if(updateRes && StringUtils.isNotEmpty(rtuIds)) {
			DeviceItemDataRepository deviceItemDataRepository = (DeviceItemDataRepository) SpringContext.getBean("deviceItemDataRepository");
			executorService.execute(() -> deviceItemDataRepository.updateCacheAfterUpdateTranItems(StrUtil.splitTrim(rtuIds,",")));
		}
		return updateRes;
	}

	/**
	 * 设置遥控传输参数
	 * @param ykTrans
	 * @param rtuIds
	 * @return
	 */
	public Boolean setYkTranInfos(List<YkTran> ykTrans, String rtuIds) {
		final Map<String,Object> params = new HashMap<String,Object>(2);
		params.put("appid", pushConfig.SYSTEM_ID);
		params.put("paras", ykTrans);
		String data = JSON.toJSONString(params);
		String res = restTemplate.postForEntity(pushConfig.getUrl(pushConfig.UPDATE_YKTRAN_URI),data, String.class).getBody();

		boolean updateRes = PushResponseUtil.processSuccess(res);
		if(updateRes && StringUtils.isNotEmpty(rtuIds)) {
			DeviceItemDataRepository deviceItemDataRepository = (DeviceItemDataRepository) SpringContext.getBean("deviceItemDataRepository");
			executorService.execute(() -> deviceItemDataRepository.updateCacheAfterUpdateTranItems(StrUtil.splitTrim(rtuIds,",")));
		}
		return updateRes;
	}


	/**
	 * 设置遥测传输参数
	 * @param ycStores
	 * @param gwIds
	 * @param rtuIds
	 * @return
	 */
	public Boolean setYcStoreInfos(List<YcStore> ycStores, List<String> gwIds, List<String> rtuIds) {
		final Map<String,Object> params = new HashMap<String,Object>(2);
		params.put("appid", pushConfig.SYSTEM_ID);
		params.put("paras", ycStores);
		String data = JSON.toJSONString(params);
		String res = restTemplate.postForEntity(pushConfig.getUrl(pushConfig.SET_YCSTORE_URI),data, String.class).getBody();

		boolean updateRes = PushResponseUtil.processSuccess(res);
		if(updateRes && (StringUtils.isNotEmpty(gwIds) || StringUtils.isNotEmpty(rtuIds))) {
			DeviceItemDataRepository deviceItemDataRepository = (DeviceItemDataRepository) SpringContext.getBean("deviceItemDataRepository");
			executorService.execute(()->deviceItemDataRepository.upateCacheAfterUpdateAlarmStoreItems(gwIds,rtuIds));
		}
		return updateRes;
	}
	/**
	 * 遥测告警参数设置
	 * @param ycAlarmConfigs
	 * @param gwIds
	 * @param rtuIds
	 * @return
	 */
	public Boolean setYcAlarmConfig(List<YcAlarmConfig> ycAlarmConfigs, List<String> gwIds,List<String> rtuIds) {
		final Map<String,Object> params = new HashMap<String,Object>(2);
		params.put("appid", pushConfig.SYSTEM_ID);
		params.put("paras", ycAlarmConfigs);
		String data = JSON.toJSONString(params);

		String res = restTemplate.postForEntity(pushConfig.getUrl(pushConfig.SET_ALARM_URI),data, String.class).getBody();
		boolean updateRes = PushResponseUtil.processSuccess(res);
		if(updateRes && (StringUtils.isNotEmpty(gwIds) || StringUtils.isNotEmpty(rtuIds))) {
			DeviceItemDataRepository deviceItemDataRepository = (DeviceItemDataRepository) SpringContext.getBean("deviceItemDataRepository");
			//修改成功则更新缓存
			deviceItemDataRepository.upateCacheAfterUpdateAlarmStoreItems(gwIds,rtuIds);
		}
		return updateRes;
	}


	/**
	 * 遥信告警参数设置
	 * @param yxAlarmConfigs
	 * @param gwIds
	 * @param rtuIds
	 * @return
	 */
	public 	Boolean setYxAlarmConfig(List<YxAlarmConfig> yxAlarmConfigs, List<String> gwIds,List<String> rtuIds) {
		final Map<String,Object> params = new HashMap<String,Object>(2);
		params.put("appid", pushConfig.SYSTEM_ID);
		params.put("paras", yxAlarmConfigs);
		String data = JSON.toJSONString(params);

		String res = restTemplate.postForEntity(pushConfig.getUrl(pushConfig.SET_YXALARM_URI),data, String.class).getBody();
		boolean updateRes = PushResponseUtil.processSuccess(res);
		if(updateRes && (StringUtils.isNotEmpty(gwIds) || StringUtils.isNotEmpty(rtuIds))) {
			//修改成功则更新缓存
			DeviceItemDataRepository deviceItemDataRepository = (DeviceItemDataRepository) SpringContext.getBean("deviceItemDataRepository");
			deviceItemDataRepository.upateCacheAfterUpdateAlarmStoreItems(gwIds,rtuIds);
		}
		return updateRes;
	}
	/**
	 * 添加运算库数据项
	 * @param deviceItem
	 * @param deviceCodes
	 * @return
	 */
	public Boolean addCalcItem(DeviceItem deviceItem,String deviceCodes) {
		final Map<String,Object> params = new HashMap<String,Object>(6);
		params.put("appid", pushConfig.SYSTEM_ID);
		params.put("id", deviceItem.getId());
		params.put("stype", deviceItem.getStype());

		params.put("formula",deviceItem.getGetval());
		params.put("gwid",deviceItem.getGwid());
		params.put("relid",deviceItem.getRelid());
		String data = JSON.toJSONString(params);
		String res = restTemplate.postForEntity(pushConfig.getUrl(pushConfig.ADD_CALCITEM_URI),data, String.class).getBody();

		boolean updateRes = PushResponseUtil.processSuccess(res);
		if(updateRes) {
			DeviceItemDataRepository deviceItemDataRepository = (DeviceItemDataRepository) SpringContext.getBean("deviceItemDataRepository");
			deviceItemDataRepository.updateCacheAfterUpdateCalcItems(StrUtil.splitTrim(deviceCodes,","));
		}
		return updateRes;
	}

	/**
	 * 修改遥测运算库数据项
	 * @param ycCalcs
	 * @param deviceCodes
	 * @return
	 */
	public Boolean setYcCalcItem(List<YcCalc> ycCalcs,String deviceCodes) {
		final Map<String,Object> params = new HashMap<String,Object>(2);
		params.put("appid", pushConfig.SYSTEM_ID);
		params.put("paras", ycCalcs);
		String data = JSON.toJSONString(params);
		String res = restTemplate.postForEntity(pushConfig.getUrl(pushConfig.UPDATE_YCCALC_URI),data, String.class).getBody();

		boolean updateRes = PushResponseUtil.processSuccess(res);
		if(updateRes) {
			DeviceItemDataRepository deviceItemDataRepository = (DeviceItemDataRepository) SpringContext.getBean("deviceItemDataRepository");

			deviceItemDataRepository.updateCacheAfterUpdateCalcItems(StrUtil.splitTrim(deviceCodes,","));
		}
		return updateRes;
	}

	/**
	 * 修改遥信运算库数据项
	 * @param yxCalcs
	 * @param deviceCodes
	 * @return
	 */
	public Boolean setYxCalcItem(List<YxCalc> yxCalcs,String deviceCodes) {
		final Map<String,Object> params = new HashMap<String,Object>(2);
		params.put("appid", pushConfig.SYSTEM_ID);
		params.put("paras", yxCalcs);
		String data = JSON.toJSONString(params);

		String res = restTemplate.postForEntity(pushConfig.getUrl(pushConfig.UPDATE_YXCALC_URI),data, String.class).getBody();
		boolean updateRes = PushResponseUtil.processSuccess(res);
		if(updateRes) {
			DeviceItemDataRepository deviceItemDataRepository = (DeviceItemDataRepository) SpringContext.getBean("deviceItemDataRepository");

			deviceItemDataRepository.updateCacheAfterUpdateCalcItems(StrUtil.splitTrim(deviceCodes,","));
		}
		return updateRes;
	}

	/**
	 * 电量价格数据项尖峰平谷时间和对应价格设置
	 * @param jfpgPriceNews
	 * @return
	 */
	public Boolean setJfpgPriceNew(List<JfpgPriceNew> jfpgPriceNews) {
		final Map<String,Object> params = new HashMap<String,Object>(2);
		params.put("appid", pushConfig.SYSTEM_ID);
		params.put("paras", jfpgPriceNews);
		String data = JSON.toJSONString(params);

		String res = restTemplate.postForEntity(pushConfig.getUrl(pushConfig.JFPGPRICENEW_URL),data, String.class).getBody();
		boolean updateRes = PushResponseUtil.processSuccess(res);
		return updateRes;
	}


	/**
	 * 设置水气的价格
	 * @param waterOrGasPrices
	 * @return
	 */
	public Boolean setWaterOrGasPrice(List<WaterOrGasPrice> waterOrGasPrices) {
		final Map<String,Object> params = new HashMap<String,Object>(2);
		params.put("appid", pushConfig.SYSTEM_ID);
		params.put("paras", waterOrGasPrices);
		String data = JSON.toJSONString(params);

		String res = restTemplate.postForEntity(pushConfig.getUrl(pushConfig.WATERORGASPRICE_URL),data, String.class).getBody();
		boolean updateRes = PushResponseUtil.processSuccess(res);
		return updateRes;
	}

	/**
	 * 设置遥控值
	 * @param itemId
	 * @param val
	 * @return
	 */
	public Boolean setCTRL5G(String itemId,Integer val){
		final Map<String,Object> params = new HashMap<String,Object>(3);
		params.put("appid", pushConfig.SYSTEM_ID);
		params.put("itemid", itemId);
		params.put("val", val);

		String param = JSON.toJSONString(params);
		String res = restTemplate.postForEntity(pushConfig.getUrl(pushConfig.CTRL5G_URL),param, String.class).getBody();
		return PushResponseUtil.processSuccess(res);
	}

	/**
	 * 设置遥调
	 * @return
	 */
	public Boolean setYt(List<SetYt> SetYts){
		final Map<String,Object> params = new HashMap<String,Object>(2);
		params.put("appid", pushConfig.SYSTEM_ID);
		params.put("paras", SetYts);
		String data = JSON.toJSONString(params);

		String res = restTemplate.postForEntity(pushConfig.getUrl(pushConfig.YTSET),data, String.class).getBody();
		boolean updateRes = PushResponseUtil.processSuccess(res);
		return updateRes;
	}

}
