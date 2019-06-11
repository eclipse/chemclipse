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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.DataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.InputEntriesWizard;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.InputWizardSettings;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.InputWizardSettings.InputDataType;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class PeakFilesInputPageWizard extends DataInputPageWizard {

	private Shell shell = Display.getDefault().getActiveShell();

	public PeakFilesInputPageWizard(String pageName) {
		super(pageName);
		setTitle("Peak Input Files");
		setDescription("This wizard lets you select peak input files and set bulk group name.");
	}

	@Override
	protected void addFiles() {

		InputWizardSettings inputWizardSettings = new InputWizardSettings(InputDataType.MSD_PEAKS);
		inputWizardSettings.setTitle("Peak Input Files");
		inputWizardSettings.setDescription("This wizard lets you select several peak input files.");
		inputWizardSettings.setPathPreferences(PreferenceSupplier.INSTANCE().getPreferences(), PreferenceSupplier.N_INPUT_FILE);
		//
		InputEntriesWizard inputWizard = new InputEntriesWizard(inputWizardSettings);
		BatchProcessWizardDialog wizardDialog = new BatchProcessWizardDialog(shell, inputWizard);
		wizardDialog.create();
		//
		if(wizardDialog.open() == Window.OK) {
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
