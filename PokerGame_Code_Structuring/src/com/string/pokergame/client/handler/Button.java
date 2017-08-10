package com.string.pokergame.client.handler;

import java.awt.image.BufferedImage;
/**
 * 封装按钮的属性和图片
 *
 */
public class Button {
	private int x, y;
	private int width, height;
	BufferedImage image;

	public Button(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.image = FrameHandler.button1;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
}
