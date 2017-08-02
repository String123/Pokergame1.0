package com.string.pokergame.client;

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
}
