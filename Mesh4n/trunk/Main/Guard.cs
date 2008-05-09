using System;
using System.Globalization;

internal static class Guard
{
	/// <summary>
	/// Checks an argument to ensure it isn't null.
	/// </summary>
	/// <param name="value">The argument value to check.</param>
	/// <param name="argumentName">The name of the argument.</param>
	public static void ArgumentNotNull(object value, string argumentName)
	{
		if (value == null)
			throw new ArgumentNullException(argumentName);
	}

	/// <summary>
	/// Checks a string argument to ensure it isn't null or empty.
	/// </summary>
	/// <param name="argumentValue">The argument value to check.</param>
	/// <param name="argumentName">The name of the argument.</param>
	public static void ArgumentNotNullOrEmptyString(string value, string argumentName)
	{
		ArgumentNotNull(value, argumentName);

		if (value.Length == 0)
			throw new ArgumentException("Value cannot be null or an empty string.",
				argumentName);
	}

	public static void ArgumentIsInstanceOfType(object value, Type type, string argumentName)
	{
		if (!type.IsInstanceOfType(value))
		{
			throw new ArgumentException(string.Format(
				CultureInfo.InvariantCulture,
				"Value is not instance of type {0}",
				type.Name), argumentName);
		}
	}
}
