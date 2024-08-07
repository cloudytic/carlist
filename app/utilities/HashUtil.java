package utilities;

import org.apache.commons.codec.binary.Base64;

public class HashUtil {
	public static String encodeBase64(String text) {
		return Base64.encodeBase64URLSafeString(text.getBytes());
	}
	
	public static String decodeBase64(String text) {
		return new String(Base64.decodeBase64(text.getBytes()));
	}
}