/*******************************************************************************
 * Copyright (c) 2016, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - Move custom made toolbar to the native toolbar with E4Handlers
 * Philip Wenig - modularization
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.converter.ISupplierFileIdentifier;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierFileEditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.swt.DataExplorerUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.editors.ProjectExplorerSupportFactory;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.GenericSupplierEditorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.SupplierEditorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;

public class DataExplorerPart extends AbstractPart {

	@Inject
	private IEventBroker broker;
	@Inject
	private IEclipseContext context;
	@Inject
	private ISupplierFileIdentifier supplierFileIdentifier;
	//
	private DataExplorerUI dataExplorerUI;
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	@PostConstruct
	public void init(Composite parent, MPart part) {

		dataExplorerUI = new DataExplorerUI(parent, broker, preferenceStore);
		setSupplierFileEditorSupport();
	}

	public void setSupplierFileEditorSupport() {

		List<ISupplierFileEditorSupport> editorSupportList = new ArrayList<ISupplierFileEditorSupport>();
		/*
		 * MSD
		 */
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_DATA_MSD)) {
			editorSupportList.add(new SupplierEditorSupport(DataType.MSD, () -> context));
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
			editorSupportList.add(new SupplierEditorSupport(DataType.CSD, () -> context));
		}
		/*
		 * WSD
		 */
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_DATA_WSD)) {
			editorSupportList.add(new SupplierEditorSupport(DataType.WSD, () -> context));
		}
		/*
		 * XIR
		 */
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_DATA_XIR)) {
			editorSupportList.add(new SupplierEditorSupport(DataType.XIR, () -> context));
		}
		/*
		 * NMR
		 */
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_DATA_NMR)) {
			editorSupportList.add(new SupplierEditorSupport(DataType.NMR, () -> context));
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
			editorSupportList.add(new SupplierEditorSupport(DataType.PCR, () -> context));
		}
		/*
		 * SEQ
		 */
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_DATA_SEQUENCE)) {
			editorSupportList.add(new SupplierEditorSupport(DataType.SEQ, () -> context));
		}
		/*
		 * MTH
		 */
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_DATA_METHOD)) {
			editorSupportList.add(new SupplierEditorSupport(DataType.MTH, () -> context));
		}
		/*
		 * QDB
		 */
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_DATA_QUANT_DB)) {
			editorSupportList.add(new SupplierEditorSupport(DataType.QDB, () -> context));
		}
		//
		editorSupportList.add(new GenericSupplierEditorSupport(supplierFileIdentifier, () -> context));
		dataExplorerUI.setSupplierFileIdentifier(editorSupportList);
		dataExplorerUI.expandLastDirectoryPath();
	}

	public DataExplorerUI getDataExplorerUI() {

		return dataExplorerUI;
	}
}
