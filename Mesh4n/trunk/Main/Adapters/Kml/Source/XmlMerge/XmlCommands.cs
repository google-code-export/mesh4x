using System;
using System.Collections.Generic;
using System.Xml;

namespace Mesh4n.Adapters.Kml.XmlMerge
{
	public class XmlCommands : List<XmlCommand>
	{
		const string XmlDiffNamespaceURI = "http://schemas.microsoft.com/xmltools/2002/xmldiff";

		public static XmlCommands ReadDiffgram(XmlReader diffgram)
		{
			List<string> pathStack = new List<string>();
			XmlCommands commands = new XmlCommands();

			XmlReader reader = XmlReader.Create(diffgram, new XmlReaderSettings { IgnoreWhitespace = true });
			reader.Read();
			bool skip = false;

			do
			{
				skip = false;
				if (reader.NamespaceURI == XmlDiffNamespaceURI)
				{
					if (reader.LocalName == "node")
					{
						// <xd:node match="1">
						if (reader.NodeType == XmlNodeType.Element)
							pathStack.Add(reader.GetAttribute("match"));
						else if (reader.NodeType == XmlNodeType.EndElement)
							pathStack.RemoveAt(pathStack.Count - 1);
					}
					else if (reader.LocalName == "add" &&
						reader.NodeType == XmlNodeType.Element)
					{
						var add = ReadAdd(reader);

						AddCommandForPath(commands, add, pathStack);

						skip = true;
					}
					else if (reader.LocalName == "remove" &&
						reader.NodeType == XmlNodeType.Element)
					{
						XmlRemove remove = ReadRemove(reader);

						AddCommandForPath(commands, remove, pathStack);

						skip = true;
					}
					else if (reader.LocalName == "change" &&
						reader.NodeType == XmlNodeType.Element)
					{
						var change = ReadChange(reader);

						AddCommandForPath(commands, change, pathStack);

						skip = true;
					}
				}
			} while (!reader.EOF && (skip || reader.Read()));


			return commands;
		}

		private static void AddCommandForPath(XmlCommands commands, XmlCommand command, List<string> pathStack)
		{
			// TODO: add multiple commands for path ranges.
			command.Path = String.Join("/", pathStack.ToArray());

			commands.Add(command);
		}

		private static void AddCommandForPath(XmlCommands commands, XmlRemove remove, List<string> pathStack)
		{
			// TODO: add multiple commands for path ranges in child spec.
			AddCommandForPath(commands, (XmlCommand)remove, pathStack);
		}

		private static XmlCommand ReadAdd(XmlReader reader)
		{
			if (!reader.HasAttributes ||
				(reader.AttributeCount == 1 && reader.MoveToAttribute("opid") && reader.MoveToElement()))
			{
				XmlAddFragment add = new XmlAddFragment();

				ReadOperationId(add, reader);

				XmlDocument doc = new XmlDocument(reader.NameTable);
				doc.Load(reader.ReadSubtree());

				foreach (XmlNode node in doc.DocumentElement.ChildNodes)
				{
					add.Nodes.Add(node);
				}

				return add;
			}
			else
			{
				XmlAdd add = new XmlAdd();

				ReadOperationId(add, reader);
				ReadNodeAttributes(reader, add);

				string value = reader.GetAttribute("type");
				if (!String.IsNullOrEmpty(value))
					add.NodeType = (XmlNodeType)int.Parse(value);

				if (add.NodeType == XmlNodeType.Attribute ||
					add.NodeType == XmlNodeType.XmlDeclaration)
				{
					var content = reader.ReadSubtree();
					content.MoveToContent();
					add.Value = content.ReadElementContentAsString();
				}
				else
				{
					// TODO: not used for now, as we'll just use the 
					// new API to detect conflicting changes.
					//int originalDepth = reader.Depth;
					//while (reader.Read() && reader.Depth > originalDepth &&
					//   reader.NodeType != XmlNodeType.EndElement)
					//{
					//   if (reader.LocalName == "add" &&
					//      reader.NodeType == XmlNodeType.Element)
					//   {
					//      var content = reader.ReadSubtree();
					//      content.MoveToContent();
					//      add.ChildNodes.Add(ReadAdd(content));
					//   }
					//}
				}

				return add;
			}
		}

		private static XmlRemove ReadRemove(XmlReader reader)
		{
			var remove = new XmlRemove();

			ReadOperationId(remove, reader);

			string value = reader.GetAttribute("match");
			if (!String.IsNullOrEmpty(value))
				remove.Child = value;

			if (reader.IsEmptyElement)
			{
				reader.Read();
			}
			else
			{
				// Consume all content.
				var content = reader.ReadSubtree();
				content.MoveToContent();
				content.ReadOuterXml();
			}

			return remove;
		}

		private static XmlChange ReadChange(XmlReader reader)
		{
			var change = new XmlChange();

			ReadOperationId(change, reader);
			ReadNodeAttributes(reader, change);

			string value = reader.GetAttribute("match");
			if (!String.IsNullOrEmpty(value))
				change.Child = value;

			if (reader.IsEmptyElement)
			{
				reader.Read();
			}
			else
			{
				var content = reader.ReadSubtree();
				content.MoveToContent();
				change.Value = content.ReadInnerXml();
			}

			return change;
		}

		private static void ReadOperationId(XmlCommand command, XmlReader reader)
		{
			string value = reader.GetAttribute("opid");
			if (!String.IsNullOrEmpty(value))
				command.OperationId = uint.Parse(value);
		}

		private static void ReadNodeAttributes(XmlReader reader, XmlNodeCommand command)
		{
			string value = reader.GetAttribute("name");
			if (!String.IsNullOrEmpty(value))
				command.LocalName = value;

			value = reader.GetAttribute("ns");
			if (!String.IsNullOrEmpty(value))
				command.NamespaceURI = value;

			value = reader.GetAttribute("prefix");
			if (!String.IsNullOrEmpty(value))
				command.Prefix = value;
		}
	}
}
