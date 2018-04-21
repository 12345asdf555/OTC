package com.yang.serialport.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.CharsetUtil;


public class TcpClientHandler extends ChannelHandlerAdapter {
	
	public Client client;
	public HashMap<String, SocketChannel> socketlist;
	public String socketfail;
	
	public TcpClientHandler(Client client) {
		// TODO Auto-generated constructor stub
		this.client = client;
	}
	
	public TcpClientHandler() {
		
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		 String str = (String) msg;
		 Iterator<Entry<String, SocketChannel>> webiter = client.mainFrame.socketlist.entrySet().iterator();
         while(webiter.hasNext()){
         	try{
         		
             	Entry<String, SocketChannel> entry = (Entry<String, SocketChannel>) webiter.next();
             	socketfail = entry.getKey();
             	SocketChannel socketcon = entry.getValue();
             	socketcon.writeAndFlush(Unpooled.copiedBuffer(str,CharsetUtil.UTF_8));
             	
             	client.mainFrame.DateView(str);
             	
         	}catch (Exception e) {
					socketlist.remove(socketfail);
					webiter = socketlist.entrySet().iterator();
			}
         }
	}
	
	@Override  
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {  
		 super.channelReadComplete(ctx);  
	     ctx.flush();  
	 } 
	
	@Override  
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {  
	
      final EventLoop eventLoop = ctx.channel().eventLoop();  
      eventLoop.schedule(new Runnable() {  
    	@Override  
        public void run() {  
    		client.createBootstrap(new Bootstrap(),eventLoop);
        }  
      }, 1L, TimeUnit.SECONDS);  
      super.channelInactive(ctx);  
    }  
	
    @Override  
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {  
         ctx.close();  
    } 
	
}
