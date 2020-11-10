package org.springblade.energy.websocket.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springblade.bean.*;
import org.springblade.constants.GwsubscribeConstant;
import org.springblade.constants.ProductConstant;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.config.SystemConfig;
import org.springblade.energy.diagram.dto.DiagramItemResq;
import org.springblade.energy.diagram.entity.Diagram;
import org.springblade.energy.diagram.entity.DiagramItem;
import org.springblade.energy.diagram.entity.DiagramProduct;
import org.springblade.energy.diagram.entity.DiagramShowItem;
import org.springblade.energy.diagram.service.IDiagramItemService;
import org.springblade.energy.diagram.service.IDiagramProductService;
import org.springblade.energy.diagram.service.IDiagramService;
import org.springblade.energy.diagram.service.IDiagramShowItemService;
import org.springblade.energy.factory.ShowGroupService;
import org.springblade.energy.factory.ShowGroupServiceFactory;
import org.springblade.enums.*;
import org.springblade.util.ListUtils;
import org.springblade.util.RedisKeysUtil;
import org.springblade.util.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author bond
 * @date 2020/4/22 15:49
 * @desc
 */
@Component
public class PushDataItemHandler {
	@Autowired
	private IDiagramItemService iDiagramItemService;
	@Autowired
	private IDiagramProductService iDiagramProductService;

	@Autowired
	private IDiagramShowItemService iDiagramShowItemService;

	@Autowired
	private IDiagramService diagramService;

	@Autowired
	private BladeRedisCache redisCache;
	@Autowired
	private SystemConfig systemConfig;
	@Async
	public void pushDataItme(JSONObject params, Channel channel){
		Long stationId =Long.valueOf(params.getString("stationId"));
		Long siteId=Long.valueOf(params.getString("siteId"));
		String diagramType=params.getString("diagramType");
		String diagramId=params.getString("diagramId");

		List<Diagram> listDiagram=getDiagram(diagramId,stationId,siteId,diagramType);

		if(Func.equals(diagramType, DiagramType.ZHONGYA.id) || Func.equals(diagramType, DiagramType.DIYA.id)||Func.equals(diagramType, DiagramType.ZHILIU.id)){
			List<DiagramItemResq> resqList=PowerDiagram(listDiagram, channel);
			HashSet h = new HashSet(resqList);
			resqList.clear();
			resqList.addAll(h);
			String data= JSON.toJSONString(resqList);
			ChannelHandlerPool.channelGroup.writeAndFlush(new TextWebSocketFrame(data), new Matcher(channel));
		}else{
			List<DiagramItemResq> resqList=PowerDiagram(listDiagram, channel);
			List<DiagramItemResq> resqshowList=DiagramShow(listDiagram, channel);

			List<DiagramItemResq> allresqList=new ArrayList<>();
			allresqList.addAll(resqshowList);
			allresqList.addAll(resqList);

			HashSet h = new HashSet(allresqList);
			allresqList.clear();
			allresqList.addAll(h);

			String data= JSON.toJSONString(allresqList);
			ChannelHandlerPool.channelGroup.writeAndFlush(new TextWebSocketFrame(data), new Matcher(channel));
		}

//		if(Func.equals(diagramType, DiagramType.GONGSHUI.id) || Func.equals(diagramType, DiagramType.GONGQI.id)
//		|| Func.equals(diagramType, DiagramType.KONGTIAN.id)){
//			DiagramShow(listDiagram, channel);
//		}
	}
	/**
	 * 自定义系统图实时数据监测
	 * @param listDiagram
	 * @param channel
	 */
	public List<DiagramItemResq> DiagramShow(List<Diagram> listDiagram,Channel channel){
		String didstatuskey= RedisKeysUtil.getKey(systemConfig.getServerIdCard(),GwsubscribeConstant.DEVICE_CODE_TO_STATUS);

		List<DiagramShowItem> diagramItemList =new ArrayList<>();
		for(Diagram diagram:listDiagram) {
			DiagramShowItem diagramItem = new DiagramShowItem();
			diagramItem.setDiagramId(diagram.getId());
			diagramItem.setIsDeleted(0);
			List<DiagramShowItem> showItemList = iDiagramShowItemService.list(Condition.getQueryWrapper(diagramItem));
			diagramItemList.addAll(showItemList);

			//查询具体数据项
			DiagramProduct product=new DiagramProduct();
			product.setStationId(diagram.getStationId());
			product.setDiagramId(diagram.getId());
			product.setSiteId(diagram.getSiteId());
			product.setProductDtype(ProductDtype.HUANJIANG.id);//环境

			List<DiagramProduct> diagramProducts=iDiagramProductService.list(Condition.getQueryWrapper(product));
			if(Func.isNotEmpty(diagramProducts)){
				for(DiagramProduct diagramProduct:diagramProducts) {
					DiagramItem huanjinItem = new DiagramItem();
					huanjinItem.setDiagramId(diagram.getId());
					huanjinItem.setIsDeleted(0);
					huanjinItem.setDiagramProductId(diagramProduct.getId());
					List<DiagramItem> huanjinItemList = iDiagramItemService.list(Condition.getQueryWrapper(huanjinItem));
					for (DiagramItem huanjindiagramItem : huanjinItemList) {
						DiagramShowItem item = new DiagramShowItem();
						BeanUtils.copyProperties(huanjindiagramItem, item);
						diagramItemList.add(item);
					}
				}
			}

		}


		List<String> itemids=new ArrayList<>();
		for(DiagramShowItem item:diagramItemList){
			itemids.add(item.getItemId());
		}
		String items [] = ListUtils.stringListToArray(itemids);
		String key= RedisKeysUtil.getKey(systemConfig.getServerIdCard(),GwsubscribeConstant.DEVICE_ITEM_REAL_TIME_DATA);
		List<DeviceItemRealTimeData> deviceItemList=(List<DeviceItemRealTimeData>)redisCache.hmGet(key,items);

		deviceItemList.removeAll(Collections.singleton(null));
		Map<String,DeviceItemRealTimeData> deviceItemRdMap=buildItemRdMap(deviceItemList);

		String keys= RedisKeysUtil.getKey(systemConfig.getServerIdCard(),GwsubscribeConstant.ID_ITEM);
		List<DeviceItem> DeviceItems=(List<DeviceItem>)redisCache.hmGet(keys,items);
		DeviceItems.removeAll(Collections.singleton(null));
		Map<String,DeviceItem> deviceItemMap=buildItemMap(DeviceItems,deviceItemRdMap);


		List<DiagramItemResq>  listDiagramItemResq =new ArrayList<>();
		for(DiagramShowItem item:diagramItemList) {

			ProductProperty ProductProperty=redisCache.hGet(ProductConstant.PROPERTY_KEY,item.getPropertyId());

			DiagramItemResq diagramItemResq = new DiagramItemResq();
			diagramItemResq.setId(item.getId());
			diagramItemResq.setPindex(item.getPindex());
			diagramItemResq.setItemId(item.getItemId());
			diagramItemResq.setPtype(item.getPropertyCode());

			DeviceCommunicationStatus deviceStatus =redisCache.hGet(didstatuskey,item.getDid());
			if(Func.isNotEmpty(deviceStatus)) {
				diagramItemResq.setStatus(deviceStatus.getStatus());
			}else{
				diagramItemResq.setStatus(0);
			}

			DeviceItem deviceItem = new DeviceItem();
			if (Func.isNotEmpty(deviceItemMap)) {
				deviceItem = deviceItemMap.get(item.getItemId());
				//
				if(Func.isNotEmpty(ProductProperty)){
					diagramItemResq.setShortname(ProductProperty.getPropertyAlias());
					diagramItemResq.setUnit(ProductProperty.getUnit());
				}else{
					diagramItemResq.setShortname(deviceItem.getShortname());
					diagramItemResq.setUnit(deviceItem.getUnit());
				}
				diagramItemResq.setName(deviceItem.getName());
				Double val = deviceItem.getVal();
				if (Func.equals(val, null) || Func.equals(val, GwsubscribeConstant.ITEM_INITIAL_VALUE)) {
					diagramItemResq.setRealTimeValue(GwsubscribeConstant.ITEM_NULL_VALUE);

				} else {
					diagramItemResq.setRealTimeValue(String.valueOf(val));
				}
			}
			diagramItemResq.setShowType("0");

			//科学计算法转换
			if(!Func.equals(diagramItemResq.getRealTimeValue(),GwsubscribeConstant.ITEM_NULL_VALUE)
				&& Func.isNotEmpty(diagramItemResq.getRealTimeValue())){
				BigDecimal bd = new BigDecimal(diagramItemResq.getRealTimeValue());
				diagramItemResq.setRealTimeValue(bd.toPlainString());
			}

			listDiagramItemResq.add(diagramItemResq);
		}

		return listDiagramItemResq;
	}
	/**
	 * 供电系统图实时数据监测
	 * @param listDiagram
	 * @param channel
	 */
	public List<DiagramItemResq> PowerDiagram(List<Diagram> listDiagram,Channel channel){

		List<DiagramItem> listDiagramItem =new ArrayList<>();
		String didstatuskey= RedisKeysUtil.getKey(systemConfig.getServerIdCard(),GwsubscribeConstant.DEVICE_CODE_TO_STATUS);
		String itemdatakey= RedisKeysUtil.getKey(systemConfig.getServerIdCard(),GwsubscribeConstant.DEVICE_ITEM_REAL_TIME_DATA);

		for(Diagram diagram:listDiagram){
			DiagramItem diagramItem=new DiagramItem();
			diagramItem.setDiagramId(diagram.getId());
			diagramItem.setIsDeleted(0);

			List<DiagramItem> diagramItemList=iDiagramItemService.list(Condition.getQueryWrapper(diagramItem));

			listDiagramItem.addAll(diagramItemList);
		}


		List<String> itemids=new ArrayList<>();
		for(DiagramItem item:listDiagramItem){
			itemids.add(item.getItemId());
		}
		String [] items=ListUtils.stringListToArray(itemids);
		List<DeviceItemRealTimeData> deviceItemList=(List<DeviceItemRealTimeData>)redisCache.hmGet(itemdatakey,items);

		deviceItemList.removeAll(Collections.singleton(null));
		Map<String,DeviceItemRealTimeData> deviceItemRdMap=buildItemRdMap(deviceItemList);

		String keys= RedisKeysUtil.getKey(systemConfig.getServerIdCard(),GwsubscribeConstant.ID_ITEM);
		List<DeviceItem> DeviceItems=(List<DeviceItem>)redisCache.hmGet(keys,items);
		DeviceItems.removeAll(Collections.singleton(null));
		Map<String,DeviceItem> deviceItemMap=buildItemMap(DeviceItems,deviceItemRdMap);


		List<DiagramItemResq>  listDiagramItemResq =new ArrayList<>();
		for(DiagramItem item:listDiagramItem){
			DeviceItem deviceItem =new DeviceItem();
			if(Func.isNotEmpty(deviceItemMap)){
				deviceItem=deviceItemMap.get(item.getItemId());
				deviceItem.setPalarm(item.getAlarm());
			}

			DeviceItemExtend deviceItemExtend =new DeviceItemExtend();
			if(Func.isNotEmpty(deviceItem)) {
				BeanUtils.copyProperties(deviceItem,deviceItemExtend);
			}

			DiagramItemResq diagramItemResq = new DiagramItemResq();
			diagramItemResq.setId(item.getId());
			diagramItemResq.setPindex(item.getPindex());
			diagramItemResq.setItemId(item.getItemId());
			diagramItemResq.setPtype(item.getPropertyCode());
			diagramItemResq.setShortname(item.getPropertyAlias());
			DeviceCommunicationStatus deviceStatus =redisCache.hGet(didstatuskey,item.getDid());
			if(Func.isNotEmpty(deviceStatus)) {
				diagramItemResq.setStatus(deviceStatus.getStatus());
			}else{
				diagramItemResq.setStatus(1);
			}

			if(Func.isNotEmpty(deviceItemExtend)) {
				if(Func.isEmpty(item.getVisible()) ){
					deviceItemExtend.setVisible(0);
				}else {
					deviceItemExtend.setVisible(item.getVisible() ? 1 : 0);
				}
				diagramItemResq.setName(deviceItemExtend.getName());
				Double val=deviceItemExtend.getVal();
				if(Func.equals(val,null) || Func.equals(val,GwsubscribeConstant.ITEM_INITIAL_VALUE)){
					diagramItemResq.setRealTimeValue(GwsubscribeConstant.ITEM_NULL_VALUE);

				}else{
					diagramItemResq.setRealTimeValue(String.valueOf(val));
				}
				if(Func.isNotEmpty(item.getUnit())){
					diagramItemResq.setUnit(item.getUnit());
				}else {
					diagramItemResq.setUnit(deviceItemExtend.getUnit());
				}
				DeviceItemExtend showtypedeviceItemExtend =new DeviceItemExtend();
				BeanUtils.copyProperties(deviceItemExtend,showtypedeviceItemExtend);
				showtypedeviceItemExtend.setBtype(item.getBtype());
				diagramItemResq.setShowType(getShowTypes(showtypedeviceItemExtend));

				//开关判断
				String sid=item.getPropertyCode();
				List<String> showTypes =Arrays.asList(diagramItemResq.getShowType().split(","));
				for(String showType:showTypes){
					if(Func.equals(String.valueOf(ShowType.SWITCH.getId()),showType) ){
						//线电压
						if((Func.equals(sid,ProductSid.SID4.id) || Func.equals(sid,ProductSid.SID5.id)  || Func.equals(sid,ProductSid.SID6.id) ) &&
							(Func.equals(val,null) || val<9) ){
							diagramItemResq.setRealTimeValue("0");
						}
						//相电压
						if((Func.equals(sid,ProductSid.SID1.id) || Func.equals(sid,ProductSid.SID2.id)  || Func.equals(sid,ProductSid.SID3.id) ) &&
							(Func.equals(val,null) || val<4) ){
							diagramItemResq.setRealTimeValue("0");
						}

						if((Func.equals(sid,ProductSid.SID1026.id)) &&
							(Func.equals(val,0)|| Func.equals(val,"0")) ){
							diagramItemResq.setRealTimeValue("0");
						}
					}
				}


			}
			//科学计算法转换
			if(!Func.equals(diagramItemResq.getRealTimeValue(),GwsubscribeConstant.ITEM_NULL_VALUE)
			&& Func.isNotEmpty(diagramItemResq.getRealTimeValue())){
				BigDecimal bd = new BigDecimal(diagramItemResq.getRealTimeValue());
				diagramItemResq.setRealTimeValue(bd.toPlainString());
			}

			listDiagramItemResq.add(diagramItemResq);

		}
		return listDiagramItemResq;
	}


	public List<Diagram> getDiagram(String diagramId,Long stationId,Long siteId,String diagramType) {
		Diagram diagram = new Diagram();
		if(Func.isNotEmpty(diagramId)){
			diagram.setId(Func.toLong(diagramId));
		}
		diagram.setStationId(stationId);
		diagram.setSiteId(siteId);
		diagram.setIsDeleted(0);
		diagram.setDiagramType(diagramType);
		List<Diagram> details = diagramService.list(Condition.getQueryWrapper(diagram));
		return details;
	}


	/**
	 * 获取开关告警状态 false 正常，true 告警
	 *
	 * @param deviceItem
	 * @return
	 */
	public boolean getSwitchAlarmStatus(DeviceItem deviceItem) {
		boolean status = false;
		Integer stype = deviceItem.getStype();
		Double val = deviceItem.getVal();
		if (StringUtils.isNotNull(val) && !GwsubscribeConstant.ITEM_INITIAL_VALUE.equals(val)) {
			if (ItemStype.TRANSPORTYC.id.equals(stype) || ItemStype.OPERATIONYC.id.equals(stype)) {
				if ((ItemBtype.ELECTRICCURRENT.id.equals(deviceItem.getBtype()) && val < 0.1) || val < 50) {
					status = true;
				}
			} else {
				if (val.equals(deviceItem.getYxalarmval())) {
					status = true;
				}
			}
		}
		return status;
	}
	/**
	 *分组
	 */
	public Integer getShowType(DeviceItemExtend deviceItemExtend){

		ShowType[] showTypes = ShowType.values();
		for (ShowType showType : showTypes) {
			ShowGroupService showGroupService = ShowGroupServiceFactory.getShowGroupStrategy(showType.getId());
				if(showGroupService.checkShowType(deviceItemExtend)){
					return showType.getId();
				}

		}
		return ShowType.SHOW.getId();
	}

	public String getShowTypes(DeviceItemExtend deviceItemExtend){
	StringBuffer types= new StringBuffer();
		ShowType[] showTypes = ShowType.values();
		for (ShowType showType : showTypes) {
			ShowGroupService showGroupService = ShowGroupServiceFactory.getShowGroupStrategy(showType.getId());
			if(showGroupService.checkShowType(deviceItemExtend)){
				types=types.append(showType.getId()).append(",") ;
			}

		}
		if(Func.isEmpty(types)){
			return "";
		}
		String showType=types.toString();
		return showType.substring(0,showType.length()-1);
	}

	public Map<String, DeviceItem> buildItemMap(List<DeviceItem> deviceItems,Map<String,DeviceItemRealTimeData> deviceItemRdMap) {
		if (deviceItems.isEmpty()) {
			return null;
		} else {
			Map<String, DeviceItem> idItemMap = new HashMap();
			for(DeviceItem deviceItem:deviceItems){
				if(Func.isEmpty(deviceItemRdMap)){
					deviceItem.setVal(GwsubscribeConstant.ITEM_INITIAL_VALUE);
					idItemMap.put(deviceItem.getId(), deviceItem);
					continue;
				}
				DeviceItemRealTimeData realTimeData=deviceItemRdMap.get(deviceItem.getId());
				if(Func.isNotEmpty(realTimeData)){
					deviceItem.setVal(realTimeData.getVal());
				}else{
					deviceItem.setVal(GwsubscribeConstant.ITEM_INITIAL_VALUE);
				}
				idItemMap.put(deviceItem.getId(), deviceItem);

			}
			return idItemMap;
		}
	}

	public Map<String, DeviceItemRealTimeData> buildItemRdMap(List<DeviceItemRealTimeData> deviceItems) {
		if (Func.isEmpty(deviceItems)) {
			return null;
		} else {
			Map<String, DeviceItemRealTimeData> idItemMap = new HashMap();
			for(DeviceItemRealTimeData deviceItem:deviceItems){
				idItemMap.put(deviceItem.getId(), deviceItem);
			}
			return idItemMap;
		}
	}
}
