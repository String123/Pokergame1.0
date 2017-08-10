package com.string.pokergame.client.handler;

/**
 * 封装客户端房间内的另外两位玩家的属性
 *
 */
public class Player {
	int score = 0; //得分
	boolean isLord = false,isDo=false; // 是否为地主,是否正在出牌或抢地主
	boolean isPass =true;//是否为过
	boolean isWin = false;//是否赢了
	int ready = 0; //是否已准备
	int isRob=0; //是否抢地主.0为还未开始抢,1为不抢,2为抢
	int cardsNum, playerID; // 手牌数和房间内ID
	Card[] cards = new Card[0]; // 打出的牌
	String name; // 游戏中的昵称

	public Player(String name, int playerID,int isReady) {
		this.ready=isReady;
		this.name = name;
		this.playerID = playerID;
		cardsNum=0;
	}
}
