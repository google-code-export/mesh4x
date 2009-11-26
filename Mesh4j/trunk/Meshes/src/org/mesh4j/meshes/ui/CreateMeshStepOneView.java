package org.mesh4j.meshes.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
		getContentPane().setLayout(new MigLayout("insets 10"));
		setSize(550, 350);
		setResizable(false);
		
		JLabel titleLabel = new JLabel("Create a new Mesh!");
		add(titleLabel, "span 2, wrap 20");
		
		JLabel subTitleLabel = new JLabel();
		subTitleLabel.setText("<html>Create a mesh! A mesh is a special database that brings together information on multiple " +
					      "computers, devices and applications, even if they occasionally connect to the internet. To " +
					      "start creating a mesh, give it a name and a description</html>");
		add(subTitleLabel, "span 2, wrap 20");
		
		JLabel nameLabel = new JLabel("Name");
		JTextField nameTextField = new JTextField();
		add(nameLabel, "gapright 20");
		add(nameTextField, "growx, wrap");
		
		JLabel descLabel = new JLabel("Description");
		JTextArea descTextArea = new JTextArea();
		JScrollPane descScrollPane = new JScrollPane();
		descTextArea.setColumns(20);
		descTextArea.setRows(5);
		descScrollPane.setViewportView(descTextArea);
		add(descLabel, "gapright 20");
		add(descScrollPane, "growx, wrap 30");
		
		JButton nextButton = new JButton("Next");
		nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });
		add(nextButton, "span 2, align right");
	}
	
	private void nextButtonActionPerformed(ActionEvent evt) {
		
	}

}
