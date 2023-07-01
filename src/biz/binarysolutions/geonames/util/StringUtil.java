package biz.binarysolutions.geonames.util;

/**
 * 
 *
 */
public class StringUtil {
	
	/**
	 * 
	 * @param string
	 * @return
	 */
	public static boolean isDefined(String string) {
		return string != null && string.length() > 0;
	}
}
