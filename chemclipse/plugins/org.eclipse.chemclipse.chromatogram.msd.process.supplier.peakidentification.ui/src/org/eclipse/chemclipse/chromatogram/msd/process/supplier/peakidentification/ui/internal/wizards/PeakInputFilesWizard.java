/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.ui.internal.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;

/**
 * @author Dr. Philip Wenig
 * 
 */
public class PeakInputFilesWizard extends Wizard {

	private PeakInputFilesWizardPage inputEntriesPage;
	private List<String> selectedPeakFiles;

	public PeakInputFilesWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	@Override
	public boolean performFinish() {

		ISelection selection = inputEntriesPage.getSelection();
		IStructuredSelection structuredSelection = (IStructuredSelection)selection;
		selectedPeakFiles = new ArrayList<String>();
		for(Object element : structuredSelection.toList()) {
			selectedPeakFiles.add(element.toString());
		}
		return true;
	}

	/**
	 * Returns the selected chromatograms.
	 * 
	 * @return List<String>
	 */
	public List<String> getSelectedPeakFiles() {

		return selectedPeakFiles;
	}

	@Override
	public void addPages() {

		inputEntriesPage = new PeakInputFilesWizardPage("Input Peak Files");
		addPage(inputEntriesPage);
	}
}
