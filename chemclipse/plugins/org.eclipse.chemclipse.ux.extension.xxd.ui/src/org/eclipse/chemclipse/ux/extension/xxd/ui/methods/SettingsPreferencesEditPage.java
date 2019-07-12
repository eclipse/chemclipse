/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.xxd.process.support.IProcessTypeSupplier;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.chemclipse.xxd.process.support.ProcessorPreferences;
import org.eclipse.chemclipse.xxd.process.support.ProcessorSupplier;
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

	private ProcessTypeSupport processTypeSupport;
	private TreeViewer treeViewer;

	public SettingsPreferencesEditPage(ProcessTypeSupport processTypeSupport) {
		super(SettingsPreferencesEditPage.class.getName());
		this.processTypeSupport = processTypeSupport;
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
					Entry<ProcessorSupplier, ProcessorPreferences> entry = getEntry(element);
					if(entry != null) {
						ProcessorSupplier key = entry.getKey();
						return key.getName();
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

					Entry<ProcessorSupplier, ProcessorPreferences> entry = getEntry(element);
					if(entry != null) {
						ProcessorPreferences preferences = entry.getValue();
						if(preferences.isAskForSettings()) {
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
					Entry<ProcessorSupplier, ProcessorPreferences> entry = getEntry(element);
					if(entry != null) {
						ProcessorPreferences preferences = entry.getValue();
						if(preferences.isUseSystemDefaults()) {
							return "(System Default)";
						} else {
							return preferences.getUserSettings();
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

				doEdit();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		edit.setEnabled(false);
		edit.setImage(ApplicationImageFactory.getInstance().getImage(ApplicationImage.IMAGE_EDIT, ApplicationImage.SIZE_16x16));
		ToolItem delete = new ToolItem(toolBar, SWT.PUSH);
		delete.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object[] array = treeViewer.getStructuredSelection().toArray();
				for(Object object : array) {
					Entry<ProcessorSupplier, ProcessorPreferences> entry = getEntry(object);
					entry.getValue().reset();
				}
				updateTree();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		delete.setEnabled(false);
		delete.setToolTipText("Removes the stored data for this processor and reset it to the defaults");
		delete.setImage(ApplicationImageFactory.getInstance().getImage(ApplicationImage.IMAGE_DELETE, ApplicationImage.SIZE_16x16));
		ToolItem delete_all = new ToolItem(toolBar, SWT.PUSH);
		delete_all.setToolTipText("Removes all stored data for this processor and reset it to the defaults");
		delete_all.setImage(ApplicationImageFactory.getInstance().getImage(ApplicationImage.IMAGE_DELETE_ALL, ApplicationImage.SIZE_16x16));
		delete_all.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Collection<ProcessorPreferences> values = processTypeSupport.getAllPreferences().values();
				for(ProcessorPreferences preferences : values) {
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

				doEdit();
			}
		});
		updateTree();
		setControl(composite);
	}

	private void doEdit() {

		ITreeSelection selection = treeViewer.getStructuredSelection();
		Entry<ProcessorSupplier, ProcessorPreferences> entry = getEntry(selection.getFirstElement());
		if(entry != null) {
			if(SettingsPreferencesWizard.openWizard(getShell(), entry.getValue(), entry.getKey())) {
				updateTree();
			}
		}
	}

	private void updateTree() {

		Map<String, TreeNode> categories = new TreeMap<>();
		for(Entry<ProcessorSupplier, ProcessorPreferences> entry : processTypeSupport.getAllPreferences().entrySet()) {
			IProcessTypeSupplier<?> supplier = processTypeSupport.getSupplier(entry.getKey().getId());
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

	protected void edit(Entry<String, ProcessorPreferences> element) {

	}

	@SuppressWarnings("unchecked")
	private static Entry<ProcessorSupplier, ProcessorPreferences> getEntry(Object element) {

		if(element instanceof TreeNode) {
			element = ((TreeNode)element).getValue();
		}
		if(element instanceof Entry<?, ?>) {
			return (Entry<ProcessorSupplier, ProcessorPreferences>)element;
		}
		return null;
	}
}
