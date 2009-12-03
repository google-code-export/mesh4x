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

public class CreateMeshStepOneView extends BaseWizardPanel {

	private static final long serialVersionUID = 1452642517775783582L;
	private static String ID = "STEP_ONE";
	
	private CreateMeshWizardController controller;
	
	private JTextField nameTextField;
	private JTextArea descTextArea;
	
	public CreateMeshStepOneView(CreateMeshWizardController controller) {
		super();
		this.controller = controller;
		initComponents();
	}
	
	private void initComponents() {
		setLayout(new MigLayout("insets 10"));
		setSize(550, 350);
		
		JLabel titleLabel = new JLabel("Create a new Mesh!");
		add(titleLabel, "span 2, wrap 20");
		
		JLabel subTitleLabel = new JLabel();
		subTitleLabel.setText("<html>Create a mesh! A mesh is a special database that brings together information on multiple " +
					      "computers, devices and applications, even if they occasionally connect to the internet. To " +
					      "start creating a mesh, give it a name and a description</html>");
		add(subTitleLabel, "span 2, wrap 20");
		
		JLabel nameLabel = new JLabel("Name");
		nameTextField = new JTextField();
		add(nameLabel, "gapright 20");
		add(nameTextField, "growx, wrap");
		
		JLabel descLabel = new JLabel("Description");
		descTextArea = new JTextArea();
		JScrollPane descScrollPane = new JScrollPane();
		descTextArea.setColumns(20);
		descTextArea.setRows(5);
		descScrollPane.setViewportView(descTextArea);
		add(descLabel, "gapright 20");
		add(descScrollPane, "growx");
		
		nameTextField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent evt) {
				nameTextFieldFocusLost(evt);
			}
		});
		
		descTextArea.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent evt) {
				descTextAreaFocusLost(evt);
			}
		});
	}
	
	private void nameTextFieldFocusLost(FocusEvent evt) {
		String name = nameTextField.getText();
		controller.changeMeshName(name);
	}

	private void descTextAreaFocusLost(FocusEvent evt) {
		String desc = descTextArea.getText();
		controller.changeMeshDescription(desc);
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
	}

	@Override
	public String getId() {
		return ID;
	}
}
