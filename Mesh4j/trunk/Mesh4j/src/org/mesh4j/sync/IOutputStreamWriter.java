package org.mesh4j.sync;

import java.io.IOError;
import java.io.IOException;
import java.io.OutputStream;

public interface IOutputStreamWriter {
	
	void write(OutputStream out) throws IOException;

}
