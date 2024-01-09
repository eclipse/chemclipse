/*******************************************************************************
 * Copyright (c) 2014, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.preferences;

import java.nio.charset.Charset;

import org.eclipse.chemclipse.msd.converter.supplier.amdis.Activator;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.chemclipse.support.text.CharsetNIO;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final String P_SPLIT_LIBRARY = "splitLibrary";
	public static final boolean DEF_SPLIT_LIBRARY = false;
	public static final String P_EXCLUDE_UNCERTAIN_IONS = "excludeUncertainIons";
	public static final boolean DEF_EXCLUDE_UNCERTAIN_IONS = false;
	public static final String P_USE_UNIT_MASS_RESOLUTION = "useUnitMassResolution";
	public static final boolean DEF_USE_UNIT_MASS_RESOLUTION = true;
	public static final String P_REMOVE_INTENSITIES_LOWER_THAN_ONE = "removeIntensitiesLowerThanOne";
	public static final boolean DEF_REMOVE_INTENSITIES_LOWER_THAN_ONE = true;
	public static final String P_NORMALIZE_INTENSITIES = "normalizeIntensities";
	public static final boolean DEF_NORMALIZE_INTENSITIES = true;
	public static final String P_EXPORT_INTENSITIES_AS_INTEGER = "exportIntensitiesAsInteger";
	public static final boolean DEF_EXPORT_INTENSITIES_AS_INTEGER = true;
	public static final String P_PARSE_COMPOUND_INFORMATION = "parseCompoundInformation";
	public static final boolean DEF_PARSE_COMPOUND_INFORMATION = true;
	public static final String P_PARSE_MOL_INFORMATION = "parseMolInformation";
	public static final boolean DEF_PARSE_MOL_INFORMATION = true;
	//
	public static final String P_CHARSET_IMPORT_MSL = "charsetImportMSL";
	public static final String DEF_CHARSET_IMPORT_MSL = CharsetNIO.US_ASCII.name();
	public static final String P_CHARSET_IMPORT_MSP = "charsetImportMSP";
	public static final String DEF_CHARSET_IMPORT_MSP = CharsetNIO.US_ASCII.name();
	public static final String P_CHARSET_IMPORT_FIN = "charsetImportFIN";
	public static final String DEF_CHARSET_IMPORT_FIN = CharsetNIO.US_ASCII.name();
	public static final String P_CHARSET_IMPORT_ELU = "charsetImportELU";
	public static final String DEF_CHARSET_IMPORT_ELU = CharsetNIO.US_ASCII.name();
	//
	public static final String P_PATH_IMPORT = "pathImport";
	public static final String DEF_PATH_IMPORT = "";
	public static final String P_PATH_EXPORT = "pathExport";
	public static final String DEF_PATH_EXPORT = "";
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

		putDefault(P_SPLIT_LIBRARY, Boolean.toString(DEF_SPLIT_LIBRARY));
		putDefault(P_EXCLUDE_UNCERTAIN_IONS, Boolean.toString(DEF_EXCLUDE_UNCERTAIN_IONS));
		putDefault(P_USE_UNIT_MASS_RESOLUTION, Boolean.toString(DEF_USE_UNIT_MASS_RESOLUTION));
		putDefault(P_REMOVE_INTENSITIES_LOWER_THAN_ONE, Boolean.toString(DEF_REMOVE_INTENSITIES_LOWER_THAN_ONE));
		putDefault(P_NORMALIZE_INTENSITIES, Boolean.toString(DEF_NORMALIZE_INTENSITIES));
		putDefault(P_EXPORT_INTENSITIES_AS_INTEGER, Boolean.toString(DEF_EXPORT_INTENSITIES_AS_INTEGER));
		putDefault(P_PARSE_COMPOUND_INFORMATION, Boolean.toString(DEF_PARSE_COMPOUND_INFORMATION));
		putDefault(P_PARSE_MOL_INFORMATION, Boolean.toString(DEF_PARSE_MOL_INFORMATION));
		//
		putDefault(P_CHARSET_IMPORT_MSL, DEF_CHARSET_IMPORT_MSL);
		putDefault(P_CHARSET_IMPORT_MSP, DEF_CHARSET_IMPORT_MSP);
		putDefault(P_CHARSET_IMPORT_FIN, DEF_CHARSET_IMPORT_FIN);
		putDefault(P_CHARSET_IMPORT_ELU, DEF_CHARSET_IMPORT_ELU);
		//
		putDefault(P_PATH_IMPORT, DEF_PATH_IMPORT);
		putDefault(P_PATH_EXPORT, DEF_PATH_EXPORT);
	}

	public static boolean isSplitLibrary() {

		return INSTANCE().getBoolean(P_SPLIT_LIBRARY, DEF_SPLIT_LIBRARY);
	}

	public static boolean isExcludeUncertainIons() {

		return INSTANCE().getBoolean(P_EXCLUDE_UNCERTAIN_IONS, DEF_EXCLUDE_UNCERTAIN_IONS);
	}

	public static boolean isUseUnitMassResolution() {

		return INSTANCE().getBoolean(P_USE_UNIT_MASS_RESOLUTION, DEF_USE_UNIT_MASS_RESOLUTION);
	}

	public static boolean isRemoveIntensitiesLowerThanOne() {

		return INSTANCE().getBoolean(P_REMOVE_INTENSITIES_LOWER_THAN_ONE, DEF_REMOVE_INTENSITIES_LOWER_THAN_ONE);
	}

	public static boolean isNormalizeIntensities() {

		return INSTANCE().getBoolean(P_NORMALIZE_INTENSITIES, DEF_NORMALIZE_INTENSITIES);
	}

	public static boolean isExportIntensitiesAsInteger() {

		return INSTANCE().getBoolean(P_EXPORT_INTENSITIES_AS_INTEGER, DEF_EXPORT_INTENSITIES_AS_INTEGER);
	}

	public static boolean isParseCompoundInformation() {

		return INSTANCE().getBoolean(P_PARSE_COMPOUND_INFORMATION, DEF_PARSE_COMPOUND_INFORMATION);
	}

	public static boolean isParseMolInformation() {

		return INSTANCE().getBoolean(P_PARSE_MOL_INFORMATION, DEF_PARSE_MOL_INFORMATION);
	}

	public static Charset getCharsetImportMSL() {

		return getCharset(P_CHARSET_IMPORT_MSL, DEF_CHARSET_IMPORT_MSL);
	}

	public static void setCharsetImportMSL(CharsetNIO charsetNIO) {

		setCharset(P_CHARSET_IMPORT_MSL, charsetNIO);
	}

	public static Charset getCharsetImportMSP() {

		return getCharset(P_CHARSET_IMPORT_MSP, DEF_CHARSET_IMPORT_MSP);
	}

	public static void setCharsetImportMSP(CharsetNIO charsetNIO) {

		setCharset(P_CHARSET_IMPORT_MSP, charsetNIO);
	}

	public static Charset getCharsetImportFIN() {

		return getCharset(P_CHARSET_IMPORT_FIN, DEF_CHARSET_IMPORT_FIN);
	}

	public static void setCharsetImportFIN(CharsetNIO charsetNIO) {

		setCharset(P_CHARSET_IMPORT_FIN, charsetNIO);
	}

	public static Charset getCharsetImportELU() {

		return getCharset(P_CHARSET_IMPORT_ELU, DEF_CHARSET_IMPORT_ELU);
	}

	public static void setCharsetImportELU(CharsetNIO charsetNIO) {

		setCharset(P_CHARSET_IMPORT_ELU, charsetNIO);
	}

	public static String getPathImport() {

		return INSTANCE().get(P_PATH_IMPORT, DEF_PATH_IMPORT);
	}

	public static void setPathImport(String path) {

		INSTANCE().put(P_PATH_IMPORT, path);
	}

	public static String getPathExport() {

		return INSTANCE().get(P_PATH_EXPORT, DEF_PATH_EXPORT);
	}

	public static void setPathExport(String path) {

		INSTANCE().put(P_PATH_EXPORT, path);
	}

	private static Charset getCharset(String key, String def) {

		try {
			return CharsetNIO.valueOf(INSTANCE().get(key, def)).getCharset();
		} catch(Exception e) {
			return CharsetNIO.US_ASCII.getCharset();
		}
	}

	private static void setCharset(String key, CharsetNIO charsetNIO) {

		INSTANCE().put(key, charsetNIO.name());
	}
}