/*******************************************************************************
 * Copyright (c) 2019, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - enable profiles
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessorPreferences;
import org.eclipse.chemclipse.processing.supplier.ProcessorPreferences.DialogBehavior;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class SettingsPreferencesEditPage extends WizardPage {

	private TreeViewer treeViewer;
	private final Supplier<Collection<ProcessorPreferences<?>>> preferenceSupplier;

	public SettingsPreferencesEditPage(Supplier<Collection<ProcessorPreferences<?>>> preferenceSupplier) {

		super(SettingsPreferencesEditPage.class.getName());
		this.preferenceSupplier = preferenceSupplier;
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		treeViewer = new TreeViewer(composite);
		treeViewer.getTree().setHeaderVisible(true);
		treeViewer.getTree().setLinesVisible(true);
		treeViewer.setContentProvider(new TreeNodeContentProvider());
		{
			TreeViewerColumn column = new TreeViewerColumn(treeViewer, SWT.NONE);
			column.getColumn().setWidth(300);
			column.setLabelProvider(new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {

					if(element instanceof TreeNode) {
						element = ((TreeNode)element).getValue();
					}
					ProcessorPreferences<?> entry = getEntry(element);
					if(entry != null) {
						return entry.getSupplier().getName();
					}
					return super.getText(element);
				}
			});
		}
		{
			TreeViewerColumn column = new TreeViewerColumn(treeViewer, SWT.CENTER);
			column.getColumn().setWidth(110);
			column.getColumn().setText("ask for settings");
			column.setLabelProvider(new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {

					ProcessorPreferences<?> preferences = getEntry(element);
					if(preferences != null) {
						if(preferences.getDialogBehaviour() == DialogBehavior.SHOW) {
							return "yes";
						} else {
							return "no";
						}
					}
					return "";
				}
			});
		}
		{
			TreeViewerColumn column = new TreeViewerColumn(treeViewer, SWT.NONE);
			column.getColumn().setWidth(500);
			column.getColumn().setText("Options");
			column.setLabelProvider(new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {

					if(element instanceof TreeNode) {
						element = ((TreeNode)element).getValue();
					}
					ProcessorPreferences<Object> preferences = getEntry(element);
					if(preferences != null) {
						if(preferences.isUseSystemDefaults()) {
							return "(System Default)";
						} else {
							return preferences.getUserSettingsAsString();
						}
					}
					return "";
				}
			});
		}
		ToolBar toolBar = new ToolBar(composite, SWT.VERTICAL | SWT.FLAT);
		toolBar.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, true));
		ToolItem edit = new ToolItem(toolBar, SWT.PUSH);
		edit.setToolTipText("Edit stored data for this processor");
		edit.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				try {
					doEdit();
				} catch(IOException e1) {
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		edit.setEnabled(false);
		edit.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
		ToolItem delete = new ToolItem(toolBar, SWT.PUSH);
		delete.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object[] array = treeViewer.getStructuredSelection().toArray();
				for(Object object : array) {
					getEntry(object).reset();
				}
				updateTree();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		delete.setEnabled(false);
		delete.setToolTipText("Removes the stored data for this processor and reset it to the defaults");
		delete.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16));
		ToolItem delete_all = new ToolItem(toolBar, SWT.PUSH);
		delete_all.setToolTipText("Removes all stored data for this processor and reset it to the defaults");
		delete_all.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE_ALL, IApplicationImage.SIZE_16x16));
		delete_all.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				for(ProcessorPreferences<?> preferences : preferenceSupplier.get()) {
					preferences.reset();
				}
				updateTree();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		treeViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				ITreeSelection selection = treeViewer.getStructuredSelection();
				Object[] array = selection.toArray();
				boolean buttonsEnabled = array.length > 0;
				for(Object object : array) {
					if(getEntry(object) == null) {
						buttonsEnabled = false;
						break;
					}
				}
				edit.setEnabled(buttonsEnabled && array.length == 1);
				delete.setEnabled(buttonsEnabled);
			}
		});
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {

				try {
					doEdit();
				} catch(IOException e) {
				}
			}
		});
		updateTree();
		setControl(composite);
	}

	private void doEdit() throws IOException {

		ITreeSelection selection = treeViewer.getStructuredSelection();
		ProcessorPreferences<?> entry = getEntry(selection.getFirstElement());
		if(entry != null) {
			if(SettingsWizard.openEditPreferencesWizard(getShell(), entry, true)) {
				updateTree();
			}
		}
	}

	private void updateTree() {

		Map<String, TreeNode> categories = new TreeMap<>();
		for(ProcessorPreferences<?> entry : preferenceSupplier.get()) {
			IProcessTypeSupplier supplier = entry.getSupplier().getTypeSupplier();
			TreeNode processorNode = new TreeNode(entry);
			String category = supplier.getCategory();
			TreeNode categoryNode = categories.get(category);
			if(categoryNode == null) {
				categoryNode = new TreeNode(category);
				categoryNode.setChildren(new TreeNode[]{processorNode});
				categories.put(category, categoryNode);
			} else {
				TreeNode[] children = categoryNode.getChildren();
				TreeNode[] copyOf = Arrays.copyOf(children, children.length + 1);
				copyOf[children.length] = processorNode;
				categoryNode.setChildren(copyOf);
			}
			processorNode.setParent(categoryNode);
		}
		treeViewer.setInput(categories.values().toArray(new TreeNode[0]));
		treeViewer.refresh();
		treeViewer.expandAll();
	}

	@SuppressWarnings("unchecked")
	private static <T> ProcessorPreferences<T> getEntry(Object element) {

		if(element instanceof TreeNode) {
			element = ((TreeNode)element).getValue();
		}
		if(element instanceof ProcessorPreferences<?>) {
			return (ProcessorPreferences<T>)element;
		}
		return null;
	}
}
