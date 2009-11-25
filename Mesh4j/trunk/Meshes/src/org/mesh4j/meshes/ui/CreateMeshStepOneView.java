package org.mesh4j.meshes.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class CreateMeshStepOneView extends AbstractView {

	private static final long serialVersionUID = 1452642517775783582L;

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
	}
	
	public CreateMeshStepOneView() {
		super();
		initComponents();
	}
	
	private void initComponents() {
		setLayout(new MigLayout());
		setSize(550, 350);
		
		JLabel titleLabel = new JLabel("Create a new Mesh!");
		add(titleLabel, "wrap");
		
		JLabel subTitleLabel = new JLabel();
		subTitleLabel.setText("<html>Create a mesh! A mesh is a special database that brings together information on multiple <br>" +
					      "computers, devices and applications, even if they occasionally connect to the internet. To <br>" +
					      "start creating a mesh, give it a name and a description</html>");
		add(subTitleLabel, "wrap");
		
		JLabel nameLabel = new JLabel("Name");
		JTextField nameTextField = new JTextField();
		add(nameLabel);
		add(nameTextField, "wrap");
		
		JLabel descLabel = new JLabel("Description");
		JTextArea descTextArea = new JTextArea();
		JScrollPane descScrollPane = new JScrollPane();
		descTextArea.setColumns(20);
		descTextArea.setRows(5);
		descScrollPane.setViewportView(descTextArea);
		add(descLabel);
		add(descScrollPane, "wrap");
		
		JButton nextButton = new JButton("Next");
		nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });
		add(nextButton);
	}
	
	private void nextButtonActionPerformed(ActionEvent evt) {
		
	}

}
