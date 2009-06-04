package org.mesh4j.ektoo.ui;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.controller.CloudUIController;
import org.mesh4j.ektoo.tasks.IErrorListener;
import org.mesh4j.ektoo.tasks.OpenURLTask;
import org.mesh4j.ektoo.ui.component.messagedialog.MessageDialog;
import org.mesh4j.ektoo.ui.image.ImageManager;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.ektoo.ui.validator.CloudUIValidator;
import org.mesh4j.ektoo.validator.IValidationStatus;

/**
 * @author Bhuiyan Mohammad Iklash
 * 
 */
public class CloudUI extends AbstractUI implements IValidationStatus {
	
	private static final long serialVersionUID = 101977159720664976L;
	private static final Log LOGGER = LogFactory.getLog(CloudUI.class);
	
	// MODEL VARIABLES
	private JLabel labelMash = null;
	private JTextField txtMash = null;

	private JLabel labelSyncURI = null;
	private JTextField syncTextURI = null;
	
	private JLabel labelDataset = null;
	private JTextField txtDataset = null;

	private CloudUIController controller = null;
	
	private JButton btnView = null;
	private JTextField txtURL = null;

	// BUSINESS METHODS
	public CloudUI(String baseURL, CloudUIController controller) {
		super();
		this.controller = controller;
		this.controller.addView(this);
		initialize();
		this.txtURL.setText(baseURL);
	}

	private void initialize() {
		this.setLayout(null);
		this.setBackground(Color.WHITE);

		this.add(getMashLabel(), null);
		this.add(getMashText(), null);

		this.add(getDataSetLabel(), null);
		this.add(getDataSetText(), null);
		
		this.add(getSyncURILabel(), null);
		this.add(getSyncURIText(), null);
		
		this.add(getURLText(), null);
		
		this.add(getBtnView(), null);
		
	}

	public JTextField getSyncURIText(){
		if (syncTextURI == null) {
			syncTextURI = new JTextField();
			syncTextURI.setBounds(new Rectangle(101, 59, 183, 20));
			syncTextURI.setToolTipText(EktooUITranslator.getTooltipCloudSyncServerURI());
			syncTextURI.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					try {
						getController().changeSyncServerUri(syncTextURI.getText());
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
						// TODO Handle exception
					}
				}
			});
			syncTextURI.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent evt) {
					try {
						getController().changeSyncServerUri(syncTextURI.getText());
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
						// TODO Handle exception
					}
				}
			});		
		}
		return syncTextURI;
	}
	private JLabel getSyncURILabel() {
		
		if (labelSyncURI == null) {
			labelSyncURI = new JLabel();
			labelSyncURI.setText( EktooUITranslator.getSyncURILabel());
			
			labelSyncURI.setSize(new Dimension(85, 16));
			labelSyncURI.setPreferredSize(new Dimension(85, 16));
			labelSyncURI.setLocation(new Point(8, 59));
		}
		return labelSyncURI;
	}
	
	private JLabel getMashLabel() {
		if (labelMash == null) {
			labelMash = new JLabel();
			labelMash.setText( EktooUITranslator.getMeshNameFieldLabel());
			
			labelMash.setSize(new Dimension(85, 16));
			labelMash.setPreferredSize(new Dimension(85, 16));
			labelMash.setLocation(new Point(8, 9));
		}
		return labelMash;
	}

	public JTextField getMashText() {
		if (txtMash == null) {
			txtMash = new JTextField();
			txtMash.setBounds(new Rectangle(101, 5, 183, 20));
			txtMash.setToolTipText(EktooUITranslator.getTooltipCloudMeshname());
			txtMash.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					try {
						getController().changeMeshName(txtMash.getText());
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
						// TODO Handle exception
					}
				}
			});
			txtMash.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent evt) {
					try {
						getController().changeMeshName(txtMash.getText());
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
						// TODO Handle exception
					}
				}
			});
		}
		return txtMash;
	}

	private JLabel getDataSetLabel() {
		if (labelDataset == null) {
			labelDataset = new JLabel();
			labelDataset.setText( EktooUITranslator.getMeshDataSetFieldLabel() );
			labelDataset.setSize(new Dimension(85, 16));
			labelDataset.setPreferredSize(new Dimension(85, 16));
			labelDataset.setLocation(new Point(8, 34));
		}
		return labelDataset;
	}

	public JTextField getDataSetText() {
		if (txtDataset == null) {
			txtDataset = new JTextField();
			txtDataset.setBounds(new Rectangle(101, 30, 183, 20));
			txtDataset.setToolTipText(EktooUITranslator.getTooltipCloudDatasetname());
			txtDataset.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					try {
						getController().changeDatasetName(txtDataset.getText());
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
						// TODO Handle exception
					}
				}
			});
			txtDataset.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent evt) {
					try {
						getController().changeDatasetName(txtDataset.getText());
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
						// TODO Handle exception
					}
				}
			});
		}
		return txtDataset;
	}
	
	public JTextField getURLText() {
		if (txtURL == null) {
			txtURL = new JTextField();
			txtURL.setBounds(new Rectangle(1, 89, 375, 20));
			txtURL.setEditable(false);
		}
		return txtURL;
	}

	public JButton getBtnView() {
		if (btnView == null) {
			btnView = new JButton();
			btnView.setIcon(ImageManager.getViewIcon());
			btnView.setContentAreaFilled(false);
			btnView.setBorderPainted(false);
			btnView.setBorder(new EmptyBorder(0, 0, 0, 0));
			btnView.setBackground(Color.WHITE);
			btnView.setText("");
			btnView.setToolTipText(EktooUITranslator.getTooltipView());
			btnView.setBounds(new Rectangle(290, 5, 34, 40));
			btnView.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFrame frame = CloudUI.this.getRootFrame();
					String url = txtURL.getText();
					
					if(txtMash.getText() != null && txtMash.getText().length() > 0){
						url = url.concat("/").concat(txtMash.getText());
						
						if(txtDataset.getText() != null && txtDataset.getText().length() > 0){
							url = url.concat("/").concat(txtDataset.getText());
						}
					}
					
					List<JComponent> uiFieldListForValidation = new ArrayList<JComponent>();
					uiFieldListForValidation.add(getMashText());
					uiFieldListForValidation.add(getDataSetText());
					uiFieldListForValidation.add(getSyncURIText());
					boolean valid = (new CloudUIValidator(CloudUI.this,
							controller.getModel(), uiFieldListForValidation)).verify();
					if(valid){
						OpenURLTask task = new OpenURLTask(frame, (IErrorListener)frame, url);
						task.execute();
					}
				}
			});
		}
		return btnView;
	}
	
	// TODO (nobel) improve it
	protected JFrame getRootFrame() {
		return (JFrame)this.getParent().getParent().getParent().getParent().getParent().getParent().getParent();
	}	
	
	public CloudUIController getController() {
		return controller;
	}

  @Override
  public void modelPropertyChange(final PropertyChangeEvent evt)
  {
    if ( evt.getPropertyName().equals( CloudUIController.MESH_NAME_PROPERTY))
    {
      String newStringValue = evt.getNewValue().toString();
      if (! getMashText().getText().equals(newStringValue))
        getMashText().setText(newStringValue);
    }
    else if ( evt.getPropertyName().equals( CloudUIController.DATASET_NAME_PROPERTY))
    {
      String newStringValue = evt.getNewValue().toString();
      if (! getDataSetText().getText().equals(newStringValue))
        getDataSetText().setText(newStringValue);
    }    
  }

	@Override
	public boolean verify() {
		List<JComponent> uiFieldListForValidation = new ArrayList<JComponent>();
		uiFieldListForValidation.add(getMashText());
		uiFieldListForValidation.add(getDataSetText());
		uiFieldListForValidation.add(getSyncURIText());
		boolean valid = (new CloudUIValidator(CloudUI.this,
				controller.getModel(), uiFieldListForValidation)).verify();
		return valid;
	}

	@Override
	public void validationFailed(Hashtable<Object, String> errorTable) {
		Object key = null;
		String err = "";
		Enumeration<Object> keys = errorTable.keys();
		while (keys.hasMoreElements()) {
			key = keys.nextElement(); 
			err =  (String)errorTable.get(key);
			MessageDialog.showErrorMessage(JOptionPane.getRootFrame(), err);
		}
	}

	@Override
	public void validationPassed() {
		// TODO Auto-generated method stub
		
	}
}
