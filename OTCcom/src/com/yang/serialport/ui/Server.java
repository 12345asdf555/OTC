package com.yang.serialport.ui;
import java.io.BufferedReader;  
import java.io.BufferedWriter;  
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;  
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.ServerSocket;  
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.websocket.Session;



public class Server implements Runnable {  
	
 	private List<Handler> handlers = new ArrayList<Handler>();  
    public static final String SERVERIP = "121.196.222.216"; 
    public static final int SERVERPORT = 5555;
    public static final int SERVERPORTWEB = 5554;
    public String str = "";
    public Socket socket=null;
    public Socket websocketlink=null;
    public ServerSocket serverSocket = null;
    public boolean webtype = false;
    public int sqlwritetype=0;
    public int websendtype=0;
    public int sockettype=0;
    public String ip;
    public String ip1;
    public String connet1 = "jdbc:mysql://";
    public String connet2 = ":3306/Weld?" + "user=root&password=123456&useUnicode=true&characterEncoding=UTF8"; 
    public String connet;
    public byte b[];


    
    public void run() {
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
		    		ip1=line;
		    		writetime=0;
		    	}
            }  

	    	/*File f = new File("IPconfig.txt");   
	        InputStream ing;
			ing = new FileInputStream(f);
	        b = new byte[1024];   
	        int len = 0;   
	        int temp=0;          //所有读取的内容都使用temp接收   
	        try {
				while((temp=ing.read())!=-1){    //当没有读取完时，继续读取   
				    b[len]=(byte)temp;   
				    len++;   
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	        ip=new String(b,0,len);*/
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		connet=connet1+ip+connet2;
		
    	new Thread(reciver).start();
    	new Thread(mysql).start();
    	new Thread(websocketstart).start();
    	new Thread(websocketsend).start();
    	//new Thread(socketsend).start();
    	
    }  
      
    public Runnable reciver = new Runnable() {
		public void run() {
			  
            try {
            	
				ServerSocket serverSocket = new ServerSocket(SERVERPORT);
				
				
				while (true) {  
					
					synchronized(this) {  
					
	                Socket client = serverSocket.accept();    
	                  
	                try {  
	                    BufferedReader in = new BufferedReader(  
	                            new InputStreamReader(client.getInputStream()));  
	                      
	                    PrintWriter out = new PrintWriter(new BufferedWriter(  
	                            new OutputStreamWriter(client.getOutputStream())),true);  
	                      
	                    int zeroc=0;
	                    int i1=0;
	                    int zerocount=0;
	                    int linecount=0;
	                    str = "";
	                    byte[] datas1 = new byte[262144]; 
	                    client.getInputStream().read(datas1);
	                    for(i1=0;i1<datas1.length;i1++){
	                    	//判断为数字还是字母，若为字母+256取正数
	                    	if(datas1[i1]<0){
	                    		String r = Integer.toHexString(datas1[i1]+256);
	                    		String rr=r.toUpperCase();
	                        	//数字补为两位数
	                        	if(rr.length()==1){
	                    			rr='0'+rr;
	                        	}
	                        	//strdata为总接收数据
	                    		str += rr;
	                    	}
	                    	else{
	                    		String r = Integer.toHexString(datas1[i1]);

	                        	if(r.length()==1)
	                    			r='0'+r;
	                        	r=r.toUpperCase();
	                    		str+=r;	
	                    	}
	                    	linecount+=2;
	                    	
	                    	//去掉后面的0
	                    	if(datas1[i1]==0){
	                    		zerocount++;
	                    		if(zerocount>25){
	                    			str=str.substring(0, linecount-52);
	                    			break;
	                    		}	
	                    	}else{
                    			zerocount=0;
                    		}
	                    }
	                    
	                    sqlwritetype=1;
	                    sockettype=1;
	                    if(str.substring(0, 2).equals("FA")){
		                    websendtype=1;
	                    }
	                    
	                    //System.out.println(str);
	                	//new Thread(mysql).start();
	                    //new Thread(websocketstart).start();
	                    
	                } catch (Exception e) {  
	                    System.out.println("S: Error "+e.getMessage());  
	                    e.printStackTrace();  
	                } finally {  
	                    client.close();  
	                } 
	                
				}
	                
	            }  
	        } catch (Exception e) {  
	            System.out.println("S: Error 2");  
	            e.printStackTrace();  
	        }  
            
            
		}
    };
    
    public Runnable mysql = new Runnable() {
		public void run() {
			while(true){
				
				synchronized(this) {  
				
				while(sqlwritetype==1){
					
					try{
					
		            if (str.length() == 108) {  
		
		            //校验第一位是否为FA末位是否为F5
		       	     String check1 =str.substring(0,2);
		       	     String check11=str.substring(106,108);
		       	     if(check1.equals("FA") && check11.equals("F5")){
			        		
		           	     //校验长度
		           	     int check2=str.length();
		           	     if(check2==108){
		           	        			
		               	     //校验位校验
		               	     String check3=str.substring(2,104);
		               	     String check5="";
		               	     int check4=0;
		               	     for (int i11 = 0; i11 < check3.length()/2; i11++)
		               	     {
		               	    	String tstr1=check3.substring(i11*2, i11*2+2);
		               	    	check4+=Integer.valueOf(tstr1,16);
		               	     }
		               	     if((Integer.toHexString(check4)).toUpperCase().length()==2){
		               	    	check5 = ((Integer.toHexString(check4)).toUpperCase());
		               	     }else{
		               	    	check5 = ((Integer.toHexString(check4)).toUpperCase()).substring(1,3);
		               	     }
		               	     String check6 = str.substring(104,106);
		               	     if(check5.equals(check6)){
		               	        				
		               	    	 for(int i=0;i<78;i+=26){
		               	    		 
		               	    		 
		               	    		 BigDecimal electricity = new BigDecimal(Integer.valueOf(str.subSequence(26+i, 30+i).toString(),16));
		                             BigDecimal voltage = new BigDecimal(Integer.valueOf(str.subSequence(30+i, 34+i).toString(),16));
		                             long sensor_Num1 = Integer.valueOf(str.subSequence(34+i, 38+i).toString(),16);
		                             String sensor_Num = String.valueOf(sensor_Num1);
		                             if(sensor_Num.length()<4){
		                            	 int num=4-sensor_Num.length();
		                            	 for(int i1=0;i1<num;i1++){
		                            		 sensor_Num="0"+sensor_Num;
		                            	 }
		                             }
		                             long machine_id1 = Integer.valueOf(str.subSequence(10, 14).toString(),16);
		                             String machine_id = String.valueOf(machine_id1);
		                             if(machine_id.length()<4){
		                            	 int num=4-machine_id.length();
		                            	 for(int i1=0;i1<num;i1++){
		                            		 machine_id="0"+machine_id;
		                            	 }
		                             }
		                             long welder_id1 = Integer.valueOf(str.subSequence(14, 18).toString(),16);
		                             String welder_id = String.valueOf(welder_id1);
		                             if(welder_id.length()<4){
		                            	 int num=4-welder_id.length();
		                            	 for(int i1=0;i1<num;i1++){
		                            		 welder_id="0"+welder_id;
		                            	 }
		                             }
		                             long code1 = Integer.valueOf(str.subSequence(18, 26).toString(),16);
		                             String code = String.valueOf(code1);
		                             if(code.length()<8){
		                            	 int num=8-code.length();
		                            	 for(int i1=0;i1<num;i1++){
		                            		 code="0"+code;
		                            	 }
		                             }
		                             long year = Integer.valueOf(str.subSequence(40+i, 42+i).toString(),16);
		                             String yearstr = String.valueOf(year);
		                             long month = Integer.valueOf(str.subSequence(42+i, 44+i).toString(),16);
		                             String monthstr = String.valueOf(month);
		                             long day = Integer.valueOf(str.subSequence(44+i, 46+i).toString(),16);
		                             String daystr = String.valueOf(day);
		                             long hour = Integer.valueOf(str.subSequence(46+i, 48+i).toString(),16);
		                             String hourstr = String.valueOf(hour);
		                             long minute = Integer.valueOf(str.subSequence(48+i, 50+i).toString(),16);
		                             String minutestr = String.valueOf(minute);
		                             long second = Integer.valueOf(str.subSequence(50+i, 52+i).toString(),16);
		                             String secondstr = String.valueOf(second);
		                             int status = Integer.parseInt(str.subSequence(38+i, 40+i).toString());
		               	    		 
		                             String timestr = yearstr+"-"+monthstr+"-"+daystr+" "+hourstr+":"+minutestr+":"+secondstr;
		                             SimpleDateFormat timeshow = new SimpleDateFormat("yy-MM-dd HH:mm:ss",Locale.CHINA);
		                             try {
			                            Date time = DateTools.parse("yy-MM-dd HH:mm:ss",timestr);
		                            	//java.util.Date time1 = timeshow.parse(timestr);
										Timestamp timesql = new Timestamp(time.getTime());

		               	    		 /*BigDecimal electricity = new BigDecimal(Integer.valueOf(str.subSequence(26+i, 30+i).toString(),16));
		                             BigDecimal voltage = new BigDecimal(Integer.valueOf(str.subSequence(30+i, 34+i).toString(),16));
		                             long sensor_Num = Integer.valueOf(str.subSequence(34+i, 38+i).toString(),16);
		                             long machine_id = Integer.valueOf(str.subSequence(10, 14).toString(),16);
		                             long welder_id = Integer.valueOf(str.subSequence(14, 18).toString(),16);
		                             long code = Integer.valueOf(str.subSequence(18, 26).toString(),16);
		                             long year = Integer.valueOf(str.subSequence(40+i, 42+i).toString(),16);
		                             long month = Integer.valueOf(str.subSequence(42+i, 44+i).toString(),16);
		                             long day = Integer.valueOf(str.subSequence(44+i, 46+i).toString(),16);
		                             long hour = Integer.valueOf(str.subSequence(46+i, 48+i).toString(),16);
		                             long minute = Integer.valueOf(str.subSequence(48+i, 50+i).toString(),16);
		                             long second = Integer.valueOf(str.subSequence(50+i, 52+i).toString(),16);
		                             int status = Integer.parseInt(str.subSequence(38+i, 40+i).toString());*/
		                                	
									 
		                             DB_Connectionmysql a = new DB_Connectionmysql(electricity,voltage,sensor_Num,machine_id,welder_id,code,status,timesql,connet);
									} catch (Exception e) {
										sqlwritetype=0;
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
		
		               	    	 }
		                   	    sqlwritetype=0;
		               	     }
		               	        			
		               	     else{
		               	        //校验位错误
		               	    	 System.out.print("数据接收校验位错误");
		               	    	 sqlwritetype=0;
		               	     }
		                               
		           	     }
		           	        		
		           	     else{
		           	        //长度错误
		           	    	 System.out.print("数据接收长度错误");
		           	    	 sqlwritetype=0;
		           	     }
		       	        		
		   	        	}
		   	        	else{
		   	        		//首位不是FE
		   	        		System.out.print("数据接收首末位错误");
		   	        		sqlwritetype=0;
		   	        	}
		       	     
		           }else if(str.length()>=108){
		        	
		       	    	String [] stringArr = str.split("FD");
		       	    	
                        for(int i =0;i < stringArr.length;i++)
        		        {
			        	     //校验第一位是否为FA末位是否为F5
				       	     String check1 =stringArr[i].substring(0,2);
				       	     if(check1.equals("FE")){
				       	    	 
				       	    	 //校验长度
				           	     int check2=stringArr[i].length();
				           	     if(check2==54){
				           	    	 
		                        	 if(stringArr[i].length()>30){
		                        		 
		                        		BigDecimal electricity = new BigDecimal(Integer.valueOf(stringArr[i].subSequence(4, 8).toString(),16));
		                                BigDecimal voltage = new BigDecimal(Integer.valueOf(stringArr[i].subSequence(8, 12).toString(),16));
		                                BigInteger sensor_Num = new BigInteger(stringArr[i].subSequence(12, 16).toString());
		                                String machine_id = stringArr[i].subSequence(16, 20).toString();
		                                String welder_id = stringArr[i].subSequence(20, 24).toString();
		                                String code = stringArr[i].subSequence(24, 32).toString();
		                                BigInteger year = new BigInteger(Integer.valueOf(stringArr[i].subSequence(32, 34).toString(),16).toString());
		                                BigInteger month = new BigInteger(Integer.valueOf(stringArr[i].subSequence(34, 36).toString(),16).toString());
		                                BigInteger day = new BigInteger(Integer.valueOf(stringArr[i].subSequence(36, 38).toString(),16).toString());
		                                BigInteger hour = new BigInteger(Integer.valueOf(stringArr[i].subSequence(38, 40).toString(),16).toString());
		                                BigInteger minute = new BigInteger(Integer.valueOf(stringArr[i].subSequence(40, 42).toString(),16).toString());
		                                BigInteger second = new BigInteger(Integer.valueOf(stringArr[i].subSequence(42, 44).toString(),16).toString());
		                                Integer status = Integer.valueOf(stringArr[i].subSequence(44, 46).toString());
		                        	
		                                DB_Connectionandroid a=new DB_Connectionandroid(electricity,voltage,sensor_Num,machine_id,welder_id,code,year,month,day,hour,minute,second,status,connet); 
		                                sqlwritetype=0;
		                                
		                        	 } 
				           	     
				           	     }   
				           	     else{
				           	    //长度错误
				           	    	 System.out.print("数据接收长度错误");
				           	    	 sqlwritetype=0;
				           	     }
			       	         }
				       	     else{
				       	    	 //首位不是FE
				   	        	 System.out.print("数据接收首末位错误");
				   	        	 sqlwritetype=0;
				       	     }
			       	     }
		        	   
			           }else {
			        	   sqlwritetype=0;
			               System.out.println("Not receiver anything from client!");  
			           }
		            
				} catch (Exception e) {
					sqlwritetype=0;
		            System.out.println("S: Error 2");  
		            e.printStackTrace();  
		        }  
		            
			}
				 }
		}
			
		}
    };
    
    
    public Runnable socketsend = new Runnable() {

 		public void run() {
 			while(true){
 				
 				synchronized(this) {  
 				
 				while(sockettype==1){
 					
 					try{
 					
 		            if (str.length() == 108) {  
 		
 		            //校验第一位是否为FA末位是否为F5
 		       	     String check1 =str.substring(0,2);
 		       	     String check11=str.substring(106,108);
 		       	     if(check1.equals("FA") && check11.equals("F5")){
 			        		
 		           	     //校验长度
 		           	     int check2=str.length();
 		           	     if(check2==108){
 		           	        			
 		               	     //校验位校验
 		               	     String check3=str.substring(2,104);
 		               	     String check5="";
 		               	     int check4=0;
 		               	     for (int i11 = 0; i11 < check3.length()/2; i11++)
 		               	     {
 		               	    	String tstr1=check3.substring(i11*2, i11*2+2);
 		               	    	check4+=Integer.valueOf(tstr1,16);
 		               	     }
	 		               	 if((Integer.toHexString(check4)).toUpperCase().length()==2){
		               	    	check5 = ((Integer.toHexString(check4)).toUpperCase());
		               	     }else{
		               	    	check5 = ((Integer.toHexString(check4)).toUpperCase()).substring(1,3);
		               	     }
 		               	     String check6 = str.substring(104,106);
 		               	     if(check5.equals(check6)){
 		               	    	 
 		               	        OutputStream outputStream;
 		               	    	 
 		               	    	byte[] data=new byte[str.length()/2];

 								for (int i = 0; i < data.length; i++)
 								{
 									String tstr1=str.substring(i*2, i*2+2);
 									Integer k=Integer.valueOf(tstr1, 16);
 									data[i]=(byte)k.byteValue();
 								}
 		               	    	 
 		               	    	String strdata = "";
								for(int i=0;i<data.length;i++){
 		                         	
 		                         	//判断为数字还是字母，若为字母+256取正数
 		                         	if(data[i]<0){
 		                         		String r = Integer.toHexString(data[i]+256);
 		                         		String rr=r.toUpperCase();
 		                             	//数字补为两位数
 		                             	if(rr.length()==1){
 		                         			rr='0'+rr;
 		                             	}
 		                             	//strdata为总接收数据
 		                         		strdata += rr;
 		                         		
 		                         	}
 		                         	else{
 		                         		String r = Integer.toHexString(data[i]);
 		                             	if(r.length()==1)
 		                         			r='0'+r;
 		                             	r=r.toUpperCase();
 		                         		strdata+=r;	
 		                         		
 		                         	}
 		                         }
 
 		                         
 		                        byte[] bb3=new byte[strdata.length()/2];
 		     					for (int i1 = 0; i1 < bb3.length; i1++)
 		     					{
 		     						String tstr1=strdata.substring(i1*2, i1*2+2);
 		     						Integer k=Integer.valueOf(tstr1, 16);
 		     						bb3[i1]=(byte)k.byteValue();
 		     					}
 		     					
 		     					try {
 									socket = new Socket(ip1, 5555);
 								} catch (IOException e1) {
 									sockettype=0;
 									e1.printStackTrace();
 								}
 		                        
 							
	 		                    try {
	 		                    	//发送消息
	 		                        // 步骤1：从Socket 获得输出流对象OutputStream
	 		                        // 该对象作用：发送数据
	 		                        outputStream = socket.getOutputStream();
	 		
	 		                        // 步骤2：写入需要发送的数据到输出流对象中
	 		                        outputStream.write(bb3);
	 		                        // 特别注意：数据的结尾加上换行符才可让服务器端的readline()停止阻塞
	 		
	 		                        // 步骤3：发送数据到服务端
	 		                        outputStream.flush();
	 		
	 		                        socket.close();
	 		                        
	 		                        sockettype=0;
	 		     		           
	 		                    } catch (IOException e1) {
	 		                    	sockettype=0;
	 		                        e1.printStackTrace();
	 		                    }
 		               	    	 
 		               	     }
 		               	     else{
 		               	        //校验位错误
 		               	    	 System.out.print("数据接收校验位错误");
 		               	    	 sockettype=0;
 		               	     }
 		                               
 		           	     }
 		           	        		
 		           	     else{
 		           	        //长度错误
 		           	    	 System.out.print("数据接收长度错误");
 		           	    	 sockettype=0;
 		           	     }
 		       	        		
 		   	        	}
 		   	        	else{
 		   	        		//首位不是FE
 		   	        		System.out.print("数据接收首末位错误");
 		   	        		sockettype=0;
 		   	        	}
 		       	     
 		           }else {
 			        	   sockettype=0;
 			               System.out.println("Not receiver anything from client!");  
 			           }
 		            
 				} catch (Exception e) {
 					sockettype=0;
 		            System.out.println("S: Error 2");  
 		            e.printStackTrace();  
 		        }  
 		            
 			}
 				 }
 		}
 			
 		}
     };
    
    
    public Runnable websocketstart = new Runnable() {  
        private PrintWriter getWriter(Socket socket) throws IOException {  
            OutputStream socketOut = socket.getOutputStream();  
            return new PrintWriter(socketOut, true);  
        }  
        public Thread workThread;
        public Server server;
		public void run() {
		
			while(true){
				//建立websocket连接
				try {
				
				    boolean hasHandshake = false;
					
					if(serverSocket==null){
						
						serverSocket = new ServerSocket(SERVERPORTWEB);
						
	                }  
					
					websocketlink = serverSocket.accept();

	                int i=0;
	                
					//开启线程，接收不同的socket请求  
	                Handler handler = new Handler(websocketlink,str,handlers,i,websendtype,connet);  
	                handlers.add(handler);  
	                workThread = new Thread(handler);  
	                workThread.start();  
	
					
					//获取socket输入流信息  
	                InputStream in = websocketlink.getInputStream(); 
	                
	                PrintWriter pw = getWriter(websocketlink);
	                
	                //读入缓存(定义一个1M的缓存区)  
	                byte[] buf = new byte[1024]; 
	                
	                //读到字节（读取输入流数据到缓存）  
	                int len = in.read(buf, 0, 1024);
	                
	                //读到字节数组（定义一个容纳数据大小合适缓存区）  
	                byte[] res = new byte[len];  
	                
	                //将buf内中数据拷贝到res中  
	                System.arraycopy(buf, 0, res, 0, len); 
	                
	                //打印res缓存内容  
	                String key = new String(res);  
	                if(!hasHandshake && key.indexOf("Key") > 0){  
	                    //握手  
	                    //通过字符串截取获取key值  
	                    key = key.substring(0, key.indexOf("==") + 2);  
	                    key = key.substring(key.indexOf("Key") + 4, key.length()).trim();  
	                    //拼接WEBSOCKET传输协议的安全校验字符串  
	                    key+= "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";  
	                    //通过SHA-1算法进行更新  
	                    MessageDigest md = null;
						try {
							md = MessageDigest.getInstance("SHA-1");
						} catch (NoSuchAlgorithmException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}    
	                    md.update(key.getBytes("utf-8"), 0, key.length());  
	                    byte[] sha1Hash = md.digest();    
	                    //进行Base64加密  
	                    pw.println("HTTP/1.1 101 Switching Protocols");  
	                    pw.println("Upgrade: websocket");  
	                    pw.println("Connection: Upgrade");  
	                    pw.println("Sec-WebSocket-Accept: " + key);  
	                    pw.println();  
	                    pw.flush();  
	                    //将握手标志更新，只握一次  
	                    hasHandshake = true;  
	    				
	                }

	                
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
 };
 
 
 
 public Runnable websocketsend = new Runnable() {
        public Thread workThread;
		public void run() {

			while(true){
			
				synchronized(this) {  
				
				while(websendtype==1){
				
					for (int i=0;i<handlers.size();i++) {  
				    	
				    	Handler web = handlers.get(i);
				    	
				    	Handler handler = new Handler(web.websocketlink,str,handlers,i,websendtype,connet);
				    	workThread = new Thread(handler); 
				    	workThread.start();
				    	
	                }
					websendtype=0;
				}
				
				}
			}
		}
 	};
 
	 public static void main(String [] args) 
	 { 
	     Thread desktopServerThread = new Thread(new Server());  
	     desktopServerThread.start();  
	 }
 
}




	class Handler implements Runnable {
		
		public DB_Connectionmysql db_connection;
	    public Socket websocketlink;
		public String str;
	    Server server;
		private List<Handler> handlers;
		private int i;
		private int websendtype;
		private boolean datawritetype = false;
		private String connet;
	    
	    public Handler(Socket socket,String str,List<Handler> handlers,int i,int websendtype,String connet) {  
	        this.websocketlink = socket; 
	        this.str = str;
	        this.handlers = handlers;
	        this.i = i;
	        this.websendtype = websendtype;
	        this.connet = connet;
	    }  
	    
	    public void run() {
			String strdata = "";
			String strsend = "";
			Timestamp timesql1 = null;
			Timestamp timesql2 = null;
			Timestamp timesql3 = null;
			try {
						
				if(str==null){
					
				}
				else
				{	
					
					/*byte[] str1=new byte[str.length()/2];
	
					for (int i = 0; i < str1.length; i++)
					{
						String tstr1=str.substring(i*2, i*2+2);
						Integer k=Integer.valueOf(tstr1, 16);
						str1[i]=(byte)k.byteValue();
					}
	            	
					//串口数据处理
					for(int i=0;i<str1.length;i++){
	                 	
	                 	//判断为数字还是字母，若为字母+256取正数
	                 	if(str1[i]<0){
	                 		String r = Integer.toHexString(str1[i]+256);
	                 		String rr=r.toUpperCase();
	                     	//数字补为两位数
	                     	if(rr.length()==1){
	                 			rr='0'+rr;
	                     	}
	                     	//strdata为总接收数据
	                 		strdata += rr;
	                 	}
	                 	else{
	                 		String r = Integer.toHexString(str1[i]);
	                     	if(r.length()==1)
	                 			r='0'+r;
	                     	r=r.toUpperCase();
	                 		strdata+=r;	
	                 	}
	                 }*/
	                     
					 strdata=str;
					 String weldname = strdata.substring(10,14);
					 String welder=strdata.substring(14,18);
					 long code1 = Integer.valueOf(strdata.subSequence(18, 26).toString(),16);
                     String code = String.valueOf(code1);
                     if(code.length()!=8){
                    	 int lenth=8-code.length();
                    	 for(int i=0;i<lenth;i++){
                    		 code="0"+code;
                    	 }
                     }
					 String electricity1=strdata.substring(26,30);
					 String voltage1=strdata.substring(30,34);
					 String status1=strdata.substring(38,40);
					 
					 long year1 = Integer.valueOf(str.subSequence(40, 42).toString(),16);
                     String yearstr1 = String.valueOf(year1);
                     long month1 = Integer.valueOf(str.subSequence(42, 44).toString(),16);
                     String monthstr1 = String.valueOf(month1);
                     if(monthstr1.length()!=2){
                    	 int lenth=2-monthstr1.length();
                    	 for(int i=0;i<lenth;i++){
                    		 monthstr1="0"+monthstr1;
                    	 }
                     }
                     long day1 = Integer.valueOf(str.subSequence(44, 46).toString(),16);
                     String daystr1 = String.valueOf(day1);
                     if(daystr1.length()!=2){
                    	 int lenth=2-daystr1.length();
                    	 for(int i=0;i<lenth;i++){
                    		 daystr1="0"+daystr1;
                    	 }
                     }
                     long hour1 = Integer.valueOf(str.subSequence(46, 48).toString(),16);
                     String hourstr1 = String.valueOf(hour1);
                     if(hourstr1.length()!=2){
                    	 int lenth=2-hourstr1.length();
                    	 for(int i=0;i<lenth;i++){
                    		 hourstr1="0"+hourstr1;
                    	 }
                     }
                     long minute1 = Integer.valueOf(str.subSequence(48, 50).toString(),16);
                     String minutestr1 = String.valueOf(minute1);
                     if(minutestr1.length()!=2){
                    	 int lenth=2-minutestr1.length();
                    	 for(int i=0;i<lenth;i++){
                    		 minutestr1="0"+minutestr1;
                    	 }
                     }
                     long second1 = Integer.valueOf(str.subSequence(50, 52).toString(),16);
                     String secondstr1 = String.valueOf(second1);
                     if(secondstr1.length()!=2){
                    	 int lenth=2-secondstr1.length();
                    	 for(int i=0;i<lenth;i++){
                    		 secondstr1="0"+secondstr1;
                    	 }
                     }
       	    		 
                     String timestr1 = yearstr1+"-"+monthstr1+"-"+daystr1+" "+hourstr1+":"+minutestr1+":"+secondstr1;
                     SimpleDateFormat timeshow1 = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
                     try {
						
						Date time1 = DateTools.parse("yy-MM-dd HH:mm:ss",timestr1);
                    	//java.util.Date time4 = timeshow3.parse(timestr3);
						timesql1 = new Timestamp(time1.getTime());
						
					 } catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					 }
	                 
					 
					 String electricity2=strdata.substring(52,56);
					 String voltage2=strdata.substring(56,60);
					 String status2=strdata.substring(64,66);
					 
					 long year2 = Integer.valueOf(str.subSequence(66, 68).toString(),16);
                     String yearstr2 = String.valueOf(year2);
                     long month2 = Integer.valueOf(str.subSequence(68, 70).toString(),16);
                     String monthstr2 = String.valueOf(month2);
                     if(monthstr2.length()!=2){
                    	 int lenth=2-monthstr2.length();
                    	 for(int i=0;i<lenth;i++){
                    		 monthstr2="0"+monthstr2;
                    	 }
                     }
                     long day2 = Integer.valueOf(str.subSequence(70, 72).toString(),16);
                     String daystr2 = String.valueOf(day2);
                     if(daystr2.length()!=2){
                    	 int lenth=2-daystr2.length();
                    	 for(int i=0;i<lenth;i++){
                    		 daystr2="0"+daystr2;
                    	 }
                     }
                     long hour2 = Integer.valueOf(str.subSequence(72, 74).toString(),16);
                     String hourstr2 = String.valueOf(hour2);
                     if(hourstr2.length()!=2){
                    	 int lenth=2-hourstr2.length();
                    	 for(int i=0;i<lenth;i++){
                    		 hourstr2="0"+hourstr2;
                    	 }
                     }
                     long minute2 = Integer.valueOf(str.subSequence(74, 76).toString(),16);
                     String minutestr2 = String.valueOf(minute2);
                     if(minutestr2.length()!=2){
                    	 int lenth=2-minutestr2.length();
                    	 for(int i=0;i<lenth;i++){
                    		 minutestr2="0"+minutestr2;
                    	 }
                     }
                     long second2 = Integer.valueOf(str.subSequence(76, 78).toString(),16);
                     String secondstr2 = String.valueOf(second2);
                     if(secondstr2.length()!=2){
                    	 int lenth=2-secondstr2.length();
                    	 for(int i=0;i<lenth;i++){
                    		 secondstr2="0"+secondstr2;
                    	 }
                     }
       	    		 
                     String timestr2 = yearstr2+"-"+monthstr2+"-"+daystr2+" "+hourstr2+":"+minutestr2+":"+secondstr2;
                     SimpleDateFormat timeshow2 = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
                     try {

						Date time2 = DateTools.parse("yy-MM-dd HH:mm:ss",timestr2);
                    	//java.util.Date time4 = timeshow3.parse(timestr3);
						timesql2 = new Timestamp(time2.getTime());
						
					 } catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					 }
					 
					 
					 String electricity3=strdata.substring(78,82);
					 String voltage3=strdata.substring(82,86);
					 String status3=strdata.substring(90,92);
					 
					 long year3 = Integer.valueOf(str.subSequence(92, 94).toString(),16);
                     String yearstr3 = String.valueOf(year3);
                     long month3 = Integer.valueOf(str.subSequence(94, 96).toString(),16);
                     String monthstr3 = String.valueOf(month3);
                     if(monthstr3.length()!=2){
                    	 int lenth=2-monthstr3.length();
                    	 for(int i=0;i<lenth;i++){
                    		 monthstr3="0"+monthstr3;
                    	 }
                     }
                     long day3 = Integer.valueOf(str.subSequence(96, 98).toString(),16);
                     String daystr3 = String.valueOf(day3);
                     if(daystr3.length()!=2){
                    	 int lenth=2-daystr3.length();
                    	 for(int i=0;i<lenth;i++){
                    		 daystr3="0"+daystr3;
                    	 }
                     }
                     long hour3 = Integer.valueOf(str.subSequence(98, 100).toString(),16);
                     String hourstr3 = String.valueOf(hour3);
                     if(hourstr3.length()!=2){
                    	 int lenth=2-hourstr3.length();
                    	 for(int i=0;i<lenth;i++){
                    		 hourstr3="0"+hourstr3;
                    	 }
                     }
                     long minute3 = Integer.valueOf(str.subSequence(100, 102).toString(),16);
                     String minutestr3 = String.valueOf(minute3);
                     if(minutestr3.length()!=2){
                    	 int lenth=2-minutestr3.length();
                    	 for(int i=0;i<lenth;i++){
                    		 minutestr3="0"+minutestr3;
                    	 }
                     }
                     long second3 = Integer.valueOf(str.subSequence(102, 104).toString(),16);
                     String secondstr3 = String.valueOf(second3);
                     if(secondstr3.length()!=2){
                    	 int lenth=2-secondstr3.length();
                    	 for(int i=0;i<lenth;i++){
                    		 secondstr3="0"+secondstr3;
                    	 }
                     }
       	    		 
                     String timestr3 = yearstr3+"-"+monthstr3+"-"+daystr3+" "+hourstr3+":"+minutestr3+":"+secondstr3;
                     SimpleDateFormat timeshow3 = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
                     try {
                    	Date time3 = DateTools.parse("yy-MM-dd HH:mm:ss",timestr3);
                    	//java.util.Date time4 = timeshow3.parse(timestr3);
						timesql3 = new Timestamp(time3.getTime());
					 } catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					 }
					 
                     try{
	                     DB_Connectionweb b =new DB_Connectionweb(connet);
	                     DB_Connectioncode c =new DB_Connectioncode(code,connet);
	                   
	                    
		                 String dbdata = b.getId();
		                 String limit = c.getId();
		                 
		                 for(int i=0;i<dbdata.length();i+=13){
		                	 String status=dbdata.substring(0+i,2+i);
		                	 String framework=dbdata.substring(2+i,4+i);
		                     String weld=dbdata.substring(4+i,8+i); 
		                     if(weldname.equals(weld)){
		                    	 strsend+=status1+framework+weld+welder+electricity1+voltage1+timesql1+limit
		                    			 +status2+framework+weld+welder+electricity2+voltage2+timesql2+limit
		                    			 +status3+framework+weld+welder+electricity3+voltage3+timesql3+limit;
		                     }
		                     else{
		                    	 strsend+=status+framework+weld+"0000"+"0000"+"0000"+"000000000000000000000"+"000000000000"
		                    			 +status+framework+weld+"0000"+"0000"+"0000"+"000000000000000000000"+"000000000000"
		                    			 +status+framework+weld+"0000"+"0000"+"0000"+"000000000000000000000"+"000000000000";
		                     }
		                 }
                     }catch (Exception e) {
 						// TODO Auto-generated catch block
                    	 System.out.println("数据库读取数据错误");
                    	 e.printStackTrace();
                    	 websendtype=0;
 					 }
	    
	                 datawritetype = true;
	                 
	                //数据发送
	                byte[] bb3=strsend.getBytes();
	                  
					ByteBuffer byteBuf = ByteBuffer.allocate(bb3.length);
					
					for(int j=0;j<bb3.length;j++){
						
						byteBuf.put(bb3[j]);
						
					}
					
					byteBuf.flip();
					
	                
	                //将内容返回给客户端  
	                responseClient(byteBuf, true, websocketlink); 
	                
						
				}
				
			} catch (IOException e) {
				
				websendtype=0;
				
				if(datawritetype = true){
					
					try {
						websendtype=0; 
						websocketlink.close();
						handlers.remove(i);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} 
					
				}
			}
		}  
	    
	    public void responseClient(ByteBuffer byteBuf, boolean finalFragment,Socket socket) throws IOException {  
	        
	    	OutputStream out = websocketlink.getOutputStream();  
	        int first = 0x00;  
	        
	        //是否是输出最后的WebSocket响应片段  
	            if (finalFragment) {  
	                first = first + 0x80;  
	                first = first + 0x1;  
	            }  
	            
	            out.write(first); 
	            
	            if (byteBuf.limit() < 126) {  
	                out.write(byteBuf.limit());  
	            } else if (byteBuf.limit() < 65536) {  
		            out.write(126);  
		            out.write(byteBuf.limit() >>> 8);  
		            out.write(byteBuf.limit() & 0xFF);  
	            } else {  
	            // Will never be more than 2^31-1  
		            out.write(127);  
		            out.write(0);  
		            out.write(0);  
		            out.write(0);  
		            out.write(0);  
		            out.write(byteBuf.limit() >>> 24);  
		            out.write(byteBuf.limit() >>> 16);  
		            out.write(byteBuf.limit() >>> 8);  
		            out.write(byteBuf.limit() & 0xFF);  
	            }  
	            // Write the content  
	            out.write(byteBuf.array(), 0, byteBuf.limit());  
	            out.flush();  
	    }  
	    
	}
