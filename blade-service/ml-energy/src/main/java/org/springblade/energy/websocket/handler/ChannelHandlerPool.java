package org.springblade.energy.websocket.handler;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author bond
 * @date 2020/4/16 19:03
 * @desc
 */
public class ChannelHandlerPool {


	public ChannelHandlerPool(){}

	public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

}
