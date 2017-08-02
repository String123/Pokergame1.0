package com.string.pokergame.server;

import java.io.BufferedReader;
import java.io.PrintWriter;
/**
 * 封装服务器端的房间内的玩家的属性
 * 玩家客户端IO流,玩家的名字,玩家在房间内的准备状态
 *
 */
public class Player {
	BufferedReader br;
	PrintWriter pw;
	String name;
	//玩家是否已在房间内准备,用于当有新玩家进来时,反馈给新玩家该玩家的准备状态
	//0为未准备状态,1为已准备状态
	int isReady=0;
	
	public Player(String name,BufferedReader br,PrintWriter pw){
		this.name=name;
		this.br=br;
		this.pw=pw;
	}
}
