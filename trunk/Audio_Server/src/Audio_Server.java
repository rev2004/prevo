import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

class Audio_Server implements Runnable {
	ArrayList<InetAddress> clientList = null;
	HashMap<String, InetAddress> clientHash = null;
	Socket tSocket = null;
	ServerSocket tServerSocket = null;

	DatagramSocket socket = null;
	DatagramPacket packet = null;
	DatagramPacket sPacket = null;
	ArrayList<InetAddress> address = null;
	byte[] buf = null;

	// 릴레이 서버
	public Audio_Server() {
		try {
			tServerSocket = new ServerSocket(2048);
			clientList = new ArrayList<InetAddress>();
			clientHash = new HashMap<String, InetAddress>();
			while (true) {
				tSocket = tServerSocket.accept();

				System.out.println("들어온 IP : " + tSocket.getInetAddress());

				// 처음에 IP한번 저장
				if (clientList.isEmpty()) {
					System.out.println("Empty일때");
					clientList.add(tSocket.getInetAddress());
				}
				// 두번째 IP 처리
				else if (clientList.size() == 1) {
					System.out.println("size가 1일때");
					for (int i = 0; i < clientList.size(); i++) {
						if (!clientList.get(i).equals(tSocket.getInetAddress())) {
							clientList.add(tSocket.getInetAddress());
						}
					}
				}
				// 서로 다른 2개이상 IP가 들어왔을때 처리
				else {
					System.out.println("size가 2이상일때");
					for (int i = 0; i < clientList.size(); i++) {
						for (int j = 0; j < clientList.size(); j++) {
							if (clientList.get(j).equals(
									tSocket.getInetAddress()))
								continue;
						}
					}
				}
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
						sPacket = new DatagramPacket(buf, buf.length,
								clientList.get(i), 2048);
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
//		new Thread(new Audio_Server()).start();
//	}
}
