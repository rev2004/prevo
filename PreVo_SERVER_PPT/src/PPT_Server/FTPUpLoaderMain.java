package PPT_Server;


import java.io.File;
import java.util.ArrayList;

public class FTPUpLoaderMain extends Thread{
	String fileName;
	String path;
	String pn;
	
	public FTPUpLoaderMain(String fname){
		fileName = fname;	// 파일명(확장자 포함)
		int extensionIdx = fileName.lastIndexOf(".");	// 확장자 분리 인덱스
		String[] pureName = {fileName.substring(0,extensionIdx), fileName.substring(extensionIdx)};	// 확장자 분리 작업
		
		path = "D:\\PreVo\\"+pureName[0]+"\\Converted";	// 경로
		pn = pureName[0];
	}

	public void run(){
		FTPUpLoader upLoader = new FTPUpLoader();
		ArrayList<String> list = new ArrayList<String>();
		File myDir = new File(path);
		String[] contents = myDir.list(); // ppttopng 한 파일들의 목록 받아오기
		
		for (int i = 0; i < contents.length; i++) { // ppttopng 한 파일들의 목록 리스트에 추가
			list.add(contents[i]);
		}

		boolean re = upLoader.sendFtpServer("cs1.kangwon.ac.kr", 21, "cs09493",	"eogur103", "PreVo/"+pn, path+"/", list);	// 도메인/포트/아이디/비번/저장될 경로/업로드할 경로/파일

		if(re){
			System.out.println("FTPUpLoaderMain.java :: 업로드 성공");
		}else{
			System.out.println("FTPUpLoaderMain.java :: 업로드 실패");
		}
	}
}



