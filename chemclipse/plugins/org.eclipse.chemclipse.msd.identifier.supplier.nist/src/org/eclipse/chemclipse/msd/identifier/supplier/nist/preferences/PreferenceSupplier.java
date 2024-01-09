/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Christoph Läubrich - using a global configuration for the nist path + add support for validate the path
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.preferences;

import java.io.File;

import org.eclipse.chemclipse.msd.identifier.supplier.nist.Activator;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.settings.PeakIdentifierSettings;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.settings.ScanDirectIdentifierSettings;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.settings.ScanIdentifierSettings;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final float MIN_FACTOR = 0.0f;
	public static final float MAX_FACTOR = 100.0f;
	public static final int MIN_NUMBER_OF_TARGETS = 1;
	public static final int MAX_NUMBER_OF_TARGETS = 100;
	public static final String MSP_EXPORT_FILE_NAME = "openchrom-unknown.msp";
	/*
	 * Preferences
	 */
	public static final String P_NIST_APPLICATION = "nistApplication";
	public static final String P_LIMIT_MATCH_FACTOR = "limitMatchFactor";
	public static final float DEF_LIMIT_MATCH_FACTOR = 80.0f;
	public static final String P_NUMBER_OF_TARGETS = "numberOfTargets";
	public static final byte DEF_NUMBER_OF_TARGETS = 15;
	public static final String P_USE_OPTIMIZED_MASS_SPECTRUM = "useOptimizedMassSpectrum";
	public static final boolean DEF_USE_OPTIMIZED_MASS_SPECTRUM = true;
	public static final String P_MAC_WINE_BINARY = "macWineBinary";
	public static final String DEF_MAC_WINE_BINARY = "/Applications/Wine.app";
	public static final String P_TIMEOUT_IN_MINUTES = "timeoutInMinutes";
	public static final int DEF_TIMEOUT_IN_MINUTES = 20;
	public static final String P_WAIT_IN_SECONDS = "waitInSeconds";
	public static final int DEF_WAIT_IN_SECONDS = 3;
	public static final String P_MIN_MATCH_FACTOR = "minMatchFactor";
	public static final float DEF_MIN_MATCH_FACTOR = 80.0f;
	public static final String P_MIN_REVERSE_MATCH_FACTOR = "minReverseMatchFactor";
	public static final float DEF_MIN_REVERSE_MATCH_FACTOR = 80.0f;
	//
	private static IPreferenceSupplier preferenceSupplier = null;

	public static IPreferenceSupplier INSTANCE() {

		if(preferenceSupplier == null) {
			preferenceSupplier = new PreferenceSupplier();
		}
		return preferenceSupplier;
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public void initializeDefaults() {

		putDefault(P_MAC_WINE_BINARY, DEF_MAC_WINE_BINARY);
		putDefault(P_LIMIT_MATCH_FACTOR, Float.toString(DEF_LIMIT_MATCH_FACTOR));
		putDefault(P_NUMBER_OF_TARGETS, Integer.toString(DEF_NUMBER_OF_TARGETS));
		putDefault(P_USE_OPTIMIZED_MASS_SPECTRUM, Boolean.toString(DEF_USE_OPTIMIZED_MASS_SPECTRUM));
		putDefault(P_TIMEOUT_IN_MINUTES, Integer.toString(DEF_TIMEOUT_IN_MINUTES));
		putDefault(P_WAIT_IN_SECONDS, Integer.toString(DEF_WAIT_IN_SECONDS));
		putDefault(P_MIN_MATCH_FACTOR, Float.toString(DEF_MIN_MATCH_FACTOR));
		putDefault(P_MIN_REVERSE_MATCH_FACTOR, Float.toString(DEF_MIN_REVERSE_MATCH_FACTOR));
	}

	/**
	 * This fetches the <b>FOLDER</b> where the NIST application is stored (e.g. c:\NIST\MSSEARCH)
	 * 
	 * @return the path where the NIST application is stored or <code>null</code> if no such folder is currently stored or exits
	 */
	public static File getNistInstallationFolder() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		String path = preferences.get(P_NIST_APPLICATION, "");
		if(!path.isEmpty()) {
			File filePath = new File(path);
			if(filePath.isFile()) {
				filePath.getParentFile();
			}
			if(filePath.isDirectory()) {
				File subfolder = new File(filePath, "MSSEARCH");
				if(subfolder.isDirectory()) {
					return subfolder;
				}
				return filePath;
			}
		}
		return null;
	}

	public static IStatus validateLocation(File location) {

		if(location == null) {
			return error("No Program-Location configured");
		}
		if(!location.isDirectory()) {
			return error("Location " + location.getAbsolutePath() + " does not exits or is not a directory");
		}
		File file = getNistExecutable(location);
		if(!file.isFile()) {
			return error("Can't find nistms.exe at path " + location.getAbsolutePath() + " or it can't be accessed");
		}
		File[] libraries = getLibraries(location);
		if(libraries.length == 0) {
			return error("Can't find any libraries at path " + location.getAbsolutePath());
		}
		return Status.OK_STATUS;
	}

	public static File[] getLibraries(File location) {

		if(location.isDirectory()) {
			File[] files = location.listFiles(File::isDirectory);
			if(files != null) {
				return files;
			}
		}
		return new File[0];
	}

	public static File getNistExecutable(File location) {

		File file = new File(location, "nistms.exe");
		if(!file.exists()) {
			File subfolder = new File(location, "MSSEARCH");
			if(subfolder.isDirectory()) {
				return getNistExecutable(subfolder);
			}
		}
		return file;
	}

	private static final IStatus error(String message) {

		return new Status(IStatus.ERROR, Activator.getContext().getBundle().getSymbolicName(), message);
	}

	public static PeakIdentifierSettings getPeakIdentifierSettings() {

		PeakIdentifierSettings settings = new PeakIdentifierSettings();
		settings.setNistFolder(PreferenceSupplier.getNistInstallationFolder());
		settings.setLimitMatchFactor(INSTANCE().getFloat(P_LIMIT_MATCH_FACTOR, DEF_LIMIT_MATCH_FACTOR));
		settings.setNumberOfTargets(INSTANCE().getByte(P_NUMBER_OF_TARGETS, DEF_NUMBER_OF_TARGETS));
		settings.setUseOptimizedMassSpectrum(INSTANCE().getBoolean(P_USE_OPTIMIZED_MASS_SPECTRUM, DEF_USE_OPTIMIZED_MASS_SPECTRUM));
		settings.setTimeoutInMinutes(INSTANCE().getInteger(P_TIMEOUT_IN_MINUTES, DEF_TIMEOUT_IN_MINUTES));
		settings.setMinMatchFactor(INSTANCE().getFloat(P_MIN_MATCH_FACTOR, DEF_MIN_MATCH_FACTOR));
		settings.setMinReverseMatchFactor(INSTANCE().getFloat(P_MIN_REVERSE_MATCH_FACTOR, DEF_MIN_REVERSE_MATCH_FACTOR));
		//
		return settings;
	}

	public static ScanIdentifierSettings getScanIdentifierSettings() {

		ScanIdentifierSettings settings = new ScanIdentifierSettings();
		settings.setNistFolder(PreferenceSupplier.getNistInstallationFolder());
		settings.setLimitMatchFactor(INSTANCE().getFloat(P_LIMIT_MATCH_FACTOR, DEF_LIMIT_MATCH_FACTOR));
		settings.setNumberOfTargets(INSTANCE().getByte(P_NUMBER_OF_TARGETS, DEF_NUMBER_OF_TARGETS));
		settings.setUseOptimizedMassSpectrum(INSTANCE().getBoolean(P_USE_OPTIMIZED_MASS_SPECTRUM, DEF_USE_OPTIMIZED_MASS_SPECTRUM));
		settings.setTimeoutInMinutes(INSTANCE().getInteger(P_TIMEOUT_IN_MINUTES, DEF_TIMEOUT_IN_MINUTES));
		settings.setMinMatchFactor(INSTANCE().getFloat(P_MIN_MATCH_FACTOR, DEF_MIN_MATCH_FACTOR));
		settings.setMinReverseMatchFactor(INSTANCE().getFloat(P_MIN_REVERSE_MATCH_FACTOR, DEF_MIN_REVERSE_MATCH_FACTOR));
		//
		return settings;
	}

	public static ScanDirectIdentifierSettings getScanDirectIdentifierSettings() {

		ScanDirectIdentifierSettings settings = new ScanDirectIdentifierSettings();
		settings.setNistFolder(PreferenceSupplier.getNistInstallationFolder());
		settings.setUseOptimizedMassSpectrum(INSTANCE().getBoolean(P_USE_OPTIMIZED_MASS_SPECTRUM, DEF_USE_OPTIMIZED_MASS_SPECTRUM));
		settings.setWaitInSeconds(INSTANCE().getInteger(P_WAIT_IN_SECONDS, DEF_WAIT_IN_SECONDS));
		//
		return settings;
	}

	public static String getMacWineBinary() {

		return INSTANCE().get(P_MAC_WINE_BINARY, DEF_MAC_WINE_BINARY);
	}
}