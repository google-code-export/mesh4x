using System;
using System.Collections.Specialized;
using System.Configuration;
using Mesh4n.Adapters.HttpService.Configuration;
using Mesh4n.Adapters.HttpService.Properties;

namespace Mesh4n.Adapters.HttpService
{
	public class SyncServiceConfigurationSection : ConfigurationSection
	{
		public const string SectionName = "feedSyncService";

		/// <summary>
		/// Gets the configuration manager configuration
		/// </summary>
		[ConfigurationProperty("feedConfigurationManager", IsRequired=true)]
		public ConfigurationManagerElement ConfigurationManager
		{
			get { return (ConfigurationManagerElement)base["feedConfigurationManager"]; }
		}

		public static SyncServiceConfigurationSection GetSection()
		{
			return (SyncServiceConfigurationSection)System.Configuration.ConfigurationManager.GetSection(SectionName);
		}

		public static IFeedConfigurationManager GetConfigurationManager()
		{
			SyncServiceConfigurationSection section = GetSection();
			
			Type type = Type.GetType(section.ConfigurationManager.TypeName, true, true);
			IFeedConfigurationManager manager = (IFeedConfigurationManager)Activator.CreateInstance(type) as IFeedConfigurationManager;

			if (manager == null)
			{
				throw new ArgumentException(string.Format(
					Resources.InvalidConfigurationManagerType, type.AssemblyQualifiedName));
			}

			manager.Initialize(section.ConfigurationManager.Attributes);

			return manager;
		}
	}

	public class ConfigurationManagerElement : ConfigurationElement
	{
		NameValueCollection attributes = new NameValueCollection();
		
		public ConfigurationManagerElement()
			: base()
		{

		}

		/// <summary>
		/// Gets the implementation type
		/// </summary>
		[ConfigurationProperty("typeName", IsRequired = true)]
		public string TypeName
		{
			get { return (string)base["typeName"]; }
		}

		/// <summary>
		/// Gets a collection of custom attributes
		/// </summary>
		public NameValueCollection Attributes
		{
			get { return attributes; }
		}

		protected override bool OnDeserializeUnrecognizedAttribute(string name, string value)
		{
			if (this.attributes[name] != null)
				throw new InvalidOperationException(string.Format("Duplicated attribute {0}", 
					name));

			this.attributes.Add(name, value);
			return true;
		}
	}
}
