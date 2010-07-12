package org.mesh4j.meshes.ui.component;

import java.awt.Component;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.mesh4j.meshes.model.SyncLog;

@SuppressWarnings("serial")
public class SyncLogList extends JList
{
	public SyncLogList() {
		setCellRenderer(new SyncLogCellRenderer());
	}
	
	private class SyncLogCellRenderer extends JLabel implements ListCellRenderer {

		private DateFormat dateFormat = DateFormat.getDateTimeInstance();
		private ImageIcon errorIcon = new ImageIcon(SyncLogCellRenderer.class.getResource("error.png"));
		private ImageIcon infoIcon = new ImageIcon(SyncLogCellRenderer.class.getResource("ok.png"));
		
		public SyncLogCellRenderer() {
			setOpaque(true);
		}
		
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			
			SyncLog log = (SyncLog) value;
			setIcon(log.isSucceeded() ? infoIcon : errorIcon);
			
			String text = String.format("<html><span style='color:gray'>%1$s</span> - %2$s",
					dateFormat.format(log.getDate()), log.getMessage());
			setText(text);
			
			setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
			setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
			
			return this;
		}
	}

	public void setLogEntries(SyncLog[] logEntries) {
		
		DefaultListModel model = new DefaultListModel();
		
		// Sort the log entries in descendant order
		Arrays.sort(logEntries, new Comparator<SyncLog>() {
			public int compare(SyncLog log1, SyncLog log2) {
				return log2.getDate().compareTo(log1.getDate());
			}
		});
		
		for (SyncLog syncLog : logEntries) {
			model.addElement(syncLog);
		}
		
		setModel(model);
	}
}
