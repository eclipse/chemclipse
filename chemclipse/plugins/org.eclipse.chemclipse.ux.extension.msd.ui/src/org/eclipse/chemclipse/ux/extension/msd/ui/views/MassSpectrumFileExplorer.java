/*******************************************************************************
 * Copyright (c) 2013, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Christoph Läubrich - reuse the DataExplorerUI
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.views;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.process.supplier.batchprocess.core.BatchJobFileSupplier;
import org.eclipse.chemclipse.ux.extension.msd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.msd.ui.support.DatabaseSupport;
import org.eclipse.chemclipse.ux.extension.msd.ui.support.MassSpectrumSupport;
import org.eclipse.chemclipse.ux.extension.ui.methods.MethodEditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.model.DataExplorerTreeSettings;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierFileEditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.swt.MultiDataExplorerTreeUI;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import jakarta.inject.Inject;

public class MassSpectrumFileExplorer {

	private AtomicReference<MultiDataExplorerTreeUI> dataExplorerControl = new AtomicReference<>();

	@Inject
	public MassSpectrumFileExplorer(Composite parent) {

		MultiDataExplorerTreeUI explorerUI = new MultiDataExplorerTreeUI(parent, SWT.NONE, new DataExplorerTreeSettings(Activator.getDefault().getPreferenceStore()));
		List<ISupplierFileEditorSupport> editorSupportList = new ArrayList<>();
		editorSupportList.add(MassSpectrumSupport.getInstanceEditorSupport());
		editorSupportList.add(new MethodEditorSupport());
		editorSupportList.add(DatabaseSupport.getInstanceEditorSupport());
		/*
		 * The batch job editor is contributed by a downstream plug-in.
		 */
		ISupplierFileEditorSupport batchJobEditorSupport = Adapters.adapt(new BatchJobFileSupplier(), ISupplierFileEditorSupport.class);
		if(batchJobEditorSupport != null) {
			editorSupportList.add(batchJobEditorSupport);
		}
		explorerUI.setSupplierFileIdentifier(editorSupportList);
		explorerUI.expandLastDirectoryPath();

		dataExplorerControl.set(explorerUI);
	}

	@Focus
	private void setFocus() {

		dataExplorerControl.get().setFocus();
	}
}