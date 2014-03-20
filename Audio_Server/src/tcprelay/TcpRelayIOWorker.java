package tcprelay;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TcpRelayIOWorker implements Runnable {
	private static final int DEFAULT_BUFFER_SIZE = 1024;

	public enum IOWorkerType {
		INBOUND, OUTBOUND
	}

	private final IOWorkerType type;
	private final InputStream is;
	private final OutputStream os;

	public TcpRelayIOWorker(IOWorkerType type, InputStream is, OutputStream os) {
		this.type = type;
		this.is = is;
		this.os = os;
	}

	@Override
	public void run() {
		try {
			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			int readBytes;
			while ((readBytes = is.read(buffer)) != -1) {
				System.out.println(type + ":\n" + new String(buffer));
				os.write(buffer, 0, readBytes);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}