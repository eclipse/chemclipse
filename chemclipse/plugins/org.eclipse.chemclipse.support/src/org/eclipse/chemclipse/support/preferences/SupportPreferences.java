/*******************************************************************************
 * Copyright (c) 2011, 2023 Lablicate GmbH.
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

	public static final String P_CLIPBOARD_TABLE_DEFAULT_SORTING = "clipboardTableDefaultSorting";
	public static final boolean DEF_CLIPBOARD_TABLE_DEFAULT_SORTING = false;
	public static final String P_UNDO_LIMIT = "undoLimit";
	public static final int DEF_UNDO_LIMIT = 5;
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

		Map<String, String> defaultValues = new HashMap<>();
		defaultValues.put(P_CLIPBOARD_TABLE_DEFAULT_SORTING, Boolean.toString(DEF_CLIPBOARD_TABLE_DEFAULT_SORTING));
		defaultValues.put(P_UNDO_LIMIT, Integer.toString(DEF_UNDO_LIMIT));
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static boolean isClipboardDefaultSorting() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getBoolean(P_CLIPBOARD_TABLE_DEFAULT_SORTING, DEF_CLIPBOARD_TABLE_DEFAULT_SORTING);
	}

	public static int getUndoLimit() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getInt(P_UNDO_LIMIT, DEF_UNDO_LIMIT);
	}
}