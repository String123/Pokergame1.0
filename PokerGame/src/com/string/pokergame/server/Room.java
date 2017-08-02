package com.string.pokergame.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;

/**
 * 封装一个房间内的属性和方法,一个房间一副牌,三个客户端的IO流等属性 发牌,出牌,抢地主,准备等方法
 * 
 */
public class Room implements Runnable {
	Player[] p = new Player[] { null, null, null };
	int pNum = 0, ready = 0, over = 0;
	int robLord = 0;
	int[] totalCardsIndex = new int[54];
	boolean putCards = true;

	// 构造房间时顺便将牌洗了
	public Room() {
		for (int i = 0; i < totalCardsIndex.length; i++) {
			totalCardsIndex[i] = i;
		}
	}

	public boolean clear(){
		return p[0]==null&&p[1]==null&&p[2]==null;
	}
	
	public void over() {
		over++;
	}

	/**
	 * 添加进入房间的客户端的信息到对应的玩家对象
	 * 
	 * @param name
	 *            玩家名字
	 * @param br
	 *            客户端输入流
	 * @param pw
	 *            客户端输出流
	 * @return 玩家在客户端的数组下标
	 */
	public synchronized int addPlayer(String name, BufferedReader br,
			PrintWriter pw) {
		int index = 0;
		while (p[index] != null) {
			index++;
		}
		p[index] = new Player(name, br, pw);
		p[index].pw.println("ID");
		p[index].pw.println(index);
		for (int i = 0; i < p.length; i++) {
			if (p[i] != null && i != index) {
				p[i].pw.println("IN");
				p[i].pw.println(name);
				p[i].pw.println(index);
				p[i].pw.println(p[index].isReady);
				p[index].pw.println("IN");
				p[index].pw.println(p[i].name);
				p[index].pw.println(i);
				p[index].pw.println(p[i].isReady);
			}
		}
		pNum++;
		return index;
	}

	/**
	 * 初始化牌的下标库
	 */
	public void newTotal() {
		totalCardsIndex = new int[54];
		for (int i = 0; i < totalCardsIndex.length; i++) {
			totalCardsIndex[i] = i;
		}
	}

	/**
	 * 发地主牌给当前客户端
	 */
	public synchronized void giveLordCards(int id) {
		if (p[id] != null) {
			p[id].pw.println(totalCardsIndex[0]);
			p[id].pw.println(totalCardsIndex[1]);
			p[id].pw.println(totalCardsIndex[2]);
		}
	}

	/**
	 * 发一张牌给当前玩家
	 */
	public synchronized void giveCard(int id) {
		int tmp = (int) (Math.random() * totalCardsIndex.length);
		p[id].pw.println(totalCardsIndex[tmp]);
		totalCardsIndex[tmp] = totalCardsIndex[totalCardsIndex.length - 1];
		totalCardsIndex = Arrays.copyOf(totalCardsIndex,
				totalCardsIndex.length - 1);
	}

	/**
	 * 玩家离开当前房间
	 * 
	 * @param playerID
	 *            离开的玩家的房间内ID
	 */
	public synchronized void playerLeave(int playerID) {
		for (int i = 0; i < p.length; i++) {
			if (i != playerID && p[i] != null) {
				p[i].pw.println("OUT");
				p[i].pw.println(playerID);
			}
		}
		p[playerID] = null;
		pNum--;
	}

	/**
	 * 玩家准备
	 * 
	 * @param id
	 *            已准备的玩家的ID
	 */
	public void ready(int id) {
		ready++;
		p[id].isReady = 1;
		for (int i = 0; i < p.length; i++) {
			if (i != id && p[i] != null) {
				p[i].pw.println("READY");
				p[i].pw.println(id);
			}
		}
	}

	public void cancelReady(int id) {
		ready--;
		p[id].isReady = 0;
		for (int i = 0; i < p.length; i++) {
			if (i != id && p[i] != null) {
				p[i].pw.println("CANCELREADY");
				p[i].pw.println(id);
			}
		}
	}

	/**
	 * 抢地主过程
	 * 
	 * @param id
	 *            当前抢地主玩家
	 * @return 抢到地主的玩家,如果返回-1则没人抢地主
	 */
	public int robLord(int id) throws Exception{
		boolean[] next = new boolean[] { true, true, true };
		int[] isRob = new int[] { 0, 0, 0 };
		int index = id;
		int times = 0;
		String msg;
		while (times < 6) {
			try {
				times++;
				if (next[index % 3] && p[index % 3] != null) {
					// 判断其他人是否都已经放弃抢地主,若是则直接分配给该玩家地主
					if ((!(next[(index + 1) % 3] || next[(index + 2) % 3]))
							&& isRob[index % 3] == 1) {
						for (int i = 0; i < p.length; i++) {
							p[i].pw.println("LORD");
							p[i].pw.println(index % 3);
						}
						return index % 3;
					}
					// 发出抢地主信号,接收客户端反馈的抢地主信息 */
					p[index % 3].pw.println("TOROB");
					for (int i = 0; i < p.length; i++) {
						if (i != index % 3) {
							p[i].pw.println("WHOROB");
							p[i].pw.println(index % 3);
						}
					}
					msg = p[index % 3].br.readLine();
					if (msg.equals("Y")) {
						isRob[index % 3]++;
						for (int i = 0; i < p.length; i++) {
							if (i != index % 3) {
								p[i].pw.println("Y");
							}
						}
					} else {
						for (int i = 0; i < p.length; i++) {
							if (i != index % 3) {
								p[i].pw.println("N");
							}
						}
						next[index % 3] = false;
					}
					// 判断是否已经抢了两次地主,若为两次则分配给该玩家地主
					if (isRob[index % 3] == 2
							|| ((!(next[(index + 1) % 3] || next[(index + 2) % 3])) && isRob[index % 3] == 1)) {
						for (int i = 0; i < p.length; i++) {
							p[i].pw.println("LORD");
							p[i].pw.println(index % 3);
						}
						return index % 3;
					}
				}// 掉线玩家直接跳过
				else if (p[index % 3] == null && next[index % 3]) {
					for (int i = 0; i < p.length; i++) {
						if (i != index % 3 && p[i] != null) {
							p[i].pw.println("WHOROB");
							p[i].pw.println(index % 3);
							p[i].pw.println("N");
						}
					}
					next[index % 3] = false;
				}
				index++;
			} catch (IOException e) {
				e.printStackTrace();
				playerLeave(index % 3);
				index++;
			}
		}
		return -1;
	}

	/**
	 * 出牌过程
	 * 
	 * @param pNum
	 *            当前出牌人ID
	 * @return 返回布尔型,判断是否结束
	 */
	public void putCards(int id) {
		boolean b = true;
		String msg;
		int cardsNum = 0, cardType = 0, index = id;
		int[] cardIndex = null;
		while (b) {
			try {
				cardIndex = new int[0];
				cardType = 0;
				cardsNum = 0;
				// 发送"哪个客户端出牌"信息给所有客户端
				for (int i = 0; i < p.length; i++) {
					p[i].pw.println("WHODO");
					p[i].pw.println(index % 3);
				}
				msg = p[index % 3].br.readLine();
				// 如果客户端发来的信息是SHOW则为要出牌,将出的牌转发给别的客户端
				if (msg.equals("SHOW")) {
					cardsNum = Integer.valueOf(p[index % 3].br.readLine());
					cardType = Integer.valueOf(p[index % 3].br.readLine());
					for (int i = 0; i < cardsNum; i++) {
						cardIndex = Arrays.copyOf(cardIndex,
								cardIndex.length + 1);
						cardIndex[cardIndex.length - 1] = Integer
								.valueOf(p[index % 3].br.readLine());
					}
					if (cardIndex.length != 0
							&& cardIndex[cardIndex.length - 1] == -1) {
						b = false;
					}
					for (int i = 0; i < p.length; i++) {
						if (i != index % 3) {
							p[i].pw.println("SHOW");
							p[i].pw.println(cardsNum);
							p[i].pw.println(cardType);
							for (int j = 0; j < cardsNum; j++) {
								p[i].pw.println(cardIndex[j]);
							}
						}
					}
				}// 如果接收到PASS信息则为该玩家不出牌,就将上家的出牌数和出牌类型传递给其他客户端
				else if (msg.equals("PASS")) {
					cardsNum = Integer.valueOf(p[index % 3].br.readLine());
					cardType = Integer.valueOf(p[index % 3].br.readLine());
					for (int i = 0; i < p.length; i++) {
						if (i != index % 3) {
							p[i].pw.println("PASS");
							p[i].pw.println(cardsNum);
							p[i].pw.println(cardType);
						}
					}
				}
				index++;
			} catch (Exception e) {
				e.printStackTrace();
				playerLeave(index % 3);
				System.out.println(GameServer.sdf.format(new Date())
						+ "一台客户端下线了!");
				if (pNum == 0) {
					putCards = false;
					break;
				}
				index++;
			}
		}
		for (int i=0;i<p.length;i++){
			if (p[i]!=null){
				p[i].isReady=0;
			}
		}
		putCards = false;
	}

	@Override
	public void run() {
		try {
			while (true) {
				while (ready < 3) {
					Thread.sleep(500);
				}
				// TODO 把房间改为单独线程来执行游戏过程,防止以客户端线程运行游戏过程时掉线导致全部离开游戏
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
