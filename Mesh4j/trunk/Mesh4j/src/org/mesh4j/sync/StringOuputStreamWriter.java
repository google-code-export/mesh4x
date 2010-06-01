package org.mesh4j.sync;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class StringOuputStreamWriter implements IOutputStreamWriter {
	
	private final String string;

	public StringOuputStreamWriter(String string) {
		this.string = string;
	}

	@Override
	public void write(OutputStream out) throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(out);
		try {
			writer.write(string);
		} finally {
			writer.close();
		}
	}

}
