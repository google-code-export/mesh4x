package com.mesh4j.sync.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.mesh4j.sync.validations.Guard;
import com.mesh4j.sync.validations.MeshException;

public class ZipUtils {

	public static String getTextEntryContent(String fileName, String entryName)
			throws IOException {
		return getTextEntryContent(new File(fileName), entryName);
	}

	public static String getTextEntryContent(File file, String entryName)
			throws IOException {
		ZipFile zipFile = new ZipFile(file);
		ZipEntry entry = zipFile.getEntry(entryName);
		if (entry != null) {
			InputStream is = zipFile.getInputStream(entry);
			InputStreamReader reader = new InputStreamReader(is);
			StringWriter writer = new StringWriter();
			for (int ch = reader.read(); ch != -1; ch = reader.read()) {
				writer.write(ch);
			}
			reader.close();
			is.close();
			zipFile.close();
			return writer.toString();
		} else {
			zipFile.close();
			Guard.throwsArgumentException("Arg_InvalidZipEntryName", file
					.getName(), entryName);
			return ""; // ONLY for java compilation
		}
	}

	public static Map<String, byte[]> getEntries(File file) throws IOException {
		HashMap<String, byte[]> result = new HashMap<String, byte[]>();
		ZipFile zipFile = new ZipFile(file);
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			if (!entry.isDirectory()) {
				byte[] bytes = getEntryContent(zipFile, entry);
				result.put(entry.getName(), bytes);
			}
		}
		zipFile.close();
		return result;
	}

	public static void write(File file, Map<String, byte[]> entries)
			throws IOException {

		if (!file.exists()) {
			FileOutputStream os = new FileOutputStream(file);
			ZipOutputStream zip = new ZipOutputStream(os);
			for (String entryName : entries.keySet()) {
				addEntry(zip, entryName, entries.get(entryName));
			}
			zip.close();
			os.close();
		} else {

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ZipOutputStream zip = new ZipOutputStream(os);

			ZipFile zipFile = new ZipFile(file);

			Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
			while (zipEntries.hasMoreElements()) {
				ZipEntry entry = zipEntries.nextElement();
				byte[] updatedContent = entries.get(entry.getName());
				if (updatedContent != null) {
					ZipEntry entryUpdated = new ZipEntry(entry.getName());
					zip.putNextEntry(entryUpdated);
					entryUpdated.setTime(System.currentTimeMillis());
					zip.write(updatedContent);
				}
				zip.closeEntry();
			}

			for (String entryName : entries.keySet()) {
				ZipEntry entry = zipFile.getEntry(entryName);
				if (entry == null) {
					byte[] newContent = entries.get(entryName);

					ZipEntry entryUpdated = new ZipEntry(entryName);
					zip.putNextEntry(entryUpdated);
					entryUpdated.setTime(System.currentTimeMillis());
					zip.write(newContent);
				}
			}
			zipFile.close();

			zip.close();
			os.close();

			ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
			FileOutputStream fos = new FileOutputStream(file);
			while (is.available() > 0) {
				fos.write(is.read());
			}
			fos.close();
			is.close();
		}
	}

	private static void addEntry(ZipOutputStream zip, String entryName,
			byte[] bytes) throws IOException {
		ZipEntry ze = new ZipEntry(entryName);
		ze.setTime(System.currentTimeMillis());
		zip.putNextEntry(ze);
		zip.write(bytes);
		zip.closeEntry();
	}

	private static byte[] getEntryContent(ZipFile zipFile, ZipEntry entry)
			throws IOException {
		InputStream reader = zipFile.getInputStream(entry);
		BufferedInputStream bis = new BufferedInputStream(reader);
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		int sz = (int) entry.getSize();
		final int N = 1024;
		byte buf[] = new byte[N];
		int ln = 0;
		while (sz > 0 && // workaround for bug
				(ln = bis.read(buf, 0, Math.min(N, sz))) != -1) {
			os.write(buf, 0, ln);
			sz -= ln;
		}
		bis.close();
		reader.close();
		os.flush();
		return os.toByteArray();
	}

	public static void write(File file, String entryName, String content)
			throws IOException {

		if (!file.exists()) {
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
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (entry.getName().equals(entryName)) {
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
					reader.close();
				}
				zip.closeEntry();
			}
			zipFile.close();

			if (!entryExists) {
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
			while (is.available() > 0) {
				fos.write(is.read());
			}
			fos.close();
			is.close();
		}
	}

	public static void write(ZipOutputStream zip, String entryName,
			String content) throws IOException {
		ZipEntry ze = new ZipEntry(entryName);
		ze.setTime(System.currentTimeMillis());
		zip.putNextEntry(ze);
		zip.write(content.getBytes());

		zip.closeEntry();
		zip.flush();
	}

	public static Reader getEntryInputStream(ZipInputStream zip,
			String entryName) throws IOException {
		for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip
				.getNextEntry()) {
			String name = entry.getName();
			if (name.equals(entryName)) {
				return new InputStreamReader(zip);
			}
		}
		return null;
	}

	public static String getTextEntryContent(ZipInputStream zip,
			String entryName) throws IOException {
		for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip
				.getNextEntry()) {
			String name = entry.getName();
			if (name.equals(entryName)) {
				InputStreamReader reader = new InputStreamReader(zip);
				StringWriter writer = new StringWriter();
				for (int ch = reader.read(); ch != -1; ch = reader.read()) {
					writer.write(ch);
				}
				return writer.toString();
			}
		}
		return null;
	}

	public static byte[] compress(byte[] input) throws IOException {

		Deflater compressor = new Deflater();
		compressor.setLevel(Deflater.BEST_COMPRESSION);

		// Give the compressor the data to compress
		compressor.setInput(input);
		compressor.finish();

		// Create an expandable byte array to hold the compressed data.
		// It is not necessary that the compressed data will be smaller than
		// the uncompressed data.
		ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);

		// Compress the data
		byte[] buf = new byte[1024];
		while (!compressor.finished()) {
			int count = compressor.deflate(buf);
			bos.write(buf, 0, count);
		}
		bos.close();

		// Get the compressed data
		byte[] compressedData = bos.toByteArray();
		return compressedData;
	}

	public static byte[] decompress(byte[] compressedData) throws IOException, DataFormatException {
		Inflater decompressor = new Inflater();
		decompressor.setInput(compressedData);

		// Create an expandable byte array to hold the decompressed data
		ByteArrayOutputStream bos = new ByteArrayOutputStream(
				compressedData.length);

		// Decompress the data
		byte[] buf = new byte[1024];
		while (!decompressor.finished()) {
			int count = decompressor.inflate(buf);
			bos.write(buf, 0, count);

		}
		bos.close();

		// Get the decompressed data
		byte[] decompressedData = bos.toByteArray();
		return decompressedData;
	}

	public static String unzip(byte[] zipBytes, String entryName) {
		try{
			ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(zipBytes));
			String result = ZipUtils.getTextEntryContent(zipInputStream, entryName);
			zipInputStream.close();
			return result;
		} catch(IOException io){
			throw new MeshException(io);
		}			
	}

	public static byte[] zip(String message, String entryName) {
		try{
			ByteArrayOutputStream itemsOS = new ByteArrayOutputStream();
			ZipOutputStream zipOS = new ZipOutputStream(itemsOS);
			ZipUtils.write(zipOS, entryName, message);
			
			itemsOS.flush();
			zipOS.close();
			return itemsOS.toByteArray();
		} catch(IOException io){
			throw new MeshException(io);
		}
	}
}
