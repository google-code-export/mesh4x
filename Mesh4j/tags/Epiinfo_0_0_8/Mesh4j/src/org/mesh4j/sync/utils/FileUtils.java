package org.mesh4j.sync.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

	
	public static byte[] read(String fileName) throws IOException {
		return read(new File(fileName));
	}
	
	public static byte[] read(File file) throws IOException {
		InputStream reader = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(reader);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
	    
	    final int N = 1024;
	    byte buf[] = new byte[N];
	    int ln = 0;
	    while ((ln = bis.read(buf, 0, N)) != -1) {
	        os.write(buf, 0, ln);
	    }
	    bis.close();
		reader.close();
		os.flush();
		return os.toByteArray();
	}

	public static void write(String fileName, byte[] bytes) throws IOException{		
		FileOutputStream output = new FileOutputStream(new File(fileName));
		try {
			output.write(bytes);
			output.flush();
		} finally {
			output.close();
		}
	}
}
