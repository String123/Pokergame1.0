package com.string.pokergame.client;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * 三人斗地主游戏的客户端
 * 
 * @author String
 * 
 */
public class GameClient extends JPanel {
	private static final long serialVersionUID = 1L;

	public static final int WIDTH = 800;
	public static final int HEIGHT = 500;
	public static int width = 70, height = 100;
	public static final int SHOW_TIME = 30;
	public static final int ROB_TIME = 15;

	public static final int START = 0;
	public static final int WAIT = 1;
	public static final int SELECT_ROOM = 2;
	public static final int IN_ROOM = 3;
	public static final int GAME = 4;
	public static final int OVER = 5;

	public static final int BOMB = 6;
	public static final int KINGBOMB = 9;

	public static BufferedImage background;
	public static BufferedImage[] card = new BufferedImage[54];
	public static BufferedImage start;
	public static BufferedImage farmer;
	public static BufferedImage lord;
	public static BufferedImage login;
	public static BufferedImage title;
	public static BufferedImage button1;
	public static BufferedImage button2;
	public static BufferedImage show;
	public static BufferedImage tip;
	public static BufferedImage wait;
	public static BufferedImage join;
	public static BufferedImage create;
	public static BufferedImage readyP;
	public static BufferedImage ready;
	public static BufferedImage noReady;
	public static BufferedImage exit;
	public static BufferedImage exitRoom;
	public static BufferedImage cardBack;
	public static BufferedImage pass;
	public static BufferedImage rob;
	public static BufferedImage noRob;
	public static BufferedImage timer;
	public static BufferedImage goOn;
	public static BufferedImage lordCard;
	public static BufferedImage leave;
	public static BufferedImage tips1;
	public static BufferedImage tips2;
	public static BufferedImage tips3;
	public static BufferedImage auto;
	public static BufferedImage cancelAuto;
	public static BufferedImage noteCard;

	public static int state = START;
	public static boolean toDo = false, toRob = false, isLord = false;
	public static boolean isPass = false, isWin = false, key = false;
	public static int isRob = 0, times = 0, errTips = 0;
	public static int score = 0, point = 0, tipsId = 0;
	public static boolean robIsOver = false, showPass = false, toPass = false;
	static { // 加载背景图和文字图片资源
		try {
			noteCard = ImageIO.read(GameClient.class.getResource("jipaiqi.png"));
			auto = ImageIO.read(GameClient.class.getResource("tuoguan.png"));
			cancelAuto = ImageIO.read(GameClient.class.getResource("qxtg.png"));
			noReady = ImageIO.read(GameClient.class.getResource("cancelReady.png"));
			tips1 = ImageIO.read(GameClient.class.getResource("pxx.png"));
			tips2 = ImageIO.read(GameClient.class.getResource("pxcw.png"));
			tips3 = ImageIO.read(GameClient.class.getResource("mdp.png"));
			leave = ImageIO.read(GameClient.class.getResource("lixian.png"));
			goOn = ImageIO.read(GameClient.class.getResource("jixu.png"));
			lordCard = ImageIO.read(GameClient.class.getResource("dzp.png"));
			timer = ImageIO.read(GameClient.class.getResource("naozhong.png"));
			noRob = ImageIO.read(GameClient.class.getResource("bq.png"));
			rob = ImageIO.read(GameClient.class.getResource("qdz.png"));
			pass = ImageIO.read(GameClient.class.getResource("bc.png"));
			cardBack = ImageIO.read(GameClient.class.getResource("paishu.png"));
			exit = ImageIO.read(GameClient.class.getResource("likai.png"));
			readyP = ImageIO.read(GameClient.class.getResource("zb.png"));
			join = ImageIO.read(GameClient.class.getResource("jrfj.png"));
			create = ImageIO.read(GameClient.class.getResource("cjfj.png"));
			wait = ImageIO.read(GameClient.class.getResource("wait.jpg"));
			tip = ImageIO.read(GameClient.class.getResource("tishi.png"));
			show = ImageIO.read(GameClient.class.getResource("showCards.png"));
			start = ImageIO.read(GameClient.class.getResource("ksyx.png"));
			button1 = ImageIO.read(GameClient.class.getResource("g.png"));
			button2 = ImageIO.read(GameClient.class.getResource("g2.png"));
			login = ImageIO.read(GameClient.class.getResource("login.jpg"));
			title = ImageIO.read(GameClient.class.getResource("nmddz.png"));
			background = ImageIO.read(GameClient.class.getResource("background.jpg"));
			farmer = ImageIO.read(GameClient.class.getResource("farmer.png"));
			lord = ImageIO.read(GameClient.class.getResource("lord.png"));
			for (int i = 0; i < 54; i++) {
				card[i] = ImageIO.read(GameClient.class.getResource((i + 1) + ".jpg"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static Card[] totalCards = new Card[54];
	public static Card[] lordCards = new Card[0];
	public static Card[] nowCards = null; // 当前在桌面上的上家的牌
	public static Card[] myCards = new Card[0];
	public static Card[] showCards = new Card[0];
	public static int[] noteCards = new int[20];
	public static int[] tipsIndex = new int[0];
	public static Button startB, showB, putDownB, joinB, createB, exitB, readyB, passB;
	public static Button robB, noRobB, noReadyB, goOnB, exitRoomB;
	public static Button autoB, cancelAutoB;
	public static TimerTik timeTik1, timeTik2, timeTik3;
	public Socket s = null;
	public OutputStream serverOut;
	public OutputStreamWriter serverOutWriter;
	public static PrintWriter serverPrinter = null;
	public InputStream serverInput;
	public InputStreamReader serverInputReader;
	public static BufferedReader serverReader = null;
	public int roomNum = 0, myId = -1;
	public static int cardType = 0;
	public String name, tmpMsg;
	public static JFrame frame;
	public static Font f;
	public static List<int[]> tips = new ArrayList<int[]>();
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
	public static GameClient game = new GameClient();
	ExecutorService exec = Executors.newFixedThreadPool(30);

	public GameClient() {
		// 初始化各种控件
		cancelAutoB = new Button(340, 295, 100, 50);
		autoB = new Button(520, 305, 80, 30);
		timeTik1 = new TimerTik(100, 150); // 左家的计时器
		timeTik2 = new TimerTik(630, 150); // 右家的计时器
		timeTik3 = new TimerTik(375, 250); // 自己的计时器
		goOnB = new Button(237, 300, 100, 50);
		exitRoomB = new Button(450, 300, 100, 50);
		noReadyB = new Button(350, 400, 100, 50);
		readyB = new Button(237, 400, 100, 50);
		exitB = new Button(450, 400, 100, 50);
		joinB = new Button(150, 200, 200, 100);
		createB = new Button(450, 200, 200, 100);
		putDownB = new Button(430, 305, 80, 30);
		passB = new Button(340, 305, 80, 30);
		startB = new Button(550, 350, button1.getWidth(), button1.getHeight());
		showB = new Button(250, 305, 80, 30);
		robB = new Button(250, 305, 80, 30);
		noRobB = new Button(430, 305, 80, 30);
		// 初始化总卡组
		Arrays.fill(noteCards, 4);
		int indexNum = 3;
		for (int i = 0; i < 52; i++) {
			totalCards[i] = new Card();
			totalCards[i].image = card[i + 2];
			totalCards[i].num = indexNum;
			indexNum++;
			if (indexNum == 15) {
				indexNum = 16;
			}
			if (indexNum == 17) {
				indexNum = 3;
			}
		}
		// 小王
		totalCards[52] = new Card();
		totalCards[52].image = card[1];
		totalCards[52].num = 18;
		noteCards[18] = 1;
		// 大王
		totalCards[53] = new Card();
		totalCards[53].image = card[0];
		totalCards[53].num = 19;
		noteCards[19] = 1;
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
	 * 交换两个卡牌对象的数值,图片和Y坐标
	 * 
	 * @param card1
	 * @param card2
	 */
	public static void swapCards(Card card1, Card card2) {
		int temp;
		BufferedImage tempImage;
		// 交换两个对象的卡牌数值
		temp = card1.num;
		card1.num = card2.num;
		card2.num = temp;
		// 交换两个对象的卡牌Y位置
		temp = card1.y;
		card1.y = card2.y;
		card2.y = temp;
		// 交换两个对象的卡牌图片
		tempImage = card1.image;
		card1.image = card2.image;
		card2.image = tempImage;
		// 交换两个对象的总卡组下标
		temp = card1.cardIndex;
		card1.cardIndex = card2.cardIndex;
		card2.cardIndex = temp;
	}

	/**
	 * 牌数值的排序,只交换图片和数值
	 * 
	 * @param cards
	 *            需要排序的卡牌组
	 */
	public static void cardSort(Card[] cards) {
		for (int i = 0; i < cards.length; i++) {
			for (int j = 0; j < cards.length - i - 1; j++) {
				if (cards[j].num < cards[j + 1].num) {
					swapCards(cards[j], cards[j + 1]);
				}
			}
		}
	}

	/**
	 * 将选择的牌缩回
	 */
	public static void clearChoose() {
		for (int i = 0; i < myCards.length; i++) {
			if (myCards[i].y == 330) {
				myCards[i].y = 350;
			}
		}
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
	 * 提出已选择的牌
	 */
	public static void chooseCard() {
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
	 * 将选好的牌发送出去
	 */
	public static void putMyCards() {
		int len;
		// 将要打出的牌从手牌中删除
		for (int i = 0; i < myCards.length; i++) {
			if (myCards[i].y == 330) {
				swapCards(myCards[i], myCards[myCards.length - 1]);
				myCards = Arrays.copyOf(myCards, myCards.length - 1);
				i--;
			}
		}
		// 排序手中的卡牌
		cardSort(myCards);
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
		timeTik3.flag = false;
		nowCardType = 0;
		nowCards = null;
		nowLen = 0;
		errTips = 0;
		toDo = false;
	}

	/**
	 * 检查要出的牌是否符合牌型规则
	 * 
	 * @param cards
	 * @return
	 */
	public static int checkCards(Card[] cards) {
		int b = pairs(cards) + threesWithOne(cards) + threes(cards) + straight(cards) + threesWithPairs(cards)
				+ bomb(cards) + kingBomb(cards) + single(cards);
		return b;
	}

	/**
	 * 检测是否为单牌
	 * 
	 * @param cards
	 * @return 0或1
	 */
	public static int single(Card[] cards) {
		if (cards.length == 1) {
			return 1;
		}
		return 0;
	}

	/**
	 * 检测是否为顺子
	 * 
	 * @param cards
	 * @return 0或12
	 */
	public static int straight(Card[] cards) {
		int b = 12;
		// 如果小于5张则不是顺子
		if (cards.length < 5) {
			b = 0;
			return b;
		}
		// 比较相邻两张是否为差一顺序,如果有一对相邻的不差一则不是顺子
		for (int i = 0; i < cards.length - 1; i++) {
			if (cards[i].num - cards[i + 1].num != 1) {
				b = 0;
				return b;
			}
		}
		return b;
	}

	/**
	 * 检测是否为对子或对顺
	 * 
	 * @param cards
	 * @return 0或2
	 */
	public static int pairs(Card[] cards) {
		int b = 2;
		// 如果张数少于5张,或者不为2的倍数,并且不为2张,则不是对子或者对顺
		if ((cards.length < 5 || cards.length % 2 != 0) && cards.length != 2) {
			b = 0;
			return b;
		} // 如果为两张但是两张不一样则不是对子
		else if (cards.length == 2 && cards[0].num != cards[1].num) {
			b = 0;
			return b;
		}
		// 两张以上的,判断以两个为跳步的相邻两张是否相等,并且每对是否相差一
		for (int i = 0; i < cards.length - 2; i += 2) {
			if (cards[i].num != cards[i + 1].num || cards[i].num - cards[i + 2].num != 1) {
				b = 0;
				break;
			}
		}
		// 判断最后两张是否相等
		if (cards[cards.length - 2].num != cards[cards.length - 1].num) {
			b = 0;
		}
		return b;
	}

	/**
	 * 检测是否为三张或三顺
	 * 
	 * @param cards
	 * @return 0或3
	 */
	public static int threes(Card[] cards) {
		int b = 3;
		// 如果长度不是三的倍数则不是三张
		if (cards.length % 3 != 0) {
			b = 0;
			return b;
		}
		// 如果长度为三,则判断三张是否相等,不等则不是三张
		if (cards.length == 3 && cards[0].num == cards[1].num && cards[0].num == cards[2].num) {
			return b;
		} else if (cards.length == 3) {
			b = 0;
			return b;
		}
		// 多张用3作为跳步,比较相邻三张是否相等,并且每对三张是否相差一
		for (int i = 0; i < cards.length - 3; i += 3) {
			if (cards[i].num != cards[i + 1].num || cards[i].num != cards[i + 2].num
					|| cards[i].num - cards[i + 3].num != 1) {
				b = 0;
				break;
			}
		}
		// 检查最后三张是否相等
		if (cards[cards.length - 3].num != cards[cards.length - 2].num
				|| cards[cards.length - 3].num != cards[cards.length - 1].num) {
			b = 0;
		}
		return b;
	}

	/**
	 * 检测是否为三带一或三顺带多一
	 * 
	 * @param cards
	 * @return 0或4
	 */
	public static int threesWithOne(Card[] cards) {
		int b = 4;
		// 如果长度不是4的倍数则不是三带一
		if (cards.length % 4 != 0 || pairs(cards) == 2 || straight(cards) == 12) {
			b = 0;
			return b;
		}
		// 计算有多少组三带一
		int x = cards.length / 4;
		// 将该三带一进行前三后一的排序
		b = judgeSort(cards, 2, x, 1, 4);
		// 如果只有一组,判断如果是四张相同就不是三带一
		if (x == 1 && cards[0].num == cards[3].num) {
			b = 0;
			return b;
		}
		// 将三张组分离出来进行三顺判断
		Card[] three = new Card[x * 3];
		for (int i = 0; i < x * 3; i++) {
			three[i] = new Card();
			three[i].image = cards[i].image;
			three[i].num = cards[i].num;
		}
		b = threes(three);
		return b;
	}

	/**
	 * 检测是否为三带对或飞机
	 * 
	 * @param cards
	 * @return 0或5
	 */
	public static int threesWithPairs(Card[] cards) {
		int b = 5;
		if (cards.length % 5 != 0 || straight(cards) == 12) {
			b = 0;
			return b;
		} else if (cards[0].num != cards[1].num || pairs(cards) == 2) {
			b = 0;
			return b;
		}

		// 计算有多少组三带对
		int x = cards.length / 4;
		// 将该三带对进行前三后对的排序
		b = judgeSort(cards, 2, x, 2, 5);
		// 将三张组和对子组分离出来进行三顺和对子判断
		Card[] three = new Card[x * 3];
		for (int i = 0; i < x * 3; i++) {
			three[i] = new Card();
			three[i].image = cards[i].image;
			three[i].num = cards[i].num;
		}
		Card[] pair = new Card[x * 2];
		for (int i = x * 3; i < cards.length; i++) {
			pair[i - x * 3] = new Card();
			pair[i - x * 3].image = cards[i].image;
			pair[i - x * 3].num = cards[i].num;
		}
		// 判断三顺部分
		if (threes(three) == 0) {
			b = 0;
			return b;
		}
		// 多对部分如果为两张
		if (pair.length == 2 && pair[0].num != pair[1].num) {
			b = 0;
			return b;
		} else if (pair.length > 2) {
			// 两张以上的,判断以两个为跳步的相邻两张是否相等
			for (int i = 0; i < pair.length - 1; i += 2) {
				if (pair[i].num != pair[i + 1].num) {
					b = 0;
					break;
				}
			}
		}
		return b;
	}

	/**
	 * 检测是否为炸弹
	 * 
	 * @param cards
	 * @return 0或6
	 */
	public static int bomb(Card[] cards) {
		// 如果是4张并且四张数值相等则为炸弹
		if (cards.length == 4 && cards[0].num == cards[1].num && cards[0].num == cards[2].num
				&& cards[0].num == cards[3].num) {
			return 6;
		}
		return 0;
	}

	/**
	 * 检测是否为炸弹带二单
	 * 
	 * @param cards
	 * @return 0或7
	 */
	public int bombWithTwo(Card[] cards) {
		int b = 7;
		if (cards.length != 6 || pairs(cards) == 2 || threes(cards) == 3) {
			b = 0;
			return b;
		} else if (cards[2].num != cards[3].num) {
			b = 0;
			return b;
		}
		// 将该四带二进行前四后二的排序
		b = judgeSort(cards, 3, 2, 1, 7);

		System.out.println("bombWithTwo:");
		for (int i = 0; i < cards.length; i++) {
			System.out.print(cards[i].num);
		}
		System.out.println();

		// 检查前四张牌是否为炸弹
		for (int i = 0; i < 3; i++) {
			if (cards[i].num != cards[i + 1].num) {
				b = 0;
				return 0;
			}
		}
		return 7;
	}

	/**
	 * 检测是否为炸弹带两对
	 * 
	 * @param cards
	 * @return 0或8
	 */
	public int bombWithPair(Card[] cards) {
		int b = 8;
		if (cards.length != 8 || pairs(cards) == 2 || straight(cards) == 12) {
			b = 0;
			return b;
		}
		// 将该牌组进行前四后二的排序
		b = judgeSort(cards, 3, 2, 2, 8);

		System.out.println("bombWithPair:");
		for (int i = 0; i < cards.length; i++) {
			System.out.print(cards[i].num);
		}
		System.out.println();

		if (b == 0) {
			return b;
		}
		// 检查前四张牌是否为炸弹
		for (int i = 0; i < 3; i++) {
			if (cards[i].num != cards[i + 1].num) {
				b = 0;
				return 0;
			}
		}
		// 检查后四张牌是否为对子
		for (int i = 4; i < cards.length - 1; i += 2) {
			if (cards[i].num != cards[i + 1].num) {
				b = 0;
				return 0;
			}
		}
		return b;
	}

	/**
	 * 判断是否为王炸
	 * 
	 * @param cards
	 * @return 0或9
	 */
	public static int kingBomb(Card[] cards) {
		if (cards.length == 2 && cards[0].num == 19 && cards[1].num == 18) {
			return 9;
		}
		return 0;
	}

	/**
	 * 比较第1张和第m+1张牌,如果不相等,就将n张牌移到最后,如果超过x次则返回0,未超过则返回t
	 * 
	 * @param cards
	 * @param m
	 * @param x
	 * @param n
	 * @param t
	 * @return
	 */
	public static int judgeSort(Card[] cards, int m, int x, int n, int t) {
		if (cards[0].num != cards[m].num) {
			int index = 0;
			while (cards[0].num != cards[m].num) {
				if (index > x) {
					return 0;
				}
				index++;
				for (int i = 0; i < n; i++) {
					for (int j = 0; j < cards.length - 1; j++) {
						swapCards(cards[j], cards[j + 1]);
					}
				}
			}
		}
		return t;
	}

	/**
	 * 递归方法找出大于上家的牌
	 * 
	 * @param index
	 *            当前的tipsIndex的下标
	 * @param myIndex
	 *            当前的myCards的下标
	 */
	public static void findTips(int index, int myIndex) {
		Card[] tip = new Card[0];
		int cardType;
		if (index > myCards.length || myIndex > myCards.length) {
			return;
		}
		if (nowLen == 0) {
			return;
		}
		if (nowLen >= 4 && index > nowLen) {
			return;
		}
		if (nowLen < 4 && index > 4) {
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
			cardType = checkCards(tip);
			if (cardType == nowCardType && tip.length == nowLen && tip[0].num > nowCards[0].num) {
				tips.add(tipsIndex);
			} else if ((cardType == BOMB && nowCardType != BOMB && nowCardType != KINGBOMB) || cardType == KINGBOMB) {
				tips.add(tipsIndex);
			}
			findTips(index + 1, i + 1);
		}
	}

	/**
	 * 在集合中循环遍历每个提示,将提示的牌的y坐标-20.
	 */
	public static void showTips() {
		if (tipsId > tips.size() - 1) {
			tipsId = 0;
		}
		clearChoose();
		if (tips.size() == 0) {
			return;
		}
		int[] arr = tips.get(tipsId);
		for (int i = 0; i < arr.length; i++) {
			myCards[arr[i]].y -= 20;
		}
		tipsId++;
	}

	public synchronized static void autoPass() {
		if (toDo && showPass) {
			serverPrinter.println("PASS");
			serverPrinter.println(nowLen);
			serverPrinter.println(nowCardType);
			toDo = false;
			isPass = true;
			timeTik3.flag = false;
			errTips = 0;
			clearChoose();
		} else if (toRob) {
			serverPrinter.println("N");
			isRob = 1;
			toRob = false;
			timeTik3.flag = false;
		} else if (toDo && !showPass) {
			clearChoose();
			myCards[myCards.length - 1].y -= 20;
			chooseCard();
			if (showCards.length != 0) {
				cardType = checkCards(showCards);
			} else {
				cardType = 0;
			}
			putMyCards();
		}
	}
	
	public static void score() {
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

	public void paintNoteCards(Graphics g) {
		f = new Font(null, 0, 15);
		g.setFont(f);
		g.drawString(String.valueOf(noteCards[19]), 32, 50);
		g.drawString(String.valueOf(noteCards[18]), 50, 50);
		int step = 0;
		for (int i = 16; i > 2; i--) {
			if (i != 15) {
				g.drawString(String.valueOf(noteCards[i]), 67 + step * 16, 50);
				step++;
			}
		}
	}

	public void paintButton(Button butt, Graphics g) {
		g.drawImage(butt.image, butt.getX(), butt.getY(), butt.getWidth(), butt.getHeight(), null);
	}

	public void paintCards(Graphics g) {
		// 打印三张地主牌
		if (lordCards != null) {
			if (robIsOver) {
				for (int i = 0; i < lordCards.length; i++) {
					g.drawImage(lordCards[i].image, lordCards[i].x, lordCards[i].y, width, height, null);
					g.drawImage(lordCard, lordCards[i].x, lordCards[i].y, width, height, null);
				}
			} else {
				for (int i = 0; i < lordCards.length; i++) {
					g.drawImage(cardBack, lordCards[i].x, lordCards[i].y, width, height, null);
					g.drawImage(lordCard, lordCards[i].x, lordCards[i].y, width, height, null);
				}
			}
		}
		// 打印自己的手牌
		for (int i = 0; i < myCards.length; i++) {
			g.drawImage(myCards[i].image, myCards[i].x, myCards[i].y, width, height, null);
		}
		// 打印自己出的牌
		for (int i = 0; i < showCards.length; i++) {
			g.drawImage(showCards[i].image, showCards[i].x, showCards[i].y, width, height, null);
		}
		// 打印左家出的牌
		if (pl != null) {
			for (int i = 0; i < pl.cards.length; i++) {
				g.drawImage(pl.cards[i].image, pl.cards[i].x, pl.cards[i].y, width, height, null);
			}
		}
		// 打印右家出的牌
		if (pr != null) {
			for (int i = 0; i < pr.cards.length; i++) {
				g.drawImage(pr.cards[i].image, pr.cards[i].x, pr.cards[i].y, width, height, null);
			}
		}
	}

	@Override
	public void paint(Graphics g) {
		g.drawImage(background, 0, 0, frame.getWidth(), frame.getHeight(), null);
		// 游戏开始状态的界面图:开始画面,开始按钮
		f = new Font(null, Font.BOLD, 20);
		if (state == START) {
			g.drawImage(login, 0, 0, frame.getWidth(), frame.getHeight(), null);
			g.drawImage(title, 50, 30, 200, 200, null);
			paintButton(startB, g);
			g.drawImage(start, 575, 370, null);
		} // 游戏大厅状态的画面打印
		else if (state == SELECT_ROOM) {
			paintButton(joinB, g);
			g.drawImage(join, joinB.getX() + 25, joinB.getY() + 20, joinB.getWidth() - 50, joinB.getHeight() - 50,
					null);
			paintButton(createB, g);
			g.drawImage(create, createB.getX() + 25, createB.getY() + 20, createB.getWidth() - 50,
					createB.getHeight() - 50, null);
		} // 房间内的画面打印
		else if (state == IN_ROOM) {
			paintButton(exitB, g);
			g.drawImage(exit, exitB.getX(), exitB.getY(), exitB.getWidth(), exitB.getHeight(), null);
			paintButton(readyB, g);
			g.drawImage(start, readyB.getX() + 10, readyB.getY() + 8, readyB.getWidth() - 20, readyB.getHeight() - 20,
					null);
			g.setFont(f);
			if (pl != null) {
				g.drawString(pl.name, 30, 120);
				if (pl.ready == 1) {
					g.drawImage(readyP, 30, 150, null);
				}
			}
			if (pr != null) {
				g.drawString(pr.name, 700, 120);
				if (pr.ready == 1) {
					g.drawImage(readyP, 700, 150, null);
				}
			}
			g.drawString(name, 150, 450);
		} // 准备状态下的 画面打印
		else if (state == WAIT) {
			g.drawImage(readyP, 375, 300, null);
			g.setFont(f);
			if (pl != null) {
				g.drawString(pl.name, 30, 120);
				if (pl.ready == 1) {
					g.drawImage(readyP, 30, 150, null);
				}
			}
			if (pr != null) {
				g.drawString(pr.name, 700, 120);
				if (pr.ready == 1) {
					g.drawImage(readyP, 700, 150, null);
				}
			}
			g.drawString(name, 150, 450);
			paintButton(noReadyB, g);
			g.drawImage(noReady, noReadyB.getX(), noReadyB.getY(), noReadyB.getWidth(), noReadyB.getHeight(), null);
		} // 游戏状态打印
		else if (state == GAME) {
			if (pl != null) {
				g.setFont(f);
				g.drawString(pl.name, 30, 250);
				g.drawString("Score:" + pl.score, 30, 275);
				if (pl.cardsNum != 0) {
					g.drawImage(cardBack, 30, 150, null);
				}
				g.setFont(f);
				g.drawString(String.valueOf(pl.cardsNum), 30, 190);
				if (pl.isDo) {
					g.drawImage(timeTik1.image, timeTik1.x, timeTik1.y, 50, 50, null);
				}
				if (pl.isRob == 1 && !robIsOver) {
					g.drawImage(noRob, 100, 150, null);
				} else if (pl.isRob == 2 && !robIsOver) {
					g.drawImage(rob, 100, 150, null);
				}
				if (pl.isPass && robIsOver) {
					g.drawImage(pass, 100, 150, null);
				}
				if (pl.isLord && robIsOver) {
					g.drawImage(lord, 15, 35, null);
				} else if (robIsOver) {
					g.drawImage(farmer, 15, 35, null);
				}
			} else {
				g.drawImage(cardBack, 30, 150, null);
				g.drawImage(leave, 30, 150, null);
			}
			if (pr != null) {
				g.setFont(f);
				g.drawString(pr.name, 700, 250);
				g.drawString("Score:" + pr.score, 700, 275);
				g.setFont(f);
				if (pr.cardsNum != 0) {
					g.drawImage(cardBack, 700, 150, null);
				}
				g.drawString(String.valueOf(pr.cardsNum), 700, 190);
				if (pr.isDo) {
					g.drawImage(timeTik2.image, timeTik2.x, timeTik2.y, 50, 50, null);
				}
				if (pr.isRob == 1 && !robIsOver) {
					g.drawImage(noRob, 600, 150, null);
				} else if (pr.isRob == 2 && !robIsOver) {
					g.drawImage(rob, 600, 150, null);
				}
				if (pr.isPass && robIsOver) {
					g.drawImage(pass, 600, 150, null);
				}
				if (pr.isLord && robIsOver) {
					g.drawImage(lord, 675, 35, null);
				} else if (robIsOver) {
					g.drawImage(farmer, 675, 35, null);
				}
			} else {
				g.drawImage(cardBack, 700, 150, null);
				g.drawImage(leave, 700, 150, null);
			}
			g.drawString(name, 625, 400);
			g.drawString("Score:" + score, 625, 435);
			paintCards(g);
			if (isRob == 1 && !robIsOver) {
				g.drawImage(noRob, 350, 300, null);
			} else if (isRob == 2 && !robIsOver) {
				g.drawImage(rob, 350, 300, null);
			}
			if (isPass && robIsOver) {
				g.drawImage(pass, 350, 300, null);
			}
			if (isLord && robIsOver) {
				g.drawImage(lord, 150, 350, 100, 100, null);
			} else if (robIsOver) {
				g.drawImage(farmer, 150, 350, 100, 100, null);
			}
			if (toPass) {
				g.drawImage(cancelAutoB.image, cancelAutoB.getX(), cancelAutoB.getY(), cancelAutoB.getWidth(),
						cancelAutoB.getHeight(), null);
				g.drawImage(cancelAuto, cancelAutoB.getX() + 10, cancelAutoB.getY() + 5, cancelAutoB.getWidth() - 20,
						cancelAutoB.getHeight() - 20, null);
			}
			if (toRob) {
				g.drawImage(autoB.image, autoB.getX(), autoB.getY(), autoB.getWidth(), autoB.getHeight(), null);
				g.drawImage(auto, autoB.getX() + 5, autoB.getY() + 3, autoB.getWidth() - 10, autoB.getHeight() - 10,
						null);
				g.drawImage(timer, 375, 250, 50, 50, null);
				g.drawImage(robB.image, robB.getX(), robB.getY(), robB.getWidth(), robB.getHeight(), null);
				g.drawImage(rob, robB.getX() + 5, robB.getY() + 3, robB.getWidth() - 10, robB.getHeight() - 10, null);
				g.drawImage(noRobB.image, noRobB.getX(), noRobB.getY(), noRobB.getWidth(), noRobB.getHeight(), null);
				g.drawImage(noRob, noRobB.getX() + 5, noRobB.getY() + 3, noRobB.getWidth() - 10,
						noRobB.getHeight() - 10, null);
			}
			if (toDo) {
				g.drawImage(autoB.image, autoB.getX(), autoB.getY(), autoB.getWidth(), autoB.getHeight(), null);
				g.drawImage(auto, autoB.getX() + 5, autoB.getY() + 3, autoB.getWidth() - 10, autoB.getHeight() - 10,
						null);
				g.drawImage(timeTik3.image, timeTik3.x, timeTik3.y, 50, 50, null);
				g.drawImage(showB.image, showB.getX(), showB.getY(), showB.getWidth(), showB.getHeight(), null);
				g.drawImage(show, showB.getX() + 5, showB.getY() + 3, showB.getWidth() - 10, showB.getHeight() - 10,
						null);
				if (showPass) {
					g.drawImage(passB.image, passB.getX(), passB.getY(), passB.getWidth(), passB.getHeight(), null);
					g.drawImage(pass, passB.getX() + 5, passB.getY() + 3, passB.getWidth() - 10, passB.getHeight() - 10,
							null);
				}
				g.drawImage(putDownB.image, putDownB.getX(), putDownB.getY(), putDownB.getWidth(), putDownB.getHeight(),
						null);
				g.drawImage(tip, putDownB.getX() + 5, putDownB.getY() + 3, putDownB.getWidth() - 10,
						putDownB.getHeight() - 10, null);
				if (errTips == 1) {
					g.drawImage(tips1, 200, 175, null);
				} else if (errTips == 2) {
					g.drawImage(tips2, 200, 175, null);
				} else if (errTips == 3) {
					g.drawImage(tips3, 200, 175, null);
				}
			}
			f = new Font(null, 0, 10);
			g.setFont(f);
			g.drawString("本局分数:" + point, 350, 20);
			// 打印记牌器:
			if (key) {
				g.drawImage(noteCard, 10, 10, 260, 50, null);
				paintNoteCards(g);
			}
		} // 游戏结束状态打印
		else if (state == OVER) {
			g.drawString(name, 125, 400);
			g.drawString("Score:" + score, 125, 435);
			paintCards(g);
			if (pl != null) {
				g.setFont(f);
				g.drawString(pl.name, 30, 120);
				g.drawString("Score:" + pl.score, 30, 145);
				g.drawImage(cardBack, 30, 150, null);
				g.setFont(f);
				g.drawString(String.valueOf(pl.cardsNum), 30, 190);
				if (pl.isWin) {
					if (isLord) {
						g.drawString("You are a Lord,You Lose!", 175, 225);
					} else if (pl.isLord) {
						g.drawString("You are a Farmer,You Lose!", 175, 225);
					} else {
						g.drawString("You are a Farmer,You Win!", 175, 225);
					}
				}
			}
			if (pr != null) {
				g.setFont(f);
				g.drawString(pr.name, 700, 120);
				g.drawString("Score:" + pr.score, 700, 145);
				g.setFont(f);
				g.drawImage(cardBack, 700, 150, null);
				g.drawString(String.valueOf(pr.cardsNum), 700, 190);
				if (pr.isWin) {
					if (isLord) {
						g.drawString("You are a Lord,You Lose!", 175, 225);
					} else if (pr.isLord) {
						g.drawString("You are a Farmer,You Lose!", 175, 225);
					} else {
						g.drawString("You are a Farmer,You Win!", 175, 225);
					}
				}
			}
			if (isWin) {
				g.setFont(f);
				g.drawString("Your WIN!", 350, 225);
			}
			g.drawImage(goOnB.image, goOnB.getX(), goOnB.getY(), goOnB.getWidth(), goOnB.getHeight(), null);
			g.drawImage(goOn, goOnB.getX(), goOnB.getY(), goOnB.getWidth(), goOnB.getHeight(), null);
			g.drawImage(exitRoomB.image, exitRoomB.getX(), exitRoomB.getY(), exitRoomB.getWidth(),
					exitRoomB.getHeight(), null);
			g.drawImage(exit, exitRoomB.getX(), exitRoomB.getY(), exitRoomB.getWidth(), exitRoomB.getHeight(), null);
		}
	}

	/**
	 * 游戏主方法
	 */
	public void action() {
		ServerHandler sh = new ServerHandler();
		final Thread gameHandler = new Thread(sh);
		Timer t = new Timer();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				repaint();
			}
		}, 0, 5);

		MouseAdapter m = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// 退出按钮
				if (buttonFlash(e, state == IN_ROOM, exitB)) {
					serverPrinter.println("EXIT");
					newData();
					pl = null;
					pr = null;
					score = 0;
					state = SELECT_ROOM;
					frame.setTitle("局域网斗地主:" + name);
				}

				// 取消准备按钮
				if (buttonFlash(e, state == WAIT, noReadyB)) {
					state = IN_ROOM;
					try {
						tmpMsg = serverReader.readLine();
						if (tmpMsg.equals("ALIVE")) {
							serverPrinter.println("NOREADY");
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}

				}

				// 准备按钮
				if (buttonFlash(e, state == IN_ROOM, readyB)) {
					System.out.println("READY");
					serverPrinter.println("READY");
					state = WAIT;

				}

				// 继续游戏按钮
				if (buttonFlash(e, state == OVER, goOnB)) {
					serverPrinter.println("CONTINUE");
					if (pl != null) {
						pl.cards = new Card[0];
					}
					if (pr != null) {
						pr.cards = new Card[0];
					}
					lordCards = new Card[0];
					myCards = new Card[0];
					showCards = new Card[0];
					isRob = 0;
					isPass = false;
					toDo = false;
					toRob = false;
					isLord = false;
					state = IN_ROOM;

				}
				// 退出房间按钮
				if (buttonFlash(e, state == OVER, exitRoomB)) {
					serverPrinter.println("EXIT");
					newData();
					score = 0;
					pl = null;
					pr = null;
					state = SELECT_ROOM;
					frame.setTitle("局域网斗地主:" + name);
				}

				// 开始游戏按钮
				if (buttonFlash(e, state == START, startB)) {
					try {
						String ip = JOptionPane.showInputDialog("请输入服务器的IP地址(直接点击确定则以默认IP:176.24.3.33连接):");
						if (ip != null && ip.equals("")) {
							ip = "176.24.3.33";
						}
						if (ip != null && !ip.equals("")) {
							s = new Socket(ip, 3000);
							// 创建服务器输出流
							serverOut = s.getOutputStream();
							serverOutWriter = new OutputStreamWriter(serverOut, "utf-8");
							serverPrinter = new PrintWriter(serverOutWriter, true);
							// 创建服务器输入流
							serverInput = s.getInputStream();
							serverInputReader = new InputStreamReader(serverInput, "utf-8");
							serverReader = new BufferedReader(serverInputReader);
							// 从用户处输入客户端名字
							name = JOptionPane.showInputDialog("服务器连接成功!请输入你的游戏昵称:");
							// 将名字传给服务器
							serverPrinter.println(name);
							// 接收从服务端发来的名字不合法或者重复的信息
							while (serverReader.readLine().equals("N")) {
								name = JOptionPane.showInputDialog("你所输入的昵称不合法或者已存在,请重新输入你的名字:");
								serverPrinter.println(name);
							}
							frame.setTitle("局域网斗地主:" + name);
							state = SELECT_ROOM;
							// 开启服务器监听线程
							gameHandler.start();

						}
					} catch (Exception e1) {
						state = START;

						JOptionPane.showMessageDialog(null, "服务器连接失败!", "连接失败", JOptionPane.ERROR_MESSAGE);
					}
				}

				// 加入房间按钮
				if (buttonFlash(e, state == SELECT_ROOM, joinB)) {
					try {
						String tmp = JOptionPane.showInputDialog("请输入你要加入的房间号(1~10):");
						while (tmp != null && (Integer.valueOf(tmp) > 10 || Integer.valueOf(tmp) < 0)) {
							tmp = JOptionPane.showInputDialog("输入有误,请输入正确范围内的房间号(1~10):");
						}
						if (tmp != null && !tmp.equals("")) {
							serverPrinter.println("Y");
							serverPrinter.println(tmp);
							tmpMsg = serverReader.readLine();
							if (tmpMsg.equals("ERROR")) {
								JOptionPane.showMessageDialog(null, "房间不存在或者房间人数已满!", "进入房间错误",
										JOptionPane.ERROR_MESSAGE);
							} else {
								roomNum = Integer.valueOf(tmp);
								frame.setTitle("局域网斗地主:" + name + ":第" + roomNum + "桌");
								state = IN_ROOM;

							}
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				// 创建房间按钮,自动分配房间号
				if (buttonFlash(e, state == SELECT_ROOM, createB)) {
					try {
						serverPrinter.println("N");
						tmpMsg = serverReader.readLine();
						roomNum = Integer.valueOf(tmpMsg);
						frame.setTitle("局域网斗地主:" + name + ":第" + (roomNum + 1) + "桌");
						state = IN_ROOM;

					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}

				// 判断是否点到牌面上
				for (int i = 0; i < myCards.length - 1; i++) {
					if (state == GAME && e.getX() >= myCards[i].x && e.getX() <= myCards[i].x + 15
							&& e.getY() >= myCards[i].y && e.getY() <= myCards[i].y + 100) {
						if (myCards[i].y == 350) {
							myCards[i].y -= 20;
						} else {
							myCards[i].y += 20;
						}
						errTips = 0;

						break;
					}
				}
				// 判断是否点到了最后一张牌
				if (state == GAME && myCards.length != 0 && e.getX() >= myCards[myCards.length - 1].x
						&& e.getX() <= myCards[myCards.length - 1].x + 70 && e.getY() >= myCards[myCards.length - 1].y
						&& e.getY() <= myCards[myCards.length - 1].y + 100) {
					if (myCards[myCards.length - 1].y == 350) {
						myCards[myCards.length - 1].y -= 20;
					} else {
						myCards[myCards.length - 1].y += 20;
					}
					errTips = 0;

				}
				// 出牌按钮
				if (buttonFlash(e, state == GAME && toDo, showB)) {
					chooseCard();
					if (showCards.length != 0) {
						cardType = checkCards(showCards);
					} else {
						cardType = 0;
					}
					// 比较过程:先比较牌的类型(炸弹等),在比较牌的数量
					if (cardType != 0) {
						if (!showPass) {
							putMyCards();
						} else if (cardType == nowCardType && showCards.length == nowLen
								&& showCards[0].num > nowCards[0].num) {
							putMyCards();
							if (cardType == BOMB || cardType == KINGBOMB) {
								point *= 2;
							}
						} else if ((cardType == BOMB && nowCardType != BOMB && nowCardType != KINGBOMB)
								|| cardType == KINGBOMB) {
							putMyCards();
							point *= 2;
						} else {
							showCards = new Card[0];
							errTips = 1;
						}
					} else if (cardType == 0 && showCards.length != 0) {
						showCards = new Card[0];
						errTips = 2;
					}
				}

				// 放下按钮
				if (buttonFlash(e, state == GAME && toDo, putDownB)) {
					if (tips.size() == 0) {
						clearChoose();
						errTips = 3;
					} else {
						showTips();
						errTips = 0;
					}
				}

				// 不出按钮
				if (buttonFlash(e, state == GAME && toDo && showPass, passB)) {
					autoPass();
				}
				// 抢地主按钮
				if (buttonFlash(e, state == GAME && toRob, robB)) {
					serverPrinter.println("Y");
					isRob = 2;
					toRob = false;
					point *= 2;
					timeTik3.flag = false;
				}
				// 不抢按钮
				if (buttonFlash(e, state == GAME && toRob, noRobB)) {
					autoPass();
				}
				// 托管按钮
				if (buttonFlash(e, state == GAME && (toRob || toDo), autoB)) {
					autoPass();
					toPass = true;
				}
				// 取消托管
				if (buttonFlash(e, state == GAME && toPass, cancelAutoB)) {
					toPass = false;
				}
			}

			/**
			 * 在移动鼠标时,显示按钮的动画效果,在鼠标点击时判断是否在该按钮范围内.
			 * 
			 * @param e
			 *            鼠标指针
			 * @param flag
			 *            判断条件
			 * @param b
			 *            按钮对象
			 * @return 布尔型,判断是否在该按钮范围内
			 */
			public boolean buttonFlash(MouseEvent e, boolean flag, Button b) {
				boolean c = false;
				if (flag && e.getX() > b.getX() && e.getY() > b.getY() && e.getX() < b.getX() + b.getWidth()
						&& e.getY() < b.getY() + b.getHeight()) {
					b.image = button2;
					repaint(b.getX(), b.getY(), b.getWidth(), b.getHeight());
					c = true;
				} else if (flag) {
					b.image = button1;
					repaint(b.getX(), b.getY(), b.getWidth(), b.getHeight());
				}
				return c;
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				// 各种按钮动画效果
				buttonFlash(e, state == START, startB);
				buttonFlash(e, state == SELECT_ROOM, joinB);
				buttonFlash(e, state == SELECT_ROOM, createB);
				buttonFlash(e, state == IN_ROOM, exitB);
				buttonFlash(e, state == IN_ROOM, readyB);
				buttonFlash(e, state == GAME && toRob, robB);
				buttonFlash(e, state == GAME && toRob, noRobB);
				buttonFlash(e, state == GAME && toDo, showB);
				buttonFlash(e, state == GAME && toDo, putDownB);
				buttonFlash(e, state == GAME && toDo && showPass, passB);
				buttonFlash(e, state == WAIT, noReadyB);
				buttonFlash(e, state == OVER, goOnB);
				buttonFlash(e, state == OVER, exitRoomB);
				buttonFlash(e, state == GAME && (toRob || toDo), autoB);
				buttonFlash(e, state == GAME && toPass, cancelAutoB);
			}

		};
		this.addMouseMotionListener(m);
		this.addMouseListener(m);

		KeyAdapter k = new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_K) {
					key = key ? false : true;
				}
			}
		};
		this.addKeyListener(k);
		this.setFocusable(true);
		this.requestFocus();
	}

	public static void main(String[] args) {
		frame = new JFrame("局域网斗地主");
		frame.setSize(WIDTH, HEIGHT);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.add(game);
		frame.setVisible(true);
		game.action();
	}

	/**
	 * 服务器信息处理线程,随时接受服务器传来的信息以处理房间状态和游戏状态
	 * 
	 * @author String
	 *
	 */
	private class ServerHandler extends Thread {

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
							cardSort(myCards);
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
									timeTik1.flag = false;
								} else if (pr != null && pr.playerID == id) {
									pr.isRob = 2;
									pr.isDo = false;
									timeTik2.flag = false;
								}
								point *= 2;
							} else {
								if (pl != null && pl.playerID == id) {
									pl.isRob = 1;
									pl.isDo = false;
									timeTik1.flag = false;
								} else if (pr != null && pr.playerID == id) {
									pr.isRob = 1;
									pr.isDo = false;
									timeTik2.flag = false;
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
								cardSort(myCards);
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
								timeTik1.flag = false;
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
								timeTik2.flag = false;
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
				if (s != null) {
					try {
						s.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
