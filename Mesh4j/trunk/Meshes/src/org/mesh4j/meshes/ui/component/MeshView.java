package org.mesh4j.meshes.ui.component;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.mesh4j.meshes.model.Mesh;

@SuppressWarnings("serial")
public class MeshView extends JComponent {

	private final Mesh mesh;
	private JTextField nameField;
	private JTextArea descriptionField;
	private JTextField serverUrlField;
	private JPasswordField passwordField;

	public MeshView(Mesh mesh) {
		this.mesh = mesh;
		addViewComponents();
	}

	private void addViewComponents() {
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 10, 5, 10);
		c.anchor = GridBagConstraints.NORTH;
		c.gridy = GridBagConstraints.RELATIVE;
		c.gridx = 0;
		
		// Labels
		
		add(new JLabel("Name: "), c);
		add(new JLabel("Description: "), c);
		add(new JLabel("Server URL: "), c);
		add(new JLabel("Password: "), c);
		
		// Controls
		
		c.gridx = 1;
		nameField = new JTextField(mesh.getName(), 30);
		nameField.setEnabled(false);
		add(nameField, c);

		descriptionField = new JTextArea(mesh.getDescription(), 3, 30);
		add(new JScrollPane(descriptionField), c);
		
		serverUrlField = new JTextField(mesh.getServerFeedUrl(), 50);
		serverUrlField.setEnabled(false);
		add(serverUrlField, c);
		
		passwordField = new JPasswordField(mesh.getPassword());
		add(passwordField, c);
		
		// Fillers
		c.gridx = 2; c.gridy = 0; c.weightx = 10;
		add(new JPanel(), c);
		c.gridx = 0; c.gridy = 4; c.weightx = 0; c.weighty = 10;
		add(new JPanel(), c);
	}
	
	
}
