/**
 *
 */
package org.mesh4j.ektoo.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.controller.EktooUIController;
import org.mesh4j.ektoo.tasks.SynchronizeTask;
import org.mesh4j.ektoo.ui.image.ImageManager;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.sync.ui.MeshCompactUI;
import org.mesh4j.sync.ui.tasks.CancelSyncTask;
import org.mesh4j.sync.ui.translator.MeshCompactUITranslator;

/**
 * @author Bhuiyan Mohammad Iklash
 * 
 */
public class EktooUI extends JFrame {

	private static final long serialVersionUID = -8703829301086394863L;
	private final Log LOGGER = LogFactory.getLog(EktooUI.class);

	// MODEL VARIABLES
	private SyncItemUI sourceItem = null;
	private SyncItemUI targetItem = null;

	private JPanel panel = null;
	private JButton btnSync = null;

	private JLabel labelConsole = null;

	private JPanel panelImage = null;
	
	private JLabel sourceImageLabel = null;
	private JLabel targetImageLabel = null;
	private JLabel directionImageLabel = null;

	
	
	private EktooUIController controller;

	// BUSINESS METHODS
	public EktooUI(EktooUIController controller) {
		super();
		initialize();
		this.controller = controller;
	}

	private void initialize() 
	{
		this.setSize(new Dimension(800, 500));
		this.setContentPane(getJPanel());
    this.setIconImage(ImageManager.getLogoSmall());
    
    this.setTitle(EktooUITranslator.getTitle());
    
    //this.setResizable(false);
	}

	private JPanel getJPanel() 
	{
		if (panel == null) 
		{
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
      
      c.insets = new Insets(0,5,0,5);
      
			//c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 1;
			c.gridwidth = 1;
			panel.add(getSourcePane(), c);
			
			
			//c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy = 1;
			panel.add(getTargetPane(), c);
			// panel.add(getViaPane(), null);
			// panel.add(getTypePane(), null);

		  c.fill = GridBagConstraints.HORIZONTAL;
      c.gridx = 0;
      c.gridy = 2;
      c.gridwidth = 2;
      panel.add(getConsole(), c);
			
      c.fill = GridBagConstraints.CENTER;
      c.gridx = 0;
      c.gridy = 3;
      c.gridwidth = 2;
			panel.add(getBtnSync(), c);

		}
		return panel;
	}

  private JPanel getImagePanel() 
  {
    if (panelImage == null) 
    {
      panelImage = new JPanel();
      panelImage.setBackground(Color.WHITE);
      
      panelImage.setLayout(new GridBagLayout());
      
      GridBagConstraints c = new GridBagConstraints();
      
      c.gridx = 0;
      
      panelImage.add(getSourceImageLabel(), c);

      c.fill = GridBagConstraints.CENTER;
      c.gridx = 1;
      panelImage.add(getDirectionImageLabel(), c);

      c.fill = GridBagConstraints.WEST;
      c.gridx = 2;
      panelImage.add(getTargetImageLabel(), c);
    }

    return panelImage;
  }
  
  private JLabel getSourceImageLabel()
  {
    if ( sourceImageLabel == null)
    {
      sourceImageLabel = new JLabel(ImageManager.getUndefinedSourceIcon());
    }
    return sourceImageLabel;
  }
  
  private void setSourceIcon(Icon icon)
  {
    setIcon(sourceImageLabel, icon);
  }
  
  private void setTargetIcon(Icon icon)
  {
    setIcon(targetImageLabel, icon);
  }

  private void setIcon(JLabel label, Icon icon)
  {
   if (label != null || icon != null)
     label.setIcon(icon);
  }
  private JLabel getTargetImageLabel()
  {
    if ( targetImageLabel == null)
    {
      targetImageLabel = new JLabel(ImageManager.getUndefinedSourceIcon());
    }
    return targetImageLabel;
  }

  private JLabel getDirectionImageLabel()
  {
    if ( directionImageLabel == null)
    {
      directionImageLabel = new JLabel(ImageManager.getSyncModeIcon(true, true));
    }
    return directionImageLabel;
  }  
  private JLabel getConsole() 
	{
		if (labelConsole == null) 
		{
			labelConsole = new JLabel();
			labelConsole.setPreferredSize(new Dimension(600,24)); 
			labelConsole.setBorder(BorderFactory.createLineBorder(Color.gray));
		}

		return labelConsole;
	}

	private JPanel getSourcePane() 
	{
		if (getSourceItem() == null) 
		{
			setSourceItem(new SyncItemUI(EktooUITranslator
					.getSourceSyncItemSelectorTitle()));
			getSourceItem().setPreferredSize(new Dimension(350, 190));
			getSourceItem().getListType().addItemListener(new ItemListener() 
			{
        public void itemStateChanged(ItemEvent e) 
        {
            setSourceIcon( ImageManager.getSourceImage((String) e.getItem(), false));
        }
      });
		}
		return getSourceItem();
	}

	private JPanel getTargetPane() 
	{
		if (getTargetItem() == null) 
		{
			setTargetItem(new SyncItemUI(EktooUITranslator
					.getTargetSyncItemSelectorTitle()));
			getTargetItem().setPreferredSize(new Dimension(350, 190));
			getTargetItem().getListType().addItemListener(new ItemListener() 
      {
        public void itemStateChanged(ItemEvent e) 
        {
            setTargetIcon( ImageManager.getSourceImage((String) e.getItem(), false));
        }
      });
			
		}

		return getTargetItem();
	}

	private JButton getBtnSync() 
	{
		if (btnSync == null) 
		{
			btnSync = new JButton();
			btnSync.setBounds(new Rectangle(315, 427, 127, 28));
			btnSync.setText(EktooUITranslator.getSyncLabel());
			btnSync.setToolTipText(EktooUITranslator.getSyncToolTip());
	    btnSync.setFont(new Font("Arial", Font.PLAIN, 16));
			
			
			
			btnSync.addActionListener(new ActionListener() 
			{
				public void actionPerformed(ActionEvent e) 
				{
					log("actionPerformed()");
					setConsole("");
					SwingWorker<Boolean, Void> task = new SynchronizeTask(
							EktooUI.this);
					task.execute();
					log("Calling Sync...");
				}
			});
			
		}
		return btnSync;
	}


	private void log(String msg) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(msg);
		}
	}

	public void setController(EktooUIController controller) {
		this.controller = controller;
	}

	public EktooUIController getController() {
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

	public void setConsole(String msg) 
	{
		labelConsole.setText( msg);
	}
}
