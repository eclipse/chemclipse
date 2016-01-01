/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.csd.ui.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;

public class ChromatogramInputEntriesWizard extends Wizard {

	private ChromatogramInputEntriesWizardPage inputEntriesPage;
	private List<String> selectedChromatograms;
	private String pageName;
	private String title;
	private String description;

	public ChromatogramInputEntriesWizard(String pageName, String title, String description) {
		super();
		setNeedsProgressMonitor(true);
		this.pageName = pageName;
		this.title = title;
		this.description = description;
	}

	@Override
	public boolean performFinish() {

		ISelection selection = inputEntriesPage.getSelection();
		IStructuredSelection structuredSelection = (IStructuredSelection)selection;
		selectedChromatograms = new ArrayList<String>();
		for(Object element : structuredSelection.toList()) {
			selectedChromatograms.add(element.toString());
		}
		return true;
	}

	/**
	 * Returns the selected chromatograms.
	 * 
	 * @return List<String>
	 */
	public List<String> getSelectedChromatograms() {

		return selectedChromatograms;
	}

	@Override
	public void addPages() {

		inputEntriesPage = new ChromatogramInputEntriesWizardPage(pageName, title, description);
		addPage(inputEntriesPage);
	}
}
