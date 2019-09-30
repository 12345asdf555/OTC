package com.yang.serialport.ui;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

//netty连接服务器,若断开自动检测重连
public class ConnectionListenerTest implements ChannelFutureListener {
	public Clientconnect client;  
	public ClientconnectTest clienttest;  
	public SocketChannel socketChannel;
	public ConnectionListenerTest(Clientconnect client) {  
	    this.client = client;  
	}  
	public ConnectionListenerTest() {
		// TODO Auto-generated constructor stub
	}
	public ConnectionListenerTest(ClientconnectTest clientconnectTest) {
		// TODO Auto-generated constructor stub
	    this.clienttest = clientconnectTest;  
	}
	@Override
	public void operationComplete(ChannelFuture channelFuture) throws Exception {
		// TODO Auto-generated method stub
		if (!channelFuture.isSuccess()) {  
		      //System.out.println("Reconnect");  
		      final EventLoop loop = channelFuture.channel().eventLoop();  
		      loop.schedule(new Runnable() {  
			        @Override  
			        public void run() {  
			          //开始连接
			        	clienttest.createBootstrap(new Bootstrap(), loop);  
			        }  
			  }, 1L, TimeUnit.SECONDS);  
		}else{
			  clienttest.NS.chclitest = socketChannel;
		}
	}
	
}

