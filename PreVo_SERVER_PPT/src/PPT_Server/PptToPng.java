package PPT_Server;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import javax.imageio.ImageIO;






import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;

public class PptToPng {
	public PptToPng(String fname) throws Exception {
		Font fontStyle = new Font("Arial Unicode MS", Font.PLAIN, 12);
		
		String fileName = fname;	// 전송받은 파일 이름(확장자 포함)
		int extensionIdx = fileName.lastIndexOf(".");	// 확장자 분리 인덱스
		String[] pureName = {fileName.substring(0,extensionIdx), fileName.substring(extensionIdx)}; // 확장자 분리 작업
		
		FileInputStream fFileInputStream = new FileInputStream("D:\\PreVo\\"+pureName[0]+"\\"+fileName); // D:\PreVo\파일명\파일명(확장자 포함) 불러오기
		XMLSlideShow ppt = new XMLSlideShow(fFileInputStream);
		fFileInputStream.close();

		double zoom = 2; // magnify it by 2
		AffineTransform at = new AffineTransform();
		at.setToScale(zoom, zoom);

		Dimension pgsize = ppt.getPageSize();

		XSLFSlide[] slide = ppt.getSlides();
		//PPT 슬라이드수만큼 PNG파일 생성
		for (int i = 0; i < slide.length; i++) {
			BufferedImage img = new BufferedImage((int)Math.ceil(pgsize.width*zoom), (int)Math.ceil(pgsize.height*zoom), BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = img.createGraphics();
			graphics.setTransform(at);

			graphics.setPaint(Color.white);
			graphics.setFont(fontStyle);

			graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));
			slide[i].draw(graphics);

			// 이미지화 된 ppt 파일들 저장될 경로
			String slideNum = String.format("%02d", i);
			File path = new File("D:\\PreVo\\"+pureName[0]+"\\Converted\\");
			path.mkdirs();
			
			FileOutputStream out = new FileOutputStream("D:\\PreVo\\"+pureName[0]+"\\Converted\\Slide" + slideNum + ".PNG");
			javax.imageio.ImageIO.write(img, "PNG", out);
			out.close();
		}
		new FTPUpLoaderMain(fileName).start(); //ftp 서버에 이미지 올리기작업 시작
	}
}
