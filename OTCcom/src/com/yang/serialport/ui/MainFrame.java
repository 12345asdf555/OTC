/*
 * MainFrame.java
 *
 * Created on 2016.8.19
 */

package com.yang.serialport.ui;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.pept.transport.ListenerThread;
import com.yang.serialport.exception.NoSuchPort;
import com.yang.serialport.exception.NotASerialPort;
import com.yang.serialport.exception.PortInUse;
import com.yang.serialport.exception.ReadDataFromSerialPortFailure;
import com.yang.serialport.exception.SendDataToSerialPortFailure;
import com.yang.serialport.exception.SerialPortInputStreamCloseFailure;
import com.yang.serialport.exception.SerialPortOutputStreamCloseFailure;
import com.yang.serialport.exception.SerialPortParameterFailure;
import com.yang.serialport.exception.TooManyListeners;
import com.yang.serialport.manage.SerialPortManager;
import com.yang.serialport.utils.ByteUtils;
import com.yang.serialport.utils.ShowUtils;

import org.sqlite.*;

import com.yang.serialport.ui.*;

public class MainFrame extends JFrame {
	 
	Connection c = null;
    Statement stmt = null;
    // 输出流对象
    OutputStream outputStream;
    // Socket变量
    private Socket socket=null;
    private Socket websocketlink=null;
    private ServerSocket serverSocket = null;
    public String IP;
    public Server server;
    public Otcserver otcserver;
    private static final int SERVERPORT = 5555;
    public String str = "";
    public String responstr="";
    public String datesend = "";
    public String msg;
    public NettyServerHandler NS = new NettyServerHandler();
    public Client client = new Client(NS);
    public TcpClientHandler TC = new TcpClientHandler();
    public HashMap<String, SocketChannel> socketlist = new HashMap<>();
    public int socketcount=0;

	/**
	 * 程序界面宽度
	 */
	public static final int WIDTH = 500;

	/**
	 * 程序界面高度
	 */
	public static final int HEIGHT = 360;

	public JTextArea dataView = new JTextArea();
	private JScrollPane scrollDataView = new JScrollPane(dataView);

	// 串口设置面板
	private JPanel serialPortPanel = new JPanel();
	private JLabel serialPortLabel = new JLabel("串口");
	private JLabel baudrateLabel = new JLabel("波特率");
	private JComboBox commChoice = new JComboBox();
	private JComboBox baudrateChoice = new JComboBox();

	// 操作面板
	private JPanel operatePanel = new JPanel();
	private JTextField dataInput = new JTextField();
	private JButton serialPortOperate = new JButton("停止接收");
	private JButton sendData = new JButton("开始接收");
   
	byte[] data;
	public int socketnortype = 0;
	public int sockettetype = 0;
	public int responsetype = 0;
	private List<String> commList = null;
	private SerialPort serialport;
	private SocketChannel socketChannel = null;
	public HashMap<String, Socket> clientList = new HashMap<>();
    public int clientcount=0;
    public boolean Firsttime=true;
	protected String fitemid;
	
	public MainFrame() {
		
		new Thread(cli).start();
		
		initView();
		initComponents();
		actionListener();
		initData();	
	}

	public Runnable cli =new Runnable(){

		private String ip;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try{
				
				client.run();
				
				/*try {
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
				
				EventLoopGroup group = new NioEventLoopGroup(); 
				
				try{
					Bootstrap b = new Bootstrap(); 
	 				b.group(group)
	 					.channel(NioSocketChannel.class)
	 					.remoteAddress(new InetSocketAddress(ip, 5555))
	 					.handler(new ChannelInitializer<SocketChannel>() {

							@Override
							protected void initChannel(SocketChannel chcli) throws Exception {
								// TODO Auto-generated method stub
								chcli.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));    
								chcli.pipeline().addLast("frameEncoder", new LengthFieldPrepender(4));    
								chcli.pipeline().addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));    
								chcli.pipeline().addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));  
								chcli.pipeline().addLast(TC);
								NS.chcli=chcli;
							}
	 						
	 					});  
	 				//b.connect().addListener(new ConnectionListener(b));
	 				//等待同步成功  
	 				ChannelFuture cf = b.connect().sync();
	 				//等待关闭监听端口 
	 				cf.channel().closeFuture().sync();
	 				
				} catch (Exception ex) {  
		 			ex.printStackTrace();
		        } finally {
					group.shutdownGracefully().sync();
				}*/
			 } catch (Exception ex) { 
	 			 ex.printStackTrace();
	         }
		}
	};
	
	
	/*public class ConnectionListener implements ChannelFutureListener {  
		  private Client client;  
		  public ConnectionListener(Client client) {  
		    this.client = client;  
		  }  
		  @Override  
		  public void operationComplete(ChannelFuture channelFuture) throws Exception {  
		    if (!channelFuture.isSuccess()) {  
		      System.out.println("Reconnect");  
		      final EventLoop loop = channelFuture.channel().eventLoop();  
		      loop.schedule(new Runnable() {  
		        @Override  
		        public void run() {  
		          client.createBootstrap(new Bootstrap(), loop);  
		        }  
		      }, 1L, TimeUnit.SECONDS);  
		    }  
		  }  
		}*/
	
	
	public void initView() {
		// 关闭程序
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		// 禁止窗口最大化
		setResizable(false);

		// 设置程序窗口居中显示
		Point p = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getCenterPoint();
		setBounds(p.x - WIDTH / 2, p.y - HEIGHT / 2, WIDTH, HEIGHT);
		this.setLayout(null);

		setTitle("Wifi采集器");
	}

	public void initComponents() {
		// 数据显示
		dataView.setFocusable(false);
		scrollDataView.setBounds(10, 10, 475, 200);
		add(scrollDataView);

		commChoice.setFocusable(false);
		commChoice.setBounds(60, 25, 100, 20);
		serialPortPanel.add(commChoice);

		baudrateLabel.setForeground(Color.gray);
		baudrateLabel.setBounds(10, 60, 40, 20);
		serialPortPanel.add(baudrateLabel);

		baudrateChoice.setFocusable(false);
		baudrateChoice.setBounds(60, 60, 100, 20);
		serialPortPanel.add(baudrateChoice);

		// 操作
		operatePanel.setBorder(BorderFactory.createTitledBorder("操作"));
		operatePanel.setBounds(70, 220, 375, 100);
		operatePanel.setLayout(null);
		add(operatePanel);

		serialPortOperate.setFocusable(false);
		serialPortOperate.setBounds(210, 40, 90, 30);
		operatePanel.add(serialPortOperate);

		sendData.setFocusable(false);
		sendData.setBounds(70, 40, 90, 30);
		operatePanel.add(sendData);
	}

	@SuppressWarnings("unchecked")
	public void initData() {
		commList = SerialPortManager.findPort();
		// 检查是否有可用串口，有则加入选项中
		if (commList == null || commList.size() < 1) {
			//ShowUtils.warningMessage("没有搜索到有效串口！");
		} else {
			for (String s : commList) {
				commChoice.addItem(s);
			}
		}

		baudrateChoice.addItem("9600");
		baudrateChoice.addItem("19200");
		baudrateChoice.addItem("38400");
		baudrateChoice.addItem("57600");
		baudrateChoice.addItem("115200");
	}

	public void actionListener() {
		serialPortOperate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if ("关闭串口".equals(serialPortOperate.getText())
						&& serialport == null) {
					//openSerialPort(e);
				} else {
					closeSerialPort(e);
				}
			}
		});

		sendData.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sendData(e);
			}
		});
	}

	/**
	 * 关闭串口
	 * 
	 * @param evt
	 *            点击事件
	 */
	private void closeSerialPort(java.awt.event.ActionEvent evt) {
		System.exit(0);
	}

	/**
	 * 发送数据
	 * 
	 * @param evt
	 *            点击事件
	 */
	private void sendData(java.awt.event.ActionEvent evt) {
		
		NS.dataView = this.dataView;
		new Thread(work).start();
		
	}


	public Runnable work = new Runnable() {
		
		EventLoopGroup bossGroup = new NioEventLoopGroup(); 
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        
        public void run() {
	        
	        try{  
	            ServerBootstrap b=new ServerBootstrap();  
	            b.group(bossGroup,workerGroup)
	            	.channel(NioServerSocketChannel.class)
	            	.option(ChannelOption.SO_BACKLOG,1024)
	            	.childHandler(NS);  
	            
	           
	            b = b.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
	                public void initChannel(SocketChannel chsoc) throws Exception {
	                   chsoc.pipeline().addLast(NS);
	                   socketcount++;
	                   socketlist.put(Integer.toString(socketcount),chsoc);
	                   TC.socketlist = socketlist;
	                }
	            });
	            
	            //绑定端口，等待同步成功  
	            ChannelFuture f;
				f = b.bind(5550).sync();
	            //等待服务端关闭监听端口  
	            f.channel().closeFuture().sync(); 
	        } catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  finally {  
	            //释放线程池资源  
	            bossGroup.shutdownGracefully();  
	            workerGroup.shutdownGracefully();  
	        } 
        }
	};
	
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new MainFrame().setVisible(true);
			}
		});
	}
	
	public void DateView(String datesend) {
		// TODO Auto-generated method stub
		dataView.append(datesend + "\r\n");
		
	}
				
 }  
	 
