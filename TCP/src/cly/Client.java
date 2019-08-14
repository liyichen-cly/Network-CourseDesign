package cly;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
@SuppressWarnings("serial")
public class Client extends JFrame {
	
    TextField windowSize = new TextField(); 
    TextArea ta = new TextArea(); 
    Label label = new Label("\u7A97\u53E3\u5927\u5C0F");//窗口大小
    Label label_1 = new Label("\u5BA2\u6237\u7AEF\u4FE1\u606F");//信息
    Button button_start = new Button("\u63A5\u6536\u6A21\u5F0F");//接收模式
    JLabel label_2 = new JLabel("\u5206\u6BB5\u5927\u5C0F");//分段大小
    TextField datalong = new TextField();
    Button button = new Button("\u53D1\u9001\u6A21\u5F0F");//发送模式
    transferm tran = new transferm();
    Button button_1 = new Button("\u5BFC\u5165\u53D1\u9001\u6570\u636E");//导入发送数据
    TextField output_textField = new TextField();
    Button button_output = new Button("\u5BFC\u51FA\u6536\u5230\u6570\u636E");//导出收到数据
    TextField zhenField = new TextField();
    Button button_zhenhao = new Button("\u663E\u793A");//显示
    
	Socket s = null;
    DataOutputStream dos = null;
    DataInputStream dis = null;
    static boolean bConnected = false;
    static boolean bSend = false;
    BlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>();
    RSThread rs = new RSThread(); 
    SThread send = new SThread();
    ACKThread ack = new ACKThread();
    
    static long endTime,startTime;
    static int windowsize,datasize,count,revccount;
    static int windowpointer = 0;
    static int ACK=-1,SEQ=-1;
    String filePath,outputPath;
    String ackframe="0";
    List<String> sendlist = new ArrayList<String>();
    List<List<String>> List_buffer = new ArrayList<List<String>>();
    List<Byte> buffer = new ArrayList<Byte>();
    static List<List<Byte>> windowbuffer = new ArrayList<List<Byte>>();
    static List<List<Byte>> sendbuffer = new ArrayList<List<Byte>>();
    static List<Byte> Byte_buffer = new ArrayList<Byte>(); 
    static List<Integer> timelist = new ArrayList<Integer>();
    static List<Integer> Ban = new ArrayList<Integer>();
    static List<List<Byte>> Listinlist = new ArrayList<List<Byte>>();
    static List<List<Byte>> display = new ArrayList<List<Byte>>();
    static List<byte[]> revclist = new ArrayList<byte[]>();
    static List<Byte> displaybuffer0 = new ArrayList<Byte>();
    static byte[] ACKbyte,SEQbyte;
    
	public Client() {

    	getContentPane().setLayout(null);
        setSize(709,515);
        setLocation(0,0);
        getContentPane().add(windowSize);
        ta.setBackground(Color.WHITE);
        getContentPane().add(ta);
        windowSize.setBounds(111, 436, 82, 23);
        ta.setBounds(10, 35, 671, 319);
        ta.setEditable(false);//不可编辑
        
        label.setBounds(108, 407, 69, 23);
        getContentPane().add(label);
        
        label_1.setBounds(309, 10, 196, 23);
        getContentPane().add(label_1);
        button_start.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		button_2(e);
        	}
        });
        
        button_start.setBounds(578, 372, 76, 23);
        getContentPane().add(button_start);
        
        windowSize.setText("5");
        datalong.setText("60");
        zhenField.setText("1");
        output_textField.setText("D:\\copy.txt");
        
        label_2.setBounds(10, 407, 82, 23);
        getContentPane().add(label_2);
        
        datalong.setBounds(10, 437, 82, 23);
        getContentPane().add(datalong);
        
        
        button.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		try {
					startsend();//以发送模式运行
				} catch (IOException e1) {
					e1.printStackTrace();
				}
        	}
        });
        button.setBounds(578, 436, 76, 23);
        getContentPane().add(button);
        
        
        button_1.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		filePath =  new FileChooser().Chooser();//选择传输文件位置
        	}
        });
        button_1.setBounds(554, 407, 100, 23);
        getContentPane().add(button_1);
        
        
        output_textField.setBounds(85, 372, 241, 23);
        getContentPane().add(output_textField);
        
        Label label_3 = new Label("\u5BFC\u51FA\u8DEF\u5F84");//导出路径
        label_3.setBounds(10, 372, 69, 23);
        getContentPane().add(label_3);
        button_output.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		output();
        	}
        });
        
        
        button_output.setBounds(346, 372, 99, 23);
        getContentPane().add(button_output);
        button_zhenhao.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		displayFrame();
        	}
        });

        button_zhenhao.setBounds(249, 436, 76, 23);
        getContentPane().add(button_zhenhao);
        
        Label label_4 = new Label("\u663E\u793A\u5B9E\u9645\u5E27");//显示实际帧
        label_4.setBounds(219, 407, 100, 23);
        getContentPane().add(label_4);
        
       
        zhenField.setBounds(219, 436, 24, 23);
        getContentPane().add(zhenField);
        addWindowListener(new WindowAdapter() { 
                    public void windowClosing(WindowEvent e) {
                        disconnect();
                        System.exit(0);
                    }
                });
	}
	protected void displayFrame() {//显示帧内容
		int framenum = Integer.parseInt(zhenField.getText());
	      for(int i = 0; i<display.get(framenum).size(); i++)
	       {
	    	  if(i%30 == 0)
	    	  {
	    		  appendStr("\n");
	    	  }
	    		  appendStr(display.get(framenum).get(i)+" ");
	       }
		
	}
	protected void output() {
		revcdecode();
	}
	
	protected void startsend() throws IOException {
		appendStr("当前以发送模式运行\n");
		button_start.disable();
		button_output.disable();
		bSend = true;
		datasize = Integer.parseInt(datalong.getText());
		windowsize = Integer.parseInt(windowSize.getText());
		sendcode(windowsize);
		creatThread();
	}
	
	protected void button_2(ActionEvent e) {
		appendStr("当前以接收模式运行\n");
		datasize = Integer.parseInt(datalong.getText());
		button.disable();
		button_1.disable();
		bSend = false;
		creatThread2();
	}

	public void sendcode(int win) throws IOException
	{
		byte[] filebytearray= toByteArray(filePath);
		//System.out.println(filebytearray.length);
		int b = 0;//段的数量
        for (int i = 1; i <= filebytearray.length; i++)
        {
            if (i % datasize == 0)
            {
            	b++;
                continue;
            }
            if (i >= filebytearray.length)
            {
                b++;
                break;
            }
        }
        for(int i = 0;i<filebytearray.length;i++)
        {
        	Byte_buffer.add(filebytearray[i]);
        }
		for(int k=0;k<b;k++)
		{	
			Make_Frame tcp = new Make_Frame();
			Listinlist.add(tcp.gettcp(8888, 8888, k*datasize, 0, 20, win, 0, 0));
	
		}
        for(int i =b-1 ; i>=0 ; i--)
        {
        	//buffu1 = big.get(i);
        	for(int j=0;j<20;j++)
        	{
        		Byte_buffer.add(i*datasize+j, Listinlist.get(i).get(j));
        	}
        }//Byte_buffer
       
        List<Byte> buffer = new ArrayList<Byte>();
        for(int i = 1;i<=Byte_buffer.size();i++)
        {
        	buffer.add(Byte_buffer.get(i-1));
        	
        	if(i%80==0)
        	{
        		List<Byte> buffer1 = new ArrayList<Byte>();
        		for(int h=0;h<buffer.size();h++)
        		buffer1.add(buffer.get(h));
        		sendbuffer.add(buffer1);
        		display.add(buffer1);
        		buffer.removeAll(buffer1);
        	}
        	if(i>=Byte_buffer.size())
        	{
        		List<Byte> buffer2 = new ArrayList<Byte>();
        		for(int h=0;h<buffer.size();h++)
        		buffer2.add(buffer.get(h));
        		sendbuffer.add(buffer2);
        		display.add(buffer2);
        		buffer.removeAll(buffer2);
        	}
        }
      
        		
	}
	//
	  public void getFile(byte[] bfile, String filePath) {  
	        BufferedOutputStream bos = null;  
	        FileOutputStream fos = null;  
	        File file = null;  
	        try {  
	            File dir = new File(filePath);  
	            if(!dir.exists()&&dir.isDirectory()){//判断文件目录是否存在  
	                dir.mkdirs();  
	            }  
	            file = new File(filePath);  
	            fos = new FileOutputStream(file);  
	            bos = new BufferedOutputStream(fos);  
	            bos.write(bfile); 
	            appendStr("文件输出完成!\n");
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        } finally {  
	            if (bos != null) {  
	                try {  
	                    bos.close();  
	                } catch (IOException e1) {  
	                    e1.printStackTrace();  
	                }  
	            }  
	            if (fos != null) {  
	                try {  
	                    fos.close();  
	                } catch (IOException e1) {  
	                    e1.printStackTrace();  
	                }  
	            }  
	        }  
	    }  
	  
    public byte[] toByteArray(String filename) throws IOException {  
    	  
        File f = new File(filename);  
        if (!f.exists()) {  
            throw new FileNotFoundException(filename);  
        }  
  
        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());  
        BufferedInputStream in = null;  
        try {  
            in = new BufferedInputStream(new FileInputStream(f));  //创建缓冲区
            int buf_size = 1024;  
            byte[] buffer = new byte[buf_size];  
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {  
                bos.write(buffer, 0, len);  
            }
            return bos.toByteArray();  
        } catch (IOException e) {  
            e.printStackTrace();  
            throw e;  
        } finally {  
            try {  
                in.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
            bos.close();  
        }  
    }  
	
	public void revcdecode()//接收后解码
	{
		outputPath = output_textField.getText();
		
		for(int i=0;i<revclist.size();i++)//仅排序
		{
			for(int j=i;j<revclist.size();j++)
			{
				int dec1=0,dec2=0;
				byte[] todec = new byte[4];
				for(int k =0;k<4;k++)
				{
					todec[k] = revclist.get(i)[k+4];
					dec1 = tran.byte4ToInt(todec);
				}
				for(int k =0;k<4;k++)
				{
					todec[k] = revclist.get(j)[k+4];
					dec2 = tran.byte4ToInt(todec);
				}
				if(dec1 > dec2)
				{
					if(revclist.get(i).length>=revclist.get(j).length)
					{
						byte[] changemax = new byte[revclist.get(i).length];
						byte[] changemin = new byte[revclist.get(j).length];
						changemax = revclist.get(i);
						changemin = revclist.get(j);
						revclist.remove(i);
						revclist.add(i, changemin);
						revclist.remove(j);
						revclist.add(j, changemax);
					}
					else
					{
						byte[] changemax = new byte[revclist.get(j).length];
						byte[] changemin = new byte[revclist.get(i).length];
						changemax = revclist.get(j);
						changemin = revclist.get(i);
						revclist.remove(i);
						revclist.add(i, changemax);
						revclist.remove(j);
						revclist.add(j, changemin);
					}
				}
			}
			
		}
		List<byte[]> revclist2 = new ArrayList<byte[]>();
		for(int i=0;i<revclist.size();i++)//仅排序
		{
			revccount = revclist2.size();
			for(int j=0;j<revccount+1;j++)
			{
				int dec1=0,dec2=0;
				byte[] todec = new byte[4];
				for(int k =0;k<4;k++)
				{
					todec[k] = revclist.get(i)[k+4];
				}
				dec1 = tran.byte4ToInt(todec);
				for(int k =0;k<4;k++)
				{
					if(revclist2.size()!=0)
						{
						if(i==0)
							todec[k] = revclist2.get(j-1)[k+4];
						else if(j<revccount)
							todec[k] = revclist2.get(j)[k+4];
						}
					else
						todec = tran.intToByte4(-1);
				}
				dec2 = tran.byte4ToInt(todec);
				if(dec1 == dec2)
				{
					break;
				}
				if(j+1 >= revclist2.size())
				{
					byte[] changemax = new byte[revclist.get(i).length];
					for(int k =0;k<revclist.get(i).length;k++)
					{
						changemax[k] = revclist.get(i)[k];
					}
					revclist2.add(changemax);
				}
				revccount = revclist2.size();
			}
		}
		int bytelength = 0;
		int a =0;
		for(int i=0;i<revclist2.size();i++)
		{
			for(int j=0;j<revclist2.get(i).length;j++)
			{
				bytelength++;
			}
		}
		
		//--------------------->目的的显示输入的帧号数据
		
		for(int i = 0;i<revclist2.size();i++)
		{
			for(int j=0; j<revclist2.get(i).length;j++)
			{
				byte displaybyte = revclist2.get(i)[j];
				displaybuffer0.add(displaybyte);
			}
		}
		//appendStr(displaybuffer0.size()+"");
        List<Byte> buffer = new ArrayList<Byte>();
        for(int i = 1;i<=displaybuffer0.size();i++)
        {
        	buffer.add(displaybuffer0.get(i-1));
        	
        	if(i%80==0)
        	{
        		List<Byte> buffer1 = new ArrayList<Byte>();
        		for(int h=0;h<buffer.size();h++)
        		buffer1.add(buffer.get(h));
        		display.add(buffer1);
        		buffer.removeAll(buffer1);
        	}
        	if(i>=displaybuffer0.size())
        	{
        		List<Byte> buffer2 = new ArrayList<Byte>();
        		for(int h=0;h<buffer.size();h++)
        		buffer2.add(buffer.get(h));
        		display.add(buffer2);
        		buffer.removeAll(buffer2);//sendbufferTCP_DATA||TCP_DATA||.................
        	}
        }
        //------------------------------>结束
		byte[] filebuffer = new byte[bytelength-revclist2.size()*20];
		for(int i = 0;i<revclist2.size();i++)
		{
			for(int j=20;j<revclist2.get(i).length;j++)
			{
				filebuffer[a] = revclist2.get(i)[j];
				a++;
			}
		}
		getFile(filebuffer,outputPath);
	}
    public static void main(String[] args){
    	Client client =  new Client();
    	client.show();
    }
    public void appendStr(String str)
    {
    	ta.append(str);
    }
    public void creatThread() { //发送端
        connect();
        send.start();
        rs.start();
        ack.start();
    }
    public void creatThread2() { //接收端
        connect();
        rs.start();
        ack.start();
    }
    public void connect() {
        try {
            s = new Socket("127.0.0.1", 8888); 
            dos = new DataOutputStream(s.getOutputStream());
            dis = new DataInputStream(s.getInputStream());
            bConnected = true;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void disconnect() {
    	try {
            dos.close();
            dis.close();
            s.close();
            rs.stop();
            send.stop();
            ack.stop();
        } catch (IOException e) {
            
            e.printStackTrace();
        }
    }
    
    public void putinbuffer()
    {
    	for(int i=0;i<sendbuffer.size();i++)
    	{
    		if(windowbuffer.size()<windowsize)
    		{
    			windowbuffer.add(sendbuffer.get(0));
    			timelist.add(0);
    			Ban.add(0);
    			sendbuffer.remove(0);
    		}
    	}
    }
    class RSThread extends Thread { //接收方
        public void run() {
            try {
                while (bConnected) {
                	if(!bSend)
                	{
                		String str = dis.readUTF();
                		SEQbyte= str.getBytes( "ISO-8859-1");
    	                byte[] SEQbuffer = new byte[20];
                    	if(SEQbyte!=null&&SEQbyte.length>20)//
                    	{
                    		byte[] revcbuffer = new byte[SEQbyte.length];
                    		for(int i=0;i<SEQbyte.length;i++)
                    		{
                    			revcbuffer[i] = SEQbyte[i];
                    		}
                    		revclist.add(revcbuffer);
                    		for(int i=0;i<20;i++)
                    		{
                    			SEQbuffer[i]=SEQbyte[i];
                    		}
                    		SEQbuffer[8] = SEQbuffer[4];
                    		SEQbuffer[9] = SEQbuffer[5];
                    		SEQbuffer[10] = SEQbuffer[6];
                    		SEQbuffer[11] = SEQbuffer[7];
                    		
                    		String strRead = new String(SEQbuffer,"ISO-8859-1");
                    		strRead = String.copyValueOf(strRead.toCharArray());
                    		dos.writeUTF(strRead);
                    		dos.flush();
                    		
                    		byte[] WIN = new byte[2];
    						byte[] HQ = new byte[4];
    						HQ[0] = SEQbyte[4];
    						HQ[1] = SEQbyte[5];
    						HQ[2] = SEQbyte[6];
    						HQ[3] = SEQbyte[7];
    						
    						WIN[0] = SEQbyte[14];
    						WIN[1] = SEQbyte[15];
    						SEQ = tran.byte4ToInt(HQ);
    						count = tran.byte2ToInt(WIN);
                        	String time = new SimpleDateFormat("HH:mm:ss:SSS") .format(new Date() );
                            appendStr(time+"------->收到的SEQ序号:"+SEQ + "				");
                            appendStr("当前窗口大小:"+count+"\n");
                    	}
                	}
                }
            } catch (SocketException e) {
                System.out.println("错误");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    class SThread extends Thread{//发送方
    	
    	public void send(byte[] sendstr) throws IOException
    	{
    		String strRead = new String(sendstr,"ISO-8859-1");
    		strRead = String.copyValueOf(strRead.toCharArray());
    		String time = new SimpleDateFormat("HH:mm:ss:SSS") .format(new Date() );
    		appendStr(time+"------>发送的SEQ帧序号:"+SEQ + "					");
    		appendStr("当前窗口大小:"+count+"\n");
			dos.writeUTF(strRead);
			
			dos.flush();
    	}
    	public void timer()//超时重传
    	{
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    for(int i = 0;i<windowbuffer.size();i++)
                    {
                    	if(timelist.get(i)==0)
                    	{
                    		continue;
                    	}
                    	int bu = timelist.get(i);
                    	timelist.remove(i);
                    	timelist.add(i,bu-500);
                    }
                }
            }, 0, 500);//500ms后调度
    	}
    	public void run()
    	{
    		timer();//定时器
    		while(bConnected)
    		{
    			putinbuffer();
					if(bSend)
					{
						count = windowbuffer.size();
						//System.out.println(windowbuffer.size());
						for(int i = 0;i<count;i++)
						{
							byte[] bytebuffer = new byte[windowbuffer.get(i).size()];
							if(Ban.get(i)<10)
							{
								if(timelist.get(i)==0)//
								{
									for(int j =0;j<windowbuffer.get(i).size();j++)
									{
										bytebuffer[j] = windowbuffer.get(i).get(j);
									}
									byte[] WIN = new byte[2];
									WIN = tran.intToByte2(count);
									bytebuffer[14] = WIN[0];
									bytebuffer[15] = WIN[1];
									byte[] HQ = new byte[4];
									HQ[3] = bytebuffer[7];
									HQ[2] = bytebuffer[6];
									HQ[1] = bytebuffer[5];
									HQ[0] = bytebuffer[4];
									SEQ = tran.byte4ToInt(HQ);
									try {
										int j = 0;
										String time = new SimpleDateFormat("HH:mm:ss:SSS") .format(new Date() );
										if(Ban.get(i)>1)
											appendStr(time+"------>SEQ号:	"+SEQ+"	因为超时重发			当前发送计数:"+Ban.get(i)+"\n");
										send(bytebuffer);//
										timelist.remove(i);
										timelist.add(i, 2500);
										j = Ban.get(i);
										Ban.remove(i);//
										Ban.add(i,j+1);
										} catch (IOException e) {
									// TODO
										e.printStackTrace();
										}
								}
							}
							else
							{
								for(int j =0;j<windowbuffer.get(i).size();j++)
								{
									bytebuffer[j] = windowbuffer.get(i).get(j);
								}
								byte[] HQ = new byte[4];
								HQ[3] = bytebuffer[7];
								HQ[2] = bytebuffer[6];
								HQ[1] = bytebuffer[5];
								HQ[0] = bytebuffer[4];
								int seq = tran.byte4ToInt(HQ);
								appendStr("当前帧因为超时(10)被放弃，SEQ序号:"+seq+"\n");
								windowbuffer.remove(i);
								timelist.remove(i);
								Ban.remove(i);
								putinbuffer();
							}
							try {
								this.sleep(10);
							} catch (InterruptedException e) {
								// TODO 自动生成的 catch 块
								e.printStackTrace();
							}
							//this.suspend();
							if(ACK != -1)
							{
								if(SEQ==ACK)//
								{
									windowbuffer.remove(i);
									timelist.remove(i);
									Ban.remove(i);
									putinbuffer();
								}
							}
							count = windowbuffer.size();
						}
				}
				
    		}
    	}
    }
    class ACKThread extends Thread{

    	public void run()
    	{
    		while(bConnected)
    		{
    			if(bSend)
    			{	
    				try {
    					String str = dis.readUTF();
    					ACKbyte = str.getBytes( "ISO-8859-1");
						//appendStr(str + "\n");
						if(dis!=null&&ACKbyte.length==20)//
						{
							byte[] HQ = new byte[4];
							HQ[3] = ACKbyte[11];
							HQ[2] = ACKbyte[10];
							HQ[1] = ACKbyte[9];
							HQ[0] = ACKbyte[8];
							ACK = tran.byte4ToInt(HQ);
							//sendBuffer.offer(windowsize);//
							String time = new SimpleDateFormat("HH:mm:ss:SSS") .format(new Date() );
								appendStr(time+"       收到的ACK帧序号:"+ACK + "\n");
							}
						send.resume();
	                   
						} catch (IOException e) {
						
							e.printStackTrace();
						}
    				}
    			
				}
				
    		}
    	}
   }
