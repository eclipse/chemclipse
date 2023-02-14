/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.identifier.supplier.pubchem.rest;

import org.eclipse.chemclipse.model.identifier.ILibraryInformation;

import com.google.common.net.UrlEscapers;

public class PowerUserGateway {

	private static String compound = "https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/";

	public static String getBestCompound(ILibraryInformation libraryInformation) {

		String url = null;
		String smiles = libraryInformation.getSmiles().trim();
		if(!smiles.isEmpty()) {
			url = compound + "smiles/" + smiles;
		}
		if(url == null) {
			String inchi = libraryInformation.getInChI().trim();
			if(!inchi.isEmpty()) {
				url = compound + "inchi/" + inchi;
			}
		}
		if(url == null) {
			String inchiKey = libraryInformation.getInChIKey().trim();
			if(!inchiKey.isEmpty()) {
				url = compound + "inchikey/" + inchiKey;
			}
		}
		if(url == null) {
			String name = libraryInformation.getName().trim();
			if(!name.isEmpty() && !name.equals("Unknown")) {
				url = compound + "name/" + name;
			}
		}
		return UrlEscapers.urlFragmentEscaper().escape(url); // e.g. space and +
	}

	private PowerUserGateway() {

	}
}
