package org.mesh4j.ektoo.ui;

import static org.mesh4j.ektoo.Util.getProperty;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;

import org.mesh4j.ektoo.controller.CloudUIController;
import org.mesh4j.ektoo.tasks.IErrorListener;
import org.mesh4j.ektoo.tasks.OpenURLTask;
import org.mesh4j.ektoo.ui.component.DocumentModelAdapter;
import org.mesh4j.ektoo.ui.image.ImageManager;
import org.mesh4j.ektoo.ui.schemas.xform.OpenXFormEditorViewTask;
import org.mesh4j.ektoo.ui.settings.prop.AppProperties;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.ektoo.ui.validator.CloudUIValidator;

public class CloudUI extends AbstractUI {

	private static final long serialVersionUID = 101977159720664976L;

	// MODEL VARIABLES
	private JLabel labelServerURL = null;
	private JTextField txtServerURL = null;

	private JLabel labelMesh = null;
	private JTextField txtMesh = null;

	private JLabel labelDataset = null;
	private JTextField txtDataset = null;
	
	private JButton xformButton = null;

	// BUSINESS METHODS
	public CloudUI(String baseURL, CloudUIController controller) {
		super(controller);
		initialize();
		this.txtServerURL.setText(getProperty(AppProperties.CLOUD_ROOT_URI));
		this.txtMesh.setText(getProperty(AppProperties.CLOUD_MESH_NAME));
		this.txtDataset.setText(getProperty(AppProperties.CLOUD_DATASET_NAME));
		this.setMessageText(getProperty(AppProperties.CLOUD_ROOT_URI));
	}

	private void initialize() {
		this.setLayout(null);
		this.setBackground(Color.WHITE);

		this.add(getServerURLLabel(), null);
		this.add(getServerURLText(), null);

		this.add(getMeshLabel(), null);
		this.add(getMeshText(), null);

		this.add(getDataSetLabel(), null);
		this.add(getDataSetText(), null);

		this.add(getMessagesText(), null);
		this.add(getViewButton(), null);
		this.add(getConflictsButton());
		this.add(getSchemaViewButton(), null);
		this.add(getMappingsButton());
		this.add(getButtonXForm());
	}

	public JTextField getServerURLText(){
		if (this.txtServerURL == null) {
			this.txtServerURL = new JTextField();
			this.txtServerURL.setBounds(new Rectangle(101, 5, 183, 20));
			this.txtServerURL.setToolTipText(EktooUITranslator.getTooltipCloudSyncServerURI());
			txtServerURL.getDocument().addDocumentListener(new DocumentModelAdapter(){
				@Override
				public void insertUpdate(DocumentEvent e) {
					getController().changeSyncServerUri(txtServerURL.getText());
					setResultURL();
				}
				@Override
				public void removeUpdate(DocumentEvent e) {
					getController().changeSyncServerUri(txtServerURL.getText());
					setResultURL();
				}
			}
			);
			
		}
		return txtServerURL;
	}

	private JLabel getServerURLLabel() {
		if (this.labelServerURL == null) {
			this.labelServerURL = new JLabel();
			this.labelServerURL.setText(EktooUITranslator.getSyncURILabel());
			labelServerURL.setSize(new Dimension(85, 16));
			labelServerURL.setPreferredSize(new Dimension(85, 16));
			labelServerURL.setLocation(new Point(8, 9));
		}
		return this.labelServerURL;
	}

	private JLabel getMeshLabel() {
		if (labelMesh == null) {
			labelMesh = new JLabel();
			labelMesh.setText(EktooUITranslator.getMeshNameFieldLabel());
			labelMesh.setSize(new Dimension(85, 16));
			labelMesh.setPreferredSize(new Dimension(85, 16));
			labelMesh.setLocation(new Point(8, 34));
		}
		return labelMesh;
	}

	public JTextField getMeshText() {
		if (txtMesh == null) {
			txtMesh = new JTextField();
			txtMesh.setBounds(new Rectangle(101, 30, 183, 20));
			txtMesh.setToolTipText(EktooUITranslator.getTooltipCloudMeshname());
			txtMesh.getDocument().addDocumentListener(new DocumentModelAdapter(){
				@Override
				public void insertUpdate(DocumentEvent e) {
					getController().changeMeshName(txtMesh.getText());
					setResultURL();
				}
				@Override
				public void removeUpdate(DocumentEvent e) {
					getController().changeMeshName(txtMesh.getText());
					setResultURL();
				}
			}
			);
		}
		return txtMesh;
	}

	protected JLabel getDataSetLabel() {
		if (labelDataset == null) {
			labelDataset = new JLabel();
			labelDataset.setText(EktooUITranslator.getMeshDataSetFieldLabel());
			labelDataset.setSize(new Dimension(85, 16));
			labelDataset.setPreferredSize(new Dimension(85, 16));
			labelDataset.setLocation(new Point(8, 59));
		}
		return labelDataset;
	}

	public JTextField getDataSetText() {
		if (txtDataset == null) {
			txtDataset = new JTextField();
			txtDataset.setBounds(new Rectangle(101, 59, 183, 20));
			txtDataset.setToolTipText(EktooUITranslator.getTooltipCloudDatasetname());
			txtDataset.getDocument().addDocumentListener(new DocumentModelAdapter(){
				@Override
				public void insertUpdate(DocumentEvent e) {
					getController().changeDatasetName(txtDataset.getText());
					setResultURL();
				}
				@Override
				public void removeUpdate(DocumentEvent e) {
					getController().changeDatasetName(txtDataset.getText());
					setResultURL();
				}
			}
			);
		}
		return txtDataset;
	}

	@Override
	protected void viewItems() {
		List<JComponent> uiFieldListForValidation = new ArrayList<JComponent>();
		uiFieldListForValidation.add(getMeshText());
		uiFieldListForValidation.add(getDataSetText());
		uiFieldListForValidation.add(getServerURLText());
		boolean valid = (new CloudUIValidator(CloudUI.this, controller
				.getModel(), uiFieldListForValidation, false)).verify();
		if (valid) {
			JFrame frame = CloudUI.this.getRootFrame();
			String url = getController().getUri();
			OpenURLTask task = new OpenURLTask(frame,
					(IErrorListener) frame, url);
			task.execute();
		}
	}
	
	public JButton getButtonXForm() {
		if (xformButton == null) {
			xformButton = new JButton();
			xformButton.setIcon(ImageManager.getXFormIcon());
			xformButton.setContentAreaFilled(false);
			xformButton.setBorderPainted(false);
			xformButton.setBorder(new EmptyBorder(0, 0, 0, 0));
			xformButton.setBackground(Color.WHITE);
			xformButton.setBounds(new Rectangle(300, 34, 25, 25));
			xformButton.setToolTipText(EktooUITranslator.getTooltipXFormView());
			
			ActionListener actionListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (verify()) {
						EktooFrame ektooFrame = ((EktooFrame)getRootFrame());
						OpenXFormEditorViewTask task = new OpenXFormEditorViewTask(ektooFrame, getController());
						task.execute();
					}
				}
			};
			
			xformButton.addActionListener(actionListener);
			addPopUpMenuItem(EktooUITranslator.getTooltipXFormView(), ImageManager.getXFormIcon(), actionListener);
		}
		return xformButton;
	}

	public CloudUIController getController() {
		return (CloudUIController)controller;
	}

	@Override
	public void modelPropertyChange(final PropertyChangeEvent evt){
		if ( evt.getPropertyName().equals( CloudUIController.MESH_NAME_PROPERTY)){
			String newStringValue = evt.getNewValue().toString();
			if (! getMeshText().getText().equals(newStringValue)){
				getMeshText().setText(newStringValue);
			}
		} else if ( evt.getPropertyName().equals( CloudUIController.DATASET_NAME_PROPERTY)){
			String newStringValue = evt.getNewValue().toString();
			if (! getDataSetText().getText().equals(newStringValue)){
				getDataSetText().setText(newStringValue);
			}
		}  else if ( evt.getPropertyName().equals( CloudUIController.SYNC_SERVER_URI)){
			String newStringValue = evt.getNewValue().toString();
			if (! getServerURLText().getText().equals(newStringValue)){
				getServerURLText().setText(newStringValue);
			}
		}     
	}

	@Override
	public boolean verify() {
		List<JComponent> uiFieldListForValidation = new ArrayList<JComponent>();
		if (EktooFrame.multiModeSync) {
			uiFieldListForValidation.add(getServerURLText());
			uiFieldListForValidation.add(getMeshText());
		} else {
			uiFieldListForValidation = null;
		}
		boolean valid = (new CloudUIValidator(CloudUI.this, controller
				.getModel(), uiFieldListForValidation)).verify();
		// if(valid){
		// getController().changeSyncServerUri(txtServerURL.getText());
		// getController().changeMeshName(txtMesh.getText());
		// getController().changeDatasetName(txtDataset.getText());
		// }
		return valid;
	}

	private void setResultURL() {
		this.setMessageText(this.getController().getUri());
	}

}
