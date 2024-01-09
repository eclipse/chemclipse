/*******************************************************************************
 * Copyright (c) 2014, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.csv.preferences;

import org.eclipse.chemclipse.model.settings.Delimiter;
import org.eclipse.chemclipse.msd.converter.supplier.csv.Activator;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final String P_IMPORT_DELIMITER = "importDelimiter";
	public static final String DEF_IMPORT_DELIMITER = Delimiter.COMMA.name();
	public static final String P_IMPORT_ZERO_MARKER = "importZeroMarker";
	public static final String DEF_IMPORT_ZERO_MARKER = "0.0";
	//
	public static final String P_EXPORT_USE_TIC = "exportUseTic";
	public static final boolean DEF_EXPORT_USE_TIC = false;
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

		putDefault(P_IMPORT_DELIMITER, DEF_IMPORT_DELIMITER);
		putDefault(P_IMPORT_ZERO_MARKER, DEF_IMPORT_ZERO_MARKER);
		putDefault(P_EXPORT_USE_TIC, Boolean.toString(DEF_EXPORT_USE_TIC));
	}

	public static Delimiter getImportDelimiter() {

		try {
			return Delimiter.valueOf(INSTANCE().get(P_IMPORT_DELIMITER, DEF_IMPORT_DELIMITER));
		} catch(Exception e) {
			return Delimiter.COMMA;
		}
	}

	public static void setImportDelimiter(Delimiter delimiter) {

		INSTANCE().put(P_IMPORT_DELIMITER, delimiter.name());
	}

	public static String getImportZeroMarker() {

		return INSTANCE().get(P_IMPORT_ZERO_MARKER, DEF_IMPORT_ZERO_MARKER);
	}

	public static void setImportZeroMarker(String zeroMarker) {

		INSTANCE().put(P_IMPORT_ZERO_MARKER, zeroMarker);
	}

	public static boolean isExportUseTic() {

		return INSTANCE().getBoolean(P_EXPORT_USE_TIC, DEF_EXPORT_USE_TIC);
	}

	public static void setExportUseTic(boolean useTic) {

		INSTANCE().putBoolean(P_EXPORT_USE_TIC, useTic);
	}
}