package Server;

import java.awt.*;
import java.io.*; //  입출력이 일어난다.
import java.net.*; //  네트워크 프로그램.
import java.util.*; //  ArrayList 사용(클라이언트를 담는 역할)
import javax.swing.*;

public class PreVo_Server extends JFrame {
	private ArrayList<MultiServerThread> clientList;	// ArrayList 는 Linked List 로 구현된 배열	// 선언방법 -> ArrayList<자료형> 변수이름
	private Socket socket;

	JTextArea viewArea;		// Text Output Area
	JTextField inputArea;	// Text Input Area

	// 생성자
	public PreVo_Server() {
		// 화면 디자인 코드
		setTitle("PreVo_Server (ver 1.0)");
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

		// 채팅 관련 코드
		clientList = new ArrayList<MultiServerThread>();	// 접속자 리스트 초기화
		try {
			ServerSocket serverSocket = new ServerSocket(8324);	// 서버 소켓 생성
			MultiServerThread mst = null;	// 한 사용자 담당할 채팅 객체
			boolean isStop = false; 			// 플래그 값
			inputArea.setText("서버 접속 대기 중...\n");

			while (!isStop) {
				socket = serverSocket.accept();	// 클라이언트별 소켓 생성
				mst = new MultiServerThread();	// 채팅 객체(접속자 객체) 생성
				clientList.add(mst);	// ArrayList에 채팅 객체(접속자 객체)를 하나 담는다.
				mst.start();	// 쓰레드 시작
			}	// while
		} catch (IOException e) {
			e.printStackTrace();
		}	// catch
	}	// 생성자

	// 메인함수
	public static void main(String[] args) {
		new PreVo_Server();
	}	// main

	// 내부 클래스
	class MultiServerThread extends Thread {
		private ObjectInputStream ois;
		private ObjectOutputStream oos;

		@Override
		public void run() {
			boolean isStop = false;	// flag value(깃발 값)
			try {
				ois = new ObjectInputStream(socket.getInputStream());	// 데이터 받음
				oos = new ObjectOutputStream(socket.getOutputStream());	// 데이터 보냄
				String message = null;	// 채팅 내용을 저장할 변수
				while (!isStop) {
					message = (String) ois.readObject();	// 클라이언트 입력 받기
					String[] str = message.split("#");		// 홍길동#방가방가
					if (str[1].equals("exit")) {				// 홍길동#exit, 종료하겠다는 뜻
						broadCasting(message);			// 모든 사용자에게 내용 전달
						isStop = true;	// 종료
					} else {
						broadCasting(message);	// 모든 사용자에게 채팅 내용 전달
					}	// else
				}	// while
				clientList.remove(this);	// 홍길동을 뺀다.
				viewArea.append(socket.getInetAddress() + " IP 주소의 사용자께서 종료하셨습니다.\n");
				inputArea.setText("남은 사용자 수 : " + clientList.size());
			} catch (Exception e) {
				clientList.remove(this);	// 장길산을 뺀다.
				viewArea.append(socket.getInetAddress() + " IP 주소의 사용자께서 비정상 종료하셨습니다.\n");
				inputArea.setText("남은 사용자 수 : " + clientList.size());
			}	// catch
		}	// run

		// 모두에게 전송
		public void broadCasting(String message) {
			for (MultiServerThread ct : clientList) {	// 향상된 for문, ct에 list의 원소를 하나씩 저장 한다.
				ct.send(message);
			} // for
		} // broadCasting

		// 한 사용자에게 전송
		public void send(String message) { 
			try {
				oos.writeObject(message);
			} catch (IOException e) {
				e.printStackTrace();
			}// catch
		}// send
	}// 내부 클래스
}// end 