package org.springblade.energy.bigscreen.schedule;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import org.springblade.constants.WebsocketConstant;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.energy.bigscreen.handler.BigScreenHandler;
import org.springblade.energy.websocket.handler.ChannelHandlerPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class PushBigScreenJob {

	@Autowired
	private BladeRedisCache redisCache;
	@Autowired
	private BigScreenHandler bigScreenHandler;

	/**
	 *大屏端websocket
	 */
	@Scheduled(initialDelay=1000*60*1,fixedDelay=1000*10)
	public void run() {

		//从redis里面取所有的socket连接数据
		String wkey= WebsocketConstant.websocket_kek;
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

				//大屏端实时数据
				if(param.get(WebsocketConstant.METHOD).equals(WebsocketConstant.getBigScreenData)) {
					bigScreenHandler.pushBigScreenData(param,channel);
				}
			}
		}

	}

}
