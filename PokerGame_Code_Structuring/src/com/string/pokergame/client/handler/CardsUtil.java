package com.string.pokergame.client.handler;

import java.awt.image.BufferedImage;
/**
 * ��װ���ƴ���Ĺ��߷���,���ƽ���,��������,�����жϵ�.
 *
 */
public class CardsUtil {
	
	public static final int BOMB = 6;
	public static final int KINGBOMB = 9;
	/**
	 * �����������ƶ������ֵ,ͼƬ��Y����
	 * 
	 * @param card1
	 * @param card2
	 */
	public static void swapCards(Card card1, Card card2) {
		int temp;
		BufferedImage tempImage;
		// ������������Ŀ�����ֵ
		temp = card1.num;
		card1.num = card2.num;
		card2.num = temp;
		// ������������Ŀ���Yλ��
		temp = card1.y;
		card1.y = card2.y;
		card2.y = temp;
		// ������������Ŀ���ͼƬ
		tempImage = card1.image;
		card1.image = card2.image;
		card2.image = tempImage;
		// ��������������ܿ����±�
		temp = card1.cardIndex;
		card1.cardIndex = card2.cardIndex;
		card2.cardIndex = temp;
	}

	/**
	 * ����ֵ������,ֻ����ͼƬ����ֵ
	 * 
	 * @param cards
	 *            ��Ҫ����Ŀ�����
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
	 * ���Ҫ�������Ƿ�������͹���
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
	 * ����Ƿ�Ϊ����
	 * 
	 * @param cards
	 * @return 0��1
	 */
	private static int single(Card[] cards) {
		if (cards.length == 1) {
			return 1;
		}
		return 0;
	}

	/**
	 * ����Ƿ�Ϊ˳��
	 * 
	 * @param cards
	 * @return 0��12
	 */
	private static int straight(Card[] cards) {
		int b = 12;
		// ���С��5������˳��
		if (cards.length < 5) {
			b = 0;
			return b;
		}
		// �Ƚ����������Ƿ�Ϊ��һ˳��,�����һ�����ڵĲ���һ����˳��
		for (int i = 0; i < cards.length - 1; i++) {
			if (cards[i].num - cards[i + 1].num != 1) {
				b = 0;
				return b;
			}
		}
		return b;
	}

	/**
	 * ����Ƿ�Ϊ���ӻ��˳
	 * 
	 * @param cards
	 * @return 0��2
	 */
	private static int pairs(Card[] cards) {
		int b = 2;
		// �����������5��,���߲�Ϊ2�ı���,���Ҳ�Ϊ2��,���Ƕ��ӻ��߶�˳
		if ((cards.length < 5 || cards.length % 2 != 0) && cards.length != 2) {
			b = 0;
			return b;
		} // ���Ϊ���ŵ������Ų�һ�����Ƕ���
		else if (cards.length == 2 && cards[0].num != cards[1].num) {
			b = 0;
			return b;
		}
		// �������ϵ�,�ж�������Ϊ���������������Ƿ����,����ÿ���Ƿ����һ
		for (int i = 0; i < cards.length - 2; i += 2) {
			if (cards[i].num != cards[i + 1].num || cards[i].num - cards[i + 2].num != 1) {
				b = 0;
				break;
			}
		}
		// �ж���������Ƿ����
		if (cards[cards.length - 2].num != cards[cards.length - 1].num) {
			b = 0;
		}
		return b;
	}

	/**
	 * ����Ƿ�Ϊ���Ż���˳
	 * 
	 * @param cards
	 * @return 0��3
	 */
	private static int threes(Card[] cards) {
		int b = 3;
		// ������Ȳ������ı�����������
		if (cards.length % 3 != 0) {
			b = 0;
			return b;
		}
		// �������Ϊ��,���ж������Ƿ����,������������
		if (cards.length == 3 && cards[0].num == cards[1].num && cards[0].num == cards[2].num) {
			return b;
		} else if (cards.length == 3) {
			b = 0;
			return b;
		}
		// ������3��Ϊ����,�Ƚ����������Ƿ����,����ÿ�������Ƿ����һ
		for (int i = 0; i < cards.length - 3; i += 3) {
			if (cards[i].num != cards[i + 1].num || cards[i].num != cards[i + 2].num
					|| cards[i].num - cards[i + 3].num != 1) {
				b = 0;
				break;
			}
		}
		// �����������Ƿ����
		if (cards[cards.length - 3].num != cards[cards.length - 2].num
				|| cards[cards.length - 3].num != cards[cards.length - 1].num) {
			b = 0;
		}
		return b;
	}

	/**
	 * ����Ƿ�Ϊ����һ����˳����һ
	 * 
	 * @param cards
	 * @return 0��4
	 */
	private static int threesWithOne(Card[] cards) {
		int b = 4;
		// ������Ȳ���4�ı�����������һ
		if (cards.length % 4 != 0 || pairs(cards) == 2 || straight(cards) == 12) {
			b = 0;
			return b;
		}
		// �����ж���������һ
		int x = cards.length / 4;
		// ��������һ����ǰ����һ������
		b = judgeSort(cards, 2, x, 1, 4);
		// ���ֻ��һ��,�ж������������ͬ�Ͳ�������һ
		if (x == 1 && cards[0].num == cards[3].num) {
			b = 0;
			return b;
		}
		// ��������������������˳�ж�
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
	 * ����Ƿ�Ϊ�����Ի�ɻ�
	 * 
	 * @param cards
	 * @return 0��5
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

		// �����ж�����������
		int x = cards.length / 4;
		// ���������Խ���ǰ����Ե�����
		b = judgeSort(cards, 2, x, 2, 5);
		// ��������Ͷ�����������������˳�Ͷ����ж�
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
		// �ж���˳����
		if (threes(three) == 0) {
			b = 0;
			return b;
		}
		// ��Բ������Ϊ����
		if (pair.length == 2 && pair[0].num != pair[1].num) {
			b = 0;
			return b;
		} else if (pair.length > 2) {
			// �������ϵ�,�ж�������Ϊ���������������Ƿ����
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
	 * ����Ƿ�Ϊը��
	 * 
	 * @param cards
	 * @return 0��6
	 */
	private static int bomb(Card[] cards) {
		// �����4�Ų���������ֵ�����Ϊը��
		if (cards.length == 4 && cards[0].num == cards[1].num && cards[0].num == cards[2].num
				&& cards[0].num == cards[3].num) {
			return 6;
		}
		return 0;
	}

	/**
	 * ����Ƿ�Ϊը��������
	 * 
	 * @param cards
	 * @return 0��7
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
		// �����Ĵ�������ǰ�ĺ��������
		b = judgeSort(cards, 3, 2, 1, 7);

		// ���ǰ�������Ƿ�Ϊը��
		for (int i = 0; i < 3; i++) {
			if (cards[i].num != cards[i + 1].num) {
				b = 0;
				return 0;
			}
		}
		return 7;
	}

	/**
	 * ����Ƿ�Ϊը��������
	 * 
	 * @param cards
	 * @return 0��8
	 */
	private static int bombWithPair(Card[] cards) {
		int b = 8;
		if (cards.length != 8 || pairs(cards) == 2 || straight(cards) == 12) {
			b = 0;
			return b;
		}
		// �����������ǰ�ĺ��������
		b = judgeSort(cards, 3, 2, 2, 8);
		if (b == 0) {
			return b;
		}

		// ���ǰ�������Ƿ�Ϊը��
		for (int i = 0; i < 3; i++) {
			if (cards[i].num != cards[i + 1].num) {
				b = 0;
				return 0;
			}
		}
		// �����������Ƿ�Ϊ����
		for (int i = 4; i < cards.length - 1; i += 2) {
			if (cards[i].num != cards[i + 1].num) {
				b = 0;
				return 0;
			}
		}
		return b;
	}

	/**
	 * �ж��Ƿ�Ϊ��ը
	 * 
	 * @param cards
	 * @return 0��9
	 */
	private static int kingBomb(Card[] cards) {
		if (cards.length == 2 && cards[0].num == 19 && cards[1].num == 18) {
			return 9;
		}
		return 0;
	}

	/**
	 * �Ƚϵ�1�ź͵�m+1����,��������,�ͽ�n�����Ƶ����,�������x���򷵻�0,δ�����򷵻�t
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
