package org.mesh4j.sync.payload.schema;

public interface ISchemaTypeFormat {

	Object format(Object fieldValue) throws Exception;

	Object parseObject(String fieldValue) throws Exception;

}
