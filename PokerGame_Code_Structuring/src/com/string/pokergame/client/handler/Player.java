package com.string.pokergame.client.handler;

/**
 * ��װ�ͻ��˷����ڵ�������λ��ҵ�����
 *
 */
public class Player {
	int score = 0; //�÷�
	boolean isLord = false,isDo=false; // �Ƿ�Ϊ����,�Ƿ����ڳ��ƻ�������
	boolean isPass =true;//�Ƿ�Ϊ��
	boolean isWin = false;//�Ƿ�Ӯ��
	int ready = 0; //�Ƿ���׼��
	int isRob=0; //�Ƿ�������.0Ϊ��δ��ʼ��,1Ϊ����,2Ϊ��
	int cardsNum, playerID; // �������ͷ�����ID
	Card[] cards = new Card[0]; // �������
	String name; // ��Ϸ�е��ǳ�

	public Player(String name, int playerID,int isReady) {
		this.ready=isReady;
		this.name = name;
		this.playerID = playerID;
		cardsNum=0;
	}
}
