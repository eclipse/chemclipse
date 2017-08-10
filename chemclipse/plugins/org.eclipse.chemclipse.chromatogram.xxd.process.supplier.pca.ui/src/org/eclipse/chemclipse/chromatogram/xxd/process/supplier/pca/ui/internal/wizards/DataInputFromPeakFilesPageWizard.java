/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
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

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.DataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
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

		PeakInputFilesWizard inputWizard = new PeakInputFilesWizard();
		BatchProcessWizardDialog wizardDialog = new BatchProcessWizardDialog(Display.getCurrent().getActiveShell(), inputWizard);
		wizardDialog.create();
		int returnCode = wizardDialog.open();
		if(returnCode == Window.OK) {
			List<String> selectedPeakFiles = inputWizard.getSelectedPeakFiles();
			for(String selectedPeakFile : selectedPeakFiles) {
				IDataInputEntry dataInputEntry = new DataInputEntry(selectedPeakFile);
				String groupName = getGroupName().trim();
				if(!groupName.isEmpty()) {
					dataInputEntry.setGroupName(groupName);
				}
				getDataInputEntries().add(dataInputEntry);
			}
		}
		updata();
	}
}
