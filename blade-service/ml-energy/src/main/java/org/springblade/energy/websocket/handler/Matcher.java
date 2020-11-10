package org.springblade.energy.websocket.handler;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelMatcher;

/**
 * @author bond
 * @date 2020/4/21 14:47
 * @desc
 */
public class Matcher implements ChannelMatcher {

	private Channel myChannel;

	public Matcher(Channel myChannel) {
		this.myChannel = myChannel;
	}


	@Override
	public boolean matches(Channel channel) {
		if (myChannel.equals(channel)) {
			return true;
		}
		else return  false;
	}
}
