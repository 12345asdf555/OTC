package com.yang.serialport.ui;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import javax.xml.ws.Endpoint;

import service.weld.jn.*;

import org.tempuri.*;
import org.apache.axis2.addressing.AddressingConstants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.engine.AxisServer;
import org.apache.axis2.transport.http.HTTPConstants;
import org.datacontract.schemas._2004._07.jn_weld_service.*;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
public class Test {
	
	private static boolean first = true;
	private static String docXmlText;
	
	public static void main(String[] args){

		String str = "FE5AA5006e000200000000000000000000000043021102010000000a000a000a000a000a000a000a000a000a000a000a000a0103000201000a00020000000001000100010001000100010101000a000a000a000a000a000a000a000a000a000a000a000a0a0a0a01010a6e0a0a00";
	
		try {
			
			EndpointReference endpoint=new EndpointReference("http://127.0.0.1:8734/JN_WELD_Service/Service1/");
			WeldServiceStub stu=new WeldServiceStub("http://127.0.0.1:8734/JN_WELD_Service/Service1/");
			
			//;
			//stu._getServiceClient().sendto
			//stu._getServiceClient().setTargetEPR(endpoint);~
			//stu._getServiceClient().getOptions().setTo(endpoint);;
		
			
			//stu._getServiceClient().getOptions().setProperty(AddressingConstants., org.apache.axis2.addressing.AddressingConstants.Final.WSA_NAMESPACE);
            //int setWebServiceTimeOutInSeconds=mySession.getVariable(IProjectVariables.SET_WEB_SERVICE_TIME_OUT_IN_SECONDS).getSimpleVariable().getIntValue();
            //stu._getServiceClient().getOptions().setTimeOutInMilliSeconds(setWebServiceTimeOutInSeconds*1000);
            stu._getServiceClient().getOptions().setProperty(HTTPConstants.REUSE_HTTP_CLIENT,true); 
            stu._getServiceClient().getOptions().setProperty(HTTPConstants.CHUNKED, "false");//���ò�������.
			
			//stu._getServiceClient().
			
			ServiceCall sc = new ServiceCall();
			
			CompositeType tt=new CompositeType();
			tt.setWeldDataTable("");
			tt.setCmdCode(6032201); //��ȡ����
			sc.setCmd(tt);
			
			ServiceCallResponse a = stu.serviceCall(sc);
			CompositeType rs= a.getServiceCallResult();
			String xml = rs.getWeldDataTable();
			System.out.println(xml);
			
			Document doc = DocumentHelper.parseText(xml);
			
			Element rootElt = doc.getRootElement(); // ��ȡ���ڵ�
            System.out.println("���ڵ㣺" + rootElt.getName() + "\r");

            String[] headbuf = xml.split("<dt>");
            String head = headbuf[0];
            
            List nodes = rootElt.elements("dt");
            int count = 0;
            for (Iterator it = nodes.iterator(); it.hasNext();) {
                Element elm = (Element) it.next();
                
                count++;
                
                Element elmbuf1 = elm.element("nom");
                Element elmbuf2 = elm.element("channel");
                
                if(elmbuf1.getStringValue().equals("3580") && elmbuf2.getStringValue().equals("1")){
                	System.out.println(elmbuf1.getStringValue()); 
                    System.out.println("�ڵ㣺" + elm.getName() + "\r");
                    
                    /*Element channel = elm.element("channel");
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
                    dwadown.setText(Integer.valueOf(str.substring(148,150),16).toString());
                    
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
                        System.out.println("�㣺" + element.getName() + " " + element.getStringValue()); // �õ����ڵ������    
                    }*/
                }else{
                	continue;
                }
            }
            
            String[] databuf = docXmlText.split("<dt>");
            String data = databuf[count];
            count = 0;
            
            
            tt.setWeldDataTable(head+"<dt>"+data+"</NewDataSet>");
			tt.setCmdCode(6032801); //�·�����
			sc.setCmd(tt);
			
			a = stu.serviceCall(sc);
			rs= a.getServiceCallResult();
			xml = rs.getWeldDataTable();
			System.out.println(xml);
            
			//String a = sc.getOMElement("509201", null);
			//System.out.println(a);
		} catch (Exception e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
	}
	
	
	
}
