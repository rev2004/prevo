import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

class Audio_Server2 implements Runnable {
	HashMap<InetAddress, String> clientList = null;
	Socket tSocket = null;
	ServerSocket tServerSocket = null;

	DatagramSocket socket = null;
	DatagramPacket packet = null;
	DatagramPacket sPacket = null;
	ArrayList<InetAddress> address = null;
	byte[] buf = null;

	// 릴레이 서버
	public Audio_Server2() {
		try {
			tServerSocket = new ServerSocket(2048);
			clientList = new HashMap<InetAddress, String>();
			while (true) {
				tSocket = tServerSocket.accept();

				System.out.println("들어온 IP : " + tSocket.getInetAddress());

				clientList.put(tSocket.getInetAddress(), "client");
				
				System.out.println(clientList);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 패킷전송 부분
	public void run() {
		try {
			buf = new byte[1024];
			socket = new DatagramSocket(2048);
			while (true) {
				packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				for (int i = 0; i < clientList.size(); i++) {
					if (!packet.getAddress().equals(clientList.get(i))) {
						sPacket = new DatagramPacket(buf, buf.length);
						socket.send(sPacket);
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	public static void main(String[] args) {
//		new Thread(new Audio_Server2()).start();
//	}
}
