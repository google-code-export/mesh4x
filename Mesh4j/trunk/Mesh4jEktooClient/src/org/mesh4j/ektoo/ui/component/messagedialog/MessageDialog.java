package org.mesh4j.ektoo.ui.component.messagedialog;

import java.awt.Frame;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * @author sharif uddin
 *
 */
public class MessageDialog {

	public static void showSimpleMessage(JFrame frame, String message) {

		JOptionPane.showMessageDialog(frame, message, "",
				JOptionPane.PLAIN_MESSAGE);
	}

	public static void showWarningMessage(JFrame frame, String message) {

		JOptionPane.showMessageDialog(frame, message, "Warning",
				JOptionPane.WARNING_MESSAGE);
	}

	public static void showInformationMessage(JFrame frame, String message) {

		JOptionPane.showMessageDialog(frame, message, "Info",
				JOptionPane.INFORMATION_MESSAGE);
	}

	public static void showErrorMessage(Frame frame, String message) {

		JOptionPane.showMessageDialog(frame, message, "Error",
				JOptionPane.ERROR_MESSAGE);
	}
}
