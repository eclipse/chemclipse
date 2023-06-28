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
package org.eclipse.chemclipse.xxd.identifier.supplier.wikidata.identifier;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.chemclipse.chromatogram.xxd.identifier.targets.ITargetIdentifierSupplier;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.cas.CasSupport;
import org.eclipse.chemclipse.model.identifier.IIdentifierSettings;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.xxd.identifier.supplier.wikidata.query.QueryEntity;

public class WikidataExternalTargetIdentifier implements ITargetIdentifierSupplier {

	private static final Logger logger = Logger.getLogger(WikidataExternalTargetIdentifier.class);

	@Override
	public String getId() {

		return "org.eclipse.chemclipse.xxd.identifier.supplier.wikidata.identifier";
	}

	@Override
	public String getDescription() {

		return "Click to open the corresponding Wikidata entry in a web browser.";
	}

	@Override
	public String getIdentifierName() {

		return "Wikidata";
	}

	@Override
	public Class<? extends IIdentifierSettings> getSettingsClass() {

		return null;
	}

	@Override
	public URL getURL(ILibraryInformation libraryInformation) {

		String url = null;
		String cas = libraryInformation.getCasNumber().trim();
		if(cas != null && !cas.isEmpty() && !CasSupport.CAS_DEFAULT.equals(cas)) {
			url = QueryEntity.fromCAS(cas);
		}
		if(url == null) {
			String name = libraryInformation.getName().trim();
			if(!name.isEmpty()) {
				url = QueryEntity.fromName(name);
			}
		}
		if(url != null) {
			try {
				return new URL(url);
			} catch(MalformedURLException e) {
				logger.warn(e);
			}
		}
		return null;
	}
}
