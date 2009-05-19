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

import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.controller.CloudUIController;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;

/**
 * @author Bhuiyan Mohammad Iklash
 * 
 */
public class CloudUI extends AbstractUI{
	
	private static final long serialVersionUID = 101977159720664976L;
	private static final Log LOGGER = LogFactory.getLog(CloudUI.class);
	
	// MODEL VARIABLES
	private JLabel labelMash = null;
	private JTextField txtMash = null;

	private JLabel labelDataset = null;
	private JTextField txtDataset = null;

	private CloudUIController controller = null;

	// BUSINESS METHODS
	public CloudUI(CloudUIController controller) {
		super();
		this.controller = controller;
		this.controller.addView(this);
		initialize();
	}

	private void initialize() {
		this.setLayout(null);
		this.setBackground(Color.WHITE);

		this.add(getMashLabel(), null);
		this.add(getMashText(), null);

		this.add(getDataSetLabel(), null);
		this.add(getDataSetText(), null);
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

	private JTextField getMashText() {
		if (txtMash == null) {
			txtMash = new JTextField();
			txtMash.setBounds(new Rectangle(101, 5, 183, 20));
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

	private JTextField getDataSetText() {
		if (txtDataset == null) {
			txtDataset = new JTextField();
			txtDataset.setBounds(new Rectangle(101, 30, 183, 20));
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
}
