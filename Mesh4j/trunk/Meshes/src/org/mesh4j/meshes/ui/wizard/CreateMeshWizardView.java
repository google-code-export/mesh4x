package org.mesh4j.meshes.ui.wizard;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import org.mesh4j.meshes.controller.CreateMeshWizardController;
import org.mesh4j.meshes.ui.AbstractView;

public class CreateMeshWizardView extends AbstractView {

	private static final long serialVersionUID = 877057816883834656L;
	
	private CreateMeshWizardController controller;
	private WizardPanelDescriptor current;
	private Map<String, WizardPanelDescriptor> registeredDescriptors;
	
	private CardLayout cardLayout;
	private JPanel cardPanel;
	private JButton backButton;
	private JButton nextButton;
	private JButton cancelButton;
	
	public CreateMeshWizardView(CreateMeshWizardController controller) {
		super();
		
		this.controller = controller;
		this.registeredDescriptors = new HashMap<String, WizardPanelDescriptor>();
		
		initComponents();
	}

	private void initComponents() {
		setSize(550, 350);
		setResizable(false);
		JPanel buttonPanel = new JPanel();
	    Box buttonBox = new Box(BoxLayout.X_AXIS);

	    cardPanel = new JPanel();
	    cardPanel.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10))); 

	    cardLayout = new CardLayout(); 
	    cardPanel.setLayout(cardLayout);
	    backButton = new JButton("Back");
	    nextButton = new JButton("Next");
	    cancelButton = new JButton("Cancel");

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

	    buttonPanel.setLayout(new BorderLayout());
	    buttonPanel.add(new JSeparator(), BorderLayout.NORTH);

	    buttonBox.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10))); 
	    buttonBox.add(backButton);
	    buttonBox.add(Box.createHorizontalStrut(10));
	    buttonBox.add(nextButton);
	    buttonBox.add(Box.createHorizontalStrut(30));
	    buttonBox.add(cancelButton);
	    buttonPanel.add(buttonBox, BorderLayout.EAST);
	    add(buttonPanel, BorderLayout.SOUTH);
	    add(cardPanel, BorderLayout.CENTER);
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
	}
	
	public void registerWizardPanel(WizardPanelDescriptor descriptor) {
		registeredDescriptors.put(descriptor.getId(), descriptor);
		cardPanel.add(descriptor.getPanel(), descriptor.getId());
		descriptor.setController(controller);
	}
	
	public void setCurrentPanel(String id) {
	    cardLayout.show(cardPanel, id.toString());
	    current = registeredDescriptors.get(id);
	}

	void setBackButtonEnabled(boolean b) {
	    backButton.setEnabled(b);
	}
	
	void setNextButtonEnabled(boolean b) {
	    nextButton.setEnabled(b);
	}
	
	void setCancelButtonEnabled(boolean b) {
	    cancelButton.setEnabled(b);
	}
	
	private void backButtonActionPerformed(ActionEvent evt) {
		String backId = current.getBackPanelDescriptor();
		
		if (backId != null) {
			setCurrentPanel(backId);
		}
	}
	
	private void nextButtonActionPerformed(ActionEvent evt) {
		String nextId = current.getNextPanelDescriptor();
		
		if (nextId != null) {
			setCurrentPanel(nextId);
		} else {
			close();
		}
	}
	
	private void cancelButtonActionPerformed(ActionEvent evt) {
		setVisible(false);
		dispose();
	}
	
	private void close() {
		setVisible(false);
		dispose();
	}

}
