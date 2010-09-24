package org.mesh4j.meshes.ui.wizard;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.mesh4j.meshes.controller.WizardController;

public class WizardUtils {
	
	public static JTextField newTextField(final WizardController controller, String name) {
		return newTextField(controller, name, "");
	}
	
	public static JTextField newTextField(final WizardController controller, final String name, String text) {
		final JTextField field = new JTextField(text);
		field.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				controller.setValue(name, field.getText());
			}
		});
		controller.setValue(name, text);
		nextWhenEnterPressedOn(controller, field);
		return field;
	}
	
	public static JPasswordField newPasswordField(final WizardController controller, final String name) {
		final JPasswordField field = new JPasswordField();
		field.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				controller.setValue(name, new String(field.getPassword()));
			}
		});
		nextWhenEnterPressedOn(controller, field);
		return field;
	}
	
	public static JComboBox newJComboBox(final WizardController controller, final String name, Object ... items) {
		final JComboBox combo = new JComboBox(items);
		combo.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				controller.setValue(name, combo.getSelectedItem());
			}
		});
		if (items.length > 0)
			controller.setValue(name, items[0]);
		return combo;
	}
	
	public static void nextWhenEnterPressedOn(final WizardController controller, Component ... components) {
		for(Component component : components) {
			component.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					if (e.getKeyCode() == 10)
						controller.nextButtonPressed();
				}
			});
		}
	}

}
