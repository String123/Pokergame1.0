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
 * ��װ���Ŀ��ƺ͵������
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
			System.out.println("�����ļ�δ�ҵ�!");
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
	 * �ڼ�����ѭ������ÿ����ʾ,����ʾ���Ƶ�y����-20.
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
		// �˳���ť
		if (buttonFlash(e, state == IN_ROOM, exitB)) {
			serverPrinter.println("EXIT");
			newData();
			pl = null;
			pr = null;
			score = 0;
			state = SELECT_ROOM;
			frame.setTitle("������������:" + name);
		}

		// ȡ��׼����ť
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

		// ׼����ť
		if (buttonFlash(e, state == IN_ROOM, readyB)) {
			System.out.println("READY");
			serverPrinter.println("READY");
			state = WAIT;

		}

		// ������Ϸ��ť
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
		// �˳����䰴ť
		if (buttonFlash(e, state == OVER, exitRoomB)) {
			serverPrinter.println("EXIT");
			newData();
			score = 0;
			pl = null;
			pr = null;
			state = SELECT_ROOM;
			frame.setTitle("������������:" + name);
		}

		// ��ʼ��Ϸ��ť
		if (buttonFlash(e, state == START, startB)) {
			try {
				String ip = JOptionPane.showInputDialog(
						"�������������IP��ַ(ֱ�ӵ��ȷ������Ĭ��IP:" + serverHost + ",Ĭ�϶˿�:" + serverPort + "����):");
				if (ip != null && ip.equals("")) {
					ip = serverHost;
				}
				if (ip != null && !ip.equals("")) {
					server = new Socket(ip, serverPort);
					// ���������������
					serverOut = server.getOutputStream();
					serverOutWriter = new OutputStreamWriter(serverOut, "utf-8");
					serverPrinter = new PrintWriter(serverOutWriter, true);
					// ����������������
					serverInput = server.getInputStream();
					serverInputReader = new InputStreamReader(serverInput, "utf-8");
					serverReader = new BufferedReader(serverInputReader);
					// ���û�������ͻ�������
					name = JOptionPane.showInputDialog("���������ӳɹ�!�����������Ϸ�ǳ�:");
					// �����ִ���������
					serverPrinter.println(name);
					// ���մӷ���˷��������ֲ��Ϸ������ظ�����Ϣ
					while (serverReader.readLine().equals("N")) {
						name = JOptionPane.showInputDialog("����������ǳƲ��Ϸ������Ѵ���,�����������������:");
						serverPrinter.println(name);
					}
					frame.setTitle("������������:" + name);
					state = SELECT_ROOM;
					// ���������������߳�
					sh = new ServerHandler(server);
					Thread gameHandler = new Thread(sh);
					gameHandler.start();
				}
			} catch (Exception e1) {
				state = START;
				JOptionPane.showMessageDialog(null, "����������ʧ��!", "����ʧ��", JOptionPane.ERROR_MESSAGE);
			}
		}

		// ���뷿�䰴ť
		if (buttonFlash(e, state == SELECT_ROOM, joinB)) {
			try {
				String tmp = JOptionPane.showInputDialog("��������Ҫ����ķ����(1~10):");
				while (tmp != null && (Integer.valueOf(tmp) > 10 || Integer.valueOf(tmp) < 0)) {
					tmp = JOptionPane.showInputDialog("��������,��������ȷ��Χ�ڵķ����(1~10):");
				}
				if (tmp != null && !tmp.equals("")) {
					serverPrinter.println("Y");
					serverPrinter.println(tmp);
					tmpMsg = serverReader.readLine();
					if (tmpMsg.equals("ERROR")) {
						JOptionPane.showMessageDialog(null, "���䲻���ڻ��߷�����������!", "���뷿�����",
								JOptionPane.ERROR_MESSAGE);
					} else {
						roomNum = Integer.valueOf(tmp);
						frame.setTitle("������������:" + name + ":��" + roomNum + "��");
						state = IN_ROOM;

					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		// �������䰴ť,�Զ����䷿���
		if (buttonFlash(e, state == SELECT_ROOM, createB)) {
			try {
				serverPrinter.println("N");
				tmpMsg = serverReader.readLine();
				roomNum = Integer.valueOf(tmpMsg);
				frame.setTitle("������������:" + name + ":��" + (roomNum + 1) + "��");
				state = IN_ROOM;

			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		// �ж��Ƿ�㵽������
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
		// �ж��Ƿ�㵽�����һ����
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
		// ���ư�ť
		if (buttonFlash(e, state == GAME && toDo, showB)) {
			chooseCard();
			if (showCards.length != 0) {
				cardType = CardsUtil.checkCards(showCards);
			} else {
				cardType = 0;
			}
			// �ȽϹ���:�ȱȽ��Ƶ�����(ը����),�ڱȽ��Ƶ�����
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

		// ���°�ť
		if (buttonFlash(e, state == GAME && toDo, putDownB)) {
			if (tips.size() == 0) {
				clearChoose();
				errTips = 3;
			} else {
				showTips();
				errTips = 0;
			}
		}

		// ������ť
		if (buttonFlash(e, state == GAME && toDo && showPass, passB)) {
			autoPass();
		}
		// ��������ť
		if (buttonFlash(e, state == GAME && toRob, robB)) {
			serverPrinter.println("Y");
			isRob = 2;
			toRob = false;
			point *= 2;
			timeTik3.stopTik();
		}
		// ������ť
		if (buttonFlash(e, state == GAME && toRob, noRobB)) {
			autoPass();
		}
		// �йܰ�ť
		if (buttonFlash(e, state == GAME && (toRob || toDo), autoB)) {
			autoPass();
			toPass = true;
		}
		// ȡ���й�
		if (buttonFlash(e, state == GAME && toPass, cancelAutoB)) {
			toPass = false;
		}
	}

	/**
	 * ���ƶ����ʱ,��ʾ��ť�Ķ���Ч��,�������ʱ�ж��Ƿ��ڸð�ť��Χ��.
	 * 
	 * @param e
	 *            ���ָ��
	 * @param flag
	 *            �ж�����
	 * @param b
	 *            ��ť����
	 * @return ������,�ж��Ƿ��ڸð�ť��Χ��
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
		// ���ְ�ť����Ч��
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
