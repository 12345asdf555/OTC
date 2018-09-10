package com.yang.serialport.ui;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.CharsetUtil;


public class TcpClientHandler extends ChannelHandlerAdapter {
	
	public Clientconnect client;
	public HashMap<String, SocketChannel> socketlist;
	public String socketfail;
	public ArrayList<String> listarrayJN = new ArrayList<String>();
	
	public TcpClientHandler(Clientconnect client) {
		// TODO Auto-generated constructor stub
		this.client = client;
	}
	
	public TcpClientHandler() {
		
	}
	
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		 String str = (String) msg;
		 
		 if(str.substring(0,2).equals("JN")){    //江南派工：任务号id、焊工id、焊机id、状态、焊机编号
			 
			 listarrayJN = client.NS.listarrayJN;
			 String[] JN = str.split(",");
			 
			 if(JN[4].equals("0")){  //任务开始
				 for(int i=1;i<JN.length;i++){
					 listarrayJN.add(JN[i]);
					 client.NS.listarrayJN  = listarrayJN;
				 }
			 }else if(JN[4].equals("1")){  //任务完成
				 for(int i=0;i<listarrayJN.size();i+=5){
					 if(listarrayJN.get(i+1).equals(JN[2])){
						 listarrayJN.set(i, "");
						 listarrayJN.set(i+1, "");
						 listarrayJN.set(i+2, "");
						 listarrayJN.set(i+3, "");
						 client.NS.listarrayJN  = listarrayJN;
						 break;
					 }
				 }
			 }else if(JN[4].equals("2")){  //任务修改
				 for(int i=0;i<listarrayJN.size();i+=4){
					 if(listarrayJN.get(i+1).equals(JN[2])){
						 listarrayJN.set(i, JN[1]);
						 listarrayJN.set(i+1, JN[2]);
						 listarrayJN.set(i+2, JN[3]);
						 listarrayJN.set(i+3, JN[4]);
						 client.NS.listarrayJN  = listarrayJN;
						 break;
					 }
				 }
			 }else if(JN[4].equals("3")){  //任务取消
				 for(int i=0;i<listarrayJN.size();i+=4){
					 if(listarrayJN.get(i+1).equals(JN[2])){
						 listarrayJN.set(i, "");
						 listarrayJN.set(i+1, "");
						 listarrayJN.set(i+2, "");
						 listarrayJN.set(i+3, "");
						 client.NS.listarrayJN  = listarrayJN;
						 break;
					 }
				 }
			 }
			 
			 System.out.println(str);
			 
		 }else{    //处理下发和上传
			 
			 Iterator<Entry<String, SocketChannel>> webiter = client.mainFrame.socketlist.entrySet().iterator();
	         while(webiter.hasNext()){
	         	try{
	         		
	             	Entry<String, SocketChannel> entry = (Entry<String, SocketChannel>) webiter.next();
	             	socketfail = entry.getKey();
	             	SocketChannel socketcon = entry.getValue();
	             	
	             	byte[] data=new byte[str.length()/2];
			        for (int i1 = 0; i1 < data.length; i1++)
			        {
				          String tstr1=str.substring(i1*2, i1*2+2);
				          Integer k=Integer.valueOf(tstr1,16);
				          data[i1]=(byte)k.byteValue();
			        }
	             	
			        ByteBuf byteBuf = Unpooled.buffer();
			        byteBuf.writeBytes(data);
			        
			        try{
			        	
			        	socketcon.writeAndFlush(byteBuf).sync();
		             	client.mainFrame.DateView(str);
		             	
			        }catch (Exception e) {
		         		client.mainFrame.socketlist.remove(socketfail);
		         		webiter = client.mainFrame.socketlist.entrySet().iterator();
						//webiter = socketlist.entrySet().iterator();
					}
	             	
	             	//socketcon.writeAndFlush(Unpooled.copiedBuffer(str,CharsetUtil.UTF_8));
	             	
	         	}catch (Exception e) {
	         		client.mainFrame.DateView("数据接收错误" + "\r\n");
					//webiter = socketlist.entrySet().iterator();
				}
	         }
	         
		 }
	}
	
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {  
		 //super.channelReadComplete(ctx);  
	     ctx.flush();  
	 } 
	
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {  
	
      final EventLoop eventLoop = ctx.channel().eventLoop();  
      eventLoop.schedule(new Runnable() {  
    	@Override  
        public void run() {  
    		client.createBootstrap(new Bootstrap(),eventLoop);
        }  
      }, 1L, TimeUnit.SECONDS);  
      //super.channelInactive(ctx);  
    }  
	
    @Override  
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {  
         ctx.close();  
    } 
	
}
