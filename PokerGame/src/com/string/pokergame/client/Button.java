package com.string.pokergame.client;

import java.awt.image.BufferedImage;
/**
 * ��װ��ť�����Ժ�ͼƬ
 *
 */
public class Button {
	int x, y;
	int width, height;
	BufferedImage image;

	public Button(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.image = GameClient.button1;
	}

}
