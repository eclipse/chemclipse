/*******************************************************************************
 * Copyright (c) 2013, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.model.Activator;
import org.eclipse.chemclipse.msd.model.core.ICombinedMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.implementation.CombinedMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final String P_SUBTRACT_MASS_SPECTRUM = "subtractMassSpectrum";
	public static final String DEF_SUBTRACT_MASS_SPECTRUM = "18:200;28:1000;32:500";
	public static final String P_USE_NOMINAL_MASSES = "useNominalMasses";
	public static final String P_USE_NORMALIZE = "useNormalize";
	public static final boolean DEF_USE_NOMINAL_MASSES = true;
	public static final boolean DEF_USE_NORMALIZE = false;
	//
	private static final String DELIMITER_ION_ABUNDANCE = ":";
	private static final String DELIMITER_IONS = ";";
	/*
	 * It is the mass spectrum that is used only by the session.
	 */
	private static IScanMSD sessionMassSpectrum;
	//
	private static final Logger logger = Logger.getLogger(PreferenceSupplier.class);
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
		defaultValues.put(P_USE_NOMINAL_MASSES, Boolean.toString(DEF_USE_NOMINAL_MASSES));
		defaultValues.put(P_USE_NORMALIZE, Boolean.toString(DEF_USE_NORMALIZE));
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	/**
	 * Loads the stored mass spectrum and sets it as the
	 * session subtract mass spectrum.
	 */
	public static void initialize() {

		IScanMSD massSpectrum = getSubtractMassSpectrum();
		setSessionSubtractMassSpectrum(massSpectrum);
	}

	/**
	 * Returns the session subtract mass spectrum, which is used by the filter.
	 * 
	 * @return {@link IScanMSD}
	 */
	public static IScanMSD getSessionSubtractMassSpectrum() {

		return sessionMassSpectrum;
	}

	/**
	 * Sets the session mass spectrum, wich is used by the filter.
	 * 
	 * @param normalizedMassSpectrum
	 */
	public static void setSessionSubtractMassSpectrum(IScanMSD normalizedMassSpectrum) {

		sessionMassSpectrum = normalizedMassSpectrum;
	}

	/**
	 * Returns the stored mass spectrum that shall be subtracted.
	 * 
	 * @return {@link IScanMSD}
	 */
	public static IScanMSD getSubtractMassSpectrum() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		String value = preferences.get(P_SUBTRACT_MASS_SPECTRUM, DEF_SUBTRACT_MASS_SPECTRUM);
		if(value == null || value.equals("")) {
			return null;
		} else {
			ICombinedMassSpectrum subtractMassSpectrum = new CombinedMassSpectrum();
			//
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
						subtractMassSpectrum.addIon(subtractIon);
					} catch(AbundanceLimitExceededException e) {
						logger.warn(e);
					} catch(IonLimitExceededException e) {
						logger.warn(e);
					}
				}
			}
			/*
			 * Returns the parsed mass spectrum.
			 */
			return subtractMassSpectrum;
		}
	}

	/**
	 * Sets and stores the mass spectrum that shall be subtracted.
	 * If subtractedMassSpectrum is null, the stored ms will be cleared.
	 * 
	 * @param subtractMassSpectrum
	 */
	public static void setSubtractMassSpectrum(IScanMSD subtractMassSpectrum) {

		/*
		 * If the mass spectrum is null, clear the stored ms.
		 */
		String value = "";
		if(subtractMassSpectrum != null) {
			StringBuilder builder = new StringBuilder();
			for(IIon ion : subtractMassSpectrum.getIons()) {
				builder.append(ion.getIon());
				builder.append(DELIMITER_ION_ABUNDANCE);
				builder.append(ion.getAbundance());
				builder.append(DELIMITER_IONS);
			}
			value = builder.toString();
		}
		IEclipsePreferences preferences = INSTANCE().getPreferences();
		preferences.put(P_SUBTRACT_MASS_SPECTRUM, value);
	}

	public static boolean isUseNominalMasses() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getBoolean(P_USE_NOMINAL_MASSES, DEF_USE_NOMINAL_MASSES);
	}

	public static boolean isUseNormalize() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getBoolean(P_USE_NORMALIZE, DEF_USE_NORMALIZE);
	}
}
