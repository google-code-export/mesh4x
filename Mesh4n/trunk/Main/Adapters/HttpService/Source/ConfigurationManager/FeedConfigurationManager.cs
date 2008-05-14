using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.Configuration;
using System.IO;
using System.Windows.Markup;
using System.Xml;
using Mesh4n.Adapters.HttpService.Properties;

namespace Mesh4n.Adapters.HttpService.Configuration
{
	public class FeedConfigurationManager : IFeedConfigurationManager
	{
		const string SerializerExtension = "xaml";

		private string configurationPath;
		private IFeedConfigurationCache cache;

		public FeedConfigurationManager()
		{
			this.cache = new FeedConfigurationCache();
		}

		public FeedConfigurationManager(IFeedConfigurationCache cache)
		{
			this.cache = cache;
		}

		public string ConfigurationPath
		{
			get { return this.configurationPath; }
		}

		public void Initialize(NameValueCollection attributes)
		{
			if(String.IsNullOrEmpty(attributes["configurationPath"]))
				throw new ConfigurationErrorsException(Resources.NullOrEmptyConfigurationPath);

			string path = attributes["configurationPath"];
			if (!Path.IsPathRooted(path))
			{
				path = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, path); 
			}
			
			DirectoryInfo di = new DirectoryInfo(path);

			if (!di.Exists)
			{
				Directory.CreateDirectory(di.FullName);
			}

			this.configurationPath = path;
		}

		public FeedConfigurationEntry Load(string feedName)
		{
			Guard.ArgumentNotNullOrEmptyString(feedName, "feedName");

			FeedConfigurationEntry entry = this.cache.GetEntry(feedName);
			if (entry == null)
			{
				string configPath = GetSettingsFile(feedName);
				if (File.Exists(configPath))
				{
					entry = DeserializeFeedEntry(configPath);
					this.cache.AddEntry(configPath, entry);
				}
			}
			return entry;
		}

		public IEnumerable<FeedConfigurationEntry> LoadAll()
		{
			string[] files = Directory.GetFiles(
				this.configurationPath, Path.ChangeExtension("*", SerializerExtension), SearchOption.AllDirectories);
			
			foreach (string file in files)
			{
				FeedConfigurationEntry configurationEntry = DeserializeFeedEntry(file);
				
				this.cache.AddEntry(file, configurationEntry);

				yield return configurationEntry;
			}
		}

		public void Save(FeedConfigurationEntry entry)
		{
			Guard.ArgumentNotNull(entry, "entry");
			Guard.ArgumentIsInstanceOfType(entry, typeof(XamlFeedConfigurationEntry), "entry");

			string configPath = GetSettingsFile(entry.Name);
			SerializeSettings(configPath, entry);

			this.cache.RemoveEntry(entry.Name);
		}

		public void Delete(string feedName)
		{
			Guard.ArgumentNotNullOrEmptyString(feedName, "feedName");

			string configFolder = GetSettingsFolder(feedName);
			if (Directory.Exists(configFolder))
			{
				Directory.Delete(configFolder, true);
			}

			this.cache.RemoveEntry(feedName);
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

		protected string GetSettingsFile(string feedName)
		{
			return Path.Combine(GetSettingsFolder(feedName),
					Path.ChangeExtension(feedName, SerializerExtension));
		}

		protected string GetSettingsFolder(string feedName)
		{
			return Path.Combine(this.configurationPath, feedName);
		}
	}
}
