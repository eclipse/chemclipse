/*******************************************************************************
 * Copyright (c) 2017, 2021 Lablicate GmbH.
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

	public boolean containsSearchText(ILibraryInformation libraryInformation, String searchText, boolean caseSensitive) {

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
			if(name.contains(searchText)) {
				return true;
			}
			/*
			 * Reference Identifier
			 */
			if(referenceIdentifier.contains(searchText)) {
				return true;
			}
			/*
			 * Formula
			 */
			if(formula.contains(searchText)) {
				return true;
			}
			/*
			 * SMILES
			 */
			if(smiles.contains(searchText)) {
				return true;
			}
			/*
			 * InChI
			 */
			if(inchi.contains(searchText)) {
				return true;
			}
			/*
			 * CAS
			 */
			if(casNumber.contains(searchText)) {
				return true;
			}
			/*
			 * Comments
			 */
			if(comments.contains(searchText)) {
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
				if(synonym.contains(searchText)) {
					return true;
				}
			}
			//
			return false;
		}
	}

	public void extractNameAndReferenceIdentifier(String name, ILibraryInformation libraryInformation, String referenceIdentifierMarker, String referenceIdentifierPrefix) {

		if(name != null && libraryInformation != null) {
			boolean setNameTraditionally = true;
			if(referenceIdentifierMarker != null && !referenceIdentifierMarker.equals("")) {
				if(name.contains(referenceIdentifierMarker)) {
					String[] values = name.split(referenceIdentifierMarker);
					if(values.length >= 2) {
						/*
						 * Extract the reference identifier.
						 */
						setNameTraditionally = false;
						libraryInformation.setName(values[0].trim());
						//
						StringBuilder builder = new StringBuilder();
						if(referenceIdentifierPrefix != null) {
							builder.append(referenceIdentifierPrefix);
						}
						int size = values.length;
						for(int i = 1; i < size; i++) {
							builder.append(values[i]);
							if(i < size - 1) {
								builder.append(" ");
							}
						}
						libraryInformation.setReferenceIdentifier(builder.toString().trim());
					}
				}
			}
			//
			if(setNameTraditionally) {
				libraryInformation.setName(name);
			}
		}
	}
}