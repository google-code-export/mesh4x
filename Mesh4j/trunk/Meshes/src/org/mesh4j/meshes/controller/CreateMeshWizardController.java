package org.mesh4j.meshes.controller;

import org.mesh4j.meshes.model.DataSet;
import org.mesh4j.meshes.model.DataSetType;
import org.mesh4j.meshes.model.DataSource;
import org.mesh4j.meshes.model.GSSheetDataSource;
import org.mesh4j.meshes.model.Mesh;
import org.mesh4j.meshes.model.MsAccessDataSource;
import org.mesh4j.meshes.model.MsExcelDataSource;
import org.mesh4j.meshes.model.Schedule;
import org.mesh4j.meshes.model.SchedulingOption;
import org.mesh4j.meshes.model.SyncMode;
import org.mesh4j.meshes.ui.wizard.BaseWizardPanel;
import org.mesh4j.meshes.ui.wizard.CreateMeshStepFiveView;
import org.mesh4j.meshes.ui.wizard.CreateMeshStepFourView;
import org.mesh4j.meshes.ui.wizard.CreateMeshStepOneView;
import org.mesh4j.meshes.ui.wizard.CreateMeshStepSevenView;
import org.mesh4j.meshes.ui.wizard.CreateMeshStepSixView;
import org.mesh4j.meshes.ui.wizard.CreateMeshStepThreeView;
import org.mesh4j.meshes.ui.wizard.CreateMeshStepTwoView;
import org.mesh4j.meshes.ui.wizard.WizardView;

public class CreateMeshWizardController extends WizardController {
	
	// MODEL
	private Mesh mesh;
	private DataSet dataSet;
	private DataSource dataSource;
	private Schedule schedule;
	
	// VIEWS
	private CreateMeshStepOneView stepOne;
	private CreateMeshStepTwoView stepTwo;
	private CreateMeshStepThreeView stepThree;
	private CreateMeshStepFourView stepFour;
	private CreateMeshStepFiveView stepFive;
	private CreateMeshStepSixView stepSix;
	private CreateMeshStepSevenView stepSeven;
	private int current;
	
	public CreateMeshWizardController(WizardView wizardView) {
		super(wizardView);
		
		// Create models
		this.mesh = new Mesh();
		this.dataSet = new DataSet();
		this.schedule = new Schedule();
		
		addModel(mesh);
		addModel(dataSet);
		addModel(schedule);
		
		// Create views
		stepOne = new CreateMeshStepOneView(this);
		stepTwo = new CreateMeshStepTwoView(this);
		stepThree = new CreateMeshStepThreeView(this);
		stepFour = new CreateMeshStepFourView(this);
		stepFive = new CreateMeshStepFiveView(this);
		stepSix = new CreateMeshStepSixView(this);
		stepSeven = new CreateMeshStepSevenView(this);
		
		current = 0;
		
		registerWizardPanel(stepOne);
		registerWizardPanel(stepTwo);
		registerWizardPanel(stepThree);
		registerWizardPanel(stepFour);
		registerWizardPanel(stepFive);
		registerWizardPanel(stepSix);
		registerWizardPanel(stepSeven);
		
		setCurrentPanel(stepOne);
	}
	
	public void changeMeshName(String name) {
		mesh.setName(name);
	}
	
	public void changeMeshDescription(String description) {
		mesh.setDescription(description);
	}
	
	public void changeMeshPassword(String password) {
		mesh.setPassword(password);
	}

	public void setTableDataSetType() {
		changeDataSetType(DataSetType.TABLE);
	}
	
	public void setMapDataSetType() {
		changeDataSetType(DataSetType.MAP);
	}
	
	public void setFilesDataSetType() {
		changeDataSetType(DataSetType.FILES);
	}
	
	public void changeDataSetName(String name) {
		dataSet.setName(name);
	}
	
	public void changeDataSetDescription(String description) {
		dataSet.setDescription(description);
	}
	
	private void changeDataSetType(DataSetType type) {
		dataSet.setType(type);
	}
	
	public void changeSchedulingOption(SchedulingOption schedulingOption) {
		schedule.setSchedulingOption(schedulingOption);
	}
	
	public void changeSyncMode(SyncMode syncMode) {
		schedule.setSyncMode(syncMode);
	}
	
	public void setMsAccessDataSource() {
		clearDataSource();
		dataSource = new MsAccessDataSource();
		stepFive.setCurrentConfig(CreateMeshStepFiveView.MS_ACCESS_PANEL);
	}
	
	public void setMsExcelDataSource() {
		clearDataSource();
		dataSource = new MsExcelDataSource();
		stepFive.setCurrentConfig(CreateMeshStepFiveView.MS_EXCEL_PANEL);
	}
	
	public void setGSSheetDataSource() {
		clearDataSource();
		dataSource = new GSSheetDataSource();
		stepFive.setCurrentConfig(CreateMeshStepFiveView.GOOGLE_SPREADSHEET_PANEL);
	}
	
	private void clearDataSource() {
		if (dataSource != null) {
			removeModel(dataSource);
		}
	}
	
	public void changeMsExcelFileName(String fileName) {
		MsExcelDataSource excelDS = (MsExcelDataSource) dataSource;
		excelDS.setFileName(fileName);
	}
	
	public void changeMsExcelWorksheetName(String worksheetName) {
		MsExcelDataSource excelDS = (MsExcelDataSource) dataSource;
		excelDS.setWorksheetName(worksheetName);
	}
	
	public void changeMsExcelUniqueColumnName(String uniqueColumnName) {
		MsExcelDataSource excelDS = (MsExcelDataSource) dataSource;
		excelDS.setUniqueColumnName(uniqueColumnName);
	}
	
	public void changeMsAccessFileName(String fileName) {
		MsAccessDataSource accessDS = (MsAccessDataSource) dataSource;
		accessDS.setFileName(fileName);
	}
	
	public void changeMsAccessTableName(String tableName) {
		MsAccessDataSource accessDS = (MsAccessDataSource) dataSource;
		accessDS.setTableName(tableName);
	}
	
	public void changeGSSheetUserName(String userName) {
		GSSheetDataSource gssheetDS = (GSSheetDataSource) dataSource;
		gssheetDS.setUserName(userName);
	}
	
	public void changeGSSheetPassword(String password) {
		GSSheetDataSource gssheetDS = (GSSheetDataSource) dataSource;
		gssheetDS.setPassword(password);
	}
	
	public void changeGSSheetSpreadsheetName(String spreadsheetName) {
		GSSheetDataSource gssheetDS = (GSSheetDataSource) dataSource;
		gssheetDS.setSpreadsheetName(spreadsheetName);
	}
	
	public void changeGSSheetWorksheetName(String worksheetName) {
		GSSheetDataSource gssheetDS = (GSSheetDataSource) dataSource;
		gssheetDS.setWorksheetName(worksheetName);
	}
	
	public void changeGSSheetUniqueColumnName(String uniqueColumnName) {
		GSSheetDataSource gssheetDS = (GSSheetDataSource) dataSource;
		gssheetDS.setPassword(uniqueColumnName);
	}

	@Override
	public void backButtonPressed() {
		if (current > 0) {
			BaseWizardPanel backPanel = wizardPanels.get(--current);
			setCurrentPanel(backPanel);
		}
	}

	@Override
	public void nextButtonPressed() {
		if (current < wizardPanels.size() - 1) {
			BaseWizardPanel nextPanel = wizardPanels.get(++current);
			setCurrentPanel(nextPanel);
		}
	}
}
