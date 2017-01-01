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

	public static final String P_VERSION_SAVE = "versionSave";
	public static final String DEF_VERSION_SAVE = IFormat.VERSION_LATEST;
	public static final String P_COMPRESSION_LEVEL = "compressionLevel";
	public static final int DEF_COMPRESSION_LEVEL = IFormat.COMPRESSION_LEVEL;
	public static final int MIN_COMPRESSION_LEVEL = 0;
	public static final int MAX_COMPRESSION_LEVEL = 9;
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
		defaultValues.put(P_VERSION_SAVE, DEF_VERSION_SAVE);
		defaultValues.put(P_COMPRESSION_LEVEL, Integer.toString(DEF_COMPRESSION_LEVEL));
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

	/**
	 * Returns the export version.
	 */
	public static String getVersionSave() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.get(P_VERSION_SAVE, DEF_VERSION_SAVE);
	}

	public static int getCompressionLevel() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getInt(P_COMPRESSION_LEVEL, DEF_COMPRESSION_LEVEL);
	}

	public static void setForceLoadAlternateDetector(boolean forceLoadAlternateDetector) {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		preferences.putBoolean(P_FORCE_LOAD_ALTERNATE_DETECTOR, forceLoadAlternateDetector);
	}

	public static boolean isForceLoadAlternateDetector() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getBoolean(P_FORCE_LOAD_ALTERNATE_DETECTOR, DEF_FORCE_LOAD_ALTERNATE_DETECTOR);
	}

	public static String[][] getVersions() {

		// TODO optimize the version handling!
		int versions = 5;
		String[][] elements = new String[versions][2];
		//
		elements[0][0] = IFormat.VERSION_0701 + " (Nernst)";
		elements[0][1] = IFormat.VERSION_0701;
		//
		elements[1][0] = IFormat.VERSION_0803 + " (Dempster)";
		elements[1][1] = IFormat.VERSION_0803;
		//
		elements[2][0] = IFormat.VERSION_0903 + " (Mattauch)";
		elements[2][1] = IFormat.VERSION_0903;
		//
		elements[3][0] = IFormat.VERSION_1004 + " (Aston)";
		elements[3][1] = IFormat.VERSION_1004;
		//
		elements[4][0] = IFormat.VERSION_1100 + " (Diels)";
		elements[4][1] = IFormat.VERSION_1100;
		//
		return elements;
	}
}
