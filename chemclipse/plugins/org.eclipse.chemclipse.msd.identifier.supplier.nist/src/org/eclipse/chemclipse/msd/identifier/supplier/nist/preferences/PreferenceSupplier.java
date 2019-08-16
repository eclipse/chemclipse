/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.msd.identifier.supplier.nist.Activator;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.settings.MassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.settings.PeakIdentifierSettings;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	/*
	 * NIST Path
	 */
	private static final String WINDOWS_NIST = "C:\\Programs\\NIST\\MSSEARCH\\nistms$.exe";
	private static final String WINE_NIST_LINUX = "drive_c/Programs/NIST/MSSEARCH/nistms$.exe";
	private static final String WINE_NIST_MAC = "drive_c/NIST/MSSEARCH/nistms$.exe";
	/*
	 * 
	 */
	public static final int MIN_NUMBER_OF_TARGETS = 1;
	public static final int MAX_NUMBER_OF_TARGETS = 100;
	public static final String MSP_EXPORT_FILE_NAME = "openchrom-unknown.msp";
	/*
	 * Preferences
	 */
	public static final String P_NIST_APPLICATION = "nistApplication";
	public static final String DEF_NIST_APPLICATION_WINDOWS = WINDOWS_NIST;
	public static final String P_NUMBER_OF_TARGETS = "numberOfTargets";
	public static final int DEF_NUMBER_OF_TARGETS = 3;
	public static final String P_STORE_TARGETS = "storeTargets";
	public static final boolean DEF_STORE_TARGETS = true;
	public static final String P_MAC_WINE_BINARY = "macWineBinary";
	public static final String DEF_MAC_WINE_BINARY = "/Applications/Wine.app";
	public static final String P_USE_GUI_DIRECT = "useGUIDirect";
	public static final boolean DEF_USE_GUI_DIRECT = false;
	public static final boolean DEF_USE_GUI_DIRECT_MAC = true;
	public static final String P_TIMEOUT_IN_MINUTES = "timeoutInMinutes";
	public static final int DEF_TIMEOUT_IN_MINUTES = 20;
	public static final String P_SHOW_GUI_DIALOG = "showGUIDialog";
	public static final boolean DEF_SHOW_GUI_DIALOG = true;
	//
	public static final String P_MIN_MATCH_FACTOR = "minMatchFactor";
	public static final float DEF_MIN_MATCH_FACTOR = 80.0f;
	public static final String P_MIN_REVERSE_MATCH_FACTOR = "minReverseMatchFactor";
	public static final float DEF_MIN_REVERSE_MATCH_FACTOR = 80.0f;
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
		/*
		 * Windows or Wine
		 */
		if(OperatingSystemUtils.isWindows()) {
			defaultValues.put(P_NIST_APPLICATION, DEF_NIST_APPLICATION_WINDOWS);
		} else {
			defaultValues.put(P_NIST_APPLICATION, getDefaultNistApplicationOther());
		}
		defaultValues.put(P_MAC_WINE_BINARY, DEF_MAC_WINE_BINARY);
		defaultValues.put(P_NUMBER_OF_TARGETS, Integer.toString(DEF_NUMBER_OF_TARGETS));
		defaultValues.put(P_STORE_TARGETS, Boolean.toString(DEF_STORE_TARGETS));
		/*
		 * Mac or other
		 */
		if(OperatingSystemUtils.isMac()) {
			defaultValues.put(P_USE_GUI_DIRECT, Boolean.toString(DEF_USE_GUI_DIRECT_MAC));
		} else {
			defaultValues.put(P_USE_GUI_DIRECT, Boolean.toString(DEF_USE_GUI_DIRECT));
		}
		defaultValues.put(P_SHOW_GUI_DIALOG, Boolean.toString(DEF_SHOW_GUI_DIALOG));
		defaultValues.put(P_TIMEOUT_IN_MINUTES, Integer.toString(DEF_TIMEOUT_IN_MINUTES));
		//
		defaultValues.put(P_MIN_MATCH_FACTOR, Float.toString(DEF_MIN_MATCH_FACTOR));
		defaultValues.put(P_MIN_REVERSE_MATCH_FACTOR, Float.toString(DEF_MIN_REVERSE_MATCH_FACTOR));
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	/**
	 * Returns the peak identifier settings.
	 * 
	 * @return IPeakIdentifierSettings
	 */
	public static PeakIdentifierSettings getPeakIdentifierSettings() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		PeakIdentifierSettings peakIdentifierSettings = new PeakIdentifierSettings();
		if(OperatingSystemUtils.isWindows()) {
			peakIdentifierSettings.setNistApplication(preferences.get(P_NIST_APPLICATION, DEF_NIST_APPLICATION_WINDOWS));
		} else {
			peakIdentifierSettings.setNistApplication(preferences.get(P_NIST_APPLICATION, getDefaultNistApplicationOther()));
		}
		peakIdentifierSettings.setNumberOfTargets(preferences.getInt(P_NUMBER_OF_TARGETS, DEF_NUMBER_OF_TARGETS));
		peakIdentifierSettings.setStoreTargets(preferences.getBoolean(P_STORE_TARGETS, DEF_STORE_TARGETS));
		peakIdentifierSettings.setTimeoutInMinutes(preferences.getInt(P_TIMEOUT_IN_MINUTES, DEF_TIMEOUT_IN_MINUTES));
		return peakIdentifierSettings;
	}

	public static MassSpectrumIdentifierSettings getMassSpectrumIdentifierSettings() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		MassSpectrumIdentifierSettings massSpectrumIdentifierSettings = new MassSpectrumIdentifierSettings();
		if(OperatingSystemUtils.isWindows()) {
			massSpectrumIdentifierSettings.setNistApplication(preferences.get(P_NIST_APPLICATION, DEF_NIST_APPLICATION_WINDOWS));
		} else {
			massSpectrumIdentifierSettings.setNistApplication(preferences.get(P_NIST_APPLICATION, getDefaultNistApplicationOther()));
		}
		massSpectrumIdentifierSettings.setNumberOfTargets(preferences.getInt(P_NUMBER_OF_TARGETS, DEF_NUMBER_OF_TARGETS));
		massSpectrumIdentifierSettings.setStoreTargets(preferences.getBoolean(P_STORE_TARGETS, DEF_STORE_TARGETS));
		massSpectrumIdentifierSettings.setTimeoutInMinutes(preferences.getInt(P_TIMEOUT_IN_MINUTES, DEF_TIMEOUT_IN_MINUTES));
		return massSpectrumIdentifierSettings;
	}

	/**
	 * Returns the default application path using wine.
	 * 
	 * @return String
	 */
	public static String getDefaultNistApplicationOther() {

		String applicationPath;
		if(OperatingSystemUtils.isMac()) {
			/*
			 * Using Wine (http://code.google.com/p/aardwolfclientpackage/downloads/detail?name=Wine_1.2.2.dmg&can=2&q=)
			 */
			applicationPath = System.getProperty("user.home") + "/Wine Files/" + WINE_NIST_MAC;
		} else {
			/*
			 * Linux ...
			 */
			applicationPath = System.getProperty("user.home") + "/.wine/" + WINE_NIST_LINUX;
		}
		return applicationPath;
	}

	public static String getMacWineBinary() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.get(P_MAC_WINE_BINARY, DEF_MAC_WINE_BINARY);
	}

	public static boolean isUseGUIDirect() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getBoolean(P_USE_GUI_DIRECT, DEF_USE_GUI_DIRECT_MAC);
	}

	public static boolean isShowGUIDialog() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getBoolean(P_SHOW_GUI_DIALOG, DEF_SHOW_GUI_DIALOG);
	}

	public static void setShowGUIDialog(boolean showGUIDialog) {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		preferences.putBoolean(P_SHOW_GUI_DIALOG, showGUIDialog);
	}

	public static float getMinMatchFactor() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getFloat(P_MIN_MATCH_FACTOR, DEF_MIN_MATCH_FACTOR);
	}

	public static float getMinReverseMatchFactor() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getFloat(P_MIN_REVERSE_MATCH_FACTOR, DEF_MIN_REVERSE_MATCH_FACTOR);
	}

	/**
	 * Returns the path of the nist application.
	 * 
	 * @return String
	 */
	public static String getNistApplication() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.get(P_NIST_APPLICATION, DEF_NIST_APPLICATION_WINDOWS);
	}

	/**
	 * Returns the number of targets to use in the report.
	 * 
	 * @return int
	 */
	public static int getNumberOfTargets() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getInt(P_NUMBER_OF_TARGETS, DEF_NUMBER_OF_TARGETS);
	}

	/**
	 * Shall the targets be stored.
	 * 
	 * @return boolean
	 */
	public static boolean getStoreTargets() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getBoolean(P_STORE_TARGETS, DEF_STORE_TARGETS);
	}

	public static int getTimeoutInMinutes() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getInt(P_TIMEOUT_IN_MINUTES, DEF_TIMEOUT_IN_MINUTES);
	}
}
