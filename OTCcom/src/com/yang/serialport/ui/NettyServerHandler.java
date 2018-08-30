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
import java.util.Date;
import java.util.HashMap;

import javax.swing.JTextArea;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
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
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
@Sharable 
public class NettyServerHandler extends ChannelHandlerAdapter{
	
	public String ip;
    public String connet;
    public String fitemid;
    public Thread workThread;
    public HashMap<String, Socket> websocket;
    public ArrayList<String> listarray1 = new ArrayList<String>();
    public ArrayList<String> listarray2 = new ArrayList<String>();
    public ArrayList<String> listarray3 = new ArrayList<String>();
    public ArrayList<String> listarrayJN = new ArrayList<String>();  //任务、焊工、焊机、状态
	private SocketChannel socketChannel = null;
	public JTextArea dataView = new JTextArea();
	public SocketChannel chcli = null;
	public Date timetran;
	public long timetran1;
	public Date time11;
	public long timetran2;
	public Date time22;
	public long timetran3;
	public Date time33;
	
	 @Override  
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		 
		 /*String str = (String) msg;
		 byte[] a = str.getBytes();
		 Workspace ws = new Workspace(str);
         workThread = new Thread(ws);  
         workThread.start(); */
         
		 InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
         String clientIP = insocket.getAddress().getHostAddress();
		 
		 ByteBuf buf=(ByteBuf)msg; 
		 byte[] req=new byte[buf.readableBytes()];  
	     buf.readBytes(req);
	     Workspace ws = new Workspace(req);
         workThread = new Thread(ws);  
         workThread.start();
         
		 /*String str = (String) msg;
		 byte[] req=str.getBytes();
         Workspace ws = new Workspace(req);
         workThread = new Thread(ws);  
         workThread.start();*/
		 
	}
 		 
	 
	 public class Workspace implements Runnable{

		private byte[] req;
		public String str="";

		public Workspace(byte[] req) {
			// TODO Auto-generated constructor stub
			this.req = req;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try{
				
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
				
				  if(str.length()>=6){
					  
					  if(str.substring(0,2).equals("7E") && (str.substring(4,6).equals("22") || (str.substring(4,6).equals("20") && str.substring(6,8).equals("22")))){
						  
						  //str = transOTC(str);
						  str = transJN(str);
						  str=str.substring(0,106)+fitemid+"F5";
				          //dataView.append("OTC:" + str + "\r\n");
				          
				          try{
				        	 chcli.writeAndFlush(str).sync();
							 System.out.println("OTC:" + str);
				          }catch(Exception ex){
							 //ex.printStackTrace();
				 			 //dataView.setText("服务器未开启" + "\r\n");
				 			 System.out.println("服务器未开启");
				          }
				          
				          str = "";
				          
					  }else if(str.substring(0,2).equals("FA")){
						  
						  str=str.substring(0,106)+fitemid+"F5";
				          //dataView.append("实时:" + str + "\r\n");
				          
				          try{
				        	 chcli.writeAndFlush(str).sync();
					         System.out.println("实时:" + str);
				          }catch(Exception ex){
							 //ex.printStackTrace();
				 			 //dataView.setText("服务器未开启" + "\r\n");
				 			 System.out.println("服务器未开启");
				          }
				          
				          str = "";
						  
					  }else{
						  
						  //dataView.append("上行:" + str + "\r\n");
						  
						  try{
		        	  		 chcli.writeAndFlush(str).sync();
							 System.out.println("上行:" + str);
				          }catch(Exception ex){
							 //ex.printStackTrace();
				 			 //dataView.setText("服务器未开启" + "\r\n");
							 System.out.println("服务器未开启");
				          }
						  
						  str = "";
						  
					  }
				  }
				
		          /*byte[] data=new byte[str.length()/2];
		          for (int i1 = 0; i1 < data.length; i1++)
		          {
			            String tstr1=str.substring(i1*2, i1*2+2);
			            Integer k=Integer.valueOf(tstr1, 16);
			            data[i1]=(byte)k.byteValue();
		          }*/
		         
		          //socketChannel.write(ByteBuffer.wrap(data));
		          //System.out.println(str);
		          
		          //new Thread(work).start();
			}catch(Exception ex){
				 ex.printStackTrace();
	 			 //dataView.append("数据接收错误" + "\r\n");
			}
		}
		
		private String transJN(String str) {
			// TODO Auto-generated method stub
			String strdata1=str;
            String strdata2=strdata1.replaceAll("7C20", "00");
            String strdata3=strdata2.replaceAll("7C5E", "7E");
            String strdata4=strdata3.replaceAll("7C5C", "7C");
            String strdata =strdata4.replaceAll("7C5D", "7D");
            
            String weld = Integer.toString(Integer.valueOf(strdata.substring(2,4), 16));
            if(weld.length()<4){
            	int length = 4 - weld.length();
            	for(int i=0;i<length;i++){
            		weld = "0" + weld;
            	}
            }
            
            //江南任务下发
            String welder = "0000";
            String code = "00000000";
            for(int i=0;i<listarrayJN.size();i+=5){
            	if(weld.equals(listarrayJN.get(i+4))){
            		welder = listarrayJN.get(i+1);
            		welder = Integer.toHexString(Integer.valueOf(welder));
            		if(welder.length()<4){
                    	int length = 4 - welder.length();
                    	for(int j=0;j<length;j++){
                    		welder = "0" + welder;
                    	}
                    }
            		
            		code = listarrayJN.get(i);
            		code = Integer.toHexString(Integer.valueOf(code));
            		if(code.length()!=8){
            			int length = 8 - code.length();
            			for(int i1=0;i1<length;i1++){
            				code = "0" + code;
                    	}
            		}
            		code.toUpperCase();
            	}
            }
            
            weld = Integer.toHexString(Integer.valueOf(weld));
            if(weld.length()<4){
            	int length = 4 - weld.length();
            	for(int i=0;i<length;i++){
            		weld = "0" + weld;
            	}
            }
            
            /*String welder1 = Integer.valueOf(strdata.substring(20,22),16).toString(); 
            String welder2 = Integer.valueOf(strdata.substring(22,24),16).toString();
            String welder3 = Integer.valueOf(strdata.substring(24,26),16).toString();
            String welder4 = Integer.valueOf(strdata.substring(26,28),16).toString();
            String welder = welder1 + "," + welder2 + ","+ welder3 + ","+ welder4;
            StringBuffer sbu = new StringBuffer();  
            String[] chars = welder.split(",");  
            for (int i = 0; i < chars.length; i++) {  
                sbu.append((char) Integer.parseInt(chars[i]));  
            }  	
            welder = Integer.toHexString(Integer.valueOf(sbu.toString()));
            if(welder.length()!=4){
            	int lenth = 4 - welder.length();
            	for(int i=0;i<lenth;i++){
            		welder = "0" + welder;
            	}
            }*/
            
            String electricity1 = strdata.substring(28,32);
            
            String electricity2 = strdata.substring(78,82);
            
            String electricity3 = strdata.substring(128,132);
            
            String voltage1 = strdata.substring(32,36);
            
            String voltage2 = strdata.substring(82,86);
            
            String voltage3 = strdata.substring(132,136);
            
            //String code = strdata.substring(48,56);
            
            String status1 = strdata.substring(56,58);
            
            String status2 = strdata.substring(106,108);
            
            String status3 = strdata.substring(156,158);
            
            /*if(First){
            	timetran = new Date();
            	timetran1 = timetran.getTime();
                time11 = new Date(timetran1);
                timetran2 = timetran1 + 1000;
                time22 = new Date(timetran2);
                timetran3 = timetran2 + 1000;
                time33 = new Date(timetran3);
                timetran1 = timetran3;
            	First = false;
            }else{
            	timetran1 = timetran1 + 1000;
                time11 = new Date(timetran1);
            	timetran2 = timetran1 + 1000;
                time22 = new Date(timetran2);
                timetran3 = timetran2 + 1000;
                time33 = new Date(timetran3);
                timetran1 = timetran3;
            }*/
            
            timetran = new Date();
        	timetran1 = timetran.getTime();
            time11 = new Date(timetran1);
            timetran2 = timetran1 + 1000;
            time22 = new Date(timetran2);
            timetran3 = timetran2 + 1000;
            time33 = new Date(timetran3);
            
            String time1 = DateTools.format("yyMMddHHmmss", time11);
            String time2 = DateTools.format("yyMMddHHmmss", time22);
            String time3 = DateTools.format("yyMMddHHmmss", time33);
            
            String year1 = time1.substring(0,2);
            String year161 = Integer.toHexString(Integer.valueOf(year1));
            year161=year161.toUpperCase();
            if(year161.length()==1){
            	year161='0'+year161;
          	}
            String month1 = time1.substring(2,4);
            String month161 = Integer.toHexString(Integer.valueOf(month1));
            month161=month161.toUpperCase();
            if(month161.length()==1){
            	month161='0'+month161;
          	}
            String day1 = time1.substring(4,6);
            String day161 = Integer.toHexString(Integer.valueOf(day1));
            day161=day161.toUpperCase();
            if(day161.length()==1){
            	day161='0'+day161;
          	}
            String hour1 = time1.substring(6,8);
            String hour161 = Integer.toHexString(Integer.valueOf(hour1));
            hour161=hour161.toUpperCase();
            if(hour161.length()==1){
            	hour161='0'+hour161;
          	}
            String minute1 = time1.substring(8,10);
            String minute161 = Integer.toHexString(Integer.valueOf(minute1));
            minute161=minute161.toUpperCase();
            if(minute161.length()==1){
            	minute161='0'+minute161;
          	}
            String second1 = time1.substring(10,12);
            String second161 = Integer.toHexString(Integer.valueOf(second1));
            second161=second161.toUpperCase();
            if(second161.length()==1){
            	second161='0'+second161;
          	}
            
            String year2 = time2.substring(0,2);
            String year162 = Integer.toHexString(Integer.valueOf(year2));
            year162=year162.toUpperCase();
            if(year162.length()==1){
            	year162='0'+year162;
          	}
            String month2 = time2.substring(2,4);
            String month162 = Integer.toHexString(Integer.valueOf(month2));
            month162=month162.toUpperCase();
            if(month162.length()==1){
            	month162='0'+month162;
          	}
            String day2 = time2.substring(4,6);
            String day162 = Integer.toHexString(Integer.valueOf(day2));
            day162=day162.toUpperCase();
            if(day162.length()==1){
            	day162='0'+day162;
          	}
            String hour2 = time2.substring(6,8);
            String hour162 = Integer.toHexString(Integer.valueOf(hour2));
            hour162=hour162.toUpperCase();
            if(hour162.length()==1){
            	hour162='0'+hour162;
          	}
            String minute2 = time2.substring(8,10);
            String minute162 = Integer.toHexString(Integer.valueOf(minute2));
            minute162=minute162.toUpperCase();
            if(minute162.length()==1){
            	minute162='0'+minute162;
          	}
            String second2 = time2.substring(10,12);
            String second162 = Integer.toHexString(Integer.valueOf(second2));
            second162=second162.toUpperCase();
            if(second162.length()==1){
            	second162='0'+second162;
          	}
            
            String year3 = time3.substring(0,2);
            String year163 = Integer.toHexString(Integer.valueOf(year3));
            year163=year163.toUpperCase();
            if(year163.length()==1){
            	year163='0'+year163;
          	}
            String month3 = time3.substring(2,4);
            String month163 = Integer.toHexString(Integer.valueOf(month3));
            month163=month163.toUpperCase();
            if(month163.length()==1){
            	month163='0'+month163;
          	}
            String day3 = time3.substring(4,6);
            String day163 = Integer.toHexString(Integer.valueOf(day3));
            day163=day163.toUpperCase();
            if(day163.length()==1){
            	day163='0'+day163;
          	}
            String hour3 = time3.substring(6,8);
            String hour163 = Integer.toHexString(Integer.valueOf(hour3));
            hour163=hour163.toUpperCase();
            if(hour163.length()==1){
            	hour163='0'+hour163;
          	}
            String minute3 = time3.substring(8,10);
            String minute163 = Integer.toHexString(Integer.valueOf(minute3));
            minute163=minute163.toUpperCase();
            if(minute163.length()==1){
            	minute163='0'+minute163;
          	}
            String second3 = time3.substring(10,12);
            String second163 = Integer.toHexString(Integer.valueOf(second3));
            second163=second163.toUpperCase();
            if(second163.length()==1){
            	second163='0'+second163;
          	}
            
            /*String status1 = "00";
            String status2 = "00";
            String status3 = "00";
            if(electricityint1 != 0){
            	status1 = "03";
            }else if(electricityint2 != 0){
            	status2 = "03";
            }else if(electricityint3 != 0){
            	status3 = "03";
            }*/
            
            String datesend = "00003101" + weld + welder + code 
            + electricity1 + voltage1 + "0000" + status1 + year161 + month161 + day161 + hour161 + minute161 + second161 
            + electricity2 + voltage2 + "0000" + status2 + year162 + month162 + day162 + hour162 + minute162 + second162
            + electricity3 + voltage3 + "0000" + status3 + year163 + month163 + day163 + hour163 + minute163 + second163;
            
            int check = 0;
            byte[] data1=new byte[datesend.length()/2];
			for (int i = 0; i < data1.length; i++)
			{
				String tstr1=datesend.substring(i*2, i*2+2);
				Integer k=Integer.valueOf(tstr1, 16);
				check += k;
			}

			String checksend = Integer.toHexString(check);
			int a = checksend.length();
			checksend = checksend.substring(a-2,a);
			checksend = checksend.toUpperCase();
			
			datesend = "FA" + datesend + checksend + "F5";
			datesend = datesend.toUpperCase();
            
            return datesend;
		}
		
		private String transOTC(String str) {
			// TODO Auto-generated method stub
			String strdata1=str;
            String strdata2=strdata1.replaceAll("7C20", "00");
            String strdata3=strdata2.replaceAll("7C5E", "7E");
            String strdata4=strdata3.replaceAll("7C5C", "7C");
            String strdata =strdata4.replaceAll("7C5D", "7D");
            
            String weld = strdata.substring(2,4);
            if(weld.length()<4){
            	int length = 4 - weld.length();
            	for(int i=0;i<length;i++){
            		weld = "0" + weld;
            	}
            }
            
            String welder1 = Integer.valueOf(strdata.substring(20,22),16).toString(); 
            String welder2 = Integer.valueOf(strdata.substring(22,24),16).toString();
            String welder3 = Integer.valueOf(strdata.substring(24,26),16).toString();
            String welder4 = Integer.valueOf(strdata.substring(26,28),16).toString();
            String welder = welder1 + "," + welder2 + ","+ welder3 + ","+ welder4;
            StringBuffer sbu = new StringBuffer();  
            String[] chars = welder.split(",");  
            for (int i = 0; i < chars.length; i++) {  
                sbu.append((char) Integer.parseInt(chars[i]));  
            }  	
            welder = Integer.toHexString(Integer.valueOf(sbu.toString()));
            if(welder.length()!=4){
            	int lenth = 4 - welder.length();
            	for(int i=0;i<lenth;i++){
            		welder = "0" + welder;
            	}
            }
            
            String electricity1 = strdata.substring(28,32);
            
            String electricity2 = strdata.substring(78,82);
            
            String electricity3 = strdata.substring(128,132);
            
            String voltage1 = strdata.substring(32,36);
            
            String voltage2 = strdata.substring(82,86);
            
            String voltage3 = strdata.substring(132,136);
            
            String code = strdata.substring(48,56);
            
            String status1 = strdata.substring(56,58);
            
            String status2 = strdata.substring(106,108);
            
            String status3 = strdata.substring(156,158);
            
            /*if(First){
            	timetran = new Date();
            	timetran1 = timetran.getTime();
                time11 = new Date(timetran1);
                timetran2 = timetran1 + 1000;
                time22 = new Date(timetran2);
                timetran3 = timetran2 + 1000;
                time33 = new Date(timetran3);
                timetran1 = timetran3;
            	First = false;
            }else{
            	timetran1 = timetran1 + 1000;
                time11 = new Date(timetran1);
            	timetran2 = timetran1 + 1000;
                time22 = new Date(timetran2);
                timetran3 = timetran2 + 1000;
                time33 = new Date(timetran3);
                timetran1 = timetran3;
            }*/
            
            timetran = new Date();
        	timetran1 = timetran.getTime();
            time11 = new Date(timetran1);
            timetran2 = timetran1 + 1000;
            time22 = new Date(timetran2);
            timetran3 = timetran2 + 1000;
            time33 = new Date(timetran3);
            
            String time1 = DateTools.format("yyMMddHHmmss", time11);
            String time2 = DateTools.format("yyMMddHHmmss", time22);
            String time3 = DateTools.format("yyMMddHHmmss", time33);
            
            String year1 = time1.substring(0,2);
            String year161 = Integer.toHexString(Integer.valueOf(year1));
            year161=year161.toUpperCase();
            if(year161.length()==1){
            	year161='0'+year161;
          	}
            String month1 = time1.substring(2,4);
            String month161 = Integer.toHexString(Integer.valueOf(month1));
            month161=month161.toUpperCase();
            if(month161.length()==1){
            	month161='0'+month161;
          	}
            String day1 = time1.substring(4,6);
            String day161 = Integer.toHexString(Integer.valueOf(day1));
            day161=day161.toUpperCase();
            if(day161.length()==1){
            	day161='0'+day161;
          	}
            String hour1 = time1.substring(6,8);
            String hour161 = Integer.toHexString(Integer.valueOf(hour1));
            hour161=hour161.toUpperCase();
            if(hour161.length()==1){
            	hour161='0'+hour161;
          	}
            String minute1 = time1.substring(8,10);
            String minute161 = Integer.toHexString(Integer.valueOf(minute1));
            minute161=minute161.toUpperCase();
            if(minute161.length()==1){
            	minute161='0'+minute161;
          	}
            String second1 = time1.substring(10,12);
            String second161 = Integer.toHexString(Integer.valueOf(second1));
            second161=second161.toUpperCase();
            if(second161.length()==1){
            	second161='0'+second161;
          	}
            
            String year2 = time2.substring(0,2);
            String year162 = Integer.toHexString(Integer.valueOf(year2));
            year162=year162.toUpperCase();
            if(year162.length()==1){
            	year162='0'+year162;
          	}
            String month2 = time2.substring(2,4);
            String month162 = Integer.toHexString(Integer.valueOf(month2));
            month162=month162.toUpperCase();
            if(month162.length()==1){
            	month162='0'+month162;
          	}
            String day2 = time2.substring(4,6);
            String day162 = Integer.toHexString(Integer.valueOf(day2));
            day162=day162.toUpperCase();
            if(day162.length()==1){
            	day162='0'+day162;
          	}
            String hour2 = time2.substring(6,8);
            String hour162 = Integer.toHexString(Integer.valueOf(hour2));
            hour162=hour162.toUpperCase();
            if(hour162.length()==1){
            	hour162='0'+hour162;
          	}
            String minute2 = time2.substring(8,10);
            String minute162 = Integer.toHexString(Integer.valueOf(minute2));
            minute162=minute162.toUpperCase();
            if(minute162.length()==1){
            	minute162='0'+minute162;
          	}
            String second2 = time2.substring(10,12);
            String second162 = Integer.toHexString(Integer.valueOf(second2));
            second162=second162.toUpperCase();
            if(second162.length()==1){
            	second162='0'+second162;
          	}
            
            String year3 = time3.substring(0,2);
            String year163 = Integer.toHexString(Integer.valueOf(year3));
            year163=year163.toUpperCase();
            if(year163.length()==1){
            	year163='0'+year163;
          	}
            String month3 = time3.substring(2,4);
            String month163 = Integer.toHexString(Integer.valueOf(month3));
            month163=month163.toUpperCase();
            if(month163.length()==1){
            	month163='0'+month163;
          	}
            String day3 = time3.substring(4,6);
            String day163 = Integer.toHexString(Integer.valueOf(day3));
            day163=day163.toUpperCase();
            if(day163.length()==1){
            	day163='0'+day163;
          	}
            String hour3 = time3.substring(6,8);
            String hour163 = Integer.toHexString(Integer.valueOf(hour3));
            hour163=hour163.toUpperCase();
            if(hour163.length()==1){
            	hour163='0'+hour163;
          	}
            String minute3 = time3.substring(8,10);
            String minute163 = Integer.toHexString(Integer.valueOf(minute3));
            minute163=minute163.toUpperCase();
            if(minute163.length()==1){
            	minute163='0'+minute163;
          	}
            String second3 = time3.substring(10,12);
            String second163 = Integer.toHexString(Integer.valueOf(second3));
            second163=second163.toUpperCase();
            if(second163.length()==1){
            	second163='0'+second163;
          	}
            
            /*String status1 = "00";
            String status2 = "00";
            String status3 = "00";
            if(electricityint1 != 0){
            	status1 = "03";
            }else if(electricityint2 != 0){
            	status2 = "03";
            }else if(electricityint3 != 0){
            	status3 = "03";
            }*/
            
            String datesend = "00003101" + weld + welder + code 
            + electricity1 + voltage1 + "0000" + status1 + year161 + month161 + day161 + hour161 + minute161 + second161 
            + electricity2 + voltage2 + "0000" + status2 + year162 + month162 + day162 + hour162 + minute162 + second162
            + electricity3 + voltage3 + "0000" + status3 + year163 + month163 + day163 + hour163 + minute163 + second163;
            
            int check = 0;
            byte[] data1=new byte[datesend.length()/2];
			for (int i = 0; i < data1.length; i++)
			{
				String tstr1=datesend.substring(i*2, i*2+2);
				Integer k=Integer.valueOf(tstr1, 16);
				check += k;
			}

			String checksend = Integer.toHexString(check);
			int a = checksend.length();
			checksend = checksend.substring(a-2,a);
			checksend = checksend.toUpperCase();
			
			datesend = "FA" + datesend + checksend + "F5";
			datesend = datesend.toUpperCase();
            
            return datesend;
		}
	 }
	 
	 
	 
	 
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
