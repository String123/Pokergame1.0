package com.string.pokergame.client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
/**
 * 封装计时器类的属性并且定时变换秒数
 * @author String
 *
 */
public class TimerTik extends Thread{
	BufferedImage image;
	boolean flag=true;
	private int max = 0;
	int x, y;
	int width = 50;
	Graphics2D g2;
	Font f =new Font(null, Font.BOLD, 25);

	public void setMax(int max){
		this.max=max;
		this.max*=10;
		g2.setFont(f);
		g2.setColor(Color.BLACK);
		g2.drawString(String.valueOf(max/10)+"S", 25, 60);
	}
	
	public TimerTik(int x, int y) {
		this.x = x;
		this.y = y;
		image = GameClient.timer;
		g2 = image.createGraphics();
	}
	
	public void run() {
		flag=true;
		while (max-- > 0&&flag) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			g2.setColor(Color.WHITE);
			g2.fillRect(25, 35, 45, 30);
			g2.setFont(f);
			g2.setColor(Color.BLACK);
			if (max/10>=10){
				g2.drawString(String.valueOf(max/10)+"S", 25, 60);
			}else{
				g2.drawString(String.valueOf(max/10)+"S", 35, 60);
			}
		}
		System.out.println("Tik is over!");
		if (max<0){
			GameClient.autoPass();
		}
	}
}
