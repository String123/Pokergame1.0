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
	int pNum = 0, ready = 0, over = 0;
	int robLord = 0;
	int[] totalCardsIndex = new int[54];
	boolean putCards = true;

	// ���췿��ʱ˳�㽫��ϴ��
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
	 * ��ʼ���Ƶ��±��
	 */
	public void newTotal() {
		totalCardsIndex = new int[54];
		for (int i = 0; i < totalCardsIndex.length; i++) {
			totalCardsIndex[i] = i;
		}
	}

	/**
	 * �������Ƹ���ǰ�ͻ���
	 */
	public synchronized void giveLordCards(int id) {
		if (p[id] != null) {
			p[id].pw.println(totalCardsIndex[0]);
			p[id].pw.println(totalCardsIndex[1]);
			p[id].pw.println(totalCardsIndex[2]);
		}
	}

	/**
	 * ��һ���Ƹ���ǰ���
	 */
	public synchronized void giveCard(int id) {
		int tmp = (int) (Math.random() * totalCardsIndex.length);
		p[id].pw.println(totalCardsIndex[tmp]);
		totalCardsIndex[tmp] = totalCardsIndex[totalCardsIndex.length - 1];
		totalCardsIndex = Arrays.copyOf(totalCardsIndex,
				totalCardsIndex.length - 1);
	}

	/**
	 * ����뿪��ǰ����
	 * 
	 * @param playerID
	 *            �뿪����ҵķ�����ID
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
	 * ���׼��
	 * 
	 * @param id
	 *            ��׼������ҵ�ID
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
	 * ����������
	 * 
	 * @param id
	 *            ��ǰ���������
	 * @return �������������,�������-1��û��������
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
					// �ж��������Ƿ��Ѿ�����������,������ֱ�ӷ��������ҵ���
					if ((!(next[(index + 1) % 3] || next[(index + 2) % 3]))
							&& isRob[index % 3] == 1) {
						for (int i = 0; i < p.length; i++) {
							p[i].pw.println("LORD");
							p[i].pw.println(index % 3);
						}
						return index % 3;
					}
					// �����������ź�,���տͻ��˷�������������Ϣ */
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
					// �ж��Ƿ��Ѿ��������ε���,��Ϊ��������������ҵ���
					if (isRob[index % 3] == 2
							|| ((!(next[(index + 1) % 3] || next[(index + 2) % 3])) && isRob[index % 3] == 1)) {
						for (int i = 0; i < p.length; i++) {
							p[i].pw.println("LORD");
							p[i].pw.println(index % 3);
						}
						return index % 3;
					}
				}// �������ֱ������
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
	 * ���ƹ���
	 * 
	 * @param pNum
	 *            ��ǰ������ID
	 * @return ���ز�����,�ж��Ƿ����
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
					p[i].pw.println("WHODO");
					p[i].pw.println(index % 3);
				}
				msg = p[index % 3].br.readLine();
				// ����ͻ��˷�������Ϣ��SHOW��ΪҪ����,��������ת������Ŀͻ���
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
				}// ������յ�PASS��Ϣ��Ϊ����Ҳ�����,�ͽ��ϼҵĳ������ͳ������ʹ��ݸ������ͻ���
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
						+ "һ̨�ͻ���������!");
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
				// TODO �ѷ����Ϊ�����߳���ִ����Ϸ����,��ֹ�Կͻ����߳�������Ϸ����ʱ���ߵ���ȫ���뿪��Ϸ
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
