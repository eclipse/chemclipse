/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.support;

import java.util.Set;

import org.eclipse.chemclipse.model.identifier.ILibraryInformation;

public class LibraryInformationSupport {

	public boolean matchSearchText(ILibraryInformation libraryInformation, String searchText, boolean caseSensitive) {

		if(libraryInformation == null || searchText == null) {
			return false;
		} else {
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
}
