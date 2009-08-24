package org.mesh4j.ektoo.ui.settings;

import java.awt.Cursor;
import java.util.Set;

import javax.swing.SwingWorker;

import org.mesh4j.ektoo.controller.AbstractUIController;
import org.mesh4j.ektoo.controller.CloudUIController;
import org.mesh4j.ektoo.controller.FeedUIController;
import org.mesh4j.ektoo.controller.FolderUIController;
import org.mesh4j.ektoo.controller.GSSheetUIController;
import org.mesh4j.ektoo.controller.KmlUIController;
import org.mesh4j.ektoo.controller.MsAccessUIController;
import org.mesh4j.ektoo.controller.MsExcelUIController;
import org.mesh4j.ektoo.controller.MySQLUIController;
import org.mesh4j.ektoo.controller.ZipFeedUIController;
import org.mesh4j.ektoo.model.FeedModel;
import org.mesh4j.ektoo.ui.EktooFrame;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;

public class SettingsNotificationTask extends SwingWorker<Void, Void>{

	private SettingsController settingsController = null;
	private EktooFrame parent = null;
	
	public SettingsNotificationTask(EktooFrame parent,SettingsController settingsController){
		this.settingsController = settingsController;
		this.parent = parent;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		Set<AbstractUIController> sourceControllers = this.parent.getSourceItem().getAllController();
		Set<AbstractUIController> targetControllers = this.parent.getTargetItem().getAllController();
		
		notifyUIControllers(sourceControllers, true);
		notifyUIControllers(targetControllers, false);
		return null;
	}

	@Override
	public void done(){
		parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	
	private void notifyUIControllers(Set<AbstractUIController> listOfAll,boolean isSource ){
		for(AbstractUIController controller : listOfAll){
			System.out.println(controller.getClass());
			updateInController(controller,isSource);
		}
	}
	
	private void updateInController(AbstractUIController controller,boolean isSource){
		
		if( controller instanceof CloudUIController ){
			updateCloud((CloudUIController)controller);
			
		} else if ( controller instanceof MsAccessUIController ){
			updateMsAccess((MsAccessUIController)controller,isSource);
		
		} else if ( controller instanceof GSSheetUIController ){
			updateGoogleSpreadsheet((GSSheetUIController)controller);
			
		} else if ( controller instanceof MsExcelUIController ){
			updateMsExcel((MsExcelUIController)controller,isSource);
			
		} else if ( controller instanceof MySQLUIController ){
			updateMysql((MySQLUIController)controller);
			
		} else if ( controller instanceof KmlUIController ){
			updateKml((KmlUIController)controller,isSource);
			
		}  else if ( controller instanceof FolderUIController ){
			updateFolder((FolderUIController)controller,isSource);
			
		} else if ( controller instanceof ZipFeedUIController ){
			updateZip((ZipFeedUIController)controller);
			
		} else if ( controller instanceof FeedUIController ){
			FeedUIController feedController = (FeedUIController)controller;
			ISyndicationFormat syndicationFormat = ((FeedModel)feedController.getModel()).
															getSyndicationFormat();
			if( syndicationFormat instanceof RssSyndicationFormat){
				updateRss((FeedUIController)controller,isSource);
			} else if( syndicationFormat instanceof AtomSyndicationFormat){
				updateAtom((FeedUIController)controller,isSource);
			}
		}
	}
	
	private void updateCloud(CloudUIController controller){
		CloudSettingsModel settingsModel = settingsController.getModel(CloudSettingsModel.class);
		controller.changeDatasetName(settingsModel.getDatasetName());
		controller.changeMeshName(settingsModel.getMeshName());
		controller.changeSyncServerUri(settingsModel.getSyncServerRootUri());
	 }
	

	private void updateGoogleSpreadsheet(GSSheetUIController controller){
		
		GSSSettingsModel settingsModel = settingsController.getModel(GSSSettingsModel.class);
		
		controller.changeUserName(settingsModel.getGUserName());
		controller.changeUserPassword(settingsModel.getGPassword());
	}
	
	
	private void updateMysql(MySQLUIController controller){
		
		MySqlSettingsModel settingsModel = settingsController.getModel(MySqlSettingsModel.class);
		controller.changeUserName(settingsModel.getUserName());
		controller.changeUserPassword(settingsModel.getUserPassword());
		controller.changeHostName(settingsModel.getHostName());
		controller.changePortNo(settingsModel.getPortNo());
		controller.changeDatabaseName(settingsModel.getDatabaseName());
	}
	
	private void updateKml(KmlUIController controller,boolean isSource){
		
		GeneralSettingsModel settingsModel = (GeneralSettingsModel)settingsController.getModel(GeneralSettingsModel.class);
		String value = "";
		if(isSource){
			value = settingsModel.getPathSourceKml();
		} else {
			value = settingsModel.getPathTargetKml();
		}
		controller.changeFileName(value);
	}
	
	private void updateRss(FeedUIController controller,boolean isSource){
		
		GeneralSettingsModel settingsModel = (GeneralSettingsModel)settingsController.getModel(GeneralSettingsModel.class);
		String value = "";
		if(isSource){
			value = settingsModel.getPathSourceRss();
		} else {
			value = settingsModel.getPathTargetRss();
		}
		controller.changeFileName(value);
	}
	
	private void updateAtom(FeedUIController controller,boolean isSource){
		
		GeneralSettingsModel settingsModel = settingsController.getModel(GeneralSettingsModel.class);
		String value = "";
		if(isSource){
			value = settingsModel.getPathSourceAtom();
		} else {
			value = settingsModel.getPathTargetAtom();
		}
		controller.changeFileName(value);
	}
	
	private void updateFolder(FolderUIController controller,boolean isSource){
		
		GeneralSettingsModel settingsModel = settingsController.getModel(GeneralSettingsModel.class);
		String value = "";
		if(isSource){
			value = settingsModel.getPathSourceFolder();
		} else {
			value = settingsModel.getPathTargetFolder();
		}
		controller.changeFileName(value);
	}
	
	private void updateMsExcel(MsExcelUIController controller,boolean isSource){
		GeneralSettingsModel settingsModel = settingsController.getModel(GeneralSettingsModel.class);
		String value = "";
		if(isSource){
			value = settingsModel.getPathSourceExcel();
		} else {
			value = settingsModel.getPathTargetExcel();
		}
		controller.changeWorkbookName(value);
	}
	
	private void updateMsAccess(MsAccessUIController controller,boolean isSource){
		
		GeneralSettingsModel settingsModel = settingsController.getModel(GeneralSettingsModel.class);
		String value = "";
		if(isSource){
			value = settingsModel.getPathSourceAccess();
		} else {
			value = settingsModel.getPathTargetAccess();
		}
		controller.changeDatabaseName(value);
	}
	
	private void updateZip(ZipFeedUIController controller){
		GeneralSettingsModel settingsModel = settingsController.getModel(GeneralSettingsModel.class);
		controller.changeFileName(settingsModel.getPathSourceZip());
	}
}
