/**
 *
 */
package org.mesh4j.ektoo.controller;
import java.io.File;
import java.util.List;

import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.security.NullIdentityProvider;

/**
 * @author Asus
 *
 */
/**
 * @author Asus
 *
 */
public class EktooUIController 
{
	public String sync(File sourceFile, File targetFile)
	{
		return sync(sourceFile, sourceFile, targetFile, targetFile);
	}
	
	public String sync(File sourceContentFile, File sourceSyncFile, File targetContentFile, File targetSyncFile)
	{
		ISyncAdapterBuilder adapterBuilder = new SyncAdapterBuilder();
		ISyncAdapter sourceAdapter = adapterBuilder.createMsExcelAdapter("CONT_INFO", "id", sourceContentFile.getAbsolutePath() , sourceSyncFile.getAbsolutePath(), NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		ISyncAdapter targetAdapter = adapterBuilder.createMsExcelAdapter("CONT_INFO", "id", targetContentFile.getAbsolutePath() , targetSyncFile.getAbsolutePath(), NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);

		SyncEngine engine = new SyncEngine(sourceAdapter, targetAdapter);
		List items = engine.synchronize();
		if (items != null && items.size() > 0)
		{
			return new String("Conflicts");
		}
		else
		{
			return new String("Sync succesfull");
		}
		
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
