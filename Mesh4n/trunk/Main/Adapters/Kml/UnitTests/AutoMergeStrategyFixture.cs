using System;
using System.Collections.Generic;
using System.Text;
using NUnit.Framework;
using Microsoft.XmlDiffPatch;
using System.Xml;
using System.IO;

namespace Mesh4n.Adapters.Kml.Tests
{
	[TestFixture]
	public class AutoMergeStrategyFixture
	{
		[Test]
		public void ShouldDiff()
		{
			string a = @"
<Placemark>
	<name>Foo</name>
	<foo name='1'>foo</foo>
	<description/>
</Placemark>
";

			string b = @"<?xml version='1.0' encoding='utf-8' ?>
<Placemark xml:id='asdf'>
	<name>FooBar</name>
	<foo id='1'><bar/>baz</foo>
	<description>The famous foo!</description>
	<icon url='http://mesh4x.org/icon.png'>An icon</icon>
</Placemark>
";

			string c = @"
<Placemark>
	<foo/>
	<description/>
</Placemark>
";

			XmlDiff xmldiff = new XmlDiff(
				XmlDiffOptions.IgnoreChildOrder |
				XmlDiffOptions.IgnoreDtd |
				XmlDiffOptions.IgnorePrefixes);
			xmldiff.Algorithm = XmlDiffAlgorithm.Precise;

			XmlDocument local = new XmlDocument();

			using (XmlWriter writer = XmlWriter.Create(local.CreateNavigator().AppendChild(), new XmlWriterSettings { Indent = true }))
			{
				Console.WriteLine("Are equal: " + xmldiff.Compare(XmlReader.Create(new StringReader(a)), XmlReader.Create(new StringReader(b)), writer));
			}

			using (XmlWriter w = XmlWriter.Create(Console.Out, new XmlWriterSettings { Indent = true }))
			{
				local.WriteTo(w);
			}
			Console.WriteLine(new string('-', 100));

			XmlDocument incoming = new XmlDocument();

			using (XmlWriter writer = XmlWriter.Create(incoming.CreateNavigator().AppendChild(), new XmlWriterSettings { Indent = true }))
			{
				Console.WriteLine("Are equal: " + xmldiff.Compare(XmlReader.Create(new StringReader(a)), XmlReader.Create(new StringReader(c)), writer));
			}

			//foreach (XmlNode node in local.DocumentElement.ChildNodes)
			//{
			//   using (XmlWriter nodeWriter = incoming.DocumentElement.CreateNavigator().AppendChild())
			//   {
			//      nodeWriter.WriteNode(new XmlNodeReader(node), false);
			//   }
			//}

			//using (XmlWriter w = XmlWriter.Create(Console.Out, new XmlWriterSettings { Indent = true }))
			//{
			//   incoming.WriteTo(w);
			//}

			//XmlDocument original = new XmlDocument();
			//original.LoadXml(a);

			//XmlPatch patch = new XmlPatch();

			//patch.Patch(original, new XmlNodeReader(incoming));

			//using (XmlWriter w = XmlWriter.Create(Console.Out, new XmlWriterSettings { Indent = true }))
			//{
			//   original.WriteTo(w);
			//}
		}
	}
}
