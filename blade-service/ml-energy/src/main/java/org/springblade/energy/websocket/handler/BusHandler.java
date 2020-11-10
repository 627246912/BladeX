package org.springblade.energy.websocket.handler;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springblade.constants.WebsocketConstant;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.bigscreen.handler.BigScreenHandler;
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
	private PushDataItemHandler pushDataItemHandler;

	@Autowired
	private RealAlarmDataHandler realAlarmDataHandler;
	@Autowired
	private BigScreenHandler bigScreenHandler;

	//@Async
	public void handler(ChannelHandlerContext ctx, String message){
		JSONObject params= JSONObject.parseObject(message);


		//系统图的实时数据
		if(params.get(WebsocketConstant.METHOD).equals(WebsocketConstant.TYPE_CHART)) {

			Long stationId =Long.valueOf(params.getString("stationId"));
			Long siteId=Long.valueOf(params.getString("siteId"));
			String diagramType=params.getString("diagramType");

			if(Func.isEmpty(stationId) || Func.isEmpty(siteId) || Func.isEmpty(diagramType) ){

				ChannelHandlerPool.channelGroup.writeAndFlush(new TextWebSocketFrame(R.fail("参数不正确，请检查").toString()), new Matcher(ctx.channel()));
				return;
			}

			//websocket请求放入redis
			Map<Object,Object> map = new HashMap<>();
			map.put(ctx.channel().id().toString(),params);
			String key= WebsocketConstant.websocket_kek;
			redisCache.hMset(key,map);
			redisCache.expire(key, WebsocketConstant.EXPIRE_TIME);


			//查询数据并推送
			//ChannelHandlerPool.channelGroup.writeAndFlush( new TextWebSocketFrame(message));
			pushDataItemHandler.pushDataItme(params,ctx.channel());
		//	ChannelHandlerPool.channelGroup.writeAndFlush(new TextWebSocketFrame(message), new Matcher(ctx.channel()));
		}


		//系统图的 电能质量  实时数据------该功能还没有做
		if(params.get(WebsocketConstant.METHOD).equals(WebsocketConstant.POWER_QUALITY)) {

			Long stationId =Long.valueOf(params.getString("stationId"));
			Long siteId=Long.valueOf(params.getString("siteId"));
			String diagramType=params.getString("diagramType");

			if(Func.isEmpty(stationId) || Func.isEmpty(siteId) || Func.isEmpty(diagramType) ){

				ChannelHandlerPool.channelGroup.writeAndFlush(new TextWebSocketFrame(R.fail("参数不正确，请检查").toString()), new Matcher(ctx.channel()));
				return;
			}

			//查询数据并推送
			//pushDataItemHandler.pushDataItme(params,ctx.channel());
		}


		//大屏端首页
		if(params.get(WebsocketConstant.METHOD).equals(WebsocketConstant.getBigScreenData)) {
			Long stationId =Long.valueOf(params.getString("stationId"));

			if(Func.isEmpty(stationId)){
				ChannelHandlerPool.channelGroup.writeAndFlush(new TextWebSocketFrame(R.fail("站点参数不能为空，请检查").toString()), new Matcher(ctx.channel()));
				return;
			}

			//websocket请求放入redis
			Map<Object,Object> map = new HashMap<>();
			map.put(ctx.channel().id().toString(),params);
			String key= WebsocketConstant.websocket_kek;
			redisCache.hMset(key,map);
			redisCache.expire(key, WebsocketConstant.EXPIRE_TIME);


			bigScreenHandler.pushBigScreenData(params,ctx.channel());
		}

		//租户所有站点未处理告警数据
		if(params.get(WebsocketConstant.METHOD).equals(WebsocketConstant.getRealAlarmData)) {
			realAlarmDataHandler.pushStationAlarmData(params,ctx.channel());
		}

	}

	public void remove(ChannelHandlerContext ctx){
		String key= WebsocketConstant.websocket_kek;
		redisCache.hDel(key,ctx.channel().id().toString());

	}

}
