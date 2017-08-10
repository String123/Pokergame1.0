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
 * 游戏的服务器端,用于接收客户端的信息和发送控制游戏的游戏状态
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
			//从config文件中读取服务器监听端口
			config = GameServer.class.getResourceAsStream("server-config.txt");
			text = "";
			while ((len = config.read(b)) != -1) {
				text = text + new String(b, 0, len);
			}
			//将监听端口打开
			serverPort = Integer.valueOf(text.substring(text.indexOf("=") + 1));
			serverSocket = new ServerSocket(serverPort);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("端口打开失败或配置文件读取失败!");
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
				System.out.println(sdf.format(new Date()) + "本机IP地址为:" + ServerUtil.getLocalInetAddress());
				System.out.println(sdf.format(new Date()) + "本机端口号为:"+serverPort);
				System.out.println(sdf.format(new Date()) + "等待客户端连接...");
				clientSockt = serverSocket.accept();
				System.out.println(sdf.format(new Date()) + "一台客户端连接成功!");

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
