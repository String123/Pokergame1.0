package com.string.pokergame.client.handler;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;
/**
 * 封装鼠标的控制和点击方法
 *
 */
public class MouseHandler extends Handler implements MouseListener, MouseWheelListener, MouseMotionListener {
	
	private static final long serialVersionUID = 1L;
	
	private ServerHandler sh = null;
	private Socket server = null;
	private OutputStream serverOut;
	private OutputStreamWriter serverOutWriter;
	private InputStream serverInput;
	private InputStreamReader serverInputReader;
	private Integer serverPort;
	private String serverHost;
	private FrameHandler frame;

	public MouseHandler(FrameHandler frame){
		this.frame=frame;
		
		byte[] b = new byte[1024 * 10];
		InputStream config = null;
		String text;
		int len;

		try {
			config = FrameHandler.class.getResourceAsStream("config.txt");
			text = "";
			while ((len = config.read(b)) != -1) {
				text = text + new String(b, 0, len);
			}
			serverHost = text.substring(text.indexOf("=") + 1, text.indexOf("\n") - 1);
			serverPort = Integer.valueOf(text.substring(text.indexOf("=", text.indexOf("=") + 1) + 1));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("配置文件未找到!");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (config != null) {
				try {
					config.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 在集合中循环遍历每个提示,将提示的牌的y坐标-20.
	 */
	public void showTips() {
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
				String ip = JOptionPane.showInputDialog(
						"请输入服务器的IP地址(直接点击确定则以默认IP:" + serverHost + ",默认端口:" + serverPort + "连接):");
				if (ip != null && ip.equals("")) {
					ip = serverHost;
				}
				if (ip != null && !ip.equals("")) {
					server = new Socket(ip, serverPort);
					// 创建服务器输出流
					serverOut = server.getOutputStream();
					serverOutWriter = new OutputStreamWriter(serverOut, "utf-8");
					serverPrinter = new PrintWriter(serverOutWriter, true);
					// 创建服务器输入流
					serverInput = server.getInputStream();
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
					sh = new ServerHandler(server);
					Thread gameHandler = new Thread(sh);
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
				cardType = CardsUtil.checkCards(showCards);
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
					if (cardType == CardsUtil.BOMB || cardType == CardsUtil.KINGBOMB) {
						point *= 2;
					}
				} else if ((cardType == CardsUtil.BOMB && nowCardType != CardsUtil.BOMB && nowCardType != CardsUtil.KINGBOMB)
						|| cardType == CardsUtil.KINGBOMB) {
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
			timeTik3.stopTik();
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
			b.image = FrameHandler.button2;
			repaint(b.getX(), b.getY(), b.getWidth(), b.getHeight());
			c = true;
		} else if (flag) {
			b.image = FrameHandler.button1;
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

	@Override
	public void mouseDragged(MouseEvent e) {
		
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

}
