package qrcode.zxing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * 有关二维码的操作，使用zxing
 * @author 
 *
 */
public class BarCodeImageUtil {
	
	/**
	 * 黑色
	 */
	public static int BLACK=0xFF000000;
	/**
	 * 白色
	 */
	public static int WHITE=0xFFFFFFFF;
	/**
	 * 透明
	 */
	public static int TRANS=0x00FFFFFF;
	
	/**
	 * 制作二维码
	 * @param content 二维码内容
	 * @param width 二维码图片的宽度(像素)
	 * @param height 二维码图片的高度(像素)
	 * @param flag 如果flag为true则二维码背景为白色，否则是透明的
	 * @param borderWidth 边框宽度
	 * @param type 条码类型，com.google.zxing.BarcodeFormat.class，来自zxing
	 * @return
	 */
	public static BufferedImage createQRcode(String content,
			int width,
			int height,
			boolean flag,
			int borderWidth){
		
		BufferedImage image = createBarCodeImage(content,BarcodeFormat.QR_CODE,
				flag,borderWidth,width,height,false);
		return image;
	}
	
	/**
	 * 制作条形码
	 * @param content 二维码内容
	 * @param width 二维码图片的宽度(像素)
	 * @param height 二维码图片的高度(像素)
	 * @param flag 如果flag为true则二维码背景为白色，否则是透明的
	 * @param borderWidth 边框宽度
	 * @param type 条码类型，com.google.zxing.BarcodeFormat.class，来自zxing
	 * @param showContent 是否在生产的图片上显示文字，true：显示，false：不显示
	 * @return
	 */
	public static BufferedImage createBarcode(String content,
			int width,
			int height,
			boolean flag,
			int borderWidth,
			BarcodeFormat type,
			boolean showContent){
		
		BufferedImage image = createBarCodeImage(content,type,flag,borderWidth,width,height,showContent);
		return image;
	}
	
	/**
	 * 将指定的字符串写入指定类型的条码中，使用zxing插件
	 * @param content 二维码内容
	 * @param width 二维码图片的宽度(像素)
	 * @param height 二维码图片的高度(像素)
	 * @param flag 如果flag为true则二维码背景为白色，否则是透明的
	 * @param borderWidth 边框宽度
	 * @param type 条码类型，com.google.zxing.BarcodeFormat.class，来自zxing
	 * @param showContent 是否在生产的图片上显示文字，true：显示，false：不显示
	 * @return
	 */
	public static BufferedImage createBarCodeImage(String content,BarcodeFormat type,
			boolean flag,int borderWidth,int width,int height,boolean showContent){
		//创建BitMatrix
		BitMatrix bitMatrix = createBitMatrix(content,width,height,type);
		//转换为图片
		BufferedImage image = bitMatrixToBufferedImage(bitMatrix,flag,false);
		
		image = resetImage(image,width,height,borderWidth,showContent?content:null,flag);
		return image;
	}
	
	/**
	 * 设定图片的样式，包括：边框、文字、内容主体
	 * @param image 内容图形
	 * @param width 图片宽度
	 * @param height 图片高度
	 * @param borderWidth 边框
	 * @param content 如果content为null则不显示文字
	 * @param flag 如果flag为true则二维码背景为白色，否则是透明的
	 * @return
	 */
	private static BufferedImage resetImage(BufferedImage image,
			int width,int height,int borderWidth,String content,boolean flag){
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = newImage.getGraphics();
		
		//判断背景颜色
		if(flag){
			g.setColor(new Color(255, 255, 255));
			g.fillRect(0, 0, width, height);
		}
		
		//判断是否添加文字
		if(content==null){
			//不添加文字
			
			int barWidth = width-(borderWidth*2);
			int barHeight = height-(borderWidth*2);
			g.drawImage(image, borderWidth, borderWidth, barWidth ,barHeight ,null);
		}else{
			//添加文字
			
			int barWidth = width-(borderWidth*2);//内容宽度
			int len = content.length();//文字数目
			int fontSize = barWidth/len;//计算字体大小
			if(fontSize>15)fontSize =15;//如果计算出的字体大于20px则设置字体大小为20px
			
			int barHeight = height-(borderWidth*2)-fontSize;//内容高度
			g.drawImage(image, borderWidth, borderWidth, barWidth ,barHeight ,null);//绘制内容区域
			
			g.setColor(new Color(0, 0, 0));//设置颜色为黑色
			g.setFont(new Font("宋体", 1, fontSize));//设置字体
			
			String[] chars = content.split("");//切割字符串，数组里面第一个元素是空字符串“”
			int fontY = borderWidth+barHeight+fontSize;//文字的y坐标
			
			int fontX = (int)(0.5*(barWidth-fontSize*len))+borderWidth-fontSize;//文字的x坐标起始位置
			len++;
			for(int i=1;i<len;i++){
				g.drawString(chars[i], fontX+=fontSize, fontY);
			}
			
		}
		
		g.dispose();
		return newImage;
	}
	
	/**
	 * 将BitMatrix转换为图片
	 * @param bitMatrix 存储二维码的bit矩阵
	 * @param flag 标记，背景色是否不为透明，true:白色，false:true
	 * @param noCut 是否图片裁剪为只剩内容区域的状态
	 * @return
	 */
	private static BufferedImage bitMatrixToBufferedImage(BitMatrix bitMatrix,boolean flag,boolean noCut){
		int width = bitMatrix.getWidth();
		int height = bitMatrix.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		//设定二维码背景是透明还是白色
		int white=flag?0xFFFFFFFF:0x00FFFFFF;
		//将二维码的内容写入图片
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				image.setRGB(i, j, bitMatrix.get(i, j) ? BLACK : white);
			}
		}
		
		if(!noCut){
			//获取二维码属性：白边宽度以及二维码内容区域的长宽
			int[] rec = bitMatrix.getEnclosingRectangle(); // 获取二维码图案的属性 0:topmargin,1:leftmargin,2:width,3:height
			//裁剪出barcode的内容区域，即将白边清除
			image = image.getSubimage(rec[0], rec[1], rec[2]+1, rec[3]+1);
			System.out.println(Arrays.toString(rec));
		}
		return image;
	}
	
	/**
	 * 创建BitMatrix(bit矩阵)
	 * @param content 将存储到二维码的字符内容
	 * @param width 二维码图片的宽度(像素)
	 * @param height 二维码图片的高度(像素)
	 * @param barCodeFormat 条形码的格式
	 * @return
	 */
	private static BitMatrix createBitMatrix(String content,int width,int height,BarcodeFormat barCodeFormat){
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");// 编码
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // 容错率
		//hints.put(EncodeHintType.MARGIN, 0); // 二维码边框宽度，这里文档说设置0-4，设置后没有效果
		
		try {
			BitMatrix bitMatrix = new MultiFormatWriter().encode(content, barCodeFormat, width, height);
			return bitMatrix;
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * 将图片设置为新的width和height
	 * 
	 */
	public static BufferedImage zoomInImage(BufferedImage originalImage, int width, int height) {
		BufferedImage newImage = new BufferedImage(width, height, originalImage.getType());
		Graphics g = newImage.getGraphics();
		g.drawImage(originalImage, 0, 0, width, height, null);
		g.dispose();
		return newImage;
	}
	
	/**
	 * 用于测试当前类的main方法
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		/*
		BufferedImage image = createQRcode("https://www.baidu.com/s?wd=pdfbox%20%E8%BD%ACword&rsf=9&rsp=0&f=1&oq=pdfbox%20pdf%E8%BD%ACword&ie=utf-8&rsv_pq=e5bbae47000fc67c&rsv_t=6e4cTBvpNdaW7myD5AxLSlBKsFMq0Da3sU7LVCJ4X%2BQ74Cnbgnhhd%2B2yMnM&rqlang=cn&rs_src=0",
				150,
				150,
				true,
				5);
		*/
		
		BufferedImage image = createBarcode("1250170120000011", 
			200, 
			80, 
			true, 
			3, 
			BarcodeFormat.CODE_128, 
			true);		
		
		OutputStream out = new FileOutputStream("C:\\tmp\\pdf\\br.gif");
		ImageIO.write(image, "gif", out);
		out.close();
	}
	
}
