package org.mesh4j.meshes.ui.wizard;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import org.mesh4j.meshes.controller.WizardController;
import org.mesh4j.meshes.ui.AbstractView;

public class WizardView extends AbstractView {

	private static final long serialVersionUID = 877057816883834656L;
	
	private WizardController controller;
	
	private CardLayout cardLayout;
	private JPanel cardPanel;
	private JLabel errorMessageLabel;
	private JButton backButton;
	private JButton nextButton;
	private JButton cancelButton;
	private JButton finishButton;
	
	public WizardView() {
		super();
		
		initComponents();
	}

	private void initComponents() {
		setSize(550, 350);
		setResizable(false);
		JPanel buttonPanel = new JPanel();
	    Box buttonBox = new Box(BoxLayout.X_AXIS);
	    
	    errorMessageLabel = new JLabel();
	    errorMessageLabel.setBorder(new EmptyBorder(new Insets(5, 20, 5, 10)));

	    cardPanel = new JPanel();
	    cardPanel.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10))); 

	    cardLayout = new CardLayout(); 
	    cardPanel.setLayout(cardLayout);
	    backButton = new JButton("Back");
	    nextButton = new JButton("Next");
	    cancelButton = new JButton("Cancel");
	    finishButton = new JButton("Finish");

	    backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				backButtonActionPerformed(evt);
			}
		});
	    
	    nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				nextButtonActionPerformed(evt);
			}
		});
	    
	    cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});
	    
	    finishButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				finishButtonActionPerformed(e);
			}
		});

	    buttonPanel.setLayout(new BorderLayout());
	    buttonPanel.add(new JSeparator(), BorderLayout.NORTH);

	    buttonBox.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10))); 
	    buttonBox.add(backButton);
	    buttonBox.add(Box.createHorizontalStrut(10));
	    buttonBox.add(nextButton);
	    buttonBox.add(Box.createHorizontalStrut(30));
	    buttonBox.add(cancelButton);
	    buttonBox.add(finishButton);
	    
	    buttonPanel.add(buttonBox, BorderLayout.EAST);
	    
	    add(buttonPanel, BorderLayout.SOUTH);
	    
	    JPanel centerPanel = new JPanel();
	    centerPanel.setLayout(new BorderLayout());
	    centerPanel.add(cardPanel, BorderLayout.NORTH);
	    centerPanel.add(errorMessageLabel, BorderLayout.SOUTH);
	    add(centerPanel, BorderLayout.CENTER);
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
	}
	
	public void setController(WizardController controller) {
		this.controller = controller;
	}
	
	public void registerWizardPanel(String id, BaseWizardPanel panel) {
		cardPanel.add(id, panel);
	}
	
	public void setCurrentPanel(String id) {
	    cardLayout.show(cardPanel, id);
	}
	
	public void setErrorMessage(String errorMessage) {
		if (errorMessage == null) {
			errorMessageLabel.setText(" ");
		} else {
			errorMessageLabel.setText("<html><span style=\"color:red\">" + errorMessage + "</span></html>");
		}
	}

	public void setBackButtonEnabled(boolean b) {
	    backButton.setEnabled(b);
	}
	
	public void setNextButtonEnabled(boolean b) {
	    nextButton.setEnabled(b);
	}
	
	public void setCancelVisible(boolean b) {
	    cancelButton.setVisible(b);
	}
	
	public void setFinishVisible(boolean b) {
	    finishButton.setVisible(b);
	}
	
	private void backButtonActionPerformed(ActionEvent evt) {
		controller.backButtonPressed();
	}
	
	private void nextButtonActionPerformed(ActionEvent evt) {
		controller.nextButtonPressed();
	}
	
	private void cancelButtonActionPerformed(ActionEvent evt) {
		setVisible(false);
		dispose();
	}
	
	private void finishButtonActionPerformed(ActionEvent evt) {
		controller.finish();
		setVisible(false);
		dispose();
	}

}
