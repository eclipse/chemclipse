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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.jface.wizard.Wizard;

public class DataInputFromPeakFilesWizard extends Wizard {

	private DataInputFromPeakFilesWizardPage inputEntriesPage;

	public DataInputFromPeakFilesWizard() {
		super();
	}

	@Override
	public void addPages() {

		inputEntriesPage = new DataInputFromPeakFilesWizardPage("Input Peak Files");
		addPage(inputEntriesPage);
	}

	/**
	 * Returns the selected chromatograms.
	 *
	 * @return List<String>
	 */
	public List<IDataInputEntry> getSelectedPeakFiles() {

		return inputEntriesPage.getDataInputEntries();
	}

	@Override
	public boolean performFinish() {

		return true;
	}
}
