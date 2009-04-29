package org.mesh4j.ektoo.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.mesh4j.ektoo.controller.MySQLUIController;
import org.mesh4j.ektoo.model.MySQLAdapterModel;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.GoogleSpreadsheet;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.IGoogleSpreadSheet;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSCell;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSRow;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSWorksheet;
/**
 * @author Bhuiyan Mohammad Iklash
 *
 */
public class MySQLUI extends JPanel
{
	private static final long serialVersionUID = 1L;

	private MySQLAdapterModel model = null;
	
	private JLabel labelUser = null;
	private JTextField txtUser= null;

	private JLabel labelPass = null;
	private JPasswordField txtPass= null;

  private JLabel labelHost = null;
  private JTextField txtHost = null;

	private JLabel labelPort = null;
  private JTextField txtPort = null;

	private JLabel labelDatabase = null;
	private JTextField txtDatabase = null;


	private JLabel labelTable = null;
	private JComboBox listTable = null;

	private JLabel labelColumn = null;
	private JComboBox listColumn = null;
	
	private MySQLUIController controller = null;



	public MySQLUI() {
		super();
		initialize();
	}

  public MySQLUI(MySQLUIController controller)
  {
    super();
    this.controller = controller;
    initialize();

  }
  
	private void initialize()
	{
		this.setSize(300, 135);
		this.setLayout(null);
		this.setPreferredSize(new Dimension(300, 95));
		this.setBackground(new Color(106, 237, 238));

		this.add(getUserLabel(), null);
		this.add(getUserText(), null);

		this.add(getPassLabel(), null);
		this.add(getPassText(), null);

		this.add(getHostLabel(), null);
		this.add(getHostText(), null);

    this.add(getPortLabel(), null);
    this.add(getPortText(), null);

    this.add(getDatabaseLabel(), null);
    this.add(getDatabaseText(), null);

		this.add(getTableLabel(), null);
		this.add(getTableList(), null);

		//setDefaultValues();
	}
	private void setDefaultValues()
	{
		txtUser.setText("gspreadsheet.test@gmail.com");
		txtPass.setText("java123456");
		txtDatabase.setText("peo4fu7AitTryKJCgRNloaQ");
	}
	
	private JLabel getUserLabel() 
	{
		if (labelUser == null) 
		{
			labelUser = new JLabel();
			labelUser.setText(EktooUITranslator.getMySQLUserLabel());
			labelUser.setSize(new Dimension(85, 16));
			labelUser.setPreferredSize(new Dimension(85, 16));
			labelUser.setLocation(new Point(8, 9));
		}
		return labelUser;
	}

	private JTextField getUserText() 
	{
		if (txtUser == null) 
		{
			txtUser = new JTextField();
			txtUser.setBounds(new Rectangle(101, 5, 183, 20));
			txtUser.addActionListener(new ActionListener() 
			{
        public void actionPerformed(ActionEvent evt) 
        {
            try 
            {
              getController().changeUserName( txtUser.getText());
            } 
            catch (Exception e) 
            {
              //  Handle exception
            }            
        }
			});
			txtUser.addFocusListener(new FocusAdapter() 
			{
        public void focusLost(FocusEvent evt) 
        {
          try 
          {
            getController().changeUserName( txtUser.getText());
          } 
          catch (Exception e) 
          {
            //  Handle exception
          }            
        }
			});			
		}
		return txtUser;
	}


	private JLabel getPassLabel() 
	{
		if (labelPass == null) 
		{
			labelPass = new JLabel();
			labelPass.setText(EktooUITranslator.getMySQLPasswordLabel());
			labelPass.setSize(new Dimension(85, 16));
			labelPass.setPreferredSize(new Dimension(85, 16));
			labelPass.setLocation(new Point(8, 34));
		}
		return labelPass;
	}

	private JPasswordField getPassText() 
	{
		if (txtPass == null) 
		{
			txtPass = new JPasswordField();
			txtPass.setBounds(new Rectangle(101, 30, 183, 20));
			txtPass.addActionListener(new ActionListener() 
      {
        public void actionPerformed(ActionEvent evt) 
        {
            try 
            {
              getController().changeUserPassword(new String(txtPass.getPassword()) );
            } 
            catch (Exception e) 
            {
              //  Handle exception
            }            
        }
      });
			txtPass.addFocusListener(new FocusAdapter() 
      {
        public void focusLost(FocusEvent evt) 
        {
          try 
          {
            getController().changeUserPassword( new String(txtPass.getPassword()) );
          } 
          catch (Exception e) 
          {
            //  Handle exception
          }            
        }
      });			
		}
		return txtPass;
	}

  private JLabel getHostLabel() 
  {
    if (labelHost == null) 
    {
      labelHost = new JLabel();
      labelHost.setText(EktooUITranslator.getMySQLHostLabel());
      labelHost.setSize(new Dimension(85, 16));
      labelHost.setPreferredSize(new Dimension(85, 16));
      labelHost.setLocation(new Point(8, 59));
    }
    return labelHost;
  }

  private JTextField getHostText() 
  {
    if (txtHost == null) 
    {
      txtHost = new JTextField();
      txtHost.setBounds(new Rectangle(101, 55, 125, 20));
      txtHost.addActionListener(new ActionListener() 
      {
        public void actionPerformed(ActionEvent ae) 
        {
          try 
          {
            getController().changeHostName(txtHost.getText());
          } 
          catch (Exception e) 
          {
            //  Handle exception
          }           
        }
      });      

      txtHost.addFocusListener(new FocusAdapter() 
      {
        public void focusLost(FocusEvent evt) 
        {
          try 
          {
            getController().changeHostName( txtHost.getText());
          } 
          catch (Exception e) 
          {
            //  Handle exception
          }            
        }
      });     
    }
    return txtHost;
  }
  
  private JLabel getPortLabel() 
  {
    if (labelPort == null) 
    {
      labelPort = new JLabel();
      labelPort.setText(EktooUITranslator.getMySQLPortLabel());
      labelPort.setSize(new Dimension(85, 16));
      labelPort.setPreferredSize(new Dimension(85, 16));
      labelPort.setLocation(new Point(228, 55));
    }
    return labelPort;
  }

  private JTextField getPortText() 
  {
    if (txtPort == null) 
    {
      txtPort = new JTextField();
      txtPort.setBounds(new Rectangle(234, 55, 50, 20));
      txtPort.addActionListener(new ActionListener() 
      {
        public void actionPerformed(ActionEvent ae) 
        {
          try 
          {
            getController().changeHostName(txtPort.getText());
          } 
          catch (Exception e) 
          {
            //  Handle exception
          }           
        }
      });      

      txtPort.addFocusListener(new FocusAdapter() 
      {
        public void focusLost(FocusEvent evt) 
        {
          try 
          {
            getController().changeHostName( txtPort.getText());
          } 
          catch (Exception e) 
          {
            //  Handle exception
          }            
        }
      });     
    }
    return txtPort;
  }
  
	private JLabel getDatabaseLabel() 
	{
		if (labelDatabase == null) 
		{
			labelDatabase = new JLabel();
			labelDatabase.setText(EktooUITranslator.getMySQLDatabaseLabel());
			labelDatabase.setSize(new Dimension(85, 16));
			labelDatabase.setPreferredSize(new Dimension(85, 16));
			labelDatabase.setLocation(new Point(8, 84));
		}
		return labelDatabase;
	}

	private JTextField getDatabaseText() 
	{
		if (txtDatabase == null) 
		{
			txtDatabase = new JTextField();
			txtDatabase.setBounds(new Rectangle(101, 80, 183, 20));
			txtDatabase.addActionListener(new ActionListener() 
			{
				public void actionPerformed(ActionEvent ae) 
				{
				  try 
          {
			      getController().changeDatabaseName(txtDatabase.getText());
			    } 
          catch (Exception e) 
          {
            //  Handle exception
          } 					
					
					SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() 
					{
						public Void doInBackground() 
						{
							setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
							setList( getUser() , getPass(), getHost(), getPort(), txtDatabase.getText());
							return null;
				        }
				        public void done() 
				        {
				        	setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				        }
					};
				  worker.execute();					
				}
			});
			
			txtDatabase.addFocusListener(new FocusAdapter() 
      {
        public void focusLost(FocusEvent evt) 
        {
          try 
          {
            getController().changeDatabaseName( txtDatabase.getText());
          } 
          catch (Exception e) 
          {
            //  Handle exception
          }            
        }
      });     
		}
		return txtDatabase;
	}

	private JLabel getTableLabel() 
	{
		if (labelTable == null) 
		{
			labelTable = new JLabel();
			labelTable.setText(EktooUITranslator.getMySQLTableLabel());
			labelTable.setLocation(new Point(8, 109));
			labelTable.setSize(new Dimension(85, 16));
			labelTable.setPreferredSize(new Dimension(85, 16));
		}
		return labelTable;
	}

	public JComboBox getTableList() 
	{
		if (listTable == null) 
		{
			listTable = new JComboBox();
			listTable.setBounds(new Rectangle(101, 105, 183, 20));
			listTable.addItemListener(new ItemListener() 
			{
				public void itemStateChanged(ItemEvent e) 
				{
				  getController().changeTableName( (String)listTable.getSelectedItem());
					
					int sheetIndex = listTable.getSelectedIndex();
					if (sheetIndex != -1)
					{
						SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() 
						{
							public Void doInBackground() 
							{
								setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
								//setList(getUser() , getPass(), getDatabase(), getPort(), (String)listTable.getSelectedItem());
								return null;
					    }

							public void done() 
					    {
							  setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
							}
						};
					  worker.execute();					
					}
				}
			});
		}
		return listTable;
	}

	public void setList(String user, String pass, String host, int port, String databaseName)
	{
		JComboBox tableList = getTableList();
		tableList.removeAllItems();

		try
		{
		  // add tables in tableList here
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void setList(String user, String pass, String host, int port, String databaseName, String tableName)
	{
		
	}

	public void setController(MySQLUIController controller) {
		this.controller = controller;
	}

	public MySQLUIController getController() 
	{
		return controller;
	}

  public String getUser()
  {
    return getUserText().getText();
  }

  public String getPass()
  {
    return new String(getPassText().getPassword());
  }
  	
  public String getHost()
  {
    return getHostText().getText();
  }

  public int getPort()
  {
    return  Integer.parseInt(getPortText().getText());
  }
 
  private String getDatabase()
  {
    return getDatabaseText().getText();
  }

  public String getTable()
  {
    return (String)getTableList().getSelectedItem();
  } 
}