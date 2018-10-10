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
package org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.Activator;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IFormat;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final int MIN_COMPRESSION_LEVEL = 0;
	public static final int MAX_COMPRESSION_LEVEL = 9;
	//
	public static final String P_CHROMATOGRAM_VERSION_SAVE = "chromaotgramVersionSave";
	public static final String DEF_CHROMATOGRAM_VERSION_SAVE = IFormat.CHROMATOGRAM_VERSION_LATEST;
	public static final String P_CHROMATOGRAM_COMPRESSION_LEVEL = "chromatogramCompressionLevel";
	public static final int DEF_CHROMATOGRAM_COMPRESSION_LEVEL = IFormat.CHROMATOGRAM_COMPRESSION_LEVEL;
	//
	public static final String P_METHOD_VERSION_SAVE = "chromaotgramVersionSave";
	public static final String DEF_METHOD_VERSION_SAVE = IFormat.METHOD_VERSION_LATEST;
	public static final String P_METHOD_COMPRESSION_LEVEL = "chromatogramCompressionLevel";
	public static final int DEF_METHOD_COMPRESSION_LEVEL = IFormat.METHOD_COMPRESSION_LEVEL;
	//
	public static final String P_FORCE_LOAD_ALTERNATE_DETECTOR = "forceLoadAlternateDetector";
	public static final boolean DEF_FORCE_LOAD_ALTERNATE_DETECTOR = false;
	//
	public static final String P_USE_SCAN_PROXIES = "useScanProxies";
	public static final boolean DEF_USE_SCAN_PROXIES = false;
	public static final String P_LOAD_SCAN_PROXIES_IN_BACKGROUND = "loadScanProxiesInBackground";
	public static final boolean DEF_LOAD_SCAN_PROXIES_IN_BACKGROUND = false; // This could lead java.util.ConcurrentModificationException if true
	public static final String P_MIN_BYTES_TO_LOAD_IN_BACKGROUND = "minBytesToLoadInBackground";
	public static final int DEF_MIN_BYTES_TO_LOAD_IN_BACKGROUND = 2000000; // 2 MB
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
		defaultValues.put(P_CHROMATOGRAM_VERSION_SAVE, DEF_CHROMATOGRAM_VERSION_SAVE);
		defaultValues.put(P_CHROMATOGRAM_COMPRESSION_LEVEL, Integer.toString(DEF_CHROMATOGRAM_COMPRESSION_LEVEL));
		defaultValues.put(P_METHOD_VERSION_SAVE, DEF_METHOD_VERSION_SAVE);
		defaultValues.put(P_METHOD_COMPRESSION_LEVEL, Integer.toString(DEF_METHOD_COMPRESSION_LEVEL));
		defaultValues.put(P_FORCE_LOAD_ALTERNATE_DETECTOR, Boolean.toString(DEF_FORCE_LOAD_ALTERNATE_DETECTOR));
		defaultValues.put(P_USE_SCAN_PROXIES, Boolean.toString(DEF_USE_SCAN_PROXIES));
		defaultValues.put(P_LOAD_SCAN_PROXIES_IN_BACKGROUND, Boolean.toString(DEF_LOAD_SCAN_PROXIES_IN_BACKGROUND));
		defaultValues.put(P_MIN_BYTES_TO_LOAD_IN_BACKGROUND, Integer.toString(DEF_MIN_BYTES_TO_LOAD_IN_BACKGROUND));
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static String getChromatogramVersionSave() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.get(P_CHROMATOGRAM_VERSION_SAVE, DEF_CHROMATOGRAM_VERSION_SAVE);
	}

	public static int getChromatogramCompressionLevel() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getInt(P_CHROMATOGRAM_COMPRESSION_LEVEL, DEF_CHROMATOGRAM_COMPRESSION_LEVEL);
	}

	public static String getMethodVersionSave() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.get(P_METHOD_VERSION_SAVE, DEF_METHOD_VERSION_SAVE);
	}

	public static int getMethodCompressionLevel() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getInt(P_METHOD_COMPRESSION_LEVEL, DEF_METHOD_COMPRESSION_LEVEL);
	}

	public static void setForceLoadAlternateDetector(boolean forceLoadAlternateDetector) {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		preferences.putBoolean(P_FORCE_LOAD_ALTERNATE_DETECTOR, forceLoadAlternateDetector);
	}

	public static boolean isForceLoadAlternateDetector() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getBoolean(P_FORCE_LOAD_ALTERNATE_DETECTOR, DEF_FORCE_LOAD_ALTERNATE_DETECTOR);
	}

	public static String[][] getChromatogramVersions() {

		// TODO optimize the version handling!
		int versions = 6;
		String[][] elements = new String[versions][2];
		//
		elements[0][0] = IFormat.CHROMATOGRAM_VERSION_0701 + " (Nernst)";
		elements[0][1] = IFormat.CHROMATOGRAM_VERSION_0701;
		//
		elements[1][0] = IFormat.CHROMATOGRAM_VERSION_0803 + " (Dempster)";
		elements[1][1] = IFormat.CHROMATOGRAM_VERSION_0803;
		//
		elements[2][0] = IFormat.CHROMATOGRAM_VERSION_0903 + " (Mattauch)";
		elements[2][1] = IFormat.CHROMATOGRAM_VERSION_0903;
		//
		elements[3][0] = IFormat.CHROMATOGRAM_VERSION_1004 + " (Aston)";
		elements[3][1] = IFormat.CHROMATOGRAM_VERSION_1004;
		//
		elements[4][0] = IFormat.CHROMATOGRAM_VERSION_1100 + " (Diels)";
		elements[4][1] = IFormat.CHROMATOGRAM_VERSION_1100;
		//
		elements[5][0] = IFormat.CHROMATOGRAM_VERSION_1300 + " (Dalton)";
		elements[5][1] = IFormat.CHROMATOGRAM_VERSION_1300;
		//
		return elements;
	}

	public static String[][] getMethodVersions() {

		// TODO optimize the version handling!
		int versions = 1;
		String[][] elements = new String[versions][2];
		//
		elements[0][0] = IFormat.METHOD_VERSION_0001 + " (Test)";
		elements[0][1] = IFormat.METHOD_VERSION_0001;
		//
		return elements;
	}
}
