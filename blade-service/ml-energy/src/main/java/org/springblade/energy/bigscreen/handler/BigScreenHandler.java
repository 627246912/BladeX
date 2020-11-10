package org.springblade.energy.bigscreen.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springblade.constants.BigScreenRedis;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.utils.Func;
import org.springblade.dto.NameValue;
import org.springblade.energy.bigscreen.dto.BigSrceenData;
import org.springblade.energy.bigscreen.dto.BigSrceenHandleAlarmData;
import org.springblade.energy.bigscreen.dto.Nenghaogongshi;
import org.springblade.energy.websocket.handler.ChannelHandlerPool;
import org.springblade.energy.websocket.handler.Matcher;
import org.springblade.util.BigDecimalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bond
 * @date 2020/8/1 15:06
 * @desc
 */
@Component
public class BigScreenHandler {

	@Autowired
	private BladeRedisCache redisCache;
	@Autowired
	private BigScreenRedisComTrue bigScreenRedisComTrue;
	/**
	 * 大屏端推送数据
	 * @param params
	 * @param channel
	 */
	public void pushBigScreenData(JSONObject params, Channel channel) {
		Long stationId =Long.valueOf(params.getString("stationId"));
		Map<String,Object> resMap=getData(stationId);

		String data= JSON.toJSONString(resMap);
		ChannelHandlerPool.channelGroup.writeAndFlush(new TextWebSocketFrame(data), new Matcher(channel));
	}


	public Map<String,Object> getData(Long stationId)
	{
		Map<String,Object> resMap=new HashMap<>();

		//平台概述
		Map<String,Integer> pingtaigaishu=new HashMap<>();
		pingtaigaishu.put("siteNum",redisCache.get(BigScreenRedis.siteNum+stationId));
		pingtaigaishu.put("alarmNum",bigScreenRedisComTrue.alarmNum(stationId));
		pingtaigaishu.put("runDay",redisCache.get(BigScreenRedis.runDay+stationId));
		resMap.put("pingtaigaishu",pingtaigaishu);

		//能耗公示
		List<Nenghaogongshi> nenghaogongshi=redisCache.get(BigScreenRedis.stationPowerNenghaogongshi+stationId);
		resMap.put("nenghaogongshi",JSONArray.parseArray(JSON.toJSONString(nenghaogongshi)));

		//能源构成
		List<NameValue<Integer>> nengyuangoucheng=new ArrayList<>();
		if(Func.isNotEmpty(nenghaogongshi)) {
			for (Nenghaogongshi data : nenghaogongshi) {
				NameValue a = new NameValue();
				a.setValue(data.getThisMothVal());
				a.setName(data.getSiteName());
				nengyuangoucheng.add(a);
			}
		}
		resMap.put("nengyuangoucheng",JSONArray.parseArray(JSON.toJSONString(nengyuangoucheng)));

		//年度累计消耗
		List<BigSrceenData> datas=new ArrayList<>();
		BigSrceenData stationYearPowerTotalVal=redisCache.get(BigScreenRedis.stationYearPowerTotalVal+stationId);
		datas.add(stationYearPowerTotalVal);
		BigSrceenData stationYearWaterTotalVal=redisCache.get(BigScreenRedis.stationYearWaterTotalVal+stationId);
		datas.add(stationYearWaterTotalVal);
		BigSrceenData stationYearGasTotalVal=redisCache.get(BigScreenRedis.stationYearGasTotalVal+stationId);
		datas.add(stationYearGasTotalVal);
		//一度电=0.1229千克标准煤
		//一吨水=0.086千克标准煤
		//一立方天然气=1.3300千克标准煤
		BigSrceenData stationYearTCETotalVal=new BigSrceenData();
		stationYearTCETotalVal.setName("综合能耗");
		stationYearTCETotalVal.setUnit("tce");
		Float thisVal= BigDecimalUtil.mulF(stationYearPowerTotalVal.getThisVal(),0.1229F)+
			BigDecimalUtil.mulF(stationYearWaterTotalVal.getThisVal(),0.086F)+
			BigDecimalUtil.mulF(stationYearGasTotalVal.getThisVal(),1.33f);
		Float lasVal= BigDecimalUtil.mulF(stationYearPowerTotalVal.getLastVal(),0.1229F)+
			BigDecimalUtil.mulF(stationYearWaterTotalVal.getLastVal(),0.086F)+
			BigDecimalUtil.mulF(stationYearGasTotalVal.getLastVal(),1.33f);
		stationYearTCETotalVal.setThisVal(thisVal);
		stationYearTCETotalVal.setLastVal(lasVal);
		datas.add(stationYearTCETotalVal);

		resMap.put("nianduleijixiaohao",JSONArray.parseArray(JSON.toJSONString(datas)));

		//月度累计消耗看板
		List<BigSrceenData> mdatas=new ArrayList<>();
		BigSrceenData stationMonthPowerTotalVal=redisCache.get(BigScreenRedis.stationMonthPowerTotalVal+stationId);
		mdatas.add(stationMonthPowerTotalVal);
		BigSrceenData stationMonthWaterTotalVal=redisCache.get(BigScreenRedis.stationMonthWaterTotalVal+stationId);
		mdatas.add(stationMonthWaterTotalVal);
		BigSrceenData stationMonthGasTotalVal=redisCache.get(BigScreenRedis.stationMonthGasTotalVal+stationId);
		mdatas.add(stationMonthGasTotalVal);
		mdatas.add(redisCache.get(BigScreenRedis.stationMonthAircPowerTotalVal+stationId));
		mdatas.add(redisCache.get(BigScreenRedis.stationMonthAircGasTotalVal+stationId));
		resMap.put("yueduleijixiaohao",JSONArray.parseArray(JSON.toJSONString(mdatas)));

		//能源成本
		List<NameValue<Float>> nengyuanchengben=new ArrayList<>();
		Float stationMonthPowerCost=stationMonthPowerTotalVal.getLastCost();
		Float stationMonthWaterCost=stationMonthWaterTotalVal.getLastCost();
		Float stationMonthGasCost=stationMonthGasTotalVal.getLastCost();

		NameValue chengben=new NameValue();
		chengben.setValue(stationMonthPowerCost+stationMonthWaterCost+stationMonthGasCost);
		chengben.setName("总额");
		nengyuanchengben.add(chengben);
		NameValue chengben1=new NameValue();
		chengben1.setValue(stationMonthPowerCost);
		chengben1.setName("电能");
		nengyuanchengben.add(chengben1);
		NameValue chengben2=new NameValue();
		chengben2.setValue(stationMonthWaterCost);
		chengben2.setName("水能");
		nengyuanchengben.add(chengben2);
		NameValue chengben3=new NameValue();
		chengben3.setValue(stationMonthGasCost);
		chengben3.setName("气能");
		nengyuanchengben.add(chengben3);
		resMap.put("nengyuanchengben", JSONArray.parseArray(JSON.toJSONString(nengyuanchengben)));




		//高低压电能
		Map<String,Float> gaodiyadianneng=new HashMap<>();
		BigSrceenData gaoya=redisCache.get(BigScreenRedis.stationMonthPowerHighTotalVal+stationId);
		gaodiyadianneng.put("gaoyayongdian",gaoya==null?0f:gaoya.getThisVal());
		BigSrceenData diya=redisCache.get(BigScreenRedis.stationMonthPowerLowTotalVal+stationId);
		gaodiyadianneng.put("diyayongdian",diya==null?0f:diya.getThisVal());
		resMap.put("gaodiyadianneng",gaodiyadianneng);


		//能耗异常统计
		List<BigSrceenHandleAlarmData> bigSrceenHandleAlarmDataList= redisCache.get(BigScreenRedis.stationhandlealarmNum+stationId);
		resMap.put("yichangtongji",JSONArray.parseArray(JSON.toJSONString(bigSrceenHandleAlarmDataList)));



		//绿色能源
		List<NameValue<Integer>> lvsenengyuan=new ArrayList<>();
		NameValue a=new NameValue();
		a.setValue(0);
		a.setName("总发电量");
		lvsenengyuan.add(a);
		NameValue a1=new NameValue();
		a1.setValue(0);
		a1.setName("清洁能源");
		lvsenengyuan.add(a1);
		resMap.put("lvsenengyuan",JSONArray.parseArray(JSON.toJSONString(lvsenengyuan)));


		return resMap;
	}
}
