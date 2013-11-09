package PPT_Server;

import java.awt.BorderLayout;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Server extends JFrame {
	JTextArea viewArea; // Text Output Area
	JTextField inputArea; // Text Input Area

	ServerSocket fServerSocket = null;
	Socket fSocket;

	public Server() {
		// 화면 디자인 코드
		setTitle("PreVo_PPT_Server (ver 1.0)");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// 채팅 출력창
		viewArea = new JTextArea();
		add(new JScrollPane(viewArea));

		// 채팅 입력창
		inputArea = new JTextField();
		inputArea.setEditable(false);
		add(inputArea, BorderLayout.SOUTH);
		setSize(300, 300);
		setVisible(true);

		try {
			fServerSocket = new ServerSocket(8432);

			while (true) {
				viewArea.append("요청 대기중...\n");
				// System.out.println("접속 대기중...");
				fSocket = fServerSocket.accept();
				FileRecive file = new FileRecive(fSocket);
				file.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fServerSocket.close();
			} catch (IOException ig) {
			}
		}
	} // 생성자

	public static void main(String[] args) {
		new Server();
	} // main

	// PPT 파일 리시버
	class FileRecive extends Thread {
		private Socket conSocket;
		String fileName;

		public FileRecive(Socket conSocket) {
			this.conSocket = conSocket;
		}

		@Override
		public void run() {
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(conSocket.getInputStream()));

				String str = in.readLine();
				String[] tmpPath = str.split("/");	// 입력받은 전체 경로에서 파일명만 거르기위해 split 처리 후 저장

				fileName = tmpPath[tmpPath.length - 1];	// 파일명.확장자

				int extensionIdx = fileName.lastIndexOf("."); // 확장자 분리 인덱스
				// 확장자 분리 작업
				String[] pureName = {	fileName.substring(0, extensionIdx),	// 확장자 제외한 파일명
										fileName.substring(extensionIdx) };		// 확장자

				viewArea.append("수신 중인 파일 이름 : " + fileName+"\n");
				// System.out.println("수신 중인 파일 이름 : " + fileName);

				// 서버 pc에 저장될 경로 생성
				File path = new File("D:\\PreVo\\" + pureName[0]);
				path.mkdirs();

				// 파일 저장
				File f = new File("D:\\PreVo\\" + pureName[0] + "\\", fileName);

				// System.out.println(pureName[0]);

				int length = 0;

				FileOutputStream output = new FileOutputStream(f);
				BufferedInputStream biStream = new BufferedInputStream(conSocket.getInputStream());

				byte[] buf = new byte[10080];
				while ((length = biStream.read(buf)) != -1) {

					output.write(buf, 0, length);

					output.flush();
				}
				if (in != null)
					in.close();
				if (output != null)
					output.close();

				viewArea.append(tmpPath[tmpPath.length - 1] + "수신완료\n");
				// System.out.println(tmpPath[tmpPath.length - 1] + "수신완료");
			}

			catch (Exception e) {
				viewArea.append("\n서버 에러!!\n\n");
				// System.out.println("서버 에러!!");

				e.printStackTrace();
			}

			finally {
				try {
					conSocket.close();
					// System.out.println(fileName);
					new PptToPng(fileName); // 파일 전송 완료후 Ppttopng 실행
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}