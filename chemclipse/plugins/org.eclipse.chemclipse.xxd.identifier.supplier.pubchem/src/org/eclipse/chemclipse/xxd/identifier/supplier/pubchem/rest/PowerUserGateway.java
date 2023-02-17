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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;

import com.google.common.net.UrlEscapers;

public class PowerUserGateway {

	private static final Logger logger = Logger.getLogger(PowerUserGateway.class);
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

	public static String getCanonicalSMILES(ILibraryInformation libraryInformation) {

		String smiles = null;
		String url = getBestCompound(libraryInformation) + "/property/CanonicalSMILES/TXT";
		try {
			smiles = IOUtils.toString(new URL(url), StandardCharsets.UTF_8);
			smiles = smiles.trim();
		} catch(FileNotFoundException e) {
			// 404
		} catch(IOException e) {
			logger.warn(e);
		}
		return smiles;
	}

	public static List<Integer> getCIDS(ILibraryInformation libraryInformation) {

		List<Integer> cids = new ArrayList<>();
		String url = getBestCompound(libraryInformation) + "/cids/TXT";
		try {
			String output = IOUtils.toString(new URL(url), StandardCharsets.UTF_8);
			String[] lines = output.split(System.lineSeparator());
			for(String line : lines) {
				cids.add(Integer.parseInt(line));
			}
		} catch(FileNotFoundException e) {
			// 404
		} catch(IOException e) {
			logger.warn(e);
		}
		return cids;
	}

	private PowerUserGateway() {

	}
}
