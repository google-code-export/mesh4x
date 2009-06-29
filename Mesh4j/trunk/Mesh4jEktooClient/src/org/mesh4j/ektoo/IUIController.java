package org.mesh4j.ektoo;

import java.util.List;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;

public interface IUIController {
  public ISyncAdapter createAdapter();
  public ISyncAdapter createAdapter(List<IRDFSchema> schemas);
  public List<IRDFSchema> fetchSchema(ISyncAdapter adapter);
}
