using System.Xml;

namespace Mesh4n.Adapters.Kml.XmlMerge
{
	public class XmlAdd : XmlNodeCommand
	{
		public override CommandKind Kind
		{
			get { return CommandKind.Add; }
		}

		public XmlNodeType NodeType { get; set; }
	}
}
