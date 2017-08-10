package com.string.pokergame.server;

import java.io.BufferedReader;
import java.io.PrintWriter;
/**
 * ��װ�������˵ķ����ڵ���ҵ�����
 * ��ҿͻ���IO��,��ҵ�����,����ڷ����ڵ�׼��״̬
 *
 */
public class Player {
	private BufferedReader br;
	private PrintWriter pw;
	private String name;
	//����Ƿ����ڷ�����׼��,���ڵ�������ҽ���ʱ,����������Ҹ���ҵ�׼��״̬
	//0Ϊδ׼��״̬,1Ϊ��׼��״̬
	private int isReady=0;
	
	public Player(String name,BufferedReader br,PrintWriter pw){
		this.name=name;
		this.br=br;
		this.pw=pw;
	}

	public BufferedReader getBr() {
		return br;
	}

	public PrintWriter getPw() {
		return pw;
	}

	public String getName() {
		return name;
	}

	public int getIsReady() {
		return isReady;
	}

	public void setIsReady(int isReady) {
		this.isReady = isReady;
	}
	
	
}
