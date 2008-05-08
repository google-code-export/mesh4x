using System.Collections.Generic;
using System.Xml;

namespace Mesh4n.Adapters.Kml.XmlMerge
{
	public class XmlAddFragment : XmlCommand
	{
		public XmlAddFragment()
		{
			Nodes = new List<XmlNode>();
		}

		public override CommandKind Kind
		{
			get { return CommandKind.Add; }
		}

		public IList<XmlNode> Nodes { get; private set; }
	}
}
