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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextArea;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;

//接收焊机数据处理
@Sharable 
public class NettyServerHandlerF extends ChannelInboundHandlerAdapter{

	public String ip;
	public String connet;
	public String fitemid;
	public Thread workThread;
	public HashMap<String, Socket> websocket;
	public ArrayList<String> listjunction = new ArrayList<String>();
	public ArrayList<String> listwelder = new ArrayList<String>();
	public ArrayList<String> listweld = new ArrayList<String>();
	public ArrayList<String> listarrayJN = new ArrayList<String>();  //任务、焊工、焊机、状态
	public JTextArea dataView = new JTextArea();
	public SocketChannel chcli = null;
	public Date timetran;
	public long timetran1;
	public Date time11;
	public long timetran2;
	public Date time22;
	public long timetran3;
	public Date time33;
	public int pantime = 1;
	public HashMap<String, String> hm;

	private boolean first1 = true;

	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String buf  = ByteBufUtil.hexDump((ByteBuf)msg);
		String data = convertHexToString(buf);

		if(data.substring(0,2).equals("00")){

			//ctx.writeAndFlush(msg).sync();
			//获取job列表
			String datasend = "00";
			String[] databuf = data.split("//");
			for(int i=3;i<databuf.length;i+=3){
				if(i == 3){
					datasend = datasend + ":" +  databuf[i].substring(databuf[i].length()-1,databuf[i].length()) + "," + databuf[i+1] + ";";
				}else if(i == databuf.length-1){
					break;
				}else{
					datasend = datasend + databuf[i].substring(2,databuf[i].length()) + "," + databuf[i+1] + ";";
				}
			}
			chcli.writeAndFlush(datasend).sync();

		}else if(data.substring(0,2).equals("10")){
			/*byte[] a = ("04"+data.substring(2,data.length())).getBytes();
			ctx.writeAndFlush(Unpooled.copiedBuffer(a)).sync();*/
			//ctx.writeAndFlush(msg).sync();
			//获取job详细数据
			String datasend1 = "01";
			String[] databuf1 = data.split("//");
			datasend1 =  datasend1 + ":" + databuf1[4] + ":" + databuf1[5] + databuf1[6].substring(0,databuf1[6].length()-1);

			chcli.writeAndFlush(datasend1).sync();

		}else if(data.substring(0,2).equals("01")){
			/*byte[] a = ("04"+data.substring(2,data.length())).getBytes();
			ctx.writeAndFlush(Unpooled.copiedBuffer(a)).sync();*/
			//ctx.writeAndFlush(msg).sync();
			//获取job详细数据
			String datasend1 = "01";
			String[] databuf1 = data.split("//");
			String[] databuf2 = databuf1[3].split("\\{");
			datasend1 =  datasend1 + ":" + databuf1[4] + ":" + databuf1[5] + databuf1[6].substring(0,databuf1[6].length()-1) + ":" + databuf2[1];

			chcli.writeAndFlush(datasend1).sync();

		}else if(data.substring(0,2).equals("02")){

			//ctx.writeAndFlush(msg).sync();
			//获取福尼斯实时
			String datasend2 = "02";
			String[] databuf2 = data.split("//");
			String[] tranbuf = databuf2[13].split("\\{");
			datasend2 = datasend2 + ":" + tranbuf[1] + "," +  databuf2[19] + "," +  databuf2[26];
			datasend2 = datasend2.substring(0, datasend2.length()-2);
			chcli.writeAndFlush(datasend2).sync();

		}else if(data.substring(0,2).equals("03")){

			//ctx.writeAndFlush(msg).sync();
			//获取福尼斯曲线
			String datasend3 = "03";
			String[] databuf3 = data.split("//");
			for(int i=5;i<databuf3.length;i+=5){
				if(i == 5){
					String[] tranbuf1 = databuf3[i+5].split("\\{");
					datasend3 = datasend3 + ":" +  databuf3[i].substring(9,databuf3[i].length()) + "," + databuf3[i+1] + "," + databuf3[i+2] + "," + databuf3[i+3] + "," + databuf3[i+4] + "," + tranbuf1[1] + ";";
				}else if(i == databuf3.length-6){
					String[] tranbuf1 = databuf3[i+5].split("\\}");
					datasend3 = datasend3 + databuf3[i].substring(3,databuf3[i].length()) + "," + databuf3[i+1] + "," + databuf3[i+2] + "," + databuf3[i+3] + "," + databuf3[i+4] + "," + tranbuf1[0] + ";";
					break;
				}else{
					String[] tranbuf1 = databuf3[i+5].split("\\{");
					datasend3 = datasend3 + databuf3[i].substring(3,databuf3[i].length()) + "," + databuf3[i+1] + "," + databuf3[i+2] + "," + databuf3[i+3] + "," + databuf3[i+4] + "," + tranbuf1[1] + ";";
				}
			}
			chcli.writeAndFlush(datasend3).sync();

		}else if(data.substring(0,2).equals("04")){

			chcli.writeAndFlush(data).sync();

		}
	}

	//福尼斯string转asc
	public String convertHexToString(String hex) { 		
		StringBuilder sb = new StringBuilder();		
		StringBuilder temp = new StringBuilder(); 		
		// 564e3a322d302e312e34 split into two characters 56, 4e, 3a...		
		for (int i = 0; i < hex.length() - 1; i += 2) { 			
			// grab the hex in pairs			
			String output = hex.substring(i, (i + 2));			
			// convert hex to decimal			
			int decimal = Integer.parseInt(output, 16);			
			// convert the decimal to character			
			sb.append((char) decimal); 			
			temp.append(decimal);		
		}		
		// System.out.println(sb.toString());		
		return sb.toString();	
	}
	
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {  
		//super.channelReadComplete(ctx);  
		ctx.flush();  
	} 
	@Override  
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {  
		ctx.close();  
	} 

}
