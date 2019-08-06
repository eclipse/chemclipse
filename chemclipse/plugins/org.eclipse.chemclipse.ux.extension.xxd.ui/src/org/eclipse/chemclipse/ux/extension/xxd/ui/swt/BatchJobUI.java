/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
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
import org.eclipse.chemclipse.model.methods.IProcessMethod;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedMethodUI;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
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

	private IModificationHandler modificationHandler;
	private DataListUI listUI;
	private ExtendedMethodUI extendedMethodUI;
	private Composite composite;

	public BatchJobUI(Composite parent, ProcessTypeSupport processingSupport, IPreferenceStore preferenceStore, String userlocationPrefrenceKey, DataType[] dataTypes, IRunnableWithProgress executionRunnable) {
		composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, true));
		ToolBar toolBar = new ToolBar(composite, SWT.FLAT);
		initToolbar(toolBar, executionRunnable);
		// left part with files
		listUI = new DataListUI(composite, this::setEditorDirty, preferenceStore, userlocationPrefrenceKey, dataTypes);
		listUI.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		listUI.getConfig().setToolbarVisible(false);
		// right part with methods
		extendedMethodUI = new ExtendedMethodUI(composite, SWT.NONE, processingSupport, dataTypes);
		extendedMethodUI.setLayoutData(new GridData(GridData.FILL_BOTH));
		extendedMethodUI.setModificationHandler(this::setEditorDirty);
		extendedMethodUI.getConfig().setToolbarVisible(false);
	}

	private void initToolbar(ToolBar toolBar, IRunnableWithProgress executionRunnable) {

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		gridData.horizontalSpan = 2;
		toolBar.setLayoutData(gridData);
		createExecuteButton(toolBar, executionRunnable);
		createSettingsButton(toolBar);
	}

	private ToolItem createSettingsButton(ToolBar toolBar) {

		final ToolItem item = new ToolItem(toolBar, SWT.PUSH);
		item.setText("Settings");
		item.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		item.setToolTipText("Modify the settings.");
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PreferenceManager preferenceManager = new PreferenceManager();
				DataListUIConfig configListUI = listUI.getConfig();
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
		return item;
	}

	private ToolItem createExecuteButton(ToolBar toolBar, IRunnableWithProgress executionRunnable) {

		final ToolItem item = new ToolItem(toolBar, SWT.PUSH);
		item.setText("Execute Job");
		item.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImage.SIZE_16x16));
		item.setToolTipText("Execute the batch job.");
		item.addSelectionListener(new SelectionAdapter() {

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
		return item;
	}

	public void doLoad(List<File> files, IProcessMethod processMethod) {

		listUI.setFiles(files);
		extendedMethodUI.update(processMethod);
		if(modificationHandler != null) {
			modificationHandler.setDirty(false);
		}
	}

	public List<File> getFiles() {

		return listUI.getFiles();
	}

	public IProcessMethod getProcessMethod() {

		return extendedMethodUI.getProcessMethod();
	}

	public void setModificationHandler(IModificationHandler modificationHandler) {

		this.modificationHandler = modificationHandler;
	}

	private void setEditorDirty(boolean dirty) {

		if(modificationHandler != null) {
			modificationHandler.setDirty(dirty);
		}
	}

	public void setFocus() {

		composite.setFocus();
	}
}
