
namespace Mesh4n.Adapters.Kml.XmlMerge
{
	public class XmlRemove : XmlCommand
	{
		public override CommandKind Kind
		{
			get { return CommandKind.Remove; }
		}

		public string Child { get; set; }
	}
}
