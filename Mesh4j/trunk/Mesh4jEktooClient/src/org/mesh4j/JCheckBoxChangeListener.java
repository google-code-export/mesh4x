package org.mesh4j;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class JCheckBoxChangeListener {
  public static void main(String args[]) {
    JFrame frame = new JFrame("Iconizing CheckBox");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JCheckBox aCheckBox4 = new JCheckBox("Stuffed Crust");

    // Define ChangeListener
    ChangeListener changeListener = new ChangeListener() {
      public void stateChanged(ChangeEvent changeEvent) {
        AbstractButton abstractButton =
          (AbstractButton)changeEvent.getSource();
        ButtonModel buttonModel = abstractButton.getModel();
        boolean armed = buttonModel.isArmed();
        boolean pressed = buttonModel.isPressed();
        boolean selected = buttonModel.isSelected();
        System.out.println("Changed: " + armed + "/" + pressed + "/" +
          selected);
      }
    };

    aCheckBox4.addChangeListener(changeListener);
    frame.add(aCheckBox4);
    frame.setSize(300, 200);
    frame.setVisible(true);
  }
}
