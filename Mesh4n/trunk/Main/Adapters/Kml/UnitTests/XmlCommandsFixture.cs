using System;
using System.Collections.Generic;
using System.Text;
using NUnit.Framework;
using Microsoft.XmlDiffPatch;
using System.Xml;
using System.IO;
using Mesh4n.Adapters.Kml.XmlMerge;

namespace Mesh4n.Adapters.Kml.Tests
{
	[TestFixture]
	public class XmlCommandsFixture
	{
		[Test]
		public void ShouldParseAddFragment()
		{
			var diff = @"
<xd:xmldiff version='1.0' srcDocHash='474585123008075885' options='IgnoreChildOrder IgnorePrefixes IgnoreDtd ' fragments='no' xmlns:xd='http://schemas.microsoft.com/xmltools/2002/xmldiff'>
  <xd:node match='1'>
    <xd:add>
      <icon url='http://mesh4x.org/icon.png'>An icon</icon>
    </xd:add>
  </xd:node>
</xd:xmldiff>";

			var cmds = XmlCommands.ReadDiffgram(XmlReader.Create(new StringReader(diff)));

			Assert.AreEqual(1, cmds.Count);
			Assert.AreEqual("1", cmds[0].Path);
			Assert.AreEqual(CommandKind.Add, cmds[0].Kind);
		}

		[Test]
		public void ShouldParseAddAttribute()
		{
			var diff = @"
<xd:xmldiff version='1.0' srcDocHash='474585123008075885' options='IgnoreChildOrder IgnorePrefixes IgnoreDtd ' fragments='no' xmlns:xd='http://schemas.microsoft.com/xmltools/2002/xmldiff'>
  <xd:node match='1'>
    <xd:add type='2' name='id'>1</xd:add>
  </xd:node>
</xd:xmldiff>";

			var cmds = XmlCommands.ReadDiffgram(XmlReader.Create(new StringReader(diff)));

			Assert.AreEqual(1, cmds.Count);
			Assert.AreEqual("1", cmds[0].Path);
			Assert.AreEqual(CommandKind.Add, cmds[0].Kind);

			var add = cmds[0] as XmlAdd;
			Assert.IsNotNull(add, "Command was not an XmlAdd");

			Assert.AreEqual(XmlNodeType.Attribute, add.NodeType);
			Assert.AreEqual("id", add.LocalName);
			Assert.AreEqual("1", add.Value);
		}

		[Test]
		public void ShouldParseAddNestedNode()
		{
			var diff = @"
<xd:xmldiff version='1.0' srcDocHash='474585123008075885' options='IgnoreChildOrder IgnorePrefixes IgnoreDtd ' fragments='no' xmlns:xd='http://schemas.microsoft.com/xmltools/2002/xmldiff'>
	<xd:node match='1'>
		<xd:node match='3'>
			<xd:add type='2' name='id'>1</xd:add>
		</xd:node>
	</xd:node>
</xd:xmldiff>";

			var cmds = XmlCommands.ReadDiffgram(XmlReader.Create(new StringReader(diff)));

			Assert.AreEqual(1, cmds.Count);
			Assert.AreEqual("1/3", cmds[0].Path);
			Assert.AreEqual(CommandKind.Add, cmds[0].Kind);
		}

		[Test]
		public void ShouldParseRemove()
		{
			var diff = @"
<xd:xmldiff version='1.0' srcDocHash='474585123008075885' options='IgnoreChildOrder IgnorePrefixes IgnoreDtd ' fragments='no' xmlns:xd='http://schemas.microsoft.com/xmltools/2002/xmldiff'>
	<xd:node match='1'>
			<xd:remove match='2' />
	</xd:node>
</xd:xmldiff>";

			var cmds = XmlCommands.ReadDiffgram(XmlReader.Create(new StringReader(diff)));

			Assert.AreEqual(1, cmds.Count);
			Assert.AreEqual("1", cmds[0].Path);
			Assert.AreEqual(CommandKind.Remove, cmds[0].Kind);
			Assert.IsTrue(cmds[0] is XmlRemove);
			Assert.AreEqual("2", ((XmlRemove)cmds[0]).Child);
		}

		[Test]
		public void ShouldParseRemoveWithChildElements()
		{
			var diff = @"
<xd:xmldiff version='1.0' srcDocHash='474585123008075885' options='IgnoreChildOrder IgnorePrefixes IgnoreDtd ' fragments='no' xmlns:xd='http://schemas.microsoft.com/xmltools/2002/xmldiff'>
	<xd:node match='1'>
			<xd:remove match='2' subtree='no'>
				<xd:add type='2' name='id'>1001</xd:add>
			</xd:remove>
	</xd:node>
</xd:xmldiff>";

			var cmds = XmlCommands.ReadDiffgram(XmlReader.Create(new StringReader(diff)));

			Assert.AreEqual(1, cmds.Count);
			Assert.AreEqual("1", cmds[0].Path);
			Assert.AreEqual(CommandKind.Remove, cmds[0].Kind);
			Assert.IsTrue(cmds[0] is XmlRemove);
			Assert.AreEqual("2", ((XmlRemove)cmds[0]).Child);
		}

		[Test]
		public void ShouldParseRemoveNestedNode()
		{
			var diff = @"
<xd:xmldiff version='1.0' srcDocHash='474585123008075885' options='IgnoreChildOrder IgnorePrefixes IgnoreDtd ' fragments='no' xmlns:xd='http://schemas.microsoft.com/xmltools/2002/xmldiff'>
	<xd:node match='1'>
		<xd:node match='3'>
			<xd:remove match='2' />
		</xd:node>
	</xd:node>
</xd:xmldiff>";

			var cmds = XmlCommands.ReadDiffgram(XmlReader.Create(new StringReader(diff)));

			Assert.AreEqual(1, cmds.Count);
			Assert.AreEqual("1/3", cmds[0].Path);
			Assert.AreEqual(CommandKind.Remove, cmds[0].Kind);
			Assert.IsTrue(cmds[0] is XmlRemove);
			Assert.AreEqual("2", ((XmlRemove)cmds[0]).Child);
		}

		[Test]
		public void ShouldParseChange()
		{
			var diff = @"
<xd:xmldiff version='1.0' srcDocHash='474585123008075885' options='IgnoreChildOrder IgnorePrefixes IgnoreDtd ' fragments='no' xmlns:xd='http://schemas.microsoft.com/xmltools/2002/xmldiff'>
	<xd:node match='1'>
			<xd:change match='2' ns='http://mesh4x.org' name='newname' prefix='f'>value</xd:change>
	</xd:node>
</xd:xmldiff>";

			var cmds = XmlCommands.ReadDiffgram(XmlReader.Create(new StringReader(diff)));

			Assert.AreEqual(1, cmds.Count);
			Assert.AreEqual("1", cmds[0].Path);
			Assert.AreEqual(CommandKind.Change, cmds[0].Kind);

			var change = cmds[0] as XmlChange;
			Assert.IsNotNull(change, "Was not a change");
			
			Assert.AreEqual("2", change.Child);
			Assert.AreEqual("http://mesh4x.org", change.NamespaceURI);
			Assert.AreEqual("newname", change.LocalName);
			Assert.AreEqual("f", change.Prefix);
			Assert.AreEqual("value", change.Value);
		}

		[Test]
		public void ShouldParseChangeWithChildElements()
		{
			var diff = @"
<xd:xmldiff version='1.0' srcDocHash='474585123008075885' options='IgnoreChildOrder IgnorePrefixes IgnoreDtd ' fragments='no' xmlns:xd='http://schemas.microsoft.com/xmltools/2002/xmldiff'>
	<xd:node match='1'>
			<xd:change match='2' ns='http://mesh4x.org' name='newname' prefix='f'>
				<xd:add type='2' name='id'>1001</xd:add>
			</xd:change>
	</xd:node>
</xd:xmldiff>";

			var cmds = XmlCommands.ReadDiffgram(XmlReader.Create(new StringReader(diff)));

			Assert.AreEqual(1, cmds.Count);
			Assert.AreEqual("1", cmds[0].Path);
			Assert.AreEqual(CommandKind.Change, cmds[0].Kind);

			var change = cmds[0] as XmlChange;
			Assert.IsNotNull(change, "Was not a change");

			Assert.AreEqual("2", change.Child);
			Assert.AreEqual("http://mesh4x.org", change.NamespaceURI);
			Assert.AreEqual("newname", change.LocalName);
			Assert.AreEqual("f", change.Prefix);
		}

		[Test]
		public void ShouldParseChangeNestedNode()
		{
			var diff = @"
<xd:xmldiff version='1.0' srcDocHash='474585123008075885' options='IgnoreChildOrder IgnorePrefixes IgnoreDtd ' fragments='no' xmlns:xd='http://schemas.microsoft.com/xmltools/2002/xmldiff'>
	<xd:node match='1'>
		<xd:node match='3'>
			<xd:change match='2' ns='http://mesh4x.org' name='newname' prefix='f'>value</xd:change>
		</xd:node>
	</xd:node>
</xd:xmldiff>";

			var cmds = XmlCommands.ReadDiffgram(XmlReader.Create(new StringReader(diff)));

			Assert.AreEqual(1, cmds.Count);
			Assert.AreEqual("1/3", cmds[0].Path);
			Assert.AreEqual(CommandKind.Change, cmds[0].Kind);

			var change = cmds[0] as XmlChange;
			Assert.IsNotNull(change, "Was not a change");

			Assert.AreEqual("2", change.Child);
			Assert.AreEqual("http://mesh4x.org", change.NamespaceURI);
			Assert.AreEqual("newname", change.LocalName);
			Assert.AreEqual("f", change.Prefix);
			Assert.AreEqual("value", change.Value);
		}

		string samplediff = @"
<xd:xmldiff version='1.0' srcDocHash='474585123008075885' options='IgnoreChildOrder IgnorePrefixes IgnoreDtd ' fragments='no' xmlns:xd='http://schemas.microsoft.com/xmltools/2002/xmldiff'>
  <xd:add type='17'>version='1.0' encoding='utf-8'</xd:add>
  <xd:node match='1'>
    <xd:add type='2' name='id' ns='http://www.w3.org/XML/1998/namespace' prefix='xml'>asdf</xd:add>
    <xd:node match='3'>
      <xd:add>The famous foo!</xd:add>
    </xd:node>
    <xd:node match='2'>
      <xd:remove match='@name' />
      <xd:add type='2' name='id'>1</xd:add>
      <xd:add>
        <bar />
      </xd:add>
      <xd:change match='1'>baz</xd:change>
    </xd:node>
    <xd:add>
      <icon url='http://mesh4x.org/icon.png'>An icon</icon>
    </xd:add>
    <xd:node match='1'>
      <xd:change match='1'>FooBar</xd:change>
    </xd:node>
  </xd:node>
</xd:xmldiff>";

		[Test]
		[Ignore]
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

		[Ignore("Simplified")]
		[Test]
		public void ShouldParseAddWithNestedAdds()
		{
			var diff = @"
<xd:xmldiff version='1.0' srcDocHash='474585123008075885' options='IgnoreChildOrder IgnorePrefixes IgnoreDtd ' fragments='no' xmlns:xd='http://schemas.microsoft.com/xmltools/2002/xmldiff'>
	<xd:node match='1'>
		<xd:add type='1' name='data'>
			<xd:add type='2' name='id'>1</xd:add>
		</xd:add>
	</xd:node>
</xd:xmldiff>";

			var cmds = XmlCommands.ReadDiffgram(XmlReader.Create(new StringReader(diff)));

			Assert.AreEqual(1, cmds.Count);
			Assert.AreEqual("1", cmds[0].Path);
			Assert.AreEqual(CommandKind.Add, cmds[0].Kind);

			var add = cmds[0] as XmlAdd;
			Assert.IsNotNull(add, "Command was not an XmlAdd");

			Assert.AreEqual(XmlNodeType.Element, add.NodeType);
			Assert.AreEqual("data", add.LocalName);
			//Assert.AreEqual(1, add.ChildNodes.Count);

			//var child = add.ChildNodes[0] as XmlAdd;
			//Assert.IsNotNull(child, "Command was not an XmlAdd");

			//Assert.AreEqual(XmlNodeType.Attribute, child.NodeType);
			//Assert.AreEqual("id", child.LocalName);
			//Assert.AreEqual("1", child.Value);
		}
	}
}
