/*******************************************************************************
 * Copyright (c) 2013, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - use ScanMSD in favor of CombinedMassSpectrum
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.model.Activator;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.ScanMSD;
import org.eclipse.chemclipse.msd.model.support.CalculationType;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final int MIN_TRACES = 1;
	public static final int MAX_TRACES = Integer.MAX_VALUE;
	//
	public static final String P_SUBTRACT_MASS_SPECTRUM = "subtractMassSpectrum";
	public static final String DEF_SUBTRACT_MASS_SPECTRUM = "18:200;28:1000;32:500";
	public static final String P_USE_NOMINAL_MZ = "useNominalMZ";
	public static final boolean DEF_USE_NOMINAL_MZ = true;
	public static final String P_USE_NORMALIZED_SCAN = "useNormalizedScan";
	public static final boolean DEF_USE_NORMALIZED_SCAN = true;
	public static final String P_CALCULATION_TYPE = "calculationType";
	public static final String DEF_CALCULATION_TYPE = CalculationType.SUM.name();
	public static final String P_USE_PEAKS_INSTEAD_OF_SCANS = "usePeaksInsteadOfScans";
	public static final boolean DEF_USE_PEAKS_INSTEAD_OF_SCANS = false;
	public static final String P_COPY_TRACES_CLIPBOARD = "copyTracesClipboard";
	public static final int DEF_COPY_TRACES_CLIPBOARD = 5;
	//
	private static final String DELIMITER_ION_ABUNDANCE = ":";
	private static final String DELIMITER_IONS = ";";
	/*
	 * It is the mass spectrum that is used only by the session.
	 */
	private static IScanMSD sessionSubtractMassSpectrum;
	//
	private static IPreferenceSupplier preferenceSupplier;

	public static IPreferenceSupplier INSTANCE() {

		if(preferenceSupplier == null) {
			preferenceSupplier = new PreferenceSupplier();
		}
		return preferenceSupplier;
	}

	@Override
	public IScopeContext getScopeContext() {

		return InstanceScope.INSTANCE;
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public Map<String, String> getDefaultValues() {

		Map<String, String> defaultValues = new HashMap<String, String>();
		defaultValues.put(P_SUBTRACT_MASS_SPECTRUM, DEF_SUBTRACT_MASS_SPECTRUM);
		defaultValues.put(P_USE_NOMINAL_MZ, Boolean.toString(DEF_USE_NOMINAL_MZ));
		defaultValues.put(P_USE_NORMALIZED_SCAN, Boolean.toString(DEF_USE_NORMALIZED_SCAN));
		defaultValues.put(P_CALCULATION_TYPE, DEF_CALCULATION_TYPE);
		defaultValues.put(P_USE_PEAKS_INSTEAD_OF_SCANS, Boolean.toString(DEF_USE_PEAKS_INSTEAD_OF_SCANS));
		defaultValues.put(P_COPY_TRACES_CLIPBOARD, Integer.toString(DEF_COPY_TRACES_CLIPBOARD));
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static void loadSessionSubtractMassSpectrum() {

		IScanMSD subtractMassSpectrum = getSubtractMassSpectrum();
		setSessionSubtractMassSpectrum(subtractMassSpectrum);
	}

	public static void storeSessionSubtractMassSpectrum() {

		setSubtractMassSpectrum(sessionSubtractMassSpectrum);
	}

	/**
	 * Returns the session subtract mass spectrum, which is used by the filter.
	 * 
	 * @return {@link IScanMSD}
	 */
	public static IScanMSD getSessionSubtractMassSpectrum() {

		return sessionSubtractMassSpectrum;
	}

	/**
	 * Sets the session mass spectrum, which is used by the filter.
	 * 
	 * @param subtractMassSpectrum
	 */
	public static void setSessionSubtractMassSpectrum(IScanMSD subtractMassSpectrum) {

		sessionSubtractMassSpectrum = subtractMassSpectrum;
	}

	public static boolean isUseNominalMZ() {

		return INSTANCE().getBoolean(P_USE_NOMINAL_MZ, DEF_USE_NOMINAL_MZ);
	}

	public static void setUseNominalMZ(boolean useNominalMZ) {

		INSTANCE().putBoolean(P_USE_NOMINAL_MZ, useNominalMZ);
	}

	public static boolean isUseNormalizedScan() {

		return INSTANCE().getBoolean(P_USE_NORMALIZED_SCAN, DEF_USE_NORMALIZED_SCAN);
	}

	public static void setUseNormalizedScan(boolean useNormalizedScan) {

		INSTANCE().putBoolean(P_USE_NORMALIZED_SCAN, useNormalizedScan);
	}

	public static String getSessionSubtractMassSpectrumAsString() {

		return INSTANCE().get(P_SUBTRACT_MASS_SPECTRUM, DEF_SUBTRACT_MASS_SPECTRUM);
	}

	public static void setCalculationType(CalculationType calculationType) {

		INSTANCE().put(P_CALCULATION_TYPE, calculationType.name());
	}

	public static CalculationType getCalculationType() {

		try {
			return CalculationType.valueOf(INSTANCE().get(P_CALCULATION_TYPE, DEF_CALCULATION_TYPE));
		} catch(Exception e) {
			return CalculationType.SUM;
		}
	}

	public static void setUsePeaksInsteadOfScans(boolean usePeaks) {

		INSTANCE().putBoolean(P_USE_PEAKS_INSTEAD_OF_SCANS, usePeaks);
	}

	public static boolean isUsePeaksInsteadOfScans() {

		return INSTANCE().getBoolean(P_USE_PEAKS_INSTEAD_OF_SCANS, DEF_USE_PEAKS_INSTEAD_OF_SCANS);
	}

	public static String getMassSpectrum(IScanMSD massSpectrum) {

		StringBuilder builder = new StringBuilder();
		if(massSpectrum != null) {
			for(IIon ion : massSpectrum.getIons()) {
				builder.append(ion.getIon());
				builder.append(DELIMITER_ION_ABUNDANCE);
				builder.append(ion.getAbundance());
				builder.append(DELIMITER_IONS);
			}
		}
		return builder.toString();
	}

	public static IScanMSD getMassSpectrum(String value) {

		if(value != null && !value.isEmpty()) {
			ScanMSD scanMSD = new ScanMSD();
			String[] ions = value.split(DELIMITER_IONS);
			for(String ion : ions) {
				String[] fragment = ion.split(DELIMITER_ION_ABUNDANCE);
				if(fragment.length == 2) {
					/*
					 * Add the mass fragment
					 */
					double mz = Double.parseDouble(fragment[0]);
					float abundance = Float.parseFloat(fragment[1]);
					try {
						IIon subtractIon = new Ion(mz, abundance);
						scanMSD.addIon(subtractIon);
					} catch(AbundanceLimitExceededException e) {
						logger.warn(e);
					} catch(IonLimitExceededException e) {
						logger.warn(e);
					}
				}
			}
			return scanMSD;
		}
		return null;
	}

	public static void setCopyTracesClipboard(int number) {

		INSTANCE().putInteger(P_COPY_TRACES_CLIPBOARD, number);
	}

	public static int getCopyTracesClipboard() {

		return INSTANCE().getInteger(P_COPY_TRACES_CLIPBOARD, DEF_COPY_TRACES_CLIPBOARD);
	}

	private static IScanMSD getSubtractMassSpectrum() {

		return getMassSpectrum(INSTANCE().get(P_SUBTRACT_MASS_SPECTRUM, DEF_SUBTRACT_MASS_SPECTRUM));
	}

	private static void setSubtractMassSpectrum(IScanMSD massSpectrum) {

		INSTANCE().put(P_SUBTRACT_MASS_SPECTRUM, getMassSpectrum(massSpectrum));
	}
}