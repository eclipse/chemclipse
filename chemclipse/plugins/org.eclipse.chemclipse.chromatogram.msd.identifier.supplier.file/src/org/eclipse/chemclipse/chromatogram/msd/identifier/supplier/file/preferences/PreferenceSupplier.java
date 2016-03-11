/*******************************************************************************
 * Copyright (c) 2014, 2016 Dr. Philip Wenig.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.preferences;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.Activator;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.VendorMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.VendorPeakIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.IFileIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.IVendorMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.IVendorPeakIdentifierSettings;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.chemclipse.support.util.FileListUtil;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final String P_MASS_SPECTRA_FILES = "massSpectraFiles";
	public static final String DEF_MASS_SPECTRA_FILES = "";
	public static final String P_MASS_SPECTRUM_COMPARATOR_ID = "massSpectrumComparatorId";
	public static final String DEF_MASS_SPECTRUM_COMPARATOR_ID = "org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.incos";
	//
	public static final String P_USE_PRE_OPTIMIZATION = "usePreOptimization";
	public static final Boolean DEF_USE_PRE_OPTIMIZATION = true;
	public static final String P_THRESHOLD_PRE_OPTIMIZATION = "thresholdPreOptimization";
	public static final double DEF_THRESHOLD_PRE_OPTIMIZATION = 0.1d;
	public static final double MIN_THRESHOLD_PRE_OPTIMIZATION = 0.0d; //
	public static final double MAX_THRESHOLD_PRE_OPTIMIZATION = 1.0d; //
	//
	public static final String P_NUMBER_OF_TARGETS = "numberOfTargets";
	public static final int DEF_NUMBER_OF_TARGETS = 3;
	public static final int MIN_NUMBER_OF_TARGETS = 1;
	public static final int MAX_NUMBER_OF_TARGETS = 100;
	public static final String P_MIN_MATCH_FACTOR = "minMatchFactor";
	public static final float DEF_MIN_MATCH_FACTOR = 80.0f;
	public static final String P_MIN_REVERSE_MATCH_FACTOR = "minReverseMatchFactor";
	public static final float DEF_MIN_REVERSE_MATCH_FACTOR = 80.0f;
	//
	public static final String P_ADD_UNKNOWN_MZ_LIST_TARGET = "addUnknownMzListTarget";
	public static final Boolean DEF_ADD_UNKNOWN_MZ_LIST_TARGET = true;
	/*
	 * RI / RT penalty calculation.
	 */
	public static final String P_PENALTY_CALCULATION = "forceMatchQualityPenaltyCalculation";
	public static final String DEF_PENALTY_CALCULATION = IFileIdentifierSettings.PENALTY_CALCULATION_NONE;
	public static final String P_RETENTION_INDEX_WINDOW = "retentionIndexWindow";
	public static final float DEF_RETENTION_INDEX_WINDOW = 20.0f;
	public static final float MIN_RETENTION_INDEX_WINDOW = 10.0f;
	public static final float MAX_RETENTION_INDEX_WINDOW = 20.0f;
	public static final String P_RETENTION_TIME_WINDOW = "retentionTimeWindow";
	public static final float DEF_RETENTION_TIME_WINDOW = 0.2f;
	public static final float MIN_RETENTION_TIME_WINDOW = 0.001f;
	public static final float MAX_RETENTION_TIME_WINDOW = 1.0f;
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
		defaultValues.put(P_MASS_SPECTRA_FILES, DEF_MASS_SPECTRA_FILES);
		defaultValues.put(P_MASS_SPECTRUM_COMPARATOR_ID, DEF_MASS_SPECTRUM_COMPARATOR_ID);
		defaultValues.put(P_USE_PRE_OPTIMIZATION, Boolean.toString(DEF_USE_PRE_OPTIMIZATION));
		defaultValues.put(P_THRESHOLD_PRE_OPTIMIZATION, Double.toString(DEF_THRESHOLD_PRE_OPTIMIZATION));
		defaultValues.put(P_NUMBER_OF_TARGETS, Integer.toString(DEF_NUMBER_OF_TARGETS));
		defaultValues.put(P_MIN_MATCH_FACTOR, Float.toString(DEF_MIN_MATCH_FACTOR));
		defaultValues.put(P_MIN_REVERSE_MATCH_FACTOR, Float.toString(DEF_MIN_REVERSE_MATCH_FACTOR));
		defaultValues.put(P_ADD_UNKNOWN_MZ_LIST_TARGET, Boolean.toString(DEF_ADD_UNKNOWN_MZ_LIST_TARGET));
		defaultValues.put(P_PENALTY_CALCULATION, DEF_PENALTY_CALCULATION);
		defaultValues.put(P_RETENTION_TIME_WINDOW, Float.toString(DEF_RETENTION_TIME_WINDOW));
		defaultValues.put(P_RETENTION_INDEX_WINDOW, Float.toString(DEF_RETENTION_INDEX_WINDOW));
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static IVendorMassSpectrumIdentifierSettings getMassSpectrumIdentifierSettings() {

		FileListUtil fileListUtil = new FileListUtil();
		IEclipsePreferences preferences = PreferenceSupplier.INSTANCE().getPreferences();
		IVendorMassSpectrumIdentifierSettings settings = new VendorMassSpectrumIdentifierSettings();
		settings.setMassSpectraFiles(fileListUtil.getFiles(preferences.get(P_MASS_SPECTRA_FILES, DEF_MASS_SPECTRA_FILES)));
		settings.setMassSpectrumComparatorId(preferences.get(P_MASS_SPECTRUM_COMPARATOR_ID, DEF_MASS_SPECTRUM_COMPARATOR_ID));
		settings.setUsePreOptimization(preferences.getBoolean(P_USE_PRE_OPTIMIZATION, DEF_USE_PRE_OPTIMIZATION));
		settings.setThresholdPreOptimization(preferences.getDouble(P_THRESHOLD_PRE_OPTIMIZATION, DEF_THRESHOLD_PRE_OPTIMIZATION));
		settings.setNumberOfTargets(preferences.getInt(P_NUMBER_OF_TARGETS, DEF_NUMBER_OF_TARGETS));
		settings.setMinMatchFactor(preferences.getFloat(P_MIN_MATCH_FACTOR, DEF_MIN_MATCH_FACTOR));
		settings.setMinReverseMatchFactor(preferences.getFloat(P_MIN_REVERSE_MATCH_FACTOR, DEF_MIN_REVERSE_MATCH_FACTOR));
		settings.setAddUnknownMzListTarget(preferences.getBoolean(P_ADD_UNKNOWN_MZ_LIST_TARGET, DEF_ADD_UNKNOWN_MZ_LIST_TARGET));
		return settings;
	}

	public static IVendorPeakIdentifierSettings getPeakIdentifierSettings() {

		FileListUtil fileListUtil = new FileListUtil();
		IEclipsePreferences preferences = PreferenceSupplier.INSTANCE().getPreferences();
		IVendorPeakIdentifierSettings settings = new VendorPeakIdentifierSettings();
		settings.setMassSpectraFiles(fileListUtil.getFiles(preferences.get(P_MASS_SPECTRA_FILES, DEF_MASS_SPECTRA_FILES)));
		settings.setMassSpectrumComparatorId(preferences.get(P_MASS_SPECTRUM_COMPARATOR_ID, DEF_MASS_SPECTRUM_COMPARATOR_ID));
		settings.setUsePreOptimization(preferences.getBoolean(P_USE_PRE_OPTIMIZATION, DEF_USE_PRE_OPTIMIZATION));
		settings.setThresholdPreOptimization(preferences.getDouble(P_THRESHOLD_PRE_OPTIMIZATION, DEF_THRESHOLD_PRE_OPTIMIZATION));
		settings.setNumberOfTargets(preferences.getInt(P_NUMBER_OF_TARGETS, DEF_NUMBER_OF_TARGETS));
		settings.setMinMatchFactor(preferences.getFloat(P_MIN_MATCH_FACTOR, DEF_MIN_MATCH_FACTOR));
		settings.setMinReverseMatchFactor(preferences.getFloat(P_MIN_REVERSE_MATCH_FACTOR, DEF_MIN_REVERSE_MATCH_FACTOR));
		settings.setAddUnknownMzListTarget(preferences.getBoolean(P_ADD_UNKNOWN_MZ_LIST_TARGET, DEF_ADD_UNKNOWN_MZ_LIST_TARGET));
		return settings;
	}

	public static List<String> getMassSpectraFiles() {

		FileListUtil fileListUtil = new FileListUtil();
		IEclipsePreferences preferences = PreferenceSupplier.INSTANCE().getPreferences();
		return fileListUtil.getFiles(preferences.get(P_MASS_SPECTRA_FILES, DEF_MASS_SPECTRA_FILES));
	}
}
