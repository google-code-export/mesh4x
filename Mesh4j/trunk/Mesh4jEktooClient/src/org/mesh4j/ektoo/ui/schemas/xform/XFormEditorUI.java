package org.mesh4j.ektoo.ui.schemas.xform;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.mesh4j.ektoo.model.CloudModel;
import org.mesh4j.ektoo.ui.EktooFrame;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.utils.XMLHelper;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class XFormEditorUI  extends JPanel {
	
	private static final long serialVersionUID = -6165842991159458249L;
	private static final Log LOGGER = LogFactory.getLog(XFormEditorUI.class);
	
	// MODEL VARIABLES
	private EktooFrame ownerUI;
	private CloudModel model;
	private IRDFSchema rdfSchema;
	
	private JLabel labelStatus;
	private JTextArea textArea;
	
	// BUSINESS METHODS
	public XFormEditorUI(EktooFrame ui, CloudModel cloudModel, IRDFSchema schema, String xFormXML) {
		super();
		this.ownerUI = ui;
		this.model = cloudModel;
		this.rdfSchema = schema;
		
		setBackground(Color.WHITE);
		setBounds(100, 100, 605, 469);
		setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("291dlu"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("256dlu"),
				RowSpec.decode("26dlu")}));

		final JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, new CellConstraints(2, 2, CellConstraints.FILL, CellConstraints.FILL));

		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);

		final JPanel panelButtons = new JPanel();
		panelButtons.setBackground(Color.WHITE);
		panelButtons.setLayout(new FormLayout(
			new ColumnSpec[] {
				ColumnSpec.decode("125dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("28dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("33dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("37dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("39dlu")},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("13dlu")}));
		add(panelButtons, new CellConstraints(2, 3, CellConstraints.FILL, CellConstraints.FILL));

		final JButton buttonCancel = new JButton();
		buttonCancel.setText(EktooUITranslator.getXFormEditorLabelCancel());
		buttonCancel.setContentAreaFilled(false);
		buttonCancel.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonCancel.setBorderPainted(false);
		buttonCancel.setOpaque(false);
		buttonCancel.setFont(new Font("", Font.BOLD, 12));
		buttonCancel.addActionListener(new ActionListener(){@Override public void actionPerformed(ActionEvent e) {closePopupViewWindow();}});
		panelButtons.add(buttonCancel, new CellConstraints(3, 2, CellConstraints.FILL, CellConstraints.FILL));

		final JButton buttonSave = new JButton();
		buttonSave.setText(EktooUITranslator.getXFormEditorLabelUpload());
		buttonSave.setContentAreaFilled(false);
		buttonSave.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonSave.setBorderPainted(false);
		buttonSave.setOpaque(false);
		buttonSave.setFont(new Font("", Font.BOLD, 12));
		buttonSave.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e) {
				UploadXFormTask task = new UploadXFormTask(XFormEditorUI.this, model, rdfSchema, textArea.getText());
				task.execute();
			}			
		});
		panelButtons.add(buttonSave, new CellConstraints(5, 2, CellConstraints.FILL, CellConstraints.FILL));
		
		final JButton buttonDownload = new JButton();
		buttonDownload.setText(EktooUITranslator.getXFormEditorLabelDownload());
		buttonDownload.setContentAreaFilled(false);
		buttonDownload.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonDownload.setBorderPainted(false);
		buttonDownload.setOpaque(false);
		buttonDownload.setFont(new Font("", Font.BOLD, 12));
		buttonDownload.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e) {
				DownloadXFormTask task = new DownloadXFormTask(XFormEditorUI.this, model.getUri());
				task.execute();
			}			
		});
		panelButtons.add(buttonDownload, new CellConstraints(7, 2, CellConstraints.FILL, CellConstraints.FILL));

		final JButton buttonRegenerate = new JButton();
		buttonRegenerate.setText(EktooUITranslator.getXFormEditorLabelGenerate());
		buttonRegenerate.setContentAreaFilled(false);
		buttonRegenerate.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonRegenerate.setBorderPainted(false);
		buttonRegenerate.setOpaque(false);
		buttonRegenerate.setFont(new Font("", Font.BOLD, 12));
		buttonRegenerate.setEnabled(rdfSchema != null);
		buttonRegenerate.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e) {
				GenerateXFormFromRDFSchemaTask task = new GenerateXFormFromRDFSchemaTask(XFormEditorUI.this, rdfSchema);
				task.execute();
			}			
		});
		panelButtons.add(buttonRegenerate, new CellConstraints(9, 2, CellConstraints.FILL, CellConstraints.FILL));

		labelStatus = new JLabel();
		labelStatus.setText("");
		panelButtons.add(labelStatus, new CellConstraints(1, 2, CellConstraints.FILL, CellConstraints.CENTER));
		
		refresh(xFormXML);
	}
	
	public void closePopupViewWindow() {
		this.ownerUI.closePopupViewWindow();		
	}

	public Frame getEktooFrame() {
		return this.ownerUI.getEktooFrame();
	}

	public void refresh(String xFormXML) {
		try{
			textArea.setText(XMLHelper.formatXML(DocumentHelper.parseText(xFormXML), OutputFormat.createPrettyPrint()));
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			//MessageDialog.showErrorMessage(getEktooFrame(), e.getMessage());
		}
	}

	public void notifyUploadDone() {
		this.labelStatus.setForeground(Color.BLACK);
		this.labelStatus.setText(EktooUITranslator.getXFormEditorMessageUploadDone());
	}

	public void notifyUploadFailed() {
		this.labelStatus.setForeground(Color.RED);
		this.labelStatus.setText(EktooUITranslator.getXFormEditorMessageUploadFailed());
	}
	public void notifyDownloadDone() {
		this.labelStatus.setForeground(Color.BLACK);
		this.labelStatus.setText(EktooUITranslator.getXFormEditorMessageDownloadDone());		
	}

	public void notifyDownloadFailed() {
		this.labelStatus.setForeground(Color.RED);
		this.labelStatus.setText(EktooUITranslator.getXFormEditorMessageDownloadFailed());		
	}

	public void notifyXFormGenerationDone() {
		this.labelStatus.setForeground(Color.BLACK);
		this.labelStatus.setText(EktooUITranslator.getXFormEditorMessageGenerationDone());		
	}

	public void notifyXFormGenerationFailed() {
		this.labelStatus.setForeground(Color.RED);
		this.labelStatus.setText(EktooUITranslator.getXFormEditorMessageGenerationFailed());		
	}
}
