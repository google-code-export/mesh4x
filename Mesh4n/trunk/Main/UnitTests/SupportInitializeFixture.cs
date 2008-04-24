using System;
using System.Collections.Generic;
using System.Text;
#if PocketPC
using Microsoft.Practices.Mobile.TestTools.UnitTesting;
#else
using Microsoft.VisualStudio.TestTools.UnitTesting;
#endif
using System.ComponentModel;

namespace SimpleSharing.Tests
{
	[TestClass]
	public class SupportInitializeFixture
	{
		[TestMethod]
		public void ShouldCallInitializeMethods()
		{
			MockInitialize mock = new MockInitialize();

			mock.BeginInit();

			Assert.IsTrue(mock.BeginInitCalled);

			mock.EndInit();

			Assert.IsTrue(mock.EndInitCalled);
		}

		[ExpectedException(typeof(InvalidOperationException))]
		[TestMethod]
		public void ShouldThrowIfEndInitCalledBeforeBeginInit()
		{
			MockInitialize mock = new MockInitialize();

			mock.EndInit();
		}

		[ExpectedException(typeof(InvalidOperationException))]
		[TestMethod]
		public void ShouldThrowIfBeginInitCalledAndAlreadyInitialized()
		{
			MockInitialize mock = new MockInitialize();

			mock.BeginInit();
			mock.EndInit();

			mock.BeginInit();
		}

		[ExpectedException(typeof(InvalidOperationException))]
		[TestMethod]
		public void ShouldThrowIfBeginInitCalledTwice()
		{
			MockInitialize mock = new MockInitialize();

			mock.BeginInit();
			mock.BeginInit();
		}

		[ExpectedException(typeof(InvalidOperationException))]
		[TestMethod]
		public void ShouldThrowIfEndInitCalledAndAlreadyInitialized()
		{
			MockInitialize mock = new MockInitialize();

			mock.BeginInit();
			mock.EndInit();

			mock.EndInit();
		}

		[ExpectedException(typeof(InvalidOperationException))]
		[TestMethod]
		public void ShouldEnsureInitializedThrowIfNotInitialized()
		{
			MockInitialize mock = new MockInitialize();

			mock.Ensure();
		}

		[TestMethod]
		public void ShouldCallValidateProperties()
		{
			MockInitialize mock = new MockInitialize();

			mock.BeginInit();
			mock.EndInit();

			Assert.IsTrue(mock.ValidatePropertiesCalled);
		}

		class MockInitialize : SupportInitialize
		{
			public bool BeginInitCalled;
			public bool EndInitCalled;
			public bool ValidatePropertiesCalled;

			public void Ensure()
			{
				base.EnsureInitialized();
			}

			protected override void OnBeginInit()
			{
				base.OnBeginInit();
				BeginInitCalled = true;
			}

			protected override void OnEndInit()
			{
				base.OnEndInit();
				EndInitCalled = true;
			}

			protected override void ValidateProperties()
			{
				ValidatePropertiesCalled = true;
			}

			internal void BeginInit()
			{
				((ISupportInitialize)this).BeginInit();
			}

			internal void EndInit()
			{
				((ISupportInitialize)this).EndInit();
			}
		}
	}
}
