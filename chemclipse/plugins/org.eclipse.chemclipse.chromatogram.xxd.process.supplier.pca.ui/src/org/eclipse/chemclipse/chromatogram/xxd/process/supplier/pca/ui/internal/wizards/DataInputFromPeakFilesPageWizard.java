/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.DataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.ux.extension.msd.ui.wizards.PeakInputEntriesWizard;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

public class DataInputFromPeakFilesPageWizard extends DataInputPageWizard {

	public DataInputFromPeakFilesPageWizard(String pageName) {
		super(pageName);
		setTitle("Peak Input Files");
		setDescription("This wizard lets you select peak input files and set bulk group name.");
	}

	@Override
	protected void addFiles() {

		PeakInputEntriesWizard inputWizard = new PeakInputEntriesWizard();
		BatchProcessWizardDialog wizardDialog = new BatchProcessWizardDialog(Display.getCurrent().getActiveShell(), inputWizard);
		inputWizard.setEclipsePreferes(PreferenceSupplier.INSTANCE().getPreferences(), PreferenceSupplier.N_INPUT_FILE);
		wizardDialog.create();
		int returnCode = wizardDialog.open();
		if(returnCode == Window.OK) {
			List<String> selectedPeakFiles = inputWizard.getChromatogramWizardElements().getSelectedChromatograms();
			List<IDataInputEntry> dataInputEntries = new ArrayList<>();
			for(String selectedPeakFile : selectedPeakFiles) {
				IDataInputEntry dataInputEntry = new DataInputEntry(selectedPeakFile);
				String groupName = getGroupName().trim();
				if(!groupName.isEmpty()) {
					dataInputEntry.setGroupName(groupName);
				}
				dataInputEntries.add(dataInputEntry);
			}
			addInputFiles(dataInputEntries);
		}
		update();
	}
}
