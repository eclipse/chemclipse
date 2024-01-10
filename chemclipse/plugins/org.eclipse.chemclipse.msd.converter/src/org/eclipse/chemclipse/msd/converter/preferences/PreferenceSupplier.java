/*******************************************************************************
 * Copyright (c) 2016, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.preferences;

import org.eclipse.chemclipse.msd.converter.Activator;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final String P_REFERENCE_IDENTIFIER_MARKER = "referenceIdentifierMarker";
	public static final String DEF_REFERENCE_IDENTIFIER_MARKER = "";
	public static final String P_REFERENCE_IDENTIFIER_PREFIX = "referenceIdentifierPrefix";
	public static final String DEF_REFERENCE_IDENTIFIER_PREFIX = "";
	/*
	 * MassLib
	 */
	public static final String P_USE_MASSLIB_CHROMATOGRAM_NAME = "useMassLibChromatogramName";
	public static final boolean DEF_USE_MASSLIB_CHROMATOGRAM_NAME = true;
	public static final String P_MASSLIB_DEFAULT_NAME = "massLibDefaultName";
	public static final String DEF_MASSLIB_DEFAULT_NAME = "calibration";
	public static final String P_PARSE_MASSLIB_RETENTION_INDEX_DATA = "parseMassLibRetentionIndexData";
	public static final boolean DEF_PARSE_MASSLIB_RETENTION_INDEX_DATA = true;
	public static final String P_PARSE_MASSLIB_TARGET_DATA = "parseMassLibTargetData";
	public static final boolean DEF_PARSE_MASSLIB_TARGET_DATA = true;
	/*
	 * AMDIS
	 */
	public static final String P_USE_AMDIS_CHROMATOGRAM_NAME = "useAMDISChromatogramName";
	public static final boolean DEF_USE_AMDIS_CHROMATOGRAM_NAME = false;
	public static final String P_AMDIS_DEFAULT_NAME = "AMDISDefaultName";
	public static final String DEF_AMDIS_DEFAULT_NAME = "calibration";
	public static final String P_PARSE_AMDIS_RETENTION_INDEX_DATA = "parseAMDISRetentionIndexData";
	public static final boolean DEF_PARSE_AMDIS_RETENTION_INDEX_DATA = true;

	public static IPreferenceSupplier INSTANCE() {

		return INSTANCE(PreferenceSupplier.class);
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public void initializeDefaults() {

		putDefault(P_REFERENCE_IDENTIFIER_MARKER, DEF_REFERENCE_IDENTIFIER_MARKER);
		putDefault(P_REFERENCE_IDENTIFIER_PREFIX, DEF_REFERENCE_IDENTIFIER_PREFIX);
		//
		putDefault(P_USE_MASSLIB_CHROMATOGRAM_NAME, Boolean.toString(DEF_USE_MASSLIB_CHROMATOGRAM_NAME));
		putDefault(P_MASSLIB_DEFAULT_NAME, DEF_MASSLIB_DEFAULT_NAME);
		putDefault(P_PARSE_MASSLIB_RETENTION_INDEX_DATA, Boolean.toString(DEF_PARSE_MASSLIB_RETENTION_INDEX_DATA));
		putDefault(P_PARSE_MASSLIB_TARGET_DATA, Boolean.toString(DEF_PARSE_MASSLIB_TARGET_DATA));
		//
		putDefault(P_USE_AMDIS_CHROMATOGRAM_NAME, Boolean.toString(DEF_USE_AMDIS_CHROMATOGRAM_NAME));
		putDefault(P_AMDIS_DEFAULT_NAME, DEF_AMDIS_DEFAULT_NAME);
		putDefault(P_PARSE_AMDIS_RETENTION_INDEX_DATA, Boolean.toString(DEF_PARSE_AMDIS_RETENTION_INDEX_DATA));
	}

	public static String getReferenceIdentifierMarker() {

		return INSTANCE().get(P_REFERENCE_IDENTIFIER_MARKER, DEF_REFERENCE_IDENTIFIER_MARKER);
	}

	public static String getReferenceIdentifierPrefix() {

		return INSTANCE().get(P_REFERENCE_IDENTIFIER_PREFIX, DEF_REFERENCE_IDENTIFIER_PREFIX);
	}

	public static boolean isUseChromatogramNameMassLib() {

		return INSTANCE().getBoolean(P_USE_MASSLIB_CHROMATOGRAM_NAME, DEF_USE_MASSLIB_CHROMATOGRAM_NAME);
	}

	public static String getDefaultNameMassLib() {

		return INSTANCE().get(P_MASSLIB_DEFAULT_NAME, DEF_MASSLIB_DEFAULT_NAME);
	}

	public static boolean isParseRetentionIndexDataMassLib() {

		return INSTANCE().getBoolean(P_PARSE_MASSLIB_RETENTION_INDEX_DATA, DEF_PARSE_MASSLIB_RETENTION_INDEX_DATA);
	}

	public static boolean isParseTargetDataMassLib() {

		return INSTANCE().getBoolean(P_PARSE_MASSLIB_TARGET_DATA, DEF_PARSE_MASSLIB_TARGET_DATA);
	}

	public static boolean isUseChromatogramNameAMDIS() {

		return INSTANCE().getBoolean(P_USE_AMDIS_CHROMATOGRAM_NAME, DEF_USE_AMDIS_CHROMATOGRAM_NAME);
	}

	public static String getDefaultNameAMDIS() {

		return INSTANCE().get(P_AMDIS_DEFAULT_NAME, DEF_AMDIS_DEFAULT_NAME);
	}

	public static boolean isParseRetentionIndexDataAMDIS() {

		return INSTANCE().getBoolean(P_PARSE_AMDIS_RETENTION_INDEX_DATA, DEF_PARSE_AMDIS_RETENTION_INDEX_DATA);
	}
}