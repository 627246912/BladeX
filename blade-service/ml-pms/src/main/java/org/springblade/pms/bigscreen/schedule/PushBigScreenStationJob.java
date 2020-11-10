package org.springblade.pms.bigscreen.schedule;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.pms.bigscreen.handler.BigScreenHandler;
import org.springblade.pms.bigscreen.handler.BigScreenStationHandler;
import org.springblade.pms.websocket.constant.SocketConstant;
import org.springblade.pms.websocket.handler.ChannelHandlerPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class PushBigScreenStationJob {

	@Autowired
	private BladeRedisCache redisCache;
	@Autowired
	private BigScreenHandler bigScreenHandler;
	@Autowired
	private BigScreenStationHandler BigScreenStationHandler;

	//容器启动后,延迟4分钟秒后再执行一次定时器,以后每10秒再执行一次该定时器
	@Scheduled(initialDelay=1000*60*2,fixedDelay=1000*10)
	public void run() {
		//System.out.println("---------------PushBigScreenJob----------------------");
		//从redis里面取所有的socket连接数据
		String wkey= SocketConstant.websocket_kek;
		Map<Object,Object> redisMap=new HashMap<>(redisCache.hGetAll(wkey));

		Map<Object,Object> websocketMap=new HashMap<>();


		//查询连接池里面的channel数据
		Iterator<Channel> iterator = ChannelHandlerPool.channelGroup.iterator();
		//iterator.hasNext()如果存在元素的话返回true
		while(iterator.hasNext()) {
			//iterator.next()返回迭代的下一个元素
			Channel channel=iterator.next();
			Object params= redisMap.get(channel.id().toString());
			if (!StringUtil.isEmpty(params)){
				JSONObject param= (JSONObject) JSONObject.toJSON(params);
				//大屏端站点实时数据
				if(param.get(SocketConstant.METHOD).equals(SocketConstant.getBigScreenStationData)) {
					BigScreenStationHandler.pushBigScreenStationData(param,channel);
				}
			}
		}

	}

}
