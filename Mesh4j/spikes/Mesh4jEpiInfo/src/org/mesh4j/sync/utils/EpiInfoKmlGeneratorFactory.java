package org.mesh4j.sync.utils;

import java.io.File;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.sync.adapters.kml.timespan.decorator.IKMLGenerator;
import org.mesh4j.sync.adapters.kml.timespan.decorator.IKMLGeneratorFactory;
import org.mesh4j.sync.payload.schema.ISchemaResolver;
import org.mesh4j.sync.payload.schema.SchemaResolver;
import org.mesh4j.sync.ui.translator.EpiInfoUITranslator;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class EpiInfoKmlGeneratorFactory implements IKMLGeneratorFactory {

	// MODEL VARIABLES
	private String baseDirectory;
	
	// BUSINESS METHODS
	
	public EpiInfoKmlGeneratorFactory(String baseDirectory) {
		Guard.argumentNotNullOrEmptyString(baseDirectory, "baseDirectory");
		this.baseDirectory = baseDirectory;
	}

	@Override
	public IKMLGenerator createKMLGenereator(String sourceName) {
		String schemaFileName = this.baseDirectory + "/" + sourceName + "_schema.xml";
		
		ISchemaResolver schemaResolver = null;
		File schemaFile = new File(schemaFileName);
		if(!schemaFile.exists()){
			throw new IllegalArgumentException(EpiInfoUITranslator.getErrorKMLSchemaNotFound());
		}
		
		try{
			byte[] bytes = FileUtils.read(schemaFile);
			String xml = new String(bytes);
			Element schema = DocumentHelper.parseText(xml).getRootElement();
			schemaResolver = new SchemaResolver(schema);
		} catch (Exception e) {
			throw new MeshException(e);
		}
		return new EpiInfoKmlGenerator(schemaResolver);
	}

}
