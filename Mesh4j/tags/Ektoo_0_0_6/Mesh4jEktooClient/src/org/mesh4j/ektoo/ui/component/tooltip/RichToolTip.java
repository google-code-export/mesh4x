package org.mesh4j.ektoo.ui.component.tooltip;

import javax.swing.ImageIcon;
import javax.swing.JToolTip;

public class RichToolTip extends JToolTip
{
  private static final long serialVersionUID = 1570944754416715865L;
  
  public RichToolTip(final ImageIcon icon)
  {
      setUI(new RichToolTipUI(icon));
  }
}
