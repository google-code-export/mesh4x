package com.mesh4j.sync.validations;

import com.mesh4j.sync.translator.MessageTranslator;

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

		if (value.length() == 0)
			throw new IllegalArgumentException(MessageTranslator.translate("ArgumentCanNotBeNullOrEmpty", argumentName));
	}
}