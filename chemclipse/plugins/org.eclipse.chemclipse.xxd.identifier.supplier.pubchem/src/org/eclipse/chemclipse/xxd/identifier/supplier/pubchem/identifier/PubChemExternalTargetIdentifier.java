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
package org.eclipse.chemclipse.xxd.identifier.supplier.pubchem.identifier;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.identifier.targets.ITargetIdentifierSupplier;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.identifier.IIdentifierSettings;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.xxd.identifier.supplier.pubchem.rest.PowerUserGateway;

public class PubChemExternalTargetIdentifier implements ITargetIdentifierSupplier {

	private static final Logger logger = Logger.getLogger(PubChemExternalTargetIdentifier.class);
	private static final String PREFIX = "https://pubchem.ncbi.nlm.nih.gov/compound/";

	public PubChemExternalTargetIdentifier() {

	}

	@Override
	public String getId() {

		return "org.eclipse.chemclipse.xxd.identifier.supplier.pubchem.identifier";
	}

	@Override
	public String getDescription() {

		return "Click to open the corresponding PubChem database entry in a web browser.";
	}

	@Override
	public String getIdentifierName() {

		return "PubChem";
	}

	@Override
	public Class<? extends IIdentifierSettings> getSettingsClass() {

		return null;
	}

	@Override
	public URL getURL(ILibraryInformation libraryInformation) {

		List<Integer> cids = PowerUserGateway.getCIDS(libraryInformation);
		if(cids.isEmpty()) {
			return null;
		}
		int cid = cids.get(0);
		URL url = null;
		try {
			url = new URL(PREFIX + cid);
		} catch(MalformedURLException e) {
			logger.warn(e);
		}
		return url;
	}
}
