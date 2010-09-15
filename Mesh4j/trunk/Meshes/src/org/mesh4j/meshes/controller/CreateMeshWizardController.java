package org.mesh4j.meshes.controller;

import java.awt.Desktop;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.mesh4j.meshes.io.ConfigurationManager;
import org.mesh4j.meshes.io.MeshMarshaller;
import org.mesh4j.meshes.model.DataSet;
import org.mesh4j.meshes.model.DataSetType;
import org.mesh4j.meshes.model.EpiInfoDataSource;
import org.mesh4j.meshes.model.Mesh;
import org.mesh4j.meshes.model.Schedule;
import org.mesh4j.meshes.model.SchedulingOption;
import org.mesh4j.meshes.model.SyncMode;
import org.mesh4j.meshes.server.MeshServer;
import org.mesh4j.meshes.ui.wizard.BaseWizardPanel;
import org.mesh4j.meshes.ui.wizard.WizardAccountCredentialsStep;
import org.mesh4j.meshes.ui.wizard.WizardChooseDataSourceTypeStep;
import org.mesh4j.meshes.ui.wizard.WizardConfigureDataSourceStep;
import org.mesh4j.meshes.ui.wizard.WizardConfigureSchedulingStep;
import org.mesh4j.meshes.ui.wizard.WizardConfirmMeshStep;
import org.mesh4j.meshes.ui.wizard.WizardMeshNameStep;
import org.mesh4j.meshes.ui.wizard.WizardView;
import org.mesh4j.sync.adapters.epiinfo.EpiInfoSyncAdapterFactory;

public class CreateMeshWizardController extends WizardController {
		
	private int current;
	
	public CreateMeshWizardController(WizardView wizardView) {
		super(wizardView);
				
		current = 0;
		BaseWizardPanel firstStep;
		
		registerWizardPanel(firstStep = new WizardAccountCredentialsStep(this));
		registerWizardPanel(new WizardMeshNameStep(this));
		//registerWizardPanel(new WizardMeshPasswordStep(this));
		registerWizardPanel(new WizardChooseDataSourceTypeStep(this));
		registerWizardPanel(new WizardConfigureDataSourceStep(this));
		registerWizardPanel(new WizardConfigureSchedulingStep(this));
		registerWizardPanel(new WizardConfirmMeshStep(this));
		
		setCurrentPanel(firstStep);
		setButtonsState();
	}
	
	public void finish() {
		try {
			Mesh mesh = buildMesh();
			String email = getStringValue("account.email");
			String password = getStringValue("account.password");
			
			MeshServer.getInstance().createMesh(mesh, email, password);
			ConfigurationManager.getInstance().saveMesh(mesh);
			
			Boolean saveConfigFile = (Boolean) getValue("saveConfigurationFile");
			if (saveConfigFile != null && saveConfigFile.booleanValue()) {
				saveConfiguration(mesh);
			}
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(wizardView, ex.getMessage(), "The mesh configuration file could not be saved", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}
	
	private Mesh buildMesh() {
		Mesh mesh = new Mesh();
		
		// Basic properties
		mesh.setName(getStringValue("mesh.name"));
		mesh.setDescription(getStringValue("mesh.description"));
		
		List<String> tableNames = EpiInfoSyncAdapterFactory.getTableNames(getStringValue("epiinfo.location"));
		for(String tableName : tableNames) {
			// DataSet
			DataSet dataSet = new DataSet();
			dataSet.setMesh(mesh);
			dataSet.setType(DataSetType.TABLE);
			dataSet.setName(tableName);
			mesh.getDataSets().add(dataSet);
			
			// Schedule
			Schedule schedule = new Schedule();
			schedule.setSchedulingOption((SchedulingOption) getValue("scheduling"));
			schedule.setSyncMode((SyncMode) getValue("syncmode"));
			dataSet.setSchedule(schedule);
			
			// DataSource
			EpiInfoDataSource dataSource = new EpiInfoDataSource();
			dataSource.setDataSet(dataSet);
			dataSource.setFileName(getStringValue("epiinfo.location"));
			dataSource.setTableName(tableName);
			dataSet.getDataSources().add(dataSource);
		}
		
		return mesh;
	}
	
	private void saveConfiguration(Mesh mesh) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Mesh Configuration File", "mesh");
		fileChooser.setFileFilter(filter);
		fileChooser.setSelectedFile(new File("configuration.mesh"));
		
		int returnVal = fileChooser.showSaveDialog(wizardView);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			if (selectedFile != null) {
				try {
					MeshMarshaller.toXml(mesh, selectedFile); 
					if (Desktop.isDesktopSupported())
						Desktop.getDesktop().open(selectedFile.getParentFile());
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(wizardView, ex.getMessage(), "The mesh configuration file could not be saved", JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void backButtonPressed() {
		if (!wizardView.isBackButtonEnabled()) return;
		
		BaseWizardPanel backPanel = wizardPanels.get(--current);
		setCurrentPanel(backPanel);
		setButtonsState();
	}

	@Override
	public void nextButtonPressed() {
		if (!wizardView.isNextButtonEnabled()) return;
		
		final BaseWizardPanel currentPanel = wizardPanels.get(current);
		if (currentPanel.needsValidationBeforeLeave()) {
			wizardView.setErrorMessage("Validating...");
			wizardView.setNextButtonEnabled(false);
			new Thread() {
				public void run() {
					final String errorMessage = currentPanel.getErrorMessageBeforeLeave();
					if (errorMessage == null) {
						wizardView.setErrorMessage(null);
						moveToNextPage();
					} else {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								wizardView.setErrorMessage(errorMessage);
								wizardView.setNextButtonEnabled(true);
							}
						});
						return;
					}
				}
			}.start();
		} else {
			moveToNextPage();
		}
	}
	
	private void moveToNextPage() {
		BaseWizardPanel nextPanel = wizardPanels.get(++current);
		setCurrentPanel(nextPanel);
		setButtonsState();
	}
	
	private void setButtonsState() {
		wizardView.setBackButtonEnabled(!isFirst());
		wizardView.setNextButtonEnabled(!isLast() && wizardPanels.get(current).getErrorMessage() == null);
		wizardView.setFinishVisible(isLast());
		wizardView.setCancelVisible(!isLast());
	}
	
	public boolean isFirst() {
		return current == 0;
	}
	
	public boolean isLast() {
		return current == (wizardPanels.size() - 1);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		
		if (current >= 0 && current < wizardPanels.size()) {
			String errorMessage = wizardPanels.get(current).getErrorMessage();
			wizardView.setNextButtonEnabled(errorMessage == null);
			wizardView.setErrorMessage(errorMessage);
		}
	}
}
