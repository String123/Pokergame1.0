package com.string.pokergame.client;

import java.awt.image.BufferedImage;
/**
 * ��װÿ�ſ��Ƶ�Ȩֵ,ͼƬ��x,y����
 *
 */
public class Card {
	int x, y; // �Ƶ�λ��
	BufferedImage image; // �Ƶ�����ͼƬ
	int num; // �Ƶ���ֵ(1-13);
	int cardIndex; // �����ܿ�����±�

	public Card() {
		this(0, 0);
	}

	public Card(int x, int y) {
		this.x = x;
		this.y = y;
	}
}
