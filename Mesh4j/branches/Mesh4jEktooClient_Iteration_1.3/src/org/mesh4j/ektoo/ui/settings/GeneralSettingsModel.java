package org.mesh4j.ektoo.ui.settings;

import org.mesh4j.ektoo.model.AbstractModel;

public class GeneralSettingsModel extends AbstractModel{
	 
	private String language = "";
	private String pathSource = "";
	private String pathTarget = "";

	private String pathSourceExcel = "";
	private String pathTargetExcel = "";

	private String pathSourceAccess = "";
	private String pathTargetAccess = "";
	
	private String pathSourceKml = "";
	private String pathTargetKml = "";
	
	private String pathSourceRss = "";
	private String pathTargetRss = "";
	
	private String pathSourceAtom = "";
	private String pathTargetAtom = "";
	
	private String pathSourceFolder = "";
	private String pathTargetFolder = "";
	
	private String pathSourceZip = "";
	
	private Boolean createAsDefaultProp = false;
	
	
	public GeneralSettingsModel(){
	}
	
	public Boolean isCreateAsDefaultProp() {
		return createAsDefaultProp;
	}

	public void setCreateAsDefaultProp(Boolean createAsDefaultProp) {
		firePropertyChange(SettingsController.CREATE_PROP_AS_DEFAULT, this.createAsDefaultProp, createAsDefaultProp);
		this.createAsDefaultProp = createAsDefaultProp;
	}

	
	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		firePropertyChange(SettingsController.LANGUAGE, this.language, language);
		this.language = language;
	}

	public String getPathSource() {
		return pathSource;
	}

	public void setPathSource(String pathSource) {
		firePropertyChange(SettingsController.PATH_SOURCE, this.pathSource, pathSource);
		this.pathSource = pathSource;
	}

	public String getPathTarget() {
		return pathTarget;
	}

	public void setPathTarget(String pathTarget) {
		firePropertyChange(SettingsController.PATH_TARGET, this.pathTarget, pathTarget);
		this.pathTarget = pathTarget;
	}

	public String getPathSourceExcel() {
		return pathSourceExcel;
	}

	public void setPathSourceExcel(String pathSourceExcel) {
		firePropertyChange(SettingsController.PATH_SOURCE_EXCEL, this.pathSourceExcel, pathSourceExcel);
		this.pathSourceExcel = pathSourceExcel;
	}

	public String getPathTargetExcel() {
		return pathTargetExcel;
	}

	public void setPathTargetExcel(String pathTargetExcel) {
		firePropertyChange(SettingsController.PATH_TARGET_EXCEL, this.pathTargetExcel, pathTargetExcel);
		this.pathTargetExcel = pathTargetExcel;
	}

	public String getPathSourceAccess() {
		return pathSourceAccess;
	}

	public void setPathSourceAccess(String pathSourceAccess) {
		firePropertyChange(SettingsController.PATH_SOURCE_ACCESS, this.pathSourceAccess, pathSourceAccess);
		this.pathSourceAccess = pathSourceAccess;
	}

	public String getPathTargetAccess() {
		return pathTargetAccess;
	}

	public void setPathTargetAccess(String pathTargetAccess) {
		firePropertyChange(SettingsController.PATH_TARGET_ACCESS, this.pathTargetAccess, pathTargetAccess);
		this.pathTargetAccess = pathTargetAccess;
	}

	public String getPathSourceKml() {
		return pathSourceKml;
	}

	public void setPathSourceKml(String pathSourceKml) {
		firePropertyChange(SettingsController.PATH_SOURCE_KML, this.pathSourceKml, pathSourceKml);
		this.pathSourceKml = pathSourceKml;
	}

	public String getPathTargetKml() {
		return pathTargetKml;
	}

	public void setPathTargetKml(String pathTargetKml) {
		firePropertyChange(SettingsController.PATH_TARGET_KML, this.pathTargetKml, pathTargetKml);
		this.pathTargetKml = pathTargetKml;
	}

	public String getPathSourceRss() {
		return pathSourceRss;
	}

	public void setPathSourceRss(String pathSourceRss) {
		firePropertyChange(SettingsController.PATH_SOURCE_RSS, this.pathSourceRss, pathSourceRss);
		this.pathSourceRss = pathSourceRss;
	}

	public String getPathTargetRss() {
		return pathTargetRss;
	}

	public void setPathTargetRss(String pathTargetRss) {
		firePropertyChange(SettingsController.PATH_TARGET_RSS, this.pathTargetRss, pathTargetRss);
		this.pathTargetRss = pathTargetRss;
	}

	public String getPathSourceAtom() {
		return pathSourceAtom;
	}

	public void setPathSourceAtom(String pathSourceAtom) {
		firePropertyChange(SettingsController.PATH_SOURCE_ATOM, this.pathSourceAtom, pathSourceAtom);
		this.pathSourceAtom = pathSourceAtom;
	}

	public String getPathTargetAtom() {
		return pathTargetAtom;
	}

	public void setPathTargetAtom(String pathTargetAtom) {
		firePropertyChange(SettingsController.PATH_TARGET_ATOM, this.pathTargetAtom, pathTargetAtom);
		this.pathTargetAtom = pathTargetAtom;
	}

	public String getPathSourceFolder() {
		return pathSourceFolder;
	}

	public void setPathSourceFolder(String pathSourceFolder) {
		firePropertyChange(SettingsController.PATH_SOURCE_FOLDER, this.pathSourceFolder, pathSourceFolder);
		this.pathSourceFolder = pathSourceFolder;
	}

	public String getPathTargetFolder() {
		return pathTargetFolder;
	}

	public void setPathTargetFolder(String pathTargetFolder) {
		firePropertyChange(SettingsController.PATH_TARGET_FOLDER, this.pathTargetFolder, pathTargetFolder);
		this.pathTargetFolder = pathTargetFolder;
	}

	public String getPathSourceZip() {
		return pathSourceZip;
	}

	public void setPathSourceZip(String pathSourceZip) {
		firePropertyChange(SettingsController.PATH_SOURCE_ZIP, this.pathSourceZip, pathSourceZip);
		this.pathSourceZip = pathSourceZip;
	}

	

	

	


	
}
