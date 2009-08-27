package org.mesh4j.ektoo.ui.settings;
import static org.mesh4j.ektoo.ui.settings.prop.AppPropertiesProvider.getProperty;
import static org.mesh4j.translator.MessageProvider.translate;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.ui.component.DocumentModelAdapter;
import org.mesh4j.ektoo.ui.component.messagedialog.MessageDialog;
import org.mesh4j.ektoo.ui.settings.prop.AppProperties;
import org.mesh4j.translator.MessageNames;

public class GeneralSettingsUI extends AbstractSettingsUI{

	private static final long serialVersionUID = -6752780815799361963L;
	private static final Log LOOGER = LogFactory.getLog(GeneralSettingsUI.class);
	
	private JComboBox langComboBox;
	private JFileChooser fileChooser = null;
//	private JTextField pathSourceTextField;
//	private JTextField pathTargetTextField;
	private JTextField pathTargetExcelTextField;
	private JTextField pathSourceAccessTextField;
	private JTextField pathSourceExcelTextField;
	private JTextField pathTargeteAccessTextField;
	private JTextField pathSourceKmlTextField;
	private JTextField pathTargetKmlTextField;
	private JTextField pathSourceRssTextField;
	private JTextField pathTargetRssTextField;
	private JTextField pathSourceAtomTextField;
	private JTextField pathTargetAtomTextField;
	private JTextField pathSourceFolderTextField;
	private JTextField pathTargetFolderTextField;
	private JTextField pathSourceZipTextField;
	


	public GeneralSettingsUI(SettingsController controller) {
		super(controller,translate(MessageNames.TITLE_SETTINGS_GENERAL));
		this.setLayout(new GridBagLayout());
		init();
	}
	
	
	
	private void init(){
		GridBagConstraints c = new GridBagConstraints();
		
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth =3;
		c.insets = new Insets(0, 0, 0, 0);
		this.add(getHeaderPane(), c);
		
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth =2;
		c.insets = new Insets(15, 20, 0, 10);
		this.add(getLanguageComboBox(), c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 4;
		c.weightx = 0.5;
		c.gridwidth =2;
		c.insets = new Insets(5, 20, 0, 10);
		this.add(getMsExcelSourceFileBrowser(), c);
		
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 5;
		c.weightx = 0.5;
		c.gridwidth =2;
		c.insets = new Insets(5, 20, 0, 10);
		this.add(getMsExcelTargetFileBrowser(), c);
		
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 6;
		c.weightx = 0.5;
		c.gridwidth =2;
		c.insets = new Insets(5, 20, 0, 10);
		this.add(getMsAccessSourceFileBrowser(), c);
		

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 7;
		c.weightx = 0.5;
		c.gridwidth =2;
		c.insets = new Insets(5, 20, 0, 10);
		this.add(getMsAccessTargetFileBrowser(), c);

		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 8;
		c.weightx = 0.5;
		c.gridwidth =2;
		c.insets = new Insets(5, 20, 0, 10);
		this.add(getKmlSourceFileBrowser(), c);
		
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 9;
		c.weightx = 0.5;
		c.gridwidth =2;
		c.insets = new Insets(5, 20, 0, 10);
		this.add(getKmlTargetFileBrowser(), c);
		
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 10;
		c.weightx = 0.5;
		c.gridwidth =2;
		c.insets = new Insets(5, 20, 0, 10);
		this.add(getAtomSourceFileBrowser(), c);
		
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 11;
		c.weightx = 0.5;
		c.gridwidth =2;
		c.insets = new Insets(5, 20, 0, 10);
		this.add(getAtomTargetFileBrowser(), c);
		
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 12;
		c.weightx = 0.5;
		c.gridwidth =2;
		c.insets = new Insets(5, 20, 0, 10);
		this.add(getRssSourceFileBrowser(), c);
		
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 13;
		c.weightx = 0.5;
		c.gridwidth =2;
		c.insets = new Insets(5, 20, 0, 10);
		this.add(getRssTargetFileBrowser(), c);
		
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 14;
		c.weightx = 0.5;
		c.gridwidth =2;
		c.insets = new Insets(5, 20, 0, 10);
		this.add(getFolderSourceFileBrowser(), c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 15;
		c.weightx = 0.5;
		c.gridwidth =2;
		c.insets = new Insets(5, 20, 0, 10);
		this.add(getFolderTargetFileBrowser(), c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 16;
		c.weightx = 0.5;
		c.gridwidth =2;
		c.insets = new Insets(5, 20, 0, 10);
		this.add(getZipSourceFileBrowser(), c);
		
		
//		c.fill = GridBagConstraints.HORIZONTAL;
//		c.gridx = 1;
//		c.gridy = 17;
//		c.weightx = 0.5;
//		c.gridwidth =2;
//		c.insets = new Insets(5, 20, 0, 10);
//		this.add(getDefaultCheckBoxPanel(), c);
		
		
		
		c.anchor = GridBagConstraints.PAGE_END; //bottom of space
		c.gridx = 1;
		c.gridy = 18;
		c.weighty = 1;
		c.weightx = 0;
		c.insets = new Insets(0, 10, 0, 10);
		this.add( getButtonPanel(), c);
	}
	
	
	
	
	
	private JPanel getDefaultCheckBoxPanel(){
		JPanel chkBoxPanel = new JPanel(new BorderLayout());
		JLabel label = new JLabel("");
		label.setPreferredSize(new Dimension(150,20));
		chkBoxPanel.add(label,BorderLayout.WEST);
		chkBoxPanel.add(getDefaultCheckBox(),BorderLayout.CENTER);
		return chkBoxPanel;
	}
	
	private JPanel getButtonPanel(){
		JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.add(getDefaultButton(),BorderLayout.EAST);
		return buttonPanel;
	}
	
	
	
	private JPanel getLanguageComboBox(){
		JPanel panel = new JPanel(new BorderLayout());
		
		
		langComboBox = new JComboBox();
		langComboBox.addItem(AppProperties.LANGUAGE_ENGLISH);
		langComboBox.addItem(AppProperties.LANGUAGE_SYSTEM_DEFAULT);
		
		JLabel label = new JLabel(translate(MessageNames.LABEL_LANGUAGE));
		label.setPreferredSize(new Dimension(150,20));
		panel.add(label,BorderLayout.WEST);
		panel.add(langComboBox,BorderLayout.CENTER);
		
		langComboBox.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED){
					getController().modifySettings(SettingsController.LANGUAGE, e.getItem());
					notifyInfoToUser();
				}
			}
		});
		return panel;
	}
	
	
	
	private JPanel getMsExcelTargetFileBrowser(){
		JPanel fileBrowserPanel = new JPanel(new BorderLayout());
		pathTargetExcelTextField = new JTextField();
		pathTargetExcelTextField.getDocument().addDocumentListener(new DocumentModelAdapter(){
			@Override
			public void insertUpdate(DocumentEvent e) {
				getController().modifySettings(SettingsController.PATH_TARGET_EXCEL, 
						pathTargetExcelTextField.getText());
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				getController().modifySettings(SettingsController.PATH_TARGET_EXCEL, 
						pathTargetExcelTextField.getText());
			}
		});
		final JButton fileBrowserButton = new JButton();
		
		JLabel label = new JLabel(translate(MessageNames.LABEL_EXCEL_TARGET));
		label.setPreferredSize(new Dimension(150,20));
		fileBrowserPanel.add(label,BorderLayout.WEST);
		
		fileBrowserPanel.add(pathTargetExcelTextField,BorderLayout.CENTER);
		fileBrowserPanel.add(fileBrowserButton,BorderLayout.EAST);
		
		fileBrowserButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				launchFileBrowser(JFileChooser.FILES_ONLY,fileBrowserButton,
						pathTargetExcelTextField,
						translate(MessageNames.DESC_EXCEL_FILE_CHOOSER),
						"xls","XLS","xlsx","XLSX");
			}
		});
		return fileBrowserPanel;
	}
	
	private JPanel getMsExcelSourceFileBrowser(){
		JPanel fileBrowserPanel = new JPanel(new BorderLayout());
		pathSourceExcelTextField = new JTextField();
		pathSourceExcelTextField.getDocument().addDocumentListener(new DocumentModelAdapter(){
			@Override
			public void insertUpdate(DocumentEvent e) {
				getController().modifySettings(SettingsController.PATH_SOURCE_EXCEL, 
						pathSourceExcelTextField.getText());
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				getController().modifySettings(SettingsController.PATH_SOURCE_EXCEL, 
						pathSourceExcelTextField.getText());
			}
		});
		final JButton fileBrowserButton = new JButton();
		
		JLabel label = new JLabel(translate(MessageNames.LABEL_EXCEL_SOURCE));
		label.setPreferredSize(new Dimension(150,20));
		fileBrowserPanel.add(label,BorderLayout.WEST);
		
		fileBrowserPanel.add(pathSourceExcelTextField,BorderLayout.CENTER);
		fileBrowserPanel.add(fileBrowserButton,BorderLayout.EAST);
		
		fileBrowserButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
					launchFileBrowser(JFileChooser.FILES_ONLY,
							fileBrowserButton,
							pathSourceExcelTextField,
							translate(MessageNames.DESC_EXCEL_FILE_CHOOSER),
							"xls","XLS","xlsx","XLSX");
			}
		});
		return fileBrowserPanel;
	}
	
	private JPanel getMsAccessSourceFileBrowser(){
		JPanel fileBrowserPanel = new JPanel(new BorderLayout());
		pathSourceAccessTextField = new JTextField();
		pathSourceAccessTextField.getDocument().addDocumentListener(new DocumentModelAdapter(){
			@Override
			public void insertUpdate(DocumentEvent e) {
				getController().modifySettings(SettingsController.PATH_SOURCE_ACCESS, 
						pathSourceAccessTextField.getText());
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				getController().modifySettings(SettingsController.PATH_SOURCE_ACCESS, 
						pathSourceAccessTextField.getText());
			}
		});
		final JButton fileBrowserButton = new JButton();
		
		
		JLabel label = new JLabel(translate(MessageNames.LABEL_ACCESS_SOURCE));
		label.setPreferredSize(new Dimension(150,20));
		fileBrowserPanel.add(label,BorderLayout.WEST);
		
		fileBrowserPanel.add(pathSourceAccessTextField,BorderLayout.CENTER);
		fileBrowserPanel.add(fileBrowserButton,BorderLayout.EAST);
		
		fileBrowserButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				launchFileBrowser(JFileChooser.FILES_ONLY,
						fileBrowserButton,
						pathSourceAccessTextField,
						translate(MessageNames.DESC_ACCESS_FILE_CHOOSER),
						"mdb");
			}
		});
		return fileBrowserPanel;
	}
	
	private JPanel getMsAccessTargetFileBrowser(){
		JPanel fileBrowserPanel = new JPanel(new BorderLayout());
		pathTargeteAccessTextField = new JTextField();
		pathTargeteAccessTextField.getDocument().addDocumentListener(new DocumentModelAdapter(){
			@Override
			public void insertUpdate(DocumentEvent e) {
				getController().modifySettings(SettingsController.PATH_TARGET_ACCESS, 
						pathTargeteAccessTextField.getText());
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				getController().modifySettings(SettingsController.PATH_TARGET_ACCESS, 
						pathTargeteAccessTextField.getText());
			}
		});
		final JButton fileBrowserButton = new JButton();
		
		
		JLabel label = new JLabel(translate(MessageNames.LABEL_ACCESS_TARGET));
		label.setPreferredSize(new Dimension(150,20));
		fileBrowserPanel.add(label,BorderLayout.WEST);
		
		fileBrowserPanel.add(pathTargeteAccessTextField,BorderLayout.CENTER);
		fileBrowserPanel.add(fileBrowserButton,BorderLayout.EAST);
		
		fileBrowserButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				launchFileBrowser(JFileChooser.FILES_ONLY,
						fileBrowserButton,
						pathTargeteAccessTextField,
						translate(MessageNames.DESC_ACCESS_FILE_CHOOSER),
						"mdb");
			}
		});
		return fileBrowserPanel;
	}
		
	private JPanel getKmlSourceFileBrowser(){
		JPanel fileBrowserPanel = new JPanel(new BorderLayout());
		pathSourceKmlTextField = new JTextField();
		pathSourceKmlTextField.getDocument().addDocumentListener(new DocumentModelAdapter(){
			@Override
			public void insertUpdate(DocumentEvent e) {
				getController().modifySettings(SettingsController.PATH_SOURCE_KML, 
						pathSourceKmlTextField.getText());
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				getController().modifySettings(SettingsController.PATH_SOURCE_KML, 
						pathSourceKmlTextField.getText());
			}
		});
		final JButton fileBrowserButton = new JButton();
		
		
		JLabel label = new JLabel(translate(MessageNames.LABEL_KML_SOURCE));
		label.setPreferredSize(new Dimension(150,20));
		fileBrowserPanel.add(label,BorderLayout.WEST);
		
		fileBrowserPanel.add(pathSourceKmlTextField,BorderLayout.CENTER);
		fileBrowserPanel.add(fileBrowserButton,BorderLayout.EAST);
		
		fileBrowserButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				launchFileBrowser(JFileChooser.FILES_ONLY,
						fileBrowserButton,
						pathSourceKmlTextField,
						translate(MessageNames.DESC_KML_FILE_CHOOSER),
						"kml", "kmz", "KML", "KMZ");
			}
		});
		return fileBrowserPanel;
	}
		
	private JPanel getKmlTargetFileBrowser(){
		JPanel fileBrowserPanel = new JPanel(new BorderLayout());
		pathTargetKmlTextField = new JTextField();
		pathTargetKmlTextField.getDocument().addDocumentListener(new DocumentModelAdapter(){
			@Override
			public void insertUpdate(DocumentEvent e) {
				getController().modifySettings(SettingsController.PATH_TARGET_KML, 
						pathTargetKmlTextField.getText());
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				getController().modifySettings(SettingsController.PATH_TARGET_KML, 
						pathTargetKmlTextField.getText());
			}
		});
		final JButton fileBrowserButton = new JButton();
		
		
		JLabel label = new JLabel(translate(MessageNames.LABEL_KML_TARGET));
		label.setPreferredSize(new Dimension(150,20));
		fileBrowserPanel.add(label,BorderLayout.WEST);
		
		fileBrowserPanel.add(pathTargetKmlTextField,BorderLayout.CENTER);
		fileBrowserPanel.add(fileBrowserButton,BorderLayout.EAST);
		
		fileBrowserButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				launchFileBrowser(JFileChooser.FILES_ONLY,
						fileBrowserButton,
						pathTargetKmlTextField,
						translate(MessageNames.DESC_KML_FILE_CHOOSER),
						"kml", "kmz", "KML", "KMZ");
			}
		});
		return fileBrowserPanel;
	}
		
	private JPanel getRssSourceFileBrowser(){
		JPanel fileBrowserPanel = new JPanel(new BorderLayout());
		pathSourceRssTextField = new JTextField();
		pathSourceRssTextField.getDocument().addDocumentListener(new DocumentModelAdapter(){
			@Override
			public void insertUpdate(DocumentEvent e) {
				getController().modifySettings(SettingsController.PATH_SOURCE_RSS, 
						pathSourceRssTextField.getText());
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				getController().modifySettings(SettingsController.PATH_SOURCE_RSS, 
						pathSourceRssTextField.getText());
			}
		});
		final JButton fileBrowserButton = new JButton();
		
		
		JLabel label = new JLabel(translate(MessageNames.LABEL_RSS_SOURCE));
		label.setPreferredSize(new Dimension(150,20));
		fileBrowserPanel.add(label,BorderLayout.WEST);
		
		fileBrowserPanel.add(pathSourceRssTextField,BorderLayout.CENTER);
		fileBrowserPanel.add(fileBrowserButton,BorderLayout.EAST);
		
		fileBrowserButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				launchFileBrowser(JFileChooser.FILES_ONLY,
						fileBrowserButton,
						pathSourceRssTextField,
						translate(MessageNames.DESC_RSS_FILE_CHOOSER),
						"xml");
			}
		});
		return fileBrowserPanel;
	}
	
	private JPanel getRssTargetFileBrowser(){
		JPanel fileBrowserPanel = new JPanel(new BorderLayout());
		pathTargetRssTextField = new JTextField();
		pathTargetRssTextField.getDocument().addDocumentListener(new DocumentModelAdapter(){
			@Override
			public void insertUpdate(DocumentEvent e) {
				getController().modifySettings(SettingsController.PATH_TARGET_RSS, 
						pathTargetRssTextField.getText());
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				getController().modifySettings(SettingsController.PATH_TARGET_RSS, 
						pathTargetRssTextField.getText());
			}
		});
		final JButton fileBrowserButton = new JButton();
		
		
		JLabel label = new JLabel(translate(MessageNames.LABEL_RSS_TARGET));
		label.setPreferredSize(new Dimension(150,20));
		fileBrowserPanel.add(label,BorderLayout.WEST);
		
		fileBrowserPanel.add(pathTargetRssTextField,BorderLayout.CENTER);
		fileBrowserPanel.add(fileBrowserButton,BorderLayout.EAST);
		
		fileBrowserButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				launchFileBrowser(JFileChooser.FILES_ONLY,
						fileBrowserButton,
						pathTargetRssTextField,
						translate(MessageNames.DESC_RSS_FILE_CHOOSER),
						"xml");
			}
		});
		return fileBrowserPanel;
	}
	
	private JPanel getAtomSourceFileBrowser(){
		JPanel fileBrowserPanel = new JPanel(new BorderLayout());
		pathSourceAtomTextField = new JTextField();
		pathSourceAtomTextField.getDocument().addDocumentListener(new DocumentModelAdapter(){
			@Override
			public void insertUpdate(DocumentEvent e) {
				getController().modifySettings(SettingsController.PATH_SOURCE_ATOM, 
						pathSourceAtomTextField.getText());
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				getController().modifySettings(SettingsController.PATH_SOURCE_ATOM, 
						pathSourceAtomTextField.getText());
			}
		});
		final JButton fileBrowserButton = new JButton();
		
		
		JLabel label = new JLabel(translate(MessageNames.LABEL_ATOM_SOURCE));
		label.setPreferredSize(new Dimension(150,20));
		fileBrowserPanel.add(label,BorderLayout.WEST);
		
		fileBrowserPanel.add(pathSourceAtomTextField,BorderLayout.CENTER);
		fileBrowserPanel.add(fileBrowserButton,BorderLayout.EAST);
		
		fileBrowserButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				launchFileBrowser(JFileChooser.FILES_ONLY,
						fileBrowserButton,
						pathSourceAtomTextField,
						translate(MessageNames.DESC_ATOM_FILE_CHOOSER),
						"xml");
			}
		});
		return fileBrowserPanel;
	}
	
	private JPanel getAtomTargetFileBrowser(){
		JPanel fileBrowserPanel = new JPanel(new BorderLayout());
		pathTargetAtomTextField = new JTextField();
		pathTargetAtomTextField.getDocument().addDocumentListener(new DocumentModelAdapter(){
			@Override
			public void insertUpdate(DocumentEvent e) {
				getController().modifySettings(SettingsController.PATH_TARGET_ATOM, 
						pathTargetAtomTextField.getText());
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				getController().modifySettings(SettingsController.PATH_TARGET_ATOM, 
						pathTargetAtomTextField.getText());
			}
		});
		final JButton fileBrowserButton = new JButton();
		
		
		JLabel label = new JLabel(translate(MessageNames.LABEL_ATOM_TARGET));
		label.setPreferredSize(new Dimension(150,20));
		fileBrowserPanel.add(label,BorderLayout.WEST);
		
		fileBrowserPanel.add(pathTargetAtomTextField,BorderLayout.CENTER);
		fileBrowserPanel.add(fileBrowserButton,BorderLayout.EAST);
		
		fileBrowserButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				launchFileBrowser(JFileChooser.FILES_ONLY,
						fileBrowserButton,
						pathTargetAtomTextField,
						translate(MessageNames.DESC_ATOM_FILE_CHOOSER),
						"xml");
			}
		});
		return fileBrowserPanel;
	}
	
	private JPanel getFolderSourceFileBrowser(){
		JPanel fileBrowserPanel = new JPanel(new BorderLayout());
		pathSourceFolderTextField = new JTextField();
		pathSourceFolderTextField.getDocument().addDocumentListener(new DocumentModelAdapter(){
			@Override
			public void insertUpdate(DocumentEvent e) {
				getController().modifySettings(SettingsController.PATH_SOURCE_FOLDER, 
						pathSourceFolderTextField.getText());
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				getController().modifySettings(SettingsController.PATH_SOURCE_FOLDER, 
						pathSourceFolderTextField.getText());
			}
		});
		final JButton fileBrowserButton = new JButton();
		
		
		JLabel label = new JLabel(translate(MessageNames.LABEL_FOLDER_SOURCE));
		label.setPreferredSize(new Dimension(150,20));
		fileBrowserPanel.add(label,BorderLayout.WEST);
		
		fileBrowserPanel.add(pathSourceFolderTextField,BorderLayout.CENTER);
		fileBrowserPanel.add(fileBrowserButton,BorderLayout.EAST);
		
		fileBrowserButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				launchFileBrowser(JFileChooser.DIRECTORIES_ONLY,
						fileBrowserButton,
						pathSourceFolderTextField,
						translate(MessageNames.DESC_FOLDER_FILE_CHOOSER));
			}
		});
		return fileBrowserPanel;
	}
	
	private JPanel getFolderTargetFileBrowser(){
		JPanel fileBrowserPanel = new JPanel(new BorderLayout());
		pathTargetFolderTextField = new JTextField();
		pathTargetFolderTextField.getDocument().addDocumentListener(new DocumentModelAdapter(){
			@Override
			public void insertUpdate(DocumentEvent e) {
				getController().modifySettings(SettingsController.PATH_TARGET_FOLDER, 
						pathTargetFolderTextField.getText());
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				getController().modifySettings(SettingsController.PATH_TARGET_FOLDER, 
						pathTargetFolderTextField.getText());
			}
		});
		final JButton fileBrowserButton = new JButton();
		
		
		JLabel label = new JLabel(translate(MessageNames.LABEL_FOLDER_TARGET));
		label.setPreferredSize(new Dimension(150,20));
		fileBrowserPanel.add(label,BorderLayout.WEST);
		
		fileBrowserPanel.add(pathTargetFolderTextField,BorderLayout.CENTER);
		fileBrowserPanel.add(fileBrowserButton,BorderLayout.EAST);
		
		fileBrowserButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				launchFileBrowser(JFileChooser.DIRECTORIES_ONLY,
						fileBrowserButton,
						pathTargetFolderTextField,
						translate(MessageNames.DESC_FOLDER_FILE_CHOOSER)
						);
			}
		});
		return fileBrowserPanel;
	}
	
	private JPanel getZipSourceFileBrowser(){
		JPanel fileBrowserPanel = new JPanel(new BorderLayout());
		pathSourceZipTextField = new JTextField();
		pathSourceZipTextField.getDocument().addDocumentListener(new DocumentModelAdapter(){
			@Override
			public void insertUpdate(DocumentEvent e) {
				getController().modifySettings(SettingsController.PATH_SOURCE_ZIP, 
						pathSourceZipTextField.getText());
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				getController().modifySettings(SettingsController.PATH_SOURCE_ZIP, 
						pathSourceZipTextField.getText());
			}
		});
		final JButton fileBrowserButton = new JButton();
		
		JLabel label = new JLabel(translate(MessageNames.LABEL_ZIP_SOURCE));
		label.setPreferredSize(new Dimension(150,20));
		fileBrowserPanel.add(label,BorderLayout.WEST);
		
		fileBrowserPanel.add(pathSourceZipTextField,BorderLayout.CENTER);
		fileBrowserPanel.add(fileBrowserButton,BorderLayout.EAST);
		
		fileBrowserButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				launchFileBrowser(JFileChooser.FILES_ONLY,
						fileBrowserButton,
						pathSourceZipTextField,
						translate(MessageNames.DESC_ZIP_FILE_CHOOSER),
						"zip");
			}
		});
		return fileBrowserPanel;
	}
	
	
	private void launchFileBrowser(int mode,JComponent component,JTextField textField,String desc,String... extensions){

		//clean up old file filter
		for(FileFilter fileFilter : getFileChooser().getChoosableFileFilters()){
			getFileChooser().removeChoosableFileFilter(fileFilter);	
		}
		
		getFileChooser().setFileSelectionMode(mode);
		getFileChooser().setAcceptAllFileFilterUsed(false);
		if(mode == JFileChooser.FILES_ONLY){
			getFileChooser().setFileFilter(new FileNameExtensionFilter(desc,extensions));
		} 
		
		getFileChooser().setSelectedFile(new File(textField.getText()));
		int returnVal = getFileChooser().showOpenDialog(component);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File selectedFile = getFileChooser().getSelectedFile();
			if (selectedFile != null) {
				try {
					textField.setText(selectedFile.getCanonicalPath());
					textField.setToolTipText(textField.getText());
				} catch (Exception ex) {
					LOOGER.error(ex);
				}
			}
		}
	}
	
	
	
	private JFileChooser getFileChooser() {
		if(fileChooser == null){
			fileChooser = new JFileChooser();
		}
		return fileChooser;
	}
	
	private SettingsController getController(){
		return (SettingsController)this.controller;
	}
	
	private void selectOrAddValueInCombo(String value){
		if(isValueContainsAtCombo(value)){
			langComboBox.setSelectedItem(value);
		} else {
			langComboBox.addItem(value);
			langComboBox.setSelectedItem(value);
		}
	}
	
	private boolean isValueContainsAtCombo(String value){
		int size = langComboBox.getItemCount();
		for(int index =0 ; index<size; index++){
			if(langComboBox.getItemAt(index).equals(value)){
				return true;
			}
		}
		return false;
	}
	private void notifyInfoToUser(){
		if(!getProperty(AppProperties.LANGUAGE).
				equals(langComboBox.getSelectedItem())){
			MessageDialog.showWarningMessage(null, "New language will be effected after " +
			"application  is restarted");	
		}
	}
	
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		String newValueAsString = evt.getNewValue().toString();
		
		if ( evt.getPropertyName().equals( SettingsController.LANGUAGE )){
			if(!langComboBox.getSelectedItem().equals(newValueAsString)){
				selectOrAddValueInCombo(newValueAsString);
			}
		} else if ( evt.getPropertyName().equals( SettingsController.PATH_SOURCE_EXCEL )){
			if(!pathSourceExcelTextField.getText().equals(newValueAsString))
				pathSourceExcelTextField.setText(newValueAsString);
		} else if ( evt.getPropertyName().equals( SettingsController.PATH_TARGET_EXCEL )){
			if(!pathTargetExcelTextField.getText().equals(newValueAsString))
				pathTargetExcelTextField.setText(newValueAsString);
		} else if ( evt.getPropertyName().equals( SettingsController.PATH_SOURCE_ACCESS )){
			if(!pathSourceAccessTextField.getText().equals(newValueAsString))
				pathSourceAccessTextField.setText(newValueAsString);
		} else if ( evt.getPropertyName().equals( SettingsController.PATH_TARGET_ACCESS )){
			if(!pathTargeteAccessTextField.getText().equals(newValueAsString))
				pathTargeteAccessTextField.setText(newValueAsString);
		} else if ( evt.getPropertyName().equals( SettingsController.PATH_SOURCE_KML )){
			if(!pathSourceKmlTextField.getText().equals(newValueAsString))
				pathSourceKmlTextField.setText(newValueAsString);
		} else if ( evt.getPropertyName().equals( SettingsController.PATH_TARGET_KML )){
			if(!pathTargetKmlTextField.getText().equals(newValueAsString))
				pathTargetKmlTextField.setText(newValueAsString);
		} else if ( evt.getPropertyName().equals( SettingsController.PATH_SOURCE_RSS )){
			if(!pathSourceRssTextField.getText().equals(newValueAsString))
				pathSourceRssTextField.setText(newValueAsString);
		} else if ( evt.getPropertyName().equals( SettingsController.PATH_TARGET_RSS )){
			if(!pathTargetRssTextField.getText().equals(newValueAsString))
				pathTargetRssTextField.setText(newValueAsString);
		} else if ( evt.getPropertyName().equals( SettingsController.PATH_SOURCE_ATOM )){
			if(!pathSourceAtomTextField.getText().equals(newValueAsString))
				pathSourceAtomTextField.setText(newValueAsString);
		} else if ( evt.getPropertyName().equals( SettingsController.PATH_TARGET_ATOM )){
			if(!pathTargetAtomTextField.getText().equals(newValueAsString))
				pathTargetAtomTextField.setText(newValueAsString);
		} else if ( evt.getPropertyName().equals( SettingsController.PATH_SOURCE_FOLDER )){
			if(!pathSourceFolderTextField.getText().equals(newValueAsString))
				pathSourceFolderTextField.setText(newValueAsString);
		} else if ( evt.getPropertyName().equals( SettingsController.PATH_TARGET_FOLDER )){
			if(!pathTargetFolderTextField.getText().equals(newValueAsString))
				pathTargetFolderTextField.setText(newValueAsString);
		} else if ( evt.getPropertyName().equals( SettingsController.PATH_SOURCE_ZIP )){
			if(!pathSourceZipTextField.getText().equals(newValueAsString))
				pathSourceZipTextField.setText(newValueAsString);
		} else if ( evt.getPropertyName().equals( SettingsController.LANGUAGE )){
			String langValueAsString = langComboBox.getSelectedItem().toString();
			if(!langValueAsString.equals(newValueAsString))
			langComboBox.setSelectedItem(evt.getNewValue().toString());
		}
		
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void loadDefault() {
		getController().loadDefaultGeneralSettings();
		
	}

}
