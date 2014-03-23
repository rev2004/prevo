package com.example.recordingtest;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.Socket;

import android.util.Log;

public class TCPConnect implements Runnable {
	String filename;
	InetAddress addr;
	Socket socket;
	File f;
	FileInputStream fis;
	DataInputStream dis;
	BufferedOutputStream bos;

	public TCPConnect(String filename) {
		this.filename = filename;
	}

	@Override
	public void run() {
		try {
			addr = InetAddress.getByName("192.168.23.15");
			socket = new Socket(addr, 2048);

			int len = 0;
			byte[] buffer = new byte[4096];
			f = new File(filename);
			Log.e(filename, "Length : " + f.length());
			fis = new FileInputStream(f);
			dis = new DataInputStream(fis);
			bos = new BufferedOutputStream(socket.getOutputStream());

			while ((len = dis.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
