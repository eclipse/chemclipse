/*******************************************************************************
 * Copyright (c) 2016, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - Move custom made toolbar to the native toolbar with E4Handlers
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.ux.extension.ui.preferences.PreferencePage;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierFileEditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.swt.DataExplorerUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.EditorSupportFactory;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.editors.ProjectExplorerSupportFactory;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageFileExplorer;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class DataExplorerPart {

	private final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private DataExplorerUI dataExplorerUI;
	@Inject
	private IEventBroker broker;
	//

	@Inject
	public DataExplorerPart(Composite parent) {
		dataExplorerUI = new DataExplorerUI(parent, broker, preferenceStore);
		setSupplierFileEditorSupport();
	}

	public void setSupplierFileEditorSupport() {

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
			editorSupportList.add(new EditorSupportFactory(DataType.MTH).getInstanceEditorSupport());
		}
		/*
		 * QDB
		 */
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_DATA_QUANT_DB)) {
			editorSupportList.add(new EditorSupportFactory(DataType.QDB).getInstanceEditorSupport());
		}
		//
		dataExplorerUI.setSupplierFileIdentifier(editorSupportList);
		dataExplorerUI.expandLastDirectoryPath();
	}

	public static final class DataExplorerSettingsHandler {

		@Execute
		public void execute(MPart part, @Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {

			DataExplorerPart explorer = (DataExplorerPart)part.getObject();
			PreferenceManager preferenceManager = new PreferenceManager();
			preferenceManager.addToRoot(new PreferenceNode("1", new PreferencePageFileExplorer()));
			preferenceManager.addToRoot(new PreferenceNode("2", new PreferencePage()));
			//
			PreferenceDialog preferenceDialog = new PreferenceDialog(shell, preferenceManager);
			preferenceDialog.create();
			preferenceDialog.setMessage("Settings");
			if(preferenceDialog.open() == Window.OK) {
				try {
					explorer.setSupplierFileEditorSupport();
				} catch(Exception e1) {
					MessageDialog.openError(shell, "Settings", "Something has gone wrong to apply the chart settings.");
				}
			}
		}
	}

	public static final class ResetDataExplorerHandler {

		@Execute
		public void execute(MPart part) {

			DataExplorerPart explorer = (DataExplorerPart)part.getObject();
			explorer.dataExplorerUI.expandLastDirectoryPath();
		}
	}
}
