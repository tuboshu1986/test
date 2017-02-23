package pdf.pdfbox;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;

import stream.CloseableUtil;

/**
 * PDFBox相关的
 * @author Administrator
 *
 */
public class HbPdfBoxUtil {

	public static void main(String[] args) throws Exception {
		pdfToImg("D:/tmp/pdf/b.pdf","D:/tmp/pdf/b.jpg");
		System.out.println("完成");
	}
	
	/**
	 * pdfbox，提取pdf文件中的text
	 * @param pdfPath
	 * @param docPath
	 * @throws IOException
	 */
	public static void stripperText(String pdfPath,String docPath) throws IOException{
		PDDocument document = PDDocument.load(new File(pdfPath));
		Writer writer = new OutputStreamWriter(new FileOutputStream(docPath));
		int pageCount = document.getNumberOfPages();
		
		PDFTextStripper stripper = new PDFTextStripper();
		stripper.setSortByPosition(true);
		stripper.setStartPage(1);
		stripper.setEndPage(pageCount);
		
		stripper.writeText(document, writer);
		
		CloseableUtil.close(writer, document);
		
	}
	
	/**
     * pdfbox，将pdf转换为png格式的图片
     * @param pdfPath
     * @param pngPath
     */
    public static void pdfToImg(String pdfPath,String pngPath){
        //将pdf装图片 并且自定义图片得格式大小
        File file = new File(pdfPath);
        try {
            PDDocument doc = PDDocument.load(file);
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            for (int i = 0; i < pageCount; i++) {
            	//BufferedImage image = renderer.renderImage(i);
                BufferedImage image = renderer.renderImage(i, 2f, ImageType.RGB);
                //BufferedImage srcImage = resize(image, image.getWidth(), image.getHeight());
                ImageIO.write(image, "jpeg", new File(pngPath.replace(".",i+".")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 重新设置图片尺寸
     * @param source
     * @param targetW
     * @param targetH
     * @return
     */
    public static BufferedImage resize(BufferedImage source, int targetW, int targetH) {
        int type = source.getType();
        BufferedImage target = null;
        double sx = (double) targetW / source.getWidth();
        double sy = (double) targetH / source.getHeight();
        if (sx > sy) {
            sx = sy;
            targetW = (int) (sx * source.getWidth());
        } else {
            sy = sx;
            targetH = (int) (sy * source.getHeight());
        }
        if (type == BufferedImage.TYPE_CUSTOM) {
            ColorModel cm = source.getColorModel();
            WritableRaster raster = cm.createCompatibleWritableRaster(targetW, targetH);
            boolean alphaPremultiplied = cm.isAlphaPremultiplied();
            target = new BufferedImage(cm, raster, alphaPremultiplied, null);
        } else {
            target = new BufferedImage(targetW, targetH, type);
        }
        Graphics2D g = target.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
        g.dispose();
        return target;
    }
}
