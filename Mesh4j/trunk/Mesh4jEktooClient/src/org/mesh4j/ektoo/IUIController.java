package org.mesh4j.ektoo;

import java.util.HashMap;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
/**
 * @author Bhuiyan Mohammad Iklash
 *
 */
public interface IUIController {
  public ISyncAdapter createAdapter();
  public ISyncAdapter createAdapter(HashMap<IRDFSchema, String> schema);
  public HashMap<IRDFSchema, String> fetchSchema(ISyncAdapter adapter);
}
