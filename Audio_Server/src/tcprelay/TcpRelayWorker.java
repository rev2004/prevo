package tcprelay;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import tcprelay.TcpRelayIOWorker.IOWorkerType;

public class TcpRelayWorker implements Runnable {
	private final Socket sourceSocket;
	private final Socket targetSocket;

	public TcpRelayWorker(Socket sourceSocket, Socket targetSocket) {
		this.sourceSocket = sourceSocket;
		this.targetSocket = targetSocket;
	}

	@Override
	public void run() {
		InputStream sourceIs = null;
		OutputStream sourceOs = null;
		InputStream targetIs = null;
		OutputStream targetOs = null;
		try {
			sourceIs = sourceSocket.getInputStream();
			sourceOs = sourceSocket.getOutputStream();
			targetIs = targetSocket.getInputStream();
			targetOs = targetSocket.getOutputStream();

			Thread inboundWorker = new Thread(new TcpRelayIOWorker(
					IOWorkerType.INBOUND, sourceIs, targetOs));
			Thread outboundWorker = new Thread(new TcpRelayIOWorker(
					IOWorkerType.OUTBOUND, targetIs, sourceOs));

			inboundWorker.start();
			outboundWorker.start();

			inboundWorker.join();
			outboundWorker.join();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (sourceIs != null) {
				try {
					sourceIs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (sourceOs != null) {
				try {
					sourceOs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (sourceSocket != null) {
				try {
					sourceSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (targetIs != null) {
				try {
					targetIs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (targetOs != null) {
				try {
					targetOs.close();
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
		}
	}
}