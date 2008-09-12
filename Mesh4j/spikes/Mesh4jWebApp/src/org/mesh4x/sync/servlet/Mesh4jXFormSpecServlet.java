package org.mesh4x.sync.servlet;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Mesh4jXFormSpecServlet extends HttpServlet {

	private static final long serialVersionUID = -3969288045575850284L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		FileReader reader = new FileReader("C:\\Clarius\\temp\\xFromSpec.xml");
		String message = readData(reader);	
		if(message == null){
			message = "ERROR";
		}
		
		System.out.println("GET RESPONSE: " + message);	
						
		response.setContentType("text/plain");
		response.setContentLength(message.length());
		PrintWriter out = response.getWriter();
		out.println(message);
	}

	private String readData(Reader reader) {
		try {
			StringBuffer result = new StringBuffer();
			char[] cb = new char[2048];
			int amtRead = reader.read(cb);
			while (amtRead > 0) {
				result.append(cb, 0, amtRead);
				amtRead = reader.read(cb);
			}
			return result.toString();
		} catch(IOException e){
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (reader != null){
					reader.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

}
