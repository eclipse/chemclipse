/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - move execute Button to the top toolbar to conserve space, use DataListUI instead, refactor run action to be given from outside, optimize the general layout
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.chemclipse.model.handler.IModificationHandler;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.supplier.ProcessSupplierContext;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedMethodUI;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class BatchJobUI {

	private Composite composite;
	private DataListUI dataListUI;
	private ExtendedMethodUI extendedMethodUI;
	//
	private IModificationHandler modificationHandler;
	private ProcessSupplierContext processingSupport;
	private IPreferenceStore preferenceStore;
	private String userlocationPrefrenceKey;
	private DataCategory[] dataCategories;
	private IRunnableWithProgress executionRunnable;

	public BatchJobUI(Composite parent, ProcessSupplierContext processingSupport, IPreferenceStore preferenceStore, String userlocationPrefrenceKey, DataType[] dataTypes, IRunnableWithProgress executionRunnable) {

		this(parent, processingSupport, preferenceStore, userlocationPrefrenceKey, DataType.convert(dataTypes), executionRunnable);
	}

	public BatchJobUI(Composite parent, ProcessSupplierContext processingSupport, IPreferenceStore preferenceStore, String userlocationPrefrenceKey, DataCategory[] dataCategories, IRunnableWithProgress executionRunnable) {

		this.processingSupport = processingSupport;
		this.preferenceStore = preferenceStore;
		this.userlocationPrefrenceKey = userlocationPrefrenceKey;
		this.dataCategories = dataCategories;
		this.executionRunnable = executionRunnable;
		//
		composite = createControl(parent);
	}

	public void setFocus() {

		composite.setFocus();
	}

	public void doLoad(List<File> files, IProcessMethod processMethod) {

		dataListUI.setFiles(files);
		extendedMethodUI.setProcessMethod(processMethod);
		if(modificationHandler != null) {
			modificationHandler.setDirty(false);
		}
	}

	public DataListUI getDataList() {

		return dataListUI;
	}

	public ExtendedMethodUI getMethod() {

		return extendedMethodUI;
	}

	public void setModificationHandler(IModificationHandler modificationHandler) {

		this.modificationHandler = modificationHandler;
	}

	protected void setEditorDirty(boolean dirty) {

		if(modificationHandler != null) {
			modificationHandler.setDirty(dirty);
		}
	}

	protected DataListUI createDataList(Composite parent, IPreferenceStore preferenceStore, String userlocationPrefrenceKey, DataCategory[] dataCategories) {

		return new DataListUI(parent, this::setEditorDirty, preferenceStore, userlocationPrefrenceKey, DataType.convert(dataCategories));
	}

	private Composite createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, true));
		//
		createToolBar(composite);
		//
		dataListUI = createDataListUI(composite);
		extendedMethodUI = createExtendedMethodUI(composite);
		//
		return composite;
	}

	private ToolBar createToolBar(Composite parent) {

		ToolBar toolBar = new ToolBar(parent, SWT.FLAT);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		gridData.horizontalSpan = 2;
		toolBar.setLayoutData(gridData);
		//
		createLabelInfo(toolBar);
		createButtonExecute(toolBar, executionRunnable);
		createButtonSettings(toolBar);
		//
		return toolBar;
	}

	private DataListUI createDataListUI(Composite parent) {

		DataListUI dataListUI = createDataList(parent, preferenceStore, userlocationPrefrenceKey, dataCategories);
		dataListUI.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		dataListUI.getConfig().setToolbarVisible(false);
		//
		return dataListUI;
	}

	private ExtendedMethodUI createExtendedMethodUI(Composite parent) {

		ExtendedMethodUI extendedMethodUI = new ExtendedMethodUI(parent, SWT.NONE, processingSupport, dataCategories);
		extendedMethodUI.setLayoutData(new GridData(GridData.FILL_BOTH));
		extendedMethodUI.setModificationHandler(this::setEditorDirty);
		extendedMethodUI.getConfig().setToolbarVisible(false);
		//
		return extendedMethodUI;
	}

	private ToolItem createLabelInfo(ToolBar toolBar) {

		final ToolItem toolItem = new ToolItem(toolBar, SWT.NONE);
		toolItem.setText(getDataCategories());
		toolItem.setToolTipText("The following data types are active.");
		toolItem.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
		toolItem.setEnabled(false);
		//
		return toolItem;
	}

	private String getDataCategories() {

		StringBuilder builder = new StringBuilder();
		builder.append("Data Type(s): ");
		int size = dataCategories.length;
		for(int i = 0; i < size; i++) {
			DataCategory dataCategory = dataCategories[i];
			builder.append(dataCategory.name());
			if(i < (size - 1)) {
				builder.append(" | ");
			}
		}
		//
		return builder.toString();
	}

	private ToolItem createButtonExecute(ToolBar toolBar, IRunnableWithProgress executionRunnable) {

		final ToolItem toolItem = new ToolItem(toolBar, SWT.PUSH);
		toolItem.setText("Execute");
		toolItem.setToolTipText("Execute the batch job.");
		toolItem.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImage.SIZE_16x16));
		//
		toolItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {

				ProgressMonitorDialog monitor = new ProgressMonitorDialog(toolBar.getShell());
				try {
					monitor.run(true, true, executionRunnable);
				} catch(InvocationTargetException e) {
					ProcessingInfoViewSupport.updateProcessingInfoError("BatchJob", "Execution of the job failed", e.getCause());
				} catch(InterruptedException e) {
					// canceled
					return;
				}
			}
		});
		return toolItem;
	}

	private ToolItem createButtonSettings(ToolBar toolBar) {

		final ToolItem toolItem = new ToolItem(toolBar, SWT.PUSH);
		toolItem.setText("Settings");
		toolItem.setToolTipText("Modify the settings.");
		toolItem.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		//
		toolItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PreferenceManager preferenceManager = new PreferenceManager();
				DataListUIConfig configListUI = dataListUI.getConfig();
				addPages("listUI", configListUI.getPreferencePages(), preferenceManager);
				MethodUIConfig methodUIConfig = extendedMethodUI.getConfig();
				addPages("extendedMethodUI", methodUIConfig.getPreferencePages(), preferenceManager);
				PreferenceDialog preferenceDialog = new PreferenceDialog(toolBar.getShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					configListUI.applySettings();
					methodUIConfig.applySettings();
				}
			}

			private void addPages(String prefix, IPreferencePage[] preferencePages, PreferenceManager preferenceManager) {

				for(int i = 0; i < preferencePages.length; i++) {
					preferenceManager.addToRoot(new PreferenceNode(prefix + "." + (i + 1), preferencePages[i]));
				}
			}
		});
		return toolItem;
	}
}
