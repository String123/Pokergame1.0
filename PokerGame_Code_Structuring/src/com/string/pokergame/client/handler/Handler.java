package com.string.pokergame.client.handler;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;
/**
 * 封装斗地主游戏中的全局属性和方法,包括各个handler的处理牌的各种方法
 *
 */
public class Handler extends JPanel {
	private static final long serialVersionUID = 1L;

	public static final int START = 0;
	public static final int WAIT = 1;
	public static final int SELECT_ROOM = 2;
	public static final int IN_ROOM = 3;
	public static final int GAME = 4;
	public static final int OVER = 5;

	public static PrintWriter serverPrinter = null;
	public static BufferedReader serverReader = null;

	public static int state = START;
	public static boolean toDo = false, toRob = false, isLord = false;
	public static boolean isPass = false, isWin = false, key = false;
	public static int isRob = 0, times = 0, errTips = 0;
	public static int score = 0, point = 0, tipsId = 0;
	public static boolean robIsOver = false, showPass = false, toPass = false;

	public static Card[] totalCards = new Card[54];
	public static Card[] lordCards = new Card[0];
	public static Card[] nowCards = null; // 当前在桌面上的上家的牌
	public static Card[] myCards = new Card[0];
	public static Card[] showCards = new Card[0];
	public static int[] noteCards = new int[20];
	public static Button startB, showB, putDownB, joinB, createB, exitB, readyB, passB;
	public static Button robB, noRobB, noReadyB, goOnB, exitRoomB;
	public static Button autoB, cancelAutoB;
	public static TimerTik timeTik1, timeTik2, timeTik3;

	public static int roomNum = 0, myId = -1;
	public static int cardType = 0;
	public static String name, tmpMsg;

	public static List<int[]> tips = new ArrayList<int[]>();
	public static List<Card[]> tipCard = new ArrayList<Card[]>();
	/**
	 * 左边的玩家对象
	 */
	public static Player pl = null;
	/**
	 * 右边的玩家对象
	 */
	public static Player pr = null;
	public static int nowLen = 0;
	public static int nowCardType = 0;
	
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
	 * 将选好的牌发送出去
	 */
	public void putMyCards() {
		int len;
		// 将要打出的牌从手牌中删除
		for (int i = 0; i < myCards.length; i++) {
			if (myCards[i].y == 330) {
				CardsUtil.swapCards(myCards[i], myCards[myCards.length - 1]);
				myCards = Arrays.copyOf(myCards, myCards.length - 1);
				i--;
			}
		}
		// 排序手中的卡牌
		CardsUtil.cardSort(myCards);
		len = showCards.length;
		if (myCards.length == 0) {
			len++;
			showCards = Arrays.copyOf(showCards, showCards.length + 1);
			showCards[showCards.length - 1] = new Card();
			showCards[showCards.length - 1].cardIndex = -1;
			state = OVER;
			isWin = true;
			score();
			pl.ready = 0;
			pr.ready = 0;
		}
		// 发送要出的卡牌的总卡组下标给服务器
		serverPrinter.println("SHOW");
		serverPrinter.println(len);
		serverPrinter.println(cardType);
		for (int i = 0; i < showCards.length; i++) {
			serverPrinter.println(showCards[i].cardIndex);
		}
		// 将要出的卡组的x,y坐标算出来
		int x = 400 - showCards.length * 15 / 2 - 50;
		int y = 200;
		for (int i = 0; i < showCards.length; i++) {
			showCards[i].x = x + 15 * i;
			showCards[i].y = y;
		}
		timeTik3.stopTik();
		nowCardType = 0;
		nowCards = null;
		nowLen = 0;
		errTips = 0;
		toDo = false;
	}

	/**
	 * 自动过
	 */
	public synchronized void autoPass() {
		if (toDo && showPass) {
			serverPrinter.println("PASS");
			serverPrinter.println(nowLen);
			serverPrinter.println(nowCardType);
			toDo = false;
			isPass = true;
			timeTik3.stopTik();
			errTips = 0;
			clearChoose();
		} else if (toRob) {
			serverPrinter.println("N");
			isRob = 1;
			toRob = false;
			timeTik3.stopTik();
		} else if (toDo && !showPass) {
			clearChoose();
			myCards[myCards.length - 1].y -= 20;
			chooseCard();
			if (showCards.length != 0) {
				cardType = CardsUtil.checkCards(showCards);
			} else {
				cardType = 0;
			}
			putMyCards();
		}
	}

	/**
	 * 提出已选择的牌
	 */
	public void chooseCard() {
		showCards = new Card[0];
		for (int i = 0; i < myCards.length; i++) {
			if (myCards[i].y == 330) {
				showCards = Arrays.copyOf(showCards, showCards.length + 1);
				showCards[showCards.length - 1] = new Card();
				showCards[showCards.length - 1].image = myCards[i].image;
				showCards[showCards.length - 1].num = myCards[i].num;
				showCards[showCards.length - 1].cardIndex = myCards[i].cardIndex;
			}
		}
	}

	
	/**
	 * 将选择的牌缩回
	 */
	public void clearChoose() {
		for (int i = 0; i < myCards.length; i++) {
			if (myCards[i].y == 330) {
				myCards[i].y = 350;
			}
		}
	}

	/**
	 * 游戏结束后计算玩家分数
	 */
	public void score() {
		if (pl.isWin) {
			if (isLord) {
				pl.score += point / 2;
				pr.score += point / 2;
				score -= point;
			} else if (pl.isLord) {
				pl.score += point;
				pr.score -= point / 2;
				score -= point / 2;
			} else {
				pl.score += point / 2;
				pr.score -= point;
				score += point / 2;
			}
		}
		if (pr.isWin) {
			if (isLord) {
				pl.score += point / 2;
				pr.score += point / 2;
				score -= point;
			} else if (pr.isLord) {
				pl.score -= point / 2;
				pr.score += point;
				score -= point / 2;
			} else {
				pl.score -= point;
				pr.score += point / 2;
				score += point / 2;
			}
		}
		if (isWin) {
			if (isLord) {
				pl.score -= point / 2;
				pr.score -= point / 2;
				score += point;
			} else if (pr != null && pr.isLord) {
				pl.score += point / 2;
				pr.score -= point;
				score += point / 2;
			} else {
				pl.score -= point;
				pr.score += point / 2;
				score += point / 2;
			}
		}
	}

}
