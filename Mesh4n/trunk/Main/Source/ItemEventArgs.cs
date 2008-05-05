using System;
using System.Collections.Generic;
using System.Text;

namespace Mesh4n
{
	public class ItemEventArgs : EventArgs
	{
		private Item item;

		public ItemEventArgs(Item item)
		{
			Guard.ArgumentNotNull(item, "item");

			this.item = item;
		}

		public Item Item
		{
			get { return item; }
		}
	}
}
