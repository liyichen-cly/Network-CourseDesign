package cly;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import java.awt.Button;
import java.awt.TextField;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.Color;
public class Server extends JFrame{
	Button button_stop = new Button("\u505C\u6B62\u670D\u52A1\u5668");
	Button button_start = new Button("\u542F\u52A8\u670D\u52A1\u5668");
	
	TextField duibaoField = new TextField();
	TextField yanchiField_2 = new TextField();
	TextField textField_port = new TextField();
	TextField cuobaoField = new TextField();
	
	Label label = new Label("\u4E22\u5305\u7387(%)");
	Label label_1 = new Label("\u670D\u52A1\u5668\u4FE1\u606F");
	Label label_3 = new Label("\u9519\u5305\u7387(%)");
	Label label_4 = new Label("\u5EF6\u8FDF(ms)");
	Label label_2 = new Label("\u7AEF\u53E3\u53F7");
	
	TextArea textArea = new TextArea();
	
	Random ra =new Random();
	
    boolean started = false;
    ServerSocket ss = null;
    List<ClientThread> clients = new ArrayList<ClientThread>(); //保存客户端线程类
    int port,yanchi,cuobao,diubao;
    boolean bConnected = true;
    private ServerThread c;
	public Server() {
        setSize(682,480);
        setLocation(0,0);
        textArea.setBackground(Color.WHITE);
        textArea.setEditable(false);
		getContentPane().setLayout(null);

		duibaoField.setBounds(421, 36, 81, 23);
		getContentPane().add(duibaoField);
		label.setFont(new Font("Calibri", Font.PLAIN, 16));
		
		label.setBounds(342, 36, 87, 23);
		getContentPane().add(label);
		
		textArea.setBounds(10, 94, 644, 331);
		getContentPane().add(textArea);
		
		textField_port.setText("8888");
		cuobaoField.setText("0");
		yanchiField_2.setText("0");
		duibaoField.setText("0");
		label_1.setFont(new Font("Calibri", Font.PLAIN, 20));
		
		label_1.setBounds(10, 65, 160, 23);
		getContentPane().add(label_1);
		button_start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					StartS();
				} catch (IOException e1) {
					// TODO 自动生成的 catch 块
					e1.printStackTrace();
				}
			}
		});
		button_start.setBounds(545, 10, 76, 23);
		
		getContentPane().add(button_start);
		label_2.setFont(new Font("Calibri", Font.PLAIN, 16));
		label_2.setBounds(153, 10, 49, 23);
		
		getContentPane().add(label_2);
		textField_port.setBounds(217, 10, 81, 23);
		
		getContentPane().add(textField_port);

		button_stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                    if (!started) {  
                        appendStr("服务器还未启动，无需停止！错误\n");  
                        return;  
                    }  
                     closeServer();  
                     started = false;
                     bConnected = false;
                     appendStr("服务器已关闭\n");
                        
                }  
		});
		button_stop.setBounds(545, 49, 76, 23);
		getContentPane().add(button_stop);
		label_3.setFont(new Font("Calibri", Font.PLAIN, 16));
		
		label_3.setBounds(342, 65, 76, 23);
		getContentPane().add(label_3);
		
		cuobaoField.setBounds(421, 65, 81, 23);
		getContentPane().add(cuobaoField);
		label_4.setFont(new Font("Calibri", Font.PLAIN, 16));
		
		label_4.setBounds(342, 10, 76, 23);
		getContentPane().add(label_4);
		
		yanchiField_2.setBounds(421, 10, 81, 23);
		getContentPane().add(yanchiField_2);
		
      	 
        addWindowListener(new WindowAdapter() { //响应关闭窗口事件
            public void windowClosing(WindowEvent e) {
                try {
					disconnect();
				} catch (IOException e1) {
					// TODO 自动生成的 catch 块
					e1.printStackTrace();
				}
                System.exit(0);
            }
        });
	}
	//关闭窗口监听器函数
	void disconnect() throws IOException
    {
        try {  
            if (c != null)  
            	c.stop();;// 停止服务器线程  
  
            for (int i = clients.size() - 1; i >= 0; i--) {  
                // 释放资源  
                clients.get(i).stop();// 停止此条为客户端服务的线程  
                clients.get(i).dis.close();  
                clients.get(i).dos.close();  
                clients.get(i).socket.close();  
                clients.remove(i);  
            }  
            if (c != null) {  
                ss.close();// 关闭服务器端连接  
            }  
            started = false;  
        } catch (IOException e) {  
            e.printStackTrace();  
            started = true;  
        } 
	
    }
	//启动服务器监听器函数
	protected void StartS() throws IOException {
        if (started) {  
            appendStr( "服务器已处于启动状态，不要重复启动！错误\n");  
            return;  
        } 
        cuobao = Integer.parseInt(cuobaoField.getText());
        diubao = Integer.parseInt(duibaoField.getText());
        yanchi = Integer.parseInt(yanchiField_2.getText());
		port = Integer.parseInt(textField_port.getText());
		serverStart(port); //启动服务器
		appendStr("服务器已启动!\n");
		appendStr("端口号:  "+textField_port.getText()+"   ");
		appendStr("延迟ms:  "+yanchiField_2.getText()+"    ");
		appendStr("丢包率%:  "+duibaoField.getText()+"    ");
		appendStr("错包率%:  "+cuobaoField.getText()+"\n\n");
	}
	//关闭服务器监听器函数
	protected void closeServer() {
        try {  
            if (c != null)  
            	c.stop();;// 停止服务器线程  
  
            for (int i = clients.size() - 1; i >= 0; i--) {  
                // 释放资源  
                clients.get(i).stop();// 停止此条为客户端服务的线程  
                clients.get(i).dis.close();  
                clients.get(i).dos.close();  
                clients.get(i).socket.close();  
                clients.remove(i);  
            }  
            if (c != null) {  
                ss.close();// 关闭服务器端连接  
            }  
            started = false;  
        } catch (IOException e) {  
            e.printStackTrace();  
            started = true;  
        } 
		
	}
	//启动服务器后续函数
	void serverStart(int port2) throws IOException {
        ss = new ServerSocket(port2); //在port2号端口创建监听socket 
        c = new ServerThread (ss);  
        c.start();
        //new Thread(c).start(); //启动线程
        started = true;
	}
	//主函数
	public static void main(String[] args) {
    	Server Frame = new Server();
    	Frame.show();
    }
	//文本显示
    public void appendStr(String str)
    {
    	textArea.append(str);
    }
    //服务器线程
    class ServerThread extends Thread { //建立客户端线程接收
    	private ServerSocket serverSocket; 
        public ServerThread (ServerSocket ss) {
        	serverSocket = ss;
        }

        public void run() {
                while (true) {
                	Socket socket;
					try {
						socket = serverSocket.accept();
						appendStr("客户端接入成功\n");
		                 ClientThread client = new ClientThread(socket);  
		                 client.start();// 开启对此客户端服务的线程  
		                 clients.add(client);    
					} catch (IOException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}   
            }
    }
}
    //客户端线程,用于服务客户端通信
    class ClientThread extends Thread {  
        private Socket socket;  
        DataInputStream dis = null;
        DataOutputStream dos = null;
        ClientThread(Socket socket)
        {
        	this.socket = socket;
        	try {
        		dis = new DataInputStream(socket.getInputStream());//发送缓冲区
        		dos = new DataOutputStream(socket.getOutputStream());//接收缓冲区
        		} catch (IOException e) {
        		e.printStackTrace();
        	}
        }
        	void send(String str) {
        		try {
        			dos.writeUTF(str);//接收到的内容回传给客户
        		} catch (SocketException e) {
        			appendStr("对方退出了\n");
        			
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
        	}
       		public void run()
       		{
       		try {
       			while(bConnected)
       			{
       				String str;
       					str = dis.readUTF();//接收客户发送的内容
	      				for (int i = 0; i < clients.size(); i++) {
	      					ClientThread c = clients.get(i);
	      					if(c.equals(this)==false)
	      						{
	      							try {
										ClientThread.sleep(yanchi);//用户定义延迟
									} catch (InterruptedException e) {
										// TODO 自动生成的 catch 块
										e.printStackTrace();
									}
	      							if(Math.sqrt((100-cuobao)/100)*100>=(ra.nextInt(100)+1))//错包率模拟
	      							{
	      								if(Math.sqrt((100-diubao)/100)*100>=(ra.nextInt(100)+1))//丢包概率模拟
	      								{
	      									c.send(str);
	      								}
	      								else
	      								{}
	      							}
	      							else
	      							{
	      								c.send(str);
	      								c.send(str);//错包发送相同的两帧
	      							}
	      							
	      						}
	                    }
       			
	} 
       		}catch (EOFException e) {
		                appendStr("客戶端退出了\n");
		                clients.removeAll(clients);
		            } catch (IOException e) {
		                e.printStackTrace();
		            }finally {
		                if (dis != null)
		                    if (socket != null)
		                        try {
		                            dis.close();
		                            socket.close();
		                            dos.close();
		                        } catch (IOException e) {
		                            e.printStackTrace();
		                        }

       			}
       		}
      }
  }  