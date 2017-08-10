package com.string.pokergame.client.handler;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

/**
 * 服务器信息处理线程,随时接受服务器传来的信息以处理房间状态和游戏状态
 * 
 * @author String
 *
 */
public class ServerHandler extends Handler implements Runnable {
	private static final long serialVersionUID = 1L;

	public static final int SHOW_TIME = 30;
	public static final int ROB_TIME = 15;
	
	private Socket server = null;
	private ExecutorService exec = Executors.newFixedThreadPool(50);
	private int[] tipsIndex = new int[0];
	
	public ServerHandler(Socket server){
		this.server=server;
	}
	/**
	 * 初始化游戏内的属性数据
	 */
	public void newData() {
		toPass = false;
		times = 0;
		robIsOver = false;
		isWin = false;
		if (pl != null) {
			pl.isWin = false;
			pl.isLord = false;
			pl.isRob = 0;
			pl.isPass = true;
			pl.isDo = false;
			pl.cardsNum = 0;
		}
		if (pr != null) {
			pr.isWin = false;
			pr.isLord = false;
			pr.isRob = 0;
			pr.isPass = true;
			pr.isDo = false;
			pr.cardsNum = 0;
		}
		isLord = false;
		isRob = 0;
		lordCards = new Card[0];
		nowCardType = 0;
		nowLen = 0;
		nowCards = null;
		toPass = false;
	}
	
	/**
	 * 拿牌
	 * 
	 * @throws Exception
	 */
	public void getCards() throws Exception {
		myCards = new Card[0];
		int index = 0;
		for (int i = 0; i < 17; i++) {
			// 从客户端接收牌的总卡组下标
			index = Integer.valueOf(serverReader.readLine());
			myCards = Arrays.copyOf(myCards, myCards.length + 1);
			myCards[i] = new Card(250 + i * 15, 350);
			myCards[i].image = totalCards[index].image;
			myCards[i].num = totalCards[index].num;
			noteCards[myCards[i].num]--;
			myCards[i].cardIndex = index;
			pl.cardsNum++;
			pr.cardsNum++;
		}
		// 向服务器发送接牌结束信号
		serverPrinter.println("OVER");
		// 接收地主牌
		for (int i = 0; i < 3; i++) {
			lordCards = Arrays.copyOf(lordCards, lordCards.length + 1);
			lordCards[i] = new Card(280 + 80 * i, 30);
			index = Integer.valueOf(serverReader.readLine());
			lordCards[i].image = totalCards[index].image;
			lordCards[i].num = totalCards[index].num;
			lordCards[i].cardIndex = index;
		}
	}

	/**
	 * 递归方法找出大于上家的牌
	 * 
	 * @param index
	 *            当前的tipsIndex的下标
	 * @param myIndex
	 *            当前的myCards的下标
	 */
	public void findTips(int index, int myIndex) {
		Card[] tip = new Card[0];
		int cardType;
		if (index > myCards.length - 1 || myIndex > myCards.length - 1) {
			return;
		}
		if (nowLen == 0) {
			return;
		}
		if (nowLen >= 4 && index > nowLen - 1) {
			return;
		}
		if (nowLen < 4 && index > 3) {
			return;
		}
		if (nowCards == null || nowCards.length == 0) {
			return;
		}
		for (int i = myCards.length - 1; i >= myIndex; i--) {
			tipsIndex = Arrays.copyOf(tipsIndex, index + 1);
			tipsIndex[index] = i;
			tip = new Card[0];
			for (int j = 0; j < tipsIndex.length; j++) {
				tip = Arrays.copyOf(tip, tip.length + 1);
				tip[tip.length - 1] = new Card();
				tip[tip.length - 1].num = myCards[tipsIndex[j]].num;
			}
			cardType = CardsUtil.checkCards(tip);
			if (cardType == nowCardType && tip.length == nowLen && tip[0].num > nowCards[0].num) {
				if (!tipCard.contains(tip)) {
					tips.add(tipsIndex);
					tipCard.add(tip);
				}
			} else if ((cardType == CardsUtil.BOMB && nowCardType != CardsUtil.BOMB && nowCardType != CardsUtil.KINGBOMB) || cardType == CardsUtil.KINGBOMB) {
				if (!tipCard.contains(tip)) {
					tips.add(tipsIndex);
					tipCard.add(tip);
				}
			}
			findTips(index + 1, i + 1);
		}
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				Thread.sleep(100);// 防止产生死循环
				// 等待和房间内状态的服务器信息分类处理
				if (state == WAIT || state == IN_ROOM || state == OVER || state == GAME) {
					tmpMsg = serverReader.readLine();
					if (tmpMsg.equals("ALIVE") && state == WAIT) {
						serverPrinter.println("ALIVE");
					} else {
						System.out.println("tmpMsg=" + tmpMsg);
					}
					if (tmpMsg.equals("ID")) {
						myId = Integer.valueOf(serverReader.readLine());
					}
					if (tmpMsg.equals("IN")) {
						String name = serverReader.readLine();
						int id = Integer.valueOf(serverReader.readLine());
						int isReady = Integer.valueOf(serverReader.readLine());
						if (pl == null) {
							pl = new Player(name, id, isReady);
						} else {
							pr = new Player(name, id, isReady);
						}

					}
					// 接收玩家退出房间的信号
					if (tmpMsg.equals("OUT")) {
						int id = Integer.valueOf(serverReader.readLine());
						if (pl != null && pl.playerID == id) {
							pl = null;
						} else if (pr != null && pr.playerID == id) {
							pr = null;
						}

					}
					// 接收玩家准备信号
					if (tmpMsg.equals("READY")) {
						int id = Integer.valueOf(serverReader.readLine());
						if (pl != null && pl.playerID == id) {
							pl.ready = 1;
						} else if (pr != null && pr.playerID == id) {
							pr.ready = 1;
						}

					}
					// 接收取消准备信号
					if (tmpMsg.equals("CANCELREADY")) {
						int id = Integer.valueOf(serverReader.readLine());
						if (pl != null && pl.playerID == id) {
							pl.ready = 0;
						} else if (pr != null && pr.playerID == id) {
							pr.ready = 0;
						}

					}
					// 接收游戏开始信号
					if (tmpMsg.equals("START")) {
						state = GAME;
					}

					// 接受服务器发的牌
					if (tmpMsg.equals("GIVECARDS")) {
						newData();
						point = 3;
						getCards();
						CardsUtil.cardSort(myCards);
						serverPrinter.println("OVER");
					}
					
					// 接收自己抢地主信号
					if (tmpMsg.equals("TOROB")) {
						if (!toPass) {
							toRob = true;
							timeTik3.setMax(ROB_TIME);
							exec.execute(timeTik3);
						} else {
							toRob = true;
							autoPass();
						}
					}
					
					// 接收谁在抢地主的信号
					if (tmpMsg.equals("WHOROB")) {
						int id = Integer.valueOf(serverReader.readLine());
						if (pl != null && pl.playerID == id) {
							pl.isDo = true;
							timeTik1.setMax(ROB_TIME);
							exec.execute(timeTik1);
						} else if (pr != null && pr.playerID == id) {
							pr.isDo = true;
							timeTik2.setMax(ROB_TIME);
							exec.execute(timeTik2);
						}

						tmpMsg = serverReader.readLine();
						if (tmpMsg.equals("Y")) {
							if (pl != null && pl.playerID == id) {
								pl.isRob = 2;
								pl.isDo = false;
								timeTik1.stopTik();
							} else if (pr != null && pr.playerID == id) {
								pr.isRob = 2;
								pr.isDo = false;
								timeTik2.stopTik();
							}
							point *= 2;
						} else {
							if (pl != null && pl.playerID == id) {
								pl.isRob = 1;
								pl.isDo = false;
								timeTik1.stopTik();
							} else if (pr != null && pr.playerID == id) {
								pr.isRob = 1;
								pr.isDo = false;
								timeTik2.stopTik();
							}
						}

					}
					// 接收已选出地主的信号
					if (tmpMsg.equals("LORD")) {
						int id = Integer.valueOf(serverReader.readLine());
						if (id == myId) {
							isLord = true;
							for (int i = 0; i < lordCards.length; i++) {
								myCards = Arrays.copyOf(myCards, myCards.length + 1);
								myCards[myCards.length - 1] = new Card(250 + (myCards.length - 1) * 15, 350);
								myCards[myCards.length - 1].image = lordCards[i].image;
								myCards[myCards.length - 1].num = lordCards[i].num;
								noteCards[lordCards[i].num]--;
								myCards[myCards.length - 1].cardIndex = lordCards[i].cardIndex;
								myCards[myCards.length - 1].y = 330;
							}
							CardsUtil.cardSort(myCards);
						} else if (pl != null && pl.playerID == id) {
							pl.isLord = true;
							pl.cardsNum += 3;
						} else if (pr != null && pr.playerID == id) {
							pr.isLord = true;
							pr.cardsNum += 3;
						}
						robIsOver = true;
					}
					// 接收谁在出牌的信号
					if (tmpMsg.equals("WHODO")) {
						int id = Integer.valueOf(serverReader.readLine());
						if (id == myId) {
							tips.clear();
							tipCard.clear();
							findTips(0, 0);
							if (!toPass) {
								if (pl.isPass && pr.isPass) {
									pl.isPass = false;
									pr.isPass = false;
									showPass = false;
								} else {
									showPass = true;
								}
								timeTik3.setMax(SHOW_TIME);
								exec.execute(timeTik3);
								toDo = true;
								isPass = false;
								showCards = new Card[0];
								times++;
							} else {
								if (pl.isPass && pr.isPass) {
									pl.isPass = false;
									pr.isPass = false;
									showPass = false;
								} else {
									showPass = true;
								}
								toDo = true;
								showCards = new Card[0];
								times++;
								autoPass();
							}
						} else if (pl != null && pl.playerID == id) {
							pl.isDo = true;
							pl.isPass = false;
							if (isPass && pr.isPass || times == 0) {
								isPass = false;
								pr.isPass = false;
							}
							timeTik1.setMax(SHOW_TIME);
							exec.execute(timeTik1);
							// 接收左家出的卡牌
							pl.cards = new Card[0];

							String msg = serverReader.readLine();
							if (msg.equals("SHOW")) {
								nowLen = Integer.valueOf(serverReader.readLine());
								nowCardType = Integer.valueOf(serverReader.readLine());
								pl.isPass = false;
								for (int i = 0; i < nowLen; i++) {
									int index = Integer.valueOf(serverReader.readLine());
									if (index == -1) {
										pl.isWin = true;
										score();
										state = OVER;
										pl.ready = 0;
										pr.ready = 0;
										break;
									}
									pl.cardsNum--;
									pl.cards = Arrays.copyOf(pl.cards, pl.cards.length + 1);
									pl.cards[i] = new Card(100 + i * 15, 120);
									pl.cards[i].image = totalCards[index].image;
									pl.cards[i].num = totalCards[index].num;
									noteCards[pl.cards[i].num]--;
								}
								nowCards = pl.cards;
								pl.isDo = false;
							} else {
								nowLen = Integer.valueOf(serverReader.readLine());
								nowCardType = Integer.valueOf(serverReader.readLine());
								pl.isPass = true;
								pl.isDo = false;
							}
							timeTik1.stopTik();
						} else if (pr != null && pr.playerID == id) {
							pr.isDo = true;
							pr.isPass = false;
							if (isPass && pl.isPass || times == 0) {
								isPass = false;
								pl.isPass = false;
							}
							timeTik2.setMax(SHOW_TIME);
							exec.execute(timeTik2);
							// 接收右家出的卡牌
							pr.cards = new Card[0];

							String msg = serverReader.readLine();
							if (msg.equals("SHOW")) {
								nowLen = Integer.valueOf(serverReader.readLine());
								nowCardType = Integer.valueOf(serverReader.readLine());
								pr.isPass = false;
								for (int i = 0; i < nowLen; i++) {
									int index = Integer.valueOf(serverReader.readLine());
									if (index == -1) {
										pr.isWin = true;
										score();
										state = OVER;
										pl.ready = 0;
										pr.ready = 0;
										break;
									}
									pr.cardsNum--;
									pr.cards = Arrays.copyOf(pr.cards, pr.cards.length + 1);
									pr.cards[i] = new Card(600 - (nowLen - i - 1) * 15, 120);
									pr.cards[i].image = totalCards[index].image;
									pr.cards[i].num = totalCards[index].num;
									noteCards[pr.cards[i].num]--;
								}
								nowCards = pr.cards;
								pr.isDo = false;
							} else {
								nowLen = Integer.valueOf(serverReader.readLine());
								nowCardType = Integer.valueOf(serverReader.readLine());
								pr.isPass = true;
								pr.isDo = false;
							}
							timeTik2.stopTik();
						}

					}
				}
			}
		} catch (Exception e) {
			// 如果出错跳出线程,则显示失去连接,并关闭客户端
			e.printStackTrace();
			state = START;
			newData();
			pl = null;
			pr = null;
			JOptionPane.showMessageDialog(null, "失去连接!", "服务器连接失败", JOptionPane.ERROR_MESSAGE);
		} finally {
			if (server != null) {
				try {
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
