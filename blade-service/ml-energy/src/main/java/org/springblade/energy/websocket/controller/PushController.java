package org.springblade.energy.websocket.controller;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.AllArgsConstructor;
import org.springblade.core.cache.utils.CacheUtil;
import org.springblade.energy.websocket.handler.ChannelHandlerPool;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;

/**
 * @author bond
 * @date 2020/4/17 10:25
 * @desc
 */
@RestController
@AllArgsConstructor
@RequestMapping("/socket")
public class PushController {

	//private ChannelMatcher channelMatcher;

	/**
	 * 详情
	 */
	@GetMapping("/push")
	public void push(String str) {

		Iterator<Channel> iterator = ChannelHandlerPool.channelGroup.iterator();

		//iterator.hasNext()如果存在元素的话返回true
		while(iterator.hasNext()) {
			//iterator.next()返回迭代的下一个元素
			Channel channel=iterator.next();
			channel.writeAndFlush( new TextWebSocketFrame(str));


		}
		CacheUtil.put("websocket","1","2","我是谁");

		System.out.println(CacheUtil.getCache("websocket"));
		ChannelHandlerPool.channelGroup.writeAndFlush( new TextWebSocketFrame(str));

	}
}
