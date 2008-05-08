
namespace Mesh4n.Adapters.Kml.XmlMerge
{
	public class XmlChange : XmlNodeCommand
	{
		public override CommandKind Kind
		{
			get { return CommandKind.Change; }
		}

		public string Child { get; set; }
	}
}
