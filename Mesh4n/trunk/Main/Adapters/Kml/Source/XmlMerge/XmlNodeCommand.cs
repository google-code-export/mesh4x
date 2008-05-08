
namespace Mesh4n.Adapters.Kml.XmlMerge
{
	public abstract class XmlNodeCommand : XmlCommand
	{
		public string LocalName { get; set; }
		public string NamespaceURI { get; set; }
		public string Prefix { get; set; }
		public string Value { get; set; }
	}
}
