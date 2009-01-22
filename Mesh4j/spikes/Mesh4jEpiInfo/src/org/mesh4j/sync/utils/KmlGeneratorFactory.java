package org.mesh4j.sync.utils;

import java.io.File;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.geo.coder.GeoCoderLatitudePropertyResolver;
import org.mesh4j.geo.coder.GeoCoderLocationPropertyResolver;
import org.mesh4j.geo.coder.GeoCoderLongitudePropertyResolver;
import org.mesh4j.geo.coder.IGeoCoder;
import org.mesh4j.sync.adapters.kml.timespan.decorator.IKMLGenerator;
import org.mesh4j.sync.adapters.kml.timespan.decorator.IKMLGeneratorFactory;
import org.mesh4j.sync.payload.mappings.IMappingResolver;
import org.mesh4j.sync.payload.mappings.MappingResolver;
import org.mesh4j.sync.ui.translator.MeshUITranslator;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class KmlGeneratorFactory implements IKMLGeneratorFactory {

	// MODEL VARIABLES
	private String baseDirectory;
	private String templateFileName;
	private IGeoCoder geoCoder;
	
	// BUSINESS METHODS
	
	public KmlGeneratorFactory(String baseDirectory, String templateFileName, IGeoCoder geoCoder) {
		Guard.argumentNotNullOrEmptyString(baseDirectory, "baseDirectory");
		Guard.argumentNotNullOrEmptyString(templateFileName, "templateFileName");
		Guard.argumentNotNull(geoCoder, "geoCoder");
		
		this.baseDirectory = baseDirectory;
		this.templateFileName = templateFileName;
		this.geoCoder = geoCoder;
	}

	@Override
	public IKMLGenerator createKMLGenereator(String sourceName) {
		String mappingsFileName = this.baseDirectory + "/" + sourceName + "_mappings.xml";
		
		IMappingResolver mappingResolver = null;
		File mappingFile = new File(mappingsFileName);
		if(!mappingFile.exists()){
			throw new IllegalArgumentException(MeshUITranslator.getErrorKMLMappingsNotFound());
		}
		
		try{
			byte[] bytes = FileUtils.read(mappingFile);
			String xml = new String(bytes);
			Element mappings = DocumentHelper.parseText(xml).getRootElement();
			
			GeoCoderLatitudePropertyResolver propertyResolverLat = new GeoCoderLatitudePropertyResolver(geoCoder);
			GeoCoderLongitudePropertyResolver propertyResolverLon = new GeoCoderLongitudePropertyResolver(geoCoder);
			GeoCoderLocationPropertyResolver propertyResolverLoc = new GeoCoderLocationPropertyResolver(geoCoder);
			mappingResolver = new MappingResolver(mappings, propertyResolverLat, propertyResolverLon, propertyResolverLoc);
		} catch (Exception e) {
			throw new MeshException(e);
		}
		return new KmlGenerator(this.templateFileName, mappingResolver);
	}

}
