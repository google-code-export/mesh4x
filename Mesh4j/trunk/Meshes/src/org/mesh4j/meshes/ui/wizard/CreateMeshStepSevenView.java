package org.mesh4j.meshes.ui.wizard;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class CreateMeshStepSevenView extends JPanel {

	private static final long serialVersionUID = -5773369351266179486L;
	
	private WizardPanelDescriptor descriptor;
	
	private JTextField nameTextField;
	private JTextArea descTextArea;
	
	public CreateMeshStepSevenView(WizardPanelDescriptor descriptor) {
		super();
		this.descriptor = descriptor;
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
		add(nameTextField, "growx, wrap 10");
		
		nameTextField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				nameTextFieldFocusLost(e);
			}
		});
		
		JLabel descLabel = new JLabel("Description");
		descTextArea = new JTextArea();
		JScrollPane descScrollPane = new JScrollPane(descTextArea);
		add(descLabel, "gapright 10");
		add(descScrollPane, "growx");
		
		descTextArea.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				descTextAreaFocusLost(e);
			}
		});
	}
	
	private void nameTextFieldFocusLost(FocusEvent e) {
		descriptor.getController().changeDataSetName(nameTextField.getText());
	}
	
	private void descTextAreaFocusLost(FocusEvent e) {
		descriptor.getController().changeDataSetDescription(descTextArea.getText());
	}

}
