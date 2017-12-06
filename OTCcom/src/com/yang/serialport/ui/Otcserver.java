package com.yang.serialport.ui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import javax.swing.JTextArea;

import com.yang.serialport.ui.MainFrame;

public class Otcserver extends Server{
   private static final int SERVERPORT = 4444;
   private MainFrame mainframe;
   public String datesend;

   public Otcserver() {
	   
	   /*mainframe.initView();
	   mainframe.countComponents();
	   mainframe.actionListener();
	   mainframe.initData();*/
	   new Thread(reciver).start();
	   
   }
	
   public Runnable reciver = new Runnable() {
		public void run() {
			  
           try {
           	
				ServerSocket serverSocket = new ServerSocket(SERVERPORT);
				
                Socket client = serverSocket.accept();   
				
				while (true) {  
					
					synchronized(this) {   
	                  
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
	                    
	                    String welder = strdata.substring(24,28);                
	                    String electricity = strdata.substring(28,32);
	                    String voltage = strdata.substring(32,36);
	                    String sensor = strdata.substring(48,52);
	                    
	                    Date timetran = new Date();
	                    long timetran1 = timetran.getTime();
	                    Date time11 = new Date(timetran1);
	                    long timetran2 = timetran1 + 1000;
	                    Date time22 = new Date(timetran2);
	                    long timetran3 = timetran2 + 1000;
	                    Date time33 = new Date(timetran3);
	                    
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
	                    
	                    datesend = "00003101" + weld + welder + "00000000" 
	                    + electricity + voltage + sensor + "31" + year161 + month161 + day161 + hour161 + minute161 + second161 
	                    + electricity + voltage + sensor + "31" + year162 + month162 + day162 + hour162 + minute162 + second162
	                    + electricity + voltage + sensor + "31" + year163 + month163 + day163 + hour163 + minute163 + second163;
	                    
	                    int check = 0;
	                    byte[] data=new byte[datesend.length()/2];
						for (int i = 0; i < data.length; i++)
						{
							String tstr1=datesend.substring(i*2, i*2+2);
							Integer k=Integer.valueOf(tstr1, 16);
							check += k;
							data[i]=(byte)k.byteValue();
						}
       
						String checksend = Integer.toHexString(check);
						checksend = checksend.substring(1,3);
						
						datesend = "FA" + datesend + checksend + "F5";
						
						
	                    
	                } catch (Exception e) {  
	                    System.out.println("S: Error "+e.getMessage());  
	                    e.printStackTrace();  
	                } 
	                
				}
	                
	            }  
	        } catch (Exception e) {  
	            System.out.println("S: Error 2");  
	            e.printStackTrace();  
	        }  
 
		}
   };
   
}
