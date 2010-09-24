package org.mesh4j.meshes.controller;

import java.awt.Desktop;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.codec.digest.DigestUtils;
import org.mesh4j.meshes.io.ConfigurationManager;
import org.mesh4j.meshes.io.MeshMarshaller;
import org.mesh4j.meshes.model.DataSet;
import org.mesh4j.meshes.model.DataSetType;
import org.mesh4j.meshes.model.EpiInfoDataSource;
import org.mesh4j.meshes.model.HibernateDataSource;
import org.mesh4j.meshes.model.Mesh;
import org.mesh4j.meshes.model.MeshVisitor;
import org.mesh4j.meshes.model.Schedule;
import org.mesh4j.meshes.model.SchedulingOption;
import org.mesh4j.meshes.model.SyncMode;
import org.mesh4j.meshes.server.MeshServer;
import org.mesh4j.meshes.ui.wizard.BaseWizardPanel;
import org.mesh4j.meshes.ui.wizard.DataSourceType;
import org.mesh4j.meshes.ui.wizard.DatabaseEngine;
import org.mesh4j.meshes.ui.wizard.WizardAccountCredentialsStep;
import org.mesh4j.meshes.ui.wizard.WizardChooseDataSourceTypeStep;
import org.mesh4j.meshes.ui.wizard.WizardConfigureDataSourceStep;
import org.mesh4j.meshes.ui.wizard.WizardConfigureDataSourceStep2;
import org.mesh4j.meshes.ui.wizard.WizardConfigureSchedulingStep;
import org.mesh4j.meshes.ui.wizard.WizardConfirmMeshStep;
import org.mesh4j.meshes.ui.wizard.WizardMeshNameStep;
import org.mesh4j.meshes.ui.wizard.WizardView;
import org.mesh4j.sync.adapters.hibernate.msaccess.MsAccessHibernateSyncAdapterFactory;

public class CreateMeshWizardController extends WizardController {
		
	private int current;
	
	public CreateMeshWizardController(WizardView wizardView) {
		super(wizardView);
				
		current = 0;
		BaseWizardPanel firstStep;
		
		registerWizardPanel(firstStep = new WizardAccountCredentialsStep(this));
		registerWizardPanel(new WizardMeshNameStep(this));
		registerWizardPanel(new WizardChooseDataSourceTypeStep(this));
		registerWizardPanel(new WizardConfigureDataSourceStep(this));
		registerWizardPanel(new WizardConfigureDataSourceStep2(this));
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
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(wizardView, ex.getMessage(), "The mesh configuration file could not be saved", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}
	
	private Mesh buildMesh() throws IOException {
		Mesh mesh = new Mesh();
		
		// Basic properties
		mesh.setName(getStringValue("mesh.name"));
		mesh.setDescription(getStringValue("mesh.description"));
		
		DataSourceType dataSourceType = (DataSourceType) getValue("dataSourceType");
		switch(dataSourceType) {
		case EPI_INFO:
			fillEpiInfoMesh(mesh);
			break;
		case DATABASE:
			fillDatabaseMesh(mesh);
			break;
		default:
			throw new IllegalStateException("Unhandled DataSourceType: " + dataSourceType.name());
		}
		
		return mesh;
	}
	
	private void fillEpiInfoMesh(Mesh mesh) throws IOException {
		String epiInfoLocation = getStringValue("datasource.location");
		@SuppressWarnings("unchecked")
		List<String> dataTableNames = (List<String>) getValue("datasource.tableNames");
		Set<String> allTableNames = MsAccessHibernateSyncAdapterFactory.getTableNames(epiInfoLocation);
		List<String> tableNames = new ArrayList<String>();
		for(String tableName : allTableNames) {
			if (dataTableNames.contains(tableName)) {
				tableNames.add(tableName);
				tableNames.add("view" + tableName);
			} else if (tableName.startsWith("code")) {
				tableNames.add(tableName);
			}
		}
		
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
			dataSource.setFileName(epiInfoLocation);
			dataSource.setTableName(tableName);
			dataSet.getDataSources().add(dataSource);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void fillDatabaseMesh(Mesh mesh) {
		DatabaseEngine engine = (DatabaseEngine) getValue("datasource.engine");
		String host = getStringValue("datasource.host");
		String port = getStringValue("datasource.port");
		String user = getStringValue("datasource.user");
		String password = getStringValue("datasource.password");
		String database = getStringValue("datasource.database");
		String url = engine.getConnectionUrl(host, port, database);
		List<String> tableNames = (List<String>) getValue("datasource.tableNames");
		
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
			HibernateDataSource dataSource = new HibernateDataSource();
			dataSource.setDataSet(dataSet);
			dataSource.setConnectionURL(url);
			dataSource.setDialectClass(engine.getDialectClass());
			dataSource.setDriverClass(engine.getDriverClass());
			dataSource.setUser(user);
			dataSource.setPassword(password);
			dataSource.setTableName(tableName);
			dataSet.getDataSources().add(dataSource);
		}
	}
	
	private void saveConfiguration(Mesh mesh) throws Exception {
		mesh.accept(new MeshVisitor() {
			@Override
			public boolean visit(HibernateDataSource ds) {
				// Erase sensitive information from the mesh. Store the
				// database name and a hash of the connection url so that when
				// a user imports it we can ask just once for the data
				try {
					String url = ds.getConnectionURL();
					String database = "";
					int lastIndex = url.lastIndexOf('/');
					if (lastIndex >= 0) {
						database = url.substring(lastIndex + 1);
					}
					url = DigestUtils.md5Hex(url);
					ds.setConnectionURL(database + "|" + url);
					ds.setUser(null);
					ds.setPassword(null);
				} catch (Exception e) {
					
				}
				return true;
			}
		});
		
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
	
	public void setErrorMessage(final String errorMessage) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				wizardView.setErrorMessage(errorMessage);
			}
		});
	}
}
