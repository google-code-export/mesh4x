
namespace Mesh4n.Adapters.Kml.XmlMerge
{
	public abstract class XmlCommand
	{
		public abstract CommandKind Kind { get; }
		public uint OperationId { get; set; }
		public string Path { get; set; }
	}
}
