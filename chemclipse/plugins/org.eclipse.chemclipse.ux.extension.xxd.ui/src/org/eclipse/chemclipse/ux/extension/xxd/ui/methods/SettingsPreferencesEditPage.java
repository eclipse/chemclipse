/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - enable profiles, refactoring page
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessorPreferences;
import org.eclipse.chemclipse.processing.supplier.IProcessorPreferences.DialogBehavior;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.messages.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.messages.IExtensionMessages;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class SettingsPreferencesEditPage extends WizardPage {

	private static final Logger logger = Logger.getLogger(SettingsPreferencesEditPage.class);
	//
	private AtomicReference<TreeViewer> treeViewerControl = new AtomicReference<>();
	private AtomicReference<ToolItem> toolItemEditControl = new AtomicReference<>();
	private AtomicReference<ToolItem> toolItemDeleteControl = new AtomicReference<>();
	private final Supplier<Collection<IProcessorPreferences<?>>> preferenceSupplier;

	public SettingsPreferencesEditPage(Supplier<Collection<IProcessorPreferences<?>>> preferenceSupplier) {

		super(SettingsPreferencesEditPage.class.getName());
		this.preferenceSupplier = preferenceSupplier;
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		//
		createTreeViewer(composite);
		createToolBar(composite);
		//
		initialize();
		setControl(composite);
	}

	private void initialize() {

		updateTree();
	}

	private void createTreeViewer(Composite parent) {

		TreeViewer treeViewer = new TreeViewer(parent);
		treeViewer.getTree().setHeaderVisible(true);
		treeViewer.getTree().setLinesVisible(true);
		treeViewer.setContentProvider(new TreeNodeContentProvider());
		treeViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		createColumnName(treeViewer);
		createColumnAskSettings(treeViewer);
		createColumnOptions(treeViewer);
		//
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
				toolItemEditControl.get().setEnabled(buttonsEnabled && array.length == 1);
				toolItemDeleteControl.get().setEnabled(buttonsEnabled);
			}
		});
		//
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {

				doEdit();
			}
		});
		//
		treeViewer.getControl().addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.keyCode == SWT.DEL) {
					resetSettings(e.display.getActiveShell());
				}
			}
		});
		//
		treeViewerControl.set(treeViewer);
	}

	private void createToolBar(Composite parent) {

		ToolBar toolBar = new ToolBar(parent, SWT.VERTICAL | SWT.FLAT);
		toolBar.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, true));
		//
		createToolItemEdit(toolBar);
		createToolItemDelete(toolBar);
		createToolItemDeleteAll(toolBar);
		createToolItemExpand(toolBar);
		createToolItemCollapse(toolBar);
	}

	private void createToolItemEdit(ToolBar toolBar) {

		ToolItem toolItem = new ToolItem(toolBar, SWT.PUSH);
		toolItem.setToolTipText(ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.EDIT_STORED_DATA_FOR_SELECTED_PROCESSOR));
		toolItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				doEdit();
			}
		});
		//
		toolItem.setEnabled(false);
		toolItem.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImageProvider.SIZE_16x16));
		//
		toolItemEditControl.set(toolItem);
	}

	private void createToolItemDelete(ToolBar toolBar) {

		ToolItem toolItem = new ToolItem(toolBar, SWT.PUSH);
		toolItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				resetSettings(e.display.getActiveShell());
			}
		});
		//
		toolItem.setEnabled(false);
		toolItem.setToolTipText(ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.REMOVE_STORED_DATA_RESET_DEFAULT_FOR_SELECTED_PROCESSOR));
		toolItem.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImageProvider.SIZE_16x16));
		//
		toolItemDeleteControl.set(toolItem);
	}

	private void createToolItemDeleteAll(ToolBar toolBar) {

		ToolItem toolItem = new ToolItem(toolBar, SWT.PUSH);
		toolItem.setToolTipText(ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.REMOVE_ALL_STORED_DATA_RESET_DEFAULTS));
		toolItem.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE_ALL, IApplicationImageProvider.SIZE_16x16));
		toolItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(MessageDialog.openConfirm(e.display.getActiveShell(), ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.SETTINGS), ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.RESET_SETTINGS_FOR_ALL_PROCESSORS))) {
					for(IProcessorPreferences<?> preferences : preferenceSupplier.get()) {
						preferences.reset();
					}
					updateTree();
				}
			}
		});
	}

	private void createToolItemExpand(ToolBar toolBar) {

		ToolItem toolItem = new ToolItem(toolBar, SWT.PUSH);
		toolItem.setToolTipText(ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.EXPAND_ALL_PROCESSOR_ITEMS));
		toolItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				treeViewerControl.get().expandAll();
			}
		});
		//
		toolItem.setEnabled(true);
		toolItem.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXPAND_ALL, IApplicationImageProvider.SIZE_16x16));
	}

	private void createToolItemCollapse(ToolBar toolBar) {

		ToolItem toolItem = new ToolItem(toolBar, SWT.PUSH);
		toolItem.setToolTipText(ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.COLLAPSE_ALL_PROCESSOR_ITEMS));
		toolItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				treeViewerControl.get().collapseAll();
			}
		});
		//
		toolItem.setEnabled(true);
		toolItem.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_COLLAPSE_ALL, IApplicationImageProvider.SIZE_16x16));
	}

	private void createColumnName(TreeViewer treeViewer) {

		TreeViewerColumn column = new TreeViewerColumn(treeViewer, SWT.NONE);
		column.getColumn().setWidth(300);
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof TreeNode treeNode) {
					element = treeNode.getValue();
				}
				//
				IProcessorPreferences<?> entry = getEntry(element);
				if(entry != null) {
					return entry.getSupplier().getName();
				}
				//
				return super.getText(element);
			}
		});
	}

	private void createColumnAskSettings(TreeViewer treeViewer) {

		TreeViewerColumn column = new TreeViewerColumn(treeViewer, SWT.CENTER);
		column.getColumn().setWidth(125);
		column.getColumn().setText(ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.DISPLAY_SETTINGS));
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				IProcessorPreferences<?> preferences = getEntry(element);
				if(preferences != null) {
					if(preferences.getDialogBehaviour() == DialogBehavior.SHOW) {
						return ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.YES);
					} else {
						return ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.NO);
					}
				}
				//
				return "";
			}
		});
	}

	private void createColumnOptions(TreeViewer treeViewer) {

		TreeViewerColumn column = new TreeViewerColumn(treeViewer, SWT.NONE);
		column.getColumn().setWidth(500);
		column.getColumn().setText(ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.OPTIONS));
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof TreeNode treeNode) {
					element = treeNode.getValue();
				}
				//
				IProcessorPreferences<Object> preferences = getEntry(element);
				if(preferences != null) {
					if(preferences.isUseSystemDefaults()) {
						return "(" + ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.SYSTEM_DEFAULT) + ")";
					} else {
						return preferences.getUserSettingsAsString();
					}
				}
				//
				return "";
			}
		});
	}

	private void resetSettings(Shell shell) {

		if(MessageDialog.openConfirm(shell, ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.SETTINGS), ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.RESET_SETTINGS_FOR_SELECTED_PROCESSORS))) {
			Object[] array = treeViewerControl.get().getStructuredSelection().toArray();
			for(Object object : array) {
				getEntry(object).reset();
			}
			updateTree();
		}
	}

	private void doEdit() {

		ITreeSelection selection = treeViewerControl.get().getStructuredSelection();
		IProcessorPreferences<?> entry = getEntry(selection.getFirstElement());
		if(entry != null) {
			try {
				if(SettingsWizard.openEditPreferencesWizard(getShell(), entry, true)) {
					updateTree();
				}
			} catch(IOException e) {
				logger.warn(e);
			}
		}
	}

	private void updateTree() {

		Map<String, TreeNode> categories = new TreeMap<>();
		for(IProcessorPreferences<?> entry : preferenceSupplier.get()) {
			/*
			 * Processsor
			 */
			IProcessTypeSupplier supplier = entry.getSupplier().getTypeSupplier();
			TreeNode processorNode = new TreeNode(entry);
			String category = supplier.getCategory();
			TreeNode categoryNode = categories.get(category);
			//
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
		//
		TreeViewer treeViewer = treeViewerControl.get();
		treeViewer.setInput(categories.values().toArray(new TreeNode[0]));
		treeViewer.refresh();
	}

	@SuppressWarnings("unchecked")
	private static <T> IProcessorPreferences<T> getEntry(Object element) {

		if(element instanceof TreeNode treeNode) {
			element = treeNode.getValue();
		}
		//
		if(element instanceof IProcessorPreferences<?>) {
			return (IProcessorPreferences<T>)element;
		}
		//
		return null;
	}
}
