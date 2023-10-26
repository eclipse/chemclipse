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
 * Christoph Läubrich - new format
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.ocx.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.chemclipse.xxd.converter.supplier.ocx.Activator;
import org.eclipse.chemclipse.xxd.converter.supplier.ocx.internal.support.IFormat;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final int MIN_COMPRESSION_LEVEL = 0;
	public static final int MAX_COMPRESSION_LEVEL = 9;
	/*
	 * *.ocb (measurement data container)
	 */
	public static final String P_CHROMATOGRAM_VERSION_SAVE = "chromatogramVersionSave";
	public static final String DEF_CHROMATOGRAM_VERSION_SAVE = IFormat.CHROMATOGRAM_VERSION_LATEST;
	public static final String P_CHROMATOGRAM_COMPRESSION_LEVEL = "chromatogramCompressionLevel";
	public static final int DEF_CHROMATOGRAM_COMPRESSION_LEVEL = IFormat.CHROMATOGRAM_COMPRESSION_LEVEL;
	//
	public static final String P_FORCE_LOAD_ALTERNATE_DETECTOR = "forceLoadAlternateDetector"; // TODO REMOVE
	public static final boolean DEF_FORCE_LOAD_ALTERNATE_DETECTOR = false;
	//
	public static final String P_USE_SCAN_PROXIES = "useScanProxies"; // TODO CHECK
	public static final boolean DEF_USE_SCAN_PROXIES = false;
	public static final String P_LOAD_SCAN_PROXIES_IN_BACKGROUND = "loadScanProxiesInBackground"; // TODO REMOVE
	public static final boolean DEF_LOAD_SCAN_PROXIES_IN_BACKGROUND = false; // This could lead java.util.ConcurrentModificationException if true
	public static final String P_MIN_BYTES_TO_LOAD_IN_BACKGROUND = "minBytesToLoadInBackground"; // TODO REMOVE
	public static final int DEF_MIN_BYTES_TO_LOAD_IN_BACKGROUND = 2000000; // 2 MB
	/*
	 * *.ocm (process method container)
	 */
	public static final String P_METHOD_VERSION_SAVE = "methodVersionSave";
	public static final String DEF_METHOD_VERSION_SAVE = IFormat.METHOD_VERSION_LATEST;
	public static final String P_METHOD_COMPRESSION_LEVEL = "methodCompressionLevel";
	public static final int DEF_METHOD_COMPRESSION_LEVEL = IFormat.METHOD_COMPRESSION_LEVEL;
	/*
	 * *.ocq (quanititation table container)
	 */
	public static final String P_QUANTITATION_DB_VERSION_SAVE = "quantitationDatabaseVersionSave";
	public static final String DEF_QUANTITATION_DB_VERSION_SAVE = IFormat.QUANTDB_VERSION_LATEST;
	public static final String P_QUANTITATION_DB_COMPRESSION_LEVEL = "quantitationDatabaseCompressionLevel";
	public static final int DEF_QUANTITATION_DB_COMPRESSION_LEVEL = IFormat.QUANTDB_COMPRESSION_LEVEL;
	/*
	 * General
	 */
	public static final String P_LIST_PATH_IMPORT = "listPathImport";
	public static final String DEF_LIST_PATH_IMPORT = "";
	public static final String P_LIST_PATH_EXPORT = "listPathExport";
	public static final String DEF_LIST_PATH_EXPORT = "";
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
		//
		defaultValues.put(P_CHROMATOGRAM_VERSION_SAVE, DEF_CHROMATOGRAM_VERSION_SAVE);
		defaultValues.put(P_CHROMATOGRAM_COMPRESSION_LEVEL, Integer.toString(DEF_CHROMATOGRAM_COMPRESSION_LEVEL));
		defaultValues.put(P_FORCE_LOAD_ALTERNATE_DETECTOR, Boolean.toString(DEF_FORCE_LOAD_ALTERNATE_DETECTOR));
		defaultValues.put(P_USE_SCAN_PROXIES, Boolean.toString(DEF_USE_SCAN_PROXIES));
		defaultValues.put(P_LOAD_SCAN_PROXIES_IN_BACKGROUND, Boolean.toString(DEF_LOAD_SCAN_PROXIES_IN_BACKGROUND));
		defaultValues.put(P_MIN_BYTES_TO_LOAD_IN_BACKGROUND, Integer.toString(DEF_MIN_BYTES_TO_LOAD_IN_BACKGROUND));
		//
		defaultValues.put(P_METHOD_VERSION_SAVE, DEF_METHOD_VERSION_SAVE);
		defaultValues.put(P_METHOD_COMPRESSION_LEVEL, Integer.toString(DEF_METHOD_COMPRESSION_LEVEL));
		//
		defaultValues.put(P_QUANTITATION_DB_VERSION_SAVE, DEF_QUANTITATION_DB_VERSION_SAVE);
		defaultValues.put(P_QUANTITATION_DB_COMPRESSION_LEVEL, Integer.toString(DEF_QUANTITATION_DB_COMPRESSION_LEVEL));
		//
		defaultValues.put(P_LIST_PATH_IMPORT, DEF_LIST_PATH_IMPORT);
		defaultValues.put(P_LIST_PATH_EXPORT, DEF_LIST_PATH_EXPORT);
		//
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static String getChromatogramVersionSave() {

		return INSTANCE().get(P_CHROMATOGRAM_VERSION_SAVE, DEF_CHROMATOGRAM_VERSION_SAVE);
	}

	public static void setChromatogramVersionSave(String value) {

		INSTANCE().put(P_CHROMATOGRAM_VERSION_SAVE, value);
	}

	public static int getChromatogramCompressionLevel() {

		return INSTANCE().getInteger(P_CHROMATOGRAM_COMPRESSION_LEVEL, DEF_CHROMATOGRAM_COMPRESSION_LEVEL);
	}

	public static void setChromatogramCompressionLevel(int value) {

		INSTANCE().putInteger(P_CHROMATOGRAM_COMPRESSION_LEVEL, value);
	}

	public static String getMethodVersionSave() {

		return INSTANCE().get(P_METHOD_VERSION_SAVE, DEF_METHOD_VERSION_SAVE);
	}

	public static void setMethodVersionSave(String value) {

		INSTANCE().put(P_METHOD_VERSION_SAVE, value);
	}

	public static int getMethodCompressionLevel() {

		return INSTANCE().getInteger(P_METHOD_COMPRESSION_LEVEL, DEF_METHOD_COMPRESSION_LEVEL);
	}

	public static void setMethodCompressionLevel(int value) {

		INSTANCE().putInteger(P_METHOD_COMPRESSION_LEVEL, value);
	}

	public static void setForceLoadAlternateDetector(boolean forceLoadAlternateDetector) {

		INSTANCE().putBoolean(P_FORCE_LOAD_ALTERNATE_DETECTOR, forceLoadAlternateDetector);
	}

	public static boolean isForceLoadAlternateDetector() {

		return INSTANCE().getBoolean(P_FORCE_LOAD_ALTERNATE_DETECTOR, DEF_FORCE_LOAD_ALTERNATE_DETECTOR);
	}

	public static String getListPathImport() {

		return INSTANCE().get(P_LIST_PATH_IMPORT, DEF_LIST_PATH_IMPORT);
	}

	public static void setListPathImport(String filterPath) {

		INSTANCE().put(P_LIST_PATH_IMPORT, filterPath);
	}

	public static String getListPathExport() {

		return INSTANCE().get(P_LIST_PATH_EXPORT, DEF_LIST_PATH_EXPORT);
	}

	public static void setListPathExport(String filterPath) {

		INSTANCE().put(P_LIST_PATH_EXPORT, filterPath);
	}
}