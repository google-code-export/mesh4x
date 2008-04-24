using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.XPath;
using System.IO;
using System.Configuration;
using System.Xml;

namespace SimpleSharing.Tests
{
	public class MockXmlRepository : IXmlRepository
	{
        Dictionary<string, IXmlItem> items = new Dictionary<string, IXmlItem>();

        private XmlElement GetElement(string xml)
        {
            XmlDocument doc = new XmlDocument();
            doc.LoadXml(xml);
            return doc.DocumentElement;
        }

        public MockXmlRepository AddOneItem()
        {
            string id = Guid.NewGuid().ToString();
            items.Add(id, new XmlItem(id,
                "Foo Title", "Foo Description",
                GetElement("<Foo Title='Foo'/>"), DateTime.Now));

            return this;
        }

        public MockXmlRepository AddTwoItems()
        {
            string id = Guid.NewGuid().ToString();
            items.Add(id, new XmlItem(id,
                "Foo Title", "Foo Description",
				GetElement("<Foo Title='Foo'/>"), DateTime.Now));

            id = Guid.NewGuid().ToString();
            items.Add(id, new XmlItem(id,
                "Bar Title", "Bar Description",
				GetElement("<Foo Title='Foo'/>"), DateTime.Now));

            return this;
        }

        public MockXmlRepository AddThreeItemsByDays()
        {
            string id = Guid.NewGuid().ToString();
            items.Add(id, new XmlItem(id,
                "Foo Title", "Foo Description",
				GetElement("<Foo Title='Foo'/>"), DateTime.Now));

            id = Guid.NewGuid().ToString();
            items.Add(id, new XmlItem(id,
                "Bar Title", "Bar Description",
				GetElement("<Foo Title='Foo'/>"), DateTime.Now.Subtract(TimeSpan.FromDays(1))));

            id = Guid.NewGuid().ToString();
            items.Add(id, new XmlItem(id,
                "Baz Title", "Baz Description",
				GetElement("<Foo Title='Foo'/>"), DateTime.Now.Subtract(TimeSpan.FromDays(3))));

            return this;
        }

        public void Add(IXmlItem item, out object tag)
        {
            Guard.ArgumentNotNull(item, "item");
            Guard.ArgumentNotNullOrEmptyString(item.Id, "item.Id");

            IXmlItem clone = item.Clone();

			tag = DateTime.Now;
			clone.Tag = tag;

            items.Add(item.Id, clone);


        }

        public bool Contains(string id)
        {
            Guard.ArgumentNotNullOrEmptyString(id, "id");

            return items.ContainsKey(id);
        }

        public IXmlItem Get(string id)
        {
            Guard.ArgumentNotNullOrEmptyString(id, "id");

            if (items.ContainsKey(id))
            {
                return items[id].Clone();
            }

            return null;
        }

        public bool Remove(string id)
        {
            return items.Remove(id);
        }

        public void Update(IXmlItem item, out object tag)
        {
            Guard.ArgumentNotNull(item, "item");
            Guard.ArgumentNotNullOrEmptyString(item.Id, "item.Id");

            if (!items.ContainsKey(item.Id))
                throw new KeyNotFoundException();

			tag = DateTime.Now;
			IXmlItem clone = item.Clone();
			clone.Tag = tag;

            items[item.Id] = clone;
        }

        public IEnumerable<IXmlItem> GetAll()
        {
            foreach (IXmlItem item in items.Values)
            {
                yield return item.Clone();
            }
        }

        public IEnumerable<IXmlItem> GetAllSince(DateTime date)
        {
            foreach (IXmlItem item in items.Values)
            {
                if (((DateTime)item.Tag) >= date)
                    yield return item.Clone();
            }
        }

		public DateTime GetFirstUpdated()
		{
			if (items.Count == 0) return DateTime.MinValue;

			DateTime first = DateTime.MaxValue;

			foreach (IXmlItem item in items.Values)
			{
				if (((DateTime)item.Tag) < first)
					first = (DateTime)item.Tag;
			}

			return first;
		}

		public DateTime GetFirstUpdated(DateTime since)
		{
			if (items.Count == 0) return since;

			DateTime first = DateTime.MaxValue;

			foreach (IXmlItem item in items.Values)
			{
				if (((DateTime)item.Tag) < first && ((DateTime)item.Tag) > since)
					first = (DateTime)item.Tag;
			}

			return first;
		}

		public DateTime GetLastUpdated()
		{
			if (items.Count == 0) return DateTime.Now;

			DateTime last = DateTime.MinValue;

			foreach (IXmlItem item in items.Values)
			{
				if (((DateTime)item.Tag) > last)
					last = (DateTime)item.Tag;
			}

			return last;
        }
    }
}
