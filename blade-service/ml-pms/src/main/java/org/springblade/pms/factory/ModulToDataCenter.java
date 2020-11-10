package org.springblade.pms.factory;

import org.springblade.bean.*;
import org.springblade.constants.GwsubscribeConstant;
import org.springblade.constants.ProductConstant;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.utils.Func;
import org.springblade.dto.*;
import org.springblade.enums.BooleanEnum;
import org.springblade.enums.ItemStype;
import org.springblade.pms.gw.feign.IDataItemClient;
import org.springblade.pms.gw.feign.IDeviceClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author bond
 * @date 2020/9/21 16:26
 * @desc
 */
@Component
public class ModulToDataCenter {

	@Autowired
	private IDataItemClient iDataItemClient;

	@Autowired
	private IDeviceClient iDeviceClient;
	@Autowired
	private BladeRedisCache redisCache;


	//产品模型id ，对应的bladex数据库t_product表的id字段 1307950870968684545
	private String productId="1307950870968684545";

	public void setProductDataItem(String gwId) {
		if (Func.isEmpty(gwId)) {
			return;
		}
		Set<String> rtuIdcbs =new HashSet<>();
		//查询rtu
		List<DeviceSub> deviceSubList = iDeviceClient.getDeviceSubsByGwid(gwId);
		if (Func.isEmpty(deviceSubList)) {
			return;
		}
		Set<String> deviceCodes =new HashSet<>();
		deviceCodes.add(gwId);
		List<ProductItem> productItemList =new ArrayList<>();
		for (DeviceSub deviceSub : deviceSubList) {
			rtuIdcbs.add(deviceSub.getRtuidcb());
			//根据RTUIDCB查询网关上报的数据线(数据中心查询)
			List<DeviceItem> deviceItemList = iDeviceClient.getDeviceItemInfosByRtuidcb(deviceSub.getRtuidcb());
			if (Func.isNotEmpty(deviceItemList)) {
				List<List<ProductProperty>> lists = new ArrayList<>();
				//map的key是sid（对应的产品属性编码） 一个rtu下sid是唯一的
				Map<Integer, DeviceItem> mapDeviceItem = buildMap(deviceItemList);
				lists = redisCache.hmGet(ProductConstant.PRODUCT_PROPERTY_KEY, productId);


				for (List<ProductProperty> productPropertys : lists) {
					if (Func.isNotEmpty(productPropertys)) {
						for (ProductProperty productProperty : productPropertys) {
							DeviceItem item = mapDeviceItem.get(Integer.valueOf(productProperty.getPropertyCode()));
							if (Func.isNotEmpty(item)) {
								ProductItem productItem = new ProductItem();
								BeanUtils.copyProperties(productProperty, productItem);
								productItem.setItemId(item.getId());
								productItemList.add(productItem);
							}
						}
					}
				}
				sendItemToDataCenter(deviceCodes,rtuIdcbs,productItemList);
			}
		}
	}










	/**
	 * 更新 遥测，遥信，遥控 设置
	 * @param
	 */
	@Async
	public void sendItemToDataCenter(Set<String> deviceCodes, Set<String> rtuIdcbs, List<ProductItem> productItemList){
		String dids= org.apache.commons.lang.StringUtils.join(deviceCodes.toArray(), ",");
		String rtuidcbs= org.apache.commons.lang.StringUtils.join(rtuIdcbs.toArray(), ",");

		//遥测
		List<YcTran> ycTrans = new ArrayList<>();
		List<YcAlarmConfig> ycAlarmConfigs = new ArrayList<>();
		List<YcStore> ycStores = new ArrayList<>();
		//遥信
		List<YxTran> yxTrans = new ArrayList<>();
		List<YxAlarmConfig> yxAlarmConfigs = new ArrayList<>();
		//遥控
		List<YkTran> ykTrans = new ArrayList<>();
		//计算遥测
		List<YcCalc> ycCalcs = new ArrayList<>();
		//计算遥信
		List<YxCalc> yxCalcs = new ArrayList<>();

		for(ProductItem deviceItem:productItemList){
			//DeviceItem deviceItem=DeviceItemMap.get(diagramItem.getItemId());
			//ProductProperty productProperty=redisCache.hGet(ProductConstant.PROPERTY_KEY,diagramItem.getPropertyId());
			//判断4遥类型
			Integer stype = Integer.valueOf(deviceItem.getFtype());
			if(ItemStype.TRANSPORTYC.id.equals(stype)){
				setYcInfo(ycTrans, ycAlarmConfigs, ycStores, deviceItem);
			}else if(ItemStype.TRANSPORTYX.id.equals(stype)){
				setYxInfo(yxTrans, yxAlarmConfigs, deviceItem);
			}else if(ItemStype.TRANSPORTYT.id.equals(stype)){
				//遥调
			}else if(ItemStype.TRANSPORTYK.id.equals(stype)){
				setYkInfo(ykTrans, deviceItem);
			}
			//计算遥测
			else if(ItemStype.OPERATIONYC.id.equals(stype)){
				setYcCalcInfo(ycCalcs, deviceItem);
			}
			//计算遥信
			else if(ItemStype.OPERATIONYX.id.equals(stype)){
				setYxCalcInfo(yxCalcs, deviceItem);
			}
		}

		//执行传输库参数配置
		boolean clearCache = false;
		if(Func.isNotEmpty(ycTrans)){
			clearCache = true;
			YcTranReq req=new YcTranReq();
			req.setYcTranInfos(ycTrans);
			req.setRtuIds(rtuidcbs);
			iDataItemClient.setYcTranInfos(req);
		}
		if(Func.isNotEmpty(yxTrans)){
			YxTranReq req=new YxTranReq();
			req.setYxTranInfos(yxTrans);
			req.setRtuIds(rtuidcbs);
			iDataItemClient.setYxTranInfos(req);
		}
		if(Func.isNotEmpty(ykTrans)){
			YkTranReq req=new YkTranReq();
			req.setYkTranInfos(ykTrans);
			req.setRtuIds(rtuidcbs);
			iDataItemClient.setYkTranInfos(req);
		}

		if(Func.isNotEmpty(ycCalcs)){
			YcCalcReq req=new YcCalcReq();
			req.setYcCalcs(ycCalcs);
			req.setDeviceCodes(dids);
			iDataItemClient.setYcCalcItem(req);
		}
		if(Func.isNotEmpty(yxCalcs)){
			YxCalcReq req=new YxCalcReq();
			req.setYxCalcs(yxCalcs);
			req.setDeviceCodes(dids);
			iDataItemClient.setYxCalcItem(req);
		}

		//执行存储库参数配置
		if(Func.isNotEmpty(ycStores)){
			clearCache = true;
			YcStoreReq req=new YcStoreReq();
			req.setYcStore(ycStores);
			req.setRtuIds(new ArrayList<>(rtuIdcbs));
			iDataItemClient.setYcStoreInfos(req);
		}

		//执行告警库参数配置
		if(Func.isNotEmpty(ycAlarmConfigs)){
			clearCache = true;
			YcAlarmConfigReq req= new YcAlarmConfigReq();
			req.setYcAlarmConfig(ycAlarmConfigs);
			req.setRtuIds(new ArrayList<>(rtuIdcbs));
			iDataItemClient.setYcAlarmConfig(req);
		}
		if(Func.isNotEmpty(yxAlarmConfigs)){
			clearCache = true;
			YxAlarmConfigReq req= new YxAlarmConfigReq();
			req.setYxAlarmConfig(yxAlarmConfigs);
			req.setRtuIds(new ArrayList<>(rtuIdcbs));
			iDataItemClient.setYxAlarmConfig(req);
		}

	}

	/**
	 * 设置遥测参数
	 * @param ycTrans
	 * @param ycAlarmConfigs
	 * @param ycStores
	 * @param deviceItem
	 */
	private void setYcInfo(List<YcTran> ycTrans, List<YcAlarmConfig> ycAlarmConfigs, List<YcStore> ycStores, ProductItem deviceItem) {
		//遥测
		YcTran ycTran = new YcTran();
		ycTran.setId(deviceItem.getItemId());
		//名称带rtu名称更能区分位置，所以名称不同步物模型配置
		ycTran.setShortname(deviceItem.getPropertyAlias());
		ycTran.setBasic(deviceItem.getBasic());
		ycTran.setPalarm(deviceItem.getAlarm());
		ycTran.setStore(deviceItem.getStore());
		ycTran.setBtype(deviceItem.getBtype());
		ycTran.setCtratio(deviceItem.getCtratio());
		ycTran.setDisplayname(deviceItem.getPropertyName());
		ycTrans.add(ycTran);

		//判断是否告警
		if(BooleanEnum.YES.id.equals(deviceItem.getAlarm())){
			YcAlarmConfig ycAlarmConfig = new YcAlarmConfig();
			ycAlarmConfig.setId(deviceItem.getItemId());
			ycAlarmConfig.setUlimit(deviceItem.getUplimit());
			ycAlarmConfig.setLlimit(deviceItem.getLowlimit());
			ycAlarmConfig.setUulimit(deviceItem.getUpuplimit());
			ycAlarmConfig.setLllimit(deviceItem.getLowlowlimit());
			ycAlarmConfig.setDuration(deviceItem.getDuration());
			ycAlarmConfig.setAlarmsms(deviceItem.getSendSms());
			ycAlarmConfig.setAlarmemail(deviceItem.getSendEmail());
			ycAlarmConfig.setAlarmurl(deviceItem.getAlarmUrl());
			ycAlarmConfig.setAlarmtype(deviceItem.getAlarmType());
			ycAlarmConfigs.add(ycAlarmConfig);
		}
		//判断是否存储
		if(BooleanEnum.YES.id.equals(deviceItem.getStore())){
			ycTran.setStore(GwsubscribeConstant.DEFAULT_SOTRE);
			YcStore ycStore = new YcStore();
			ycStore.setId(deviceItem.getItemId());
			ycStore.setStore(GwsubscribeConstant.DEFAULT_SOTRE);
			ycStore.setCalcrule(deviceItem.getCalcrule());
			ycStores.add(ycStore);
		}
	}
	/**
	 * 设置遥控参数
	 * @param ykTrans
	 * @param deviceItem
	 */
	private void setYkInfo(List<YkTran> ykTrans, ProductItem deviceItem) {
		//遥控
		YkTran ykTran = new YkTran();
		ykTran.setId(deviceItem.getItemId());
		ykTran.setShortname(deviceItem.getPropertyAlias());
		ykTran.setDesc(deviceItem.getRemark());
		ykTran.setDisplayname(deviceItem.getPropertyName());
		ykTrans.add(ykTran);
	}

	/**
	 * 设置遥信参数
	 * @param yxTrans
	 * @param yxAlarmConfigs
	 * @param deviceItem
	 */
	private void setYxInfo(List<YxTran> yxTrans, List<YxAlarmConfig> yxAlarmConfigs, ProductItem deviceItem) {
		//遥信
		YxTran yxTran = new YxTran();
		yxTran.setId(deviceItem.getItemId());
		yxTran.setShortname(deviceItem.getPropertyAlias());
		yxTran.setPalarm(deviceItem.getAlarm());
		yxTran.setBtype(deviceItem.getBtype());
		yxTran.setDisplayname(deviceItem.getPropertyName());
		yxTrans.add(yxTran);
		//判断是否告警
		if(BooleanEnum.YES.id.equals(deviceItem.getAlarm())){
			YxAlarmConfig yxAlarmConfig = new YxAlarmConfig();
			yxAlarmConfig.setId(deviceItem.getItemId());
			yxAlarmConfig.setYxalarmval(deviceItem.getYxAlarmVal());
			yxAlarmConfig.setYxalarmlevel(deviceItem.getYxAlarmLevel());
			yxAlarmConfig.setAlarmsms(deviceItem.getSendSms());
			yxAlarmConfig.setAlarmemail(deviceItem.getSendEmail());
			yxAlarmConfig.setAlarmurl(deviceItem.getAlarmUrl());
			yxAlarmConfig.setAlarmtype(deviceItem.getAlarmType());
			yxAlarmConfigs.add(yxAlarmConfig);
		}
	}


	/**
	 * 设置计算遥测参数
	 * @param ycCalcs
	 * @param deviceItem
	 */
	private void setYcCalcInfo(List<YcCalc> ycCalcs, ProductItem deviceItem) {
		//遥控
		YcCalc ycCalc = new YcCalc();
		ycCalc.setShortname(deviceItem.getPropertyAlias());
		ycCalc.setBtype(deviceItem.getBtype());
		ycCalc.setDisplayname(deviceItem.getPropertyAlias());
		ycCalc.setFormula(deviceItem.getFormula());
		ycCalc.setId(deviceItem.getItemId());
		ycCalc.setName(deviceItem.getPropertyName());
		ycCalc.setPalarm(deviceItem.getAlarm());
		ycCalc.setRelid(deviceItem.getFormula());
		ycCalc.setStore(deviceItem.getStore());
		ycCalc.setUnit(deviceItem.getUnit());
		ycCalcs.add(ycCalc);
	}



	/**
	 * 设置计算遥测参数
	 * @param yxCalcs
	 * @param deviceItem
	 */
	private void setYxCalcInfo(List<YxCalc> yxCalcs, ProductItem deviceItem) {
		//遥信
		YxCalc yxCalc = new YxCalc();
		yxCalc.setShortname(deviceItem.getPropertyAlias());
		yxCalc.setBtype(deviceItem.getBtype());
		yxCalc.setDisplayname(deviceItem.getPropertyAlias());
		yxCalc.setFormula(deviceItem.getFormula());
		yxCalc.setId(deviceItem.getItemId());
		yxCalc.setName(deviceItem.getPropertyName());
		yxCalc.setPalarm(deviceItem.getAlarm());
		//yxCalc.setRelid(deviceItem.getRelid());
		yxCalc.setUnit(deviceItem.getUnit());
		//yxCalc.setDesc0(deviceItem.getd);
		//yxCalc.setDesc1(deviceItem.getDesc1());
		yxCalcs.add(yxCalc);
	}

	public Map<Integer, DeviceItem> buildMap(List<DeviceItem> deviceItems) {
		if (deviceItems.isEmpty()) {
			return null;
		} else {
			Map<Integer, DeviceItem> idItemMap = new HashMap(deviceItems.size());
			Iterator var3 = deviceItems.iterator();

			while(var3.hasNext()) {
				DeviceItem item = (DeviceItem)var3.next();
				idItemMap.put(item.getSid(), item);
			}

			return idItemMap;
		}
	}

}
