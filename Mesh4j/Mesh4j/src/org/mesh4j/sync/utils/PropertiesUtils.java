package org.mesh4j.sync.utils;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.mesh4j.sync.validations.MeshException;

public class PropertiesUtils {
	
	public static Map<String, String> getProperties(String fileName){
		try{
			FileReader reader = new FileReader(fileName);
			Properties prop = new Properties();
			prop.load(reader);

			Map<String, String> result = new HashMap<String,String>();
			Map.Entry<Object, Object> entry;
			for (Iterator<Map.Entry<Object, Object>> iterator = prop.entrySet().iterator(); iterator.hasNext();) {
				entry = iterator.next();
				result.put((String)entry.getKey(), (String)entry.getValue());
			}		
			
			reader.close();
			return result;
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	public static void store(String fileName, Map<String, String> properties) {
		try{
			FileWriter writer = new FileWriter(fileName);
			Properties prop = new Properties();
			
			for (String key : properties.keySet()) {
				prop.put(key, properties.get(key));
			}
			
			prop.store(writer, "");
			writer.close();
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

}
