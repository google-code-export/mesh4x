package org.mesh4j.ektoo;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Util {
	private final  static Log LOOGER = LogFactory.getLog(Util.class);   

	
	public static boolean isInteger(String integerAsString){
		Pattern pattern = Pattern.compile("^\\d*$");
		Matcher matcher = pattern.matcher(integerAsString);
		if(matcher.matches()){
			return true;
		}
		return false;
	}
	
	//TODO need to improve
	public static int getAsInteger(String integerAsString){
		try{
			return  Integer.parseInt(integerAsString);	
		} catch (Exception ec){
			LOOGER.error(ec);
		}
		return new Integer(null);
	}
	
	
	
	

}
