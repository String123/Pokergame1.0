package com.string.pokergame.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 游戏的服务器端,用于接收客户端的信息和发送控制游戏的游戏状态
 * 
 * @author String
 * 
 */
public class GameServer {
	ServerSocket serverSocket = null;
	ExecutorService execut = Executors.newFixedThreadPool(50);
	Room[] room = new Room[10];
	List<String> nameList = new ArrayList<String>();
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public GameServer() {
		try {
			// 服务器接收端口为3000
			serverSocket = new ServerSocket(3000);
			// 初始化每个房间
			for (int i = 0; i < room.length; i++) {
				room[i] = new Room();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		Socket clientSockt = null;
		while (true) {
			try {
				System.out.println(sdf.format(new Date()) + "本机IP地址为:"
						+ ServerUtil.getLocalInetAddress());
				System.out.println(sdf.format(new Date()) + "本机端口号为:3000");
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

	private class ClientHandler implements Runnable {
		Socket clientSocket = null;

		public ClientHandler(Socket s) {
			clientSocket = s;
		}

		@Override
		public void run() {
			InputStream clientIs = null;
			InputStreamReader clientIsr = null;
			BufferedReader clientBr = null;
			OutputStream clientOs = null;
			OutputStreamWriter clientOsw = null;
			PrintWriter clientPw = null;
			String name = null, joinOrNot = null, ready = null, tmpMsg = null;
			int roomNum = -1, playerID = -1, toDoId = -1;
			try {
				clientIs = clientSocket.getInputStream();
				clientIsr = new InputStreamReader(clientIs,"utf-8");
				clientBr = new BufferedReader(clientIsr);
				clientOs = clientSocket.getOutputStream();
				clientOsw = new OutputStreamWriter(clientOs,"utf-8");
				clientPw = new PrintWriter(clientOsw, true);

				name = clientBr.readLine();
				// 判断名字是否合法或者重复,传回客户端对应的信息
				while (name == null || nameList.contains(name)
						|| name.equals("")) {
					clientPw.println("N");
					name = clientBr.readLine();
				}
				clientPw.println("Y");
				nameList.add(name);
				// 该循环为进入大厅后的循环
				while (true) {
					// 判断是加入房间还是创建新房间 该循环为大厅循环
					while (true) {
						joinOrNot = null;
						while (joinOrNot == null
								|| !(joinOrNot.equals("Y") || joinOrNot
										.equals("N"))) {
							joinOrNot = clientBr.readLine();
						}
						if (joinOrNot.equals("Y")) {
							tmpMsg = clientBr.readLine();
							if (tmpMsg == null || tmpMsg.equals("")) {
								clientPw.println("ERROR");
								continue;
							}
							roomNum = Integer.valueOf(tmpMsg) - 1;
							if (room[roomNum].pNum == 0
									|| room[roomNum].pNum > 2) {
								clientPw.println("ERROR");
								continue;
							}
							clientPw.println("RIGHT");
							playerID = room[roomNum].addPlayer(name, clientBr,
									clientPw);
							execut.execute(room[roomNum]);
							System.out.println(sdf.format(new Date()) + name
									+ "加入了第" + (roomNum + 1) + "桌游戏");
							break;
						} else {
							roomNum = 0;
							while (room[roomNum].pNum != 0) {
								roomNum++;
							}
							clientPw.println(roomNum);
							playerID = room[roomNum].addPlayer(name, clientBr,
									clientPw);
							System.out.println(sdf.format(new Date()) + name
									+ "加入了第" + (roomNum + 1) + "桌游戏");
							break;
						}
					}
					// 进入等待状态,如果接收到准备后则无法取消准备状态 该循环为房间循环,从准备到出房间
					while (true) {
						// 准备阶段的循环
						boolean isReady = false;
						while (!isReady) {
							ready = clientBr.readLine();
							System.out.println(sdf.format(new Date())
									+ "ready=" + ready);
							// 如果用户在此时退出则退出准备循环
							if (ready.equals("EXIT")) {
								room[roomNum].playerLeave(playerID);
								playerID = -1;
								clientPw.println("EXIT");
								System.out.println(sdf.format(new Date())
										+ name + "退出了" + "第" + (roomNum + 1)
										+ "桌");
								break;
							}
							System.out.println(sdf.format(new Date()) + "第"
									+ (roomNum + 1) + "桌的" + name + "准备了!");

							room[roomNum].ready(playerID);
							isReady = true;
							// 实时监听当前房间的准备人数和当前玩家的准备状态
							System.out.println(sdf.format(new Date())
									+ room[roomNum].ready);
							while (room[roomNum].ready < 3) {
								clientPw.println("ALIVE");
								Thread.sleep(100);
								// 如果发现客户端取消准备则退出准备状态监听循环
								if (clientBr.readLine().equals("NOREADY")) {
									isReady = false;
									room[roomNum].cancelReady(playerID);
									System.out.println(sdf.format(new Date())
											+ "第" + (roomNum + 1) + "桌的" + name
											+ "取消准备了!");
									break;
								}
							}
						}
						// 全部已准备,开始游戏 该循环为游戏循环,从发牌到游戏结束
						if (playerID != -1) {
							clientPw.println("START"); // 发送开始信号
							while (true) {
								// 发牌
								clientPw.println("GIVECARDS"); // 发送发牌信号,客户端开始接收卡牌
								for (int i = 0; i < 17; i++) {
									room[roomNum].giveCard(playerID);
									try {
										Thread.sleep(200);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}

								// 等待三个玩家接受完全部的手牌
								clientBr.readLine();
								room[roomNum].over();
								while (room[roomNum].over < 3) {
									Thread.sleep(200);
								}

								// 发送三张地主牌
								room[roomNum].giveLordCards(playerID);
								// 等待三位玩家接受完全部的地主牌并且显示完毕后再开始抢地主环节
								clientBr.readLine();
								room[roomNum].over();
								while (room[roomNum].over < 6) {
									Thread.sleep(200);
								}
								// 将下一轮的牌洗好
								room[roomNum].newTotal();
								// 随机一个人开始叫地主
								if (room[roomNum].robLord++ == 0) {// 判断其中是否有线程已进入
									toDoId = (int) (Math.random() * 3);
									toDoId = room[roomNum].robLord(toDoId);
									if (toDoId == -1) {
										room[roomNum].ready = 0;
										room[roomNum].putCards = false;
										room[roomNum].over = 0;
										room[roomNum].robLord = -1;
										continue;
									}
									room[roomNum].putCards(toDoId);
									break;
								} else {// 未进入的线程等待游戏结束
									while (room[roomNum].putCards) {
										Thread.sleep(500);
									}
									if (room[roomNum].robLord == -1) {
										room[roomNum].over = 0;
										room[roomNum].ready = 0;
										Thread.sleep(550);
										room[roomNum].robLord = 0;
										room[roomNum].putCards = true;
										continue;
									} else {
										break;
									}
								}
							}
						}
						// 游戏结束,判断是继续还是退出房间,如果playerID为-1则直接退出房间循环
						if (playerID == -1) {
							break;
						} else {
							Thread.sleep(600);
							room[roomNum].ready = 0;
							room[roomNum].over = 0;
							room[roomNum].robLord = 0;
							room[roomNum].putCards = true;
							System.out.println(sdf.format(new Date()) + "第"
									+ roomNum + "桌的游戏结束了!");
							tmpMsg = clientBr.readLine();
							if (tmpMsg.equals("CONTINUE")) {
								System.out.println(sdf.format(new Date())
										+ playerID + "继续游戏!");
								continue;
							} else {
								clientPw.println("EXIT");
								room[roomNum].playerLeave(playerID);
								System.out.println(sdf.format(new Date())
										+ name + "退出了" + "第" + roomNum + "桌");
								break;
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				System.out.println(sdf.format(new Date()) + "一台客户端下线了!");
				nameList.remove(name);
				if (playerID != -1) {
					room[roomNum].playerLeave(playerID);
				}
				if(room[roomNum].clear()){
					room[roomNum]=new Room();
				}
				try {
					if (clientSocket != null) {
						clientSocket.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
