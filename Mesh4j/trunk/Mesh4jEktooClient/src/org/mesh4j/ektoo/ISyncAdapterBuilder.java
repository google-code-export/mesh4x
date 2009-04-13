package org.mesh4j.ektoo;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.security.IIdentityProvider;

public interface ISyncAdapterBuilder {

	/**
	 * creates adapter for excel repository
	 * 
	 * @param sheetName, the physical sheet name of the excel file 
	 * @param idColumnName, the name of the identity column of entity
	 * @param contentFileName,the content file which actually contains the provided sheet
	 * @param syncFileName,the sync file which actually contains the
	 * @param identityProvider
	 * @param idGenerator
	 * @return ISyncAdapter   
	 */
	public ISyncAdapter createMsExcelAdapter(String sheetName, String idColumnName, 
										String contentFileName, String syncFileName, 
										IIdentityProvider identityProvider, IdGenerator idGenerator); 
}
