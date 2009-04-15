/**
 *
 */
package org.mesh4j.ektoo.ui;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.mesh4j.sync.adapters.msexcel.MsExcel;
import org.mesh4j.sync.adapters.msexcel.MsExcelUtils;


/**
 * @author Asus
 *
 */
public class EktooUI extends JFrame {

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


	private MsExcel sourceMsExcelFile = null;
	private MsExcel targetMsExcelFile = null;

	private File sourceFile = null;
	private File targetFile = null;

	private JPanel jPanel = null;
	private JPanel sourcePane = null;
	private JLayeredPane targetPane = null;
	private JLayeredPane viaPane = null;
	private JLayeredPane typePane = null;
	private JButton btnSync = null;
	private JComboBox sourceType = null;


	private JLabel labelSourceType = null;
	private JComboBox targetType = null;
	private JLabel labelSourceType1 = null;

	private JRadioButton rbWeb = null;
	private JRadioButton rbSMS = null;
	private JRadioButton rbFile = null;
	private JRadioButton rbSent = null;
	private JRadioButton rbReceive = null;
	private JRadioButton rbSendReceive = null;

	private ButtonGroup btngSyncVia = new ButtonGroup();
	private ButtonGroup btngSyncType = new ButtonGroup();
	private JLabel txtConsole = null;

	private JPanel jPanel2 = null;

	/**
	 * This method initializes this
	 *
	 */
	private void initialize() {
        this.setSize(new Dimension(564, 511));
        this.setContentPane(getJPanel());
	}

	/**
	 * This method initializes jPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel()
	{
		if (jPanel == null) {
			txtConsole = new JLabel();
			txtConsole.setBounds(new Rectangle(15, 389, 525, 16));
			txtConsole.setText("");
			jPanel = new JPanel();
			jPanel.setLayout(null);
			jPanel.add(getSourcePane(), null);
			jPanel.add(getTargetPane(), null);
			jPanel.add(getViaPane(), null);
			jPanel.add(getTypePane(), null);
			jPanel.add(getBtnSync(), null);
			jPanel.add(txtConsole, null);
		}
		return jPanel;
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
		    "Source"));
			sourcePane.setSize(new Dimension(350, 197));
			sourcePane.setLocation(new Point(13, 36));
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
	 * This method initializes targetPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JLayeredPane getTargetPane()
	{
		if (targetPane == null) {
			targetPane = new JLayeredPane();
			targetPane.setBorder(BorderFactory.createTitledBorder(
		    "Target"));
			targetPane.setSize(new Dimension(350, 166));
			targetPane.setLocation(new Point(21, 244));
			targetPane.add(getTargetType(), null);
			targetPane.add(getLabelSourceType1(), null);
		}
		return targetPane;
	}

	/**
	 * This method initializes viaPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JLayeredPane getViaPane() {
		if (viaPane == null) {
			viaPane = new JLayeredPane();
			//viaPane.setLayout(new GridBagLayout());
			viaPane.setBorder(BorderFactory.createTitledBorder(
		    "Sync Via"));
			viaPane.setSize(new Dimension(150, 164));
			viaPane.setLocation(new Point(390, 16));
			viaPane.add(getRbWeb(), null);
			viaPane.add(getRbSMS(), null);
			viaPane.add(getRbFile(), null);


		}
		return viaPane;
	}

	/**
	 * This method initializes typePane
	 *
	 * @return javax.swing.JPanel
	 */
	private JLayeredPane getTypePane() {
		if (typePane == null) {
			typePane = new JLayeredPane();
			typePane.setBorder(BorderFactory.createTitledBorder(
		    "Sync Type"));
			typePane.setSize(new Dimension(150, 166));
			typePane.setLocation(new Point(387, 210));
			typePane.add(getRbSent(), null);
			typePane.add(getRbReceive(), null);
			typePane.add(getRbSendReceive(), null);
		}
		return typePane;
	}

	/**
	 * This method initializes btnSync
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getBtnSync() {
		if (btnSync == null) {
			btnSync = new JButton();
			btnSync.setBounds(new Rectangle(315, 427, 127, 28));
			btnSync.setText("Sync Now");
			btnSync.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()


					//txtConsole.setText("");
					//String result = new EktooUIController().sync(sourceFile, (String)listSourceWorksheet.getSelectedItem(), (String)listSourceWorksheetColumn.getSelectedItem(),
															  // targetFile, (String)listTargetWorksheet.getSelectedItem(), (String)listTargetWorksheetColumn.getSelectedItem());
					///txtConsole.setText(result);
					//System.out.println("????????->" + test);
					//new EktooUIController().sync(sourceFile, sourceSyncFile, targetFile, targetSyncFile);

				}
			});
		}
		return btnSync;
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
	 * This method initializes targetType
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getTargetType() {
		if (targetType == null) {
			targetType = new JComboBox();
			targetType.setBounds(new Rectangle(118, 22, 194, 20));

			if (SourceOrTargetType != null)
			{
				String[] types = SourceOrTargetType.split("~");

				for(int i=0; i < types.length; i++)
				{
					if (types[i] != null && types[i].length()!= 0)
						targetType.addItem(types[i]);
				}
			}
			targetType.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					System.out.println("itemStateChanged()"); // TODO Auto-generated Event stub itemStateChanged()
					//int index = targetType.getSelectedIndex();
					//if (index != -1)
					//	showHide(index, 1);
				}
			});

		}
		return targetType;
	}

	/**
	 * This method initializes labelSourceType1
	 *
	 * @return javax.swing.JLabel
	 */
	private JLabel getLabelSourceType1() {
		if (labelSourceType1 == null) {
			labelSourceType1 = new JLabel();
			labelSourceType1.setBounds(new Rectangle(16, 23, 54, 16));
			labelSourceType1.setText("Type");
		}
		return labelSourceType1;
	}

	/**
	 * This method initializes rbWeb
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getRbWeb() {
		if (rbWeb == null) {
			rbWeb = new JRadioButton();
			rbWeb.setBounds(new Rectangle(18, 26, 95, 24));
			rbWeb.setText("Web");
			btngSyncVia.add(rbWeb);
		}
		return rbWeb;
	}

	/**
	 * This method initializes rbSMS
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getRbSMS() {
		if (rbSMS == null) {
			rbSMS = new JRadioButton();
			rbSMS.setBounds(new Rectangle(18, 49, 92, 24));
			rbSMS.setText("SMS");
			btngSyncVia.add(rbSMS);
		}
		return rbSMS;
	}

	/**
	 * This method initializes rbFile
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getRbFile() {
		if (rbFile == null) {
			rbFile = new JRadioButton();
			rbFile.setBounds(new Rectangle(19, 77, 107, 24));
			rbFile.setText("File");
			rbFile.setSelected(true);
			btngSyncVia.add(rbFile);
		}
		return rbFile;
	}

	/**
	 * This method initializes rbSent
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getRbSent() {
		if (rbSent == null) {
			rbSent = new JRadioButton();
			rbSent.setBounds(new Rectangle(13, 28, 124, 21));
			rbSent.setText("Send");
			btngSyncType.add(rbSent);
		}
		return rbSent;
	}

	/**
	 * This method initializes rbReceive
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getRbReceive() {
		if (rbReceive == null) {
			rbReceive = new JRadioButton();
			rbReceive.setBounds(new Rectangle(13, 53, 124, 21));
			rbReceive.setText("Receive");
			btngSyncType.add(rbReceive);
		}
		return rbReceive;
	}

	/**
	 * This method initializes rbSendReceive
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getRbSendReceive() {
		if (rbSendReceive == null) {
			rbSendReceive = new JRadioButton();
			rbSendReceive.setBounds(new Rectangle(13, 78, 124, 21));
			rbSendReceive.setText("Send & Receive");
			rbSendReceive.setSelected(true);
			btngSyncType.add(rbSendReceive);
		}
		return rbSendReceive;
	}

	/**
	 * This method initializes jPanel2
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			jPanel2 = new JPanel();
			jPanel2.setLayout(new GridBagLayout());
			jPanel2.setBackground(new Color(238, 65, 238));
		}
		return jPanel2;
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

	/**
	 * This is the default constructor
	 */
	public EktooUI() {
		super();
		initialize();
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
}  //  @jve:decl-index=0:visual-constraint="10,10"
