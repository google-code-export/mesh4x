package org.mesh4j.sync.payload.schema.rdf;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.XSD;

public class SchemaMappedRDFSchema extends RDFSchema{
	// MODEL VARIABLES
	
	//field name <-> field data-type (XSD.xstring, XSD.integer, XSD.date etc)
	private Map<String, Resource> syncSchema;
	
	//original field name <-> field name of base schema to map with
	private Map<String, String> schemaConvertMap;
	
	private Map<String, String> schemaRevertMap;
	
		
	// BUSINESS METHODS
	public SchemaMappedRDFSchema(String ontologyNameSpace,
			String ontologyBaseClassUri, String ontologyClassName, 
			Map<String, Resource> syncSchema, Map<String, String> schemaConvertMap) {
		super(ontologyNameSpace, ontologyBaseClassUri, ontologyClassName);
		
		Guard.argumentNotNullOrEmptyCollection(syncSchema, "syncSchema");
		Guard.argumentNotNullOrEmptyCollection(schemaConvertMap, "schemaConvertMap");
		
		//validateSyncSchemaWithConversionMap(syncSchema, schemaConvertMap);
		
		this.syncSchema = syncSchema;
		this.schemaConvertMap = schemaConvertMap;
		
		//reverse each key-value pair to value-key pair
		schemaRevertMap = new HashMap<String, String>();
		for(Entry<String, String> e : schemaConvertMap.entrySet()){
			schemaRevertMap.put(e.getValue(),e.getKey());
		}
	}
	
	private void validateSyncSchemaWithConversionMap(Map<String, Resource> syncSchema, Map<String, String> schemaConvertMap){
		if(syncSchema.size() != schemaConvertMap.size())
			throw new MeshException("Error in schema conversion mapping");
		
		//check entity name mapping
		if(schemaConvertMap.get(this.getName()) == null
				|| syncSchema.get(schemaConvertMap.get(this.getName())) == null
				|| syncSchema.get(schemaConvertMap.get(this.getName())).getURI() != XSD.ENTITY.getURI())
			throw new MeshException("Error or missing mapping for entity name: "+ this.getName());
		
		//check from convert map to sync schema
		for(Entry<String, String> entry :schemaConvertMap.entrySet()){
			String key = entry.getKey();
			String value = entry.getValue();
			if(!syncSchema.containsKey(value))
				throw new MeshException("Invalid mapping for reposity field: "+key+" with unknown sync schema field: "+value);
			if(syncSchema.get(value) == null)
				throw new MeshException("Invalid mapping for reposity field: "+key+" with unknown type sync schema field: "+value+"");
		}
		
		//check from sync schema to convert map
		for(Entry<String, Resource> entry :syncSchema.entrySet()){
			String key = entry.getKey();
			if(!schemaConvertMap.containsValue(key))
				throw new MeshException("Missing repository field mapping for sync schema field : "+key);
		}
		
		//field type conversion checking
		for(Entry<String, String> entry :schemaConvertMap.entrySet()){
			if(this.getName().equals(entry.getKey()))
				continue;
			String repositoryField = entry.getKey();
			String schemaMappedField = entry.getValue();
			
			//TypeMapper.getInstance().getTypeByName(uri)
			String schemaMappedFieldType = syncSchema.get(schemaMappedField).getURI();
			String repositoryFieldType = this.getPropertyType(repositoryField);
			
			//do check for 
			if(repositoryFieldType.equals(XSD.date.getURI())){
				if(schemaMappedFieldType.equals(XSD.date.getURI())
						||schemaMappedFieldType.equals(XSD.xstring.getURI()))
					continue;
				else
					throw new MeshException("Incompatible type mappign for repository field '"+repositoryField+"' of type '"+repositoryFieldType+"' " +
							"with sync schema field '"+schemaMappedField+"' of type '"+schemaMappedFieldType+"'");
			}
			else if(repositoryFieldType.equals(XSD.dateTime.getURI())){
				if(schemaMappedFieldType.equals(XSD.dateTime.getURI())
						||schemaMappedFieldType.equals(XSD.xstring.getURI()))
					continue;
				else
					throw new MeshException("Incompatible type mappign for repository field '"+repositoryField+"' of type '"+repositoryFieldType+"' " +
							"with sync schema field '"+schemaMappedField+"' of type '"+schemaMappedFieldType+"'");
			}
			else if(repositoryFieldType.equals(XSD.decimal.getURI())){
				if(schemaMappedFieldType.equals(XSD.decimal.getURI())
						||schemaMappedFieldType.equals(XSD.xfloat.getURI())
						||schemaMappedFieldType.equals(XSD.xdouble.getURI())
						||schemaMappedFieldType.equals(XSD.xstring.getURI()))
					continue;
				else
					throw new MeshException("Incompatible type mappign for repository field '"+repositoryField+"' of type '"+repositoryFieldType+"' " +
							"with sync schema field '"+schemaMappedField+"' of type '"+schemaMappedFieldType+"'");
			}
			else if(repositoryFieldType.equals(XSD.xboolean.getURI())){
				if(schemaMappedFieldType.equals(XSD.xboolean.getURI())
						||schemaMappedFieldType.equals(XSD.xstring.getURI()))
					continue;
				else
					throw new MeshException("Incompatible type mappign for repository field '"+repositoryField+"' of type '"+repositoryFieldType+"' " +
							"with sync schema field '"+schemaMappedField+"' of type '"+schemaMappedFieldType+"'");
			}
			else if(repositoryFieldType.equals(XSD.xbyte.getURI())){
				if(schemaMappedFieldType.equals(XSD.xbyte.getURI())
						||schemaMappedFieldType.equals(XSD.xshort.getURI())
						||schemaMappedFieldType.equals(XSD.integer.getURI())
						||schemaMappedFieldType.equals(XSD.xlong.getURI())
						||schemaMappedFieldType.equals(XSD.xstring.getURI()))
					continue;
				else
					throw new MeshException("Incompatible type mappign for repository field '"+repositoryField+"' of type '"+repositoryFieldType+"' " +
							"with sync schema field '"+schemaMappedField+"' of type '"+schemaMappedFieldType+"'");
			}
			else if(repositoryFieldType.equals(XSD.xshort.getURI())){
				if(schemaMappedFieldType.equals(XSD.xshort.getURI())
						||schemaMappedFieldType.equals(XSD.integer.getURI())
						||schemaMappedFieldType.equals(XSD.xlong.getURI())
						||schemaMappedFieldType.equals(XSD.xstring.getURI()))
					continue;
				else
					throw new MeshException("Incompatible type mappign for repository field '"+repositoryField+"' of type '"+repositoryFieldType+"' " +
							"with sync schema field '"+schemaMappedField+"' of type '"+schemaMappedFieldType+"'");
			}
			else if(repositoryFieldType.equals(XSD.integer.getURI())){
				if(schemaMappedFieldType.equals(XSD.integer.getURI())
						||schemaMappedFieldType.equals(XSD.xlong.getURI())
						||schemaMappedFieldType.equals(XSD.xstring.getURI()))
					continue;
				else
					throw new MeshException("Incompatible type mappign for repository field '"+repositoryField+"' of type '"+repositoryFieldType+"' " +
							"with sync schema field '"+schemaMappedField+"' of type '"+schemaMappedFieldType+"'");
			}
			else if(repositoryFieldType.equals(XSD.xlong.getURI())){
				if(schemaMappedFieldType.equals(XSD.xlong.getURI())
						||schemaMappedFieldType.equals(XSD.xstring.getURI()))
					continue;
				else
					throw new MeshException("Incompatible type mappign for repository field '"+repositoryField+"' of type '"+repositoryFieldType+"' " +
							"with sync schema field '"+schemaMappedField+"' of type '"+schemaMappedFieldType+"'");
			}
			else if(repositoryFieldType.equals(XSD.xfloat.getURI())){
				if(schemaMappedFieldType.equals(XSD.xfloat.getURI())
						||schemaMappedFieldType.equals(XSD.xdouble.getURI())
						||schemaMappedFieldType.equals(XSD.xstring.getURI()))
					continue;
				else
					throw new MeshException("Incompatible type mappign for repository field '"+repositoryField+"' of type '"+repositoryFieldType+"' " +
							"with sync schema field '"+schemaMappedField+"' of type '"+schemaMappedFieldType+"'");
			}
			else if(repositoryFieldType.equals(XSD.xdouble.getURI())){
				if(schemaMappedFieldType.equals(XSD.xdouble.getURI())
						||schemaMappedFieldType.equals(XSD.xstring.getURI()))
					continue;
				else
					throw new MeshException("Incompatible type mappign for repository field '"+repositoryField+"' of type '"+repositoryFieldType+"' " +
							"with sync schema field '"+schemaMappedField+"' of type '"+schemaMappedFieldType+"'");
			}
			else if(repositoryFieldType.equals(XSD.xstring.getURI())){
				if(schemaMappedFieldType.equals(XSD.xstring.getURI()))
					continue;
				else
					throw new MeshException("Incompatible type mappign for repository field '"+repositoryField+"' of type '"+repositoryFieldType+"' " +
							"with sync schema field '"+schemaMappedField+"' of type '"+schemaMappedFieldType+"'");
			}
		}
	}
	
	public Map<String, Resource> getSyncSchema() {
		return syncSchema;
	}

	public Map<String, String> getSchemaConvertMap() {
		return schemaConvertMap;
	}

	public Map<String, String> getSchemaRevertMap() {
		return schemaRevertMap;
	}

	@Override
	public boolean isCompatible(ISchema schema){
		if(schema == null) return false;
		
		SchemaMappedRDFSchema otherSchema = (SchemaMappedRDFSchema)schema;
		
		if(this.syncSchema.size() != otherSchema.syncSchema.size()){
			System.out.println("Field numbers are not equal in sync schema");
			return false;
		}
		
		//check entity name mapping
		for(Entry<String, Resource> entry:this.syncSchema.entrySet()){
			String field = entry.getKey();
			Resource fieldType = entry.getValue();
			if(otherSchema.syncSchema.get(field) == null
					|| !fieldType.getURI().equals(otherSchema.syncSchema.get(field).getURI())){
				//throw new MeshException("Dissimilar sync schema for field: "+ field);
				System.out.println("Dissimilar data type in sync schema for field: "+ field);
				return false;
			}
		}
		
		//check as earlier but in reverse mode
		for(Entry<String, Resource> entry:otherSchema.syncSchema.entrySet()){
			String field = entry.getKey();
			Resource fieldType = entry.getValue();
			if(this.syncSchema.get(field) == null
					|| !fieldType.getURI().equals(this.syncSchema.get(field).getURI())){
				//throw new MeshException("Dissimilar sync schema for field: "+ field);
				System.out.println("Dissimilar data type in sync schema for field: "+ field);
				return false;
			}
		}
		
		return true;
	}

}
