/*******************************************************************************
 * Copyright (c) 2014, 2016 Dr. Philip Wenig.
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

import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.model.core.ILibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.IMassSpectrumTarget;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class MassSpectrumListFilter extends ViewerFilter {

	private String searchText;
	private boolean caseSensitive;

	public void setSearchText(String searchText, boolean caseSensitive) {

		this.searchText = ".*" + searchText + ".*";
		this.caseSensitive = caseSensitive;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {

		/*
		 * Pre-Condition
		 */
		if(searchText == null || searchText.equals("")) {
			return true;
		}
		/*
		 * ILibraryMassSpectrum
		 */
		if(element instanceof ILibraryMassSpectrum) {
			//
			ILibraryMassSpectrum libraryMassSpectrum = (ILibraryMassSpectrum)element;
			ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
			if(matchLibraryInformation(libraryInformation)) {
				return true;
			}
		} else if(element instanceof IScanMSD) {
			//
			IScanMSD massSpectrum = (IScanMSD)element;
			List<IMassSpectrumTarget> massSpectrumTargets = massSpectrum.getTargets();
			for(IMassSpectrumTarget massSpectrumTarget : massSpectrumTargets) {
				/*
				 * Check if the target is an identification entry.
				 */
				if(massSpectrumTarget instanceof IIdentificationTarget) {
					IIdentificationTarget identificationEntry = (IIdentificationTarget)massSpectrumTarget;
					ILibraryInformation libraryInformation = identificationEntry.getLibraryInformation();
					if(matchLibraryInformation(libraryInformation)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean matchLibraryInformation(ILibraryInformation libraryInformation) {

		/*
		 * Search the name.
		 */
		String searchText = this.searchText;
		String name = libraryInformation.getName();
		String referenceIdentifier = libraryInformation.getReferenceIdentifier();
		String formula = libraryInformation.getFormula();
		//
		if(!caseSensitive) {
			searchText = searchText.toLowerCase();
			name = name.toLowerCase();
			referenceIdentifier = referenceIdentifier.toLowerCase();
			formula = formula.toLowerCase();
		}
		/*
		 * Name
		 */
		if(name.matches(searchText)) {
			return true;
		}
		/*
		 * Reference Identifier
		 */
		if(referenceIdentifier.matches(searchText)) {
			return true;
		}
		/*
		 * Formula
		 */
		if(formula.matches(searchText)) {
			return true;
		}
		/*
		 * Search the synonyms.
		 */
		Set<String> synonyms = libraryInformation.getSynonyms();
		for(String synonym : synonyms) {
			/*
			 * Pre-check
			 */
			if(!caseSensitive) {
				synonym = synonym.toLowerCase();
			}
			/*
			 * Search
			 */
			if(synonym.matches(searchText)) {
				return true;
			}
		}
		//
		return false;
	}
}
