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
package org.eclipse.chemclipse.model.quantitation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.ISignal;

public class QuantitationSupport {

	private static final Logger logger = Logger.getLogger(QuantitationSupport.class);
	//
	private IPeak peak;
	private Map<Double, Double> integratedSignals;

	public QuantitationSupport(IPeak peak) {
		this.peak = peak;
		integratedSignals = calculateIntegratedSignals();
	}

	public boolean validateTIC() {

		if(isTotalSignalIntegrated()) {
			return true;
		} else {
			String info = (peak != null) ? peak.toString() : "--";
			logger.warn("Quantitation: The peak integration entry doesn't match: " + info);
		}
		return false;
	}

	/**
	 * Validates if the integration entries (XIC) of the peak matches the quantitation entries (XIC).
	 * 
	 * @param selectedSignals
	 * @return boolean
	 */
	public boolean validateXIC(List<Double> selectedSignals) {

		/*
		 * If it's a TIC integration, its ok too.
		 */
		if(isTotalSignalIntegrated()) {
			return true;
		} else if(integratedSignals.keySet().containsAll(selectedSignals)) {
			return true;
		} else {
			StringBuilder builder = new StringBuilder();
			builder.append("Quantitation: The peak integration entries do not match quantitation entries: ");
			builder.append((peak != null) ? peak : "--");
			builder.append(" Integrated Signals: ");
			builder.append(integratedSignals);
			builder.append(" Selected Quantitation Signals: ");
			builder.append(selectedSignals);
			logger.warn(builder.toString());
		}
		return false;
	}

	/**
	 * Checks if only the total signal is integrated.
	 * 
	 * @return boolean
	 */
	public boolean isTotalSignalIntegrated() {

		if(integratedSignals.size() == 1 && integratedSignals.containsKey(ISignal.TOTAL_INTENSITY)) {
			return true;
		}
		return false;
	}

	/**
	 * If only the total signal is integrated, the TIC value will be returned.
	 * Else, the integration signal value will be returned.
	 * 
	 * @param signal
	 * @return double
	 */
	public double getIntegrationArea(double signal) {

		Double value;
		if(isTotalSignalIntegrated()) {
			value = integratedSignals.get(ISignal.TOTAL_INTENSITY);
		} else {
			value = integratedSignals.get(signal);
		}
		/*
		 * Check null values.
		 */
		return (value == null) ? 0.0d : value;
	}

	/*
	 * Check the integrated ions to avoid problems with
	 * wrong integrated area and percentage signal (m/z, wavelength) values.
	 */
	private Map<Double, Double> calculateIntegratedSignals() {

		Map<Double, Double> integratedIons = new HashMap<Double, Double>();
		if(peak != null) {
			for(IIntegrationEntry integrationEntry : peak.getIntegrationEntries()) {
				/*
				 * KEY = Signal
				 * VALUE = Integrated Area
				 */
				integratedIons.put(integrationEntry.getSignal(), integrationEntry.getIntegratedArea());
			}
		}
		//
		return integratedIons;
	}
}