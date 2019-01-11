package com.yang.serialport.ui;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.transport.http.HTTPConstants;
import org.datacontract.schemas._2004._07.jn_weld_service.CompositeType;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.tempuri.WeldServiceStub;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.CharsetUtil;
import service.weld.jn.ServiceCall;
import service.weld.jn.ServiceCallResponse;


public class TcpClientHandler extends ChannelHandlerAdapter {
	
	public Clientconnect client;
	public MainFrame mainframe;
	public HashMap<String, SocketChannel> socketlist;
	public String socketfail;
	public ArrayList<String> listarrayJN = new ArrayList<String>();
	private String docXmlText = "";
	
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
				 if(!listarrayJN.contains(JN[1])){
					 for(int i=1;i<JN.length;i++){
						 listarrayJN.add(JN[i]);
						 client.NS.listarrayJN  = listarrayJN;
					 }
				 }else{
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
				 }
			 }else if(JN[4].equals("1")){  //任务完成
				 for(int i=0;i<listarrayJN.size();i+=5){
					 if(listarrayJN.get(i).equals(JN[1])){
						 for(int j=0;j<5;j++){
							 listarrayJN.remove(i);
						 }
						 
						 client.NS.listarrayJN  = listarrayJN;
					 }
				 }
			 }else if(JN[4].equals("2")){  //任务修改
				 for(int i=0;i<listarrayJN.size();i+=5){
					 if(listarrayJN.get(i+1).equals(JN[2])){
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
						 for(int j=0;j<5;j++){
							 listarrayJN.remove(i);
						 }
						 
						 client.NS.listarrayJN  = listarrayJN;
					 }
				 }
			 }
			 
			 System.out.println(str);
			 }
		 }else if(str.substring(0,6).equals("FE5AA5") && str.substring(40,44).equals("0211") && str.length() != 52){
			  
			  wcfset(str,ctx); 
			  
		 }else if(str.substring(0,6).equals("FE5AA5") && str.substring(40,44).equals("0211") && str.length() == 52){
			  
			  wcfget(str,ctx); 
			  
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
	
	private void wcfget(String str, ChannelHandlerContext ctx) {
		// TODO Auto-generated method stub
		try {
			
			EndpointReference endpoint=new EndpointReference("http://192.168.3.212:8734/JN_WELD_Service/Service1/");
			WeldServiceStub stu=new WeldServiceStub("http://192.168.3.212:8734/JN_WELD_Service/Service1/");
			
			//;
			//stu._getServiceClient().sendto
			//stu._getServiceClient().setTargetEPR(endpoint);~
			//stu._getServiceClient().getOptions().setTo(endpoint);;
		
			
			//stu._getServiceClient().getOptions().setProperty(AddressingConstants., org.apache.axis2.addressing.AddressingConstants.Final.WSA_NAMESPACE);
            //int setWebServiceTimeOutInSeconds=mySession.getVariable(IProjectVariables.SET_WEB_SERVICE_TIME_OUT_IN_SECONDS).getSimpleVariable().getIntValue();
            //stu._getServiceClient().getOptions().setTimeOutInMilliSeconds(setWebServiceTimeOutInSeconds*1000);
            stu._getServiceClient().getOptions().setProperty(HTTPConstants.REUSE_HTTP_CLIENT,true); 
            stu._getServiceClient().getOptions().setProperty(HTTPConstants.CHUNKED, "false");//设置不受限制.
			
			//stu._getServiceClient().
			
			ServiceCall sc = new ServiceCall();
			
			CompositeType tt=new CompositeType();
			tt.setWeldDataTable("");
			tt.setCmdCode(6032201);
			sc.setCmd(tt);
			
			ServiceCallResponse a = stu.serviceCall(sc);
			CompositeType rs= a.getServiceCallResult();
			String xml = rs.getWeldDataTable();
			System.out.println(xml);
			
			Document doc = DocumentHelper.parseText(xml);
			
			Element rootElt = doc.getRootElement(); // 获取根节点
            System.out.println("根节点：" + rootElt.getName() + "\r");

            List nodes = rootElt.elements("dt");
        	String str1 = "";
            for (Iterator it = nodes.iterator(); it.hasNext();) {
                Element elm = (Element) it.next();
                
                Element elmbuf1 = elm.element("nom");
                Element elmbuf2 = elm.element("channel");
                
                if((Integer.valueOf(elmbuf1.getStringValue()).equals(Integer.valueOf(str.substring(10,14),16))) && (Integer.valueOf(elmbuf2.getStringValue()).equals(Integer.valueOf(str.substring(46,48),16)))){
/*                    for(Iterator it1=elm.elementIterator();it1.hasNext();){
                        Element element = (Element) it1.next();
                        json.put(element.getName(), element.getStringValue());
                        System.out.println("点：" + element.getName() + " " + element.getStringValue()); // 拿到根节点的名称    
                    };*/
                	
                	str1 = "FE5AA5005A"+str.substring(10,14)+"00000000000000000000000000021101"+str.substring(46,48)+"0100";
                    String va_up = Integer.toHexString(Integer.valueOf(elm.element("va_up").getStringValue())); //预置电流上限
                    if(va_up.length()<4){
                    	int len = 4 - va_up.length();
                    	for(int i=0;i<len;i++){
                    		va_up = "0" + va_up;
                    	}
                    }
                    str1 += va_up;
                    String vv_up = Integer.toHexString(Integer.valueOf(elm.element("vv_up").getStringValue())*10); //预置电压上限
                    if(vv_up.length()<4){
                    	int len = 4 - vv_up.length();
                    	for(int i=0;i<len;i++){
                    		vv_up = "0" + vv_up;
                    	}
                    }
                    str1 += vv_up;
                    String va_down = Integer.toHexString(Integer.valueOf(elm.element("va_down").getStringValue())); //预置电流下限
                    if(va_down.length()<4){
                    	int len = 4 - va_down.length();
                    	for(int i=0;i<len;i++){
                    		va_down = "0" + va_down;
                    	}
                    }
                    str1 += va_down;
                    String vv_down = Integer.toHexString(Integer.valueOf(elm.element("vv_down").getStringValue())*10); //预置电压下限
                    if(vv_down.length()<4){
                    	int len = 4 - vv_down.length();
                    	for(int i=0;i<len;i++){
                    		vv_down = "0" + vv_down;
                    	}
                    }
                    str1 += vv_down;
                    String vai_up = Integer.toHexString(Integer.valueOf(elm.element("vai_up").getStringValue())); //初期电流上限
                    if(vai_up.length()<4){
                    	int len = 4 - vai_up.length();
                    	for(int i=0;i<len;i++){
                    		vai_up = "0" + vai_up;
                    	}
                    }
                    str1 += vai_up;
                    String vvi_up = Integer.toHexString(Integer.valueOf(elm.element("vvi_up").getStringValue())*10); //初期电压上限
                    if(vvi_up.length()<4){
                    	int len = 4 - vvi_up.length();
                    	for(int i=0;i<len;i++){
                    		vvi_up = "0" + vvi_up;
                    	}
                    }
                    str1 += vvi_up;
                    String vai_down = Integer.toHexString(Integer.valueOf(elm.element("vai_down").getStringValue())); //初期电流下限
                    if(vai_down.length()<4){
                    	int len = 4 - vai_down.length();
                    	for(int i=0;i<len;i++){
                    		vai_down = "0" + vai_down;
                    	}
                    }
                    str1 += vai_down;
                    String vvi_down = Integer.toHexString(Integer.valueOf(elm.element("vvi_down").getStringValue())*10); //初期电压下限
                    if(vvi_down.length()<4){
                    	int len = 4 - vvi_down.length();
                    	for(int i=0;i<len;i++){
                    		vvi_down = "0" + vvi_down;
                    	}
                    }
                    str1 += vvi_down;
                    String vaf_up = Integer.toHexString(Integer.valueOf(elm.element("vaf_up").getStringValue())); //收弧电流上限
                    if(vaf_up.length()<4){
                    	int len = 4 - vaf_up.length();
                    	for(int i=0;i<len;i++){
                    		vaf_up = "0" + vaf_up;
                    	}
                    }
                    str1 += vaf_up;
                    String vvf_up = Integer.toHexString(Integer.valueOf(elm.element("vvf_up").getStringValue())*10); //收弧电压上限
                    if(vvf_up.length()<4){
                    	int len = 4 - vvf_up.length();
                    	for(int i=0;i<len;i++){
                    		vvf_up = "0" + vvf_up;
                    	}
                    }
                    str1 += vvf_up;
                    String vaf_down = Integer.toHexString(Integer.valueOf(elm.element("vaf_down").getStringValue())); //收弧电流下限
                    if(vaf_down.length()<4){
                    	int len = 4 - vaf_down.length();
                    	for(int i=0;i<len;i++){
                    		vaf_down = "0" + vaf_down;
                    	}
                    }
                    str1 += vaf_down;
                    String vvf_down = Integer.toHexString(Integer.valueOf(elm.element("vvf_down").getStringValue())*10); //收弧电压下限
                    if(vvf_down.length()<4){
                    	int len = 4 - vvf_down.length();
                    	for(int i=0;i<len;i++){
                    		vvf_down = "0" + vvf_down;
                    	}
                    }
                    str1 += vvf_down;
                    String mt = Integer.toHexString(Integer.valueOf(elm.element("mt").getStringValue())); //材质
                    if(mt.length()<2){
                    	int len = 2 - mt.length();
                    	for(int i=0;i<len;i++){
                    		mt = "0" + mt;
                    	}
                    }
                    str1 += mt;
                    String wd = Integer.toHexString(Integer.valueOf(elm.element("wd").getStringValue())); //丝径
                    if(wd.length()<2){
                    	int len = 2 - wd.length();
                    	for(int i=0;i<len;i++){
                    		wd = "0" + wd;
                    	}
                    }
                    str1 += wd;
                    String wp = Integer.toHexString(Integer.valueOf(elm.element("wp").getStringValue())); //气体
                    if(wp.length()<2){
                    	int len = 2 - wp.length();
                    	for(int i=0;i<len;i++){
                    		wp = "0" + wp;
                    	}
                    }
                    str1 += wp;
                    String wc = Integer.toHexString(Integer.valueOf(elm.element("wc").getStringValue())); //焊接控制
                    if(wc.length()<2){
                    	int len = 2 - wc.length();
                    	for(int i=0;i<len;i++){
                    		wc = "0" + wc;
                    	}
                    }
                    str1 += wc;
                    String mp = Integer.toHexString(Integer.valueOf(elm.element("mp").getStringValue())); //脉冲有无
                    if(mp.length()<2){
                    	int len = 2 - mp.length();
                    	for(int i=0;i<len;i++){
                    		mp = "0" + mp;
                    	}
                    }
                    str1 += mp;
                    String pwtime = Integer.toHexString(Integer.valueOf(elm.element("pwtime").getStringValue())*10); //点焊时间
                    if(pwtime.length()<4){
                    	int len = 4 - pwtime.length();
                    	for(int i=0;i<len;i++){
                    		pwtime = "0" + pwtime;
                    	}
                    }
                    str1 += pwtime;
                    String yiyuan = Integer.toHexString(Integer.valueOf(elm.element("yiyuan").getStringValue())); //一元/个别
                    if(yiyuan.length()<2){
                    	int len = 2 - yiyuan.length();
                    	for(int i=0;i<len;i++){
                    		yiyuan = "0" + yiyuan;
                    	}
                    }
                    str1 += yiyuan+"00000000";
                    String dwa_up = Integer.toHexString(Integer.valueOf(elm.element("dwa_up").getStringValue())); //焊接上限
                    if(dwa_up.length()<4){
                    	int len = 4 - dwa_up.length();
                    	for(int i=0;i<len;i++){
                    		dwa_up = "0" + dwa_up;
                    	}
                    }
                    str1 += dwa_up;
                    String dwa_down = Integer.toHexString(Integer.valueOf(elm.element("dwa_down").getStringValue())); //焊接下限
                    if(dwa_down.length()<4){
                    	int len = 4 - dwa_down.length();
                    	for(int i=0;i<len;i++){
                    		dwa_down = "0" + dwa_down;
                    	}
                    }
                    str1 += dwa_down+"0000000000000000";
                    String dwa_outtime = Integer.toHexString((int) Float.parseFloat(elm.element("dwa_outtime").getStringValue())); //延时时间
                    if(dwa_outtime.length()<2){
                    	int len = 2 - dwa_outtime.length();
                    	for(int i=0;i<len;i++){
                    		dwa_outtime = "0" + dwa_outtime;
                    	}
                    }
                    str1 += dwa_outtime;
                    String dwai_outtime = Integer.toHexString((int) Float.parseFloat(elm.element("dwai_outtime").getStringValue())); //修正周期
                    if(dwai_outtime.length()<2){
                    	int len = 2 - dwai_outtime.length();
                    	for(int i=0;i<len;i++){
                    		dwai_outtime = "0" + dwai_outtime;
                    	}
                    }
                    str1 += dwai_outtime;
/*                    String dwai_up = Integer.toHexString(Integer.valueOf(elm.element("dwai_up").getStringValue())); //初期上限
                    if(dwai_up.length()<4){
                    	int len = 4 - dwai_up.length();
                    	for(int i=0;i<len;i++){
                    		dwai_up = "0" + dwai_up;
                    	}
                    }
                    String dwai_down = Integer.toHexString(Integer.valueOf(elm.element("dwai_down").getStringValue())); //初期下限
                    if(dwai_down.length()<4){
                    	int len = 4 - dwai_down.length();
                    	for(int i=0;i<len;i++){
                    		dwai_down = "0" + dwai_down;
                    	}
                    }
                    String dwaf_up = Integer.toHexString(Integer.valueOf(elm.element("dwaf_up").getStringValue())); //收弧上限
                    if(dwaf_up.length()<4){
                    	int len = 4 - dwaf_up.length();
                    	for(int i=0;i<len;i++){
                    		dwaf_up = "0" + dwaf_up;
                    	}
                    }
                    String dwaf_down = Integer.toHexString(Integer.valueOf(elm.element("dwaf_down").getStringValue())); //收弧下限
                    if(dwaf_down.length()<4){
                    	int len = 4 - dwaf_down.length();
                    	for(int i=0;i<len;i++){
                    		dwaf_down = "0" + dwaf_down;
                    	}
                    }*/
                    String wa_up = Integer.toHexString(Integer.valueOf(elm.element("wa_up").getStringValue())); //预置电流报警上限
                    if(wa_up.length()<4){
                    	int len = 4 - wa_up.length();
                    	for(int i=0;i<len;i++){
                    		wa_up = "0" + wa_up;
                    	}
                    }
                    str1 += wa_up;
                    String wv_up = Integer.toHexString(Integer.valueOf(elm.element("wv_up").getStringValue())*10); //预置电压报警上限
                    if(wv_up.length()<4){
                    	int len = 4 - wv_up.length();
                    	for(int i=0;i<len;i++){
                    		wv_up = "0" + wv_up;
                    	}
                    }
                    str1 += wv_up;
                    String wa_down = Integer.toHexString(Integer.valueOf(elm.element("wa_down").getStringValue())); //预置电流报警下限
                    if(wa_down.length()<4){
                    	int len = 4 - wa_down.length();
                    	for(int i=0;i<len;i++){
                    		wa_down = "0" + wa_down;
                    	}
                    }
                    str1 += wa_down;
                    String wv_down = Integer.toHexString(Integer.valueOf(elm.element("wv_down").getStringValue())*10); //预置电压报警下限
                    if(wv_down.length()<4){
                    	int len = 4 - wv_down.length();
                    	for(int i=0;i<len;i++){
                    		wv_down = "0" + wv_down;
                    	}
                    }
                    str1 += wv_down+"00000000000000000000000000000000";
                    String wai_outtime = Integer.toHexString((int) Float.parseFloat(elm.element("wai_outtime").getStringValue())); //起弧延时时间
                    if(wai_outtime.length()<2){
                    	int len = 2 - wai_outtime.length();
                    	for(int i=0;i<len;i++){
                    		wai_outtime = "0" + wai_outtime;
                    	}
                    }
                    str1 += wai_outtime;
                    String wa_outtime = Integer.toHexString((int) Float.parseFloat(elm.element("wa_outtime").getStringValue())); //报警延时时间
                    if(wa_outtime.length()<2){
                    	int len = 2 - wa_outtime.length();
                    	for(int i=0;i<len;i++){
                    		wa_outtime = "0" + wa_outtime;
                    	}
                    }
                    str1 += wa_outtime;
                    String waf_outtime = Integer.toHexString((int) Float.parseFloat(elm.element("waf_outtime").getStringValue())); //报警停机时间
                    if(waf_outtime.length()<2){
                    	int len = 2 - waf_outtime.length();
                    	for(int i=0;i<len;i++){
                    		waf_outtime = "0" + waf_outtime;
                    	}
                    }
                    str1 += waf_outtime;
                    String AlarmType = Integer.toHexString(Integer.valueOf(elm.element("AlarmType").getStringValue())); //报警停机时间
                    if(AlarmType.length()<2){
                    	int len = 2 - AlarmType.length();
                    	for(int i=0;i<len;i++){
                    		AlarmType = "0" + AlarmType;
                    	}
                    }
                    str1 += AlarmType+"000000000000";
             /*       String wai_up = Integer.toHexString(Integer.valueOf(elm.element("wai_up").getStringValue())); //初期电流报警上限
                    if(wai_up.length()<4){
                    	int len = 4 - wai_up.length();
                    	for(int i=0;i<len;i++){
                    		wai_up = "0" + wai_up;
                    	}
                    }
                    String wvi_up = Integer.toHexString(Integer.valueOf(elm.element("wvi_up").getStringValue())*10); //初期电压报警上限
                    if(wvi_up.length()<4){
                    	int len = 4 - wvi_up.length();
                    	for(int i=0;i<len;i++){
                    		wvi_up = "0" + wvi_up;
                    	}
                    }
                    String wai_down = Integer.toHexString(Integer.valueOf(elm.element("wai_down").getStringValue())); //初期电流报警下限
                    if(wai_down.length()<4){
                    	int len = 4 - wai_down.length();
                    	for(int i=0;i<len;i++){
                    		wai_down = "0" + wai_down;
                    	}
                    }
                    String wvi_down = Integer.toHexString(Integer.valueOf(elm.element("wvi_down").getStringValue())*10); //初期电压报警下限
                    if(wvi_down.length()<4){
                    	int len = 4 - wvi_down.length();
                    	for(int i=0;i<len;i++){
                    		wvi_down = "0" + wvi_down;
                    	}
                    }
                    String waf_up = Integer.toHexString(Integer.valueOf(elm.element("waf_up").getStringValue())); //收弧电流报警上限
                    if(waf_up.length()<4){
                    	int len = 4 - waf_up.length();
                    	for(int i=0;i<len;i++){
                    		waf_up = "0" + waf_up;
                    	}
                    }
                    String wvf_up = Integer.toHexString(Integer.valueOf(elm.element("wvf_up").getStringValue())*10); //收弧电压报警上限
                    if(wvf_up.length()<4){
                    	int len = 4 - wvf_up.length();
                    	for(int i=0;i<len;i++){
                    		wvf_up = "0" + wvf_up;
                    	}
                    }
                    String waf_down = Integer.toHexString(Integer.valueOf(elm.element("waf_down").getStringValue())); //收弧电流报警下限
                    if(waf_down.length()<4){
                    	int len = 4 - waf_down.length();
                    	for(int i=0;i<len;i++){
                    		waf_down = "0" + waf_down;
                    	}
                    }
                    String wvf_down = Integer.toHexString(Integer.valueOf(elm.element("wvf_down").getStringValue())*10); //收弧电压报警下限
                    if(wvf_down.length()<4){
                    	int len = 4 - wvf_down.length();
                    	for(int i=0;i<len;i++){
                    		wvf_down = "0" + wvf_down;
                    	}
                    }
                    String wa_TJtime = Integer.toHexString(Integer.valueOf(elm.element("wa_TJtime").getStringValue())); //报警停机时间
                    if(wa_TJtime.length()<2){
                    	int len = 2 - wa_TJtime.length();
                    	for(int i=0;i<len;i++){
                    		wa_TJtime = "0" + wa_TJtime;
                    	}
                    }*/
                    break;
                }
            }
            ctx.writeAndFlush(str1).sync();
            String docXmlText=doc.asXML();
            System.out.println(docXmlText); 
            
			//String a = sc.getOMElement("509201", null);
			//System.out.println(a);
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

	private void wcfset(String str, ChannelHandlerContext ctx) {
		// TODO Auto-generated method stub
		
		//调用wcf连接服务器
		
		try {
			EndpointReference endpoint=new EndpointReference("http://192.168.3.212:8734/JN_WELD_Service/Service1/");
			WeldServiceStub stu=new WeldServiceStub("http://192.168.3.212:8734/JN_WELD_Service/Service1/");
			
			//;
			//stu._getServiceClient().sendto
			//stu._getServiceClient().setTargetEPR(endpoint);~
			//stu._getServiceClient().getOptions().setTo(endpoint);;
		
			
			//stu._getServiceClient().getOptions().setProperty(AddressingConstants., org.apache.axis2.addressing.AddressingConstants.Final.WSA_NAMESPACE);
            //int setWebServiceTimeOutInSeconds=mySession.getVariable(IProjectVariables.SET_WEB_SERVICE_TIME_OUT_IN_SECONDS).getSimpleVariable().getIntValue();
            //stu._getServiceClient().getOptions().setTimeOutInMilliSeconds(setWebServiceTimeOutInSeconds*1000);
            stu._getServiceClient().getOptions().setProperty(HTTPConstants.REUSE_HTTP_CLIENT,true); 
            stu._getServiceClient().getOptions().setProperty(HTTPConstants.CHUNKED, "false");//设置不受限制.
			
			//stu._getServiceClient().
			
			ServiceCall sc = new ServiceCall();
			
			CompositeType tt=new CompositeType();
			tt.setWeldDataTable("");
			tt.setCmdCode(6032201); //获取参数
			sc.setCmd(tt);
			
			ServiceCallResponse a = stu.serviceCall(sc);
			CompositeType rs= a.getServiceCallResult();
			String xml = rs.getWeldDataTable();
			System.out.println(xml);
			
			Document doc = DocumentHelper.parseText(xml);
			
			Element rootElt = doc.getRootElement(); // 获取根节点
            System.out.println("根节点：" + rootElt.getName() + "\r");

            String[] headbuf = xml.split("<dt>");
            String head = headbuf[0];
            
            List nodes = rootElt.elements("dt");
            int count = 0;
            for (Iterator it = nodes.iterator(); it.hasNext();) {
                Element elm = (Element) it.next();
                
                count++;
                
                Element elmbuf1 = elm.element("nom");
                Element elmbuf2 = elm.element("channel");
                
                System.out.println(Integer.valueOf(elmbuf1.getStringValue()));
                System.out.println(Integer.valueOf(elmbuf2.getStringValue()));
                System.out.println(Integer.valueOf(str.substring(10,14),16));
                System.out.println(Integer.valueOf(str.substring(46,48),16));
                
                if((Integer.valueOf(elmbuf1.getStringValue()).equals(Integer.valueOf(str.substring(10,14),16))) && (Integer.valueOf(elmbuf2.getStringValue()).equals(Integer.valueOf(str.substring(46,48),16)))){
                	System.out.println(elmbuf1.getStringValue()); 
                    System.out.println("节点：" + elm.getName() + "\r");
                    
                    /*Element channel = elm.element("channel");E
                    channel.setText(Integer.valueOf(str.substring(46,48),16).toString());*/
                    
                    Element vaup = elm.element("va_up");
                    vaup.setText(Integer.valueOf(str.substring(52,56),16).toString());
                    
                    Element vvup = elm.element("vv_up");
                    float vvupbuf = ((float)(Integer.valueOf(str.substring(56,60),16).intValue())/10);
                    vvup.setText(String.valueOf(vvupbuf));
                    
                    Element vadown = elm.element("va_down");
                    vadown.setText(Integer.valueOf(str.substring(60,64),16).toString());
                    
                    Element vvdown = elm.element("vv_down");
                    float vvdownbuf = ((float)(Integer.valueOf(str.substring(64,68),16).intValue())/10);
                    vvdown.setText(String.valueOf(vvdownbuf));
                    
                    Element vaiup = elm.element("vai_up");
                    vaiup.setText(Integer.valueOf(str.substring(68,72),16).toString());
                    
                    Element vviup = elm.element("vvi_up");
                    float vviupbuf = ((float)(Integer.valueOf(str.substring(72,76),16).intValue())/10);
                    vviup.setText(String.valueOf(vviupbuf));
                    
                    Element vaidown = elm.element("vai_down");
                    vaidown.setText(Integer.valueOf(str.substring(76,80),16).toString());
                    
                    Element vvidown = elm.element("vvi_down");
                    float vvidownbuf = ((float)(Integer.valueOf(str.substring(80,84),16).intValue())/10);
                    vvidown.setText(String.valueOf(vvidownbuf));
                    
                    Element vafup = elm.element("vaf_up");
                    vafup.setText(Integer.valueOf(str.substring(84,88),16).toString());
                    
                    Element vvfup = elm.element("vvf_up");
                    float vvfupbuf = ((float)(Integer.valueOf(str.substring(88,92),16).intValue())/10);
                    vvfup.setText(String.valueOf(vvfupbuf));
                    
                    Element vafdown = elm.element("vaf_down");
                    vafdown.setText(Integer.valueOf(str.substring(92,96),16).toString());
                    
                    Element vvfdown = elm.element("vvf_down");
                    float vvfdownbuf = ((float)(Integer.valueOf(str.substring(96,100),16).intValue())/10);
                    vvfdown.setText(String.valueOf(vvfdownbuf));
                    
                    Element mt = elm.element("mt");
                    mt.setText(Integer.valueOf(str.substring(100,102),16).toString());
                    
                    Element wd = elm.element("wd");
                    wd.setText(Integer.valueOf(str.substring(102,104),16).toString());
                    
                    Element wp = elm.element("wp");
                    wp.setText(Integer.valueOf(str.substring(104,106),16).toString());
                    
                    Element wc = elm.element("wc");
                    wc.setText(Integer.valueOf(str.substring(106,108),16).toString());
                    
                    Element mp = elm.element("mp");
                    mp.setText(Integer.valueOf(str.substring(108,110),16).toString());
                    
                    Element pwtime = elm.element("pwtime");
                    float pwtimebuf = ((float)(Integer.valueOf(str.substring(110,114),16).intValue())/10);
                    pwtime.setText(String.valueOf(pwtimebuf));
                    
                    Element yiyuan = elm.element("yiyuan");
                    yiyuan.setText(Integer.valueOf(str.substring(114,116),16).toString());
                 
                    Element dwaup = elm.element("dwa_up");
                    dwaup.setText(Integer.valueOf(str.substring(124,128),16).toString());
                    
                    Element dwadown = elm.element("dwa_down");
                    dwadown.setText(Integer.valueOf(str.substring(128,132),16).toString());
                    
                    Element dwaouttime = elm.element("dwa_outtime");
                    dwaouttime.setText(Integer.valueOf(str.substring(148,150),16).toString());
                    
                    Element dwaiouttime = elm.element("dwai_outtime");
                    dwaiouttime.setText(Integer.valueOf(str.substring(150,152),16).toString());
                    
                    Element waup = elm.element("wa_up");
                    waup.setText(Integer.valueOf(str.substring(152,156),16).toString());
                    
                    Element wvup = elm.element("wv_up");
                    float wvupbuf = ((float)(Integer.valueOf(str.substring(156,160),16).intValue())/10);
                    wvup.setText(String.valueOf(wvupbuf));
                    
                    Element wadown = elm.element("wa_down");
                    wadown.setText(Integer.valueOf(str.substring(160,164),16).toString());
                    
                    Element wvdown = elm.element("wv_down");
                    float wvdownbuf = ((float)(Integer.valueOf(str.substring(164,168),16).intValue())/10);
                    wvdown.setText(String.valueOf(wvdownbuf));
                    
                    Element waiouttime = elm.element("wai_outtime");
                    waiouttime.setText(Integer.valueOf(str.substring(200,202),16).toString());
                    
                    Element waouttime = elm.element("wa_outtime");
                    waouttime.setText(Integer.valueOf(str.substring(202,204),16).toString());
                    
                    Element wafouttime = elm.element("waf_outtime");
                    wafouttime.setText(Integer.valueOf(str.substring(204,206),16).toString());
                    
                    Element AlarmType = elm.element("AlarmType");
                    AlarmType.setText(Integer.valueOf(str.substring(206,208),16).toString());
                    
                    docXmlText=doc.asXML();
                    System.out.println(docXmlText);  
                    
                  
                    break;
                    /*for(Iterator it1=elm.elementIterator();it1.hasNext();){
                        Element element = (Element) it1.next();
                        System.out.println("点：" + element.getName() + " " + element.getStringValue()); // 拿到根节点的名称    
                    }*/
                }else{
                	continue;
                }
            }
            
            String[] databuf = docXmlText.split("<dt>");
            String data = databuf[count];
            count = 0;
            
            
            tt.setWeldDataTable(head+"<dt>"+data+"</NewDataSet>");
			tt.setCmdCode(6032801); //下发参数
			sc.setCmd(tt);
			
			a = stu.serviceCall(sc);
			rs= a.getServiceCallResult();
			xml = rs.getWeldDataTable();
			System.out.println(xml);
            
			ctx.writeAndFlush("FE5AA5001A"+str.substring(10,14)+"00000000000000000000000000021102"+str.substring(46,48)+"0000").sync();
			
			//String a = sc.getOMElement("509201", null);
			//System.out.println(a);
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
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
