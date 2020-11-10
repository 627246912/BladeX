package org.springblade.energy.websocket.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springblade.energy.websocket.handler.WebSocketHandler;

/**
 * @author bond
 * @date 2020/4/16 18:55
 * @desc
 */
public class NettyServer {

	private final int port;

	public NettyServer(int port){
		this.port = port;
	}

	public void start() throws Exception{
		EventLoopGroup bossGroup = new NioEventLoopGroup();

		EventLoopGroup group = new NioEventLoopGroup();

		try {


			ServerBootstrap sb = new ServerBootstrap();
			sb.option(ChannelOption.SO_BACKLOG,1024);
			sb.group(group,bossGroup)//绑定线程池
				.channel(NioServerSocketChannel.class)//指定使用的channel
				.localAddress(this.port)//绑定监听端口
				.childHandler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						System.out.println("收到新连接");
						//websocket协议本身就是基于http协议的，所以这边也要使用http编解码器
						ch.pipeline().addLast(new HttpServerCodec());
						//以块的方式来写处理器
						ch.pipeline().addLast(new ChunkedWriteHandler());
						ch.pipeline().addLast(new HttpObjectAggregator(8192));
						ch.pipeline().addLast(new WebSocketServerProtocolHandler("/ws",null,true,65536*10));
						ch.pipeline().addLast(new WebSocketHandler());

					}

				});
			//服务器异步创建绑定
			ChannelFuture cf = sb.bind().sync();
			System.out.println(NettyServer.class+" 启动正在监听： "+cf.channel().localAddress());
			//关闭服务器通道
			cf.channel().closeFuture().sync();
		} finally {
			group.shutdownGracefully().sync();
			bossGroup.shutdownGracefully().sync();
		}
	}

}
