/**
 *
 */
package org.mesh4j.ektoo.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.StringTokenizer;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.mesh4j.ektoo.controller.EktooController;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.ektoo.tasks.IErrorListener;
import org.mesh4j.ektoo.tasks.ISynchronizeTaskListener;
import org.mesh4j.ektoo.tasks.OpenURLTask;
import org.mesh4j.ektoo.tasks.SynchronizeTask;
import org.mesh4j.ektoo.ui.component.HyperLink;
import org.mesh4j.ektoo.ui.component.PopupDialog;
import org.mesh4j.ektoo.ui.component.statusbar.Statusbar;
import org.mesh4j.ektoo.ui.image.ImageManager;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;

public class EktooFrame extends JFrame implements IErrorListener,
		ISynchronizeTaskListener {
	private static final long serialVersionUID = -8703829301086394863L;

	// MODEL VARIABLES
	private SyncItemUI sourceItem = null;
	private SyncItemUI targetItem = null;

	private JPanel panel = null;
	//private JButton btnSync = null;
	private JLabel btnSync = null;

	private JPanel panelImage = null;
	private JPanel headerPanel = null;

	public static boolean multiModeSync = false;
	
	private JPanel panelSyncMode = null;
	private JRadioButton singleModeRadio;
	private JRadioButton multiModeRadio;
	
	private JLabel sourceImageLabel = null;
	private JLabel targetImageLabel = null;
	private JLabel directionImageLabel = null;
	private JLabel syncImageLabel = null;

	private Statusbar statusBar = null;
	private EktooController controller;

	// BUSINESS METHODS
	public EktooFrame(EktooController controller) {
		super();
		initialize();
		this.controller = controller;
	}

	private void initialize() {
		this.setSize(new Dimension(800, 570));
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
		OpenURLTask openURLTask = new OpenURLTask(this,this,new PropertiesProvider().getMesh4xURL());
		openURLTask.execute();
	}

	//TODO(raju) please user PropertiesProvider class as single tone for the application
	//because its not necessary to load property file every time.
	private void goToMesh4xEktooHelpSite(){
		OpenURLTask openURLTask = new OpenURLTask(this,this,new PropertiesProvider().getMesh4xEktooURL());
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
			
			c.fill = GridBagConstraints.CENTER;
			c.gridx = 0;
			c.gridy = 1;
			c.gridwidth = 2;
			panel.add(getSyncModePanel(), c);			
			
			// c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 2;
			c.gridwidth = 1;
			panel.add(getSourcePane(), c);
			
			// c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy = 2;
			panel.add(getTargetPane(), c);
			
			c.fill = GridBagConstraints.CENTER;
			c.gridx = 0;
			c.gridy = 4;
			c.gridwidth = 2;
			panel.add(getBtnSync(), c);		
			
			c.insets = new Insets(0, 3, -17, 3);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 5;
			c.gridwidth = 2;
			panel.add(getStatusBar(), c);
		}
		return panel;
	}
	
	
	private JPanel getImagePanel() {
		if (panelImage == null) {
			panelImage = new JPanel();
			panelImage.setOpaque(false);
			panelImage.setLayout(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(-2, 17, 0, 17);

//			c.gridx = 0;
//			c.gridy = 0;
//			c.gridwidth = 3;
//
//			JPanel tempPanel = new JPanel();
//			tempPanel.setPreferredSize(new Dimension(428, 47));
//			tempPanel.setOpaque(false);
//			tempPanel.add(getSyncImageLabel());
//			panelImage.add(tempPanel, c);

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

	@SuppressWarnings("unused")
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
					.getSourceSyncItemSelectorTitle(), false, SyncItemUI.UI_AS_SOURCE));
			getSourceItem().setPreferredSize(new Dimension(370, 250));
			getSourceItem().getListType().addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {
					if (evt.getStateChange() == ItemEvent.SELECTED) {
						setSourceIcon(ImageManager.getSourceImage((String) evt
								.getItem(), false));
						filterCombobox();
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
					.getTargetSyncItemSelectorTitle(), true, SyncItemUI.UI_AS_TARGET));
			getTargetItem().setPreferredSize(new Dimension(370, 250));
			getTargetItem().getListType().addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {
					if (evt.getStateChange() == ItemEvent.SELECTED) {
						setTargetIcon(ImageManager.getSourceImage((String) evt
								.getItem(), false));
					}
				}
			});
			setTargetIcon(ImageManager.getSourceImage((String) getTargetItem()
					.getListType().getSelectedItem(), false));

		}

		return getTargetItem();
	}
	
/*	public JButton getBtnSync() {
		if (btnSync == null) {
			btnSync = new JButton();
			btnSync.setIcon(ImageManager.getSyncIcon(false));
			
			btnSync.setContentAreaFilled(false);
			btnSync.setBorderPainted(false);
			btnSync.setBorder(new EmptyBorder(0, 0, 0, 0));
			btnSync.setBackground(Color.WHITE);
			btnSync.setText("");
			btnSync.setBounds(new Rectangle(315, 427, 50, 50));			
			//btnSync.setText(EktooUITranslator.getSyncLabel());
			btnSync.setToolTipText(EktooUITranslator.getSyncToolTip());
			//btnSync.setFont(new Font("Arial", Font.PLAIN, 16));

			btnSync.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(getSourceItem().verify() && getTargetItem().verify()){
						setStatusbarText("", Statusbar.NORMAL_STATUS);
						//showSyncImageLabel(true);
						SwingWorker<String, Void> task = new SynchronizeTask(
								EktooFrame.this, EktooFrame.this);
						task.execute();	
					}
				}
			});

		}
		return btnSync;
	}*/

	public JLabel getBtnSync() {
		if (btnSync == null) {
			btnSync = new JLabel();
			btnSync.setIcon(ImageManager.getSyncIcon(false));
			
			btnSync.setBorder(new EmptyBorder(0, 0, 0, 0));
			btnSync.setBackground(Color.WHITE);
			btnSync.setText("");
			btnSync.setBounds(new Rectangle(315, 427, 50, 50));			
			btnSync.setToolTipText(EktooUITranslator.getSyncToolTip());

			btnSync.addMouseListener(new MouseAdapter(){
				@Override
				public void mouseClicked(MouseEvent e) {
					if(getSourceItem().verify() && getTargetItem().verify()){
						setStatusbarText("", Statusbar.NORMAL_STATUS);
						SwingWorker<String, Void> task = new SynchronizeTask(
								EktooFrame.this, EktooFrame.this);
						task.execute();	
					}
				}
			});

		}
		return btnSync;
	}
	
	JPanel getSyncModePanel(){
		if (panelSyncMode == null) {
			panelSyncMode = new JPanel();
			panelSyncMode.setOpaque(false);
			panelSyncMode.setLayout(new GridLayout(1,2));			
			
			panelSyncMode.add(getSingleModeRadio());
			panelSyncMode.add(getMultiModeRadio());
			
			getSingleModeRadio().setSelected(true);
			
			ButtonGroup group = new ButtonGroup();
			group.add(getSingleModeRadio());
			group.add(getMultiModeRadio());
		}
		return panelSyncMode;		
	}

	JRadioButton getSingleModeRadio(){
		if (singleModeRadio == null) {
		    singleModeRadio = new JRadioButton("Single Mode Sync");
		    singleModeRadio.setToolTipText(EktooUITranslator.getTooltipSyncModeSingle());
		    singleModeRadio.addChangeListener(new ChangeListener(){
				@Override
				public void stateChanged(ChangeEvent e) {
				    AbstractButton aButton = (AbstractButton)e.getSource();
			        ButtonModel aModel = aButton.getModel();
			        boolean selected = aModel.isSelected();
			        boolean previousMode = multiModeSync;
			        multiModeSync = selected ? false : true;
			        if(previousMode != multiModeSync){
			        	updateSourceAdapterList();
			        }
				}
		    });
		}
	    return singleModeRadio;
	}
	
	JRadioButton getMultiModeRadio(){
		if (multiModeRadio == null) {
		    multiModeRadio = new JRadioButton("Multi Mode Sync");
		    multiModeRadio.setToolTipText(EktooUITranslator.getTooltipSyncModeMulti());
		    multiModeRadio.addChangeListener(new ChangeListener(){
				@Override
				public void stateChanged(ChangeEvent e) {
				    AbstractButton aButton = (AbstractButton)e.getSource();
			        ButtonModel aModel = aButton.getModel();
			        boolean selected = aModel.isSelected();
			        boolean previousMode = multiModeSync;
			        multiModeSync = selected? true : false;
			        if(previousMode != multiModeSync){
			        	if(getSourceItem() != null){
				        	if(getSourceItem().getCurrentView() instanceof MySQLUI){
				        		MySQLUI mui = (MySQLUI) getSourceItem().getCurrentView();
				        		int i[] = mui.getTableList().getSelectedIndices();
				        		mui.getTableList().setSelectedIndex(i.length == 0 ? 0 : i[0]);
				        		mui.repaint();
				        	} else if(getSourceItem().getCurrentView() instanceof MsAccessUI){
				        		MsAccessUI mui = (MsAccessUI) getSourceItem().getCurrentView();
				        		int i[] = mui.getTableList().getSelectedIndices();
				        		mui.getTableList().setSelectedIndex(i.length == 0 ? 0 : i[0]);
				        		mui.repaint();
				        	}
			        	}
			        	if(getTargetItem() != null){
				        	if(getTargetItem().getCurrentView() instanceof MySQLUI){
				        		MySQLUI mui = (MySQLUI) getTargetItem().getCurrentView();
				        		int i[] = mui.getTableList().getSelectedIndices();
				        		mui.getTableList().setSelectedIndex(i.length == 0 ? 0 : i[0]);
				        		mui.repaint();
				        	} else if(getTargetItem().getCurrentView() instanceof MsAccessUI){
				        		MsAccessUI mui = (MsAccessUI) getTargetItem().getCurrentView();
				        		int i[] = mui.getTableList().getSelectedIndices();
				        		mui.getTableList().setSelectedIndex(i.length == 0 ? 0 : i[0]);
				        		mui.repaint();
				        	}			        		
			        	}
			        	updateSourceAdapterList();
			        }
				    
				}
		    });	    
		} 
	    return multiModeRadio;
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

  
  void updateSourceAdapterList() {

		String sourceItem = (String) getSourceItem().getListType().getSelectedItem();
		String targetItem = (String) getTargetItem().getListType().getSelectedItem();

		if (multiModeSync) { // multi mode
			getSourceItem().getListType().removeAllItems();
			String multiModeSupportedSourceOrTargetType = EktooUITranslator
					.getMultiModeSyncSupportedDataSourceType();
			if (multiModeSupportedSourceOrTargetType != null) {
				StringTokenizer st = new StringTokenizer(
						multiModeSupportedSourceOrTargetType, "|");
				String type = null;
				while (st.hasMoreTokens()) {
					type = st.nextToken();
					if (type != null && type.length() != 0){
						if (type.equals(SyncItemUI.KML_PANEL))
							getSourceItem().getListType().addItem(
									SyncItemUI.KML_PANEL);
						else if (type.equals(SyncItemUI.CLOUD_PANEL))
							getSourceItem().getListType().addItem(
									SyncItemUI.CLOUD_PANEL);
						else if (type.equals(SyncItemUI.MS_ACCESS_PANEL))
							getSourceItem().getListType().addItem(
									SyncItemUI.MS_ACCESS_PANEL);
						else if (type
								.equals(SyncItemUI.GOOGLE_SPREADSHEET_PANEL))
							getSourceItem().getListType().addItem(
									SyncItemUI.GOOGLE_SPREADSHEET_PANEL);
						else if (type.equals(SyncItemUI.MS_EXCEL_PANEL))
							getSourceItem().getListType().addItem(
									SyncItemUI.MS_EXCEL_PANEL);
						else if (type.equals(SyncItemUI.MYSQL_PANEL))
							getSourceItem().getListType().addItem(
									SyncItemUI.MYSQL_PANEL);
						else if (type.equals(SyncItemUI.RSS_FILE_PANEL))
							getSourceItem().getListType().addItem(
									SyncItemUI.RSS_FILE_PANEL);
						else if (type.equals(SyncItemUI.ATOM_FILE_PANEL))
							getSourceItem().getListType().addItem(
									SyncItemUI.ATOM_FILE_PANEL);
						else if (type.equals(SyncItemUI.FOLDER_PANEL))
							getSourceItem().getListType().addItem(
									SyncItemUI.FOLDER_PANEL);					
					}
				}
			}

		} else { // single mode
			getSourceItem().getListType().removeAllItems();
			getSourceItem().getListType().addItem(SyncItemUI.KML_PANEL);
			getSourceItem().getListType().addItem(SyncItemUI.CLOUD_PANEL);
			getSourceItem().getListType().addItem(SyncItemUI.MS_ACCESS_PANEL);
			getSourceItem().getListType().addItem(SyncItemUI.GOOGLE_SPREADSHEET_PANEL);
			getSourceItem().getListType().addItem(SyncItemUI.MS_EXCEL_PANEL);
			getSourceItem().getListType().addItem(SyncItemUI.MYSQL_PANEL);
			getSourceItem().getListType().addItem(SyncItemUI.RSS_FILE_PANEL);
			getSourceItem().getListType().addItem(SyncItemUI.ATOM_FILE_PANEL);
			getSourceItem().getListType().addItem(SyncItemUI.FOLDER_PANEL);
		}

		getSourceItem().getListType().setSelectedItem(sourceItem);
		getTargetItem().getListType().setSelectedItem(targetItem);
	}
  
	// TODO (NBL) disables unsupported features from ui
	private void filterCombobox() {
		
		String item = (String) getSourceItem().getListType().getSelectedItem();
		
		if(multiModeSync){ //multi mode
			
			if (item.equals(SyncItemUI.MS_EXCEL_PANEL)) {
				getTargetItem().getListType().removeAllItems();
				getTargetItem().getListType().addItem(SyncItemUI.MS_ACCESS_PANEL);
//				getTargetItem().getListType().addItem(SyncItemUI.GOOGLE_SPREADSHEET_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.MS_EXCEL_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.MYSQL_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.CLOUD_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.ZIP_FILE_PANEL);
			} else if (item.equals(SyncItemUI.MS_ACCESS_PANEL)) {
				getTargetItem().getListType().removeAllItems();
				getTargetItem().getListType().addItem(SyncItemUI.MS_ACCESS_PANEL);
//				getTargetItem().getListType().addItem(SyncItemUI.GOOGLE_SPREADSHEET_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.MS_EXCEL_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.MYSQL_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.CLOUD_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.ZIP_FILE_PANEL);
			} else if (item.equals(SyncItemUI.GOOGLE_SPREADSHEET_PANEL)) {
				getTargetItem().getListType().removeAllItems();
				getTargetItem().getListType().addItem(SyncItemUI.MS_ACCESS_PANEL);
//				getTargetItem().getListType().addItem(SyncItemUI.GOOGLE_SPREADSHEET_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.MS_EXCEL_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.MYSQL_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.CLOUD_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.ZIP_FILE_PANEL);
			} else if (item.equals(SyncItemUI.MYSQL_PANEL)) {
				getTargetItem().getListType().removeAllItems();
				getTargetItem().getListType().addItem(SyncItemUI.MS_ACCESS_PANEL);
//				getTargetItem().getListType().addItem(SyncItemUI.GOOGLE_SPREADSHEET_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.MS_EXCEL_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.MYSQL_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.CLOUD_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.ZIP_FILE_PANEL);
			}else {
				getTargetItem().getListType().removeAllItems();
				setTargetIcon(ImageManager.getUndefinedSourceIcon());
				getTargetItem().showInitCard();
			}	
			
		}else{ //single mode

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
				getTargetItem().getListType().addItem(SyncItemUI.ZIP_FILE_PANEL);
			} else if (item.equals(SyncItemUI.MS_ACCESS_PANEL)) {
				getTargetItem().getListType().removeAllItems();
				getTargetItem().getListType().addItem(SyncItemUI.MS_ACCESS_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.MS_EXCEL_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.MYSQL_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.CLOUD_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.RSS_FILE_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.ATOM_FILE_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.ZIP_FILE_PANEL);
			} else if (item.equals(SyncItemUI.GOOGLE_SPREADSHEET_PANEL)) {
				getTargetItem().getListType().removeAllItems();
				getTargetItem().getListType().addItem(SyncItemUI.MS_EXCEL_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.MS_ACCESS_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.GOOGLE_SPREADSHEET_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.MYSQL_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.CLOUD_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.RSS_FILE_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.ATOM_FILE_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.ZIP_FILE_PANEL);
			} else if (item.equals(SyncItemUI.MYSQL_PANEL)) {
				getTargetItem().getListType().removeAllItems();
				getTargetItem().getListType().addItem(SyncItemUI.MS_EXCEL_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.MS_ACCESS_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.MYSQL_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.CLOUD_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.RSS_FILE_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.ATOM_FILE_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.ZIP_FILE_PANEL);
			} else if (item.equals(SyncItemUI.CLOUD_PANEL)) {
				getTargetItem().getListType().removeAllItems();
				getTargetItem().getListType().addItem(SyncItemUI.KML_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.MS_EXCEL_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.MS_ACCESS_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.GOOGLE_SPREADSHEET_PANEL);
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
				getTargetItem().getListType().addItem(SyncItemUI.ZIP_FILE_PANEL);
			} else if (item.equals(SyncItemUI.ZIP_FILE_PANEL)) {
				getTargetItem().getListType().removeAllItems();
				getTargetItem().getListType().addItem(SyncItemUI.MS_EXCEL_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.MS_ACCESS_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.GOOGLE_SPREADSHEET_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.MYSQL_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.CLOUD_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.RSS_FILE_PANEL);
				getTargetItem().getListType().addItem(SyncItemUI.ATOM_FILE_PANEL);
			}else {
				getTargetItem().getListType().removeAllItems();
				setTargetIcon(ImageManager.getUndefinedSourceIcon());
				getTargetItem().showInitCard();
			}
		}
	}

	@Override
	public void notifyError(String error) {
		setStatusbarText(error, Statusbar.ERROR_STATUS);

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
	
	public void showViewInPopup(String title,JComponent component){
		PopupDialog dialog = new PopupDialog(this,title);
		dialog.setLayout(new BorderLayout());
		dialog.add(component);
		dialog.setSize(getWidth() - 100, getHeight()/2);
		dialog.setVisible(true);
	}
}
