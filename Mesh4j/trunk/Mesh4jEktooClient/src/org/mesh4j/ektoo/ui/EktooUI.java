/**
 *
 */
package org.mesh4j.ektoo.ui;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.controller.EktooUIController;
import org.mesh4j.ektoo.tasks.SynchronizeTask;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;


/**
 * @author Bhuiyan Mohammad Iklash
 *
 */
public class EktooUI extends JFrame
{
  // CONSTANTS
  private final static long serialVersionUID = 1L;
  private final Log LOGGER = LogFactory.getLog(EktooUI.class);
  
  // MODEL VARIABLES
  private SyncItemUI sourceItem = null;
  private SyncItemUI targetItem = null;

  private JPanel panel = null;
  private JPanel viaPane = null;
  private JPanel typePane = null;
  private JButton btnSync = null;

  private JRadioButton rbWeb = null;
  private JRadioButton rbSMS = null;
  private JRadioButton rbFile = null;
  private JRadioButton rbSent = null;
  private JRadioButton rbReceive = null;
  private JRadioButton rbSendReceive = null;

  private ButtonGroup btngSyncVia = new ButtonGroup();
  private ButtonGroup btngSyncType = new ButtonGroup();
  private JLabel labelConsole = null;

  private EktooUIController controller;
  
  // BUSINESS METHODS
  public EktooUI() {
    super();
    initialize();
  }

  public EktooUI(EktooUIController controller) {
    super();
    initialize();
    this.controller = controller;
  }

  private void initialize() 
  {
    this.setSize(new Dimension(564, 511));
        this.setContentPane(getJPanel());
  }

  private JPanel getJPanel()
  {
    if (panel == null) {

      panel = new JPanel();
      panel.setLayout(null);
      panel.add(getSourcePane(), null);
      panel.add(getTargetPane(), null);
      panel.add(getViaPane(), null);
      panel.add(getTypePane(), null);
      panel.add(getBtnSync(), null);
      panel.add(getConsole(), null);
    }
    return panel;
  }


  private JLabel getConsole() 
  {
    if (labelConsole == null)
    {
      labelConsole = new JLabel();
      labelConsole.setSize(500, 16);
      labelConsole.setLocation(new Point(10, 400));
      labelConsole.setBackground(Color.red);
      labelConsole.setText("Console....");    
    }
    
    return labelConsole;
  }

  private JPanel getSourcePane()
  {
    if (getSourceItem() == null)
    {
      setSourceItem(new SyncItemUI(EktooUITranslator.getSourceSyncItemSelectorTitle()));
      getSourceItem().setSize(new Dimension(350, 190));
      getSourceItem().setLocation(new Point(10, 10));
    }

    return getSourceItem();
  }


  private JPanel getTargetPane()
  {
    if(getTargetItem() == null)
    {
      setTargetItem(new SyncItemUI(EktooUITranslator.getTargetSyncItemSelectorTitle()));
      getTargetItem().setSize(new Dimension(350, 190));
      getTargetItem().setLocation(new Point(10, 210));
    }

    return getTargetItem();
  }


  private JPanel getViaPane() {
    if (viaPane == null) {
      viaPane = new JPanel();
      viaPane.setBorder(BorderFactory.createTitledBorder( EktooUITranslator.getSyncViaLabel()));
      viaPane.setSize(new Dimension(150, 190));
      viaPane.setLocation(new Point(390, 10));
      viaPane.add(getRbWeb(), null);
      viaPane.add(getRbSMS(), null);
      viaPane.add(getRbFile(), null);


    }
    return viaPane;
  }


  private JPanel getTypePane() {
    if (typePane == null) {
      typePane = new JPanel();
      typePane.setBorder(BorderFactory.createTitledBorder(EktooUITranslator.getSyncTypeLabel()));
      typePane.setSize(new Dimension(150, 190));
      typePane.setLocation(new Point(387, 210));
      typePane.add(getRbSent(), null);
      typePane.add(getRbReceive(), null);
      typePane.add(getRbSendReceive(), null);
    }
    return typePane;
  }


  private JButton getBtnSync() 
  {
    if (btnSync == null) {
      btnSync = new JButton();
      btnSync.setBounds(new Rectangle(315, 427, 127, 28));
      btnSync.setText( EktooUITranslator.getSyncViaLabel());
      btnSync.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) 
        {
          log("actionPerformed()");
          labelConsole.setText("");
          SwingWorker<String, Void> task = new SynchronizeTask(EktooUI.this);
          task.execute();         
          log("Calling Sync...");
        }
      });
    }
    return btnSync;
  }

  private JRadioButton getRbWeb() {
    if (rbWeb == null) {
      rbWeb = new JRadioButton();
      rbWeb.setBounds(new Rectangle(18, 26, 95, 24));
      rbWeb.setText( EktooUITranslator.getSyncViaWebLabel());
      btngSyncVia.add(rbWeb);
    }
    return rbWeb;
  }

  private JRadioButton getRbSMS() 
  {
    if (rbSMS == null) 
    {
      rbSMS = new JRadioButton();
      rbSMS.setBounds(new Rectangle(18, 49, 92, 24));
      rbSMS.setText(EktooUITranslator.getSyncViaSMSLabel());
      btngSyncVia.add(rbSMS);
    }
    return rbSMS;
  }

  private JRadioButton getRbFile() 
  {
    if (rbFile == null) {
      rbFile = new JRadioButton();
      rbFile.setBounds(new Rectangle(19, 77, 107, 24));
      rbFile.setText(EktooUITranslator.getSyncViaFileLabel());
      rbFile.setSelected(true);
      btngSyncVia.add(rbFile);
    }
    return rbFile;
  }


  private JRadioButton getRbSent() 
  {
    if (rbSent == null) 
    {
      rbSent = new JRadioButton();
      rbSent.setBounds(new Rectangle(13, 28, 124, 21));
      rbSent.setText(EktooUITranslator.getSyncTypeSendLabel());
      btngSyncType.add(rbSent);
    }
    return rbSent;
  }

  private JRadioButton getRbReceive() 
  {
    if (rbReceive == null) {
      rbReceive = new JRadioButton();
      rbReceive.setBounds(new Rectangle(13, 53, 124, 21));
      rbReceive.setText(EktooUITranslator.getSyncTypeReceiveLabel());
      btngSyncType.add(rbReceive);
    }
    return rbReceive;
  }


  private JRadioButton getRbSendReceive() 
  {
    if (rbSendReceive == null) {
      rbSendReceive = new JRadioButton();
      rbSendReceive.setBounds(new Rectangle(13, 78, 124, 21));
      rbSendReceive.setText(EktooUITranslator.getSyncTypeSendAndReceiveLabel());
      rbSendReceive.setSelected(true);
      btngSyncType.add(rbSendReceive);
    }
    return rbSendReceive;
  }

  private void log(String msg) {
    if(LOGGER.isDebugEnabled()){
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
    labelConsole.setText(msg);
  }
  
} 
