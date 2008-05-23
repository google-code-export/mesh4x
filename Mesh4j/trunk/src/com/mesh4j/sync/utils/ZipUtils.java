package com.mesh4j.sync.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import com.mesh4j.sync.validations.Guard;

public class ZipUtils {

	public static String getTextEntryContent(String fileName, String entryName) throws IOException{
		return getTextEntryContent(new File(fileName), entryName);
	}	

	public static String getTextEntryContent(File file, String entryName) throws IOException{
		ZipFile zipFile = new ZipFile(file);
		ZipEntry entry = zipFile.getEntry(entryName);
		if(entry != null){
			InputStream is = zipFile.getInputStream(entry);
			InputStreamReader reader = new InputStreamReader(is);
			StringWriter writer = new StringWriter();
			for (int ch = reader.read(); ch!= -1; ch= reader.read()){
				writer.write(ch);
			}
			return writer.toString();
		} else {
			Guard.throwsArgumentException("Arg_InvalidZipEntryName", file.getName(), entryName);
			return ""; // ONLY for java compilation
		}
	} 
		
	public static void write(File file, String entryName, String content) throws IOException{
		
		if(!file.exists()){
			FileOutputStream os = new FileOutputStream(file);		
			ZipOutputStream zip = new ZipOutputStream(os);
			
			ZipEntry ze = new ZipEntry(entryName);
			ze.setTime(System.currentTimeMillis());
			zip.putNextEntry(ze);
			zip.write(content.getBytes());			
			zip.closeEntry();
			
			zip.close();        
			os.close();
        
		} else {
			
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ZipOutputStream zip = new ZipOutputStream(os);
			
			boolean entryExists = false;
			ZipFile zipFile = new ZipFile(file);
			
			Enumeration<? extends ZipEntry> entries = zipFile.entries(); 
			while(entries.hasMoreElements()){
				ZipEntry entry = entries.nextElement();
				if(entry.getName().equals(entryName)){
					ZipEntry entryUpdated = new ZipEntry(entryName);				
					zip.putNextEntry(entryUpdated);	
					entryUpdated.setTime(System.currentTimeMillis());
					zip.write(content.getBytes());
					entryExists = true;
				} else {
					ZipEntry entryUpdated = new ZipEntry(entry);				
					zip.putNextEntry(entryUpdated);	
					InputStream reader = zipFile.getInputStream(entry);
					int n = 0;
					while ((n = reader.read()) > 0) {
						zip.write(n);
					}
				}
		        zip.closeEntry();
			}
			zipFile.close();
			
			if(!entryExists){
				ZipEntry ze = new ZipEntry(entryName);
				ze.setTime(System.currentTimeMillis());
				zip.putNextEntry(ze);
				zip.write(content.getBytes());
		        zip.closeEntry();
			}			
	        zip.close();	        
	      	os.close();
	      	
	      	ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
	      	FileOutputStream fos = new FileOutputStream(file);
	      	while(is.available() > 0){
	      		fos.write(is.read());
	      	}
	      	fos.close();
	      	is.close();
		}
	}
}
