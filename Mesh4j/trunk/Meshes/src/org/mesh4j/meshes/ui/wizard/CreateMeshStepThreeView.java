package org.mesh4j.meshes.ui.wizard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JToggleButton;

import net.miginfocom.swing.MigLayout;

import org.mesh4j.meshes.ui.resource.ResourceManager;

public class CreateMeshStepThreeView extends BaseWizardPanel {

	private static final long serialVersionUID = -5773369351266179486L;
	
	private JToggleButton tableButton;
	private JToggleButton mapButton;
	private JToggleButton filesButton;
	private ButtonGroup buttonGroup;
	
	public CreateMeshStepThreeView() {
		super();
		initComponents();
	}

	private void initComponents() {
		setLayout(new MigLayout("insets 10"));
		setSize(550, 350);
		
		JLabel titleLabel = new JLabel("Create a new data source");
		add(titleLabel, "span 2, wrap 20");
		
		JLabel subTitleLabel = new JLabel();
		subTitleLabel.setText("<html>When you add a data surce to your mesh, you can see it mobile devices, " +
							  "maps, or applications. You can even allow other applications to update the data</html>");
		add(subTitleLabel, "span 2, wrap 20");
		
		JLabel dataSourceQuestion = new JLabel();
		dataSourceQuestion.setText("What data source would you like to add?");
		add(dataSourceQuestion, "span 2, wrap 5");
		
		tableButton = new JToggleButton();
		tableButton.setText("A table of data");
		ImageIcon tableIcon = new ImageIcon(ResourceManager.getTableImage());
		tableButton.setIcon(tableIcon);
		JLabel tableLabel = new JLabel();
		tableLabel.setText("You can add Access Databases, EpiInfo surveys, Excel Spreadsheets, JavaROSA Forms, or Google Spreadsheets to your mesh");
		add(tableButton, "gapright 10");
		add(tableLabel, "growx, wrap");
		
		tableButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tableButtonActionPerformed(e);
			}
		});
		
		mapButton = new JToggleButton();
		mapButton.setText("A map");
		ImageIcon mapIcon = new ImageIcon(ResourceManager.getMapImage());
		mapButton.setIcon(mapIcon);
		JLabel mapLabel = new JLabel();
		mapLabel.setText("Share maps including pushpins, polygons, lines, icons and other information");
		add(mapButton, "gapright 10");
		add(mapLabel, "growx, wrap");
		
		mapButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mapButtonActionPerformed(e);
			}
		});
		
		filesButton = new JToggleButton();
		filesButton.setText("Files");
		ImageIcon filesIcon = new ImageIcon(ResourceManager.getFolderImage());
		filesButton.setIcon(filesIcon);
		JLabel filesLabel = new JLabel();
		filesLabel.setText("Share a set of files");
		add(filesButton, "gapright 10");
		add(filesLabel, "growx, wrap");
		
		filesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				filesButtonActionPerformed(e);
			}
		});
		
		buttonGroup = new ButtonGroup();
		buttonGroup.add(tableButton);
		buttonGroup.add(mapButton);
		buttonGroup.add(filesButton);
	}
	

	private void tableButtonActionPerformed(ActionEvent e) {
		getController().setTableDataSetType();
	}
	

	private void mapButtonActionPerformed(ActionEvent e) {
		getController().setMapDataSetType();
	}
	

	private void filesButtonActionPerformed(ActionEvent e) {
		getController().setFilesDataSetType();
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
	}

}
