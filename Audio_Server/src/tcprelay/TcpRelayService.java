package tcprelay;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpRelayService {
	private static final int DEFAULT_PORT = 12345;

	private static final String DEFAULT_TARGET_SERVER = "www.naver.com";
	private static final int DEFAULT_TARGET_PORT = 80;

	private final String targetServer;
	private final int targetPort;

	private boolean running = false;

	private final ExecutorService executorService = Executors
			.newFixedThreadPool(100);

	public TcpRelayService() {
		this(DEFAULT_TARGET_SERVER, DEFAULT_TARGET_PORT);
	}

	public TcpRelayService(String targetServer, int targetPort) {
		this.targetServer = targetServer;
		this.targetPort = targetPort;

		System.out.println("Target server: " + targetServer);
		System.out.println("Target port: " + targetPort);
	}

	public void start() {
		System.out.println("TCP relay service is starting.");
		running = true;

		ServerSocket serverSocket = null;
		Socket sourceSocket = null;
		Socket targetSocket = null;
		try {
			serverSocket = new ServerSocket(DEFAULT_PORT);
			System.out.println("Sever socket is ready.");
			while (running) {
				sourceSocket = serverSocket.accept();
				System.out.println();
				targetSocket = new Socket(targetServer, targetPort);
				TcpRelayWorker worker = new TcpRelayWorker(sourceSocket,
						targetSocket);
				executorService.submit(worker);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (sourceSocket != null) {
				try {
					sourceSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (targetSocket != null) {
				try {
					targetSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void stop() {
		running = false;
	}

	public static void main(String[] args) {
		TcpRelayService tcpRelayService = null;
		switch (args.length) {
		case 0:
			tcpRelayService = new TcpRelayService();
			break;

		case 2:
			String targetServer = args[0];
			int targetPort = Integer.parseInt(args[1]);
			tcpRelayService = new TcpRelayService(targetServer, targetPort);
			break;

		default:
			System.out.println("java "
					+ TcpRelayService.class.getCanonicalName()
					+ " <target server> <target port>");
			System.exit(1);
		}

		tcpRelayService.start();
	}
}