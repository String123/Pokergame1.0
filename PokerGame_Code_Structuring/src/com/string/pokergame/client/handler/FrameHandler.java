package com.string.pokergame.client.handler;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * 封装窗口的创建和打印各种对象
 * 
 * @author String
 * 
 */
public class FrameHandler extends Handler {
	private static final long serialVersionUID = 1L;

	public static final int WIDTH = 800;
	public static final int HEIGHT = 500;
	public static final int CARD_WIDTH = 70, CARD_HEIGHT = 100;
	
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

	private Font f;
	private JFrame frame;

	static { // 加载背景图和文字图片资源
		try {
			noteCard = ImageIO.read(FrameHandler.class.getResource("image/jipaiqi.png"));
			auto = ImageIO.read(FrameHandler.class.getResource("image/tuoguan.png"));
			cancelAuto = ImageIO.read(FrameHandler.class.getResource("image/qxtg.png"));
			noReady = ImageIO.read(FrameHandler.class.getResource("image/cancelReady.png"));
			tips1 = ImageIO.read(FrameHandler.class.getResource("image/pxx.png"));
			tips2 = ImageIO.read(FrameHandler.class.getResource("image/pxcw.png"));
			tips3 = ImageIO.read(FrameHandler.class.getResource("image/mdp.png"));
			leave = ImageIO.read(FrameHandler.class.getResource("image/lixian.png"));
			goOn = ImageIO.read(FrameHandler.class.getResource("image/jixu.png"));
			lordCard = ImageIO.read(FrameHandler.class.getResource("image/dzp.png"));
			timer = ImageIO.read(FrameHandler.class.getResource("image/naozhong.png"));
			noRob = ImageIO.read(FrameHandler.class.getResource("image/bq.png"));
			rob = ImageIO.read(FrameHandler.class.getResource("image/qdz.png"));
			pass = ImageIO.read(FrameHandler.class.getResource("image/bc.png"));
			cardBack = ImageIO.read(FrameHandler.class.getResource("image/paishu.png"));
			exit = ImageIO.read(FrameHandler.class.getResource("image/likai.png"));
			readyP = ImageIO.read(FrameHandler.class.getResource("image/zb.png"));
			join = ImageIO.read(FrameHandler.class.getResource("image/jrfj.png"));
			create = ImageIO.read(FrameHandler.class.getResource("image/cjfj.png"));
			wait = ImageIO.read(FrameHandler.class.getResource("image/wait.jpg"));
			tip = ImageIO.read(FrameHandler.class.getResource("image/tishi.png"));
			show = ImageIO.read(FrameHandler.class.getResource("image/showCards.png"));
			start = ImageIO.read(FrameHandler.class.getResource("image/ksyx.png"));
			button1 = ImageIO.read(FrameHandler.class.getResource("image/g.png"));
			button2 = ImageIO.read(FrameHandler.class.getResource("image/g2.png"));
			login = ImageIO.read(FrameHandler.class.getResource("image/login.jpg"));
			title = ImageIO.read(FrameHandler.class.getResource("image/nmddz.png"));
			background = ImageIO.read(FrameHandler.class.getResource("image/background.jpg"));
			farmer = ImageIO.read(FrameHandler.class.getResource("image/farmer.png"));
			lord = ImageIO.read(FrameHandler.class.getResource("image/lord.png"));
			for (int i = 0; i < 54; i++) {
				card[i] = ImageIO.read(FrameHandler.class.getResource("image/" + (i + 1) + ".jpg"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static{
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
	
	private void paintNoteCards(Graphics g) {
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

	private void paintButton(Button butt, Graphics g) {
		g.drawImage(butt.image, butt.getX(), butt.getY(), butt.getWidth(), butt.getHeight(), null);
	}

	private void paintCards(Graphics g) {
		// 打印三张地主牌
		if (lordCards != null) {
			if (robIsOver) {
				for (int i = 0; i < lordCards.length; i++) {
					g.drawImage(lordCards[i].image, lordCards[i].x, lordCards[i].y, CARD_WIDTH, CARD_HEIGHT, null);
					g.drawImage(lordCard, lordCards[i].x, lordCards[i].y, CARD_WIDTH, CARD_HEIGHT, null);
				}
			} else {
				for (int i = 0; i < lordCards.length; i++) {
					g.drawImage(cardBack, lordCards[i].x, lordCards[i].y, CARD_WIDTH, CARD_HEIGHT, null);
					g.drawImage(lordCard, lordCards[i].x, lordCards[i].y, CARD_WIDTH, CARD_HEIGHT, null);
				}
			}
		}
		// 打印自己的手牌
		for (int i = 0; i < myCards.length; i++) {
			g.drawImage(myCards[i].image, myCards[i].x, myCards[i].y, CARD_WIDTH, CARD_HEIGHT, null);
		}
		// 打印自己出的牌
		for (int i = 0; i < showCards.length; i++) {
			g.drawImage(showCards[i].image, showCards[i].x, showCards[i].y, CARD_WIDTH, CARD_HEIGHT, null);
		}
		// 打印左家出的牌
		if (pl != null) {
			for (int i = 0; i < pl.cards.length; i++) {
				g.drawImage(pl.cards[i].image, pl.cards[i].x, pl.cards[i].y, CARD_WIDTH, CARD_HEIGHT, null);
			}
		}
		// 打印右家出的牌
		if (pr != null) {
			for (int i = 0; i < pr.cards.length; i++) {
				g.drawImage(pr.cards[i].image, pr.cards[i].x, pr.cards[i].y, CARD_WIDTH, CARD_HEIGHT, null);
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
					g.drawImage(timeTik1.getImage(), timeTik1.getX(), timeTik1.getY(), 50, 50, null);
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
					g.drawImage(timeTik2.getImage(), timeTik2.getX(), timeTik2.getY(), 50, 50, null);
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
				g.drawImage(timeTik3.getImage(), timeTik3.getX(), timeTik3.getY(), 50, 50, null);
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
	
	public void setTitle(String title){
		frame.setTitle(title);
	}
	
	public FrameHandler(){
		frame = new JFrame("局域网斗地主");
		frame.setSize(WIDTH, HEIGHT);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.add(this);
		frame.setVisible(true);
		
		Timer t = new Timer();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				repaint();
			}
		}, 0, 5);
	}

}
