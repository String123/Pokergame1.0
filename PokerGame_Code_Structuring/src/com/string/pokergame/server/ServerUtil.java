package com.string.pokergame.server;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 封装查找IP地址的工具
 *
 */
public class ServerUtil {
	/**
	 * 封装查找IP地址的工具
	 * @return IP地址
	 * @throws SocketException
	 */
	public static String getLocalInetAddress() throws SocketException{
		Enumeration<NetworkInterface> netEnum = NetworkInterface.getNetworkInterfaces();
		InetAddress ip = null;
		while(netEnum.hasMoreElements()){
			NetworkInterface net = netEnum.nextElement();
			Enumeration<InetAddress> addrEnum = net.getInetAddresses();
			while(addrEnum.hasMoreElements()){
				InetAddress addr = addrEnum.nextElement();
				if(!addr.isLoopbackAddress() && (addr instanceof Inet4Address)){
					ip = addr;
				}
			}
		}
		return ip.toString().substring(1);
	}
}
