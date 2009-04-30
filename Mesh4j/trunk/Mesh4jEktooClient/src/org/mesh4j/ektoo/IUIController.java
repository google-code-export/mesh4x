package org.mesh4j.ektoo;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
/**
 * @author Bhuiyan Mohammad Iklash
 *
 */
public interface IUIController 
{
  public ISyncAdapter createAdapter();
  public ISyncAdapter createAdapter(IRDFSchema schema);
  public IRDFSchema createSchema();
  
}
