package com.example.recordingtest;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.Socket;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	// 녹음된 오디오 저장할 위치
	// 내장 메모리를 사용하려면 permission.WRITE_EXTERNAL_STORAGE 를 추가해야한다.
	// Environment.getExternalStorageDirectory()로 각기 다른 핸드폰의 내장메모리의 디렉토리를 알수있다.
	final private static File RECORDED_FILE = Environment
			.getExternalStorageDirectory();

	String filename;
	// MediaPlayer 클래스에 재생에 관련된 메서드와 멤버변수가 저장어되있다.
	MediaPlayer player;
	// MediaRecorder 클래스에 녹음에 관련된 메서드와 멤버 변수가 저장되어있다.
	MediaRecorder recorder;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btnListener();
	}

	void btnListener() {
		Button recordBtn = (Button) findViewById(R.id.recordBtn);
		Button recordStopBtn = (Button) findViewById(R.id.recordStopBtn);
		Button playBtn = (Button) findViewById(R.id.playBtn);
		Button playStopBtn = (Button) findViewById(R.id.playStopBtn);

		// 저장할 파일 위치를 String 으로 처리했다.
		// RECORDED_FILE.getAbsolutePath() == /mnt/sdcard 뒤에 저장할 파일엔 '/' 가 필요하다.
		filename = RECORDED_FILE.getAbsolutePath() + "/test.mp4";

		// 녹음 시작 버튼
		recordBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (recorder != null) {
					recorder.stop();
					recorder.release();
					recorder = null;
				}

				// 실험 결과 왠만하면 아래 recorder 객체의 속성을 지정하는 순서는 이대로 하는게 좋다 위치를 바꿨을때
				// 에러가 났었음
				// 녹음 시작을 위해 MediaRecorder 객체 recorder를 생성한다.
				recorder = new MediaRecorder();

				// 오디오 입력 형식 설정
				recorder.setAudioSource(MediaRecorder.AudioSource.MIC);

				// 음향을 저장할 방식을 설정
				recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

				// 오디오 인코더 설정
				recorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);

				// 저장될 파일 지정
				recorder.setOutputFile(filename);

				try {
					Toast.makeText(getApplicationContext(), "녹음이 시작되었습니다.",
							Toast.LENGTH_LONG).show();

					// 녹음 준비,시작
					recorder.prepare();
					recorder.start();
					
				} catch (Exception ex) {
					Log.e("SampleAudioRecorder", "Exception : ", ex);
				}
			}
		});

		// 녹음 중지 버튼
		recordStopBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (recorder == null)
					return;

				// 녹음을 중지
				recorder.stop();

				// 오디오 녹음에 필요한 메모리를 해제한다
				recorder.release();
				recorder = null;

				Toast.makeText(getApplicationContext(), "녹음이 중지되었습니다.",
						Toast.LENGTH_LONG).show();
			}
		});

		// 오디오 플레이 버튼
		playBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				new Thread(new TCPConnect(filename)).start();
				if (player != null) {
					player.stop();
					player.release();
					player = null;
				}

				Toast.makeText(getApplicationContext(), "녹음된 파일을 재생합니다.",
						Toast.LENGTH_LONG).show();
				try {

					// 오디오를 플레이 하기위해 MediaPlayer 객체 player를 생성한다.
					player = new MediaPlayer();

					// 재생할 오디오 파일 저장위치를 설정
					player.setDataSource(filename);
					// 웹상에 있는 오디오 파일을 재생할때
					// player.setDataSource(Audio_Url);

					// 오디오 재생준비,시작
					player.prepare();
					player.start();
				} catch (Exception e) {
					Log.e("SampleAudioRecorder", "Audio play failed.", e);
				}
			}
		});

		// 오디오 재생 중지 버튼
		playStopBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (player == null)
					return;

				Toast.makeText(getApplicationContext(), "재생이 중지되었습니다.",
						Toast.LENGTH_LONG).show();

				// 오디오 재생 중지
				player.stop();

				// 오디오 재생에 필요한 메모리들을 해제한다
				player.release();
				player = null;
			}
		});
	}

	protected void onPause() {
		if (recorder != null) {
			recorder.release();
			recorder = null;
		}

		if (player != null) {
			player.release();
			player = null;
		}

		super.onPause();
	}
}