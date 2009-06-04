/**
 *
 */
package org.mesh4j.ektoo.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.mesh4j.ektoo.controller.EktooController;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.ektoo.tasks.IErrorListener;
import org.mesh4j.ektoo.tasks.ISynchronizeTaskListener;
import org.mesh4j.ektoo.tasks.OpenURLTask;
import org.mesh4j.ektoo.tasks.SynchronizeTask;
import org.mesh4j.ektoo.ui.component.HyperLink;
import org.mesh4j.ektoo.ui.component.statusbar.Statusbar;
import org.mesh4j.ektoo.ui.image.ImageManager;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;

/**
 * @author Bhuiyan Mohammad Iklash
 * 
 */
public class EktooFrame extends JFrame implements IErrorListener,
		ISynchronizeTaskListener {
	private static final long serialVersionUID = -8703829301086394863L;

	// MODEL VARIABLES
	private SyncItemUI sourceItem = null;
	private SyncItemUI targetItem = null;

	private JPanel panel = null;
	private JButton btnSync = null;

	private JPanel panelImage = null;
	private JPanel headerPanel = null;

	private JLabel sourceImageLabel = null;
	private JLabel targetImageLabel = null;
	private JLabel directionImageLabel = null;
	private JLabel syncImageLabel = null;

	private Statusbar statusBar = null;
	private EktooController controller;
	private JCheckBox schemaCreationChkBox = null;
	

	// BUSINESS METHODS
	public EktooFrame(EktooController controller) {
		super();
		initialize();
		this.controller = controller;
	}

	private void initialize() {
		this.setSize(new Dimension(800, 500));
		this.getContentPane().setLayout(new BorderLayout());
		this.add(getHeaderPanel(),BorderLayout.NORTH);
		this.add(getJPanel(),BorderLayout.CENTER);
		this.setIconImage(ImageManager.getLogoSmall());

		this.setTitle(EktooUITranslator.getTitle());
		this.filterCombobox();
		this.setResizable(false);
	}

	private JPanel getHeaderPanel(){
		if(headerPanel == null){
			headerPanel = new JPanel(new BorderLayout(10,10));	
			headerPanel.setBackground(Color.WHITE);
			
			JPanel linkPanel = new JPanel();
			headerPanel.add(linkPanel,BorderLayout.EAST);
			linkPanel.setOpaque(false);
			
//			HyperLink settingsLink = new HyperLink(EktooUITranslator.getSettingsText());
			HyperLink helpLink = new HyperLink(EktooUITranslator.getHelpText());
			HyperLink aboutLink = new HyperLink(EktooUITranslator.getAboutText());
			
//			settingsLink.addMouseListener(new MouseAdapter(){
//				 public void mouseClicked(MouseEvent e) {
//					 loadSettingsUI();
//				 }
//			});
			
			helpLink.addMouseListener(new MouseAdapter(){
				 public void mouseClicked(MouseEvent e) {
					 gotToMesh4xHelpSite();
				 }
			});
			
			aboutLink.addMouseListener(new MouseAdapter(){
				 public void mouseClicked(MouseEvent e) {
					 goToMesh4xEktooHelpSite();
				 }
			});
			
//			linkPanel.add(settingsLink);
			linkPanel.add(helpLink);
			linkPanel.add(aboutLink);
		}
		 
	
		return headerPanel;
	}
	
	//TODO(raju) please user PropertiesProvider class as single tone for the application
	//because its not necessary to load property file every time.
	private void gotToMesh4xHelpSite(){
		OpenURLTask openURLTask = new OpenURLTask(this,this,new PropertiesProvider().getMesh4xEktooURL());
		openURLTask.execute();
	}

	//TODO(raju) please user PropertiesProvider class as single tone for the application
	//because its not necessary to load property file every time.
	private void goToMesh4xEktooHelpSite(){
		OpenURLTask openURLTask = new OpenURLTask(this,this,new PropertiesProvider().getMesh4xURL());
		openURLTask.execute();
	}
	
	private JPanel getJPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setBackground(Color.WHITE);
			GridBagLayout gridBagLayout = new GridBagLayout();

			panel.setLayout(gridBagLayout);
			GridBagConstraints c = new GridBagConstraints();

			c.fill = GridBagConstraints.BOTH;
			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 2;
			c.weightx = .5;
			c.weighty = 1.0;
			panel.add(getImagePanel(), c);

			c.insets = new Insets(0, 5, 0, 5);

			// c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 1;
			c.gridwidth = 1;
			panel.add(getSourcePane(), c);

			// c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy = 1;
			panel.add(getTargetPane(), c);

			
			c.fill = GridBagConstraints.CENTER;
			c.gridx = 0;
			c.gridy = 2;
			c.gridwidth = 2;
			panel.add(getCheckBox(), c);
			
			
			c.fill = GridBagConstraints.CENTER;
			c.gridx = 0;
			c.gridy = 3;
			c.gridwidth = 2;
			panel.add(getBtnSync(), c);

			
			
			c.insets = new Insets(0, 3, -17, 3);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 4;
			c.gridwidth = 2;
			panel.add(getStatusBar(), c);
		}
		return panel;
	}

	private JCheckBox getCheckBox(){
		schemaCreationChkBox = new JCheckBox(EktooUITranslator.getSchemaCreationCheckboxLabel());
		schemaCreationChkBox.setOpaque(false);
		schemaCreationChkBox.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				AbstractButton abstractButton = (AbstractButton) e.getSource();
		        boolean isSelected = abstractButton.getModel().isSelected();
		        if(isSelected){
		        	getTargetItem().updateUiForSchemaCreation(false);
		        	getTargetItem().setCreateSchema(true);
		        }else{
		        	getTargetItem().updateUiForSchemaCreation(true);
		        	getTargetItem().setCreateSchema(false);
		        }
			}
		});

		schemaCreationChkBox.setVisible(false);
		return schemaCreationChkBox;
	}
	
	
	private JPanel getImagePanel() {
		if (panelImage == null) {
			panelImage = new JPanel();
			panelImage.setOpaque(false);
			panelImage.setLayout(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(-15, 17, 0, 17);

			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 3;

			JPanel tempPanel = new JPanel();
			tempPanel.setPreferredSize(new Dimension(428, 47));
			tempPanel.setOpaque(false);
			tempPanel.add(getSyncImageLabel());
			panelImage.add(tempPanel, c);

			c.fill = GridBagConstraints.CENTER;
			c.gridx = 0;
			c.gridy = 1;
			c.gridwidth = 1;

			panelImage.add(getSourceImageLabel(), c);

			c.fill = GridBagConstraints.CENTER;
			c.gridx = 1;
			panelImage.add(getDirectionImageLabel(), c);

			c.fill = GridBagConstraints.CENTER;
			c.gridx = 2;
			panelImage.add(getTargetImageLabel(), c);
		}

		return panelImage;
	}

	private JLabel getSyncImageLabel() {
		if (syncImageLabel == null) {
			syncImageLabel = new JLabel(ImageManager.getSyncProcessIcon());
			syncImageLabel.setPreferredSize(new Dimension(428, 47));
			showSyncImageLabel(false);
		}
		return syncImageLabel;
	}

	public void showSyncImageLabel(boolean bool) {
		syncImageLabel.setVisible(bool);
	}

	private JLabel getSourceImageLabel() {
		if (sourceImageLabel == null) {
			sourceImageLabel = new JLabel(ImageManager.getUndefinedSourceIcon());
		}
		return sourceImageLabel;
	}

	private void setSourceIcon(Icon icon) {
		setIcon(sourceImageLabel, icon);
	}

	private void setTargetIcon(Icon icon) {
		setIcon(targetImageLabel, icon);
	}

	private void setIcon(JLabel label, Icon icon) {
		if (label != null || icon != null)
			label.setIcon(icon);
	}

	private JLabel getTargetImageLabel() {
		if (targetImageLabel == null) {
			targetImageLabel = new JLabel(ImageManager.getUndefinedSourceIcon());
		}
		return targetImageLabel;
	}

	private JLabel getDirectionImageLabel() {
		if (directionImageLabel == null) {
			directionImageLabel = new JLabel(ImageManager.getSyncModeIcon(true,
					true));
		}
		return directionImageLabel;
	}

	private Statusbar getStatusBar() {
		if (statusBar == null) {
			statusBar = new Statusbar(this);
		}

		return statusBar;
	}

	private JPanel getSourcePane() {
		if (getSourceItem() == null) {
			setSourceItem(new SyncItemUI(EktooUITranslator
					.getSourceSyncItemSelectorTitle()));
			getSourceItem().setPreferredSize(new Dimension(350, 190));
			getSourceItem().getListType().addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {
					if (evt.getStateChange() == ItemEvent.SELECTED) {
						setSourceIcon(ImageManager.getSourceImage((String) evt
								.getItem(), false));
						filterCombobox();
						showHideSchemaChkBox();
					}
				}
			});
			setSourceIcon(ImageManager.getSourceImage((String) getSourceItem()
					.getListType().getSelectedItem(), false));
		}
		return getSourceItem();
	}

	private JPanel getTargetPane() {
		if (getTargetItem() == null) {
			setTargetItem(new SyncItemUI(EktooUITranslator
					.getTargetSyncItemSelectorTitle()));
			getTargetItem().setPreferredSize(new Dimension(350, 190));
			getTargetItem().getListType().addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {
					if (evt.getStateChange() == ItemEvent.SELECTED) {
						setTargetIcon(ImageManager.getSourceImage((String) evt
								.getItem(), false));
						showHideSchemaChkBox();
					}
				}
			});
			setTargetIcon(ImageManager.getSourceImage((String) getTargetItem()
					.getListType().getSelectedItem(), false));

		}

		return getTargetItem();
	}

	//TODO(raju) refactor this implementation later.
	private void showHideSchemaChkBox(){
		
		String sourceType = (String)getSourceItem().getListType().getSelectedItem();
		String targetType = (String)getTargetItem().getListType().getSelectedItem();
		if(sourceType.equals(SyncItemUI.MS_EXCEL_PANEL) && 
				targetType.equals(SyncItemUI.MS_EXCEL_PANEL)){
			schemaCreationChkBox.setVisible(true);
		}else if(sourceType.equals(SyncItemUI.MYSQL_PANEL) && 
				targetType.equals(SyncItemUI.MS_EXCEL_PANEL)){
			schemaCreationChkBox.setVisible(true);
		}else{
			schemaCreationChkBox.setVisible(false);
		}
	}
	private JButton getBtnSync() {
		if (btnSync == null) {
			btnSync = new JButton();
			btnSync.setBounds(new Rectangle(315, 427, 127, 28));
			btnSync.setText(EktooUITranslator.getSyncLabel());
			btnSync.setToolTipText(EktooUITranslator.getSyncToolTip());
			btnSync.setFont(new Font("Arial", Font.PLAIN, 16));

			btnSync.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(getSourceItem().verify() && getTargetItem().verify()){
						setStatusbarText("", Statusbar.NORMAL_STATUS);
						showSyncImageLabel(true);
						SwingWorker<String, Void> task = new SynchronizeTask(
								EktooFrame.this, EktooFrame.this);
						task.execute();	
					}
				}
			});

		}
		return btnSync;
	}

	public void setController(EktooController controller) {
		this.controller = controller;
	}

	public EktooController getController() {
		return controller;
	}

	public void setSourceItem(SyncItemUI sourceItem) {
		this.sourceItem = sourceItem;
	}

	public SyncItemUI getSourceItem() {
		return sourceItem;
	}

	public void setTargetItem(SyncItemUI targetItem) {
		this.targetItem = targetItem;
	}

	public SyncItemUI getTargetItem() {
		return targetItem;
	}


  public void setStatusbarText(String msg, int statusStyle) 
  {
    if (statusStyle == Statusbar.NORMAL_STATUS)
    {
      statusBar.setStaus(msg, Statusbar.NORMAL_COLOR, Statusbar.NORMAL_ICON);
    }
    else if (statusStyle == Statusbar.WARNING_STATUS)
    {
      statusBar.setStaus(msg, Statusbar.WARNING_COLOR, Statusbar.WARNING_ICON);
    }
    else if (statusStyle == Statusbar.ERROR_STATUS)
    {
      statusBar.setStaus(msg, Statusbar.ERROR_COLOR, Statusbar.ERROR_ICON);
    } 
    else if (statusStyle == Statusbar.SUCCESS_STATUS)
    {
      statusBar.setStaus(msg, Statusbar.SUCCESS_COLOR, Statusbar.SUCCESS_ICON);
    } 
    else if (statusStyle == Statusbar.PROGRESS_STATUS)
    {
      statusBar.setStaus(msg, Statusbar.PROGRESS_COLOR, Statusbar.PROGRESS_ICON);
    }     
  }

	// TODO (NBL) disables unsupported features from ui
	private void filterCombobox() {
		String item = (String) getSourceItem().getListType().getSelectedItem();
		if (item.equals(SyncItemUI.MS_EXCEL_PANEL)
				|| item.equals(SyncItemUI.RSS_FILE_PANEL)
				|| item.equals(SyncItemUI.ATOM_FILE_PANEL)) {
			getTargetItem().getListType().removeAllItems();
			getTargetItem().getListType().addItem(SyncItemUI.MS_EXCEL_PANEL);
			getTargetItem().getListType().addItem(SyncItemUI.MS_ACCESS_PANEL);
			getTargetItem().getListType().addItem(SyncItemUI.GOOGLE_SPREADSHEET_PANEL);
			getTargetItem().getListType().addItem(SyncItemUI.MYSQL_PANEL);
			getTargetItem().getListType().addItem(SyncItemUI.CLOUD_PANEL);
			getTargetItem().getListType().addItem(SyncItemUI.RSS_FILE_PANEL);
			getTargetItem().getListType().addItem(SyncItemUI.ATOM_FILE_PANEL);
		} else if (item.equals(SyncItemUI.MS_ACCESS_PANEL)) {
			getTargetItem().getListType().removeAllItems();
			getTargetItem().getListType().addItem(SyncItemUI.MS_ACCESS_PANEL);
			getTargetItem().getListType().addItem(SyncItemUI.MS_EXCEL_PANEL);
			getTargetItem().getListType().addItem(SyncItemUI.MYSQL_PANEL);
			getTargetItem().getListType().addItem(SyncItemUI.CLOUD_PANEL);
			getTargetItem().getListType().addItem(SyncItemUI.RSS_FILE_PANEL);
			getTargetItem().getListType().addItem(SyncItemUI.ATOM_FILE_PANEL);
		} else if (item.equals(SyncItemUI.GOOGLE_SPREADSHEET_PANEL)) {
			getTargetItem().getListType().removeAllItems();
			getTargetItem().getListType().addItem(SyncItemUI.MS_EXCEL_PANEL);
			getTargetItem().getListType().addItem(SyncItemUI.MS_ACCESS_PANEL);
			getTargetItem().getListType().addItem(SyncItemUI.GOOGLE_SPREADSHEET_PANEL);
			getTargetItem().getListType().addItem(SyncItemUI.MYSQL_PANEL);
			getTargetItem().getListType().addItem(SyncItemUI.CLOUD_PANEL);
			getTargetItem().getListType().addItem(SyncItemUI.RSS_FILE_PANEL);
			getTargetItem().getListType().addItem(SyncItemUI.ATOM_FILE_PANEL);
		} else if (item.equals(SyncItemUI.MYSQL_PANEL)) {
			getTargetItem().getListType().removeAllItems();
			getTargetItem().getListType().addItem(SyncItemUI.MS_EXCEL_PANEL);
			getTargetItem().getListType().addItem(SyncItemUI.MS_ACCESS_PANEL);
			getTargetItem().getListType().addItem(SyncItemUI.MYSQL_PANEL);
			getTargetItem().getListType().addItem(SyncItemUI.CLOUD_PANEL);
			getTargetItem().getListType().addItem(SyncItemUI.RSS_FILE_PANEL);
			getTargetItem().getListType().addItem(SyncItemUI.ATOM_FILE_PANEL);
		} else if (item.equals(SyncItemUI.CLOUD_PANEL)) {
			getTargetItem().getListType().removeAllItems();
			getTargetItem().getListType().addItem(SyncItemUI.KML_PANEL);
			getTargetItem().getListType().addItem(SyncItemUI.MS_EXCEL_PANEL);
			getTargetItem().getListType().addItem(SyncItemUI.MS_ACCESS_PANEL);
			getTargetItem().getListType().addItem(
					SyncItemUI.GOOGLE_SPREADSHEET_PANEL);
			getTargetItem().getListType().addItem(SyncItemUI.MYSQL_PANEL);
			getTargetItem().getListType().addItem(SyncItemUI.RSS_FILE_PANEL);
			getTargetItem().getListType().addItem(SyncItemUI.ATOM_FILE_PANEL);
		} else if (item.equals(SyncItemUI.KML_PANEL)) {
			getTargetItem().getListType().removeAllItems();
			getTargetItem().getListType().addItem(SyncItemUI.CLOUD_PANEL);
			getTargetItem().getListType().addItem(SyncItemUI.KML_PANEL);
			getTargetItem().getListType().addItem(SyncItemUI.RSS_FILE_PANEL);
			getTargetItem().getListType().addItem(SyncItemUI.ATOM_FILE_PANEL);
		} else if (item.equals(SyncItemUI.FOLDER_PANEL)) {
			getTargetItem().getListType().removeAllItems();
			getTargetItem().getListType().addItem(SyncItemUI.FOLDER_PANEL);
			getTargetItem().getListType().addItem(SyncItemUI.CLOUD_PANEL);
			getTargetItem().getListType().addItem(SyncItemUI.RSS_FILE_PANEL);
			getTargetItem().getListType().addItem(SyncItemUI.ATOM_FILE_PANEL);
		} else {
			getTargetItem().getListType().removeAllItems();
			setTargetIcon(ImageManager.getUndefinedSourceIcon());
			getTargetItem().showInitCard();
		}

	}

	@Override
	public void notifyError(String error) {
		// TODO (Nobel) error messages handling

	}

	@Override
	public void notifySynchronizeTaskConflict(String conflict) {
		setStatusbarText(conflict, Statusbar.ERROR_STATUS);
	}

	@Override
	public void notifySynchronizeTaskError(String error) {
		setStatusbarText(error, Statusbar.ERROR_STATUS);
	}

	@Override
	public void notifySynchronizeTaskSuccess(String success) {
		setStatusbarText(success, Statusbar.SUCCESS_STATUS);
	}
}
