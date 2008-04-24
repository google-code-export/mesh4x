using System;
using System.Collections.Generic;
using System.Windows.Forms;
using System.IO;
using System.Data.SqlServerCe;
using System.Threading;
using System.Security.Principal;
using Microsoft.Practices.EnterpriseLibrary.Data;
using System.Data.Common;

namespace CustomerSample
{
	static class Program
	{
		/// <summary>
		/// The main entry point for the application.
		/// </summary>
		[STAThread]
		static void Main()
		{
			AppDomain.CurrentDomain.SetPrincipalPolicy(PrincipalPolicy.WindowsPrincipal);

			// Initialize SyncDB
			DbConnection cn = DatabaseFactory.CreateDatabase("SyncDB").CreateConnection();
			if (!File.Exists(cn.Database))
				new SqlCeEngine(cn.ConnectionString).CreateDatabase();

			cn = DatabaseFactory.CreateDatabase("CustomerDB").CreateConnection();
			if (!File.Exists(cn.Database))
				new SqlCeEngine(cn.ConnectionString).CreateDatabase();

			Application.EnableVisualStyles();
			Application.SetCompatibleTextRenderingDefault(false);
			Application.Run(new MainForm());
		}
	}
}