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
 * ��Ϸ�ķ�������,���ڽ��տͻ��˵���Ϣ�ͷ��Ϳ�����Ϸ����Ϸ״̬
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
			// ���������ն˿�Ϊ3000
			serverSocket = new ServerSocket(3000);
			// ��ʼ��ÿ������
			for (int i = 0; i < room.length; i++) {
				room[i] = new Room();
				// execut.execute(room[i]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		Socket clientSockt = null;
		while (true) {
			try {
				System.out.println(sdf.format(new Date()) + "����IP��ַΪ:" + ServerUtil.getLocalInetAddress());
				System.out.println(sdf.format(new Date()) + "�����˿ں�Ϊ:3000");
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
				clientIsr = new InputStreamReader(clientIs, "utf-8");
				clientBr = new BufferedReader(clientIsr);
				clientOs = clientSocket.getOutputStream();
				clientOsw = new OutputStreamWriter(clientOs, "utf-8");
				clientPw = new PrintWriter(clientOsw, true);

				name = clientBr.readLine();
				// �ж������Ƿ�Ϸ������ظ�,���ؿͻ��˶�Ӧ����Ϣ
				while (name == null || nameList.contains(name) || name.equals("")) {
					clientPw.println("N");
					name = clientBr.readLine();
				}
				clientPw.println("Y");
				nameList.add(name);
				// ��ѭ��Ϊ����������ѭ��
				while (true) {
					// �ж��Ǽ��뷿�仹�Ǵ����·��� ��ѭ��Ϊ����ѭ��
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
							System.out.println(sdf.format(new Date()) + name + "�����˵�" + (roomNum + 1) + "����Ϸ");
							break;
						} else {
							roomNum = 0;
							while (room[roomNum].getpNum() != 0) {
								roomNum++;
							}
							clientPw.println(roomNum);
							playerID = room[roomNum].addPlayer(name, clientBr, clientPw);
							System.out.println(sdf.format(new Date()) + name + "�����˵�" + (roomNum + 1) + "����Ϸ");
							break;
						}
					}
					// ����ȴ�״̬,������յ�׼�������޷�ȡ��׼��״̬ ��ѭ��Ϊ����ѭ��,��׼����������
					while (true) {
						// ׼���׶ε�ѭ��
						boolean isReady = false;
						while (!isReady) {
							ready = clientBr.readLine();
							System.out.println(sdf.format(new Date()) + "ready=" + ready);
							// ����û��ڴ�ʱ�˳����˳�׼��ѭ��
							if (ready.equals("EXIT")) {
								room[roomNum].playerLeave(playerID);
								playerID = -1;
								clientPw.println("EXIT");
								System.out.println(sdf.format(new Date()) + name + "�˳���" + "��" + (roomNum + 1) + "��");
								break;
							}
							System.out.println(sdf.format(new Date()) + "��" + (roomNum + 1) + "����" + name + "׼����!");

							room[roomNum].ready(playerID);
							isReady = true;
							// ʵʱ������ǰ�����׼�������͵�ǰ��ҵ�׼��״̬
							while (room[roomNum].getReady() < 3) {
								clientPw.println("ALIVE");
								Thread.sleep(100);
								// ������ֿͻ���ȡ��׼�����˳�׼��״̬����ѭ��
								if (clientBr.readLine().equals("NOREADY")) {
									isReady = false;
									room[roomNum].cancelReady(playerID);
									System.out.println(
											sdf.format(new Date()) + "��" + (roomNum + 1) + "����" + name + "ȡ��׼����!");
									break;
								}
							}
						}

						// ȫ����׼��,��ʼ��Ϸ ��ѭ��Ϊ��Ϸѭ��,�ӷ��Ƶ���Ϸ����
						if (playerID != -1) {
							clientPw.println("START"); // ���Ϳ�ʼ�ź�

							while (true) {
								// ����
								clientPw.println("GIVECARDS"); // ���ͷ����ź�,�ͻ��˿�ʼ���տ���
								for (int i = 0; i < 17; i++) {
									room[roomNum].giveCard(playerID);
									try {
										Thread.sleep(200);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}

								// �ȴ�������ҽ�����ȫ��������
								clientBr.readLine();
								room[roomNum].over();
								while (room[roomNum].getOver() < 3) {
									Thread.sleep(200);
								}

								// �������ŵ�����
								room[roomNum].giveLordCards(playerID);
								// �ȴ���λ��ҽ�����ȫ���ĵ����Ʋ�����ʾ��Ϻ��ٿ�ʼ����������
								clientBr.readLine();
								room[roomNum].over();
								while (room[roomNum].getOver() < 6) {
									Thread.sleep(200);
								}
								// ����һ�ֵ���ϴ��
								room[roomNum].newTotal();
								// ���һ���˿�ʼ�е���
								synchronized (room[roomNum]) {
									if (room[roomNum].getRobLord() == 0) {// �ж������Ƿ����߳��ѽ���
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
								// δ������̵߳ȴ���Ϸ����
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
						// ��Ϸ����,�ж��Ǽ��������˳�����,���playerIDΪ-1��ֱ���˳�����ѭ��
						if (playerID == -1) {
							break;
						} else {
							Thread.sleep(600);
							room[roomNum].setOver(0);
							room[roomNum].setReady(0);
							room[roomNum].setRobLord(0);
							room[roomNum].setPutCards(true);
							System.out.println(sdf.format(new Date()) + "��" + roomNum + "������Ϸ������!");
							tmpMsg = clientBr.readLine();
							if (tmpMsg.equals("CONTINUE")) {
								System.out.println(sdf.format(new Date()) + playerID + "������Ϸ!");
								continue;
							} else {
								clientPw.println("EXIT");
								room[roomNum].playerLeave(playerID);
								System.out.println(sdf.format(new Date()) + name + "�˳���" + "��" + roomNum + "��");
								break;
							}
						}
					}
				}
			} catch (IOException e) {
			} catch (InterruptedException e) {
			} catch (Exception e) {
			} finally {
				System.out.println(sdf.format(new Date()) + "һ̨�ͻ���������!");
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
}
