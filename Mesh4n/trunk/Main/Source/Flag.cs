using System;
using System.Collections.Generic;
using System.Text;

namespace Mesh4n
{
	/// <summary>
	/// Helper class to test and set bit flags in an enumeration or integer.
	/// </summary>
	public static class Flag
	{
		public static bool IsSet<TFlags>(TFlags currentValue, TFlags flagToCheck)
			where TFlags : struct
		{
			if (typeof(TFlags) == typeof(int) || typeof(TFlags).IsEnum)
			{
				int current = (int)(object)currentValue;
				int flag = (int)(object)flagToCheck;
				return (current & flag) == flag;
			}

			ThrowInvalidFlagType();
			// satisfy compiler ;)
			return false;
		}

		public static TFlags Set<TFlags>(TFlags currentValue, TFlags flagToSet)
			where TFlags : struct
		{
			if (typeof(TFlags) == typeof(int) || typeof(TFlags).IsEnum)
			{
				int current = (int)(object)currentValue;
				int flag = (int)(object)flagToSet;
				return (TFlags)(object)(current |= flag);
			}
		
			ThrowInvalidFlagType();
			// satisfy compiler ;)
			return default(TFlags);
		}

		public static TFlags Clear<TFlags>(TFlags currentValue, TFlags flagToClear)
			where TFlags : struct
		{
			if (typeof(TFlags) == typeof(int) || typeof(TFlags).IsEnum)
			{
				int current = (int)(object)currentValue;
				int flag = (int)(object)flagToClear;
				return (TFlags)(object)(current &= ~flag);
			}

			ThrowInvalidFlagType();
			// satisfy compiler ;)
			return default(TFlags);
		}

		private static void ThrowInvalidFlagType()
		{
			throw new ArgumentException("Flags must be an integer or an integer enumeration");
		}
	}
}
