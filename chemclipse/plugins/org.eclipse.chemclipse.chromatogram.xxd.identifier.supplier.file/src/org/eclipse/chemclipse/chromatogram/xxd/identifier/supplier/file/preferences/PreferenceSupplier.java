/*******************************************************************************
 * Copyright (c) 2014, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Christoph Läubrich - make the default for addUnknownMzListTarget to false
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.preferences;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IIdentifierSettingsMSD;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.settings.ILibraryIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.settings.IUnknownSettings;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.settings.IdentifierSettings;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.settings.MassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.settings.MassSpectrumUnknownSettings;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.settings.PeakIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.settings.PeakUnknownSettingsCSD;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.settings.PeakUnknownSettingsMSD;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.settings.PeakUnknownSettingsWSD;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.settings.WaveSpectrumUnknownSettings;
import org.eclipse.chemclipse.model.identifier.DeltaCalculation;
import org.eclipse.chemclipse.model.identifier.IIdentifierSettings;
import org.eclipse.chemclipse.model.identifier.PenaltyCalculation;
import org.eclipse.chemclipse.model.support.CalculationType;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.chemclipse.support.util.FileListUtil;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final String POSTFIX_MSD = "MSD";
	public static final String POSTFIX_CSD = "CSD";
	public static final String POSTFIX_WSD = "WSD";
	//
	public static final float MIN_FACTOR = 0.0f;
	public static final float MAX_FACTOR = 100.0f;
	public static final int MIN_NUMBER_OF_TARGETS = 1;
	public static final int MAX_NUMBER_OF_TARGETS = 100;
	public static final int MIN_NUMBER_OF_MZ = 0;
	public static final int MAX_NUMBER_OF_MZ = Integer.MAX_VALUE;
	public static final int MIN_NUMBER_OF_WAVELENGTH = 0;
	public static final int MAX_NUMBER_OF_WAVELENGTH = Integer.MAX_VALUE;
	//
	public static final String P_MASS_SPECTRA_FILES = "massSpectraFiles";
	public static final String DEF_MASS_SPECTRA_FILES = "";
	public static final String P_MASS_SPECTRUM_COMPARATOR_ID = "massSpectrumComparatorId";
	public static final String DEF_MASS_SPECTRUM_COMPARATOR_ID = IIdentifierSettingsMSD.DEFAULT_COMPARATOR_ID;
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
	/*
	 * File
	 */
	public static final String P_LIMIT_MATCH_FACTOR_FILE = "limitMatchFactorFile";
	public static final float DEF_LIMIT_MATCH_FACTOR_FILE = 80.0f;
	public static final String P_MIN_MATCH_FACTOR = "minMatchFactor";
	public static final float DEF_MIN_MATCH_FACTOR = 80.0f;
	public static final String P_MIN_REVERSE_MATCH_FACTOR = "minReverseMatchFactor";
	public static final float DEF_MIN_REVERSE_MATCH_FACTOR = 80.0f;
	/*
	 * Unknown General
	 */
	public static final String P_LIMIT_MATCH_FACTOR_UNKNOWN = "limitMatchFactorUnknown";
	public static final float DEF_LIMIT_MATCH_FACTOR_UNKOWN = 80.0f;
	public static final String P_TARGET_NAME_UNKNOWN = "targetNameUnknown";
	public static final String DEF_TARGET_NAME_UNKNOWN = "Unknown";
	public static final String P_MATCH_QUALITY_UNKNOWN = "matchQualityUnknown";
	public static final float DEF_MATCH_QUALITY_UNKNOWN = 80.0f;
	public static final String P_INCLUDE_INTENSITY_PERCENT_UNKNOWN = "includeIntensityPercentUnknown";
	public static final boolean DEF_INCLUDE_INTENSITY_PERCENT_UNKNOWN = false;
	public static final String P_MARKER_START_UNKNOWN = "markerStartUnknown";
	public static final String DEF_MARKER_START_UNKNOWN = "[";
	public static final String P_MARKER_STOP_UNKNOWN = "markerStopUnknown";
	public static final String DEF_MARKER_STOP_UNKNOWN = "]";
	public static final String P_INCLUDE_RETENTION_TIME_UNKNOWN = "includeRetentionTimeUnknown";
	public static final boolean DEF_INCLUDE_RETENTION_TIME_UNKNOWN = true;
	public static final String P_INCLUDE_RETENTION_INDEX_UNKNOWN = "includeRetentionIndexUnknown";
	public static final boolean DEF_INCLUDE_RETENTION_INDEX_UNKNOWN = false;
	// MSD
	public static final String P_NUMBER_OF_MZ_UNKNOWN = "numberOfMzUnknown";
	public static final int DEF_NUMBER_OF_MZ_UNKNOWN = 5;
	// WSD
	public static final String P_NUMBER_OF_WAVELENGTH_UNKNOWN = "numberOfWavelengthUnknown";
	public static final int DEF_NUMBER_OF_WAVELENGTH_UNKNOWN = 5;
	/*
	 * Penalty calculation.
	 */
	public static final String P_DELTA_CALCULATION = "deltaCalculation";
	public static final String DEF_DELTA_CALCULATION = DeltaCalculation.NONE.name();
	public static final String P_DELTA_WINDOW = "deltaWindow";
	public static final float DEF_DELTA_WINDOW = 0.0f;
	public static final String P_PENALTY_CALCULATION = "penaltyCalculation";
	public static final String DEF_PENALTY_CALCULATION = PenaltyCalculation.NONE.name();
	public static final String P_PENALTY_WINDOW = "penaltyWindow";
	public static final float DEF_PENALTY_WINDOW = 0.0f;
	public static final String P_PENALTY_LEVEL_FACTOR = "penaltyLevelFactor";
	public static final float DEF_PENALTY_LEVEL_FACTOR = IIdentifierSettings.DEF_PENALTY_LEVEL_FACTOR;
	public static final String P_MAX_PENALTY = "maxPenalty";
	public static final float DEF_MAX_PENALTY = IIdentifierSettings.DEF_PENALTY_MATCH_FACTOR;
	//
	public static final String P_FILTER_PATH_IDENTIFIER_FILES = "filterPathIdentifierFiles";
	public static final String DEF_FILTER_PATH_IDENTIFIER_FILES = "";
	/*
	 * Combined Scan Identifier
	 */
	public static final String P_USE_NORMALIZED_SCAN = "useNormalizedScan";
	public static final boolean DEF_USE_NORMALIZED_SCAN = true;
	public static final String P_CALCULATION_TYPE = "calculationType";
	public static final String DEF_CALCULATION_TYPE = CalculationType.SUM.name();
	public static final String P_USE_PEAKS_INSTEAD_OF_SCANS = "usePeaksInsteadOfScans";
	public static final boolean DEF_USE_PEAKS_INSTEAD_OF_SCANS = false;
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
		defaultValues.put(P_LIMIT_MATCH_FACTOR_FILE, Float.toString(DEF_LIMIT_MATCH_FACTOR_FILE));
		defaultValues.put(P_MIN_MATCH_FACTOR, Float.toString(DEF_MIN_MATCH_FACTOR));
		defaultValues.put(P_MIN_REVERSE_MATCH_FACTOR, Float.toString(DEF_MIN_REVERSE_MATCH_FACTOR));
		//
		setUnknownValues(defaultValues, POSTFIX_MSD);
		setUnknownValues(defaultValues, POSTFIX_CSD);
		setUnknownValues(defaultValues, POSTFIX_WSD);
		//
		defaultValues.put(P_DELTA_CALCULATION, DEF_DELTA_CALCULATION);
		defaultValues.put(P_DELTA_WINDOW, Float.toString(DEF_DELTA_WINDOW));
		defaultValues.put(P_PENALTY_CALCULATION, DEF_PENALTY_CALCULATION);
		defaultValues.put(P_PENALTY_WINDOW, Float.toString(DEF_PENALTY_WINDOW));
		defaultValues.put(P_PENALTY_LEVEL_FACTOR, Float.toString(DEF_PENALTY_LEVEL_FACTOR));
		defaultValues.put(P_MAX_PENALTY, Float.toString(DEF_MAX_PENALTY));
		defaultValues.put(P_FILTER_PATH_IDENTIFIER_FILES, DEF_FILTER_PATH_IDENTIFIER_FILES);
		//
		defaultValues.put(P_USE_NORMALIZED_SCAN, Boolean.toString(DEF_USE_NORMALIZED_SCAN));
		defaultValues.put(P_CALCULATION_TYPE, DEF_CALCULATION_TYPE);
		defaultValues.put(P_USE_PEAKS_INSTEAD_OF_SCANS, Boolean.toString(DEF_USE_PEAKS_INSTEAD_OF_SCANS));
		//
		return defaultValues;
	}

	private void setUnknownValues(Map<String, String> defaultValues, String postfix) {

		defaultValues.put(P_LIMIT_MATCH_FACTOR_UNKNOWN + postfix, Float.toString(DEF_LIMIT_MATCH_FACTOR_UNKOWN));
		defaultValues.put(P_MATCH_QUALITY_UNKNOWN + postfix, Float.toString(DEF_MATCH_QUALITY_UNKNOWN));
		defaultValues.put(P_TARGET_NAME_UNKNOWN + postfix, DEF_TARGET_NAME_UNKNOWN);
		defaultValues.put(P_NUMBER_OF_MZ_UNKNOWN + postfix, Integer.toString(DEF_NUMBER_OF_MZ_UNKNOWN));
		defaultValues.put(P_NUMBER_OF_WAVELENGTH_UNKNOWN + postfix, Integer.toString(DEF_NUMBER_OF_WAVELENGTH_UNKNOWN));
		defaultValues.put(P_INCLUDE_INTENSITY_PERCENT_UNKNOWN + postfix, Boolean.toString(DEF_INCLUDE_INTENSITY_PERCENT_UNKNOWN));
		defaultValues.put(P_MARKER_START_UNKNOWN + postfix, DEF_MARKER_START_UNKNOWN);
		defaultValues.put(P_MARKER_STOP_UNKNOWN + postfix, DEF_MARKER_STOP_UNKNOWN);
		defaultValues.put(P_INCLUDE_RETENTION_TIME_UNKNOWN + postfix, Boolean.toString(DEF_INCLUDE_RETENTION_TIME_UNKNOWN));
		defaultValues.put(P_INCLUDE_RETENTION_INDEX_UNKNOWN + postfix, Boolean.toString(DEF_INCLUDE_RETENTION_INDEX_UNKNOWN));
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static MassSpectrumIdentifierSettings getMassSpectrumIdentifierSettings() {

		MassSpectrumIdentifierSettings settings = new MassSpectrumIdentifierSettings();
		initialize(settings);
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
		settings.setLimitMatchFactor(preferences.getFloat(P_LIMIT_MATCH_FACTOR_FILE, DEF_LIMIT_MATCH_FACTOR_FILE));
		settings.setMinMatchFactor(preferences.getFloat(P_MIN_MATCH_FACTOR, DEF_MIN_MATCH_FACTOR));
		settings.setMinReverseMatchFactor(preferences.getFloat(P_MIN_REVERSE_MATCH_FACTOR, DEF_MIN_REVERSE_MATCH_FACTOR));
		//
		settings.setDeltaCalculation(getDeltaCalculation());
		settings.setDeltaWindow(preferences.getFloat(P_DELTA_WINDOW, DEF_DELTA_WINDOW));
		settings.setPenaltyCalculation(getPenaltyCalculation());
		settings.setPenaltyWindow(preferences.getFloat(P_PENALTY_WINDOW, DEF_PENALTY_WINDOW));
		settings.setPenaltyLevelFactor(preferences.getFloat(P_PENALTY_LEVEL_FACTOR, DEF_PENALTY_LEVEL_FACTOR));
		settings.setMaxPenalty(preferences.getFloat(P_MAX_PENALTY, DEF_MAX_PENALTY));
		//
		return settings;
	}

	public static PeakUnknownSettingsMSD getPeakUnknownSettingsMSD() {

		IEclipsePreferences preferences = PreferenceSupplier.INSTANCE().getPreferences();
		//
		String postfix = POSTFIX_MSD;
		PeakUnknownSettingsMSD settings = new PeakUnknownSettingsMSD();
		settings.setMassSpectrumComparatorId("");
		initalizeUnknownSettings(settings, postfix);
		settings.setNumberOfMZ(preferences.getInt(P_NUMBER_OF_MZ_UNKNOWN + postfix, DEF_NUMBER_OF_MZ_UNKNOWN));
		//
		return settings;
	}

	public static PeakUnknownSettingsCSD getPeakUnknownSettingsCSD() {

		PeakUnknownSettingsCSD settings = new PeakUnknownSettingsCSD();
		initalizeUnknownSettings(settings, POSTFIX_CSD);
		//
		return settings;
	}

	public static PeakUnknownSettingsWSD getPeakUnknownSettingsWSD() {

		IEclipsePreferences preferences = PreferenceSupplier.INSTANCE().getPreferences();
		//
		String postfix = POSTFIX_WSD;
		PeakUnknownSettingsWSD settings = new PeakUnknownSettingsWSD();
		initalizeUnknownSettings(settings, postfix);
		settings.setNumberOfWavelengths(preferences.getInt(P_NUMBER_OF_WAVELENGTH_UNKNOWN + postfix, DEF_NUMBER_OF_WAVELENGTH_UNKNOWN));
		//
		return settings;
	}

	public static MassSpectrumUnknownSettings getMassSpectrumUnknownSettings() {

		IEclipsePreferences preferences = PreferenceSupplier.INSTANCE().getPreferences();
		//
		String postfix = POSTFIX_MSD;
		MassSpectrumUnknownSettings settings = new MassSpectrumUnknownSettings();
		settings.setMassSpectrumComparatorId("");
		initalizeUnknownSettings(settings, postfix);
		settings.setNumberOfMZ(preferences.getInt(P_NUMBER_OF_MZ_UNKNOWN + postfix, DEF_NUMBER_OF_MZ_UNKNOWN));
		//
		return settings;
	}

	public static WaveSpectrumUnknownSettings getWaveSpectrumUnknownSettings() {

		IEclipsePreferences preferences = PreferenceSupplier.INSTANCE().getPreferences();
		//
		String postfix = POSTFIX_WSD;
		WaveSpectrumUnknownSettings settings = new WaveSpectrumUnknownSettings();
		initalizeUnknownSettings(settings, postfix);
		settings.setNumberOfWavelengths(preferences.getInt(P_NUMBER_OF_WAVELENGTH_UNKNOWN + postfix, DEF_NUMBER_OF_WAVELENGTH_UNKNOWN));
		//
		return settings;
	}

	private static void initalizeUnknownSettings(IUnknownSettings settings, String postfix) {

		IEclipsePreferences preferences = PreferenceSupplier.INSTANCE().getPreferences();
		//
		settings.setLimitMatchFactor(preferences.getFloat(P_LIMIT_MATCH_FACTOR_UNKNOWN + postfix, DEF_LIMIT_MATCH_FACTOR_UNKOWN));
		settings.setTargetName(preferences.get(P_TARGET_NAME_UNKNOWN + postfix, DEF_TARGET_NAME_UNKNOWN));
		settings.setMatchQuality(preferences.getFloat(P_MATCH_QUALITY_UNKNOWN + postfix, DEF_MATCH_QUALITY_UNKNOWN));
		settings.setIncludeIntensityPercent(preferences.getBoolean(P_INCLUDE_INTENSITY_PERCENT_UNKNOWN + postfix, DEF_INCLUDE_INTENSITY_PERCENT_UNKNOWN));
		settings.setMarkerStart(preferences.get(P_MARKER_START_UNKNOWN + postfix, DEF_MARKER_START_UNKNOWN));
		settings.setMarkerStop(preferences.get(P_MARKER_STOP_UNKNOWN + postfix, DEF_MARKER_STOP_UNKNOWN));
		settings.setIncludeRetentionTime(preferences.getBoolean(P_INCLUDE_RETENTION_TIME_UNKNOWN + postfix, DEF_INCLUDE_RETENTION_TIME_UNKNOWN));
		settings.setIncludeRetentionIndex(preferences.getBoolean(P_INCLUDE_RETENTION_INDEX_UNKNOWN + postfix, DEF_INCLUDE_RETENTION_INDEX_UNKNOWN));
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

	public static String getFilterPathIdentifierFiles() {

		return getFilterPath(P_FILTER_PATH_IDENTIFIER_FILES, DEF_FILTER_PATH_IDENTIFIER_FILES);
	}

	public static void setFilterPathIdentifierFiles(String filterPath) {

		INSTANCE().put(P_FILTER_PATH_IDENTIFIER_FILES, filterPath);
	}

	public static IdentifierSettings getIdentifierSettings() {

		IdentifierSettings settings = new IdentifierSettings();
		settings.setUseNormalizedScan(isUseNormalizedScan());
		settings.setCalculationType(getCalculationType());
		settings.setUsePeaksInsteadOfScans(isUsePeaksInsteadOfScans());
		initialize(settings);
		//
		return settings;
	}

	public static boolean isUseNormalizedScan() {

		return INSTANCE().getBoolean(P_USE_NORMALIZED_SCAN, DEF_USE_NORMALIZED_SCAN);
	}

	public static boolean isUsePeaksInsteadOfScans() {

		return INSTANCE().getBoolean(P_USE_PEAKS_INSTEAD_OF_SCANS, DEF_USE_PEAKS_INSTEAD_OF_SCANS);
	}

	public static CalculationType getCalculationType() {

		try {
			IEclipsePreferences preferences = INSTANCE().getPreferences();
			return CalculationType.valueOf(preferences.get(P_CALCULATION_TYPE, DEF_CALCULATION_TYPE));
		} catch(Exception e) {
			return CalculationType.SUM;
		}
	}

	private static DeltaCalculation getDeltaCalculation() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		try {
			return DeltaCalculation.valueOf(preferences.get(P_DELTA_CALCULATION, DEF_DELTA_CALCULATION));
		} catch(Exception e) {
			return DeltaCalculation.NONE;
		}
	}

	private static PenaltyCalculation getPenaltyCalculation() {

		IEclipsePreferences eclipsePreferences = INSTANCE().getPreferences();
		String setting = eclipsePreferences.get(P_PENALTY_CALCULATION, DEF_PENALTY_CALCULATION);
		/*
		 * Backward compatibility, see:
		 * org.eclipse.chemclipse.model.identifier.IIdentifierSettings
		 * commit 58061db00e5a47107fc35255135e2f368bd75e32
		 * commit 2022/01 - improvement identifier settings
		 */
		if("RT".equals(setting)) {
			INSTANCE().put(P_PENALTY_CALCULATION, PenaltyCalculation.RETENTION_TIME_MS.name());
			return PenaltyCalculation.RETENTION_TIME_MS;
		} else if("RI".equals(setting)) {
			INSTANCE().put(P_PENALTY_CALCULATION, PenaltyCalculation.RETENTION_INDEX.name());
			return PenaltyCalculation.RETENTION_INDEX;
		} else if("RETENTION_TIME".equals(setting)) {
			INSTANCE().put(P_PENALTY_CALCULATION, PenaltyCalculation.RETENTION_TIME_MS.name());
			return PenaltyCalculation.RETENTION_TIME_MS;
		} else {
			try {
				return PenaltyCalculation.valueOf(setting);
			} catch(Exception e) {
				/*
				 * The option BOTH has been removed as it doesn't make sense.
				 */
				INSTANCE().put(P_PENALTY_CALCULATION, PenaltyCalculation.NONE.name());
				return PenaltyCalculation.NONE;
			}
		}
	}

	private static void initialize(ILibraryIdentifierSettings settings) {

		IEclipsePreferences preferences = PreferenceSupplier.INSTANCE().getPreferences();
		//
		settings.setMassSpectraFiles(preferences.get(P_MASS_SPECTRA_FILES, DEF_MASS_SPECTRA_FILES));
		settings.setUsePreOptimization(preferences.getBoolean(P_USE_PRE_OPTIMIZATION, DEF_USE_PRE_OPTIMIZATION));
		settings.setThresholdPreOptimization(preferences.getDouble(P_THRESHOLD_PRE_OPTIMIZATION, DEF_THRESHOLD_PRE_OPTIMIZATION));
		settings.setMassSpectrumComparatorId(preferences.get(P_MASS_SPECTRUM_COMPARATOR_ID, DEF_MASS_SPECTRUM_COMPARATOR_ID));
		settings.setNumberOfTargets(preferences.getInt(P_NUMBER_OF_TARGETS, DEF_NUMBER_OF_TARGETS));
		settings.setLimitMatchFactor(preferences.getFloat(P_LIMIT_MATCH_FACTOR_FILE, DEF_LIMIT_MATCH_FACTOR_FILE));
		settings.setMinMatchFactor(preferences.getFloat(P_MIN_MATCH_FACTOR, DEF_MIN_MATCH_FACTOR));
		settings.setMinReverseMatchFactor(preferences.getFloat(P_MIN_REVERSE_MATCH_FACTOR, DEF_MIN_REVERSE_MATCH_FACTOR));
		//
		settings.setDeltaCalculation(getDeltaCalculation());
		settings.setDeltaWindow(preferences.getFloat(P_DELTA_WINDOW, DEF_DELTA_WINDOW));
		settings.setPenaltyCalculation(getPenaltyCalculation());
		settings.setPenaltyWindow(preferences.getFloat(P_PENALTY_WINDOW, DEF_PENALTY_WINDOW));
		settings.setPenaltyLevelFactor(preferences.getFloat(P_PENALTY_LEVEL_FACTOR, DEF_PENALTY_LEVEL_FACTOR));
		settings.setMaxPenalty(preferences.getFloat(P_MAX_PENALTY, DEF_MAX_PENALTY));
	}

	private static String getFilterPath(String key, String def) {

		IEclipsePreferences eclipsePreferences = INSTANCE().getPreferences();
		return eclipsePreferences.get(key, def);
	}
}