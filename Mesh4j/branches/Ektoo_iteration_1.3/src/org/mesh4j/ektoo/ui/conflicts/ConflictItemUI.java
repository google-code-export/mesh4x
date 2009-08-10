package org.mesh4j.ektoo.ui.conflicts;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.io.OutputFormat;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.utils.XMLHelper;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class ConflictItemUI extends JPanel implements IWinnerUI {

	private final static Log LOGGER = LogFactory.getLog(ConflictItemUI.class);
	
	// MODEL VARIABLES
	private ConflictsUI owner;
	private Item conflict;
	private IRDFSchema rdfSchema;
	private RDFInstance rdfInstance;

	private JButton buttonWinner;
	private JButton buttonXML;
	private boolean viewAsXML;
	private JScrollPane scrollPanePropertiesTable;
	private JScrollPane scrollPaneXML;
	private JTextArea textAreaXML;
	
	// BUSINESS METHODS

	private static final long serialVersionUID = 1873730501513867425L;

	public ConflictItemUI(ConflictsUI ui, Item conflictItem, IRDFSchema rdfSchema, boolean isCurrentVersion) {
		super();
		this.owner = ui;
		this.conflict = conflictItem;
		this.rdfSchema = rdfSchema;	
		if(rdfSchema != null && conflict != null && !conflict.isDeleted()){
			this.rdfInstance = rdfSchema.createNewInstanceFromRDFXML(conflict.getContent().getPayload());
		}
				
		setPreferredSize(new Dimension(90, 106));

		setBounds(100, 100, 199, 265);
		setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("76dlu"),
				ColumnSpec.decode("14dlu"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("106dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC}));

		final JLabel labelBy = new JLabel();
		labelBy.setFont(new Font("Calibri", Font.PLAIN, 9));
		labelBy.setText(getByMessage());
		add(labelBy, new CellConstraints(2, 2, 2, 1));

		final JLabel labelVersion = new JLabel();
		if(isCurrentVersion){
			labelVersion.setText(getCurrentVersionMessage());
		} else {
			labelVersion.setText(getLastVersionMessage());
		}
		add(labelVersion, new CellConstraints(2, 4));

		buttonWinner = new JButton();
		buttonWinner.setContentAreaFilled(true);
		buttonWinner.setBorderPainted(true);
		buttonWinner.setBackground(Color.WHITE);
		buttonWinner.setText(EktooUITranslator.getConflictItemLabelChooseWinner());
		buttonWinner.addActionListener(new ActionListener(){ @Override public void actionPerformed(ActionEvent e) { setAsWinner(); }});
		add(buttonWinner, new CellConstraints(2, 8, 2, 1, CellConstraints.FILL, CellConstraints.FILL));

		ConflictUITableModel tableModel = new ConflictUITableModel();
		
		final JTable table = new JTable();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setModel(tableModel);
		table.setAutoscrolls(true);
		table.setShowGrid(true);
		table.setShowVerticalLines(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setBackground(Color.WHITE);
		table.setPreferredScrollableViewportSize(new Dimension(200, 106));
		table.setFillsViewportHeight(true);

		this.initColumnSizes(table);

		scrollPanePropertiesTable = new JScrollPane(table);
		add(scrollPanePropertiesTable, new CellConstraints(2, 6, 2, 1, CellConstraints.FILL, CellConstraints.FILL));

		textAreaXML = new JTextArea();
		textAreaXML.setWrapStyleWord(true);
		textAreaXML.setLineWrap(true);
		textAreaXML.setEditable(false);
		
		scrollPaneXML = new JScrollPane(textAreaXML);
		scrollPaneXML.setVisible(false);
		add(scrollPaneXML, new CellConstraints(2, 6, 2, 1, CellConstraints.FILL, CellConstraints.FILL));

		buttonXML = new JButton();
		buttonXML.setMargin(new Insets(0, 0, 0, 0));
		buttonXML.setIconTextGap(4);
		buttonXML.setContentAreaFilled(true);
		buttonXML.setBorderPainted(false);
		buttonXML.addActionListener(new ActionListener() {public void actionPerformed(final ActionEvent arg) { setViewPanel(); }});
		buttonXML.setBackground(Color.WHITE);
		buttonXML.setText(EktooUITranslator.getConflictItemLabelXML());
		add(buttonXML, new CellConstraints(3, 4, CellConstraints.CENTER, CellConstraints.FILL));
	
		// initialize

		if(conflict != null){
			if(conflict.isDeleted()){
				textAreaXML.setText(EktooUITranslator.getConflictItemMessageDeleted());
				scrollPanePropertiesTable.setVisible(false);
				scrollPaneXML.setVisible(true);
				buttonXML.setVisible(false);
				buttonXML.setEnabled(false);
				viewAsXML = true;
			} else if(rdfSchema == null){
				try{
					textAreaXML.setText(XMLHelper.formatXML(conflict.getContent().getPayload(), OutputFormat.createPrettyPrint()));
					scrollPanePropertiesTable.setVisible(false);
					scrollPaneXML.setVisible(true);
					buttonXML.setVisible(false);
					buttonXML.setEnabled(false);
					viewAsXML = true;
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			} else {
				viewAsXML = false;
			}
		} else {
			viewAsXML = false;
		}
		
		if(isCurrentVersion){
			setAsWinner();
		} else {
			setAsConflict();
		}
	}

	private String getLastVersionMessage(){
		if(conflict == null){
			return EktooUITranslator.getConflictItemMessageLastVersion(0);
		} else {
			return EktooUITranslator.getConflictItemMessageLastVersion(conflict.getLastUpdate().getSequence());
		}
	}

	private String getCurrentVersionMessage(){
		if(conflict == null){
			return EktooUITranslator.getConflictItemMessageCurrentVersion(0);
		} else {
			return EktooUITranslator.getConflictItemMessageCurrentVersion(conflict.getLastUpdate().getSequence());
		}
	}

	private String getByMessage() {
		if(conflict == null){
			return EktooUITranslator.getConflictItemMessageByOn(null, null);
		} else {
			return EktooUITranslator.getConflictItemMessageByOn(conflict.getLastUpdate().getBy(), conflict.getLastUpdate().getWhen());
		}
	}

	private void initColumnSizes(JTable table) {
		ConflictUITableModel model = (ConflictUITableModel) table.getModel();
		TableColumn column = null;
		Component comp = null;
		int headerWidth = 0;
		int cellWidth = 0;
		String[] longValues = model.getLongValues();
		TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();

		int minWidth = 139;
		for (int i = 0; i < 2; i++) {
			column = table.getColumnModel().getColumn(i);

			comp = headerRenderer.getTableCellRendererComponent(null, column.getHeaderValue(), false, false, 0, 0);
			headerWidth = comp.getPreferredSize().width;

			String longValue = longValues[i];
			if(longValue.isEmpty()){
				cellWidth = 69;	
			} else {
				comp = table.getDefaultRenderer(model.getColumnClass(i)).getTableCellRendererComponent(table, longValue, false, false, 0, i);
				cellWidth = comp.getPreferredSize().width;
				if(i==1){
					cellWidth = Math.max(cellWidth, minWidth);
				}
			}
			
			int width = Math.max(headerWidth, cellWidth);
			minWidth = minWidth - width;
			column.setPreferredWidth(width + 8);
		}
	}
	
	public void setAsWinner() {
		this.buttonWinner.setVisible(false);
		this.setBackground(Color.GREEN);		
		this.buttonXML.setBackground(Color.GREEN);
		
		this.owner.setWinner(ConflictItemUI.this);
	}
	
	public void setAsConflict() {
		this.buttonWinner.setVisible(true);
		this.buttonWinner.setEnabled(true);
		this.setBackground(Color.WHITE);
		this.buttonXML.setBackground(Color.WHITE);		
	}
	
	public void setViewPanel(){
		if(!viewAsXML){
			try{
				if(rdfInstance == null){
					if(conflict == null){
						textAreaXML.setText("");
					} else {
						if(conflict.isDeleted()){
							textAreaXML.setText(EktooUITranslator.getConflictItemMessageDeleted());
						} else {
							textAreaXML.setText(XMLHelper.formatXML(conflict.getContent().getPayload(), OutputFormat.createPrettyPrint()));
						}
					}
				} else {
					textAreaXML.setText(XMLHelper.formatXML(rdfInstance.asElementPlainXml(ISchema.EMPTY_FORMATS, null), OutputFormat.createPrettyPrint()));
				}
				scrollPanePropertiesTable.setVisible(false);
				scrollPaneXML.setVisible(true);
				buttonXML.setText(EktooUITranslator.getConflictItemLabelVAL());
				viewAsXML = true;
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		} else {
			scrollPaneXML.setVisible(false);
			scrollPanePropertiesTable.setVisible(true);
			buttonXML.setText(EktooUITranslator.getConflictItemLabelXML());
			viewAsXML = false;
		}
	}

	class ConflictUITableModel extends AbstractTableModel {
		
		private static final long serialVersionUID = -391843927926100462L;
		
		// MODEL VARIABLES
		private String[] columns;
		private String[][] cells;
		
		// BUSINESS METHODS
		
		public ConflictUITableModel() {
			
			this.columns = new String[2];
			
			this.columns[0] = EktooUITranslator.getConflictItemLabelField();
			this.columns[1] = EktooUITranslator.getConflictItemLabelValue();
			
			if(rdfSchema == null || (conflict != null && conflict.isDeleted())){
				this.cells = new String[1][2];
				this.cells[0][0] = "";
				this.cells[0][1] = "";
			} else {
				int size =  rdfSchema.getPropertyCount();
				this.cells = new String[size][2];
				
				String propertyName;
				for (int i = 0; i < size; i++) {
					propertyName = rdfSchema.getPropertyName(i);
					this.cells[i][0] = rdfSchema.getPropertyLabel(propertyName);
					
					if(rdfInstance == null){
						this.cells[i][1] = "";
					} else {
						this.cells[i][1] = rdfInstance.getPropertyValueAsLexicalForm(propertyName);
					}
				}
			}
			
		}

		public int getRowCount() {
			return this.cells.length;
		}

		public int getColumnCount() {
			return this.columns.length;
		}

		public String getColumnName(int column) {
			return this.columns[column];
		}

		public Object getValueAt(int row, int column) {
			return this.cells[row].length > column ? this.cells[row][column] : "";
		}
		
		public String[] getLongValues() {
			String[] max = new String[]{"", ""};
			for (String[] cell : this.cells) {
				if (cell[0].length() > max[0].length()){
					max[0] = cell[0];
				}
				
				if (cell[1] != null && cell[1].length() > max[1].length()){
					max[1] = cell[1];
				}
			}
			return max;
		}
		
		@Override
		public boolean isCellEditable(int x, int y){
			return false;
		}
	}
	
	public String getSyncId() {
		return this.conflict == null ? "" : this.conflict.getSyncId();
	}

	public Item getWinner() {
		return this.conflict;
	}

}
