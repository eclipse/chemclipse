/*******************************************************************************
 * Copyright (c) 2010, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.Activator;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.settings.IOnsiteSettings;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.settings.PeakDetectorSettings;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	/*
	 * AMDIS Path
	 */
	public static final String WINDOWS_AMDIS = "C:\\Programs\\NIST\\AMDIS32\\AMDIS32$.exe";
	public static final String WINE_AMDIS_LINUX = "drive_c/Programs/NIST/AMDIS32/AMDIS32$.exe";
	public static final String WINE_AMDIS_MAC = "drive_c/NIST/AMDIS32/AMDIS32$.exe";
	public static final String WINE_AMDIS_UNIX = "drive_c/Programs/NIST/AMDIS32/AMDIS32$.exe";
	//
	public static final String WINDOWS_AMDIS_TMP_PATH = "C:\\tmp";
	public static final String WINE_AMDIS_TMP_PATH_LINUX = "drive_c/tmp";
	public static final String WINE_AMDIS_TMP_PATH_MAC = "drive_c/tmp";
	public static final String WINE_AMDIS_TMP_PATH_UNIX = "drive_c/tmp";
	//
	public static final String P_MAC_WINE_BINARY = "macWineBinary";
	public static final String DEF_MAC_WINE_BINARY = "/Applications/Wine.app";
	//
	public static final String P_AMDIS_APPLICATION = "amdisApplication";
	public static final String DEF_AMDIS_APPLICATION = WINDOWS_AMDIS;
	public static final String P_AMDIS_TMP_PATH = "amdisTmpPath";
	public static final String DEF_AMDIS_TMP_PATH = WINDOWS_AMDIS_TMP_PATH;
	/*
	 * AMDIS settings
	 */
	public static final String P_LOW_MZ_AUTO = "lowMzAuto";
	public static final boolean DEF_LOW_MZ_AUTO = true;
	public static final String P_START_MZ = "startMz";
	public static final int DEF_START_MZ = 35;
	public static final String P_HIGH_MZ_AUTO = "highMzAuto";
	public static final boolean DEF_HIGH_MZ_AUTO = true;
	public static final String P_STOP_MZ = "stopMz";
	public static final int DEF_STOP_MZ = 600;
	public static final String P_OMIT_MZ = "omitMz";
	public static final boolean DEF_OMIT_MZ = false;
	public static final String P_OMITED_MZ = "omitedMz";
	public static final String DEF_OMITED_MZ = "0 18 28";
	//
	public static final String P_USE_SOLVENT_TAILING = "useSolventTailing";
	public static final boolean DEF_USE_SOLVENT_TAILING = true;
	public static final String P_SOLVENT_TAILING_MZ = "solventTailingMz";
	public static final int DEF_SOLVENT_TAILING_MZ = IOnsiteSettings.VALUE_SOLVENT_TAILING_MZ;
	public static final String P_USE_COLUMN_BLEED = "useColumnBleed";
	public static final boolean DEF_USE_COLUMN_BLEED = true;
	public static final String P_COLUMN_BLEED_MZ = "columnBleedMz";
	public static final int DEF_COLUMN_BLEED_MZ = IOnsiteSettings.VALUE_COLUMN_BLEED_MZ;
	//
	public static final String P_THRESHOLD = "threshold";
	public static final String DEF_THRESHOLD = IOnsiteSettings.VALUE_THRESHOLD_MEDIUM;
	public static final String P_PEAK_WIDTH = "peakWidth";
	public static final int DEF_PEAK_WIDTH = IOnsiteSettings.VALUE_DEF_PEAK_WIDTH;
	//
	public static final String P_ADJACENT_PEAK_SUBTRACTION = "adjacentPeakSubtraction";
	public static final String DEF_ADJACENT_PEAK_SUBTRACTION = IOnsiteSettings.VALUE_ADJACENT_PEAK_SUBTRACTION_NONE;
	public static final String P_RESOLUTION = "resolution";
	public static final String DEF_RESOLUTION = IOnsiteSettings.VALUE_RESOLUTION_MEDIUM;
	public static final String P_SENSITIVITY = "sensitivity";
	public static final String DEF_SENSITIVITY = IOnsiteSettings.VALUE_SENSITIVITY_MEDIUM;
	public static final String P_SHAPE_REQUIREMENTS = "shapeRequirements";
	public static final String DEF_SHAPE_REQUIREMENTS = IOnsiteSettings.VALUE_SHAPE_REQUIREMENTS_HIGH;
	/*
	 * Extra settings
	 */
	public static final String P_MIN_SN_RATIO = "minSignalToNoiseRatio";
	public static final float DEF_MIN_SN_RATIO = 0.0f; // 0 = all peaks will be added
	public static final String P_MIN_LEADING = "minLeading";
	public static final float DEF_MIN_LEADING = 0.1f;
	public static final String P_MAX_LEADING = "maxLeading";
	public static final float DEF_MAX_LEADING = 2.0f;
	public static final String P_MIN_TAILING = "minTailing";
	public static final float DEF_MIN_TAILING = 0.1f;
	public static final String P_MAX_TAILING = "maxTailing";
	public static final float DEF_MAX_TAILING = 2.0f;
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
		if(OperatingSystemUtils.isLinux()) {
			defaultValues.put(P_AMDIS_APPLICATION, WINE_AMDIS_LINUX);
			defaultValues.put(P_AMDIS_TMP_PATH, WINE_AMDIS_TMP_PATH_LINUX);
		} else if(OperatingSystemUtils.isMac()) {
			defaultValues.put(P_AMDIS_APPLICATION, WINE_AMDIS_MAC);
			defaultValues.put(P_AMDIS_TMP_PATH, WINE_AMDIS_TMP_PATH_MAC);
		} else if(OperatingSystemUtils.isUnix()) {
			defaultValues.put(P_AMDIS_APPLICATION, WINE_AMDIS_UNIX);
			defaultValues.put(P_AMDIS_TMP_PATH, WINE_AMDIS_TMP_PATH_UNIX);
		} else {
			defaultValues.put(P_AMDIS_APPLICATION, WINDOWS_AMDIS);
			defaultValues.put(P_AMDIS_TMP_PATH, WINDOWS_AMDIS_TMP_PATH);
		}
		/*
		 * AMDIS settings
		 */
		defaultValues.put(P_LOW_MZ_AUTO, Boolean.toString(DEF_LOW_MZ_AUTO));
		defaultValues.put(P_START_MZ, Integer.toString(DEF_START_MZ));
		defaultValues.put(P_HIGH_MZ_AUTO, Boolean.toString(DEF_HIGH_MZ_AUTO));
		defaultValues.put(P_STOP_MZ, Integer.toString(DEF_STOP_MZ));
		defaultValues.put(P_OMIT_MZ, Boolean.toString(DEF_OMIT_MZ));
		defaultValues.put(P_OMITED_MZ, DEF_OMITED_MZ);
		//
		defaultValues.put(P_USE_SOLVENT_TAILING, Boolean.toString(DEF_USE_SOLVENT_TAILING));
		defaultValues.put(P_SOLVENT_TAILING_MZ, Integer.toString(DEF_SOLVENT_TAILING_MZ));
		defaultValues.put(P_USE_COLUMN_BLEED, Boolean.toString(DEF_USE_COLUMN_BLEED));
		defaultValues.put(P_COLUMN_BLEED_MZ, Integer.toString(DEF_COLUMN_BLEED_MZ));
		//
		defaultValues.put(P_THRESHOLD, DEF_THRESHOLD);
		defaultValues.put(P_PEAK_WIDTH, Integer.toString(DEF_PEAK_WIDTH));
		defaultValues.put(P_ADJACENT_PEAK_SUBTRACTION, DEF_ADJACENT_PEAK_SUBTRACTION);
		defaultValues.put(P_RESOLUTION, DEF_RESOLUTION);
		defaultValues.put(P_SENSITIVITY, DEF_SENSITIVITY);
		defaultValues.put(P_SHAPE_REQUIREMENTS, DEF_SHAPE_REQUIREMENTS);
		/*
		 * Extra settings
		 */
		defaultValues.put(P_MIN_SN_RATIO, Float.toString(DEF_MIN_SN_RATIO));
		defaultValues.put(P_MIN_LEADING, Float.toString(DEF_MIN_LEADING));
		defaultValues.put(P_MAX_LEADING, Float.toString(DEF_MAX_LEADING));
		defaultValues.put(P_MIN_TAILING, Float.toString(DEF_MIN_TAILING));
		defaultValues.put(P_MAX_TAILING, Float.toString(DEF_MAX_TAILING));
		//
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static PeakDetectorSettings getPeakDetectorSettings() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		PeakDetectorSettings peakDetectorSettings = new PeakDetectorSettings();
		peakDetectorSettings.setMinSignalToNoiseRatio(preferences.getFloat(P_MIN_SN_RATIO, DEF_MIN_SN_RATIO));
		peakDetectorSettings.setMinLeading(preferences.getFloat(P_MIN_LEADING, DEF_MIN_LEADING));
		peakDetectorSettings.setMaxLeading(preferences.getFloat(P_MAX_LEADING, DEF_MAX_LEADING));
		peakDetectorSettings.setMinTailing(preferences.getFloat(P_MIN_TAILING, DEF_MIN_TAILING));
		peakDetectorSettings.setMaxTailing(preferences.getFloat(P_MAX_TAILING, DEF_MAX_TAILING));
		//
		IOnsiteSettings onsiteSettings = peakDetectorSettings.getOnsiteSettings();
		onsiteSettings.setValue(IOnsiteSettings.KEY_LOW_MZ_AUTO, (preferences.getBoolean(P_LOW_MZ_AUTO, DEF_LOW_MZ_AUTO)) ? IOnsiteSettings.VALUE_YES : IOnsiteSettings.VALUE_NO);
		onsiteSettings.setValue(IOnsiteSettings.KEY_START_MZ, Integer.toString(preferences.getInt(P_START_MZ, DEF_START_MZ)));
		onsiteSettings.setValue(IOnsiteSettings.KEY_HIGH_MZ_AUTO, (preferences.getBoolean(P_HIGH_MZ_AUTO, DEF_HIGH_MZ_AUTO)) ? IOnsiteSettings.VALUE_YES : IOnsiteSettings.VALUE_NO);
		onsiteSettings.setValue(IOnsiteSettings.KEY_STOP_MZ, Integer.toString(preferences.getInt(P_STOP_MZ, DEF_STOP_MZ)));
		onsiteSettings.setValue(IOnsiteSettings.KEY_OMIT_MZ, (preferences.getBoolean(P_OMIT_MZ, DEF_OMIT_MZ)) ? IOnsiteSettings.VALUE_YES : IOnsiteSettings.VALUE_NO);
		onsiteSettings.setValue(IOnsiteSettings.KEY_OMITED_MZ, preferences.get(P_OMITED_MZ, DEF_OMITED_MZ));
		//
		onsiteSettings.setValue(IOnsiteSettings.KEY_USE_SOLVENT_TAILING, (preferences.getBoolean(P_USE_SOLVENT_TAILING, DEF_USE_SOLVENT_TAILING)) ? IOnsiteSettings.VALUE_YES : IOnsiteSettings.VALUE_NO);
		onsiteSettings.setValue(IOnsiteSettings.KEY_SOLVENT_TAILING_MZ, Integer.toString(preferences.getInt(P_SOLVENT_TAILING_MZ, DEF_SOLVENT_TAILING_MZ)));
		onsiteSettings.setValue(IOnsiteSettings.KEY_USE_COLUMN_BLEED, (preferences.getBoolean(P_USE_COLUMN_BLEED, DEF_USE_COLUMN_BLEED)) ? IOnsiteSettings.VALUE_YES : IOnsiteSettings.VALUE_NO);
		onsiteSettings.setValue(IOnsiteSettings.KEY_COLUMN_BLEED_MZ, Integer.toString(preferences.getInt(P_COLUMN_BLEED_MZ, DEF_COLUMN_BLEED_MZ)));
		//
		onsiteSettings.setValue(IOnsiteSettings.KEY_THRESHOLD, preferences.get(P_THRESHOLD, DEF_THRESHOLD));
		onsiteSettings.setValue(IOnsiteSettings.KEY_PEAK_WIDTH, Integer.toString(preferences.getInt(P_PEAK_WIDTH, DEF_PEAK_WIDTH)));
		onsiteSettings.setValue(IOnsiteSettings.KEY_ADJACENT_PEAK_SUBTRACTION, preferences.get(P_ADJACENT_PEAK_SUBTRACTION, DEF_ADJACENT_PEAK_SUBTRACTION));
		onsiteSettings.setValue(IOnsiteSettings.KEY_RESOLUTION, preferences.get(P_RESOLUTION, DEF_RESOLUTION));
		onsiteSettings.setValue(IOnsiteSettings.KEY_SENSITIVITY, preferences.get(P_SENSITIVITY, DEF_SENSITIVITY));
		onsiteSettings.setValue(IOnsiteSettings.KEY_SHAPE_REQUIREMENTS, preferences.get(P_SHAPE_REQUIREMENTS, DEF_SHAPE_REQUIREMENTS));
		//
		return peakDetectorSettings;
	}

	public static String getMacWineBinary() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.get(P_MAC_WINE_BINARY, DEF_MAC_WINE_BINARY);
	}

	public static String getAmdisApplication() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.get(P_AMDIS_APPLICATION, DEF_AMDIS_APPLICATION);
	}

	public static String getAmdisTmpPath() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.get(P_AMDIS_TMP_PATH, DEF_AMDIS_TMP_PATH);
	}
}
