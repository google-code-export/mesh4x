package org.mesh4j.meshes.controller;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;

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
import org.mesh4j.meshes.ui.wizard.WizardChooseDataSourceTypeStep;
import org.mesh4j.meshes.ui.wizard.WizardConfigureDataSourceStep;
import org.mesh4j.meshes.ui.wizard.WizardConfigureSchedulingStep;
import org.mesh4j.meshes.ui.wizard.WizardConfirmMeshStep;
import org.mesh4j.meshes.ui.wizard.WizardMeshNameStep;
import org.mesh4j.meshes.ui.wizard.WizardMeshPasswordStep;
import org.mesh4j.meshes.ui.wizard.WizardView;
import org.mesh4j.sync.adapters.epiinfo.EpiInfoSyncAdapterFactory;

public class CreateMeshWizardController extends WizardController {
		
	private int current;
	
	public CreateMeshWizardController(WizardView wizardView) {
		super(wizardView);
				
		current = 0;
		BaseWizardPanel firstStep;
		
		registerWizardPanel(firstStep = new WizardMeshNameStep(this));
		registerWizardPanel(new WizardMeshPasswordStep(this));
		registerWizardPanel(new WizardChooseDataSourceTypeStep(this));
		registerWizardPanel(new WizardConfigureDataSourceStep(this));
		registerWizardPanel(new WizardConfigureSchedulingStep(this));
		registerWizardPanel(new WizardConfirmMeshStep(this));
		
		setCurrentPanel(firstStep);
		setButtonsState();
	}
							
	public void saveConfiguration(File file) throws IOException {
		MeshMarshaller.toXml(buildMesh(), file); 
	}
	
	public void finish() {
		try {
			Mesh mesh = buildMesh();
			MeshServer.getInstance().createMesh(mesh);
			ConfigurationManager.getInstance().saveMesh(mesh);
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(wizardView, ex.getMessage(), "The mesh configuration file could not be saved", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}
	
	private Mesh buildMesh()
	{
		Mesh mesh = new Mesh();
		
		// Basic properties
		mesh.setName(getStringValue("mesh.name"));
		mesh.setDescription(getStringValue("mesh.description"));
		mesh.setPassword(getStringValue("mesh.password"));
		mesh.setServerFeedUrl("https://mesh.instedd.org/feeds/" + getStringValue("mesh.name") + "/");
		
		List<String> tableNames = EpiInfoSyncAdapterFactory.getTableNames(getStringValue("epiinfo.location"));
		for(String tableName : tableNames) {
			// DataSet
			DataSet dataSet = new DataSet();
			dataSet.setMesh(mesh);
			dataSet.setType(DataSetType.TABLE);
			dataSet.setName(tableName);
			dataSet.setServerFeedUrl(tableName);
			mesh.getDataSets().add(dataSet);
			
			// Schedule
			Schedule schedule = new Schedule();
			schedule.setSchedulingOption((SchedulingOption) getValue("scheduling"));
			schedule.setSyncMode((SyncMode) getValue("syncmode"));
			dataSet.setSchedule(schedule);
			
			// DataSource
			EpiInfoDataSource dataSource = new EpiInfoDataSource();
			dataSource.setFileName(getStringValue("epiinfo.location"));
			dataSource.setTableName(tableName);
			dataSet.getDataSources().add(dataSource);
		}
		
		return mesh;
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
	
	private boolean isFirst() {
		return current == 0;
	}
	
	private boolean isLast() {
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
