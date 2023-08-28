/*******************************************************************************
 * Copyright (c) 2014, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.internal.provider;

import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.support.LibraryInformationSupport;
import org.eclipse.chemclipse.msd.model.core.ILibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class MassSpectrumListFilter extends ViewerFilter {

	private String searchText;
	private boolean caseSensitive;
	private LibraryInformationSupport libraryInformationSupport;

	public MassSpectrumListFilter() {

		libraryInformationSupport = new LibraryInformationSupport();
	}

	public void setSearchText(String searchText, boolean caseSensitive) {

		this.searchText = searchText;
		this.caseSensitive = caseSensitive;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {

		return matchElement(element);
	}

	public boolean matchElement(Object element) {

		/*
		 * Pre-Condition
		 */
		if(searchText == null || searchText.equals("")) {
			return true;
		}
		/*
		 * ILibraryMassSpectrum
		 */
		if(element instanceof ILibraryMassSpectrum libraryMassSpectrum) {
			//
			ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
			if(libraryInformationSupport.containsSearchText(libraryInformation, searchText, caseSensitive)) {
				return true;
			}
		} else if(element instanceof IScanMSD massSpectrum) {
			//
			for(IIdentificationTarget massSpectrumTarget : massSpectrum.getTargets()) {
				IIdentificationTarget identificationEntry = (IIdentificationTarget)massSpectrumTarget;
				ILibraryInformation libraryInformation = identificationEntry.getLibraryInformation();
				if(libraryInformationSupport.containsSearchText(libraryInformation, searchText, caseSensitive)) {
					return true;
				}
			}
		}
		return false;
	}
}