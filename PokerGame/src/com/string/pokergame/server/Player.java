package com.string.pokergame.server;

import java.io.BufferedReader;
import java.io.PrintWriter;
/**
 * ��װ�������˵ķ����ڵ���ҵ�����
 * ��ҿͻ���IO��,��ҵ�����,����ڷ����ڵ�׼��״̬
 *
 */
public class Player {
	BufferedReader br;
	PrintWriter pw;
	String name;
	//����Ƿ����ڷ�����׼��,���ڵ�������ҽ���ʱ,����������Ҹ���ҵ�׼��״̬
	//0Ϊδ׼��״̬,1Ϊ��׼��״̬
	int isReady=0;
	
	public Player(String name,BufferedReader br,PrintWriter pw){
		this.name=name;
		this.br=br;
		this.pw=pw;
	}
}
