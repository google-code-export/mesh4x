package org.mesh4j.meshes.ui.wizard;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.mesh4j.meshes.controller.CreateMeshWizardController;

public class CreateMeshStepSevenView extends BaseWizardPanel {

	private static final long serialVersionUID = -5773369351266179486L;
	private static String ID = "STEP_SEVEN";
	
	private CreateMeshWizardController controller;
	
	private JTextField nameTextField;
	private JTextArea descTextArea;
	
	public CreateMeshStepSevenView(CreateMeshWizardController controller) {
		super();
		this.controller = controller;
		initComponents();
	}

	private void initComponents() {
		setLayout(new MigLayout("insets 10"));
		setSize(550, 350);
		
		JLabel titleLabel = new JLabel("Name your data");
		add(titleLabel, "span, wrap 10");
		
		JLabel subTitleLabel = new JLabel();
		subTitleLabel.setText("Give a name to the name of the data in the mesh");
		add(subTitleLabel, "span, wrap 20");
		
		JLabel nameLabel = new JLabel("Name");
		nameTextField = new JTextField();
		add(nameLabel, "gapright 10");
		add(nameTextField, "pushx, growx, wrap 10");
		
		nameTextField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				nameTextFieldFocusLost(e);
			}
		});
		
		JLabel descLabel = new JLabel("Description");
		descTextArea = new JTextArea();
		descTextArea.setColumns(20);
		descTextArea.setRows(5);
		JScrollPane descScrollPane = new JScrollPane(descTextArea);
		add(descLabel, "gapright 10");
		add(descScrollPane, "pushx, growx");
		
		descTextArea.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				descTextAreaFocusLost(e);
			}
		});
	}
	
	private void nameTextFieldFocusLost(FocusEvent e) {
		controller.changeDataSetName(nameTextField.getText());
	}
	
	private void descTextAreaFocusLost(FocusEvent e) {
		controller.changeDataSetDescription(descTextArea.getText());
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
	}

	@Override
	public String getId() {
		return ID;
	}

}
