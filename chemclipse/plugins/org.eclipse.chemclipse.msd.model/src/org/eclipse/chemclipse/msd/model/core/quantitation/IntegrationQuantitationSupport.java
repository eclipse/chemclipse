/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.quantitation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IIntegrationEntryMSD;

public class IntegrationQuantitationSupport {

	private static final Logger logger = Logger.getLogger(IntegrationQuantitationSupport.class);
	private IPeak peak;
	private Map<Double, Double> integratedIons;

	public IntegrationQuantitationSupport(IPeak peak) {
		this.peak = peak;
		integratedIons = getListOfIntegratedIons();
	}

	/**
	 * Validates if the integration entries (TIC) of the peak matches the quantitation entry (TIC).
	 * 
	 * @return boolean
	 */
	public boolean validateTIC() {

		/**
		 * In case of TIC, there is only one integration entry.
		 * AbstractIon.TIC_ION
		 */
		if(isTheTotalSignalIntegrated()) {
			return true;
		} else {
			logger.warn("Quantitation: The peak integration entry doesn't match: " + peak);
		}
		return false;
	}

	/**
	 * Validates if the integration entries (XIC) of the peak matches the quantitation entries (XIC).
	 * 
	 * @param selectedQuantitationIons
	 * @return boolean
	 */
	public boolean validateXIC(List<Double> selectedQuantitationIons) {

		/*
		 * If it's a TIC integration, its ok too.
		 */
		if(isTheTotalSignalIntegrated()) {
			return true;
		} else if(integratedIons.keySet().containsAll(selectedQuantitationIons)) {
			return true;
		} else {
			StringBuilder builder = new StringBuilder();
			builder.append("Quantitation: The peak integration entries do not match quantitation entries: ");
			builder.append(peak);
			builder.append(" Integrated Ions: ");
			builder.append(integratedIons);
			builder.append(" Selected Quantitation Ions: ");
			builder.append(selectedQuantitationIons);
			logger.warn(builder.toString());
		}
		return false;
	}

	/**
	 * Checks if only the total signal is integrated.
	 * 
	 * @return boolean
	 */
	public boolean isTheTotalSignalIntegrated() {

		if(integratedIons.size() == 1 && integratedIons.containsKey(AbstractIon.TIC_ION)) {
			return true;
		}
		return false;
	}

	/**
	 * If only the total signal is integrated, the TIC value will be returned.
	 * Else, the integration ion value will be returned.
	 * 
	 * @param ion
	 * @return double
	 */
	public double getIntegrationArea(double ion) {

		Double value;
		if(isTheTotalSignalIntegrated()) {
			value = integratedIons.get(AbstractIon.TIC_ION);
		} else {
			value = integratedIons.get(ion);
		}
		/*
		 * Check null values.
		 */
		return (value == null) ? 0.0d : value;
	}

	/*
	 * Check the integrated ions to avoid problems with
	 * wrong integrated area and percentage m/z values.
	 */
	private Map<Double, Double> getListOfIntegratedIons() {

		Map<Double, Double> integratedIons = new HashMap<Double, Double>();
		for(IIntegrationEntry entry : peak.getIntegrationEntries()) {
			if(entry instanceof IIntegrationEntryMSD) {
				IIntegrationEntryMSD integrationEntry = (IIntegrationEntryMSD)entry;
				/*
				 * KEY = Ion
				 * VALUE = Integrated Area
				 */
				double key = integrationEntry.getIon();
				double value = integrationEntry.getIntegratedArea();
				integratedIons.put(key, value);
			}
		}
		return integratedIons;
	}
}
