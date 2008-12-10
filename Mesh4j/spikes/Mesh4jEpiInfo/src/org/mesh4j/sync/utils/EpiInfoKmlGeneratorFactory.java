package org.mesh4j.sync.utils;

import java.io.File;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.geo.coder.GeoCoderLatitudePropertyResolver;
import org.mesh4j.geo.coder.GeoCoderLongitudePropertyResolver;
import org.mesh4j.geo.coder.IGeoCoder;
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
	private String templateFileName;
	private IGeoCoder geoCoder;
	
	// BUSINESS METHODS
	
	public EpiInfoKmlGeneratorFactory(String baseDirectory, String templateFileName, IGeoCoder geoCoder) {
		Guard.argumentNotNullOrEmptyString(baseDirectory, "baseDirectory");
		Guard.argumentNotNullOrEmptyString(templateFileName, "templateFileName");
		Guard.argumentNotNull(geoCoder, "geoCoder");
		
		this.baseDirectory = baseDirectory;
		this.templateFileName = templateFileName;
		this.geoCoder = geoCoder;
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
			
			GeoCoderLatitudePropertyResolver propertyResolverLat = new GeoCoderLatitudePropertyResolver(geoCoder);
			GeoCoderLongitudePropertyResolver propertyResolverLon = new GeoCoderLongitudePropertyResolver(geoCoder);

			schemaResolver = new SchemaResolver(schema, propertyResolverLat, propertyResolverLon);
		} catch (Exception e) {
			throw new MeshException(e);
		}
		return new EpiInfoKmlGenerator(this.templateFileName, schemaResolver);
	}

}
