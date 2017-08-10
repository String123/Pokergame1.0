package com.string.pokergame.client.handler;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
/**
 * 封装键盘响应事件
 *
 */
public class KeyHandler extends Handler implements KeyListener {

	private static final long serialVersionUID = 1L;

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_K) {
			key = key ? false : true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

}
