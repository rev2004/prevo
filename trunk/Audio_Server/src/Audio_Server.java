import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

class Audio_Server implements Runnable {
	ArrayList<InetAddress> clientList = null;
	Socket tSocket = null;
	ServerSocket tServerSocket = null;

	DatagramSocket socket = null;
	DatagramPacket packet = null;
	ArrayList<InetAddress> address = null;
	byte[] buf = null;

	public Audio_Server() {
		try {
			tServerSocket = new ServerSocket(2048);
			clientList = new ArrayList<InetAddress>();
			while (true) {
				tSocket = tServerSocket.accept();
				clientList.add(tSocket.getInetAddress());
				System.out.println(clientList);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			buf = new byte[512];
			socket = new DatagramSocket(2048);
			while (true) {
				packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				socket.send(packet);
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Thread(new Audio_Server()).start();
	}
}
