/*******************************************************************************
 * Copyright (c) 2017, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.support;

import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.model.identifier.IFlavorMarker;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;

public class LibraryInformationSupport {

	public boolean containsSearchText(ILibraryInformation libraryInformation, String searchText, boolean caseSensitive) {

		if(libraryInformation == null || searchText == null) {
			return false;
		} else {
			/*
			 * Searh Text
			 */
			searchText = caseSensitive ? searchText : searchText.toLowerCase();
			/*
			 * Name
			 */
			String name = libraryInformation.getName();
			name = caseSensitive ? name : name.toLowerCase();
			if(name.contains(searchText)) {
				return true;
			}
			/*
			 * Reference Identifier
			 */
			String referenceIdentifier = libraryInformation.getReferenceIdentifier();
			referenceIdentifier = caseSensitive ? referenceIdentifier : referenceIdentifier.toLowerCase();
			if(referenceIdentifier.contains(searchText)) {
				return true;
			}
			/*
			 * Formula
			 */
			String formula = libraryInformation.getFormula();
			formula = caseSensitive ? formula : formula.toLowerCase();
			if(formula.contains(searchText)) {
				return true;
			}
			/*
			 * SMILES
			 */
			String smiles = libraryInformation.getSmiles();
			smiles = caseSensitive ? smiles : smiles.toLowerCase();
			if(smiles.contains(searchText)) {
				return true;
			}
			/*
			 * InChI
			 */
			String inchi = libraryInformation.getInChI();
			inchi = caseSensitive ? inchi : inchi.toLowerCase();
			if(inchi.contains(searchText)) {
				return true;
			}
			/*
			 * InChI Key
			 */
			String inchiKey = libraryInformation.getInChIKey();
			inchiKey = caseSensitive ? inchiKey : inchiKey.toLowerCase();
			if(inchiKey.contains(searchText)) {
				return true;
			}
			/*
			 * CAS Numbers
			 */
			List<String> casNumbers = libraryInformation.getCasNumbers();
			for(String casNumber : casNumbers) {
				casNumber = caseSensitive ? casNumber : casNumber.toLowerCase();
				if(casNumber.contains(searchText)) {
					return true;
				}
			}
			/*
			 * Comments
			 */
			String comments = libraryInformation.getComments();
			comments = caseSensitive ? comments : comments.toLowerCase();
			if(comments.contains(searchText)) {
				return true;
			}
			/*
			 * Synonyms
			 */
			Set<String> synonyms = libraryInformation.getSynonyms();
			for(String synonym : synonyms) {
				synonym = caseSensitive ? synonym : synonym.toLowerCase();
				if(synonym.contains(searchText)) {
					return true;
				}
			}
			/*
			 * Flavor Marker
			 */
			for(IFlavorMarker flavorMarker : libraryInformation.getFlavorMarkers()) {
				/*
				 * Odor
				 */
				String odor = flavorMarker.getOdor();
				odor = caseSensitive ? odor : odor.toLowerCase();
				if(odor.contains(searchText)) {
					return true;
				}
				/*
				 * Matrix
				 */
				String matrix = flavorMarker.getMatrix();
				matrix = caseSensitive ? matrix : matrix.toLowerCase();
				if(matrix.contains(searchText)) {
					return true;
				}
				/*
				 * Solvent
				 */
				String solvent = flavorMarker.getOdor();
				solvent = caseSensitive ? solvent : solvent.toLowerCase();
				if(solvent.contains(searchText)) {
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