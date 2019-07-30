/*******************************************************************************
 * Copyright (c) 2017, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.DataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.SupplierEditorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.InputEntriesWizard;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.InputWizardSettings;
import org.eclipse.chemclipse.xxd.process.files.ISupplierFileIdentifier;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class PeakFilesInputPageWizard extends DataInputPageWizard {

	public PeakFilesInputPageWizard(String pageName) {
		super(pageName);
		setTitle("Peak Input Files");
		setDescription("This wizard lets you select peak input files and set bulk group name.");
	}

	@Override
	protected void addFiles() {

		InputWizardSettings inputWizardSettings = new InputWizardSettings(new ScopedPreferenceStore(InstanceScope.INSTANCE, ChromatogramMSDFilesPageWizard.class.getName()), Collections.singleton(new SupplierEditorSupport(DataType.MSD)));
		inputWizardSettings.setTitle("Peak Input Files");
		inputWizardSettings.setDescription("This wizard lets you select several peak input files.");
		Map<File, Collection<ISupplierFileIdentifier>> map = InputEntriesWizard.openWizard(getShell(), inputWizardSettings);
		List<IDataInputEntry> dataInputEntries = new ArrayList<>();
		for(File file : map.keySet()) {
			IDataInputEntry dataInputEntry = new DataInputEntry(file.getAbsolutePath());
			String groupName = getGroupName().trim();
			if(!groupName.isEmpty()) {
				dataInputEntry.setGroupName(groupName);
			}
			dataInputEntries.add(dataInputEntry);
		}
		addInputFiles(dataInputEntries);
		update();
	}
}
