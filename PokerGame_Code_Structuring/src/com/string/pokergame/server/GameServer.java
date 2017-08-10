package com.string.pokergame.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ��Ϸ�ķ�������,���ڽ��տͻ��˵���Ϣ�ͷ��Ϳ�����Ϸ����Ϸ״̬
 * 
 * @author String
 * 
 */
public class GameServer {
	public ServerSocket serverSocket = null;
	ExecutorService execut = Executors.newFixedThreadPool(50);
	
	public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public Integer serverPort;

	
	public GameServer() {
		InputStream config=null;
		String text;
		int len;
		byte[] b = new byte[1024 * 10];
		
		try {
			//��config�ļ��ж�ȡ�����������˿�
			config = GameServer.class.getResourceAsStream("server-config.txt");
			text = "";
			while ((len = config.read(b)) != -1) {
				text = text + new String(b, 0, len);
			}
			//�������˿ڴ�
			serverPort = Integer.valueOf(text.substring(text.indexOf("=") + 1));
			serverSocket = new ServerSocket(serverPort);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("�˿ڴ�ʧ�ܻ������ļ���ȡʧ��!");
		} finally{
			if (config!=null){
				try {
					config.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void start() {
		Socket clientSockt = null;
		while (true) {
			try {
				System.out.println(sdf.format(new Date()) + "����IP��ַΪ:" + ServerUtil.getLocalInetAddress());
				System.out.println(sdf.format(new Date()) + "�����˿ں�Ϊ:"+serverPort);
				System.out.println(sdf.format(new Date()) + "�ȴ��ͻ�������...");
				clientSockt = serverSocket.accept();
				System.out.println(sdf.format(new Date()) + "һ̨�ͻ������ӳɹ�!");

				ClientHandler ch = new ClientHandler(clientSockt);
				execut.execute(ch);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		GameServer server = new GameServer();
		server.start();
	}

	
}
