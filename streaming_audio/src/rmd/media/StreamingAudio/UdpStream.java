package rmd.media.StreamingAudio;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;

public class UdpStream extends Activity {
	Thread sendThread = new Thread(new SendAudio());
	Thread recvThread = new Thread(new RecvAudio());
	Thread tcpThread = new Thread(new TCPConnect());

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.udpstream);

		tcpThread.start();

		if ((sendThread != null && sendThread.isAlive())
				&& (recvThread != null && recvThread.isAlive())) {
			sendThread.interrupt();
			recvThread.interrupt();
		} else {
			sendThread.start();
			recvThread.start();
		}
	}

	static final String LOG_TAG = "UdpStream";
	static final int AUDIO_PORT = 2048;
	static final int SAMPLE_RATE = 11025;
	static final int BUF_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE,
			AudioFormat.CHANNEL_CONFIGURATION_MONO,
			AudioFormat.ENCODING_PCM_16BIT);;

	class TCPConnect implements Runnable {
		Socket socket = null;
		InetAddress address = null;

		@Override
		public void run() {
			try {
				address = InetAddress.getByName("192.168.23.15");
				socket = new Socket(address, 2048);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	class SendAudio implements Runnable {
		@Override
		public void run() {
			try {
				Log.e(LOG_TAG, "start SendMicAudio thread, thread id: "
						+ Thread.currentThread().getId());
				AudioRecord audio_recorder = new AudioRecord(
						MediaRecorder.AudioSource.DEFAULT, SAMPLE_RATE,
						AudioFormat.CHANNEL_CONFIGURATION_MONO,
						AudioFormat.ENCODING_PCM_16BIT,
						AudioRecord.getMinBufferSize(SAMPLE_RATE,
								AudioFormat.CHANNEL_CONFIGURATION_MONO,
								AudioFormat.ENCODING_PCM_16BIT));
				int bytes_read = 0;
				int bytes_count = 0;
				byte[] buf = new byte[BUF_SIZE];
				audio_recorder.startRecording();
				InetAddress addr = InetAddress.getByName("192.168.23.15");
				DatagramSocket sock = new DatagramSocket();

				while (!sendThread.isInterrupted()) {
					bytes_read = audio_recorder.read(buf, 0, BUF_SIZE);
					DatagramPacket pack = new DatagramPacket(buf, bytes_read,
							addr, AUDIO_PORT);
					sock.send(pack);
					bytes_count += bytes_read;
					// Log.e(LOG_TAG, "bytes_count : " + bytes_count);
				}
			} catch (SocketException se) {
				Log.e(LOG_TAG, "Send SocketException" + se.getMessage());
			} catch (UnknownHostException uhe) {
				Log.e(LOG_TAG, "UnknownHostException");
			} catch (IOException ie) {
				Log.e(LOG_TAG, "IOException");
			}
		} // end run
	}

	class RecvAudio implements Runnable {
		@Override
		public void run() {
			try {
				Log.e(LOG_TAG, "start recv thread, thread id: "
						+ Thread.currentThread().getId());
				AudioTrack track = new AudioTrack(
						AudioManager.STREAM_VOICE_CALL, SAMPLE_RATE,
						AudioFormat.CHANNEL_CONFIGURATION_MONO,
						AudioFormat.ENCODING_PCM_16BIT, BUF_SIZE,
						AudioTrack.MODE_STREAM);
				track.play();
				int size = 0;
				DatagramSocket sock = new DatagramSocket(AUDIO_PORT);

				byte[] buf = new byte[BUF_SIZE];

				while (!recvThread.isInterrupted()) {
					DatagramPacket pack = new DatagramPacket(buf, BUF_SIZE);
					sock.receive(pack);
					size += pack.getLength();
					Log.e("Address", "" +pack.getAddress());
					//Log.e(LOG_TAG, "recv pack: " + size);
					track.write(pack.getData(), 0, pack.getLength());
				}
			} catch (SocketException se) {
				Log.e(LOG_TAG, "Recv SocketException: " + se.getMessage());
			} catch (IOException ie) {
				Log.e(LOG_TAG, "IOException" + ie.getMessage());
			}
		} // end run
	}

}