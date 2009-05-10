package org.mesh4j.ektoo.ui.validator;

import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.model.AbstractModel;
import org.mesh4j.ektoo.model.MySQLAdapterModel;
import org.mesh4j.ektoo.ui.EktooUI;
import org.mesh4j.ektoo.validator.AbstractValidator;
/**
 * @author Bhuiyan Mohammad Iklash
 *
 */
public class MySQLConnectionValidator extends AbstractValidator
{ 
  private final Log LOGGER = LogFactory.getLog(MySQLConnectionValidator.class);
 
  private Object form;
  private Object model;
  
  public MySQLConnectionValidator(JPanel form, AbstractModel model)
  {
    super(form, model);
    this.form = form;
    this.model = model;
  }
 
  @Override
  protected boolean validate()
  {
    MySQLAdapterModel mysqlModel  = (MySQLAdapterModel) this.model;
    if (mysqlModel == null) 
      return false;
    
    String userName = mysqlModel.getUserName();
    if (userName == null || userName.trim().length() == 0)
      return false;

    String hostName = mysqlModel.getHostName();
    if (hostName == null || hostName.trim().length() == 0)
      return false;

    int portNo = mysqlModel.getPortNo();
    if (portNo < 0)
      return false;

    String databaseName = mysqlModel.getDatabaseName();
    if (databaseName == null || databaseName.trim().length() == 0)
      return false;
    
    return true;
  }
}
