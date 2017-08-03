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
	private int pNum = 0, ready = 0, over = 0;
	private int robLord = 0;
	private int[] totalCardsIndex = new int[54];
	private boolean putCards = true;

	// 构造房间时顺便将牌洗了
	public Room() {
		for (int i = 0; i < totalCardsIndex.length; i++) {
			totalCardsIndex[i] = i;
		}
	}

	public int getpNum() {
		return pNum;
	}

	public void setpNum(int pNum) {
		this.pNum = pNum;
	}

	public int getReady() {
		return ready;
	}

	public void setReady(int ready) {
		this.ready = ready;
	}

	public int getOver() {
		return over;
	}

	public void setOver(int over) {
		this.over = over;
	}

	public int getRobLord() {
		return robLord;
	}

	public void setRobLord(int robLord) {
		this.robLord = robLord;
	}

	public boolean isPutCards() {
		return putCards;
	}

	public void setPutCards(boolean putCards) {
		this.putCards = putCards;
	}

	public boolean clear() {
		return p[0] == null && p[1] == null && p[2] == null;
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
	public synchronized int addPlayer(String name, BufferedReader br, PrintWriter pw) {
		int index = 0;
		while (p[index] != null) {
			index++;
		}
		p[index] = new Player(name, br, pw);
		p[index].getPw().println("ID");
		p[index].getPw().println(index);
		for (int i = 0; i < p.length; i++) {
			if (p[i] != null && i != index) {
				p[i].getPw().println("IN");
				p[i].getPw().println(name);
				p[i].getPw().println(index);
				p[i].getPw().println(p[index].getIsReady());
				p[index].getPw().println("IN");
				p[index].getPw().println(p[i].getName());
				p[index].getPw().println(i);
				p[index].getPw().println(p[i].getIsReady());
			}
		}
		pNum++;
		return index;
	}

	/**
	 * 初始化牌的下标库
	 */
	public void newTotal() {
		synchronized (this.totalCardsIndex) {
			totalCardsIndex = new int[54];
			for (int i = 0; i < totalCardsIndex.length; i++) {
				totalCardsIndex[i] = i;
			}
		}
	}

	/**
	 * 发地主牌给当前客户端
	 */
	public synchronized void giveLordCards(int id) {
		if (p[id] != null) {
			p[id].getPw().println(totalCardsIndex[0]);
			p[id].getPw().println(totalCardsIndex[1]);
			p[id].getPw().println(totalCardsIndex[2]);
		}
	}

	/**
	 * 发一张牌给当前玩家
	 */
	public void giveCard(int id) {
		synchronized (this.totalCardsIndex) {
			int tmp = (int) (Math.random() * totalCardsIndex.length);
			p[id].getPw().println(totalCardsIndex[tmp]);
			totalCardsIndex[tmp] = totalCardsIndex[totalCardsIndex.length - 1];
			totalCardsIndex = Arrays.copyOf(totalCardsIndex, totalCardsIndex.length - 1);
		}
	}

	/**
	 * 玩家离开当前房间
	 * 
	 * @param playerID
	 *            离开的玩家的房间内ID
	 */
	public void playerLeave(int playerID) {
		synchronized (this.p) {
			for (int i = 0; i < p.length; i++) {
				if (i != playerID && p[i] != null) {
					p[i].getPw().println("OUT");
					p[i].getPw().println(playerID);
				}
			}
			p[playerID] = null;
			pNum--;
		}
	}

	/**
	 * 玩家准备
	 * 
	 * @param id
	 *            已准备的玩家的ID
	 */
	public void ready(int id) {
		synchronized (this.p) {
			ready++;
			p[id].setIsReady(1);
			for (int i = 0; i < p.length; i++) {
				if (i != id && p[i] != null) {
					p[i].getPw().println("READY");
					p[i].getPw().println(id);
				}
			}
		}
	}

	public void cancelReady(int id) {
		synchronized (this.p) {
			ready--;
			p[id].setIsReady(0);
			for (int i = 0; i < p.length; i++) {
				if (i != id && p[i] != null) {
					p[i].getPw().println("CANCELREADY");
					p[i].getPw().println(id);
				}
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
	public int robLord(int id) throws Exception {
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
					if ((!(next[(index + 1) % 3] || next[(index + 2) % 3])) && isRob[index % 3] == 1) {
						for (int i = 0; i < p.length; i++) {
							p[i].getPw().println("LORD");
							p[i].getPw().println(index % 3);
						}
						return index % 3;
					}
					// 发出抢地主信号,接收客户端反馈的抢地主信息 */
					p[index % 3].getPw().println("TOROB");
					for (int i = 0; i < p.length; i++) {
						if (i != index % 3) {
							p[i].getPw().println("WHOROB");
							p[i].getPw().println(index % 3);
						}
					}
					msg = p[index % 3].getBr().readLine();
					if (msg.equals("Y")) {
						isRob[index % 3]++;
						for (int i = 0; i < p.length; i++) {
							if (i != index % 3) {
								p[i].getPw().println("Y");
							}
						}
					} else {
						for (int i = 0; i < p.length; i++) {
							if (i != index % 3) {
								p[i].getPw().println("N");
							}
						}
						next[index % 3] = false;
					}
					// 判断是否已经抢了两次地主,若为两次则分配给该玩家地主
					if (isRob[index % 3] == 2
							|| ((!(next[(index + 1) % 3] || next[(index + 2) % 3])) && isRob[index % 3] == 1)) {
						for (int i = 0; i < p.length; i++) {
							p[i].getPw().println("LORD");
							p[i].getPw().println(index % 3);
						}
						return index % 3;
					}
				} // 掉线玩家直接跳过
				else if (p[index % 3] == null && next[index % 3]) {
					for (int i = 0; i < p.length; i++) {
						if (i != index % 3 && p[i] != null) {
							p[i].getPw().println("WHOROB");
							p[i].getPw().println(index % 3);
							p[i].getPw().println("N");
						}
					}
					next[index % 3] = false;
				}
				index++;
			} catch (IOException e) {
				playerLeave(index % 3);
				index++;
			}
		}
		return -1;
	}

	/**
	 * 出牌过程
	 * 
	 * @param id
	 *            当前出牌人ID
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
					p[i].getPw().println("WHODO");
					p[i].getPw().println(index % 3);
				}
				msg = p[index % 3].getBr().readLine();
				// 如果客户端发来的信息是SHOW则为要出牌,将出的牌转发给别的客户端
				if (msg.equals("SHOW")) {
					cardsNum = Integer.valueOf(p[index % 3].getBr().readLine());
					cardType = Integer.valueOf(p[index % 3].getBr().readLine());
					for (int i = 0; i < cardsNum; i++) {
						cardIndex = Arrays.copyOf(cardIndex, cardIndex.length + 1);
						cardIndex[cardIndex.length - 1] = Integer.valueOf(p[index % 3].getBr().readLine());
					}
					if (cardIndex.length != 0 && cardIndex[cardIndex.length - 1] == -1) {
						b = false;
					}
					for (int i = 0; i < p.length; i++) {
						if (i != index % 3) {
							p[i].getPw().println("SHOW");
							p[i].getPw().println(cardsNum);
							p[i].getPw().println(cardType);
							for (int j = 0; j < cardsNum; j++) {
								p[i].getPw().println(cardIndex[j]);
							}
						}
					}
				} // 如果接收到PASS信息则为该玩家不出牌,就将上家的出牌数和出牌类型传递给其他客户端
				else if (msg.equals("PASS")) {
					cardsNum = Integer.valueOf(p[index % 3].getBr().readLine());
					cardType = Integer.valueOf(p[index % 3].getBr().readLine());
					for (int i = 0; i < p.length; i++) {
						if (i != index % 3) {
							p[i].getPw().println("PASS");
							p[i].getPw().println(cardsNum);
							p[i].getPw().println(cardType);
						}
					}
				}
				index++;
			} catch (Exception e) {
				playerLeave(index % 3);
				System.out.println(GameServer.sdf.format(new Date()) + "一台客户端在打牌时下线了!");
				if (pNum == 0) {
					putCards = false;
					break;
				}
				index++;
			}
		}
		for (int i = 0; i < p.length; i++) {
			if (p[i] != null) {
				p[i].setIsReady(0);
			}
		}
		putCards = false;
	}

	@Override
	public void run() {
		try {
			while (true) {
				System.out.println("room线程开始了");
				System.out.println(Thread.currentThread().getState());
				Thread.currentThread().wait();
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
