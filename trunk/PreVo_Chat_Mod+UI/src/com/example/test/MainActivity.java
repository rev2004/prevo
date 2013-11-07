package com.example.test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
public class MainActivity extends Activity {
	String[] receiveMsg;	// 수신 메세지 분할할때 쓰임

	// UI변수 선언
	ImageView img_ppt;				// PPT 띄울 이미지 뷰
	LinearLayout layout_option;		// 좌측 사이드 메뉴 (옵션메뉴)
	LinearLayout layout_joinMember;	// 우측 사이드 메뉴 
	LinearLayout layout_txtMain;	// 메인 화면

	Button btn_optionCall;		// 좌측 사이드 메뉴 호출 버튼
	Button btn_joinMemberCall;	// 우측 사이드 메뉴 호출 버튼
	Button btn_txtCall;			// 채팅창 여닫는 버튼

	// 메뉴 버튼 눌림 감지
	boolean flag_option_call = true;
	boolean flag_joinMember_call = true;
	boolean flag_txt_call = true;

	// 채팅 변수 선언
	EditText txt_msg;	// 메세지 입력란
	Button btn_txtSend;	// send 버튼
	ListView txt_view;	// 채팅창
	ArrayAdapter<String> txt_adapter;
	ArrayList<String> txt_list;
	private String txtString;

	PreVoChatMod pcm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		img_ppt = (ImageView) findViewById(R.id.img_ppt);
		btn_optionCall = (Button) findViewById(R.id.btn_optionCall);
		btn_joinMemberCall = (Button) findViewById(R.id.btn_joinMemberCall);
		btn_txtCall = (Button) findViewById(R.id.btn_txtCall);
		layout_option = (LinearLayout) findViewById(R.id.layout_option);
		layout_joinMember = (LinearLayout) findViewById(R.id.layout_joinMember);
		layout_txtMain = (LinearLayout) findViewById(R.id.layout_txtMain);

		UiInit();
		txtInit();

		pcm = new PreVoChatMod();
		Thread t = new Thread(pcm);
		t.start();
	}

	private void UiInit() {
		// TODO Auto-generated method stub

		layout_option.setX(-(R.dimen.layout_option_width));
		layout_joinMember.setX(R.dimen.layout_option_width);
		layout_txtMain.setY(-(R.dimen.layout_txtMainHeight));

		btn_optionCall.setText("▷");
		btn_joinMemberCall.setText("◁");
		btn_txtCall.setText("△");

		// 옵션 메뉴 호출 버튼 이벤트 리스너
		btn_optionCall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (flag_option_call == true) {

					flag_option_call = false;
					btn_joinMemberCall.setClickable(false);
					layout_option.animate().translationXBy(
							R.dimen.layout_option_width);
					img_ppt.animate().translationXBy(
							R.dimen.layout_option_width / 5300000);
					btn_optionCall.animate().translationXBy(
							R.dimen.layout_option_width / 5300000);
					btn_joinMemberCall.animate().translationXBy(
							R.dimen.layout_option_width / 5300000);
					btn_optionCall.setText("◁");

				} else {

					flag_option_call = true;
					btn_joinMemberCall.setClickable(true);
					layout_option.animate().translationXBy(
							-(R.dimen.layout_option_width));
					img_ppt.animate().translationXBy(
							-(R.dimen.layout_option_width / 5300000));
					btn_optionCall.animate().translationXBy(
							-(R.dimen.layout_option_width / 5300000));
					btn_joinMemberCall.animate().translationXBy(
							-(R.dimen.layout_option_width / 5300000));
					btn_optionCall.setText("▷");

				}

			}
		});

		// 멤버 메뉴 호출 버튼 이벤트 리스너
		// 추후 res/values/dimens.xml에서 레이아웃 값 조절 시 적절한 '/' 뒤의 값 조절 필요
		btn_joinMemberCall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (flag_joinMember_call == true) {

					flag_joinMember_call = false;
					btn_optionCall.setClickable(false);
					layout_joinMember.animate().translationXBy(
							-(R.dimen.layout_joinMember_width));
					img_ppt.animate().translationXBy(
							-(R.dimen.layout_joinMember_width / 5300000));
					btn_joinMemberCall.animate().translationXBy(
							-(R.dimen.layout_joinMember_width / 5300000));
					btn_optionCall.animate().translationXBy(
							-(R.dimen.layout_joinMember_width / 5300000));
					btn_joinMemberCall.setText("▷");

				} else {

					flag_joinMember_call = true;
					btn_optionCall.setClickable(true);
					layout_joinMember.animate().translationXBy(
							R.dimen.layout_joinMember_width);
					img_ppt.animate().translationXBy(
							R.dimen.layout_joinMember_width / 5300000);
					btn_joinMemberCall.animate().translationXBy(
							R.dimen.layout_joinMember_width / 5300000);
					btn_optionCall.animate().translationXBy(
							R.dimen.layout_joinMember_width / 5300000);
					btn_joinMemberCall.setText("◁");

				}

			}
		});

		// 채팅 호출 버튼 이벤트 리스너
		btn_txtCall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (flag_txt_call == true) {

					flag_txt_call = false;
					layout_txtMain.animate().translationYBy(
							R.dimen.layout_txtMainHeight);
					btn_txtCall.animate().translationYBy(
							-(R.dimen.layout_txtMainHeight / 7100000));
					btn_txtCall.setText("▽");

				} else {

					flag_txt_call = true;
					layout_txtMain.animate().translationYBy(
							-(R.dimen.layout_txtMainHeight));
					btn_txtCall.animate().translationYBy(
							R.dimen.layout_txtMainHeight / 7100000);
					btn_txtCall.setText("△");

				}
			}
		});
	}


	// 채팅관련 초기화
	private void txtInit() {
		// TODO Auto-generated method stub

		txt_view = (ListView) findViewById(R.id.txt_view);
		txt_list = new ArrayList<String>();
		txt_adapter = new ArrayAdapter<String>(this, R.layout.txt_row, txt_list);
		btn_txtSend = (Button) findViewById(R.id.btn_txtSend);
		txt_msg = (EditText) findViewById(R.id.txt_Text);

		txt_view.setAdapter(txt_adapter);
		txt_view.setDivider(null);
		txt_view.setChoiceMode(ListView.CHOICE_MODE_NONE);

		// 채팅 Send 버튼 이벤트 리스너
		btn_txtSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				txtString = txt_msg.getText().toString();
				txtString = txtString.trim();
				txt_msg.setText(null);

				if (txtString.getBytes().length > 0) {

					txt_adapter.add(txtString);
					txt_view.setSelection(txt_list.size() - 1);

				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	//내부 채팅 클래스
	private class PreVoChatMod implements Runnable{
		// 멤버 변수 정의
		private String IP = "125.179.159.11";		// IP 주소 변수
		private int PORT = 8324;					// port 번호  변수
		private String id = "Kwon_Android";	// 대화명 변수

		private Socket socket;
		private ObjectInputStream ois;
		private ObjectOutputStream oos;
		private Handler mHandler;	// 스레드 핸들러

		boolean isRun = true;

		public void init(){
			try {
				socket = new Socket(IP, PORT);
				mHandler.post(new Runnable(){
					public void run(){
						txt_adapter.add("### PreVo 서버 접속 완료 ###\n\n");
					}
				});
				oos = new ObjectOutputStream(socket.getOutputStream());
				ois = new ObjectInputStream(socket.getInputStream());
				//				Thread t = new Thread(this);
				//				t.start();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public PreVoChatMod(){
			mHandler = new Handler();

			// 채팅 Send 버튼 이벤트 리스너
			btn_txtSend.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(txt_msg.getText().toString() == ""){
						return;
					} else {
						String msg = txt_msg.getText().toString(); // 채팅 내용 입력 받기
						txt_msg.setText("");
						try{
							oos.writeObject(id + "#" + msg);
							oos.flush();
						} catch (IOException e) {
							// TODO Auto-generated method stub
							e.printStackTrace();
						}
					}
				}
			});

//			사용자 추가
//			mHandler.post(new Runnable(){
//				public void run(){
//					txt_status.append("대화명 : [" + id + "]");
//				}
//			});
		}	// 생성자

		public void stop(){
			try {
				ois.close();
				oos.close();
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void run(){
			String message = null;
			receiveMsg = null;
			boolean isStop = false;

			init();

			while(!isStop){
				try {
					message = (String)ois.readObject();
					receiveMsg = message.split("#");
				} catch (OptionalDataException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					isStop = true; // 반복문 종료로 설정
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					isStop = true; // 반복문 종료로 설정
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					isStop = true; // 반복문 종료로 설정
				}

				mHandler.post(new Runnable(){
					public void run(){
						txt_adapter.add(receiveMsg[0] + ":" +  receiveMsg[1] +"\n");
					}
				});

				if(isRun == false){
					stop();
				}
			}
		}
	}
}
