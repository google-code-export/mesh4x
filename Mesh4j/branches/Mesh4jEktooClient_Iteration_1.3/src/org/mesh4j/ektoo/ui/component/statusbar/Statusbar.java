package org.mesh4j.ektoo.ui.component.statusbar;

import static org.mesh4j.ektoo.Util.getProperty;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import org.mesh4j.ektoo.tasks.IErrorListener;
import org.mesh4j.ektoo.tasks.OpenURLTask;
import org.mesh4j.ektoo.ui.component.RoundBorder;
import org.mesh4j.ektoo.ui.image.ImageManager;
import org.mesh4j.ektoo.ui.settings.prop.AppProperties;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;

public class Statusbar extends JPanel implements IStatus
{
  private static final long serialVersionUID = -2823544263384204307L;
  
  public static final int NORMAL_STATUS  = 1;
  public static final int WARNING_STATUS = 2;
  public static final int ERROR_STATUS   = 3;
  public static final int SUCCESS_STATUS = 4;
  public static final int PROGRESS_STATUS = 5;
  
  public static final Icon NORMAL_ICON  = ImageManager.getInfoIcon();
  public static final Icon WARNING_ICON = ImageManager.getWarningIcon();
  public static final Icon ERROR_ICON   = ImageManager.getErrorIcon();
  public static final Icon SUCCESS_ICON = ImageManager.getSuccessIcon();
  public static final Icon PROGRESS_ICON = ImageManager.getProgressIcon();
  
  public static final Color NORMAL_COLOR  = Color.BLACK;
  public static final Color WARNING_COLOR = Color.YELLOW;
  public static final Color ERROR_COLOR   = Color.RED;
  public static final Color SUCCESS_COLOR = Color.GREEN;
  public static final Color PROGRESS_COLOR = Color.BLACK;
  
  private JLabel labelStatus = null;

  private JFrame parent = null;
  
  public Statusbar(JFrame parent)
  {
    this.parent = parent;
    createUI();
  }
  
  private void createUI()
  {
    setOpaque(false);
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints c = new GridBagConstraints();
    
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 1.0;
    c.weighty = 1.0;
    add(getConsole(), c);

    c.fill = GridBagConstraints.VERTICAL;
    c.gridx = 1;
    add( getSeperator(), c);
    
    c.gridx = 2;
    add( getPoweredByLabel(), c);
    
    setBorder(new RoundBorder( getBackground() ));
    
  }
  
  private JLabel getConsole() 
  {
    
    if (labelStatus == null) 
    {
      labelStatus = new JLabel();
      labelStatus.setPreferredSize(new Dimension(650, 22));
      labelStatus.setIcon(ImageManager.getInfoIcon());
      labelStatus.setText(EktooUITranslator.getStatusbarMessage());
    }
    return labelStatus;
  }
  
  private JComponent getSeperator(){ 
 	  
	 JSeparator separator = null;
	 JPanel spePanel = new JPanel(new GridLayout(1,2,0,0));
	 spePanel.setOpaque(false);

	 separator = new JSeparator(SwingConstants.VERTICAL);
	 spePanel.add(separator);
	 
	 separator = new JSeparator(SwingConstants.VERTICAL);
	 spePanel.add(separator);
    return spePanel;
  }

  private JLabel getPoweredByLabel() 
  {
    JLabel poweredByLabel = new JLabel(EktooUITranslator.getPoweredByLabel(),
          ImageManager.getTrademarkIcon(), JLabel.RIGHT);
      poweredByLabel.setHorizontalTextPosition(JLabel.LEFT);
      poweredByLabel.setVerticalTextPosition(JLabel.CENTER);
      poweredByLabel.setIconTextGap(0);
      poweredByLabel.setForeground(Color.BLUE);
      poweredByLabel.setToolTipText(EktooUITranslator
          .getPoweredByLabelTooltip());
      poweredByLabel.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          OpenURLTask task = new OpenURLTask(parent,
              (IErrorListener)parent, getProperty(AppProperties.URL_MESH4X));
          task.execute();
        }
      });
    return poweredByLabel;
  }

  @Override
  public void setStaus(String message, Color color, Icon icon)
  {
    labelStatus.setText(message);
    labelStatus.setForeground(color);
    labelStatus.setIcon(icon);
  }
}
