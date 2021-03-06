package com.string.pokergame.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * 服务器端,用于处理游戏主逻辑
 * @author String
 *
 */
public class ClientHandler implements Runnable {
	private Socket clientSocket = null;
	private Room[] room = new Room[10];
	private List<String> nameList = new ArrayList<String>();
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public ClientHandler(Socket s) {
		clientSocket = s;
		// 初始化每个房间
		for (int i = 0; i < room.length; i++) {
			room[i] = new Room();
		}
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
			clientIsr = new InputStreamReader(clientIs, "utf-8");
			clientBr = new BufferedReader(clientIsr);
			clientOs = clientSocket.getOutputStream();
			clientOsw = new OutputStreamWriter(clientOs, "utf-8");
			clientPw = new PrintWriter(clientOsw, true);

			name = clientBr.readLine();
			// 判断名字是否合法或者重复,传回客户端对应的信息
			while (name == null || nameList.contains(name) || name.equals("")) {
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
					while (joinOrNot == null || !(joinOrNot.equals("Y") || joinOrNot.equals("N"))) {
						joinOrNot = clientBr.readLine();
					}
					if (joinOrNot.equals("Y")) {
						tmpMsg = clientBr.readLine();
						if (tmpMsg == null || tmpMsg.equals("")) {
							clientPw.println("ERROR");
							continue;
						}
						roomNum = Integer.valueOf(tmpMsg) - 1;
						if (room[roomNum].getpNum() == 0 || room[roomNum].getpNum() > 2) {
							clientPw.println("ERROR");
							continue;
						}
						clientPw.println("RIGHT");
						playerID = room[roomNum].addPlayer(name, clientBr, clientPw);
						System.out.println(sdf.format(new Date()) + name + "加入了第" + (roomNum + 1) + "桌游戏");
						break;
					} else {
						roomNum = 0;
						while (room[roomNum].getpNum() != 0) {
							roomNum++;
						}
						clientPw.println(roomNum);
						playerID = room[roomNum].addPlayer(name, clientBr, clientPw);
						System.out.println(sdf.format(new Date()) + name + "加入了第" + (roomNum + 1) + "桌游戏");
						break;
					}
				}
				// 进入等待状态,如果接收到准备后则无法取消准备状态 该循环为房间循环,从准备到出房间
				while (true) {
					// 准备阶段的循环
					boolean isReady = false;
					while (!isReady) {
						ready = clientBr.readLine();
						System.out.println(sdf.format(new Date()) + "ready=" + ready);
						// 如果用户在此时退出则退出准备循环
						if (ready.equals("EXIT")) {
							room[roomNum].playerLeave(playerID);
							playerID = -1;
							clientPw.println("EXIT");
							System.out.println(sdf.format(new Date()) + name + "退出了" + "第" + (roomNum + 1) + "桌");
							break;
						}
						System.out.println(sdf.format(new Date()) + "第" + (roomNum + 1) + "桌的" + name + "准备了!");

						room[roomNum].ready(playerID);
						isReady = true;
						// 实时监听当前房间的准备人数和当前玩家的准备状态
						while (room[roomNum].getReady() < 3) {
							clientPw.println("ALIVE");
							Thread.sleep(100);
							// 如果发现客户端取消准备则退出准备状态监听循环
							if (clientBr.readLine().equals("NOREADY")) {
								isReady = false;
								room[roomNum].cancelReady(playerID);
								System.out.println(
										sdf.format(new Date()) + "第" + (roomNum + 1) + "桌的" + name + "取消准备了!");
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
							while (room[roomNum].getOver() < 3) {
								Thread.sleep(200);
							}

							// 发送三张地主牌
							room[roomNum].giveLordCards(playerID);
							// 等待三位玩家接受完全部的地主牌并且显示完毕后再开始抢地主环节
							clientBr.readLine();
							room[roomNum].over();
							while (room[roomNum].getOver() < 6) {
								Thread.sleep(200);
							}
							// 将下一轮的牌洗好
							room[roomNum].newTotal();
							// 随机一个人开始叫地主
							synchronized (room[roomNum]) {
								if (room[roomNum].getRobLord() == 0) {// 判断其中是否有线程已进入
									room[roomNum].setRobLord(room[roomNum].getRobLord() + 1);
									toDoId = (int) (Math.random() * 3);
									toDoId = room[roomNum].robLord(toDoId);
									if (toDoId == -1) {
										room[roomNum].setReady(0);
										room[roomNum].setPutCards(false);
										room[roomNum].setOver(0);
										room[roomNum].setRobLord(-1);
										continue;
									}
									room[roomNum].putCards(toDoId);
									break;
								}
							}
							// 未进入的线程等待游戏结束
							while (room[roomNum].isPutCards()) {
								Thread.sleep(500);
							}
							if (room[roomNum].getRobLord() == -1) {
								room[roomNum].setOver(0);
								room[roomNum].setReady(0);
								Thread.sleep(550);
								room[roomNum].setRobLord(0);
								room[roomNum].setPutCards(true);
								continue;
							} else {
								break;
							}
						}
					}
					// 游戏结束,判断是继续还是退出房间,如果playerID为-1则直接退出房间循环
					if (playerID == -1) {
						break;
					} else {
						Thread.sleep(600);
						room[roomNum].setOver(0);
						room[roomNum].setReady(0);
						room[roomNum].setRobLord(0);
						room[roomNum].setPutCards(true);
						System.out.println(sdf.format(new Date()) + "第" + roomNum + "桌的游戏结束了!");
						tmpMsg = clientBr.readLine();
						if (tmpMsg.equals("CONTINUE")) {
							System.out.println(sdf.format(new Date()) + playerID + "继续游戏!");
							continue;
						} else {
							clientPw.println("EXIT");
							room[roomNum].playerLeave(playerID);
							System.out.println(sdf.format(new Date()) + name + "退出了" + "第" + roomNum + "桌");
							break;
						}
					}
				}
			}
		} catch (IOException e) {
		} catch (InterruptedException e) {
		} catch (Exception e) {
		} finally {
			System.out.println(sdf.format(new Date()) + "一台客户端下线了!");
			nameList.remove(name);
			if (playerID != -1) {
				room[roomNum].playerLeave(playerID);
			}
			if (room[roomNum].clear()) {
				room[roomNum] = new Room();
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
