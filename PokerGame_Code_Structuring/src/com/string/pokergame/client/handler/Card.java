package com.string.pokergame.client.handler;

import java.awt.image.BufferedImage;
/**
 * 封装每张卡牌的权值,图片和x,y坐标
 *
 */
public class Card {
	int x, y; // 牌的位置
	BufferedImage image; // 牌的正面图片
	int num; // 牌的数值(1-13);
	int cardIndex; // 卡在总卡组的下标

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
