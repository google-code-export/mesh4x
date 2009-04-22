/**
 *
 */
package org.mesh4j.ektoo.ui;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.mesh4j.ektoo.ISyncTableTypeItem;
import org.mesh4j.ektoo.IUIController;
import org.mesh4j.ektoo.controller.GSSheetUIController;
import org.mesh4j.ektoo.controller.MsAccessUIController;
import org.mesh4j.ektoo.controller.MsExcelUIController;
import org.mesh4j.ektoo.model.GSSheetModel;
import org.mesh4j.ektoo.model.MsAccessModel;
import org.mesh4j.ektoo.model.MsExcelModel;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.sync.ISyncAdapter;


/**
 * @author Bhuiyan Mohammad Iklash
 *
 */
public class SyncItemUI extends JPanel implements ISyncTableTypeItem, IUIController 
{
	private final static long serialVersionUID = 1L;
	private final static String DYMMY_PANEL = "DUMMY_PANEL";
	private final static String KML_PANEL = "KML";
	private final static String MS_EXCEL_PANEL = "MS Excel";
	private final static String GOOGLE_SPREADSHEET_PANEL = "Google Spreadsheet";
	private final static String MS_ACCESS_PANEL = "MS Access";
	private final static String CLOUD_PANEL = "Cloud";

	private JPanel body = null;
	private JPanel head = null;
	private JPanel firstPanel = new JPanel();
	
	private MsExcelUI excelUI = null;
	private MsExcelUIController	excelUIController = null;
	
	private MsAccessUI accessUI = null;
	private MsAccessUIController accessUIController = null;
	
	
	private GSSheetUI googleUI = null;
	private GSSheetUIController googleUIControler = null;
	
	private KmlUI kmlUI = null;
	private CloudUI cloudUI = null;

	private String SourceOrTargetType = "KML~Cloud~MS Access~Google Spreadsheet~MS Excel";//EktooUITranslator.getDataSourceType();
	
	private JComboBox listType = null;
	private JLabel labelType = null;
	String title = null;


	public SyncItemUI(String title)
	{
		this.title = title;
		initialize();
	}

	
	private void initialize() 
	{
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder(this.title));
		setSize(new Dimension(350, 250));
		add(getHeadPane(), BorderLayout.NORTH);
		add(getBodyPane(), BorderLayout.CENTER);
		
		updateLayout((String)getListType().getSelectedItem());
	}

	private JPanel getHeadPane()
	{
		if (head == null)
		{
			head = new JPanel();
			head.setBackground(Color.green);
			head.setLayout(new FlowLayout());
			head.add(getTypeLabel(), null);
			head.add(getDataSourceType(), null);
		}
		return head;
	}
	
	private JPanel getBodyPane()
	{
		if (body == null)
		{
			body = new JPanel();
			body.setBackground(Color.red);
			body.setLayout(new CardLayout());

			//
			firstPanel.setBackground(Color.red);
			body.add(firstPanel, DYMMY_PANEL);

			// add cards here
			body.add(getMsExcelUI(), MS_EXCEL_PANEL);
			body.add(getMsAccessUI(), MS_ACCESS_PANEL);
			body.add(getGSSheetUI(), GOOGLE_SPREADSHEET_PANEL);
			body.add(getKmlUI(), KML_PANEL);
			body.add(getCloudUI(), CLOUD_PANEL);
		}
		
		return body;
	}
	
	private JComboBox getDataSourceType() 
	{
		if (getListType() == null) 
		{
			setListType(new JComboBox());
			getListType().setBounds(new Rectangle(107, 13, 230, 22));
			if (SourceOrTargetType != null)
			{
				String[] types = SourceOrTargetType.split("~");

				for(int i=0; i < types.length; i++)
				{
					if (types[i] != null && types[i].length()!= 0)
						getListType().addItem(types[i]);
				}
			}
			getListType().addItemListener(new ItemListener() 
			{
				public void itemStateChanged(ItemEvent e) 
				{
					//System.out.println("getDataSourceType()->itemStateChanged()");
					int index = getListType().getSelectedIndex();
					if (index != -1)
						updateLayout((String)e.getItem());

				}
			});

		}
		return getListType();
	}

	/**
	 * This method initializes labelSourceType
	 *
	 * @return javax.swing.JLabel
	 */
	private JLabel getTypeLabel() 
	{
		if (labelType == null) 
		{
			labelType = new JLabel();
			labelType.setText( EktooUITranslator.getSyncDataSourceType());
			labelType.setBounds(new Rectangle(16, 18, 27, 16));
		}
		return labelType;
	}


	private MsExcelUI getMsExcelUI()
	{
		if (excelUI == null) 
		{
			//excelUI = new MsExcelUI( EktooUITranslator.getExcelFileLabel(), EktooUITranslator.getExcelWorksheetLabel(), EktooUITranslator.getExcelUniqueColumnLabel());
			excelUIController = new MsExcelUIController(new PropertiesProvider());
			excelUIController.addModel( new MsExcelModel());
			
			excelUI = new MsExcelUI( excelUIController );
			excelUI.setLabelFile(EktooUITranslator.getExcelFileLabel());
			excelUI.setLabelTable(EktooUITranslator.getExcelTableLabel());
			excelUI.setLabelColumn(EktooUITranslator.getExcelUniqueColumnLabel());
		}
		return excelUI;
	}

	private MsAccessUI getMsAccessUI()
	{
		if (accessUI == null) 
		{
			//accessUI = new MsAccessUI("Database", "Table");			
			accessUIController = new MsAccessUIController(new PropertiesProvider());
			accessUIController.addModel(new MsAccessModel());
			
			accessUI = new MsAccessUI(accessUIController);	
			accessUI.setLabelFile(EktooUITranslator.getAccessFileLabel());
			accessUI.setLabelTable(EktooUITranslator.getAccessTableLabel());				
		}
		return accessUI;
	}
	
	private GSSheetUI getGSSheetUI()
	{
		if (googleUI == null) 
		{
		  googleUIControler = new GSSheetUIController(new PropertiesProvider());
	    googleUIControler.addModel(new GSSheetModel());
	    
	    googleUI = new GSSheetUI( googleUIControler );
	    googleUI.setUserLabel(EktooUITranslator.getGoogleUserLabel());
	    googleUI.setPasswordLabel(EktooUITranslator.getGooglePasswordLabel());
	    googleUI.setKeyLabel(EktooUITranslator.getGoogleKeyLabel());
	    googleUI.WorksheetLabel(EktooUITranslator.getGoogleWorksheetLabel());
	    googleUI.setUniqueColumnLabel(EktooUITranslator.getUniqueColumnNameLabel()); 
		}
		return googleUI;
	}

	private KmlUI getKmlUI()
	{
		if (kmlUI == null) 
		{
			kmlUI = new KmlUI( EktooUITranslator.getKmlUriLabel() );
		}
		return kmlUI;
	}	

	private CloudUI getCloudUI()
	{
		if (cloudUI == null) {
			cloudUI = new CloudUI("Mash", "Data Set");
		}
		return cloudUI;
	}	
	
	
	private void updateLayout(String item)
	{
		CardLayout cl = (CardLayout)(body.getLayout());
		if (item.equals(MS_EXCEL_PANEL))
		{
		    cl.show(body, MS_EXCEL_PANEL);
		}
		else if (item.equals(MS_ACCESS_PANEL))
		{
			cl.show(body, MS_ACCESS_PANEL);
		}
		else if (item.equals(GOOGLE_SPREADSHEET_PANEL))
		{
			cl.show(body, GOOGLE_SPREADSHEET_PANEL);
		}
		else if (item.equals(KML_PANEL))
		{
			cl.show(body, KML_PANEL);
		}		
		else if (item.equals(CLOUD_PANEL))
		{
			cl.show(body, CLOUD_PANEL);
		}		else
		{
			cl.show(body, DYMMY_PANEL);
		}
	}
	
	@Override
	public String getColumn() 
	{
		String column = null;
		String item = (String)getDataSourceType().getSelectedItem();
		CardLayout cl = (CardLayout)(body.getLayout());
		if (item.equals(MS_EXCEL_PANEL))
		{
			column = excelUI.getColumn();
		}
		else if (item.equals(MS_ACCESS_PANEL))
		{
			column = accessUI.getColumn();
		}
		else if (item.equals(GOOGLE_SPREADSHEET_PANEL))
		{
			column = googleUI.getColumn();
		}
		else
		{
		}
		
		return column;
	}


	@Override
	public File getFile() 
	{
		File file = null;
		String item = (String)getDataSourceType().getSelectedItem();
		CardLayout cl = (CardLayout)(body.getLayout());
		if (item.equals(MS_EXCEL_PANEL))
		{
			file = excelUI.getFile();
		}
		else if (item.equals(MS_ACCESS_PANEL))
		{
			System.out.println(">>>>>>" + accessUI.getFile());
			file = accessUI.getFile();
		}
		else if (item.equals(GOOGLE_SPREADSHEET_PANEL))
		{
			//file = googleUI.getKey();
		}
		else
		{
		}
		
		return file;
	}


	@Override
	public String getTable() 
	{
		String table = null;
		String item = (String)getDataSourceType().getSelectedItem();
		CardLayout cl = (CardLayout)(body.getLayout());
		if (item.equals(MS_EXCEL_PANEL))
		{
			table = excelUI.getTable();
		}
		else if (item.equals(MS_ACCESS_PANEL))
		{
			table = accessUI.getTable();
		}
		else if (item.equals(GOOGLE_SPREADSHEET_PANEL))
		{
			table = googleUI.getSheet();
		}
		else
		{
		}
		return table;
	}


	public void setListType(JComboBox listType) {
		this.listType = listType;
	}


	public JComboBox getListType() {
		return listType;
	}

	@Override
	public ISyncAdapter createAdapter() 
	{
		ISyncAdapter syncAdapter = null;
		
		String item = (String)getDataSourceType().getSelectedItem();
		CardLayout cl = (CardLayout)(body.getLayout());
		if (item.equals(MS_EXCEL_PANEL))
		{
			syncAdapter = excelUI.getController().createAdapter();
		}
		else if (item.equals(MS_ACCESS_PANEL))
		{
			syncAdapter = accessUI.getController().createAdapter();
		}
		else if (item.equals(GOOGLE_SPREADSHEET_PANEL))
		{
			syncAdapter = googleUI.getController().createAdapter();
		}
		else
		{
		}		
		return syncAdapter;
	}	
}
