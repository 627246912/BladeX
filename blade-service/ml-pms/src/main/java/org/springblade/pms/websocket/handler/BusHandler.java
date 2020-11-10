package org.springblade.pms.websocket.handler;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.pms.bigscreen.handler.BigScreenHandler;
import org.springblade.pms.bigscreen.handler.BigScreenStationHandler;
import org.springblade.pms.websocket.constant.SocketConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bond
 * @date 2020/4/17 17:36
 * @desc
 */
@Repository
public class BusHandler {

	@Autowired
	private BladeRedisCache redisCache;
	@Autowired
	private BigScreenHandler bigScreenHandler;
	@Autowired
	private BigScreenStationHandler bigScreenStationHandler;

	//@Async
	public void handler(ChannelHandlerContext ctx, String message){
		JSONObject params= JSONObject.parseObject(message);

		//大屏端首页
		if(params.get(SocketConstant.METHOD).equals(SocketConstant.getBigScreenData)) {
			String areaCode =params.getString("areaCode");

			//websocket请求放入redis
			Map<Object,Object> map = new HashMap<>();
			map.put(ctx.channel().id().toString(),params);
			String key= SocketConstant.websocket_kek;
			redisCache.hMset(key,map);
			redisCache.expire(key, SocketConstant.EXPIRE_TIME);

			bigScreenHandler.pushBigScreenData(params,ctx.channel());
		}

		//大屏端站点数据
		if(params.get(SocketConstant.METHOD).equals(SocketConstant.getBigScreenStationData)) {
			String stationId =params.getString("stationId");

			if(Func.isEmpty(stationId)){
				ChannelHandlerPool.channelGroup.writeAndFlush(new TextWebSocketFrame(R.fail("站点id不能为空，请检查").toString()), new Matcher(ctx.channel()));
				return;
			}

			//websocket请求放入redis
			Map<Object,Object> map = new HashMap<>();
			map.put(ctx.channel().id().toString(),params);
			String key= SocketConstant.websocket_kek;
			redisCache.hMset(key,map);
			redisCache.expire(key, SocketConstant.EXPIRE_TIME);

			bigScreenStationHandler.pushBigScreenStationData(params,ctx.channel());
		}

	}

	public void remove(ChannelHandlerContext ctx){
		String key= SocketConstant.websocket_kek;
		redisCache.hDel(key,ctx.channel().id().toString());

	}

}
