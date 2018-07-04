/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.support.Activator;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class SupportPreferences implements IPreferenceSupplier {

	public static final String P_CHROMATOGRAM_OPERATION_IS_UNDOABLE = "chromatogramOperationIsUndoable";
	public static final boolean DEF_CHROMATOGRAM_OPERATION_IS_UNDOABLE = true;
	public static final String P_APPLICATION_LANGUAGE = "applicationLanguage";
	public static final String DEF_APPLICATION_LANGUAGE = "";
	public static final String P_CLIPBOARD_TABLE_DEFAULT_SORTING = "clipboardTableDefaultSorting";
	public static final boolean DEF_CLIPBOARD_TABLE_DEFAULT_SORTING = false;
	//
	public static final String LANGUAGE_AUTODETECT = "";
	public static final String LANGUAGE_EN_GB = "en_GB";
	public static final String LANGUAGE_DE_DE = "de_DE";
	/*
	 * No Selection. Use the language of the system.
	 * de (ISO 639)
	 * DE (ISO 3166)
	 */
	public static final String[][] AVAILABLE_LANGUAGES = new String[][]{{"Autodetect", LANGUAGE_AUTODETECT}, {"English", LANGUAGE_EN_GB}, {"German", LANGUAGE_DE_DE}};
	//
	private static IPreferenceSupplier preferenceSupplier;

	public static IPreferenceSupplier INSTANCE() {

		if(preferenceSupplier == null) {
			preferenceSupplier = new SupportPreferences();
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
		defaultValues.put(P_CHROMATOGRAM_OPERATION_IS_UNDOABLE, Boolean.toString(DEF_CHROMATOGRAM_OPERATION_IS_UNDOABLE));
		defaultValues.put(P_APPLICATION_LANGUAGE, DEF_APPLICATION_LANGUAGE);
		defaultValues.put(P_CLIPBOARD_TABLE_DEFAULT_SORTING, Boolean.toString(DEF_CLIPBOARD_TABLE_DEFAULT_SORTING));
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	/**
	 * Returns whether chromatogram operations are undoable.
	 * 
	 * @return boolean
	 */
	public static boolean isUndoable() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getBoolean(P_CHROMATOGRAM_OPERATION_IS_UNDOABLE, DEF_CHROMATOGRAM_OPERATION_IS_UNDOABLE);
	}

	public static String getApplicationLanguage() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.get(P_APPLICATION_LANGUAGE, DEF_APPLICATION_LANGUAGE);
	}

	public static boolean isClipboardDefaultSorting() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getBoolean(P_CLIPBOARD_TABLE_DEFAULT_SORTING, DEF_CLIPBOARD_TABLE_DEFAULT_SORTING);
	}
}
