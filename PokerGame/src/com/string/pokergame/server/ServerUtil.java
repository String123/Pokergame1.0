package com.string.pokergame.server;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 服务器端工具类，获取服务器端IP地址
 *
 */
public class ServerUtil {
	/**
	 * 精准的获取本机的IP地址
	 * @return
	 * @throws SocketException
	 */
	public static String getLocalInetAddress() throws SocketException{
		Enumeration<NetworkInterface> netEnum = NetworkInterface.getNetworkInterfaces();//获取所有的网络接口
		InetAddress ip = null;
		while(netEnum.hasMoreElements()){//遍历IP地址集合
			NetworkInterface net = netEnum.nextElement();
			Enumeration<InetAddress> addrEnum = net.getInetAddresses();//遍历每一个网络接口中的ip地址
			while(addrEnum.hasMoreElements()){
				InetAddress addr = addrEnum.nextElement();
				if(!addr.isLoopbackAddress() && (addr instanceof Inet4Address)){//不是本地回环地址，同时是IPV4地址，则说明这个是本机地址
					ip = addr;
				}
			}
		}
		return ip.toString().substring(1);
	}
}
