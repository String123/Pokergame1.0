package com.string.pokergame.client.handler;

import java.awt.image.BufferedImage;
/**
 * 封装卡牌处理的工具方法,卡牌交换,卡牌排序,牌型判断等.
 *
 */
public class CardsUtil {
	
	public static final int BOMB = 6;
	public static final int KINGBOMB = 9;
	/**
	 * 交换两个卡牌对象的数值,图片和Y坐标
	 * 
	 * @param card1
	 * @param card2
	 */
	public static void swapCards(Card card1, Card card2) {
		int temp;
		BufferedImage tempImage;
		// 交换两个对象的卡牌数值
		temp = card1.num;
		card1.num = card2.num;
		card2.num = temp;
		// 交换两个对象的卡牌Y位置
		temp = card1.y;
		card1.y = card2.y;
		card2.y = temp;
		// 交换两个对象的卡牌图片
		tempImage = card1.image;
		card1.image = card2.image;
		card2.image = tempImage;
		// 交换两个对象的总卡组下标
		temp = card1.cardIndex;
		card1.cardIndex = card2.cardIndex;
		card2.cardIndex = temp;
	}

	/**
	 * 牌数值的排序,只交换图片和数值
	 * 
	 * @param cards
	 *            需要排序的卡牌组
	 */
	public static void cardSort(Card[] cards) {
		for (int i = 0; i < cards.length; i++) {
			for (int j = 0; j < cards.length - i - 1; j++) {
				if (cards[j].num < cards[j + 1].num) {
					swapCards(cards[j], cards[j + 1]);
				}
			}
		}
	}
	
	/**
	 * 检查要出的牌是否符合牌型规则
	 * 
	 * @param cards
	 * @return
	 */
	public static int checkCards(Card[] cards) {
		int b = pairs(cards) + threesWithOne(cards) + threes(cards) + straight(cards) + threesWithPairs(cards)
				+ bomb(cards) + kingBomb(cards) + single(cards) + bombWithTwo(cards) + bombWithPair(cards);
		return b;
	}

	/**
	 * 检测是否为单牌
	 * 
	 * @param cards
	 * @return 0或1
	 */
	private static int single(Card[] cards) {
		if (cards.length == 1) {
			return 1;
		}
		return 0;
	}

	/**
	 * 检测是否为顺子
	 * 
	 * @param cards
	 * @return 0或12
	 */
	private static int straight(Card[] cards) {
		int b = 12;
		// 如果小于5张则不是顺子
		if (cards.length < 5) {
			b = 0;
			return b;
		}
		// 比较相邻两张是否为差一顺序,如果有一对相邻的不差一则不是顺子
		for (int i = 0; i < cards.length - 1; i++) {
			if (cards[i].num - cards[i + 1].num != 1) {
				b = 0;
				return b;
			}
		}
		return b;
	}

	/**
	 * 检测是否为对子或对顺
	 * 
	 * @param cards
	 * @return 0或2
	 */
	private static int pairs(Card[] cards) {
		int b = 2;
		// 如果张数少于5张,或者不为2的倍数,并且不为2张,则不是对子或者对顺
		if ((cards.length < 5 || cards.length % 2 != 0) && cards.length != 2) {
			b = 0;
			return b;
		} // 如果为两张但是两张不一样则不是对子
		else if (cards.length == 2 && cards[0].num != cards[1].num) {
			b = 0;
			return b;
		}
		// 两张以上的,判断以两个为跳步的相邻两张是否相等,并且每对是否相差一
		for (int i = 0; i < cards.length - 2; i += 2) {
			if (cards[i].num != cards[i + 1].num || cards[i].num - cards[i + 2].num != 1) {
				b = 0;
				break;
			}
		}
		// 判断最后两张是否相等
		if (cards[cards.length - 2].num != cards[cards.length - 1].num) {
			b = 0;
		}
		return b;
	}

	/**
	 * 检测是否为三张或三顺
	 * 
	 * @param cards
	 * @return 0或3
	 */
	private static int threes(Card[] cards) {
		int b = 3;
		// 如果长度不是三的倍数则不是三张
		if (cards.length % 3 != 0) {
			b = 0;
			return b;
		}
		// 如果长度为三,则判断三张是否相等,不等则不是三张
		if (cards.length == 3 && cards[0].num == cards[1].num && cards[0].num == cards[2].num) {
			return b;
		} else if (cards.length == 3) {
			b = 0;
			return b;
		}
		// 多张用3作为跳步,比较相邻三张是否相等,并且每对三张是否相差一
		for (int i = 0; i < cards.length - 3; i += 3) {
			if (cards[i].num != cards[i + 1].num || cards[i].num != cards[i + 2].num
					|| cards[i].num - cards[i + 3].num != 1) {
				b = 0;
				break;
			}
		}
		// 检查最后三张是否相等
		if (cards[cards.length - 3].num != cards[cards.length - 2].num
				|| cards[cards.length - 3].num != cards[cards.length - 1].num) {
			b = 0;
		}
		return b;
	}

	/**
	 * 检测是否为三带一或三顺带多一
	 * 
	 * @param cards
	 * @return 0或4
	 */
	private static int threesWithOne(Card[] cards) {
		int b = 4;
		// 如果长度不是4的倍数则不是三带一
		if (cards.length % 4 != 0 || pairs(cards) == 2 || straight(cards) == 12) {
			b = 0;
			return b;
		}
		// 计算有多少组三带一
		int x = cards.length / 4;
		// 将该三带一进行前三后一的排序
		b = judgeSort(cards, 2, x, 1, 4);
		// 如果只有一组,判断如果是四张相同就不是三带一
		if (x == 1 && cards[0].num == cards[3].num) {
			b = 0;
			return b;
		}
		// 将三张组分离出来进行三顺判断
		Card[] three = new Card[x * 3];
		for (int i = 0; i < x * 3; i++) {
			three[i] = new Card();
			three[i].image = cards[i].image;
			three[i].num = cards[i].num;
		}
		b = threes(three);
		return b;
	}

	/**
	 * 检测是否为三带对或飞机
	 * 
	 * @param cards
	 * @return 0或5
	 */
	private static int threesWithPairs(Card[] cards) {
		int b = 5;
		if (cards.length % 5 != 0 || straight(cards) == 12) {
			b = 0;
			return b;
		} else if (cards[0].num != cards[1].num || pairs(cards) == 2) {
			b = 0;
			return b;
		}

		// 计算有多少组三带对
		int x = cards.length / 4;
		// 将该三带对进行前三后对的排序
		b = judgeSort(cards, 2, x, 2, 5);
		// 将三张组和对子组分离出来进行三顺和对子判断
		Card[] three = new Card[x * 3];
		for (int i = 0; i < x * 3; i++) {
			three[i] = new Card();
			three[i].image = cards[i].image;
			three[i].num = cards[i].num;
		}
		Card[] pair = new Card[x * 2];
		for (int i = x * 3; i < cards.length; i++) {
			pair[i - x * 3] = new Card();
			pair[i - x * 3].image = cards[i].image;
			pair[i - x * 3].num = cards[i].num;
		}
		// 判断三顺部分
		if (threes(three) == 0) {
			b = 0;
			return b;
		}
		// 多对部分如果为两张
		if (pair.length == 2 && pair[0].num != pair[1].num) {
			b = 0;
			return b;
		} else if (pair.length > 2) {
			// 两张以上的,判断以两个为跳步的相邻两张是否相等
			for (int i = 0; i < pair.length - 1; i += 2) {
				if (pair[i].num != pair[i + 1].num) {
					b = 0;
					break;
				}
			}
		}
		return b;
	}

	/**
	 * 检测是否为炸弹
	 * 
	 * @param cards
	 * @return 0或6
	 */
	private static int bomb(Card[] cards) {
		// 如果是4张并且四张数值相等则为炸弹
		if (cards.length == 4 && cards[0].num == cards[1].num && cards[0].num == cards[2].num
				&& cards[0].num == cards[3].num) {
			return 6;
		}
		return 0;
	}

	/**
	 * 检测是否为炸弹带二单
	 * 
	 * @param cards
	 * @return 0或7
	 */
	private static int bombWithTwo(Card[] cards) {
		int b = 7;
		if (cards.length != 6 || pairs(cards) == 2 || threes(cards) == 3) {
			b = 0;
			return b;
		} else if (cards[2].num != cards[3].num) {
			b = 0;
			return b;
		}
		// 将该四带二进行前四后二的排序
		b = judgeSort(cards, 3, 2, 1, 7);

		// 检查前四张牌是否为炸弹
		for (int i = 0; i < 3; i++) {
			if (cards[i].num != cards[i + 1].num) {
				b = 0;
				return 0;
			}
		}
		return 7;
	}

	/**
	 * 检测是否为炸弹带两对
	 * 
	 * @param cards
	 * @return 0或8
	 */
	private static int bombWithPair(Card[] cards) {
		int b = 8;
		if (cards.length != 8 || pairs(cards) == 2 || straight(cards) == 12) {
			b = 0;
			return b;
		}
		// 将该牌组进行前四后二的排序
		b = judgeSort(cards, 3, 2, 2, 8);
		if (b == 0) {
			return b;
		}

		// 检查前四张牌是否为炸弹
		for (int i = 0; i < 3; i++) {
			if (cards[i].num != cards[i + 1].num) {
				b = 0;
				return 0;
			}
		}
		// 检查后四张牌是否为对子
		for (int i = 4; i < cards.length - 1; i += 2) {
			if (cards[i].num != cards[i + 1].num) {
				b = 0;
				return 0;
			}
		}
		return b;
	}

	/**
	 * 判断是否为王炸
	 * 
	 * @param cards
	 * @return 0或9
	 */
	private static int kingBomb(Card[] cards) {
		if (cards.length == 2 && cards[0].num == 19 && cards[1].num == 18) {
			return 9;
		}
		return 0;
	}

	/**
	 * 比较第1张和第m+1张牌,如果不相等,就将n张牌移到最后,如果超过x次则返回0,未超过则返回t
	 * 
	 * @param cards
	 * @param m
	 * @param x
	 * @param n
	 * @param t
	 * @return
	 */
	private static int judgeSort(Card[] cards, int m, int x, int n, int t) {
		if (cards[0].num != cards[m].num) {
			int index = 0;
			while (cards[0].num != cards[m].num) {
				if (index > x) {
					return 0;
				}
				index++;
				for (int i = 0; i < n; i++) {
					for (int j = 0; j < cards.length - 1; j++) {
						swapCards(cards[j], cards[j + 1]);
					}
				}
			}
		}
		return t;
	}

}
