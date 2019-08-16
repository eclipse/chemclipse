/*******************************************************************************
 * Copyright (c) 2011, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Jan Holy - initial API and implementation
 * Christoph Läubrich - use generic selection page
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.RawFileSelectionWizardPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;

/**
 * @author Dr. Philip Wenig
 *
 */
public class ChromatogramMSDInputFilesWizard extends Wizard {

	private RawFileSelectionWizardPage scanEntriesPage;
	private List<String> selectedChromatogramFiles;

	public ChromatogramMSDInputFilesWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {

		scanEntriesPage = new RawFileSelectionWizardPage(DataType.MSD, "Input Peak Files", null);
		addPage(scanEntriesPage);
	}

	/**
	 * Returns the selected chromatograms.
	 *
	 * @return List<String>
	 */
	public List<String> getSelectedChromatogramFiles() {

		return selectedChromatogramFiles;
	}

	@Override
	public boolean performFinish() {

		ISelection selection = scanEntriesPage.getSelection();
		IStructuredSelection structuredSelection = (IStructuredSelection)selection;
		selectedChromatogramFiles = new ArrayList<String>();
		for(Object element : structuredSelection.toList()) {
			selectedChromatogramFiles.add(element.toString());
		}
		return true;
	}
}
