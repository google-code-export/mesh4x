package org.mesh4j.meshes.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.mesh4j.meshes.action.CreateNewMeshAction;
import org.mesh4j.meshes.action.ExitAction;
import org.mesh4j.meshes.model.DataSet;
import org.mesh4j.meshes.model.DataSource;
import org.mesh4j.meshes.model.Mesh;
import org.mesh4j.meshes.ui.Editable.EditableListener;
import org.mesh4j.meshes.ui.component.DataSetView;
import org.mesh4j.meshes.ui.component.DataSourceView;
import org.mesh4j.meshes.ui.component.MeshView;
import org.mesh4j.meshes.ui.component.MeshesTree;
import org.mesh4j.meshes.ui.resource.ResourceManager;

public class MainWindow extends JFrame {
	
	private static final long serialVersionUID = -1240237676349762846L;
	private JPanel viewContainer;
	private JButton applyButton;
	private JButton revertButton;
	private Component currentView = null;
	private JPanel editingButtonsContainer;

	public MainWindow() {
		super();
		initialize();
	}

	private void initialize() {
		this.setSize(new Dimension(800, 560));
		this.getContentPane().setLayout(new BorderLayout());
		
		this.setIconImage(ResourceManager.getLogo());
		
		// Menu
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
			// File
			JMenu fileMenu = new JMenu("File");
			menuBar.add(fileMenu);
				// Create new mesh...
				fileMenu.add(new CreateNewMeshAction());
				fileMenu.addSeparator();
				fileMenu.add(new ExitAction());
		
		// Split panel
		final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		this.add(splitPane, BorderLayout.CENTER);
		viewContainer = new JPanel(new BorderLayout());
		splitPane.setRightComponent(viewContainer);
		splitPane.setDividerLocation(200);
		
		editingButtonsContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		editingButtonsContainer.add(applyButton = new JButton("Apply"));
		editingButtonsContainer.add(revertButton = new JButton("Revert"));
		viewContainer.add(editingButtonsContainer, BorderLayout.SOUTH);
		editingButtonsContainer.setVisible(false);
		
		// Tree for Meshes
		final MeshesTree meshesTree = new MeshesTree();
		splitPane.setLeftComponent(new JScrollPane(meshesTree));
		meshesTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent ev) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) ev.getPath().getLastPathComponent();
				Object userObject = node.getUserObject();
				
				if (userObject instanceof Mesh) {	
					setView(new MeshView((Mesh) userObject));
				} else if (userObject instanceof DataSet) {
					setView(new DataSetView((DataSet) userObject));
				} else if (userObject instanceof DataSource) {
					setView(new DataSourceView((DataSource) userObject));
				} else {
					setView(null);
				}
			}
		});
		meshesTree.setSelectionRow(0);

		this.setTitle("Meshes");
		this.setResizable(false);
		
		applyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (currentView instanceof Editable) {
					((Editable) currentView).saveChanges();
					meshesTree.repaint();
				}
			}
		});

		revertButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (currentView instanceof Editable) {
					((Editable) currentView).discardChanges();
				}
			}
		});
	}
	
	private void setView(Component view) {
		if (currentView != null)
			viewContainer.remove(currentView);
		currentView = view;
		
		if (view != null) {
			viewContainer.add(view);
			if (view instanceof Editable) {
				editingButtonsContainer.setVisible(true);
				Editable editable = (Editable) view;
				editable.setEditableListener(new MainWindowEditableListener());
				applyButton.setEnabled(false);
				revertButton.setEnabled(false);
			} else {
				editingButtonsContainer.setVisible(false);
			}
		}
		
		viewContainer.revalidate();
		viewContainer.repaint();
	}
	
	private class MainWindowEditableListener implements EditableListener {
		@Override
		public void dirtyChanged(boolean isDirty) {
			applyButton.setEnabled(isDirty);
			revertButton.setEnabled(isDirty);
		}
	}
}
