package org.mesh4j.meshes.ui.component;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.mesh4j.meshes.io.ConfigurationManager;
import org.mesh4j.meshes.model.Mesh;
import org.mesh4j.sync.validations.MeshException;

@SuppressWarnings("serial")
public class MeshView extends EditableComponent {

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
		c.weightx = 1;
		nameField = new JTextField(mesh.getName(), 30);
		nameField.setEnabled(false);
		add(nameField, c);

		descriptionField = new JTextArea(mesh.getDescription(), 3, 30);
		add(new JScrollPane(descriptionField), c);
		descriptionField.getDocument().addDocumentListener(new DocumentListenerImplementation());
		
		serverUrlField = new JTextField(mesh.getServerFeedUrl(), 50);
		serverUrlField.setEnabled(false);
		add(serverUrlField, c);
		
		passwordField = new JPasswordField(mesh.getPassword());
		add(passwordField, c);
		passwordField.getDocument().addDocumentListener(new DocumentListenerImplementation());
		
		// Fillers
		c.gridx = 0; c.gridy = 4; c.weightx = 0; c.weighty = 10;
		add(new JPanel(), c);
	}

	@Override
	protected void loadModel() {
		descriptionField.setText(mesh.getDescription());
		passwordField.setText(mesh.getPassword());
	}

	@Override
	public boolean isDirty() {
		return !descriptionField.getText().equals(mesh.getDescription()) ||
			!new String(passwordField.getPassword()).equals(mesh.getPassword());
	}

	@Override
	public void saveChanges() {
		mesh.setDescription(descriptionField.getText());
		mesh.setPassword(new String(passwordField.getPassword()));
		try {
			ConfigurationManager.getInstance().saveMesh(mesh);
			notifyEditableListener();
		} catch (IOException e) {
			throw new MeshException(e);
		}
	}
	
	private final class DocumentListenerImplementation implements DocumentListener {
		@Override
		public void removeUpdate(DocumentEvent e) {
			notifyEditableListener();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			notifyEditableListener();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			notifyEditableListener();
		}
	}
}
