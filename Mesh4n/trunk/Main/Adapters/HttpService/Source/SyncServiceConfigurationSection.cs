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

		private static volatile IFeedConfigurationManager managerInstance = null;

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
			if (managerInstance == null)
			{
				lock (typeof(SyncServiceConfigurationSection))
				{
					if (managerInstance == null)
					{
						SyncServiceConfigurationSection section = GetSection();

						Type type = Type.GetType(section.ConfigurationManager.TypeName, true, true);
						managerInstance = (IFeedConfigurationManager)Activator.CreateInstance(type) as IFeedConfigurationManager;

						if (managerInstance == null)
						{
							throw new ArgumentException(string.Format(
								Resources.InvalidConfigurationManagerType, type.AssemblyQualifiedName));
						}

						managerInstance.Initialize(section.ConfigurationManager.Attributes);
					}
				}
			}

			return managerInstance;      
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
