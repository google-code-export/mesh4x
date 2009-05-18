/**
 *
 */
package org.mesh4j.ektoo.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.mesh4j.ektoo.ISyncTableTypeItem;
import org.mesh4j.ektoo.IUIController;
import org.mesh4j.ektoo.controller.AbstractController;
import org.mesh4j.ektoo.controller.CloudUIController;
import org.mesh4j.ektoo.controller.FeedUIController;
import org.mesh4j.ektoo.controller.FolderUIController;
import org.mesh4j.ektoo.controller.GSSheetUIController;
import org.mesh4j.ektoo.controller.KmlUIController;
import org.mesh4j.ektoo.controller.MsAccessUIController;
import org.mesh4j.ektoo.controller.MsExcelUIController;
import org.mesh4j.ektoo.controller.MySQLUIController;
import org.mesh4j.ektoo.model.CloudModel;
import org.mesh4j.ektoo.model.FeedModel;
import org.mesh4j.ektoo.model.FolderModel;
import org.mesh4j.ektoo.model.GSSheetModel;
import org.mesh4j.ektoo.model.KmlModel;
import org.mesh4j.ektoo.model.MsAccessModel;
import org.mesh4j.ektoo.model.MsExcelModel;
import org.mesh4j.ektoo.model.MySQLAdapterModel;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.ektoo.ui.component.RoundBorder;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;

/**
 * @author Bhuiyan Mohammad Iklash
 * 
 */

// TODO filter combo box
public class SyncItemUI extends JPanel implements ISyncTableTypeItem,
		IUIController {

	private static final long serialVersionUID = 8681801062827267140L;
	
	private final static String DYMMY_PANEL = "DUMMY_PANEL";
	public final static String KML_PANEL = "KML";
	public final static String MS_EXCEL_PANEL = "MS Excel";
	public final static String GOOGLE_SPREADSHEET_PANEL = "Google Spreadsheet";
	public final static String MS_ACCESS_PANEL = "MS Access";
	public final static String CLOUD_PANEL = "Cloud";
	public final static String MYSQL_PANEL = "MySQL";
	public final static String RSS_FILE_PANEL = "Rss 2.0";
	public final static String ATOM_FILE_PANEL = "Atom 1.0";
	public final static String FOLDER_PANEL = "Folder";

	// MODEL VARIABLES
	private PropertiesProvider propertiesProvider;

	private JPanel body = null;
	private JPanel head = null;
	private JPanel firstPanel = new JPanel();

	private MsExcelUI excelUI = null;
	private MsExcelUIController excelUIController = null;

	private MsAccessUI accessUI = null;
	private MsAccessUIController accessUIController = null;

	private GSSheetUI googleUI = null;
	private GSSheetUIController googleUIControler = null;

	private KmlUI kmlUI = null;
	private KmlUIController kmlUIControler = null;

	private CloudUI cloudUI = null;
	private CloudUIController cloudUIControler = null;

	private MySQLUI mysqlUI = null;
	private MySQLUIController mysqlUIControler = null;

	private FeedUI rssUI = null;
	private FeedUIController rssUIControler = null;

	private FeedUI atomUI = null;
	private FeedUIController atomUIControler = null;

	private FolderUI folderUI = null;
	private FolderUIController folderUIController = null;
	
	private String SourceOrTargetType = null;

	private JComboBox listType = null;
	private JLabel labelType = null;
	String title = null;

	// BUSINESS MODEL
	public SyncItemUI(String title) {
		this.title = title;
		initialize();
	}

	private void initialize() {
		this.propertiesProvider = new PropertiesProvider();

		SourceOrTargetType = EktooUITranslator.getDataSourceType();
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder( new RoundBorder(Color.LIGHT_GRAY), this.title));

		setBackground(Color.WHITE);
		add(getHeadPane(), BorderLayout.NORTH);
		add(getBodyPane(), BorderLayout.CENTER);

		updateLayout((String) getListType().getSelectedItem());
	}

	private JPanel getHeadPane() {
		if (head == null) {
			head = new JPanel();
			head.setLayout(null);
			head.setPreferredSize(new Dimension(390, 32));
			head.add(getTypeLabel());
			head.add(getDataSourceType());
		}
		return head;
	}

	private JPanel getBodyPane() {
		if (body == null) {
			body = new JPanel();
			body.setLayout(new CardLayout());

			firstPanel.setBackground(Color.WHITE);
			body.add(firstPanel, DYMMY_PANEL);

			// add cards here
			body.add(getMsExcelUI(), MS_EXCEL_PANEL);
			body.add(getMsAccessUI(), MS_ACCESS_PANEL);
			body.add(getGSSheetUI(), GOOGLE_SPREADSHEET_PANEL);
			body.add(getKmlUI(), KML_PANEL);
			body.add(getCloudUI(), CLOUD_PANEL);
			body.add(getMySQLUI(), MYSQL_PANEL);
			body.add(getRSSFileUI(), RSS_FILE_PANEL);
			body.add(getAtomFileUI(), ATOM_FILE_PANEL);
			body.add(getFolderUI(), FOLDER_PANEL);
		}
		return body;
	}

	private JComboBox getDataSourceType() {
		if (getListType() == null) {
			setListType(new JComboBox());
			getListType().setBounds(new Rectangle(100, 5, 230, 22));
			getListType().setPreferredSize(new Dimension(200, 22));

			if (SourceOrTargetType != null) {
				StringTokenizer st = new StringTokenizer(SourceOrTargetType,
						"|");
				String type = null;
				while (st.hasMoreTokens()) {
					type = st.nextToken();
					if (type != null && type.length() != 0)
						getListType().addItem(type);
				}
			}
			getListType().addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {
					if (evt.getStateChange() == ItemEvent.SELECTED) {
						int index = getListType().getSelectedIndex();
						if (index != -1)
							updateLayout((String) evt.getItem());
					}
				}
			});

		}
		return getListType();
	}

	private JLabel getTypeLabel() {
		if (labelType == null) {
			labelType = new JLabel();
			labelType.setText(EktooUITranslator.getSyncDataSourceType());
			labelType.setBounds(new Rectangle(5, 11, 116, 16));
		}
		return labelType;
	}

	private MsExcelUI getMsExcelUI() {
		if (excelUI == null) {
			excelUIController = new MsExcelUIController(this.propertiesProvider);
			excelUIController.addModel(new MsExcelModel());

			excelUI = new MsExcelUI(excelUIController);
			excelUI.setLabelFile(EktooUITranslator.getExcelFileLabel());
			excelUI.setLabelTable(EktooUITranslator.getExcelWorksheetLabel());
			excelUI.setLabelColumn(EktooUITranslator
					.getExcelUniqueColumnLabel());
		}
		return excelUI;
	}

	private MsAccessUI getMsAccessUI() {
		if (accessUI == null) {
			accessUIController = new MsAccessUIController(
					this.propertiesProvider);
			accessUIController.addModel(new MsAccessModel());
			accessUI = new MsAccessUI(accessUIController);
			accessUI.setLabelFile(EktooUITranslator.getAccessFileLabel());
			accessUI.setLabelTable(EktooUITranslator.getAccessTableLabel());
		}
		return accessUI;
	}

	private GSSheetUI getGSSheetUI() {
		if (googleUI == null) {
			googleUIControler = new GSSheetUIController(this.propertiesProvider);
			googleUIControler.addModel(new GSSheetModel());

			googleUI = new GSSheetUI(googleUIControler);
			googleUI.setUserLabel(EktooUITranslator.getGoogleUserLabel());
			googleUI.setPasswordLabel(EktooUITranslator
					.getGooglePasswordLabel());
			googleUI.setKeyLabel(EktooUITranslator.getGoogleKeyLabel());
			googleUI
					.WorksheetLabel(EktooUITranslator.getGoogleWorksheetLabel());
			googleUI.setUniqueColumnLabel(EktooUITranslator
					.getUniqueColumnNameLabel());
		}
		return googleUI;
	}

	private KmlUI getKmlUI() {
		if (kmlUI == null) {
			kmlUIControler = new KmlUIController(this.propertiesProvider);
			kmlUIControler.addModel(new KmlModel(this.propertiesProvider
					.getDefaultKMLFile()));
			kmlUI = new KmlUI(this.propertiesProvider.getDefaultKMLFile(),
					kmlUIControler);
		}
		return kmlUI;
	}

	private CloudUI getCloudUI() {
		if (cloudUI == null) {
			cloudUIControler = new CloudUIController(this.propertiesProvider);
			cloudUIControler.addModel(new CloudModel(this.propertiesProvider
					.getMeshSyncServerURL()));
			cloudUI = new CloudUI(cloudUIControler);
		}
		return cloudUI;
	}

	private MySQLUI getMySQLUI() {
		if (mysqlUI == null) {
			mysqlUIControler = new MySQLUIController(this.propertiesProvider);
			mysqlUIControler.addModel(new MySQLAdapterModel());
			mysqlUI = new MySQLUI(mysqlUIControler);
		}
		return mysqlUI;
	}

	private FeedUI getRSSFileUI() {
		if (rssUI == null) {
			rssUIControler = new FeedUIController(this.propertiesProvider);
			rssUIControler.addModel(
				new FeedModel(
					this.propertiesProvider.getDefaultRSSFile(),
					RssSyndicationFormat.INSTANCE,
					this.propertiesProvider.getMeshSyncServerURL()));
			rssUI = new FeedUI(this.propertiesProvider.getDefaultRSSFile(),
					rssUIControler);
		}
		return rssUI;
	}

	private FeedUI getAtomFileUI() {
		if (atomUI == null) {
			atomUIControler = new FeedUIController(this.propertiesProvider);
			atomUIControler.addModel(
				new FeedModel(
						this.propertiesProvider.getDefaultAtomFile(),
						AtomSyndicationFormat.INSTANCE,
						this.propertiesProvider.getMeshSyncServerURL()));
			atomUI = new FeedUI(this.propertiesProvider.getDefaultAtomFile(),
					atomUIControler);
		}
		return atomUI;
	}
	
	private FolderUI getFolderUI() {
		if (folderUI == null) {
			folderUIController = new FolderUIController(this.propertiesProvider);
			folderUIController.addModel(new FolderModel(this.propertiesProvider.getDefaultFolderFile()));
			folderUI = new FolderUI(this.propertiesProvider.getDefaultFolderFile(), folderUIController);
		}
		return folderUI;
	}

	private void updateLayout(String item) {
		CardLayout cl = (CardLayout) (body.getLayout());

		if (item.equals(MS_EXCEL_PANEL)) {
			cl.show(body, MS_EXCEL_PANEL);
		} else if (item.equals(MS_ACCESS_PANEL)) {
			cl.show(body, MS_ACCESS_PANEL);
		} else if (item.equals(GOOGLE_SPREADSHEET_PANEL)) {
			cl.show(body, GOOGLE_SPREADSHEET_PANEL);
		} else if (item.equals(KML_PANEL)) {
			cl.show(body, KML_PANEL);
		} else if (item.equals(CLOUD_PANEL)) {
			cl.show(body, CLOUD_PANEL);
		} else if (item.equals(MYSQL_PANEL)) {
			cl.show(body, MYSQL_PANEL);
		} else if (item.equals(RSS_FILE_PANEL)) {
			cl.show(body, RSS_FILE_PANEL);
		} else if (item.equals(ATOM_FILE_PANEL)) {
			cl.show(body, ATOM_FILE_PANEL);
		} else if (item.equals(FOLDER_PANEL)) {
			cl.show(body, FOLDER_PANEL);
		} else {
			cl.show(body, DYMMY_PANEL);
		}
	}

	public void showInitCard() {
		CardLayout cl = (CardLayout) (body.getLayout());
		cl.show(body, DYMMY_PANEL);
	}

	@Override
	public String getColumn() {
		String column = null;
		String item = (String) getDataSourceType().getSelectedItem();
		// CardLayout cl = (CardLayout) (body.getLayout());
		if (item.equals(MS_EXCEL_PANEL)) {
			column = excelUI.getColumn();
		} else if (item.equals(MS_ACCESS_PANEL)) {
			column = accessUI.getColumn();
		} else if (item.equals(GOOGLE_SPREADSHEET_PANEL)) {
			column = googleUI.getColumn();
		} else {
		}

		return column;
	}

	@Override
	public File getFile() {
		File file = null;
		String item = (String) getDataSourceType().getSelectedItem();
		// CardLayout cl = (CardLayout) (body.getLayout());
		if (item.equals(MS_EXCEL_PANEL)) {
			file = excelUI.getFile();
		} else if (item.equals(MS_ACCESS_PANEL)) {
			file = accessUI.getFile();
		} else if (item.equals(GOOGLE_SPREADSHEET_PANEL)) {
			// file = googleUI.getKey();
		} else if (item.equals(MYSQL_PANEL)) {
			// file = mysqlUI.getDatabase();
		} else {
		}
		return file;
	}

	@Override
	public String getTable() {
		String table = null;
		String item = (String) getDataSourceType().getSelectedItem();

		// CardLayout cl = (CardLayout) (body.getLayout());
		if (item.equals(MS_EXCEL_PANEL)) {
			table = excelUI.getTable();
		} else if (item.equals(MS_ACCESS_PANEL)) {
			table = accessUI.getTable();
		} else if (item.equals(GOOGLE_SPREADSHEET_PANEL)) {
			table = googleUI.getSheet();
		} else if (item.equals(MYSQL_PANEL)) {
			table = mysqlUI.getTable();
		} else {
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
	public ISyncAdapter createAdapter() {
		return getCurrentController().createAdapter();
	}

	@Override
	public IRDFSchema fetchSchema(ISyncAdapter adapter) {
		return getCurrentController().fetchSchema(adapter);
	}

	@Override
	public ISyncAdapter createAdapter(IRDFSchema schema) {
		return getCurrentController().createAdapter(schema);
	}

	public String toString() {
		return getCurrentController().toString();
	}

	// TODO (NBL)improve this section
	public AbstractController getCurrentController() {
		AbstractController currrentController = null;

		String item = (String) getDataSourceType().getSelectedItem();

		if (item.equals(KML_PANEL)) {
			currrentController = kmlUI.getController();
		} else if (item.equals(MS_EXCEL_PANEL)) {
			currrentController = excelUI.getController();
		} else if (item.equals(MS_ACCESS_PANEL)) {
			currrentController = accessUI.getController();
		} else if (item.equals(GOOGLE_SPREADSHEET_PANEL)) {
			currrentController = googleUI.getController();
		} else if (item.equals(CLOUD_PANEL)) {
			currrentController = cloudUI.getController();
		} else if (item.equals(MYSQL_PANEL)) {
			currrentController = mysqlUI.getController();
		} else if (item.equals(RSS_FILE_PANEL)) {
			currrentController = rssUI.getController();
		} else if (item.equals(ATOM_FILE_PANEL)) {
			currrentController = atomUI.getController();
		} else if (item.equals(FOLDER_PANEL)) {
			currrentController = folderUI.getController();
		}

		return currrentController;
	}
}