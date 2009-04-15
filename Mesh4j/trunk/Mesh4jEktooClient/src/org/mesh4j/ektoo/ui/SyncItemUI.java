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
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


/**
 * @author Bhuiyan Mohammad Iklash
 *
 */
public class SyncItemUI extends JPanel 
{
	private final static long serialVersionUID = 1L;
	private final static String DYMMY_PANEL = "DUMMY_PANEL";
	private final static String KML_PANEL = "KML";
	private final static String MS_EXCEL_PANEL = "MS Excel";
	private final static String GOOGLE_SPREADSHEET_PANEL = "Google Spreadsheet";
	private final static String MS_ACCESS_PANEL = "MS Access";
	private final static String SYNC_SERVER_PANEL = "Sync Server";

	JPanel panels = null;
	JPanel head = null;
	JPanel firstPanel = new JPanel();
	private MsExcelUI excelUI = null;
	//private MsAccessUI accessUI = null;
	//private KmlUI kmlUI = null;
	//private SyncServerUI suncServerUI = null;

	private final String SourceOrTargetType = "KML~Sync Server~MS Access~Google Spreadsheet~MS Excel";//EktooUITranslator.getDataSourceType();



	private File sourceFile = null;
	private File targetFile = null;

	private JPanel jPanel = null;
	private JPanel sourcePane = null;

	private JButton btnSync = null;
	private JComboBox sourceType = null;


	private JLabel labelSourceType = null;
	private JComboBox targetType = null;
	private JLabel labelSourceType1 = null;

	String title = null;

	public SyncItemUI(String title)
	{
		this.title = title;
		initialize();
	}


	/**
	 * This method initializes this
	 *
	 */
	private void initialize() 
	{
		add(getSourcePane(), null);	
	}


	/**
	 * This method initializes sourcePane
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getSourcePane() {
		if (sourcePane == null) {
			sourcePane = new JPanel();
			//sourcePane.setLayout(new GridBagLayout());
			sourcePane.setLayout(new BorderLayout());
			sourcePane.setBorder(BorderFactory.createTitledBorder(
		    this.title));
			sourcePane.setSize(new Dimension(350, 177));
			//sourcePane.setLocation(new Point(13, 36));
			sourcePane.add(getSourceHeadPane(), BorderLayout.NORTH);
			sourcePane.add(getSourceBodyPane(), BorderLayout.CENTER);
		}
		return sourcePane;
	}
	private JPanel getSourceHeadPane()
	{
		if (head == null)
		{
			head = new JPanel();
			head.setBackground(Color.green);
			head.setLayout(new FlowLayout());
			head.add(getLabelSourceType(), null);
			head.add(getSourceType(), null);
		}
		return head;

	}
	private JPanel getSourceBodyPane()
	{
		if (panels == null)
		{
			panels = new JPanel();
			panels.setBackground(Color.red);
			panels.setLayout(new CardLayout());

			//
			firstPanel.setBackground(Color.red);
			panels.add(firstPanel, DYMMY_PANEL);

//			 add cards here
			panels.add(getMsExcelUI(), MS_EXCEL_PANEL);

		}
		return panels;
	}
	

	




	/**
	 * This method initializes sourceType
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getSourceType() {
		if (sourceType == null) {
			sourceType = new JComboBox();
			sourceType.setBounds(new Rectangle(107, 13, 230, 22));
			if (SourceOrTargetType != null)
			{
				String[] types = SourceOrTargetType.split("~");

				for(int i=0; i < types.length; i++)
				{
					if (types[i] != null && types[i].length()!= 0)
						sourceType.addItem(types[i]);
				}
			}
			sourceType.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					System.out.println("itemStateChanged()"); // TODO Auto-generated Event stub itemStateChanged()
					int index = sourceType.getSelectedIndex();
					if (index != -1)
						updateLayout((String)e.getItem());

				}
			});

		}
		return sourceType;
	}

	/**
	 * This method initializes labelSourceType
	 *
	 * @return javax.swing.JLabel
	 */
	private JLabel getLabelSourceType() {
		if (labelSourceType == null) {
			labelSourceType = new JLabel();
			labelSourceType.setText("Type");
			labelSourceType.setBounds(new Rectangle(16, 18, 27, 16));
			System.out.println("dsdsdsd");
		}
		return labelSourceType;
	}



	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame thisClass = new EktooUI();
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.setVisible(true);
			}
		});
	}



	private MsExcelUI getMsExcelUI()
	{
		if (excelUI == null) {
			excelUI = new MsExcelUI("File", "Wroksheet", "Unique Column");
			//excelUI.setBounds(6,55,337,126);
			//excelUI.setBounds(new Rectangle(5, 21, 340, 30));
			//excelUI.setSize(400, 95);
			//excelUI.setLocation(8, 55);
		}
		return excelUI;
	}

	private void updateLayout(String item)
	{
		CardLayout cl = (CardLayout)(panels.getLayout());
		if (item.equals(MS_EXCEL_PANEL))
		{
		    cl.show(panels, MS_EXCEL_PANEL);
		}
		else
		{
			cl.show(panels, DYMMY_PANEL);
		}
	}
	
	
	public File getFile()
	{
		return excelUI.getFile();
	}
	
	public String getTable()
	{
		return excelUI.getTable();
	}
	
	public String getColumn()
	{
		return excelUI.getColumn();
	}
	
	public String getSyncType()
	{
		return "TABLE";
	}
	
	public String getUri()
	{
		return "";
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
