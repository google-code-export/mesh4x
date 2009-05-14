package org.mesh4j.ektoo.ui.validator;

import javax.swing.JComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.controller.MySQLUIController;
import org.mesh4j.ektoo.model.AbstractModel;
import org.mesh4j.ektoo.model.MySQLAdapterModel;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.ektoo.validator.AbstractValidator;
/**
 * @author Bhuiyan Mohammad Iklash
 *
 */
public class MySQLConnectionValidator extends AbstractValidator
{ 
  private final Log LOGGER = LogFactory.getLog(MySQLConnectionValidator.class);
  
  public MySQLConnectionValidator(JComponent form, AbstractModel model)
  {
    super(form, model);
  }
 
  @Override
  protected boolean validate()
  {
    boolean isValid = true;
    MySQLAdapterModel mysqlModel  = (MySQLAdapterModel)this.model;
    if (mysqlModel == null) 
    {
      setError( MySQLAdapterModel.class.getName(), EktooUITranslator.getErrorEmptyOrNull(MySQLAdapterModel.class.getName()));
      isValid = false;
    }
      
    String userName = mysqlModel.getUserName();
    if (userName == null || userName.trim().length() == 0)
    {
      setError( MySQLUIController.USER_NAME_PROPERTY, EktooUITranslator.getErrorEmptyOrNull(MySQLUIController.USER_NAME_PROPERTY));
      isValid = false;
    }
      
    String hostName = mysqlModel.getHostName();
    if (hostName == null || hostName.trim().length() == 0)
    {
      setError( MySQLUIController.HOST_NAME_PROPERTY, EktooUITranslator.getErrorEmptyOrNull(MySQLUIController.HOST_NAME_PROPERTY));
      isValid = false;
    }

    int portNo = mysqlModel.getPortNo();
    if (portNo < 0)
    {
      setError( MySQLUIController.PORT_NO_PROPERTY, EktooUITranslator.getErrorInvalid(MySQLUIController.PORT_NO_PROPERTY));
      isValid = false;
    }

    String databaseName = mysqlModel.getDatabaseName();
    if (databaseName == null || databaseName.trim().length() == 0)
    {
      setError( MySQLUIController.DATABASE_NAME_PROPERTY, EktooUITranslator.getErrorEmptyOrNull(MySQLUIController.DATABASE_NAME_PROPERTY));
      isValid = false;
    }
    
    return isValid;
  }
}
