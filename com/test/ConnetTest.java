package com.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import onenet.edp.ConnectMsg;
import onenet.edp.ConnectRespMsg;
import onenet.edp.EdpKit;
import onenet.edp.EdpMsg;
import onenet.edp.Common.MsgType;

public class ConnetTest {
	
	/**
	 * byte数组转换转16进制字符串
	 * @param array
	 * @return hex string. if array is null, return null.
	 */
	public static String byteArrayToString(byte[] array)
	{
		if (array == null)
		{
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++)
		{
			String hex = Integer.toHexString(array[i] & 0xff);
			if (hex.length() == 1)
			{
				sb.append("0" + hex);
			}
			else
			{
				sb.append(hex);
			}
		}
		
		return sb.toString();
	}

	/**
	 * 打印日志到控制台
	 * @param info log information
	 */
	public static void log(Object info) {
		System.out.println(info);
	}

	public static void main(String[] args) throws IOException, InterruptedException{
		// TODO Auto-generated method stub
		//test sdk
				//与服务器建立socket连接
				//online
				String serverIp = "192.168.0.104";   //
				int serverPort = 876;
				Socket socket = new Socket(serverIp, serverPort);
				socket.setSoTimeout(60 * 1000);		//设置超时时长为一分钟
				InputStream inStream = socket.getInputStream();
				OutputStream outStream = socket.getOutputStream();
				
				//向服务器发送连接请求
				int devId = 621074839;		   //***用户请使用自己的设备ID***
				//int proId = 367884;
				String devKey = "6o3gZHKW8F2SYEcVEp0WYXI3eCw=";	//***用户请使用自己的设备的鉴权key***
				ConnectMsg connectMsg = new ConnectMsg();
				byte[] packet = connectMsg.packMsg(devId,devKey);
				outStream.write(packet);
				log("[connect]packet size:" + packet.length);
				log("[connect]packet:" + byteArrayToString(packet));
				
				Thread.sleep(500);
				
				//接收服务器的连接响应
				EdpKit kit =new EdpKit();		//初始化一个EdpKit实例，用于服务器响应包的数据解析
				byte[] readBuffer = new byte[1024];	//接收数据的缓存池
				int readSize = inStream.read(readBuffer);
				if (readSize > 0) {
					byte[] recvPacket = new byte[readSize];
					System.arraycopy(readBuffer, 0, recvPacket, 0, readSize);
					List<EdpMsg> msgs = kit.unpack(recvPacket);
					if (msgs == null || msgs.size() > 1) {
						log("[connect responce]receive packet exception.");
					}
					else {
						EdpMsg msg = msgs.get(0);
						if (msg.getMsgType() == MsgType.CONNRESP) {
							ConnectRespMsg connectRespMsg = (ConnectRespMsg) msg;
							log("[connect responce] res_code:" + connectRespMsg.getResCode());
						}
						else {
							log("[connect responce]responce packet is not connect responce.type:"+ msg.getMsgType());
						}
					}
				}
				
				//关闭socket连接
				socket.close();
				inStream.close();
				outStream.close();
		

	}

}
