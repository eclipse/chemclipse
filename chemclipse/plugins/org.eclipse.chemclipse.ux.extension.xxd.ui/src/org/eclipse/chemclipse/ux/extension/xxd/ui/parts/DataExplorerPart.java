/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.ux.extension.ui.preferences.PreferencePage;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierFileEditorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.editors.EditorSupportFactory;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.editors.MethodSupportFactory;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.editors.ProjectExplorerSupportFactory;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.DataType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageFileExplorer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.DataExplorerUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class DataExplorerPart {

	private DataExplorerUI dataExplorerUI;
	//

	@Inject
	public DataExplorerPart(Composite parent) {
		initialize(parent);
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		parent.setBackground(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		//
		createToolbarMain(parent);
		createFileExplorerTreeViewer(parent);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(2, false));
		//
		createResetButton(composite);
		createSettingsButton(composite);
	}

	private void createResetButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the file explorer.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setSupplierFileEditorSupport();
			}
		});
	}

	private void createSettingsButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferencePage preferencePage1 = new PreferencePageFileExplorer();
				preferencePage1.setTitle("File Explorer Settings");
				//
				IPreferencePage preferencePage2 = new PreferencePage();
				preferencePage2.setTitle("Data Settings");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePage1));
				preferenceManager.addToRoot(new PreferenceNode("2", preferencePage2));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(DisplayUtils.getShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					try {
						setSupplierFileEditorSupport();
					} catch(Exception e1) {
						MessageDialog.openError(e.widget.getDisplay().getActiveShell(), "Settings", "Something has gone wrong to apply the chart settings.");
					}
				}
			}
		});
	}

	private void createFileExplorerTreeViewer(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new FillLayout());
		//
		dataExplorerUI = new DataExplorerUI(composite);
		setSupplierFileEditorSupport();
	}

	public void setSupplierFileEditorSupport() {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		List<ISupplierFileEditorSupport> editorSupportList = new ArrayList<ISupplierFileEditorSupport>();
		/*
		 * MSD
		 */
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_DATA_MSD)) {
			editorSupportList.add(new EditorSupportFactory(DataType.MSD).getInstanceEditorSupport());
		}
		/*
		 * MSD Library
		 */
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_LIBRARY_MSD)) {
			editorSupportList.add(org.eclipse.chemclipse.ux.extension.msd.ui.support.DatabaseSupport.getInstanceEditorSupport());
		}
		/*
		 * MSD Scan
		 */
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_SCANS_MSD)) {
			editorSupportList.add(org.eclipse.chemclipse.ux.extension.msd.ui.support.MassSpectrumSupport.getInstanceEditorSupport());
		}
		/*
		 * CSD
		 */
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_DATA_CSD)) {
			editorSupportList.add(new EditorSupportFactory(DataType.CSD).getInstanceEditorSupport());
		}
		/*
		 * WSD
		 */
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_DATA_WSD)) {
			editorSupportList.add(new EditorSupportFactory(DataType.WSD).getInstanceEditorSupport());
		}
		/*
		 * XIR
		 */
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_DATA_XIR)) {
			editorSupportList.add(new EditorSupportFactory(DataType.XIR).getInstanceEditorSupport());
		}
		/*
		 * NMR
		 */
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_DATA_NMR)) {
			editorSupportList.add(new EditorSupportFactory(DataType.NMR).getInstanceEditorSupport());
		}
		/*
		 * CAL
		 */
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_DATA_CAL)) {
			editorSupportList.add(new ProjectExplorerSupportFactory(DataType.CAL).getInstanceEditorSupport());
		}
		/*
		 * PCR
		 */
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_DATA_PCR)) {
			editorSupportList.add(new EditorSupportFactory(DataType.PCR).getInstanceEditorSupport());
		}
		/*
		 * SEQ
		 */
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_DATA_SEQUENCE)) {
			editorSupportList.add(new EditorSupportFactory(DataType.SEQ).getInstanceEditorSupport());
		}
		/*
		 * MTH
		 */
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_DATA_METHOD)) {
			editorSupportList.add(new MethodSupportFactory().getInstanceEditorSupport());
		}
		//
		dataExplorerUI.setSupplierFileEditorSupportList(editorSupportList);
	}
}
