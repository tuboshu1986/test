package stream;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

public class CloseableUtil {
	
	/**
	 * 关闭流，如果流同时属于Flushable的类，则关闭之前flush
	 * @param closes
	 */
	public static void close(Closeable... closes){
		for (Closeable c : closes) {
			if(c != null){
				try {
					if(c instanceof Flushable)
						((Flushable) c).flush();
					c.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
