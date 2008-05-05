using System;
using System.Xml;

namespace Mesh4n
{
    [Serializable]
    public class XmlItem : IXmlItem
    {
        private string id;
        private string description;
        private string title;
		private object tag;

        private XmlElement payload;

        public XmlItem(string title, string description, XmlElement payload, object tag)
            : this(Guid.NewGuid().ToString(), title, description, payload, tag)
        {
        }

		public XmlItem(string title, string description, XmlElement payload)
			: this(Guid.NewGuid().ToString(), title, description, payload)
		{
		}


		public XmlItem(string id, string title, string description, XmlElement payload)
			: this(id, title, description, payload, null)
		{
		}

		public XmlItem(string id, string title, string description, XmlElement payload, object tag)
        {
            Guard.ArgumentNotNullOrEmptyString(id, "id");

            if (payload == null)
            {
                payload = new XmlDocument().CreateElement("payload");
            }

            this.id = id;
            this.title = title;
            this.description = description;
			this.tag = tag;
            this.payload = payload;
        }

        public string Id
        {
            get { return id; }
            set
            {
                Guard.ArgumentNotNullOrEmptyString(value, "Id");
                id = value;
            }
        }

        public string Title
        {
            get { return title; }
            set { title = value; }
        }

        public string Description
        {
            get { return description; }
            set { description = value; }
        }

        public XmlElement Payload
        {
            get { return payload; }
            set
            {
                Guard.ArgumentNotNull(value, "Payload");
                payload = value;
            }
        }

		public object Tag
		{
			get { return tag; }
			set
			{
				Guard.ArgumentNotNull(value, "Tag");
				tag = value;
			}
		}

        #region Equality

        public bool Equals(IXmlItem other)
        {
            return XmlItem.Equals(this, other);
        }

        public override bool Equals(object obj)
        {
            return XmlItem.Equals(this, obj as IXmlItem);
        }

        public static bool Equals(IXmlItem obj1, IXmlItem obj2)
        {
            if (Object.ReferenceEquals(obj1, obj2))
                return true;
            if (!Object.Equals(null, obj1) && !Object.Equals(null, obj2))
            {
                return obj1.Id == obj2.Id &&
                    obj1.Title == obj2.Title &&
                    obj1.Description == obj2.Description &&
					obj1.Tag == obj2.Tag &&
                    obj1.Payload.OuterXml == obj2.Payload.OuterXml;
            }

            return false;
        }

        public override int GetHashCode()
        {
			string resultingPayload = id.ToString() +
				((title != null) ? title : "") +
				((description != null) ? description : "") +
				payload.OuterXml;

			return resultingPayload.GetHashCode();
        }

        #endregion

        #region ICloneable Members

        object ICloneable.Clone()
        {
            return DoClone();
        }

        public IXmlItem Clone()
        {
            return DoClone();
        }

        protected virtual IXmlItem DoClone()
        {
            return new XmlItem(id, title, description, payload, tag);
        }

        #endregion
    }
}