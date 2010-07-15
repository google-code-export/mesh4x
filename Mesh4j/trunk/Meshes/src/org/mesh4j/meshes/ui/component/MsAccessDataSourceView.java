package org.mesh4j.meshes.ui.component;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.mesh4j.meshes.model.MsAccessDataSource;
import org.mesh4j.meshes.ui.FileChooserUtil;

@SuppressWarnings("serial")
public class MsAccessDataSourceView extends EditableComponent {

	private final MsAccessDataSource dataSource;
	private JTextField fileNameField;
	private JTextField tableField;
	private JButton fileBrowseButton;

	public MsAccessDataSourceView(MsAccessDataSource dataSource) {
		this.dataSource = dataSource;
		addViewComponents();
	}

	private void addViewComponents() {
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 10, 5, 10);
		//c.anchor = GridBagConstraints.NORTH;
		c.gridy = GridBagConstraints.RELATIVE;
		c.gridx = 0;
		
		// Labels
		add(new JLabel("MS Access File: "), c);
		add(new JLabel("Data Table: "), c);
		
		// Controls
		c.gridx = 1;
		c.weightx = 1;
		fileNameField = new JTextField(dataSource.getFileName());
		fileNameField.setEnabled(false);
		JPanel filePanel = new JPanel(new BorderLayout(3, 3));
		filePanel.add(fileNameField, BorderLayout.CENTER);
		filePanel.add(fileBrowseButton = new JButton("..."), BorderLayout.EAST);
		add(filePanel, c);
		
		tableField = new JTextField(dataSource.getTableName());
		tableField.setEnabled(false);
		add(tableField, c);
				
		// Fillers
		c.gridx = 0; c.gridy = 4; c.weightx = 0; c.weighty = 10;
		add(new JPanel(), c);
		
		fileBrowseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File file = FileChooserUtil.chooseEpiInfoFile(MsAccessDataSourceView.this, new File(fileNameField.getText()));
				if (file != null) {
					fileNameField.setText(file.getAbsolutePath());
					notifyEditableListener();
				}
			}
		});
	}

	@Override
	protected void loadModel() {
		fileNameField.setText(dataSource.getFileName());
	}

	@Override
	public boolean isDirty() {
		return !dataSource.getFileName().equals(fileNameField.getText());
	}

	@Override
	public void saveChanges() {
		dataSource.setFileName(fileNameField.getText());
	}
}
