package PPT_Server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;


public class FTPUpLoader {
	public boolean sendFtpServer(String ip, int port, String id, String password, String folder, String localPath, ArrayList<String> files) {
		boolean isSuccess = false;
		FTPClient ftp = null;
		int reply;
		
		System.out.println("FTP에 저장 될 경로 : " + folder);
		
		try {
			ftp = new FTPClient();
			ftp.connect(ip, port);
			System.out.println("Connected to " + ip + " on " + ftp.getRemotePort());

			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				System.err.println("FTP server refused connection.");
				System.exit(1);
			}

			if (!ftp.login(id, password)) {
				ftp.logout();
				throw new Exception("ftp 서버에 로그인하지 못했습니다.");
			}
			
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			ftp.enterLocalPassiveMode();
			ftp.setDefaultTimeout(0);

			System.out.println(ftp.printWorkingDirectory());
			try {
				ftp.makeDirectory(folder);
			} catch (Exception e) {
				e.printStackTrace();
			}
			ftp.changeWorkingDirectory(folder);
			System.out.println(ftp.printWorkingDirectory());

			for (int i = 0; i < files.size(); i++) {
				String tempFileName = new String(files.get(i).getBytes("utf-8"), "iso_8859_1"); // 한글 깨짐 방지
				String tmpPath;
				
				String sourceFile = localPath + files.get(i);
				File uploadFile = new File(sourceFile);
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(uploadFile);
					System.out.println(sourceFile + " : 전송시작 = >");
					isSuccess = ftp.storeFile(tempFileName, fis); // ftp 서버에 업로드하기
					System.out.println(sourceFile + " : 전송결과 = >" + isSuccess);
				} catch (IOException e) {
					e.printStackTrace();
					isSuccess = false;
				} finally {
					if (fis != null) {
						try {
							fis.close();
						} catch (IOException e) {
						}
					}
				}
			}

			ftp.logout();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ftp != null && ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException e) {
				}
			}
		}
		return isSuccess;
	}

}
