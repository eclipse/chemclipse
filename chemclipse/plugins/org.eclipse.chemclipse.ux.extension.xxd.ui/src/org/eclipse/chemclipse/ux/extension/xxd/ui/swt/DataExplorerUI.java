/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - performance optimization and cleanup, refactor handling of Suppliers
 * Matthias Mailänder - right-click refresh option
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.converter.ISupplierFileIdentifier;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.swt.ui.preferences.PreferencePageSystem;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierFileEditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.swt.MultiDataExplorerTreeUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.editors.ProjectExplorerSupportFactory;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.GenericSupplierEditorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.SupplierEditorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePage;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageFileExplorer;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class DataExplorerUI extends MultiDataExplorerTreeUI implements IExtendedPartUI {

	private IEclipseContext context;
	private ISupplierFileIdentifier supplierFileIdentifier;
	//
	IPreferenceStore preferenceStore;

	public DataExplorerUI(Composite parent, IPreferenceStore preferenceStore, IEclipseContext context, ISupplierFileIdentifier supplierFileIdentifier) {

		super(parent, preferenceStore);
		this.preferenceStore = preferenceStore;
		this.context = context;
		this.supplierFileIdentifier = supplierFileIdentifier;
		setSupplierFileEditorSupport();
	}

	@Override
	protected void createToolbarMain(Composite parent) {

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
		button.setToolTipText("Reset the data explorer.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setSupplierFileEditorSupport();
				expandLastDirectoryPath();
			}
		});
	}

	private void createSettingsButton(Composite parent) {

		createSettingsButton(parent, Arrays.asList( //
				PreferencePageFileExplorer.class, //
				PreferencePageSystem.class, //
				PreferencePage.class, //
				org.eclipse.chemclipse.ux.extension.ui.preferences.PreferencePage.class //
		), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				setSupplierFileEditorSupport();
			}
		}, false);
	}

	private void setSupplierFileEditorSupport() {

		List<ISupplierFileEditorSupport> editorSupportList = new ArrayList<>();
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
		 * ISD
		 */
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_DATA_ISD)) {
			editorSupportList.add(new SupplierEditorSupport(DataType.ISD, () -> context));
		}
		/*
		 * TSD
		 */
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_DATA_TSD)) {
			editorSupportList.add(new SupplierEditorSupport(DataType.TSD, () -> context));
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
		setSupplierFileIdentifier(editorSupportList);
		expandLastDirectoryPath();
	}
}