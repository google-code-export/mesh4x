package org.mesh4j.meshes.io;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.mesh4j.meshes.model.Mesh;

public class MeshMarshaller {
	
	public static void toXml(Mesh mesh, OutputStream out) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Mesh.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(mesh, out);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Mesh fromXml(InputStream in) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Mesh.class);
			Unmarshaller unMarshaller = jaxbContext.createUnmarshaller();
			Mesh mesh = (Mesh) unMarshaller.unmarshal(in);
			return mesh;
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
