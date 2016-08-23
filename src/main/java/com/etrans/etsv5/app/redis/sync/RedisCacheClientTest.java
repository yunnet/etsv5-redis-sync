package com.etrans.etsv5.app.redis.sync;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.etrans.etsv5.lib.redisSync.ObjCache;
import com.etrans.etsv5.lib.redisSync.ObjCacheRequest;
import com.etrans.etsv5.lib.redisSync.ObjCacheResponse;
import com.etrans.etsv5.lib.redisSync.ObjCacheType;
import com.etrans.lib.net.tcp.ITcpClient;
import com.etrans.lib.net.tcp.ITcpListener;
import com.etrans.lib.net.tcp.codec.CodecType;
import com.etrans.lib.net.tcp.codec.kryo.KryoHeader;
import com.etrans.lib.net.tcp.codec.kryo.KryoMessage;
import com.etrans.lib.net.tcp.netty.NettyTcpClient;

/** 
 * 工程名称: ets-lib  <br />
 * 版权所有 (C) 2016 e-Trans Company  <br />
 * 单元名称: NettyClientTest.java  <br />
 * 说       明:   <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016年6月15日 下午1:21:20  <br />
 * 最后修改: 2016年6月15日 下午1:21:20  <br />
 * 修改历史:   <br />
 */
public class RedisCacheClientTest extends JFrame {
	private static final long serialVersionUID = -755821087911297460L;

	private static final Logger logger = LoggerFactory.getLogger(RedisCacheClientTest.class.getSimpleName());

	//设置label长度
	public static final int JLABEL_WIDTH = 100;
	
	//设置label宽度
	public static final int JLABEL_HEIGHT = 21;
	
	//设置text框长度
	public static final int JTEXTFIELD_WIDTH = 121;
	
	//设置text框高度
	public static final int JTEXTFIELD_HEIGHT = 21;
	
	//设置text框长度
	public static final int JBUTTON_WIDTH = 100;
	
	//设置text框高度
	public static final int JBUTTON_HEIGHT = 25;
	
	//设置上面控件直接的间隔
	public static final int SPACING = 10;
		
	private JLabel label_host = null;
	private JTextField text_host  = null;
	
	private JLabel label_port = null;
	private JTextField text_port = null;
	
	private JLabel label_table = null;
	private JTextField text_table = null;
	
	private JLabel label_fields = null;
	private JTextField text_fields = null;
	
	private JLabel label_condition = null;
	private JTextField text_condition = null;
	
	private JLabel label_user = null;
	private JTextField text_user = null;
	
	private JButton login_button = null;
	private boolean isOK;
	private JButton exec_button = null;
	
	private JTextArea log_memo = null;
	
	private JPopupMenu popupMenu = null;
	private JMenuItem item1 = null;
	
	
	private NettyTcpClient client;
	private int seq = 0;
	
	/**
	 * 构造函数
	 */
	public RedisCacheClientTest(){
		//设置工具标题
		setTitle("Redis cache client");
		
		setName("formMain");
		
		//最大化
//		setExtendedState(MAXIMIZED_BOTH);
		
		//获取屏幕大小
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    
	    int width = (int) (screenSize.getWidth() * 8 / 10);
	    int height = (int) (screenSize.getHeight() * 8 / 10);
	    
		//设置界面大小
		setSize(width, height);
		
		//设置界面居中
		setLocationRelativeTo(null);
		
		//设置后，关闭程序时自动释放资源
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		//设置界面信息
		setContentPane(getPanel());
		
		//设置界面可见
		setVisible(true);
		
		
		client = new NettyTcpClient();
		client.setCodec(CodecType.KRYO);
		client.addListener(listener);
	}
	
	public Container getPanel(){
		JPanel panel = new JPanel();
		panel.setLayout(null);
		
		JPanel panel2 = new JPanel();
		panel2.setLayout(new BorderLayout());
		
		item1 = new JMenuItem("clear");
		item1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				log_memo.setText("");
			}
		});
		
		popupMenu = new JPopupMenu();
		popupMenu.add(item1);
		
		log_memo = new JTextArea(100, 300);
		log_memo.setLineWrap(true);
		log_memo.setWrapStyleWord(true);
		log_memo.add(popupMenu);
		log_memo.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
						popupMenu.show(e.getComponent(), e.getX(), e.getY());
				   }
			}
		});
		JScrollPane scrollPane = new JScrollPane(log_memo, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		label_host = new JLabel("host");
		text_host = new JTextField("127.0.0.1");
		
		label_port = new JLabel("port");
		text_port = new JTextField("7001");
		
		label_table = new JLabel("table");
		text_table = new JTextField("pub_user");
		
		label_fields = new JLabel("fields");
		text_fields = new JTextField("");
		
		label_condition = new JLabel("condition");
		text_condition = new JTextField("");
		
		label_user = new JLabel("user");
		text_user = new JTextField("1");
		
		login_button = new JButton("connect");
		login_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!isOK){
					isOK = true;
					if(text_host.getText().isEmpty())
						text_host.setText("请输入服务地址");
					
					if (text_port.getText().isEmpty())
						text_port.setText("请输入端口");
					
					client.setServer(text_host.getText(), Utils.StrToIntDef(text_port.getText(), 10, 0));
					client.start();
					
					login_button.setText("disconnect");
					
					String tmp_str = String.format("redis client<%s:%s>", text_host.getText(), text_port.getText());
					doAddLog(tmp_str);
				}else{
					isOK = false;
					
					client.stop();
					login_button.setText("connect");
					
					doAddLog("redis disconnect.");
				}
			}
		});
		
		exec_button = new JButton("go");
		exec_button.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				ObjCacheRequest request = new ObjCacheRequest();
				request.setTableName(text_table.getText());
				request.setFields(text_fields.getText());
				request.setCondition(text_condition.getText());
				request.setUserID(Utils.StrToIntDef(text_user.getText(), 10, 0));
				request.setRequestID(getSeq());
				
				doAddLog("request <<< " + request.toString());
				
				send(request);			
			}
		});
		
		
		//host
		label_host.setBounds(30, 10, JLABEL_WIDTH, JLABEL_HEIGHT);
		text_host.setBounds(30, label_host.getY() + label_host.getHeight(), JTEXTFIELD_WIDTH, JTEXTFIELD_HEIGHT);
		
		//port
		label_port.setBounds(text_host.getX() + text_host.getWidth() + 5, 10, JLABEL_WIDTH, JLABEL_HEIGHT);
		text_port.setBounds(text_host.getX() + text_host.getWidth() + 5, label_port.getY() + label_port.getHeight(), JTEXTFIELD_WIDTH, JTEXTFIELD_HEIGHT);
		
		//login
		login_button.setBounds(text_port.getX() + text_port.getWidth() + 20, label_port.getY() + label_port.getHeight() - 2, JBUTTON_WIDTH, JBUTTON_HEIGHT);
		
		//table
		label_table.setBounds(30, text_host.getY() + text_host.getHeight(), JLABEL_WIDTH, JLABEL_HEIGHT);
		text_table.setBounds(30, label_table.getY() + label_table.getHeight(), JTEXTFIELD_WIDTH, JTEXTFIELD_HEIGHT);
		
		//fields
		label_fields.setBounds(text_table.getX() + text_table.getWidth() + 5, text_host.getY() + text_host.getHeight(), JLABEL_WIDTH, JLABEL_HEIGHT);
		text_fields.setBounds(text_table.getX() + text_table.getWidth() + 5, label_fields.getY() + label_fields.getHeight(), JTEXTFIELD_WIDTH, JTEXTFIELD_HEIGHT);
		
		//condition
		label_condition.setBounds(text_fields.getX() + text_fields.getWidth() + 5, text_host.getY() + text_host.getHeight(), JLABEL_WIDTH, JLABEL_HEIGHT);
		text_condition.setBounds(text_fields.getX() + text_fields.getWidth() + 5, label_condition.getY() + label_condition.getHeight(), JTEXTFIELD_WIDTH, JTEXTFIELD_HEIGHT);
		
		label_user.setBounds(text_condition.getX() + text_condition.getWidth() + 5, text_host.getY() + text_host.getHeight(), JLABEL_WIDTH, JLABEL_HEIGHT);
		text_user.setBounds(text_condition.getX() + text_condition.getWidth() + 5, label_user.getY() + label_user.getHeight(), JTEXTFIELD_WIDTH, JTEXTFIELD_HEIGHT);
		
		//go
		exec_button.setBounds(text_user.getX() + text_user.getWidth() + 20, label_user.getY() + label_user.getHeight() - 2, JBUTTON_WIDTH, JBUTTON_HEIGHT);
		
		//panel2
		panel2.setBounds(0, text_table.getY() + text_table.getHeight() + 20, this.getWidth()-17, this.getHeight() - text_table.getY() - text_table.getHeight()-60);
		
		panel.add(label_host);
		panel.add(text_host);
		
		panel.add(label_port);
		panel.add(text_port);
		
		panel.add(login_button);
		panel.add(exec_button);
		
		panel.add(label_table);
		panel.add(text_table);
		
		panel.add(label_fields);
		panel.add(text_fields);
		
		panel.add(label_condition);
		panel.add(text_condition);
		
		panel.add(label_user);
		panel.add(text_user);
		
		panel2.add(scrollPane);
		
		panel.add(panel2);
		
		return panel;
	}
	
	public void doAddLog(final String _data){
		if(log_memo.getLineCount() > 1000)
			log_memo.setText("");
		
		log_memo.append(_data);
		log_memo.append("\r\n");
	}

	ITcpListener<ITcpClient> listener = new ITcpListener<ITcpClient>() {
		@Override
		public void onConn(ITcpClient _sender, boolean _success, Exception _e) {
			if(_success){
				text_host.setEditable(false);
				text_port.setEditable(false);
				doAddLog(String.format("connect is ok. %s", _sender.getServerKey()));
			}
		}

		@Override
		public void onBrok(ITcpClient _sender) {
			text_host.setEditable(true);
			text_port.setEditable(true);
			doAddLog(String.format("connect is brok. %s", _sender.getServerKey()));
		}

		@Override
		public void onData(ITcpClient _sender, Object _data) {
			if(_data instanceof KryoMessage) {
				KryoMessage message = (KryoMessage)_data;
				if(ObjCacheType.C_OCT_Response.getValue() == message.getHeader().getType()) {
					ObjCacheResponse obj = (ObjCacheResponse) message.getBody();
					
					String tmp_str = String.format("%s, user:%d, id:%d, result=%s", 
							Utils.DateTimeToStr(obj.getGenTime()),
                            obj.getUserID(),
                            obj.getReceiptID(), 
                            obj.isResult() ? "true" : "false"
							);
					
					doAddLog("response >>> " +tmp_str);
				}
			}else
				doAddLog("recv err: " + _data);
		}
	};
	
	
	private int getSeq(){
		return seq++ & 0xFFFF;
	}

	public void send(ObjCache _data){
		KryoHeader header = new KryoHeader();
		header.setType((byte)_data.getObjType().getValue());

		KryoMessage message = new KryoMessage();
		message.setHeader(header);
		message.setBody(_data);

		if(client.isConnected()) {
			logger.info("send: {}", message);
			client.write(message);
		}
	}
	
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		new RedisCacheClientTest();
	}
}
