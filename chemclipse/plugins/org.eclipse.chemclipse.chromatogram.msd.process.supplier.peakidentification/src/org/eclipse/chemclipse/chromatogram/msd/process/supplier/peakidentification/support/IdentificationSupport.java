/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.support;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.IPeakIntegrationEntry;
import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.PeakIdentifierMSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.NoIdentifierAvailableException;
import org.eclipse.chemclipse.model.identifier.core.ISupplier;

public class IdentificationSupport {

	private static final Logger logger = Logger.getLogger(IdentificationSupport.class);
	public static final String NOT_AVAILABLE = "n.a.";

	public String[] getIdentifierNames(String[] identifierIds) {

		int size = identifierIds.length;
		String[] processorNames = new String[size];
		for(int index = 0; index < size; index++) {
			processorNames[index] = getIdentifierName(identifierIds[index]);
		}
		return processorNames;
	}

	public String getIdentifierName(IPeakIntegrationEntry entry) {

		return getIdentifierName(entry.getProcessorId());
	}

	public String getIdentifierName(String identifierId) {

		String processorName = NOT_AVAILABLE;
		try {
			ISupplier peakIdentifierSupplier = PeakIdentifierMSD.getPeakIdentifierSupport().getIdentifierSupplier(identifierId);
			processorName = peakIdentifierSupplier.getIdentifierName();
		} catch(NoIdentifierAvailableException e) {
			logger.warn(e);
		}
		return processorName;
	}

	public String[] getPluginIds() {

		List<String> ids;
		String[] pluginIds = {NOT_AVAILABLE};
		try {
			ids = PeakIdentifierMSD.getPeakIdentifierSupport().getAvailableIdentifierIds();
			pluginIds = ids.toArray(new String[ids.size()]);
		} catch(NoIdentifierAvailableException e) {
			logger.warn(e);
		}
		return pluginIds;
	}

	public String getIdentifierId(String name) {

		String[] identifierIds = getPluginIds();
		String[] identifierNames = getIdentifierNames(identifierIds);
		int index = 0;
		for(String entry : identifierNames) {
			if(entry.equals(name)) {
				return identifierIds[index];
			}
			index++;
		}
		return "";
	}
}
