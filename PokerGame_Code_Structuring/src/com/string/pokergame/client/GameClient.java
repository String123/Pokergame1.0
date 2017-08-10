package com.string.pokergame.client;

import com.string.pokergame.client.handler.FrameHandler;
import com.string.pokergame.client.handler.KeyHandler;
import com.string.pokergame.client.handler.MouseHandler;

public class GameClient {
	public FrameHandler frame;
	public MouseHandler m;
	public KeyHandler k;
	/**
	 * 游戏主方法
	 */
	public void action() {
		frame = new FrameHandler();
		m = new MouseHandler(frame);
		frame.addMouseMotionListener(m);
		frame.addMouseListener(m);

		k = new KeyHandler();
		frame.addKeyListener(k);
		frame.setFocusable(true);
		frame.requestFocus();
	}
	
	public static void main(String[] args) {
		new GameClient().action();
	}
}
