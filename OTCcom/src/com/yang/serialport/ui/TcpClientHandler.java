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
	public MainFrame mainframe;
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

			 synchronized (listarrayJN) {
			 listarrayJN = client.NS.listarrayJN;
			 String[] JN = str.split(",");
			 
			 if(JN[4].equals("0")){  //任务开始
				 for(int i=1;i<JN.length;i++){
					 listarrayJN.add(JN[i]);
					 client.NS.listarrayJN  = listarrayJN;
				 }
			 }else if(JN[4].equals("1")){  //任务完成
				 for(int i=0;i<listarrayJN.size();i+=5){
					 if(listarrayJN.get(i).equals(JN[1])){
						 
						 String weld = listarrayJN.get(i+2);
						 
						 listarrayJN.set(i, "");
						 listarrayJN.set(i+1, "");
						 listarrayJN.set(i+2, "");
						 listarrayJN.set(i+3, "");
						 listarrayJN.set(i+4, "");
						 
						 for(int j=0;j<listarrayJN.size();j+=5){
							 if(listarrayJN.get(i+2).equals(weld)){
								 listarrayJN.set(i, "");
								 listarrayJN.set(i+1, "");
								 listarrayJN.set(i+2, "");
								 listarrayJN.set(i+3, "");
								 listarrayJN.set(i+4, "");
							 }
						 }
						 
						 client.NS.listarrayJN  = listarrayJN;
						 
					 }
				 }
			 }else if(JN[4].equals("2")){  //任务修改
				 for(int i=0;i<listarrayJN.size();i+=5){
					 if(listarrayJN.get(i).equals(JN[1])){
						 listarrayJN.set(i, JN[1]);
						 listarrayJN.set(i+1, JN[2]);
						 listarrayJN.set(i+2, JN[3]);
						 listarrayJN.set(i+3, JN[4]);
						 listarrayJN.set(i+4, JN[5]);
						 client.NS.listarrayJN  = listarrayJN;
					 }
				 }
			 }else if(JN[4].equals("3")){  //任务取消
				 for(int i=0;i<listarrayJN.size();i+=5){
					 if(listarrayJN.get(i).equals(JN[1])){
						 
						 String weld = listarrayJN.get(i+2);
						 
						 listarrayJN.set(i, "");
						 listarrayJN.set(i+1, "");
						 listarrayJN.set(i+2, "");
						 listarrayJN.set(i+3, "");
						 listarrayJN.set(i+4, "");
						 
						 for(int j=0;j<listarrayJN.size();j+=5){
							 if(listarrayJN.get(i+2).equals(weld)){
								 listarrayJN.set(i, "");
								 listarrayJN.set(i+1, "");
								 listarrayJN.set(i+2, "");
								 listarrayJN.set(i+3, "");
								 listarrayJN.set(i+4, "");
							 }
						 }
						 
						 client.NS.listarrayJN  = listarrayJN;
					 }
				 }
			 }
			 
			 System.out.println(str);
			 }
		 }else{    //处理下发和上传
			 synchronized (client.mainFrame.socketlist) {
				 
			 ArrayList<String> listarraybuf = new ArrayList<String>();
        	 boolean ifdo= false;
				 
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
		             	
			        }catch (Exception e) {
			        	listarraybuf.add(socketfail);
 	                    ifdo = true;
					}
	             	
	         	}catch (Exception e) {
	         		client.mainFrame.DateView("数据接收错误" + "\r\n");
					//webiter = socketlist.entrySet().iterator();
				}
	         }
	         
          	 client.mainFrame.DateView(str);
	         
	         if(ifdo){
	        	 for(int i=0;i<listarraybuf.size();i++){
	        		 client.mainFrame.socketlist.remove(listarraybuf.get(i));
            	 }
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
