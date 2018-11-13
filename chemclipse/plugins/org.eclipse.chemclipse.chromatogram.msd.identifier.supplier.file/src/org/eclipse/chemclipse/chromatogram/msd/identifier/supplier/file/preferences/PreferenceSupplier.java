/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IIdentifierSettingsMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.Activator;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.MassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.PeakIdentifierSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.chemclipse.support.util.FileListUtil;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

public class PreferenceSupplier implements IPreferenceSupplier {

	private static final Logger logger = Logger.getLogger(PreferenceSupplier.class);
	//
	public static final String P_MASS_SPECTRA_FILES = "massSpectraFiles";
	public static final String DEF_MASS_SPECTRA_FILES = "";
	public static final String P_MASS_SPECTRUM_COMPARATOR_ID = "massSpectrumComparatorId";
	public static final String DEF_MASS_SPECTRUM_COMPARATOR_ID = "org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.incos";
	//
	public static final String P_USE_PRE_OPTIMIZATION = "usePreOptimization";
	public static final Boolean DEF_USE_PRE_OPTIMIZATION = true;
	public static final String P_THRESHOLD_PRE_OPTIMIZATION = "thresholdPreOptimization";
	public static final double DEF_THRESHOLD_PRE_OPTIMIZATION = 0.1d;
	public static final double MIN_THRESHOLD_PRE_OPTIMIZATION = 0.0d; // no restriction
	public static final double MAX_THRESHOLD_PRE_OPTIMIZATION = 1.0d; // hardest restriction
	//
	public static final String P_NUMBER_OF_TARGETS = "numberOfTargets";
	public static final int DEF_NUMBER_OF_TARGETS = 3;
	public static final int MIN_NUMBER_OF_TARGETS = 1;
	public static final int MAX_NUMBER_OF_TARGETS = 100;
	//
	public static final String P_MIN_MATCH_FACTOR = "minMatchFactor";
	public static final float DEF_MIN_MATCH_FACTOR = 80.0f;
	public static final float MIN_MIN_MATCH_FACTOR = 0.0f;
	public static final float MAX_MIN_MATCH_FACTOR = 100.0f;
	//
	public static final String P_MIN_REVERSE_MATCH_FACTOR = "minReverseMatchFactor";
	public static final float DEF_MIN_REVERSE_MATCH_FACTOR = 80.0f;
	public static final float MIN_MIN_REVERSE_MATCH_FACTOR = 0.0f;
	public static final float MAX_MIN_REVERSE_MATCH_FACTOR = 100.0f;
	//
	public static final String P_ADD_UNKNOWN_MZ_LIST_TARGET = "addUnknownMzListTarget";
	public static final Boolean DEF_ADD_UNKNOWN_MZ_LIST_TARGET = true;
	/*
	 * RI / RT penalty calculation.
	 */
	public static final String P_PENALTY_CALCULATION = "penaltyCalculation";
	public static final String DEF_PENALTY_CALCULATION = IIdentifierSettingsMSD.PENALTY_CALCULATION_NONE;
	//
	public static final String P_PENALTY_CALCULATION_LEVEL_FACTOR = "penaltyCalculationLevelFactor";
	public static final String P_MAX_PENALTY = "maxPenalty";
	//
	public static final String P_RETENTION_TIME_WINDOW = "retentionTimeWindow";
	public static final int DEF_RETENTION_TIME_WINDOW = 12000; // = 0.2 minutes
	public static final int MIN_RETENTION_TIME_WINDOW = 60; // = 0.001 minutes
	public static final int MAX_RETENTION_TIME_WINDOW = 60000; // = 1.0 minutes;
	//
	public static final String P_RETENTION_INDEX_WINDOW = "retentionIndexWindow";
	public static final float DEF_RETENTION_INDEX_WINDOW = 20.0f;
	public static final float MIN_RETENTION_INDEX_WINDOW = 10.0f;
	public static final float MAX_RETENTION_INDEX_WINDOW = 20.0f;
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
		defaultValues.put(P_PENALTY_CALCULATION_LEVEL_FACTOR, Float.toString(IIdentifierSettingsMSD.DEF_PENALTY_CALCULATION_LEVEL_FACTOR));
		defaultValues.put(P_MAX_PENALTY, Float.toString(IComparisonResult.DEF_MAX_PENALTY));
		defaultValues.put(P_RETENTION_TIME_WINDOW, Integer.toString(DEF_RETENTION_TIME_WINDOW));
		defaultValues.put(P_RETENTION_INDEX_WINDOW, Float.toString(DEF_RETENTION_INDEX_WINDOW));
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static MassSpectrumIdentifierSettings getMassSpectrumIdentifierSettings() {

		IEclipsePreferences preferences = PreferenceSupplier.INSTANCE().getPreferences();
		MassSpectrumIdentifierSettings settings = new MassSpectrumIdentifierSettings();
		settings.setMassSpectraFiles(preferences.get(P_MASS_SPECTRA_FILES, DEF_MASS_SPECTRA_FILES));
		settings.setUsePreOptimization(preferences.getBoolean(P_USE_PRE_OPTIMIZATION, DEF_USE_PRE_OPTIMIZATION));
		settings.setThresholdPreOptimization(preferences.getDouble(P_THRESHOLD_PRE_OPTIMIZATION, DEF_THRESHOLD_PRE_OPTIMIZATION));
		settings.setMassSpectrumComparatorId(preferences.get(P_MASS_SPECTRUM_COMPARATOR_ID, DEF_MASS_SPECTRUM_COMPARATOR_ID));
		settings.setNumberOfTargets(preferences.getInt(P_NUMBER_OF_TARGETS, DEF_NUMBER_OF_TARGETS));
		settings.setMinMatchFactor(preferences.getFloat(P_MIN_MATCH_FACTOR, DEF_MIN_MATCH_FACTOR));
		settings.setMinReverseMatchFactor(preferences.getFloat(P_MIN_REVERSE_MATCH_FACTOR, DEF_MIN_REVERSE_MATCH_FACTOR));
		settings.setAddUnknownMzListTarget(preferences.getBoolean(P_ADD_UNKNOWN_MZ_LIST_TARGET, DEF_ADD_UNKNOWN_MZ_LIST_TARGET));
		//
		settings.setPenaltyCalculation(preferences.get(P_PENALTY_CALCULATION, DEF_PENALTY_CALCULATION));
		settings.setPenaltyCalculationLevelFactor(preferences.getFloat(P_PENALTY_CALCULATION_LEVEL_FACTOR, IIdentifierSettingsMSD.DEF_PENALTY_CALCULATION_LEVEL_FACTOR));
		settings.setMaxPenalty(preferences.getFloat(P_MAX_PENALTY, IComparisonResult.DEF_MAX_PENALTY));
		settings.setRetentionTimeWindow(preferences.getInt(P_RETENTION_TIME_WINDOW, DEF_RETENTION_TIME_WINDOW));
		settings.setRetentionIndexWindow(preferences.getFloat(P_RETENTION_INDEX_WINDOW, DEF_RETENTION_INDEX_WINDOW));
		//
		return settings;
	}

	public static PeakIdentifierSettings getPeakIdentifierSettings() {

		IEclipsePreferences preferences = PreferenceSupplier.INSTANCE().getPreferences();
		PeakIdentifierSettings settings = new PeakIdentifierSettings();
		settings.setMassSpectraFiles(preferences.get(P_MASS_SPECTRA_FILES, DEF_MASS_SPECTRA_FILES));
		settings.setUsePreOptimization(preferences.getBoolean(P_USE_PRE_OPTIMIZATION, DEF_USE_PRE_OPTIMIZATION));
		settings.setThresholdPreOptimization(preferences.getDouble(P_THRESHOLD_PRE_OPTIMIZATION, DEF_THRESHOLD_PRE_OPTIMIZATION));
		settings.setMassSpectrumComparatorId(preferences.get(P_MASS_SPECTRUM_COMPARATOR_ID, DEF_MASS_SPECTRUM_COMPARATOR_ID));
		settings.setNumberOfTargets(preferences.getInt(P_NUMBER_OF_TARGETS, DEF_NUMBER_OF_TARGETS));
		settings.setMinMatchFactor(preferences.getFloat(P_MIN_MATCH_FACTOR, DEF_MIN_MATCH_FACTOR));
		settings.setMinReverseMatchFactor(preferences.getFloat(P_MIN_REVERSE_MATCH_FACTOR, DEF_MIN_REVERSE_MATCH_FACTOR));
		settings.setAddUnknownMzListTarget(preferences.getBoolean(P_ADD_UNKNOWN_MZ_LIST_TARGET, DEF_ADD_UNKNOWN_MZ_LIST_TARGET));
		//
		settings.setPenaltyCalculation(preferences.get(P_PENALTY_CALCULATION, DEF_PENALTY_CALCULATION));
		settings.setPenaltyCalculationLevelFactor(preferences.getFloat(P_PENALTY_CALCULATION_LEVEL_FACTOR, IIdentifierSettingsMSD.DEF_PENALTY_CALCULATION_LEVEL_FACTOR));
		settings.setMaxPenalty(preferences.getFloat(P_MAX_PENALTY, IComparisonResult.DEF_MAX_PENALTY));
		settings.setRetentionTimeWindow(preferences.getInt(P_RETENTION_TIME_WINDOW, DEF_RETENTION_TIME_WINDOW));
		settings.setRetentionIndexWindow(preferences.getFloat(P_RETENTION_INDEX_WINDOW, DEF_RETENTION_INDEX_WINDOW));
		//
		return settings;
	}

	public static List<String> getMassSpectraFiles() {

		FileListUtil fileListUtil = new FileListUtil();
		IEclipsePreferences preferences = PreferenceSupplier.INSTANCE().getPreferences();
		return fileListUtil.getFiles(preferences.get(P_MASS_SPECTRA_FILES, DEF_MASS_SPECTRA_FILES));
	}

	public static void setMassSpectraFiles(List<String> massSpectraFiles) {

		try {
			FileListUtil fileListUtil = new FileListUtil();
			IEclipsePreferences preferences = PreferenceSupplier.INSTANCE().getPreferences();
			String items[] = massSpectraFiles.toArray(new String[massSpectraFiles.size()]);
			preferences.put(P_MASS_SPECTRA_FILES, fileListUtil.createList(items));
			preferences.flush();
		} catch(BackingStoreException e) {
			logger.warn(e);
		}
	}
}
