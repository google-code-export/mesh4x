package org.mesh4j.sync.payload.schema;

public interface ISchemaTypeFormat {

	Object format(Object fieldValue);

	Object parseObject(String fieldValue) throws Exception;

}
