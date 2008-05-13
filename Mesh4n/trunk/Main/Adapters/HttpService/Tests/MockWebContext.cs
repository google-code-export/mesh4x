using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Moq;
using System.ServiceModel.Web;

namespace Mesh4n.Adapters.HttpService.Tests
{
	public class MockWebContext : Mock<IWebOperationContext>
	{
		Mock<IIncomingWebRequestContext> requestContextMock = new Mock<IIncomingWebRequestContext>();
		Mock<IOutgoingWebResponseContext> responseContextMock = new Mock<IOutgoingWebResponseContext>();

		public MockWebContext()
			: base()
		{
			this.ExpectGet(webContext => webContext.OutgoingResponse).Returns(responseContextMock.Object);
			this.ExpectGet(webContext => webContext.IncomingRequest).Returns(requestContextMock.Object);
		}

		public Mock<IIncomingWebRequestContext> IncomingRequest
		{
			get { return requestContextMock; }
		}

		public Mock<IOutgoingWebResponseContext> OutgoingResponse
		{
			get { return responseContextMock; }
		}

		public override void Verify()
		{
			base.Verify();
			requestContextMock.Verify();
			responseContextMock.Verify();
		}

		public override void VerifyAll()
		{
			base.VerifyAll();
			requestContextMock.VerifyAll();
			responseContextMock.VerifyAll();
		}
	}
}
