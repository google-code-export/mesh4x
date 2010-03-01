package org.mesh4j.sync.adapters.folder;

import java.io.File;

import org.mesh4j.sync.adapters.file.FileSyncRepository;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class FolderSyncAdapterFactory {

	public static SplitAdapter createFolderAdapter(String folderName, IIdentityProvider identityProvider, IIdGenerator idGenerator) {
		Guard.argumentNotNullOrEmptyString(folderName, "folderName");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(idGenerator, "idGenerator");
		
		try{
			File folder = new File(folderName);
			if(!folder.exists()){
				folder.mkdirs();
			}
			
			File syncFile = new File(folder.getCanonicalPath() + File.separator + folder.getName() + "_sync.xml");
				
			FilesFilter filter = new FilesFilter();
			filter.excludeFileName(syncFile.getName());
			FolderContentAdapter folderContent = new FolderContentAdapter(folder, filter);
			FileSyncRepository syncRepo = new FileSyncRepository(syncFile, identityProvider, idGenerator);
			SplitAdapter adapter = new SplitAdapter(syncRepo, folderContent,identityProvider);
			return adapter;
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}	
}
