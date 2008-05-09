package com.mesh4j.sync.utils;

public class StringHelper {
	
	public static String replace(String baseString, String oldString, String newString) {
		if (baseString == null) {
			return null;
		}
		int oldStringLength = oldString.length();
		StringBuffer sb = new StringBuffer(oldStringLength + 100);
		int occ = 0;
		int start = 0;
		while (occ != -1) {
			occ = baseString.indexOf(oldString, start);
			if (occ != -1) {
				sb.append(baseString.substring(start, occ));
				sb.append(newString);
				start = occ + oldStringLength;
			}
		}
		sb.append(baseString.substring(start));
		return sb.toString();
	}
}
