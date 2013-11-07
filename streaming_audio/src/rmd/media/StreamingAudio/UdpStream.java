package rmd.media.StreamingAudio;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.udpstream);

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

	class SendAudio implements Runnable {
		@Override
		public void run() {
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
			try {
				InetAddress addr = InetAddress.getByName("192.168.173.255");
				DatagramSocket sock = new DatagramSocket();
				sock.setBroadcast(true);

				while (!sendThread.isInterrupted()) {
					bytes_read = audio_recorder.read(buf, 0, BUF_SIZE);
					DatagramPacket pack = new DatagramPacket(buf, bytes_read,
							addr, AUDIO_PORT);
					sock.send(pack);
					bytes_count += bytes_read;
					Log.e(LOG_TAG, "bytes_count : " + bytes_count);
				}
			} catch (SocketException se) {
				Log.e(LOG_TAG, "SocketException");
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
			Log.e(LOG_TAG, "start recv thread, thread id: "
					+ Thread.currentThread().getId());
			AudioTrack track = new AudioTrack(AudioManager.STREAM_VOICE_CALL,
					SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioFormat.ENCODING_PCM_16BIT, BUF_SIZE,
					AudioTrack.MODE_STREAM);
			track.play();
			int size = 0;
			try {
				DatagramSocket sock = new DatagramSocket(AUDIO_PORT);

				byte[] buf = new byte[BUF_SIZE];

				while (!recvThread.isInterrupted()) {
					DatagramPacket pack = new DatagramPacket(buf, BUF_SIZE);
					sock.receive(pack);
					size += pack.getLength();
					Log.e(LOG_TAG, "recv pack: " + size);
					track.write(pack.getData(), 0, pack.getLength());
				}
			} catch (SocketException se) {
				Log.e(LOG_TAG, "SocketException: " + se.getMessage());
			} catch (IOException ie) {
				Log.e(LOG_TAG, "IOException" + ie.getMessage());
			}
		} // end run
	}

}
