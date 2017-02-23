package pdf.itext;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.AcroFields.FieldPosition;
import com.itextpdf.text.pdf.AcroFields.Item;
import com.itextpdf.text.pdf.PdfReader;

import net.sf.json.JSONObject;

public class HbITextUtil {
	
	public static String getPDFFieldsInfo(File file, int fontSize, String fontName) throws Exception {
		JSONObject json = new JSONObject();
		json.accumulate("fontName", fontName);
		json.accumulate("fontSize", fontSize);
		
		InputStream is = new FileInputStream(file);
		PdfReader reader = new PdfReader(is);
		
		AcroFields fields = reader.getAcroFields();
		Map<String, Item> map = fields.getFields();
		Iterator<String> iterator = map.keySet().iterator();
		JSONObject tmpObject = null;
		JSONObject obj = new JSONObject();
		FieldPosition position = null;
		while (iterator.hasNext()) {
			tmpObject = new JSONObject();
			String key = iterator.next();
			position = fields.getFieldPositions(key).get(0);
			
			tmpObject.element("name", key);
			tmpObject.element("top", (int)position.position.getTop());
			tmpObject.element("left", (int)position.position.getLeft());
			tmpObject.element("w", (int)position.position.getWidth());
			tmpObject.element("h", (int)position.position.getHeight());
			tmpObject.element("val", "");
			
			obj.element(key, tmpObject);
		}
		
		reader.close();
		is.close();
		
		json.accumulate("elems", obj);
		return json.toString();
	}
	
	public static void main(String[] args) throws Exception {
		File file = new File("D:/tmp/pdf/466_1_1_1_1_794x1123.pdf");
		String str = getPDFFieldsInfo(file, 20, "微软雅黑");
		System.out.println(str);
		//System.out.println(JSONObject.fromObject(str).get("elems").getClass().getName());
	}
	
}
