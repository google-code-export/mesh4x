using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Windows.Markup;
using Mesh4n.Adapters.HttpService.Properties;
using System.Xml;
using System.Collections.Specialized;
using System.Configuration;

namespace Mesh4n.Adapters.HttpService.Configuration
{
	public class FeedConfigurationManager : IFeedConfigurationManager
	{
		const string SerializerExtension = "xaml";

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
			if(String.IsNullOrEmpty(attributes["configurationPath"]))
				throw new ConfigurationErrorsException(Resources.NullOrEmptyConfigurationPath);

			DirectoryInfo di = new DirectoryInfo(attributes["configurationPath"]);
			
			if (!di.Exists)
			{
				Directory.CreateDirectory(di.FullName);
			}

			this.configurationPath = attributes["configurationPath"];
		}

		public void Save(FeedConfigurationEntry entry)
		{
			Guard.ArgumentNotNull(entry, "entry");
			Guard.ArgumentIsInstanceOfType(entry, typeof(XamlFeedConfigurationEntry), "entry");

			string configPath = GetSettingsFile(entry.Name);
			SerializeSettings(configPath, entry);
		}

		public FeedConfigurationEntry Load(string feedName)
		{
			Guard.ArgumentNotNullOrEmptyString(feedName, "feedName");
			
			string configPath = GetSettingsFile(feedName);
			if (File.Exists(configPath))
			{
				return DeserializeFeedEntry(configPath);
			}
			return null;
		}

		public IEnumerable<FeedConfigurationEntry> LoadAll()
		{
			string[] files = Directory.GetFiles(
				this.configurationPath, Path.ChangeExtension("*", SerializerExtension), SearchOption.AllDirectories);
			
			foreach (string file in files)
			{
				FeedConfigurationEntry configurationEntry = DeserializeFeedEntry(file);
				yield return configurationEntry;
			}
		}

		private FeedConfigurationEntry DeserializeFeedEntry(string filePath)
		{
			using (FileStream stream = new FileStream(filePath, FileMode.Open))
			{
				try
				{
					return XamlReader.Load(stream) as FeedConfigurationEntry;
				}
				catch (Exception ex)
				{
					throw new ArgumentException(String.Format(
						Resources.InvalidConfigurationFile,
						filePath,
						ex.Message),
						ex);
				}
			}
		}

		private void SerializeSettings(string filePath, FeedConfigurationEntry entry)
		{
			string dir = Path.GetDirectoryName(filePath);
			if (!Directory.Exists(dir))
				Directory.CreateDirectory(dir);

			using (FileStream stream = new FileStream(filePath, FileMode.Create))
			{
				XmlWriterSettings settings = new XmlWriterSettings();
				settings.Indent = true;
				settings.NewLineOnAttributes = true;

				using (XmlWriter writer = XmlWriter.Create(stream, settings))
				{
					XamlWriter.Save(entry, writer);
				}
			}
		}

		public string GetSettingsFile(string feedName)
		{
			return Path.Combine(this.configurationPath,
				Path.Combine(
					feedName,
					Path.ChangeExtension(feedName, SerializerExtension)
				)
			);
		}
	}
}
