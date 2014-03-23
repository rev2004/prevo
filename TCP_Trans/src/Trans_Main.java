import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Trans_Main {
	Socket client;
	ServerSocket socket;

	File f;

	DataInputStream dis;
	FileOutputStream fos;
	BufferedOutputStream bos;

	public Trans_Main() {
		try {
			socket = new ServerSocket(2048);
			System.out.println("서버가 시작되었습니다.");

			client = socket.accept();
			System.out.println("클라이언트가 연결되었습니다.");

			System.out.println("음성을 파일 변환중입니다.");
			dis = new DataInputStream(client.getInputStream());

			f = new File("d:/test.mp4");
			fos = new FileOutputStream(f);
			bos = new BufferedOutputStream(fos);

			int len;
			int size = 4096;
			byte[] data = new byte[size];
			while ((len = dis.read(data)) != -1) {
				bos.write(data, 0, len);
			}

			bos.flush();
			bos.close();
			fos.close();
			dis.close();
			System.out.println("음성 수신 작업을 종료하였습니다.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Trans_Main();
	}
}
