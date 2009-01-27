package org.mesh4j.sync.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.mesh4j.sync.adapters.msaccess.MsAccessSyncAdapterFactory;
import org.mesh4j.sync.mappings.DataSourceMapping;
import org.mesh4j.sync.message.core.repository.ISourceIdMapper;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class SourceIdMapper implements ISourceIdMapper {

	// MODEL VARIABLES
	private String fileName;
	private ArrayList<DataSourceMapping> dataSourceMappings = new ArrayList<DataSourceMapping>();
	
	// BUSINESS METHODS
	public SourceIdMapper(String fileName) {
		Guard.argumentNotNullOrEmptyString(fileName, "fileName");
		this.fileName = fileName;
		load();
	}

	public String getSourceName(String fileName, String mdbTableName) {
		for (DataSourceMapping dataSourceMapping : this.dataSourceMappings) {
			if(dataSourceMapping.getFileName().equals(fileName) && dataSourceMapping.getTableName().equals(mdbTableName)){
				return dataSourceMapping.getAlias();
			}
		}
		return null;
	}

	public ArrayList<DataSourceMapping> getDataSourceMappings() {
		ArrayList<DataSourceMapping> result = new ArrayList<DataSourceMapping>();
		for (DataSourceMapping dataSourceMapping : this.dataSourceMappings) {
			result.add(
				new DataSourceMapping(
						dataSourceMapping.getAlias(), 
						dataSourceMapping.getMDBName(),
						dataSourceMapping.getTableName(),
						dataSourceMapping.getFileName()));
		}
		return result;
	}
	public boolean isDataSourceAvailable(String alias) {
		return getDataSource(alias) != null;
	}

	public void saveDataSourceMapping(DataSourceMapping dataSourceMapping) {
		this.dataSourceMappings.add(dataSourceMapping);
		this.store();		
	}

	public void updateDataSourceMapping(DataSourceMapping oldDataSourceMapping, DataSourceMapping newDataSourceMapping) {
		DataSourceMapping dataSourceMapping = getDataSource(oldDataSourceMapping.getAlias());
		if(dataSourceMapping != null){
			this.dataSourceMappings.remove(dataSourceMapping);
		}
		this.dataSourceMappings.add(newDataSourceMapping);
		this.store();		
	}

	public void deleteDataSourceMapping(DataSourceMapping dataSource) {
		DataSourceMapping dataSourceMapping = getDataSource(dataSource.getAlias());
		if(dataSourceMapping != null){
			this.dataSourceMappings.remove(dataSourceMapping);
			this.store();
		}
	}

	public DataSourceMapping getDataSource(String alias){
		for (DataSourceMapping dataSourceMapping : this.dataSourceMappings) {
			if(dataSourceMapping.getAlias().equals(alias)){
				return dataSourceMapping;
			}
		}
		return null;
	}
	

	public void load() {
		File file = new File(this.fileName);
		if(!file.exists()){
			try{
				file.getParentFile().mkdirs();
				file.createNewFile();
			} catch (Exception e) {
				throw new MeshException(e);
			}
		} else {
			try{
				FileReader reader = new FileReader(fileName);
				Properties prop = new Properties();
				prop.load(reader);
				
				Map.Entry<Object, Object> entry;
				for (Iterator<Map.Entry<Object, Object>> iterator = prop.entrySet().iterator(); iterator.hasNext();) {
					entry = iterator.next();
					
					String alias = (String)entry.getKey();
					String sourceDefinition = (String)entry.getValue();
					String tableName = MsAccessSyncAdapterFactory.getTableName(sourceDefinition);
					String fileName= MsAccessSyncAdapterFactory.getFileName(sourceDefinition);
					String mdbName= new File(fileName).getName();
					DataSourceMapping dataSourceMapping = new DataSourceMapping(alias, mdbName, tableName, fileName);
					this.dataSourceMappings.add(dataSourceMapping);
					
				}	
				
				reader.close();
			} catch (Exception e) {
				throw new MeshException(e);
			}
		}
	}
	
	public void store() {
		try{
			FileWriter writer = new FileWriter(fileName);
			Properties prop = new Properties();
			
			for (DataSourceMapping dataSourceMapping : this.dataSourceMappings) {
				String sourceDefinition = MsAccessSyncAdapterFactory.createSourceDefinition(dataSourceMapping.getFileName(), dataSourceMapping.getTableName());
				prop.put(dataSourceMapping.getAlias(), sourceDefinition);
			}
			
			prop.store(writer, "");
			writer.close();
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	@Override
	public String getSourceDefinition(String sourceId) {
		DataSourceMapping dataSourceMapping = getDataSource(sourceId);
		if(dataSourceMapping != null){
			return MsAccessSyncAdapterFactory.createSourceDefinition(dataSourceMapping.getFileName(), dataSourceMapping.getTableName());
		} else {
			return null;
		}
	}
}
