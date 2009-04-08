package org.mesh4j.sync.utils;

import org.mesh4j.geo.coder.GoogleGeoCoder;
import org.mesh4j.geo.coder.IGeoCoder;
import org.mesh4j.sync.adapters.kml.timespan.decorator.IKMLGenerator;
import org.mesh4j.sync.adapters.kml.timespan.decorator.IKMLGeneratorFactory;
import org.mesh4j.sync.payload.mappings.IMapping;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.validations.Guard;

public class KmlGeneratorFactory implements IKMLGeneratorFactory {

	// MODEL VARIABLES
	private String templateFileName;
	private ISchema schema;
	private IMapping mapping;
	
	// BUSINESS METHODS
	
	public KmlGeneratorFactory(String templateFileName, ISchema schema, IMapping mapping) {
		Guard.argumentNotNullOrEmptyString(templateFileName, "templateFileName");
		Guard.argumentNotNull(schema, "schema");
		Guard.argumentNotNull(mapping, "mapping");
		
		this.templateFileName = templateFileName;
		this.schema = schema;
		this.mapping = mapping;
	}

	@Override
	public IKMLGenerator createKMLGenereator(String sourceName) {
		return new KmlGenerator(this.templateFileName, this.schema, this.mapping);
	}
	
	public static IGeoCoder makeGeoCoder(String geoCoderKey){
		return new GoogleGeoCoder(geoCoderKey);
	}
}
