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
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.peaks.IPeakIntegratorSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.peaks.PeakIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.NoIntegratorAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;

public class IntegrationSupport {

	private static final Logger logger = Logger.getLogger(IdentificationSupport.class);
	public static final String NOT_AVAILABLE = "n.a.";

	public String[] getIntegratorNames(String[] integratorIds) {

		int size = integratorIds.length;
		String[] processorNames = new String[size];
		for(int index = 0; index < size; index++) {
			processorNames[index] = getIntegratorName(integratorIds[index]);
		}
		return processorNames;
	}

	public String getIntegratorName(IPeakIntegrationEntry entry) {

		return getIntegratorName(entry.getProcessorId());
	}

	public String getIntegratorName(String integratorId) {

		String processorName = NOT_AVAILABLE;
		try {
			IPeakIntegratorSupplier integratorSupplier = PeakIntegrator.getPeakIntegratorSupport().getIntegratorSupplier(integratorId);
			processorName = integratorSupplier.getIntegratorName();
		} catch(NoIntegratorAvailableException e) {
			logger.warn(e);
		}
		return processorName;
	}

	public String[] getPluginIds() {

		List<String> ids;
		String[] pluginIds = {NOT_AVAILABLE};
		try {
			ids = PeakIntegrator.getPeakIntegratorSupport().getAvailableIntegratorIds();
			pluginIds = ids.toArray(new String[ids.size()]);
		} catch(NoIntegratorAvailableException e) {
			logger.warn(e);
		}
		return pluginIds;
	}

	public String getIntegratorId(String name) {

		String[] integratorIds = getPluginIds();
		String[] integratorNames = getIntegratorNames(integratorIds);
		int index = 0;
		for(String entry : integratorNames) {
			if(entry.equals(name)) {
				return integratorIds[index];
			}
			index++;
		}
		return "";
	}
}
