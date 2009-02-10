package org.mesh4j.sync.utils;

import java.io.File;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.geo.coder.GeoCoderLatitudePropertyResolver;
import org.mesh4j.geo.coder.GeoCoderLocationPropertyResolver;
import org.mesh4j.geo.coder.GeoCoderLongitudePropertyResolver;
import org.mesh4j.geo.coder.GoogleGeoCoder;
import org.mesh4j.geo.coder.IGeoCoder;
import org.mesh4j.sync.adapters.kml.timespan.decorator.IKMLGenerator;
import org.mesh4j.sync.adapters.kml.timespan.decorator.IKMLGeneratorFactory;
import org.mesh4j.sync.payload.mappings.IMappingResolver;
import org.mesh4j.sync.payload.mappings.MappingResolver;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class KmlGeneratorFactory implements IKMLGeneratorFactory {

	// MODEL VARIABLES
	private String baseDirectory;
	private String templateFileName;
	private String geoCoderKey;
	
	// BUSINESS METHODS
	
	public KmlGeneratorFactory(String baseDirectory, String templateFileName, String geoCoderKey) {
		Guard.argumentNotNullOrEmptyString(baseDirectory, "baseDirectory");
		Guard.argumentNotNullOrEmptyString(templateFileName, "templateFileName");
		Guard.argumentNotNullOrEmptyString(geoCoderKey, "geoCoderKey");
		
		this.baseDirectory = baseDirectory;
		this.templateFileName = templateFileName;
		this.geoCoderKey = geoCoderKey;
	}

	@Override
	public IKMLGenerator createKMLGenereator(String sourceName) {
		IMappingResolver mappingResolver = createMappingResolver(sourceName, this.baseDirectory, this.geoCoderKey);
		return new KmlGenerator(this.templateFileName, mappingResolver);
	}
	
	public static IGeoCoder makeGeoCoder(String geoCoderKey){
		return new GoogleGeoCoder(geoCoderKey);
	}
	
	public static MappingResolver createMappingResolver(String alias, String baseDirectory, String geoCoderKey){
		String mappingsFileName = baseDirectory + "/" + alias + "_mappings.xml";
		File mappingFile = new File(mappingsFileName);
		if(!mappingFile.exists()){
			return null;
		}
		
		try{
			byte[] bytes = FileUtils.read(mappingFile);
			String xml = new String(bytes);
			Element mappings = DocumentHelper.parseText(xml).getRootElement();
			
			IGeoCoder geoCoder = makeGeoCoder(geoCoderKey);
			GeoCoderLatitudePropertyResolver propertyResolverLat = new GeoCoderLatitudePropertyResolver(geoCoder);
			GeoCoderLongitudePropertyResolver propertyResolverLon = new GeoCoderLongitudePropertyResolver(geoCoder);
			GeoCoderLocationPropertyResolver propertyResolverLoc = new GeoCoderLocationPropertyResolver(geoCoder);
			MappingResolver mappingResolver = new MappingResolver(mappings, propertyResolverLat, propertyResolverLon, propertyResolverLoc);
			return mappingResolver;
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

}
