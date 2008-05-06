using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Windows.Markup;
using Mesh4n.Adapters.HttpService.Properties;
using System.Xml;
using System.Collections.Specialized;

namespace Mesh4n.Adapters.HttpService.Configuration
{
	public class FeedConfigurationManager : IFeedConfigurationManager
	{
		private string configurationPath;

		public FeedConfigurationManager()
		{
		}

		public string ConfigurationPath
		{
			get { return this.configurationPath; }
		}

		public void Initialize(NameValueCollection attributes)
		{
			Guard.ArgumentNotNullOrEmptyString(attributes["configurationPath"], "configurationPath");
			
			this.configurationPath = attributes["configurationPath"];
		}

		public void Save(FeedConfiguration configuration)
		{
			using (FileStream stream = new FileStream(this.configurationPath, FileMode.Create))
			{
				XmlWriterSettings settings = new XmlWriterSettings();
				settings.Indent = true;
				settings.CloseOutput = true;

				using (XmlWriter writer = XmlWriter.Create(stream, settings))
				{
					XamlWriter.Save(configuration, writer);
				}
			}
		}

		public FeedConfiguration Load()
		{
			using (FileStream stream = new FileStream(this.configurationPath, FileMode.Open))
			{
				try
				{
					return XamlReader.Load(stream) as FeedConfiguration;
				}
				catch (Exception ex)
				{
					throw new ArgumentException(String.Format(
						Resources.InvalidConfigurationFile,
						this.configurationPath,
						ex.Message),
						ex);
				}
			}
		}

		
	}
}
