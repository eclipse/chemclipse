/*******************************************************************************
 * Copyright (c) 2011, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
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
}
