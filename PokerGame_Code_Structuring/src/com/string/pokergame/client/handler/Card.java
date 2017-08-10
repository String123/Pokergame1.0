package com.string.pokergame.client.handler;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + num;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Card other = (Card) obj;
		if (num != other.num)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Card [num=" + num + "]";
	}
	
}
