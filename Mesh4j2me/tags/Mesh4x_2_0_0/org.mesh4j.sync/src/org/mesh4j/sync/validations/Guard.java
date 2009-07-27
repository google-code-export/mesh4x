package org.mesh4j.sync.validations;

import org.mesh4j.sync.translator.MessageTranslator;

public class Guard {

	/// <summary>
	/// Checks an argument to ensure it isn't null.
	/// </summary>
	/// <param name="value">The argument value to check.</param>
	/// <param name="argumentName">The name of the argument.</param>
	public static void argumentNotNull(Object value, String argumentName)
	{
		if (value == null)
			throw new IllegalArgumentException(argumentName);
	}

	/// <summary>
	/// Checks a string argument to ensure it isn't null or empty.
	/// </summary>
	/// <param name="argumentValue">The argument value to check.</param>
	/// <param name="argumentName">The name of the argument.</param>
	public static void argumentNotNullOrEmptyString(String value, String argumentName) 
	{
		argumentNotNull(value, argumentName);

		if (value.length() == 0){
			throw new IllegalArgumentException(MessageTranslator.translate("ArgumentCanNotBeNullOrEmpty", argumentName));
		}
	}
	
	public static void argumentNotNullOrEmptyStringMaxSize(String value, String argumentName, int maxSize) {
		argumentNotNullOrEmptyString(value, argumentName);
		
		if (value.length() > maxSize){
			throw new IllegalArgumentException(MessageTranslator.translate("ArgumentSizeIsGreatherThanMaxSize", new String[]{argumentName , String.valueOf(maxSize)}));
		}
	}
	
	public static void throwsArgumentException(String key, String[] args){
		throw new IllegalArgumentException(MessageTranslator.translate(key, args));
	}
	
	public static void throwsArgumentException(String key){
		throw new IllegalArgumentException(MessageTranslator.translate(key));
	}
	
	public static void throwsException(String key, String[] args){
		throw new MeshException(MessageTranslator.translate(key, args));
	}
	
	public static void throwsException(String key){
		throw new MeshException(MessageTranslator.translate(key));
	}

}
