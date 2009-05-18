package org.mesh4j.ektoo.validator;

import java.util.Hashtable;

public interface IValidationStatus
{
  void validationFailed(Hashtable errorTable);
  void validationPassed();
}
