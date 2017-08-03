package com.string.pokergame.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;

/**
 * ��װһ�������ڵ����Ժͷ���,һ������һ����,�����ͻ��˵�IO�������� ����,����,������,׼���ȷ���
 * 
 */
public class Room implements Runnable {
	Player[] p = new Player[] { null, null, null };
	private int pNum = 0, ready = 0, over = 0;
	private int robLord = 0;
	private int[] totalCardsIndex = new int[54];
	private boolean putCards = true;

	// ���췿��ʱ˳�㽫��ϴ��
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
	 * ��ӽ��뷿��Ŀͻ��˵���Ϣ����Ӧ����Ҷ���
	 * 
	 * @param name
	 *            �������
	 * @param br
	 *            �ͻ���������
	 * @param pw
	 *            �ͻ��������
	 * @return ����ڿͻ��˵������±�
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
	 * ��ʼ���Ƶ��±��
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
	 * �������Ƹ���ǰ�ͻ���
	 */
	public synchronized void giveLordCards(int id) {
		if (p[id] != null) {
			p[id].getPw().println(totalCardsIndex[0]);
			p[id].getPw().println(totalCardsIndex[1]);
			p[id].getPw().println(totalCardsIndex[2]);
		}
	}

	/**
	 * ��һ���Ƹ���ǰ���
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
	 * ����뿪��ǰ����
	 * 
	 * @param playerID
	 *            �뿪����ҵķ�����ID
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
	 * ���׼��
	 * 
	 * @param id
	 *            ��׼������ҵ�ID
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
	 * ����������
	 * 
	 * @param id
	 *            ��ǰ���������
	 * @return �������������,�������-1��û��������
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
					// �ж��������Ƿ��Ѿ�����������,������ֱ�ӷ��������ҵ���
					if ((!(next[(index + 1) % 3] || next[(index + 2) % 3])) && isRob[index % 3] == 1) {
						for (int i = 0; i < p.length; i++) {
							p[i].getPw().println("LORD");
							p[i].getPw().println(index % 3);
						}
						return index % 3;
					}
					// �����������ź�,���տͻ��˷�������������Ϣ */
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
					// �ж��Ƿ��Ѿ��������ε���,��Ϊ��������������ҵ���
					if (isRob[index % 3] == 2
							|| ((!(next[(index + 1) % 3] || next[(index + 2) % 3])) && isRob[index % 3] == 1)) {
						for (int i = 0; i < p.length; i++) {
							p[i].getPw().println("LORD");
							p[i].getPw().println(index % 3);
						}
						return index % 3;
					}
				} // �������ֱ������
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
	 * ���ƹ���
	 * 
	 * @param id
	 *            ��ǰ������ID
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
				// ����"�ĸ��ͻ��˳���"��Ϣ�����пͻ���
				for (int i = 0; i < p.length; i++) {
					p[i].getPw().println("WHODO");
					p[i].getPw().println(index % 3);
				}
				msg = p[index % 3].getBr().readLine();
				// ����ͻ��˷�������Ϣ��SHOW��ΪҪ����,��������ת������Ŀͻ���
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
				} // ������յ�PASS��Ϣ��Ϊ����Ҳ�����,�ͽ��ϼҵĳ������ͳ������ʹ��ݸ������ͻ���
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
				System.out.println(GameServer.sdf.format(new Date()) + "һ̨�ͻ����ڴ���ʱ������!");
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
				System.out.println("room�߳̿�ʼ��");
				System.out.println(Thread.currentThread().getState());
				Thread.currentThread().wait();
				while (ready < 3) {
					Thread.sleep(500);
				}
				// TODO �ѷ����Ϊ�����߳���ִ����Ϸ����,��ֹ�Կͻ����߳�������Ϸ����ʱ���ߵ���ȫ���뿪��Ϸ
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
