/*******************************************************************************
 * Copyright (c) 2017, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Christoph Läubrich - change to new Wizard API
 * Philip Wenig - get rid of JavaFX
 * Matthias Mailänder - adapted for MALDI
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.DataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.Activator;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.InputEntriesWizard;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.InputWizardSettings;

public class MALDIFilesWizardPage extends DataInputPageWizard {

	public MALDIFilesWizardPage() {

		super("DataInputFiles");
		setTitle("MALDI-TOF MS Input Files");
		setDescription("This wizard lets you select MALDI-TOF MS input files and set bulk group name.");
	}

	@Override
	protected void addFiles() {

		InputWizardSettings inputWizardSettings = InputWizardSettings.create(Activator.getDefault().getPreferenceStore(), PreferenceSupplier.N_INPUT_FILE, DataType.MALDI);
		inputWizardSettings.setTitle("MALDI-TOF MS Input Files");
		inputWizardSettings.setDescription("This wizard lets you select several mass spectra input files.");
		//
		List<IDataInputEntry> dataInputEntries = new ArrayList<>();
		for(File file : InputEntriesWizard.openWizard(getShell(), inputWizardSettings).keySet()) {
			dataInputEntries.add(new DataInputEntry(file.getAbsolutePath()));
		}
		//
		addInputFiles(dataInputEntries);
		update();
	}
}