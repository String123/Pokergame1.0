package com.string.pokergame.server;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * �������˹����࣬��ȡ��������IP��ַ
 *
 */
public class ServerUtil {
	/**
	 * ��׼�Ļ�ȡ������IP��ַ
	 * @return
	 * @throws SocketException
	 */
	public static String getLocalInetAddress() throws SocketException{
		Enumeration<NetworkInterface> netEnum = NetworkInterface.getNetworkInterfaces();//��ȡ���е�����ӿ�
		InetAddress ip = null;
		while(netEnum.hasMoreElements()){//����IP��ַ����
			NetworkInterface net = netEnum.nextElement();
			Enumeration<InetAddress> addrEnum = net.getInetAddresses();//����ÿһ������ӿ��е�ip��ַ
			while(addrEnum.hasMoreElements()){
				InetAddress addr = addrEnum.nextElement();
				if(!addr.isLoopbackAddress() && (addr instanceof Inet4Address)){//���Ǳ��ػػ���ַ��ͬʱ��IPV4��ַ����˵������Ǳ�����ַ
					ip = addr;
				}
			}
		}
		return ip.toString().substring(1);
	}
}
