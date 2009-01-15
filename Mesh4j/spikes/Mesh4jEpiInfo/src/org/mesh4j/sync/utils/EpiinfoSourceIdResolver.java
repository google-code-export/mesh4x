package org.mesh4j.sync.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.mesh4j.sync.adapters.msaccess.IMsAccessSourceIdResolver;
import org.mesh4j.sync.adapters.msaccess.MsAccessSyncAdapterFactory;
import org.mesh4j.sync.mappings.DataSourceMapping;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class EpiinfoSourceIdResolver implements IMsAccessSourceIdResolver {

	private static final String DEFAULT_SEPARATOR = "@";
	// MODEL VARIABLES
	private String fileName;
	private ArrayList<DataSourceMapping> dataSourceMappings = new ArrayList<DataSourceMapping>();
	
	// BUSINESS METHODS
	public EpiinfoSourceIdResolver(String fileName) {
		Guard.argumentNotNullOrEmptyString(fileName, "fileName");
		this.fileName = fileName;
		load();
	}

	@Override
	public String getSourceName(String sourceId){
		String alias = MsAccessSyncAdapterFactory.getDataSource(sourceId);
		return alias;
	}

	@Override
	public String getFileName(String sourceId) {
		String alias = MsAccessSyncAdapterFactory.getDataSource(sourceId);
		DataSourceMapping dataSourceMapping = getDataSource(alias);
		if(dataSourceMapping != null){
			return dataSourceMapping.getFileName();
		} else {
			return null;
		}
	}

	@Override
	public String getTableName(String sourceId) {
		String alias = MsAccessSyncAdapterFactory.getDataSource(sourceId);
		DataSourceMapping dataSourceMapping = getDataSource(alias);
		if(dataSourceMapping != null){
			return dataSourceMapping.getTableName();
		} else {
			return null;
		}
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
					String source = MsAccessSyncAdapterFactory.getDataSource((String)entry.getValue());
					String[] elements = source.split(DEFAULT_SEPARATOR);
					String tableName = elements[1];
					String fileName= elements[0];
					String mdbName=new File(fileName).getName();
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
				String source = dataSourceMapping.getFileName() + DEFAULT_SEPARATOR + dataSourceMapping.getTableName();
				String sourceId = MsAccessSyncAdapterFactory.createSourceId(source);
				prop.put(dataSourceMapping.getAlias(), sourceId);
			}
			
			prop.store(writer, "");
			writer.close();
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
}
