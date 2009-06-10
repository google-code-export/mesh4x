package org.mesh4j.ektoo.ui.component.messagedialog;

import java.awt.Frame;

import javax.swing.JOptionPane;

public class MessageDialog {

	public static void showSimpleMessage(Frame frame, String message) {

		JOptionPane.showMessageDialog(frame, message, "",
				JOptionPane.PLAIN_MESSAGE);
	}

	public static void showWarningMessage(Frame frame, String message) {

		JOptionPane.showMessageDialog(frame, message, "Warning",
				JOptionPane.WARNING_MESSAGE);
	}

	public static void showInformationMessage(Frame frame, String message) {

		JOptionPane.showMessageDialog(frame, message, "Info",
				JOptionPane.INFORMATION_MESSAGE);
	}

	public static void showErrorMessage(Frame frame, String message) {
		JOptionPane.showMessageDialog(frame, message, "Error",
				JOptionPane.ERROR_MESSAGE);
	}
	
	public static void showErrorMessage(Frame frame, String title, String message) {
		JOptionPane.showMessageDialog(frame, message, "Error: " + title,
				JOptionPane.ERROR_MESSAGE);
	}
}
