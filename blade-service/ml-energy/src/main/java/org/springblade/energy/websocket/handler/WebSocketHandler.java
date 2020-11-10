package org.springblade.energy.websocket.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springblade.energy.config.SpringContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bond
 * @date 2020/4/16 19:01
 * @desc
 */
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

	/**
	 * 通道建立调用
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("与客户端建立连接，通道开启！");

		//将链接存入连接池(自定义)
		ChannelHandlerPool.channelGroup.add(ctx.channel());
	}

	/**
	 * 通道断开调用
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		//System.out.println("与客户端断开连接，通道关闭！");
		//添加到channelGroup 通道组
		ChannelHandlerPool.channelGroup.remove(ctx.channel());
		//关闭通道删除redis缓存数据
		BusHandler busHandler=(BusHandler) SpringContext.getBean("busHandler");
		busHandler.remove(ctx);
	}

	/**
	 * 通道数据读取
	 * @param ctx
	 * @param msg
	 * @throws Exception
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		//首次连接是FullHttpRequest，处理参数 by zhengkai.blog.csdn.net
		if (null != msg && msg instanceof FullHttpRequest) {
			FullHttpRequest request = (FullHttpRequest) msg;
			String uri = request.uri();

			Map paramMap=getUrlParams(uri);
			//System.out.println("接收到的参数是："+JSON.toJSONString(paramMap));
			//如果url包含参数，需要处理
			if(uri.contains("?")){
				String newUri=uri.substring(0,uri.indexOf("?"));
				request.setUri(newUri);
			}

		}else if(msg instanceof TextWebSocketFrame){
			//正常的TEXT消息类型
			TextWebSocketFrame frame=(TextWebSocketFrame)msg;
			//System.out.println("客户端收到服务器数据：" +frame.text());
			//业务处理
			BusHandler busHandler=(BusHandler) SpringContext.getBean("busHandler");
			busHandler.handler(ctx, frame.text());
			//sendAllMessage(ctx, frame.text());
		}
		super.channelRead(ctx, msg);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {

	}

	private void sendAllMessage(ChannelHandlerContext ctx,String message){
		//收到信息后，群发给所有channel
		ChannelHandlerPool.channelGroup.writeAndFlush( new TextWebSocketFrame(message));

	}

	private static Map getUrlParams(String url){
		Map<String,String> map = new HashMap<>();
		url = url.replace("?",";");
		if (!url.contains(";")){
			return map;
		}
		if (url.split(";").length > 0){
			String[] arr = url.split(";")[1].split("&");
			for (String s : arr){
				String key = s.split("=")[0];
				String value = s.split("=")[1];
				map.put(key,value);
			}
			return  map;

		}else{
			return map;
		}
	}
}
