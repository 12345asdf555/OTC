package com.yang.serialport.ui;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JTextArea;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
@Sharable 
public class NettyServerHandler extends ChannelHandlerAdapter{
	
	public String ip;
    public String str="";
    public String connet;
    protected String fitemid;
    public HashMap<String, Socket> websocket;
    public ArrayList<String> listarray1 = new ArrayList<String>();
    public ArrayList<String> listarray2 = new ArrayList<String>();
    public ArrayList<String> listarray3 = new ArrayList<String>();
	private SocketChannel socketChannel = null;
	public JTextArea dataView = new JTextArea();
	
	 @Override  
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		 
		 ByteBuf buf=(ByteBuf)msg;  
         byte[] req=new byte[buf.readableBytes()];  
         buf.readBytes(req); 
 		
 		 try {    
 			 
         	if(socketChannel==null){
         		
         		try {
						FileInputStream in = new FileInputStream("IPconfig.txt");  
			            InputStreamReader inReader = new InputStreamReader(in, "UTF-8");  
			            BufferedReader bufReader = new BufferedReader(inReader);  
			            String line = null; 
			            int writetime=0;
						
					    while((line = bufReader.readLine()) != null){ 
					    	if(writetime==0){
				                ip=line;
				                writetime++;
					    	}
					    	else{
					    		fitemid=line;
					    		writetime=0;
					    	}
			            }  

					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					   
					if(fitemid.length()!=2){
	            		int count = 2-fitemid.length();
	            		for(int i=0;i<count;i++){
	            			fitemid="0"+fitemid;
	            		}
	            	}
         		
					socketChannel = SocketChannel.open(); 
	                SocketAddress socketAddress = new InetSocketAddress(ip, 5555);    
	                socketChannel.connect(socketAddress);
         	}
 		
         
         
         	for(int i=0;i<req.length;i++){
             
             //判断为数字还是字母，若为字母+256取正数
             if(req[i]<0){
               String r = Integer.toHexString(req[i]+256);
               String rr=r.toUpperCase();
                 //数字补为两位数
                 if(rr.length()==1){
                 rr='0'+rr;
                 }
                 //strdata为总接收数据
               str += rr;
               
             }
             else{
            	 String r = Integer.toHexString(req[i]);
                 if(r.length()==1)
                 r='0'+r;
                 r=r.toUpperCase();
                 str+=r;  
              }
           }
          
          str=str.substring(0,106)+fitemid+"F5";
         
          dataView.append(str + "\r\n");
          
          byte[] data=new byte[str.length()/2];
          for (int i1 = 0; i1 < data.length; i1++)
          {
	            String tstr1=str.substring(i1*2, i1*2+2);
	            Integer k=Integer.valueOf(tstr1, 16);
	            data[i1]=(byte)k.byteValue();
          }
          
          socketChannel.write(ByteBuffer.wrap(data));
         
          str = "";
          //System.out.println(str);
          
          //new Thread(work).start();
 		 } catch (Exception ex) {  
 			 socketChannel = null;
 			 dataView.setText("服务器未开启" + "\r\n");
         }
	 }
	 
	 /*public Runnable work = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			new Mysql(str,connet,listarray1);
	        new Socketsend(str,ip1);
	        new Websocket(str,connet,websocket,listarray2,listarray3);
			str = "";
		} 
		 
	 };*/
	 
	 @Override  
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {  
		 super.channelReadComplete(ctx);  
	     ctx.flush();  
	 } 
     @Override  
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {  
         ctx.close();  
     } 
	 
}
