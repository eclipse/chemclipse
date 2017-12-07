/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.provider;

import java.util.Set;

import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class TargetListFilter extends ViewerFilter {

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
		//
		if(element instanceof IIdentificationTarget) {
			IIdentificationTarget target = (IIdentificationTarget)element;
			return matchLibraryInformation(target.getLibraryInformation());
		}
		//
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
		String smiles = libraryInformation.getSmiles();
		String inchi = libraryInformation.getInChI();
		String casNumber = libraryInformation.getCasNumber();
		String comments = libraryInformation.getComments();
		//
		if(!caseSensitive) {
			searchText = searchText.toLowerCase();
			name = name.toLowerCase();
			referenceIdentifier = referenceIdentifier.toLowerCase();
			formula = formula.toLowerCase();
			casNumber = casNumber.toLowerCase();
			smiles = smiles.toLowerCase();
			inchi = inchi.toLowerCase();
			comments = comments.toLowerCase();
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
		 * SMILES
		 */
		if(smiles.matches(searchText)) {
			return true;
		}
		/*
		 * InChI
		 */
		if(inchi.matches(searchText)) {
			return true;
		}
		/*
		 * CAS
		 */
		if(casNumber.matches(searchText)) {
			return true;
		}
		/*
		 * Comments
		 */
		if(comments.matches(searchText)) {
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
