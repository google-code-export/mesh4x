using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Mesh4n
{
	[TestClass]
	public class FlagFixture
	{
		[TestMethod]
		public void ShouldCheckFlagSet()
		{
			MockFlags flags = MockFlags.one | MockFlags.three;

			Assert.IsTrue(Flag.IsSet(flags, MockFlags.one));
			Assert.IsTrue(Flag.IsSet(flags, MockFlags.three));
			Assert.IsFalse(Flag.IsSet(flags, MockFlags.two));
			Assert.IsFalse(Flag.IsSet(flags, MockFlags.four));

			Assert.IsFalse(Flag.IsSet(flags, MockFlags.one | MockFlags.four));
			Assert.IsFalse(Flag.IsSet(flags, MockFlags.two | MockFlags.four));
			Assert.IsTrue(Flag.IsSet(flags, MockFlags.one | MockFlags.three));
		}

		[TestMethod]
		public void ShouldSetFlag()
		{
			MockFlags flags = MockFlags.one;

			Assert.IsFalse(Flag.IsSet(flags, MockFlags.three));

			flags = Flag.Set(flags, MockFlags.three);

			Assert.IsTrue(Flag.IsSet(flags, MockFlags.three));

			flags = Flag.Set(flags, MockFlags.three | MockFlags.four);

			Assert.IsTrue(Flag.IsSet(flags, MockFlags.three));
			Assert.IsTrue(Flag.IsSet(flags, MockFlags.four));


			flags = MockFlags.one;

			flags = Flag.Set(flags, MockFlags.two | MockFlags.three | MockFlags.four);

			Assert.IsTrue(Flag.IsSet(flags, MockFlags.one | MockFlags.two));
			Assert.IsTrue(Flag.IsSet(flags, MockFlags.one | MockFlags.three));
			Assert.IsTrue(Flag.IsSet(flags, MockFlags.one | MockFlags.two | MockFlags.three | MockFlags.four));
		}

		[TestMethod]
		public void ShouldClearFlag()
		{
			MockFlags flags = MockFlags.one | MockFlags.three;

			flags = Flag.Clear(flags, MockFlags.three);

			Assert.IsTrue(Flag.IsSet(flags, MockFlags.one));
			Assert.IsFalse(Flag.IsSet(flags, MockFlags.three));

			flags  = MockFlags.two | MockFlags.three | MockFlags.four;
			Assert.IsTrue(Flag.IsSet(flags, MockFlags.two));
			Assert.IsTrue(Flag.IsSet(flags, MockFlags.three));
			Assert.IsTrue(Flag.IsSet(flags, MockFlags.four));

			flags = Flag.Clear(flags, MockFlags.two);
			Assert.IsFalse(Flag.IsSet(flags, MockFlags.two));
			Assert.IsTrue(Flag.IsSet(flags, MockFlags.three));
			Assert.IsTrue(Flag.IsSet(flags, MockFlags.four));

			flags = Flag.Clear(flags, MockFlags.three);
			Assert.IsFalse(Flag.IsSet(flags, MockFlags.two));
			Assert.IsFalse(Flag.IsSet(flags, MockFlags.three));
			Assert.IsTrue(Flag.IsSet(flags, MockFlags.four));
		}

		enum MockFlags
		{
			one = 0,
			two = 1,
			three = 2,
			four = 4,
		}
	}
}
