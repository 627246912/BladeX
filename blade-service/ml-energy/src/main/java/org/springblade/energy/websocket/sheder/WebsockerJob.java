package org.springblade.energy.websocket.sheder;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import org.springblade.constants.WebsocketConstant;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.energy.websocket.handler.ChannelHandlerPool;
import org.springblade.energy.websocket.handler.PushDataItemHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author bond
 * @date 2020/4/21 11:48
 * @desc
 */
@Component
@EnableScheduling // 可以在启动类上注解也可以在当前文件
public class WebsockerJob {
	@Autowired
	private BladeRedisCache redisCache;
	@Autowired
	private PushDataItemHandler pushDataItemHandler;

	@Scheduled(initialDelay=1000*60*2,fixedDelay=1000*10)
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
				//系统图的实时数据
				if(param.get(WebsocketConstant.METHOD).equals(WebsocketConstant.TYPE_CHART)) {
					pushDataItemHandler.pushDataItme(param,channel);
				}

			}
		}

	}

}
