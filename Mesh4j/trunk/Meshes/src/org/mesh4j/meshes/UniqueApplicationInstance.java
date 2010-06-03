package org.mesh4j.meshes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.mesh4j.meshes.io.ConfigurationManager;

/**
 * Represents the unique application instance that is running on the current
 * machine.
 * 
 * This is a singleton class.
 */
public class UniqueApplicationInstance {
	
	private final static Logger LOGGER = Logger.getLogger(UniqueApplicationInstance.class);
	
	// Command to import a configuration file
	private final static String IMPORT_COMMAND = "import "; 
	
	private int port = 52428;
	private ServerSocket server;
	private static UniqueApplicationInstance instance = new UniqueApplicationInstance();
	
	private UniqueApplicationInstance() {
		try {
			listen();
		} catch (IOException e) {
			LOGGER.error("Failed listening port " + port, e);
		}
	}
	
	/**
	 * Returns the only instance of this class.
	 */
	public static UniqueApplicationInstance getInstance() {
		return instance;
	}
	
	/**
	 * Returns true if another instance of this program is already running.
	 */
	public boolean anotherInstanceIsRunning() {
		return server == null;
	}
	
	/**
	 * Tells the unique instance to import a configuration file.
	 */
	public void importConfigurationFile(String file) {
		try {
			Socket socket = new Socket("localhost", port);
			OutputStream out = socket.getOutputStream();
			PrintWriter writer = new PrintWriter(out, true);
			writer.write(IMPORT_COMMAND + file);
			writer.close();
		} catch (IOException e) {
			LOGGER.error("Failed telling to import the file " + file + " via the port " + port, e);
		}
	}

	private void listen() throws IOException {
		try {
			server = new ServerSocket(port);
			new Thread(new Runnable() {
				@Override
				public void run() {
					while(true) {
						try {
							Socket client = server.accept();
							InputStream in = client.getInputStream();
							BufferedReader reader = new BufferedReader(new InputStreamReader(in));
							String line;
							while((line = reader.readLine()) != null) {
								if (line.startsWith(IMPORT_COMMAND)) {
									String fileName = line.substring(IMPORT_COMMAND.length());
									ConfigurationManager.getInstance().importFile(fileName);
								}
							}
						} catch (IOException e) {
							LOGGER.error("Failed reading socket port " + port, e);
						}
					}
				}
			}).start();
		} catch (BindException e) {
			// Can happen if the address is already in use
		}
	}

	

}
